/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import java.util.concurrent.atomic.AtomicInteger;



import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.IFPField;


/**
 * @author Dmitry Vulf
 * 
 * Base class for all handlers
 *
 */
public abstract class MoexHandler  implements IFPFHandler, IFPField {

	private final String instrumentID;
	private AtomicInteger  rptSeq = new AtomicInteger(-1);
	
	public MoexHandler(String instrument) {
		instrumentID = instrument;
	}

	@Override
	public String getInstrumentID() {
		// TODO Auto-generated method stub
		return instrumentID;
	}

	
	
	protected boolean checkRepeatMessage(String sRpt) {
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
	public Double getVWAPBid(Integer size) {
		// TODO Auto-generated method stub
		return 0d;
	}

	@Override
	public Double getVWAPAsk(Integer size) {
		// TODO Auto-generated method stub
		return 0d;
	}
	

}
