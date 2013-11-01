package com.fxsession.fastplus.fpf;


/**
 * @author Dmitry Vulf
 * define fields which are used in the program
 */
public interface IFPField {
        
        /*
         * Defined only those fields which are used 
         */
    static public final int HEARTBEAT              = 100;
    static public final int SYMBOL                 = 1000;
    static public final int MSGSEQNUM              = 1001;
    static public final int GROUPMDENTRIES         = 1002; 
    static public final int MDENTRYID              = 1003;
    static public final int MDENTRYPX              = 1004;
    static public final int MDENTRYSIZE            = 1005;
    static public final int MDENTRYTYPE            = 1006;
    static public final int MDUPDATEACTION         = 1007;
    static public final int RPTSEQ                 = 1008;
    static public final int ORIGINTIME             = 1009;
    static public final int MDENTRYTIME            = 1100; 
    static public final int LASTMSGSEQNUMPROCESSED = 1101;
}