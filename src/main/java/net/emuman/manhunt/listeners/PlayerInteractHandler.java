package net.emuman.manhunt.listeners;

import net.emuman.manhunt.ManhuntManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractHandler implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        Player player = event.getPlayer();
        if (!ManhuntManager.getHunters().contains(player)) return;
        ItemStack item = event.getItem();
        if (ManhuntManager.isTracker(item)) {
            ItemMeta meta = item.getItemMeta();
            int runnerCount = ManhuntManager.getRunners().size();
            int cmd = meta.getCustomModelData();
            if (cmd >= runnerCount) meta.setCustomModelData(runnerCount - 1);
            if (cmd == runnerCount - 1) meta.setCustomModelData(0);
            else meta.setCustomModelData(cmd + 1);
            item.setItemMeta(meta);
            player.getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), item);
            ManhuntManager.updateTrackers(player);
        }
    }

}
