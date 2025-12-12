package ModernizeSystem;

import ModernizeSystem.Controller.*;
import ModernizeSystem.Model.*;
import ModernizeSystem.Service.CartService;
import ModernizeSystem.Service.FileIOService;
import ModernizeSystem.Service.GameService;
import ModernizeSystem.View.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final GameService gameService = new GameService();
    private static final CartService cartService = new CartService();
    private static final PaymentView paymentView = new PaymentView(cartService);

    public static void main(String[] args) {
        titleScreen();
    }

    // ======================================================
    //              LOGIN / REGISTER ENTRY
    // ======================================================
    public static void titleScreen() {

        ConsoleAuthController authController = new ConsoleAuthController();
        LoginRegisterView view = new LoginRegisterView();

        boolean exitChoice = false;

        while (!exitChoice) {

            view.displayTitle();
            LoginRegisterView.MenuOption option = view.promptMenuChoice();

            if (option == null) {
                System.out.println("Invalid option!");
                continue;
            }

            switch (option) {

                case REGISTER -> authController.handleRegistration();

                case LOGIN -> {
                    UserModel user = authController.handleLogin();

                    if (user == null) {
                        System.out.println("Login cancelled or failed.");
                        break;
                    }

                    if (user instanceof StaffModel) {
                        StaffMenu();
                    } else {
                        CustomerMainMenu(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new AccountWallet(),
                                new Credit()
                        );
                    }
                }

                case EXIT -> {
                    System.out.println("Exiting program...");
                    exitChoice = true;
                }
            }
        }
    }

    // ======================================================
    //                     STAFF MENU
    // ======================================================
    public static void StaffMenu() {

        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {

            System.out.println("""
                    =========================
                        STAFF MENU
                    =========================
                    1. Add Game
                    2. View Sales Report
                    3. Exit
                    """);

            int choice = sc.nextInt();

            switch (choice) {

                case 1 -> fileWritingGame();

                case 2 -> {
                    ArrayList<Game> gameList = filereadingGame(new ArrayList<>());
                    SalesSummaryModel model = new SalesSummaryModel(gameList);

                    SummaryReportController controller =
                            new SummaryReportController(model);

                    System.out.println(controller.getSummary("Sales Summary"));

                    SummaryReportView view = new SummaryReportView();
                    view.displaySummary(model);
                }

                case 3 -> ExitProgram();

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ======================================================
    //                CUSTOMER MAIN MENU
    // ======================================================
    public static void CustomerMainMenu(
            ArrayList<Cart> cartList,
            ArrayList<Game> gameList,
            AccountWallet wallet,
            Credit card) {

        Order sessionOrder = new Order();

        while (true) {

            int choice = MainMenu();

            double subTotal = cartService.calculateSubTotal(cartList);
            sessionOrder.setSubTotal(subTotal);
            sessionOrder.calculateTaxAndTotal();

            switch (choice) {

                case 1 -> {
                    gameList = (ArrayList<Game>) FileIOService.readGameData();
                    gameSelection(gameList, cartList, wallet, card);
                }

                case 2 -> {
                    if (cartList.isEmpty()) {
                        System.out.println("\n[ Cart is Empty ]\n");
                        break;
                    }
                    System.out.println(sessionOrder);
                    CartMenu(cartList, gameList, wallet, card, sessionOrder);
                }

                case 3 -> paymentView.addTopUpAmount(wallet);

                case 0 -> ExitProgram();
            }
        }
    }

    // ======================================================
    //                    MAIN MENU UI
    // ======================================================
    public static int MainMenu() {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    ==============================
                       CUSTOMER MAIN MENU
                    ==============================
                    1. Games on Sale
                    2. Open Cart
                    3. Top-Up Wallet
                    0. Exit
                    """);

            try {
                int choice = sc.nextInt();
                if (choice >= 0 && choice <= 3) return choice;
            } catch (Exception e) {
                sc.nextLine();
            }

            System.out.println("Invalid input!");
        }
    }

    // ======================================================
    //          GAME SELECTION + ADD TO CART
    // ======================================================
    public static double gameSelection(
            ArrayList<Game> gameList,
            ArrayList<Cart> cartList,
            AccountWallet wallet,
            Credit card) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            menucontent(gameList);

            System.out.print("0) Back\nSelect game > ");

            int choice;
            try {
                choice = sc.nextInt();
                if (choice == 0) return cartService.calculateSubTotal(cartList);

                Game selected = gameService.getSelectedGame(gameList, choice);
                cartService.addItemToCart(cartList, selected);
                System.out.println("Added to cart!");

            } catch (Exception e) {
                sc.nextLine();
                System.out.println("Invalid choice!");
            }
        }
    }

    // ======================================================
    //                    CART MENU
    // ======================================================
    public static void CartMenu(
            ArrayList<Cart> cartList,
            ArrayList<Game> gameList,
            AccountWallet wallet,
            Credit card,
            Order sessionOrder) {

        Scanner sc = new Scanner(System.in);

        System.out.println("\nYour Cart:");
        for (Cart c : cartList) {
            System.out.printf("%s - %.2f\n", c.getGameName(), c.getPrice());
        }

        System.out.printf("Total: %.2f\n", sessionOrder.getTotal());

        System.out.println("""
                1. Checkout
                2. Remove Item
                3. Clear Cart
                4. Back
                """);

        int choice = sc.nextInt();

        switch (choice) {

            case 1 -> paymentView.displayPaymentMenu(
                    cartList, gameList, wallet, card, sessionOrder);

            case 2 -> removeItemView(cartList);

            case 3 -> {
                cartService.clearCart(cartList);
                sessionOrder.setSubTotal(0);
                sessionOrder.calculateTaxAndTotal();
            }

            case 4 -> { }
        }
    }

    // ======================================================
    //                 REMOVE CART ITEM
    // ======================================================
    public static void removeItemView(List<Cart> cartList) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Game ID to remove: ");
        String id = sc.nextLine();

        boolean removed = cartService.removeItemFromCart(cartList, id);
        System.out.println(removed ? "Item removed." : "Item not found.");
    }

    // ======================================================
    //                 GAME LIST DISPLAY
    // ======================================================
    public static void menucontent(List<Game> gameList) {
        int i = 0;
        for (Game game : gameList) {
            System.out.println(++i + ") " + game.getGameName());
        }
    }

    // ======================================================
    //                 READ GAME FILE
    // ======================================================
    public static ArrayList<Game> filereadingGame(ArrayList<Game> gameList) {

        gameList.clear();

        try (Scanner sc = new Scanner(new File("available_games.txt"))) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split("\\|");
                gameList.add(new Game(
                        p[0], p[1], Double.parseDouble(p[2]), p[3], p[4]
                ));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Game file not found!");
        }

        return gameList;
    }

    // ======================================================
    //                 ADD GAME (STAFF)
    // ======================================================
    public static void fileWritingGame() {

        Scanner sc = new Scanner(System.in);
        Game game = new Game();

        System.out.print("Game Name: ");
        game.setGameName(sc.nextLine());

        System.out.print("Price: ");
        game.setPrice(sc.nextDouble());

        sc.nextLine();
        System.out.print("Genre: ");
        game.setGenre(sc.nextLine());

        System.out.print("Description: ");
        game.setGameDesc(sc.nextLine());

        try (FileWriter fw = new FileWriter("available_games.txt", true)) {
            fw.write(String.format(
                    "%s|%s|%.2f|%s|%s\n",
                    game.getGameID(),
                    game.getGameName(),
                    game.getPrice(),
                    game.getGenre(),
                    game.getGameDesc()
            ));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File error", e);
        }
    }

    // ======================================================
    //                     EXIT
    // ======================================================
    public static void ExitProgram() {
        System.out.println("See you next time!");
        System.exit(0);
    }
}
