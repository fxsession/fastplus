/**
 *  @author Dmitry Vulf
 *  
 *  Orders snapshot
 */
package com.fxsession.fastplus.handler.moex;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.IFPField;
import com.fxsession.fastplus.fpf.OnCommand;

public class MoexHandlerOLS implements IFPFHandler,IFPField{


	@Override
	public String getInstrumentID() {
		return "EURUSD000TOM";
	}

	@Override
	public OnCommand push(FPFMessage message) {
		OnCommand retval = OnCommand.ON_PROCESS;
		return retval;
	}

	@Override
	public boolean checkRepeatMessage(String sRpt) {
		// TODO Auto-generated method stub
		return false;
	}

}
