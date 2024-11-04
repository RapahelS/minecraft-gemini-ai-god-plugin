package net.bigyous.gptgodmc.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import dev.jensderuiter.minecraft_imagery.image.ImageCapture;
import net.bigyous.gptgodmc.GPTGOD;
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
            GPTGOD.LOGGER.warn(
                    String.format("An IO Exception occured getting output stream for: %s", title));
            return null;
        }
    }

    public static void takePicture(Player player) {
        ImageCapture capture = new ImageCapture(player.getEyeLocation());

        // capture asynchronously has it may run for a while
        new BukkitRunnable() {
            @Override
            public void run() {
                BufferedImage img = capture.render();

                // get bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // for testing:
                OutputStream imgOut = getImageOutputStream("eyephoto", fileIdx++);
                try {
                    ImageIO.write(img, "png", baos);
                    // output to file for testing
                    ImageIO.write(img, "png", imgOut);
                } catch (IOException e) {
                    GPTGOD.LOGGER.error("failed to output image bytes on capture", e);
                    return;
                }
                // raw png image bytes
                byte[] bytes = baos.toByteArray();
                GoogleFile upload = new GoogleFile(bytes, "image/png", "MINECRAFT_PICTURE");
                if (upload.tryUpload()) {
                    // todo add result to gemini vision queue
                }
            }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(GPTGOD.class));
    }
}
