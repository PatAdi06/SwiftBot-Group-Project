package main_program;
import swiftbot.*;
import java.util.Scanner;

import Navigate.*;
import snakes_ladder.*;
import dance_task.*;
import detect_object.*;
import traffic_light.*;
import tunnel_vision.*;
import zig_zag.userUI;
import master_mind.*;
import search_light.*;

public class SwiftBot_MainProgram {
	
    public static void main(String[] args) throws InterruptedException {


System.out.println(" Welcome!");
Scanner scanner = new Scanner(System.in);  // Create a Scanner object

System.out.println(" Choose the game you want to play:");
System.out.println("1 - Search for light");
System.out.println("2 - Dance");
System.out.println("3 - Master Mind");
System.out.println("4 - Draw Shape");
System.out.println("5 - Tunnel Vision");
System.out.println("6 - Detect Object");
System.out.println("7 - Traffic Light");
System.out.println("8 - Navigate");
System.out.println("9 - Snakes and ladders");
System.out.println("10 - ZigZag");

System.out.print("Enter a number: ");
int number = scanner.nextInt();  // Read an integer from the user

switch(number) {
case 1: //Search for Light
	UserUI.main(null);
	break;
case 2: //Dance
	DanceMainGame.main(null);
	break;
case 3: //Master Mind
	MasterMind.main(null);
	break;
case 4: //Draw Shape
		System.out.println("Missing Task. Try another one!");
		break;
case 5: //Tunnel Vision
	DoesMySwiftBotWork.main(null);
	break;
case 6: //Detect Object
	ObjectDetection.main(null);
	break;
case 7: //Traffic Light
	TrafficLight.main(null);
	break;
case 8: //Navigate
	Navigate.main(null);
	break;
case 9: //Snakes and Ladders
	main_program.main(null);
	break;
	
case 10: //Zig Zag
	userUI.main(args);
	break;

default: break;
	
}
 
    }

	
	
}
