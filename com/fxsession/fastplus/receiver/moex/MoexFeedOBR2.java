package com.fxsession.fastplus.receiver.moex;

import com.fxsession.fastplus.fpf.FPFeedDispatcher;

/**
 * @author Dmitry Vulf
 * 
 * Read OBR from second site
 *
 */
public class MoexFeedOBR2 extends MoexFeedOBR{
	
	public MoexFeedOBR2(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public String getSiteID() {
		return "OBR-B";	}
}
