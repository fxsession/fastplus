package com.fxsession.fastplus.fpf;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


import org.apache.log4j.Logger;

/**
 * @author Dmitry Vulf
 * 
 * L2. Extends L3 behavior. Child classes can inherit either from L2 or L3.  
 *
 */
public class FPFOrderBookL2 extends FPFOrderBookL3{
	private final int L2_MAX_SIZE = 5;    //order book max size
   	private static Logger askloggerL2 = Logger.getLogger("L2askLooger");
   	private static Logger bidloggerL2 = Logger.getLogger("L2bidLooger");
   	
   	
   	

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
	private final TreeMap <Double,Integer> bidBookL2 = new TreeMap<Double,Integer> (Collections.reverseOrder());
	
	private final TreeMap <Double,Integer> askBookL2 = new TreeMap<Double,Integer>();

	public FPFOrderBookL2() {
	}
	
	/*
	 * BID
	 */
	private void addBidL2new (Double px, Integer size) {
		if (bidBookL2.size()<L2_MAX_SIZE)
			addBidL2simple(px,size);
		else // have reached max depth
		{
			Double lastKey = bidBookL2.lastKey(); 
			if ( lastKey < px){
			    /*for bid the element with lowest value is less significant  
				 *remove last key - add new level
				 */
				bidBookL2.remove(lastKey);
				addBidL2simple(px,size);
			}
		}
	}
	
	private void addBidL2simple (Double px, Integer size) {
		bidBookL2.put(px,size);
    	bidloggerL2.info(px + " " + size);		
		 if (size<0){
			System.exit(-1);
		 }
			 
	}
	
	
	protected void addBidL2(Double px, Integer size) {
		if (bidBookL2.containsKey(px))
			//map already  contains level for this price
			addBidL2simple(px,bidBookL2.get(px) + size);
		else 
		    addBidL2new(px,size);
	}
	
	protected void changeBidL2(Double px, Integer size, Integer prevsize){
		/*change the size for the found level
		*1. delete size formed by the deal
		*2. add new size  
		*if the key doesn't exist simply store update as a new level
		*/
		if (bidBookL2.containsKey(px)) 
			addBidL2simple(px,(bidBookL2.get(px) - prevsize) +size);
		else 
			addBidL2new(px,size); 
	}
	
	protected void deleteBidL2(Double px, Integer size){
		Integer newsize = bidBookL2.get(px);
		if (newsize!=null) {
			newsize-= size;
			if (newsize > 0)
				addBidL2simple(px, newsize);
			else
				bidBookL2.remove(px);
		}
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
		for (Map.Entry<Double,Integer> entry : bidBookL2.entrySet()) {
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
	 * ASK
	 */

	private void addAskL2new(Double px, Integer size){
		if (askBookL2.size()<L2_MAX_SIZE)
			addAskL2simple(px,size);
		else { // have reached max depth
			Double lastKey = askBookL2.lastKey(); 
			if ( lastKey > px){
				/*for ask the element with highest value is less significant
				 *remove last key - add new level
				 */
				askBookL2.remove(lastKey);
				addAskL2simple(px,size);
			}
		}
	}

	private void addAskL2simple (Double px, Integer size) {
		 askBookL2.put(px,size);
    	 askloggerL2.info(px + " " + size);		 
    	 if (size<0){
			System.exit(-1);
		 }
			 
	}

		
	protected void addAskL2(Double px, Integer size) {
		if (askBookL2.containsKey(px)) 
			//map already  contains level for this price
			addAskL2simple(px,askBookL2.get(px) + size);
		else 
		  	addAskL2new(px,size);
	}
	
	protected void changeAskL2(Double px, Integer size, Integer prevsize){
		/*change the size for the found level
		*1. delete size formed by the deal
		*2. add new size
		*if the key doesn't exist simply store update as a new level 
		*/
	    if (askBookL2.containsKey(px))
	    	addAskL2simple(px,(askBookL2.get(px) - prevsize) +size);
	    else
		  	addAskL2new(px,size);
	}
	
	protected void deleteAskL2(Double px, Integer size){
		Integer newsize = askBookL2.get(px);
		if (newsize!=null) {
			newsize-=size;
			if (newsize > 0)
				addAskL2simple(px, newsize);
			else
				askBookL2.remove(px);
		}
	}

	/*
	 * Returns price weighted by minimal size for given amount   
	 */
	
	public Double getAskWeightedBySize(Integer size, Integer topLevels2skip){
		Double weighted = 0d;
		Integer intrsize = size;
		Integer level = 0;
		for (Map.Entry<Double,Integer> entry : askBookL2.entrySet()) {
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
	
	public void scanBid(){
		//scans current status of the bid
   	    for (Map.Entry<Double,Integer> entry : bidBookL2.entrySet()) {
	            bidloggerL2.info(entry.getKey() + " " +
	                               entry.getValue());
	        }		
   	 	}
	
	public Double logBidWeighted(){
		Double thisfigure = getBidWeightedBySize(1);
		bidloggerL2.info(String.format("%.5f", thisfigure));
		return thisfigure;
	}

	public void scanAsk(){
		//scans current status of the ask
		for (Map.Entry<Double,Integer> entry : askBookL2.entrySet()) {
			  askloggerL2.info(entry.getKey() + " " +
	                               entry.getValue());
	        }		
		}
	
	public Double logAskWeighted(){
		Double thisfigure = getAskWeightedBySize(1);
		askloggerL2.info(String.format("%.5f", thisfigure));
		return thisfigure;
	}


}
