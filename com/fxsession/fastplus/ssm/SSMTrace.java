package com.fxsession.fastplus.ssm;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.openfast.Message;

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

	public void initDecoded(String Site){
		try{
			String ifTracedecoded = SSMConnection.readConnectionElement(Site,SSMConnection.TRACE_DECODED);
			if (ifTracedecoded.equals("true")){
				isTracedecoded = true;
				if (decodedFile == null){
					String FileName = SSMConnection.readConnectionElement(Site,SSMConnection.TRACE_DECODED_FILE);
					logger.debug("Enabled debug trace to file :"+ FileName);
					File tmpFile = new File (FileName);
					if (tmpFile.exists())
						tmpFile.delete();
					decodedFile = new RandomAccessFile(FileName,"rw");
				}
			}
		} catch (Exception e) {
        	logger.error(e);			
		}
	}	
	
	public void traceDecoded(Message message){
		if (isTracedecoded && decodedFile!=null){
    		String msg = message.toString();
			FileChannel outChannel = decodedFile.getChannel();
			ByteBuffer bb = ByteBuffer.allocate(msg.length());
			bb.clear();
			bb.put(msg.getBytes());
			bb.flip();
			try {
				while (bb.hasRemaining())
					outChannel.write(bb);
			} catch (IOException e) {
				logger.error(e);
			}
		}
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
