package tunnel_vision;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import swiftbot.Button;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;

public class DoesMySwiftBotWork {
    private final SwiftBotAPI robot;
    private final TunnelDetector tunnelDetector;
    private final ImageProcessor imageProcessor;
    private final JourneyLogger journeyLogger;
    private final RobotController robotController;
    
    // Configuration
    private static final class Config {
        static final int STOP_DISTANCE_CM = 40; // Distance at which the bot stops for an obstacle
        static final int IMAGE_CAPTURE_INTERVAL_MS = 500; // Time between image captures
        static final int BOT_SPEED_PERCENT = 33; // Speed of the bot in percentage
        static final double LIGHT_INTENSITY_THRESHOLD = 0.3; // Threshold for detecting tunnels based on darkness
        static final double DISTANCE_PER_CYCLE_CM = 2.5; // Distance covered per cycle in cm
        static final int TOTAL_TUNNELS = 3;// Maximum number of tunnels in the sequence
        static final int CALIBRATION_DELAY_MS = 1000;  // Wait time for calibration
        static final String LOG_FILE_NAME = "tunnel_vision_SwiftBot_log.txt"; // Name of the log file
        static final String IMAGE_DIRECTORY = "."; // Directory where images are saved
    }
    

    private boolean isOperational = true;
    private boolean hasStarted = false;
    private boolean hasCompleted = false;

    public DoesMySwiftBotWork() {
        this.robot = new SwiftBotAPI();
        this.tunnelDetector = new TunnelDetector();
        this.imageProcessor = new ImageProcessor();
        this.journeyLogger = new JourneyLogger();
        this.robotController = new RobotController(robot);
    }

    public static void main(String[] args) {
        DoesMySwiftBotWork program = new DoesMySwiftBotWork();
        program.initialize();
        program.run();
    }

    private void initialize() {
        displayWelcomeMessage();
        configureRobotButtons();
    }

    private void run() {
        while (isOperational) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Program interrupted: " + e.getMessage());
                break;
            }
        }
    }

    private void displayWelcomeMessage() {
        System.out.println("\n************************************");
        System.out.println("Task 5: Tunnel Vision");
        System.out.println("************************************");
        System.out.println("\nWelcome to Tunnel Vision!");
        System.out.println("\nProgram Requirements:");
        System.out.println("- Minimum tunnel length: 30 cm");
        System.out.println("- Tunnel lengths should vary by 10-15 cm");
        System.out.println("- Three tunnels will be detected");
        System.out.println("- Robot will move at a slow speed");
        System.out.println("- Light intensity will be used to detect tunnels");
        System.out.println("- Obstacles will be detected within 40 cm");
        System.out.println("\nControls:");
        System.out.println("- Press 'Y' to start the journey");
        System.out.println("- Press 'X' to exit the program");
        System.out.println("\nAfter completion:");
        System.out.println("- Press 'Y' to view detailed journey log");
        System.out.println("- Press 'X' to save log and exit");
        System.out.println("\n************************************");
    }

    private void configureRobotButtons() {
        robot.disableAllButtons();
        setupStartButton();
        setupExitButton();
    }

    private void setupStartButton() {
        robot.enableButton(Button.Y, () -> {
            if (!hasStarted && !hasCompleted) {
                hasStarted = true;
                tunnelDetector.beginDetection();
            } else if (hasCompleted) {
                journeyLogger.displayLog();
            }
        });
    }

    private void setupExitButton() {
        robot.enableButton(Button.X, () -> {
            System.out.println("Shutting down...");
            isOperational = false;
            robotController.stop();
            robot.disableAllButtons();
            System.exit(0);
        });
    }

    // tunnel detection
    private class TunnelDetector {
        private double baselineBrightness = 0;
        private int tunnelsFound = 0;
        private double totalDistanceTraveled = 0;
        private boolean isTunnelActive = false;
        private double tunnelEntryPoint = 0;
        private double tunnelExitPoint = 0;
        private final double[] tunnelMeasurements = new double[Config.TOTAL_TUNNELS];
        private final double[] tunnelBrightnessLevels = new double[Config.TOTAL_TUNNELS];
        private final double[] interTunnelDistances = new double[Config.TOTAL_TUNNELS - 1];
        private double previousTunnelEnd = 0;

        public void beginDetection() {
            System.out.println("\n************************************");
            System.out.println("Starting Tunnel Detection Journey");
            System.out.println("************************************");
            System.out.println("\nInitial Setup:");
            System.out.println("- Setting underlights to RED for normal movement");
            System.out.println("- Robot speed: " + Config.BOT_SPEED_PERCENT + "%");
            System.out.println("- Sampling interval: " + Config.IMAGE_CAPTURE_INTERVAL_MS + "ms");
            
            robotController.setLightsRed();
            long startTimestamp = System.currentTimeMillis();
            calibrateBrightness();

            while (isOperational && tunnelsFound < Config.TOTAL_TUNNELS) {
                if (checkForObstacles()) break;
                processTunnelDetection();
            }

            completeDetection(startTimestamp);
        }

        private void calibrateBrightness() {
            try {
                System.out.println("\nCalibrating Environment:");
                System.out.println("- Taking initial light reading...");
                BufferedImage calibrationImage = robot.takeStill(ImageSize.SQUARE_480x480);
                baselineBrightness = imageProcessor.calculateBrightness(calibrationImage);
                System.out.println("- Baseline brightness level: " + String.format("%.2f", baselineBrightness));
                System.out.println("- Darkness threshold: " + String.format("%.2f", baselineBrightness * (1 - Config.LIGHT_INTENSITY_THRESHOLD)));
                System.out.println("- Waiting " + (Config.CALIBRATION_DELAY_MS/1000) + " second for stabilization...");
                Thread.sleep(Config.CALIBRATION_DELAY_MS);
                System.out.println("- Calibration complete");
                System.out.println("\nBeginning tunnel detection...");
            } catch (Exception e) {
                System.out.println("ERROR: Calibration failed - " + e.getMessage());
            }
        }

        private boolean checkForObstacles() {
            double obstacleDistance = robot.useUltrasound();
            if (obstacleDistance < Config.STOP_DISTANCE_CM) {
                return robotController.handleObstacleDetection(obstacleDistance);
            }
            return false;
        }

        private void processTunnelDetection() {
            try {
                robotController.moveForwardInterval();
                BufferedImage currentImage = robot.takeStill(ImageSize.SQUARE_480x480);
                if (currentImage == null) return;

                double currentBrightness = imageProcessor.calculateBrightness(currentImage);
                processBrightnessChange(currentBrightness, currentImage);
                totalDistanceTraveled += Config.DISTANCE_PER_CYCLE_CM;

            } catch (Exception e) {
                System.out.println("Detection error: " + e.getMessage());
            }
        }

        private void processBrightnessChange(double currentBrightness, BufferedImage image) {
            double darknessThreshold = baselineBrightness * (1 - Config.LIGHT_INTENSITY_THRESHOLD);

            if (!isTunnelActive && currentBrightness < darknessThreshold) {
                handleTunnelEntry();
            } else if (isTunnelActive && currentBrightness > darknessThreshold) {
                handleTunnelExit(image);
            }
        }

        private void handleTunnelEntry() {
            isTunnelActive = true;
            tunnelEntryPoint = totalDistanceTraveled;
            
            System.out.println("\n************************************");
            System.out.println("Tunnel " + (tunnelsFound + 1) + " Detected");
            System.out.println("************************************");
            System.out.println("Entry Details:");
            System.out.println("- Position: " + String.format("%.2f", tunnelEntryPoint) + " cm");
            if (tunnelsFound > 0) {
                System.out.println("- Distance from previous tunnel: " + 
                    String.format("%.2f", (tunnelEntryPoint - previousTunnelEnd)) + " cm");
            }
            System.out.println("- Setting underlights to BLUE");
            
            robotController.setLightsBlue();
        }

        private void handleTunnelExit(BufferedImage image) {
            isTunnelActive = false;
            tunnelExitPoint = totalDistanceTraveled;
            double tunnelLength = tunnelExitPoint - tunnelEntryPoint;
            
            tunnelMeasurements[tunnelsFound] = tunnelLength;
            tunnelBrightnessLevels[tunnelsFound] = imageProcessor.calculateBrightness(image);

            System.out.println("\nTunnel " + (tunnelsFound + 1) + " Exit:");
            System.out.println("- Exit position: " + String.format("%.2f", tunnelExitPoint) + " cm");
            System.out.println("- Tunnel length: " + String.format("%.2f", tunnelLength) + " cm");
            System.out.println("- Average brightness: " + String.format("%.2f", tunnelBrightnessLevels[tunnelsFound]));
            
            if (tunnelsFound > 0) {
                interTunnelDistances[tunnelsFound - 1] = tunnelEntryPoint - previousTunnelEnd;
            }
            previousTunnelEnd = tunnelExitPoint;
            
            tunnelsFound++;
            System.out.println("- Returning to normal movement (RED lights)");
            System.out.println("- Tunnels remaining: " + (Config.TOTAL_TUNNELS - tunnelsFound));
            robotController.setLightsRed();
        }

        private void completeDetection(long startTimestamp) {
            robotController.stop();
            double journeyDuration = (System.currentTimeMillis() - startTimestamp) / 1000.0;
            
            System.out.println("\n************************************");
            System.out.println("Journey Complete");
            System.out.println("************************************");
            System.out.println("Final Statistics:");
            System.out.println("- Total tunnels found: " + tunnelsFound);
            System.out.println("- Total distance: " + String.format("%.2f", totalDistanceTraveled) + " cm");
            System.out.println("- Journey duration: " + String.format("%.2f", journeyDuration) + " seconds");
            System.out.println("- Average speed: " + String.format("%.2f", totalDistanceTraveled/journeyDuration) + " cm/s");
            
            journeyLogger.recordJourney(tunnelsFound, tunnelMeasurements, tunnelBrightnessLevels, 
                                     interTunnelDistances, totalDistanceTraveled, journeyDuration);
            hasCompleted = true;
            
            System.out.println("\nOptions:");
            System.out.println("- Press 'Y' to view detailed journey log");
            System.out.println("- Press 'X' to save log and exit");
        }
    }

    // process images
    private class ImageProcessor {
        public double calculateBrightness(BufferedImage image) {
            if (image == null) return 0;
            
            int width = image.getWidth();
            int height = image.getHeight();
            double totalBrightness = 0;
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    totalBrightness += (red + green + blue) / 3.0;
                }
            }
            
            return totalBrightness / (width * height);
        }
    }

    // robot control
    private class RobotController {
        private final SwiftBotAPI robot;
        private final int[] RED_LIGHT = {255, 0, 0};
        private final int[] BLUE_LIGHT = {0, 0, 255};
        private static final int BLINK_INTERVAL_MS = 500; // 0.5 seconds for blinking

        public RobotController(SwiftBotAPI robot) {
            this.robot = robot;
        }

        public void setLightsRed() {
            try {
                robot.fillUnderlights(RED_LIGHT);
            } catch (Exception e) {
                System.out.println("Failed to set lights: " + e.getMessage());
            }
        }

        public void setLightsBlue() {
            try {
                robot.fillUnderlights(BLUE_LIGHT);
            } catch (Exception e) {
                System.out.println("Failed to set lights: " + e.getMessage());
            }
        }


        public void stop() {
            robot.stopMove();
        }

        public void moveForwardInterval() throws InterruptedException {
            robot.move(Config.BOT_SPEED_PERCENT, Config.BOT_SPEED_PERCENT, 
                      Config.IMAGE_CAPTURE_INTERVAL_MS);
        }

        public boolean handleObstacleDetection(double obstacleDistance) {
            System.out.println("\n************************************");
            System.out.println("OBSTACLE DETECTED!");
            System.out.println("************************************");
            System.out.println("Details:");
            System.out.println("- Distance to obstacle: " + String.format("%.2f", obstacleDistance) + " cm");
            System.out.println("- Blinking RED lights");
            System.out.println("- Waiting 10 seconds to check if obstacle is removed...");
            
            // redlight blinks
            Thread blinkThread = startBlinkingLights();
            
            // Capture obstacle image
            captureObstacleImage();
            
            // Wait 10 seconds
            try {
                for (int i = 10; i > 0; i--) {
                    System.out.println("Time remaining: " + i + " seconds");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Wait interrupted: " + e.getMessage());
            }
            
            // Stop blinking
            blinkThread.interrupt();
            
            // Check if obstacle is still there
            double newDistance = robot.useUltrasound();
            if (newDistance < Config.STOP_DISTANCE_CM) {
                System.out.println("\nObstacle still present. Terminating program.");
                hasCompleted = true;
                return true;
            } else {
                System.out.println("\nObstacle removed. Continuing journey.");
                setLightsRed();
                return false;
            }
        }

        private Thread startBlinkingLights() {
            Thread blinkThread = new Thread(() -> {
                boolean isOn = false;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if (isOn) {
                            robot.disableUnderlights();
                        } else {
                            robot.fillUnderlights(RED_LIGHT);
                        }
                        isOn = !isOn;
                        Thread.sleep(BLINK_INTERVAL_MS);
                    } catch (Exception e) {
                        break;
                    }
                }
                // off lights when done
                try {
                    robot.disableUnderlights();
                } catch (Exception e) {
                    System.out.println("Failed to disable lights: " + e.getMessage());
                }
            });
            blinkThread.start();
            return blinkThread;
        }

        private void captureObstacleImage() {
            try {
                BufferedImage obstacleImage = robot.takeStill(ImageSize.SQUARE_480x480);
                if (obstacleImage != null) {
                    String filename = "obstacle_" + System.currentTimeMillis() + ".png";
                    File imageFile = new File(Config.IMAGE_DIRECTORY, filename);
                    ImageIO.write(obstacleImage, "PNG", imageFile);
                    String absolutePath = imageFile.getAbsolutePath();
                    System.out.println("Obstacle image saved: " + absolutePath);
                    journeyLogger.setObstacleInfo(absolutePath);
                }
            } catch (Exception e) {
                System.out.println("Failed to capture obstacle image: " + e.getMessage());
            }
        }
    }

    // logging
    private class JourneyLogger {
        private boolean obstacleDetected = false;
        private String obstaclePhotoPath = null;

        public void setObstacleInfo(String photoPath) {
            this.obstacleDetected = true;
            this.obstaclePhotoPath = photoPath;
        }

        public void recordJourney(int tunnelCount, double[] lengths, double[] brightness,
                                double[] gaps, double totalDistance, double duration) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(Config.LOG_FILE_NAME))) {
                writeHeader(writer);
                writeTunnelDetails(writer, tunnelCount, lengths, brightness, gaps);
                writeDistanceAnalysis(writer, tunnelCount, lengths, gaps);
                writeObstacleInfo(writer);
                writeSummary(writer, totalDistance, duration);
                System.out.println("Journey log saved to: " + Config.LOG_FILE_NAME);
            } catch (IOException e) {
                System.out.println("Failed to save journey log: " + e.getMessage());
            }
        }

        private void writeHeader(BufferedWriter writer) throws IOException {
            writer.write("************************************\n");
            writer.write("Tunnel Vision Journey Log\n");
            writer.write("************************************\n\n");
        }

        private void writeObstacleInfo(BufferedWriter writer) throws IOException {
            writer.write("Obstacle Information:\n");
            writer.write("-------------------\n");
            writer.write("Obstacle detected: " + (obstacleDetected ? "Yes" : "No") + "\n");
            if (obstacleDetected && obstaclePhotoPath != null) {
                writer.write("Obstacle photo saved at: " + obstaclePhotoPath + "\n");
            }
            writer.write("\n");
        }

        private void writeTunnelDetails(BufferedWriter writer, int count, double[] lengths,
                                      double[] brightness, double[] gaps) throws IOException {
            writer.write("Tunnel Details:\n");
            writer.write("--------------\n");
            writer.write("Total tunnels found: " + count + "\n\n");

            double position = 0;
            for (int i = 0; i < count; i++) {
                writer.write("Tunnel " + (i + 1) + ":\n");
                writer.write(String.format("  Length: %.2f cm\n", lengths[i]));
                writer.write(String.format("  Entry position: %.2f cm\n", position));
                position += lengths[i];
                writer.write(String.format("  Exit position: %.2f cm\n", position));
                writer.write(String.format("  Average brightness: %.2f\n", brightness[i]));
                
                if (i < count - 1) {
                    writer.write(String.format("  Distance to next tunnel: %.2f cm\n", gaps[i]));
                    position += gaps[i];
                }
                writer.write("\n");
            }
        }

        private void writeDistanceAnalysis(BufferedWriter writer, int count,
                                         double[] lengths, double[] gaps) throws IOException {
            writer.write("Distance Analysis:\n");
            writer.write("-----------------\n");
            
            double totalTunnelLength = 0;
            for (int i = 0; i < count; i++) {
                totalTunnelLength += lengths[i];
            }
            writer.write(String.format("Total tunnel length: %.2f cm\n", totalTunnelLength));

            double totalGapDistance = 0;
            for (int i = 0; i < count - 1; i++) {
                totalGapDistance += gaps[i];
            }
            writer.write(String.format("Total gap distance: %.2f cm\n\n", totalGapDistance));
        }

        private void writeSummary(BufferedWriter writer, double totalDistance,
                                double duration) throws IOException {
            writer.write("Journey Summary:\n");
            writer.write("---------------\n");
            writer.write(String.format("Total distance: %.2f cm\n", totalDistance));
            writer.write(String.format("Duration: %.2f seconds\n", duration));
            writer.write(String.format("Average speed: %.2f cm/s\n", totalDistance/duration));
            writer.write("Log location: " + Config.LOG_FILE_NAME + "\n");
        }

        public void displayLog() {
            System.out.println("\n************************************");
            System.out.println("Journey Log");
            System.out.println("************************************");
            try {
                java.nio.file.Files.readAllLines(java.nio.file.Paths.get(Config.LOG_FILE_NAME))
                    .forEach(System.out::println);
                System.out.println("\nLog file location: " + Config.LOG_FILE_NAME);
            } catch (IOException e) {
                System.out.println("ERROR: Could not read log file - " + e.getMessage());
            }
            System.out.println("\nPress 'X' to exit program.");
        }
    }
} 