package detect_object;

import swiftbot.SwiftBotAPI;

public class Scaredy extends Mode {
	SwiftBotAPI swiftBot = ObjectDetection.getSwiftbot();
	long noObjectTimer;

	public Scaredy(boolean dubiousModeIndicator) throws InterruptedException {
		lastMode = (dubiousModeIndicator) ? "Dubious swiftbot" : "Scaredy swiftbot";
		noObjectTimer = System.currentTimeMillis() + 5_000;

		while (valid) {
			// if the swiftbot has not encountered anything for 5s, then do below.
			if (System.currentTimeMillis() > noObjectTimer) {
				outBuffer();
			}

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

			ObjectDetection.underlightColor(0, 0, 255);
			// object is 50cm away
			if ((int) swiftBot.useUltrasound() == 50) {
				justInBuffer();
			}
		}
	}

	/*
	 * TODO: Added separate methods that command how the swiftbot responds to where
	 * the object is. This helps make the code more compact. methods are not public
	 * as they are not used outside of class.
	 */
	void justInBuffer() throws InterruptedException {
		noObjectTimer = 0;
		objectsFound++;
		ObjectDetection.underlightColor(255, 0, 0);
		ObjectDetection.takeAndSavePhoto();

		for (int i = 0; i < 5; i++) {
			ObjectDetection.underlightColor(255, 0, 0);
			swiftBot.disableUnderlights();
			Thread.sleep(1000);
		}

		rightWheelVelocity = 20;
		leftWheelVelocity = 20;
		swiftBot.move(leftWheelVelocity, rightWheelVelocity, 3000);
		swiftBot.move(100, 0, 1000);
		rightWheelVelocity = -20;
		leftWheelVelocity = -20;
		ObjectDetection.underlightColor(255, 0, 0);
		swiftBot.move(leftWheelVelocity, rightWheelVelocity, 5000);
		swiftBot.disableUnderlights();
	}

	public void outBuffer() throws InterruptedException {
		noObjectTimer = System.currentTimeMillis() + 5_000;
		Thread.sleep(1000);
		swiftBot.move(0, 50, (random.nextInt(2) == 0) ? 1000 : 500);
		swiftBot.move(50, 50, 1000);
		swiftBot.disableUnderlights();

	}
}
