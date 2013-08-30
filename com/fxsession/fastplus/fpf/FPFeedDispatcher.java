package com.fxsession.fastplus.fpf;


import java.util.HashMap;

import java.util.Map;


import org.apache.log4j.Logger;

import com.fxsession.fastplus.handler.moex.MoexHandlerOBR;
import com.fxsession.fastplus.handler.moex.MoexHandlerOLR;
import com.fxsession.fastplus.receiver.moex.MoexFeedOBR;
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
	private class MessageDispathcer{
		private IFPFHandler handler;
		private IFPFeed feed;
		MessageDispathcer (IFPFHandler phandler, IFPFeed pfeed){
			handler = phandler;
			feed = pfeed;
		}
		IFPFeed getFeed() {return feed;}
		IFPFHandler getHandler() {return handler;} 
	} 
	
	private final Map <String, MessageDispathcer> handlers = new HashMap<String,MessageDispathcer>();  
		
	private MoexFeedOLR olrFeed;
	private MoexHandlerOLR olrHandler;
	private MoexFeedOBR obrFeed;
	private MoexHandlerOBR obrHandler;
	
	private void registerHandler(IFPFHandler handler,IFPFeed feed){
		if (handler!=null && feed!=null){
			String mapKey = composeKey(feed,handler.getInstrumentID());
			handlers.put(mapKey, new MessageDispathcer(handler,feed));
		}
		else 
			throw new NullPointerException();
	}
	
	private boolean monitorFeed(){
		boolean retVal = true;
     	try {		
			for (Map.Entry<String, MessageDispathcer> entry : handlers.entrySet())
			if (!entry.getValue().getFeed().hasStarted()){
				mylogger.error("no response from  " + entry.getValue().getFeed().toString());
				//trying to restart
				entry.getValue().getFeed().restart();   //this shoul be done in a separate thread
				retVal = false;
			}
         } catch (Exception e) {
             mylogger.error( e);
        }
		return retVal;
	}
	
	
	private String composeKey (IFPFeed ifeed, String key){
		
		String feedKey = ifeed.getSiteID();
		
		if (feedKey == null) {
			throw new IllegalArgumentException("define feed ID"); }

		if (key == null) {
			throw new IllegalArgumentException("define key"); }

		return feedKey+key; 
	}
	
	
	private void startFeeds(){
	//each feeds start sending messages in a separate thread
	 	for (final Map.Entry<String, MessageDispathcer> entry : handlers.entrySet()) 
        new Thread(new Runnable() {
            public void run() {
                try {
                	entry.getValue().getFeed().start();        	
                } catch (Exception e) {
                    mylogger.error( e);
                }
            }
        }).start();
	}
	
	
	
	public void dispatch(IFPFeed ifeed, //pointer for feed which called this method ie dispatch(this...)
						FPFMessage message) // the message itself
	{
		MessageDispathcer dispatcher = handlers.get(composeKey(ifeed,message.getKeyFieldValue()));
		if (dispatcher!=null){
			IFPFHandler handle = dispatcher.getHandler();
			OnCommand command = handle.push(message);
			switch(command){
				case ON_STOP_FEED : ifeed.stopProcess();
			default:
				break;
			}
		}
	}
	
    public void run(){
			olrFeed = new MoexFeedOLR(this);
			obrFeed = new MoexFeedOBR(this);
			olrHandler = new MoexHandlerOLR();
			obrHandler = new MoexHandlerOBR();
    		//OLR
			registerHandler(olrHandler,olrFeed);
			//OBR
			registerHandler(obrHandler,obrFeed);
			
    		/*by now I can only register that feed us down
    		 *do later reconnection attempt
    		 */
    		 
    		 startFeeds();

    		new Thread(new Runnable() {
    			public void run() {
    				try {
    					
    					while (true){
    						Thread.sleep(10000);// parameter - millis
    						if (monitorFeed()) {
								obrHandler.scanBid();
								obrHandler.getBidWeightedBySize(10000);
   								obrHandler.scanAsk();
   								obrHandler.getAskWeightedBySize(10000);
   							}
//    						if (globalCounter==5)
//    							System.exit(-1);
    					}
    				} catch (Exception e) {
    					mylogger.error( e);
    				}
    			}
    		}).start();
    		
    }
}
