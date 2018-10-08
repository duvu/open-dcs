package com.vd5.dcs2.protocol.tk10x;

import com.vd5.dcs2.AbstractProtocol;
import com.vd5.dcs2.PipelineBuilder;
import com.vd5.dcs2.TrackerServer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

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
                pipelineBuilder.addLast("frameDecoder", new Tk103FrameDecoder());
                pipelineBuilder.addLast("stringDecoder", new StringDecoder());
                pipelineBuilder.addLast("stringEncoder", new StringEncoder());
                pipelineBuilder.addLast("objectEncoder", new Tk103ProtocolEncoder());
                pipelineBuilder.addLast("objectDecoder", new Tk103ProtocolDecoder(Tk10xProtocol.this));
            }
        });

        serverList.add(new TrackerServer(true, getName()) {

            @Override
            protected void addProtocolHandlers(PipelineBuilder pipelineBuilder) {
                pipelineBuilder.addLast("stringDecoder", new StringDecoder());
                pipelineBuilder.addLast("stringEncoder", new StringEncoder());
                pipelineBuilder.addLast("objectEncoder", new Tk103ProtocolEncoder());
                pipelineBuilder.addLast("objectDecoder", new Tk103ProtocolDecoder(Tk10xProtocol.this));
            }
        });
    }
}
