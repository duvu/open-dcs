package com.vd5.dcs2.protocol.tk10x;

import com.vd5.dcs2.protocol.AbstractProtocol;
import com.vd5.dcs2.protocol.PipelineBuilder;
import com.vd5.dcs2.protocol.TrackerServer;

import java.util.List;

/**
 * @author beou on 10/1/18 15:48
 */
public class Tk10xProtocol extends AbstractProtocol {

    public Tk10xProtocol() {
        super("tk10x");
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
