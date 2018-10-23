//package com.vd5.dcs.protocols;
//
//import com.vd5.data.StatusCodes;
//import com.vd5.data.entities.*;
//import com.vd5.data.model.AlertType;
//import com.vd5.data.model.geo.*;
//import com.vd5.data.repository.GeofenceRepository;
//import com.vd5.dcs.ApplicationContext;
//import com.vd5.dcs.model.Position;
//import com.vd5.dcs.services.AlertEventLogService;
//import com.vd5.dcs.services.EmailService;
//import com.vd5.data.utils.GeoJsonUtils;
//import com.vd5.dcs2.utils.StringUtils;
//import io.netty.channel.ChannelHandler.Sharable;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageDecoder;
//import org.joda.time.DateTime;
//import org.joda.time.Seconds;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * @author beou on 7/17/18 10:48
// */
//@Sharable
//@Component
//public class AlertHandler extends MessageToMessageDecoder<Position> {
//    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
//
//    private final ApplicationContext applicationContext;
//    private final EmailService emailService;
//    private final AlertEventLogService alertEventLogService;
//    private final GeofenceRepository geofenceRepository;
//
//    public AlertHandler(ApplicationContext applicationContext, EmailService emailService,
//                        AlertEventLogService alertEventLogService, GeofenceRepository geofenceRepository) {
//        this.applicationContext = applicationContext;
//        this.emailService = emailService;
//        this.alertEventLogService = alertEventLogService;
//        this.geofenceRepository = geofenceRepository;
//    }
//
//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, Position position, List<Object> list) throws Exception {
//        String deviceId = position.getDeviceId();
//        Device device = StringUtils.isNotEmpty(deviceId) ? applicationContext.getDevice(deviceId) : null;
//        //EventData lastEvent = deviceManager.getLastEvent(deviceId);
//
//        //-- get list of send and process
//        if (device != null) {
//            Set<AlertProfile> alertProfileSet = device.getAlertProfiles();
//
//            for (AlertProfile ap : alertProfileSet) {
//                switch (ap.getType()) {
//                    case ALERT_START:
//                        //do send starting
//                        alertStart(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_STOP:
//                        alertStop(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_ENGINE_START:
//                        alertEngineStart(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_ENGINE_STOP:
//                        alertEngineStop(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_FUEL_DROP:
//                        alertFuelDrop(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_FUEL_FILL:
//                        alertFuelFill(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_OVER_SPEED:
//                        alertOverSpeed(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_GEOFENCE_IN:
//                        alertGeofenceIn(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_GEOFENCE_OUT:
//                        alertGeofenceOut(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_IGNITION_ON:
//                        alertIgnitionOn(channelHandlerContext, position, ap);
//                        break;
//                    case ALERT_IGNITION_OFF:
//                        alertIgnitionOff(channelHandlerContext, position, ap);
//                        break;
//                }
//            }
//        }
//        list.add(position);
//    }
//
//    /*1*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertStart
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertStart(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-start");
//        String deviceId = position.getDeviceId();
//        EventData eventData = applicationContext.getLastEvent(deviceId);
//        Device device = applicationContext.getDevice(deviceId);
//
//        AlertEventLog lastStartStopLog = alertEventLogService.findStartStop(deviceId);
//        if ((position.getStatusList().contains(StatusCodes.STATUS_MOTION_MOVING) ||
//                position.getStatusList().contains(StatusCodes.STATUS_MOTION_EN_ROUTE) ||
//                position.getStatusList().contains(StatusCodes.STATUS_MOTION_START))) {
//            if (lastStartStopLog.getType()==AlertType.ALERT_STOP) {
//                send(alertProfile, device, "["+ position.getDeviceId() +"] Alert Start", "Device "+ deviceId +" is starting");
//            }
//        }
//
//        saveDB(alertProfile, position, AlertType.ALERT_START);
//    }
//
//    /*2*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertStop
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertStop(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send stop");
//        String deviceId = position.getDeviceId();
//        EventData eventData = applicationContext.getLastEvent(deviceId);
//        Device device = applicationContext.getDevice(deviceId);
//
//        AlertEventLog lastStartStopLog = alertEventLogService.findStartStop(deviceId);
//        if ((position.getStatusList().contains(StatusCodes.STATUS_MOTION_STOPPED) ||
//                position.getStatusList().contains(StatusCodes.STATUS_MOTION_STOP))) {
//            if (lastStartStopLog.getType()==AlertType.ALERT_START) {
//                send(alertProfile, device, "["+ position.getDeviceId() +"] Alert Stop", "Device "+ deviceId +" is stopped");
//            }
//        }
//
//        saveDB(alertProfile, position, AlertType.ALERT_STOP);
//    }
//
//    /*3*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertEngineStart
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertEngineStart(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-engine-start");
//        String deviceId = position.getDeviceId();
//        EventData eventData = applicationContext.getLastEvent(deviceId);
//        Device device = applicationContext.getDevice(deviceId);
//
//        AlertEventLog lastStartStopLog = alertEventLogService.findStartStop(deviceId);
//        if (position.getStatusList().contains(StatusCodes.STATUS_ENGINE_START)) {
//            if (lastStartStopLog.getType()==AlertType.ALERT_ENGINE_STOP) {
//                send(alertProfile, device, "["+ position.getDeviceId() +"] Alert Engine Start", "Device "+ deviceId +" Engine Start");
//            }
//        }
//
//        saveDB(alertProfile, position, AlertType.ALERT_ENGINE_START);
//    }
//
//    /*4*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertEngineStop
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertEngineStop(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-engine-stop");
//        String deviceId = position.getDeviceId();
//        EventData eventData = applicationContext.getLastEvent(deviceId);
//        Device device = applicationContext.getDevice(deviceId);
//
//        AlertEventLog lastStartStopLog = alertEventLogService.findStartStop(deviceId);
//        if (position.getStatusList().contains(StatusCodes.STATUS_ENGINE_STOP)) {
//            if (lastStartStopLog.getType()==AlertType.ALERT_ENGINE_START) {
//                send(alertProfile, device, "["+ position.getDeviceId() +"] Alert Engine Stop", "Device "+ deviceId +" Engine Stop");
//            }
//        }
//
//        saveDB(alertProfile, position, AlertType.ALERT_ENGINE_STOP);
//    }
//
//    /*5*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertFuelDrop
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertFuelDrop(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-fuel-drop");
//    }
//
//    /*6*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertFuelFill
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertFuelFill(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-fuel-fill");
//    }
//
//    /*7*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertOverSpeed
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertOverSpeed(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-over-speed");
//        double limitSpeed = alertProfile.getSpeedKph();
//        String deviceId = position.getDeviceId();
//
//        Device device = applicationContext.getDevice(deviceId);
//        if (position.getSpeedKPH() >= limitSpeed) {
//
//            //-- check last over-speed alert, only send if alert-age > 5 minutes
//            AlertEventLog lastIgnitionLog = alertEventLogService.findIgnition(deviceId);
//            if (Seconds.secondsBetween(DateTime.now(), new DateTime(lastIgnitionLog.getCreatedOn())).isGreaterThan(Seconds.seconds(5*60))) {
//                send(alertProfile, device, "["+deviceId+"] OverSpeeding", "Device [" + deviceId + "] Over Speeding");
//
//                // store alert event
//                saveDB(alertProfile, position, AlertType.ALERT_OVER_SPEED);
//            }
//        }
//    }
//
//    /*8*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertGeofenceIn
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertGeofenceIn(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-geofence-in");
//
//        String deviceId = position.getDeviceId();
//        Device device = applicationContext.getDevice(deviceId);
//        EventData lastEvent = applicationContext.getLastEvent(deviceId);
//
//        Long zoneId = alertProfile.getZoneId();
//        Geofence geofence = geofenceRepository.getOne(zoneId);
//        String geojson = geofence.getGeojson();
//        Geometry geometry = GeometryFactory.getInstance().create(geojson);
//
//        if (geometry != null) {
//            //-- contain current point
//            boolean isCurrentIn = geometry.containsPoint(position.getLatitude(), position.getLongitude());
//            boolean isLastIn = geometry.containsPoint(lastEvent.getLatitude(), lastEvent.getLongitude());
//            if (isCurrentIn && !isLastIn) {
//                send(alertProfile, device, getFormattedSubject(alertProfile, device, position), getFormattedBody(alertProfile, device, position));
//                saveDB(alertProfile, position, AlertType.ALERT_GEOFENCE_IN);
//            }
//        }
//    }
//
//    /*9*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertGeofenceOut
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertGeofenceOut(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        LOGGER.info("[>_] processing send-geofence-out");
//    }
//
//    /*10*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertIgnitionOn
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertIgnitionOn(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        if (position.getStatusList().contains(StatusCodes.STATUS_IGNITION_ON)) {
//            String deviceId = position.getDeviceId();
//            LOGGER.info("[>_] processing send-ignition-on #" + deviceId);
//
//            Device device = applicationContext.getDevice(deviceId);
//            EventData lastEvent = applicationContext.getLastEvent(deviceId);
//
//            AlertEventLog lastIgnitionLog = alertEventLogService.findIgnition(deviceId);
//            if (lastIgnitionLog == null || lastIgnitionLog.getType() == AlertType.ALERT_IGNITION_OFF) {
//                // store alert event
//                saveDB(alertProfile, position, AlertType.ALERT_IGNITION_ON);
//                send(alertProfile, device, "[" + deviceId + "] " + "Ignition On", "Device [" + deviceId + "] + IgnitionOn");
//            }
//        }
//    }
//
//    /*11*/
//    //------------------------------------------------------------------------------------------------------------------
//    //-- alertIgnitionOff
//    //------------------------------------------------------------------------------------------------------------------
//    private void alertIgnitionOff(ChannelHandlerContext channelContext, Position position, AlertProfile alertProfile) {
//        if (position.getStatusList().contains(StatusCodes.STATUS_IGNITION_OFF)) {
//
//            String deviceId = position.getDeviceId();
//            LOGGER.info("[>_] processing send-ignition-off #" + deviceId);
//
//
//            Device device = applicationContext.getDevice(deviceId);
//            EventData lastEvent = applicationContext.getLastEvent(deviceId);
//
//
//            AlertEventLog lastIgnitionLog = alertEventLogService.findIgnition(deviceId);
//            if (lastIgnitionLog == null || lastIgnitionLog.getType() == AlertType.ALERT_IGNITION_ON) {
//                // store alert event
//                saveDB(alertProfile, position, AlertType.ALERT_IGNITION_OFF);
//                send(alertProfile, device, "["+deviceId+"] " + "Ignition Off", "Device [" + deviceId + "] + IgnitionOff");
//            }
//        }
//    }
//
//
//    //------------------------------------------------------------------------------------------------------------------
//    //-- private functions
//    //------------------------------------------------------------------------------------------------------------------
//    private void send(AlertProfile alertProfile, Device device, String subject, String body) {
//        Set<Contact> contactSet = alertProfile.getContacts();
//        if (contactSet != null && contactSet.size() > 0) {
//            for (Contact c : contactSet) {
//                emailService.send(device.getDeviceId(), c.getEmailAddress(), subject, body);
//            }
//        }
//    }
//
//    private void saveDB(AlertProfile profile, Position position, AlertType type) {
//
//        // store alert event
//        Set<Contact> contactSet = profile.getContacts();
//        String receivers = getReceivers(contactSet);
//        AlertEventLog alertEventLog = AlertEventLog.builder()
//                .alertName(profile.getName())
//                .alertDescription(profile.getDescription())
//                .companyId(profile.getCompany().getId())
//                .accountId(profile.getAccount().getId())
//                .deviceId(position.getDeviceId())
//                .eventDataId(0L) //last event id
//                .type(type)
//                .speedKph(position.getSpeedKPH())
//                .zoneId(profile.getZoneId())
//                .alertEmail(profile.isAlertEmail())
//                .alertSms(profile.isAlertSms())
//                .alertApp(profile.isAlertApp())
//                .cannedAction(profile.getCannedAction())
//                .receivers(receivers)
//                .subject(profile.getSubject())
//                .text(profile.getText())
//                .templateId(profile.getTemplateId())
//                .build();
//        alertEventLogService.save(alertEventLog);
//    }
//
//    private String getReceivers(Set<Contact> contactSet) {
//        StringBuilder receiver = new StringBuilder();
//        if (contactSet != null && contactSet.size() > 0) {
//            String prefix = "";
//            for (Contact contact : contactSet) {
//                receiver.append(prefix);
//                prefix = ",";
//                receiver.append(contact.getName());
//            }
//        }
//        return receiver.toString();
//    }
//
//    private String getFormattedSubject(AlertProfile alertProfile, Device device, Position position) {
//        return "Subject: ";
//    }
//
//    private String getFormattedBody(AlertProfile alertProfile, Device device, Position position) {
//        return "Body: ";
//    }
//}
