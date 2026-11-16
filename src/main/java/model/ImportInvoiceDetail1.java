package model;

import java.math.BigDecimal;

public class ImportInvoiceDetail1 {
    private long id;
    private int quantity;
    private BigDecimal unitPrice;
    private Document1 document;

    public ImportInvoiceDetail1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Document1 getDocument() { return document; }
    public void setDocument(Document1 document) { this.document = document; }
}
