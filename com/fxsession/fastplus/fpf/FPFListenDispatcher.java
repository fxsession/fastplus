package com.fxsession.fastplus.fpf;

import java.util.ArrayList;

import com.fxsession.fastplus.listeners.IListener;
import com.fxsession.fastplus.listeners.IOrderbookListener;
import com.fxsession.fastplus.listeners.ISystemListener;
import com.fxsession.utils.FXPException;

/**
 * @author Dmitry Vulf
 * 
 * Listener dispatcher 
 *
 */
public class FPFListenDispatcher{
	
	static  final int ON_HEARTBEAT              = 1;
	static  final int ON_DISCONNECT             = 2;
	static  final int ON_CHANGEBID              = 3;
	static  final int ON_CHANGEASK              = 4;
	static  final int ON_VWAP                   = 5;

	
	IListener listener = null;
	
	private final ArrayList <Integer> eventQueue = new ArrayList();
	
	/**
	 * Adds outer listener 
	 * @throws FXPException 
	 */
	public void addListener(IListener listener) throws FXPException{
	    if (this.listener !=null)
	        throw new FXPException("Listener already exists");
	        
		this.listener = listener;
	}


    public void addEvent(int event){
    	eventQueue.add(event);
    }

    /**
     * main dipathch method running in a separate thread
     */
	private void dispatch(){
        new Thread(new Runnable() {
	            public void run() {
	                try {
			              while (true){
		            	    for (int i = 0; i < eventQueue.size(); i++){
		            	    	switch (eventQueue.get(i)) {
		            	    	case ON_HEARTBEAT:
		            	    		if (listener instanceof ISystemListener){
		            	    			((ISystemListener)listener).OnHeartbeat();
		            	    		}
		            	    		break;
		            	    	case ON_DISCONNECT:
		            	    		if (listener instanceof ISystemListener){
		            	    			((ISystemListener)listener).OnDisconnect();
		            	    		}
		            	    		break;
		            	    	case ON_VWAP:
		            	    	case ON_CHANGEBID:
		            	    	case ON_CHANGEASK:
		            	    	 	if (listener instanceof IOrderbookListener){
		            	    	 	    ((IOrderbookListener)listener).OnVWAP();
		            	    		}
		            	    		break;
		            	    				            	    		
		            	    	}
             	    		  eventQueue.remove(i);	
		            	    }
//		            	        System.out.println(nums.get(i));

//		       			   if (listener instanceof ISystemListener){
			            	  
			              }

	        			
	                } catch (Exception e) {
		              }
	            }
	        }).start();
 }

    
}
