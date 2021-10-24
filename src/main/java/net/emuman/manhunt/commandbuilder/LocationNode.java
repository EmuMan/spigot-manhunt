package net.emuman.manhunt.commandbuilder;

import net.emuman.manhunt.commandbuilder.exceptions.CommandStructureException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.*;

public class LocationNode extends NodeBase {

    public LocationNode(String name) {
        super(name);
    }

    @Override
    public void onExecute(String[] args, Map<String, Object> values, CommandTraceLog traceLog)
            throws CommandStructureException {
        if (args.length < 3) {
            addTraceLogData(traceLog, CommandTraceLog.ReturnCode.MISSING_ARGUMENT, null);
            return;
        }

        // TODO: Add more options here
        Location loc;
        CommandSender sender = (CommandSender) values.get("sender");
        if (sender == null) {
            loc = new Location(Bukkit.getWorlds().get(0), 0.0d, 0.0d, 0.0d);
        } else if (sender instanceof Entity) {
            loc = ((Entity) sender).getLocation();
        } else {
            loc = new Location(Bukkit.getWorlds().get(0), 0.0d, 0.0d, 0.0d);
        }

        try {
            loc.setX(parseSingleComponent(loc.getX(), args[0]));
            loc.setY(parseSingleComponent(loc.getY(), args[1]));
            loc.setZ(parseSingleComponent(loc.getZ(), args[2]));
        } catch (NumberFormatException e) {
            addTraceLogData(traceLog, CommandTraceLog.ReturnCode.INVALID_ARGUMENT, null);
            traceLog.setMessage(getName() + " must be a valid location");
            return;
        }

        addTraceLogData(traceLog, CommandTraceLog.ReturnCode.SUCCESS, args);
        values.put(getName(), loc);
        if (getNodes().size() == 0) {
            throw new CommandStructureException("IntegerNode must point towards one other node");
        }
        getNodes().get(0).onExecute(Arrays.copyOfRange(args, 3, args.length), values, traceLog);
    }

    @Override
    public NodeBase createCopy(String name) {
        return new LocationNode(name);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        // TODO: Add more options here
        Location loc;
        if (sender instanceof Entity) {
            loc = ((Entity) sender).getLocation();
        } else {
            loc = new Location(Bukkit.getWorlds().get(0), 0.0d, 0.0d, 0.0d);
        }

        if (args.length <= 3) {
            return new ArrayList<>(Arrays.asList(
                    // TODO: Create a more sophisticated tab completion process
                    String.format("%.2f", loc.getX()),
                    String.format("%.2f %.2f", loc.getX(), loc.getY()),
                    String.format("%.2f %.2f %.2f", loc.getX(), loc.getY(), loc.getZ())
            ));
        } else {
            return getNodes().get(0).onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 3, args.length));
        }
    }

    private void addTraceLogData(CommandTraceLog traceLog, CommandTraceLog.ReturnCode code, String[] args) {
        if (code == CommandTraceLog.ReturnCode.SUCCESS) {
            traceLog.addTrace(args[0] + " " + args[1] + " " + args[2]);
        } else {
            traceLog.addTrace("<" + getName() + ">");
            traceLog.setReturnCode(code);
        }
    }

    private double parseSingleComponent(double baseValue, String component) {
        if (component.startsWith("~")) {
            if (component.length() == 1) {
                return baseValue;
            } else {
                return baseValue + Double.parseDouble(component.substring(1));
            }
        }
        return Double.parseDouble(component);
    }

}
