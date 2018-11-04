package com.vd5.dcs2.websocket;

import com.vd5.dcs2.utils.GsonFactory;

/**
 * @author beou on 11/4/18 08:33
 */
public class SimpleWSEvent implements WSEvent {
    private String command;
    private String data;

    public SimpleWSEvent() {
    }

    public SimpleWSEvent(String command, String data) {
        this.command = command;
        this.data = data;
    }

    public static SimpleWSEvent parse(String text) {
        return GsonFactory.getGson().fromJson(text, SimpleWSEvent.class);
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getData() {
        return data;
    }
}
