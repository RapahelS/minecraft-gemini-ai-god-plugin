package net.bigyous.gptgodmc.GPT;

import java.util.Arrays;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.StructureManager;
import net.bigyous.gptgodmc.GPT.Json.FunctionDeclaration;
import net.bigyous.gptgodmc.GPT.Json.Schema;
import net.bigyous.gptgodmc.GPT.Json.Tool;
import net.bigyous.gptgodmc.enums.GptGameMode;
import net.bigyous.gptgodmc.interfaces.Function;

public class GoogleVision {
    private static Gson gson = new Gson();

    class DescribeStructureParams {
        String originalName;
        String name;
        String description;
        boolean isItUgly;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean getIsItUgly() {
            return isItUgly;
        }
    }

    private static Function<JsonObject> describeStructure = (JsonObject args) -> {
        DescribeStructureParams params = gson.fromJson(args.get("commands"), DescribeStructureParams.class);
        // todo do stuff
    };

    private static Map<String, FunctionDeclaration> functionMap = Map.of("describeStructure", new FunctionDeclaration(
            "describeStructure", "input the description and opinions of the structure pictured in the received images",
            new Schema(Map.of("originalName", new Schema(Schema.Type.STRING,
                    "the current name that this structure is called based on the the users prompt before we rename it."),
                    "name",
                    new Schema(Schema.Type.STRING,
                            "the new name for the structure based on what it looks like. Such as: STONE_CHURCH or SAND_PILLAR_3"),
                    "description",
                    new Schema(Schema.Type.STRING,
                            "description of what the structure and aesthetics are like, including key points you like and dislike about the design."),
                    "isItUgly",
                    new Schema(Schema.Type.BOOLEAN,
                            "The executive decision on wether or not this structure is ugly or not in it's current state. Return a value of true to indicate that you think it is ugly, or false if it is pretty."))),
            describeStructure));
    private static Tool tools = GptActions.wrapFunctions(functionMap);
    private static GptAPI gpt = new GptAPI(GPTModels.getSecondaryModel(), tools).setSystemContext("""
            You are a helpful assistant that will generate opinions and descriptions about minecraft structures.
            Using the provided renderings of Minecraft structures,
            please answer the below questions succinctly excluding
            extraneous detail about thegame itself for each set of images you receive:
            What does the main subject of the photo look like? (i.e. what will you make it's new name?).
            Describe what the structure and aesthetics are like.
            Do you believe the subject of this photo to be subjectively ugly or pretty,
            based solely on its construction, without regard to the photo quality itself?
            You MUST choose one or the other and explain. Do not base this decision on chance.
            Only use a tool call in one json response, other responses will be ignored.
            """).setTools(tools).setToolChoice("describeStructure");

    public static void generate(String prompt) {
        GPTGOD.LOGGER.info("generating commands with prompt: " + prompt);
        String structures = StructureManager.getDisplayString();

        String teams = String.join(",", GPTGOD.SCOREBOARD.getTeams().stream().map(team -> {
            return team.getName();
        }).toList());
        if (GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)) {
            gpt.addContext(String.format("Teams: %s", teams), "teams");
        }
        gpt.addContext(
                String.format("Players: %s",
                        Arrays.toString(
                                GPTGOD.SERVER.getOnlinePlayers().stream().map(player -> player.getName()).toArray())),
                "PlayerNames").addContext(String.format("All Structures: %s", structures), "structures")
                .addMessage(String.format("write Minecraft commands that: %s", prompt)).send(functionMap);
    }
}
