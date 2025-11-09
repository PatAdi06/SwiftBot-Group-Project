package search_light;
import java.util.Random;
import java.util.Scanner;
public class UserUI {

	public static void main(String[] args) {
		Scanner scanner  = new Scanner(System.in);
		Random random = new Random();
		System.out.println("***************************");
		System.out.println("            SWIFTBOT          ");
		System.out.println("***************************");
		System.out.println("Press A to start detecting light");
		String X = scanner.nextLine().toUpperCase();

		while(!X.equals("A")) {
			System.out.println("Inalid input! Press A to start");
			X= scanner.nextLine().toUpperCase();
		}
		//The User will press the A button the program will progress
		System.out.println("Press A to Detect Light ");
		System.out.println("Press X to Exit ");
		// this detects to see whether it detects light or exits the program
		while (true) {
			X = scanner.nextLine().toUpperCase();
			if(X.equals("X")) {
				System.out.println("Program will now terminate");
				break;
			} else if(X.equals("A")) {
				System.out.println("Detecting Light");
			}else {
				System.out.println("Invalid input! Press A to detect light or X to exit.");
			
			}
			
		
		
		while(!X.equals("A")) {
			System.out.println("Inalid input! Press A to start");
			X = scanner.nextLine().toUpperCase();
		}
		

	}

	}




	}


