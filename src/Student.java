// File: Student.java
import java.sql.*;
import java.util.Scanner;

public class Student {
    private Scanner scanner;
    private String studentName;
    private String studentUsername;

    public Student(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        while (true) {
            System.out.println("\n--- Student Login ---");
            System.out.print("Enter Student Username (or 'back' to return to main menu): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Student Password (or 'back' to return): ");
            String password = scanner.nextLine().trim();

            if (password.equalsIgnoreCase("back")) {
                continue;
            }

            String name = authenticate(username, password);
            if (name != null) {
                this.studentName = name;
                this.studentUsername = username;
                dashboard();
                break;
            } else {
                System.out.println("❌ Invalid student credentials! Try again or type 'back' to return.");
            }
        }
    }

    private String authenticate(String username, String password) {
        String sql = "SELECT name FROM students WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                System.out.println("✅ Welcome, " + name + "!");
                return name;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading student data: " + e.getMessage());
        }
        return null;
    }

    private void dashboard() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         STUDENT DASHBOARD");
            System.out.println("========================================");
            System.out.println("1. View My Today's Attendance");
            System.out.println("2. View My Attendance History");
            System.out.println("3. View My Profile");
            System.out.println("4. View My Attendance Summary");
            System.out.println("5. Back to Main Menu");
            System.out.println("6. Exit Program");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        AttendanceManager.viewMyTodayAttendance(studentUsername);
                        pressEnterToContinue();
                        break;
                    case 2:
                        AttendanceManager.viewMyAttendanceHistory(scanner, studentUsername);
                        break;
                    case 3:
                        viewProfile();
                        break;
                    case 4:
                        AttendanceManager.viewMyAttendanceSummary(studentUsername);
                        pressEnterToContinue();
                        break;
                    case 5:
                        System.out.println("🔙 Returning to Main Menu...");
                        return;
                    case 6:
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

    private void viewProfile() {
        System.out.println("\n--- My Profile ---");
        String sql = "SELECT name, username FROM students WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("👤 Profile Information:");
                System.out.println("----------------------------------------");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Status: Active Student");
                System.out.println("----------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading profile: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
