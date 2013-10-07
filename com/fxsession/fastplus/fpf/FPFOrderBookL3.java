package com.fxsession.fastplus.fpf;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import org.openfast.session.FastConnectionException;


/**
 * @author Dmitry Vulf
 * 
 * extends L3 interface behavior 
 * Works in a shadow mode most of the time.
 * Can be useful to identify "own" deal to exclude it from 
 * the orderbook.
 */


public abstract class FPFOrderBookL3 implements IFPFOrderBook{

	private  Logger loggerL3 = Logger.getLogger("L3");
	
	protected final String instrumentID;	
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
	
	public FPFOrderBookL3(String instrument) {
   		instrumentID = instrument;    
	}
	
	/*
	 * BID PART
	 * 
	 */
	
	
	public void addBid(String entryId, String size, String px, Long timestamp, Long timeMcs) throws FastConnectionException {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.size = FPUtility.string2Int(size);
		obr.px = FPUtility.string2Double(px);
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		bidBook.put(entryId, obr);
		loggerL3.info("bid " + entryId + " add "  + obr.toString());
	}

	
	public void changeBid(String entryId, String size,String px, Long timestamp, Long timeMcs) throws FastConnectionException {
		if (entryId ==null) 
			return;
		Integer newSize = FPUtility.string2Int(size);
		Double _px = FPUtility.string2Double(px);
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.px = _px;
		obr.size = newSize;
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		bidBook.put(entryId, obr);
		loggerL3.info("bid " + entryId + " change "  + obr.toString());
	}

	
	public void deleteBid(String entryId,String px) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = bidBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> could be not registered earlier
			 */
			loggerL3.info("bid " + entryId + " delete " + obr.toString());
			bidBook.remove(entryId);			
		}
	}
	
	/*
	 * ASK PART
	 * 
	 */
	
	public void addAsk(String entryId, String size, String px,Long timestamp, Long timeMcs) throws FastConnectionException {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.size = FPUtility.string2Int(size);
		obr.px = FPUtility.string2Double(px);
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		askBook.put(entryId, obr);
		loggerL3.info("                         ask" + entryId + " add "  + obr.toString());
	}

	
	public void changeAsk(String entryId, String size,String px,Long timestamp, Long timeMcs) throws FastConnectionException {
		if (entryId ==null) 
			return;
		Integer newSize = FPUtility.string2Int(size);
		Double _px = FPUtility.string2Double(px);
		OrderBookRecord obr = new OrderBookRecord(); 
		obr.px = _px;
		obr.size = newSize;
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		askBook.put(entryId, obr);
		loggerL3.info("                         ask" + entryId + " change " + obr.toString());
	}

	
	public void deleteAsk(String entryId, String px) {
		if (entryId ==null) 
			return;
		OrderBookRecord obr = askBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> can be not registered earlier
			 */
			loggerL3.info("                         ask" + entryId + " delete " + obr.toString());
			askBook.remove(entryId);
		}
	}
	
	@Override
	public String toString(){
		return "nothing tp print";
	}

}
