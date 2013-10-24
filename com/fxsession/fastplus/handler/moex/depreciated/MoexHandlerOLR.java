package com.fxsession.fastplus.handler.moex.depreciated;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;



import com.fxsession.fastplus.fpf.FPFMessage;

import com.fxsession.fastplus.fpf.OnCommand;
import com.fxsession.fastplus.fpf.depreciated.FPFTransaction;
import com.fxsession.fastplus.handler.moex.MoexHandler;
import com.fxsession.utils.FXPException;


/**
 * @author Dmitry Vulf
 * 
 * Incremenetal order book handler
 * 
 * It is possible, though not guaranteed, that a set of these book update messages can be used to construct the current, 
 * correct state of a book without prior book state knowledge. 
 * This process called Natural Refresh. 
 * Prior to beginning a natural refresh, the entire book should be emptied. 
 * Natural refresh assumes no prior knowledge of book state. 
 * Natural Refresh works best for aggregated orderbook feed and for highly liquid securities. 
 *
 */
public class MoexHandlerOLR  extends MoexHandler {
	private static Logger mylogger = Logger.getLogger(MoexHandlerOLR.class);
	private final FPFTransaction transaction;
	
	AtomicInteger  rptSeq = new AtomicInteger(-1);

	public MoexHandlerOLR(String instrument) {
		super(instrument);
		transaction = new FPFTransaction();
	}


	@Override
	public OnCommand push(FPFMessage message) throws FXPException {
				OnCommand retval = OnCommand.ON_PROCESS;
			try{	
			    String rptseq = message.getFieldValue(RPTSEQ);
			    if (checkRepeatMessage(rptseq))
			    	return retval;
				String key =  message.getFieldValue(MDENTRYID);
			    String type = message.getFieldValue(MDENTRYTYPE);
			    String size = message.getFieldValue(MDENTRYSIZE);
			    String px = message.getFieldValue(MDENTRYPX);
			    String timemcs = message.getFieldValue(ORIGINTIME);
			    String timestamp = message.getFieldValue(MDENTRYTIME);
			    String updAction =message.getFieldValue(MDUPDATEACTION);
			    transaction.disptch (updAction, 
			    		             type, 
			    		             key, 
			    		             size, 
			    		             px, 
			    		             timestamp, 
			    		             timemcs);		    
			}catch(Exception e) {
	        	mylogger.error(e);
	        	throw new FXPException(e);
	        }
		return retval;
	}
	
}
