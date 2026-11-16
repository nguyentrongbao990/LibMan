package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

public class ImportInvoiceDetailDAO1 extends DAO1 {
    public ImportInvoiceDetailDAO1() { super(); }

    public ImportInvoiceDetail1[] getImportInvoiceDetails(ImportInvoice1 importInvoice) {
        String sql = """
            SELECT d.*,t."documentCode",t."title",t."publicationYear",t."edition",
                   t."tblPublisherid",p."publisherName"
            FROM "tblImportInvoiceDetail" d JOIN "tblDocument" t ON t."id"=d."tblDocumentid"
            LEFT JOIN "tblPublisher" p ON p."id"=t."tblPublisherid"
            WHERE d."tblImportInvoiceid"=? ORDER BY d."id"
            """;
        ArrayList<ImportInvoiceDetail1> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1,importInvoice.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Document1 doc = new Document1(); doc.setId(rs.getLong("tblDocumentid"));
                    doc.setDocumentCode(rs.getString("documentCode")); doc.setTitle(rs.getString("title"));
                    doc.setPublicationYear(rs.getInt("publicationYear")); doc.setEdition(rs.getString("edition"));
                    if (rs.getObject("tblPublisherid") != null) {
                        Publisher1 p = new Publisher1(); p.setId(rs.getLong("tblPublisherid"));
                        p.setPublisherName(rs.getString("publisherName")); doc.setPublisher(p);
                    }
                    ImportInvoiceDetail1 detail = new ImportInvoiceDetail1(); detail.setId(rs.getLong("id"));
                    detail.setQuantity(rs.getInt("quantity")); detail.setUnitPrice(rs.getBigDecimal("unitPrice"));
                    detail.setDocument(doc); list.add(detail);
                }
            }
            return list.toArray(new ImportInvoiceDetail1[0]);
        } catch (SQLException e) { throw new IllegalStateException("Không lấy được chi tiết phiếu nhập.",e); }
    }
}
