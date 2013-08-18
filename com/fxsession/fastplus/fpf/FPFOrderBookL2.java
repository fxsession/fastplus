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
   	private static Logger askloggerL2 = Logger.getLogger("L2askLooger");
   	private static Logger bidloggerL2 = Logger.getLogger("L2bidLooger");

	/*
	 *  private maps containing sorted by price orders books
	 *  for bid and ask
	 *  key - price, value - size
	 */
	private final Map <Double,Integer> bidBookL2 = new TreeMap<Double,Integer> ();
	private final Map <Double,Integer> askBookL2 = new TreeMap<Double,Integer> ();

	public FPFOrderBookL2() {
	}
	
	protected void addBidL2(Double px, Integer size) {
		if (bidBookL2.containsKey(px)) {
			//map already  contains level for this price
			bidBookL2.put(px,bidBookL2.get(px) + size);
		}
		else {
			bidBookL2.put(px,size);
		}
	}
	
	protected void changeBidL2(Double px, Integer size, Integer prevsize){
		if (bidBookL2.containsKey(px)) {
			//change the size for the found level
			//1. delete size formed by the deal
			//2. add new size  
			bidBookL2.put(px,(bidBookL2.get(px) - prevsize) +size);
		}
		else 
			bidloggerL2.error("L2: can't change bid record for price level : " +px); 
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
  		else 
  			bidloggerL2.error("L2: can't delete bid record for price level : " +px); 
	}

	protected void addAskL2(Double px, Integer size) {
		if (askBookL2.containsKey(px)) {
			//map already  contains level for this price
			askBookL2.put(px,askBookL2.get(px) + size);
		}
		else {
			askBookL2.put(px,size);
		}
	}
	
	protected void changeAskL2(Double px, Integer size, Integer prevsize){
		if (askBookL2.containsKey(px)) {
			//change the size for the found level
			//1. delete size formed by the deal
			//2. add new size  
			askBookL2.put(px,(askBookL2.get(px) - prevsize) +size);
		}
		else 
			askloggerL2.error("L2: can't change ask record for price level : " +px); 
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
  	else 
  		askloggerL2.error("L2: can't delete ask record for price level : " +px); 
	}
	
	public void scanBid(){
		//scans current status of the bid
		  for (Map.Entry<Double,Integer> entry : bidBookL2.entrySet()) {
	            bidloggerL2.info(entry.getKey() + " " +
	                               entry.getValue());
	        }		
	}

	public void scanAsk(){
		//scans current status of the bid
		  for (Map.Entry<Double,Integer> entry : askBookL2.entrySet()) {
			  askloggerL2.info(entry.getKey() + " " +
	                               entry.getValue());
	        }		
	}

}
