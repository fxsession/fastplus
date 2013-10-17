package com.fxsession.fastplus.fpf;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.fxsession.utils.FXPException;
import com.fxsession.utils.FXPUtils;

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
	
	public void dispatch(String action, String type, String size, String price) throws FXPException{
		
		switch (action){
		case T_ADD 		: 
			if (type.equals(T_BID))  
				addBid(size, price); 
			else if (type.equals(T_ASK))
				addAsk(size, price);  
		break;
		case T_CHANGE 	:   
			if (type.equals(T_BID))
				changeBid(size, price);
			else if (type.equals(T_ASK))
				changeAsk(size, price);
		break;
		case T_DELETE 	: 
			if (type.equals(T_BID)) 
				deleteBid(size,price); 
			if (type.equals(T_ASK))
				deleteAsk(size,price);
		break;
		default : throw new FXPException("unknown operation"); 
		}
	
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
		mylogger.info(instrumentId + " bid   a  " + FXPUtils.Double2String(dpx,5) + " " + isize);
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
		mylogger.info(instrumentId + " bid   d  " + FXPUtils.Double2String(dpx,5) + " " + isize);		
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
   	    //retval +=Double.toString(getBidWeightedBySize(10000000)) + "\r\n";
   	    return retval;
	}
	
	/*
	 * Returns price weighted by minimal size for given amount
	 * 
	 * topLevels2skip - skip N levels in calculations. Them most liquid levels usually are closed "immediately" 
	 * so there is no much sense to count them      
	 */
	
	public Double getBidWeightedBySize(Integer size, Integer topLevels2skip){
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

	public Double getBidWeightedBySize(Integer size){
		return getBidWeightedBySize(size,0);
	}


	/*
	 * ASK PART
	 * 
	 */
	
	public void addAsk(String size, String px) throws FXPException {
		Integer isize = FXPUtils.string2Int(size);
		Double dpx = FXPUtils.string2Double(px);
		askBook.put(dpx,isize);  
		mylogger.info(instrumentId + " ask   a                      "+ FXPUtils.Double2String(dpx,5) + " " + isize);
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
		mylogger.info(instrumentId + " ask   d                      " + FXPUtils.Double2String(dpx,5) + " " + isize);		
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
   	    //retval +="                       "+Double.toString(getAskWeightedBySize(10000)) + "\r\n";
   	    return retval;
	}
	
	/*
	 * Returns price weighted by minimal size for given amount   
	 */
	
	public Double getAskWeightedBySize(Integer size, Integer topLevels2skip){
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
	
	public Double getAskWeightedBySize(Integer size){
		return getAskWeightedBySize(size,0);
	}
	
	
	public String toString(){
	    String header = "\r\n" + instrumentId;
		String bidbook = "\r\nBID \r\n" + scanBid();
		String askbook = "\r\n                       ASK \r\n" +  scanAsk();
		return header + askbook + bidbook;
	}

}
