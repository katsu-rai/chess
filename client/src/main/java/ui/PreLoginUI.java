package ui;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.*;
import client.ServerFacade;

public class PreLoginUI {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server = new ServerFacade("http://localhost:8080");
    private PostLoginUI postLogin;

    public void run() {
        boolean loggedIn = false;

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to Chess Game!");
        out.println();

        while (!loggedIn) {
            printHelpMenu();
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

//         Proceed to post-login UI
        this.postLogin = new PostLoginUI(server);
        postLogin.run();
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
            out.println("Enter username and password separated by space (or type 'back' to return):");
            String[] credentials = scanner.nextLine().trim().split(" ", 2);
            if (credentials[0].equalsIgnoreCase("back")) return false;
            if (credentials.length < 2) {
                out.println("Invalid input. Please enter both username and password.");
                continue;
            }

            String username = credentials[0];
            String password = credentials[1];

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
            out.println("Enter username, password, and email separated by spaces (or type 'back' to return):");
            String[] credentials = scanner.nextLine().trim().split(" ", 3);
            if (credentials[0].equalsIgnoreCase("back")) return false;
            if (credentials.length < 3) {
                out.println("Invalid input. Please enter username, password, and email.");
                continue;
            }

            String username = credentials[0];
            String password = credentials[1];
            String email = credentials[2];

            boolean success = server.register(username, password, email);
            if (success) {
                out.println("Registration successful! You are now logged in.");
                return true;
            } else {
                out.println("Registration failed. " + (server.getLastResponseCode() == 403 ? "Username is already taken." : "Please try again."));
            }
        }
    }
}
