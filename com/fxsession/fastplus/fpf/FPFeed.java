package com.fxsession.fastplus.fpf;

/**
 * @author Dmitry Vulf
 * Abstract class for all feeds
 * 
 * Responsible for applying logic for all feeds. IN fact it's made transport independent ie 
 * overriding  getEndpoint() will return required Endpoint instance either SSMEndpoint (for MOEX) or 
 * MulticastClientEndpoint/TcpEndpoint for another venue.It hasn't been tested so far but based on the exmaples provided 
 * I assume it should work.
 * 
 * ProcessMessage was also made abstract - the logic of getting key can be varied from one feed to another that's why it's better to store 
 * it inside the ending class 
 * 
 * GetSideID uniquely identifies the feed and must match connection id= ... in fastplus.xml
 *
 */






import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.openfast.Context;
import org.openfast.MessageBlockReader;
import org.openfast.MessageInputStream;
import org.openfast.session.Connection;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.Message;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.log4j.xml.DOMConfigurator;



public abstract class FPFeed implements IFPFeed {
		
    private static Logger mylogger = Logger.getLogger(FPFeed.class);
	
 
    private long startTime;
    private long stopTime;


 	private Endpoint endpoint = null;
 	
 	//Dispatcher
 	protected final FPFeedDispatcher dispatcher;

 	
 	
    private static TemplateRegistry templateRegistry = null;
    protected MessageBlockReader blockReader =  MessageBlockReader.NULL;
     
    private String logFileName = "log4j.xml";
     
    private boolean started = false;
    
    
    //Signals that main class loop can be stopped - overriden   
    protected boolean isProcessing =true;
    
    //returns processing duration in microseconds
    protected final long getDeltaMcs(){
    	return ((stopTime-startTime)/1000); }
  //returns processing duration in milliseconds
    protected final long getDeltaMs(){
    	return ((stopTime-startTime)/1000000); }

    

    
    /**
     * Pre and post processing
     */
    protected void preProcess() {
   	 	mylogger.info("started listening to " + getSiteID());
    }
    
    protected void postProcess(){
    	mylogger.info("stoped listening to " + getSiteID());
    }
    
    

    /**
     * I assume that log4j.xml file is stored in the same folder as this jar if not specified directly - parapath = null 
     */
    private final void initLogging(String parampath) throws FastConnectionException{
  	   	String localPath;
    	if (parampath==null){
    		File currentJavaJarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());   
    		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
    		String currentRootDirectoryPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");
    		localPath = currentRootDirectoryPath;
    	} else{
    		localPath = parampath; 	}
    	
    	localPath+= logFileName;
    	
   		try{
   			File paramFile = new File(localPath);
   			if (!paramFile.exists())
					throw new IOException ("Parameters file <log4j.xml> can't be found in " + localPath);
            DOMConfigurator.configure(localPath);
   		}catch (Exception e){
        	mylogger.error(e);
   			throw new FastConnectionException(e);
   		}  
    }
    
    
	public FPFeed(FPFeedDispatcher dispatcher) {
        /**
        * Initialization part
        */
		this.dispatcher = dispatcher;
		try {
			initLogging(null);
			FPFXmlSettings.getInstance().Init(null);
		
			endpoint = getEndpoint();

			File templateFile = new File(getTemplFileName(getSiteID()));
	        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
	        loader.setLoadTemplateIdFromAuxId(true);
            loader.load(new FileInputStream(templateFile));
	        templateRegistry = loader.getTemplateRegistry();
    		mylogger.info("initialized");
        } catch (Exception e) {
        	mylogger.error("exiting application. ", e);
        	throw new RuntimeException(e);
        } 
    }

	/**
	 * 
	 * @throws FastConnectionException
	 * @throws IOException
	 * 
	 * Starts the process
	 */
    public void start() throws FastConnectionException, IOException {
    	Connection connection = endpoint.connect();
        Context context = new Context();
        context.setTemplateRegistry(templateRegistry);
        MessageInputStream msgInStream = new MessageInputStream(connection.getInputStream(), context);
        setBlockReader();
        msgInStream.setBlockReader(blockReader);
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss Z");
            	Date date = new Date();
            	mylogger.info("application shutdown "+ dt.format(date));
                endpoint.close();
                LogManager.shutdown();
            }
        });

    	mylogger.info("connecting to " + toString());
    	
        while (isProcessing) {
            try {
                startTime = System.nanoTime();            	 
                Message message = msgInStream.readMessage();
                if (message == null) 
                	break;
                else
          		if (!hasStarted()){
           			mylogger.info("connected to " + toString());
           			preProcess();
           			started = true;
           		}
                if (mylogger.isDebugEnabled())
                	mylogger.debug(getSiteID() + ": " + message);
                processMessage(message);
                msgInStream.reset();
                stopTime = System.nanoTime();
            }
            catch(Exception e) {
            	mylogger.error(e);
            	throw new FastConnectionException(e);
            }
        }
        postProcess();
    }
    
	/**
	 * 
	 * 
	 * stop the process closing existing 
	 */
    public void stop() {
    	isProcessing = false;
        endpoint.close();
    }
    
    public void restart() throws FastConnectionException, IOException, InterruptedException {
    	stop();
    	isProcessing = true;
    	Thread.sleep(1000);
        start();
    }
    
    
    
    protected String getTemplFileName(String sitename) throws FastConnectionException{
    	String templfile =  FPFXmlSettings.readConnectionElement(sitename,FPFXmlSettings.TEMPLATE_FILE);
    	if (templfile==null){
    		mylogger.error("template file is missig");
        	throw new FastConnectionException("template file is missig");
    	}
    	return templfile;
   	}
    
    public String toString() {
    	return getSiteID()+" "+ endpoint.toString();
    }

    public abstract void processMessage(Message message);
    
    public abstract Endpoint getEndpoint();
    
    public abstract String getSiteID();
  
    public final void stopProcess() { isProcessing = false;}
    
    public boolean hasStarted() {return started;}    
}

