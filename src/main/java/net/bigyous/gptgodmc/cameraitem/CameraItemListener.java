package net.bigyous.gptgodmc.cameraitem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.utils.ImageUtils;

// listen for item uses to trigger camera
public class CameraItemListener implements Listener {
    
    @EventHandler
    public void onClick(final PlayerInteractEvent event) {
        // only accept right click if primary hand
        // 1.9+ fires once for left hand and once for right hand
        if(event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        // only run on valid camera items (has hidden meta)
        if(!GiveCameraCommand.isItemValidCamera(hand)) return;

        GPTGOD.LOGGER.info(String.format("%s is trying to take a picture with the camera item", player.getName()));

        // snap a picture from the current players view
        ImageUtils.takePicture(player);
    }
}
