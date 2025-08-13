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
            System.out.println("2. Add Student");
            System.out.println("3. Delete Student");
            System.out.println("4. View All Teachers");
            System.out.println("5. View All Students");
            System.out.println("6. View Daily Attendance Report");
            System.out.println("7. View Monthly Attendance Summary");
            System.out.println("8. Back to Main Menu");
            System.out.println("9. Exit Program");
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
                        StudentManager.addStudent(scanner);
                        break;
                    case 3:
                        StudentManager.deleteStudent(scanner);
                        break;
                    case 4:
                        viewAllTeachers();
                        break;
                    case 5:
                        StudentManager.viewAllStudents();
                        pressEnterToContinue();
                        break;
                    case 6:
                        AttendanceManager.viewDailyAttendanceReport(scanner);
                        break;
                    case 7:
                        AttendanceManager.viewMonthlyAttendanceSummary(scanner);
                        break;
                    case 8:
                        System.out.println("🔙 Returning to Main Menu...");
                        return;
                    case 9:
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