import java.util.Scanner;

public class Admin {
    public void dashboard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Admin Dashboard ---");
        System.out.println("1. Add Teacher");
        System.out.println("2. Add Student");
        System.out.println("3. View Attendance Report");
        System.out.println("4. Logout");

        while (true) {
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Adding teacher...");
                    break;
                case 2:
                    System.out.println("Adding student...");
                    break;
                case 3:
                    System.out.println("Viewing attendance report...");
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
