package com.vd5.dcs2;

/**
 * @author beou on 10/1/18 17:33
 */
public class Main {

    public static void main(String[] args) {
        Log.info("Initiating DCS server ...");
        try {
            ApplicationContext.getWebClient().send("###Test###");
            ApplicationContext.getServerManager().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.error("ERROR", e);
        }
    }
}
