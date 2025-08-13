// File: DatabaseManager.java
import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:attendance.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables
            stmt.execute("CREATE TABLE IF NOT EXISTS teachers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "date TEXT NOT NULL," +
                    "student_username TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "teacher_name TEXT NOT NULL)");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}