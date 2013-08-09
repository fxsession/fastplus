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
		 public Integer type = null; //bid -0, ask -1
		 public Integer size = null;
		 public Double px = null;
		 public String toString(){return new String(((type == 1)? "ask":"bid") + " " + size + " " +px);}
		 public void string2Type(String _type) {if (_type!=null) 
			 										type = Integer.valueOf(_type); }
		 public void string2Size(String _size) {if (_size!=null) 
			 										size = Integer.valueOf(_size);}
		 public void string2Px(String _px) {if (_px !=null)
			 									px = Double.valueOf(_px);}
		};
		
	static  final String ADD= "0";
	static  final String UPDATE= "1";
	static  final String DELETE= "2";

		
	final Map <String, OrderBookRecord> addList = new HashMap<String,OrderBookRecord>();
	final Map <String, OrderBookRecord> changeList  = new HashMap<String,OrderBookRecord>();
	final Map <String, OrderBookRecord> deleteList  = new HashMap<String,OrderBookRecord>();
	
}
