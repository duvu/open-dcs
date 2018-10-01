//package com.vd5.dcs.protocols.tk10x;
//
//import com.vd5.data.StatusCodes;
//import com.vd5.data.entities.Device;
//import com.vd5.data.entities.EventData;
//import com.vd5.data.utils.DistanceCalculator;
//import com.vd5.dcs.helper.*;
//import com.vd5.dcs.model.Position;
//import com.vd5.dcs.ApplicationContext;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageDecoder;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//
//import static com.vd5.dcs.protocols.ServerManager.VD5_AUTO_ADD;
//import static com.vd5.dcs.protocols.ServerManager.VD5_DEVICE_MANAGER;
//import static com.vd5.dcs.protocols.ServerManager.VD5_MIN_SPEED_KPH;
//
///**
// * @author beou on 11/23/17 21:28
// */
//public class Tk10xProtocolDecoder extends MessageToMessageDecoder<String> {
//
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    private boolean isAutoAdd;
//    private double minSpeedKPH;
//    private ApplicationContext applicationContext;
//
//    private TkDeviceType deviceType = TkDeviceType.UNKNOWN;
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
//        isAutoAdd = ctx.channel().attr(VD5_AUTO_ADD).get();
//        minSpeedKPH = ctx.channel().attr(VD5_MIN_SPEED_KPH).get();
//        applicationContext = ctx.channel().attr(VD5_DEVICE_MANAGER).get();
//    }
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, String sentence, List<Object> list) {
//        log.info("[Tk10x >_]: " + sentence);
//        detectDeviceType(sentence);
//
//        if (sentence.startsWith("(")) {
//            if (IsDeviceTypeUnknown(deviceType)) {
//                deviceType = sentence.contains(",") ? TkDeviceType.VJOY : TkDeviceType.TK103_3;
//            }
//            parseTk103_3(ctx, sentence, list);
//        }
//    }
//
//    private void detectDeviceType(String s) {
//        if (StringUtils.isEmpty(s)) {
//            deviceType = TkDeviceType.UNKNOWN;
//            return;
//        }
//        if (s.startsWith("[")) {
//            // TK102B: "[!0000000001.(...)]"
//            // -- at least 14 bytes, to include the "(" character
//            deviceType = TkDeviceType.TK102B;
//        } else if (s.startsWith("(")) {
//            // TK103-3: "(...)"
//            // VJoy   : "(...,...,...)"
//            // PacketTerminator: ')'
//            deviceType = TkDeviceType.TK103_3;
//            if (s.contains(",")) {
//                deviceType = TkDeviceType.VJOY;
//            }
//        } else if (s.startsWith("*")) {
//            // TKnano-1: "*HQ,...#"
//            // PacketTerminator: #
//            deviceType = TkDeviceType.TKnano_1;
//        } else if (s.startsWith("$")) {
//            deviceType = TkDeviceType.TKnano_2;
//        } else if (s.startsWith("#")) {
//            deviceType = TkDeviceType.TK103_2;
//        } else if (s.startsWith("i") || s.startsWith("I")) {
//            deviceType = TkDeviceType.TK103_2;
//        } else {
//            deviceType = TkDeviceType.TK102;
//        }
//    }
//
//    private static boolean IsDeviceTypeUnknown(TkDeviceType dt)
//    {
//        return ((dt == null) || dt.isUnknown());
//    }
//
//    private void parseTk103_3(ChannelHandlerContext channelContext, String sentence, List<Object> list) {
//        log.info("[>_] Parsing TK103-3 (" + deviceType + ") ...");
//
//
//        String id = sentence.substring(1, 13);
//        String type = sentence.substring(13, 17);
//        if (type.equals("BP00")) {
//            channelContext.writeAndFlush("(" + id + "AP01HSO)");
//            return;
//        } else if (type.equals("BP05")) {
//            channelContext.writeAndFlush("(" + id + "AP05)");
//        }
//
//        Position p = new Position();
//        p.setAutoAddDevice(isAutoAdd);
//
//        if (sentence.contains("ZC20")) {
//            decodeBattery(p, sentence);
//        } else if (sentence.contains("BZ00")) {
//            decodeNetwork(p, sentence);
//        } else if (sentence.contains("ZC03")) {
//            decodeCommandResult(p, sentence);
//        } else if (sentence.contains("DW5")) {
//            decodeLbsWifi(p, sentence);
//        } else {
//            decodeLocation(p, sentence);
//        }
//        list.add(p);
//    }
//
//    private void decodeBattery(Position p, String sentence) {
//        log.info("[>_] decode-battery");
//        Parser parser = new Parser(TkPatterns.PATTERN_BATTERY, sentence);
//
//        Device device = applicationContext.getDevice(parser.next());
//        if (device != null) {
//            p.setDeviceId(device.getDeviceId());
//        }
//    }
//
//    private void decodeNetwork(Position p, String sentence) {
//    }
//
//    private void decodeCommandResult(Position p, String sentence) {
//    }
//
//    private void decodeLbsWifi(Position p, String sentence) {
//    }
//
//    private void decodeAlarm(Position position, int value) {
//        switch (value) {
//            case 1:
//                //position.addStatus(StatusCodes.STATUS_ACCIDENT);
//            case 2:
//                //position.addStatus(StatusCodes.STATUS_SOS);
//            case 3:
//                position.addStatus(StatusCodes.STATUS_VIBRATION_ON);
//            case 4:
//                position.addStatus(StatusCodes.STATUS_LOW_SPEED);
//            case 5:
//                position.addStatus(StatusCodes.STATUS_MOTION_EXCESS_SPEED);
//            case 6:
//                position.addStatus(StatusCodes.STATUS_GEOFENCE_DEPART);
//        }
//    }
//
//    private void decodeType(Position position, String type, String data) {
//        switch (type) {
//            case "BP05":
//                position.setDeviceImei(data);
//            case "BO01":
//                decodeAlarm(position,data.charAt(0) - '0');
//                break;
//            case "ZC11":
//            case "DW31":
//            case "DW51":
//                position.addStatus(StatusCodes.STATUS_MOTION_EN_ROUTE);
//                break;
//            case "ZC12":
//            case "DW32":
//            case "DW52":
//                position.addStatus(StatusCodes.STATUS_LOW_BATTERY);
//                break;
//            case "ZC13":
//            case "DW33":
//            case "DW53":
//                position.addStatus(StatusCodes.STATUS_POWER_OFF);
//                break;
//            case "ZC15":
//            case "DW35":
//            case "DW55":
//                position.addStatus(StatusCodes.STATUS_IGNITION_ON);
//                break;
//            case "ZC16":
//            case "DW36":
//            case "DW56":
//                position.addStatus(StatusCodes.STATUS_IGNITION_OFF);
//                break;
//            case "ZC29":
//            case "DW42":
//            case "DW62":
//                position.addStatus(StatusCodes.STATUS_IGNITION_ON);
//                break;
//            case "ZC17":
//            case "DW37":
//            case "DW57":
//                position.addStatus(StatusCodes.STATUS_ALARM_OFF);
//                break;
//            case "ZC25":
//            case "DW3E":
//            case "DW5E":
//                position.addStatus(StatusCodes.STATUS_PANIC_ON);
//                break;
//            case "ZC26":
//            case "DW3F":
//            case "DW5F":
//                position.addStatus(StatusCodes.STATUS_TAMPER_ON);
//                break;
//            case "ZC27":
//            case "DW40":
//            case "DW60":
//                position.addStatus(StatusCodes.STATUS_POWER_ALARM);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void decodeLocation(Position position, String sentence) {
//        Parser parser = new Parser(TkPatterns.PATTERN, sentence);
//        if (!parser.matches()) {
//            return;
//        }
//
//        position.setProtocol("tk10x");
//
//        String deviceId = parser.next();
//        String dNext = parser.next();
//        boolean alternative = (dNext != null);
//
//        String type = parser.next();
//        String data = parser.next();
//
//        decodeType(position, type, data);
//
//        DateBuilder dateBuilder = new DateBuilder();
//        if (alternative) {
//            dateBuilder.setDateReverse(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
//        } else {
//            dateBuilder.setDate(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
//        }
//
//        String valid = parser.next();
//        position.setValid(valid.equals("A"));
//
//        double latitude = parser.nextCoordinate();
//        double longitude = parser.nextCoordinate();
//
//        String spd = parser.next();
//        double speed = UnitsConverter.knotsFromKph(Double.parseDouble(spd));
//
//        dateBuilder.setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
//        long timestamp = dateBuilder.getDate().getTime();
//
//        if (speed <= minSpeedKPH) {
//            //
//            EventData lastEventData = applicationContext.getLastEvent(deviceId);
//            if (lastEventData != null) {
//                double deltaL = DistanceCalculator.distance(latitude, longitude, lastEventData.getLatitude(), lastEventData.getLongitude());
//                double deltaT = (timestamp - lastEventData.getTimestamp()) / 1000.0;
//                speed = (deltaL/deltaT) * 3.6;
//            }
//        }
//        speed = speed < minSpeedKPH ? 0 : speed;
//
//        double heading = parser.nextDouble(0);
//
//        position.setDeviceId(deviceId);
//        position.setLatitude(BigDecimal.valueOf(latitude).setScale(6, RoundingMode.HALF_UP).doubleValue());
//        position.setLongitude(BigDecimal.valueOf(longitude).setScale(6, RoundingMode.HALF_UP).doubleValue());
//        position.setSpeedKPH(BigDecimal.valueOf(speed).setScale(1, RoundingMode.HALF_UP).doubleValue());
//        position.setHeading(heading);
//        position.setTimestamp(timestamp);
//        position.setFixedtime(timestamp);
//
//        position.setRawData(sentence);
//
//        if (parser.hasNext(6)) {
//            log.info("[>_] parsing additional data");
//
//            int sc = parser.nextInt() == 0 ? StatusCodes.STATUS_INPUT_ON : StatusCodes.STATUS_INPUT_OFF;
//            position.addStatus(sc);
//            sc = parser.nextInt() == 1 ? StatusCodes.STATUS_IGNITION_ON : StatusCodes.STATUS_IGNITION_OFF;
//            position.addStatus(sc);
//
//            int mask1 = parser.nextHexInt();
////            position.set(Position.PREFIX_IN + 2, BitUtil.check(mask1, 0) ? 1 : 0);
////            position.set("panic", BitUtil.check(mask1, 1) ? 1 : 0);
////            position.set(Position.PREFIX_OUT + 2, BitUtil.check(mask1, 2) ? 1 : 0);
////            if (dcsConfig.isDecodeLow() || BitUtil.check(mask1, 3)) {
////                position.set(Position.KEY_BLOCKED, BitUtil.check(mask1, 3) ? 1 : 0);
////            }
//
//            int mask2 = parser.nextHexInt();
////            for (int i = 0; i < 3; i++) {
////                if (dcsConfig.isDecodeLow() || BitUtil.check(mask2, i)) {
////                    position.set("hs" + (3 - i), BitUtil.check(mask2, i) ? 1 : 0);
////                }
////            }
////            if (dcsConfig.isDecodeLow() || BitUtil.check(mask2, 3)) {
////                position.set(Position.KEY_DOOR, BitUtil.check(mask2, 3) ? 1 : 0);
////            }
////
//            int mask3 = parser.nextHexInt();
////            for (int i = 1; i <= 3; i++) {
////                if (dcsConfig.isDecodeLow() || BitUtil.check(mask3, i)) {
////                    position.set("ls" + (3 - i + 1), BitUtil.check(mask3, i) ? 1 : 0);
////                }
////            }
//            position.setFuelLevel(parser.nextHexInt());
//        }
//
//        if (parser.hasNext()) {
//            position.setOdometerKM(parser.nextLong(16, 0));
//        }
//
//        if (parser.hasNext()) {
////            position.set(Position.PREFIX_TEMP + 1, parser.nextDouble(0));
//            parser.nextDouble(0);
//        }
//
//        if (parser.hasNext()) {
//            position.setAltitude(parser.nextDouble(0));
//        }
//
//        if (parser.hasNext()) {
//            position.setSatelliteCount(parser.nextInt(0));
//        }
//    }
//}
