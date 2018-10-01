//package com.vd5.dcs.protocols.wlink;
//
//import com.vd5.dcs.protocols.AbstractProtocolAdapter;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.epoll.EpollDatagramChannel;
//import io.netty.channel.socket.DatagramChannel;
//import io.netty.channel.socket.nio.NioDatagramChannel;
//import io.netty.handler.codec.DelimiterBasedFrameDecoder;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author beou on 11/25/17 01:57
// */
//public class WlinkProtocol extends AbstractProtocolAdapter<EpollDatagramChannel> {
//    private final int MAX_LENGTH = 600;
//    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    protected void initChannel(EpollDatagramChannel channel) throws Exception {
//        LOGGER.info("[Wlink >_] start");
//        ChannelPipeline pipeline = channel.pipeline();
//        pipeline
//                .addLast(new LoggingHandler(LogLevel.DEBUG))
//                .addLast(new WlinkFrameDecoder())
//                .addLast(new StringDecoder())
//                .addLast(new StringEncoder())
//                .addLast(new WlinkProtocolDecoder());
//
//        for (ChannelHandler handler : handlerList) {
//            pipeline.addLast(handler);
//        }
//    }
//}
