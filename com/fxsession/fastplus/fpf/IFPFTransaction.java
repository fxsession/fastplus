package com.fxsession.fastplus.fpf;

/**
 * @author Dmitry Vulf
 * base L3, L3 and L1 
 *
 */

public interface IFPFTransaction {
	static  final String T_ADD= "0";
	static  final String T_CHANGE= "1";
	static  final String T_DELETE= "2";
	static  final String T_BID= "0";  //quote for buy
	static  final String T_ASK= "1";  //quote for sell
}
