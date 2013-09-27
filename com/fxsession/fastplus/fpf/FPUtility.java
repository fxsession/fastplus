package com.fxsession.fastplus.fpf;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.openfast.session.FastConnectionException;




/**
 * @author Dmitry Vulf
 * 
 * Common class containing different useful things
 *
 */
public class FPUtility {
	private static Logger mylogger = Logger.getLogger(FPUtility.class);
	
	static private SimpleDateFormat dateformat= new SimpleDateFormat("HH:mm:ss");   
	
	/*
	 * Converts timestamp  + microseconds to String representation 
	 */
 public static String toTimestampMcs(long value, long microsecs){
       
     Calendar cal = Calendar.getInstance();
     cal.setTimeZone(TimeZone.getTimeZone("GMT"));

     int hour = (int) (value / 10000000);
     value %= 10000000;
     int min = (int) (value / 100000);
     value %= 100000;
     int sec = (int) (value / 1000);
     cal.set(0, 0, 0, hour, min, sec);     
	 Date dt = cal.getTime();
	 int mls = (int) (microsecs / 1000);
	 int msc = (int) (microsecs % 1000);
	 String date = dateformat.format(dt) + "."+ mls + "." + msc;
	 return date;
  }
 
 public static String toTimestampMls(long value, long microsecs){
     Calendar cal = Calendar.getInstance();
     cal.setTimeZone(TimeZone.getTimeZone("GMT"));

     int hour = (int) (value / 10000000);
     value %= 10000000;
     int min = (int) (value / 100000);
     value %= 100000;
     int sec = (int) (value / 1000);
     cal.set(0, 0, 0, hour, min, sec);     
	 Date dt = cal.getTime();
	 int mls = (int) (microsecs / 1000);
	 String date = dateformat.format(dt) + "."+ mls;
	 return date;
 }
 
 public static Integer string2Size(String _size) throws FastConnectionException {
	 try {
		 if (_size.equals(null) || _size.trim().isEmpty())
		 	return 0;   
		 else 
		 	return Integer.valueOf(_size.trim());
	 } catch (Exception e){
     	mylogger.error(e + " input value:<" + _size + ">");
     	throw new FastConnectionException(e);
	 }
 }
 
 public static Double string2Px(String _px) throws FastConnectionException{
 	 try {
	 	if (_px.equals(null) || _px.trim().isEmpty())
			return 0d;
	 	else 
	 		return Double.valueOf(_px);
 	}catch (Exception e){
     	mylogger.error(e + " input value:<" + _px+">");
   		throw new FastConnectionException(e);
	}
  }
 }
