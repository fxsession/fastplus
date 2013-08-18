package com.fxsession.fastplus.fpf;

import java.util.HashMap;
import java.util.Map;



/**
 * @author Dmitry Vulf
 * 
 * Message class 
 *
 */
public class FPFMessage implements IFPField{
	
	private static Map<Integer,String> templateFields = new HashMap<Integer, String>();
	
	static {
		templateFields.put(HEARTBEAT,"HB");
		templateFields.put(SYMBOL,"Symbol");
		templateFields.put(MSGSEQNUM,"MsgSeqNum");
		templateFields.put(GROUPMDENTRIES, "GroupMDEntries");
		templateFields.put(MDENTRYID,"MDEntryID");
		templateFields.put(MDENTRYPX, "MDEntryPx");
		templateFields.put(MDENTRYSIZE,"MDEntrySize");
		templateFields.put(MDENTRYTYPE,"MDEntryType");
		templateFields.put(MDUPDATEACTION,"MDUpdateAction");
		templateFields.put(RPTSEQ, "RptSeq");
	}
		
	private Map <Integer,String> messageFields = new HashMap<Integer, String>();
	
	/*
	 * A keyField which uniquely defines this message 
	 */
	private final Integer keyField;
	
	public static String getFieldName(Integer key) {return templateFields.get(key);}
	
	public void putFieldValue(Integer key, String value){
		if (templateFields.containsKey(key))
			messageFields.put(key, value);
	}

	public String getFieldValue(Integer key){
		if (messageFields.containsKey(key)){
			return messageFields.get(key);
		}
		else
			throw new RuntimeException("Invalid key field"); 
	}
	
	public String getKeyFieldValue(){
		return getFieldValue(keyField);
	}
	
	public FPFMessage(Integer keyF) {
		this.keyField = keyF;
		putFieldValue(HEARTBEAT,"HB");
	}
}
