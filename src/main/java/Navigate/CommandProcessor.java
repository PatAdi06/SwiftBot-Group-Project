package Navigate;

import swiftbot.*;
import java.util.ArrayList;
import java.util.List;

// This class is responsible for processing commands from a decoded QR code
// It validates the command format and then executes only those commands that pass the checks
// It also maintains a history of movement commands for possible undo actions
public class CommandProcessor {
    // This variable holds the robot API instance used for moving the robot
    private SwiftBotAPI swiftBot;
    // This variable stores the time when the program started running
    private static long startTime= System.currentTimeMillis();
;
    // This constant defines the maximum number of commands allowed in the command history
    public static final int MAX_COMMAND_HISTORY = 100;
    // This list holds the history of commands that have been executed
    public static List<String> commandHistory = new ArrayList<>();
    // The constructor accepts a robot API instance and the start time and then saves these values
    public CommandProcessor(SwiftBotAPI swiftBot, long startTime) {
        this.swiftBot = swiftBot;
        CommandProcessor.startTime = startTime;
    }

    // This method validates the decoded message from the QR code and processes the commands if they are valid
    // It first prints that it is checking the command format and then splits the message into individual commands
    // It checks that the number of commands does not exceed ten and that none of the commands are empty
    // For each command it splits the text using the comma and extracts the command type and its parameters
    // For movement commands it verifies that the speed does not exceed one hundred and the duration does not exceed six seconds
    // For retrace commands it ensures that the retrace count is a positive number and that the format is correct
    // For log commands no further validation is necessary
    // If any check fails an error message is printed and the method returns false otherwise it executes the commands and returns true
    public boolean validateAndProcess(String decodedMessage) {
        System.out.println(Navigate.ANSI_BLUE + "Checking command format: " + decodedMessage + Navigate.ANSI_RESET);
        String[] commands = decodedMessage.split(";");
        List<String> validCommands = new ArrayList<>();

        if (commands.length > 10) {
            System.out.println(Navigate.ANSI_RED + "ERROR: Maximum 10 commands allowed per QR code." + Navigate.ANSI_RESET);
            return false;
        }

        for (String cmd : commands) {
            String command = cmd.trim();
            if (command.isEmpty()) {
                System.out.println(Navigate.ANSI_RED + "ERROR: Empty command." + Navigate.ANSI_RESET);
                return false;
            }

            String[] parts = command.split(",");
            if (parts.length < 1) {
                System.out.println(Navigate.ANSI_RED + "ERROR: Invalid command format: " + command + Navigate.ANSI_RESET);
                return false;
            }

            String direction = parts[0].trim();
            int speed = 0, duration = 0;

            if (parts.length >= 3) {
                try {
                    speed = Integer.parseInt(parts[1].trim());
                    duration = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    System.out.println(Navigate.ANSI_RED + "ERROR: Invalid number format in command: " + command + Navigate.ANSI_RESET);
                    return false;
                }
            }

            // Validate the command based on the command type
            switch (direction) {
                case "F":
                case "B":
                case "R":
                case "L":
                    if (speed > 100 || duration > 6) {
                        System.out.println(Navigate.ANSI_RED + "ERROR: Speed or duration out of bounds: " + command + Navigate.ANSI_RESET);
                        return false;
                    }
                    break;
                case "T":
                    if (parts.length == 2) {
                        try {
                            int retraceCount = Integer.parseInt(parts[1].trim());
                            if (retraceCount <= 0) {
                                System.out.println(Navigate.ANSI_RED + "ERROR: Retrace count must be positive." + Navigate.ANSI_RESET);
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(Navigate.ANSI_RED + "ERROR: Invalid retrace count format." + Navigate.ANSI_RESET);
                            return false;
                        }
                    } else {
                        System.out.println(Navigate.ANSI_RED + "ERROR: Invalid retrace command format." + Navigate.ANSI_RESET);
                        return false;
                    }
                    break;
                case "W":
                    // This command indicates that logging is needed so no further check is performed
                    break;
                default:
                    System.out.println(Navigate.ANSI_RED + "ERROR: Unrecognized command type: " + command + Navigate.ANSI_RESET);
                    return false;
            }

            validCommands.add(command);
        }

        executeCommands(validCommands);
        return true;
    }

    // This method executes the list of commands that have been validated
    // It loops through the list of commands and if the system is paused it waits until it is resumed
    // For each command it extracts the direction and parameters and then calls the movement controller for movement commands
    // It adds movement commands to the command history and calls the retrace method for retrace commands
    // If a log command is present a flag is set so that after all commands are processed the log is written
    public void executeCommands(List<String> validCommands) {
        boolean logExecutionNeeded = false;
        int commandIndex = 0;

        while (commandIndex < validCommands.size()) {
            // Wait while the system is in a paused state
            while (Navigate.paused.get()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(Navigate.ANSI_RED + "ERROR: Pause interrupted." + Navigate.ANSI_RESET);
                }
            }

            String command = validCommands.get(commandIndex);
            String[] parts = command.split(",");
            String direction = parts[0].trim();

            switch (direction) {
                case "F":
                case "B":
                case "R":
                case "L":
                    int speed = Integer.parseInt(parts[1].trim());
                    int duration = Integer.parseInt(parts[2].trim());
                    MovementController.executeMovement(swiftBot, direction, speed, duration);
                    addToHistory(direction + "," + speed + "," + duration);
                    break;
                case "T":
                    int retraceCount = Integer.parseInt(parts[1].trim());
                    MovementController.retraceMovements(swiftBot, retraceCount);
                    break;
                case "W":
                    logExecutionNeeded = true;
                    break;
            }
            commandIndex++;
        }

        if (logExecutionNeeded) {
            LogManager.logExecution(commandHistory, startTime);
        }
    }

    // This method adds a movement command to the command history list
    // It only adds commands that are movement related and ignores commands for retrace or logging
    // If the history list is full the oldest command is removed before adding the new command
    public static void addToHistory(String command) {
        if (!command.startsWith("W") && !command.startsWith("T")) {
            if (commandHistory.size() >= MAX_COMMAND_HISTORY) {
                commandHistory.remove(0);
            }
            commandHistory.add(command);
        }
    }

    // This method undoes the last movement command by removing it from the command history
    // If there are no commands to undo an error message is printed
    // Otherwise the last command is removed and the log file is updated accordingly
    public static void undoLastCommand() {
        if (commandHistory.isEmpty()) {
            System.out.println(Navigate.ANSI_RED + "No commands to undo." + Navigate.ANSI_RESET);
            return;
        }
        String lastCommand = commandHistory.remove(commandHistory.size() - 1);
        System.out.println(Navigate.ANSI_YELLOW + "Undoing last command: " + lastCommand + Navigate.ANSI_RESET);
        LogManager.updateLogFile(commandHistory, startTime);
    }
}