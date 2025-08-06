// File: Student.java
import java.io.*;
import java.util.Scanner;

public class Student {
    private Scanner scanner;

    public Student(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        System.out.print("Enter Student Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Student Password: ");
        String password = scanner.nextLine();

        if (authenticate(username, password)) {
            dashboard(username);
        } else {
            System.out.println("❌ Invalid student credentials!");
        }
    }

    private boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String fileName = parts[0];
                    String fileUsername = parts[1];
                    String filePassword = parts[2];

                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        System.out.println("✅ Welcome, " + fileName + "!");
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading students.txt file: " + e.getMessage());
        }
        return false;
    }

    private void dashboard(String username) {
        while (true) {
            System.out.println("\n--- Student Dashboard ---");
            System.out.println("1. View My Attendance (coming soon)");
            System.out.println("2. View Profile (coming soon)");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Feature coming soon...");
                    break;
                case 2:
                    System.out.println("Feature coming soon...");
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