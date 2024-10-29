package net.bigyous.gptgodmc.GPT.Json;

import com.google.gson.annotations.SerializedName;

// a message data part. Can only be one of the types
// https://ai.google.dev/api/caching#Part
public class Part {
    @SerializedName("text")
    String text;

    // @SerializedName("inlineData")
    // private Blob inlineData;

    @SerializedName("functionCall")
    private FunctionCall functionCall;

    // @SerializedName("functionResponse")
    // private FunctionResponse functionResponse;

    @SerializedName("fileData")
    private FileData fileData;

    // @SerializedName("executableCode")
    // private ExecutableCode executableCode;

    // @SerializedName("codeExecutionResult")
    // private CodeExecutionResult codeExecutionResult;

    public String getText() {
        return text;
    }

    public FunctionCall getFunctionCall() {
        return functionCall;
    }

    // constructors for the various union types
    public Part(String text) {
        this.text = text;
    }

    public Part(FileData fileData) {
        this.fileData = fileData;
    }

    public Part(FunctionCall function) {
        this.functionCall = function;
    }
}

class FileData {
    private String mimeType;

    private String fileUri;

    public FileData(String mimeType, String fileUri) {
        this.mimeType = mimeType;
        this.fileUri = fileUri;
    }
}