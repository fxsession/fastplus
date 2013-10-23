/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.openfast.session.FastConnectionException;


import com.fxsession.fastplus.fpf.FPFMessage;



import com.fxsession.fastplus.fpf.FPFOrderBook;
import com.fxsession.fastplus.fpf.OnCommand;


/**
 * @author Dmitry Vulf
 * 
 * OBR
 *
 */

public class MoexHandlerOBR extends MoexHandler {
    private static Logger mylogger = Logger.getLogger(MoexHandlerOBR.class);
	private final FPFOrderBook orderbook; 
	
	public MoexHandlerOBR(String instrument) {
		super(instrument);
		
		orderbook = new FPFOrderBook(instrument);
	}

	/*
	 * Dispatcher
	 * 
	 */
	public OnCommand push(FPFMessage message) throws FastConnectionException {
	
		OnCommand retval = OnCommand.ON_PROCESS;
		
		try{
			String rptseq = message.getFieldValue(RPTSEQ);
			
			if (checkRepeatMessage(rptseq))
				return retval;
				
			String type = message.getFieldValue(MDENTRYTYPE);
			String size = message.getFieldValue(MDENTRYSIZE);
			String px = message.getFieldValue(MDENTRYPX);
			String updAction =message.getFieldValue(MDUPDATEACTION);
			 
		    orderbook.dispatch(updAction, 
		    		           type, 
		    		           size, 
		    		           px);

		} catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
      }
	  return retval;
	}
	
   /**
   * Clones bidbook to object 
   * @param object  
   * object should be null
   */
	public void cloneBidbook(TreeMap <Double,Integer> object){
		orderbook.cloneBid(object);
	}
	
	/**
	 * Clones askbook to object 
	 * @param object  
	 * object should be null
	 */
	
	public void cloneAskbook(TreeMap <Double,Integer> object){
		orderbook.cloneAsk(object);
	}

	
}
