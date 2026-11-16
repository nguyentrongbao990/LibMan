package dao;

import model.SupplierStatistic1;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SupplierStatisticDAO1 extends DAO1 {
    public SupplierStatisticDAO1() { super(); }

    public SupplierStatistic1[] getSupplierStatistics(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT s.*, SUM(d."quantity") AS "importedVolume"
            FROM "tblSupplier" s JOIN "tblImportInvoice" i ON i."tblSupplierid"=s."id"
            JOIN "tblImportInvoiceDetail" d ON d."tblImportInvoiceid"=i."id"
            WHERE i."importDate" BETWEEN ? AND ?
            GROUP BY s."id",s."supplierName",s."taxCode",s."address",s."phone",s."email"
            ORDER BY "importedVolume" DESC,s."id" ASC
            """;
        ArrayList<SupplierStatistic1> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1,startDate); ps.setObject(2,endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupplierStatistic1 s = new SupplierStatistic1();
                    s.setId(rs.getLong("id")); s.setSupplierName(rs.getString("supplierName"));
                    s.setTaxCode(rs.getString("taxCode")); s.setAddress(rs.getString("address"));
                    s.setPhone(rs.getString("phone")); s.setEmail(rs.getString("email"));
                    s.setImportedVolume(rs.getLong("importedVolume")); list.add(s);
                }
            }
            return list.toArray(new SupplierStatistic1[0]);
        } catch (SQLException e) { throw new IllegalStateException("Không lấy được thống kê nhà cung cấp.",e); }
    }
}
