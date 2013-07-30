package com.fxsession.fastplus.handler.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;

import com.fxsession.fastplus.fpf.IFPFHandler;

/**
 * @author Dmitry Vulf
 *
 */
public class MoexHandler implements IFPFHandler{
	
	private static Logger mylogger = Logger.getLogger(MoexHandler.class);

	static private final String GROUPMDENTRIES = "GroupMDEntries";
	static private final String MDENTRYPX = "MDEntryPx";
	static private final String MDENTRYSIZE = "MDEntrySize";
	static private final String MDENTRYTYPE = "MDEntryType";
	static private final String MDUPDATEACTION = "MDUpdateAction";
	static private final String ORDERSTATUS = "OrderStatus";
	
	static private final String ADD= "0";
	static private final String UPDATE= "1";
	static private final String DELETE= "2";
	
	@Override
	public String getInstrumentID() {
		return "EUR_RUB__TOD";
	}

	@Override
	public void push(Message message) {
		if (mylogger.isDebugEnabled()) {
			SequenceValue secval =message.getSequence (GROUPMDENTRIES);
			if (secval.getValues().length>0){
				String updateaction;
				switch (secval.getValues()[0].getString(MDUPDATEACTION)){
			       case ADD : updateaction = " add"; break;
			       case UPDATE : updateaction = " update"; break;
			       case DELETE : updateaction = " delete"; break;
			       default : updateaction = " ?"; 
			       }
				
				mylogger.info("price :"+secval.getValues()[0].getString(MDENTRYPX) + " [" +  secval.getValues()[0].getString(MDENTRYSIZE)+ 
						       "] " + ((secval.getValues()[0].getString(MDENTRYTYPE) =="0") ? "buy" : "sell") + 
						       updateaction + " {" +
						       secval.getValues()[0].getString(ORDERSTATUS)+"}");
			}
		}
	}

}
