package com.fxsession.fastplus.ssm;

/**
 * @author Dmitry Vulf
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class SSMInputStream extends InputStream{
    private static final int BUFFER_SIZE = 64 * 1024;
    DatagramChannel datachannel;
    private final ByteBuffer buffer;
    private boolean tracemode = false;

    public SSMInputStream(DatagramChannel dc) {
    	String ifTraceraw = SSMParameters.getInstance().readConnectionElementA(SSMParameters.TRACE_RAW);
		if (ifTraceraw.equals("true")){
			tracemode = true;
		}
        this.datachannel = dc;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.flip();
    }


/*
 *   to debug:	
    int lim = buffer.limit();
    int pos = buffer.position();
*/
    @Override

 
    public int read() throws IOException {
        if (!buffer.hasRemaining()) {
        	buffer.clear();
            datachannel.receive(buffer);
            if (tracemode)
        		SSMTrace.getInstance().traceRaw(buffer);  //to trace TRACE_RAW flag should be raised
            buffer.flip();
        }
        return (buffer.get() & 0xFF);
    }
    @Override
    public int available() throws IOException {
        return buffer.remaining();
    }

}
