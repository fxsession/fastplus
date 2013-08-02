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
public class MoexHandlerOLS implements IFPFHandler{

	private static Logger mylogger = Logger.getLogger(MoexHandlerOLS.class);
	
	static private final String LASTSGMPROCEEDED = "LastMsgSeqNumProcessed";
	static private final String RPTSEQ = "RptSeq";
	static private final String LASTFRG =  "LastFragment";
	static private final String GROUPMDENTRIES = "GroupMDEntries";
		static private final String MDENTRYID = "MDEntryID";
		static private final String MDENTRYTYPE = "MDEntryType";
		static private final String MDENTRYSIZE = "MDEntrySize";
		static private final String MDENTRYPX ="MDEntryPx";
		
	static private final String BID= "0";  //quote for buy		

	@Override
	public String getInstrumentID() {
		return "EURUSD000TOM";	
	}

	@Override
	public OnCommand push(Message message) {
/*		
		SequenceValue secval =message.getSequence (GROUPMDENTRIES);
		String lastmshprd  = message.getString(LASTSGMPROCEEDED);
		String rptseq = message.getString(RPTSEQ);
		String lastfrg  = message.getString(LASTFRG);

		for (int i=0;i < secval.getValues().length;i++){
			mylogger.info("MDEntryID->" + secval.getValues()[i].getString(MDENTRYID) + 
								" RptSeq->"+	rptseq +
								" LastMsgSeqNumProcessed->" + lastmshprd + 
								" LastFragment->" + lastfrg + " "+
								" price :"+secval.getValues()[i].getString(MDENTRYPX) + "		[" +  
								secval.getValues()[i].getString(MDENTRYSIZE)+ 
						       "]		" +
						       (secval.getValues()[i].getString(MDENTRYTYPE).trim().equals(BID) ? "bid" : "ask"));
		}
*/		
		return OnCommand.ON_PROCESS;
	}


}
