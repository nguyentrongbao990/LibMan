package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class DAO1 implements AutoCloseable {
    protected Connection con;

    public DAO1() {
        Properties config = new Properties();
        String file = System.getProperty("libman.db.config", System.getenv("LIBMAN_DB_CONFIG"));
        if (file != null && !file.isBlank()) {
            try (InputStream in = Files.newInputStream(Path.of(file))) { config.load(in); }
            catch (IOException e) { throw new IllegalStateException("Không đọc được cấu hình DB cục bộ.", e); }
        }
        String url = System.getenv("LIBMAN_DB_URL");
        String user = System.getenv("LIBMAN_DB_USER");
        String password = System.getenv("LIBMAN_DB_PASSWORD");
        if (url == null) url = config.getProperty("url");
        if (user == null) user = config.getProperty("user");
        if (password == null) password = config.getProperty("password");
        if (url == null || user == null || password == null)
            throw new IllegalStateException("Chưa cấu hình kết nối PostgreSQL.");
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Không kết nối được PostgreSQL.", e);
        }
    }

    @Override public void close() {
        try { if (con != null) con.close(); }
        catch (SQLException e) { throw new IllegalStateException("Không đóng được kết nối DB.", e); }
    }
}
