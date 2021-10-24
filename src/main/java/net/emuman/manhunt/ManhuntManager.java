package net.emuman.manhunt;

import net.emuman.manhunt.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ManhuntManager {

    private static final List<Player> hunters = new ArrayList<>();
    private static final List<Player> runners = new ArrayList<>();
    private static final List<Player> deadRunners = new ArrayList<>();

    private static boolean inProgress = false;
    private static boolean huntersReleased = false;
    private static long time = 0;
    private static BukkitRunnable timeRunnable = null;
    private static BukkitRunnable trackerRunnable = null;


    public static boolean addHunter(Player hunter) {
        if (hunters.contains(hunter)) {
            return false;
        }
        hunters.add(hunter);
        return true;
    }

    public static boolean removeHunter(Player hunter) {
        if (hunters.contains(hunter)) {
            hunters.remove(hunter);
            return true;
        }
        return false;
    }

    public static List<Player> getHunters() {
        return new ArrayList<>(hunters);
    }

    public static boolean clearHunters() {
        if (hunters.size() > 0) {
            hunters.clear();
            return true;
        }
        return false;
    }

    public static boolean addRunner(Player runner) {
        if (runners.contains(runner)) {
            return false;
        }
        runners.add(runner);
        return true;
    }

    public static boolean removeRunner(Player runner) {
        if (runners.contains(runner)) {
            runners.remove(runner);
            return true;
        }
        return false;
    }

    public static List<Player> getRunners() {
        return new ArrayList<>(runners);
    }

    public static boolean clearRunners() {
        if (runners.size() > 0) {
            runners.clear();
            return true;
        }
        return false;
    }

    public static boolean addDeadRunner(Player runner) {
        if (deadRunners.contains(runner)) {
            return false;
        }
        deadRunners.add(runner);
        return true;
    }

    public static boolean removeDeadRunner(Player runner) {
        if (deadRunners.contains(runner)) {
            deadRunners.remove(runner);
            return true;
        }
        return false;
    }

    public static List<Player> getDeadRunners() {
        return new ArrayList<>(deadRunners);
    }

    public static boolean clearDeadRunners() {
        if (deadRunners.size() > 0) {
            deadRunners.clear();
            return true;
        }
        return false;
    }

    public static boolean killRunner(Player runner) {
        if (!addDeadRunner(runner)) return false;
        runner.setGameMode(GameMode.SPECTATOR);
        return true;
    }

    public static boolean isTracker(ItemStack itemStack) {
        return itemStack.getType().equals(Material.COMPASS) &&
                itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData();
    }

    public static String getTrackerName(int runnerIndex) {
        return ChatColor.GREEN + (runners.size() == 0 ? "No target" :
                "Tracking " + runners.get(runnerIndex < runners.size() ? runnerIndex : runners.size() - 1).getDisplayName());
    }

    public static void giveTracker(Player hunter) {
        ItemStack compass = new ItemStackBuilder(Material.COMPASS)
                .setName(getTrackerName(0))
                .setLore(Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Player tracker"))
                .setCustomModelData(0)
                .build();
        hunter.getInventory().addItem(compass);
        updateTrackers(hunter);
    }

    public static void updateTrackers(Player hunter) {
        for (int i = 0; i < hunter.getInventory().getContents().length; i++) {
            ItemStack itemStack = hunter.getInventory().getItem(i);
            if (itemStack == null) continue;
            if (isTracker(itemStack)) {
                CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
                if (meta.getCustomModelData() >= runners.size()) {
                    meta.setCustomModelData(runners.size() - 1);
                }
                meta.setLodestone(runners.get(meta.getCustomModelData()).getLocation());
                meta.setLodestoneTracked(false);
                meta.setDisplayName(getTrackerName(meta.getCustomModelData()));
                itemStack.setItemMeta(meta);
                hunter.getInventory().setItem(i, itemStack);
            }
        }
    }

    public static void sendCountdownMessage(int secondsRemaining) {
        for (Player hunter : hunters) {
            hunter.sendMessage(ChatColor.YELLOW + "You will be released in " + secondsRemaining + "...");
        }
        for (Player runner : runners) {
            runner.sendMessage(ChatColor.GREEN + "The hunters will be released in " + secondsRemaining + "...");
        }
    }

    public static void bindHunters(int headStart) {
        for (Player hunter : hunters) {
            hunter.setGameMode(GameMode.ADVENTURE);
            new BukkitRunnable() {
                @Override
                public void run() {
                    hunter.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, headStart, 1, false, false));
                    hunter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, headStart, 255, false, false));
                }
            }.runTaskLater(Manhunt.getInstance(), 1L);
        }
    }

    public static void releaseHunters() {
        for (Player hunter : hunters) {
            for (PotionEffect effect : hunter.getActivePotionEffects()) {
                hunter.removePotionEffect(effect.getType());
            }
            hunter.setGameMode(GameMode.SURVIVAL);
            hunter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been released!");
        }
        huntersReleased = true;
    }

    public static void resetPlayer(Player player) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        player.getInventory().clear();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static boolean start(int headStart) {
        if (inProgress) return false;

        time = 0;
        inProgress = true;
        clearDeadRunners();
        if (headStart == 0) huntersReleased = true;
        else bindHunters(headStart);

        for (Player hunter : getHunters()) {
            resetPlayer(hunter);
            giveTracker(hunter);
            hunter.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The game has started!" +
                    (headStart == 0 ? "" : (" You will be released in " + headStart / 20 + " seconds!")));
        }

        for (Player runner : getRunners()) {
            resetPlayer(runner);
            runner.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The game has started!" +
                    (headStart == 0 ? "" : (" You have a " + headStart / 20 + " second head start!")));
        }

        timeRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!huntersReleased) {
                    if (time == headStart - 60) {
                        sendCountdownMessage(3);
                    } else if (time == headStart - 40) {
                        sendCountdownMessage(2);
                    } else if (time == headStart - 20) {
                        sendCountdownMessage(1);
                    } else if (time == headStart) {
                        releaseHunters();
                        for (Player runner : runners) {
                            runner.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The hunters have been released!");
                        }
                    }
                }
                time++;
            }
        };
        timeRunnable.runTaskTimer(Manhunt.getInstance(), 0L, 1L);

        trackerRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player hunter : hunters) {
                    updateTrackers(hunter);
                }
            }
        };
        trackerRunnable.runTaskTimer(Manhunt.getInstance(), 0L, 20L);

        return true;
    }

    public static boolean stop() {
        if (!inProgress) return false;

        shutdownGame();

        String timeMessage = getTimeMessage();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "The match was ended by a moderator.");
            player.sendMessage(timeMessage);
        }

        return true;
    }

    public static void end(boolean runnerFinish) {
        shutdownGame();
        String timeMessage = getTimeMessage();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(ChatColor.GREEN + "The match has finished. " + ChatColor.GOLD + ChatColor.BOLD + (runnerFinish ? "Runners" : "Hunters") + " win!");
            player.sendMessage(timeMessage);
        }


    }

    private static void shutdownGame() {
        if (timeRunnable != null) {
            timeRunnable.cancel();
            timeRunnable = null;
        }
        if (trackerRunnable != null) {
            trackerRunnable.cancel();
            trackerRunnable = null;
        }
        inProgress = false;
        if (!huntersReleased) releaseHunters(); // idk if this will ever happen but just in case
        huntersReleased = false;
    }

    private static String getTimeMessage() {
        long totalSeconds = time / 20;
        long hours = totalSeconds / (60 * 60);
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;
        return ChatColor.GREEN + "Total time: " + ChatColor.GOLD +
                hours + (hours == 1 ? "hour" : "hours") + ", " +
                minutes + (minutes == 1 ? "minute" : "minutes") + ", and " +
                seconds + (seconds == 1 ? "second" : "seconds") + ".";
    }

}
