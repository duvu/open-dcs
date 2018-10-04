package com.vd5.dcs2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author beou on 10/1/18 00:18
 */

public abstract class AbstractProtocol implements Protocol {
    private final String name;
    private final Set<String> supportedDataCommands = new HashSet<>();
    private final Set<String> supportedTextCommands = new HashSet<>();

    public AbstractProtocol(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setSupportedDataCommands(String... commands) {
        supportedDataCommands.addAll(Arrays.asList(commands));
    }

    public void setSupportedTextCommands(String commands) {
        supportedTextCommands.addAll(Arrays.asList(commands));
    }

    public Collection<String> getSupportedDataCommands() {
        Set<String> commands = new HashSet<>(supportedDataCommands);
        return commands;
    }

    public Collection<String> getSupportedTextCommands() {
        return new HashSet<>(supportedTextCommands);
    }

    @Override
    public void sendDataCommand(ActiveDevice activeDevice, String command) {
        //todo
    }

    @Override
    public void sendTextCommand(String destAddress, String command) {
        //todo
    }
}
