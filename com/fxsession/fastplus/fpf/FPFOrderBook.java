package com.fxsession.fastplus.fpf;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.fxsession.utils.FXPException;
import com.fxsession.utils.FXPUtils;

import com.fxsession.fastplus.fpf.OnCommand;
/**
 * @author Dmitry Vulf
 *  
 *  Contains methods and structures to implement classical orderbook
 *  Can be applied for different objects
 */
public class FPFOrderBook implements IFPFTransaction{
	private static Logger mylogger = Logger.getLogger(FPFOrderBook.class);	
	
	
	private final String instrumentId;

	
	/*
	 *  private maps containing sorted by price orders books
	 *  for bid and ask
	 *  key - price, value - size
	 *
	 */
	private final TreeMap <Double,Integer> bidBook = new TreeMap<Double,Integer> (Collections.reverseOrder());
	
	private final TreeMap <Double,Integer> askBook = new TreeMap<Double,Integer>(Collections.reverseOrder());
	

	

	public FPFOrderBook(String _instrument){
		instrumentId = _instrument;
	}
	
	public OnCommand dispatch(String action, String type, String size, String price) throws FXPException{
		OnCommand retval = OnCommand.ON_NULL;
		if (type.equals(T_BID)){
			switch (action){
		    	case T_ADD 		: 
				   addBid(size, price); break;
		        case T_CHANGE 	:   
				   changeBid(size, price); break;
		        case T_DELETE 	: 
				   deleteBid(size,price);  break;
		        default : throw new FXPException("unknown operation");}
		        retval = OnCommand.ON_CHANGED_BID;
 
		}else if (type.equals(T_ASK)){
		    switch (action){
		    	case T_ADD 		: 
				   addAsk(size, price); break;
		        case T_CHANGE 	:   
				   changeAsk(size, price); break;
		        case T_DELETE 	: 
				   deleteAsk(size,price);  break;
		        default : throw new FXPException("unknown operation"); }
		        retval = OnCommand.ON_CHANGED_ASK;
		}
		return retval;
	}
	
	/*	
	 *  bid book keeps key in the reverse order. 
	 *  highest rate(1st entry) - best deal 
	 *  
	 *  ask book - keeps direct order. 
	 *  lowest rate(1st entry) - best deal.
	 *   
	 *  in both books  - the 1st key is the best deal. 
	 */


	/*
	 * BID PART
	 * 
	 */
	
	public void addBid(String size, String px) throws FXPException {
		Integer isize = FXPUtils.string2Int(size);
		Double dpx = FXPUtils.string2Double(px);
		bidBook.put(dpx,isize);  
		if (mylogger.isDebugEnabled())
			mylogger.debug(instrumentId + " bid   a  " + FXPUtils.Double2String(dpx,5) + " " + isize);
	}

	
	public void changeBid(String size,String px) throws FXPException {
		addBid(size, px);
	}

	
	public void deleteBid(String size,String px) throws  FXPException {
		if (px == null)
			return;
		Integer isize = FXPUtils.string2Int(size);
		Double dpx = FXPUtils.string2Double(px);
		bidBook.remove(dpx);
		if (mylogger.isDebugEnabled())
			mylogger.debug(instrumentId + " bid   d  " + FXPUtils.Double2String(dpx,5) + " " + isize);		
	}
	
	
	private String scanBid() {
		/*
		 * a template how to visualize bid orderbook
		 * best bid(maximum) - on the top 
		 */
		
		String retval = "";
   	    for (Map.Entry<Double,Integer> entry : bidBook.entrySet()) {
	            retval += FXPUtils.Double2String(entry.getKey(),5) + " " +entry.getValue() + "\r\n";
	        }		
   	    
   	    return retval;
	}
	
	/*
	 * Returns price weighted by minimal size for given amount
	 * 
	 * topLevels2skip - skip N levels in calculations. Them most liquid levels usually are closed "immediately" 
	 * so there is no much sense to count them      
	 */
	
	public Double getBidVWAP(Integer size, Integer topLevels2skip){
		Double weighted = 0d;
		Integer intrsize = size;
		Integer level = 0;
		for (Map.Entry<Double,Integer> entry : bidBook.entrySet()) {
			Integer newsize = entry.getValue();
			if (level>=topLevels2skip){
				if (newsize < intrsize){  //take full size of the level
					weighted += entry.getKey()*newsize;
					intrsize -= newsize; 
				}
				else   //take only part of the size of the level
				{
					weighted +=entry.getKey()*intrsize;
					intrsize = 0;
					break;
				}
			}
			level++;
        }
		Integer remainder = size-intrsize;
		return ((remainder ==0) ? 0 : weighted/remainder);
	}
	/*
	 * Calculate the full book
	 */
	public Double getBidVWAP(Integer size){
		return getBidVWAP(size,0);
	}


	/*
	 * ASK PART
	 * 
	 */
	
	
	public void addAsk(String size, String px) throws FXPException {
		Integer isize = FXPUtils.string2Int(size);
		Double dpx = FXPUtils.string2Double(px);
		askBook.put(dpx,isize);
		if (mylogger.isDebugEnabled())
			mylogger.debug(instrumentId + " ask   a                      "+ FXPUtils.Double2String(dpx,5) + " " + isize);
	}

	
	public void changeAsk(String size,String px) throws FXPException {
		addAsk(size, px);
	}

	
	public void deleteAsk(String size,String px) throws FXPException {
		if (px ==null) 
			return;
		Integer isize = FXPUtils.string2Int(size);
		Double dpx = FXPUtils.string2Double(px);
		askBook.remove(dpx);
		if (mylogger.isDebugEnabled())
			mylogger.debug(instrumentId + " ask   d                      " + FXPUtils.Double2String(dpx,5) + " " + isize);		
	}

	
	public String scanAsk() {
		/*
		 * a template how to visualize bid orderbook
		 * best ask(minimum) - on the bottom
		 */
		String retval = ""; 
   	    for (Map.Entry<Double,Integer> entry : askBook.entrySet()) {
   	    	retval +="                       " + FXPUtils.Double2String(entry.getKey(),5) + " " +entry.getValue() + "\r\n";
        }		
   	    return retval;
	}
	
	/*
	 * Returns price weighted by minimal size for given amount   
	 */
	
	public Double getAskVWAP(Integer size, Integer topLevels2skip){
		Double weighted = 0d;
		Integer intrsize = size;
		Integer level = 0;
		for (Map.Entry<Double,Integer> entry : askBook.descendingMap().entrySet()) {
			Integer newsize = entry.getValue();
			if (level>=topLevels2skip){
				if (newsize < intrsize){  //take full size of the level
					weighted += entry.getKey()*newsize;
					intrsize -= newsize; 
				}
				else   //take only part of the size of the level
				{
					weighted +=entry.getKey()*intrsize;
					intrsize = 0;
					break;
				}
			}
			level++;
        }
		Integer remainder = size-intrsize;
		return ((remainder ==0) ? 0 : weighted/remainder);
	}
	
	public Double getAskWVAP(Integer size){
		return getAskVWAP(size,0);
	}
	
	
	public String toString(){
	    String header = "\r\n" + instrumentId;
		String bidbook = "\r\nBID \r\n" + scanBid();
		String askbook = "\r\n                       ASK \r\n" +  scanAsk();
		return header + askbook + bidbook;
	}

}
