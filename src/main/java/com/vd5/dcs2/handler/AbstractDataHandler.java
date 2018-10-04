package com.vd5.dcs2.handler;

import com.vd5.dcs.model.Position;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author beou on 10/1/18 03:55
 */
public abstract class AbstractDataHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Position) {
            Position position = handlePosition((Position) msg);
            if (position != null) {
                ctx.fireChannelRead(position);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    protected abstract Position handlePosition(Position position);
}
