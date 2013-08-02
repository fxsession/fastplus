package com.fxsession.fastplus.handler.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;

import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.OnCommand;

/**
 * @author Dmitry Vulf
 *
 */
public class MoexHandlerOLR implements IFPFHandler{
	
	private static Logger mylogger = Logger.getLogger(MoexHandlerOLR.class);

	static private final String GROUPMDENTRIES = "GroupMDEntries";
	static private final String MDENTRYPX = "MDEntryPx";
	static private final String MDENTRYSIZE = "MDEntrySize";
	static private final String MDENTRYTYPE = "MDEntryType";
	static private final String MDUPDATEACTION = "MDUpdateAction";
	static private final String MDENTRYID = "MDEntryID";
	
	static private final String RPTSEQ = "RptSeq";
	
	static private final String ADD= "0";
	static private final String UPDATE= "1";
	static private final String DELETE= "2";
	
	static private final String BID= "0";  //quote for buy
	
	@Override
	public String getInstrumentID() {
		return "EURUSD000TOM";
	}

	@Override
	public OnCommand push(Message message) {
		OnCommand retval = OnCommand.ON_PROCESS;
		SequenceValue secval =message.getSequence (GROUPMDENTRIES);

		for (int i=0;i < secval.getValues().length;i++){
		    String updateaction;
			switch (secval.getValues()[i].getString(MDUPDATEACTION)){
			       case ADD : updateaction = " add"; break;
			       case UPDATE : updateaction = " update"; break;
			       case DELETE : updateaction = " delete"; break;
			       default : updateaction = " ?"; 
			       }
			mylogger.info(secval.getValues()[i].getString(MDENTRYID) + 
								" price :"+secval.getValues()[i].getString(MDENTRYPX) + "		[" +  
								secval.getValues()[i].getString(MDENTRYSIZE)+ 
						       "]		" +
						       (secval.getValues()[i].getString(MDENTRYTYPE).trim().equals(BID) ? "bid" : "ask") +  
						       updateaction + " {" +
						       secval.getValues()[i].getString(RPTSEQ)+"}" );
		}
		return retval;
	}

}
