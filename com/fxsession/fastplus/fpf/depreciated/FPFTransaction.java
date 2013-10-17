package com.fxsession.fastplus.fpf.depreciated;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


import com.fxsession.fastplus.fpf.IFPFTransaction;
import com.fxsession.utils.FXPException;
import com.fxsession.utils.FXPUtils;


/**
 * @author Dmitry Vulf
 *
 * This class makes basic transaction operations.
 * add, change, delete
 * 
 * Storing up-to-date transaction information in 2 books. 
 * 
 */


public class FPFTransaction implements IFPFTransaction{

	private  Logger mylogger = Logger.getLogger(FPFTransaction.class);
	
	
	class TransactionRecord {
		 public Integer size = null;
		 public Double px = null;
		 public Long timestmp; //time in millis
		 public Long timesmcs; //time in micros
		 public String toString(){
			 return new String(size + " " +px + " " + FXPUtils.toTimestampMcs(timestmp,timesmcs));
		 }
	};

	final Map <String,TransactionRecord> bidBook = new HashMap<String,TransactionRecord> ();
	final Map <String,TransactionRecord> askBook = new HashMap<String,TransactionRecord> ();
	
	public void disptch (String updAction, String type, String key, String size, String px, String timestamp, String timemcs) throws FXPException{
		
	
	    Long  ltimestamp = Long.parseLong(timestamp);
		Long  ltimemcs = Long.parseLong(timemcs);
		
		switch (updAction){
		case T_ADD 		: 
			if (type.equals(T_BID))  
				addBid(key,size, px,ltimestamp,ltimemcs); 
		    else if (type.equals(T_ASK))
				addAsk(key,size, px,ltimestamp,ltimemcs);  
		break;
		case T_CHANGE 	:   
			if (type.equals(T_BID))
				changeBid(key,size, px, ltimestamp,ltimemcs);
			else if (type.equals(T_ASK))
				changeAsk(key,size, px, ltimestamp,ltimemcs);
	    break;
		case T_DELETE 	: 
			if (type.equals(T_BID)) 
			  deleteBid(null,px); 
			else if (type.equals(T_ASK))
			  deleteAsk(null,px);
		break;
		default :break;
		}
	}
	
	/*
	 * BID PART
	 * 
	 */
	
	
	public void addBid(String entryId, String size, String px, Long timestamp, Long timeMcs) throws FXPException {
		if (entryId ==null) 
			return;
		TransactionRecord obr = new TransactionRecord(); 
		obr.size = FXPUtils.string2Int(size);
		obr.px = FXPUtils.string2Double(px);
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		bidBook.put(entryId, obr);
		mylogger.info("bid " + entryId + " add "  + obr.toString());
	}

	
	public void changeBid(String entryId, String size,String px, Long timestamp, Long timeMcs) throws FXPException {
		if (entryId ==null) 
			return;
		Integer newSize = FXPUtils.string2Int(size);
		Double _px = FXPUtils.string2Double(px);
		TransactionRecord obr = new TransactionRecord(); 
		obr.px = _px;
		obr.size = newSize;
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		bidBook.put(entryId, obr);
		mylogger.info("bid " + entryId + " change "  + obr.toString());
	}

	
	public void deleteBid(String entryId,String px) {
		if (entryId ==null) 
			return;
		TransactionRecord obr = bidBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> could be not registered earlier
			 */
			mylogger.info("bid " + entryId + " delete " + obr.toString());
			bidBook.remove(entryId);			
		}
	}
	
	/*
	 * ASK PART
	 * 
	 */
	
	public void addAsk(String entryId, String size, String px,Long timestamp, Long timeMcs) throws FXPException {
		if (entryId ==null) 
			return;
		TransactionRecord obr = new TransactionRecord(); 
		obr.size = FXPUtils.string2Int(size);
		obr.px = FXPUtils.string2Double(px);
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		askBook.put(entryId, obr);
		mylogger.info("                         ask" + entryId + " add "  + obr.toString());
	}

	
	public void changeAsk(String entryId, String size,String px,Long timestamp, Long timeMcs) throws FXPException {
		if (entryId ==null) 
			return;
		Integer newSize = FXPUtils.string2Int(size);
		Double _px = FXPUtils.string2Double(px);
		TransactionRecord obr = new TransactionRecord(); 
		obr.px = _px;
		obr.size = newSize;
		obr.timestmp = timestamp;
		obr.timestmp = timeMcs;
		askBook.put(entryId, obr);
		mylogger.info("                         ask" + entryId + " change " + obr.toString());
	}

	
	public void deleteAsk(String entryId, String px) {
		if (entryId ==null) 
			return;
		TransactionRecord obr = askBook.get(entryId);
		if (obr!=null){
			/*
			 * for the same reasons as in change. The previous <add> can be not registered earlier
			 */
			mylogger.info("                         ask" + entryId + " delete " + obr.toString());
			askBook.remove(entryId);
		}
	}
	
	@Override
	public String toString(){
		return "nothing tp print";
	}

}
