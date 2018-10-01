package com.vd5.dcs.protocols.tk10x;

import com.vd5.dcs.helper.PatternBuilder;

import java.util.regex.Pattern;

/**
 * @author beou on 5/9/18 18:27
 */
public class TkPatterns {
    public static final Pattern PATTERN = new PatternBuilder()
            .text("(").optional()
            .number("(d+)(,)?")                  // device id
            .expression("(.{4}),?")              // command
            .number("(d*)")
            .number("(dd)(dd)(dd),?")            // date (mmddyy if comma-delimited, otherwise yyddmm)
            .expression("([AV]),?")              // validity
            .number("(d+)(dd.d+)")               // latitude
            .expression("([NS]),?")
            .number("(d+)(dd.d+)")               // longitude
            .expression("([EW]),?")
            .number("(d+.d)(?:d*,)?")            // speed
            .number("(dd)(dd)(dd),?")            // time (hhmmss)
            .number("(d+.?d{1,2}),?")            // course
            .groupBegin()
            .number("([01])")                    // charge
            .number("([01])")                    // ignition
            .number("(x)")                       // io
            .number("(x)")                       // io
            .number("(x)")                       // io
            .number("(xxx),?")                   // fuel
            .groupEnd("?")
            .number("(?:L(x+))?")                // odometer
            .any()
            .number("([+-]ddd.d)?")              // temperature
            .groupBegin()
            .number("([+-]?d+.d{1,2}),")         // altitude
            .number("(d+)$")                     // number of visible satellites
            .groupEnd("?")
            .text(")").optional()
            .compile();

    public static final Pattern PATTERN_BATTERY = new PatternBuilder()
            .text("(").optional()
            .number("(d+),")                     // device id
            .text("ZC20,")
            .number("(dd)(dd)(dd),")             // date (ddmmyy)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(d+),")                     // battery level
            .number("(d+),")                     // battery voltage
            .number("(d+),")                     // power voltage
            .number("d+")                        // installed
            .any()
            .compile();

    public static final Pattern PATTERN_NETWORK = new PatternBuilder()
            .text("(").optional()
            .number("(d{12})")                   // device id
            .text("BZ00,")
            .number("(d+),")                     // mcc
            .number("(d+),")                     // mnc
            .number("(x+),")                     // lac
            .number("(x+),")                     // cid
            .any()
            .compile();

    public static final Pattern PATTERN_LBSWIFI = new PatternBuilder()
            .text("(").optional()
            .number("(d+),")                     // device id
            .expression("(.{4}),")               // command
            .number("(d+),")                     // mcc
            .number("(d+),")                     // mnc
            .number("(d+),")                     // lac
            .number("(d+),")                     // cid
            .number("(d+),")                     // number of wifi macs
            .number("((?:(?:xx:){5}(?:xx)\\*[-+]?d+\\*d+,)*)")
            .number("(dd)(dd)(dd),")             // date (ddmmyy)
            .number("(dd)(dd)(dd)")              // time (hhmmss)
            .any()
            .compile();

    public static final Pattern PATTERN_COMMAND_RESULT = new PatternBuilder()
            .text("(").optional()
            .number("(d+),")                     // device id
            .expression(".{4},")                 // command
            .number("(dd)(dd)(dd),")             // date (ddmmyy)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .expression("\\$([\\s\\S]*?)(?:\\$|$)") // message
            .any()
            .compile();
}
