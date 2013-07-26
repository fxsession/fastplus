package com.fxsession.fastplus.receiver.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;




/**
 * @author Dmitry Vulf
 * 
 * Implements OBR(Order book incremental) feed
 * 
 * Read OBR from primary site
 *
 */
public class MoexFeedOBR extends MoexFeed{

	private static Logger mylogger = Logger.getLogger(MoexFeedOBR.class);
	
	static private final String TEMPLATE_ID = "3312";
	
	static private final String SYMBOL = "Symbol";
	static private final String MSGSEQNUM = "MsgSeqNum";
	static private final String GROUPMDENTRIES = "GroupMDEntries"; 

	public MoexFeedOBR(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	
	
	@Override
	public void processMessage(Message message) {
	
		if (message.getTemplate().getId().equals(TEMPLATE_ID)){
			//pack raw message into new construction
			
			String msgSeqNum = message.getString(MSGSEQNUM);
			int iMsgSeqNum = Integer.parseInt(msgSeqNum); 
			SequenceValue secval =message.getSequence (GROUPMDENTRIES);
			String keyValue = null;
			if (secval.getValues().length>0){
				keyValue = secval.getValues()[0].getString(SYMBOL);}
			dispatcher.dispatch(new FPFMessage(keyValue,iMsgSeqNum,message));
		}
		else{//heartbeat
			if (mylogger.isDebugEnabled()){				 
				mylogger.debug("Heartbeat");
			}
		}
	}

	@Override
	public String getSiteID() {
		return "OBR-A";	}
}
