package com.vd5.dcs2.protocol.wlink;

import com.vd5.dcs.helper.Parser;
import com.vd5.dcs.model.CellTower;
import com.vd5.dcs.model.Network;
import com.vd5.dcs2.AbstractProtocolDecoder;
import com.vd5.dcs2.DeviceSession;
import com.vd5.dcs2.Protocol;
import com.vd5.dcs2.model.Position;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author beou on 10/14/18 23:31
 */
public class WlinkProtocolDecoder extends AbstractProtocolDecoder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //-- General position report
    private static final String FRI = "FRI"; //GL200, GL300, ATPlus,      , GV500
    private static final String CTN = "CTN"; //     ,      ,       , GL500
    private static final String GEO = "GEO"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String SPD = "SPD"; //GL200, GL300, ATPlus       , GV500
    private static final String SOS = "SOS"; //GL200, GL300, ATPlus
    private static final String RTL = "RTL"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String STR = "STR"; //                    , GL500
    private static final String PNL = "PNL"; //GL200, GL300, ATPlus
    private static final String NMR = "NMR"; //GL200, GL300, ATPlus, GL500
    private static final String DIS = "DIS"; //GL200, GL300, ATPlus,      , GV500
    private static final String DOG = "DOG"; //GL200, GL300, ATPlus       , GV500
    private static final String IGL = "IGL"; //     , GL300,       ,      , GV500
    private static final String TOW = "TOW"; //                           , GV500
    private static final String HBM = "HBM"; //                           , GV500
    private static final String EPS = "EPS"; //                           , GV500
    private static final String GES = "GES"; //                           , GV500

    //--Location by call report
    private static final String LBC = "LBC"; //GL200, GL300, ATPlus,      , GV500

    //--Location as center of geo-fence
    private static final String GCR = "GCR"; //GL200, GL300, ATPlus

    //--History report
    private static final String HIS = "HIS"; //     ,      , ATPlus

    //--Device infomation report
    private static final String INF = "INF"; //GL200, GL300, ATPlus,      , GV500

    //--Lightness information report
    private static final String LGT = "LGT"; //     ,      , ATPlus

    //--Report for querying
    private static final String DIF = "DIF"; //     ,      ,       , GL500
    private static final String GPS = "GPS"; //GL200, GL300, ATPlus,      , GV500
    private static final String ALL = "ALL"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String CID = "CID"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String CSQ = "CSQ"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String VER = "VER"; //GL200, GL300, ATPlus,      , GV500
    private static final String BAT = "BAT"; //GL200, GL300, ATPlus,      , GV500
    private static final String TMZ = "TMZ"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String ALS = "ALS"; //     , GL300,       ,      , GV500
    private static final String GSV = "GSV"; //     , GL300

    //--Event Report
    private static final String PNA = "PNA"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String PFA = "PFA"; //GL200, GL300, ATPlus,      , GV500
    private static final String EPN = "EPN"; //GL200, GL300, ATPlus
    private static final String EPF = "EPF"; //GL200, GL300, ATPlus
    private static final String BPL = "BPL"; //GL200, GL300, ATPlus, GL500
    private static final String BTC = "BTC"; //GL200, GL300, ATPlus, GL500, GV500
    private static final String STC = "STC"; //GL200, GL300, ATPlus
    private static final String STT = "STT"; //GL200, GL300, ATPlus
    private static final String ANT = "ANT"; //GL200,      , ATPlus
    private static final String PDP = "PDP"; //GL200, GL300, ATPlus, GL500
    private static final String SWG = "SWG"; //GL200, GL300, ATPlus
    private static final String IGN = "IGN"; //GL200, GL300,
    private static final String IGF = "IGF"; //GL200, GL300,
    private static final String GSM = "GSM"; //     , GL300, ATPlus, GL500
    private static final String TEM = "TEM"; //     , GL300,       , GL500
    private static final String UPC = "UPC"; //     , GL300
    private static final String JDR = "JDR"; //     , GL300
    private static final String JDS = "JDS"; //     , GL300
    private static final String DAT = "DAT"; //     , GL300
    private static final String MPN = "MPN"; //     ,      ,        ,     , GV500
    private static final String MPF = "MPF"; //     ,      ,        ,     , GV500

    //Hearbeat
    private static final String HBD = "HBD"; //GL200, GL300, ATPlus, GL500, GV500


    public WlinkProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {
        String message = (String) msg;

        if (message.startsWith("+ACK")) {
            handleACK(channel, message);
            //decode ACK and write answer
        } else { // +RESP
            return handlePacket(channel, remoteAddress, message);
        }

        return new Position();
    }

    private void handleACK(Channel channel, String msg) {
        log.info("ACK: " + msg);
    }

    private Position handlePacket(Channel channel, SocketAddress remoteAddress, String msg) {
        int typeIndex = msg.indexOf(":GT");
        if (typeIndex < 0) {
            return null;
        }
        //STT, EPF, EPN, BPL,
        String type = msg.substring(typeIndex + 3, typeIndex + 6);
        switch (type) {
            case NMR:
            case PNL:
            case GEO:
            case SPD:
            case SOS:
            case RTL:
            case DIS:
            case DOG:
            case IGL:
            case FRI:
                return handleFRI(channel, remoteAddress, msg);

            case CTN:
                return handleCTN(channel, msg);
            case STR:
                return handleSTR(channel, msg);
            case TOW:
                return handleTOW(channel, msg);
            case HBM:
                return handleHBM(channel, msg);
            case EPS:
                return handleEPS(channel, msg);
            case GES:
                return handleOTHER(channel, msg);

            //--Location by call report
            case LBC:
                return handleLBC(channel, msg);

            //--Location as center of geo-fence
            case GCR:
                return handleGCR(channel, msg);

            //--History report
            case HIS:
                return handleHIS(channel, msg);

            case INF:
                return handleINF(channel, msg);

            //--Lightness information report
            case LGT:
                return handleLGT(channel, msg);

            //--Report for querying
            case DIF:
                return handleDIF(channel, msg);
            case GPS:
                return handleGPS(channel, msg);
            case ALL:
                return handleALL(channel, msg);
            case CID:
                return handleCID(channel, msg);
            case CSQ:
                return handleCSQ(channel, msg);
            case VER:
                return handleVER(channel, msg);
            case BAT:
                return handleBAT(channel, msg);
            case TMZ:
                return handlTMZ(channel, msg);
            case ALS:
                return handleALS(channel, msg);
            case GSV:
                return handleGSV(channel, msg);

            //--Event Report
            case PNA:
                return handlePNA(channel, msg);
            case PFA:
                return handlePFA(channel, msg);
            case EPN:
                return handleEPN(channel, msg);
            case EPF:
                return handleEPF(channel, msg);
            case BPL:
                return handleBPL(channel, msg);
            case BTC:
                return handleBTC(channel, msg);
            case STC:
                return handleSTC(channel, msg);
            case STT:
                return handleSTT(channel, remoteAddress, msg);
            case ANT:
                return handleANT(channel, msg);
            case PDP:
                return handlePDP(channel, msg);
            case SWG:
                return handleSWG(channel, msg);
            case IGN:
                return handleIGN(channel, msg);
            case IGF:
                return handleIGF(channel, msg);
            case GSM:
                return handleGSM(channel, msg);
            case TEM:
                return handleTEM(channel, msg);
            case UPC:
                return handleUPC(channel, msg);
            case JDR:
                return handleJDR(channel, msg);
            case JDS:
                return handleJDS(channel, msg);
            case DAT:
                return handleDAT(channel, msg);
            case MPN:
                return handleMPN(channel, msg);
            case MPF:
                return handleMPF(channel, msg);

            //Hearbeat
            case HBD:
                return handleHDB(channel, msg);

            case "OBD":
                return handleOBD(channel, msg);
            case "CAN":
                return handleCAN(channel, msg);
            case "ERI":
                return handleERI(channel, remoteAddress, msg);
            case "LSW":
            case "TSW":
                return handleLSW(channel, msg);
            case "IDA":
                return handleIDA(channel, msg);
            case "WIF":
                return handleWIF(channel, msg);
            default:
                return handleOTHER(channel, msg);
        }
    }

    private Position handleHDB(Channel channel, String msg) {
        log.info("HDB: " + msg);
        return null;
    }

    private Position handleMPF(Channel channel, String msg) {
        log.info("MPF: " + msg);
        return null;
    }

    private Position handleMPN(Channel channel, String msg) {
        log.info("MPN: " + msg);
        return null;
    }

    private Position handleDAT(Channel channel, String msg) {
        log.info("DAT: " + msg);
        return null;
    }

    private Position handleJDS(Channel channel, String msg) {
        log.info("JDS: " + msg);
        return null;
    }

    private Position handleJDR(Channel channel, String msg) {
        log.info("JDR: " + msg);
        return null;
    }

    private Position handleUPC(Channel channel, String msg) {
        log.info("UPC: " + msg);
        return null;
    }

    private Position handleTEM(Channel channel, String msg) {
        log.info("TEM: " + msg);
        return null;
    }

    private Position handleIGF(Channel channel, String msg) {
        log.info("IGF: " + msg);
        return null;
    }

    private Position handleIGN(Channel channel, String msg) {
        log.info("IGN: " + msg);
        return null;
    }

    private Position handleSWG(Channel channel, String msg) {
        log.info("SWG: " + msg);
        return null;
    }

    private Position handlePDP(Channel channel, String msg) {
        log.info("PDP: " + msg);
        return null;
    }

    private Position handleANT(Channel channel, String msg) {
        log.info("ANT: " + msg);
        return null;
    }

    private Position handleSTC(Channel channel, String msg) {
        log.info("STC: " + msg);
        return null;
    }

    private Position handleBTC(Channel channel, String msg) {
        log.info("BTC: " + msg);
        return null;
    }

    private Position handleBPL(Channel channel, String msg) {
        log.info("BPL: " + msg);
        Parser parser = new Parser(WlinkPatterns.PATTERN_BPL, msg);

        if (!parser.matches()) {
            log.info("NO MATCHES");
            return null;
        }

        String imei = parser.next();
        double battVolt = parser.nextDouble();
        Position position = new Position();
        position.setImei(imei);
        position.set(Position.KEY_BATTERY, battVolt);

        decodeLocation(parser, position);
        decodeDeviceTime(parser, position);
        return position;
    }

    private Position handleEPF(Channel channel, String msg) {
        log.info("EPF: " + msg);
        return null;
    }

    private Position handleEPN(Channel channel, String msg) {
        log.info("EPN: " + msg);
        return null;
    }

    private Position handlePFA(Channel channel, String msg) {
        log.info("PFA: " + msg);
        return null;
    }

    private Position handlePNA(Channel channel, String msg) {
        log.info("PNA: " + msg);
        return null;
    }

    private Position handleGSV(Channel channel, String msg) {
        log.info("GSV: " + msg);
        return null;
    }

    private Position handleALS(Channel channel, String msg) {
        log.info("ALS: " + msg);
        return null;
    }

    private Position handlTMZ(Channel channel, String msg) {
        log.info("TMZ: " + msg);
        return null;
    }

    private Position handleBAT(Channel channel, String msg) {
        log.info("BAT: " + msg);
        return null;
    }

    private Position handleCSQ(Channel channel, String msg) {
        log.info("CSQ: " + msg);
        return null;
    }

    private Position handleCID(Channel channel, String msg) {
        log.info("CID: " + msg);
        return null;
    }

    private Position handleALL(Channel channel, String msg) {
        log.info("ALL: " + msg);
        return null;
    }

    private Position handleGPS(Channel channel, String msg) {
        log.info("GPS: " + msg);
        return null;
    }

    private Position handleDIF(Channel channel, String msg) {
        log.info("DIF: " + msg);
        return null;
    }

    private Position handleLGT(Channel channel, String msg) {
        log.info("LGT: " + msg);
        return null;
    }

    private Position handleHIS(Channel channel, String msg) {
        log.info("HIS: " + msg);
        return null;
    }

    private Position handleGCR(Channel channel, String msg) {
        log.info("GCR: " + msg);
        return null;
    }

    private Position handleLBC(Channel channel, String msg) {
        log.info("LBC: " + msg);
        return null;
    }

    private Position handleEPS(Channel channel, String msg) {
        log.info("EPS: " + msg);
        return null;
    }

    private Position handleHBM(Channel channel, String msg) {
        log.info("HBM: " + msg);
        return null;
    }

    private Position handleTOW(Channel channel, String msg) {
        log.info("TOW: " + msg);
        return null;
    }

    private Position handleSTR(Channel channel, String msg) {
        log.info("STR: " + msg);
        return null;
    }

    private Position handleCTN(Channel channel, String msg) {
        log.info("CTN: " + msg);
        return null;
    }

    private Position handleINF(Channel channel, String msg) {
        log.info("INF");
        Parser parser = new Parser(WlinkPatterns.PATTERN_INF, msg);

        if (!parser.matches()) {
            log.info("NOT MATCHES");
            return null;
        }

        return null;
    }

    private Position handleOBD(Channel channel, String msg) {
        log.info("[Wlink >_] OBD");
        return  new Position();
    }

    private Position handleCAN(Channel channel, String msg) {
        log.info("[Wlink >_] CAN");
        return new Position();
    }

    private Position handleFRI(Channel channel, SocketAddress remoteAddress, String msg) {
        log.info("[Wlink >_] FRI " + msg);

        Parser parser = new Parser(WlinkPatterns.PATTERN_FRI, msg);

        if (!parser.matches()) {
            return null;
        }

        String imei = parser.next();
        log.info("[>_] Imei: " + imei);
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        LinkedList<Position> positions = new LinkedList<>();

        String uniqueId = parser.next();
        log.info("[>_] UniqueID:     " + uniqueId);
        String vin = parser.next();
        log.info("[>_] vin:          " + vin);
        Integer power = parser.nextInt();
        log.info("[>_] power:        " + power);

        Parser itemParser = new Parser(WlinkPatterns.PATTERN_LOCATION, parser.next());
        while (itemParser.find()) {
            Position position = new Position();

            position.setDeviceId(deviceSession.getDeviceId());
            decodeLocation(itemParser, position);
            positions.add(position);
        }
        Position position = positions.getLast();
        decodeLocation(parser, position);

        if (power != null && power > 10) {
            position.set(Position.KEY_BATTERY_LEVEL, power * 0.001); // only on some devices
        }

        return position;
    }

    private Position handleERI(Channel channel, SocketAddress remoteAddress, String msg) {
        log.info("[Wlink >_] ERI");
        return new Position();
    }

    private Position handleSTT(Channel channel, SocketAddress remoteAddress, String msg) {
        log.info("STT: " + msg);
        Parser parser = new Parser(WlinkPatterns.PATTERN_STT, msg);
        Position position = new Position();

        if (!parser.matches()) {
            log.info("NOT MATCHES");
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        String imei = parser.next();
        position.setDeviceId(deviceSession.getDeviceId());
        position.setImei(imei);

        int state = parser.nextInt();

        decodeLocation(parser, position);
        decodeDeviceTime(parser, position);
        return position;
    }

    private Position handleING(Channel channel, String msg) {
        log.info("[Wlink >_] ING");
        Parser parser = new Parser(WlinkPatterns.PATTERN_IGN, msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();

        String imei = parser.next();
        Long ignDuratiron = parser.nextLong();

        log.info("IMEI:      " + imei);
        log.info("IGN-DUR    " + ignDuratiron);

        decodeLocation(parser, position);
        decodeDeviceTime(parser, position);

        return position;
    }

    private Position handleLSW(Channel channel, String msg) {
        log.info("[Wlink >_] LSW");
        return new Position();
    }

    private Position handleIDA(Channel channel, String msg) {
        log.info("[Wlink >_] IDA");
        return new Position();
    }

    private Position handleWIF(Channel channel, String msg) {
        log.info("[Wlink >_] WIF");
        return new Position();
    }

    private Position handleGSM(Channel channel, String msg) {
        log.info("[Wlink >_] GSM");
        return new Position();
    }

    private Position handleVER(Channel channel, String msg) {
        log.info("[Wlink >_] VER");
        return new Position();
    }

    private Position handleOTHER(Channel channel, String msg) {
        return null;
    }

    private void decodeLocation(Parser parser, Position position) {
        int hdop = parser.nextInt(0);
        double speed = parser.nextDouble(0);
        double course = parser.nextDouble(0);
        double altitude = parser.nextDouble(0);

        if (speed < 5) {
            speed = 0;
        }

        position.set(Position.KEY_HDOP, hdop);
        position.setSpeed(speed);
        position.setCourse(course);
        position.setAltitude(altitude);

        log.info("[>_] HDOP: " + hdop);
        log.info("[>_] SpeedKPH: " + speed);
        log.info("[>_] Heading: " + course);

        if (parser.hasNext(8)) {
            position.setValid(true);

            Double longitude = parser.nextDouble();
            Double latitude = parser.nextDouble();

            log.info("[>_] Longitude: " + longitude);
            log.info("[>_] Latitude: " + latitude);

            position.setLongitude(longitude);
            position.setLatitude(latitude);
            long timestamp = parser.nextDateTime().getTime();
            position.setFixTime(new Date(timestamp));
            position.setTime(new Date(timestamp));
        } else {
            //getLastLocation(position);
        }

        if (parser.hasNext(6)) {
            int mcc = parser.nextInt();
            int mnc = parser.nextInt();
            CellTower cellTower = CellTower.builder()
                    .mcc(mcc)
                    .mnc(mnc)
                    .lac(parser.nextInt())
                    .cid(parser.nextInt())
                    .build();

            if (parser.hasNext(2)) {
                position.setNetwork(new Network(cellTower));
            }
        }

        if (parser.hasNext()) {
            position.set(Position.KEY_ODOMETER, parser.nextDouble());
        }
    }

    private void decodeDeviceTime(Parser parser, Position position) {
        if (parser.hasNext(6)) {

                Date fixedDate = parser.nextDateTime();

                position.setTime(fixedDate);
                position.setFixTime(fixedDate);
        }
    }

//    private void getLastLocation(Position position) {
//        if (position.getDeviceId() > 0) {
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
}
