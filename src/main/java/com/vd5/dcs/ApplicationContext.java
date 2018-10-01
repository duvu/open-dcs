//package com.vd5.dcs;
//
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.vd5.data.entities.Company;
//import com.vd5.data.entities.Device;
//import com.vd5.data.entities.EventData;
//import com.vd5.data.model.SmtpProperties;
//import com.vd5.dcs.services.DeviceService;
//import com.vd5.dcs.services.EventDataService;
//import io.netty.channel.ChannelHandler.Sharable;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.validation.constraints.NotNull;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//import java.util.Properties;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author beou on 5/14/18 13:35
// */
//
//@Sharable
//@Component
//public class ApplicationContext {
//    private final LoadingCache<String, Optional<Device>> deviceSessions;
//    private final LoadingCache<String, Optional<EventData>> lastEventCache;
//    private final LoadingCache<String, Optional<JavaMailSender>> mailSenders;
//
//    public ApplicationContext(DeviceService deviceService, EventDataService evdtService, JavaMailSender javaMailSender) {
//
//        deviceSessions = CacheBuilder.newBuilder()
//                .expireAfterAccess(1, TimeUnit.DAYS)
//                .refreshAfterWrite(1, TimeUnit.MINUTES)
//                .maximumSize(1000)
//                .build(new CacheLoader<String, Optional<Device>>() {
//
//                    @Override
//                    public Optional<Device> load(@NotNull String s) throws Exception {
//                        return Optional.ofNullable(deviceService.getOne(s));
//                    }
//                });
//
//        CacheLoader<String, Optional<EventData>> in = new CacheLoader<String, Optional<EventData>>() {
//
//            @Override
//            public Optional<EventData> load(@NotNull String s) throws Exception {
//                Device device = deviceSessions.get(s).orElse(null);
//                if (device == null) {
//                    return Optional.empty();
//                } else {
//                    EventData evdt = evdtService.getLastEvent(device.getId());
//                    return Optional.ofNullable(evdt);
//                }
//            }
//        }; // com.google.common.cache.CacheLoader
//        lastEventCache = CacheBuilder.newBuilder()
//                .expireAfterAccess(1, TimeUnit.DAYS)
//                .refreshAfterWrite(8, TimeUnit.HOURS)
//                .build(in);
//
//        mailSenders = CacheBuilder.newBuilder()
//                .expireAfterWrite(1,TimeUnit.DAYS)
//                .maximumSize(1000)
//                .build(new CacheLoader<String, Optional<JavaMailSender>>() {
//
//                    @Override
//                    public Optional<JavaMailSender> load(@NotNull String s) throws Exception {
//                        Device device = deviceSessions.get(s).orElse(null);
//                        if (device != null) {
//                            Company company = device.getCompany();
//                            SmtpProperties properties = company != null ? company.getSmtpProperties() : null;
//                            return createJavaMailSender(properties, javaMailSender);
//                        } else {
//                            return Optional.of(javaMailSender);
//                        }
//                    }
//                });
//
//    }
//
//    //------------------------------------------------------------------------------------------------------------------
//    //------------------------------------------------------------------------------------------------------------------
//    private Optional<JavaMailSender> createJavaMailSender(SmtpProperties properties, JavaMailSender defaultSender) {
//        if (properties == null) {
//            return Optional.of(defaultSender);
//        } else {
//            JavaMailSenderImpl sender = new JavaMailSenderImpl();
//            sender.setHost(properties.getSmtp_host());
//            sender.setPort(properties.getSmtp_port());
//            sender.setUsername(properties.getSmtp_user_name());
//            sender.setPassword(properties.getSmtp_password());
//
//            Properties props = sender.getJavaMailProperties();
//            props.put("mail.transport.protocol", "smtp");
//            props.put("mail.smtp.auth", properties.getSmtp_prop_auth());
//            props.put("mail.smtp.starttls.enable", properties.getSmtp_prop_starttls());
//
//            return Optional.of(sender);
//        }
//    }
//
//    public void put(String key, Object session) {
//        if (session instanceof Device) {
//            deviceSessions.put(key, Optional.of((Device) session));
//        } else if (session instanceof EventData) {
//            lastEventCache.put(key, Optional.of((EventData) session));
//        }
//    }
//
//    public Device getDevice(String key) {
//        try {
//            return deviceSessions.get(key).orElse(null);
//        } catch (ExecutionException e) {
//            return null;
//        }
//    }
//
//    public JavaMailSender getMailSender(String key) {
//        try {
//            return mailSenders.get(key).orElse(null);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            mailSenders.refresh(key);
//            return null;
//        }
//    }
//
//    public EventData getLastEvent(String deviceId) {
//        try {
//            Optional<EventData> evdt = lastEventCache.get(deviceId);
//            return evdt.orElse(null);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            lastEventCache.refresh(deviceId);
//            return null;
//        }
//    }
//
//    public Collection<Optional<EventData>> getAllLastEvent() {
//        return lastEventCache.asMap().values();
//    }
//
//    public void refresh(String key) {
//        deviceSessions.refresh(key);
//    }
//
//    @Transactional
//    public void refreshLastEvent(List<String> deviceIds) {
//        for (String id : deviceIds) {
//            lastEventCache.refresh(id);
//        }
//    }
//}
