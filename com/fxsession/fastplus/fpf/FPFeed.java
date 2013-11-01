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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.MessageInputStream;
import org.openfast.session.Connection;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.fxsession.utils.FXPException;
import com.fxsession.utils.FXPXml;

public abstract class FPFeed implements IFPFeed {

	private static Logger mylogger = Logger.getLogger(FPFeed.class);

	private long startTime;
	private long stopTime;

	// session
	private final Endpoint endpoint;
	private final Context context = new Context();

	// Dispatcher
	protected final FPFeedDispatcher dispatcher;

	private static TemplateRegistry templateRegistry = null;

	protected MessageBlockReader blockReader = MessageBlockReader.NULL;

	private String logFileName = "log4j.xml";

	private boolean started = false;

	// Signals that main class loop can be stopped - overriden
	protected boolean isProcessing = true;

	// returns processing duration in microseconds
	protected final long getDeltaMcs() {

		return ((stopTime - startTime) / 1000);
	}

	// returns processing duration in milliseconds
	protected final long getDeltaMs() {

		return ((stopTime - startTime) / 1000000);
	}

	/**
	 * Pre and post processing
	 */
	protected void preProcess() {

		mylogger.info("started listening to " + getSiteID());
	}

	protected void postProcess() {

		mylogger.info("stoped listening to " + getSiteID());
	}

	/*
	 * Initialization part
	 */

	/*
	 * I assume that log4j.xml file is stored in the same folder as this jar
	 */
	private final void initLogging() throws FastConnectionException {

		try {
			File currentJavaJarFile = new File(getClass().getProtectionDomain()
					.getCodeSource().getLocation().getPath());
			String currentJavaJarFilePath = currentJavaJarFile
					.getAbsolutePath();
			String currentRootDirectoryPath = currentJavaJarFilePath.replace(
					currentJavaJarFile.getName(), "");
			String localPath = currentRootDirectoryPath;

			localPath += logFileName;

			File paramFile = new File(localPath);
			if (!paramFile.exists())
				throw new IOException("Can't find " + localPath);
			else {
				// found. configure it
				DOMConfigurator.configure(localPath);
				if (mylogger.isDebugEnabled())
					mylogger.debug("started logging");
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(-1);
		}
	}

	/*
	 * Init specific openfast staff
	 */
	private final void initOpenFastContent() {

		File templateFile;
		try {
			// read <templateFileName> parameter
			templateFile = new File(getTemplFileName(getSiteID()));
			XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
			loader.setLoadTemplateIdFromAuxId(true);
			loader.load(new FileInputStream(templateFile));
			templateRegistry = loader.getTemplateRegistry();
			context.setTemplateRegistry(templateRegistry);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Simply adds gentle exit
	 */
	private final void initInternals() {

		setBlockReader();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				SimpleDateFormat dt = new SimpleDateFormat(
						"yyyy-mm-dd HH:mm:ss Z");
				Date date = new Date();
				mylogger.info("application shutdown " + dt.format(date));
				endpoint.close();
				LogManager.shutdown();
			}
		});
	}

	public FPFeed(FPFeedDispatcher dispatcher) {

		// Initialization part
		this.dispatcher = dispatcher;
		try {
			initLogging();
			// init openfast content
			endpoint = getEndpoint();

			initOpenFastContent();

			initInternals();

			mylogger.info("initialized");
		} catch (Exception e) {
			mylogger.error("exiting application. ", e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * 
	 * @throws FastConnectionException
	 * 
	 * @throws IOException
	 * 
	 * Starts the process
	 */
	public void start() throws FastConnectionException, IOException {
		Connection connection = endpoint.connect();
		MessageInputStream msgInStream = new MessageInputStream(
				connection.getInputStream(), context);

		msgInStream.setBlockReader(blockReader);

		mylogger.info("connecting to " + toString());

		while (isProcessing) {
			try {
				startTime = System.nanoTime();
				Message message = msgInStream.readMessage();
				if (message == null)
					break;
				else if (!hasStarted()) {
					mylogger.info("connected to " + toString());
					preProcess();
					started = true;
				}
				// message raw out output
				if (mylogger.isDebugEnabled())
					mylogger.debug(getSiteID() + ": " + message);
				processMessage(message);
				msgInStream.reset();
				stopTime = System.nanoTime();
			} catch (Exception e) {
				mylogger.error(e);
				throw new FastConnectionException(e);
			}
		}
		postProcess();

		msgInStream.close();
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

	public void restart() throws FastConnectionException, IOException,

	InterruptedException {
		isProcessing = true;
		start();
	}

	protected String getTemplFileName(String sitename)
			throws FastConnectionException, ParserConfigurationException,
			FXPException {

		String templfile = FXPXml.readConnectionElement(sitename,
				FXPXml.TEMPLATE_FILE);
		if (templfile == null) {
			mylogger.error("template file is missig");
			throw new FastConnectionException("template file is missig");
		}
		return templfile;
	}

	public String toString() {
		return getSiteID() + " " + endpoint.toString();
	}

	public abstract void processMessage(Message message)
			throws FastConnectionException;

	public abstract Endpoint getEndpoint() throws FastConnectionException;

	public abstract String getSiteID();

	public final void stopProcess() {
		isProcessing = false;
	}

	public boolean hasStarted() {
		return started;
	}
}