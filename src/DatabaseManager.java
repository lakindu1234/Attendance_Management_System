// File: DatabaseManager.java
import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sql极速赛车开奖直播网
    We are generating the code. Let me complete the `DatabaseManager` class.

            ```java
// File: DatabaseManager.java
import java.sql.*;

    public class DatabaseManager {
        private static final String DB_URL = "jdbc:sqlite:attendance.db";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL);
        }

        public static void initializeDatabase() throws SQLException {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                // Create teachers table
                stmt.execute("CREATE TABLE IF NOT EXISTS teachers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "username TEXT UNIQUE NOT NULL," +
                        "password TEXT NOT NULL)");

                // Create students table
                stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "username TEXT UNIQUE NOT NULL," +
                        "password TEXT NOT NULL)");

                // Create attendance table
                stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date TEXT NOT NULL," +
                        "student_username TEXT NOT NULL," +
                        "status TEXT NOT NULL," +
                        "teacher_name TEXT NOT NULL)");
            }
        }
    }