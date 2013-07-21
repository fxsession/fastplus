package com.fxsession.fastplus.receiver.moex;




import org.apache.log4j.Logger;
import org.openfast.Message;
import com.fxsession.fastplus.ssm.SSMMessageConsumer;
/**
 * @author Dmitry Vulf
 * 
 * implements IDF logics
 *
 */



public class MoexIDF extends SSMMessageConsumer{
	
	//Settings for the Instrument definition site (IDF)
	
	static public final String IDF_A = "IDF-A";
	
	static public final String TEMPLATE_ID = "2005";
	static public final String MESSAGE_ENCODING = "MessageEncoding";
	static public final String SYMBOL = "Symbol";
	static public final String SECURITY_DESC = "SecurityDesc";
	static public final String ENCODED_SECURITY_DESC = "EncodedSecurityDesc";
	static public final String ENCODED_SHORT_SEC_DESC = "EncodedShortSecurityDesc";
	static public final String ROUND_LOT = "RoundLot";
	static public final String TRADING_SESSION_ID = "TradingSessionID";
	static public final String SECURITY_TRADE_STAT = "SecurityTradingStatus";
	static public final String MIN_PRICE_INC = "MinPriceIncrement";
	static public final String FACE_VAL = "FaceValue";
	static public final String BASE_SWAP_PX = "BaseSwapPx"; 
	static public final String PRICE_MVM_LIMIT = "PriceMvmLimit"; 
	//Settings for the Instrument definition site (IDF)
	
	
	private static Logger mylogger = Logger.getLogger(SSMMessageConsumer.class);
	
	
	@Override
    public String getSiteID() { 
    	return IDF_A; } 

	@Override
	public void processMessage(Message message) {
			if (message.getTemplate().getId().equals(TEMPLATE_ID)){
				String outp;
				outp= message.getString(SYMBOL) + new String("(Symbol)	");
				outp+= message.getString(SECURITY_DESC) +			new String("(SecurityDesc)			");
				outp+= message.getString(ENCODED_SHORT_SEC_DESC) + 	new String("(EncodedShortSecurityDesc)		");
				outp+= message.getString(PRICE_MVM_LIMIT)+ new String("(PriceMvmLimit)	");
				outp+= message.getString(MIN_PRICE_INC)+ new String("(MinPriceIncrement)	");
				outp+= message.getString(FACE_VAL) +  new String("(FaceValue)	");
				outp+= message.getString(BASE_SWAP_PX) + new String ("(BaseSwapPx)");
			
				mylogger.info(outp);
			}
			else if (message.getTemplate().getId().equals("2008")){ //change later to class definition
				mylogger.info("Heartbeat " + super.getDeltaMs() + "ms");
			}else {
				mylogger.info("Can't identify the template");
			}
			
	}
	

}
