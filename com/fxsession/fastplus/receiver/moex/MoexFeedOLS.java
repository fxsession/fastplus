/**
 * 
 */
package com.fxsession.fastplus.receiver.moex;

import com.fxsession.fastplus.fpf.FPFeedDispatcher;

/**
 * @author Администратор
 *
 */
public class MoexFeedOLS extends MoexFeed{

	/**
	 * 
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
