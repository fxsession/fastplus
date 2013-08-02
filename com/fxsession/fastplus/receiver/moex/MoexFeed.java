package com.fxsession.fastplus.receiver.moex;



import java.io.IOException;
import java.io.InputStream;

import org.openfast.Message;
import org.openfast.MessageBlockReader;
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
	
	
	static protected final String SYMBOL = "Symbol";
	static protected final String MSGSEQNUM = "MsgSeqNum";
	static protected final String GROUPMDENTRIES = "GroupMDEntries"; 


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

			String msgSeqNum = message.getString(MSGSEQNUM);
			int iMsgSeqNum = Integer.parseInt(msgSeqNum);
			SequenceValue secval =message.getSequence (GROUPMDENTRIES);
			String keyValue = null;
			if (secval.getValues().length>0){
				keyValue = secval.getValues()[0].getString(SYMBOL);
				dispatcher.dispatch(this,keyValue,iMsgSeqNum,message);
			}
			
		}
	}
	
	
    public void setBlockReader() {
    	this.blockReader =new MessageBlockReader() {
			byte[] buffer = new byte[4];
				public boolean readBlock(InputStream in) {
					try {
						int numRead = in.read(buffer);
						if (numRead < buffer.length) {
						return false;}
						} catch (IOException e) {
							return false;   }
						return true;
						}
				public void messageRead(InputStream in, Message message) {
				}
        	};    	
    }

		
}
