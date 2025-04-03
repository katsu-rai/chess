package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;
import java.util.Map;
import java.util.Scanner;
import static ui.EscapeSequences.*;

import static java.lang.System.out;

public class PostLoginUI {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private Map<Integer, GameData> games;
    private boolean inGame = false;

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
        games = server.listGamesMap();
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

            if (!color.equals("WHITE") && !color.equals("BLACK")){
                out.println("Invalid Color");
                return;
            }

            if (server.joinGame(gameID, color)) {
                out.println("You have joined the game");
                inGame = true;
                server.connectWS();
                ChessGame.TeamColor teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                server.connect(gameID);
                GamePlayUI gameplayUI = new GamePlayUI(server, games.get(gameID), teamColor);
                gameplayUI.run();
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

        games = server.listGamesMap();

        try {
            int gameID = Integer.parseInt(input[1]);
            GameData gameData = games.get(gameID);

            if (gameData == null) {
                out.println("Game does not exist.");
                return;
            }

            out.println("Observing game: " + gameData.gameName());
            new BoardPrinter(gameData.game()).printBoard(ChessGame.TeamColor.WHITE, null);
        } catch (NumberFormatException e) {
            out.println("Invalid game ID format. Please enter a valid number.");
        }
    }


    private void printHelpMenu() {
        out.println(SET_TEXT_COLOR_YELLOW + "\nAvailable Commands:" + RESET_TEXT_COLOR);
        out.println(SET_TEXT_COLOR_BLUE + "create <NAME>" + RESET_TEXT_COLOR + " - Create a new game");
        out.println(SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_COLOR + " - List all available games");
        out.println(SET_TEXT_COLOR_BLUE + "join <ID> <WHITE|BLACK>" + RESET_TEXT_COLOR + " - Join a game as a player");
        out.println(SET_TEXT_COLOR_BLUE + "observe <ID>" + RESET_TEXT_COLOR + " - Observe a game as a spectator");
        out.println(SET_TEXT_COLOR_RED + "logout" + RESET_TEXT_COLOR + " - Log out of current user");
        out.println(SET_TEXT_COLOR_RED + "quit" + RESET_TEXT_COLOR + " - Stop playing");
        out.println(SET_TEXT_COLOR_GREEN + "help" + RESET_TEXT_COLOR + " - Show this menu");
    }
}
