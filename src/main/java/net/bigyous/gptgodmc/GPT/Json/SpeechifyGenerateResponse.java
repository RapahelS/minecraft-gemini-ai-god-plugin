package net.bigyous.gptgodmc.GPT.Json;

public class SpeechifyGenerateResponse {
    // the base64 encoded audio bytes
    private String audio_data;
    private AudioFormat audio_format;
    private SpeechMarks speech_marks;

    public String getAudio_data() {
        return audio_data;
    }
    public AudioFormat getAudio_format() {
        return audio_format;
    }
    public SpeechMarks getSpeech_marks() {
        return speech_marks;
    }
    
}
