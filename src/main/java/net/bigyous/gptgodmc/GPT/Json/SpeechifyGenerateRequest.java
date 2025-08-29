package net.bigyous.gptgodmc.GPT.Json;

public class SpeechifyGenerateRequest {

    private AudioFormat audio_format = AudioFormat.wav;
    private String input;
    private String voice_id = "linus";
    private AudioModel model = AudioModel.simba_multilingual;

    public SpeechifyGenerateRequest(String input) {
        this.input = input;
    }

    public SpeechifyGenerateRequest(String input, String voice) {
        this.input = input;
        this.voice_id = voice;
    }

    public SpeechifyGenerateRequest(String input, String voice, AudioModel model) {
        this.input = input;
        this.voice_id = voice;
        if (model != null) {
            this.model = model;
        }
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

    public void setModel(AudioModel model) {
        if (model != null) {
            this.model = model;
        }
    }

}