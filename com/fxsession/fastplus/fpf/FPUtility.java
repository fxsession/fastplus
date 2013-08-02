package com.fxsession.fastplus.fpf;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;




/**
 * @author Dmitry Vulf
 * 
 * Common class containing different useful things
 *
 */
public class FPUtility {
	
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

}
