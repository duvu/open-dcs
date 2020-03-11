package com.vd5.dcs2.websocket;

/**
 * @author beou on 10/24/18 01:28
 */
public class WSMessage {
    public static final String UNKNOWN_DEVICE = "UNKNOWNDEVICE";

    private String command;
    private Object data;

    public WSMessage() {
    }

    public WSMessage(String command) {
        this.command = command;
    }

    public WSMessage(String command, Object data) {
        this.command = command;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
