package com.vd5.dcs2;

import com.vd5.dcs2.websocket.WebSocketClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author beou on 10/1/18 17:33
 */
public class Main {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        Log.info("Initiating DCS server ...");
        try {
            ApplicationContext.getServerManager().start();
        } catch (InterruptedException e) {
            Log.error("Not able to start DCS server", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                Log.info("[>_] Shutting down Scheduler#!");
                scheduler.shutdown();
            }
        });

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Log.info("[>_] HEARTBEAT+");
                WebSocketClient websocket = ApplicationContext.getWebClient();
                if (!websocket.isClose()) {
                    ApplicationContext.getWebClient().ping();
                }
            }
        }, 3, 1, TimeUnit.MINUTES);
    }
}
