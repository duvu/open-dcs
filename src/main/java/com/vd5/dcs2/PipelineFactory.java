package com.vd5.dcs2;

import com.vd5.dcs2.handler.*;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author beou on 10/1/18 00:47
 */
public abstract class PipelineFactory extends ChannelInitializer<Channel> {
    private final TrackerServer server;
    private int timeout;

    private GeocoderHandler geocoderHandler;
    private StandardLoggingHandler standardLoggingHandler;
    private RemoteAddressHandler remoteAddressHandler;

    public PipelineFactory(TrackerServer server, String protocol) {
        this.server = server;
        this.timeout = ApplicationContext.getTimeout(protocol);

        //-- init list of handler
        geocoderHandler = new GeocoderHandler(ApplicationContext.getProcessInvalidPosition());
        standardLoggingHandler = new StandardLoggingHandler();
        remoteAddressHandler = new RemoteAddressHandler();

    }

    protected abstract void addProtocolHandler(PipelineBuilder pipelineBuilder);

    @Override
    protected void initChannel(Channel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();

        //--1--//
        if (timeout > 0 && server.isDuplex()) {
            pipeline.addLast("idleHandler", new IdleStateHandler(timeout, 0, 0));
        }

        //--2--//
        pipeline.addLast("openHandler", new OpenChannelHandler(server));

        //--3--//
        pipeline.addLast("networkMessage", new NetworkMessageHandler()); // decode data to network-message

        //--4--//
        pipeline.addLast("loggingHandler", standardLoggingHandler);

        // add protocol specific handlers -- decode data to position. After this step message will be process as a position-object
        addProtocolHandler(new PipelineBuilder() {

            @Override
            public void addLast(String name, ChannelHandler handler) {
                if (!(handler instanceof AbstractProtocolDecoder || handler instanceof AbstractProtocolEncoder)) {
                    if (handler instanceof ChannelInboundHandler) {
                        handler = new WrapperInboundHandler((ChannelInboundHandler) handler);
                    } else {
                        handler = new WrapperOutboundHandler((ChannelOutboundHandler) handler);
                    }
                }
                pipeline.addLast(name, handler);
            }
        });


        //--5--//
        pipeline.addLast("remoteAddress", remoteAddressHandler);

        //--6--//
        pipeline.addLast("geocoder", geocoderHandler);


        //--7--//
        pipeline.addLast("mainHandler", new MainEventHandler());
    }
}
