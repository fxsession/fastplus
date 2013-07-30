package com.fxsession.fastplus.receiver.moex;


import com.fxsession.fastplus.fpf.FPFeedDispatcher;

/**
 * @author Dmitry Vulf
 * 
 * Orders feed
 *
 */
public class MoexFeedOLR extends MoexFeed{

	public MoexFeedOLR(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}


	@Override
	public String getSiteID() {
		return "OLR-A";
	}


	@Override
	public String getTemplateID() {
		return "3310";
	}

}
