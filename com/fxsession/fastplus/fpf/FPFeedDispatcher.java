package com.fxsession.fastplus.fpf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.handler.moex.MoexHandler;
import com.fxsession.fastplus.receiver.moex.MoexFeedOBR;

/**
 * @author Dmitry Vulf
 * 
 * Main class for dispatcher all events from Feeds
 *
 */

public class FPFeedDispatcher {
	
	
	private Map <String, IFPFHandler> handlers;
	
	
	private void registerHandler(IFPFHandler handler){
		handlers = new HashMap<String,IFPFHandler>();
		handlers.put(handler.getSiteID(), handler);
	}
	
	public void dispatch(FPFMessage message){
		//extract keyValue and MsgSeqNum
	
		IFPFHandler handler = handlers.get(message.getKey());
		if (handler !=null)
			handler.push(message);
	}
	
    public void run(){		 
		try{
			//in one thread read from primary site
    		MoexFeedOBR idf_consumer= new MoexFeedOBR(this);
    		registerHandler(new MoexHandler());
    		/*
    	     * setBlockerReader works with preambula which can be specific for 
    	     * for different venues.
    	     * Here it reads 4 bytes preambula (which is compatible with Micex)
    	     * To specify new behavior - override this method 
    		*/
            idf_consumer.setBlockReader(new MessageBlockReader() {
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
            	});
            idf_consumer.start();
			} catch (FastConnectionException e) {
				System.out.println("Unable to connect to endpoint: " + e.getMessage());
				System.exit(1);
			} catch (IOException e) {
				System.out.println("An IO error occurred while consuming messages: " + e.getMessage());
				System.exit(1);
			}
    	    //in the second thread read from secondary site
	}
	

}
