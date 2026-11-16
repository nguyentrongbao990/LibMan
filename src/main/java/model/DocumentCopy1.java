package model;

public class DocumentCopy1 {
    private long id;
    private String copyCode;
    private String status;
    private String condition;
    private Document1 document;

    public DocumentCopy1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCopyCode() { return copyCode; }
    public void setCopyCode(String copyCode) { this.copyCode = copyCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public Document1 getDocument() { return document; }
    public void setDocument(Document1 document) { this.document = document; }
}
