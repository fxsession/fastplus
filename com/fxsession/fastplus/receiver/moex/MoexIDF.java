package com.fxsession.fastplus.receiver.moex;




import java.util.HashMap;




import org.apache.log4j.Logger;
import org.openfast.Message;
import com.fxsession.fastplus.ssm.SSMMessageConsumer;
/**
 * @author Dmitry Vulf
 * 
 * implements IDF(Instruments definition) logic
 *
 */



public class MoexIDF extends SSMMessageConsumer{

	private static Logger mylogger = Logger.getLogger(MoexIDF.class);
	private static boolean finished = false;
	private static int packageCounter = 0;
	private static boolean heartBeat = false; 
	
	//Settings for the Instrument definition site (IDF)
		
	static private final String TEMPLATE_ID = "2005";
	
	

	static public final String SYMBOL = "Symbol";
	static public final String ENCODED_SHORT_SEC_DESC = "EncodedShortSecurityDesc";
	static public final String SECURITY_TRADE_STAT = "SecurityTradingStatus";
	static public final String MIN_PRICE_INC = "MinPriceIncrement";
	static public final String FACE_VAL = "FaceValue";
	static public final String BASE_SWAP_PX = "BaseSwapPx"; 
	
	private HashMap<String, String[]> instruments = new HashMap<String, String[]>(); 	
	
	public MoexIDF(String id) {
		super(id);
	}
	
	@Override
	public void preProcess() {
		 mylogger.info("Started listening to " + getSiteID()); 
	};

	@Override
	public void postProcess() {
		mylogger.info("Stoped listening to " + getSiteID());
		if (mylogger.isDebugEnabled()){
			mylogger.debug("Recorded " + instruments.size() + " entries");
		}
	};

	@Override	
    public boolean isProcessing() {
    	return (!finished);
    }
	
	
	@Override
	public void processMessage(Message message) {
        String p1,p2,p3,p4,p5;   
			if (message.getTemplate().getId().equals(TEMPLATE_ID)){
                p1 = message.getString(SYMBOL);
				p2 = message.getString(ENCODED_SHORT_SEC_DESC);
				p3 = message.getString(MIN_PRICE_INC);
				p4 = message.getString(FACE_VAL);
				p5 = message.getString(BASE_SWAP_PX);
				instruments.put(p1, new String[] {p2,p3,p4,p5});
				
				if (mylogger.isDebugEnabled()){   
					//can significantly slow down execution
					mylogger.debug(SYMBOL + p1 + ENCODED_SHORT_SEC_DESC + p2 + MIN_PRICE_INC +  p3 + FACE_VAL + p4 +BASE_SWAP_PX +p5);
				}
				
				if (heartBeat){
					//first entry after heartbeat package
					packageCounter++;
				}
				heartBeat = false;
			}
			else {
				//heartbeat 
					if (mylogger.isDebugEnabled()){				 
						mylogger.debug("Heartbeat");
					}
					if (packageCounter!=0 && heartBeat){
						//the point is to get 1 full message package to make sure that we got full instrument list and then exit
						finished = true;
					}
				heartBeat = true;
			}
	}
}
