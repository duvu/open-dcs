package com.vd5.dcs2.protocol;

import com.vd5.dcs2.ApplicationContext;
import com.vd5.dcs2.Log;
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
    private final Map<String, AbstractProtocol> protocolMap = new ConcurrentHashMap<>();

    public ServerManager() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] nameList = ApplicationContext.getProtocolNameList();
        for (String name: nameList) {
            ProtocolObject protocolObject = ApplicationContext.getProtocolObject(name);

            if (protocolObject.isEnabled()) {
                Class protocolClazz = Class.forName(protocolObject.getClazz());
                if (AbstractProtocol.class.isAssignableFrom(protocolClazz)) {
                    AbstractProtocol protocol = (AbstractProtocol) protocolClazz.newInstance();

                    initProtocolServer(protocol);
                    protocolMap.put(protocol.getName(), protocol);
                }
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

    private void initProtocolServer(final Protocol protocol) {
        protocol.initTrackerServer(serverList);
    }
}
