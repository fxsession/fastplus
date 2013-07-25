package com.fxsession.fastplus.fpf;

import org.openfast.Message;

public class FPFMessage {
	private Message message;
	private String key;
	
	FPFMessage(Message message) {
//		this.key=key;
		this.message=message;
	};
	
	public String getKey() {
		return key;
	}
	
	public Message getMessage(){
		return message;
	}

}
