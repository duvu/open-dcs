package com.vd5.dcs.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author beou on 12/26/17 10:32
 */
@Getter @Setter
public class Position implements Serializable {

    private static final long serialVersionUID = -8621975637574810233L;

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    private boolean autoAddDevice;      // add device automatically

    private String remoteAddress;


    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    private List<Integer> statusList;

    private Boolean outdated;

    private Long deviceId;

    private String uniqueId;

    private String deviceImei;

    private boolean valid;

    private String protocol;

    private int port;

    private String ipAddress;

    private long timestamp;

    private long fixedtime;

    private double latitude;

    private double longitude;

    private double altitude;

    private double speed;

    private double heading;

    private double accuracy;

    private String address;

    private double distanceKM;

    private double odometerKM;

    private double odometterOffsetKM;

    private long geozoneId;

    //-- gps information
    private int gpsFixType; // fix type 0/1 NONE, 2 = 2D, 3 = 3D

    private long gpsFixStatus; //

    private long gpsAge;

    private double horzAccuracy; // horizontal accuracy (metters)

    private double vertAccuracy; // vertical accuracy (metters)

    private int HDOP;

    private int satelliteCount;

    private double batteryLevel;

    private double batteryVolts;

    private double batteryTemp;

    private double signalStrength;

    private String driverId;

    private long driverStatus;

    private String driverMessage;

    private long jobNumber;

    private String rfidTag;

    //-- OBD fields;
    private double fuelPressure;

    private double fuelUsage;

    private double fuelTemp;

    private double fuelLevel;

    private double fuelLevel2;

    private double fuelRemain;

    private long engineRpm;

    private double engineHours;

    private double engineOnHours;

    private double engineLoad;

    private double idleHours;

    private double workHours;

    private double tirePressure;

    private double tireTemp;

    private CellTower cellTower;

    private String rawData;

    public Position(String protocol) {
        this.protocol = protocol;
        this.statusList = new ArrayList<>();
    }

    public Position() {
        this.statusList = new ArrayList<>();
    }

    public void addStatus(int status) {
        statusList.add(status);
    }
}
