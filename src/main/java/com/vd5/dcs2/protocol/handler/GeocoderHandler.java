package com.vd5.dcs2.protocol.handler;

import com.vd5.dcs.model.Position;
import com.vd5.dcs2.ApplicationContext;
import com.vd5.dcs.geocoder.Geocoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author beou on 10/1/18 01:50
 */
@ChannelHandler.Sharable
public class GeocoderHandler extends ChannelInboundHandlerAdapter {
    private final boolean processInvalidPosition;
    private final int geocoderReuseDistance;

    public GeocoderHandler(boolean processInvalidPosition) {
        this.processInvalidPosition = processInvalidPosition;
        this.geocoderReuseDistance = ApplicationContext.getGeocoderReuseDistance();
    }

    @Override
    public void channelRead(final ChannelHandlerContext context, Object message) throws Exception {
        //--//--//--//--//--//--//
        Geocoder geocoder = ApplicationContext.getGeocoder();
        if (message instanceof Position) {
            final Position position = (Position) message;
            if (processInvalidPosition || position.isValid()) {
                geocoder.getAddress(position.getLatitude(), position.getLongitude(), new Geocoder.ReverseGeocoderCallback() {

                    @Override
                    public void onSuccess(String address) {
                        position.setAddress(address);
                        context.fireChannelRead(position);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        context.fireChannelRead(position);
                    }
                });
            } else {
                context.fireChannelRead(position);
            }
        } else {
            context.fireChannelRead(message);
        }
    }
}
