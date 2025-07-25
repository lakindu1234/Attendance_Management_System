package service;

import dao.AttendanceDAO;
import model.Attendance;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    // Mark attendance for a specific student by ID
    public void markAttendance(int studentId, String status) {
        if (attendanceDAO.markAttendance(studentId, status)) {
            System.out.println("✅ Attendance marked successfully.");
        } else {
            System.out.println("❌ Failed to mark attendance.");
        }
    }

    // View all attendance records for a specific student
    public void viewAttendanceByStudent(int studentId) {
        List<Attendance> records = attendanceDAO.getAttendanceByStudentId(studentId);
        if (records.isEmpty()) {
            System.out.println("⚠️ No attendance records found.");
        } else {
            System.out.println("\n📋 Attendance for Student ID: " + studentId);
            for (Attendance a : records) {
                System.out.println("Date: " + a.getDate() + " | Status: " + a.getStatus());
            }
        }
    }

    // View all attendance records (for teachers/admin)
    public void viewAllAttendance() {
        List<Attendance> allRecords = attendanceDAO.getAllAttendance();
        if (allRecords.isEmpty()) {
            System.out.println("⚠️ No attendance records found.");
        } else {
            System.out.println("\n📋 All Attendance Records:");
            for (Attendance a : allRecords) {
                System.out.println("Student ID: " + a.getStudentId() +
                        " | Date: " + a.getDate() +
                        " | Status: " + a.getStatus());
            }
        }
    }
}
