///*
// * Copyright (c) 2017. by Vu.Du
// */
//
//package com.vd5.dcs.protocols.wlink;
//
//import com.vd5.data.entities.EventData;
//import com.vd5.data.model.CellTower;
//import com.vd5.dcs.helper.Parser;
//import com.vd5.dcs.model.Position;
//import com.vd5.dcs.ApplicationContext;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageDecoder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//
//import static com.vd5.dcs.protocols.ServerManager.VD5_AUTO_ADD;
//import static com.vd5.dcs.protocols.ServerManager.VD5_DEVICE_MANAGER;
//import static com.vd5.dcs.protocols.ServerManager.VD5_MIN_SPEED_KPH;
//
///**
// * @author beou on 11/8/17 16:43
// */
//public class WlinkProtocolDecoder extends MessageToMessageDecoder<String> {
//
//    //-- General position report
//    private static final String FRI = "FRI"; //GL200, GL300, ATPlus,      , GV500
//    private static final String CTN = "CTN"; //     ,      ,       , GL500
//    private static final String GEO = "GEO"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String SPD = "SPD"; //GL200, GL300, ATPlus       , GV500
//    private static final String SOS = "SOS"; //GL200, GL300, ATPlus
//    private static final String RTL = "RTL"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String STR = "STR"; //                    , GL500
//    private static final String PNL = "PNL"; //GL200, GL300, ATPlus
//    private static final String NMR = "NMR"; //GL200, GL300, ATPlus, GL500
//    private static final String DIS = "DIS"; //GL200, GL300, ATPlus,      , GV500
//    private static final String DOG = "DOG"; //GL200, GL300, ATPlus       , GV500
//    private static final String IGL = "IGL"; //     , GL300,       ,      , GV500
//    private static final String TOW = "TOW"; //                           , GV500
//    private static final String HBM = "HBM"; //                           , GV500
//    private static final String EPS = "EPS"; //                           , GV500
//    private static final String GES = "GES"; //                           , GV500
//
//    //--Location by call report
//    private static final String LBC = "LBC"; //GL200, GL300, ATPlus,      , GV500
//
//    //--Location as center of geo-fence
//    private static final String GCR = "GCR"; //GL200, GL300, ATPlus
//
//    //--History report
//    private static final String HIS = "HIS"; //     ,      , ATPlus
//
//    //--Device infomation report
//    private static final String INF = "INF"; //GL200, GL300, ATPlus,      , GV500
//
//    //--Lightness information report
//    private static final String LGT = "LGT"; //     ,      , ATPlus
//
//    //--Report for querying
//    private static final String DIF = "DIF"; //     ,      ,       , GL500
//    private static final String GPS = "GPS"; //GL200, GL300, ATPlus,      , GV500
//    private static final String ALL = "ALL"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String CID = "CID"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String CSQ = "CSQ"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String VER = "VER"; //GL200, GL300, ATPlus,      , GV500
//    private static final String BAT = "BAT"; //GL200, GL300, ATPlus,      , GV500
//    private static final String TMZ = "TMZ"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String ALS = "ALS"; //     , GL300,       ,      , GV500
//    private static final String GSV = "GSV"; //     , GL300
//
//    //--Event Report
//    private static final String PNA = "PNA"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String PFA = "PFA"; //GL200, GL300, ATPlus,      , GV500
//    private static final String EPN = "EPN"; //GL200, GL300, ATPlus
//    private static final String EPF = "EPF"; //GL200, GL300, ATPlus
//    private static final String BPL = "BPL"; //GL200, GL300, ATPlus, GL500
//    private static final String BTC = "BTC"; //GL200, GL300, ATPlus, GL500, GV500
//    private static final String STC = "STC"; //GL200, GL300, ATPlus
//    private static final String STT = "STT"; //GL200, GL300, ATPlus
//    private static final String ANT = "ANT"; //GL200,      , ATPlus
//    private static final String PDP = "PDP"; //GL200, GL300, ATPlus, GL500
//    private static final String SWG = "SWG"; //GL200, GL300, ATPlus
//    private static final String IGN = "IGN"; //GL200, GL300,
//    private static final String IGF = "IGF"; //GL200, GL300,
//    private static final String GSM = "GSM"; //     , GL300, ATPlus, GL500
//    private static final String TEM = "TEM"; //     , GL300,       , GL500
//    private static final String UPC = "UPC"; //     , GL300
//    private static final String JDR = "JDR"; //     , GL300
//    private static final String JDS = "JDS"; //     , GL300
//    private static final String DAT = "DAT"; //     , GL300
//    private static final String MPN = "MPN"; //     ,      ,        ,     , GV500
//    private static final String MPF = "MPF"; //     ,      ,        ,     , GV500
//
//    //Hearbeat
//    private static final String HBD = "HBD"; //GL200, GL300, ATPlus, GL500, GV500
//
//
//
//    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
//
//    private boolean isAutoAdd;
//    private double minSpeedKPH;
//    private ApplicationContext applicationContext;
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
//        isAutoAdd = ctx.channel().attr(VD5_AUTO_ADD).get();
//        minSpeedKPH = ctx.channel().attr(VD5_MIN_SPEED_KPH).get();
//        applicationContext = ctx.channel().attr(VD5_DEVICE_MANAGER).get();
//    }
//
//    /**
//     * Decode from one message to an other. This method will be called for each written message that can be handled
//     * by this encoder.
//     *
//     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToMessageDecoder} belongs to
//     * @param msg the message to decode to an other one
//     * @param out the {@link List} to which decoded messages should be added
//     * @throws Exception is thrown if an error occurs
//     */
//    @Override
//    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
//        if (msg.startsWith("+ACK")) {
//            handleACK(ctx, msg);
//            //decode ACK and write answer
//        } else { // +RESP
//            Position p = handlePacket(ctx, msg);
//            if (p != null) {
//                out.add(p);
//            }
//        }
//    }
//
//    private void handleACK(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("ACK: " + msg);
//    }
//
//    private Position handlePacket(ChannelHandlerContext ctx, String msg) {
//        int typeIndex = msg.indexOf(":GT");
//        if (typeIndex < 0) {
//            return null;
//        }
//        //STT, EPF, EPN, BPL,
//        String type = msg.substring(typeIndex + 3, typeIndex + 6);
//        switch (type) {
//            case NMR:
//            case PNL:
//            case GEO:
//            case SPD:
//            case SOS:
//            case RTL:
//            case DIS:
//            case DOG:
//            case IGL:
//            case FRI:
//                return handleFRI(ctx, msg);
//
//            case CTN:
//                return handleCTN(ctx, msg);
//            case STR:
//                return handleSTR(ctx, msg);
//            case TOW:
//                return handleTOW(ctx, msg);
//            case HBM:
//                return handleHBM(ctx, msg);
//            case EPS:
//                return handleEPS(ctx, msg);
//            case GES:
//                return handleOTHER(ctx, msg);
//
//            //--Location by call report
//            case LBC:
//                return handleLBC(ctx, msg);
//
//            //--Location as center of geo-fence
//            case GCR:
//                return handleGCR(ctx, msg);
//
//            //--History report
//            case HIS:
//                return handleHIS(ctx, msg);
//
//            case INF:
//                return handleINF(ctx, msg);
//
//            //--Lightness information report
//            case LGT:
//                return handleLGT(ctx, msg);
//
//            //--Report for querying
//            case DIF:
//                return handleDIF(ctx, msg);
//            case GPS:
//                return handleGPS(ctx, msg);
//            case ALL:
//                return handleALL(ctx, msg);
//            case CID:
//                return handleCID(ctx, msg);
//            case CSQ:
//                return handleCSQ(ctx, msg);
//            case VER:
//                return handleVER(ctx, msg);
//            case BAT:
//                return handleBAT(ctx, msg);
//            case TMZ:
//                return handlTMZ(ctx, msg);
//            case ALS:
//                return handleALS(ctx, msg);
//            case GSV:
//                return handleGSV(ctx, msg);
//
//            //--Event Report
//            case PNA:
//                return handlePNA(ctx, msg);
//            case PFA:
//                return handlePFA(ctx, msg);
//            case EPN:
//                return handleEPN(ctx, msg);
//            case EPF:
//                return handleEPF(ctx, msg);
//            case BPL:
//                return handleBPL(ctx, msg);
//            case BTC:
//                return handleBTC(ctx, msg);
//            case STC:
//                return handleSTC(ctx, msg);
//            case STT:
//                return handleSTT(ctx, msg);
//            case ANT:
//                return handleANT(ctx, msg);
//            case PDP:
//                return handlePDP(ctx, msg);
//            case SWG:
//                return handleSWG(ctx, msg);
//            case IGN:
//                return handleIGN(ctx, msg);
//            case IGF:
//                return handleIGF(ctx, msg);
//            case GSM:
//                return handleGSM(ctx, msg);
//            case TEM:
//                return handleTEM(ctx, msg);
//            case UPC:
//                return handleUPC(ctx, msg);
//            case JDR:
//                return handleJDR(ctx, msg);
//            case JDS:
//                return handleJDS(ctx, msg);
//            case DAT:
//                return handleDAT(ctx, msg);
//            case MPN:
//                return handleMPN(ctx, msg);
//            case MPF:
//                return handleMPF(ctx, msg);
//
//            //Hearbeat
//            case HBD:
//                return handleHDB(ctx, msg);
//
//            case "OBD":
//                return handleOBD(ctx, msg);
//            case "CAN":
//                return handleCAN(ctx, msg);
//            case "ERI":
//                return handleERI(ctx, msg);
//            case "LSW":
//            case "TSW":
//                return handleLSW(ctx, msg);
//            case "IDA":
//                return handleIDA(ctx, msg);
//            case "WIF":
//                return handleWIF(ctx, msg);
//            default:
//                return handleOTHER(ctx, msg);
//        }
//    }
//
//    private Position handleHDB(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("HDB: " + msg);
//        return null;
//    }
//
//    private Position handleMPF(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("MPF: " + msg);
//        return null;
//    }
//
//    private Position handleMPN(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("MPN: " + msg);
//        return null;
//    }
//
//    private Position handleDAT(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("DAT: " + msg);
//        return null;
//    }
//
//    private Position handleJDS(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("JDS: " + msg);
//        return null;
//    }
//
//    private Position handleJDR(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("JDR: " + msg);
//        return null;
//    }
//
//    private Position handleUPC(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("UPC: " + msg);
//        return null;
//    }
//
//    private Position handleTEM(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("TEM: " + msg);
//        return null;
//    }
//
//    private Position handleIGF(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("IGF: " + msg);
//        return null;
//    }
//
//    private Position handleIGN(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("IGN: " + msg);
//        return null;
//    }
//
//    private Position handleSWG(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("SWG: " + msg);
//        return null;
//    }
//
//    private Position handlePDP(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("PDP: " + msg);
//        return null;
//    }
//
//    private Position handleANT(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("ANT: " + msg);
//        return null;
//    }
//
//    private Position handleSTC(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("STC: " + msg);
//        return null;
//    }
//
//    private Position handleBTC(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("BTC: " + msg);
//        return null;
//    }
//
//    private Position handleBPL(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("BPL: " + msg);
//        Parser parser = new Parser(WlinkPatterns.PATTERN_BPL, msg);
//
//        if (!parser.matches()) {
//            LOGGER.info("NO MATCHES");
//            return null;
//        }
//
//        String imei = parser.next();
//        double battVolt = parser.nextDouble();
//        Position position = new Position();
//        position.setDeviceImei(imei);
//        position.setDeviceId(imei);
//        position.setBatteryVolts(battVolt);
//
//        decodeLocation(parser, position);
//        decodeDeviceTime(parser, position);
//        return position;
//    }
//
//    private Position handleEPF(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("EPF: " + msg);
//        return null;
//    }
//
//    private Position handleEPN(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("EPN: " + msg);
//        return null;
//    }
//
//    private Position handlePFA(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("PFA: " + msg);
//        return null;
//    }
//
//    private Position handlePNA(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("PNA: " + msg);
//        return null;
//    }
//
//    private Position handleGSV(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("GSV: " + msg);
//        return null;
//    }
//
//    private Position handleALS(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("ALS: " + msg);
//        return null;
//    }
//
//    private Position handlTMZ(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("TMZ: " + msg);
//        return null;
//    }
//
//    private Position handleBAT(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("BAT: " + msg);
//        return null;
//    }
//
//    private Position handleCSQ(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("CSQ: " + msg);
//        return null;
//    }
//
//    private Position handleCID(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("CID: " + msg);
//        return null;
//    }
//
//    private Position handleALL(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("ALL: " + msg);
//        return null;
//    }
//
//    private Position handleGPS(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("GPS: " + msg);
//        return null;
//    }
//
//    private Position handleDIF(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("DIF: " + msg);
//        return null;
//    }
//
//    private Position handleLGT(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("LGT: " + msg);
//        return null;
//    }
//
//    private Position handleHIS(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("HIS: " + msg);
//        return null;
//    }
//
//    private Position handleGCR(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("GCR: " + msg);
//        return null;
//    }
//
//    private Position handleLBC(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("LBC: " + msg);
//        return null;
//    }
//
//    private Position handleEPS(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("EPS: " + msg);
//        return null;
//    }
//
//    private Position handleHBM(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("HBM: " + msg);
//        return null;
//    }
//
//    private Position handleTOW(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("TOW: " + msg);
//        return null;
//    }
//
//    private Position handleSTR(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("STR: " + msg);
//        return null;
//    }
//
//    private Position handleCTN(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("CTN: " + msg);
//        return null;
//    }
//
//    private Position handleINF(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("INF");
//        Parser parser = new Parser(WlinkPatterns.PATTERN_INF, msg);
//
//        if (!parser.matches()) {
//            LOGGER.info("NOT MATCHES");
//            return null;
//        }
//
//        return null;
//    }
//
//    private Position handleOBD(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] OBD");
//        return  new Position();
//    }
//
//    private Position handleCAN(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] CAN");
//        return new Position();
//    }
//
//    private Position handleFRI(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] FRI");
//
//        Parser parser = new Parser(WlinkPatterns.PATTERN_FRI, msg);
//
//        if (!parser.matches()) {
//            return null;
//        }
//
//        LinkedList<Position> positions = new LinkedList<>();
//
//        String uniqueId = parser.next();
//        String vin = parser.next();
//        Integer power = parser.nextInt();
//
//        LOGGER.info("[>_] UniqueID:     " + uniqueId);
//        LOGGER.info("[>_] vin:          " + vin);
//        LOGGER.info("[>_] power:        " + power);
//
//        Parser itemParser = new Parser(WlinkPatterns.PATTERN_LOCATION, parser.next());
//        while (itemParser.find()) {
//            Position position = new Position();
//
//            //set auto-add
//            position.setAutoAddDevice(isAutoAdd);
//
//            position.setDeviceId(uniqueId);
//            decodeLocation(itemParser, position);
//            positions.add(position);
//        }
//        Position position = positions.getLast();
//        decodeLocation(parser, position);
//
//        if (power != null && power > 10) {
//            position.setBatteryLevel(power * 0.001); // only on some devices
//        }
//
//        return position;
//    }
//
//    private Position handleERI(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] ERI");
//        return new Position();
//    }
//
//    private Position handleSTT(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("STT: " + msg);
//        Parser parser = new Parser(WlinkPatterns.PATTERN_STT, msg);
//        Position position = new Position();
//
//        if (!parser.matches()) {
//            LOGGER.info("NOT MATCHES");
//            return null;
//        }
//        String imei = parser.next();
//        position.setDeviceId(imei);
//        position.setDeviceImei(imei);
//
//        int state = parser.nextInt();
//
//        decodeLocation(parser, position);
//        decodeDeviceTime(parser, position);
//        return position;
//    }
//
//    private Position handleING(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] ING");
//        Parser parser = new Parser(WlinkPatterns.PATTERN_IGN, msg);
//        if (!parser.matches()) {
//            return null;
//        }
//
//        Position position = new Position();
//
//        String imei = parser.next();
//        Long ignDuratiron = parser.nextLong();
//
//        LOGGER.info("IMEI:      " + imei);
//        LOGGER.info("IGN-DUR    " + ignDuratiron);
//
//        position.setDeviceId(imei);
//        position.setDeviceImei(imei);
//        decodeLocation(parser, position);
//        decodeDeviceTime(parser, position);
//
//        return position;
//    }
//
//    private Position handleLSW(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] LSW");
//        return new Position();
//    }
//
//    private Position handleIDA(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] IDA");
//        return new Position();
//    }
//
//    private Position handleWIF(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] WIF");
//        return new Position();
//    }
//
//    private Position handleGSM(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] GSM");
//        return new Position();
//    }
//
//    private Position handleVER(ChannelHandlerContext ctx, String msg) {
//        LOGGER.info("[Wlink >_] VER");
//        return new Position();
//    }
//
//    private Position handleOTHER(ChannelHandlerContext ctx, String msg) {
//        return null;
//    }
//
//    private void decodeLocation(Parser parser, Position position) {
//        int hdop = parser.nextInt(0);
//        double speed = parser.nextDouble(0);
//        double course = parser.nextDouble(0);
//        double altitude = parser.nextDouble(0);
//
//        if (speed < minSpeedKPH) {
//            speed = 0;
//        }
//
//        position.setHDOP(hdop);
//        position.setSpeedKPH(speed);
//        position.setHeading(course);
//        position.setAltitude(altitude);
//
//        LOGGER.info("[>_] HDOP: " + hdop);
//        LOGGER.info("[>_] SpeedKPH: " + speed);
//        LOGGER.info("[>_] Heading: " + course);
//
//        if (parser.hasNext(8)) {
//            position.setValid(true);
//
//            Double longitude = parser.nextDouble();
//            Double latitude = parser.nextDouble();
//
//            LOGGER.info("[>_] Longitude: " + longitude);
//            LOGGER.info("[>_] Latitude: " + latitude);
//
//            position.setLongitude(longitude);
//            position.setLatitude(latitude);
//            long timestamp = parser.nextDateTime().getTime();
//            position.setTimestamp(timestamp);
//            position.setFixedtime(timestamp);
//        } else {
//            getLastLocation(position);
//        }
//
//        if (parser.hasNext(6)) {
//            int mcc = parser.nextInt();
//            int mnc = parser.nextInt();
//            if (parser.hasNext(2)) {
//                position.setCellTower(CellTower.builder()
//                        .mcc(mcc)
//                        .mnc(mnc)
//                        .lac(parser.nextInt())
//                        .cid(parser.nextInt())
//                        .build());
//            }
//            if (parser.hasNext(2)) {
//                position.setCellTower(CellTower.builder()
//                        .mcc(mcc)
//                        .mnc(mnc)
//                        .lac(parser.nextHexInt())
//                        .cid(parser.nextHexInt())
//                        .build());
//            }
//        }
//
//        if (parser.hasNext()) {
//            position.setOdometerKM(parser.nextDouble());
//        }
//    }
//
//    private void decodeDeviceTime(Parser parser, Position position) {
//        if (parser.hasNext(6)) {
//
//                Date fixedDate = parser.nextDateTime();
//
//                position.setTimestamp(fixedDate.getTime());
//                position.setFixedtime(fixedDate.getTime());
//        }
//    }
//
//    private void getLastLocation(Position position) {
//        if (position.getDeviceId() != null) {
//            position.setOutdated(true);
//
//            EventData last = applicationContext.getLastEvent(position.getDeviceId());
//
//            if (last != null) {
//                position.setFixedtime(last.getFixedtime());
//                //position.setValid(last.get());
//                position.setLatitude(last.getLatitude());
//                position.setLongitude(last.getLongitude());
//                position.setAltitude(last.getAltitude());
//                position.setSpeedKPH(last.getSpeedKPH());
//                position.setHeading(last.getHeading());
//                position.setAccuracy(last.getAccuracy());
//            } else {
//                position.setFixedtime(new Date(0).getTime()/1000);
//            }
//        }
//    }
//}
