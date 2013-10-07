package com.fxsession.fastplus.receiver.moex;


import com.fxsession.fastplus.fpf.FPFeedDispatcher;




/**
 * @author Dmitry Vulf
 * 
 * implements IDF(Instruments definition) feed
 *
 */



public class MoexFeedIDF extends MoexFeed{
	
	
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
