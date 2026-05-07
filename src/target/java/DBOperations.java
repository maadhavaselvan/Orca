package target.java;
import java.sql.*;

public class DBOperations {

    private static final String URL  = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASS = "your_password";

    // 🔍 SELECT
    public static void selectData() throws SQLException {
        String sql = "SELECT id, name, email FROM users";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("name") + " | " +
                        rs.getString("email"));
            }
        }
    }

    // ➕ INSERT (using PreparedStatement to prevent SQL injection)
    public static void insertData(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            int rows = pstmt.executeUpdate();
            System.out.println("✅ Inserted " + rows + " row(s)");
        }
    }

    // ✏️ UPDATE
    public static void updateData(int id, String newEmail) throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newEmail);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            System.out.println("✅ Updated " + rows + " row(s)");
        }
    }

    // 🗑️ DELETE
    public static void deleteData(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            System.out.println("✅ Deleted " + rows + " row(s)");
        }
    }
}