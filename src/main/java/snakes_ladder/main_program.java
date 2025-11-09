package snakes_ladder;
import swiftbot.*;

public class main_program {
	public static SwiftBotAPI swiftBot;
    public static int player_score = 0, robot_score = 0;
    public static String player_name;

    public static int generating_dice() {
    	int dice_value = board_snakes_ladder.rand.nextInt(6) + 1;
    	
        return dice_value;
        
    }
    
    public static void two_second_timer() {
    	try {
    	    Thread.sleep(2000); // 2000 milliseconds = 2 seconds
    	} catch (InterruptedException e) {
    	    e.printStackTrace(); // Handle exception if needed
    	}
    }

    public static void moveForward(int diceValue) {
        int velocity = 100; // Maximum speed
        int duration = 825; // Time to move one tile
        
        try {
            for (int i = 0; i < diceValue; i++) {
                if(robot_score % 5==0)  swiftBot.move(velocity, velocity, duration-450);
                else swiftBot.move(velocity, velocity, duration);
                robot_score++;
                if((robot_score % 10 ==0 || (robot_score-1)%10==0) && robot_score-1!=0) turnRight();
                else if((robot_score % 5==0 || (robot_score-1) %5==0) && robot_score-1!=0)turnLeft();
            }
            
        	robot_score = board_snakes_ladder.check_for_snake_or_ladder(robot_score);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while moving forward");
            System.exit(5);
        }
    }

    public static void moveBackward(int diceValue) {
        int velocity = -100; // Maximum speed
        int duration = 750; // Time to move one tile
        
        try {
            for (int i = 0; i < diceValue; i++) {
                if(robot_score % 5==0)  swiftBot.move(velocity, velocity, duration-450);
                else swiftBot.move(velocity, velocity, duration);
                robot_score--;
                if((robot_score % 10 ==0 || (robot_score - 1) % 10==0) && robot_score-1!=0) back_turnRight();
                else if((robot_score % 5==0 || (robot_score-1) %5==0) && robot_score-1!=0) back_turnLeft();
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while moving forward");
            System.exit(5);
        }
    }

    public static void back_turnLeft() {
        try {
            swiftBot.move(0, -100, 810); // Left wheel stops, right wheel moves
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while turning left");
        }
    }

    public static void back_turnRight() {
        try {
            swiftBot.move(-100, 0, 850); // Right wheel stops, left wheel moves
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while turning right");
        }
    }

    public static void turnLeft() {
        try {
            swiftBot.move(0, 100, 810); // Left wheel stops, right wheel moves
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while turning left");
        }
    }

    public static void turnRight() {
        try {
            swiftBot.move(100, 0, 850); // Right wheel stops, left wheel moves
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while turning right");
        }
    }

    
    public static void robot_move(int dice_value) {
    	
    	System.out.println();
    	System.out.println("Robot's Turn ->"); 
    	two_second_timer();
    	System.out.println("The dice is rolling ....");
    	two_second_timer();
    	System.out.println("The dice value: " + dice_value);
    	two_second_timer();
    	System.out.print("Robot has moved from tile " + robot_score +" to ");
    	System.out.println(robot_score+dice_value);
    	
    	
    	if(robot_score + dice_value==25) {
    		robot_score += dice_value;
        	two_second_timer();
        	moveForward(dice_value);

    		checking_tests.winning_screen("Robot");

    	} else if(robot_score + dice_value>25){
        	two_second_timer();
    		System.out.println("The tile is out of board. Skip move! ");
    		player_move(generating_dice());

    	} 
    	
    	moveForward(dice_value);
    	player_move(generating_dice());
    	
        // Define robot movement logic here
    }

    
    public static void player_move(int dice_value) {
    	two_second_timer();


    	System.out.println();
    	System.out.println(player_name + "'s Turn ->");
        System.out.println("Button A - Roll Dice");
        System.out.println("Button X - Exit Game");
        

    	int checking_button = checking_tests.check_button_pressed();

        if(checking_button==2) {
        	System.out.println("The dice is rolling ....");
        	two_second_timer();

            System.out.println("The dice value: " + dice_value);
        	two_second_timer();

            System.out.print(player_name + " has moved from tile " + player_score + " to ");
            player_score += dice_value;
            System.out.println(player_score);

            if (player_score == 25) {
            	two_second_timer();

        		checking_tests.winning_screen(player_name);

            } else if (player_score > 25) {
            	two_second_timer();

                System.out.println("The tile is out of board. Skip move!");
                player_score -= dice_value;
                robot_move(generating_dice());
            } else {
                player_score = board_snakes_ladder.check_for_snake_or_ladder(player_score);
                robot_move(generating_dice());
            }
        } else if(checking_button==3) {
        	checking_tests.saveGameResult();
        	System.exit(2);
        } else {
        	System.out.println("Wrong Button Pressed! Try Again!");
        	player_move(dice_value);
        }
    
    }


    
    
    public static void startGame() {
    	
    	two_second_timer();

        board_snakes_ladder.assign_array_board();
        board_snakes_ladder.snakes_positions();
        board_snakes_ladder.ladders_positions();
        
    	two_second_timer();

        // Determine who starts first
        
    	System.out.println();
    	System.out.println("Press Button 'Y' to start the game!");
        

    	int checking_button = checking_tests.check_button_pressed();

        if(checking_button==1) {
            while(player_score==robot_score) {
            	player_score = board_snakes_ladder.rand.nextInt(6) + 1;
            	robot_score = board_snakes_ladder.rand.nextInt(6) + 1;
            	System.out.println();
                System.out.println(player_name + " Rolled: " + player_score);
            	two_second_timer();

                System.out.println("Robot Rolled: " + robot_score);
            	
            	two_second_timer();

            	if(player_score == robot_score) {
            		System.out.println("The values are similar. The dice will be thrown again!");
                    System.out.println("");
                	two_second_timer();

            	} else if(player_score>robot_score) {
            		System.out.println(player_name + "'s value is bigger. He will start FIRST!");
                    System.out.println("");
                	two_second_timer();

            		player_score=1;
            		robot_score=1;
            		player_move(generating_dice());

            	} else {
            		
            		System.out.println("Robot's value is bigger. It will start FIRST!");
                    System.out.println("");
                	two_second_timer();

            		player_score=1;
            		robot_score=1;
            		robot_move(generating_dice());
            }}} else {
        	System.out.println("Wrong Button Pressed! Try Again!");
        	startGame();
        }
    	
    	


    } 
    
    
    
    
    public static void main(String[] args) {
        // Initialise the board
    	
		try {
			swiftBot = new SwiftBotAPI();
		} catch (Exception e) {
			/*
			 * Outputs a warning if I2C is disabled. This only needs to be turned on once,
			 * so you won't need to worry about this problem again!
			 */
			System.out.println("\nI2C disabled!");
			System.out.println("Run the following command:");
			System.out.println("sudo raspi-config nonint do_i2c 0\n");
			System.exit(5);
		}
		
		System.out.println("");
		System.out.println("..######..##....##....###....##....##.########..######........###....##....##.########.....##..........###....########..########..########.########...######.");
		System.out.println(".##....##.###...##...##.##...##...##..##.......##....##......##.##...###...##.##.....##....##.........##.##...##.....##.##.....##.##.......##.....##.##....##");
		System.out.println(".##.......####..##..##...##..##..##...##.......##...........##...##..####..##.##.....##....##........##...##..##.....##.##.....##.##.......##.....##.##......");
		System.out.println("..######..##.##.##.##.....##.#####....######....######.....##.....##.##.##.##.##.....##....##.......##.....##.##.....##.##.....##.######...########...######.");
		System.out.println(".......##.##..####.#########.##..##...##.............##....#########.##..####.##.....##....##.......#########.##.....##.##.....##.##.......##...##.........##");
		System.out.println(".##....##.##...###.##.....##.##...##..##.......##....##....##.....##.##...###.##.....##....##.......##.....##.##.....##.##.....##.##.......##....##..##....##");
		System.out.println("..######..##....##.##.....##.##....##.########..######.....##.....##.##....##.########.....########.##.....##.########..########..########.##.....##..######.");
		System.out.println("");
		
		qr_code.testQRCodeDetection();
		

 
    }
}
