package net.bigyous.gptgodmc.GPT;

import com.google.gson.JsonObject;

import net.bigyous.gptgodmc.EventLogger;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.FunctionDeclaration;
import net.bigyous.gptgodmc.GPT.Json.Schema;
import net.bigyous.gptgodmc.GPT.Json.Tool;
import net.bigyous.gptgodmc.interfaces.SimpFunction;

import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SummarizeLogs {
        private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
        private static String DEFAULT_CONTEXT = """
                        You are a helpful assistant that will recieve a log of events from a minecraft server, \
                        or a historical summary and a log of events. \
                        You will create a short summary based on this information that preserves the plot detailed by both, you are viewing these logs from the perspective of a god that rewards %s and punishes %s \
                        Keep track of the reputation of each player, if information in the logs isn't important to the plot omit it. Do not add any extra flourishes, just state the facts, pay attention to actions that align with any objectives listed in the objectives and promises that god makes to the players.
                        These logs are the history of the server so keep everything in the past tense.
                        """;
        private static SimpFunction<JsonObject> submitSummary = (JsonObject argObject) -> {

                // JsonObject argObject = JsonParser.parseString(args).getAsJsonObject();

                GPTGOD.LOGGER.info("summary submitted with args: " + argObject.toString());

                String summary = argObject.get("summary").getAsString();

                EventLogger.setSummary(summary);
        };
        private static Map<String, FunctionDeclaration> functionMap = Map.of("submitSummary", new FunctionDeclaration(
                        "submitSummary", "input the summary, keep the summary below 1000 tokens",
                        new Schema(Map.of("summary", new Schema(Schema.Type.STRING, "the summary"))), submitSummary));
        private static Tool tools = GptActions.wrapFunctions(functionMap);
        private static String getOverrideOrDefault(String key, String def) {
                if (config.isSet(key)) {
                        String v = config.getString(key);
                        if (v != null && !v.isBlank()) return v;
                }
                return def;
        }

        private static String CONTEXT_TEMPLATE = getOverrideOrDefault("prompts.summarize.CONTEXT", DEFAULT_CONTEXT);

        private static GptAPI gpt = new GptAPI(GPTModels.getSecondaryModel(), tools)
                        .setSystemContext(String.format(CONTEXT_TEMPLATE, String.join(",", Personality.getLikes()),
                                        String.join(",", Personality.getDislikes())))
                        .setToolChoice("submitSummary");

        public static void summarize(String log, String summary) {
                String content = String.format("Write a short summary that summarizes the events of these logs: %s%s",
                                log, summary != null ? String.format(":and this History Summary %s", summary) : "");
                gpt.addLogs(content, "logs").send(functionMap);
        }
}
