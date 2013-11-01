package com.fxsession.fastplus.listeners;

/**
 * @author Dmitry Vulf
 * 
 * Receives system events
 *
 */
public interface ISystemListener extends IListener{
        /*
         * Receives heartbeat - all messages from all feeds
         */
        public void OnHeartbeat();
        
        /*
         * Receives disconnect event.
         */
        public void OnDisconnect();
}