//package com.vd5.dcs;
//
//import com.vd5.data.StatusCodes;
//import com.vd5.data.entities.EventData;
//import com.vd5.dcs.services.PositionService;
//import com.vd5.dcs.utils.TimeUtils;
//import org.apache.commons.lang3.SerializationUtils;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.Optional;
//
///**
// * @author beou on 9/1/18 15:01
// */
//
//@Component
//public class ScheduledJobs {
//
//    private final ApplicationContext applicationContext;
//    private final PositionService positionService;
//
//    public ScheduledJobs(ApplicationContext applicationContext,
//                         PositionService positionService) {
//        this.applicationContext = applicationContext;
//        this.positionService = positionService;
//    }
//
//    // check every minute
//    @Scheduled(fixedRate = 1000 * 60)
//    public void checkIfStopped() {
//        //-- a vehicle/device is stopped if server not got any event for last 5 minutes
//        Collection<Optional<EventData>> dataCollection = applicationContext.getAllLastEvent();
//        for (Optional<EventData> oed : dataCollection) {
//            EventData last = oed.orElse(null);
//            if ((last != null) &&
//                    (last.getStatus() != StatusCodes.STATUS_MOTION_STOPPED) &&
//                    TimeUtils.distanceToNow(last.getTimestamp()) >= 10*60*1000) {
//
//                EventData evdt = SerializationUtils.clone(last);
//                evdt.setId(null);
//                evdt.setSignalStrength(0);
//                evdt.setSatelliteCount(0);
//                evdt.setStatus(StatusCodes.STATUS_MOTION_STOPPED);
//                positionService.insertEventData(evdt);
//            }
//        }
//    }
//}
