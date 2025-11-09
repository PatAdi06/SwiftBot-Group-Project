package detect_object;

import swiftbot.SwiftBotAPI;

public class Dubious extends Mode {
	SwiftBotAPI swiftBot = ObjectDetection.getSwiftbot();

	public Dubious() throws InterruptedException {
		lastMode = "Dubious swiftbot";

		while (valid) {
			/*
			 * TODO: Added Random() instead of math.random() because math.random() would
			 * give me decimal values between 0 and 1, which are not needed since I only
			 * needed only the number 0 or 1.
			 */
			int rand = random.nextInt(2);

			// if 1, operate in curious mode
			if (rand == 1) {
				Curious curiousMode = new Curious(true);
				// if 0, operate in scaredy mode
			} else if (rand == 0) {
				Scaredy scaradeyMode = new Scaredy(true);
			}

		}

	}

}
