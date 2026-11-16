import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.sql.*;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.regex.*;

/** Real HTTP/JDBC regression: detect a fresh SELECT without modifying demo data. */
public class InvoiceListReloadChecks1 {
    private static final String BASE="http://127.0.0.1:18080/LibMan";
    private static final String STATS="/view/managementStaff/SupplierStatisticPage1.jsp";
    private static final HttpClient HTTP=HttpClient.newBuilder()
        .cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL))
        .followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(5)).build();

    private static void check(boolean ok,String label) {
        if(!ok) throw new AssertionError(label);
        System.out.println("PASS "+label);
    }
    private static HttpRequest request(String path) {
        return HttpRequest.newBuilder(URI.create(BASE+path)).timeout(Duration.ofSeconds(15)).GET().build();
    }
    private static String get(String path) throws Exception {
        var response=HTTP.send(request(path),HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()!=200) throw new AssertionError("HTTP status "+response.statusCode());
        return response.body();
    }
    private static String link(String body,String label) {
        Matcher m=Pattern.compile("<a[^>]*href=\"([^\"]+)\"[^>]*>(.*?)</a>",Pattern.DOTALL).matcher(body);
        while(m.find()) if(m.group(2).contains(label)) return m.group(1).replace("&amp;","&").substring("/LibMan".length());
        throw new AssertionError("Missing link "+label);
    }
    private static void freshSelect(Connection con,String path,String label) throws Exception {
        CompletableFuture<HttpResponse<String>> pending=null;
        boolean observed=false;
        con.setAutoCommit(false);
        try {
            // An ACCESS EXCLUSIVE lock blocks a real SELECT, but cannot block a cached Java array.
            // Local demo only; rollback releases it and never changes table contents.
            try(Statement s=con.createStatement()) {
                s.execute("SET LOCAL lock_timeout='2s'");
                s.execute("LOCK TABLE \"tblImportInvoice\" IN ACCESS EXCLUSIVE MODE");
            }
            pending=HTTP.sendAsync(request(path),HttpResponse.BodyHandlers.ofString());
            long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(5);
            try(PreparedStatement ps=con.prepareStatement("""
                SELECT EXISTS(SELECT 1 FROM pg_locks l
                WHERE pg_backend_pid()=ANY(pg_blocking_pids(l.pid)) AND NOT l.granted
                AND l.mode='AccessShareLock' AND l.relation='"tblImportInvoice"'::regclass)
                """)) {
                while(System.nanoTime()<deadline && !pending.isDone()) {
                    try(ResultSet rs=ps.executeQuery()) { rs.next(); if(rs.getBoolean(1)) {observed=true;break;} }
                    Thread.sleep(50);
                }
            }
        } finally { con.rollback(); con.setAutoCommit(true); }
        if(pending==null) throw new AssertionError("HTTP request was not started");
        var response=pending.get(15,TimeUnit.SECONDS);
        check(observed,label+": fresh DAO SELECT observed via pg_blocking_pids");
        check(response.statusCode()==200 && response.body().contains("PN000137")
            && response.body().contains("31/03/2026"),label+": list and dates retained after lock release");
        String detail=get(link(response.body(),"PN000137"));
        check(detail.contains("14.350.000") && detail.contains("LS001"),label+": refreshed session list supports invoice detail");
    }
    public static void main(String[] args) throws Exception {
        Properties p=new Properties();
        try(InputStream in=Files.newInputStream(Path.of(".local/db.properties"))) {p.load(in);}
        var login=HttpRequest.newBuilder(URI.create(BASE+"/view/staff/LoginPage1.jsp"))
            .timeout(Duration.ofSeconds(15)).header("Content-Type","application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString("username=manager_demo&password=LibManDemo1%21")).build();
        var response=HTTP.send(login,HttpResponse.BodyHandlers.ofString());
        check(response.uri().getPath().endsWith("ManagementStaffHomePage1.jsp"),"management login");
        String stats=get(STATS+"?inStartDate=2026-01-01&inEndDate=2026-03-31");
        String listPath=link(stats,"Công ty TNHH Sách Minh Triết");
        String list=get(listPath);
        check(list.contains("PN000101") && list.contains("PN000118") && list.contains("PN000137"),"initial list populated in session");
        try(Connection con=DriverManager.getConnection(p.getProperty("url"),p.getProperty("user"),p.getProperty("password"))) {
            freshSelect(con,listPath,"reselect same supplier");
            String detail=get(link(get(listPath),"PN000137"));
            freshSelect(con,link(detail,"Quay lại"),"back from invoice detail");
        }
        check(get(STATS).contains(">320</td>"),"statistics context/results retained");
        System.out.println("Read-only reload regression complete; no fixture rows inserted/updated/deleted.");
    }
}
