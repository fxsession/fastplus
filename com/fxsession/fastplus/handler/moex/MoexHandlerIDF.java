package com.fxsession.fastplus.handler.moex;



import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.OnCommand;


public class MoexHandlerIDF implements IFPFHandler{
	
	


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
		return false;
	}
	
	
}
