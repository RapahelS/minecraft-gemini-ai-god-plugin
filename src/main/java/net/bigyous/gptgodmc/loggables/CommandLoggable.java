package net.bigyous.gptgodmc.loggables;

import org.bukkit.entity.HumanEntity;

public class CommandLoggable extends BaseLoggable {
    protected String output;
    protected boolean success;

    public CommandLoggable(String output, boolean success) {
        super();
        this.output = output;
        this.success = success;
    }

    @Override
    public String getLog() {
        return String.format("command %s with output: %s", success ? "succeeded": "failed", output);
    }

    @Override
    public boolean combine(Loggable l) {
        return false;
    }
}
