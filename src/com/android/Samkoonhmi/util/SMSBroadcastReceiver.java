package com.android.Samkoonhmi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;

public class SMSBroadcastReceiver extends BroadcastReceiver{

	private static HashMap<String, ArrayList<SmsCall>> callBacks = new HashMap<String, ArrayList<SmsCall>>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object [] pdus = (Object[]) bundle.get("pdus");
			
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for(int i = 0; i < messages.length; i++){
				byte[] pdu = (byte[])pdus[i];
				messages[i] = SmsMessage.createFromPdu(pdu);
			}
			
			for(SmsMessage msg: messages){
				String smsContent = msg.getMessageBody();
				String senderNum = msg.getDisplayOriginatingAddress();
				
				Iterator<Entry<String, ArrayList<SmsCall>>> iterator = callBacks.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, ArrayList<SmsCall>> entry =  iterator.next();
					ArrayList<SmsCall> calls = entry.getValue();
					
					for(SmsCall call : calls){
						if (call != null) {
							call.onSmsCall(senderNum, smsContent);
						}
					}
					
				}
			}
			
			
			//终止广播
			abortBroadcast();
		}
	}
	
	
	
	public interface IBinder{
		 void onRegister(String key, SmsCall call); // 注册短信 接受短信
		 void onDestroy(String key);  // 注销接受短信
	
	}
	
	public interface SmsCall{
		void onSmsCall(String fromNum, String content);// 短信回调
	}
	
	private final static String mSmsKey ="SMS_KEY";
	private static IBinder binder = new IBinder() {
		
		@Override
		public synchronized void onRegister(String key, SmsCall call) {
			// TODO Auto-generated method stub
			key = (TextUtils.isEmpty(key) ? mSmsKey : key);
			
			if (!callBacks.containsKey(key)) {
				ArrayList<SmsCall> callList = new ArrayList<SMSBroadcastReceiver.SmsCall>();
				callBacks.put(key, callList);
			}
			
			ArrayList<SmsCall> calls = callBacks.get(key);
			calls.add(call);
			
		}
		
		@Override
		public synchronized void onDestroy(String key) {
			// TODO Auto-generated method stub
			ArrayList<SmsCall> calls = callBacks.get(key);
			if (calls != null) {
				while (calls.size() > 0) {
					SmsCall call = calls.remove(0);
					call = null;
				}
				
				calls = null;
			}
		}

	};
	
	public static IBinder getBinder(){
		return binder;
	}

}
