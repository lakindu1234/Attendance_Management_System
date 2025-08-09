// File: AttendanceManager.java
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Mark attendance for students
    public static void markAttendance(Scanner scanner, String teacherName) {
        String today = LocalDate.now().format(DATE_FORMATTER);

        while (true) {
            System.out.println("\n--- Mark Daily Attendance ---");
            System.out.println("Date: " + today);
            System.out.println("Teacher: " + teacherName);

            // Check if attendance already marked for today
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

            // Display students list with numbers
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

                if (input.equalsIgnoreCase("all")) {
                    // Mark all present
                    for (String[] student : students) {
                        attendanceMap.put(student[1], "Present");
                    }
                } else if (input.equalsIgnoreCase("none")) {
                    // Mark all absent
                    for (String[] student : students) {
                        attendanceMap.put(student[1], "Absent");
                    }
                } else if (input.equalsIgnoreCase("individual")) {
                    // Individual marking
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
                    // Parse comma-separated numbers
                    Set<Integer> presentNumbers = new HashSet<>();
                    String[] numbers = input.split(",");

                    for (String num : numbers) {
                        try {
                            int studentNum = Integer.parseInt(num.trim());
                            if (studentNum >= 1 && studentNum <= students.size()) {
                                presentNumbers.add(studentNum - 1); // Convert to 0-based index
                            } else {
                                System.out.println("⚠️ Invalid student number: " + studentNum);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ Invalid number format: " + num);
                        }
                    }

                    // Mark attendance based on selected numbers
                    for (int i = 0; i < students.size(); i++) {
                        String status = presentNumbers.contains(i) ? "Present" : "Absent";
                        attendanceMap.put(students.get(i)[1], status);
                    }
                }

                // Save attendance
                if (saveAttendance(today, teacherName, attendanceMap)) {
                    System.out.println("✅ Attendance marked successfully for " + today + "!");

                    // Show summary
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

    // Get list of all students
    private static List<String[]> getStudentsList() {
        List<String[]> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    students.add(new String[]{parts[0], parts[1]}); // name, username
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading students file: " + e.getMessage());
        }
        return students;
    }

    // Check if attendance is already marked for today
    private static boolean isAttendanceMarkedToday(String date) {
        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(date + ",")) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File doesn't exist, so no attendance marked yet
            return false;
        }
        return false;
    }

    // Save attendance to file
    private static boolean saveAttendance(String date, String teacher, Map<String, String> attendanceMap) {
        try {
            // Remove existing attendance for the same date first
            removeAttendanceForDate(date);

            // Append new attendance
            FileWriter writer = new FileWriter("attendance.txt", true);
            for (Map.Entry<String, String> entry : attendanceMap.entrySet()) {
                String username = entry.getKey();
                String status = entry.getValue();
                writer.write(date + "," + username + "," + status + "," + teacher + "\n");
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error saving attendance: " + e.getMessage());
            return false;
        }
    }

    // Remove attendance for a specific date
    private static void removeAttendanceForDate(String date) {
        File inputFile = new File("attendance.txt");
        File tempFile = new File("attendance_temp.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                FileWriter writer = new FileWriter(tempFile)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(date + ",")) {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            // File might not exist, that's okay
        }

        // Replace original file
        try {
            if (inputFile.exists()) {
                inputFile.delete();
            }
            if (tempFile.exists()) {
                tempFile.renameTo(inputFile);
            }
        } catch (Exception e) {
            // Handle silently
        }
    }

    // View today's attendance
    public static void viewTodayAttendance(Scanner scanner) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        System.out.println("\n--- Today's Attendance (" + today + ") ---");

        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            int presentCount = 0;
            int absentCount = 0;
            boolean found = false;

            System.out.println("📋 Attendance Status:");
            System.out.println("----------------------------------------");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(today)) {
                    found = true;
                    String username = parts[1];
                    String status = parts[2];
                    String studentName = getStudentName(username);

                    String statusIcon = status.equals("Present") ? "✅" : "❌";
                    System.out.println(statusIcon + " " + studentName + " (" + username + ") - " + status);

                    if (status.equals("Present")) presentCount++;
                    else absentCount++;
                }
            }

            if (!found) {
                System.out.println("No attendance marked for today yet.");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("📊 Summary:");
                System.out.println("Present: " + presentCount + " students");
                System.out.println("Absent: " + absentCount + " students");
                System.out.println("Total: " + (presentCount + absentCount) + " students");
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }

        pressEnterToContinue(scanner);
    }

    // View student's today attendance
    public static void viewMyTodayAttendance(String studentUsername) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        System.out.println("\n--- My Today's Attendance (" + today + ") ---");

        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(today) && parts[1].equals(studentUsername)) {
                    found = true;
                    String status = parts[2];
                    String teacher = parts[3];

                    String statusIcon = status.equals("Present") ? "✅" : "❌";
                    System.out.println("📅 Date: " + today);
                    System.out.println("📝 Status: " + statusIcon + " " + status);
                    System.out.println("👨‍🏫 Marked by: " + teacher);
                    break;
                }
            }

            if (!found) {
                System.out.println("❌ No attendance marked for you today yet.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }
    }

    // View student's attendance history
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

            BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"));
            String line;
            List<String[]> myAttendance = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[1].equals(studentUsername)) {
                    myAttendance.add(parts);
                }
            }
            reader.close();

            // Sort by date (descending)
            myAttendance.sort((a, b) -> b[0].compareTo(a[0]));

            System.out.println("\n📋 Your Last " + Math.min(days, myAttendance.size()) + " Days Attendance:");
            System.out.println("----------------------------------------");

            int count = 0;
            for (String[] record : myAttendance) {
                if (count >= days) break;

                String date = record[0];
                String status = record[2];
                String teacher = record[3];
                String statusIcon = status.equals("Present") ? "✅" : "❌";

                System.out.println(statusIcon + " " + date + " - " + status + " (by " + teacher + ")");
                count++;
            }

            if (myAttendance.isEmpty()) {
                System.out.println("No attendance records found for you.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format.");
        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }

        pressEnterToContinue(scanner);
    }

    // View student's attendance summary
    public static void viewMyAttendanceSummary(String studentUsername) {
        System.out.println("\n--- My Attendance Summary ---");

        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            int totalDays = 0;
            int presentDays = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[1].equals(studentUsername)) {
                    totalDays++;
                    if (parts[2].equals("Present")) {
                        presentDays++;
                    }
                }
            }

            if (totalDays == 0) {
                System.out.println("❌ No attendance records found for you.");
                return;
            }

            int absentDays = totalDays - presentDays;
            double attendancePercentage = (double) presentDays / totalDays * 100;

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

        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }
    }

    // View attendance reports for teachers
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

    // View daily attendance report
    public static void viewDailyAttendanceReport(Scanner scanner) {
        System.out.println("\n--- Daily Attendance Report ---");
        System.out.print("Enter date (YYYY-MM-DD) or 'today' for today's report (or 'back' to return): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            return;
        }

        String targetDate;
        if (input.equalsIgnoreCase("today")) {
            targetDate = LocalDate.now().format(DATE_FORMATTER);
        } else {
            targetDate = input;
        }

        System.out.println("\n📋 Attendance Report for " + targetDate + ":");
        System.out.println("----------------------------------------");

        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            int presentCount = 0;
            int absentCount = 0;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(targetDate)) {
                    found = true;
                    String username = parts[1];
                    String status = parts[2];
                    String studentName = getStudentName(username);

                    String statusIcon = status.equals("Present") ? "✅" : "❌";
                    System.out.println(statusIcon + " " + studentName + " (" + username + ") - " + status);

                    if (status.equals("Present")) presentCount++;
                    else absentCount++;
                }
            }

            if (!found) {
                System.out.println("No attendance records found for " + targetDate);
            } else {
                System.out.println("----------------------------------------");
                System.out.println("📊 Summary for " + targetDate + ":");
                System.out.println("Present: " + presentCount + " students");
                System.out.println("Absent: " + absentCount + " students");
                System.out.println("Total: " + (presentCount + absentCount) + " students");

                if (presentCount + absentCount > 0) {
                    double percentage = (double) presentCount / (presentCount + absentCount) * 100;
                    System.out.println("Attendance Rate: " + String.format("%.1f", percentage) + "%");
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }

        pressEnterToContinue(scanner);
    }

    // View student attendance summary
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

        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            int totalDays = 0;
            int presentDays = 0;
            List<String> recentAttendance = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[1].equals(username)) {
                    totalDays++;
                    if (parts[2].equals("Present")) {
                        presentDays++;
                    }
                    recentAttendance.add(parts[0] + " - " + parts[2]);
                }
            }

            if (totalDays == 0) {
                System.out.println("No attendance records found for this student.");
            } else {
                int absentDays = totalDays - presentDays;
                double attendancePercentage = (double) presentDays / totalDays * 100;

                System.out.println("✅ Present Days: " + presentDays);
                System.out.println("❌ Absent Days: " + absentDays);
                System.out.println("📅 Total Days: " + totalDays);
                System.out.println("📈 Attendance Percentage: " + String.format("%.1f", attendancePercentage) + "%");

                // Show recent 5 attendance records
                System.out.println("\n📋 Recent Attendance (Last 5 records):");
                Collections.reverse(recentAttendance);
                for (int i = 0; i < Math.min(5, recentAttendance.size()); i++) {
                    System.out.println("• " + recentAttendance.get(i));
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }

        pressEnterToContinue(scanner);
    }

    // View monthly attendance summary
    public static void viewMonthlyAttendanceSummary(Scanner scanner) {
        System.out.println("\n--- Monthly Attendance Summary ---");
        System.out.print("Enter month (YYYY-MM) or 'current' for current month (or 'back' to return): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            return;
        }

        String targetMonth;
        if (input.equalsIgnoreCase("current")) {
            targetMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } else {
            targetMonth = input;
        }

        System.out.println("\n📅 Monthly Attendance Summary for " + targetMonth + ":");
        System.out.println("========================================");

        try (BufferedReader reader = new BufferedReader(new FileReader("attendance.txt"))) {
            String line;
            Map<String, int[]> studentStats = new HashMap<>(); // username -> [present, total]
            Set<String> datesInMonth = new HashSet<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].startsWith(targetMonth)) {
                    datesInMonth.add(parts[0]);
                    String username = parts[1];
                    String status = parts[2];

                    studentStats.putIfAbsent(username, new int[2]);
                    studentStats.get(username)[1]++; // total days
                    if (status.equals("Present")) {
                        studentStats.get(username)[0]++; // present days
                    }
                }
            }

            if (studentStats.isEmpty()) {
                System.out.println("No attendance records found for " + targetMonth);
            } else {
                System.out.println("Total school days in month: " + datesInMonth.size());
                System.out.println("----------------------------------------");

                for (Map.Entry<String, int[]> entry : studentStats.entrySet()) {
                    String username = entry.getKey();
                    int[] stats = entry.getValue();
                    int present = stats[0];
                    int total = stats[1];
                    double percentage = (double) present / total * 100;
                    String studentName = getStudentName(username);

                    System.out.printf("👤 %s (%s): %d/%d days (%.1f%%)\n",
                            studentName, username, present, total, percentage);
                }

                // Overall statistics
                int totalStudentDays = 0;
                int totalPresentDays = 0;
                for (int[] stats : studentStats.values()) {
                    totalPresentDays += stats[0];
                    totalStudentDays += stats[1];
                }

                if (totalStudentDays > 0) {
                    double overallPercentage = (double) totalPresentDays / totalStudentDays * 100;
                    System.out.println("----------------------------------------");
                    System.out.printf("📊 Overall Class Attendance: %.1f%%\n", overallPercentage);
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading attendance file: " + e.getMessage());
        }

        pressEnterToContinue(scanner);
    }

    // Helper method to get student name by username
    private static String getStudentName(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[1].equals(username)) {
                    return parts[0];
                }
            }
        } catch (IOException e) {
            // Handle silently
        }
        return username; // Return username if name not found
    }

    // Helper method for press enter to continue
    private static void pressEnterToContinue(Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}