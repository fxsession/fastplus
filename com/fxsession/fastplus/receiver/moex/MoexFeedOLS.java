package com.fxsession.fastplus.receiver.moex;



import com.fxsession.fastplus.fpf.FPFeedDispatcher;

/**
 * @author Dmitry Vulf
 * 
 * Orders snapshoot
 *
 */
public class MoexFeedOLS extends MoexFeed {

	/**
	 * @param dispatcher
	 */
	public MoexFeedOLS(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public String getTemplateID() {
		return "3300";
	}

	@Override
	public String getSiteID() {
		return "OLS-A";
	}

}
