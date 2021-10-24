package net.emuman.manhunt.listeners;

import net.emuman.manhunt.ManhuntManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathHandler implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ManhuntManager.getRunners().contains(player) && ManhuntManager.killRunner(player)) {
            if (ManhuntManager.getDeadRunners().size() == ManhuntManager.getRunners().size()) {
                ManhuntManager.end(false);
            }
        }
    }

}
