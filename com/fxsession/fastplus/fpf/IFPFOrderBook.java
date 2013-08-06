package com.fxsession.fastplus.fpf;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Dmitry Vulf
 * 
 * Order book logics
 *
 */
public interface IFPFOrderBook {
	
	class OrderBookRecord {
		 public Boolean type; //bid -0, ask -1
		 public Integer size;
		 public Double px;
		 public String toString(){return new String(type + " " + size + " " +px);}
		};
		
	static  final String ADD= "0";
	static  final String UPDATE= "1";
	static  final String DELETE= "2";

		
	final Map <String, OrderBookRecord> addList = new HashMap<String,OrderBookRecord>();
	final Map <String, OrderBookRecord> changeList  = new HashMap<String,OrderBookRecord>();
	final Map <String, OrderBookRecord> deleteList  = new HashMap<String,OrderBookRecord>();
	
}
