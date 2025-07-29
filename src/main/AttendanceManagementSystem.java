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
        scanner.nextLine(); // consume the newline

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

        // Here you can call role-based authentication or dashboard loading
        System.out.println("\nLogin details:");
        System.out.println("Role    : " + role);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password); // For demonstration only; don’t show passwords in real systems.

        System.out.println("\nRedirecting to " + role + " Dashboard...");
    }
}
