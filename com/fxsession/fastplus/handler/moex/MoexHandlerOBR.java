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
import com.fxsession.utils.FXPException;


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
	public OnCommand push(FPFMessage message) throws FXPException {
	
		OnCommand retval = OnCommand.ON_PROCESS;
		
		try{
			String rptseq = message.getFieldValue(RPTSEQ);
			
			if (checkRepeatMessage(rptseq))
				return retval;
				
			String type = message.getFieldValue(MDENTRYTYPE);
			String size = message.getFieldValue(MDENTRYSIZE);
			String px = message.getFieldValue(MDENTRYPX);
			String updAction =message.getFieldValue(MDUPDATEACTION);
			 
		    retval = orderbook.dispatch(updAction, 
		    		           type, 
		    		           size, 
		    		           px);
		    
		} catch(Exception e) {
        	mylogger.error(e);
        	throw new FXPException(e);
      }
	  return retval;
	}
	
	
	/**
	 * Get vwap 
	 */
	public Double getVWAPBid(Integer size) {
		return null;
		
		гдеяэ
	}
	
	public Double getVWAPAsk(Integer size) {
		return null;
	}

	

	
}
