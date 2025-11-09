package dance_task;
import swiftbot.*;
import java.awt.image.BufferedImage;

public class QRCode {
	
	public boolean correctQRCode = true;
	public String[] danceOrder;
	private SwiftBotAPI swiftBot;
	
    public QRCode(SwiftBotAPI swiftbot) { //Initialising the SwiftBot API
        this.swiftBot = swiftbot;
    }


	public String[] validQRCode() { //This method will be used to SCAN a QR Code and check whether its valid
		
		try {
			System.out.println("Welcome to the Dance Game! Please Scan a QR Code that is UNDER 5 Values.");
			
			correctQRCode = true; //Used to reset the value every time the method is called
			while(correctQRCode == true) { //Will keep on going until a Valid QR is got
				
				BufferedImage image = swiftBot.getQRImage(); //Takes image using camera
				String decodeQR = swiftBot.decodeQRImage(image); //Decode image
				
				if(decodeQR.isEmpty()) {
					System.out.println("There is no QR Code for the SwiftBot to Scan. Please try to scan another QR Code!");
					Thread.sleep(1750);
				} else {
					danceOrder = decodeQR.split(":");
					
					if(danceOrder.length > 5 || danceOrder.length < 1) { //Making sure its in the requirements (< 5 Values)
						System.out.println("This QR Code is INVALID. Please try to scan another QR Code!");
					} else {
						System.out.println("This QR Code is VALID!");
						correctQRCode = false;
					}
				}
			}	
		} catch (Exception e) { //Catching any potential issues
			System.out.println("There is an error whilst trying to scan or use the QR Code.");
			e.printStackTrace();
		}
		return danceOrder;
	}
	
	public boolean isValueValid(String string) { //This method is used to check whether  a specific value is valid
		boolean value = false;
		
		try {//Uses regular expressions, checking if the value matches whatever is in "string.matches"
			if(string.matches("[1-9A-F]{1,2}") || string.matches("[1-9a-f]{1,2}")) { 
				value = true;
			} else {
				value = false;
			}
		} catch(Exception e) { //Gets any error with the Code not working (invalid input maybe)
			System.out.println("There is an error with checking the QR Code");
		}
		
		return value;
	}
}