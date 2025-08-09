// File: AttendanceManagementSystem.java
import java.util.Scanner;

public class AttendanceManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Welcome to Attendance Management System =====");

        // Main program loop
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         MAIN MENU");
            System.out.println("========================================");
            System.out.println("Select your role:");
            System.out.println("1. Admin");
            System.out.println("2. Teacher");
            System.out.println("3. Student");
            System.out.println("4. Exit Program");
            System.out.println("========================================");
            System.out.print("Enter your choice (1-4): ");

            try {
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
                    case 4:
                        System.out.println("Thank you for using Attendance Management System!");
                        System.out.println("Goodbye! 👋");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Please select 1-4.");
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
}