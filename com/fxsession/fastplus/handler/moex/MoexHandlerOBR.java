/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import org.apache.log4j.Logger;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFOrderBookL2;
import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.IFPFOrderBook;
import com.fxsession.fastplus.fpf.IFPField;
import com.fxsession.fastplus.fpf.OnCommand;

/**
 * @author Dmitry Vulf
 * 
 * OBR
 *
 */
public class MoexHandlerOBR extends FPFOrderBookL2 implements IFPFHandler, IFPField {
	private static Logger mylogger = Logger.getLogger("OBR");
	@Override
	public String getInstrumentID() {

		return "EURUSD000TOM";
	}
	@Override
	public OnCommand push(FPFMessage message) {
		OnCommand retval = OnCommand.ON_PROCESS;
	    String rptseq = message.getFieldValue(RPTSEQ);
		String key =  message.getFieldValue(MDENTRYID);
	    String type = message.getFieldValue(MDENTRYTYPE);
	    String size = message.getFieldValue(MDENTRYSIZE);
	    String px = message.getFieldValue(MDENTRYPX);
	    String updAction =message.getFieldValue(MDUPDATEACTION); 
		if (mylogger.isDebugEnabled())
			mylogger.info(getInstrumentID()+
						"<" + FPFMessage.getFieldName(MDENTRYID)+">"+ key +
						"<" + FPFMessage.getFieldName(MDENTRYTYPE)+">"+ type + 
						"<" + FPFMessage.getFieldName(MDENTRYSIZE)+">"+ size + 
						"<" + FPFMessage.getFieldName(MDENTRYPX)+">"+ px + 
						"<" + FPFMessage.getFieldName(MDUPDATEACTION) + ">" +updAction + 
						"<" + FPFMessage.getFieldName(RPTSEQ) + ">" +rptseq);
		return retval;
	}

}
