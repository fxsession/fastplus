package com.fxsession.fastplus.fpf;

import org.apache.log4j.Logger;


/**
 * @author Dmitry Vulf
 * 
 * extends L3 interface behavior 
 */


public abstract class FPFOrderBookL3 implements IFPFOrderBook{

	private  Logger askloggerL3 = Logger.getLogger("L3askLooger");
	private  Logger bidloggerL3 = Logger.getLogger("L3bidLooger");
		
	public FPFOrderBookL3() {

	}

	/*
	 * BID PART
	 * 
	 */
	
	@Override
	public void addBid(String entryId, String size, String px) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.size = OrderBookRecord.string2Size(size);
		obr.px = OrderBookRecord.string2Px(px);
		obr.logtime =System.nanoTime();
		bidBook.put(entryId, obr);
		bidloggerL3.info(entryId + " " + IFPFOrderBook.ADD + " " + obr.toString());
		addBidL2(obr.px,obr.size);		
	}

	@Override
	public void changeBid(String entryId, String size,String px) {
		if (entryId ==null) 
			return;
		//has to remember previous value to build L2 book
		Integer newSize = OrderBookRecord.string2Size(size);
		Double _px = OrderBookRecord.string2Px(px);
		OrderBookRecord obr = bidBook.get(entryId);
		OrderBookRecord obrnew = new OrderBookRecord(); 
		obrnew.px = _px;
		obrnew.size = newSize;
		obrnew.logtime =System.nanoTime();
		bidBook.put(entryId, obrnew);
		bidloggerL3.info(entryId + " " + IFPFOrderBook.CHANGE + " " + obrnew.toString());
		if (obr!=null)
		{/*however previous value can be absent, 
		  *due to the late connection - <change> may come for the <add> which hasn't registered 
		  */ 
		  Integer prevsize =obr.size;
		  changeBidL2(obr.px,newSize,prevsize);
		}
		else
			/*
			 * so I simply add this as a new level
			 */
		  changeBidL2(_px,newSize,0);	
	}

	@Override
	public void deleteBid(String entryId) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = bidBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> could be not registered earlier
			 */
			bidloggerL3.info(entryId + " " + IFPFOrderBook.DELETE + " " + obr.toString());
			deleteBidL2(obr.px,obr.size);
			bidBook.remove(entryId);			
		}
	}
	protected  abstract void addBidL2(Double px, Integer size);
	protected  abstract void changeBidL2(Double px, Integer size, Integer prevsize);
	protected  abstract void deleteBidL2(Double px, Integer size);

	/*
	 * ASK PART
	 * 
	 */
	@Override
	public void addAsk(String entryId, String size, String px) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.size = OrderBookRecord.string2Size(size);
		obr.px = OrderBookRecord.string2Px(px);
		obr.logtime =System.nanoTime();
		askBook.put(entryId, obr);
		askloggerL3.info(entryId + " " + IFPFOrderBook.ADD + " " + obr.toString());
		addAskL2(obr.px,obr.size);		
	}

	@Override
	public void changeAsk(String entryId, String size,String px) {
		if (entryId ==null) 
			return;
		//has to remember previous value to build L2 book
		Integer newSize = OrderBookRecord.string2Size(size);
		Double _px = OrderBookRecord.string2Px(px);
		OrderBookRecord obr = askBook.get(entryId);
		OrderBookRecord obrnew = new OrderBookRecord(); 
		obrnew.px = _px;
		obrnew.size = newSize;
		obrnew.logtime =System.nanoTime();
		askBook.put(entryId, obrnew);
		askloggerL3.info(entryId + " " + IFPFOrderBook.CHANGE + " " + obrnew.toString());
		if (obr!=null)
		{/*however previous value can be absent, 
		  *due to the late connection - <change> may come for the <add> which hasn't been registered 
		  */
			Integer prevsize =obr.size;
	    	changeAskL2(obr.px,newSize,prevsize);
		}
		else
			/*
			 * so I simply add this as a new level
			 */
			changeAskL2(_px,newSize,0);
	}

	@Override
	public void deleteAsk(String entryId) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = askBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> can be not registered earlier
			 */
			askloggerL3.info(entryId + " " + IFPFOrderBook.DELETE + " " + obr.toString());
			deleteAskL2(obr.px,obr.size);
			askBook.remove(entryId);
		}
	}
	
	/*
	 * This methods are overridden on the L2 level
	 */
	protected  abstract void addAskL2(Double px, Integer size);
	protected  abstract void changeAskL2(Double px, Integer size, Integer prevsize);
	protected  abstract void deleteAskL2(Double px, Integer size);
}
