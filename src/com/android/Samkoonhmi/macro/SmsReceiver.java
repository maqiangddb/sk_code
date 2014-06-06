package com.android.Samkoonhmi.macro;

import java.util.ArrayList;

import android.util.Log;

import com.android.Samkoonhmi.util.SMSBroadcastReceiver;
import com.android.Samkoonhmi.util.SMSBroadcastReceiver.IBinder;

public class SmsReceiver {

	private static final String TAG="SmsReceiver";
	//信息,号码
	private String[] mSmsMsg=new String[]{"",""};
	private ArrayList<String[]> mList=new ArrayList<String[]>();
		
	// 单例
	private static SmsReceiver sInstance = null;
	public synchronized static SmsReceiver getInstance() {
		if (sInstance == null) {
			sInstance = new SmsReceiver();
		}
		return sInstance;
	}
	
	
	private boolean isRegister=true;
	public void onRegister(){
		if (!isRegister) {
			return;
		}
		IBinder binder=SMSBroadcastReceiver.getBinder();
		if (binder!=null) {
			binder.onRegister(calls);
			isRegister=false;
		}
		Log.d(TAG, "ak sms onRegister ...");
	}
	
	/**
	 * 获取短信信息
	 */
	public String[] getSms(){
		return mSmsMsg;
	}
	
	/**
	 * 获取短信信息
	 * @param clearMsg-删除缓存信息
	 */
	public String[] getSms(boolean clearMsg){
		if (clearMsg) {
			String[] temp=new String[]{"",""};
			temp[0]=mSmsMsg[0];
			temp[1]=mSmsMsg[1];
			mSmsMsg[0]=null;
			mSmsMsg[1]=null;
			return temp;
		}else {
			return mSmsMsg;
		}
	}
	
	/**
	 * 获取所以短信
	 */
	public ArrayList<String[]> getSmsList(){
		return mList;
	}
	
	/**
	 * 获取所以短信
	 * @param clearMsg-删除缓存信息
	 */
	public ArrayList<String[]> getSmsList(boolean clearMsg){
		if (clearMsg) {
			ArrayList<String[]> temp=new ArrayList<String[]>();
			for (int i = 0; i < mList.size(); i++) {
				temp.add(mList.get(i));
			}
			mList.clear();
			return temp;
		}else {
			return mList;
		}
		
	}
	
	/**
	 * 接收短信内容
	 */
	SMSBroadcastReceiver.SmsCall calls=new SMSBroadcastReceiver.SmsCall() {
		
		@Override
		public void onSmsCall(String fromNum, String content) {
			// TODO 自动生成的方法存根
			
			//Log.d(TAG, "onSmsCall fromNum="+fromNum+",content="+content);
			
			//防止出现 +86、17951、12593 等等开头的手机号码
			if (fromNum!=null) {
				if (fromNum.length() > 11) {
					fromNum = fromNum.substring(fromNum.length() - 11);
				}
			}
			
			mSmsMsg[0]=content;
			mSmsMsg[1]=fromNum;
			
			String []temp=new String[]{content,fromNum};
			mList.add(temp);
		}
	};
}
