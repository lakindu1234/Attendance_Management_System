import java.io.*;
import java.util.Scanner;

public class Student {
    private Scanner scanner;

    public Student(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        if (isStudentExists(username)) {
            System.out.println("Login successful!");
            System.out.println("Welcome, " + username + "!");
            // Add dashboard logic here if needed
        } else {
            System.out.println("Login failed. Student not found.");
        }
    }

    private boolean isStudentExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading students.txt file.");
        }
        return false;
    }
}
