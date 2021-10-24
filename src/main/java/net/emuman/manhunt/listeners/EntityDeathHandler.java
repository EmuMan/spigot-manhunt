package net.emuman.manhunt.listeners;

import net.emuman.manhunt.ManhuntManager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathHandler implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
            ManhuntManager.end(true);
        }
    }

}
