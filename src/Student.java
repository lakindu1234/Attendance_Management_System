// File: Student.java
import java.io.*;
import java.util.Scanner;

public class Student {
    private Scanner scanner;

    public Student(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        while (true) {
            System.out.println("\n--- Student Login ---");
            System.out.print("Enter Student Username (or 'back' to return to main menu): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Student Password (or 'back' to return): ");
            String password = scanner.nextLine().trim();

            if (password.equalsIgnoreCase("back")) {
                continue;
            }

            if (authenticate(username, password)) {
                dashboard(username);
                break;
            } else {
                System.out.println("❌ Invalid student credentials! Try again or type 'back' to return.");
            }
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
            System.out.println("\n========================================");
            System.out.println("         STUDENT DASHBOARD");
            System.out.println("========================================");
            System.out.println("1. View My Attendance (coming soon)");
            System.out.println("2. View My Profile");
            System.out.println("3. View Class Schedule (coming soon)");
            System.out.println("4. Back to Main Menu");
            System.out.println("5. Exit Program");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("📊 Feature coming soon...");
                        pressEnterToContinue();
                        break;
                    case 2:
                        viewProfile(username);
                        break;
                    case 3:
                        System.out.println("📅 Feature coming soon...");
                        pressEnterToContinue();
                        break;
                    case 4:
                        System.out.println("🔙 Returning to Main Menu...");
                        return;
                    case 5:
                        System.out.println("Thank you for using Attendance Management System!");
                        System.out.println("Goodbye! 👋");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Please try again.");
                        pressEnterToContinue();
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                scanner.nextLine();
                pressEnterToContinue();
            }
        }
    }

    private void viewProfile(String username) {
        System.out.println("\n--- My Profile ---");
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[1].equals(username)) {
                    System.out.println("👤 Profile Information:");
                    System.out.println("----------------------------------------");
                    System.out.println("Name: " + parts[0]);
                    System.out.println("Username: " + parts[1]);
                    System.out.println("Status: Active Student");
                    System.out.println("----------------------------------------");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading profile: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
