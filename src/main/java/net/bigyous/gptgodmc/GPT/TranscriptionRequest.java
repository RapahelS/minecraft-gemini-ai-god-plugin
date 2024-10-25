package net.bigyous.gptgodmc.GPT;

public class TranscriptionRequest {
    private String fileUri;
    private String playerName;
    private String timeStamp;

    public TranscriptionRequest(String fileUri, String playerName, String timeStamp) {
        this.fileUri = fileUri;
        this.playerName = playerName;
        this.timeStamp = timeStamp;
    }

    public Object getPlayerName() {
        return playerName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public String getUri() {
        return fileUri;
    }
}
