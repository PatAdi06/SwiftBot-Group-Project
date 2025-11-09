package master_mind;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

//To store game data
public class GameData {
	public int randomColoursCount;
	public int totalPossibleAttempts;
	public int numberOfAtempts;
	
	public GameColours[] randomColours;
	public GameColours[] userGuess;
	
	public boolean gameWon;
	public int round;
	
	public int userScore = 0;
	public int computerScore = 0;
	
	public String logFileName = "MasterMindScore.csv";
	public GameData() {
		
	}
	
	public void mainMenu(char swiftBotButton) {
		
		System.out.println(" ");
		
		if(swiftBotButton == 'A') {
			randomColoursCount = 4;
			totalPossibleAttempts = 6;
			numberOfAtempts = 0;
		} else if(swiftBotButton == 'B') {
			randomColoursCount = 0;
			while(randomColoursCount < 3 || randomColoursCount > 6 ) {
				try {
					System.out.print("Select colours range of 3 to 6 = ");
					String ret = System.console().readLine();
					randomColoursCount = Integer.valueOf(ret);
				} catch(Exception ex) {
					//Ignore error, ask user again
				}
			}
			System.out.println(" ");
			
			totalPossibleAttempts = 0;
			while(totalPossibleAttempts < 3 || totalPossibleAttempts > 10) {
				try {
					System.out.print("Set maximum number of guesses between 3 to 10 = ");
					String ret1 = System.console().readLine();
					totalPossibleAttempts = Integer.valueOf(ret1);
				} catch(Exception ex) {
					//Ignore error, ask user again
				}
				
			}
			
		}
		
	}
	
	public void initialiseArrays() {
		randomColours = new GameColours[randomColoursCount];
		userGuess = new GameColours[randomColoursCount];
		gameWon = false;
		round = 0;
		
		for(int i = 0; i < randomColoursCount; i++) {
			// Used to make all colours different
			boolean valid = false;
			while(!valid) {
				int randomColour =  (int) (Math.random() * 6);
				
				//Using array list for contains method
				List<GameColours> checkArrayList = Arrays.asList(randomColours);
				if(!checkArrayList.contains(GameColours.values()[randomColour])) {
					randomColours[i] = GameColours.values()[randomColour];
					//To exit while, make valid true
					valid = true;
				}
				
			}		
			
		}
		
	}
	
	public void askUserColouredCard() {
		System.out.println(" ");
		System.out.println("Show colour number " + (round + 1));
		System.out.println(" ");
		System.out.println("Available colours Red, Green, Blue, Yellow, Orange and Pink");
		System.out.println(" ");
		try {
			Thread.sleep(2000);
		} catch(Exception ex) {}
			
		//Count down from 5
		for(int i = 5; i > 0; i--) {
			System.out.print(i + " ");
			try{
			Thread.sleep(1000);
			} catch(Exception ex) {}		
		
		}
		System.out.println();
		
	}
	
	public void checkColourArray() {
		GameColours[] tempArray = new GameColours[randomColoursCount];
		//set check array data
		for(int i = 0; i < randomColoursCount; i++)
			tempArray[i] = randomColours[i];
		int positiveScore = 0;
		int negativeScore = 0;
		
		// To use for finding item. Array doesn't have contains
		List<GameColours> checkArrayList = Arrays.asList(tempArray);
		
		System.out.println();
		System.out.println(Arrays.toString(userGuess));
		
		for(int i = 0; i < randomColoursCount; i++) {
			if(userGuess[i] == tempArray[i]) {
				positiveScore++;
				tempArray[i] = null;
				// Also remove from the list
				checkArrayList.set(i, null);
			}else if(checkArrayList.contains(userGuess[i])) {
				negativeScore++;
			}else {
				//Do nothing
			}
		}
		
		String score = "";
		//print scores 
		for(int i = 0; i < positiveScore; i++)
			score += "+";
		for(int i = 0; i < negativeScore; i++)
			score += "-";
		System.out.println("Your Score: " + score);
		
		// increase number of attempts
		numberOfAtempts++;
		
		writeLog(logFileName, score);
		System.out.println();
		
		//Check if game is won
		if(positiveScore == randomColoursCount)
			gameWon = true;
		
	}
	
	public void writeLog(String fileName, String score) {

		String dir = System.getProperty("user.dir");
		String logLine = (userScore + computerScore + 1) + "," + Arrays.toString(randomColours);
		logLine += "," + Arrays.toString(userGuess) + "," + score + "," + numberOfAtempts 
				+ "," + (totalPossibleAttempts - numberOfAtempts) + System.getProperty("line.separator");
		try {	
			File f = new File(dir + "/" + fileName);
			if(f.exists() && !f.isDirectory()) {
				//append to log file
				Files.write(Paths.get(dir + "/" + fileName), logLine.getBytes(), StandardOpenOption.APPEND);		
			}
			else {
				//Create the log file
				PrintWriter writer;
				try {
					writer = new PrintWriter(fileName, "UTF-8");
					//Add header
					writer.println("Round, Computer Guess, User Guess, Score, Attempt No, Attempts Rem.");
					//Add line
					writer.print(logLine);
					writer.close();
				} catch (Exception e) {
					// Ignore error
					e.printStackTrace();
				} 
			}
		}
		catch(Exception ex){
			//Ignore all exceptions
		}
	
	}
	
	public void newGame() {
			
		if(gameWon == true) {
			userScore++;
			System.out.println("\u001B[32mYou Won!\u001B[0m");
		} else {
			computerScore++;
			System.out.println("\u001B[31mGame Over!\u001B[0m");
			System.out.println(Arrays.toString(randomColours));
		}
		System.out.println();
		System.out.print("User Score: " + userScore);
		System.out.println("  Computer Score: " + computerScore);
		
	}
}
