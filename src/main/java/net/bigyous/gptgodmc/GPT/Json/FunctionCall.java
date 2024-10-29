package net.bigyous.gptgodmc.GPT.Json;

import com.google.gson.JsonObject;

public class FunctionCall {
    private String name;
    private JsonObject args;

    public FunctionCall(String name, JsonObject args) {
        this.name = name;
        this.args = args;
    }

    public JsonObject getArguments() {
        return args;
    }

    public String getName() {
        return name;
    }
}
