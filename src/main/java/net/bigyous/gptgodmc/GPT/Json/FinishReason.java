package net.bigyous.gptgodmc.GPT.Json;

// https://ai.google.dev/api/generate-content#FinishReason
public enum FinishReason {
    FINISH_REASON_UNSPECIFIED,
    STOP,
    SAFETY,
    RECITATION,
    LANGUAGE,
    OTHER,
    BLOCKLIST,
    PROHIBITED_CONTENT,
    SPII,
    MALFORMED_FUNCTION_CALL
}