/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.openfast.session.FastConnectionException;


import com.fxsession.fastplus.fpf.FPFMessage;


import com.fxsession.fastplus.fpf.FPUtility;
import com.fxsession.fastplus.fpf.IFPFOrderBook;
import com.fxsession.fastplus.fpf.OnCommand;


/**
 * @author Dmitry Vulf
 * 
 * OBR
 *
 */

public class MoexHandlerOBR   extends MoexHandler {
	
	private static Logger mylogger = Logger.getLogger("L2OBR");
	

	public MoexHandlerOBR(String instrument) {
		super(instrument);
	}

	/*
	 * Dispatcher
	 * 
	 */
	public OnCommand push(FPFMessage message) throws FastConnectionException {
		OnCommand retval = OnCommand.ON_PROCESS;
		try{
			String rptseq = message.getFieldValue(RPTSEQ);
			if (checkRepeatMessage(rptseq))
				return retval;
			String type = message.getFieldValue(MDENTRYTYPE);
			String size = message.getFieldValue(MDENTRYSIZE);
			String px = message.getFieldValue(MDENTRYPX);
			String updAction =message.getFieldValue(MDUPDATEACTION);

			switch (updAction){
				case IFPFOrderBook.ADD 		: 
					if (type.equals(IFPFOrderBook.BID))  
						addBid(size, px); 
					else
						addAsk(size, px);  
				break;
				case IFPFOrderBook.CHANGE 	:   
					if (type.equals(IFPFOrderBook.BID))
						changeBid(size, px);
					else
						changeAsk(size, px);
				break;
				case IFPFOrderBook.DELETE 	: 
					if (type.equals(IFPFOrderBook.BID)) 
						deleteBid(size,px); 
					else
						deleteAsk(size,px);
				break;
				default :break; 
			}
		} catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
        }
		return retval;
	}

	/*
	 * ORDER BOOK PART
	 */
	
	/*
	 *  private maps containing sorted by price orders books
	 *  for bid and ask
	 *  key - price, value - size
	 *
	 */
	private final TreeMap <Double,Integer> bidBook = new TreeMap<Double,Integer> (Collections.reverseOrder());
	
	private final TreeMap <Double,Integer> askBook = new TreeMap<Double,Integer>(Collections.reverseOrder());
	
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
	
	public void addBid(String size, String px) throws FastConnectionException {
		Integer isize = FPUtility.string2Int(size);
		Double dpx = FPUtility.string2Double(px);
		bidBook.put(dpx,isize);  
		mylogger.info(getInstrumentID() + " bid   a  " + FPUtility.Double2String(dpx,5) + " " + isize);
	}

	
	public void changeBid(String size,String px) throws FastConnectionException {
		addBid(size, px);
	}

	
	public void deleteBid(String size,String px) throws FastConnectionException {
		if (px == null)
			return;
		Integer isize = FPUtility.string2Int(size);
		Double dpx = FPUtility.string2Double(px);
		bidBook.remove(dpx);
		mylogger.info(getInstrumentID() + " bid   d  " + FPUtility.Double2String(dpx,5) + " " + isize);		
	}
	
	
	private String scanBid() {
		/*
		 * a template how to visualize bid orderbook
		 * best bid(maximum) - on the top 
		 */
		
		String retval = "";
   	    for (Map.Entry<Double,Integer> entry : bidBook.entrySet()) {
	            retval += FPUtility.Double2String(entry.getKey(),5) + " " +entry.getValue() + "\r\n";
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
	
	public void addAsk(String size, String px) throws FastConnectionException {
		Integer isize = FPUtility.string2Int(size);
		Double dpx = FPUtility.string2Double(px);
		askBook.put(dpx,isize);  
		mylogger.info(getInstrumentID() + " ask   a                      "+ FPUtility.Double2String(dpx,5) + " " + isize);
	}

	
	public void changeAsk(String size,String px) throws FastConnectionException {
		addAsk(size, px);
	}

	
	public void deleteAsk(String size,String px) throws FastConnectionException {
		if (px ==null) 
			return;
		Integer isize = FPUtility.string2Int(size);
		Double dpx = FPUtility.string2Double(px);
		askBook.remove(dpx);
		mylogger.info(getInstrumentID() + " ask   d                      " + FPUtility.Double2String(dpx,5) + " " + isize);		
	}

	
	public String scanAsk() {
		/*
		 * a template how to visualize bid orderbook
		 * best ask(minimum) - on the bottom
		 */
		String retval = ""; 
   	    for (Map.Entry<Double,Integer> entry : askBook.entrySet()) {
   	    	retval +="                       " + FPUtility.Double2String(entry.getKey(),5) + " " +entry.getValue() + "\r\n";
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
	
	@Override
	public String toString(){
	    String header = "\r\n" + getInstrumentID();
		String bidbook = "\r\nBID \r\n" + scanBid();
		String askbook = "\r\n                       ASK \r\n" +  scanAsk();
		return header + askbook + bidbook;
	}

	
}
