package net.bigyous.gptgodmc.cameraitem;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.utils.ImageUtils;
import net.md_5.bungee.api.ChatColor;

// listen for item uses to trigger camera
public class CameraItemListener implements Listener {

    // hard coded camera ratelimit for now
    // might add this to config file later
    static final int MIN_PHOTO_DELY_SECONDS = 5;
    // rate limiter
    Map<UUID, Instant> lastPlayerPhoto = new HashMap<>();

    @EventHandler
    public void onClick(final PlayerInteractEvent event) {
        // only accept right click if primary hand
        // 1.9+ fires once for left hand and once for right hand
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        Player player = event.getPlayer();
        Instant lastTime = lastPlayerPhoto.get(player.getUniqueId());
        Instant currentTime = Instant.now();
        if (lastTime == null || Duration.between(lastTime, currentTime).getSeconds() > MIN_PHOTO_DELY_SECONDS) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            // only run on valid camera items (has hidden meta)
            if (!GiveCameraCommand.isItemValidCamera(hand))
                return;
            GPTGOD.LOGGER.info(String.format("%s is trying to take a picture with the camera item", player.getName()));
            // snap a picture from the current players view
            ImageUtils.takePicture(player);
            player.sendMessage("Snapped a photo for god");
            // set the last time a photo was taken
            lastTime = Instant.now();
        } else {
            player.sendMessage(String.format("You may only take a picture every %d seconds.", MIN_PHOTO_DELY_SECONDS)
                    + ChatColor.RED);
        }
    }
}
