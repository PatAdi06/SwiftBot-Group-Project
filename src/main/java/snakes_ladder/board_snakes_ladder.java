package snakes_ladder;

import java.util.Random;

public class board_snakes_ladder {
	
    public static int[][] board_game = new int[5][5];
    public static int[][] snakes = new int[2][2], ladders = new int[2][2];
    public static Random rand = new Random();

    // Initialise the board values
    public static void assign_array_board() {
        int value_cell = 1;

        for (int i = 4; i >= 0; i--) {
            if (i % 2 == 0) {
                for (int j = 0; j < 5; j++) {
                    board_game[i][j] = value_cell++;
                }
            } else {
                for (int j = 4; j >= 0; j--) {
                    board_game[i][j] = value_cell++;
                }
            }
        }
    }

    // Function to check if a snake/ladder conflicts with existing ones
    public static boolean checking_ladder_snake(int a, int b) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (a == snakes[i][j] || a == ladders[i][j] || 
                    b == snakes[i][j] || b == ladders[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    

    // Create Snakes on the board
    public static void snakes_positions() {
        int index = 0, i = 0, j = 0;
        int tail = 0, head = 0;

        while (index < 2) {
            boolean valid = false;

            while (!valid) {
                i = rand.nextInt(4); // Random between 0-3
                if (i == 0) {
                    j = rand.nextInt(4); // j cannot be 4 as it will land on tile 25 (Finish)
                } else {
                    j = rand.nextInt(5);
                }

                head = board_game[i][j];

                i = rand.nextInt(4 - i) + i + 1; // Tail must be below head
                if (i == 4) {
                    j = rand.nextInt(4) + 1; // Avoid tail to land on tile 1 (which is Start)
                } else {
                    j = rand.nextInt(5);
                }

                tail = board_game[i][j];

                if (checking_ladder_snake(head, tail)) {
                    snakes[index][0] = head;
                    snakes[index][1] = tail;
                    valid = true;
                }
            }
            index++;
        }
        System.out.println("<--------- Snake 1 ---------->");
        System.out.print("Head: " + snakes[0][0]);
        System.out.println(" \tTail: " + snakes[0][1]);
        System.out.println("<--------- Snake 2 ---------->");
        System.out.print("Head: " + snakes[1][0]);
        System.out.println(" \tTail: " + snakes[1][1]);
        System.out.println("");
    }

    // Create Ladders on the board
    public static void ladders_positions() {
        int index = 0, i = 0, j = 0;


        while (index < 2) {
            boolean valid = false;
            int top = 0, bottom = 0;

            while (!valid) {
            	i=rand.nextInt(4) + 1; //random between 1-4

    	        if (i==4) {

    	        	j=rand.nextInt(4)+1; //j cannot be 0, as it will land on Start tile therefore 1-4

    	        } else j = rand.nextInt(5);
    	        
                bottom = board_game[i][j];

                i=rand.nextInt(i); //top from the next row until the end

    	        if (i==0) {

    	        	j=rand.nextInt(4); //j cannot be 0, as it will land on Start tile therefore 1-4

    	        } else j = rand.nextInt(5);
    	        
                top = board_game[i][j];

                if (checking_ladder_snake(bottom, top)) {
                    ladders[index][0] = bottom;
                    ladders[index][1] = top;
                    valid = true;
                }
            }
            index++;
        }
        System.out.println("<--------- Ladder 1 ---------->");
        System.out.print("Bottom: " + ladders[0][0]);
        System.out.println(" \tTop: " + ladders[0][1]);
        System.out.println("<--------- Ladder 2 ---------->");
        System.out.print("Bottom: " + ladders[1][0]);
        System.out.println(" \tTop: " + ladders[1][1]);
        System.out.println("");
    }
    
    public static int check_for_snake_or_ladder(int check) {
    	
    	if(check == snakes[0][0]) {
    		System.out.println();
    		System.out.print("OMG Snake!!!");
    		if(check == main_program.player_score) System.out.print(" The PLAYER moved from " + check + " to ");
    		else if(check == main_program.robot_score) {
    			System.out.print(" The ROBOT moved from " + check + " to ");
        		check = snakes[0][1];
        		System.out.println(check);
        		System.out.println();
    			main_program.moveBackward(snakes[0][0]-snakes[0][1]);
    			}
    		check = snakes[0][1];
    		System.out.println(check);
    		System.out.println();

    		}
    	else if(check == snakes[1][0]) {
    		System.out.println();
    		System.out.print("OMG Snake!!!");
    		if(check == main_program.player_score) System.out.print(" The PLAYER moved from " + check + " to ");
    		else if(check == main_program.robot_score) {
    			System.out.print(" The ROBOT moved from " + check + " to ");
        		check = snakes[1][1];
        		System.out.println(check);
        		System.out.println();
    			main_program.moveBackward(snakes[1][0]-snakes[1][1]);
    			}
    		check = snakes[1][1];
    		System.out.println(check);
    		System.out.println();
    	}
    	else if(check == ladders[0][0]) {
    		System.out.println();
    		System.out.print("All the way to the top!!! ");
    		if(check == main_program.player_score) System.out.print(" The PLAYER moved from " + check + " to ");
    		else if(check == main_program.robot_score) {
    			System.out.print(" The ROBOT moved from " + check + " to ");
        		check = ladders[0][1];
        		System.out.println(check);
        		System.out.println();
    			main_program.moveForward(ladders[0][1]-ladders[0][0]);
    			}
    		check = ladders[0][1];
    		System.out.println(check);
    		System.out.println();
    	}
    	else if(check == ladders[1][0]) {
    		System.out.println();
    		System.out.print("All the way to the top!!! ");
    		if(check == main_program.player_score) System.out.print(" The PLAYER moved from " + check + " to ");
    		else if(check == main_program.robot_score) {
    			System.out.print(" The ROBOT moved from " + check + " to ");
        		check = ladders[0][1];
        		System.out.println(check);
        		System.out.println();
    			main_program.moveForward(ladders[1][1]-ladders[1][0]);
    			}
    		check = ladders[1][1];
    		System.out.println(check);
    		System.out.println();
    	}

    	
    	
    	return check;
    	
    }
}


