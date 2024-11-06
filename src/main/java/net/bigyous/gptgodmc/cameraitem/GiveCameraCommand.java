package net.bigyous.gptgodmc.cameraitem;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.bigyous.gptgodmc.GPTGOD;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class GiveCameraCommand implements CommandExecutor {

    static final Component cameraDisplayNameComponent = Component.text("GOD CAMERA").color(TextColor.color(98, 66, 83))
            .decorate(TextDecoration.BOLD);

    static final List<Component> cameraLore = Arrays.asList(Component.text("Take pictures for god to see!").color(TextColor.color(32, 66, 83)).decorate(TextDecoration.BOLD));

    static ItemStack cameraItemDef = null;

    public static final NamespacedKey godCameraKey = NamespacedKey.fromString("godcamera", JavaPlugin.getPlugin(GPTGOD.class));

    // generate camera item def only once
    private ItemStack getCameraItem() {
        if (cameraItemDef == null) {
            cameraItemDef = new ItemStack(Material.SHULKER_SHELL, 1);
            ItemMeta meta = cameraItemDef.getItemMeta();
            // meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // if we decide to make hidden attributes
            meta.displayName(cameraDisplayNameComponent);
            meta.getPersistentDataContainer().set(godCameraKey, PersistentDataType.BOOLEAN, true);
            meta.lore(cameraLore);
            cameraItemDef.setItemMeta(meta);
        }
        return cameraItemDef;
    }

    public static boolean isItemValidCamera(ItemStack item) {
        Boolean data = item.getItemMeta().getPersistentDataContainer().get(godCameraKey, PersistentDataType.BOOLEAN);
        return data != null && data.booleanValue();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;

        // get the camera item def
        ItemStack newStack = getCameraItem();

        // get a reference to the players inventory
        PlayerInventory i = p.getInventory();

        // Add camera to inventory if there is not already one in there

        if(i.contains(newStack)) {
            sender.sendMessage("you already have a camera!");
            return false;
        }

        i.addItem(newStack);

        sender.sendMessage("Added a camera to your inventory.");

        return true;
    }

}
