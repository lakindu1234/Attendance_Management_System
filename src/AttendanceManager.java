// File: AttendanceManager.java
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void markAttendance(Scanner scanner, String teacherName) {
        String today = LocalDate.now().format(DATE_FORMATTER);

        while (true) {
            System.out.println("\n--- Mark Daily Attendance ---");
            System.out.println("Date: " + today);
            System.out.println("Teacher: " + teacherName);

            if (isAttendanceMarkedToday(today)) {
                System.out.println("⚠️ Attendance for today (" + today + ") is already marked!");
                System.out.print("Do you want to update existing attendance? (y/n): ");
                String choice = scanner.nextLine().trim();
                if (!choice.equalsIgnoreCase("y")) {
                    return;
                }
            }

            List<String[]> students = getStudentsList();
            if (students.isEmpty()) {
                System.out.println("❌ No students found! Please add students first.");
                pressEnterToContinue(scanner);
                return;
            }

            System.out.println("\n📝 Select students to mark attendance:");
            System.out.println("----------------------------------------");
            for (int i = 0; i < students.size(); i++) {
                System.out.println((i + 1) + ". " + students.get(i)[0] + " (" + students.get(i)[1] + ")");
            }

            System.out.println("----------------------------------------");
            System.out.println("Options:");
            System.out.println("• Enter student numbers separated by commas for PRESENT (e.g., 1,2,3)");
            System.out.println("• Type 'all' to mark all students present");
            System.out.println("• Type 'none' to mark all students absent");
            System.out.println("• Type 'individual' for individual marking");
            System.out.println("• Type 'back' to return");
            System.out.print("\nYour choice: ");

            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Map<String, String> attendanceMap = new HashMap<>();
                Map<String, String> usernameToName = new HashMap<>();
                for (String[] student : students) {
                    usernameToName.put(student[1], student[0]);
                }

                if (input.equalsIgnoreCase("all")) {
                    for (String[] student : students) {
                        attendanceMap.put(student[1], "Present");
                    }
                } else if (input.equalsIgnoreCase("none")) {
                    for (String[] student : students) {
                        attendanceMap.put(student[1], "Absent");
                    }
                } else if (input.equalsIgnoreCase("individual")) {
                    for (String[] student : students) {
                        System.out.print("Mark " + student[0] + " as (P)resent or (A)bsent? ");
                        String status = scanner.nextLine().trim().toLowerCase();
                        if (status.equals("p") || status.equals("present")) {
                            attendanceMap.put(student[1], "Present");
                        } else {
                            attendanceMap.put(student[1], "Absent");
                        }
                    }
                } else {
                    Set<Integer> presentNumbers = new HashSet<>();
                    String[] numbers = input.split(",");
                    for (String num : numbers) {
                        try {
                            int studentNum = Integer.parseInt(num.trim());
                            if (studentNum >= 1 && studentNum <= students.size()) {
                                presentNumbers.add(studentNum - 1);
                            } else {
                                System.out.println("⚠️ Invalid student number: " + studentNum);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ Invalid number format: " + num);
                        }
                    }

                    for (int i = 0; i < students.size(); i++) {
                        String status = presentNumbers.contains(i) ? "Present" : "Absent";
                        attendanceMap.put(students.get(i)[1], status);
                    }
                }

                if (saveAttendance(today, teacherName, attendanceMap, usernameToName)) {
                    System.out.println("✅ Attendance marked successfully for " + today + "!");
                    int presentCount = 0;
                    int absentCount = 0;
                    for (String status : attendanceMap.values()) {
                        if (status.equals("Present")) presentCount++;
                        else absentCount++;
                    }

                    System.out.println("\n📊 Attendance Summary:");
                    System.out.println("Present: " + presentCount + " students");
                    System.out.println("Absent: " + absentCount + " students");
                    System.out.println("Total: " + (presentCount + absentCount) + " students");
                } else {
                    System.out.println("❌ Error saving attendance!");
                }

            } catch (Exception e) {
                System.out.println("❌ Error processing attendance: " + e.getMessage());
            }

            System.out.print("\nDo you want to mark attendance for another day? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (!choice.equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    private static List<String[]> getStudentsList() {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT name, username FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new String[]{rs.getString("name"), rs.getString("username")});
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading students: " + e.getMessage());
        }
        return students;
    }

    private static boolean isAttendanceMarkedToday(String date) {
        String sql = "SELECT COUNT(*) AS count FROM attendance WHERE date = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("count") > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean saveAttendance(String date, String teacher,
                                          Map<String, String> attendanceMap,
                                          Map<String, String> usernameToName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Delete existing records for this date
            String deleteSql = "DELETE FROM attendance WHERE date = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, date);
                deleteStmt.executeUpdate();
            }

            // Insert new attendance records
            String insertSql = "INSERT INTO attendance (date, student_username, status, teacher_name) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<String, String> entry : attendanceMap.entrySet()) {
                    String username = entry.getKey();
                    String status = entry.getValue();
                    insertStmt.setString(1, date);
                    insertStmt.setString(2, username);
                    insertStmt.setString(3, status);
                    insertStmt.setString(4, teacher);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error saving attendance: " + e.getMessage());
            return false;
        }
    }

    public static void viewTodayAttendance(Scanner scanner) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        System.out.println("\n--- Today's Attendance (" + today + ") ---");
        viewAttendanceByDate(today);
        pressEnterToContinue(scanner);
    }

    public static void viewAttendanceByDate(String date) {
        String sql = "SELECT a.student_username, s.name, a.status, a.teacher_name " +
                "FROM attendance a " +
                "JOIN students s ON a.student_username = s.username " +
                "WHERE a.date = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();

            int presentCount = 0;
            int absentCount = 0;
            boolean found = false;

            System.out.println("📋 Attendance Status:");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                found = true;
                String name = rs.getString("name");
                String username = rs.getString("student_username");
                String status = rs.getString("status");
                String teacher = rs.getString("teacher_name");

                String statusIcon = status.equals("Present") ? "✅" : "❌";
                System.out.println(statusIcon + " " + name + " (" + username + ") - " + status + " (by " + teacher + ")");

                if (status.equals("Present")) presentCount++;
                else absentCount++;
            }

            if (!found) {
                System.out.println("No attendance marked for this date.");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("📊 Summary:");
                System.out.println("Present: " + presentCount + " students");
                System.out.println("Absent: " + absentCount + " students");
                System.out.println("Total: " + (presentCount + absentCount) + " students");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading attendance: " + e.getMessage());
        }
    }

    public static void viewMyTodayAttendance(String studentUsername) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        System.out.println("\n--- My Today's Attendance (" + today + ") ---");
        String sql = "SELECT status, teacher_name FROM attendance " +
                "WHERE date = ? AND student_username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, today);
            pstmt.setString(2, studentUsername);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                String teacher = rs.getString("teacher_name");
                String statusIcon = status.equals("Present") ? "✅" : "❌";
                System.out.println("📅 Date: " + today);
                System.out.println("📝 Status: " + statusIcon + " " + status);
                System.out.println("👨‍🏫 Marked by: " + teacher);
            } else {
                System.out.println("❌ No attendance marked for you today yet.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading attendance: " + e.getMessage());
        }
    }

    public static void viewMyAttendanceHistory(Scanner scanner, String studentUsername) {
        System.out.println("\n--- My Attendance History ---");
        System.out.print("Enter number of recent days to view (or 'back' to return): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            return;
        }

        try {
            int days = Integer.parseInt(input);
            if (days <= 0) {
                System.out.println("❌ Please enter a positive number.");
                return;
            }

            String sql = "SELECT date, status, teacher_name " +
                    "FROM attendance " +
                    "WHERE student_username = ? " +
                    "ORDER BY date DESC " +
                    "LIMIT ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentUsername);
                pstmt.setInt(2, days);
                ResultSet rs = pstmt.executeQuery();

                System.out.println("\n📋 Your Last " + days + " Days Attendance:");
                System.out.println("----------------------------------------");
                int count = 0;
                while (rs.next()) {
                    count++;
                    String date = rs.getString("date");
                    String status = rs.getString("status");
                    String teacher = rs.getString("teacher_name");
                    String statusIcon = status.equals("Present") ? "✅" : "❌";
                    System.out.println(statusIcon + " " + date + " - " + status + " (by " + teacher + ")");
                }

                if (count == 0) {
                    System.out.println("No attendance records found for you.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format.");
        } catch (SQLException e) {
            System.out.println("❌ Error reading attendance: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

    public static void viewMyAttendanceSummary(String studentUsername) {
        System.out.println("\n--- My Attendance Summary ---");
        String sql = "SELECT " +
                "COUNT(*) AS total_days, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_days " +
                "FROM attendance " +
                "WHERE student_username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentUsername);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalDays = rs.getInt("total_days");
                int presentDays = rs.getInt("present_days");
                int absentDays = totalDays - presentDays;
                double attendancePercentage = totalDays > 0 ?
                        (double) presentDays / totalDays * 100 : 0;

                System.out.println("📊 Overall Attendance Summary:");
                System.out.println("----------------------------------------");
                System.out.println("✅ Present Days: " + presentDays);
                System.out.println("❌ Absent Days: " + absentDays);
                System.out.println("📅 Total Days: " + totalDays);
                System.out.println("📈 Attendance Percentage: " + String.format("%.1f", attendancePercentage) + "%");
                System.out.println("----------------------------------------");

                if (attendancePercentage >= 90) {
                    System.out.println("🌟 Excellent attendance! Keep it up!");
                } else if (attendancePercentage >= 75) {
                    System.out.println("👍 Good attendance! Try to improve further.");
                } else {
                    System.out.println("⚠️ Low attendance! Please attend classes regularly.");
                }
            } else {
                System.out.println("❌ No attendance records found for you.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading attendance: " + e.getMessage());
        }
    }

    public static void viewAttendanceReports(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Attendance Reports ---");
            System.out.println("1. Daily Attendance Report");
            System.out.println("2. Student Attendance Summary");
            System.out.println("3. Monthly Attendance Report");
            System.out.println("4. Back to Dashboard");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewDailyAttendanceReport(scanner);
                        break;
                    case 2:
                        viewStudentAttendanceSummary(scanner);
                        break;
                    case 3:
                        viewMonthlyAttendanceSummary(scanner);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("❌ Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    public static void viewDailyAttendanceReport(Scanner scanner) {
        System.out.println("\n--- Daily Attendance Report ---");
        System.out.print("Enter date (YYYY-MM-DD) or 'today' for today's report (or 'back' to return): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            return;
        }

        String targetDate = input.equalsIgnoreCase("today") ?
                LocalDate.now().format(DATE_FORMATTER) : input;

        System.out.println("\n📋 Attendance Report for " + targetDate + ":");
        viewAttendanceByDate(targetDate);
        pressEnterToContinue(scanner);
    }

    public static void viewStudentAttendanceSummary(Scanner scanner) {
        System.out.println("\n--- Student Attendance Summary ---");
        System.out.print("Enter student username (or 'back' to return): ");
        String username = scanner.nextLine().trim();

        if (username.equalsIgnoreCase("back")) {
            return;
        }

        String studentName = getStudentName(username);
        if (studentName == null) {
            System.out.println("❌ Student not found!");
            pressEnterToContinue(scanner);
            return;
        }

        System.out.println("\n📊 Attendance Summary for " + studentName + " (" + username + "):");
        System.out.println("----------------------------------------");

        String sql = "SELECT " +
                "COUNT(*) AS total_days, " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_days " +
                "FROM attendance " +
                "WHERE student_username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalDays = rs.getInt("total_days");
                int presentDays = rs.getInt("present_days");
                int absentDays = totalDays - presentDays;
                double attendancePercentage = totalDays > 0 ?
                        (double) presentDays / totalDays * 100 : 0;

                System.out.println("✅ Present Days: " + presentDays);
                System.out.println("❌ Absent Days: " + absentDays);
                System.out.println("📅 Total Days: " + totalDays);
                System.out.println("📈 Attendance Percentage: " + String.format("%.1f", attendancePercentage) + "%");

                // Recent attendance
                String recentSql = "SELECT date, status FROM attendance " +
                        "WHERE student_username = ? " +
                        "ORDER BY date DESC LIMIT 5";
                try (PreparedStatement recentStmt = conn.prepareStatement(recentSql)) {
                    recentStmt.setString(1, username);
                    ResultSet recentRs = recentStmt.executeQuery();

                    System.out.println("\n📋 Recent Attendance (Last 5 records):");
                    while (recentRs.next()) {
                        String date = recentRs.getString("date");
                        String status = recentRs.getString("status");
                        String statusIcon = status.equals("Present") ? "✅" : "❌";
                        System.out.println(statusIcon + " " + date + " - " + status);
                    }
                }
            } else {
                System.out.println("No attendance records found for this student.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading attendance: " + e.getMessage());
        }
        pressEnterToContinue(scanner);
    }

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

    private static String getStudentName(String username) {
        String sql = "SELECT name FROM students WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("name") : username;
        } catch (SQLException e) {
            return username;
        }
    }

    private static void pressEnterToContinue(Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}