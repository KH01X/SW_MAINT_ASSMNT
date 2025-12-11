package ModernizeSystem;

import ModernizeSystem.Controller.ConsoleAuthController;
import ModernizeSystem.Model.*;
import ModernizeSystem.View.PaymentView;

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

        // ========== GAME REVIEWS (unchanged) ==========
        Review[] Game1Reviews = new Review[10];
        Game1Reviews[0] = new Review("Markiplier#87", "This game is lowkey amazing!");
        Game1Reviews[1] = new Review("AmogusSussy#69", "AMOGUS!");
        Game1Reviews[2] = new Review("Cool#420", "I love Mae!");

        Review[] Game2Reviews = new Review[10];
        Game2Reviews[0] = new Review("AloySUS#24", "Undertail is COOL");
        Game2Reviews[1] = new Review("sanslover#16", "I WANNA BANG THAT SKEELTON");
        Game2Reviews[2] = new Review("Metaton#420", "RATINGS ARE OFF THE CHARTS!");

        Review[] Game3Reviews = new Review[10];
        Game3Reviews[0] = new Review("HollowKnightFan#124", "Hollow Knight has been my long time favourite!");
        Game3Reviews[1] = new Review("Kevin#****", "ShAW!");
        Game3Reviews[2] = new Review("Hornet", "@Kevin#**** that's not funny");

        Review[] Game4Reviews = new Review[10];
        Game4Reviews[0] = new Review("PewDiePie#bro", "THIS GAME IS TRASH! :trash_bin:");
        Game4Reviews[1] = new Review("Alden Ling#132", "this game copied my name!!!");
        Game4Reviews[2] = new Review("Eiden Ring#992", "@Alden Ling#132 IKR");

        Review[] Game5Reviews = new Review[10];
        Game5Reviews[0] = new Review("Markus#sigma", "well, i tried");
        Game5Reviews[1] = new Review("Android#182", "I LOVE THIS GAME!");
        Game5Reviews[2] = new Review("Markiplier#87", "Hello everybody my name is Markiplier.");

        // START PROGRAM
        titleScreen();
    }

    // ===========================
    //    TITLE SCREEN (UPDATED)
    // ===========================
    public static void titleScreen() {

        // NEW: Our unified Login + Register controller
        ConsoleAuthController auth = new ConsoleAuthController();

        Scanner scanner = new Scanner(System.in);
        boolean exitChoice = false;

        do {
            System.out.println(" |<========================================================================>|");
            System.out.println(" |    ===++=== ||                  /======>>                       |  //    |");
            System.out.println(" |       []    ||      //====     (                          ____  | //     |");
            System.out.println(" |       []    |====|  |_____      \\====\\  //==\\\\ //==\\\\  //      ||        |");
            System.out.println(" |       []    ||  ||  |                  ) ||  || \\\\__||  [       | \\      |");
            System.out.println(" |       []    ||  ||  L=====      ======/  ||  ||     ||  \\\\____  |  \\\\    |");
            System.out.println(" |                                                                          |");
            System.out.println(" |<========================================================================>|");

            System.out.println("1. REGISTER");
            System.out.println("2. LOGIN");
            System.out.println("3. EXIT");
            System.out.print("Enter choice > ");
            //put try catch if not enter int
            int choice = scanner.nextInt();

            switch (choice) {

                // ======================
                //   REGISTRATION (NEW)
                // ======================
                case 1:
                    auth.handleRegistration();
                    break;

                // ======================
                //        LOGIN (NEW)
                // ======================
                case 2:
                    UserModel user = auth.handleLogin();  // returns StaffModel or CustomerModel

                    if (user == null) {
                        System.out.println("Login cancelled or failed.");
                        break;
                    }

                    // Determine role automatically
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
                    break;

                // ======================
                //        EXIT
                // ======================
                case 3:
                    System.out.println("Exiting program...");
                    exitChoice = true;
                    break;

                default:
                    System.out.println("Invalid option!");
                    break;
            }

        } while (!exitChoice);
    }


    // ===========================
    //         STAFF MENU
    // ===========================
    public static void StaffMenu() {
        Scanner sc = new Scanner(System.in);
        boolean staffLooper = false;

        while (!staffLooper) {
            System.out.println("What do you want to do ?");
            System.out.println("1. Add Game");
            System.out.println("2. View Report");
            System.out.println("3. Exit Program");

            int staffChoice = sc.nextInt();

            switch (staffChoice) {
                case 1:
                    fileWritingGame();
                    break;
                case 2:
                    int whichGame = 0;
                    int[] quantity = getQuantity(whichGame);
                    summaryReport(quantity);
                    break;
                case 3:
                    ExitProgram();
                    break;
                default:
                    System.out.println("\n Error ! Invalid Choice");
            }
        }
    }


    // ===========================
    //   READ CUSTOMER DATA FILE
    // ===========================
    public static ArrayList<Customer> filereadingCusData(ArrayList<Customer> cusLogin) {

        File cusData = new File("cusData.txt");
        try (Scanner fileread = new Scanner(cusData)) {
            while (fileread.hasNextLine()) {
                String cusread = fileread.nextLine();
                Customer cus = new Customer();
                String[] parts = cusread.split("\\|");

                if (parts.length == 3) {
                    cus.setuserID(parts[0]);
                    cus.setuserPw(parts[1]);
                    cus.setuserEmail(parts[2]);
                }

                cusLogin.add(new Customer(cus.getuserID(), cus.getuserPw(), cus.getuserEmail()));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error ! File Not Found!");
        }
        return cusLogin;
    }

    // ======================================================
    //                CUSTOMER MAIN MENU
    // ======================================================
    public static void CustomerMainMenu(ArrayList<Cart> cartList,
                                        ArrayList<Game> gameList,
                                        AccountWallet wallet,
                                        Credit card) {

        Order sessionOrder = new Order();
        int exitProg = 0;
        do {
            int choice = MainMenu();

            double currentSubTotal = cartService.calculateSubTotal(cartList);

            sessionOrder.setSubTotal(currentSubTotal);
            sessionOrder.calculateTaxAndTotal();

            switch (choice) {
                case 1:
                    // Load games using your FileIOService
                    gameList = (ArrayList<Game>) FileIOService.readGameData();
                    gameSelection(gameList, cartList, wallet, card);
                    break;

                case 2:
                    if (cartList.isEmpty()) {
                        System.out.println("\n [ Your Cart is Empty!! ]\n");
                        CustomerMainMenu(cartList, gameList, wallet, card);
                    }
                    System.out.println(sessionOrder);
                    System.out.printf("Your total price is....   %.2f!\n\n", sessionOrder.getTotal());

                    CartMenu(cartList, gameList, wallet, card, sessionOrder);

                    break;

                case 3:
                    paymentView.addTopUpAmount(wallet);
                    break;

                case 0:
                    ExitProgram();
                    break;
            }
            choice = exitProg;

        } while (exitProg != 5);
    }

    // ======================================================
    //               READ GAMES INTO STORE
    // ======================================================
    public static ArrayList<Game> filereadingGame(ArrayList<Game> gameList) {
        gameList.clear();
        File gameFile = new File("available_games.txt");

        try (Scanner fileread = new Scanner(gameFile)) {
            while (fileread.hasNextLine()) {
                String gameread = fileread.nextLine();
                Game game = new Game();
                String[] parts = gameread.split("\\|");

                if (parts.length == 5) {
                    game.setGameID(parts[0]);
                    game.setGameName(parts[1]);
                    game.setPrice(Double.parseDouble(parts[2]));
                    game.setGenre(parts[3]);
                    game.setGameDesc(parts[4]);
                }

                gameList.add(new Game(
                        game.getGameID(),
                        game.getGameName(),
                        game.getPrice(),
                        game.getGenre(),
                        game.getGameDesc()
                ));
            }

        } catch (FileNotFoundException e) {
            System.out.println("The file does not exist :(");
        }

        return gameList;
    }
    // ======================================================
    //             ADDING NEW GAME (STAFF ONLY)
    // ======================================================
    public static void fileWritingGame() {
        Scanner sc = new Scanner(System.in);
        char comfirmation;
        Game game = new Game();
        System.out.println("Add new game? (Y/N)> ");

        do {
            comfirmation = sc.next().charAt(0);

            if (Character.toUpperCase(comfirmation) == 'Y') {

                sc.nextLine();
                System.out.printf("\nNew Game Name: ");
                game.setGameName(sc.nextLine());

                System.out.printf("\nNew Game Price: ");
                game.setPrice(sc.nextDouble());

                System.out.print("""
                        New Game Genre (Enter the number for a Genre)
                        ---------------------------------------------
                        1)RPG           5)Adventure
                        2)Action        6)Horror
                        3)Shooter       7)Relaxing
                        4)Story Rich    8)Strategy
                        """);

                switch (sc.nextInt()) {
                    case 1 -> game.setGenre("RPG");
                    case 2 -> game.setGenre("Action");
                    case 3 -> game.setGenre("Shooter");
                    case 4 -> game.setGenre("Story Rich");
                    case 5 -> game.setGenre("Adventure");
                    case 6 -> game.setGenre("Horror");
                    case 7 -> game.setGenre("Relaxing");
                    case 8 -> game.setGenre("Strategy");
                    default -> System.out.println("\nPlease select 1-8 only.\n");
                }

                sc.nextLine();
                System.out.printf("\nNew Game Description: ");
                game.setGameDesc(sc.nextLine());

                try {
                    FileWriter writegame = new FileWriter("available_games.txt", true);
                    writegame.write(String.format(
                            "%s|%s|%.2f|%s|%s\n",
                            game.getGameID(),
                            game.getGameName(),
                            game.getPrice(),
                            game.getGenre(),
                            game.getGameDesc()
                    ));
                    writegame.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.printf("\nAdd another Game? > ");
                comfirmation = sc.next().charAt(0);
            }
            else if (Character.toUpperCase(comfirmation) == 'N') {
                break;
            }
            else {
                System.out.println("Please select Yes [Y] or No [N]");
                sc.nextLine();
            }

        } while (Character.toUpperCase(comfirmation) == 'Y' || Character.toUpperCase(comfirmation) != 'N');
    }

    // ======================================================
    //                 DISPLAY MAIN MENU
    // ======================================================
    public static int MainMenu() {
        boolean valid = false;
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("""
                            ========================================
                                  (===M===)  _____   []
                                  |   |   |  [___|   __   [===         
                                  O   o   O  [   |   ||   O   O        
                                  X       X  |   |   ||   X   X                  
                            ========================================
                              Welcome to Main Menu!
                              1. Games on Sale
                              2. Open Cart
                              3. Top-Up Wallet
                            
                              0. Exit Program
                            """);
            System.out.print("Enter choice > ");

            try {
                choice = sc.nextInt();

                if (choice < 0 || choice > 3) {
                    valid = false;
                    System.out.println("Enter a number from 0 - 3!");
                } else valid = true;

            } catch (Exception ex) {
                valid = false;
                sc.nextLine();
                System.out.println("Only numbers are allowed!");
            }

        } while (!valid);

        return choice;
    }

    // ======================================================
    //                 GAME CATALOG MENU
    // ======================================================
    public static void menucontent(List<Game> gameList) {
        int i = 0;

        System.out.print("""
            
            
            ==============================================================
               ====    ==   == = ==  ====    == = ==  ====  ==  =  =   =
              =       =  =  =======  =       =======  =     === =  =   =
              =  ===  ====  =  =  =  ====    =  =  =  ====  = ===  =   =
              =   =   =  =  =  =  =  =       =  =  =  =     =   =  =   =
               ====   =  =  =  =  =  ====    =  =  =  ====  =   =   ===
            ==============================================================
            """);

        for (Game printgame : gameList) {
            System.out.println(++i + ") " + printgame.getGameName());
        }
    }


    // ======================================================
    //          GAME SELECTION + ADD TO CART FLOW
    // ======================================================
    public static double gameSelection(
            List<Game> gameList,
            List<Cart> cartList,
            AccountWallet wallet,
            Credit card) {

        Scanner sc = new Scanner(System.in);
        int option = 0;
        char proceed;
        Game selectedGame = null;

        do {
            menucontent(gameList);

            System.out.printf("\n0) Exit Game Menu\nSelect a game > ");

            boolean inputValid = false;
            while (!inputValid) {
                try {
                    option = sc.nextInt();

                    // Exit back to main
                    if (option == 0) {
                        CustomerMainMenu((ArrayList<Cart>) cartList, (ArrayList<Game>) gameList, wallet, card);
                        return cartService.calculateSubTotal(cartList);
                    }

                    selectedGame = gameService.getSelectedGame(gameList, option);
                    inputValid = true;

                } catch (IndexOutOfBoundsException e) {
                    LOGGER.log(Level.WARNING, "User entered invalid game index: " + option);
                    System.out.println(ErrorMessage.INVALID_CHOICE);
                    sc.nextLine();
                } catch (java.util.InputMismatchException e) {
                    System.out.println(ErrorMessage.INVALID_CHOICE);
                    sc.nextLine();
                }
            }

            // Show game details
            System.out.printf("""
                    =================================================================
                         Game Name  : %s
                         Game Price : %.2f
                         Game Genre : %s
                        ______________________________
                         Game Description
                        ------------------------------ 
                    
                    """, selectedGame.getGameName(), selectedGame.getPrice(), selectedGame.getGenre());

            formatGameDesc(selectedGame.getGameDesc(), 40);
            System.out.println("=================================================================");

            System.out.printf("""
            [1] Add to Cart     [2] Reviews     [3] Back to Games
            Please Enter An Option (1-3):  """);

            option = sc.nextInt();

            switch (option) {
                case 1 -> {
                    cartService.addItemToCart(cartList, selectedGame);

                    System.out.printf("================= Your Cart Content =================\n");
                    System.out.println("Game Name                        Price");

                    for (Cart cartprint : cartList) {
                        System.out.println(cartprint.getGameName() + "              " + cartprint.getPrice());
                    }

                    double totalPrice = cartService.calculateSubTotal(cartList);
                    System.out.println("Total price: " + String.format("%.2f", totalPrice));
                }
                case 2 -> System.out.println("\n  Showing recent reviews:\n  -------------------------");
                case 3 -> { /* back */ }
            }

            System.out.print("\n Continue Looking For Games? (Y/N) > ");
            proceed = sc.next().charAt(0);
            sc.nextLine();

        } while (Character.toUpperCase(proceed) == 'Y');

        return cartService.calculateSubTotal(cartList);
    }
    // ======================================================
    //        FORMAT LONG GAME DESCRIPTIONS (WRAPPING)
    // ======================================================
    public static void formatGameDesc(String desc, int width) {
        String[] descSplit = desc.split("\\s+");
        StringBuilder nextline = new StringBuilder();

        for (String descG : descSplit) {
            if (nextline.length() + descG.length() + 1 <= width) {
                if (nextline.length() > 0) {
                    nextline.append(" ");
                }
                nextline.append(descG);
            } else {
                System.out.println("    " + nextline);
                nextline = new StringBuilder(descG);
            }
        }

        if (nextline.length() > 0) {
            System.out.println(nextline);
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
        boolean valid = false;
        int choice = 0;

        double total = sessionOrder.getTotal();

        do {
            System.out.println("""
                    ========================================
                          o====-  __    __   |
                          |      |__|  [    =|== 
                          o====o |  |  [     |_
                    ========================================
                      Displaying Cart:
                    """);

            if (cartList.isEmpty()) {
                System.out.println("       [ Cart is currently empty ]");
            } else {
                System.out.println(" Name                           Price");
                for (Cart cartprint : cartList) {
                    System.out.printf("%-30s %.2f\n", cartprint.getGameName(), cartprint.getPrice());
                }
                System.out.println("----------------------------------------");
                System.out.printf(" Total Price:                     %.2f\n", total);
            }

            System.out.println("""
                    ========================================
                        Please pick an option:
                    1. Proceed with Checkout Order
                    2. Remove Item From Cart
                    3. Clear Cart
                    4. Return to Main Menu
                    """);
            System.out.print("Enter choice > ");

            try {
                choice = sc.nextInt();

                if (choice < 1 || choice > 4) {
                    valid = false;
                    System.out.println(ErrorMessage.INVALID_CHOICE);
                } else valid = true;

            } catch (Exception ex) {
                valid = false;
                sc.nextLine();
                System.out.println(ErrorMessage.INVALID_CHOICE);
            }

        } while (!valid);

        switch (choice) {
            case 1 -> {
                if (cartList.isEmpty()) {
                    System.out.println("Cannot proceed to payment. Cart is empty!");
                    CartMenu(cartList, gameList, wallet, card, sessionOrder);
                } else {
                    paymentView.displayPaymentMenu(cartList, gameList, wallet, card, sessionOrder);
                }
            }

            case 2 -> {
                removeItemView(cartList);

                double updatedSubTotal = cartService.calculateSubTotal(cartList);
                sessionOrder.setSubTotal(updatedSubTotal);
                sessionOrder.calculateTaxAndTotal();

                CartMenu(cartList, gameList, wallet, card, sessionOrder);
            }

            case 3 -> {
                cartService.clearCart(cartList);
                System.out.println("\n[ Cart has been cleared! ]\n");
                sessionOrder.setSubTotal(0.0);
                sessionOrder.calculateTaxAndTotal();
                CartMenu(cartList, gameList, wallet, card, sessionOrder);
            }

            case 4 -> CustomerMainMenu(cartList, gameList, wallet, card);
        }
    }

    // ======================================================
    //                    REMOVE CART ITEM
    // ======================================================
    public static void removeItemView(List<Cart> cartList) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Game ID (e.g., G1001) to remove: ");
        String idToRemove = sc.nextLine();

        boolean removed = cartService.removeItemFromCart(cartList, idToRemove);

        if (removed) {
            System.out.println("Successfully removed item with ID: " + idToRemove);
        } else {
            System.out.println(ErrorMessage.ITEM_NOT_FOUND);
        }
    }

    // ======================================================
    //                    SUMMARY REPORT
    // ======================================================
    public static void summaryReport(int[] quant) {

        ArrayList<Game> gameList = new ArrayList<>();
        filereadingGame(gameList);

        System.out.println("                           Welcome to Summary Report!                        ");
        System.out.println("=============================================================================");
        System.out.println("Games                         Quantity Sold                            Amount");

        for (int i = 0; i < gameList.size(); i++) {
            Game game = gameList.get(i);
            int quantitySold = quant[i];
            double amount = game.getPrice() * quantitySold;

            System.out.printf("\n%-30s %-20d %.2f%n\n", game.getGameName(), quantitySold, amount);
        }

        System.out.println("Total :                                                                      ");
        System.out.println("=============================================================================");
        System.out.println("                              Have a nice day!                               ");
    }

    // ======================================================
    //             GET QUANTITY FOR SUMMARY
    // ======================================================
    public static int[] getQuantity(int whichGame) {

        ArrayList<Game> gameList = new ArrayList();
        filereadingGame(gameList);

        int sizeHolder = gameList.size() - 1;
        int[] quantity = new int[sizeHolder];

        quantity[whichGame] += 1;

        return quantity;
    }

    // ======================================================
    //                     EXIT PROGRAM
    // ======================================================
    public static void ExitProgram() {
        System.out.println("See you next time!");
        System.exit(0);
    }
}
