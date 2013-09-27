package com.fxsession.fastplus.receiver.moex;


import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;




/**
 * @author Dmitry Vulf
 * 
 * implements IDF(Instruments definition) feed
 *
 */



public class MoexFeedIDF extends MoexFeed{
	
	private static Logger mylogger = Logger.getLogger(MoexFeedIDF.class);
	
	public MoexFeedIDF(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public String getSiteID() {
		return "IDF-A";
	}

	@Override
	public String getTemplateID() {
		return "2005";
	}
	
	
}
