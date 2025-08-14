// File: Admin.java
import java.sql.*;
import java.util.Scanner;

public class Admin {
    private Scanner scanner;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public Admin(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        while (true) {
            System.out.println("\n--- Admin Login ---");
            System.out.print("Enter Admin Username (or 'back' to return to main menu): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Admin Password (or 'back' to return): ");
            String password = scanner.nextLine().trim();

            if (password.equalsIgnoreCase("back")) {
                continue;
            }

            if (authenticate(username, password)) {
                System.out.println("✅ Admin login successful!");
                dashboard();
                break;
            } else {
                System.out.println("❌ Invalid admin credentials! Try again or type 'back' to return.");
            }
        }
    }

    private boolean authenticate(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public void dashboard() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         ADMIN DASHBOARD");
            System.out.println("========================================");
            System.out.println("1. Add Teacher");
            System.out.println("2. Delete Teacher");
            System.out.println("3. Add Student");
            System.out.println("4. Delete Student");
            System.out.println("5. View All Teachers");
            System.out.println("6. View All Students");
            System.out.println("7. View Daily Attendance Report");
            System.out.println("8. View Monthly Attendance Summary");
            System.out.println("9. Back to Main Menu");
            System.out.println("10. Exit Program");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addTeacher();
                        break;
                    case 2:
                        deleteTeacher();
                        break;
                    case 3:
                        StudentManager.addStudent(scanner);
                        break;
                    case 4:
                        StudentManager.deleteStudent(scanner);
                        break;
                    case 5:
                        viewAllTeachers();
                        break;
                    case 6:
                        StudentManager.viewAllStudents();
                        pressEnterToContinue();
                        break;
                    case 7:
                        AttendanceManager.viewDailyAttendanceReport(scanner);
                        break;
                    case 8:
                        AttendanceManager.viewMonthlyAttendanceSummary(scanner);
                        break;
                    case 9:
                        System.out.println("🔙 Returning to Main Menu...");
                        return;
                    case 10:
                        System.out.println("Thank you for using Attendance Management System!");
                        System.out.println("Goodbye! 👋");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Please try again.");
                        pressEnterToContinue();
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                scanner.nextLine();
                pressEnterToContinue();
            }
        }
    }

    private void addTeacher() {
        while (true) {
            System.out.println("\n--- Add New Teacher ---");
            try {
                System.out.print("Enter Teacher Name (or 'back' to return): ");
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

                if (isTeacherUsernameExists(username)) {
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

                String sql = "INSERT INTO teachers (name, username, password) VALUES (?, ?, ?)";
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);
                    pstmt.executeUpdate();
                    System.out.println("✅ Teacher added successfully!");
                } catch (SQLException e) {
                    System.out.println("❌ Error adding teacher: " + e.getMessage());
                }

                System.out.print("Do you want to add another teacher? (y/n): ");
                String choice = scanner.nextLine().trim();
                if (!choice.equalsIgnoreCase("y")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                pressEnterToContinue();
                break;
            }
        }
    }

    private void deleteTeacher() {
        while (true) {
            System.out.println("\n--- Delete Teacher ---");

            // First, show all teachers
            System.out.println("Current Teachers:");
            viewAllTeachersForDeletion();

            System.out.print("\nEnter the username of the teacher to delete (or 'back' to return): ");
            String targetUsername = scanner.nextLine().trim();

            if (targetUsername.equalsIgnoreCase("back")) {
                return;
            }

            if (targetUsername.isEmpty()) {
                System.out.println("❌ Username cannot be empty!");
                continue;
            }

            // Check if teacher exists and show confirmation
            if (confirmTeacherDeletion(targetUsername)) {
                try {
                    String sql = "DELETE FROM teachers WHERE username = ?";
                    try (Connection conn = DatabaseManager.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, targetUsername);
                        int rowsAffected = pstmt.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("✅ Teacher deleted successfully.");
                        } else {
                            System.out.println("⚠️ Teacher not found.");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("❌ Error deleting teacher: " + e.getMessage());
                }
            } else {
                System.out.println("❌ Teacher deletion cancelled or teacher not found.");
            }

            System.out.print("Do you want to delete another teacher? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (!choice.equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    private void viewAllTeachersForDeletion() {
        String sql = "SELECT name, username FROM teachers";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            System.out.println("📋 Teachers List:");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                count++;
                System.out.println(count + ". Name: " + rs.getString("name") +
                        " | Username: " + rs.getString("username"));
            }
            if (count == 0) {
                System.out.println("No teachers found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading teachers: " + e.getMessage());
        }
    }

    private boolean confirmTeacherDeletion(String username) {
        String sql = "SELECT name FROM teachers WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                System.out.println("\nTeacher found: " + name + " (" + username + ")");
                System.out.print("Are you sure you want to delete this teacher? (y/n): ");
                String confirmation = scanner.nextLine().trim();
                return confirmation.equalsIgnoreCase("y");
            } else {
                System.out.println("❌ Teacher with username '" + username + "' not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error checking teacher: " + e.getMessage());
            return false;
        }
    }

    private boolean isTeacherUsernameExists(String username) {
        String sql = "SELECT COUNT(*) AS count FROM teachers WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("count") > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private void viewAllTeachers() {
        System.out.println("\n--- All Teachers ---");
        String sql = "SELECT name, username FROM teachers";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            System.out.println("📋 Teachers List:");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                count++;
                System.out.println(count + ". Name: " + rs.getString("name") +
                        " | Username: " + rs.getString("username"));
            }
            if (count == 0) {
                System.out.println("No teachers found.");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("Total Teachers: " + count);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading teachers: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}