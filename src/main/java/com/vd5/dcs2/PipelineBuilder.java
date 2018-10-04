package com.vd5.dcs2;

import io.netty.channel.ChannelHandler;

/**
 * @author beou on 10/1/18 02:56
 */
public interface PipelineBuilder {
    void addLast(String name, ChannelHandler handler);
}
