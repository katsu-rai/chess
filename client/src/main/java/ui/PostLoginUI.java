package ui;

import client.ServerFacade;
import model.GameData;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.out;

public class PostLoginUI {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private Map<Integer, GameData> games;

    public PostLoginUI(ServerFacade server) {
        this.server = server;
        this.games = server.listGamesMap();
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
                    listGames();
                    break;
                case "create":
                    createGame(input);
                    break;
                case "join":
                    joinGame(input);
                    break;
                case "observe":
                    observeGame(input);
                    break;
                default:
                    out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }

    private void listGames() {
        games = server.listGamesMap(); // Refresh game list
        if (games.isEmpty()) {
            out.println("No Game Available");
        } else {
            out.println("Active Games:");
            games.forEach((id, game) -> {
                String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
                String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
                out.printf("ID: %d | Name: %s | White: %s | Black: %s%n",
                        id, game.gameName(), whiteUser, blackUser);
            });
        }
    }

    private void createGame(String[] input) {
        if (input.length < 2) {
            out.println("Missing a game name.");
            return;
        }
        int gameID = server.createGame(input[1]);
        out.printf("Created game, ID: %d%n", gameID);
    }

    private void joinGame(String[] input) {
        if (input.length < 3) {
            out.println("Usage: join <ID> [WHITE|BLACK]");
            return;
        }

        try {
            int gameID = Integer.parseInt(input[1]);
            String color = input[2].toUpperCase();
            if (!games.containsKey(gameID)) {
                out.println("Game does not exist.");
                return;
            }

            if (server.joinGame(gameID, color)) {
                out.println("You have joined the game as " + color);
                new BoardPrinter(games.get(gameID).game().getBoard()).printBoard();
            } else {
                out.println("Game does not exist or color is already taken.");
            }
        } catch (NumberFormatException e) {
            out.println("Invalid game ID format. Please enter a valid number.");
        }
    }

    private void observeGame(String[] input) {
        if (input.length < 2) {
            out.println("Usage: observe <ID>");
            return;
        }

        try {
            int gameID = Integer.parseInt(input[1]);
            if (!games.containsKey(gameID)) {
                out.println("Game does not exist.");
                return;
            }

            if (server.joinGame(gameID, null)) { // null means observer
                out.println("You have joined the game as an observer.");
                new BoardPrinter(games.get(gameID).game().getBoard()).printBoard();
            } else {
                out.println("Game does not exist.");
            }
        } catch (NumberFormatException e) {
            out.println("Invalid game ID format. Please enter a valid number.");
        }
    }

    private void printHelpMenu() {
        out.println("\nAvailable Commands:");
        out.println("create <NAME> - Create a new game");
        out.println("list - List all available games");
        out.println("join <ID> <WHITE|BLACK> - Join a game as a player");
        out.println("observe <ID> - Observe a game as a spectator");
        out.println("logout - Log out of current user");
        out.println("quit - Stop playing");
        out.println("help - Show this menu");
    }
}
