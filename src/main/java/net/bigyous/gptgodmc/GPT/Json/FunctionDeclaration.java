package net.bigyous.gptgodmc.GPT.Json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.bigyous.gptgodmc.GPTGOD;
import net.bigyous.gptgodmc.interfaces.Function;
import net.bigyous.gptgodmc.utils.GPTUtils;

public class FunctionDeclaration {
    private String name;
    private String description;
    private Schema parameters;
    // excluded from serialization
    private transient Function<JsonObject> function;

    public FunctionDeclaration(String name, String description, Schema parameters, Function<JsonObject> function) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.function = function;

        // default to all fields required
        // List<String> required = new ArrayList<>();
        // for(String key : parameters.getProperties().keySet()) {
        //     required.add(key);
        // }
        // this.parameters.setRequiredFields(required);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Schema getParameters() {
        return parameters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParameters(Schema parameters) {
        this.parameters = parameters;
    }

    public Function<JsonObject> getFunction() {
        return function;
    }

    public List<String> getRequired() {
        return this.parameters.getRequiredFields();
    }

    public void runFunction(JsonObject jsonArgs){
        GPTGOD.LOGGER.info(String.format("%s invoked", this.name));
        function.run(jsonArgs);
    }

    public int calculateFunctionTokens(){
        return GPTUtils.countTokens(name) + GPTUtils.countTokens(description) + parameters.calculateParameterTokens();
    }
}
