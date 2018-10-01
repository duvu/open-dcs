//package com.vd5.dcs.services;
//
//import com.vd5.data.entities.AlertEventLog;
//import com.vd5.data.model.AlertType;
//import com.vd5.data.repository.AlertEventLogRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author beou on 9/10/18 16:27
// */
//
//@Service
//public class AlertEventLogService {
//    private final AlertEventLogRepository alertEventLogRepository;
//
//    public AlertEventLogService(AlertEventLogRepository alertEventLogRepository) {
//        this.alertEventLogRepository = alertEventLogRepository;
//    }
//
//    public AlertEventLog save(AlertEventLog eventLog) {
//        return alertEventLogRepository.save(eventLog);
//    }
//
//    public AlertEventLog findIgnition(String deviceId) {
//        AlertType[] types = {AlertType.ALERT_IGNITION_ON, AlertType.ALERT_IGNITION_OFF};
//        return alertEventLogRepository.findLastIn(deviceId, types);
//    }
//
//    public AlertEventLog findStartStop(String deviceId) {
//        AlertType[] types = {AlertType.ALERT_STOP, AlertType.ALERT_START, AlertType.ALERT_ENGINE_START, AlertType.ALERT_ENGINE_STOP};
//        return alertEventLogRepository.findLastIn(deviceId, types);
//    }
//}
