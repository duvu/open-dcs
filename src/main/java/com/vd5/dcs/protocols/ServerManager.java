///*
// * Copyright (c) 2017. by Vu.Du
// */
//
//package com.vd5.dcs.protocols;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.RemovalListener;
//import com.google.common.cache.RemovalNotification;
//import com.vd5.dcs.ApplicationContext;
//import com.vd5.dcs.DcsConfiguration;
//import com.vd5.dcs.utils.StringUtils;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.epoll.EpollChannelOption;
//import io.netty.channel.epoll.EpollDatagramChannel;
//import io.netty.channel.epoll.EpollEventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//import io.netty.util.AttributeKey;
//import lombok.Data;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PreDestroy;
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author beou on 8/24/17 06:24
// */
//@Data
//@Component
//public class ServerManager {
//    private static final int DEFAULT_BOSS_N_THREAD = 2;
//    private static final int DEFAULT_WORKER_N_THREAD = 4;
//
//    private final LastHandler lastHandler;
//    private final GeocoderHandler geocoderHandler;
//    private final AlertHandler alertHandler;
//
//    private final ApplicationContext applicationContext;
//    private final DcsConfiguration dcsConfiguration;
//
//    private final Cache<String, Channel> channelCache;
//
//    //-- close later
//    private final NioEventLoopGroup bossGroup;
//    private final NioEventLoopGroup workerGroup;
//    private final EpollEventLoopGroup epollEventLoopGroup;
//
//    int bossNThread = DEFAULT_BOSS_N_THREAD;
//    int workerNThread = DEFAULT_WORKER_N_THREAD;
//
//
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    public static final int VD5_DEFAULT_TIMEOUT = 30;
//
//    //--
//    public static final String PACKAGE = "com.vd5.dcs";
//    public static final AttributeKey<ConnectionTypes> VD5_CONNECTION_TYPE       = AttributeKey.valueOf(PACKAGE + ".channel_type");
//    public static final AttributeKey<Double> VD5_MIN_SPEED_KPH                  = AttributeKey.valueOf(PACKAGE + ".min_speed_kph");
//    public static final AttributeKey<Integer> VD5_TIMEOUT                       = AttributeKey.valueOf(PACKAGE + ".timeout");
//    public static final AttributeKey<Boolean> VD5_DECODE_LOW                    = AttributeKey.valueOf(PACKAGE + ".decode_low");
//    public static final AttributeKey<Boolean> VD5_AUTO_ADD                      = AttributeKey.valueOf(PACKAGE + ".auto_add");
//    public static final AttributeKey<ApplicationContext> VD5_DEVICE_MANAGER     = AttributeKey.valueOf(PACKAGE + ".device_manager");
//
//    public enum  ConnectionTypes {
//        TCP,
//        UDP
//    }
//
//    @Autowired
//    public ServerManager(LastHandler lastHandler,
//                         GeocoderHandler geocoderHandler,
//                         AlertHandler alertHandler,
//                         ApplicationContext applicationContext,
//                         DcsConfiguration dcsConfiguration) {
//
//        this.applicationContext = applicationContext;
//        this.dcsConfiguration = dcsConfiguration;
//
//        //common handler
//        this.alertHandler = alertHandler;
//        this.geocoderHandler = geocoderHandler;
//        this.lastHandler = lastHandler;
//
//        bossNThread = dcsConfiguration.getBossNThread() > 0 ? dcsConfiguration.getBossNThread() : DEFAULT_BOSS_N_THREAD;
//        workerNThread = dcsConfiguration.getWorkerNThread() > 0 ? dcsConfiguration.getWorkerNThread() : DEFAULT_WORKER_N_THREAD;
//
//
//        channelCache = CacheBuilder.newBuilder()
//                .expireAfterAccess(5, TimeUnit.MINUTES)
//                .maximumSize(workerNThread)
//                .removalListener((RemovalListener<String, Channel>) notification -> {
//                    log.info("Removing cached");
//                    notification.getValue().close();
//                })
//                .build();
//
//        bossGroup = new NioEventLoopGroup(bossNThread);
//        workerGroup = new NioEventLoopGroup(workerNThread);
//        epollEventLoopGroup = new EpollEventLoopGroup(workerNThread);
//
//    }
//
//    public void init() throws InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException {
//        List<DcsConfiguration.Server> serverList = dcsConfiguration.getServerList();
//        log.info("Dcs-list-size: " + serverList.size());
//
//        for (DcsConfiguration.Server dcsConfig : serverList) {
//            if (!dcsConfig.isEnabled() || StringUtils.isEmpty(dcsConfig.getClazz())) {
//                continue;
//            }
//
//            String name         = dcsConfig.getName();
//            boolean isDuplex    = dcsConfig.isDuplex();
//            String address      = dcsConfig.getAddress();
//            int port            = dcsConfig.getPort();
//            String className    = dcsConfig.getClazz();
//
//            InetSocketAddress endpoint = StringUtils.isNotEmpty(address) ? new InetSocketAddress(address, port) : new InetSocketAddress(port);
//
//            Class protocolClass = Class.forName(className);
//            AbstractProtocolAdapter protocol = null;
//            if (AbstractProtocolAdapter.class.isAssignableFrom(protocolClass)) {
//                protocol = (AbstractProtocolAdapter) protocolClass.newInstance();
//            }
//
//            if (protocol == null) {
//                continue;
//            } else {
//                protocol.addHandler(geocoderHandler);
//                protocol.addHandler(alertHandler);
//                protocol.addHandler(lastHandler);
//
//                protocol.setServerConfig(dcsConfig);
//                protocol.setApplicationContext(applicationContext);
//            }
//
//            if (isDuplex) {
//                ServerBootstrap bootstrapping = new ServerBootstrap();
//                bootstrapping.group(bossGroup, workerGroup)
//                        .channel(NioServerSocketChannel.class)
//                        .handler(new LoggingHandler(LogLevel.DEBUG))
//                        .childHandler(protocol)
//                        .childAttr(VD5_CONNECTION_TYPE, ConnectionTypes.TCP)
//                        .childAttr(VD5_MIN_SPEED_KPH, dcsConfig.getMinSpeedKph())
//                        .childAttr(VD5_TIMEOUT, dcsConfig.getTimeout())
//                        .childAttr(VD5_DECODE_LOW, dcsConfig.isDecodeLow())
//                        .childAttr(VD5_AUTO_ADD, dcsConfig.isAutoAdd())
//                        .childAttr(VD5_DEVICE_MANAGER, applicationContext)
//                        .bind(endpoint);
//
//
//
//            } else {
//                Bootstrap bootstrapping = new Bootstrap();
//                bootstrapping.group(epollEventLoopGroup)
//                        .channel(EpollDatagramChannel.class)
//                        .option(ChannelOption.SO_BROADCAST, true)
//                        .option(EpollChannelOption.SO_REUSEPORT, true)
//                        .handler(protocol)
//                        .attr(VD5_CONNECTION_TYPE, ConnectionTypes.UDP)
//                        .attr(VD5_MIN_SPEED_KPH, dcsConfig.getMinSpeedKph())
//                        .attr(VD5_TIMEOUT, dcsConfig.getTimeout())
//                        .attr(VD5_DECODE_LOW, dcsConfig.isDecodeLow())
//                        .attr(VD5_AUTO_ADD, dcsConfig.isAutoAdd())
//                        .attr(VD5_DEVICE_MANAGER, applicationContext);
//                        //.bind(endpoint);
//                List<ChannelFuture> futures = new ArrayList<>(workerNThread);
//
//                for (int i = 0; i < workerNThread; i++) {
//                    futures.add(bootstrapping.bind(endpoint).await());
//                }
//
//                for (ChannelFuture future : futures) {
//                    future.channel().closeFuture().await();
//                }
//            }
//        }
//    }
//
//    private void cache(String sessionId, Channel channel) {
//        channelCache.put(sessionId, channel);
//    }
//
//    public void sendCommand(String name, String command) {
//        Channel channel = channelCache.getIfPresent(name);
//        if (channel != null && channel.isWritable()) {
//            channel.write(command);
//        }
//    }
//
//    @PreDestroy
//    public void destroy() {
//        try {
//            log.info("Shutting dcs");
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//            epollEventLoopGroup.shutdownGracefully();
//        } catch (Exception e) {
//            log.error("Not able to shutdown the proxy", e);
//        }
//    }
//}
//
