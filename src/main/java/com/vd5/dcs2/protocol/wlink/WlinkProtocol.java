package com.vd5.dcs2.protocol.wlink;

import com.vd5.dcs2.AbstractProtocol;
import com.vd5.dcs2.PipelineBuilder;
import com.vd5.dcs2.TrackerServer;

import java.util.List;

/**
 * @author beou on 10/1/18 20:14
 */
public class WlinkProtocol extends AbstractProtocol {

    public WlinkProtocol() {
        super("wlink");
    }

    @Override
    public void initTrackerServer(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(false, getName()) {

            @Override
            protected void addProtocolHandlers(PipelineBuilder pipelineBuilder) {

            }
        });

        serverList.add(new TrackerServer(true, getName()) {

            @Override
            protected void addProtocolHandlers(PipelineBuilder pipelineBuilder) {

            }
        });
    }
}
