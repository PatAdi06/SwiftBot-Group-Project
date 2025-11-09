package Navigate;

import swiftbot.*;
import java.awt.image.BufferedImage;

// This class is responsible for scanning for a QR code using the robot camera
// It repeatedly captures an image and attempts to decode a QR code within a ten second time limit
// If a QR code is found the decoded message is returned and a success message is printed
// If no QR code is found before the timeout the method returns null
// The class also respects the paused state by waiting when the system is paused and catches any exceptions that occur during scanning
public class QRScanner {
    // This variable holds the robot API instance used for capturing images and decoding QR codes
    private SwiftBotAPI swiftBot;

    // The constructor accepts a robot API instance and saves it in the object
    public QRScanner(SwiftBotAPI swiftBot) {
        this.swiftBot = swiftBot;
    }

    // This method captures an image from the robot camera and attempts to decode a QR code within a ten second window
    // If a QR code is successfully decoded a success message is printed and the decoded message is returned
    // If no QR code is found or an error occurs the method returns null
    public String captureImage() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 10000; // ten second timeout

        try {
            while (System.currentTimeMillis() < endTime && Navigate.running) {
                // Wait if the system is paused
                while (Navigate.paused.get()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(Navigate.ANSI_RED + "ERROR: Pause interrupted." + Navigate.ANSI_RESET);
                    }
                }

                BufferedImage img = swiftBot.getQRImage();
                if (img == null) {
                    System.out.println(Navigate.ANSI_RED + "ERROR: No image captured. Ensure the camera is connected." + Navigate.ANSI_RESET);
                    continue;
                }

                String decodedMessage = swiftBot.decodeQRImage(img);
                if (decodedMessage != null && !decodedMessage.isEmpty()) {
                    System.out.println(Navigate.ANSI_GREEN + "SUCCESS: QR code found - " + decodedMessage + Navigate.ANSI_RESET);
                    return decodedMessage;
                }
            }
        } catch (Exception e) {
            System.out.println(Navigate.ANSI_RED + "ERROR: Unable to scan for code." + Navigate.ANSI_RESET);
            e.printStackTrace();
        }
        return null;
    }
}