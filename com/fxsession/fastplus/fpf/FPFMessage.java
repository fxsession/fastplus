package com.fxsession.fastplus.fpf;

import org.openfast.Message;

public class FPFMessage {
	private Message message;
	private int msgSeqNum;  
	private String key;
	
	public FPFMessage(String key, int msgSeqNum, Message message) {
		this.key=key;
		this.msgSeqNum = msgSeqNum;
		this.message=message;
	};
	
	public String getKey() {
		return key;
	}
	
	public Message getMessage(){
		return message;
	}

	public int getMsgSeqNum(){
		return msgSeqNum;
	}
}
