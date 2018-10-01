//package com.vd5.dcs;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//import java.util.List;
//
///**
// * @author beou on 5/8/18 15:56
// */
//@Getter @Setter
//@Configuration
//@PropertySource("classpath:dcs-configuration.properties")
//@ConfigurationProperties(prefix = "dcs")
//public class DcsConfiguration {
//    private int bossNThread;
//    private int workerNThread;
//    private List<Server> serverList;
//
//    @Setter @Getter
//    public static class Server {
//        private String name;
//        private int port;
//        private boolean duplex;
//        private boolean enabled;
//        private String clazz;
//        private String address;
//        private double minSpeedKph;
//
//        private Integer timeout; // sec
//
//        private boolean decodeLow;
//        private boolean autoAdd;
//    }
//}
