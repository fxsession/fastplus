package com.fxsession.fastplus.ssm;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

/**
 * @author Dmitry Vulf
 * IMplement 2 main functions:
 * logs decoded messages to the file
 * logs raw messages to the file
 * Singleton
 *
 */

public class SSMTrace {
	
	private static Logger logger = Logger.getLogger(SSMTrace.class);
	
	private static SSMTrace instance;
	private RandomAccessFile decodedFile = null;
	private RandomAccessFile rawFile = null;
	private boolean isTracedecoded = false;
	private boolean isTraceraw = false;
	
	private SSMTrace () {
	}

    static {
	 instance = new SSMTrace();	    
    }

	public static SSMTrace getInstance() {
		/*
		 * first call - created trace files if flags are raised 
		 */
        return instance;
    }	

	public void initDecoded(){
		try{
			String ifTracedecoded = SSMParameters.getInstance().readConnectionElementA(SSMParameters.TRACE_DECODED);
			if (ifTracedecoded.equals("true")){
				isTracedecoded = true;
				logger.debug("Enabled debug trace");
				if (decodedFile == null){
					String FileName = SSMParameters.getInstance().readConnectionElementA(SSMParameters.TRACE_DECODED_FILE);
					decodedFile = new RandomAccessFile(FileName,"rw");
				}
			}
		} catch (Exception e) {
        	logger.error(e);			
		}
	}	
	
	public void traceDecoded(String message){
		if (isTracedecoded && decodedFile!=null){
			FileChannel outChannel = decodedFile.getChannel();
			ByteBuffer bb = ByteBuffer.allocate(message.length());
			bb.clear();
			bb.put(message.getBytes());
			bb.flip();
			try {
				while (bb.hasRemaining())
					outChannel.write(bb);
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}


	public void initRaw(){
		try{
			String ifTraceraw = SSMParameters.getInstance().readConnectionElementA(SSMParameters.TRACE_RAW);
			if (ifTraceraw.equals("true")){
				logger.debug("Enabled raw trace");
				isTraceraw = true;
				if (rawFile == null){
					String FileName = SSMParameters.getInstance().readConnectionElementA(SSMParameters.TRACE_RAW_FILE);
					rawFile = new RandomAccessFile(FileName,"rw");
				}
			}
		} catch (Exception e) {
        	logger.error(e);			
		}
	}
	
	public int traceRaw(ByteBuffer message){
		int bytesWritten = -1;
		if (isTraceraw && rawFile!=null){		
			FileChannel outChannel = rawFile.getChannel();
			ByteBuffer bb = message.duplicate();
			bb.flip();
			try {
				bytesWritten = outChannel.write(bb);
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return bytesWritten;		
	}
	
	public void close(){
		try {
			if (decodedFile!=null)
				decodedFile.close();
			if (rawFile!=null)
				rawFile.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

}
