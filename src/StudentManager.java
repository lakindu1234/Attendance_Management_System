// File: StudentManager.java
import java.io.*;
import java.util.*;

public class StudentManager {

    public static void addStudent(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Add New Student ---");
            try {
                System.out.print("Enter Student Name (or 'back' to return): ");
                String name = scanner.nextLine().trim();

                if (name.equalsIgnoreCase("back")) {
                    return;
                }

                if (name.isEmpty()) {
                    System.out.println("❌ Name cannot be empty!");
                    continue;
                }

                System.out.print("Enter Username (or 'back' to return): ");
                String username = scanner.nextLine().trim();

                if (username.equalsIgnoreCase("back")) {
                    return;
                }

                if (username.isEmpty()) {
                    System.out.println("❌ Username cannot be empty!");
                    continue;
                }

                // Check if username already exists
                if (isUsernameExists(username)) {
                    System.out.println("❌ Username already exists! Please choose a different username.");
                    continue;
                }

                System.out.print("Enter Password (or 'back' to return): ");
                String password = scanner.nextLine().trim();

                if (password.equalsIgnoreCase("back")) {
                    return;
                }

                if (password.isEmpty()) {
                    System.out.println("❌ Password cannot be empty!");
                    continue;
                }

                FileWriter writer = new FileWriter("students.txt", true);
                writer.write(name + "," + username + "," + password + "\n");
                writer.close();

                System.out.println("✅ Student added successfully!");
                System.out.print("Do you want to add another student? (y/n): ");
                String choice = scanner.nextLine().trim();
                if (!choice.equalsIgnoreCase("y")) {
                    break;
                }
            } catch (IOException e) {
                System.out.println("❌ Error adding student: " + e.getMessage());
                pressEnterToContinue(scanner);
                break;
            }
        }
    }

    public static void deleteStudent(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Delete Student ---");
            System.out.print("Enter the username of the student to delete (or 'back' to return): ");
            String targetUsername = scanner.nextLine().trim();

            if (targetUsername.equalsIgnoreCase("back")) {
                return;
            }

            if (targetUsername.isEmpty()) {
                System.out.println("❌ Username cannot be empty!");
                continue;
            }

            File inputFile = new File("students.txt");
            File tempFile = new File("students_temp.txt");

            boolean found = false;

            try (
                    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    FileWriter writer = new FileWriter(tempFile)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String username = parts[1];
                        if (username.equals(targetUsername)) {
                            found = true;
                            System.out.println("Found student: " + parts[0]);
                            System.out.print("Are you sure you want to delete this student? (y/n): ");
                            String confirm = scanner.nextLine().trim();
                            if (!confirm.equalsIgnoreCase("y")) {
                                writer.write(line + "\n");
                                found = false;
                            }
                            continue; // skip or delete this student
                        }
                        writer.write(line + "\n");
                    }
                }

            } catch (IOException e) {
                System.out.println("❌ Error processing file: " + e.getMessage());
                return;
            }

            try {
                if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                    if (found) {
                        System.out.println("✅ Student deleted successfully.");
                    } else {
                        System.out.println("⚠️ Student not found.");
                    }
                } else {
                    System.out.println("❌ Error updating student records.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error updating student records: " + e.getMessage());
            }

            System.out.print("Do you want to delete another student? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (!choice.equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    public static void viewAllStudents() {
        System.out.println("\n--- All Students ---");
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            int count = 0;
            System.out.println("👥 Students List:");
            System.out.println("----------------------------------------");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    count++;
                    System.out.println(count + ". Name: " + parts[0] + " | Username: " + parts[1]);
                }
            }
            if (count == 0) {
                System.out.println("No students found.");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("Total Students: " + count);
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading students file: " + e.getMessage());
        }
    }

    private static boolean isUsernameExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[1].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File doesn't exist or error reading, assume username doesn't exist
            return false;
        }
        return false;
    }

    private static void pressEnterToContinue(Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}