package Navigate;

import java.util.Scanner;

// This class listens for user input from the console
// It runs in its own thread and continuously waits for input commands from the user
// When the user types pause the system state is changed so that the program pauses execution
// When the user types resume the system state is updated so that the program resumes
// If the user types undo the last movement command is undone from the command history
// When the user types the digit zero the program exits
public class InputListener implements Runnable {
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (Navigate.running) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("pause")) {
                Navigate.paused.set(true);
                System.out.println(Navigate.ANSI_YELLOW + "Game paused." + Navigate.ANSI_RESET);
                Utils.displayStatus();
            } else if (input.equals("resume")) {
                Navigate.paused.set(false);
                System.out.println(Navigate.ANSI_GREEN + "Game resumed." + Navigate.ANSI_RESET);
                Utils.displayStatus();
            } else if (input.equals("undo")) {
                // Call the method to undo the last executed command
                CommandProcessor.undoLastCommand();
            } else if (input.equals("0")) {
                System.exit(0);
            }
        }
        scanner.close();
    }
}