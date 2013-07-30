package com.fxsession.fastplus.fpf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.handler.moex.MoexHandler;
import com.fxsession.fastplus.receiver.moex.MoexFeedOBR;
import com.fxsession.fastplus.receiver.moex.MoexFeedOBR2;
import com.fxsession.fastplus.receiver.moex.MoexFeedOLR;

/**
 * @author Dmitry Vulf
 * 
 * Main class dispatching all events comind from feeds.
 * Main functions:
 * - Create feeds that we are going to listen
 * - Register handlers (implementors of IFPFHandler). Handlers implement logic specific for each particular instrument i.e. EUR_RUB__TOD
 * - Dispatch every message coming from the created feed (a feed in a processMessage method calls <dispatcher.dispatch> 
 * sending key,msgSeqNum, message. See FPFeed) matching key parameter with getSiteID() and forwarding message to the certain handler.
 * Thus a key coming from feed.processMessage must coincide with getSiteID() of one of the registered handlers in order to be processed        
 *
 */

public class FPFeedDispatcher {
	
	private static Logger mylogger = Logger.getLogger(FPFeedDispatcher.class);
	
	private Map <String, IFPFHandler> handlers;
	
  
	
	private final MoexFeedOLR olr_consumer;
	
	private void registerHandler(IFPFHandler handler){
		handlers = new HashMap<String,IFPFHandler>();
		handlers.put(handler.getInstrumentID(), handler);
	}
	
	
	private void listenObr2 (){
		/**
		 * Run secondary feed in the background
		 * Left if to future do far.  Can simuteniously 2 feeds. 
		 */
/*		
		obr_consumer2.setBlockReader(new MessageBlockReader() {
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
        new Thread(new Runnable() {
            public void run() {
                try {
                	obr_consumer2.start();        	
                } catch (Exception e) {
                    mylogger.error( e);
                }
            }
        }).start();
*/
	}

	
	public	FPFeedDispatcher (){
	    olr_consumer = new MoexFeedOLR(this);
	}
	
	public void dispatch(String key, int msgSeqNum, Message message){
		/**
		 * Dispatching is quite simple 	
		 */
		IFPFHandler handler = handlers.get(key);
		if (handler !=null){
			handler.push(message);
		}else{
		  if (mylogger.isDebugEnabled())
		  	mylogger.debug(msgSeqNum + ": " +key);
		}
		
	}
	
    public void run(){		 
		try{
			//in one thread read from primary site
    		registerHandler(new MoexHandler());


    		/*
    	     * setBlockerReader works with preambula which can be specific for 
    	     * for different venues.
    	     * Here it reads 4 bytes preambula (which is compatible with Micex)
    	     * To specify new behavior - override this method 
    		*/
//    		listenObr2();
    		
    		
            olr_consumer.setBlockReader(new MessageBlockReader() {
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
            	
            olr_consumer.start();

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
