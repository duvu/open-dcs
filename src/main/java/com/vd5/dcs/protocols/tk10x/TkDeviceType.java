package com.vd5.dcs.protocols.tk10x;

/**
 * @author beou on 5/5/18 00:02
 */
/**
 *** Enumerated type: TK device type
 **/
public enum TkDeviceType {
    UNKNOWN  (0,"Unknown" ),
    TK102    (1,"TK102"   ),
    TK102_2  (2,"TK102-2" ),
    TK102B   (3,"TK102B"  ),
    TK103_1  (4,"TK103-1" ),
    TK103_2  (5,"TK103-2" ),
    TK103_3  (6,"TK103-3" ),
    TKnano_1 (7,"TKnano-1"), // EXPERIMENTAL: may not be supported
    TKnano_2 (8,"TKnano-2"), // EXPERIMENTAL: may not be supported
    VJOY     (9,"VJoy"    ); // EXPERIMENTAL: may not be supported
    // ---
    private int    vv = 0;
    private String dd = null;
    TkDeviceType(int v, String d) { vv = v; dd = d; }
    public String  toString()     { return dd; }
    public boolean isUnknown()    { return this.equals(UNKNOWN); }
    public boolean isTK102()      { return this.equals(TK102); }
    public boolean isTKnano()     { return this.equals(TKnano_1) || this.equals(TKnano_2); }
};
