package snakes_ladder;

import java.awt.image.BufferedImage;

public class qr_code {
	   public static void testQRCodeDetection() {

	        System.out.println("Press Button 'X' to Scan the QR Code!");
	        
	        if(checking_tests.check_button_pressed()==3) {
	            
	            System.out.println("Starting 10s timer to scan a QR code");
	            startQRCodeScanning(); // Start QR code scanning
	        } else {
	        	System.out.println("Wrong Button was Pressed! Would you like to try again?");
	        	System.out.println("Press Button Y - Yes \nAny other button will Exit the program!");
	        	int checking_button = checking_tests.check_button_pressed();

				if(checking_button==1) {
	        		testQRCodeDetection();
	        	} else System.exit(1);
	        }
	    }

	    private static void startQRCodeScanning() {
	        String decoded_name = "";

	       
	        long startTime = System.currentTimeMillis();
	        long endTime = startTime + 10000; // 10 seconds in milliseconds


	        try {

	            while (System.currentTimeMillis() < endTime) {

	        		BufferedImage img = main_program.swiftBot.getQRImage();
	               decoded_name = main_program.swiftBot.decodeQRImage(img);
	                if (!decoded_name.isEmpty()) {
	                	System.out.println("");
	                    System.out.println("SUCCESS: QR code found");
	                    System.out.println("Player's name: " + decoded_name);
	                	System.out.println("");
	                	main_program.player_name = decoded_name;
	                    // Continue the game after decoding
	                	
	                    break;
	                } 
	            }
	            
	            if(!decoded_name.isEmpty()) {
	            	if(decoded_name.length()>10) {
	            		System.out.println("The name should be 10 characters! Would you like to try again?");
	                	System.out.println("=>Press Button Y - Yes \n=>Any other button will Exit the program!");
	                	if(checking_tests.check_button_pressed()==1) {
	                        System.out.println("Starting 10s timer to scan a QR code");
	                		startQRCodeScanning();
	                	} else System.exit(1);
	                	
	            	} else main_program.startGame();
	            } else{
	            	System.out.println("Unable to scan the QR Code");
	            	System.out.println("Try Again Later!");
	                System.exit(5);
	            }

	            


	        } catch (Exception e) {
	            System.out.println("ERROR: Unable to scan for code.");
	            e.printStackTrace();
	            System.exit(5);
	        }

	    }
}
