package com.fxsession.fastplus.receiver.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;

import com.fxsession.fastplus.ssm.SSMMessageConsumer;

/**
 * @author Dmitry Vulf
 * 
 * Implements OBR(Order book incremental) logic
 *
 */
public class MoexOBR extends SSMMessageConsumer{
	private static Logger mylogger = Logger.getLogger(MoexIDF.class);
	
	static private final String TEMPLATE_ID = "3312";
	static public final String MSGSEQNUM = "MsgSeqNum";
	static public final String MDUPDATEACTION = "MDUpdateAction";
	static public final String MDENTRYTYPE = "MDEntryType";
	static public final String MDENTRYID = "MDEntryID";
	static public final String SYMBOL = "Symbol";
	static public final String RPTSEQ = "RptSeq";
	static public final String MDENTRYPX =  "MDEntryPx";
	static public final String MDENTRYSIZE = "MDEntrySize";
	static public final String TRADINGSESSIONID= "TradingSessionID";
	static public final String TRADINGSESSIONSUBID	= "TradingSessionSubID";
	
	public MoexOBR(String id) {
		super(id);
	}
	
	@Override
	public void processMessage(Message message) {
		if (message.getTemplate().getId().equals(TEMPLATE_ID)){
			String p1 = MSGSEQNUM + message.getString(MSGSEQNUM);
			String p2 = MDUPDATEACTION + message.getString(MDUPDATEACTION);
			String p3 = MDENTRYTYPE+message.getString(MDENTRYTYPE);
			String p4 = MDENTRYID + message.getString(MDENTRYID);
			String p5 = SYMBOL + message.getString(SYMBOL);
			String p6 = RPTSEQ + message.getString(RPTSEQ);
			String p7 = MDENTRYPX + message.getString(MDENTRYPX);
			String p8 = MDENTRYSIZE + message.getString(MDENTRYSIZE);
			String p9 = TRADINGSESSIONID +message.getString(TRADINGSESSIONID);
			String p10 =TRADINGSESSIONSUBID +message.getString(TRADINGSESSIONSUBID);			
			if (mylogger.isDebugEnabled()){   
				//can significantly slow execution
				mylogger.debug(p1+p2+p3+p4+p5+p6+p7+p8+p9+p10);
			}
		}
		else{
			//heartbeat 
			if (mylogger.isDebugEnabled()){				 
				mylogger.debug("Heartbeat");
			}
		}
	}

}
