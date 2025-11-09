package zig_zag;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import swiftbot.*;

public class ZigzagLengthAndSection {

    private SwiftBotAPI API;
    private Scanner s = new Scanner(System.in);
    private Random random = new Random();
    private Stack<Integer[]> movementStack = new Stack<>(); 

    public ZigzagLengthAndSection(SwiftBotAPI API) {
        this.API = API;
        System.out.println("\nTest\n");
    }

    public void runZigZag() {
        System.out.println("Please enter the first length of the zigzag (15cm - 85cm): ");
        int value1 = s.nextInt();
        if (value1 < 15 || value1 > 85) {
            System.out.println("Invalid length. Exiting...");
            System.exit(0);
        }

        System.out.println("Please enter the second length of the zigzag (15cm - 85cm): ");
        int value2 = s.nextInt();
        if (value2 < 15 || value2 > 85) {
            System.out.println("Invalid length. Exiting...");
            System.exit(0);
        }

        System.out.println("Please enter how many sections (even number, max 12): ");
        int sections = s.nextInt();
        if (sections > 12 || sections % 2 != 0 || sections <= 0) {
            System.out.println("Invalid sections. Exiting...");
            System.exit(0);
        }

        System.out.println("Length 1: " + value1 + "cm, Length 2: " + value2 + "cm, Sections: " + sections);

        int sectionsCompleted = 0;
        while (sectionsCompleted < sections) {
            try {
                int speed = 80 + random.nextInt(20);
                long moveTime1 = calculateMoveTime(value1, speed);
                long moveTime2 = calculateMoveTime(value2, speed);
                
                if (sectionsCompleted % 2 == 0) {
                	//this sets colour green
                	int[] colourToLightUp = {0,255,0};
                	try {
                	API.setUnderlight(Underlight.FRONT_LEFT,colourToLightUp);
                	} catch (IllegalArgumentException e) {
                	((Throwable) e).printStackTrace();
                	}
                	
                } else {
                	int[] colourToLightUp = {0,0,255};
                	try {
                	API.setUnderlight(Underlight.FRONT_LEFT,colourToLightUp);
                	} catch (IllegalArgumentException e) {
                	((Throwable) e).printStackTrace();
                	} 
                }

                // Move forward for first length
                System.out.println("Moving first length: " + value1 + "cm at speed " + speed);
                API.move(speed, speed, (int) moveTime1);
                API.stopMove();
                Thread.sleep(500);

                // Store movement in stack for retrace
                movementStack.push(new Integer[]{speed, speed, (int) moveTime1});
                
                sectionsCompleted++;
                if (sectionsCompleted >= sections) break;

                
                System.out.println("Adjusting turn before second length...");
                API.move(speed / 2, -speed / 2, 500);
                API.stopMove();
                Thread.sleep(200);
                
                
                if (sectionsCompleted % 2 == 0) {
                	//this sets colour green
                	int[] colourToLightUp = {0,255,0};
                	try {
                	API.setUnderlight(Underlight.FRONT_LEFT,colourToLightUp);
                	} catch (IllegalArgumentException e) {
                	((Throwable) e).printStackTrace();
                	}
                	
                } else {
                	int[] colourToLightUp = {0,0,255};
                	try {
                	API.setUnderlight(Underlight.FRONT_LEFT,colourToLightUp);
                	} catch (IllegalArgumentException e) {
                	((Throwable) e).printStackTrace();
                	} 
                }
                

                // Move in the opposite direction for the second length
                System.out.println("Moving second length: " + value2 + "cm at speed " + speed);
                API.move(speed, speed, (int) moveTime2);
                API.stopMove();
                Thread.sleep(500);

                // Store movement in stack for retrace
                movementStack.push(new Integer[]{speed, speed, (int) moveTime2});
                
                sectionsCompleted++;
                System.out.println("Completed section: " + sectionsCompleted);

            } catch (IllegalArgumentException | InterruptedException e) {
                e.printStackTrace();
            }
        }

       
        retracePath();
        
        System.out.println();
    }

    // Retrace function to move back to starting position
    private void retracePath() {
        System.out.println("Retracing path back to starting position...");
        while (!movementStack.isEmpty()) {
            try {
                Integer[] lastMove = movementStack.pop(); // Get last movement
                System.out.println("Retracing movement: Speed " + -lastMove[0] + " for " + lastMove[2] + "ms");
                API.move(-lastMove[0], -lastMove[1], lastMove[2]); // Move backward
                API.stopMove();
                Thread.sleep(500); 
                
            } catch (IllegalArgumentException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Returned to the starting position!");
    }

    
    private long calculateMoveTime(double distance, double speed) {
        double scaleFactor = 15.0; 
        return (long) (distance / speed * 1000 * scaleFactor);
    }
}
