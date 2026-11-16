import dao.*;
import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;

/** Real PostgreSQL checks. Only synthetic demo readers are committed; never deletes data. */
public class IntegrationChecks1 {
    static void check(boolean ok,String message) { if(!ok) throw new AssertionError(message); System.out.println("PASS "+message); }
    static class FailCardDAO1 extends ReaderCardDAO1 {
        FailCardDAO1() {
            Connection real=con;
            con=(Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class<?>[]{Connection.class},(proxy,m,args)->{
                if(m.getName().equals("prepareStatement") && ((String)args[0]).contains("INSERT INTO \"tblReaderCard\""))
                    throw new SQLException("Injected test failure between INSERTs","P0001");
                try { return m.invoke(real,args); } catch(InvocationTargetException e) { throw e.getCause(); }
            });
        }
    }
    static Reader1 reader(String cccd) {
        Reader1 r=new Reader1();r.setCccd(cccd);r.setFullName("Bạn đọc kiểm thử Demo");r.setDateOfBirth(LocalDate.of(2003,8,15));
        r.setAddress("Địa chỉ Demo");r.setPhone("0900000000");r.setEmail("test@example.com");return r;
    }
    public static void main(String[] args) throws Exception {
        System.setProperty("libman.db.config",Path.of(".local/db.properties").toAbsolutePath().toString());
        Properties p=new Properties();try(InputStream in=Files.newInputStream(Path.of(".local/db.properties"))){p.load(in);}
        try(Connection c=DriverManager.getConnection(p.getProperty("url"),p.getProperty("user"),p.getProperty("password"))) {
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT count(*) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'")) {rs.next();check(rs.getInt(1)==19,"19 PostgreSQL tables");}
            Map<String,Set<String>> unique=new HashMap<>();
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT t.relname,pg_get_constraintdef(c.oid) FROM pg_constraint c JOIN pg_class t ON t.oid=c.conrelid JOIN pg_namespace n ON n.oid=t.relnamespace WHERE c.contype='u' AND n.nspname='public'")) {
                while(rs.next()) unique.computeIfAbsent(rs.getString(1),k->new HashSet<>()).add(rs.getString(2));
            }
            check(unique.get("tblDocumentAuthor").equals(Set.of("UNIQUE (\"tblDocumentid\", \"tblAuthorid\")")),"DocumentAuthor composite only");
            check(unique.get("tblDocumentCategory").equals(Set.of("UNIQUE (\"tblDocumentid\", \"tblCategoryid\")")),"DocumentCategory composite only");
            check(unique.get("tblLoanDetail").equals(Set.of("UNIQUE (\"tblLoanid\", \"tblDocumentCopyid\")")),"LoanDetail composite only");
            check(unique.get("tblImportInvoiceDetail").equals(Set.of("UNIQUE (\"tblImportInvoiceid\", \"tblDocumentid\")")),"ImportInvoiceDetail composite only");
            check(unique.get("tblReturnDetail").equals(Set.of("UNIQUE (\"tblLoanDetailid\")")),"ReturnDetail loanDetail unique, receipt not unique");
            check(unique.get("tblReaderCard").contains("UNIQUE (\"readerId\")"),"one card per reader");
            long beforeCancel;
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT count(*) FROM \"tblReader\"")){rs.next();beforeCancel=rs.getLong(1);}
            java.net.http.HttpClient http=java.net.http.HttpClient.newHttpClient();
            for(String page:new String[]{"ReaderCardRegistrationPage1","LibManHomePage1"}) {
                var response=http.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://127.0.0.1:18080/LibMan/view/reader/"+page+".jsp")).GET().build(),java.net.http.HttpResponse.BodyHandlers.discarding());
                check(response.statusCode()==200,"open/cancel registration page "+page);
            }
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT count(*) FROM \"tblReader\"")){rs.next();check(rs.getLong(1)==beforeCancel,"cancel navigation writes no reader rows");}
            LocalDate start=LocalDate.of(2026,1,1),end=LocalDate.of(2026,3,31);
            SupplierStatistic1[] stats;
            try(SupplierStatisticDAO1 dao=new SupplierStatisticDAO1()) {stats=dao.getSupplierStatistics(start,end);}
            check(stats.length==3 && stats[0].getImportedVolume()==320 && stats[1].getImportedVolume()==245 && stats[2].getImportedVolume()==180,"real SUM(quantity) gives 320/245/180");
            check(stats[0].getTaxCode().equals("0101234567") && stats[1].getTaxCode().equals("0102345678") && stats[2].getTaxCode().equals("0103456789"),"correct supplier ranking");
            boolean independentMatch=true;
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("""
                SELECT s."taxCode", (SELECT SUM(d."quantity") FROM "tblImportInvoiceDetail" d
                  WHERE d."tblImportInvoiceid" IN (SELECT i."id" FROM "tblImportInvoice" i
                    WHERE i."tblSupplierid"=s."id" AND i."importDate">=DATE '2026-01-01'
                    AND i."importDate"<=DATE '2026-03-31')) AS volume
                FROM "tblSupplier" s WHERE s."taxCode" IN ('0101234567','0102345678','0103456789')
                """)) {
                int count=0;
                while(rs.next()) {count++;boolean found=false;for(SupplierStatistic1 stat:stats) if(stat.getTaxCode().equals(rs.getString(1))) found=stat.getImportedVolume()==rs.getLong(2);independentMatch &= found;}
                independentMatch &= count==3;
            }
            check(independentMatch,"independent SQL SUM(quantity) matches DAO ranking volumes");
            try(SupplierStatisticDAO1 dao=new SupplierStatisticDAO1()) {SupplierStatistic1[] a=dao.getSupplierStatistics(start,start);check(a.length==1 && a[0].getImportedVolume()==100,"start date inclusive");}
            try(SupplierStatisticDAO1 dao=new SupplierStatisticDAO1()) {SupplierStatistic1[] a=dao.getSupplierStatistics(end,end);check(a.length==1 && a[0].getImportedVolume()==145,"end date inclusive");}
            try(SupplierStatisticDAO1 dao=new SupplierStatisticDAO1()) {check(dao.getSupplierStatistics(LocalDate.of(2024,1,1),LocalDate.of(2024,12,31)).length==0,"empty range returns empty array");}
            try(SupplierStatisticDAO1 dao=new SupplierStatisticDAO1()) {SupplierStatistic1[] a=dao.getSupplierStatistics(LocalDate.of(2026,4,1),LocalDate.of(2026,4,1));check(a.length==1 && a[0].getImportedVolume()==9,"different period queries real rows");}
            ImportInvoice1[] invoices;
            try(ImportInvoiceDAO1 dao=new ImportInvoiceDAO1()) {invoices=dao.getImportInvoicesBySupplier(stats[0],start,end);}
            check(invoices.length==3 && invoices[0].getInvoiceCode().equals("PN000101") && invoices[1].getInvoiceCode().equals("PN000118") && invoices[2].getInvoiceCode().equals("PN000137"),"Minh Triet three invoices/date order");
            ImportInvoice1 invoice=invoices[2];
            check(invoice.getLibraryStaff().getStaffCode().equals("LS001") && invoice.getLibraryStaff().getFullName().equals("Trần Thị Bình") && invoice.getLibraryStaff().getPassword()==null,"invoice staff populated without password");
            try(ImportInvoiceDetailDAO1 dao=new ImportInvoiceDetailDAO1()) {invoice.setImportInvoiceDetails(dao.getImportInvoiceDetails(invoice));}
            int[] quantities={40,30,50};String[] codes={"DOC001","DOC002","DOC003"};BigDecimal sum=BigDecimal.ZERO;
            check(invoice.getImportInvoiceDetails().length==3,"three detail rows in PN000137");
            for(int i=0;i<3;i++){ImportInvoiceDetail1 d=invoice.getImportInvoiceDetails()[i];check(d.getQuantity()==quantities[i] && d.getDocument().getDocumentCode().equals(codes[i]),"detail "+codes[i]);sum=sum.add(d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())));}
            check(sum.compareTo(new BigDecimal("14350000"))==0 && sum.compareTo(invoice.getTotalAmount())==0,"BigDecimal sum and stored total 14350000");
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT count(*) FROM \"tblImportInvoice\" i WHERE i.\"totalAmount\"<>(SELECT SUM(d.\"quantity\"*d.\"unitPrice\") FROM \"tblImportInvoiceDetail\" d WHERE d.\"tblImportInvoiceid\"=i.\"id\")")){rs.next();check(rs.getInt(1)==0,"independent SQL validates all stored totals");}
            try(StaffDAO1 dao=new StaffDAO1()){ManagementStaff1 m=dao.login("manager_demo","LibManDemo1!");check(m!=null && m.getPassword()==null,"management login without session password");}
            try(StaffDAO1 dao=new StaffDAO1()){check(dao.login("manager_demo","wrong")==null,"wrong password denied");}
            try(StaffDAO1 dao=new StaffDAO1()){check(dao.login("library_demo","LibraryDemo1!")==null,"library staff role denied");}
            try(StaffDAO1 dao=new StaffDAO1()){check(dao.login("' OR 1=1 --","x")==null,"PreparedStatement login injection denied");}
            // Pick an unused synthetic CCCD so repeat execution never deletes prior test records.
            String cccd;
            do {cccd="000"+String.format(Locale.ROOT,"%09d",new Random().nextInt(1000000000));}
            while(exists(cccd));
            Reader1 r=reader(cccd);
            try(FailCardDAO1 dao=new FailCardDAO1()) {try{dao.registerReaderCard(r);throw new AssertionError("Expected injected failure");}catch(IllegalStateException expected){}}
            check(!exists(cccd) && r.getReaderCard()==null && r.getId()==0,"rollback after first INSERT leaves no orphan/success object");
            try(ReaderCardDAO1 dao=new ReaderCardDAO1()){r=dao.registerReaderCard(r);}
            check(r.getId()>0 && r.getReaderCard().getIssueDate().equals(LocalDate.now()) && r.getReaderCard().getStatus().equals("ACTIVE") && r.getReaderCard().getCardCode().equals("RD"+String.format(Locale.ROOT,"%06d",r.getId())),"reader/card committed with generated code/date/status");
            try(ReaderCardDAO1 dao=new ReaderCardDAO1()){try{dao.registerReaderCard(reader(cccd));throw new AssertionError("Duplicate accepted");}catch(IllegalArgumentException expected){}}
            try(PreparedStatement ps=c.prepareStatement("SELECT count(*) FROM \"tblReader\" r JOIN \"tblReaderCard\" c ON c.\"readerId\"=r.\"id\" WHERE r.\"cccd\"=?")){ps.setString(1,cccd);try(ResultSet rs=ps.executeQuery()){rs.next();check(rs.getInt(1)==1,"duplicate creates no second row/card");}}
        }
    }
    static boolean exists(String cccd) {try(ReaderDAO1 dao=new ReaderDAO1()){return dao.findReaderByCccd(cccd)!=null;}}
}
