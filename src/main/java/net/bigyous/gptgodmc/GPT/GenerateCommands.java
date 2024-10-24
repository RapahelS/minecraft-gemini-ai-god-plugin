package net.bigyous.gptgodmc.GPT;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.StructureManager;
import net.bigyous.gptgodmc.GPT.Json.FunctionDeclaration;
import net.bigyous.gptgodmc.GPT.Json.Schema;
import net.bigyous.gptgodmc.GPT.Json.Tool;
import net.bigyous.gptgodmc.enums.GptGameMode;
import net.bigyous.gptgodmc.interfaces.Function;

import java.util.Arrays;
import java.util.Map;

public class GenerateCommands {
        private static Gson gson = new Gson();
        private static Function<JsonObject> inputCommands = (JsonObject args) -> {
                String[] commands = gson.fromJson(args.get("commands"), String[].class);
                GptActions.executeCommands(commands);
        };

        private static Map<String, FunctionDeclaration> functionMap = Map.of("inputCommands",
                        new FunctionDeclaration("inputCommands", "input the minecraft commands to be executed",
                                        new Schema(Map.of("commands", new Schema(Schema.Type.ARRAY,
                                                        "list of minecraft commands, each entry in the list is an individual command",
                                                        Schema.Type.STRING))),
                                        inputCommands));
        private static Tool[] tools = GptActions.wrapFunctions(functionMap);
        private static GptAPI gpt = new GptAPI(GPTModels.getSecondaryModel(), tools)
                        .setSystemContext(
                                        """
                                                        You are a helpful assistant that will generate \
                                                        one or more minecraft java edition commands based on a prompt inputted by the user, \
                                                        even if the prompt seems impossible in minecraft try to approximate it as close as possible \
                                                        with functioning minecraft commands. A wrong answer is better than no answer. \
                                                        The commands must be compatible with minecraft. \
                                                        There must always be at least one command in the response. \
                                                        If the description calls for multiple events then make multiple commands but try not to go far past 24. \
                                                        Try to offset dangerous spawns from the exact player position. \
                                                        Make sure that title text displays fit in the screen. \
                                                        Ensure that positionaly dependent code is executed relative to the specific player. \
                                                        Only use a tool call in one json response, other responses will be ignored. \
                                                        You MUST respond with a command. \
                                                        Do not use any escape characters (slashes) in your response; The response must be both valid json and a valid minecraft command. \
                                                        Example command to communicate something physically in the world: "execute at PlayerNameHere run summon armor_stand ~ ~1 ~ {Invisible:1b,Invulnerable:1b,NoGravity:1b,Marker:1b,CustomName:'{"text":"thou shalt not commit friendship","color":"red","bold":true,"italic":true,"strikethrough":false,"underlined":true}',CustomNameVisible:1b}". \
                                                        Do not use item frames with books to display text.
                                                        """)
                        .setToolChoice("inputCommands");

        public static void generate(String prompt) {
                GPTGOD.LOGGER.info("generating commands with prompt: " + prompt);
                String structures = Arrays.toString(StructureManager.getStructures().stream().map((String key) -> {
                        return String.format("%s: (%s)", key,
                                        StructureManager.getStructure(key).getLocation().toVector().toString());
                }).toArray());

                String teams = String.join(",", GPTGOD.SCOREBOARD.getTeams().stream().map(team -> {
                        return team.getName();
                }).toList());
                if (GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)) {
                        gpt.addContext(String.format("Teams: %s", teams), "teams");
                }
                gpt.addContext(
                                String.format("Players: %s",
                                                Arrays.toString(
                                                                GPTGOD.SERVER.getOnlinePlayers().stream()
                                                                                .map(player -> player.getName())
                                                                                .toArray())),
                                "PlayerNames")
                                .addContext(String.format("Structures: %s", structures), "structures")
                                .addLogs(String.format("write Minecraft commands that: %s", prompt), "prompt")
                                .send(functionMap);
        }
}
