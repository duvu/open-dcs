package com.vd5.dcs2.protocol.wlink;

import com.vd5.dcs2.AbstractProtocol;
import com.vd5.dcs2.PipelineBuilder;
import com.vd5.dcs2.TrackerServer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;
import java.util.Map;

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
                pipelineBuilder.addLast("frameDecoder", new WlinkFrameDecoder());
                pipelineBuilder.addLast("stringDecoder", new StringDecoder());
                pipelineBuilder.addLast("stringEncoder", new StringEncoder());
                pipelineBuilder.addLast("objectEncoder", new WlinkProtocolEncoder());
                pipelineBuilder.addLast("objectDecoder", new WlinkProtocolDecoder(WlinkProtocol.this));
            }
        });

        serverList.add(new TrackerServer(true, getName()) {

            @Override
            protected void addProtocolHandlers(PipelineBuilder pipelineBuilder) {
                pipelineBuilder.addLast("stringDecoder", new StringDecoder());
                pipelineBuilder.addLast("stringEncoder", new StringEncoder());
                pipelineBuilder.addLast("objectEncoder", new WlinkProtocolEncoder());
                pipelineBuilder.addLast("objectDecoder", new WlinkProtocolDecoder(WlinkProtocol.this));
            }
        });
    }
}
