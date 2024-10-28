package net.bigyous.gptgodmc.loggables;

import org.bukkit.event.entity.PlayerDeathEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
public class DeathLoggable extends BaseLoggable {
    
    private String deathMessage;

    public DeathLoggable(PlayerDeathEvent event){
        super();
        this.deathMessage = PlainTextComponentSerializer.plainText().serialize(event.deathMessage());
        
    }

    public String getLog(){
        return deathMessage;
    }

}
