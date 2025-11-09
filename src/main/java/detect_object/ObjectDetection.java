package detect_object;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import javax.imageio.ImageIO;

import swiftbot.Button;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;

public class ObjectDetection {

	public static SwiftBotAPI swiftBot;
	static Mode mode;
	static LocalDate currentDate;
	static long startingTime = System.currentTimeMillis();
	static Long currentTime;
	static Long executionTime;
	static boolean terminating = false;

	public static void main(String[] args) throws InterruptedException {
		try {
			swiftBot = new SwiftBotAPI();

		} catch (Exception e) {
			System.out.println("\nI2C disabled!");
			System.out.println("Run the following command:");
			System.out.println("sudo raspi-config nonint do_i2c 0\n");
			System.exit(5);
		}
		/*
		 * make the robot halt its motion and turn off all its underlights. This is done
		 *
		 * as sometimes the user might terminate the program before the program gets a
		 * chance to turn of the underlights or stop the movement of the swiftbot.
		 */
		swiftBot.move(1, 1, 1);
		swiftBot.disableUnderlights();
		mode = new Mode();

		// TOOD: Added a more visually appealing text.
		System.out.println("  ___  _     _           _     ____       _            _             \r\n"
				+ " / _ \\| |__ (_) ___  ___| |_  |  _ \\  ___| |_ ___  ___| |_ ___  _ __ \r\n"
				+ "| | | | '_ \\| |/ _ \\/ __| __| | | | |/ _ \\ __/ _ \\/ __| __/ _ \\| '__|\r\n"
				+ "| |_| | |_) | |  __/ (__| |_  | |_| |  __/ ||  __/ (__| || (_) | |   \r\n"
				+ " \\___/|_.__// |\\___|\\___|\\__| |____/ \\___|\\__\\___|\\___|\\__\\___/|_|   \r\n"
				+ "          |__/");

		System.out.println();
		System.out.println(
				"Select a mode by presenting a text-based QR code to the camera of the swiftbot containing one of the following modes (the text contained in the QR code, must be written exactly like it is below):");
		System.out.println();
		System.out.println("Curious Swiftbot");
		System.out.println("Scaredy Swiftbot");
		System.out.println("Dubious Swiftbot");
		System.out.println();

		mode.setMode(readTextInQRCode());
		mode.setLastMode(readTextInQRCode());

		if (mode.getMode().equalsIgnoreCase("curious swiftbot")) {
			System.out.println("  ____           _                   __  __           _      \r\n"
					+ " / ___|   _ _ __(_) ___  _   _ ___  |  \\/  | ___   __| | ___ \r\n"
					+ "| |  | | | | '__| |/ _ \\| | | / __| | |\\/| |/ _ \\ / _` |/ _ \\\r\n"
					+ "| |__| |_| | |  | | (_) | |_| \\__ \\ | |  | | (_) | (_| |  __/\r\n"
					+ " \\____\\__,_|_|  |_|\\___/ \\__,_|___/ |_|  |_|\\___/ \\__,_|\\___|\r\n"
					+ "    _        _   _            _           _                  \r\n"
					+ "   / \\   ___| |_(_)_   ____ _| |_ ___  __| |                 \r\n"
					+ "  / _ \\ / __| __| \\ \\ / / _` | __/ _ \\/ _` |                 \r\n"
					+ " / ___ \\ (__| |_| |\\ V / (_| | ||  __/ (_| |                 \r\n"
					+ "/_/   \\_\\___|\\__|_| \\_/ \\__,_|\\__\\___|\\__,_|                 \r\n");

		} else if (mode.getMode().equalsIgnoreCase("scaredy swiftbot")) {
			System.out.println(" ____                         _         __  __           _      \r\n"
					+ "/ ___|  ___ __ _ _ __ ___  __| |_   _  |  \\/  | ___   __| | ___ \r\n"
					+ "\\___ \\ / __/ _` | '__/ _ \\/ _` | | | | | |\\/| |/ _ \\ / _` |/ _ \\\r\n"
					+ " ___) | (_| (_| | | |  __/ (_| | |_| | | |  | | (_) | (_| |  __/\r\n"
					+ "|____/ \\___\\__,_|_|  \\___|\\__,_|\\__, | |_|  |_|\\___/ \\__,_|\\___|\r\n"
					+ "    _        _   _            _ |___/     _                     \r\n"
					+ "   / \\   ___| |_(_)_   ____ _| |_ ___  __| |                    \r\n"
					+ "  / _ \\ / __| __| \\ \\ / / _` | __/ _ \\/ _` |                    \r\n"
					+ " / ___ \\ (__| |_| |\\ V / (_| | ||  __/ (_| |                    \r\n"
					+ "/_/   \\_\\___|\\__|_| \\_/ \\__,_|\\__\\___|\\__,_|                    \r\n");

		} else {
			System.out.println(" ____        _     _                   __  __           _      \r\n"
					+ "|  _ \\ _   _| |__ (_) ___  _   _ ___  |  \\/  | ___   __| | ___ \r\n"
					+ "| | | | | | | '_ \\| |/ _ \\| | | / __| | |\\/| |/ _ \\ / _` |/ _ \\\r\n"
					+ "| |_| | |_| | |_) | | (_) | |_| \\__ \\ | |  | | (_) | (_| |  __/\r\n"
					+ "|____/ \\__,_|_.__/|_|\\___/ \\__,_|___/ |_|  |_|\\___/ \\__,_|\\___|\r\n"
					+ "    _        _   _            _           _                    \r\n"
					+ "   / \\   ___| |_(_)_   ____ _| |_ ___  __| |                   \r\n"
					+ "  / _ \\ / __| __| \\ \\ / / _` | __/ _ \\/ _` |                   \r\n"
					+ " / ___ \\ (__| |_| |\\ V / (_| | ||  __/ (_| |                   \r\n"
					+ "/_/   \\_\\___|\\__|_| \\_/ \\__,_|\\__\\___|\\__,_|                                     ");

		}

		System.out.println();

		System.out.println(
				"If at any point beyond this message you wish to terminate the program, you may press the button \"X\" on the swiftbot");
		System.out.println();

		startButtons();

	}

	/*
	 * TODO: Added new method that returns the instance to the swiftbotAPI, that way
	 * I can use it across multiple classes.
	 */
	public static SwiftBotAPI getSwiftbot() {
		return swiftBot;

	}

	/*
	 * TODO: Added class which setups all the buttons in order to keep the code more
	 * consise and clean.
	 */
	public static void startButtons() throws InterruptedException {
		try {
			swiftBot.enableButton(Button.X, () -> {
				mode.setValid((false));
				terminating = true;
				currentDate = LocalDate.now();
				currentTime = System.currentTimeMillis();
				executionTime = currentTime - startingTime;
				try {
					writeTo();
				} catch (Exception e) {

					e.printStackTrace();
					System.out.println("File was not able to be generate, try again.");
					// TODO: Added a message for when the save file was not able to be created.
				}

				/*
				 * TODO: Added 'terminating', which once set to true, means that buttons A and B
				 * will work as intended.
				 */
				swiftBot.disableButton(Button.X);

			});

			/*
			 * TODO: Completely removed the program termination function, as in order to use
			 * a button, the program has to be running, which is not possible when you set
			 * the button to immediately terminate the program when pressed.
			 */

			/*
			 * TODO: Changed the buttons for 'yes' and 'no' when the user is asked if they
			 * would like to view the log file. This was done because button X was already
			 * in use, therefore it was changed to A being 'yes' and B being 'no'.
			 */

			/*
			 * Buttons A and B are always active, however only actually do something once
			 * 'terminating' is true. was done because buttons A and B couldn't be enabled
			 * in writeTo function.
			 */
			swiftBot.enableButton(Button.A, () -> {
				if (terminating) {
					System.out.println("Last mode the swiftbot ran: " + mode.getLastMode());
					System.out.println("Execution time for the program: " + executionTime + "ms");
					System.out.println("Number of times the swiftbot encountered an object: " + mode.getObjectsFound());
					System.out.println("Path to where the images where stored: " + "/data/home/pi");
					System.out.println("Path to where this current log file will be stored: " + "/data/home/pi/");
					System.out.println();
					System.out.println("Program terminated.");

					swiftBot.disableButton(Button.A);
					System.exit(0);

				}

			});

			swiftBot.enableButton(Button.B, () -> {
				if (terminating) {
					System.out.println("Path to where this current log file will be stored: " + "/data/home/pi");
					System.out.println();
					System.out.println("Program terminated.");

					swiftBot.disableButton(Button.B);
					System.exit(0);

				}

			});

			/*
			 * have to use a dummy variable, 'valid', since if I put 'true', I can't run
			 * 'swiftBot.disableAllButtons();'.
			 */
			while (mode.getValid()) {
				mode.outBuffer();

				if (mode.getLastMode().equalsIgnoreCase("curious swiftbot")) {
					Curious curiousMode = new Curious(false);

				} else if (mode.getLastMode().equalsIgnoreCase("scaredy swiftbot")) {
					Scaredy scaredyMode = new Scaredy(false);

				} else if (mode.getLastMode().equalsIgnoreCase("dubious swiftbot")) {
					Dubious dubiousMode = new Dubious();

				}
				;
			}
			while (terminating) {

				/*
				 * while terminating is true, meaning that the user has pressed X, do nothing
				 * until the user presses either A or B.
				 */
			}

			swiftBot.disableAllButtons();

		} catch (Exception e) {
			System.out.println("ERROR occurred when setting up buttons.");
			System.out.println();
			// TODO: Added error message for when button do not setup properly
			e.printStackTrace();
			System.exit(5);
		}

	}

	public static String readTextInQRCode() {
		/*
		 * TODO: Added dummy variable 'cameraSuccess' in order to make the program be
		 *
		 * able to keep searching for a qr code indefinitely -- until the user has a
		 * valid text as the qr code message.
		 */
		boolean cameraSuccess = false;
		String userInput = "";
		String userInputMode;
		String userInputSwiftbot;

		try {

			while (!cameraSuccess && mode.getValid()) {

				BufferedImage img = swiftBot.getQRImage();
				userInput = swiftBot.decodeQRImage(img);

				// check if input is empty, or a valid mode is not entered
				if ((userInput.equalsIgnoreCase("Curious Swiftbot")) || (userInput.equalsIgnoreCase("Scaredy Swiftbot"))
						|| (userInput.equalsIgnoreCase("Dubious Swiftbot"))) {
					cameraSuccess = true;

				} else if (userInput.isEmpty()) {
					System.out.println(
							"Please present a QR code that has a correctly typed mode to the camera of the Swiftbot. A message was not able to be derived from the QR code. Try adjusting the distance from the QR code to the camera.");
					System.out.println();

				} else {
					System.out.println(
							"Please present a QR code that has a correctly typed mode to the camera of the Swiftbot.");
					System.out.println();

				}

				Thread.sleep(1000);

			}

		} catch (Exception e) {
			System.exit(5);
		}

		/*
		 * since 'userInput' may be "CUrious SwIFtBot", it needs to be capitalized
		 * properly in order for it to be displayed on the console.
		 */
		userInput = userInput.toLowerCase();

		/*
		 * split user input "[mode] swiftbot" into two, that way it is easier to
		 * capitalize the individual.
		 */
		userInputMode = userInput.substring(0, userInput.indexOf(32) + 1);
		userInputSwiftbot = userInput.substring(userInput.indexOf(32) + 1, userInput.length());

		// replace the first letter in the strings, with its capital version
		userInputMode = userInputMode.replace(userInputMode.charAt(0), (char) ((int) userInputMode.charAt(0) - 32));
		userInputSwiftbot = userInputSwiftbot.replace(userInputSwiftbot.charAt(0),
				(char) ((int) userInputSwiftbot.charAt(0) - 32));

		userInput = userInputMode.concat(userInputSwiftbot);
		return userInput;
	}

	public static void writeTo() throws IOException, InterruptedException {
		FileWriter file = new FileWriter("/data/home/pi/" + currentDate + " " + currentTime);
		BufferedWriter bw = new BufferedWriter(file);

		bw.write("Last mode: ");
		bw.write(mode.getLastMode());
		bw.newLine();

		bw.write("Execution time: ");
		bw.write(Long.toString(executionTime));
		bw.write("ms");
		bw.newLine();

		bw.write("Objects found: ");
		bw.write(Integer.toString(mode.getObjectsFound()));
		bw.newLine();

		bw.write("File path to any images: ");
		bw.write("/data/home/pi");
		bw.newLine();

		bw.write("File path: ");
		bw.write("/data/home/pi");
		bw.newLine();

		System.out.println("Would you like to view the log file created? A for yes and B for no");
		System.out.println();

		bw.close();
		file.close();

	}

	public static void underlightColor(int r, int g, int b) throws InterruptedException {
		/*
		 * TODO: Completely changed the underlightColor method. The code works the same
		 * and is more compact if the method simply grabs the individual rgb values in
		 * the parameters and adds them to an array instead of running many if
		 * statements to check which rgb values were given in the parameter.
		 */

		int[] RGB = { r, g, b };

		try {

			swiftBot.fillUnderlights(RGB);
			Thread.sleep(300);

			swiftBot.disableUnderlights();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: Added error for when underlights fail to set up
			System.out.println("ERROR: failed to set all under lights");
			System.out.println();
			System.exit(5);
		}

	}

	public static void takeAndSavePhoto() {

		try {
			BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_720x720);

			if (image == null) {
				// TODO: Added error if image is not able to be took.
				System.out.println("ERROR: Image is null");
				System.out.println();
				System.exit(5);

			} else {
				// saving Image to a directory.
				ImageIO.write(image, "png", new File("/data/home/pi/Image.png"));
				/*
				 * TODO: Added success message to let the user know that the image was able to
				 * be taken.
				 */
				System.out.println("Image was taken successfully!");
				System.out.println();

				Thread.sleep(1000);
			}
		} catch (Exception e) {
			/*
			 * TODO: Added error messages to allow the user to troubleshoot any problems
			 * with the swiftbot taking photos.
			 */
			System.out.println("\nCamera not enabled!");
			System.out.println("Try running the following command: ");
			System.out.println("sudo raspi-config nonint do_camera 0\n");
			System.out.println("Then reboot using the following command: ");
			System.out.println("sudo reboot\n");
			System.out.println();
			System.exit(5);
		}
	}

}
