package ui;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.*;
import client.ServerFacade;

public class PreLoginUI {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server = new ServerFacade("http://localhost:8080");

    public void run() {
        boolean loggedIn = false;

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to Chess Game!");
        out.println();
        printHelpMenu();

        while (!loggedIn) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "quit":
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "login":
                    loggedIn = handleLogin();
                    break;
                case "register":
                    loggedIn = handleRegister();
                    break;
                default:
                    out.println("Invalid command, please try again");
                    printHelpMenu();
                    break;
            }
        }

        // Proceed to post-login UI
        // PostLoginUI.run();
    }

    private String[] getUserInput() {
        return scanner.nextLine().trim().split(" ");
    }

    private void printHelpMenu() {
        out.println("register - create a new user");
        out.println("login - login to an existing user");
        out.println("quit - stop playing");
        out.println("help - show menu");
    }

    private boolean handleLogin() {
        while (true) {
            out.println("Enter username (or type 'back' to return):");
            String username = scanner.nextLine().trim();
            if (username.equalsIgnoreCase("back")) return false;

            out.println("Enter password:");
            String password = scanner.nextLine().trim();

            if (server.login(username, password)) {
                out.println("You are now logged in!");
                return true;
            } else {
                out.println("Invalid username or password. Please try again.");
            }
        }
    }

    private boolean handleRegister() {
        while (true) {
            out.println("Enter a username (or type 'back' to return):");
            String username = scanner.nextLine().trim();
            if (username.equalsIgnoreCase("back")) return false;

            out.println("Enter a password:");
            String password = scanner.nextLine().trim();

            out.println("Enter an email:");
            String email = scanner.nextLine().trim();

            if (server.register(username, password, email)) {
                out.println("Registration successful! You are now logged in.");
                return true;
            } else {
                out.println("Registration failed. Please try again.");
            }
        }
    }
}
