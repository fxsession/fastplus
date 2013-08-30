package com.fxsession.fastplus.fpf;

/**
 * @author Dmitry Vulf
 * base L3, L3 and L1 
 *
 */



public interface IFPFOrderBook {
	
		
	static  final String ADD= "0";
	static  final String CHANGE= "1";
	static  final String DELETE= "2";
	static  final String BID= "0";  //quote for buy
	
	void addBid(String entryId, String size, String px, Long timestamp, Long mcs);
	void changeBid(String entryId, String size,String px,Long timestamp, Long mcs);
	void deleteBid(String entryId,String px);
	void scanBid();
	void addAsk(String entryId, String size, String px,Long timestamp, Long mcs);
	void changeAsk(String entryId, String size,String px,Long timestamp, Long mcs);
	void deleteAsk(String entryId,String px);
	void scanAsk();
}
