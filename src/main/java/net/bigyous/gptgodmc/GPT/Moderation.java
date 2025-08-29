package net.bigyous.gptgodmc.GPT;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.ModerationResult;
import net.bigyous.gptgodmc.loggables.UserInputLoggable;

public class Moderation {
    private static Gson gson = new Gson();
    private static String MODERATION_URL = "https://api.openai.com/v1/moderations";

    public static void moderateUserInput(String input, UserInputLoggable loggable) {
        if (input == null) {
            return;
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        Thread worker = new Thread(() -> {
            String auth = JavaPlugin.getPlugin(GPTGOD.class).getConfig().getString("openAiKey");
            TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
            };
            String payload = gson.toJson(Collections.singletonMap("input", input), mapType.getType());
            try {
                StringEntity data = new StringEntity(payload, ContentType.APPLICATION_JSON);
                HttpPost post = new HttpPost(MODERATION_URL);
                post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + auth);
                post.setEntity(data);
                HttpResponse response = client.execute(post);
                String raw = new String(response.getEntity().getContent().readAllBytes());
                EntityUtils.consume(response.getEntity());
                if (response.getStatusLine().getStatusCode() != 200) {
                    GPTGOD.LOGGER.warn("API call failed");
                    Thread.currentThread().interrupt();
                }
                String out = processResponse(raw);
                if (out != null) {
                    loggable.updateUserInput(out);
                }
            } catch (IOException e) {
                GPTGOD.LOGGER.error("There was an error making a request to GPT", e);
            }
            Thread.currentThread().interrupt();
        });
        worker.start();

    }

    private static String processResponse(String rawResponse) {
    JsonObject moderationObject = new Gson().fromJson(rawResponse, JsonObject.class);
        JsonArray array = moderationObject.get("results").getAsJsonArray();
        ModerationResult results = gson.fromJson(array.get(0), ModerationResult.class);
        if (results.isFlagged())
            return String.format("Content flagged for: [%s]", results.getCategories());
        return null;
    }
}
