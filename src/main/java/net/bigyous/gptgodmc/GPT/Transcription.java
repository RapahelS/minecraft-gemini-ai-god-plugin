package net.bigyous.gptgodmc.GPT;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.github.mizosoft.methanol.Methanol;

import java.nio.file.Path;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.GenerateContentRequest;
import net.bigyous.gptgodmc.GPT.Json.GenerateContentResponse;
import net.bigyous.gptgodmc.GPT.Json.GptModel;

public class Transcription {

    private static String BASE_URL = "https://generativelanguage.googleapis.com";
    private static Gson gson = new Gson();
    private static Methanol client = Methanol.create();
    private static FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();

    public static String Transcribe(Path audioPath) {
        if (config.getString("openAiKey").isBlank()) {
            GPTGOD.LOGGER.warn("No API Key");
            return "something";
        }

        String apiKey = config.getString("geminiKey");
        GptModel model = GPTModels.getSecondaryModel();

        try {

            GoogleFile file = new GoogleFile(audioPath);

            if (!file.tryUpload()) {
                GPTGOD.LOGGER.error("Transcription failed during upload");
                return "something";
            }

            // Create the content request object
            GenerateContentRequest contentRequest = new GenerateContentRequest()
                .setSystemInstruction("""
                    you are a voice chat transcriber. 
                    Try to transcrive what you hear from the input audio as closly as possible. 
                    If no words are heard then you may return descriptions of what you hear or do 
                    not hear inbetween of astrix cahracters e.x. *birds chirping* or *nothing*.
                    """)
                .addFileWithPrompt("transcribe this audio clip", "audio/mp3", file.getUri());
            String jsonBody = gson.toJson(contentRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model.getName()
                            + ":generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            System.out.println("Received transcription response:\n" + response.body());

            return gson.fromJson(response.body(), GenerateContentResponse.class).getText();
        } catch (FileNotFoundException e) {
            GPTGOD.LOGGER.error("Attempted to Transcribe non-existant file", e);
            e.printStackTrace();
        } catch (IOException e) {
            GPTGOD.LOGGER.error("An error occured during Transcription", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            GPTGOD.LOGGER.error("Transcription Interrupted", e);
        }
        return "something";
    }

}
