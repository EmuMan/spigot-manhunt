package net.emuman.manhunt.commandbuilder;

import net.emuman.manhunt.commandbuilder.exceptions.CommandStructureException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class SingleStringNode extends NodeBase {

    private List<String> options;

    /**
     * A node for parsing an integer out of an argument
     *
     * @param name the name of the node
     * @param options the list of words that can be passed
     */
    public SingleStringNode(String name, List<String> options) {
        super(name);
        this.options = options;
    }

    /**
     * A node for parsing an integer out of an argument
     *
     * @param name the name of the node
     */
    public SingleStringNode(String name) {
        super(name);
        this.options = null;
    }

    @Override
    public void onExecute(String[] args, Map<String, Object> values, CommandTraceLog traceLog)
            throws CommandStructureException {
        if (args.length == 0) {
            addTraceLogData(traceLog, CommandTraceLog.ReturnCode.MISSING_ARGUMENT, null);
            return;
        }
        String choice = args[0].toLowerCase();
        // if there are specific options for this string
        if (options != null && options.size() != 0) {
            // make sure variable is in the list of options
            if (!options.contains(choice)) {
                addTraceLogData(traceLog, CommandTraceLog.ReturnCode.INVALID_ARGUMENT, null);
                return;
            }
        }
        addTraceLogData(traceLog, CommandTraceLog.ReturnCode.SUCCESS, choice);
        values.put(getName(), choice);
        if (getNodes().size() == 0) {
            throw new CommandStructureException("SingleStringNode must point towards one other node");
        }
        getNodes().get(0).onExecute(Arrays.copyOfRange(args, 1, args.length), values, traceLog);
    }

    @Override
    public NodeBase createCopy(String name) {
        return new SingleStringNode(name, options);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (options != null && options.size() != 0) {
                return options.stream().filter(s -> s.toLowerCase().startsWith(args[0])).collect(Collectors.toList());
            } else {
                return new ArrayList<>(Collections.singletonList("<" + getName() + ">"));
            }
        } else {
            return getNodes().get(0).onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    private void addTraceLogData(CommandTraceLog traceLog, CommandTraceLog.ReturnCode code, String choice) {
        if (code == CommandTraceLog.ReturnCode.SUCCESS) {
            traceLog.addTrace(choice);
        } else {
            if (options == null || options.size() == 0) {
                // there are no options, display name of node
                traceLog.addTrace("<" + getName() + ">");
            } else {
                // there are options to choose from, display that
                traceLog.addTrace("[" + String.join("|", options) + "]");
            }
            traceLog.setReturnCode(code);
        }
    }

}
