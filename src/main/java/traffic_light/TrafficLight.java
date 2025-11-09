package traffic_light;
import swiftbot.*;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class TrafficLight {
	static SwiftBotAPI swiftBot;
	public static double objectDistance = 40;
	public static boolean start;
	public static String newColour = null;
	public static int numLights;
	public static int numRed = 0, numGreen = 0, numBlue = 0, numYellow = 0;
	public static String mostFreqColour;
	public static int highestFreq;
	public static Date timeDate;
	public static Long startTime = Calendar.getInstance().getTimeInMillis();
	public static Long endTime;
	public static double duration;
	public static String previousColour = null;
	public static Button buttonPressed = null;
	
	ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);
	
	public TrafficLight() {
		
		Runnable pic5Secs = () -> {
			if (start == true) {
				processPicture();
			}
		};
		
		Runnable terminateCheck = () -> {
			if (start == true) {
				swiftBot.enableButton(Button.X, () ->  terminate());
			}
		};
		
		schedule.scheduleAtFixedRate(pic5Secs, 0, 5, TimeUnit.SECONDS);
		schedule.scheduleAtFixedRate(terminateCheck, 0, 100, TimeUnit.MILLISECONDS);
		
	}	
		
	public static void main(String[] args) {
		new TrafficLight();
		swiftBot = new SwiftBotAPI();
		System.out.println("Press 'A' button to start.");
		swiftBot.enableButton(Button.A, () -> {try {
			start();
		} catch (Exception e) {
			if (start != false) {
				System.out.println("Failed to start program.");
				e.printStackTrace();
			}
		}
		});
	}
	
	public static void start() throws Exception {
		start = true;
		swiftBot.disableButton(Button.A);
		String trafficColour = "Yellow";
		while (start == true) {
			
			
			setUnderlights(trafficColour);
			move(trafficColour);
			previousColour = trafficColour;
			
			if (newColour != null) {
				if (objectDistance <= 30) {
					trafficColour = newColour;
					
					switch (newColour) {
					case "Red":
						numRed += 1;
						break;
					case "Green":
						numGreen += 1;
						break;
					case "Blue":
						numBlue += 1;
						break;
					case "Yellow":
						numYellow += 1;
						break;
					}
					
					numLights += 1;
					newColour = null;
				}
			} else {
				trafficColour = "Yellow";
			}
		}
		
	}
	
	
	public static void setUnderlights(String colour) {
		int red[] = {255, 0, 0};
		int green[] = {0, 255, 0};
		int blue[] = {0, 0, 255};
		int yellow[] = {255, 255, 0};
		int off[] = {0, 0, 0};
		
		switch (colour) {
			case "Yellow":
				swiftBot.fillUnderlights(yellow);;
				break;		
			case "Blue":
				swiftBot.fillUnderlights(blue);;
				break;	
			case "Red":
				swiftBot.fillUnderlights(red);
				break;
			case "Green":
				swiftBot.fillUnderlights(green);
				break;	
			case "Off":
				swiftBot.fillUnderlights(off);
				break;
		}
	}
	
	
	public static void move(String colour) throws Exception {
		double speed = 27;
		double distanceToMove;
		double speedPercentValue;
		switch (colour) {
			case "Yellow":
				if (previousColour == null || !previousColour.equals("Yellow")) {
					System.out.println("Slow");
					swiftBot.startMove(40, 40);
				}
				break;
			case "Blue":
				swiftBot.stopMove();
				System.out.println("Turning");
				swiftBot.move(0, 0, 1000);
				setUnderlights("Off");
				swiftBot.move(0,  0, 1000);
				swiftBot.move(0, 100, 1000);
				swiftBot.move(0, 0, 1000);
				swiftBot.move(40, 40, 1000);
				swiftBot.move(0, 0, 1000);
				swiftBot.move(-40, -40, 1000);
				swiftBot.move(0, 0, 1000);
				swiftBot.move(0, -100, 1000);
				break;
			case "Green":
				swiftBot.stopMove();
				System.out.println("Speeding");
				swiftBot.move(100,  100, 2000);
				Thread.sleep(1000);
				break;
			case "Red":
				distanceToMove = objectDistance - 5;
				speedPercentValue = speed * 0.4;
				int timeToMove = (int) (distanceToMove * 1000 / speedPercentValue);
				System.out.println("Stopping");
				swiftBot.stopMove();
				swiftBot.move(40,  40, timeToMove);
				swiftBot.stopMove();
				Thread.sleep(1000);
				break;
		}
		
	}
	
	
	public static BufferedImage picture() {
		BufferedImage pic = swiftBot.takeStill(ImageSize.SQUARE_48x48);
		objectDistance = swiftBot.useUltrasound();
		System.out.println("Taking picture");
		return pic;
	}
	
	
	public static Color processColour() {
		int height = 48;
		int width = 48;
		int imageSize = width * height;
		int redSum = 0, greenSum = 0, blueSum = 0;
		BufferedImage pic = picture();
		
		for (int x = 0; x < height; x ++) {
			for (int y = 0; y < width; y ++) {
				Color pixelColour = new Color(pic.getRGB(x, y));
				
				redSum += pixelColour.getRed();
				greenSum += pixelColour.getGreen();
				blueSum += pixelColour.getBlue();
			}
		}
		
		int red = (int) redSum / imageSize;
		int green = (int) greenSum / imageSize;
		int blue = (int) blueSum / imageSize;
		return new Color(red, green, blue);
	}
	
	
	public static void processPicture() {
		Color imageColour = processColour();
		
		String colour = null;
		
		int red = imageColour.getRed();
		int green = imageColour.getGreen();
		int blue = imageColour.getBlue();
		
		if (red > green + 25 && red > blue + 25) {
			colour = "Red";
		} else if (green > blue + 25 && green > red + 25) {
			colour = "Green";
		} else if (blue > red + 25 && blue > green + 25) {
			colour = "Blue";
		} else if (red > blue + 50 && green > blue + 50 && red < green + 40 && green < red + 40) {
			colour = "Yellow";
		}
		
		newColour = colour;
		if (colour!= null) {
			System.out.println(colour + " traffic light detected.");
		} else {
			System.out.println("No valid colour detected.");
		}
	}
	
	
	public static void displayLog() {
		System.out.println("Displaying execution log...");
		System.out.println(("Duration of program: " + duration + " seconds"));
		System.out.println(("Number of traffic lights detected:" + numLights));
		System.out.println(("Most frequent colour detected: " + mostFreqColour));
		System.out.println(("The most frequent colour was detected " + highestFreq + " times."));
	}
	
	
	public static void writeLog() {
		Calendar time = Calendar.getInstance();
		timeDate = time.getTime();
		SimpleDateFormat parseFormat =new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String formattedDate = parseFormat.format(timeDate);
		File execLog = createLogFile(formattedDate);
		if (execLog == null) {
			return;
		}
		
		try {
			FileWriter writer = new FileWriter("ExecutionLog" + formattedDate +".txt");
			writer.write("Duration of program: " + duration + " seconds\n");
			writer.write("Number of traffic lights detected:" + numLights + "\n");
			writer.write("Most frequent colour detected: " + mostFreqColour + "\n");
			writer.write("The most frequent colour was detected " + highestFreq + " times.\n");
			writer.close();
			System.out.println("Execution log written to log file successfully");
		} catch (IOException e) {
			System.out.println("An error occurred whilst writing to the log file.");
		}
		
	}
	
	
	public static File createLogFile(String timeValue) {
		System.out.println("Creating log file.");
		try {
			File execLog = new File("ExecutionLog" + timeValue + ".txt");
			if (execLog.createNewFile()) {
				System.out.println("Log file created.\nFile location: " + execLog.getAbsolutePath());
				return execLog;
			}
		} catch (IOException e) {
			System.out.println("An error occurred whilst creating the log file.");
			}
		return null;
	}
	
	
	public static void setColourFrequency() {
		ArrayList<ArrayList<Object>> freqArray = new ArrayList<>();
		for (int x = 1; x <= 4; x ++) {
			freqArray.add(new ArrayList<>());
		}
		freqArray.get(0).add("Red");
		freqArray.get(0).add(numRed);
		freqArray.get(1).add("Yellow");
		freqArray.get(1).add(numYellow);
		freqArray.get(2).add("Green");
		freqArray.get(2).add(numGreen);
		freqArray.get(3).add("Blue");
		freqArray.get(3).add(numBlue);
		
		String highest = null;
		
		int freq = 0;
		
		for (int x = 0; x <= 3; x ++) {
			if ((int) freqArray.get(x).get(1) > freq) {
				freq = (int) freqArray.get(x).get(1);
				highest = (String) freqArray.get(x).get(0);
			} else if ((int) freqArray.get(x).get(1) == freq) {
				if (freq > 0) {
					highest += ", " + (String) freqArray.get(x).get(0);
				}
			}
		}
		
		mostFreqColour = highest;
		highestFreq = freq;
	}
		
	public static void terminate() {
		start = false;
		swiftBot.disableAllButtons();
		swiftBot.stopMove();
		swiftBot.disableUnderlights();
		endTime = Calendar.getInstance().getTimeInMillis();
		duration = Math.round((endTime - startTime) / 1000);
		System.out.println("Terminating...");
		setColourFrequency();
		
		System.out.println("Press button Y to display execution log. Press button X skip.");
		
		
		do {
			swiftBot.enableButton(Button.Y, () -> {
				buttonPressed = Button.Y;
				swiftBot.disableAllButtons();
				displayLog();});
			swiftBot.enableButton(Button.X, () -> {
				swiftBot.disableAllButtons();
				buttonPressed = Button.X;});
			try {
				Thread.sleep(100);
				swiftBot.disableAllButtons();
			} catch (InterruptedException e) {
				
			}
		} while (buttonPressed == null);
		
		
		writeLog();
		
		System.exit(0);
	}
	
}