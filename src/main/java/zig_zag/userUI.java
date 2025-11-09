package zig_zag;
import java.util.Scanner;

import swiftbot.SwiftBotAPI;


public class userUI {
	private static SwiftBotAPI API = new SwiftBotAPI();
	public static void main(String[] args) {
	
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("=================================");
		System.out.println("  Welcome to the Zigzag Program  ");
		System.out.println("=================================");

		// Ask user to scan a QR code before proceeding
		String qrCodeData = "";
		while (qrCodeData.isEmpty()) {
			System.out.println("Please scan a valid QR code to continue...");
			QrScanner qrScanner = new QrScanner(API); // Start QR scanning
			qrCodeData = qrScanner.scanQR(); // Get the scanned QR data

			if (qrCodeData.isEmpty()) {
				System.out.println("Invalid QR code! Please try again.");
			}
		}
			
		
		System.out.println("QR Code Verified: " + qrCodeData);
		

		
		System.out.println("1. Start Zigzag Configuration");
		System.out.println("2. Exit");
		System.out.print("Please select an option: ");
		
		int choice = scanner.nextInt();
		scanner.nextLine();

		if (choice == 1) {
			ZigzagLengthAndSection zigzag = new ZigzagLengthAndSection(API);
				zigzag.runZigZag();
			
		} else if (choice == 2) {
			System.out.println("Exiting program!! Goodbye...");
			scanner.close();
			System.exit(0);
		} else {
			System.out.println("Invalid choice! Please select 1 or 2 next time... ");
		}

	}
}