package com.fxsession.fastplus.receiver.moex.depreciated;

import com.fxsession.fastplus.fpf.FPFeedDispatcher;

/**
 * @author Dmitry Vulf
 * 
 * Orders feed - backup channel of OLR
 * FeedDispatcher should arbitrate between this 2 channel 
 *
 */
public class MoexFeedOLR2 extends MoexFeedOLR{

	/**
	 * 
	 */
	public MoexFeedOLR2(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public String getSiteID() {
		return "OLR-B";
	}
}
