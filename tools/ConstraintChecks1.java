import java.sql.*;
import java.util.Properties;
import java.nio.file.*;
import java.io.*;

/** All inserted constraint fixtures are rolled back, including when an assertion fails. */
public class ConstraintChecks1 {
    static long id(Connection c,String sql) throws SQLException {try(Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getLong(1);}}
    static void execute(Connection c,String sql) throws SQLException {try(Statement s=c.createStatement()){s.executeUpdate(sql);}}
    static void duplicate(Connection c,String sql,String label) throws SQLException {
        Savepoint save=c.setSavepoint();
        try{execute(c,sql);throw new AssertionError("Duplicate accepted: "+label);}
        catch(SQLException e){if(!"23505".equals(e.getSQLState()))throw e;System.out.println("PASS duplicate rejected: "+label);}
        finally{c.rollback(save);}
    }
    public static void main(String[] args) throws Exception {
        Properties p=new Properties();try(InputStream in=Files.newInputStream(Path.of(".local/db.properties"))){p.load(in);}
        try(Connection c=DriverManager.getConnection(p.getProperty("url"),p.getProperty("user"),p.getProperty("password"))) {
            c.setAutoCommit(false);
            try {
                long doc1=id(c,"SELECT \"id\" FROM \"tblDocument\" WHERE \"documentCode\"='DOC001'");
                long doc2=id(c,"SELECT \"id\" FROM \"tblDocument\" WHERE \"documentCode\"='DOC002'");
                long a=id(c,"INSERT INTO \"tblAuthor\" (\"authorName\") VALUES ('Tác giả Demo kiểm thử') RETURNING \"id\"");
                long b=id(c,"INSERT INTO \"tblAuthor\" (\"authorName\") VALUES ('Tác giả Demo thứ hai') RETURNING \"id\"");
                execute(c,"INSERT INTO \"tblDocumentAuthor\" (\"tblDocumentid\",\"tblAuthorid\") VALUES ("+doc1+","+a+"),("+doc1+","+b+"),("+doc2+","+a+")");
                duplicate(c,"INSERT INTO \"tblDocumentAuthor\" (\"tblDocumentid\",\"tblAuthorid\") VALUES ("+doc1+","+a+")","document+author");
                long cat1=id(c,"INSERT INTO \"tblCategory\" (\"categoryName\") VALUES ('Thể loại Demo A') RETURNING \"id\"");
                long cat2=id(c,"INSERT INTO \"tblCategory\" (\"categoryName\") VALUES ('Thể loại Demo B') RETURNING \"id\"");
                execute(c,"INSERT INTO \"tblDocumentCategory\" (\"tblDocumentid\",\"tblCategoryid\") VALUES ("+doc1+","+cat1+"),("+doc1+","+cat2+"),("+doc2+","+cat1+")");
                duplicate(c,"INSERT INTO \"tblDocumentCategory\" (\"tblDocumentid\",\"tblCategoryid\") VALUES ("+doc1+","+cat1+")","document+category");
                long reader=id(c,"SELECT \"id\" FROM \"tblReader\" LIMIT 1");
                long ls=id(c,"SELECT \"id\" FROM \"tblLibraryStaff\" LIMIT 1");
                String suffix=Long.toString(System.currentTimeMillis());
                long copy1=id(c,"INSERT INTO \"tblDocumentCopy\" (\"copyCode\",\"status\",\"condition\",\"tblDocumentid\") VALUES ('TESTA"+suffix+"','AVAILABLE','GOOD',"+doc1+") RETURNING \"id\"");
                long copy2=id(c,"INSERT INTO \"tblDocumentCopy\" (\"copyCode\",\"status\",\"condition\",\"tblDocumentid\") VALUES ('TESTB"+suffix+"','AVAILABLE','GOOD',"+doc1+") RETURNING \"id\"");
                long loan1=id(c,"INSERT INTO \"tblLoan\" (\"loanCode\",\"loanDate\",\"dueDate\",\"tblReaderid\") VALUES ('TESTA"+suffix+"',CURRENT_DATE,CURRENT_DATE,"+reader+") RETURNING \"id\"");
                long loan2=id(c,"INSERT INTO \"tblLoan\" (\"loanCode\",\"loanDate\",\"dueDate\",\"tblReaderid\") VALUES ('TESTB"+suffix+"',CURRENT_DATE,CURRENT_DATE,"+reader+") RETURNING \"id\"");
                long ld1=id(c,"INSERT INTO \"tblLoanDetail\" (\"tblLoanid\",\"tblDocumentCopyid\") VALUES ("+loan1+","+copy1+") RETURNING \"id\"");
                long ld2=id(c,"INSERT INTO \"tblLoanDetail\" (\"tblLoanid\",\"tblDocumentCopyid\") VALUES ("+loan1+","+copy2+") RETURNING \"id\"");
                execute(c,"INSERT INTO \"tblLoanDetail\" (\"tblLoanid\",\"tblDocumentCopyid\") VALUES ("+loan2+","+copy1+")");
                duplicate(c,"INSERT INTO \"tblLoanDetail\" (\"tblLoanid\",\"tblDocumentCopyid\") VALUES ("+loan1+","+copy1+")","loan+copy");
                long receipt=id(c,"INSERT INTO \"tblReturnReceipt\" (\"returnCode\",\"returnDate\",\"tblReaderid\",\"tblLibraryStaffid\") VALUES ('TEST"+suffix+"',CURRENT_DATE,"+reader+","+ls+") RETURNING \"id\"");
                execute(c,"INSERT INTO \"tblReturnDetail\" (\"lateFine\",\"damageFine\",\"tblReturnReceiptid\",\"tblLoanDetailid\") VALUES (0,0,"+receipt+","+ld1+"),(0,0,"+receipt+","+ld2+")");
                duplicate(c,"INSERT INTO \"tblReturnDetail\" (\"lateFine\",\"damageFine\",\"tblReturnReceiptid\",\"tblLoanDetailid\") VALUES (0,0,"+receipt+","+ld1+")","return loanDetail");
                long invoice=id(c,"SELECT \"id\" FROM \"tblImportInvoice\" WHERE \"invoiceCode\"='PN000137'");
                duplicate(c,"INSERT INTO \"tblImportInvoiceDetail\" (\"quantity\",\"unitPrice\",\"tblImportInvoiceid\",\"tblDocumentid\") VALUES (1,1,"+invoice+","+doc1+")","invoice+document");
                System.out.println("PASS repeated individual FK values accepted in all four composite tables; multiple details per return receipt accepted");
            } finally { c.rollback();System.out.println("All constraint fixtures rolled back; existing rows preserved"); }
        }
    }
}
