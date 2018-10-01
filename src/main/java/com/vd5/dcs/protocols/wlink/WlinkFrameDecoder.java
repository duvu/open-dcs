//package com.vd5.dcs.protocols.wlink;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.socket.DatagramPacket;
//import io.netty.handler.codec.MessageToMessageDecoder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
///**
// * @author beou on 7/21/18 14:23
// */
//public class WlinkFrameDecoder extends MessageToMessageDecoder<DatagramPacket> {
//    /**
//     * Decode from one message to an other. This method will be called for each written message that can be handled
//     * by this encoder.
//     *
//     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToMessageDecoder} belongs to
//     * @param msg the message to decode to an other one
//     * @param out the {@link List} to which decoded messages should be added
//     * @throws Exception is thrown if an error occurs
//     */
//    @Override
//    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
//        ByteBuf buf = msg.content();
//        buf.retain();
//        out.add(buf);
//    }
//}
