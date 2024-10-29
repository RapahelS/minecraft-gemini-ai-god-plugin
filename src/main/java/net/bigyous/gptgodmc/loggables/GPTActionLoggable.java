package net.bigyous.gptgodmc.loggables;

import net.bigyous.gptgodmc.GameLoop;

public class GPTActionLoggable extends BaseLoggable {
    private String text;

    public GPTActionLoggable(String text) {
        super();
        this.text = "God " + text;
        GameLoop.logAction(text);
    }

    @Override
    public String getLog() {
        return text;
    }
}
