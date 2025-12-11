package ModernizeSystem.View;

import ModernizeSystem.*;
import ModernizeSystem.CartService;
import ModernizeSystem.Controller.FailedPaymentOption;
import ModernizeSystem.Controller.PaymentProcessor;
import ModernizeSystem.Controller.IPaymentMethod;
import ModernizeSystem.Controller.PaymentChoice;
import ModernizeSystem.Model.AccountWallet;
import ModernizeSystem.Model.Order;
import ModernizeSystem.Model.Credit;
import ModernizeSystem.Main;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentView {

    private static final Logger LOGGER = Logger.getLogger(PaymentView.class.getName());
    private final CartService cartService;
    private final Scanner sc;

    /**
     * Constructor for the PaymentView, initializing its dependencies.
     *
     * @param cartService The service used to perform Cart-related logic (like calculating totals), ensuring Separation of Concerns.
     * @return A new PaymentView instance used to handle all payment-related user input and output.
     */
    public PaymentView(CartService cartService) {
        this.cartService = cartService;
        this.sc = new Scanner(System.in);
    }

    /**
     * Displays the main menu for selecting a payment type (Credit Card or Account Wallet)
     * and handles the core transaction dispatching using the selected IPaymentMethod.
     *
     * @param cartList The current list of items being purchased.
     * @param gameList The current list of available games.
     * @param wallet The user's AccountWallet object, passed for balance check and debiting.
     * @param card The user's Credit object, passed for setting details and debiting.
     * @param sessionOrder The persistent Order object containing the final calculated total and tax breakdown, used to execute the transaction.
     * @return void. Returns nothing, as navigation (to Main Menu or Failed Payment Menu) is handled internally.
     */
    public void displayPaymentMenu(
            ArrayList<Cart> cartList,
            ArrayList<Game> gameList,
            AccountWallet wallet,
            Credit card,
            Order sessionOrder) {

        int choiceInt = 0;
        boolean valid;
        double finalTotal = sessionOrder.getTotal();

        // ... [ASCII art removed for brevity] ...

        do {
            System.out.println("""
                  Select Payment Type:
                 1. Credit Card
                 2. Account Wallet
                """);
            System.out.print("Enter choice > ");

            try {
                choiceInt = sc.nextInt();
                valid = (choiceInt >= 1 && choiceInt <= 2);
            } catch (InputMismatchException ex) {
                valid = false;
                sc.nextLine();
                LOGGER.log(Level.WARNING, "Invalid input in PaymentMenu: " + ex.getMessage());
                System.out.println(ErrorMessage.INVALID_CHOICE);
            }

        } while (!valid);

        sc.nextLine();

        IPaymentMethod selectedMethod = null;
        PaymentChoice choice = PaymentChoice.fromValue(choiceInt);

        switch (choice) {
            case CREDIT_CARD -> {
                addBank(cartList, gameList, wallet, card);
                selectedMethod = card;
            }
            case ACCOUNT_WALLET -> {
                selectedMethod = wallet;

                System.out.println("\nYour Wallet Balance: " + wallet.checkBalance());
                System.out.printf("Total Price       : %.2f", finalTotal);

                // Nested decision loop
                if (!confirmWalletPayment()) {
                    // Navigate back to main menu by exiting current flow
                    Main.CustomerMainMenu(cartList, gameList, wallet, card);
                    return;
                }
            }
            case INVALID -> {
                System.out.println("Invalid payment method!");
                return;
            }
        }

        boolean paymentSuccess = PaymentProcessor.executePayment(selectedMethod, cartList, sessionOrder, wallet, card);

        if (paymentSuccess) {
            Main.titleScreen();
        } else {
            displayFailedPaymentMenu(cartList, gameList, wallet, card, sessionOrder);
        }
    }

    /**
     * Prompts the user for final confirmation before processing an Account Wallet payment.
     * This separates the confirmation step from the main payment method selection.
     *
     * @return true if the user chooses to proceed with the payment (Option 1), false if they choose to cancel and return to the main menu.
     */
    private boolean confirmWalletPayment() {
        int subChoice = 0;
        boolean subValid;

        do {
            System.out.println("""
                \n\t1. Yes, pay with Account Wallet.
                2. Cancel, return to Main Menu.
            """);
            System.out.print("Enter choice > ");

            try {
                subChoice = sc.nextInt();
                subValid = (subChoice == 1 || subChoice == 2);
            } catch (InputMismatchException ex) {
                subValid = false;
                sc.nextLine();
                LOGGER.log(Level.WARNING, "Invalid input in Wallet confirm: " + ex.getMessage());
                System.out.println(ErrorMessage.INVALID_CHOICE);
            }

            sc.nextLine();

        } while (!subValid);

        return subChoice == 1; // True = Proceed, False = Cancel
    }


    /**
     * Displays a dedicated menu when a payment fails (e.g., due to insufficient funds)
     * and handles user navigation based on the selected remediation option (Top-Up, Retry, etc.).
     *
     * @param cartList The current list of items being purchased.
     * @param gameList The current list of available games.
     * @param wallet The user's AccountWallet object.
     * @param card The user's Credit object.
     * @param sessionOrder The persistent Order object, passed to maintain state.
     * @return void. Returns nothing, as navigation (to Main Menu, Top-Up, Cart Menu) is handled internally.
     */
    public void displayFailedPaymentMenu(
            ArrayList<Cart> cartList,
            ArrayList<Game> gameList,
            AccountWallet wallet,
            Credit card,
            Order sessionOrder) {

        boolean valid;
        int choiceInt = 0;

        do {
            System.out.println("""
                    \nPlease select another option:
                        1. Top-Up Account Wallet
                        2. Return to Main Menu
                        3. Return to Cart Menu
                        4. Retry Payment Methods
                    """);
            System.out.print("Enter choice > ");

            try {
                choiceInt = sc.nextInt();
                valid = (choiceInt >= 1 && choiceInt <= 4);
            } catch (InputMismatchException ex) {
                valid = false;
                sc.nextLine();
                LOGGER.log(Level.WARNING, "Invalid input in FailedPaymentMenu: " + ex.getMessage());
                System.out.println(ErrorMessage.INVALID_CHOICE);
            }

        } while (!valid);

        sc.nextLine();
        FailedPaymentOption choice = FailedPaymentOption.fromValue(choiceInt);

        switch (choice) {
            case TOP_UP_WALLET -> {
                addTopUpAmount(wallet);
                displayPaymentMenu(cartList, gameList, wallet, card, sessionOrder);
            }
            case RETURN_TO_MAIN_MENU -> Main.CustomerMainMenu(cartList, gameList, wallet, card);
            case RETURN_TO_CART_MENU -> Main.CartMenu(cartList, gameList, wallet, card, sessionOrder);
            case RETRY_PAYMENT -> displayPaymentMenu(cartList, gameList, wallet, card, sessionOrder);
            case INVALID -> {
                LOGGER.log(Level.SEVERE, "Unexpected Invalid state in FailedPaymentMenu.");
                System.out.println("Unexpected error. Returning to Main Menu.");
                Main.CustomerMainMenu(cartList, gameList, wallet, card);
            }
        }
    }

    /**
     * Handles the input and validation for adding a credit card ("Add Bank").
     * Collects the card type (bank name) before delegating subsequent card number and expiry input to helper methods.
     *
     * @param cartList The current list of items.
     * @param gameList The list of available games.
     * @param wallet The user's AccountWallet.
     * @param card The Credit object to be populated with the user's input.
     * @return void. Returns nothing, as navigation (back to main menu) is handled internally if the user chooses 'X'.
     */
    public void addBank(
            ArrayList<Cart> cartList,
            ArrayList<Game> gameList,
            AccountWallet wallet,
            Credit card) {

        String cardType;

        System.out.print("""
                \n=============== Adding Bank Account ================
                Do 'X' to Exit To Main Menu
                """);
        System.out.print("\nEnter Bank Associated with Account > ");

        cardType = sc.nextLine();

        if (cardType.equalsIgnoreCase("X")) {
            Main.CustomerMainMenu(cartList, gameList, wallet, card);
            return;
        }

        handleCardNumberInput(card, cartList, gameList, wallet);
        handleExpiryDateInput(card, cartList, gameList, wallet);

        card.setType(cardType);

        System.out.println("Successfully Added a Credit Card!");
        System.out.println(card);
    }

    /**
     * Handles the specific input and validation loop for the 8-digit bank account/card number.
     *
     * @param card The Credit object to set the validated card number on.
     * @param cartList The current list of items (used for navigation fallback).
     * @param gameList The list of available games (used for navigation fallback).
     * @param wallet The user's AccountWallet (used for navigation fallback).
     * @return void. Returns nothing, setting the card number on the Credit object internally.
     */
    public void handleCardNumberInput(Credit card, ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet) {
        Scanner sc = new Scanner(System.in);
        String cardNumber;
        boolean validNumber = false;

        System.out.print("Enter Bank Account Number (8 digits) > ");
        do {
            cardNumber = sc.next();
            if (cardNumber.equalsIgnoreCase("X")) {
                Main.CustomerMainMenu(cartList, gameList, wallet, card);
                return;
            } else if (isEightDigits(cardNumber)) {
                validNumber = true;
            } else {
                System.out.println("Invalid Card Number! Must be exactly 8 digits!");
                System.out.print(" Enter Bank Account Number (8 digits) > ");
            }
        } while (!validNumber);
        card.setNumber(cardNumber);
    }

    /**
     * Handles the specific input and validation loop for the card expiration date (MM/YY).
     *
     * @param card The Credit object to set the validated expiration date on.
     * @param cartList The current list of items (used for navigation fallback).
     * @param gameList The list of available games (used for navigation fallback).
     * @param wallet The user's AccountWallet (used for navigation fallback).
     * @return void. Returns nothing, setting the expiration date on the Credit object internally.
     */
    public void handleExpiryDateInput(Credit card, ArrayList<Cart> cartList, ArrayList<Game> gameList, AccountWallet wallet) {
        Scanner sc = new Scanner(System.in);
        String cardExpDate;
        boolean validDate = false;

        System.out.print("Enter Card Expiration Date (MM/YY) > ");

        do {
            cardExpDate = sc.next();
            if (cardExpDate.equalsIgnoreCase("X")) {
                Main.CustomerMainMenu(cartList, gameList, wallet, card);
                return;
            } else if (isValidDate(cardExpDate)) {
                validDate = true;
            } else {
                System.out.println("Invalid Expiration Date!");
                System.out.print(" Enter Card Expiration Date (MM/YY) > ");
            }
        } while (!validDate);
        card.setExpDate(cardExpDate);
    }

    /**
     * Utility method to validate if a date string is in MM/YY format and represents a future date.
     * Uses the modern java.time API for robust and accurate date checking.
     *
     * @param dateStr The date string provided by the user (e.g., "12/26").
     * @return true if the date is in MM/YY format, the month is valid (1-12), and the date is strictly after the current month, false otherwise.
     */
    public static boolean isValidDate(String dateStr) {
        String patternStr = "^\\d{2}/\\d{2}$";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(dateStr);

        if(!matcher.matches()) {
            return false;
        }

        try{
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth inputYearMonth = YearMonth.parse(dateStr, inputFormatter);
            YearMonth currentYearMonth = YearMonth.now();

            //check the month range 1-12
            if(inputYearMonth.getMonthValue() <1 || inputYearMonth.getMonthValue() > 12) {
                return false;
            }

            //return true if the input date is STRICTLY AFTER the current YearMonth. Else false
            return inputYearMonth.isAfter(currentYearMonth);

        } catch (DateTimeParseException e) {
            return false;
        }
    }


    /**
     * Utility method to validate if an input string consists of exactly 8 digits,
     * used for checking the card number input format.
     *
     * @param input The string entered by the user for the card number.
     * @return true if the string matches the pattern of exactly eight numerical digits, false otherwise.
     */
    public static boolean isEightDigits(String input) {
        return Pattern.compile("\\d{8}").matcher(input).matches();
    }

    /**
     * Handles the input and validation loop for the Account Wallet top-up amount.
     * Ensures the input is numerical, positive, or the exit command 'X'. Uses a loop for robust retries.
     *
     * @param wallet The AccountWallet object to be credited (increased) with the validated amount.
     * @return void. Returns nothing, performing the transaction internally and handling exits/errors.
     */
    public void addTopUpAmount(AccountWallet wallet) {
        LOGGER.log(Level.INFO, "Initiating Top-Up process.");
        String topupInput;
        double amount;
        boolean inputValid = false;

        System.out.println(wallet.toString());

        while (!inputValid) {
            System.out.println("\nPlease input amount to Top-Up below: ");
            System.out.println("Do 'X' to Exit");
            System.out.print("Enter Amount to Top-Up: ");

            topupInput = sc.nextLine().trim();

            if (topupInput.equalsIgnoreCase("X")) {
                LOGGER.log(Level.INFO, "Top-Up cancelled by user.");
                return;
            }

            try {
                amount = Double.parseDouble(topupInput);

                if (amount <= 0) {
                    LOGGER.log(Level.WARNING, "Attempted to top up zero or negative amount: " + amount);
                    System.out.println("Amount must be greater than zero.");
                } else {
                    wallet.increase(amount);
                    System.out.println("\nSuccessful Top-Up!");
                    System.out.println("Your Current Balance is: " + String.format("%.2f", wallet.checkBalance()));
                    inputValid = true;
                }

            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid input for Top-Up amount: " + topupInput);
                System.out.println("Invalid input. Please enter a numerical amount (e.g., 50.00).");
            }
        }
    }

}