package com.android.Samkoonhmi.util;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadcastReceiver {

	private static final String TAG = "SMSBroadcastReceiver";
	private static ArrayList<SmsCall> callBacks = new ArrayList<SmsCall>();
	private static Object[] pduArray = null;

	public static void sms(Context context, Intent intent) {

		pduArray = (Object[]) intent.getExtras().get("pdus");
		if (pduArray==null||pduArray.length==0) {
			return;
		}
		
		SmsMessage[] messages = new SmsMessage[pduArray.length];
		for (int i = 0; i < pduArray.length; i++) {
			messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
		}
		
		for (SmsMessage cur : messages) {
			for (int i = 0; i < callBacks.size(); i++) {
				SmsCall call=callBacks.get(i);
				if (call != null) {
					Log.d(TAG, "call sms=" + cur.getDisplayMessageBody()
							+ ",num=" + cur.getDisplayOriginatingAddress());
					call.onSmsCall(cur.getDisplayOriginatingAddress(),
							cur.getDisplayMessageBody());
				}
				
			}
		}

	}

	public interface IBinder {
		boolean onRegister(SmsCall call); // 注册短信 接受短信

		void onDestroy(SmsCall call); // 注销接受短信

	}

	public interface SmsCall {
		void onSmsCall(String fromNum, String content);// 短信回调
	}

	private static IBinder binder = new IBinder() {

		@Override
		public boolean onRegister(SmsCall call) {
			// TODO Auto-generated method stub

			if (call==null) {
				return false;
			}
			remove(call);
			callBacks.add(call);
			return true;
		}

		@Override
		public void onDestroy(SmsCall call) {
			// TODO Auto-generated method stub
		}

	};

	public static IBinder getBinder() {
		return binder;
	}

	private static void remove(SmsCall call){
		for (int i = 0; i < callBacks.size(); i++) {
			if (callBacks.get(i)==call) {
				callBacks.remove(i);
				break;
			}
		}
	}
}
