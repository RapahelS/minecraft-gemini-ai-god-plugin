package net.bigyous.gptgodmc.loggables;

import java.time.Instant;

import net.bigyous.gptgodmc.utils.GPTUtils;

public class ChatLoggable implements Loggable, UserInputLoggable {
    /**
     * Chat or voice chat event
     */
    public String playerName;
    public String message;
    private Instant timestamp;
    private int tokens = -1;
    private String minecraftTime;

    public ChatLoggable(String playerName, String minecraftTime, String message) {
        this.playerName = playerName;
        this.message = message;
        timestamp = Instant.now();
        minecraftTime = GPTUtils.getTimeStamp();
        // Moderation.moderateUserInput(message, this);
    }

    public ChatLoggable(String playerName, String minecraftTime, String message, Instant timestamp) {
        this.playerName = playerName;
        this.message = message;
        this.timestamp = timestamp;
        minecraftTime = GPTUtils.getTimeStamp();
        // Moderation.moderateUserInput(message, this);
    }
    public String getLog() {
        return playerName + " said \"" + message + "\"";
    }

    public boolean combine(Loggable event) {
        // if (!(event instanceof ChatLoggable)) return false;
        // ChatLoggable other = (ChatLoggable) event;
        // if(other.playerName.equals(this.playerName)){
        //     this.message = String.format("%s. %s", this.message, other.message);
        //     return true;
        // }
        return false;
    }

    @Override
    public Instant getRawInstant() {
        return timestamp;
    }

    public int getTokens(){
        if(tokens<0){
            this.tokens = GPTUtils.countTokens(getLog());
        }
        return tokens;
    }
    public void resetTokens(){
        tokens = -1;
    }

    @Override
    public void updateUserInput(String input) {
        this.message = input;
    }

    @Override
    public String getMinecraftTimeStamp() {
        return minecraftTime;
    }
}

