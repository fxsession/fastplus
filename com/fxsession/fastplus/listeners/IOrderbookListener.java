package com.fxsession.fastplus.listeners;

/**
 * @author Dmitry Vulf
 * 
 * Receive orderbook events
 *
 */
public interface IOrderbookListener extends IListener {
	/*
	 * Receives orederbook change event
	 */
	public void OnChangeBid();
	
	public void OnChangeAsk();
	
	/*
	 * Receives feedback for weighted price
	 */
	public void OnWeightedPriceChange();
}
