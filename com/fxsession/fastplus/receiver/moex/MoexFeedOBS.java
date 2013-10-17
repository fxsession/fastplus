package com.fxsession.fastplus.receiver.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;
import com.fxsession.fastplus.receiver.moex.depreciated.MoexFeedOLS;

/**
 * @author Dmitry Vulf
 * Order Book snapshot
 *
 */
public class MoexFeedOBS extends MoexFeed {
	private static Logger mylogger = Logger.getLogger(MoexFeedOLS.class);
	
	public MoexFeedOBS(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public String getTemplateID() {
		return "3302";
	}

	@Override
	public String getSiteID() {
		return "OBS-A";
	}
	
	@Override
	public void processMessage(Message message) throws FastConnectionException {
		try{
			if (message.getTemplate().getId().equals(getTemplateID())){
				FPFMessage fmessage = new FPFMessage(SYMBOL); 	
				String value = message.getString(FPFMessage.getFieldName(MSGSEQNUM));
				fmessage.putFieldValue(MSGSEQNUM, value);
				value = message.getString(FPFMessage.getFieldName(LASTMSGSEQNUMPROCESSED));
				fmessage.putFieldValue(LASTMSGSEQNUMPROCESSED, value);
				value = message.getString(FPFMessage.getFieldName(RPTSEQ));
				fmessage.putFieldValue(RPTSEQ, value);
				value = message.getString(FPFMessage.getFieldName(SYMBOL));
				fmessage.putFieldValue(SYMBOL, value);
				SequenceValue secval =message.getSequence (FPFMessage.getFieldName(GROUPMDENTRIES));
				for (int i=0;i < secval.getValues().length;i++){
					value =  secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYID));
					fmessage.putFieldValue(MDENTRYID, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYTYPE));
					fmessage.putFieldValue(MDENTRYTYPE, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYSIZE));
					fmessage.putFieldValue(MDENTRYSIZE, value);
					value = secval.getValues()[i].getString(FPFMessage.getFieldName(MDENTRYPX));
					fmessage.putFieldValue(MDENTRYPX, value);
					dispatcher.dispatch(this,fmessage);
				}
			}
			else 
				processHeartbeat(message);
		}
        catch(Exception e) {
        	mylogger.error(e);
        	throw new FastConnectionException(e);
        }
	}

}
