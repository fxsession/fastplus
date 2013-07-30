package com.fxsession.fastplus.fpf;

import org.openfast.Message;
import org.openfast.session.Endpoint;

public interface IFPFeed {
	public void processMessage(Message message);
	public Endpoint getEndpoint();
	public String getSiteID();
	public String getTemplateID();
}
