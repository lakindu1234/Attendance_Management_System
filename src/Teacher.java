import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Teacher {

    public static boolean authenticate(String username, String password) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("teachers.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String fileName = parts[0];
                    String fileUsername = parts[1];
                    String filePassword = parts[2];

                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        System.out.println("✅ Welcome, " + fileName + "!");
                        reader.close();
                        return true;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("❌ Error reading teacher data: " + e.getMessage());
        }

        return false;
    }

    public void dashboard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Teacher Dashboard ---");
        while (true) {
            System.out.println("\n1. Mark Attendance");
            System.out.println("2. View Students");
            System.out.println("3. View Attendance Report");
            System.out.println("4. Logout");
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
