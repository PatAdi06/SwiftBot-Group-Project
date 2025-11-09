package Navigate;

import swiftbot.*;

// This class handles movement operations using the robot API
// It provides a method to execute a movement command based on the given direction speed and duration
// The method ensures that while the movement is in progress it will pause if the system is paused and resume when it is not
// In addition this class provides a method to retrace a specified number of movement commands by reading the command history in reverse order
public class MovementController {
    public static void executeMovement(SwiftBotAPI swiftBot, String direction, int speed, int duration) {
        System.out.println(Navigate.ANSI_GREEN + "Executing movement: " + direction + " Speed: " + speed + " Duration: " + duration + Navigate.ANSI_RESET);
        int totalTime = duration * 1000;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        switch (direction) {
            case "F":
                swiftBot.move(speed, speed, totalTime);
                break;
            case "B":
                swiftBot.move(-speed, -speed, totalTime);
                break;
            case "R":
                swiftBot.move(60, -60, 500); // Initial rotation
                swiftBot.move(speed, speed, totalTime - 500);
                break;
            case "L":
                swiftBot.move(-60, 60, 500);
                swiftBot.move(speed, speed, totalTime - 500);
                break;
        }

        while (elapsedTime < totalTime) {
            if (Navigate.paused.get()) {
                System.out.println(Navigate.ANSI_YELLOW + "Movement paused. Waiting to finish current movement..." + Navigate.ANSI_RESET);
                while (Navigate.paused.get()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println(Navigate.ANSI_RED + "ERROR: Pause interrupted." + Navigate.ANSI_RESET);
                    }
                }
                System.out.println(Navigate.ANSI_GREEN + "Game resumed." + Navigate.ANSI_RESET);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(Navigate.ANSI_RED + "ERROR: Sleep interrupted." + Navigate.ANSI_RESET);
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        swiftBot.stopMove();
    }

    public static void retraceMovements(SwiftBotAPI swiftBot, int retraceCount) {
        if (retraceCount > CommandProcessor.commandHistory.size()) {
            System.out.println(Navigate.ANSI_RED + "ERROR: Not enough movements to retrace." + Navigate.ANSI_RESET);
            return;
        }
        System.out.println(Navigate.ANSI_GREEN + "Retracing last " + retraceCount + " movements..." + Navigate.ANSI_RESET);
        int count = 0;
        // Iterate in reverse order through the command history to retrace the movements
        for (int i = CommandProcessor.commandHistory.size() - 1; i >= 0 && count < retraceCount; i--) {
            String cmd = CommandProcessor.commandHistory.get(i);
            if (!cmd.startsWith("T")) {
                String[] parts = cmd.split(",");
                String dir = parts[0];
                int spd = Integer.parseInt(parts[1]);
                int dur = Integer.parseInt(parts[2]);
                executeMovement(swiftBot, dir, spd, dur);
                count++;
            }
        }
    }
}