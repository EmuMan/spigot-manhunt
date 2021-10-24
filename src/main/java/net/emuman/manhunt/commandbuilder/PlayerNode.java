package net.emuman.manhunt.commandbuilder;

import net.emuman.manhunt.commandbuilder.exceptions.CommandStructureException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerNode extends NodeBase{

    public PlayerNode(String name) {
        super(name);
    }

    @Override
    public void onExecute(String[] args, Map<String, Object> values, CommandTraceLog traceLog)
            throws CommandStructureException {
        if (args.length == 0) {
            addTraceLogData(traceLog, CommandTraceLog.ReturnCode.MISSING_ARGUMENT, null);
            return;
        }
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            addTraceLogData(traceLog, CommandTraceLog.ReturnCode.INVALID_ARGUMENT, playerName);
            return;
        }
        addTraceLogData(traceLog, CommandTraceLog.ReturnCode.SUCCESS, playerName);
        values.put(getName(), player);
        if (getNodes().size() == 0) {
            throw new CommandStructureException("PlayerNode must point towards one other node");
        }
        getNodes().get(0).onExecute(Arrays.copyOfRange(args, 1, args.length), values, traceLog);
    }

    @Override
    public NodeBase createCopy(String name) {
        return new PlayerNode(name);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(args[0])).collect(Collectors.toList());
        } else {
            return getNodes().get(0).onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    private void addTraceLogData(CommandTraceLog traceLog, CommandTraceLog.ReturnCode code, String choice) {
        if (code == CommandTraceLog.ReturnCode.SUCCESS) {
            traceLog.addTrace(choice);
        } else {
            traceLog.addTrace("<" + getName() + ">"); // TODO: Maybe put incorrect player name if invalid?
            traceLog.setReturnCode(code);
        }
    }
}
