package com.fxsession.fastplus.fpf;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * @author Dmitry Vulf
 * 
 * extends L3 interface behavior 
 * Works in a shadow mode most of the time.
 * Can be useful to identify "own" deal to exclude it from 
 * the orderbook.
 */


public abstract class FPFOrderBookL3 implements IFPFOrderBook{

	private  Logger askloggerL3 = Logger.getLogger("L3askLooger");
	private  Logger bidloggerL3 = Logger.getLogger("L3bidLooger");
		
	class OrderBookRecord {
		 public Integer size = null;
		 public Double px = null;
		 public Long timestmp; //time in millis
		 public Long timesmcs; //time in micros
		 public String toString(){
			 return new String(size + " " +px + " " + FPUtility.toTimestampMcs(timestmp,timesmcs));
		 }
	};

	final Map <String,OrderBookRecord> bidBook = new HashMap<String,OrderBookRecord> ();
	final Map <String,OrderBookRecord> askBook = new HashMap<String,OrderBookRecord> ();
	

	/*
	 * BID PART
	 * 
	 */
	
	@Override
	public void addBid(String entryId, String size, String px, Long timestamp, Long timeMcs) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.size = FPUtility.string2Size(size);
		obr.px = FPUtility.string2Px(px);
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		bidBook.put(entryId, obr);
		bidloggerL3.info(entryId + " " + IFPFOrderBook.ADD + " " + obr.toString());
	}

	@Override
	public void changeBid(String entryId, String size,String px, Long timestamp, Long timeMcs) {
		if (entryId ==null) 
			return;
		Integer newSize = FPUtility.string2Size(size);
		Double _px = FPUtility.string2Px(px);
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.px = _px;
		obr.size = newSize;
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		bidBook.put(entryId, obr);
		bidloggerL3.info(entryId + " " + IFPFOrderBook.CHANGE + " " + obr.toString());
	}

	@Override
	public void deleteBid(String entryId,String px) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = bidBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> could be not registered earlier
			 */
			bidloggerL3.info(entryId + " " + IFPFOrderBook.DELETE + " " + obr.toString());
			bidBook.remove(entryId);			
		}
	}
	
	/*
	 * ASK PART
	 * 
	 */
	@Override
	public void addAsk(String entryId, String size, String px,Long timestamp, Long timeMcs) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.size = FPUtility.string2Size(size);
		obr.px = FPUtility.string2Px(px);
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		askBook.put(entryId, obr);
		askloggerL3.info(entryId + " " + IFPFOrderBook.ADD + " " + obr.toString());
	}

	@Override
	public void changeAsk(String entryId, String size,String px,Long timestamp, Long timeMcs) {
		if (entryId ==null) 
			return;
		Integer newSize = FPUtility.string2Size(size);
		Double _px = FPUtility.string2Px(px);
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.px = _px;
		obr.size = newSize;
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		askBook.put(entryId, obr);
		askloggerL3.info(entryId + " " + IFPFOrderBook.CHANGE + " " + obr.toString());
	}

	@Override
	public void deleteAsk(String entryId, String px) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = askBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> can be not registered earlier
			 */
			askloggerL3.info(entryId + " " + IFPFOrderBook.DELETE + " " + obr.toString());
			askBook.remove(entryId);
		}
	}

}
