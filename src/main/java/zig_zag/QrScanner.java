package zig_zag;
import swiftbot.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QrScanner {
    private SwiftBotAPI API;

    public QrScanner(SwiftBotAPI API) {
        this.API = API;
    }

    // Method to scan a QR code and return the decoded text
    public String scanQR() {
        System.out.println("Scanning for QR codes...");
        
        BufferedImage img = API.takeGrayscaleStill(ImageSize.SQUARE_240x240);
        try {
            ImageIO.write(img, "jpg", new File(""));
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        // Try to scan up to 60 times
        for (int i = 0; i < 60; i++) {
            try {
                BufferedImage img1 = API.getQRImage();  // Get the QR image
                String decodedText = API.decodeQRImage(img1);  // Decode the QR image

                if (!decodedText.isEmpty()) {
                    System.out.println("QR Code Found: " + decodedText);
                    return decodedText;  // Return the scanned QR code
                } else {
                    System.out.println("No QR Code detected...");
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            // Small delay to avoid spamming
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("No valid QR code found after 60 attempts.");
        return "";  // Return empty if no valid QR code was found
    }
}