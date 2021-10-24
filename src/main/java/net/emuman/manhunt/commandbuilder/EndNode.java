package net.emuman.manhunt.commandbuilder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EndNode extends NodeBase {

    private final Consumer<Map<String, Object>> function;

    public EndNode(String name, Consumer<Map<String, Object>> function) {
        super(name);
        this.function = function;
    }

    @Override
    public void onExecute(String[] args, Map<String, Object> values, CommandTraceLog traceLog) {
        if (args.length != 0) {
            traceLog.setReturnCode(CommandTraceLog.ReturnCode.EXTRA_ARGUMENT);
        }
        function.accept(values);
        traceLog.setReturnCode(CommandTraceLog.ReturnCode.SUCCESS);
    }

    @Override
    public NodeBase createCopy(String name) {
        return new EndNode(name, function);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        // this tab complete should never be used because an end node is not technically part of the chain, meaning
        // if the arguments get to this point there are extra arguments and nothing should be suggested.
        // we can suggest no completions by just returning an empty list
        return new ArrayList<>();
    }
}
