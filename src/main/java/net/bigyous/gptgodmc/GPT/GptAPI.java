package net.bigyous.gptgodmc.GPT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.GPT.Json.Candidate;
import net.bigyous.gptgodmc.GPT.Json.Content;
import net.bigyous.gptgodmc.GPT.Json.FunctionCall;
import net.bigyous.gptgodmc.GPT.Json.FunctionDeclaration;
import net.bigyous.gptgodmc.GPT.Json.GenerateContentRequest;
import net.bigyous.gptgodmc.GPT.Json.GenerateContentResponse;
import net.bigyous.gptgodmc.GPT.Json.GptModel;
import net.bigyous.gptgodmc.GPT.Json.ModelSerializer;
import net.bigyous.gptgodmc.GPT.Json.ParameterExclusion;
import net.bigyous.gptgodmc.GPT.Json.Part;
import net.bigyous.gptgodmc.GPT.Json.Tool;
import net.bigyous.gptgodmc.GPT.Json.ToolConfig;
import net.bigyous.gptgodmc.GPT.Json.Content.Role;

public class GptAPI {
    private GsonBuilder gson = new GsonBuilder();

    private GptModel model;
    private GenerateContentRequest body;
    private String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private Map<String, Integer> messageMap = new HashMap<String, Integer>();
    private boolean isSending = false;
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static JavaPlugin plugin = JavaPlugin.getPlugin(GPTGOD.class);

    public GptAPI(GptModel model, double temperature) {
        this.model = model;
        this.body = new GenerateContentRequest(GptActions.GetAllTools(), temperature);
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI(GptModel model) {
        this.model = model;
        this.body = new GenerateContentRequest(GptActions.GetAllTools());
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI(GptModel model, Tool customTools, double tempurature) {
        this.model = model;
        this.body = new GenerateContentRequest(customTools, tempurature);
        
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI(GptModel model, Tool customTools) {
        this.model = model;
        this.body = new GenerateContentRequest(customTools);
        
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI(GenerateContentRequest request) {
        this.body = request;
        gson.registerTypeAdapter(GptModel.class, new ModelSerializer());
        gson.setExclusionStrategies(new ParameterExclusion());
    }

    public GptAPI addContext(String context, String name) {
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), context);
            return this;
        }
        this.body.addMessage(Content.Role.user, context);
        this.messageMap.put(name, this.body.getMessagesSize() - 1);
        return this;
    }

    public GptAPI addFileWithContext(String context, String fileMimeType, String fileUri) {
        this.body.addFileWithPrompt(context, fileMimeType, fileUri);
        return this;
    }

    // sets the system direction parameter
    public GptAPI setSystemContext(String context) {
        this.body.setSystemInstruction(context);
        return this;
    }

    public GptAPI setSystemContext(String[] context) {
        this.body.setSystemInstruction(context);
        return this;
    }

    // public GptAPI addContext(String context, String name, int index) {
    //     if (this.messageMap.containsKey(name)) {
    //         this.body.replaceMessage(messageMap.get(name), context);
    //         return this;
    //     }
    //     this.body.addMessage("system", context);
    //     for (String key : messageMap.keySet()) {
    //         if (messageMap.get(key) == index) {
    //             messageMap.replace(key, index + 1);
    //         }
    //     }
    //     this.messageMap.put(name, index);
    //     return this;
    // }

    public GptAPI setTools(Tool tools) {
        this.body.setTools(tools);
        return this;
    }

    public GptAPI addLogs(List<String> Logs, String name) {
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage(Content.Role.user, Logs);
        this.messageMap.put(name, this.body.getMessagesSize() - 1);
        return this;
    }

    public GptAPI addLogs(String Logs, String name) {
        if(Logs.length() < 1) {
            GPTGOD.LOGGER.warn("tried to add empty logs");
            return this;
        }

        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage(Content.Role.user, Logs);
        this.messageMap.put(name, this.body.getMessagesSize() - 1);
        return this;
    }

    public GptAPI addLogs(String Logs, String name, int index) {
        if(this.body.getMessagesSize() <= index){
            addLogs(Logs, name);
            return this;
        }
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage(Content.Role.user, Logs, index);
        for (String key : messageMap.keySet()) {
            if (messageMap.get(key) == index) {
                messageMap.replace(key, index + 1);
            }
        }
        this.messageMap.put(name, index);
        return this;
    }

    public GptAPI addLogs(List<String> Logs, String name, int index) {
        if(this.body.getMessagesSize() <= index){
            addLogs(Logs, name);
            return this;
        }
        if (this.messageMap.containsKey(name)) {
            this.body.replaceMessage(messageMap.get(name), Logs);
            return this;
        }
        this.body.addMessage(Content.Role.user, Logs, index);
        for (String key : messageMap.keySet()) {
            if (messageMap.get(key) == index) {
                messageMap.replace(key, index + 1);
            }
        }
        this.messageMap.put(name, index);
        return this;
    }

    public void addResponse(Content responseContent) {
        GPTGOD.LOGGER.info("Adding response " + gson.create().toJson(responseContent));
        this.body.addMessage(responseContent);
    }

    public void addMessage(String message) {
        GPTGOD.LOGGER.info("Adding prompt to get response: " + message);
        this.body.addMessage(Role.user, message);
    }

    public GptAPI setToolChoice(String tool_choice) {
        this.body.setToolConfig(new ToolConfig(new String[]{tool_choice}));
        return this;
    }

    // public void removeLastMessage() {
    //     this.body.removeLastMessage();
    // }

    public int getMaxTokens() {
        return model.getTokenLimit();
    }

    public String getModelName() {
        return model.getName();
    }

    public boolean isLatestMessageFromModel() {
        return this.body.isLatestMessageFromModel();
    }

    public void send() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        pool.execute(() -> {
            this.isSending = true;
            FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
            StringEntity data = new StringEntity(gson.create().toJson(body), ContentType.APPLICATION_JSON);
            GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
            HttpPost post = new HttpPost(BASE_URL + model.getName() + ":generateContent" + "?key=" + config.getString("geminiKey"));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            GPTGOD.LOGGER.info("Making POST request");
            post.setEntity(data);
            try {
                HttpResponse response = client.execute(post);
                String out = new String(response.getEntity().getContent().readAllBytes());
                EntityUtils.consume(response.getEntity());
                GPTGOD.LOGGER.info("recieved response from Gemini: " + out);
                if (response.getStatusLine().getStatusCode() != 200) {
                    GPTGOD.LOGGER.warn("API call failed");
                    this.isSending = false;
                }
                processResponse(out);
                client.close();
                // after everything finishes executing, the request is finished
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(GPTGOD.class), () -> {
                    this.isSending = false;
                }, 10);
            } catch (IOException e) {
                GPTGOD.LOGGER.error("There was an error making a request to GPT", e);
                this.isSending = false;
            }
        });
    }

    public void send(Map<String, FunctionDeclaration> functions) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        pool.execute(() -> {
            this.isSending = true;
            FileConfiguration config = JavaPlugin.getPlugin(GPTGOD.class).getConfig();
            StringEntity data = new StringEntity(gson.create().toJson(body), ContentType.APPLICATION_JSON);
            GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
            HttpPost post = new HttpPost(BASE_URL + model.getName() + ":generateContent" + "?key=" + config.getString("geminiKey"));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            GPTGOD.LOGGER.info("Making POST request");
            post.setEntity(data);
            try {
                HttpResponse response = client.execute(post);
                String out = new String(response.getEntity().getContent().readAllBytes());
                EntityUtils.consume(response.getEntity());
                GPTGOD.LOGGER.info("recieved response from Gemini: " + out);
                if (response.getStatusLine().getStatusCode() != 200) {
                    GPTGOD.LOGGER.warn("API call failed");
                    this.isSending = false;
                }
                processResponse(out, functions);
                client.close();
                // after everything finishes, executing the request is finished
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(GPTGOD.class), () -> {
                    this.isSending = false;
                }, 20);
            } catch (IOException e) {
                GPTGOD.LOGGER.error("There was an error making a request to GPT", e);
                this.isSending = false;
            }
        });
    }

    public boolean isSending() {
        return isSending;
    }

    // DEBUG method
    public void checkRequestBody() {
        GPTGOD.LOGGER.info("POSTING " + gson.setPrettyPrinting().create().toJson(body));
    }

    private void processResponse(String response) {
        // shadow gson builder with gson
        Gson gson = this.gson.create();

        GenerateContentResponse responseObject = gson.fromJson(response, GenerateContentResponse.class);

        if (responseObject.isError()) {
            GPTGOD.LOGGER.error(responseObject.getError().toString());
            if(responseObject.getError().getStatus() == "INVALID_ARGUMENT") {
                GPTGOD.LOGGER.info("GEMINI API RETURNED INVALID ARGUMENT! Suggestion: Double check that the gemini model names are correct.");
            }
            return;
        }

        // run all candidates for now if their parts are not null
        for (Candidate choice : responseObject.getCandidates()) {
            ArrayList<Part> parts = choice.getContent().getParts();
            if (parts == null) {
                continue;
            }

            // add non null candidates to response history for multi-turn
            this.addResponse(choice.getContent());

            for (Part call : parts) {
                FunctionCall func = call.getFunctionCall();
                if (func == null) {
                    continue;
                }
                System.out
                        .println("Trying to execute function " + func.getName() + " with args: " + func.getArguments());
                GptActions.run(func.getName(), func.getArguments());
            }
        }
    }

    private void processResponse(String response, Map<String, FunctionDeclaration> functions) {
        // shadow gson builder with gson
        Gson gson = this.gson.create();

        GenerateContentResponse responseObject = gson.fromJson(response, GenerateContentResponse.class);

        if (responseObject.isError()) {
            GPTGOD.LOGGER.error("error loading gemini response: " + responseObject.getError().toString());
        }

        for (Candidate cand : responseObject.getCandidates()) {
            ArrayList<Part> parts = cand.getContent().getParts();
            if (parts == null) {
                continue;
            }
            for (Part call : parts) {
                FunctionCall func = call.getFunctionCall();
                if (func == null) {
                    continue;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    System.out.println("Trying to execute function " + func.getName() + " from map with args: "
                            + func.getArguments());
                    functions.get(func.getName()).runFunction(func.getArguments());
                });
            }
        }
    }
}
