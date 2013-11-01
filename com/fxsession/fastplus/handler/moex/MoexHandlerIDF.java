package com.fxsession.fastplus.handler.moex;



import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.OnCommand;
import com.fxsession.utils.FXPException;


public class MoexHandlerIDF extends MoexHandler{
        
        public MoexHandlerIDF(String instrument) {
                super(instrument);
                // TODO Auto-generated constructor stub
        }

        public OnCommand push(FPFMessage message) throws FXPException{
                OnCommand retval = OnCommand.ON_PROCESS;
                return retval;
        }

        public boolean checkRepeatMessage(String sRpt) {
                return false;
        }

        
}