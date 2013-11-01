package com.fxsession.fastplus.fpf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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
public class FPFListenDispatcher implements Runnable{
        
        
   IListener listener = null;
        
   /*
    * Event queue - contains events from all events coming from different handlers
    * as a key is used Index {command + handler ID}    
    */
   public class QueueKey {

	    private FPFCommand command;
	    private String handlerID;

	    public QueueKey(FPFCommand command, String handlerID) {
	        this.command = command;
	        this.handlerID = handlerID;
	    }
	}
   
   private final Vector <QueueKey> eventQueue = new Vector <QueueKey>();
        
   /**
   * Adds outer listener 
   * @throws FXPException 
   */
   public void addListener(IListener listener) throws FXPException{
      if (this.listener !=null)
         throw new FXPException("Listener already exists");
                
      this.listener = listener;
   }


   public void addEvent(FPFCommand command,IFPFHandler handler){
	   
         eventQueue.put(command,handler);
   }

   /**
    * main dipathch method running in a separate thread
    */


@Override
public void run() {
    try {
        while (true){
           for (int i = 0; i < eventQueue.size(); i++){
               eventQueue.remove(i);        
               switch (eventQueue.get(i)) {
                  case ON_HEARTBEAT:
                  if (listener instanceof ISystemListener){
                      eventQueue.remove(i);        
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
           }

                                
    } catch (Exception e) {
 }
}