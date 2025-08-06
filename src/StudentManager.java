// File: StudentManager.java
import java.io.*;
import java.util.*;

public class StudentManager {

    public static void addStudent(Scanner scanner) {
        try {
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Username: ");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            FileWriter writer = new FileWriter("students.txt", true);
            writer.write(name + "," + username + "," + password + "\n");
            writer.close();

            System.out.println("✅ Student added successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error adding student: " + e.getMessage());
        }
    }

    public static void deleteStudent(Scanner scanner) {
        System.out.print("Enter the username of the student to delete: ");
        String targetUsername = scanner.nextLine();

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
                        continue; // skip this student
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
    }
}