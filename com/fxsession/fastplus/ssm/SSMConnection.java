package com.fxsession.fastplus.ssm;

/**
 * @author Dmitry Vulf
 * Implements Connection. 
 * implements only close logic that can be actually placed to SSMEndpoint     
 *
 */


import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;

import org.openfast.session.Connection;
import org.apache.log4j.Logger;

public class SSMConnection implements Connection {
        static public final String GROUP_IP = "groupIp";
        static public final String INTERFACE_IP ="interfaceIp";
        static public final String PORT_N="port";
        
        private static Logger logger = Logger.getLogger(SSMConnection.class);
        
        
    protected DatagramChannel datachannel;
    protected int port;
    protected InetAddress group;
    protected InetAddress ifaddr;

 
        
    public SSMConnection(DatagramChannel dc, int port, InetAddress group, InetAddress ifaddr) {
        this.datachannel = dc;
        this.port = port;
        this.group = group;
        
    }
    
    public void open() {
            
    }
    
    public void close() {
        try {
            datachannel.close();
            logger.info("closed connection to group IP " + group.getHostAddress());
        }
        catch (IOException e) {
            logger.error("failed to close connection to  group IP"  + group.getHostAddress(),e);
        }
    }

    public InputStream getInputStream() throws IOException {
        return new SSMInputStream(datachannel);
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }
    


}