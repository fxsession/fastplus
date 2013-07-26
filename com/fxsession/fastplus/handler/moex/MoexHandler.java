package com.fxsession.fastplus.handler.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.IFPFHandler;

/**
 * @author Dmitry Vulf
 *
 */
public class MoexHandler implements IFPFHandler{
	
	private static Logger mylogger = Logger.getLogger(MoexHandler.class);

	static private final String GROUPMDENTRIES = "GroupMDEntries";
	static private final String MDENTRYPX = "MDEntryPx";
	static private final String MDENTRYSIZE = "MDEntrySize";
	@Override
	public String getSiteID() {
		return "EUR_RUB__TOD";
	}

	@Override
	public void push(FPFMessage message) {
		if (mylogger.isDebugEnabled()) {
			
			Message rawmessage = message.getMessage();
			
			SequenceValue secval =rawmessage.getSequence (GROUPMDENTRIES);
			if (secval.getValues().length>0){
				mylogger.debug(message.getKey() + ":"+secval.getValues()[0].getString(MDENTRYPX) + "[" +  secval.getValues()[0].getString(MDENTRYSIZE)+"]");
			}
		}

	}

}
