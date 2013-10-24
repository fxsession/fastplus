package com.fxsession.fastplus.listeners;

import com.fxsession.fastplus.fpf.IFPFHandler;


/**
 * @author Dmitry Vulf
 * 
 * Receive orderbook events
 *
 */
public interface IOrderbookListener extends IListener {
	
	/*
	 * Sends event that the orderbook has changed
	 */
	public void OnChangeBid(IFPFHandler handle);
	public void OnChangeAsk(IFPFHandler handle);
	/*
	 * Receives feedback for weighted price
	 */
	public void OnVWAP();
}
