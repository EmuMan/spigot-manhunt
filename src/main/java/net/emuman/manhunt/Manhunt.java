package net.emuman.manhunt;

import net.emuman.manhunt.commands.HunterCommand;
import net.emuman.manhunt.commands.ManhuntCommand;
import net.emuman.manhunt.commands.RunnerCommand;
import net.emuman.manhunt.listeners.EntityDeathHandler;
import net.emuman.manhunt.listeners.PlayerDeathHandler;
import net.emuman.manhunt.listeners.PlayerInteractHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Manhunt extends JavaPlugin {

    private static Manhunt instance = null;

    public Manhunt() {
        super();
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ManhuntCommand manhuntCommand = new ManhuntCommand();
        getCommand("manhunt").setExecutor(manhuntCommand);
        getCommand("manhunt").setTabCompleter(manhuntCommand);

        HunterCommand hunterCommand = new HunterCommand();
        getCommand("hunter").setExecutor(hunterCommand);
        getCommand("hunter").setTabCompleter(hunterCommand);

        RunnerCommand runnerCommand = new RunnerCommand();
        getCommand("runner").setExecutor(runnerCommand);
        getCommand("runner").setTabCompleter(runnerCommand);

        getServer().getPluginManager().registerEvents(new EntityDeathHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractHandler(), this);
    }

    @Override
    public void onDisable() {
        ManhuntManager.stop();
        super.onDisable();
    }

    public static Manhunt getInstance() {
        return instance;
    }
}
