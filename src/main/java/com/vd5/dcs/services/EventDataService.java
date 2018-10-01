//package com.vd5.dcs.services;
//
//import com.vd5.data.entities.EventData;
//import com.vd5.data.repository.EventDataRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author beou on 5/15/18 16:11
// */
//@Service
//public class EventDataService {
//
//    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
//
//    private final EventDataRepository eventDataRepository;
//
//
//    public EventDataService(EventDataRepository eventDataRepository) {
//        this.eventDataRepository = eventDataRepository;
//    }
//
//    public EventData getLastEvent(Long deviceId) {
//        List<EventData> evdtList = eventDataRepository.getLastEventData(deviceId);
//        EventData lastEvent = (evdtList != null && evdtList.size() > 0) ? evdtList.get(0) : null;
//        return lastEvent;
//    }
//}
