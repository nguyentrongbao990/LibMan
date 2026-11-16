package model;

public class Document1 {
    private long id;
    private String documentCode;
    private String title;
    private int publicationYear;
    private String edition;
    private DocumentAuthor1[] documentAuthors;
    private DocumentCategory1[] documentCategories;
    private Publisher1 publisher;

    public Document1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDocumentCode() { return documentCode; }
    public void setDocumentCode(String documentCode) { this.documentCode = documentCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public DocumentAuthor1[] getDocumentAuthors() { return documentAuthors; }
    public void setDocumentAuthors(DocumentAuthor1[] documentAuthors) { this.documentAuthors = documentAuthors; }

    public DocumentCategory1[] getDocumentCategories() { return documentCategories; }
    public void setDocumentCategories(DocumentCategory1[] documentCategories) { this.documentCategories = documentCategories; }

    public Publisher1 getPublisher() { return publisher; }
    public void setPublisher(Publisher1 publisher) { this.publisher = publisher; }
}
