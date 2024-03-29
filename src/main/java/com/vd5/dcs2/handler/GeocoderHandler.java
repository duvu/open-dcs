package com.vd5.dcs2.handler;

import com.vd5.dcs2.ApplicationContext;
import com.vd5.dcs.geocoder.Geocoder;
import com.vd5.dcs2.Log;
import com.vd5.dcs2.model.Position;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
    public void channelRead(final ChannelHandlerContext context, Object message) {
        if (message instanceof Position) {
            final Position position = (Position) message;
            if (processInvalidPosition || position.getValid()) {
                ApplicationContext.getGeocoderManager().get(position.getLatitude(), position.getLongitude(), new Geocoder.ReverseGeocoderCallback() {

                    @Override
                    public void onSuccess(String address) {
                        Log.info("... geocoding services ..." + address);
                        position.setAddress(address);
                        context.fireChannelRead(position);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.info("... geocoding services ... ERROR" + e.getMessage());
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
