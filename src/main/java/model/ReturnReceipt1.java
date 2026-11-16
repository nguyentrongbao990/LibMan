package model;

import java.time.LocalDate;

public class ReturnReceipt1 {
    private long id;
    private String returnCode;
    private LocalDate returnDate;
    private Reader1 reader;
    private LibraryStaff1 libraryStaff;
    private ReturnDetail1[] returnDetails;

    public ReturnReceipt1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getReturnCode() { return returnCode; }
    public void setReturnCode(String returnCode) { this.returnCode = returnCode; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Reader1 getReader() { return reader; }
    public void setReader(Reader1 reader) { this.reader = reader; }

    public LibraryStaff1 getLibraryStaff() { return libraryStaff; }
    public void setLibraryStaff(LibraryStaff1 libraryStaff) { this.libraryStaff = libraryStaff; }

    public ReturnDetail1[] getReturnDetails() { return returnDetails; }
    public void setReturnDetails(ReturnDetail1[] returnDetails) { this.returnDetails = returnDetails; }
}
