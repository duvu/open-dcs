//package com.vd5.dcs.protocols;
//
//import com.vd5.dcs.model.Position;
//import com.vd5.dcs.services.PositionService;
//import io.netty.channel.ChannelHandler.Sharable;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
///**
// * @author beou on 11/23/17 22:54
// */
//@Sharable
//@Component
//public class LastHandler extends ChannelInboundHandlerAdapter {
//    private final PositionService positionService;
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    public LastHandler(PositionService positionService) {
//        this.positionService = positionService;
//    }
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        // NOOP
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        Position position = (Position) msg;
//        positionService.insertEventData(position);
//
//        //-- closing channel
//        ServerManager.ConnectionTypes channelType = ctx.channel().attr(ServerManager.VD5_CONNECTION_TYPE).get();
//        if (ServerManager.ConnectionTypes.TCP.equals(channelType)) {
//            ctx.close();
//        }
//    }
//
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        log.error("[>_] exception", cause);
//        ServerManager.ConnectionTypes channelType = ctx.channel().attr(ServerManager.VD5_CONNECTION_TYPE).get();
//        if (ServerManager.ConnectionTypes.TCP == channelType) {
//            ctx.close();
//        }
//    }
//}
