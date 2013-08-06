
package com.fxsession.fastplus.fpf;

import java.util.HashMap;
import java.util.Map;

import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
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
	static public final String MSGSEQNUM = "MsgSeqNum";
	static public final String MDENTRYID = "MDEntryID";
	static public final String MDENTRYPX = "MDEntryPx";
	static public final String MDENTRYSIZE = "MDEntrySize";
	static public final String MDENTRYTYPE = "MDEntryType";
	static public final String MDUPDATEACTION = "MDUpdateAction";
	
	String getInstrumentID();
	OnCommand push(Message message); 
}
