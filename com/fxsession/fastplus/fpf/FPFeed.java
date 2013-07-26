package com.fxsession.fastplus.fpf;

/**
 * @author Dmitry Vulf
 * Abstract class for all feeds
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
import org.openfast.error.FastException;
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
	
    /* 
     * This variable should coincide with connection attribute id value in the settings file
     * e.g.  <connection id="IDF-A">  siteID =="IDF-A"   
     */
    protected String siteID = null; 
    private long startTime;
    private long stopTime;

    //connectivity object. Constructs connection calling connect() method
 	private final Endpoint endpoint;
 	
 	//Dispatcher
 	protected final FPFeedDispatcher dispatcher;

 	
 	
    private static TemplateRegistry templateRegistry = null;
    private MessageBlockReader blockReader =  MessageBlockReader.NULL;
     
    private String logFileName = "log4j.xml";
     
    private boolean started = false;
    
    
    //Signals that main class loop can be stopped - overriden   
    protected boolean isProcessing() {
    	return true;
    }
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
   	 	mylogger.info("Started listening to " + getSiteID());
    }
    
    protected void postProcess(){
    	mylogger.info("Stoped listening to " + getSiteID());
    }
    
    

    /**
     * I assume that log4j.xml file is stored in the same folder as this jar if not specified directly - parapath = null 
     */
    private final void initLogging(String parampath){
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
					throw new IOException ("Paramters file <log4j.xml> can't be found in " + localPath);
            DOMConfigurator.configure(localPath);
   		}catch (Exception e){
   			System.out.println("Exiting application. " + e.getMessage());
   			System.exit(-1);
   		}  
    }
    
    
	public FPFeed(FPFeedDispatcher dispatcher) {
        /**
        * Initialization part
        */
		
		this.dispatcher = dispatcher; 
		initLogging(null);
		FPFXmlSettings.getInstance().Init(null);
		
		endpoint = getEndpoint();
		
		
        try{
			File templateFile = new File(getTemplFileName(getSiteID()));
	        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
	        loader.setLoadTemplateIdFromAuxId(true);
            loader.load(new FileInputStream(templateFile));
	        templateRegistry = loader.getTemplateRegistry();
    		mylogger.info("Initialized");
        } catch (Exception e) {
        	mylogger.error("Exiting application. ", e);
            System.exit(-1);   //”¡–¿“‹ !!!!!
        } 
    }

    	

	/**
	 * 
	 * @throws FastConnectionException
	 * @throws IOException
	 * 
	 * Starts the process
	 */
    public final void start() throws FastConnectionException, IOException {
    	final Connection connection = endpoint.connect();
        Context context = new Context();
        context.setTemplateRegistry(templateRegistry);
        MessageInputStream msgInStream = new MessageInputStream(connection.getInputStream(), context);
    
        msgInStream.setBlockReader(blockReader);
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss Z");
            	Date date = new Date();
            	mylogger.info("Application shutdown "+ dt.format(date));
                endpoint.close();
                FPFTrace.getInstance().close();
                LogManager.shutdown();
            }
        });
		/**
		 * Initialize trace
		 */
		FPFTrace.getInstance().initDecoded(getSiteID());

    	mylogger.info("Wait.Connecting ... ");
    	
        while (isProcessing()) {
            try {
                startTime = System.nanoTime();            	 
                Message message = msgInStream.readMessage();
                if (message == null) 
                	break;
                else
          		if (!started){
           			mylogger.info("Started!");
           			preProcess();
           			started = true;
           		}
                FPFTrace.getInstance().traceDecoded(message);  
                processMessage(message);
                msgInStream.reset();
                stopTime = System.nanoTime();
            }
            catch(final FastException e) {
            	mylogger.error(e);
            }
        }
        postProcess();
    }
    
    
    public void setBlockReader(MessageBlockReader messageBlockReader) {
        this.blockReader = messageBlockReader;
    }
    
    protected String getTemplFileName(String sitename) {
    	String templfile =  FPFXmlSettings.readConnectionElement(sitename,FPFXmlSettings.TEMPLATE_FILE);
    	if (templfile==null)
    		mylogger.error("Template file is missig");
    	return templfile;
   	}

    /**
     * Perform logic over the message  
     */
    public abstract void processMessage(Message message);    
    
    public abstract Endpoint getEndpoint();
    
    /** 
    * Each side should be identified in the settings xml by assigning to Attribute id ,e.g.  <connection id="Smth"> 
    * so "Smth" should returned. Can't be overriden
    **/     
    public abstract String getSiteID(); 
    
}

