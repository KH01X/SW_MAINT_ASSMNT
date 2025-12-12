package ModernizeSystem.View;

import java.util.Scanner;

/**
 * View responsible ONLY for displaying and handling
 * Login / Register menu input.
 */
public class LoginRegisterView {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Enum kept INSIDE the view to avoid over-engineering.
     */
    public enum MenuOption {
        REGISTER(1),
        LOGIN(2),
        EXIT(3);

        private final int value;

        MenuOption(int value) {
            this.value = value;
        }

        public static MenuOption fromInt(int input) {
            for (MenuOption option : values()) {
                if (option.value == input) {
                    return option;
                }
            }
            return null;
        }
    }

    public void displayTitle() {
        System.out.println(" |<========================================================================>|");
        System.out.println(" |    ===++=== ||                  /======>>                       |  //    |");
        System.out.println(" |       []    ||      //====     (                          ____  | //     |");
        System.out.println(" |       []    |====|  |_____      \\====\\  //==\\\\ //==\\\\  //      ||        |");
        System.out.println(" |       []    ||  ||  |                  ) ||  || \\\\__||  [       | \\      |");
        System.out.println(" |       []    ||  ||  L=====      ======/  ||  ||     ||  \\\\____  |  \\\\    |");
        System.out.println(" |                                                                          |");
        System.out.println(" |<========================================================================>|");
    }

    public MenuOption promptMenuChoice() {
        System.out.println("1. REGISTER");
        System.out.println("2. LOGIN");
        System.out.println("3. EXIT");
        System.out.print("Enter choice > ");

        try {
            int input = scanner.nextInt();
            scanner.nextLine(); // clear buffer
            return MenuOption.fromInt(input);
        } catch (Exception e) {
            scanner.nextLine();
            return null;
        }
    }
}
