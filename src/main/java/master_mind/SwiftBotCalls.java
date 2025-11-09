package master_mind;

import swiftbot.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

//Class for SwiftBot calls
public class SwiftBotCalls {
	
	static SwiftBotAPI swiftbot;
	public char swiftBotButton = ' ';
	
	public SwiftBotCalls() {
		try {
			swiftbot = new SwiftBotAPI();
		}catch(Exception ex) {
			System.out.println("No swiftbot found " + ex.getMessage());
		}
	}
	
	public char readButton() {
		try {
			swiftBotButton = ' '; //initialise button
			long endtime = System.currentTimeMillis() + 10_000;
			swiftbot.enableButton(Button.A, () -> {
				swiftBotButton = 'A';
				swiftbot.disableButton(Button.A);
			});
	
			swiftbot.enableButton(Button.B, () -> {
				swiftBotButton = 'B';
				swiftbot.disableButton(Button.B);
			});
			swiftbot.enableButton(Button.X, () -> {
				swiftBotButton = 'X';
				swiftbot.disableButton(Button.X);
			});
	
			swiftbot.enableButton(Button.Y, () -> {
				swiftBotButton = 'Y';
				swiftbot.disableButton(Button.Y);
			});
			while (System.currentTimeMillis() < endtime) {
				 // This while loop waits for 10 seconds.
				if(swiftBotButton != ' ')
					break;
				Thread.sleep(200);
			}
	
			swiftbot.disableAllButtons(); // Turns off all buttons now that it's been 10 seconds.
		} catch(Exception ex) {
			
		}
		
		return swiftBotButton;
	}
	
	public GameColours readColour() {
		int treshold = 100;
		int redCount = 0;
		int greenCount = 0;
		int blueCount = 0;
		int yellowCount = 0;
		int orangeCount = 0;
		int pinkCount = 0;
		
		try { 
		BufferedImage img = swiftbot.takeStill(ImageSize.SQUARE_48x48);
		
		int[] redMin = {180,0,0};
		int[] redMax = {255,50,50};
		
		int[] greenMin = {0,190,0};
		int[] greenMax = {100,255,50};
		
		int[] blueMin = {0,0,180};
		int[] blueMax = {80,80,255};
		
		int[] yellowMin = {180,180,0};
		int[] yellowMax = {255,255,100};
		
		int[] orangeMin = {200,100,0};
		int[] orangeMax = {255,165,100};
		
		int[] pinkMin = {180,0,140};
		int[] pinkMax = {255,100,255};
		
		 for (int y = 0; y < img.getHeight(); y++) {
	         for (int x = 0; x < img.getWidth(); x++) {
	            //Retrieving contents of a pixel
	            int pixel = img.getRGB(x,y);
	            //Creating a Colour object from pixel value
	            Color color = new Color(pixel, true);
	            //Retrieving the R G B values
	            int red = color.getRed();
	            int green = color.getGreen();
	            int blue = color.getBlue();
	            //System.out.println("Red " + red + "Green " + green + "Blue " + blue); 
	            
	            if(red >= redMin[0] && red <= redMax[0] && green >= redMin[1] && 
	            		green <= redMax[1] && blue >= redMin[2] && blue <= redMax[2])
	            	redCount++;
	            if(red >= greenMin[0] && red <= greenMax[0] && green >= greenMin[1] && 
	            		green <= greenMax[1] && blue >= greenMin[2] && blue <= greenMax[2])
	            	greenCount++;
	            if(red >= blueMin[0] && red <= blueMax[0] && green >= blueMin[1] && 
	            		green <= blueMax[1] && blue >= blueMin[2] && blue <= blueMax[2])
	            	blueCount++;
	            if(red >= yellowMin[0] && red <= yellowMax[0] && green >= yellowMin[1] && 
	            		green <= yellowMax[1] && blue >= yellowMin[2] && blue <= yellowMax[2])
	            	yellowCount++;
	            if(red >= orangeMin[0] && red <= orangeMax[0] && green >= orangeMin[1] && 
	            		green <= orangeMax[1] && blue >= orangeMin[2] && blue <= orangeMax[2])
	            	orangeCount++;
	            if(red >= pinkMin[0] && red <= pinkMax[0] && green >= pinkMin[1] && 
	            		green <= pinkMax[1] && blue >= pinkMin[2] && blue <= pinkMax[2])
	            	pinkCount++;
	         }
	      }
		 System.out.println();

		} catch(Exception ex) {
			System.out.println("Error reading colour " + ex.getMessage());
		}
		if(pinkCount >= treshold)
			return GameColours.Pink;
		else if(orangeCount >= treshold)
			return GameColours.Orange;
		else if(greenCount >= treshold)
			return GameColours.Green;
		else if(blueCount >= treshold)
			return GameColours.Blue;
		else if(redCount >= treshold)
			return GameColours.Red;
		else if(yellowCount >= treshold)
			return GameColours.Yellow;
		else
			return GameColours.NotValid;
	}
	
	
	public void lightLED(GameColours colour, int round) {
		//Colour definition of swiftbot
		//If not valid, light white led
		int[] rgb = new int[] {255,255,255};
		if(colour == GameColours.Blue)
			rgb = new int[] { 0, 0, 255 };
		else if(colour == GameColours.Red)
			rgb = new int[] { 255, 0, 0 };
		else if(colour == GameColours.Green)
			rgb = new int[] { 0, 255, 0 };
		else if(colour == GameColours.Yellow)
			rgb = new int[] { 255, 255, 0 };
		else if(colour == GameColours.Orange)
			rgb = new int[] { 255, 165, 0 };
		else if(colour == GameColours.Pink)
			rgb = new int[] { 255, 100, 255 };
			
		if(round == 0)
			swiftbot.setUnderlight(Underlight.BACK_RIGHT, rgb);
		if(round == 1)
			swiftbot.setUnderlight(Underlight.BACK_LEFT, rgb);
		if(round == 2)
			swiftbot.setUnderlight(Underlight.MIDDLE_RIGHT, rgb);
		if(round == 3)
			swiftbot.setUnderlight(Underlight.MIDDLE_LEFT, rgb);
		if(round == 4)
			swiftbot.setUnderlight(Underlight.FRONT_RIGHT, rgb);
		if(round == 5)
			swiftbot.setUnderlight(Underlight.FRONT_LEFT, rgb);
			
		
	}
	
	public void disableLEDs() {
		swiftbot.disableUnderlights();
	}
	
}
