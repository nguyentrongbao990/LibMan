package dao;

import model.Reader1;
import model.ReaderCard1;
import java.sql.*;
import java.time.LocalDate;
import java.util.Locale;

public class ReaderCardDAO1 extends DAO1 {
    public ReaderCardDAO1() { super(); }

    public Reader1 registerReaderCard(Reader1 reader) {
        try {
            con.setAutoCommit(false);
            long readerId;
            try (PreparedStatement ps = con.prepareStatement("""
                INSERT INTO "tblReader" ("cccd","fullName","dateOfBirth","address","phone","email")
                VALUES (?,?,?,?,?,?) RETURNING "id"
                """)) {
                ps.setString(1,reader.getCccd()); ps.setString(2,reader.getFullName()); ps.setObject(3,reader.getDateOfBirth());
                ps.setString(4,reader.getAddress()); ps.setString(5,reader.getPhone()); ps.setString(6,reader.getEmail());
                try (ResultSet rs = ps.executeQuery()) { rs.next(); readerId = rs.getLong(1); }
            }
            ReaderCard1 card = new ReaderCard1();
            card.setCardCode("RD" + String.format(Locale.ROOT,"%06d",readerId));
            card.setIssueDate(LocalDate.now()); card.setStatus("ACTIVE");
            try (PreparedStatement ps = con.prepareStatement("""
                INSERT INTO "tblReaderCard" ("cardCode","issueDate","status","readerId") VALUES (?,?,?,?) RETURNING "id"
                """)) {
                ps.setString(1,card.getCardCode()); ps.setObject(2,card.getIssueDate());
                ps.setString(3,card.getStatus()); ps.setLong(4,readerId);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); card.setId(rs.getLong(1)); }
            }
            con.commit();
            reader.setId(readerId); reader.setReaderCard(card);
            return reader;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
            if ("23505".equals(e.getSQLState()) && e instanceof org.postgresql.util.PSQLException
                    && ((org.postgresql.util.PSQLException)e).getServerErrorMessage() != null
                    && "tblReader_cccd_key".equals(((org.postgresql.util.PSQLException)e).getServerErrorMessage().getConstraint()))
                throw new IllegalArgumentException("CCCD đã đăng ký thẻ bạn đọc.", e);
            throw new IllegalStateException("Không lưu được đăng ký; giao dịch đã hủy.", e);
        }
    }
}
