import java.io.*;
import java.util.Scanner;

public class Admin {
    private Scanner scanner;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public Admin(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        if (authenticate(username, password)) {
            System.out.println("✅ Admin login successful!");
            dashboard();
        } else {
            System.out.println("❌ Invalid admin credentials!");
        }
    }

    private boolean authenticate(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public void dashboard() {
        while (true) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. Add Teacher");
            System.out.println("2. Add Student");
            System.out.println("3. Delete Student");
            System.out.println("4. View Attendance Report (coming soon)");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addTeacher();
                    break;
                case 2:
                    StudentManager.addStudent(scanner);
                    break;
                case 3:
                    StudentManager.deleteStudent(scanner);
                    break;
                case 4:
                    System.out.println("Feature coming soon...");
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addTeacher() {
        try {
            System.out.print("Enter Teacher Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Username: ");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            FileWriter writer = new FileWriter("teachers.txt", true); // Append mode
            writer.write(name + "," + username + "," + password + "\n");
            writer.close();

            System.out.println("✅ Teacher added successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error while saving teacher: " + e.getMessage());
        }
    }
}