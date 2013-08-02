package com.fxsession.fastplus.handler.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;


import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.OnCommand;


public class MoexHandlerIDF implements IFPFHandler{
	
	private static Logger mylogger = Logger.getLogger(MoexHandlerIDF.class);
	static private final String MARKETSEGMENTGROUP = "MarketSegmentGrp";
	static private final String SECURITYDESC = "SecurityDesc";
	static private final String ENCODEDDESC = "EncodedSecurityDesc";
	static private final String ROUNDLOT = "RoundLot";
	static private final String TRADINGSESSIONGRP = "TradingSessionRulesGrp";
	static private final String TRADINGSESSIONID ="TradingSessionID";


	@Override
	public String getInstrumentID() {
		return "EURUSD000TOM";
	}

	@Override
	public OnCommand push(Message message) {
		OnCommand retval = OnCommand.ON_PROCESS;
		SequenceValue secval =message.getSequence (MARKETSEGMENTGROUP);
		if (secval.getValues().length>0){
		    SequenceValue secvalin =  secval.getValues()[0].getSequence(TRADINGSESSIONGRP);
			if(secvalin.getValues().length>0)
			mylogger.info(getInstrumentID() + " :  " + 
							message.getString(SECURITYDESC)+ " " +
							message.getString(ENCODEDDESC) + " " +
							secval.getValues()[0].getString(ROUNDLOT) + " " +
							secvalin.getValues()[0].getString(TRADINGSESSIONID));
			retval = OnCommand.ON_STOP_FEED; //stop the feed after getting the first message;
		}
		return retval;
	}
}
