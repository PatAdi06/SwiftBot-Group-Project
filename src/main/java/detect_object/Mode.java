package detect_object;

import java.util.Random;

import swiftbot.SwiftBotAPI;

// TODO: Added a mode class that contains most variables that are used in between all classes.
public class Mode {
	SwiftBotAPI swiftBot = ObjectDetection.getSwiftbot();
	/*
	 * no getter or setter for random bc it will never be used outside of a
	 * inhereted class
	 */
	protected Random random = new Random();
	protected String mode = "";
	protected String lastMode = "";
	protected static int objectsFound = 0;
	protected int rightWheelVelocity = 0;
	protected int leftWheelVelocity = 0;
	protected double distance;
	protected boolean success = false;
	protected boolean valid = true;

	public String getMode() {
		return this.mode;
	}

	public void setMode(String newMode) {
		this.mode = newMode;
	}

	public String getLastMode() {
		return this.lastMode;
	}

	public void setLastMode(String newLastMode) {
		this.lastMode = newLastMode;
	}

	public Boolean getSuccess() {
		return this.success;
	}

	public void setSuccess(Boolean newValid) {
		this.success = newValid;
	}

	public Boolean getValid() {
		return this.valid;
	}

	public void setValid(Boolean newValid) {
		this.valid = newValid;
	}

	public int getObjectsFound() {
		return Mode.objectsFound;
	}

	/*
	 * function for when the object is out of the swiftbot's buffer zone. When no
	 * mode is slected, and an object is unable to be detected (i.e: out of the
	 * buffer zone), then swiftbot will wonder around.
	 */
	public void outBuffer() throws InterruptedException {
		try {

			ObjectDetection.underlightColor(0, 0, 255);

			// while the ultrasound returns a numeric value
			while (!success && valid) {

				try {
					distance = swiftBot.useUltrasound();
					success = true;
					Thread.sleep(1000);

				} catch (Exception e) {
					e.printStackTrace();
					success = false;
					// wonders in a different direction
					/*
					 * TODO: Added randomness to where the swiftbot turns to when it has not found
					 * an object
					 */
					swiftBot.move(0, 50, (random.nextInt(2) == 0) ? 1000 : 500);
					rightWheelVelocity = 30;
					leftWheelVelocity = 30;
					swiftBot.move(leftWheelVelocity, rightWheelVelocity, 1000);

				}

			}
		} catch (Exception e) {

		}

	}
}
