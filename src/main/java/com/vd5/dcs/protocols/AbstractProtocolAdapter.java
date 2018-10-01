//package com.vd5.dcs.protocols;
//
//import com.vd5.dcs.ApplicationContext;
//import com.vd5.dcs.DcsConfiguration;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelInitializer;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author beou on 11/24/17 17:30
// */
//public abstract class AbstractProtocolAdapter<T extends Channel> extends ChannelInitializer<T> {
//
//    private DcsConfiguration.Server serverConfig;
//    private ApplicationContext applicationContext;
//    protected List<ChannelHandler> handlerList;
//
//    public AbstractProtocolAdapter() {
//        handlerList = new ArrayList<>();
//    }
//
//    protected void addHandler(ChannelHandler handler) {
//        if (handlerList == null) {
//            handlerList = new ArrayList<>();
//        }
//
//        handlerList.add(handler);
//    }
//
//    protected void setHandlerList(List<ChannelHandler> handlerList) {
//        this.handlerList = handlerList;
//    }
//
//    protected List<ChannelHandler> getHandlerList() {
//        return handlerList;
//    }
//
//    protected void setServerConfig(DcsConfiguration.Server config) {
//        serverConfig = config;
//    }
//
//    protected void setApplicationContext(ApplicationContext devManager) {
//        this.applicationContext = devManager;
//    }
//
//    protected DcsConfiguration.Server getServerConfig() {
//        return serverConfig;
//    }
//
//    protected ApplicationContext getApplicationContext() {
//        return applicationContext;
//    }
//}
