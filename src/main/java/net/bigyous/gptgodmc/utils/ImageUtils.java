package net.bigyous.gptgodmc.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import dev.jensderuiter.minecraft_imagery.image.ImageCapture;
import dev.jensderuiter.minecraft_imagery.image.ImageCaptureOptions;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.Structure;
import net.bigyous.gptgodmc.GPT.GoogleFile;

public class ImageUtils {

    private static int fileIdx = 0;

    public static Path IMAGE_DATA = JavaPlugin.getPlugin(GPTGOD.class).getDataFolder().toPath()
            .resolve("ai_image_data");

    public static Path getImageFile(String title, int fileNumber) {

        return IMAGE_DATA.resolve(String.format("%s/%d.png", title, fileNumber));
    }

    public static OutputStream getImageOutputStream(String title, int fileNumber) {
        Path imageFile = getImageFile(title, fileNumber);
        try {
            Files.createDirectories(imageFile.getParent());
            return Files.newOutputStream(imageFile);
        } catch (IOException e) {
            GPTGOD.LOGGER.warn(String.format("An IO Exception occured getting output stream for: %s", title));
            return null;
        }
    }

    // takes a picture from the given camera location
    public static void takePicture(Location cameraLocation, String pictureName) {
        ImageCapture capture = new ImageCapture(cameraLocation, ImageCaptureOptions.builder().fov(1).build());

        // capture asynchronously as it may run for a while
        new BukkitRunnable() {
            @Override
            public void run() {
                BufferedImage img = capture.render();

                // get bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(img, "png", baos);
                    // output to file for testing
                    ImageIO.write(img, "png", getImageOutputStream("eyephoto", fileIdx++));
                } catch (IOException e) {
                    GPTGOD.LOGGER.error("failed to output image bytes on capture", e);
                    return;
                }
                // raw png image bytes
                byte[] bytes = baos.toByteArray();
                GoogleFile upload = new GoogleFile(bytes, "image/png", pictureName);
                if (upload.tryUpload()) {
                    // todo add result to gemini vision queue
                }
            }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(GPTGOD.class));
    }

    // same as takePicture(Location) but with a default picture name
    public static void takePicture(Location location) {
        takePicture(location, "MINECRAFT_PICTURE");
    }

    // takes a picture through the eyes of the provided player
    public static void takePicture(Player player) {
        takePicture(player.getEyeLocation());
    }

    // takes a picture of the given structure
    public static void takePicture(Structure structure) {
        Location structureCenter = structure.getLocation();
        double[] cameraDirectionAxisUp = { 0, 1, 0};
        double cameraDistance = calculateCameraDistance(structure);
        Location cameraLocation = lookAt(structureCenter, cameraDirectionAxisUp, cameraDistance);
        takePicture(cameraLocation);
    }

    // calculates how far the camera has to be from a structure at a specified fov
    // for all of it to fit in the view
    private static double calculateCameraDistance(Structure structure) {
        return 0; // TODO
    }

    // takes in the location for the camera to look at
    // a normalized directional vector for the axis to move the camera along compared to the target
    // and how far (magnitude) the camera should move along this axis from the look target
    // returns the computed Location for our camera with the included look direction
    private static Location lookAt(Location target, double[] cameraDirection, double cameraDistance) {
        return null; // TODO
    }
}
