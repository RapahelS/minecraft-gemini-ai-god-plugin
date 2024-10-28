package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.entity.EntityTameEvent;

public class TameAnimalLoggable extends BaseLoggable {
    private String player;
    private String animal;

    public TameAnimalLoggable(EntityTameEvent event){
        super();
        this.player = event.getOwner().getName();
        this.animal = event.getEntity().getName();
    }

    @Override
    public String getLog() {
        return String.format("%s tamed a %s.", player, animal);
    }
}
