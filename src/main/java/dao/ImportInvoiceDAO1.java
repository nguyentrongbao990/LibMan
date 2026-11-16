package dao;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ImportInvoiceDAO1 extends DAO1 {
    public ImportInvoiceDAO1() { super(); }

    public ImportInvoice1[] getImportInvoicesBySupplier(Supplier1 supplier, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT i.*,s."supplierName",s."taxCode",s."address",s."phone",s."email",
                   st."staffCode",st."fullName"
            FROM "tblImportInvoice" i JOIN "tblSupplier" s ON s."id"=i."tblSupplierid"
            JOIN "tblLibraryStaff" ls ON ls."id"=i."tblLibraryStaffid"
            JOIN "tblStaff" st ON st."id"=ls."id"
            WHERE s."id"=? AND i."importDate" BETWEEN ? AND ?
            ORDER BY i."importDate",i."id"
            """;
        ArrayList<ImportInvoice1> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1,supplier.getId()); ps.setObject(2,startDate); ps.setObject(3,endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier1 s = new Supplier1(); s.setId(rs.getLong("tblSupplierid"));
                    s.setSupplierName(rs.getString("supplierName")); s.setTaxCode(rs.getString("taxCode"));
                    s.setAddress(rs.getString("address")); s.setPhone(rs.getString("phone")); s.setEmail(rs.getString("email"));
                    LibraryStaff1 staff = new LibraryStaff1(); staff.setId(rs.getLong("tblLibraryStaffid"));
                    staff.setStaffCode(rs.getString("staffCode")); staff.setFullName(rs.getString("fullName"));
                    ImportInvoice1 invoice = new ImportInvoice1(); invoice.setId(rs.getLong("id"));
                    invoice.setInvoiceCode(rs.getString("invoiceCode")); invoice.setImportDate(rs.getObject("importDate",LocalDate.class));
                    invoice.setTotalAmount(rs.getBigDecimal("totalAmount")); invoice.setSupplier(s); invoice.setLibraryStaff(staff);
                    list.add(invoice);
                }
            }
            return list.toArray(new ImportInvoice1[0]);
        } catch (SQLException e) { throw new IllegalStateException("Không lấy được danh sách phiếu nhập.",e); }
    }
}
