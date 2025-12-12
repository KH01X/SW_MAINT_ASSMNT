package ModernizeSystem.View;

import java.util.Scanner;

public class StaffView {

    private final Scanner scanner = new Scanner(System.in);

    public enum StaffOption {
        ADD_GAME(1),
        VIEW_REPORT(2),
        EXIT(3);

        private final int value;

        StaffOption(int value) {
            this.value = value;
        }

        public static StaffOption fromInt(int input) {
            for (StaffOption option : values()) {
                if (option.value == input) return option;
            }
            return null;
        }
    }

    public StaffOption prompt() {
        System.out.println("What do you want to do?");
        System.out.println("1. Add Game");
        System.out.println("2. View Report");
        System.out.println("3. Exit");
        System.out.print("Enter choice > ");

        try {
            int input = scanner.nextInt();
            scanner.nextLine();
            return StaffOption.fromInt(input);
        } catch (Exception e) {
            scanner.nextLine();
            return null;
        }
    }
}
