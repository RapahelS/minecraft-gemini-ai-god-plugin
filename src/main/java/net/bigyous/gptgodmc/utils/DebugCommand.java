package net.bigyous.gptgodmc.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.GptActions;

public class DebugCommand implements CommandExecutor {

    Gson gson = new Gson();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage("Use the Server console to use this command");
            return false;
        }

        String commandName = args[0];
        String jsonArgs = String.join(" ", ArrayUtils.subarray(args, 1, args.length));

        GPTGOD.LOGGER.info("trying command " + commandName + " with args " + jsonArgs);
        try {
            GptActions.run(commandName, new Gson().fromJson(jsonArgs, JsonObject.class));
        } catch (JsonSyntaxException e) {
            GPTGOD.LOGGER.error("syntax error in json args " + jsonArgs, e);
        } catch (JsonParseException e) {
            GPTGOD.LOGGER.error("parse error in json args " + jsonArgs, e);
        }
        return true;
    }

}
