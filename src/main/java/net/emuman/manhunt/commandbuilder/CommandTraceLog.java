package net.emuman.manhunt.commandbuilder;

import java.util.ArrayList;
import java.util.List;

public class CommandTraceLog {

    public enum ReturnCode {
        SUCCESS,
        MISSING_ARGUMENT,
        INVALID_ARGUMENT,
        EXTRA_ARGUMENT,
        ARGUMENT_NOT_IN_BOUNDS
    }

    private ReturnCode returnCode;
    private final List<String> traceLog;
    private String message;

    public CommandTraceLog() {
        this.returnCode = null;
        traceLog = new ArrayList<>();
        message = null;
    }

    public void addTrace(String trace) {
        traceLog.add(trace);
    }

    public void setReturnCode(ReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public List<String> getTraceLog() {
        return traceLog;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTraceString() {
        return String.join(" ", traceLog);
    }

    public String toString() {
        return returnCode + " > " + getTraceString() + (message == null ? "" : " (" + message + ")");
    }

}
