package com.fxsession.fastplus.fpf;

import org.openfast.Message;
import org.openfast.session.Endpoint;

public interface IFPFeed {
	
	static final public String SELECT_ALL = "*";
	
	/*
	 * Process a message came from the stream. Can have different logics - but the normal behavior: parse the message, get key fields and send them to dispatcher
	 * that's why it can't be implemented in the base abstract class - venue specific.   
	 */
	public void processMessage(Message message);
	
	/*
	 * Gets connectivity object.
	 * Constructs connection calling connect() method 
	 */
	public Endpoint getEndpoint();
	
	/*
	 * Identifies the feed. should coincide with feed settings in the xml file
	 * e.g.  <connection id="IDF-A">  getSiteID =="IDF-A" 
	 */
	public String getSiteID();
	
	/*
	 * Template ID in the xml file from the venue.  
	 */
	public String getTemplateID();
	
	/*
     * setBlockerReader works with preambula which can be specific for 
     * for different venues.
     * Here it reads 4 bytes preambula (which is compatible with Micex)
     * To specify new behavior - override this method 
	*/
	public void setBlockReader(); 
	
	/*
	 * Stop listening the feed
	 */
	public void stopProcess();
	
}
