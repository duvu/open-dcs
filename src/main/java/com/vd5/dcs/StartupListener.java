//package com.vd5.dcs;
//
//import com.vd5.dcs.protocols.ServerManager;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//import javax.validation.constraints.NotNull;
//
///**
// * @author beou on 5/9/18 03:36
// */
//@Component
//public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
//    private final ServerManager serverManager;
//    private final Logger log = LoggerFactory.getLogger(getClass());
//    public StartupListener(ServerManager serverManager) {
//        this.serverManager = serverManager;
//    }
//
//    @Override
//    public void onApplicationEvent(@NotNull ApplicationReadyEvent contextStartedEvent) {
//        log.info("[>_] application ready");
//        try {
//            serverManager.init();
//        } catch (IllegalAccessException | InterruptedException | InstantiationException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
