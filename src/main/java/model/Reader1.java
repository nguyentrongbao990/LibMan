package model;

import java.time.LocalDate;

public class Reader1 {
    private long id;
    private String cccd;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private ReaderCard1 readerCard;

    public Reader1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ReaderCard1 getReaderCard() { return readerCard; }
    public void setReaderCard(ReaderCard1 readerCard) { this.readerCard = readerCard; }
}
