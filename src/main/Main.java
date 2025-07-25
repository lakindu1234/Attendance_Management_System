import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import dao.AttendanceDAO;
import model.Attendance;
import model.User;
import service.AuthService;

public class Main {
    public static void main(String[] args) {
        AuthService authService = new AuthService();
        User user = authService.login();

        if (user == null)
            return;

        Scanner scanner = new Scanner(System.in);
        AttendanceDAO attendanceDAO = new AttendanceDAO();

        switch (user.getRole()) {
            case "admin":
                System.out.println("Welcome Admin! (You can manage users here)");
                break;

            case "teacher":
                System.out.print("Enter student ID to mark attendance: ");
                int sid = scanner.nextInt();
                System.out.print("Enter 1 for present or 0 for absent: ");
                boolean status = scanner.nextInt() == 1;
                Attendance att = new Attendance();
                att.setStudentId(sid);
                att.setDate(new Date(System.currentTimeMillis()));
                att.setStatus(status);
                attendanceDAO.markAttendance(att);
                System.out.println("Attendance recorded!");
                break;

            case "student":
                List<Attendance> records = attendanceDAO.getAttendanceByStudent(user.getId());
                System.out.println("Your attendance records:");
                for (Attendance a : records) {
                    System.out.println("Date: " + a.getDate() + ", Present: " + a.isStatus());
                }
                break;

            default:
                System.out.println("Unknown role!");
        }
    }
}
// main file
