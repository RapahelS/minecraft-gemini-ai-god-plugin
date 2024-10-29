package net.bigyous.gptgodmc.GPT.Json;

public class SpeechifyGenerateRequest {

    private AudioFormat audio_format = AudioFormat.wav;
    private String input;
    private String voice_id = "benjamin";
    private AudioModel model = AudioModel.simba_english;

    public SpeechifyGenerateRequest(String input) {
        this.input = input;
    }

    public SpeechifyGenerateRequest(String input, String voice) {
        this.input = input;
        this.voice_id = voice;
    }

    public AudioFormat getAudio_format() {
        return audio_format;
    }

    public String getInput() {
        return input;
    }

    public String getVoice_id() {
        return voice_id;
    }

    public AudioModel getModel() {
        return model;
    }

}