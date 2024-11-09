package net.bigyous.gptgodmc.GPT.Json;

public class SpeechifyVoiceInfo {

    public enum VoiceType {
        personal,
        shared
    }

    public class SpeechifyLanguage {
        String locale;
        String preview_audio;
    }

    public class SpeechifyModel {
        String name;

    }

    public class VoiceInfo {
        String id;
        VoiceType type;
        String display_name;
        SpeechifyModel[] models;
    }
}
