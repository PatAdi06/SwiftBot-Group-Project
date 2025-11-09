package Navigate;

// This class provides utility functions for the navigation system
// Currently it includes a method to display the current status of the system on the console
// The display status method prints whether the program is running or paused and shows the number of executed commands
// This helps the user know the current state of the navigation system at any time
public class Utils {
    public static void displayStatus() {
        System.out.println(Navigate.ANSI_CYAN + "----------------------------------------" + Navigate.ANSI_RESET);
        System.out.println(Navigate.ANSI_BLUE + "Current Status:" + Navigate.ANSI_RESET);
        System.out.println("Program Running: " + (Navigate.running ? Navigate.ANSI_GREEN + "Yes" : Navigate.ANSI_RED + "No") + Navigate.ANSI_RESET);
        System.out.println("Program Paused: " + (Navigate.paused.get() ? Navigate.ANSI_YELLOW + "Yes" : Navigate.ANSI_GREEN + "No") + Navigate.ANSI_RESET);
        System.out.println("Commands Executed: " + CommandProcessor.commandHistory.size());
        System.out.println(Navigate.ANSI_CYAN + "----------------------------------------" + Navigate.ANSI_RESET);
    }
}