
// Source code is decompiled from a .class file using FernFlower decompiler.
import java.util.Scanner;

public class AttendanceManagementSystem {
    public AttendanceManagementSystem() {
    }

    public static void main(String[] var0) {
        Scanner var1 = new Scanner(System.in);
        System.out.println("===== Attendance Management System =====");
        System.out.println("Select your role:");
        System.out.println("1. Admin");
        System.out.println("2. Teacher");
        System.out.println("3. Student");
        System.out.print("Enter your choice (1-3): ");
        int var2 = var1.nextInt();
        var1.nextLine();
        String var3 = "";
        switch (var2) {
            case 1:
                var3 = "Admin";
                break;
            case 2:
                var3 = "Teacher";
                break;
            case 3:
                var3 = "Student";
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                return;
        }

        System.out.println("\n--- " + var3 + " Login ---");
        System.out.print("Enter username: ");
        String var4 = var1.nextLine();
        System.out.print("Enter password: ");
        String var5 = var1.nextLine();
        System.out.println("\nLogin details:");
        System.out.println("Role    : " + var3);
        System.out.println("Username: " + var4);
        System.out.println("Password: " + var5);
        System.out.println("\nRedirecting to " + var3 + " Dashboard...");
    }
}
