package ModernizeSystem;

import ModernizeSystem.Controller.*;
import ModernizeSystem.Model.*;
import ModernizeSystem.Service.CartService;
import ModernizeSystem.Service.FileIOService;
import ModernizeSystem.Service.GameService;
import ModernizeSystem.Service.ReviewService;
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
    private static final ReviewService reviewService = new ReviewService();

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
    // ======================================================
    //          GAME SELECTION + ADD TO CART (FINAL FIX)
    // ======================================================
    public static double gameSelection(
            ArrayList<Game> gameList,
            ArrayList<Cart> cartList,
            AccountWallet wallet,
            Credit card) { // Card and Wallet are passed for potential future expansion

        Scanner sc = new Scanner(System.in);
        int option = 0;
        char proceed;
        Game selectedGame = null;

        do {

            System.out.print("""
                          
                          
                          ==============================================================
                             ====    ==   == = ==  ====    == = ==  ====  ==  =  =   =
                            =       =  =  =======  =       =======  =     === =  =   =
                            =  ===  ====  =  =  =  ====    =  =  =  ====  = ===  =   =
                            =   =   =  =  =  =  =  =       =  =  =  =     =   =  =   =
                             ====   =  =  =  =  =  ====    =  =  =  ====  =   =   ===
                          ==============================================================
                          """);

            menucontent(gameList);

            System.out.print("\n0) Back\nSelect game > ");

            boolean inputValid = false;
            while (!inputValid) {
                try {
                    option = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    // Exit back to main menu
                    if (option == 0) return cartService.calculateSubTotal(cartList);

                    // Delegation: Use GameService to retrieve the game (SRP)
                    selectedGame = gameService.getSelectedGame(gameList, option);
                    inputValid = true;

                } catch (IndexOutOfBoundsException e) {
                    LOGGER.log(Level.WARNING, "User entered invalid game index: " + option);
                    System.out.println("Invalid Option! Please select a number from the list.");
                    sc.nextLine(); // Clear scanner buffer if needed
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Option! Only Enter number!");
                    sc.nextLine(); // Clear scanner buffer
                }
            }

            // --- Display Game Details ---
            System.out.printf("""
                    =================================================================
                         Game Name  : %s
                         Game Price : %.2f
                         Game Genre : %s
                        ______________________________
                         Game Description
                        ------------------------------ 
                    
                    """, selectedGame.getGameName(), selectedGame.getPrice(), selectedGame.getGenre());

            // Assuming formatGameDesc handles wrapping
            // formatGameDesc(selectedGame.getGameDesc(), 40);
            System.out.println(selectedGame.getGameDesc()); // Simplified display for brevity
            System.out.println("=================================================================");

            // --- Options Segment ---
            System.out.print("""
            [1] Add to Cart     [2] Reviews     [3] Back to Games   
            Please Enter An Option (1-3):  """);

            int actionChoice;
            try {
                actionChoice = sc.nextInt();
            } catch (InputMismatchException e) {
                actionChoice = -1;
                sc.nextLine();
            }

            switch (actionChoice) {
                case 1 -> {
                    // Delegation: Use CartService to add item (SRP)
                    cartService.addItemToCart(cartList, selectedGame);
                    System.out.println("\n[Successfully added " + selectedGame.getGameName() + " to cart.]\n");
                }
                case 2 -> {
                    // FIX APPLIED HERE: Call the method on the static instance
                    System.out.println("\n  Showing recent reviews:\n  -------------------------");

                    // Use the instance 'reviewService' instead of the class name 'ReviewService'
                    List<Review> currentReviews = reviewService.getReviewsForGame(selectedGame.getGameID());

                    if (currentReviews.isEmpty()) {
                        System.out.println("  No reviews found for " + selectedGame.getGameName());
                    } else {
                        // Display results from the Model layer
                        for (Review review : currentReviews) {
                            System.out.println(review.displayReview());
                        }
                    }
                }
                case 3 -> {
                    // Back to list
                }
                default -> System.out.println("Invalid action. Returning to game list.");
            }

            System.out.print("\n Continue Looking For Games? (Y/N) > ");
            proceed = sc.next().charAt(0);
            sc.nextLine();

        } while (Character.toUpperCase(proceed) == 'Y');

        // Final subtotal calculation before exiting the menu
        return cartService.calculateSubTotal(cartList);
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
