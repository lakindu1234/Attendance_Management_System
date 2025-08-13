// File: AttendanceManager.java
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ... [rest of the code remains the same] ...

    public static void viewMonthlyAttendanceSummary(Scanner scanner) {
        System.out.println("\n--- Monthly Attendance Summary ---");
        System.out.print("Enter month (YYYY-MM) or 'current' for current month (or 'back' to return): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            return;
        }

        String targetMonth = input.equalsIgnoreCase("current") ?
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")) : input;

        System.out.println("\n📅 Monthly Attendance Summary for " + targetMonth + ":");
        System.out.println("========================================");

        String sql = "SELECT " +
                "student_username, " +
                "COUNT(*) AS total_days, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_days " +
                "FROM attendance " +
                "WHERE strftime('%Y-%m', date) = ? " +
                "GROUP BY student_username";

        String distinctDatesSql = "SELECT COUNT(DISTINCT date) AS school_days " +
                "FROM attendance " +
                "WHERE strftime('%Y-%m', date) = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement dateStmt = conn.prepareStatement(distinctDatesSql)) {

            pstmt.setString(1, targetMonth);
            dateStmt.setString(1, targetMonth);

            ResultSet dateRs = dateStmt.executeQuery();
            int schoolDays = dateRs.next() ? dateRs.getInt("school_days") : 0;
            System.out.println("Total school days in month: " + schoolDays);
            System.out.println("----------------------------------------");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("student_username");
                int totalDays = rs.getInt("total_days");
                int presentDays = rs.getInt("present_days");
                double percentage = totalDays > 0 ? (double) presentDays / totalDays * 100 : 0;
                String studentName = getStudentName(username);

                System.out.printf("👤 %s (%s): %d/%d days (%.1f%%)\n",
                        studentName, username, presentDays, totalDays, percentage);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading attendance: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

    // ... [rest of the code] ...
}