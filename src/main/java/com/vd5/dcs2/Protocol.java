package com.vd5.dcs2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author beou on 9/30/18 00:21
 */

public interface Protocol {
    String getName();
    void initTrackerServer(List<TrackerServer> serverList);


    Collection<String> getSupportedDataCommands();
    Collection<String> getSupportedTextCommands();

    void sendDataCommand(ActiveDevice activeDevice, String command);
    void sendTextCommand(String destAddress, String command);
}
