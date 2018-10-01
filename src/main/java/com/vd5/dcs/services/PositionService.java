///*
// * Copyright (c) 2017. by Vu.Du
// */
//
//package com.vd5.dcs.services;
//
//import com.vd5.data.StatusCodes;
//import com.vd5.data.entities.Device;
//import com.vd5.data.entities.EventData;
//import com.vd5.data.model.CellTower;
//import com.vd5.data.repository.DeviceRepository;
//import com.vd5.data.repository.EventDataRepository;
//import com.vd5.dcs.model.Position;
//import com.vd5.dcs.ApplicationContext;
//import io.netty.channel.ChannelHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.SerializationUtils;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author beou on 11/21/17 18:28
// */
//
//@Slf4j
//@ChannelHandler.Sharable
//@Service
//public class PositionService {
//
//    private final EventDataRepository eventDataRepository;
//    private final DeviceRepository deviceRepository;
//    private final ApplicationContext applicationContext;
//
//    private final List<EventData> eventDataList = Collections.synchronizedList(new ArrayList<>());
//
//    public PositionService(EventDataRepository eventDataRepository,
//                           DeviceRepository deviceRepository, ApplicationContext applicationContext) {
//        this.eventDataRepository = eventDataRepository;
//        this.deviceRepository = deviceRepository;
//        this.applicationContext = applicationContext;
//    }
//
//    public void insertEventData(Position position) {
//        if (position.getDeviceId() == null) {
//            return;
//        }
//
//        EventData evdt = new EventData();
//        Device device = applicationContext.getDevice(position.getDeviceId());
//
//        if (device == null && position.isAutoAddDevice()) {
//            log.info("[>_] Creating new Device #" + position.getDeviceId());
//            device = Device.builder()
//                    .deviceId(position.getDeviceId())
//                    .port(position.getPort())
//                    .imei(position.getDeviceImei())
//                    .protocol(position.getProtocol())
//                    .build();
//            try {
//                deviceRepository.save(device);
//            } catch (Exception e) {
//                log.error("ERROR: " + e.getMessage());
//            } finally {
//                applicationContext.refresh(position.getDeviceId());
//            }
//        }
//
//        if (device != null){
//            evdt.setDevice(device);
//            evdt.setCompany(device.getCompany());
//            evdt.setTimestamp(position.getTimestamp());
//            evdt.setSpeedKPH(position.getSpeedKPH());
//            evdt.setLatitude(position.getLatitude());
//            evdt.setLongitude(position.getLongitude());
//            evdt.setAddress(position.getAddress());
//            evdt.setHeading(position.getHeading());
//            evdt.setSatelliteCount(position.getSatelliteCount());
//            evdt.setAltitude(position.getAltitude());
//            evdt.setFuelLevel(position.getFuelLevel());
//
//            if (evdt.getCellTower() == null) {
//                evdt.setCellTower(new CellTower());
//            }
//
//            if (position.getStatusList() != null && position.getStatusList().size() > 0) {
//                for (Integer sc : position.getStatusList()) {
//                    EventData e = SerializationUtils.clone(evdt);
//                    e.setStatus(sc);
//                    eventDataList.add(e);
//                }
//            } else {
//                evdt.setStatus(StatusCodes.STATUS_LOCATION);
//                eventDataList.add(evdt);
//            }
//        }
//    }
//
//    public void insertEventData(EventData eventData) {
//        eventDataList.add(eventData);
//    }
//
//
//    @Scheduled(fixedRate = 10000)
//    public void scheduleFixedRateTask() {
//        synchronized (eventDataList) {
//            try {
//                if (eventDataList.size() > 0) {
//                    log.info("[>_] saving " + eventDataList.size() + " items");
//                    eventDataRepository.saveAll(eventDataList);
//                }
//            } finally {
//                List<String> deviceIds = eventDataList.stream().map(x -> x.getDevice().getDeviceId()).collect(Collectors.toList());
//                applicationContext.refreshLastEvent(deviceIds);
//                eventDataList.clear();
//            }
//        }
//    }
//}
