package com.vd5.dcs2.protocol.wlink;

import com.vd5.dcs.helper.PatternBuilder;

import java.util.regex.Pattern;

/**
 * @author beou on 7/22/18 16:16
 *
 * 05-08-2018 06:37:46.273 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleBTC - BTC: +RESP:GTBTC,120113,351564054113923,,23,8.2,340,54.0,16.228674,51.394864,20180805063740,260,03,dde0,8021,,20180805063740,8820$
 * 05-08-2018 06:37:46.882 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleSTC - STC: +RESP:GTSTC,120113,351564054113923,,,23,8.2,340,54.0,16.228674,51.394864,20180805063741,260,03,dde0,8021,,20180805063741,8821$
 * 05-08-2018 06:37:46.960 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleBTC - BTC: +RESP:GTBTC,120113,351564054113923,,23,8.2,340,54.0,16.228674,51.394864,20180805063745,260,03,dde0,8021,,20180805063745,8822$
 * 05-08-2018 06:37:48.280 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleSTC - STC: +RESP:GTSTC,120113,351564054113923,,,23,8.2,340,54.0,16.228674,51.394864,20180805063746,260,03,dde0,8021,,20180805063746,8823$
 * 05-08-2018 06:37:51.963 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleBTC - BTC: +RESP:GTBTC,120113,351564054113923,,23,8.2,340,54.0,16.228674,51.394864,20180805063749,260,03,dde0,8021,,20180805063749,8824$
 * 05-08-2018 06:37:52.941 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleSTC - STC: +RESP:GTSTC,120113,351564054113923,,,23,8.2,340,54.0,16.228674,51.394864,20180805063751,260,03,dde0,8021,,20180805063751,8825$
 * 05-08-2018 06:37:54.640 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleBTC - BTC: +RESP:GTBTC,120113,351564054113923,,23,8.2,340,54.0,16.228674,51.394864,20180805063752,260,03,dde0,8021,,20180805063752,8826$
 * 05-08-2018 06:37:58.041 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleSTC - STC: +RESP:GTSTC,120113,351564054113923,,,23,8.2,340,54.0,16.228674,51.394864,20180805063756,260,03,dde0,8021,,20180805063756,8827$
 * 05-08-2018 06:37:58.163 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleBTC - BTC: +RESP:GTBTC,120113,351564054113923,,23,8.2,340,54.0,16.228674,51.394864,20180805063756,260,03,dde0,8021,,20180805063756,8828$
 * 05-08-2018 06:37:59.800 [nioEventLoopGroup-3-1] INFO  c.v.d.p.wlink.WlinkProtocolDecoder.handleSTC - STC: +RESP:GTSTC,120113,351564054113923,,,23,8.2,340,54.0,16.228674,51.394864,20180805063757,260,03,dde0,8021,,20180805063757,8829$
 *
 */


public class WlinkPatterns {
    public static final Pattern PATTERN_ACK = new PatternBuilder()
            .text("+ACK:GT")
            .expression("...,")                  // type
            .number("([0-9A-Z]{2}xxxx),")        // protocol version
            .number("(d{15}|x{14}),")            // imei
            .any().text(",")
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(xxxx)")                    // counter
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_INF = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF):GTINF,")
            .number("[0-9A-Z]{2}xxxx,")          // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("(?:[0-9A-Z]{17},)?")    // vin
            .expression("(?:[^,]+)?,")           // device name
            .number("(xx),")                     // state
            .expression("(?:[0-9Ff]{20})?,")     // iccid
            .number("(d{1,2}),")                 // rssi
            .number("d{1,2},")
            .expression("[01],")                 // external power
            .number("([d.]+)?,")                 // odometer or external power
            .number("d*,")                       // backup battery or lightness
            .number("(d+.d+),")                  // battery
            .expression("([01]),")               // charging
            .number("(?:d),")                    // led
            .number("(?:d)?,")                   // gps on need
            .number("(?:d)?,")                   // gps antenna type
            .number("(?:d)?,").optional()        // gps antenna state
            .number("d{14},")                    // last fix time
            .groupBegin()
            .number("(d+),")                     // battery percentage
            .number("[d.]*,")                    // flash type / power
            .number("(-?[d.]+)?,,,")             // temperature
            .or()
            .expression("(?:[01])?,").optional() // pin15 mode
            .number("(d+)?,")                    // adc1
            .number("(d+)?,").optional()         // adc2
            .number("(xx)?,")                    // digital input
            .number("(xx)?,")                    // digital output
            .number("[-+]dddd,")                 // timezone
            .expression("[01],")                 // daylight saving
            .groupEnd()
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(xxxx)")                    // counter
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_VER = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF):GTVER,")
            .number("[0-9A-Z]{2}xxxx,")          // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .expression("([^,]*),")              // device type
            .number("(xxxx),")                   // firmware version
            .number("(xxxx),")                   // hardware version
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(xxxx)")                    // counter
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_LOCATION = new PatternBuilder()
            .number("(d{1,2})?,")                // hdop
            .number("(d{1,3}.d)?,")              // speed
            .number("(d{1,3})?,")                // course
            .number("(-?d{1,5}.d)?,")            // altitude
            .number("(-?d{1,3}.d{6})?,")         // longitude
            .number("(-?d{1,2}.d{6})?,")         // latitude
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(d+)?,")                    // mcc
            .number("(d+)?,")                    // mnc
            .groupBegin()
            .number("(d+),")                     // lac
            .number("(d+),")                     // cid
            .or()
            .number("(x+)?,")                    // lac
            .number("(x+)?,")                    // cid
            .groupEnd()
            .number("(?:d+|(d+.d))?,")           // odometer
            .compile();

    public static final Pattern PATTERN_OBD = new PatternBuilder()
            .text("+RESP:GTOBD,")
            .number("[0-9A-Z]{2}xxxx,")          // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("(?:[0-9A-Z]{17})?,")    // vin
            .expression("[^,]{0,20},")           // device name
            .expression("[01],")                 // report type
            .number("x{1,8},")                   // report mask
            .expression("(?:[0-9A-Z]{17})?,")    // vin
            .number("[01],")                     // obd connect
            .number("(?:d{1,5})?,")              // obd voltage
            .number("(?:x{8})?,")                // support pids
            .number("(d{1,5})?,")                // engine rpm
            .number("(d{1,3})?,")                // speed
            .number("(-?d{1,3})?,")              // coolant temp
            .number("(d+.?d*|Inf|NaN)?,")        // fuel consumption
            .number("(d{1,5})?,")                // dtcs cleared distance
            .number("(?:d{1,5})?,")
            .expression("([01])?,")              // obd connect
            .number("(d{1,3})?,")                // number of dtcs
            .number("(x*),")                     // dtcs
            .number("(d{1,3})?,")                // throttle
            .number("(?:d{1,3})?,")              // engine load
            .number("(d{1,3})?,")                // fuel level
            .expression("(?:[0-9A],)?")          // obd protocol
            .number("(d+),")                     // odometer
            .expression(PATTERN_LOCATION.pattern())
            .number("(d{1,7}.d)?,")              // odometer
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    //+RESP:GTNMR,020301,867844002284563,GL200,0,0,1,1,0.0,0,8.9,4.329701,50.862681,20180802185946,0206,0010,3F48,3216,,100,20180802185946,3A86$
    // +RESP:GTFRI,120113,351564054084983,     ,0,3,1,8, 0.0,0   ,27.2,14.472553,53.373787,20181022092836,260,03,dfe8,cc51,00,97,20181022092836,06A1
    // +RESP:GTFRI,020301,867844002284563,GL200,0,0,1,1, 58.9,114,-0.4,3.661158,50.989108,20181022093014,0206,0010,4B00,8867,,100,20181022093022,2C5E
    public static final Pattern PATTERN_FRI = new PatternBuilder()
            .text("+")
            .expression("(?:RESP|BUFF):GTFRI,")
            .number("([0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("(?:([0-9A-Z]{17}),)?")  // vin
            .expression("[^,]*,")                // device name
            .number("(d+)?,")                    // power
            .number("d{1,2},")                   // report type
            .number("d{1,2},")                   // count
            .expression("((?:")
            .expression(PATTERN_LOCATION.pattern())
            .expression(")+)")
            .groupBegin()
            .number("(d{1,7}.d)?,").optional()   // odometer
            .number("(d{1,3})?,")                // battery
            .or()
            .number("(d{1,7}.d)?,")              // odometer
            .number("(d{5}:dd:dd)?,")            // hour meter
            .number("(x+)?,")                    // adc 1
            .number("(x+)?,")                    // adc 2
            .number("(d{1,3})?,")                // battery
            .number("(?:(xx)(xx)(xx))?,")        // device status
            .number("(d+)?,")                    // rpm
            .number("(?:d+.?d*|Inf|NaN)?,")      // fuel consumption
            .number("(d+)?,")                    // fuel level
            .groupEnd()
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_ERI = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF):GTERI,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("(x{8}),")                   // mask
            .number("(d+)?,")                    // power
            .number("d{1,2},")                   // report type
            .number("d{1,2},")                   // count
            .expression("((?:")
            .expression(PATTERN_LOCATION.pattern())
            .expression(")+)")
            .number("(d{1,7}.d)?,")              // odometer
            .number("(d{5}:dd:dd)?,")            // hour meter
            .number("(x+)?,")                    // adc 1
            .number("(x+)?,").optional()         // adc 2
            .number("(d{1,3})?,")                // battery
            .number("(?:(xx)(xx)(xx))?,")        // device status
            .expression("(.*)")                  // additional data
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_IGN = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF):GTIG[NF],")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("(d+),")                       // ignition off duration
            .expression(PATTERN_LOCATION.pattern())
            .number("(d{5}:dd:dd)?,")            // hour meter
            .number("(d{1,7}.d)?,")              // odometer
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_LSW = new PatternBuilder()
            .text("+RESP:").expression("GT[LT]SW,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("[01],")                     // type
            .number("([01]),")                   // state
            .expression(PATTERN_LOCATION.pattern())
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_IDA = new PatternBuilder()
            .text("+RESP:GTIDA,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,,")               // device name
            .number("([^,]+),")                  // rfid
            .expression("[01],")                 // report type
            .number("1,")                        // count
            .expression(PATTERN_LOCATION.pattern())
            .number("(d+.d),")                   // odometer
            .text(",,,,")
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_WIF = new PatternBuilder()
            .text("+RESP:GTWIF,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("(d+),")                     // count
            .number("((?:x{12},-?d+,,,,)+),,,,") // wifi
            .number("(d{1,3}),")                 // battery
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_GSM = new PatternBuilder()
            .text("+RESP:GTGSM,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("(?:STR|CTN|NMR|RTL),")  // fix type
            .expression("(.*)")                  // cells
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF):GT...,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("d*,")
            .number("(d{1,2}),")                 // report type
            .number("d{1,2},")                   // count
            .expression(PATTERN_LOCATION.pattern())
            .groupBegin()
            .number("(d{1,7}.d)?,").optional()   // odometer
            .number("(d{1,3})?,")                // battery
            .or()
            .number("(d{1,7}.d)?,")              // odometer
            .groupEnd()
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)")  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_BASIC = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF)").text(":")
            .expression("GT...,")
            .number("(?:[0-9A-Z]{2}xxxx)?,").optional() // protocol version
            .number("(d{15}|x{14}),")            // imei
            .any()
            .number("(d{1,2})?,")                // hdop
            .number("(d{1,3}.d)?,")              // speed
            .number("(d{1,3})?,")                // course
            .number("(-?d{1,5}.d)?,")            // altitude
            .number("(-?d{1,3}.d{6})?,")         // longitude
            .number("(-?d{1,2}.d{6})?,")         // latitude
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(d+),")                     // mcc
            .number("(d+),")                     // mnc
            .number("(x+),")                     // lac
            .number("(x+),").optional(4)         // cell
            .any()
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    //--
    //+RESP:GTSTT,120113,351564053997177,,42,13,0.0,0,113.8,20.534895,52.503012,20180801162918,260,03,e7a7,5f20,,20180801162918,250E$
    //+RESP:GTSTT,120113,351564053997177,,41,8,0.0,0,108.6,21.039612,52.108128,20180802192317,260,03,e2ea,7031,,20180802192317,2FE8$
    //+RESP:GTSTT,1A0500,860599000310581,GL300,42,0,0.0,0,266.3,17.333674,50.477497,20180803010215,0260,0003,D1C5,24E0,,20180803010442,A5E0$
    //--
    public static final Pattern PATTERN_STT = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF):GTSTT,")
            .number("(?:[0-9A-Z]{2}xxxx)?,")     // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("(d{2}),")                    // state 21|22|41|42
            .expression(PATTERN_LOCATION.pattern())
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)")              // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();

    public static final Pattern PATTERN_BPL = new PatternBuilder()
            .text("+").expression("(?:RESP|BUFF)").text(":")
            .expression("GTBPL,")
            .number("(?:[0-9A-Z]{2}xxxx)?,").optional() // protocol version
            .number("(d{15}|x{14}),")            // imei
            .expression("[^,]*,")                // device name
            .number("(d+.d+),")                  // battery
            .expression(PATTERN_LOCATION.pattern())
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)").optional(2)  // time (hhmmss)
            .text(",")
            .number("(xxxx)")                    // count number
            .text("$").optional()
            .compile();



}
