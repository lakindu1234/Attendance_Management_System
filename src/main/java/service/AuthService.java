package service;

import dao.UserDAO;
import model.User;
import java.util.Scanner;

public class AuthService {
    UserDAO userDAO = new UserDAO();

    public User login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDAO.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            System.out.println("Invalid credentials");
            return null;
        }
    }
}