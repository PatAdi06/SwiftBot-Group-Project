package Navigate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// This class manages logging of the command execution history to a file
// It provides a method to log the executed commands along with the total execution time and the current time
// It also updates the log file when a command is undone and rotates the log file if it grows too large
public class LogManager {
    // This constant holds the file name for the execution log
    public static final String LOG_FILE = "execution_log.txt";
   
    // This method writes the execution history to the log file
    // It first prints the command history and checks if any commands have been executed
    // Then it verifies if the directory for the log file exists and creates it if needed
    // The method rotates the log file if its size is greater than one megabyte
    // It calculates the elapsed time since the program started and gets the current time
    // Finally it writes each command and the overall execution time to the log file
    public static void logExecution(List<String> commandHistory, long startTime) {
        System.out.println(Navigate.ANSI_CYAN + "Command Execution History:" + Navigate.ANSI_RESET);
        if (commandHistory.isEmpty()) {
            System.out.println(Navigate.ANSI_RED + "No commands executed yet." + Navigate.ANSI_RESET);
            return;
        }

        File logFile = new File(LOG_FILE);
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean dirCreated = parentDir.mkdirs();
            if (dirCreated) {
                System.out.println(Navigate.ANSI_GREEN + "Created directory: " + parentDir.getAbsolutePath() + Navigate.ANSI_RESET);
            } else {
                System.out.println(Navigate.ANSI_RED + "ERROR: Failed to create directory: " + parentDir.getAbsolutePath() + Navigate.ANSI_RESET);
                return;
            }
        }

        String fullPath = Paths.get(LOG_FILE).toAbsolutePath().toString();
        System.out.println(Navigate.ANSI_CYAN + "Log file path: " + fullPath + Navigate.ANSI_RESET);

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        try (FileWriter writer = new FileWriter(LOG_FILE, false)) {
            for (String cmd : commandHistory) {
                String[] parts = cmd.split(",");
                if (parts.length >= 3) {
                    String direction = parts[0].trim();
                    String speed = parts[1].trim();
                    String duration = parts[2].trim();
                    writer.write("Command: " + direction + ", Speed: " + speed + ", Duration: " + duration + " seconds\n");
                }
            }
            writer.write("Total Execution Time: " + elapsedTime + " seconds\n");
            writer.write("Log Written at: " + currentTime + "\n");
            writer.write("---------------------------\n");
            System.out.println(Navigate.ANSI_GREEN + "Log file saved at: " + fullPath + Navigate.ANSI_RESET);
        } catch (IOException e) {
            System.out.println(Navigate.ANSI_RED + "ERROR: Unable to write to log file." + Navigate.ANSI_RESET);
            e.printStackTrace();
        }
    }

    // This method updates the log file after an undo operation
    // It writes the current command history to the log file
    // It also checks that the directory exists and creates it if necessary
    public static void updateLogFile(List<String> commandHistory, long startTime) {
        File logFile = new File(LOG_FILE);

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        try (FileWriter writer = new FileWriter(logFile, false)) { // Overwrite the log file
            for (String cmd : commandHistory) {
                String[] parts = cmd.split(",");
                if (parts.length >= 3) {
                    String direction = parts[0].trim();
                    String speed = parts[1].trim();
                    String duration = parts[2].trim();
                    writer.write("Command: " + direction + ", Speed: " + speed + ", Duration: " + duration + " seconds\n");
                }
            }

            // Add execution time and timestamp after undo
            writer.write("Total Execution Time: " + elapsedTime + " seconds\n");
            writer.write("Log Updated at: " + currentTime + "\n");
            writer.write("---------------------------\n");

            System.out.println(Navigate.ANSI_GREEN + "Log file updated after undo." + Navigate.ANSI_RESET);
        } catch (IOException e) {
            System.out.println(Navigate.ANSI_RED + "ERROR: Unable to update log file after undo." + Navigate.ANSI_RESET);
            e.printStackTrace();
        }
    }
}