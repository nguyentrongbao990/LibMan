package model;

public abstract class Staff1 {
    private long id;
    private String staffCode;
    private String fullName;
    private String username;
    private String password;

    public Staff1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
