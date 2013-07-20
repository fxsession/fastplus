package com.fxsession.fastplus.receiver.moex;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.session.FastConnectionException;
import com.fxsession.fastplus.ssm.SSMMessageConsumer;
/**
 * @author Dmitry Vulf
 * 
 * implements IDF logics
 *
 */
import com.fxsession.fastplus.ssm.SSMMessageConsumer;


public class MoexIDF extends SSMMessageConsumer{
	
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
	
	
	
	private static Logger mylogger = Logger.getLogger(SSMMessageConsumer.class);
	
	@Override
	public void processMessage(Message message){
		String outp; 
		if (message.getTemplate().getId().equals(TEMPLATE_ID)){
			String encodeCharset = message.getString(MESSAGE_ENCODING);
			outp= message.getString(SYMBOL) + new String("		");
			outp+= message.getString(SECURITY_DESC) +			new String("			");
			outp+= message.getString(ENCODED_SECURITY_DESC) + 	new String("			");
			outp+= message.getString(ENCODED_SHORT_SEC_DESC) + 	new String("			");
/*			
			outp+= message.getString(ROUND_LOT) + new String("		");
			outp+= message.getString(TRADING_SESSION_ID) + new String("		");
			outp+= message.getString(SECURITY_TRADE_STAT) + new String("		");
			outp+= message.getString(SECURITY_TRADE_STAT) + new String("		");
*/			
			outp+= message.getString(PRICE_MVM_LIMIT)+ new String("		");
			outp+= message.getString(MIN_PRICE_INC)+ new String("		");
			outp+= message.getString(FACE_VAL) +  new String("		");
			outp+= message.getString(BASE_SWAP_PX);
			
			mylogger.info(outp);
		}
		else if (message.getTemplate().getId().equals("2008")){ //change later to class definition
			mylogger.info("Heartbeat " + super.getDeltaMs() + "ms");
		}else {
			mylogger.info("Can't identify the template");
		}
			
	}
	
	@Override
	 public void preProcess() {
		//Print header
	}

}
