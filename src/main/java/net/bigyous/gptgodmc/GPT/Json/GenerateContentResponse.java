package net.bigyous.gptgodmc.GPT.Json;

import com.google.gson.annotations.SerializedName;

public class GenerateContentResponse {
    Candidate[] candidates;
    PromptFeedback promptFeedback;
    UsageMetadata usageMetadata;

    // shortcut to just return the first candidate response
    public String getText() {
        return candidates[0].getText();
    }
}

// https://ai.google.dev/api/generate-content#candidate
class Candidate {
    @SerializedName("content")
    private Content content;

    @SerializedName("finishReason")
    private FinishReason finishReason;

    @SerializedName("safetyRatings")
    private SafetyRating[] safetyRatings;

    @SerializedName("citationMetadata")
    private CitationMetadata citationMetadata;

    @SerializedName("tokenCount")
    private int tokenCount;

    @SerializedName("avgLogprobs")
    private double avgLogprobs;

    @SerializedName("logprobsResult")
    private LogprobsResult logprobsResult;

    @SerializedName("index")
    private int index;

    // shortcut to the first model response
    public String getText() {
        return content.getParts()[0].getText();
    }

    // get the Content of the response
    public Content getContent() {
        return content;
    }

    public FinishReason getFinishReason() {
        return finishReason;
    }

    public SafetyRating[] getSafetyRatings() {
        return safetyRatings;
    }

    public CitationMetadata getCitationMetadata() {
        return citationMetadata;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public double getAvgLogprobs() {
        return avgLogprobs;
    }

    public LogprobsResult getLogprobsResult() {
        return logprobsResult;
    }

    public int getIndex() {
        return index;
    }
}

class PromptFeedback {

}

// https://ai.google.dev/api/generate-content#UsageMetadata
class UsageMetadata {
    private int promptTokenCount;
    private int cachedContentTokenCount;
    private int candidatesTokenCount;
    private int totalTokenCount;
}

enum Role {
    user,
    model
}



class SafetyRating {
    // Define the structure of SafetyRating object
}

class CitationMetadata {
    // Define the structure of CitationMetadata object
}

// https://ai.google.dev/api/generate-content#LogprobsResult
class LogprobsResult {
    // Define the structure of LogprobsResult object
}

