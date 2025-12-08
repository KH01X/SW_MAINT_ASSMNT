package ModernizeSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final GameService gameService = new GameService();
    private static final CartService cartService = new CartService();
    private static final CustomerRepository customerRepository = new FileCustomerRepository();
    private static final AuthenticationService authenticationService = new AuthenticationService(customerRepository);
    private static final LoginService loginService = new LoginService(authenticationService);
    private static final RegistrationService registrationService = new RegistrationService(customerRepository);
    private static final Scanner SHARED_SCANNER = new Scanner(System.in);

    private static final ConsoleAuthController authController =
            new ConsoleAuthController(loginService, registrationService, SHARED_SCANNER);

    // ============================================================================
    // ENTRY POINT (REQUIRED TO RUN PROGRAM)
    // ============================================================================
    public static void main(String[] args) {
        titleScreen();
    }

    // ============================================================================
    // TITLE SCREEN
    // ============================================================================
    public static void titleScreen() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n================ GAME STORE =================");
            System.out.println("1. REGISTER");
            System.out.println("2. LOGIN");
            System.out.println("3. EXIT");

            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> authController.handleRegistration();
                case 2 -> handleLoginFlow();
                case 3 -> {
                    System.out.println("Goodbye!");
                    exit = true;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static int readMenuChoice() {
        while (true) {
            System.out.print("Enter choice > ");
            try {
                int choice = Integer.parseInt(SHARED_SCANNER.nextLine().trim());
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input!");
            }
        }
    }

    // ============================================================================
    // LOGIN FLOW
    // ============================================================================
    private static void handleLoginFlow() {
        User loggedInUser = authController.handleLogin();
        if (loggedInUser == null) return;

        if (loggedInUser instanceof Staff) {
            StaffMenu();
        } else if (loggedInUser instanceof Customer) {
            AccountWallet wallet = new AccountWallet();
            Credit card = new Credit();
            CustomerMainMenu(new ArrayList<>(), new ArrayList<>(), wallet, card);
        } else {
            LOGGER.warning("Unknown user type logged in.");
        }
    }

    // ============================================================================
    // STAFF MENU
    // ============================================================================
    public static void StaffMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== STAFF MENU ===");
            System.out.println("1. Add Game");
            System.out.println("2. View Report");
            System.out.println("3. Exit");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> fileWritingGame();
                case 2 -> summaryReport(getQuantity(0));
                case 3 -> ExitProgram();
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ============================================================================
    // CUSTOMER MAIN MENU
    // ============================================================================
    public static void CustomerMainMenu(ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet, Credit card) {
        Order order = new Order();

        while (true) {
            int choice = MainMenu();

            switch (choice) {
                case 1 -> {
                    gameList = filereadingGame(new ArrayList<>());
                    order.setSubTotal(gameSelection(gameList, cartList, wallet, card));
                }
                case 2 -> {
                    if (cartList.isEmpty()) {
                        System.out.println("Your cart is empty.");
                    } else {
                        System.out.println("Your total: " + viewOrder(order.getSubTotal()));
                        CartMenu(cartList, gameList, wallet, card, order.getSubTotal());
                    }
                }
                case 3 -> topUp(wallet);
                case 0 -> ExitProgram();
            }
        }
    }

    // ============================================================================
    // READ GAME DATA FROM FILE
    // ============================================================================
    public static ArrayList<Game> filereadingGame(ArrayList<Game> gameList) {
        gameList.clear();
        File file = new File("available_games.txt");

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split("\\|");
                if (p.length == 5) {
                    gameList.add(new Game(p[0], p[1], Double.parseDouble(p[2]), p[3], p[4]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Game file not found.");
        }

        return gameList;
    }

    // ============================================================================
    // ADD GAME (STAFF ONLY)
    // ============================================================================
    public static void fileWritingGame() {
        Scanner sc = new Scanner(System.in);
        Game game = new Game();

        System.out.print("Add new game? (Y/N) > ");
        char ans = sc.next().charAt(0);

        while (Character.toUpperCase(ans) == 'Y') {
            sc.nextLine();
            System.out.print("Game Name: ");
            game.setGameName(sc.nextLine());

            System.out.print("Price: ");
            game.setPrice(sc.nextDouble());

            sc.nextLine();
            System.out.print("Genre: ");
            game.setGenre(sc.nextLine());

            System.out.print("Description: ");
            game.setGameDesc(sc.nextLine());

            try (FileWriter w = new FileWriter("available_games.txt", true)) {
                w.write(String.format("%s|%s|%.2f|%s|%s%n",
                        game.getGameID(), game.getGameName(), game.getPrice(),
                        game.getGenre(), game.getGameDesc()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.print("Add another? (Y/N) > ");
            ans = sc.next().charAt(0);
        }
    }

    // ============================================================================
    // MAIN MENU OPTIONS
    // ============================================================================
    public static int MainMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("""
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
        }
    }

    // ============================================================================
    // GAME SELECTION MENU
    // ============================================================================
    public static double gameSelection(List<Game> gameList, List<Cart> cartList, AccountWallet wallet, Credit card) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            int i = 1;
            for (Game g : gameList) System.out.println(i++ + ") " + g.getGameName());

            System.out.print("\n0) Exit\nSelect a game > ");
            int option = sc.nextInt();

            if (option == 0) return cartService.calculateSubTotal(cartList);

            Game g = gameService.getSelectedGame(gameList, option);

            System.out.println("Selected: " + g.getGameName());
            System.out.println("[1] Add to Cart  [2] Back");
            int c = sc.nextInt();

            if (c == 1) {
                cartService.addItemToCart(cartList, g);
                System.out.println("Added!");
            }
        }
    }

    // ============================================================================
    // FORMAT ORDER SUMMARY
    // ============================================================================
    public static double viewOrder(double subtotal) {
        Order order = new Order();
        order.setSubTotal(subtotal);
        order.calculateTaxAndTotal();
        System.out.println(order);
        return order.getTotal();
    }

    // ============================================================================
    // TOP UP WALLET
    // ============================================================================
    public static void topUp(AccountWallet wallet) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Current balance: " + wallet.checkBalance());
        System.out.print("Enter amount (X to cancel): ");

        String input = sc.nextLine();
        if (input.equalsIgnoreCase("X")) return;

        try {
            double amt = Double.parseDouble(input);
            wallet.increase(amt);
            System.out.println("New balance: " + wallet.checkBalance());
        } catch (Exception e) {
            System.out.println("Invalid amount!");
        }
    }

    // ============================================================================
    // FULLY FIXED addBank()
    // ============================================================================
    public static void addBank(ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet, Credit card) {

        Scanner sc = new Scanner(System.in);
        String type, number, date;

        System.out.println("== Add Credit Card ==");

        System.out.print("Bank Name (X to cancel): ");
        type = sc.nextLine();
        if (type.equalsIgnoreCase("X")) return;

        System.out.print("8-digit Card Number: ");
        while (true) {
            number = sc.nextLine();
            if (number.matches("\\d{8}")) break;
            System.out.println("Invalid! Must be 8 digits.");
        }

        System.out.print("Expiry (MM/YY): ");
        while (true) {
            date = sc.nextLine();
            if (date.matches("\\d{2}/\\d{2}")) break;
            System.out.println("Invalid format.");
        }

        card.setType(type);
        card.setNumber(number);
        card.setExpDate(date);

        System.out.println("Card added successfully!");
    }

    // ============================================================================
    // CART MENU
    // ============================================================================
    public static void CartMenu(ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet, Credit card, double total) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CART ===");
        for (Cart c : cartList)
            System.out.println(c.getGameName() + " RM " + c.getPrice());

        System.out.println("""
                1. Checkout
                2. Back
                """);

        int choice = sc.nextInt();

        if (choice == 1)
            PaymentMenu(cartList, gameList, wallet, card, total);
        else
            CustomerMainMenu(cartList, gameList, wallet, card);
    }

    // ============================================================================
    // PAYMENT MENU
    // ============================================================================
    public static void PaymentMenu(ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet, Credit card, double total) {
        Scanner sc = new Scanner(System.in);

        System.out.println("""
                Payment Method:
                1. Credit Card
                2. Wallet
                """);

        int c = sc.nextInt();

        IPaymentMethod method;

        if (c == 1) {
            addBank(cartList, gameList, wallet, card);
            method = card;
        } else {
            method = wallet;
        }

        Order order = new Order();
        order.setSubTotal(total);
        order.calculateTaxAndTotal();

        boolean success = PaymentProcessor.executePayment(method, cartList, order, wallet, card);

        if (success) {
            System.out.println("Payment Successful!");
            titleScreen();
        } else {
            FailedPaymentMenu(cartList, gameList, wallet, card, total);
        }
    }

    // ============================================================================
    // FAILED PAYMENT MENU
    // ============================================================================
    public static void FailedPaymentMenu(ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet, Credit card, double total) {
        Scanner sc = new Scanner(System.in);

        System.out.println("""
                Insufficient Funds!
                1. Top-Up Wallet
                2. Main Menu
                3. Cart Menu
                4. Retry Payment
                """);

        int c = sc.nextInt();

        switch (c) {
            case 1 -> {
                topUp(wallet);
                PaymentMenu(cartList, gameList, wallet, card, total);
            }
            case 2 -> CustomerMainMenu(cartList, gameList, wallet, card);
            case 3 -> CartMenu(cartList, gameList, wallet, card, total);
            case 4 -> PaymentMenu(cartList, gameList, wallet, card, total);
        }
    }

    // ============================================================================
    // SUMMARY REPORT
    // ============================================================================
    public static void summaryReport(int[] quant) {
        ArrayList<Game> games = filereadingGame(new ArrayList<>());

        System.out.println("\n=== SALES REPORT ===");

        for (int i = 0; i < games.size(); i++) {
            Game g = games.get(i);
            System.out.printf("%-20s Qty: %d  RM %.2f%n",
                    g.getGameName(), quant[i], quant[i] * g.getPrice());
        }
    }

    public static int[] getQuantity(int whichGame) {
        ArrayList<Game> games = filereadingGame(new ArrayList<>());
        int[] qty = new int[games.size()];
        qty[whichGame]++;
        return qty;
    }

    // ============================================================================
    // EXIT PROGRAM
    // ============================================================================
    public static void ExitProgram() {
        System.out.println("See you next time!");
        System.exit(0);
    }
}
