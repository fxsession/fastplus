package com.fxsession.fastplus.handler.moex;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFOrderBookL2;
import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.IFPFOrderBook;
import com.fxsession.fastplus.fpf.IFPField;

import com.fxsession.fastplus.fpf.OnCommand;

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
public class MoexHandlerOLR extends FPFOrderBookL2 implements IFPFHandler, IFPField {
	private static Logger mylogger = Logger.getLogger(MoexHandlerOLR.class);
	
	AtomicInteger  rptSeq = new AtomicInteger(-1);
	
	private boolean checkRepeatMessage(String sRpt) {
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
		
	@Override
	public String getInstrumentID() {
		return "EURUSD000TOM";
	}

	@Override
	public OnCommand push(FPFMessage message) {
				OnCommand retval = OnCommand.ON_PROCESS;
			    String rptseq = message.getFieldValue(RPTSEQ);
			    if (checkRepeatMessage(rptseq))
			    	return retval;
				String key =  message.getFieldValue(MDENTRYID);
			    String type = message.getFieldValue(MDENTRYTYPE);
			    String size = message.getFieldValue(MDENTRYSIZE);
			    String px = message.getFieldValue(MDENTRYPX);
			    String updAction =message.getFieldValue(MDUPDATEACTION); 
				switch (updAction){
				case IFPFOrderBook.ADD 		: 
					if (type.equals(IFPFOrderBook.BID))  
						addBid(key,size, px); 
				    else
						addAsk(key,size, px);  
				break;
				case IFPFOrderBook.CHANGE 	:   
					if (type.equals(IFPFOrderBook.BID))
						changeBid(key,size, px);
					else
						changeAsk(key,size, px);
			    break;
				case IFPFOrderBook.DELETE 	: 
					if (type.equals(IFPFOrderBook.BID)) 
					  deleteBid(key); 
					else
					  deleteAsk(key);
				break;
				default :break; 
		       }
				if (mylogger.isDebugEnabled())
					mylogger.info(getInstrumentID()+
								"<" + FPFMessage.getFieldName(MDENTRYID)+">"+ key +
								"<" + FPFMessage.getFieldName(MDENTRYTYPE)+">"+ type + 
								"<" + FPFMessage.getFieldName(MDENTRYSIZE)+">"+ size + 
								"<" + FPFMessage.getFieldName(MDENTRYPX)+">"+ px + 
								"<" + FPFMessage.getFieldName(MDUPDATEACTION) + ">" +updAction + 
								"<" + FPFMessage.getFieldName(RPTSEQ) + ">" +rptseq);
		return retval;
	}
}
