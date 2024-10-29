package net.bigyous.gptgodmc.GPT.Json;

public class GenerateContentResponse {
    Candidate[] candidates;
    PromptFeedback promptFeedback;
    UsageMetadata usageMetadata;

    GoogError error;

    // shortcut to just return the first candidate response
    public String getText() {
        return candidates[0].getText();
    }

    public Candidate[] getCandidates() {
        return candidates;
    }

    public GoogError getError() {
        return this.error;
    }

    public boolean isError() {
        return this.error != null;
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
