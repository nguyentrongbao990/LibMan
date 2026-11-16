package model;

import java.time.LocalDate;

import java.math.BigDecimal;

public class ImportInvoice1 {
    private long id;
    private String invoiceCode;
    private LocalDate importDate;
    private BigDecimal totalAmount;
    private LibraryStaff1 libraryStaff;
    private Supplier1 supplier;
    private ImportInvoiceDetail1[] importInvoiceDetails;

    public ImportInvoice1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }

    public LocalDate getImportDate() { return importDate; }
    public void setImportDate(LocalDate importDate) { this.importDate = importDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LibraryStaff1 getLibraryStaff() { return libraryStaff; }
    public void setLibraryStaff(LibraryStaff1 libraryStaff) { this.libraryStaff = libraryStaff; }

    public Supplier1 getSupplier() { return supplier; }
    public void setSupplier(Supplier1 supplier) { this.supplier = supplier; }

    public ImportInvoiceDetail1[] getImportInvoiceDetails() { return importInvoiceDetails; }
    public void setImportInvoiceDetails(ImportInvoiceDetail1[] importInvoiceDetails) { this.importInvoiceDetails = importInvoiceDetails; }
}
