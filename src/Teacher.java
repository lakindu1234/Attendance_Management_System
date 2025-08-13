// File: Teacher.java
import java.sql.*;
import java.util.Scanner;

public class Teacher {
    private Scanner scanner;
    private String teacherName;

    public Teacher(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        while (true) {
            System.out.println("\n--- Teacher Login ---");
            System.out.print("Enter Teacher Username (or 'back' to return to main menu): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Teacher Password (or 'back' to return): ");
            String password = scanner.nextLine().trim();

            if (password.equalsIgnoreCase("back")) {
                continue;
            }

            String name = authenticate(username, password);
            if (name != null) {
                this.teacherName = name;
                dashboard();
                break;
            } else {
                System.out.println("❌ Invalid teacher credentials! Try again or type 'back' to return.");
            }
        }
    }

    // Changed from public to private
    private String authenticate(String username, String password) {
        String sql = "SELECT name FROM teachers WHERE username = ? AND password = ?";
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
            System.out.println("❌ Error reading teacher data: " + e.getMessage());
        }
        return null;
    }

    public void dashboard() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         TEACHER DASHBOARD");
            System.out.println("========================================");
            System.out.println("1. Mark Daily Attendance");
            System.out.println("2. View Today's Attendance");
            System.out.println("3. View Students List");
            System.out.println("4. Add Student");
            System.out.println("5. Delete Student");
            System.out.println("6. View Attendance Reports");
            System.out.println("7. Back to Main Menu");
            System.out.println("8. Exit Program");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        AttendanceManager.markAttendance(scanner, teacherName);
                        break;
                    case 2:
                        AttendanceManager.viewTodayAttendance(scanner);
                        break;
                    case 3:
                        StudentManager.viewAllStudents();
                        pressEnterToContinue();
                        break;
                    case 4:
                        StudentManager.addStudent(scanner);
                        break;
                    case 5:
                        StudentManager.deleteStudent(scanner);
                        break;
                    case 6:
                        AttendanceManager.viewAttendanceReports(scanner);
                        break;
                    case 7:
                        System.out.println("🔙 Returning to Main Menu...");
                        return;
                    case 8:
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

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}