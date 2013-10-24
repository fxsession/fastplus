
package com.fxsession.fastplus.fpf;


import com.fxsession.utils.FXPException;



/**
 * @author Dmitry Vulf
 * 
 * Base interface for all handlers. A handler belongs to the  object of business layer applying specific logic 
 * for each particular instrument i.e. EUR_RUB__TOD. 
 * Each instrument is implemented by a separate class and uniquely identified by getInstrumentID().
 *
 */
public interface IFPFHandler {
	/*
	 * Return instrument ID. which is passed in constructor
	 */
	String getInstrumentID();
	/*
	 * method that catches a message from dispatcher    
	 */
	OnCommand push(FPFMessage message) throws FXPException;
	/*
	 * calculate VWAP
	 */
	Double getVWAPBid(Integer size);
	Double getVWAPAsk(Integer size);
}
