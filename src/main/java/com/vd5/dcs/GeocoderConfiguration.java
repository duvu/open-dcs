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
// * @author beou on 9/12/18 11:47
// */
//
//@Getter
//@Setter
//@Configuration
//@PropertySource("classpath:geocoder.properties")
//@ConfigurationProperties
//public class GeocoderConfiguration {
//    List<Geocoder> geocoder;
//
//    @Getter @Setter
//    public static class Geocoder {
//        private String name;
//        private String key;
//        private String url;
//        private String clazz;
//    }
//}
