package com.fxsession.fastplus.receiver.moex;



import java.io.IOException;
import java.io.InputStream;


import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFeed;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;
import com.fxsession.fastplus.fpf.IFPField;

import com.fxsession.fastplus.receiver.moex.depreciated.MoexFeedOLR;
import com.fxsession.fastplus.ssm.SSMConnection;
import com.fxsession.fastplus.ssm.SSMEndpoint;

import com.fxsession.utils.FXPException;
import com.fxsession.utils.FXPXml;

/**
 * @author Dmitry Vulf
 *  
 *  Abstract (still) for all MOEX feeds
 *  
 *  Main purpose - get SSM connection
 *  and process basic fields
 */
public abstract class MoexFeed extends FPFeed implements IFPField{
	
	private static Logger mylogger = Logger.getLogger(MoexFeedOLR.class);

	public MoexFeed(FPFeedDispatcher dispatcher){
		super(dispatcher);
	}

	/**
	 * On this point SSM is applied for all inheritors of MoexFeed
	 * @throws FastConnectionException 
	 */
	public Endpoint getEndpoint() throws FastConnectionException {
      String sitename = getSiteID();
	  Endpoint epoint =null; 	
      String port;
	try {
		port = FXPXml.readConnectionElement(sitename,SSMConnection.PORT_N);
        String group  = FXPXml.readConnectionElement(sitename,SSMConnection.GROUP_IP);
        String ifaddr = FXPXml.readConnectionElement(sitename,SSMConnection.INTERFACE_IP);
        epoint = new SSMEndpoint(Integer.parseInt(port),group,ifaddr);
	}catch (Exception e) {
    	mylogger.error(e);
    	throw new FastConnectionException(e);
 	}
 	return epoint;
   }

	/*
	 * Basic behavior - send a heartbeat, should overriden to fill message
	 */
	
	@Override
	public void processMessage(Message message) throws FastConnectionException {
		try {
			FPFMessage fmessage = new FPFMessage(HEARTBEAT);			
			dispatcher.dispatch(this,fmessage);
		}catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
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

	public String getHeartbeatID() {
		return "2008";
	}

	protected void processHeartbeat( Message message) throws FXPException{
		if (message.getTemplate().getId().equals(getHeartbeatID())){
			FPFMessage fmessage = new FPFMessage(HEARTBEAT);
			dispatcher.dispatch(this,fmessage);
		}
		else {
			mylogger.error("Check Template ID");
		}
	}
	
}
