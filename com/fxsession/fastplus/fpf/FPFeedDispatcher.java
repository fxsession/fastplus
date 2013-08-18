package com.fxsession.fastplus.fpf;


import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.log4j.Logger;

import com.fxsession.fastplus.handler.moex.MoexHandlerOLR;
import com.fxsession.fastplus.receiver.moex.MoexFeedOLR;
import com.fxsession.fastplus.receiver.moex.MoexFeedOLR2;


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
	private Integer procCounter = 0;
	private Integer globalCounter = 0;
	
	
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
	private final MoexFeedOLR olr2_consumer;
	
	private void registerHandler(IFPFHandler handler,IFPFeed feed){
		String mapKey = composeKey(feed,handler.getInstrumentID());
		handlers.put(mapKey, handler);
	}
	
	private String composeKey (IFPFeed ifeed, String key){
		
		String feedKey = ifeed.getSiteID();
		
		if (feedKey == null) {
			throw new IllegalArgumentException("Define feed ID"); }

		if (key == null) {
			throw new IllegalArgumentException("Define key"); }

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
	
	private void listenOlr2(){
		/**
		 * Run secondary feed in the background
		 *  
		 */

        new Thread(new Runnable() {
            public void run() {
                try {
                	olr2_consumer.start();        	
                } catch (Exception e) {
                    mylogger.error( e);
                }
            }
        }).start();
	}

	
	public	FPFeedDispatcher (){
	    //ols_consumer = new MoexFeedOLS(this);
		olr_consumer = new MoexFeedOLR(this);
		olr2_consumer = new MoexFeedOLR2(this);
	}
	
	
	public void dispatch(IFPFeed ifeed, //pointer for feed which called this method ie dispatch(this...)
						FPFMessage message) // the message itself
	{
		procCounter ++;
		IFPFHandler handler = handlers.get(composeKey(ifeed,message.getKeyFieldValue()));
		
		if (handler !=null){
			OnCommand command = handler.push(message);
			switch(command){
				case ON_STOP_FEED : ifeed.stopProcess();
			default:
				break;
			}
		}
		else if (mylogger.isDebugEnabled())
			mylogger.debug(message.getKeyFieldValue());
	}
	
    public void run(){
			//in one thread read from primary site
    		final MoexHandlerOLR olrHandler = new MoexHandlerOLR();   
			registerHandler(olrHandler,olr_consumer);
//			registerHandler(olrHandler,olr2_consumer);
			listenOlr();
//			listenOlr2();
			
    		/*by now I can only register that feed us down
    		 *do later reconnection attempt
    		 */	
    		new Thread(new Runnable() {
    			public void run() {
    				try {
    					while (true){
    						Integer intCounter = procCounter;
    						globalCounter ++;
    						Thread.sleep(60000);
    						if (intCounter.equals(procCounter)){
    							mylogger.info("No response from the feed");
    						}
    						else
    							procCounter = 0;
    						olrHandler.scanBid();
    						olrHandler.scanAsk();
    						
    						if (globalCounter==2)
    							System.exit(-1);
    					}
    				} catch (Exception e) {
    					mylogger.error( e);
    				}
    			}
    		}).start();
    }
}
