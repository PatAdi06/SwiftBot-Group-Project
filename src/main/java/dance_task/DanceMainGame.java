package dance_task;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import swiftbot.*; //https://swiftbot-maven.brunel.ac.uk/javadoc/releases/com/swiftbotlabs/SwiftBot-API/5.1.3 (SWIFTBOT JAVA DOCUMETATION)

public class DanceMainGame {
	
	//Creating Private Static Variables that can be accessed and used from anywhere within the class
	private static boolean gameRunning = true;
	private static ArrayList<String> hexaInputs = new ArrayList<String>();
	private static ArrayList<String> invalidInputs = new ArrayList<String>();
	private static ArrayList<String> SortedValues = new ArrayList<String>();
	
	public static void RunningGame(SwiftBotAPI swiftbot) { //This Method has the main game code
		
		
		//Utilising other classes (e.g Conversion.java)
		QRCode QR = new QRCode(swiftbot);
		Conversion Convert = new Conversion();
		BotMovement RobotMove = new BotMovement(swiftbot);
		
		try {
			while(gameRunning == true) { //Starting the Main Game Loop
				System.out.println(" ");
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				System.out.println("			 SWIFTBOT DANCE GAME			");
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				System.out.println(" ");
				System.out.println(" ");
				
				String[] danceOrder = QR.validQRCode(); //Storing all the values that came out of the VALID QR that was scanned
				
				
				for(int i = 0; i < danceOrder.length; i++) {
					boolean isHexaValid = QR.isValueValid(danceOrder[i]); //Using Method from different class
					
					if(isHexaValid == true){ //If value is TRUE, it will run the rest of the code (do conversions and move SwiftBot)
						String hexadecimal = danceOrder[i];
						int denary = Convert.hexaDenary(hexadecimal);
						String binary = Convert.hexaBinary(hexadecimal);
						int octal = Convert.denaryOctal(denary);
						
						int speed = RobotMove.speed(octal);
						int[] rgb = RobotMove.underlightColour(denary);
						
						hexaInputs.add(danceOrder[i]);
						
						//Displaying all the values in a UI on the Command-Line Interface
						System.out.println(" ");
						System.out.println(" ");
						System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
						System.out.println("			 SWIFTBOT DANCE GAME		");
						System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
						System.out.println(" ");
						System.out.println(" ");
						System.out.println("Hexadecimal Value: " + hexadecimal);
						System.out.println("Binary Value: " + binary);
						System.out.println("Denary Value: " + denary);
						System.out.println("Octal Value: " + octal);
						System.out.println("Speed: " + speed);
						System.out.println("RGB Values - Red: " + rgb[0] + " Green: " + rgb[1] + " Blue:" + rgb[2] );
						System.out.println(" ");
						System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
						System.out.println("The SwiftBot will Start Moving in 3 Seconds.");
						System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
						
						Thread.sleep(3000); //A small 3 second delay in between displaying UI and Moving SwiftBot
						RobotMove.movement(hexadecimal, binary, speed, rgb); //Allows for user to place down SwiftBot on Ground
						
						Thread.sleep(1000); //1 Second before next value in FOR loop is started
						
					} else { //The Value isn't true, so it will add to "invalidInputs" and continue the FOR loop.
						System.out.println("The Value: " + danceOrder[i] + " is INVALID.");
						invalidInputs.add(danceOrder[i]);
						continue; //Continues with the FOR loop
					} //END OF IF STATEMENT
					
				gameRunning = false; //Ending the while loop, will be reactivated if user decides to continue again
				} //END OF FOR LOOP
			}
				//STOP UNDERLIGHTS
				System.out.println("The SwiftBot has finished all of its movements. Would you like to scan another QR Code?");
				
		} catch (Exception e) {
			System.out.println("There's an error when starting the Main Program.");
			e.printStackTrace(); //Prints out the Error in the Program
		}
		
			
		
		}
		
	
	
	public static void main(String[] args) { //This is the main code that will be executed when the program runs
		
		SwiftBotAPI swiftbot = new SwiftBotAPI(); //Calling Instance of SwiftBotAPI Class
		Conversion Convert = new Conversion();
		
		DanceMainGame.RunningGame(swiftbot); //Calling the program, giving it its first go
		
		swiftbot.enableButton(Button.X, () -> { //IF Button X is pressed on SwiftBot, rest of the code will happen
			
			try { //Starts File Handling
				ArrayList<Integer> tempStoreValues = new ArrayList<Integer>(); //Used to store Converted hexadecimal values
				for(int i = 0; i < hexaInputs.size(); i++) {
					
					String inputValue = hexaInputs.get(i);
					int convertedInput = Convert.hexaDenary(inputValue);
					tempStoreValues.add(convertedInput);
				}
				
				File hexadecimalInputs = new File("HexadecimalInputs.txt");
				try (PrintWriter readingToFile = new PrintWriter(hexadecimalInputs)) {
					Collections.sort(tempStoreValues); //Sorting ArrayList using Collections
					
					for(int x = 0; x < tempStoreValues.size(); x++) { //Used to convert the sorted values back into Hexadecimal
						int hexaDenaryConvert = tempStoreValues.get(x);
						String denaryHexa = Convert.denaryHexa(hexaDenaryConvert);
						SortedValues.add(denaryHexa);
					}
					
					readingToFile.println("VALID INPUTS:");
					readingToFile.println(" ");
					for(int i = 0; i < SortedValues.size(); i++) {
						readingToFile.println(SortedValues.get(i)); //Writing the Valid Inputs onto the txt file 
					}
					
					readingToFile.println(" ");
					readingToFile.println(" ");
					readingToFile.println("INVALID Inputs:"); 
					readingToFile.println(" ");
					for(int j = 0; j < invalidInputs.size(); j++) {
						readingToFile.println(invalidInputs.get(j)); //Writing the Invalid Inputs onto the txt file
					}
				}
				
				String fileName = hexadecimalInputs.getPath(); //Getting the Name of the File
				String filePath = hexadecimalInputs.getAbsolutePath(); //Getting the File Path of the File
				
				System.out.println(" "); //Displaying File Name and File Path
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				System.out.println("			 SWIFTBOT DANCE GAME		");
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				System.out.println("You have decided to exit the program.");
				System.out.println("A Text File has been created with all the inputs in ASCENDING order in it.");
				System.out.println("The file name is: " + fileName);
				System.out.println("The path to the file is: " + filePath);
				System.out.println("Thank You for running the code!");
				System.out.println(" ");
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				
				gameRunning = false;
				System.exit(0); //Terminating the Program
				
			} catch (IOException e) { //Trying to catch if there is a issue with the Text File and Catching It
				System.out.println("There has been an error when writing to the Text File.");
			}
		});	
		swiftbot.enableButton(Button.Y, () -> {
			System.out.println("You have decided to continue the Program!");
			System.out.println(" ");
			System.out.println(" ");
			gameRunning = true; //Setting the game loop back to True
			DanceMainGame.RunningGame(swiftbot); //Running the Main Code again
		});
	}
}