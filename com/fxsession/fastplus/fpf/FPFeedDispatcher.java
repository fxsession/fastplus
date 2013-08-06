package com.fxsession.fastplus.fpf;

import java.io.IOException;
import java.util.HashMap;

import java.util.Map;


import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.session.FastConnectionException;


import com.fxsession.fastplus.handler.moex.MoexHandlerIDF;
import com.fxsession.fastplus.handler.moex.MoexHandlerOLR;
import com.fxsession.fastplus.handler.moex.MoexHandlerOLS;
import com.fxsession.fastplus.receiver.moex.MoexFeedIDF;
import com.fxsession.fastplus.receiver.moex.MoexFeedOLR;
import com.fxsession.fastplus.receiver.moex.MoexFeedOLS;


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
	
	
	/**
	* handlers - the map between instrument received from the feed and its handler
	* 
	*
	* key is a concatenation of the Feed.getSideID and Instrument.getInstrumentID
	* The best way to iterate through this map is :
	*
	*			for (Map.Entry<String,IFPFHandler> entry : handlers.entrySet()) {
	*			    String key = entry.getKey();			    }
	*
	*/
	private Map <String, IFPFHandler> handlers = new HashMap<String,IFPFHandler>();  
		
	private final MoexFeedOLR olr_consumer;
	private final MoexFeedIDF idf_consumer;
	private final MoexFeedOLS ols_consumer;
	
	private void registerHandler(IFPFHandler handler,IFPFeed feed){
		String mapKey = composeKey(feed,handler.getInstrumentID());
		handlers.put(mapKey, handler);
	}
	
	private String composeKey (IFPFeed ifeed, String key){
		
		String feedKey = ifeed.getSiteID();
		
		if (feedKey == null) {
			throw new IllegalArgumentException(); }
		
		return feedKey+key; 
	}
	
	private void listenOlr(){
		/**
		 * Run secondary feed in the background
		 * Left if to future do far.  Can simuteniously 2 feeds. 
		 */

        new Thread(new Runnable() {
            public void run() {
                try {
                	olr_consumer.start();        	
                } catch (Exception e) {
                    mylogger.error( e);
                }
            }
        }).start();
	}
	
	private void listenOls(){
		/**
		 * Run secondary feed in the background
		 * Left if to future do far.  Can simuteniously 2 feeds. 
		 */

        new Thread(new Runnable() {
            public void run() {
                try {
                	ols_consumer.start();        	
                } catch (Exception e) {
                    mylogger.error( e);
                }
            }
        }).start();
	}

	
	public	FPFeedDispatcher (){
	    //ols_consumer = new MoexFeedOLS(this);
		olr_consumer = new MoexFeedOLR(this);
		idf_consumer = new MoexFeedIDF(this);
		ols_consumer = new MoexFeedOLS(this);
	}
	
	
	public void dispatch(IFPFeed ifeed, //pointer for feed which called this method ie dispatch(this...)
						String key, 	//key of the instrument - should explicitly coinside with getInstrumentsID in the handler 
						int msgSeqNum, 	// for future use - seqNUmber of the record
						Message message) // the message itself
	{
	    
		IFPFHandler handler = handlers.get(composeKey(ifeed,key));

		
		if (handler !=null){
			OnCommand command = handler.push(message);
			switch(command){
				case ON_STOP_FEED : ifeed.stopProcess();
			default:
				break;
			}
		}else{
		  if (mylogger.isDebugEnabled())
		  	mylogger.debug(ifeed.getSiteID() + "->" + msgSeqNum + ": " +key);
		}
		
	}
	
    public void run(){		 

			//in one thread read from primary site
			registerHandler(new MoexHandlerOLR(),olr_consumer);
//			registerHandler(new MoexHandlerOLS(),ols_consumer);
			listenOlr();
//			listenOls();
	}
	

}
