package com.fxsession.fastplus.receiver.moex;



import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.session.Endpoint;

import com.fxsession.fastplus.fpf.FPFXmlSettings;
import com.fxsession.fastplus.fpf.FPFeed;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;
import com.fxsession.fastplus.ssm.SSMConnection;
import com.fxsession.fastplus.ssm.SSMEndpoint;

/**
 * @author Dmitry Vulf
 *  
 *  Abstract (still) for all MOEX feeds
 *  
 *  Main purpose - get SSM connection
 *  and process basic fields
 */
public abstract class MoexFeed extends FPFeed{
	
	private static Logger mylogger = Logger.getLogger(MoexFeed.class);
	
	static private final String SYMBOL = "Symbol";
	static private final String MSGSEQNUM = "MsgSeqNum";
	static private final String GROUPMDENTRIES = "GroupMDEntries"; 


	public MoexFeed(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	/**
	 * On this point SSM is applied for all inheritors of MoexFeed
	 */
	public Endpoint getEndpoint() {
      String sitename = getSiteID();

      String port   = FPFXmlSettings.readConnectionElement(sitename,SSMConnection.PORT_N);
      String group  = FPFXmlSettings.readConnectionElement(sitename,SSMConnection.GROUP_IP);
      String ifaddr = FPFXmlSettings.readConnectionElement(sitename,SSMConnection.INTERFACE_IP);
      return new SSMEndpoint(Integer.parseInt(port),group,ifaddr);
	}

	/*
	 * Basic messages processing is common for all MOEX feeds
	 * Need specific implementation - override processMessage 
	 * 
	 */
	
	@Override
	public void processMessage(Message message) {
		if (message.getTemplate().getId().equals(getTemplateID())){
			//pack raw message into new construction
			String msgSeqNum = message.getString(MSGSEQNUM);
			int iMsgSeqNum = Integer.parseInt(msgSeqNum);
			SequenceValue secval =message.getSequence (GROUPMDENTRIES);
			String keyValue = null;
			if (secval.getValues().length>0){
				keyValue = secval.getValues()[0].getString(SYMBOL);
				dispatcher.dispatch(keyValue,iMsgSeqNum,message);
			}
			
		} else{ //else heartbeat
		 if (mylogger.isDebugEnabled()){
			 mylogger.debug("Heartbeat");
		 }
		}
		
	}


}
