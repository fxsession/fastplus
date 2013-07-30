package com.fxsession.fastplus.receiver.moex;

import com.fxsession.fastplus.fpf.FPFeedDispatcher;




/**
 * @author Dmitry Vulf
 * 
 * Implements OBR(Order book incremental) feed
 * 
 * Read OBR from primary site
 *
 */
public class MoexFeedOBR extends MoexFeed{


	public MoexFeedOBR(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}


	@Override
	public String getSiteID() {
		return "OBR-A";	}


	@Override
	public String getTemplateID() {
		return "3312";
	}
}
