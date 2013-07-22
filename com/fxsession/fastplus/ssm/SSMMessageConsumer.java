package com.fxsession.fastplus.ssm;


/**
 * @author Dmitry Vulf
 * Base class for all adapters
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
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.Message;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.log4j.xml.DOMConfigurator;


public class SSMMessageConsumer {
		
    private static Logger mylogger = Logger.getLogger(SSMMessageConsumer.class);
	
    /* 
     * This variable should coincide with connection attribute id value in the settings file
     * e.g.  <connection id="IDF-A">  siteID =="IDF-A"   
     */
    protected String siteID = null; 
    private long startTime;
    private long stopTime;
    
    /** 
    *Each side should be identified in the settings xml by assigning to Attribute id ,e.g.  <connection id="Smth"> 
    * so "Smth" should returned
    **/     
    public String getSiteID() { 
    	return siteID; }
    
    //Signals that main class loop can be stopped - overriden   
    public boolean isProcessing() {
    	return true;
    }
    //returns processing duration in microseconds
    public long getDeltaMcs(){
    	return ((stopTime-startTime)/1000); }
  //returns processing duration in milliseconds
    public long getDeltaMs(){
    	return ((stopTime-startTime)/1000000); }
    
   //connectivity object. Constructs connection calling connect() method
	private final SSMEndpoint endpoint;    
	
    private static TemplateRegistry templateRegistry = null;
    private MessageBlockReader blockReader =  MessageBlockReader.NULL;
    
    private String logFileName = "log4j.xml";
    
    boolean started = false;
    
    public boolean traceFlag = false;
    
    /**
     * THis 2 methods were made specifically to be overridden and made some job in ancestors
     */
    public void preProcess() {};
    public void postProcess() {};

    /**
     * I assume that log4j.xml file is stored in the same folder as this jar if not specified directly - parapath = null 
     */
    private void initLogging(String parampath){
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
    
    
	public SSMMessageConsumer(String id) {
        /**
        * Initialization part
        */
         siteID = id; 
		
		initLogging(null);
		SSMParameters.getInstance().Init(null);
		
        String sitename = getSiteID();

		String port   = SSMConnection.readConnectionElement(sitename,SSMConnection.PORT_N);
    	String group  = SSMConnection.readConnectionElement(sitename,SSMConnection.GROUP_IP);
        String ifaddr = SSMConnection.readConnectionElement(sitename,SSMConnection.INTERFACE_IP);
        String templfile = SSMConnection.readConnectionElement(sitename,SSMConnection.TEMPLATE_FILE);
        
		if (port ==null | group ==null | ifaddr == null | templfile ==null)
		{
        	mylogger.error("Initialize properly connecton settings. Some of current settings are missed. group IP:" +group +
        			" port:" + port + " interface IP:" + ifaddr + " Template file:" + templfile);
            System.exit(-1);
			
		}
		
		endpoint = new SSMEndpoint(Integer.parseInt(port),group,ifaddr);
		
		
        try{
			File templateFile = new File(templfile);
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
    public void start() throws FastConnectionException, IOException {
    	final SSMConnection connection = (SSMConnection) endpoint.connect();
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
                SSMTrace.getInstance().close();
                LogManager.shutdown();
            }
        });
		/**
		 * Initialize trace
		 */
		SSMTrace.getInstance().initDecoded(getSiteID());

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
                stopTime = System.nanoTime();
                SSMTrace.getInstance().traceDecoded(message);  
                processMessage(message);
                msgInStream.reset();
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
    
    /**
     * Logs each message immediately after receipt  
     * Override for another logic 
     */
    public void processMessage(Message message){

            if (mylogger.isDebugEnabled())
            	mylogger.debug(getDeltaMs()+ "ms " +message.toString().length() + "bytes");
    	}
    
    }
    
    

