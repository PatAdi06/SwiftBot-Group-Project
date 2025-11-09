package dance_task;

import swiftbot.SwiftBotAPI;

public class BotMovement {
	
	private SwiftBotAPI swiftBot;
	
	public BotMovement(SwiftBotAPI swiftbot) { //Initialising SwiftBot in the class
        this.swiftBot = swiftbot;
    }
	
	public int speed(int octal) {
		int speed = octal; //Speed value is set to whatever integer is fed into the class
		
		if(octal < 50) {
			speed = speed + 50;
		}
		if(speed > 100) {
			speed = 100;
		}
		
		return speed;
	}
	
	public int[] underlightColour(int denary) {
		int red = denary;
		int green = (denary % 80) * 3; //Remainder of denary value divided by 80, then * by 3
		int blue;
		
		int[] rgb;
		rgb = new int[3]; //Creating array that can only store 3 values
		
		if(red > green) {
			blue = red;
		} else {
			blue = green;
		}
		
		//Assigning the RGB values to indexes
		rgb[0] = red;
		rgb[1] = green;
		rgb[2] = blue;
		
		return rgb;
	}
	
	public void movement(String hexadecimal, String binary, int speed, int[] rgb) {
		swiftBot.fillUnderlights(rgb); //SwiftBot API used to turn on lights with values rgb
		
		try {
			for(int i = binary.length() - 1; i >= 0; i--) { //Works Right to Left, takes whole length and -1
				char characterAtPoint = binary.charAt(i); //Gets the char at point "i" (either 0 or 1)
			
				if(characterAtPoint == '0') { 
					swiftBot.move(speed, 0, 1500); //Will Spin around if Char is 0
					
				} else if(characterAtPoint == '1') { // Will Move at a speed if Char is 1
					
					if(hexadecimal.length() == 1) {
						swiftBot.move(speed, speed, 1000); //Move for 1s
						
					} if(hexadecimal.length() == 2) {
						swiftBot.move(speed, speed, 500); //Moves for 0.5s
					}
				}
			}
			
		} catch (Exception e) { //Catches any issue with Underlights, SwiftBot or Code
			System.out.println("There was an error with the Underlights or Moving the SwiftBot");
			e.printStackTrace();
		}
		
		swiftBot.disableUnderlights(); //Turns off the Lights at end of movement
		
	}
	
}
