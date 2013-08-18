package com.fxsession.fastplus.handler.moex;



import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.IFPFHandler;

import com.fxsession.fastplus.fpf.OnCommand;

/**
 * @author Dmitry Vulf
 *
 */
public class MoexHandlerOLS implements IFPFHandler{


	@Override
	public String getInstrumentID() {
		return "EURUSD000TOM";	
	}

	@Override
	public OnCommand push(FPFMessage message) {
		return OnCommand.ON_PROCESS;
	}


}
