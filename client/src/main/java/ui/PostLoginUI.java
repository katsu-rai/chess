package ui;

import client.ServerFacade;
import model.GameData;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class PostLoginUI {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public PostLoginUI(ServerFacade server){
        this.server = server;
    }

    public void run() {
        out.println("Welcome to the Chess Game Lobby!");

        while (true) {
            printHelpMenu();
            out.print("\n[LOGGED IN] >>> ");
            String[] input = scanner.nextLine().trim().split(" ", 3);
            String command = input[0].toLowerCase();

            switch (command) {
                case "quit":
                    return;

                case "help":
                    printHelpMenu();
                    break;

                case "logout":
                    out.println("Logging out...");
                    new PreLoginUI().run();
                    return;

                case "list":
                    List<GameData> games = server.listGames();
                    if (games.isEmpty()) {
                        out.println("No Game Available");
                    } else {
                        StringBuilder sb = new StringBuilder("Active Games:\n");
                        int index = 1;
                        for (GameData game : games) {
                            String gameName = game.gameName();
                            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
                            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";

                            sb.append(index++).append(". Game Name: ").append(gameName)
                                    .append(" | Players: White - ").append(whiteUser)
                                    .append(", Black - ").append(blackUser)
                                    .append("\n");
                        }
                        out.println(sb.toString());
                    }
                    break;

                case "create":
                    if (input.length < 2) {
                        out.println("Missing a game name");
                    } else {
                        int gameID = server.createGame(input[1]);
                        out.printf("Created game, ID: %d%n", gameID);
                    }
                    break;

                case "join":
                    if (input.length < 3) {
                        out.println("Usage: join <ID> [WHITE|BLACK]");
                    } else {
                        try {
                            int gameID = Integer.parseInt(input[1]);
                            if (server.joinGame(gameID, input[2])) {
                                out.println("You have joined the game");
                            } else {
                                out.println("Game does not exist or color taken");
                            }
                        } catch (NumberFormatException e) {
                            out.println("Invalid game ID format. Please enter a valid number.");
                        }
                    }
                    break;

                default:
                    out.println("Unknown command. Type 'help' for a list of commands.");
                    break;
            }
        }
    }

    private void printHelpMenu() {
        out.println("\nAvailable Commands:");
        out.println("create <NAME> - create a new game");
        out.println("list - list all games");
        out.println("join <ID> <WHITE|BLACK> - join a game as a player");
        out.println("logout - log out of current user");
        out.println("quit - stop playing");
        out.println("help - show menu");
    }
}