/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import org.apache.log4j.Logger;

import com.fxsession.fastplus.fpf.FPFCommand;
import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFOrderBook;
import com.fxsession.utils.FXPException;

/**
 * @author Dmitry Vulf
 * 
 *         OBR
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
	 */
	public FPFCommand push(FPFMessage message) throws FXPException {

		FPFCommand retval = FPFCommand.ON_PROCESS;

		try {
			String rptseq = message.getFieldValue(RPTSEQ);

			if (checkRepeatMessage(rptseq))
				return retval;

			String type = message.getFieldValue(MDENTRYTYPE);
			String size = message.getFieldValue(MDENTRYSIZE);
			String px = message.getFieldValue(MDENTRYPX);
			String updAction = message.getFieldValue(MDUPDATEACTION);

			retval = orderbook.dispatch(updAction, type, size, px);

		} catch (Exception e) {
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
	}

	public Double getVWAPAsk(Integer size) {
		return null;
	}

}