package dance_task;
public class Conversion {
	
	private int setBaseValue = 16;
	
	public int hexaDenary(String value) { //This Method converts from hexadecimal to denary
		int total = 0;
		int charValue;
		
		for(int i = 0 ; i < value.length(); i++) { //Length of Hexadecimal (1 or 2 values)
			char actualChar = value.charAt(i);
			if(actualChar == 'A' || actualChar == 'a') { //Check what value is and do calculation with formula
				charValue = 10;
				total = (total * setBaseValue) + charValue;
				
			} else if(actualChar == 'B' || actualChar == 'b') {
				charValue = 11;
				total = (total * setBaseValue) + charValue;
				
			} else if(actualChar == 'C' || actualChar == 'c') {
				charValue = 12;
				total = (total * setBaseValue) + charValue;
				
			} else if(actualChar == 'D' || actualChar == 'd') {
				charValue = 13;
				total = (total * setBaseValue) + charValue;
				
			} else if(actualChar == 'E' || actualChar == 'e') {
				charValue = 14;
				total = (total * setBaseValue) + charValue;
				
			} else if(actualChar == 'F' || actualChar == 'f') {
				charValue = 15;
				total = (total * setBaseValue) + charValue;
				
			} else {
				String stringValue = String.valueOf(actualChar);
				charValue = Integer.parseInt(stringValue);
				total = (total * setBaseValue) + charValue;
			}
			
		}
		return total;
	} //END OF HEX TO DENARY
	
	
	public String hexaBinary(String value) { //This method converts from Hexadecimal to Binary, and should only take in Hexadecimal
		String binaryTotal = "";
		String binaryValue = "";
		
		for(int i = 0 ; i < value.length(); i++) {
			char charValue = value.charAt(i);
			
			switch(charValue) //Switch Case to change binaryValue depending on char at point
			{				// Then will be added to binaryTotal at the end
				case '0':
					binaryValue = "0000";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '1':
					binaryValue = "0001";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '2':
					binaryValue = "0010";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '3':
					binaryValue = "0011";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '4':
					binaryValue = "0100";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '5':
					binaryValue = "0101";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '6':
					binaryValue = "0110";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '7':
					binaryValue = "0111";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '8':
					binaryValue = "1000";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case '9':
					binaryValue = "1001";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case 'A':
				case 'a':
					binaryValue = "1010";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case 'B':
				case 'b':
					binaryValue = "1011";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case 'C':
				case 'c':
					binaryValue = "1100";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case 'D':
				case 'd':
					binaryValue = "1101";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case 'E':
				case 'e':
					binaryValue = "1110";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				case 'F':
				case 'f':
					binaryValue = "1111";
					binaryTotal = binaryTotal + binaryValue;
					continue;
				default: //If its not a stated case, it will go to here
					binaryValue = "";
					binaryTotal = binaryTotal + binaryValue;
					continue;
			}
		}
		return binaryTotal;
	} //END OF HEX TO BINARY
	
	public int denaryOctal(int value) { //This will convert back from denary to octal
		int div = value;
		int remainder;
		String total = "";
		
		while(div != 0) {
			remainder = div % 8;
			String tempString = String.valueOf(remainder);
			total = tempString + total; //Will add whatever the remainder is to the front of the total
			div = div / 8;
		}
		int octalValue = Integer.parseInt(total);
		
		return octalValue;
	} //END OF DENARY TO OCTAL
	
	public String denaryHexa(int value) { //Will convert from Denary to Hexadecimal
		int remainder;
		int denary = value;
		String hexa = "";
		String letterValue; //Used to store what the Letter is, if above a number (A-F)
		
		while(denary > 0) {
			remainder = denary % 16;
			if(remainder == 10) {
				letterValue = "A";
				hexa = letterValue + hexa; //Adding the Letter to the FRONT of the hexadecimal
			} else if(remainder == 11) {
				letterValue = "B";
				hexa = letterValue + hexa;
			} else if(remainder == 12) {
				letterValue = "C";
				hexa = letterValue + hexa;
			} else if(remainder == 13) {
				letterValue = "D";
				hexa = letterValue + hexa;
			} else if(remainder == 14) {
				letterValue = "E";
				hexa = letterValue + hexa;
			} else if(remainder == 15) {
				letterValue = "F";
				hexa = letterValue + hexa;
			} else {
				hexa = String.valueOf(remainder) + hexa;
			}
			
			denary = denary / 16;
		
		}
		
		return hexa;
	} //END OF DENARY TO HEXA
		
}
