package model;

import java.time.LocalDate;

public class ReaderCard1 {
    private long id;
    private String cardCode;
    private LocalDate issueDate;
    private String status;

    public ReaderCard1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCardCode() { return cardCode; }
    public void setCardCode(String cardCode) { this.cardCode = cardCode; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
