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
        scanner.nextLine();

        String role = "";
        switch (choice) {
            case 1:
                role = "Admin";
                break;
            case 2:
                role = "Teacher";
                break;
            case 3:
                role = "Student";
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                return;
        }

        System.out.println("\n--- " + role + " Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("\nLogin details:");
        System.out.println("Role    : " + role);
        System.out.println("Username: " + username);

        switch (role) {
            case "Admin":
                new Admin().dashboard();
                break;
            case "Teacher":
                if (Teacher.authenticate(username, password)) {
                    new Teacher().dashboard();
                } else {
                    System.out.println("❌ Invalid Teacher credentials. Access denied.");
                }
                break;
            case "Student":
                new Student().dashboard(); // Add authentication later
                break;
        }
    }
}
