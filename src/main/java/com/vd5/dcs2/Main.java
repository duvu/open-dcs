package com.vd5.dcs2;

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
            ApplicationContext.getWebClient().open();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.error("ERROR", e);
        } catch (Exception e) {
            e.printStackTrace();
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
                Log.info("[>_] Sending data!");
                ApplicationContext.getWebClient().ping();
            }
        }, 3, 10, TimeUnit.SECONDS);
    }
}
