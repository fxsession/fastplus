package com.fxsession.fastplus.receiver.moex;


import org.openfast.Message;

import com.fxsession.fastplus.fpf.FPFeedDispatcher;




/**
 * @author Dmitry Vulf
 * 
 * implements IDF(Instruments definition) feed
 *
 */



public class MoexFeedIDF extends MoexFeed{
	

	
	public MoexFeedIDF(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public String getSiteID() {
		return "IDF-A";
	}

	@Override
	public String getTemplateID() {
		return "2005";
	}
	
	/**
	 * Logic impemented in MoexFeed doesn't work here. 
	 */
	
	@Override
	public void processMessage(Message message) {
		if (message.getTemplate().getId().equals(getTemplateID())){
			String msgSeqNum = message.getString(MSGSEQNUM);
			int iMsgSeqNum = Integer.parseInt(msgSeqNum);
			String key = message.getString(SYMBOL);
			dispatcher.dispatch(this,key,iMsgSeqNum,message);
		} 
	}

}
