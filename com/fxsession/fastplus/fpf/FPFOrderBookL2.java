package com.fxsession.fastplus.fpf;

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
	private final TreeMap <Double,Integer> bidBookL2 = new TreeMap<Double,Integer> ();
	private final TreeMap <Double,Integer> askBookL2 = new TreeMap<Double,Integer> ();

	public FPFOrderBookL2() {
	}
	
	private void addBidL2new (Double px, Integer size) {
		if (bidBookL2.size()<L2_MAX_SIZE)
				bidBookL2.put(px,size);
		else // have reached max depth
		{
			Double firstKey = bidBookL2.firstKey(); 
			if ( firstKey < px){
			    /*for bid the element with lowest value is less significant  
				 *remove first key - add new level
				 */
				bidBookL2.remove(firstKey);
				bidBookL2.put(px,size);
			}
		}
	}
	
	protected void addBidL2(Double px, Integer size) {
		if (bidBookL2.containsKey(px))
			//map already  contains level for this price
			bidBookL2.put(px,bidBookL2.get(px) + size);
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
			bidBookL2.put(px,(bidBookL2.get(px) - prevsize) +size);
		else 
			addBidL2new(px,size); 
	}
	
	protected void deleteBidL2(Double px, Integer size){
		Integer newsize = bidBookL2.get(px);
		if (newsize!=null) {
			newsize-= size;
			if (newsize > 0)
				bidBookL2.put(px, newsize);
			else
				bidBookL2.remove(px);
		}
	}

	private void addAskL2new(Double px, Integer size){
		if (askBookL2.size()<L2_MAX_SIZE)
			askBookL2.put(px,size);
		else { // have reached max depth
			Double lastKey = askBookL2.lastKey(); 
			if ( lastKey > px){
				/*for ask the element with highest value is less significant
				 *remove last key - add new level
				 */
				askBookL2.remove(lastKey);
				askBookL2.put(px,size);
			}
		}
	}

	protected void addAskL2(Double px, Integer size) {
		if (askBookL2.containsKey(px)) 
			//map already  contains level for this price
			askBookL2.put(px,askBookL2.get(px) + size);
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
	    	askBookL2.put(px,(askBookL2.get(px) - prevsize) +size);
	    else
		  	addAskL2new(px,size);
	}
	
	protected void deleteAskL2(Double px, Integer size){
		Integer newsize = askBookL2.get(px);
		if (newsize!=null) {
			newsize-=size;
			if (newsize > 0)
				askBookL2.put(px, newsize);
			else
				askBookL2.remove(px);
		}
	}
	
	public void scanBid(){
		//scans current status of the bid
		//while displaying it should be reversed
   	    for (Map.Entry<Double,Integer> entry : bidBookL2.entrySet()) {
	            bidloggerL2.info(entry.getKey() + " " +
	                               entry.getValue());
	        }		
   	 bidloggerL2.info("--->" + bidBook.size());
	}

	public void scanAsk(){
		//scans current status of the bid
		//while displaying it should be reversed
		for (Map.Entry<Double,Integer> entry : askBookL2.entrySet()) {
			  askloggerL2.info(entry.getKey() + " " +
	                               entry.getValue());
	        }		
		askloggerL2.info("--->" + askBook.size());
	}

}
