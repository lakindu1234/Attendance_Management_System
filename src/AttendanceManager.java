// File: AttendanceManager.java
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AttendanceManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public static void markAttendance(Scanner scanner, String teacherName) {
        System.out.println("\n--- Mark Daily Attendance ---");
        System.out.print("Enter date (YYYY-MM-DD) or 'today' for today (or 'back' to return): ");
        String dateInput = scanner.nextLine().trim();

        if (dateInput.equalsIgnoreCase("back")) return;
        String date = dateInput.equalsIgnoreCase("today") ?
                LocalDate.now().format(DATE_FORMATTER) : dateInput;

        try (Connection conn = DatabaseManager.getConnection()) {
            String selectSql = "SELECT username, name FROM students";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {

                while (rs.next()) {
                    String username = rs.getString("username");
                    String name = rs.getString("name");

                    while (true) {
                        System.out.printf("%s (%s): Enter status [P/A/L] (Present/Absent/Late): ", name, username);
                        String statusInput = scanner.nextLine().trim().toUpperCase();

                        if (statusInput.equals("P") || statusInput.equals("A") || statusInput.equals("L")) {
                            String status = "";
                            switch (statusInput) {
                                case "P": status = "Present"; break;
                                case "A": status = "Absent"; break;
                                case "L": status = "Late"; break;
                            }

                            // Insert attendance
                            String insertSql = "INSERT INTO attendance (date, student_username, status, teacher_name) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setString(1, date);
                                insertStmt.setString(2, username);
                                insertStmt.setString(3, status);
                                insertStmt.setString(4, teacherName);
                                insertStmt.executeUpdate();
                            }
                            break;
                        } else {
                            System.out.println("Invalid input! Use P, A, or L.");
                        }
                    }
                }
                System.out.println("✅ Attendance marked successfully!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

    public static void viewTodayAttendance(Scanner scanner) {
        viewAttendanceForDate(scanner, LocalDate.now().format(DATE_FORMATTER));
    }

    private static void viewAttendanceForDate(Scanner scanner, String date) {
        System.out.println("\n--- Attendance for " + date + " ---");
        String sql = "SELECT a.student_username, s.name, a.status, a.teacher_name " +
                "FROM attendance a JOIN students s ON a.student_username = s.username " +
                "WHERE a.date = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.printf("%d. %s (%s): %s (by %s)\n",
                            count,
                            rs.getString("name"),
                            rs.getString("student_username"),
                            rs.getString("status"),
                            rs.getString("teacher_name"));
                }
                if (count == 0) System.out.println("No records found");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

    public static void viewDailyAttendanceReport(Scanner scanner) {
        System.out.print("\nEnter date (YYYY-MM-DD) or 'today': ");
        String input = scanner.nextLine().trim();
        String date = input.equalsIgnoreCase("today") ?
                LocalDate.now().format(DATE_FORMATTER) : input;
        viewAttendanceForDate(scanner, date);
    }

    public static void viewMonthlyAttendanceSummary(Scanner scanner) {
        System.out.print("\nEnter month (YYYY-MM) or 'current': ");
        String input = scanner.nextLine().trim();
        String month = input.equalsIgnoreCase("current") ?
                LocalDate.now().format(MONTH_FORMATTER) : input;

        System.out.println("\n📅 Monthly Summary for " + month);

        String sql = "SELECT student_username, " +
                "COUNT(*) AS total_days, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_days " +
                "FROM attendance " +
                "WHERE strftime('%Y-%m', date) = ? " +
                "GROUP BY student_username";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("student_username");
                    int total = rs.getInt("total_days");
                    int present = rs.getInt("present_days");
                    double percentage = total > 0 ? (present * 100.0) / total : 0;

                    System.out.printf("%s: %d/%d days (%.1f%%)\n",
                            getStudentName(username),
                            present, total, percentage);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

    private static String getStudentName(String username) {
        String sql = "SELECT name FROM students WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : "Unknown";
            }
        } catch (SQLException e) {
            return "Error";
        }
    }

    public static void viewAttendanceReports(Scanner scanner) {
        while (true) {
            System.out.println("\n1. Daily Report");
            System.out.println("2. Monthly Summary");
            System.out.println("3. Back");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": viewDailyAttendanceReport(scanner); break;
                case "2": viewMonthlyAttendanceSummary(scanner); break;
                case "3": return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    public static void viewMyTodayAttendance(String username) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String sql = "SELECT status, teacher_name FROM attendance " +
                "WHERE date = ? AND student_username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, today);
            pstmt.setString(2, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("Today's status: %s (by %s)\n",
                            rs.getString("status"),
                            rs.getString("teacher_name"));
                } else {
                    System.out.println("No attendance recorded today");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void viewMyAttendanceHistory(Scanner scanner, String username) {
        System.out.print("\nEnter month (YYYY-MM) or 'all': ");
        String month = scanner.nextLine().trim();

        String sql = "SELECT date, status, teacher_name FROM attendance " +
                "WHERE student_username = ? " +
                (month.equalsIgnoreCase("all") ? "" : "AND strftime('%Y-%m', date) = ? ") +
                "ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            if (!month.equalsIgnoreCase("all")) pstmt.setString(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.printf("%d. %s: %s (by %s)\n",
                            count,
                            rs.getString("date"),
                            rs.getString("status"),
                            rs.getString("teacher_name"));
                }
                if (count == 0) System.out.println("No records found");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

    public static void viewMyAttendanceSummary(String username) {
        String sql = "SELECT COUNT(*) AS total, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present " +
                "FROM attendance WHERE student_username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int present = rs.getInt("present");
                    double percentage = total > 0 ? (present * 100.0) / total : 0;
                    System.out.printf("Total days: %d\nPresent: %d (%.1f%%)\n",
                            total, present, percentage);
                } else {
                    System.out.println("No attendance records");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void pressEnterToContinue(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}