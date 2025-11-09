package search_light;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import swiftbot.Button;
import swiftbot.ButtonFunction;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

public class SearchforLight {
    static SwiftBotAPI API = new SwiftBotAPI();
    private double lightThreshold;
    private double obstacleThreshold = 50.0;
    private int[] lastIntensities;
    private static final Underlight[] ALL_LIGHTS = {
        Underlight.FRONT_LEFT, Underlight.FRONT_RIGHT,
        Underlight.MIDDLE_LEFT, Underlight.MIDDLE_RIGHT,
        Underlight.BACK_LEFT, Underlight.BACK_RIGHT
    };
    private static Random rand = new Random();
    
    private void displayWelcomeMessage() {
        System.out.println("\n************************************");
        System.out.println("SWIFTBOT");
        System.out.println("************************************");
        System.out.println("\nWelcome to search for light!");
        System.out.println("\nControls:");
        System.out.println("- Press 'A' to start the journey");
        System.out.println("- Press 'X' to exit the program");
        System.out.println("\nAfter completion:");
        System.out.println("- Press 'Y' to view detailed journey log");
        System.out.println("- Press 'X' to save log and exit");
        System.out.println("\n************************************");
    }

    // Log tracking
    private ArrayList<String> movements = new ArrayList<>();
    private ArrayList<int[]> lightDetections = new ArrayList<>();
    private int obstacleCount = 0;
    private double totalDistance = 0.0;
    private int highestIntensity = 0;
    private long startTime;
    // Button control flags
    private volatile boolean isRunning = false;
    private volatile boolean stopRequested = false;
    private volatile boolean showLog = false;
    private volatile boolean logDecisionMade = false;

    public static void main(String[] args) {
        SearchforLight bot = new SearchforLight();
        bot.displayWelcomeMessage(); // Call the welcome message
        bot.setupButtons();
        bot.waitForStart();
        
        while (!bot.stopRequested) {
            BufferedImage image = captureImage();
            if (image != null) {
                int[] intensity = calculateLightIntensity(image);
                int leftSection = intensity[0];
                int centreSection = intensity[1];
                int rightSection = intensity[2];
                bot.lastIntensities = intensity;
                bot.lightDetections.add(intensity);
                bot.highestIntensity = Math.max(bot.highestIntensity, Math.max(leftSection, Math.max(centreSection, rightSection)));
                
                //Display Light intensity values to the user
                System.out.println("Light Intensities: Left = " + leftSection +
                                   ", Centre = " + centreSection +
                                   ", Right = " + rightSection);
                
                int direction = determineDirection(leftSection, centreSection, rightSection);
                //check obstacles before moving
                direction = bot.CheckForObstacles(direction);
                
                //set underlights to green before moving forward
                int[] green = {0, 255, 0};
                for (Underlight light : ALL_LIGHTS) {
                    try {
                        API.setUnderlight(light, green);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Light " + light + " error: " + e.getMessage());
                    }
                }
                System.out.println("Lights green—ready to move forwards");

                //move based on directions
                double distanceMoved = 0.0;
                if (direction == 1) {
                    System.out.println("turning right");
                    API.startMove(40, 0);
                    distanceMoved = 10.0;
                    bot.movements.add("Right 10 cm");
                } else if (direction == 0) {
                    System.out.println("moving forward");
                    API.startMove(100, 100);
                    distanceMoved = 15.0;
                    bot.movements.add("Straight ahead 15 cm");
                } else if (direction == -1) {
                    System.out.println("turning left");
                    API.startMove(0, 40);
                    distanceMoved = 10.0;
                    bot.movements.add("Left 10 cm");
                }
                bot.totalDistance += distanceMoved;
                try {
                    Thread.sleep(100); //Move for 100ms
                } catch (InterruptedException e) {
                    System.out.println("Program interrupted: " + e.getMessage());
                    return;
                }
                API.stopMove(); //stop
            }
        }
        bot.handleExitPrompt(); // Handle log decision after loop ends
    }

    private void setupButtons() {
        // Button A to start
        API.enableButton(Button.A, new ButtonFunction() {
            @Override
            public void run() {
                if (!isRunning) {
                    System.out.println("A pressed—let’s go!");
                    isRunning = true;
                    startTime = System.currentTimeMillis();
                    setLightThreshold();
                }
            }
        });

        // Button X to stop and handle log decision
        API.enableButton(Button.X, new ButtonFunction() {
            @Override
            public void run() {
                if (isRunning && !stopRequested) {
                    System.out.println("X pressed—time to wrap up! Show the log? Press ‘Y’ for yes, ‘X’ for no.");
                    stopRequested = true;
                    showLog = false;
                    logDecisionMade = false;
                } else if (stopRequested && !logDecisionMade) {
                    // Second X press means "no"
                    showLog = false;
                    logDecisionMade = true;
                }
            }
        });

        // Button Y for log yes
        API.enableButton(Button.Y, new ButtonFunction() {
            @Override
            public void run() {
                if (stopRequested && !logDecisionMade) {
                    showLog = true;
                    logDecisionMade = true;
                }
            }
        });
    }

    private void waitForStart() {
    	System.out.println("press 'A' to start");
        while (!isRunning) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }

    private void handleExitPrompt() {
        // Wait for Y or X decision
        while (stopRequested && !logDecisionMade) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        finishExit();
    }

    private void finishExit() {
        String log = buildLog();
        if (showLog) {
            System.out.println("\n=== Execution Log ===");
            System.out.println(log);
        }
        saveLog(log);
        System.out.println("Peace out!");
        System.exit(0);
    }

    

    private void setLightThreshold() {
        BufferedImage image = captureImage();
        if (image != null) {
            int[] intensities = calculateLightIntensity(image); //get the light intensities froom the image
            lightThreshold = (intensities[0] + intensities[1] + intensities[2]) / 3.0;
            System.out.println("brightest light level: " + lightThreshold);
        } else {
            lightThreshold = 50; //in case of no pictures go with a default threshold
            System.out.println("No image—guessing threshold at " + lightThreshold);
        }
    }

    private static BufferedImage captureImage() {
        try {
            return API.takeStill(ImageSize.SQUARE_144x144);
        } catch (Exception e) {
            System.out.println("Error capturing image.");
            e.printStackTrace();
            return null;
        }
    }

    private static int[] calculateLightIntensity(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int[][] pixelMatrix = convertToMatrix(image, width, height);

        //split the image into 3 sections
        int leftSection = calculateSectionLightIntensity(pixelMatrix, 0, width / 3, height);
        int centreSection = calculateSectionLightIntensity(pixelMatrix, width / 3, (2 * width) / 3, height);
        int rightSection = calculateSectionLightIntensity(pixelMatrix, (2 * width) / 3, width, height);

        //strore the results in an array
        return new int[]{leftSection, centreSection, rightSection};
    }
    
    //determine the direction based on intensities
    private static int determineDirection(int left, int centre, int right) {
        if (right > centre && right > left) {
            return 1;
        } else if (centre > left && centre > right) {
            return 0;
        } else if (left > centre && left > right) {
            return -1;
        } else {
            return rand.nextBoolean() ? 1 : -1; //in case of a tie randomly choose a direction
        }
    }
    
    private static int calculateSectionLightIntensity(int[][] pixelMatrix, int startX, int endX, int height) {
    	//Set up to sum the light and count pixels 
        long sum = 0;
        int pixelCount = 0;
        for (int x = startX; x < endX; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = pixelMatrix[y][x];
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                sum += (r + g + b) / 3; //Average RGB to get brightness
                pixelCount++;
            }
        }
        return pixelCount > 0 ? (int) (sum / pixelCount) : 0;//average the sum over pixels if there's any, otherwise 0
    }

    private static int[][] convertToMatrix(BufferedImage img, int width, int height) {
        int[][] matrix = new int[height][width];
        //loop through every pixel in the image
        for (int y = 0; y < height; y++) { 
            for (int x = 0; x < width; x++) {
                matrix[y][x] = img.getRGB(x, y);
            }
        }
        return matrix;
    }

    private int CheckForObstacles(int direction) {
        double distance = API.useUltrasound();//check how far the obstacle is using ultrasound
        System.out.println("obstacle ahead " + distance + "cm away");//get distance from the obstacle
        
        if (distance >= obstacleThreshold || distance <= 0) { //compare the obstacle distance to the obstacleThreshold set
            System.out.println("Path’s clear or ultrasound’s off—let’s go!");
            return direction;
        }
        
        //if obstacle within the threshold blink underlights in red
        int[] red = {255, 0, 0}; //full red
        int[] off = {0, 0, 0}; //lights off
        for (int i = 0; i < 5; i++) { //loop to blink lights 5 times
        	//set all underlights in red
            for (Underlight light : ALL_LIGHTS) {
                try {
                    API.setUnderlight(light, red);
                } catch (Exception e) {
                    System.out.println("Light " + light + " blinked out: " + e.getMessage());//catch error if the light malfunctions
                }
            }
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            //turn off all lights
            for (Underlight light : ALL_LIGHTS) {
                try {
                    API.setUnderlight(light, off);
                } catch (Exception e) {}
            }
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }
        //take a pic of the obstacle
        BufferedImage obstacle = captureImage();
        if (obstacle != null) {
            try {
                String filename = "obstacle_" + System.currentTimeMillis() + ".png";//name the pic with timestamp 
                ImageIO.write(obstacle, "png", new File(filename));
                System.out.println("saved the image of the obstacle as " + filename);
                movements.add("Obstacle detected - image saved as " + filename);
                obstacleCount++;
            } catch (IOException e) {
                System.out.println("Pic didn’t save " + e.getMessage());
            }
        }
        //countdown till 10 while waiting for the obstacle to be removed
        for (int i = 10; i > 0; i--) {
            System.out.println("Waiting for the object to be removed");
            try {
                Thread.sleep(1000);//pause for a second each loop
            } catch (InterruptedException e) {}
        }
        //check again to see if obstacleis removed
        double newdistance = API.useUltrasound(); //get new distance
        if (newdistance < obstacleThreshold && newdistance > 0) {
            System.out.println("Still blocked");
            return pickSecondBest(direction);
        } else {
            System.out.println("Path’s clear");
            return direction;
        }
    }

    //second best direction to move into in case of the obstacle not being removed  
    private int pickSecondBest(int direction) {
    	//get the light intensities
        int left = lastIntensities[0];
        int centre = lastIntensities[1];
        int right = lastIntensities[2];
        
        //figureout the brightest section
        int maxDirection = determineDirection(left, centre, right);
        //check if original direction isn't being blocked
        if (maxDirection != direction) {
            System.out.println("highest light intensity isn’t blocked"); //movein the brightest direction
            return maxDirection;
        }
        //map intensities to directions
        int[] intensities = {left, centre, right};
        int[] directions = {-1, 0, 1};
        //find the max brightness starting with left
        int maxIndex = 0;
        for (int i = 1; i < 3; i++) {
        	//check if right or centre is brighter
            if (intensities[i] > intensities[maxIndex]) maxIndex = i;
        }
        //find second best
        int secondMaxIndex = (maxIndex == 0) ? 1 : 0;
        for (int i = 0; i < 3; i++) {
        	//look for the brightest skipping max intensity
            if (i != maxIndex && intensities[i] > intensities[secondMaxIndex]) {
                secondMaxIndex = i;
            }
        }
        //find a third option
        int otherIndex = 3 - maxIndex - secondMaxIndex;
        if (intensities[secondMaxIndex] == intensities[otherIndex] && directions[otherIndex] != direction) {
        	//tie between second and third
            System.out.println("Tie");
            return rand.nextBoolean() ? directions[secondMaxIndex] : directions[otherIndex];//choose a direction randomly in case of a tie
        }
        //return second best if thats not blocked direction
        if (directions[secondMaxIndex] != direction) {
            String dirText = directions[secondMaxIndex] == 1 ? "right" : 
                            directions[secondMaxIndex] == 0 ? "forward" : "left";
            System.out.println("Dodging to second best: " + dirText + "!");
            return directions[secondMaxIndex];
        } else {
        	//if second best is blocked too go with the last option
            System.out.println("Second highest intensity is blocked");
            return directions[otherIndex]; //return the direction
        }
    }
    
    private String buildLog() {
        long duration = (System.currentTimeMillis() - startTime) / 1000; //calculate how long the bot's been running
        StringBuilder log = new StringBuilder(); //string builder to piece together the log
        log.append("Light Threshold at Start: ").append(lightThreshold).append("\n");
        log.append("Brightest Light Detected: ").append(highestIntensity).append("\n");
        log.append("Light Detections: ").append(lightDetections.size()).append("\n");
        for (int i = 0; i < lightDetections.size(); i++) {
            int[] intensities = lightDetections.get(i);
            log.append("  Detection ").append(i + 1).append(": Left=").append(intensities[0])
               .append(", Centre=").append(intensities[1]).append(", Right=").append(intensities[2]).append("\n");
        }
        log.append("Execution Duration: ").append(duration).append(" seconds\n"); //time taken for the whole process
        log.append("Total Distance Travelled: ").append(totalDistance).append(" cm\n"); //total distance the bot travelled to search for light
        log.append("Movements:\n");
        
        //loop through each detection to gather info
        for (String move : movements) {
            log.append("  ").append(move).append("\n");
        } 
        
        log.append("Objects Detected: ").append(obstacleCount).append("\n"); // count of obstacles the bot ran into
        log.append("Obstacle Images Saved In: Current directory (obstacle_*.png)\n");
        return log.toString();
    }
    //file the logs to keep safe 
    private void saveLog(String log) {
        try (FileWriter writer = new FileWriter("swiftbot_log.txt")) {
            writer.write(log);
            System.out.println("Log saved to swiftbot_log.txt—catch ya later!");
        } catch (IOException e) {
            System.out.println("Couldn’t save log—sad vibes! " + e.getMessage());
        }
    }
}