// File: Teacher.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Teacher {
    private Scanner scanner;

    public Teacher(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        while (true) {
            System.out.println("\n--- Teacher Login ---");
            System.out.print("Enter Teacher Username (or 'back' to return to main menu): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Teacher Password (or 'back' to return): ");
            String password = scanner.nextLine().trim();

            if (password.equalsIgnoreCase("back")) {
                continue;
            }

            if (authenticate(username, password)) {
                dashboard();
                break;
            } else {
                System.out.println("❌ Invalid teacher credentials! Try again or type 'back' to return.");
            }
        }
    }

    public boolean authenticate(String username, String password) {
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
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         TEACHER DASHBOARD");
            System.out.println("========================================");
            System.out.println("1. Mark Attendance (coming soon)");
            System.out.println("2. View Students");
            System.out.println("3. Add Student");
            System.out.println("4. Delete Student");
            System.out.println("5. View Attendance Reports (coming soon)");
            System.out.println("6. Back to Main Menu");
            System.out.println("7. Exit Program");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("📝 Feature coming soon...");
                        pressEnterToContinue();
                        break;
                    case 2:
                        StudentManager.viewAllStudents();
                        pressEnterToContinue();
                        break;
                    case 3:
                        StudentManager.addStudent(scanner);
                        break;
                    case 4:
                        StudentManager.deleteStudent(scanner);
                        break;
                    case 5:
                        System.out.println("📊 Feature coming soon...");
                        pressEnterToContinue();
                        break;
                    case 6:
                        System.out.println("🔙 Returning to Main Menu...");
                        return;
                    case 7:
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

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}