package Navigate;

import swiftbot.*;
import java.util.concurrent.atomic.AtomicBoolean;

// This is the main class that sets up and runs the navigation system
// It initializes the robot API and sets the global state such as whether the program is running or paused
// It displays a banner and instructions for the user and starts a separate thread to listen for input commands
// The class also enables a physical button on the robot to exit the program when pressed
// In the main loop it continuously scans for QR codes and processes any commands found in the code
// It respects the paused state and pauses scanning when necessary and sleeps between scans
public class Navigate {
    // This variable holds the robot API instance for controlling the robot
    public static SwiftBotAPI swiftBot;
    // This variable indicates whether the program should continue running
    public static volatile boolean running = true;
    // This atomic boolean indicates whether the program is currently paused
    public static AtomicBoolean paused = new AtomicBoolean(false);
    // This variable stores the time when the program started
    public static long startTime;

    // The following constants are ANSI escape codes for colored output on the console
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static void main(String[] args) {
        // Print a banner and instructions for the navigation system
        System.out.println(ANSI_CYAN + "  _   _    ___     _____ ____    _  _____ _____ " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | \\ | |  / \\ \\   / /_ _/ ___|  / \\|_   _| ____|" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " |  \\| | / _ \\ \\ / / | | |  _  / _ \\ | | |  _|  " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |\\  |/ ___ \\ V /  | | |_| |/ ___ \\| | | |___ " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " |_| \\_/_/   \\_\\_/  |___\\____/_/   \\_\\_| |_____|" + ANSI_RESET);
        System.out.println();
        System.out.println(ANSI_RED + "To Pause the game or Resume it: type pause/resume" + ANSI_RESET);
        System.out.println(ANSI_RED + "To Undo the last command: type undo" + ANSI_RESET);
        System.out.println(ANSI_RED + "If you want to suddenly quit the game: type 0" + ANSI_RESET);
        System.out.println();

        // Initialize the robot API and record the start time of the program
        try {
            swiftBot = new SwiftBotAPI();
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            System.out.println(ANSI_RED + "\nI2C disabled!" + ANSI_RESET);
            System.out.println("Run the following command:");
            System.out.println("sudo raspi-config nonint do_i2c 0\n");
            System.exit(5);
        }

        // Enable a physical button on the robot so that pressing it will exit the program
        swiftBot.enableButton(Button.X, () -> {
            System.out.println(ANSI_RED + "Button X pressed. Quitting game." + ANSI_RESET);
            System.out.println();
            System.out.println(ANSI_RED + "  ____    _    __  __ _____                                 " + ANSI_RESET);
            System.out.println(ANSI_RED + " / ___|  / \\  |  \\/  | ____|                                " + ANSI_RESET);
            System.out.println(ANSI_RED + "| |  _  / _ \\ | |\\/| |  _|                                  " + ANSI_RESET);
            System.out.println(ANSI_RED + "| |_| |/ ___ \\| |  | | |___                                 " + ANSI_RESET);
            System.out.println(ANSI_RED + " \\____/_/___\\_\\_|  |_|_____|_ _   _    _  _____ _____ ____  " + ANSI_RESET);
            System.out.println(ANSI_RED + "|_   _| ____|  _ \\|  \\/  |_ _| \\ | |  / \\|_   _| ____|  _ \\ " + ANSI_RESET);
            System.out.println(ANSI_RED + "  | | |  _| | |_) | |\\/| || ||  \\| | / _ \\ | | |  _| | | | |" + ANSI_RESET);
            System.out.println(ANSI_RED + "  | | | |___|  _ <| |  | || || |\\  |/ ___ \\| | | |___| |_| |" + ANSI_RESET);
            System.out.println(ANSI_RED + "  |_| |_____|_| \\_\\_|  |_|___|_| \\_/_/   \\_\\_| |_____|____/ " + ANSI_RESET);
            System.out.println();
            running = false;
            System.exit(0);
        });

        // Start a separate thread that listens for input commands such as pause resume and undo
        InputListener inputListener = new InputListener();
        Thread listenerThread = new Thread(inputListener);
        listenerThread.setDaemon(true);
        listenerThread.start();

        // Create instances of the QR scanner and the command processor
        QRScanner qrScanner = new QRScanner(swiftBot);
        CommandProcessor commandProcessor = new CommandProcessor(swiftBot, startTime);

        System.out.println(ANSI_CYAN + "Waiting to scan QR code..." + ANSI_RESET);
        Utils.displayStatus();

        // Main loop that continuously scans for QR codes and processes commands when a code is detected
        while (running) {
            // Wait if the system is paused
            while (paused.get()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(ANSI_RED + "ERROR: Pause interrupted." + ANSI_RESET);
                }
            }

            String decodedMessage = qrScanner.captureImage();
            if (decodedMessage != null && !decodedMessage.isEmpty()) {
                if (!commandProcessor.validateAndProcess(decodedMessage)) {
                    System.out.println(ANSI_RED + "Invalid command(s) detected. No execution performed." + ANSI_RESET);
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(ANSI_RED + "ERROR: Sleep interrupted." + ANSI_RESET);
            }
        }
    }
}