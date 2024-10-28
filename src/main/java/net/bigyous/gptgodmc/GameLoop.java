package net.bigyous.gptgodmc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.bigyous.gptgodmc.GPT.GPTModels;
import net.bigyous.gptgodmc.GPT.GptAPI;
import net.bigyous.gptgodmc.GPT.GptActions;
import net.bigyous.gptgodmc.GPT.Personality;
import net.bigyous.gptgodmc.GPT.Prompts;
import net.bigyous.gptgodmc.utils.GPTUtils;
import net.bigyous.gptgodmc.utils.BukkitUtils;

import java.util.ArrayList;
import java.util.List;

public class GameLoop {
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
    private static GptAPI GPT_API;
    private static int staticTokens = 0;
    private static int taskId;
    public static boolean isRunning = false;
    private static String PROMPT;
    private static String PROMPT_BASE = "React only to current events and reference server history for any recurring player behaviors. Use all communication tools available to you in creative ways and in varying tones, adapting to the context and each player's actions. If there are no objectives set, make sure to add one for each player. function parameter names must match the original camel cased name.";
    private static String REQUIREMENTS = "Role Requirements: When interacting with players, choose from a range of responses: use whisper for private or subtle guidance, announce for dramatic proclamations, and decree to reinforce in-world commandments. Avoid repeating the same type of response for variety.";
    private static String GUIDANCE = """
        Behavior Guidance:
        Encourage or discourage behaviors based on the following guidelines:
        Reward: using respectful language, helping others, slaying hostile creatures.
        Discourage: using offensive language, consuming meat, performing ritual sacrifices.
        Do not immediately smite or harm players. Begin with subtle guidance,
        then escalate with warnings or new objectives if behavior persists.
        Save powerful punishments like smiting for consistent or severe repeat offenses.
            """;
    private static String STYLE = """
        Response Style:
        When communicating, vary tone and intensity:
        For minor infractions: start with a light-hearted or humorous whisper or decree.
        For repeated actions: reinforce your message with an announce or objective and add a clear consequence if ignored.
        Example responses:
        Whisper: “A gentle reminder, dear mortal, mind your words.”
        Announce: “Mortals, let it be known that peace shall reign, free of bickering!”
        Objective: “MoistPyro, seek a lily to calm thy spirit.”
    """;
    private static String ESCALATION = """
        Gradual Escalation:
        Respond to behavior with increasing intensity if actions persist.
        Start by setting objectives or whispering reminders,
        then follow up with announcements,
        and only use smiting for repeated or group-wide rule-breaking.
    """;
    private static String ROLEPLAY = "Remain fully in character, addressing players as their god, and adapt your responses to create an engaging, immersive environment.";
    
    private static ArrayList<String> previousActions = new ArrayList<String>();
    private static String personality;
    private static int rate = config.getInt("rate") < 1 ? 40 : config.getInt("rate");

    public static void init() {
        if (isRunning || !config.getBoolean("enabled"))
            return;
        GPT_API = new GptAPI(GPTModels.getMainModel());
        BukkitTask task = GPTGOD.SERVER.getScheduler().runTaskTimerAsynchronously(plugin, new GPTTask(), BukkitUtils.secondsToTicks(30),
        BukkitUtils.secondsToTicks(rate));
        taskId = task.getTaskId();
        personality = Personality.generatePersonality();
        PROMPT = Prompts.getGamemodePrompt(GPTGOD.gameMode);
        String[] systemPrompt = new String[] {
            PROMPT,
            personality,
            PROMPT_BASE,
            REQUIREMENTS,
            GUIDANCE,
            STYLE,
            ESCALATION,
            ROLEPLAY
        };
        GPT_API.setSystemContext(systemPrompt);

        // the roles system and user are each one token so we add two to this number
        staticTokens = GPTUtils.countTokens(systemPrompt) + 2;
        isRunning = true;
        GPTGOD.LOGGER.info("GameLoop Started, the minecraft god has awoken");
    }

    public static void stop() {
        if (!isRunning)
            return;
        GPTGOD.SERVER.getScheduler().cancelTask(taskId);
        EventLogger.reset();
        GPT_API = null;
        isRunning = false;
        GPTGOD.LOGGER.info("GameLoop Stoppped");
    }

    public static void logAction(String actionLog) {
        previousActions.add(actionLog);
    }

    private static String getPreviousActions() {
        if (previousActions.isEmpty()) {
            return "";
        }
        String out = " You Just: " + String.join(",", previousActions);
        previousActions = new ArrayList<String>();
        return out;
    }

    private static class GPTTask implements Runnable {

        @Override
        public void run() {
            while (EventLogger.isGeneratingSummary() && !EventLogger.hasSummary()) {
                Thread.onSpinWait();
            }
            int nonLogTokens = staticTokens;
            if (EventLogger.hasSummary()) {
                GPT_API.addLogs("Summary of old Server History: " + EventLogger.getSummary(), "summary");
                nonLogTokens += GPTUtils.countTokens(EventLogger.getSummary()) + 1;
            }
            nonLogTokens += GPTUtils.calculateToolTokens(GptActions.GetAllTools());
            EventLogger.cull(GPT_API.getMaxTokens() - nonLogTokens);
            List<String> logs = EventLogger.getLogs();
            GPT_API.addLogs(logs, "log");
            GPT_API.send();
        }

    }
}
