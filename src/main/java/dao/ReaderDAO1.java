package dao;

import model.Reader1;
import model.ReaderCard1;
import java.sql.*;
import java.time.LocalDate;

public class ReaderDAO1 extends DAO1 {
    public ReaderDAO1() { super(); }

    public Reader1 findReaderByCccd(String cccd) {
        String sql = """
            SELECT r.*, c."id" AS "cardId", c."cardCode", c."issueDate", c."status"
            FROM "tblReader" r LEFT JOIN "tblReaderCard" c ON c."readerId"=r."id"
            WHERE r."cccd"=?
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reader1 reader = new Reader1();
                reader.setId(rs.getLong("id")); reader.setCccd(rs.getString("cccd"));
                reader.setFullName(rs.getString("fullName")); reader.setDateOfBirth(rs.getObject("dateOfBirth", LocalDate.class));
                reader.setAddress(rs.getString("address")); reader.setPhone(rs.getString("phone")); reader.setEmail(rs.getString("email"));
                if (rs.getObject("cardId") != null) {
                    ReaderCard1 card = new ReaderCard1(); card.setId(rs.getLong("cardId"));
                    card.setCardCode(rs.getString("cardCode")); card.setIssueDate(rs.getObject("issueDate", LocalDate.class));
                    card.setStatus(rs.getString("status")); reader.setReaderCard(card);
                }
                return reader;
            }
        } catch (SQLException e) { throw new IllegalStateException("Không tra cứu được bạn đọc.", e); }
    }
}
