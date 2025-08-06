import java.util.Scanner;

public class Teacher {
    public void dashboard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Teacher Dashboard ---");
        System.out.println("1. Mark Attendance");
        System.out.println("2. View Students");
        System.out.println("3. View Attendance Report");
        System.out.println("4. Logout");

        while (true) {
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Marking attendance...");
                    break;
                case 2:
                    System.out.println("Viewing students...");
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
