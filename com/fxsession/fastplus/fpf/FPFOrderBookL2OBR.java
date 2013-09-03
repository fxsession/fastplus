package com.fxsession.fastplus.fpf;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;


/**
 * @author Dmitry Vulf
 * 
 * This class is used now to construct L2 book(20x20) instead of FPFOrderBookL2 which use OLR (full deal log) 
 *  FPFOrderBookL2 doesn't work correctly because data is incremental and for the proper counting i need to use 
 *  snapshots (OLS) that I don't really want (too heavy logics) 
 *
 */
public class FPFOrderBookL2OBR implements IFPFOrderBook{

	private  Logger askloggerL2 = Logger.getLogger("L2ObrAskLogger");
	private  Logger bidloggerL2 = Logger.getLogger("L2ObrBidLogger");
	
	/*
	 *  private maps containing sorted by price orders books
	 *  for bid and ask
	 *  key - price, value - size
	 */
   	
	 /*  
	 *  bid book keep key in the reverse order. 
	 *  highest rate(1st entry) - best deal 
	 *  
	 *  ask book - keeps direct order. 
	 *  lowest rate(1st entry) - best deal. 
	 *  in both books  - the 1st key is the best deal. 
	 */
	private final TreeMap <Double,Integer> bidBook = new TreeMap<Double,Integer> (Collections.reverseOrder());
	
	private final TreeMap <Double,Integer> askBook = new TreeMap<Double,Integer>(Collections.reverseOrder());


	/*
	 * BID PART
	 * 
	 */
	
	
	@Override
	public void addBid(String entryId, String size, String px, Long timestamp, Long timeMcs) {
		Integer isize = FPUtility.string2Size(size);
		Double dpx = FPUtility.string2Px(px);
	
		bidBook.put(dpx,isize);  
		bidloggerL2.info(IFPFOrderBook.ADD + " " + dpx + " " + isize);
	}

	@Override
	public void changeBid(String entryId, String size,String px, Long timestamp, Long timeMcs) {
		addBid(entryId, size, px, timestamp, timeMcs);
	}

	@Override
	public void deleteBid(String entryId,String px) {
		if (px ==null) 
			return;
		bidloggerL2.info(IFPFOrderBook.DELETE + " " + px.toString());
		bidBook.remove(entryId);			
	}
	
	
	private String scanBid() {
		/*
		 * a template how to visualize bid orderbook
		 * best bid(maximum) - on the top 
		 */
		
		String retval = "";
   	    for (Map.Entry<Double,Integer> entry : bidBook.entrySet()) {
	            retval += entry.getKey() + " " +entry.getValue() + "\r\n";
	        }		
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
	@Override
	public void addAsk(String entryId, String size, String px,Long timestamp, Long timeMcs) {
		Integer isize = FPUtility.string2Size(size);
		Double dpx = FPUtility.string2Px(px);
	
		askBook.put(dpx,isize);  
		askloggerL2.info(IFPFOrderBook.ADD + " " + dpx + " " + isize);
	}

	@Override
	public void changeAsk(String entryId, String size,String px,Long timestamp, Long timeMcs) {
		addAsk(entryId, size, px, timestamp, timeMcs);
	}

	@Override
	public void deleteAsk(String entryId,String px) {
		if (px ==null) 
			return;
		askloggerL2.info(IFPFOrderBook.DELETE + " " + px.toString());
		askBook.remove(entryId);			
	}

	
	public String scanAsk() {
		/*
		 * a template how to visualize bid orderbook
		 * best ask(minimum) - on the bottom
		 */
		String retval = ""; 
   	    for (Map.Entry<Double,Integer> entry : askBook.entrySet()) {
   	    	retval += entry.getKey() + " " +entry.getValue() + "\r\n";
        }		
   	    return retval;
	}
	
	/*
	 * Returns price weighted by minimal size for given amount   
	 */
	
	public Double getAskWeightedBySize(Integer size, Integer topLevels2skip){
		Double weighted = 0d;
		Integer intrsize = size;
		Integer level = 0;
		for (Map.Entry<Double,Integer> entry : askBook.entrySet()) {
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
	
	@Override
	public String toString(){
		String bidbook = "\r\nBID \r\n" + scanBid();
		String askbook = "\r\nASK \r\n" +  scanAsk();
		return bidbook + askbook;
	}
	
}
