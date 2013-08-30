package com.fxsession.fastplus.handler.moex;

import java.util.concurrent.atomic.AtomicInteger;



import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFOrderBookL3;
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
public class MoexHandlerOLR extends FPFOrderBookL3 implements IFPFHandler, IFPField {
	
	AtomicInteger  rptSeq = new AtomicInteger(-1);
	
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
			    String timemcs = message.getFieldValue(ORIGINTIME);
			    String timestamp = message.getFieldValue(MDENTRYTIME);
			    Long  ltimestamp = Long.parseLong(timestamp);
				Long  ltimemcs = Long.parseLong(timemcs);
			    String updAction =message.getFieldValue(MDUPDATEACTION); 
				switch (updAction){
				case IFPFOrderBook.ADD 		: 
					if (type.equals(IFPFOrderBook.BID))  
						addBid(key,size, px,ltimestamp,ltimemcs); 
				    else
						addAsk(key,size, px,ltimestamp,ltimemcs);  
				break;
				case IFPFOrderBook.CHANGE 	:   
					if (type.equals(IFPFOrderBook.BID))
						changeBid(key,size, px, ltimestamp,ltimemcs);
					else
						changeAsk(key,size, px, ltimestamp,ltimemcs);
			    break;
				case IFPFOrderBook.DELETE 	: 
					if (type.equals(IFPFOrderBook.BID)) 
					  deleteBid(null,px); 
					else
					  deleteAsk(null,px);
				break;
				default :break; 
		       }
		return retval;
	}
}
