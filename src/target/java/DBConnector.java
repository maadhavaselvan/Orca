package target.java;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    // ✅ Update these values for your database
    private static final String URL      = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER     = "your_username";
    private static final String PASSWORD = "your_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connected to database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
