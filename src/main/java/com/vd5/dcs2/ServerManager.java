package com.vd5.dcs2;

import com.vd5.dcs2.model.ProtocolObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author beou on 9/30/18 06:37
 */
public class ServerManager {
    private final List<TrackerServer> serverList = new LinkedList<>();
    private final Map<String, TrackerServer> serverMap = new ConcurrentHashMap<>();
    private final Map<String, AbstractProtocol> protocolMap = new ConcurrentHashMap<>();

    public ServerManager() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] nameList = ApplicationContext.getProtocolNameList();
        for (String name: nameList) {
            ProtocolObject protocolObject = ApplicationContext.getProtocolObject(name);
            Log.info("Protocol #{} is enabled", name);
            if (protocolObject.isEnabled()) {
                initProtocolServer(protocolObject);
            }
        }
    }

    public AbstractProtocol getProtocol(String name) {
        return protocolMap.get(name);
    }

    public void start() throws InterruptedException {
        for (TrackerServer server : serverList) {
            server.start();
        }
    }

    public void stop() {
        for (TrackerServer server: serverList) {
            server.stop();
        }
    }

    public void start(String name) throws InterruptedException {
        final Protocol protocol = protocolMap.get(name);
        protocol.initTrackerServer(serverList, true);

        serverMap.get(name + ".duplex").start();
    }

    public void stop(String name) {

    }

    private void initProtocolServer(ProtocolObject protocolObject) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class protocolClazz = Class.forName(protocolObject.getClazz());
        if (AbstractProtocol.class.isAssignableFrom(protocolClazz)) {
            AbstractProtocol protocol = (AbstractProtocol) protocolClazz.newInstance();
            protocol.initTrackerServer(serverList, protocolObject.isDuplex());
            protocolMap.put(protocol.getName(), protocol);
        }
    }
}
