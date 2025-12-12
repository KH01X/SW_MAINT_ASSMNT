package ModernizeSystem.View;

import java.util.Scanner;

public class CustomerView {

    private final Scanner scanner = new Scanner(System.in);

    public enum CustomerOption {
        GAMES(1),
        CART(2),
        TOP_UP(3),
        EXIT(0);

        private final int value;

        CustomerOption(int value) {
            this.value = value;
        }

        public static CustomerOption fromInt(int input) {
            for (CustomerOption option : values()) {
                if (option.value == input) return option;
            }
            return null;
        }
    }

    public CustomerOption prompt() {
        System.out.println("""
                ==============================
                 Welcome to Main Menu
                 1. Games on Sale
                 2. Open Cart
                 3. Top-Up Wallet
                 0. Exit
                ==============================
                """);
        System.out.print("Enter choice > ");

        try {
            int input = scanner.nextInt();
            scanner.nextLine();
            return CustomerOption.fromInt(input);
        } catch (Exception e) {
            scanner.nextLine();
            return null;
        }
    }
}
