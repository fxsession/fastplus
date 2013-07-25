package com.fxsession.fastplus.fpf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.openfast.Message;

/**
 * @author Dmitry Vulf

 * logs decoded messages to the file

 * Singleton
 *
 */

public class FPFTrace {
	
	private static Logger logger = Logger.getLogger(FPFTrace.class);
	
	private static FPFTrace instance;
	private RandomAccessFile decodedFile = null;
	private RandomAccessFile rawFile = null;
	private boolean isTracedecoded = false;
	
	private FPFTrace () {
	}

    static {
	 instance = new FPFTrace();	    
    }

	public static FPFTrace getInstance() {
		/*
		 * first call - created trace files if flags are raised 
		 */
        return instance;
    }	

	public void initDecoded(String Site){
		try{
			String ifTracedecoded = FPFXmlSettings.readConnectionElement(Site,FPFXmlSettings.TRACE_DECODED);
			if (ifTracedecoded.equals("true")){
				isTracedecoded = true;
				if (decodedFile == null){
					String FileName = FPFXmlSettings.readConnectionElement(Site,FPFXmlSettings.TRACE_DECODED_FILE);
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
