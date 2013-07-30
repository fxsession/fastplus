
package com.fxsession.fastplus.fpf;

import org.openfast.Message;


/**
 * @author Dmitry Vulf
 * 
 * Base interface for all handlers. A handler belongs to the  object of business layer applying specific logic 
 * for each particular instrument i.e. EUR_RUB__TOD. 
 * Each instrument is implemented by a separate class and uniquely identified by getInstrumentID().
 * A symbol returned by getSideID must exactly match instrument id coming in particular feed.
 * see FPFFeedDispatcher for more info      
 *
 */
public interface IFPFHandler {
	public String getInstrumentID();
	public void push(Message message); 
}
