package master_mind;

import java.io.IOException;

//Main game class, game play methods
public class MasterMind {
	
	public static void main(String[] args) {
		// Initialise SwiftBot
		SwiftBotCalls swiftBot = new SwiftBotCalls();
		GameData game = new GameData();
		
		//Main game loop
		boolean playGame = true;
		while(playGame) {
			System.out.println();
			// Colourful Master Mind 
			System.out.println("\u001B[31mM\u001B[33m   \u001B[32mM   \u001B[36mA\u001B[34mAAAA   \u001B[35mSSSSS  \u001B[31mTTTTT  \u001B[37mEEEEE  \u001B[33mRRRRR    \u001B[0m");
	        System.out.println("\u001B[31mMM\u001B[33m MM  \u001B[32mA     A \u001B[36mS         \u001B[34mT    \u001B[35mE      \u001B[37mR   R    \u001B[0m");
	        System.out.println("\u001B[31mM\u001B[33m M\u001B[32m M  \u001B[36mAAAAAAA \u001B[34mSSSSS     \u001B[31mT    \u001B[37mEEEE   \u001B[33mRRRRR    \u001B[0m");
	        System.out.println("\u001B[31mM   M  \u001B[33mA     A \u001B[32m     S    \u001B[36mT    \u001B[34mE      \u001B[35mR  R     \u001B[37m \u001B[0m");
	        System.out.println("\u001B[31mM   M  \u001B[33mA     A \u001B[32mSSSSS     \u001B[36mT    \u001B[34mEEEEE  \u001B[35mR   R    \u001B[37m \u001B[0m");

	        System.out.println("");  

	     
	        System.out.println("\u001B[36mM\u001B[34m   M   \u001B[35mIII  \u001B[32mN   N  \u001B[33mDDDD \u001B[37m\u001B[0m");
	        System.out.println("\u001B[36mMM\u001B[34m MM    \u001B[35mI   \u001B[32mNN  N  \u001B[33mD   D \u001B[37m\u001B[0m");
	        System.out.println("\u001B[36mM\u001B[34m M\u001B[35m M    I   \u001B[32mN N N  \u001B[33mD   D \u001B[37m\u001B[0m");
	        System.out.println("\u001B[36mM   M    I   \u001B[34mN  NN  \u001B[35mD   D \u001B[32m\u001B[37m \u001B[0m");
	        System.out.println("\u001B[36mM   M   III  \u001B[34mN   N  \u001B[35mDDDD \u001B[37m\u001B[0m");
	        
			while(swiftBot.swiftBotButton != 'B' && swiftBot.swiftBotButton != 'A') {
				System.out.println();
				System.out.println("\u001B[32mClick button 'A' for default mode or button 'B' for customised mode.\u001B[0m");
				// Read button form SwiftBot 
				swiftBot.swiftBotButton = swiftBot.readButton();			
		
			}
			
			game.mainMenu(swiftBot.swiftBotButton);
			
			game.initialiseArrays();
			
			while(game.numberOfAtempts < game.totalPossibleAttempts) {
				oneGameAttempt(game, swiftBot);
				game.checkColourArray();
				swiftBot.disableLEDs();
				
				if(game.gameWon)
					break;
			}
			
			game.newGame();
			
			while(swiftBot.swiftBotButton != 'Y' && swiftBot.swiftBotButton != 'X') {
				System.out.println("\u001B[32mPress 'Y' for new game and press 'X' to quit.\u001B[0m");
				// Read button form SwiftBot 
				swiftBot.swiftBotButton = swiftBot.readButton();				
			}
			
			if(swiftBot.swiftBotButton == 'X') {
				System.out.println("Thanks for playing!");
				String dir = System.getProperty("user.dir");
				System.out.println("Log file is in " + dir + "/" + game.logFileName);
				playGame = false;
			}
			//initialise number of attempts
			game.numberOfAtempts = 0;
			
		}
		
		return;
		
	}
	
	public static void oneGameAttempt(GameData game, SwiftBotCalls swiftBot){
		System.out.println("------------------------------------------------------------");
		int na = game.numberOfAtempts + 1;
		System.out.println();
		System.out.println("Guess attempt " + na);
		System.out.println();
		
		if(na != 1) {
		System.out.println("Press Enter to continue...");
		try {
		System.in.read();
		}catch(IOException ex) {
				
		}
		}
		while(game.round < game.randomColoursCount) {
			
			game.askUserColouredCard();
			
			GameColours userColour = swiftBot.readColour();
			//Light user selected colour
			swiftBot.lightLED(userColour, game.round);
			if(userColour == GameColours.NotValid) {
				System.out.println();
				System.out.println("\u001B[31m" + "Coloured card presented is not valid!" + "\u001B[0m");
			} else {
				game.userGuess[game.round] = userColour;
				game.round++;
			}			
		}
		// initialise for the next attempt
		game.round = 0;	
		try {
			Thread.sleep(3000);
		} catch(Exception ex) {
			// ignore exception
		}
	}
	

}
