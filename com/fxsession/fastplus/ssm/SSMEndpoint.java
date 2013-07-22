package com.fxsession.fastplus.ssm;

/**
 * @author Dmitry Vulf
 * Implements Endpoint. Contains connection to udp ssm, which is different to MUlticastConnnection   
 *
 */
import java.net.*;
import java.nio.channels.DatagramChannel;

import org.openfast.session.Connection;
import org.openfast.session.ConnectionListener;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;

import org.apache.log4j.Logger;


public class SSMEndpoint implements Endpoint{
	
	private static Logger mylogger = Logger.getLogger(SSMEndpoint.class);

    private   SSMConnection connection;
    
    protected int port;
    protected String group;
    protected String ifaddr;
  

    public SSMEndpoint(int port, String group, String ifaddr) {
        this.port = port;
        this.group = group;
    	this.ifaddr = ifaddr;
    }
    
    public String toString() {
        return new StringBuilder(getClass().getName())
            .append("[").append("group=").append(group)
            .append(",").append("port=").append(port)
            .append(",").append("ifaddr=").append(ifaddr)
            .append("]")
            .toString();
    }

    public Connection connect() throws FastConnectionException {
    	
        try {
           	InetAddress localHost = InetAddress.getLocalHost(); 
            NetworkInterface source_interf =  NetworkInterface.getByInetAddress(localHost);
            //Select a multicasting IP address
       	    InetAddress groupIp = InetAddress.getByName(group);
       	    DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
       	                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
       	                .bind(new InetSocketAddress(port))
       	                .setOption(StandardSocketOptions.IP_MULTICAST_IF, source_interf);
            //From now on, all multicast traffic generated in this socket will be output from the interface chosen	                
            dc.join(groupIp,source_interf,InetAddress.getByName(ifaddr));
            //I haven't find any method detecting that join failed
            mylogger.info("Joining group IP " +  group + ":" + port);
            
            connection = new SSMConnection(dc, port, groupIp, localHost);
            return connection;

        }catch (Exception e) {
            mylogger.error("Exiting application", e);
           	System.exit(1);
       }
    	return null;
    }

    public void accept() throws FastConnectionException {
        throw new UnsupportedOperationException();
    }
    public void setConnectionListener(ConnectionListener listener) {
        throw new UnsupportedOperationException();
    }

    public void close() {
    	connection.close();
    }
    
    public Connection reconnect(int port, String group, String ifaddr) throws FastConnectionException {
    	close();
        this.port = port;
        this.group = group;
    	this.ifaddr = ifaddr;
    	return connect();
    }

}
