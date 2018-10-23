//package com.vd5.dcs.protocols;
//
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.google.common.util.concurrent.SettableFuture;
//import com.vd5.dcs.GeocoderConfiguration;
//import com.vd5.dcs.geocoder.*;
//import com.vd5.dcs.model.Position;
//import com.vd5.dcs2.utils.Circular;
//import io.netty.channel.ChannelHandler.Sharable;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageDecoder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import javax.validation.constraints.NotNull;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author beou on 11/25/17 01:49
// */
//@Sharable
//@Component
//public class GeocoderHandler extends MessageToMessageDecoder<Position> {
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    private final GeocoderConfiguration geocoderConfiguration;
//
////    private final Geocoder geocoder = new NominatimGeocoder("http://144.217.7.179/reverse.php",
////            "cuztXOMLF97Dm66u49UBEoNkk0gysgqs","en", 1, new AddressFormat());
//
////    private final Geocoder googleGeocoder = new GoogleGeocoder("AIzaSyDA5FASaYQWmUmXHgZoe8bFDZT6EgjrUHc", "en", 1, new AddressFormat());
////    private final Geocoder mapquestGeocoder = new MapQuestGeocoder("http://www.mapquestapi.com/geocoding/v1/reverse","28NT1b4gUWITckIkNAmtgwG0cGULPgse", 1, new AddressFormat());
//
//    //TODO implement round-robin (using Guava  - )
//    // https://stackoverflow.com/questions/22869350/java-implementing-a-round-robin-circular-list-and-counting-elements-access-c
//    private final LoadingCache<String, Optional<String>> geoCached;
//    private final Circular<Geocoder> circular;
//
//
//    public GeocoderHandler(GeocoderConfiguration geocoderConfiguration) {
//        this.geocoderConfiguration = geocoderConfiguration;
//
//        List<Geocoder> geocoderList = geocoderConfiguration.getGeocoder().stream().map(x -> {
//            String name = x.getName();
//            String className = x.getClazz();
//            String key = x.getKey();
//            String url = x.getUrl();
//
//            AddressFormat addressFormat = new AddressFormat();
//
//            switch (name) {
//                case "google":
//                    return new GoogleGeocoder(key, "en", addressFormat);
//                case "mapquest":
//                    return new MapQuestGeocoder(url, key, addressFormat);
//                case "bing":
//                    return new BingMapsGeocoder(url, key, addressFormat);
//                case "here":
//                    return new HereGeocoder(url, key, addressFormat);
//                default:
//                    return new NominatimGeocoder(url, key, "en", addressFormat);
//            }
//        }).collect(Collectors.toList());
//
//        circular = new Circular<>(geocoderList);
//
//        geoCached  = CacheBuilder.newBuilder()
//                .maximumSize(10000)
//                .expireAfterAccess(8, TimeUnit.HOURS)
//                .build(new CacheLoader<String, Optional<String>>() {
//
//                    @Override
//                    public Optional<String> load(@NotNull String s) throws Exception {
//                        double latitude = Double.parseDouble(s.split(",")[0]);
//                        double longitude = Double.parseDouble(s.split(",")[1]);
//                        SettableFuture<String> rtn = SettableFuture.create();
//                        circular.getOne().getAddress(latitude, longitude, new Geocoder.ReverseGeocoderCallback() {
//                            @Override
//                            public void onSuccess(String address) {
//                                rtn.set(address);
//                            }
//
//                            @Override
//                            public void onFailure(Throwable e) {
//                                circular.getOne().getAddress(latitude, longitude, new Geocoder.ReverseGeocoderCallback() {
//
//                                    @Override
//                                    public void onSuccess(String address) {
//                                        rtn.set(address);
//                                    }
//
//                                    @Override
//                                    public void onFailure(Throwable e) {
//                                        rtn.setException(e);
//                                    }
//                                });
//                            }
//                        });
//                        return Optional.of(rtn.get());
//                    }
//                });
//    }
//
//    /**
//     * Decode from one message to an other. This method will be called for each written message that can be handled
//     * by this encoder.
//     *
//     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToMessageDecoder} belongs to
//     * @param msg the message to decode to an other one
//     * @param out the {@link List} to which decoded messages should be added
//     */
//    @Override
//    protected void decode(ChannelHandlerContext ctx, Position msg, List<Object> out) {
//        //-- get address and return
//        double latitude = msg.getLatitude();
//        double longitude = msg.getLongitude();
//        String latlng = latitude + "," + longitude;
//        try {
//            String address = geoCached.get(latlng).orElse("No Address");
//            //log.info("Address: " + address);
//            msg.setAddress(address);
//        } catch (ExecutionException e) {
//            log.warn("Not found address #" + latlng, e);
//            geoCached.refresh(latlng);
//        } finally {
//            out.add(msg);
//        }
//    }
//}
