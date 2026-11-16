import java.sql.*;
import java.util.Properties;
import java.nio.file.*;
import java.io.*;

/** Run manually against the user's locally configured server, never resets data. */
public class DatabaseSetup1 {
    public static void main(String[] args) throws Exception {
        Properties p=new Properties();
        try(InputStream in=Files.newInputStream(Path.of(".local/db.properties"))) { p.load(in); }
        String url=p.getProperty("url");
        if(!url.matches("jdbc:postgresql://(localhost|127\\.0\\.0\\.1):[0-9]+/libman")) throw new IllegalArgumentException("Require localhost /libman database URL");
        String adminUrl=url.substring(0,url.lastIndexOf('/')+1)+"postgres";
        try(Connection c=DriverManager.getConnection(adminUrl,p.getProperty("user"),p.getProperty("password"))) {
            try(PreparedStatement ps=c.prepareStatement("SELECT 1 FROM pg_database WHERE datname=?")) {
                ps.setString(1,"libman");
                try(ResultSet rs=ps.executeQuery()) {
                    if(!rs.next()) { try(Statement s=c.createStatement()) { s.executeUpdate("CREATE DATABASE libman"); } System.out.println("Created database libman"); }
                    else System.out.println("Database libman exists; checking tables before any DDL");
                }
            }
        }
        try(Connection c=DriverManager.getConnection(url,p.getProperty("user"),p.getProperty("password"))) {
            try(Statement s=c.createStatement(); ResultSet rs=s.executeQuery("SELECT count(*) FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace WHERE n.nspname NOT IN ('pg_catalog','information_schema') AND n.nspname NOT LIKE 'pg_toast%' AND c.relkind IN ('r','p','v','m','f','S')")) {
                rs.next(); if(rs.getLong(1)!=0) throw new IllegalStateException("Database contains user objects; refused schema/seed. No data changed.");
            }
            for(String file:new String[]{"database/schema.sql","database/seed.sql"}) {
                try(Statement s=c.createStatement()) { s.execute(Files.readString(Path.of(file))); }
                System.out.println("Applied "+file);
            }
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT version()")) { rs.next();System.out.println(rs.getString(1)); }
        } catch(SQLException e) { System.err.println("DB setup failed SQLState="+e.getSQLState()+"; no credentials printed.");System.exit(1); }
    }
}
