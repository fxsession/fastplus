package com.fxsession.fastplus.receiver.moex.depreciated;


import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;
import com.fxsession.fastplus.receiver.moex.MoexFeed;


/**
 * @author Dmitry Vulf
 * 
 * Orders feed
 *
 */
public class MoexFeedOLR extends MoexFeed{
		private static Logger mylogger = Logger.getLogger(MoexFeedOLR.class);
	
	public MoexFeedOLR(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}


	@Override
	public String getSiteID() {
		return "OLR-A";
	}


	@Override
	public String getTemplateID() {
		return "3310";
	}

	/*
	 * Each table has it's own field structure - therefore processMessages is defined on the higher level 
	 * Each message contains up to 3 entries - so I have to parse the massage and create separate messages for each entry 
	 */
	
	@Override
	public void processMessage(Message message) throws FastConnectionException {
		try {
			if (message.getTemplate().getId().equals(getTemplateID())){
				FPFMessage fmessage = new FPFMessage(SYMBOL); 	
				String value = message.getString(FPFMessage.getFieldName(MSGSEQNUM));
				fmessage.putFieldValue(MSGSEQNUM, value);
				SequenceValue secval =message.getSequence (FPFMessage.getFieldName(GROUPMDENTRIES));
				for (int i=0;i < secval.getValues().length;i++){
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(SYMBOL));
					fmessage.putFieldValue(SYMBOL, value);
					value =  secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYID));
					fmessage.putFieldValue(MDENTRYID, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYTYPE));
					fmessage.putFieldValue(MDENTRYTYPE, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYSIZE));
					fmessage.putFieldValue(MDENTRYSIZE, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYPX));
					fmessage.putFieldValue(MDENTRYPX, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(RPTSEQ));
					fmessage.putFieldValue(RPTSEQ, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDUPDATEACTION));
					fmessage.putFieldValue(MDUPDATEACTION, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(ORIGINTIME));
					fmessage.putFieldValue(ORIGINTIME, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYTIME));
					fmessage.putFieldValue(MDENTRYTIME, value);
					dispatcher.dispatch(this,fmessage);
				}
			
			}
			else 
				processHeartbeat(message);
		}catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
        }
	}
	
	
}
