package net.bigyous.gptgodmc.GPT.Json;

import java.util.Map;

public class GoogError {
    int code;
    String message;
    String status;
    Map<String,String> details;

    @Override
    public String toString() {
        return "Google Error " + status + " " + code + " " + message;
    }
}
