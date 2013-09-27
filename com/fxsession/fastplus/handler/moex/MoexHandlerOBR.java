/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.openfast.session.FastConnectionException;


import com.fxsession.fastplus.fpf.FPFMessage;

import com.fxsession.fastplus.fpf.FPFOrderBookL2OBR;
import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.IFPFOrderBook;
import com.fxsession.fastplus.fpf.IFPField;
import com.fxsession.fastplus.fpf.OnCommand;


/**
 * @author Dmitry Vulf
 * 
 * OBR
 *
 */

public class MoexHandlerOBR extends FPFOrderBookL2OBR implements IFPFHandler, IFPField {
	


	private static Logger mylogger = Logger.getLogger(MoexHandlerOBR.class);
	
	private AtomicInteger  rptSeq = new AtomicInteger(-1);

	public MoexHandlerOBR(String instrument) {
		super(instrument);

	}
	
	
	public boolean checkRepeatMessage(String sRpt) {
		/*
		 * THis method cuts off duplicate messages coming from the 2 stream. However it cuts only 95% of duplicates 
		 */
		Integer iRep =   Integer.valueOf(sRpt);
		if (iRep ==rptSeq.intValue())
			return true;
		else{
			rptSeq.set(iRep);
			return false;
		}
	} 

	
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

			switch (updAction){
				case IFPFOrderBook.ADD 		: 
					if (type.equals(IFPFOrderBook.BID))  
						addBid(size, px); 
					else
						addAsk(size, px);  
				break;
				case IFPFOrderBook.CHANGE 	:   
					if (type.equals(IFPFOrderBook.BID))
						changeBid(size, px);
					else
						changeAsk(size, px);
				break;
				case IFPFOrderBook.DELETE 	: 
					if (type.equals(IFPFOrderBook.BID)) 
						deleteBid(px,size); 
					else
						deleteAsk(px,size);
				break;
				default :break; 
			}
		} catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
        }
		return retval;
	}

	
}
