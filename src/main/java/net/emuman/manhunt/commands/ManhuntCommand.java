package net.emuman.manhunt.commands;

import net.emuman.manhunt.ManhuntManager;
import net.emuman.manhunt.commandbuilder.*;
import net.emuman.manhunt.commandbuilder.exceptions.CommandStructureException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class ManhuntCommand implements CommandExecutor, TabCompleter {

    private final NodeBase nodeBase;

    public ManhuntCommand() {
        nodeBase = new BranchNode("operation");

        IntegerNode startNode = new IntegerNode("start", 0, 600);
        startNode.addNode(new EndNode("startEnd", values -> {
            CommandSender sender = (CommandSender) values.get("sender");
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;
            int headStart = (Integer) values.get("start");
            if (ManhuntManager.getHunters().size() == 0) {
                player.sendMessage(ChatColor.RED + "There are not enough hunters to start the match.");
            } else if (ManhuntManager.getRunners().size() == 0) {
                player.sendMessage(ChatColor.RED + "There are not enough runners to start the match.");
            } else if (!ManhuntManager.start(headStart * 20)) {
                player.sendMessage(ChatColor.RED + "There is already a match in progress.");
            } else if (!ManhuntManager.getHunters().contains(player) && !ManhuntManager.getRunners().contains(player)) {
                player.sendMessage(ChatColor.GREEN + "The round has started!");
            }
        }));

        EndNode stopNode = new EndNode("stop", values -> {
            CommandSender sender = (CommandSender) values.get("sender");
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;
            if (ManhuntManager.stop()) {
                if (!ManhuntManager.getHunters().contains(player) && !ManhuntManager.getRunners().contains(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Manhunt successfully stopped.");
                }
                return;
            }
            player.sendMessage(ChatColor.RED + "There is not currently any manhunt in progress.");
        });

        nodeBase.addNode(startNode);
        nodeBase.addNode(stopNode);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            CommandTraceLog log = nodeBase.run(sender, args);
            if (log.getReturnCode() != CommandTraceLog.ReturnCode.SUCCESS) {
                sender.sendMessage(ChatColor.RED + "Usage: /manhunt " + log.getTraceString() + (log.getMessage() == null ? "" : " (" + log.getMessage() + ")"));
            }
        } catch (CommandStructureException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return nodeBase.onTabComplete(sender, cmd, label, args);
    }

}
