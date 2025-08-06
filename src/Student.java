import java.util.Scanner;

public class Student {
    public void dashboard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Student Dashboard ---");
        while (true) {
            System.out.println("\n1. View Attendance");
            System.out.println("2. View Profile");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Showing attendance...");
                    break;
                case 2:
                    System.out.println("Showing profile...");
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
