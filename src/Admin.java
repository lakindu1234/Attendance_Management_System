import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Admin {
    Scanner scanner = new Scanner(System.in);

    public void dashboard() {
        System.out.println("\n--- Admin Dashboard ---");
        while (true) {
            System.out.println("\n1. Add Teacher");
            System.out.println("2. Add Student (coming soon)");
            System.out.println("3. View Attendance Report (coming soon)");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addTeacher();
                    break;
                case 2:
                    System.out.println("Feature coming soon...");
                    break;
                case 3:
                    System.out.println("Feature coming soon...");
                    break;
                case 4:
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
