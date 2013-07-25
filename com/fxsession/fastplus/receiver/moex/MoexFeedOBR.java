package com.fxsession.fastplus.receiver.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;



/**
 * @author Dmitry Vulf
 * 
 * Implements OBR(Order book incremental) feed
 *
 */
public class MoexFeedOBR extends MoexFeed{
	private static Logger mylogger = Logger.getLogger(MoexFeedOBR.class);
	
	static private final String TEMPLATE_ID = "3312";
	static private final String GROUPMDENTRIES = "GroupMDEntries";
	static private final String MSGSEQNUM = "MsgSeqNum";
	static private final String MDUPDATEACTION = "MDUpdateAction";
	static private final String MDENTRYTYPE = "MDEntryType";
	static private final String MDENTRYID = "MDEntryID"; 
	static private final String SYMBOL = "Symbol";
	static private final String RPTSEQ = "RptSeq";
	static private final String MDENTRYPX =  "MDEntryPx";
	static private final String MDENTRYSIZE = "MDEntrySize";
	
	public MoexFeedOBR(String id) {
		super(id);
	}
	
	@Override
	public void processMessage(Message message) {
		if (message.getTemplate().getId().equals(TEMPLATE_ID)){
  
			String p1 = MSGSEQNUM + ":"+	message.getString(MSGSEQNUM);

			SequenceValue secval =message.getSequence (GROUPMDENTRIES);
			if (secval.getValues().length>0){
				String p2 = MDUPDATEACTION + ":"+ secval.getValues()[0].getString(MDUPDATEACTION);
				String p3 = MDENTRYTYPE + ":"+ secval.getValues()[0].getString(MDENTRYTYPE);
				String p4 = MDENTRYID + ":"+ secval.getValues()[0].getString(MDENTRYID);
				String p5 = SYMBOL + ":"+ secval.getValues()[0].getString(SYMBOL);
				String p6 = RPTSEQ + ":"+ secval.getValues()[0].getString(RPTSEQ);
				String p7 = MDENTRYPX + ":"+ secval.getValues()[0].getString(MDENTRYPX);
				String p8 = MDENTRYSIZE + ":"+ secval.getValues()[0].getString(MDENTRYSIZE);
				if (mylogger.isDebugEnabled()){   
					//can significantly slow execution
					mylogger.debug(p1+p2+p3+p4+p5+p6+p7+p8);
				}
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
