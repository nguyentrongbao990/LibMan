package dao;

import model.ManagementStaff1;
import java.sql.*;

/** Auth demo localhost, plaintext explicitly chosen by the user. */
public class StaffDAO1 extends DAO1 {
    public StaffDAO1() { super(); }

    public ManagementStaff1 login(String username, String password) {
        String sql = """
            SELECT s."id",s."staffCode",s."fullName",s."username"
            FROM "tblStaff" s JOIN "tblManagementStaff" m ON m."tblStaffid"=s."id"
            WHERE s."username"=? AND s."password"=?
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1,username); ps.setString(2,password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                ManagementStaff1 staff = new ManagementStaff1(); staff.setId(rs.getLong("id"));
                staff.setStaffCode(rs.getString("staffCode")); staff.setFullName(rs.getString("fullName"));
                staff.setUsername(rs.getString("username")); return staff;
            }
        } catch (SQLException e) { throw new IllegalStateException("Không xác thực được tài khoản quản lý.",e); }
    }
}
