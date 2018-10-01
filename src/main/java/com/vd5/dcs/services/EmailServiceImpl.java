//package com.vd5.dcs.services;
//
//import com.vd5.dcs.ApplicationContext;
//import com.vd5.dcs.utils.TimeUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
///**
// * @author beou on 5/17/18 00:28
// */
//@Component
//public class EmailServiceImpl implements EmailService {
//    private final ApplicationContext applicationContext;
//    private final Logger LOG = LoggerFactory.getLogger(getClass());
//
//    private long waitTill;
//
//    public EmailServiceImpl(JavaMailSender mailSender, ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//
//    public void send(String deviceId, String to, String subject, String text) {
//        JavaMailSender sender = applicationContext.getMailSender(deviceId);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        if (TimeUtils.getCurrentTimestamp() >= waitTill) {
//            try {
//                sender.send(message);
//            } catch (Exception e) {
//                LOG.error("Not able to send email now " + e.getMessage());
//                waitTill = TimeUtils.getCurrentTimestamp() + 10*60*1000; //10 minutes
//            }
//        }
//    }
//
//    @Override
//    public void send(String deviceId, String to, String subject, String text, String pathToAttach) {
//
//    }
//
////    private JavaMailSender getMailSender(String deviceId) {
////        JavaMailSenderImpl sender = new JavaMailSenderImpl();
////        Device device = deviceManager.getDevice(deviceId);
////        Company company = device.getCompany();
////
////        SmtpProperties properties = null;
////        if (company != null) {
////            properties = company.getSmtpProperties();
////        }
////
////        if (properties != null) {
////            sender.setHost(properties.getSmtp_host());
////            sender.setPort(properties.getSmtp_port());
////            sender.setUsername(properties.getSmtp_user_name());
////            sender.setPassword(properties.getSmtp_password());
////
////            Properties props = sender.getJavaMailProperties();
////            props.put("mail.transport.protocol", "smtp");
////            props.put("mail.smtp.auth", properties.getSmtp_prop_auth());
////            props.put("mail.smtp.starttls.enable", properties.getSmtp_prop_starttls());
////
////            return sender;
////        } else {
////            return mailSender;
////        }
////    }
//}
