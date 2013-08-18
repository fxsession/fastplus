package com.fxsession.fastplus.fpf;

/**
 * @author Dmitry Vulf
 * base L3, L3 and L1 
 *
 */
import java.util.HashMap;
import java.util.Map;



public interface IFPFOrderBook {
	
	class OrderBookRecord {
		 public Integer size = null;
		 public Double px = null;
		 public String toString(){return new String(size + " " +px);}
		 public static Integer string2Size(String _size) { 
			 if (_size!=null)   
				 return Integer.valueOf(_size); 
			 else return null;}
		 public static Double string2Px(String _px) {
			 if (_px !=null)
				return Double.valueOf(_px);
			 else return null;}
	};
		
	static  final String ADD= "0";
	static  final String CHANGE= "1";
	static  final String DELETE= "2";
	static  final String BID= "0";  //quote for buy
	
	final Map <String,OrderBookRecord> bidBook = new HashMap<String,OrderBookRecord> ();
	final Map <String,OrderBookRecord> askBook = new HashMap<String,OrderBookRecord> ();
	
	void addBid(String entryId, String size, String px);
	void changeBid(String entryId, String size,String px);
	void deleteBid(String entryId);
	void addAsk(String entryId, String size, String px);
	void changeAsk(String entryId, String size,String px);
	void deleteAsk(String entryId);
 
}
