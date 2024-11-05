package net.bigyous.gptgodmc.GPT;

import java.util.Arrays;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.bigyous.gptgodmc.EventLogger;
import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.StructureManager;
import net.bigyous.gptgodmc.GPT.Json.FunctionDeclaration;
import net.bigyous.gptgodmc.GPT.Json.Schema;
import net.bigyous.gptgodmc.GPT.Json.Tool;
import net.bigyous.gptgodmc.enums.GptGameMode;
import net.bigyous.gptgodmc.interfaces.Function;
import net.bigyous.gptgodmc.loggables.CameraEventLoggable;

// Let the AI god see the structures we build,
// so that when we build a phalic monument they are wise to it,
// and we may be smited >:D
public class GoogleVision {
    private static Gson gson = new Gson();

    // the parameters that the vision model returns
    class DescribeStructureParams {
        protected String originalName;
        private String newName;
        private String description;
        private String photographer;
        private boolean isItUgly;
        // just in case the ai decides to be dumb af
        private boolean is_it_ugly;

        public String getOriginalName() {
            return originalName;
        }

        public String getNewName() {
            return newName;
        }

        public String getDescription() {
            return description;
        }

        public String getPhotographer() {
            return photographer;
        }

        public boolean getIsItUgly() {
            return isItUgly || is_it_ugly;
        }
    }

    // // for the task queue
    // class DescribeStructureRequest {
    // // the name that the structure currently has (the ai might choose to rename
    // it)
    // String currentStructureName;
    // }

    // this would run sequentially 🤔
    // not sure if I should prefer this approach (for rate limit purposes)
    // or the async callback approach so that multiple requests may run
    // private static TaskQueue<DescribeStructureRequest> describeStructureQueue =
    // new TaskQueue<>((DescribeStructureRequest buffer) -> {});

    // handles the results when gemini vision finishes processing
    private static Function<JsonObject> describeStructure = (JsonObject args) -> {
        DescribeStructureParams params = gson.fromJson(args.get("commands"), DescribeStructureParams.class);

        if (params.originalName == null || params.originalName.length() < 1 || params.newName == null
                || params.newName.length() < 1 || params.description == null || params.description.length() < 1) {
            GPTGOD.LOGGER.error("describeStructure response had invalid args " + args.toString());
        }

        StructureManager.updateStructureDetails(params.originalName, params.newName, params.description,
                params.getIsItUgly());
        EventLogger.addLoggable(
                new CameraEventLoggable(params.getNewName(), params.getDescription(), params.getIsItUgly()));
    };

    private static Map<String, FunctionDeclaration> functionMap = Map.of("describeStructure", new FunctionDeclaration(
            "describeStructure", "input the description and opinions of the structure pictured in the received images",
            new Schema(Map.of("originalName", new Schema(Schema.Type.STRING,
                    "the current name that this structure is called based on the the users prompt before we rename it."),
                    "newName",
                    new Schema(Schema.Type.STRING,
                            "the new name for the structure based on what it looks like. Such as: STONE_CHURCH or SAND_PILLAR_3"),
                    "photographer",
                    new Schema(Schema.Type.STRING,
                            "the name of whoever took the photo (either God, or one of the players)"),
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

    // have the ai model rate a structure with multiple different camera angles
    // available to it
    public static void lookAtPhoto(String photographer, String photoSubject, GoogleFile[] imageFiles) {
        GPTGOD.LOGGER.info("generating vision request for: " + photoSubject);
        String allStructures = StructureManager.getDisplayString();

        String teams = String.join(",", GPTGOD.SCOREBOARD.getTeams().stream().map(team -> {
            return team.getName();
        }).toList());
        if (GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)) {
            gpt.addContext(String.format("Teams: %s", teams), "teams");
        }

        String imageSlashS = imageFiles.length > 1 ? "these images" : "this image";

        gpt.addContext(
                String.format("Current Players: %s",
                        Arrays.toString(
                                GPTGOD.SERVER.getOnlinePlayers().stream().map(player -> player.getName()).toArray())),
                "PlayerNames").addContext(String.format("All Structures: %s", allStructures), "structures")
                .addFilesWithContext(String.format(
                        "describe and critique photo of %s depictied in %s photographed by %s.",
                        photoSubject, imageSlashS, photographer), imageFiles)
                .send(functionMap);
    }

    public static void lookAtPhoto(String photographer, String photoSubject, GoogleFile imageFile) {
        lookAtPhoto(photographer, photoSubject, new GoogleFile[] { imageFile });
    }

    public static void lookAtStructure(String photographer, String structureName, GoogleFile[] imageFiles) {
        GPTGOD.LOGGER.info("generating vision request for structure: " + structureName);
        lookAtPhoto(photographer, String.format("the minecraft structure currently named %s", structureName), imageFiles);
    }

    // send off an image of a structure to be rated by the secondary ai model
    public static void lookAtStructure(String photographer, String structureName, GoogleFile imageFile) {
        lookAtStructure(photographer, structureName, new GoogleFile[] { imageFile });
    }
}
