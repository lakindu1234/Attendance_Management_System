import java.util.Scanner;

public class AttendanceManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Attendance Management System =====");
        System.out.println("Select your role:");
        System.out.println("1. Admin");
        System.out.println("2. Teacher");
        System.out.println("3. Student");
        System.out.print("Enter your choice (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // consume newline

        switch (choice) {
            case 1:
                Admin admin = new Admin(scanner);
                admin.login();
                break;
            case 2:
                Teacher teacher = new Teacher(scanner);
                teacher.login();
                break;
            case 3:
                Student student = new Student(scanner);
                student.login();
                break;
            default:
                System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}