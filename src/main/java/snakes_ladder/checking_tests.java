package snakes_ladder;
import swiftbot.Button;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
public class checking_tests {
	
    private static int buttonPressResult;  // This will hold the result of the button press

	 public static int check_button_pressed() {
			buttonPressResult = -1;
	        // Enable button B
	        main_program.swiftBot.enableButton(Button.B, () -> {
	            buttonPressResult = 0;  // Set result when button B is pressed
	            
	        });

	        // Enable button A
	        main_program.swiftBot.enableButton(Button.Y, () -> {
	            buttonPressResult = 1;  // Set result when button Y is pressed
	        });

	        // Enable button X
	        main_program.swiftBot.enableButton(Button.A, () -> {
	            buttonPressResult = 2;  // Set result when button A is pressed
	        });

	        // Enable button Y
	        main_program.swiftBot.enableButton(Button.X, () -> {
	            buttonPressResult = 3;  // Set result when button X is pressed
	        });
	        while (buttonPressResult<0) {
	            try {
	                Thread.sleep(10);  // Sleep briefly to avoid busy-waiting (consume too much CPU)
	            } catch (InterruptedException e) {
	                e.printStackTrace();  // Handle interruptions if needed
	            }
	        }
	        main_program.swiftBot.disableAllButtons();
			
			
	        return buttonPressResult;  // Return the result of the button press
	    }
	 
	 public static void winning_screen(String winner_name) {
		 
		System.out.println("");
		System.out.println(":::       ::: ::::::::::: ::::    ::: ::::    ::: :::::::::: :::::::::  ");
		System.out.println(":+:       :+:     :+:     :+:+:   :+: :+:+:   :+: :+:        :+:    :+: ");
		System.out.println(" +:+       +:+     +:+     :+:+:+  +:+ :+:+:+  +:+ +:+        +:+    +:+ ");
		System.out.println(" +#+  +:+  +#+     +#+     +#+ +:+ +#+ +#+ +:+ +#+ +#++:++#   +#++:++#:  ");
		System.out.println(" +#+ +#+#+ +#+     +#+     +#+  +#+#+# +#+  +#+#+# +#+        +#+    +#+ ");
		System.out.println("  #+#+# #+#+#      #+#     #+#   #+#+# #+#   #+#+# #+#        #+#    #+# ");
		System.out.println("  ###   ###   ########### ###    #### ###    #### ########## ###    ### ");
		System.out.println("");

		System.out.println("The winner is: " + winner_name);
        System.exit(5);

        saveGameResult();
	 }
	 
	 public static void saveGameResult() {
		    try {
		        // Create logs directory if it does not exist
		        String logDirectory = System.getProperty("user.home") + "/logs";
		        File directory = new File(logDirectory);
		        if (!directory.exists()) {
		            directory.mkdir(); //Create the logs folder
		        }

		        // Format time stamp for the log file name
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		        String timestamp = LocalDateTime.now().format(formatter);
		        String filePath = logDirectory + "/game_log_" + timestamp + ".log";

		        // Open the log file
		        try (FileWriter writer = new FileWriter(filePath)) {
		            writer.write("=== Game Log ===\n");
		            writer.write("Date and Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
		            writer.write("Player Name: " + main_program.player_name + "\n");
		            writer.write("Player Score: " + main_program.player_score + "\n");
		            writer.write("Robot Score: " + main_program.robot_score + "\n\n");

		            writer.write("<-------- Snakes -------->\n");
		            writer.write("Snake 1: Head: " + board_snakes_ladder.snakes[0][0] + " -> Tail: " + board_snakes_ladder.snakes[0][1] + "\n");
		            writer.write("Snake 2: Head: " + board_snakes_ladder.snakes[1][0] + " -> Tail: " + board_snakes_ladder.snakes[1][1] + "\n\n");

		            writer.write("<-------- Ladders -------->\n");
		            writer.write("Ladder 1: Bottom: " + board_snakes_ladder.ladders[0][0] + " -> Top: " + board_snakes_ladder.ladders[0][1] + "\n");
		            writer.write("Ladder 2: Bottom: " + board_snakes_ladder.ladders[1][0] + " -> Top: " + board_snakes_ladder.ladders[1][1] + "\n");

		            writer.write("\n=== End of Log ===\n");

		            // Display the file location in the terminal
		            System.out.println("\n Game log saved to: " + filePath);

		        } catch (IOException e) {
		            System.out.println("Error writing log file: " + e.getMessage());
		            e.printStackTrace();
		        }
		    } catch (Exception e) {
		        System.out.println("Error creating log directory: " + e.getMessage());
		        e.printStackTrace();
		    }
		}
}
