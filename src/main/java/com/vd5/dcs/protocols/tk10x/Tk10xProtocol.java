///*
// * Copyright (c) 2017. by Vu.Du
// */
//
//package com.vd5.dcs.protocols.tk10x;
//
//import com.vd5.dcs.protocols.AbstractProtocolAdapter;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.DelimiterBasedFrameDecoder;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static com.vd5.dcs.protocols.ServerManager.VD5_DEFAULT_TIMEOUT;
//import static com.vd5.dcs.protocols.ServerManager.VD5_TIMEOUT;
//
///**
// * @author beou on 8/25/17 14:52
// * @version 1.0
// */
//
//public class Tk10xProtocol extends AbstractProtocolAdapter<NioSocketChannel> {
//    private static final int MAX_LENGTH = 600;
//
//
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    @Override
//    protected void initChannel(NioSocketChannel ch) throws Exception {
//        log.info("[>_ Tk10x] start");
//
//        Integer timeout = ch.attr(VD5_TIMEOUT).get();
//        timeout = timeout > 0 ? timeout : VD5_DEFAULT_TIMEOUT;
//
//        ChannelPipeline pipeline = ch.pipeline();
//        //pipeline.addLast(new ReadTimeoutHandler(timeout));
//        pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_LENGTH, false, delimiters()));
//        pipeline.addLast(new StringDecoder());
//        pipeline.addLast(new StringEncoder());
//
//        Tk10xProtocolDecoder tkDecoder = new Tk10xProtocolDecoder();
//        pipeline.addLast(tkDecoder);
//
//        for (ChannelHandler handler : handlerList) {
//            pipeline.addLast(handler);
//        }
//    }
//
//    private ByteBuf[] delimiters() {
//        return new ByteBuf[]{
//                Unpooled.wrappedBuffer(new byte[]{13, 10}),         //CR-LF
//                Unpooled.wrappedBuffer(new byte[]{0x0F, 0x0F}),     //FF
//                Unpooled.wrappedBuffer(new byte[]{0x0C, 0x0E}),     //CE
//                Unpooled.wrappedBuffer(new byte[]{0x00}),
//                Unpooled.wrappedBuffer(new byte[]{10}),
//                Unpooled.wrappedBuffer(new byte[]{'\n'}),
//                Unpooled.wrappedBuffer(new byte[]{'\r'}),
//                Unpooled.wrappedBuffer(new byte[]{')'}),
//                Unpooled.wrappedBuffer(new byte[]{';'}),
//        };
//    }
//}
