package com.fxsession.fastplus.receiver.moex;



import org.openfast.session.Endpoint;

import com.fxsession.fastplus.fpf.FPFXmlSettings;
import com.fxsession.fastplus.fpf.FPFeed;
import com.fxsession.fastplus.ssm.SSMConnection;
import com.fxsession.fastplus.ssm.SSMEndpoint;

/**
 * @author Dmitry Vulf
 *  
 *  Abstract (still) for all MOEX feeds
 *  
 *  Main purpose - get SSM connection
 */
public abstract class MoexFeed extends FPFeed{
	public MoexFeed(String id) {
		super(id);

	}


	/**
	 * On this point SSM is applied for all inheritors of MoexFeed
	 */
	public Endpoint getEndpoint() {
      String sitename = getSiteID();

      String port   = FPFXmlSettings.readConnectionElement(sitename,SSMConnection.PORT_N);
      String group  = FPFXmlSettings.readConnectionElement(sitename,SSMConnection.GROUP_IP);
      String ifaddr = FPFXmlSettings.readConnectionElement(sitename,SSMConnection.INTERFACE_IP);
      return new SSMEndpoint(Integer.parseInt(port),group,ifaddr);
	}

}
