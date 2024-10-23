package net.bigyous.gptgodmc.GPT.Json;

public class FunctionCall {
    private String name;
    private Schema args;

    public Schema getArguments() {
        return args;
    }

    public String getName() {
        return name;
    }
}
