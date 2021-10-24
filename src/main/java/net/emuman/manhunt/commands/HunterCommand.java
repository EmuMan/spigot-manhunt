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

public class HunterCommand implements TabCompleter, CommandExecutor {

    private final NodeBase nodeBase;

    public HunterCommand() {
        nodeBase = new BranchNode("operation");

        PlayerNode addNode = new PlayerNode("add");
        addNode.addNode(new EndNode("addEnd", values -> {
            CommandSender sender = (CommandSender) values.get("sender");
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;
            Player added = (Player) values.get("add");
            if (ManhuntManager.addHunter(added)) {
                if (ManhuntManager.getRunners().contains(player)) {
                    ManhuntManager.removeRunner(player);
                }
                if (!player.equals(added)) {
                    player.sendMessage(ChatColor.GREEN + added.getDisplayName() + " was added to the hunters!");
                }
                added.sendMessage(ChatColor.GREEN + "You have joined the hunters!");
            } else {
                player.sendMessage(ChatColor.RED + added.getDisplayName() + " is already a hunter.");
            }
        }));

        PlayerNode removeNode = new PlayerNode("remove");
        removeNode.addNode(new EndNode("removeEnd", values -> {
            CommandSender sender = (CommandSender) values.get("sender");
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;
            Player removed = (Player) values.get("remove");
            if (ManhuntManager.removeHunter(removed)) {
                if (!player.equals(removed)) {
                    player.sendMessage(ChatColor.YELLOW + removed.getDisplayName() + " was removed from the hunters!");
                }
                removed.sendMessage(ChatColor.YELLOW + "You have been removed from the hunters!");
            } else {
                player.sendMessage(ChatColor.RED + removed.getDisplayName() + " is not a hunter.");
            }
        }));

        EndNode listNode = new EndNode("list", values -> {
            CommandSender sender = (CommandSender) values.get("sender");
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;
            List<Player> hunters = ManhuntManager.getHunters();
            if (hunters.size() == 0) {
                player.sendMessage(ChatColor.YELLOW + "There are currently no assigned hunters.");
                return;
            }
            StringBuilder listMessage = new StringBuilder();
            listMessage.append(ChatColor.YELLOW + "List of current hunters:\n");
            for (Player hunter : hunters) {
                listMessage.append(ChatColor.YELLOW + " - " + hunter.getDisplayName() + "\n");
            }
            player.sendMessage(listMessage.toString());
        });

        EndNode clearNode = new EndNode("clear", values -> {
            CommandSender sender = (CommandSender) values.get("sender");
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;
            if (ManhuntManager.clearHunters()) {
                player.sendMessage(ChatColor.GREEN + "The hunter list was successfully cleared.");
                return;
            }
            player.sendMessage(ChatColor.RED + "No hunters have been assigned yet.");
        });

        nodeBase.addNode(addNode);
        nodeBase.addNode(removeNode);
        nodeBase.addNode(listNode);
        nodeBase.addNode(clearNode);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            CommandTraceLog log = nodeBase.run(sender, args);
            if (log.getReturnCode() != CommandTraceLog.ReturnCode.SUCCESS) {
                sender.sendMessage(ChatColor.RED + "Usage: /runner " + log.getTraceString() + (log.getMessage() == null ? "" : " (" + log.getMessage() + ")"));
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
