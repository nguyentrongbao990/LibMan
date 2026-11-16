package model;

import java.time.LocalDate;

public class Loan1 {
    private long id;
    private String loanCode;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private Reader1 reader;
    private LoanDetail1[] loanDetails;

    public Loan1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getLoanCode() { return loanCode; }
    public void setLoanCode(String loanCode) { this.loanCode = loanCode; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Reader1 getReader() { return reader; }
    public void setReader(Reader1 reader) { this.reader = reader; }

    public LoanDetail1[] getLoanDetails() { return loanDetails; }
    public void setLoanDetails(LoanDetail1[] loanDetails) { this.loanDetails = loanDetails; }
}
