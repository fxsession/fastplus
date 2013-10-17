/**
 *  @author Dmitry Vulf
 *  
 *  Orders snapshot
 */
package com.fxsession.fastplus.handler.moex.depreciated;

import org.apache.log4j.Logger;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFOrderBook;
import com.fxsession.fastplus.fpf.OnCommand;
import com.fxsession.fastplus.handler.moex.MoexHandler;
import com.fxsession.fastplus.handler.moex.MoexHandlerOBR;

public class MoexHandlerOLS  extends MoexHandler {
    private static Logger mylogger = Logger.getLogger(MoexHandlerOBR.class);
	private final FPFOrderBook orderbook; 
	
	public MoexHandlerOLS(String instrument) {
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
			 
		    orderbook.dispatch(updAction, type, size, px);

		} catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
      }
	  return retval;
	}

}
