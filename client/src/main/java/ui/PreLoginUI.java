package ui;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.*;
import client.ServerFacade;

public class PreLoginUI {
    Scanner scanner = new Scanner(System.in);
    ServerFacade server = new ServerFacade();

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


                case "register":


                default:
                    out.println("Invalid command, please try again");
                    printHelpMenu();
                    break;
            }
        }

//        PostLoginUI.run();

    }

    private String[] getUserInput() {
        return scanner.nextLine().split(" ");
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

}