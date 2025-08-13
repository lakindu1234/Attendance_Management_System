// File: StudentManager.java
import java.sql.*;
import java.util.Scanner;

public class StudentManager {

    public static void addStudent(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Add New Student ---");
            try {
                System.out.print("Enter Student Name (or 'back' to return): ");
                String name = scanner.nextLine().trim();

                if (name.equalsIgnoreCase("back")) {
                    return;
                }

                if (name.isEmpty()) {
                    System.out.println("❌ Name cannot be empty!");
                    continue;
                }

                System.out.print("Enter Username (or 'back' to return): ");
                String username = scanner.nextLine().trim();

                if (username.equalsIgnoreCase("back")) {
                    return;
                }

                if (username.isEmpty()) {
                    System.out.println("❌ Username cannot be empty!");
                    continue;
                }

                if (isUsernameExists(username)) {
                    System.out.println("❌ Username already exists! Please choose a different username.");
                    continue;
                }

                System.out.print("Enter Password (or 'back' to return): ");
                String password = scanner.nextLine().trim();

                if (password.equalsIgnoreCase("back")) {
                    return;
                }

                if (password.isEmpty()) {
                    System.out.println("❌ Password cannot be empty!");
                    continue;
                }

                String sql = "INSERT INTO students (name, username, password) VALUES (?, ?, ?)";
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);
                    pstmt.executeUpdate();
                    System.out.println("✅ Student added successfully!");
                } catch (SQLException e) {
                    System.out.println("❌ Error adding student: " + e.getMessage());
                }

                System.out.print("Do you want to add another student? (y/n): ");
                String choice = scanner.nextLine().trim();
                if (!choice.equalsIgnoreCase("y")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                pressEnterToContinue(scanner);
                break;
            }
        }
    }

    public static void deleteStudent(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Delete Student ---");
            System.out.print("Enter the username of the student to delete (or 'back' to return): ");
            String targetUsername = scanner.nextLine().trim();

            if (targetUsername.equalsIgnoreCase("back")) {
                return;
            }

            if (targetUsername.isEmpty()) {
                System.out.println("❌ Username cannot be empty!");
                continue;
            }

            try {
                String sql = "DELETE FROM students WHERE username = ?";
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, targetUsername);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("✅ Student deleted successfully.");
                    } else {
                        System.out.println("⚠️ Student not found.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ Error deleting student: " + e.getMessage());
            }

            System.out.print("Do you want to delete another student? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (!choice.equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    public static void viewAllStudents() {
        System.out.println("\n--- All Students ---");
        String sql = "SELECT name, username FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            System.out.println("👥 Students List:");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                count++;
                System.out.println(count + ". Name: " + rs.getString("name") +
                        " | Username: " + rs.getString("username"));
            }
            if (count == 0) {
                System.out.println("No students found.");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("Total Students: " + count);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading students: " + e.getMessage());
        }
    }

    private static boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) AS count FROM students WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("count") > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void pressEnterToContinue(Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}