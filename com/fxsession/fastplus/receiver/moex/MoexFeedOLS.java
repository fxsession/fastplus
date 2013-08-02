package com.fxsession.fastplus.receiver.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;



import com.fxsession.fastplus.fpf.FPFeedDispatcher;
import com.fxsession.fastplus.handler.moex.MoexHandlerOLS;

/**
 * @author Dmitry Vulf
 * 
 * Orders snapshoot
 *
 */
public class MoexFeedOLS extends MoexFeed {
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

	/**
	 * @param dispatcher
	 */
	public MoexFeedOLS(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public String getTemplateID() {
		return "3300";
	}

	@Override
	public String getSiteID() {
		return "OLS-A";
	}
	
	@Override
	public void processMessage(Message message) {
		if (message.getTemplate().getId().equals(getTemplateID())){
			String keyValue = message.getString(SYMBOL);
			String msgSeqNum = message.getString(MSGSEQNUM);
			int iMsgSeqNum = Integer.parseInt(msgSeqNum);
//->			
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
//->			
			dispatcher.dispatch(this,keyValue,iMsgSeqNum,message);
		} 
	}
}
