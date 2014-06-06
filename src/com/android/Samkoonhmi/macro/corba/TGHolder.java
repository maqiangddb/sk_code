package com.android.Samkoonhmi.macro.corba;

import java.util.ArrayList;
import com.android.Samkoonhmi.macro.SmsReceiver;
import com.android.Samkoonhmi.network.PhoneManager;
import com.android.Samkoonhmi.util.SMSBroadcastReceiver;
import com.android.Samkoonhmi.util.SMSBroadcastReceiver.IBinder;

/**
 * 3G操作接口
 */
public class TGHolder extends PHolder{
	
	private TGSmsCall mSmsCall;
	public TGHolder(){
		//
	}
		
	/**
	 * 设置短信回调接口
	 */
	public void setmSmsCall(TGSmsCall call) {
		if (call==null) {
			return;
		}
		IBinder binder=SMSBroadcastReceiver.getBinder();
		if (binder!=null) {
			binder.onRegister(calls);
		}
		this.mSmsCall = call;
	}
	
	/**
	 * 获取短信
	 * String[0]-内容
	 * String[1]-发送人号码
	 */
	public String[] getSms(){
		return SmsReceiver.getInstance().getSms();
	}
	
	/**
	 * 获取短信
	 * String[0]-内容
	 * String[1]-发送人号码
	 * @param clearMsg-删除缓存信息
	 */
	public String[] getSms(boolean clearMsg){
		return SmsReceiver.getInstance().getSms(clearMsg);
	}
	
	/**
	 * 获取所有短信
	 */
	public ArrayList<String[]> getSmsList(){
		return SmsReceiver.getInstance().getSmsList();
	}
	
	/**
	 * 获取所有短信
	 * @param clearMsg-删除缓存信息
	 */
	public ArrayList<String[]> getSmsList(boolean clearMsg){
		return SmsReceiver.getInstance().getSmsList(clearMsg);
	}
	
	
	/**
	 * 短信发送
	 * @param sms-短信内容
	 * @param num-接收号码
	 */
	public void sendSms(String sms,String num){
		PhoneManager.getInstance().sendMSM(num, sms);
	}
	
	
	
	/**
	 * 接收短信内容
	 */
	SMSBroadcastReceiver.SmsCall calls=new SMSBroadcastReceiver.SmsCall() {
		
		@Override
		public void onSmsCall(String fromNum, String content) {
			// TODO 自动生成的方法存根
			if (mSmsCall!=null) {
				mSmsCall.onReceive(content, fromNum);
			}
		}
	};
	
	/**
	 * 短信回调接口
	 */
	public interface TGSmsCall {

		/**
		 * 短信接收
		 * @param sms-短信内容
		 * @param num-短信号码
		 */
		void onReceive(String sms,String num);
	}
}
