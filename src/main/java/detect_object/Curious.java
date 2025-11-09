package detect_object;

import swiftbot.SwiftBotAPI;

// different classes for different modes are help with organization in case the program scalable
public class Curious extends Mode {
	SwiftBotAPI swiftBot = ObjectDetection.getSwiftbot();

	/*
	 * TODO: Added 'dubiousModeIndicator' variable in order to make the program be
	 * able to recognize weather to write 'lastMode' as Dubious or the actual mode.
	 * dubiousModeIndicator makes it so the program is able to recognize weather to
	 * write 'lastMode' as Dubious or the actual mode.
	 */
	public Curious(boolean dubiousModeIndicator) throws InterruptedException {
		lastMode = (dubiousModeIndicator) ? "Dubious swiftbot" : "Curious swiftbot";

		/*
		 * since there is no option for the user to switch modes, once they enter a
		 * valid mode, the program will run indefinitely until they terminate it.
		 */
		while (valid) {
			/*
			 * this is the same try-catch as in the objectDetection class because this is
			 * going to be ran in a loop, and so every time the loop is ran, the program
			 * needs to make sure the the ultrasound returns a value.
			 */
			try {
				distance = swiftBot.useUltrasound();
				success = true;

			} catch (Exception e) {
				e.printStackTrace();
				success = false;
				Thread.sleep(1000);
				swiftBot.move(0, 50, 1000);
				swiftBot.move(50, 50, 1000);
				swiftBot.disableUnderlights();
			}

			ObjectDetection.underlightColor(0, 255, 0);
			// object is 30cm away
			if ((int) swiftBot.useUltrasound() == 30) {
				justInBuffer();
				// object is more than 30cm away
			} else if ((int) swiftBot.useUltrasound() > 30) {
				outBuffer();
				// object is less than 30cm away
			} else if ((int) swiftBot.useUltrasound() < 30) {
				inBuffer();

			}
		}
	}

	/*
	 * TODO: Added separate methods that command how the swiftbot responds to where
	 * the object is. This helps make the code more compact. methods are not public
	 * as they are not used outside of class.
	 */
	void justInBuffer() throws InterruptedException {
		objectsFound++;

		// blinking underlights
		for (int i = 0; i < 5; i++) {

			ObjectDetection.underlightColor(0, 255, 0);
			swiftBot.disableUnderlights();
			Thread.sleep(1000);
		}

		ObjectDetection.takeAndSavePhoto();
		Thread.sleep(5000);

	}

	public void outBuffer() throws InterruptedException {
		objectsFound++;
		rightWheelVelocity = 20;
		leftWheelVelocity = 20;

		swiftBot.move(rightWheelVelocity, leftWheelVelocity, 500);
		swiftBot.disableUnderlights();

	}

	void inBuffer() throws InterruptedException {
		objectsFound++;
		rightWheelVelocity = -20;
		leftWheelVelocity = -20;

		swiftBot.move(rightWheelVelocity, leftWheelVelocity, 500);
		swiftBot.disableUnderlights();

	}

}
