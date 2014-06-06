package com.android.Samkoonhmi.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;

import android.R.integer;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.renderscript.Type;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;

/**
 * 3G 信号管理
 */
public class PhoneManager {

	// 单例
	private static PhoneManager sInstance = null;

	public synchronized static PhoneManager getInstance() {
		if (sInstance == null) {
			sInstance = new PhoneManager();
		}
		return sInstance;
	}

	private TelephonyManager mTelephonyManager;
	private Context mContext;
	private WifiManager wifi_service =null; 
	private static final String TAG="PhoneManager";
	public void onStart(Context context) {
		mContext=context;
		mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
						| PhoneStateListener.LISTEN_DATA_ACTIVITY
						| PhoneStateListener.LISTEN_CELL_LOCATION
						| PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
						| PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
		
		wifi_service = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
		
		mContext.registerReceiver(sendMessageReceiver, new IntentFilter(SENT_SMS_ACTION));
		
		//先获取本机ip
		wifiEnabled=false;
		wifiUpdateState();
	}
	
	
	private final String SENT_SMS_ACTION = "com.samkoon.send.sms";
	private boolean bReset;
	public void sendMSM() {

		bReset=true;
		String message = SystemInfo.getsSmsMsg();
		String phoneNumber = SystemInfo.getsTgNum();
		
		if (SystemInfo.getsTgNum()==null||SystemInfo.getsTgNum().equals("")||SystemInfo.getsTgNum().length()!=11) {
			return;
		}
		
		if (phoneNumber!=null) {
			phoneNumber=phoneNumber.trim();
		}
		
		if (message!=null) {
			message=message.trim();
		}

		Log.d(TAG, "phoneNumber="+phoneNumber);
		Log.d(TAG, "message="+message);
		
		sendMSM(phoneNumber, message,true);
		
	}
	
	
	/**
	 * 给多个手机发送短信
	 * @param list  发送短信的手机号码
	 * @param message  信息
	 */
	public void sendMSMs(ArrayList<String> list, String message){
		if (list == null || list.size() == 0  || TextUtils.isEmpty(message)) {
			return ;
		}
		
		if (list.size() == 1) {
			String phoneNum = list.get(0);
			sendMSM(phoneNum,message,true); //发送失败， 进行再次发送
		}
		else {
			for(String phoneNum : list){
				sendMSM(phoneNum, message, false); // 发送失败，不进行再次发送
			}
		}
		
		
	}
	
	/**
	 * 给一个手机发送短信
	 * @param phoneNum  手机号码
	 * @param message   短信内容
	 */
	public void sendMSM(String phoneNum, String message){
		sendMSM(phoneNum,message,true); //发送失败， 进行再次发送
	}
	
	/**
	 * 给一个手机发送短信
	 * @param phoneNum  手机号码
	 * @param message   短信内容
	 * @param isFirst   true：如果发送失败了，会重新发送一次； false： 只发送一次
	 */
	private void sendMSM(String phoneNum, String message, boolean isFirst) {
		
		//判断短信是否发送成功  复位
		SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().isSmsSend());
		
		if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(message)) {
			return ;
		}
		
		//防止出现 +86、17951、12593 等等开头的手机号码
		if (phoneNum.length() > 11) {
			phoneNum = phoneNum.substring(phoneNum.length() - 11);
		}
		
		if (!SystemVariable.isMobileNO(phoneNum)) {
			return;
		}
		
		if (message!=null) {
			message=message.trim();
		}

		Intent mIntent = new Intent(SENT_SMS_ACTION);
		mIntent.putExtra("sendMSM", isFirst);
		mIntent.putExtra("sendMSM_NUM", phoneNum);
		mIntent.putExtra("sendMSM_MSG", message);
		PendingIntent mPi = PendingIntent.getBroadcast(mContext, 0, new Intent(SENT_SMS_ACTION), 0);
		
		SmsManager sms = SmsManager.getDefault();
		if (message.length() > 70) {
			ArrayList<String> msgs = sms.divideMessage(message);
			for (String msg : msgs) {
				sms.sendTextMessage(phoneNum, null, msg+"", mPi, null);
			}
		} else {
			sms.sendTextMessage(phoneNum, null, message+"", mPi, null);
		}
	}
	
	private BroadcastReceiver sendMessageReceiver = new BroadcastReceiver() {  

	    @Override  
	    public void onReceive(Context context, Intent intent) {  

	        //判断短信是否发送成功  
	    	if (getResultCode() == Activity.RESULT_OK) { // 发送成功
	    		SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().isSmsSend());
	    		if (bReset) {
	    			bReset=false;
		    		//自动复位
			    	SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().TriSms());
				}
	    	}
	    	else {
	    		if (intent.getBooleanExtra("sendMSM", false)) { //第一次发送失败， 再次发送
	    			String phoneNUM =  intent.getStringExtra("sendMSM_NUM");
	    			String phoneMess = intent.getStringExtra("sendMSM_MSG");
	    			sendMSM(phoneNUM, phoneMess, false);
				}
	    		else {  //第二次发送失败，停止发送
	    			SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().isSmsSend());
				}
			}
	    }  

	    };

	/**
	 * wifi状态更新
	 */
	public void wifiUpdateState(){
		wifiSetting();
		wifiRssi();
	}
	
	private int ip=0;
	private int signal=0;
	private boolean wifiEnabled;
	private void wifiSetting(){
		
		if (mContext==null) {
			return ;
		}
		
		//WifiManager wifimanage=(WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);//获取WifiManager  
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
		wifiEnabled=mWifi.isConnected();
		if (wifiEnabled) {
			
			if(wifi_service==null||wifi_service.getConnectionInfo()==null){
				SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().wifiStatus());
				return;
			}
			
			SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().wifiStatus());
			int ips=wifi_service.getConnectionInfo().getIpAddress();
			if (ip!=ips) {
				//
				ip=ips;
				int[] mIp=intToIp(ip);
				SystemVariable.getInstance().write16WordAddr(mIp[0], SystemAddress.getInstance().wifiIp1());
				SystemVariable.getInstance().write16WordAddr(mIp[1], SystemAddress.getInstance().wifiIp2());
				SystemVariable.getInstance().write16WordAddr(mIp[2], SystemAddress.getInstance().wifiIp3());
				SystemVariable.getInstance().write16WordAddr(mIp[3], SystemAddress.getInstance().wifiIp4());
			}
			
		}
		
	}
	
	public int nCount=0;
	private void getLocalIp(){
		HashMap<Integer, String> map= getLocalIpAddress();
		
		if (map==null) {
			return;
		}
		
		if (map.containsKey(0)) {
			//wifiap
			String ip=map.get(0);
			//Log.d(TAG, "ak wifiap ip="+ip);
			if (ip!=null) {
				wifiRssi();
				setLocalIpToAddress(ip);
				//SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().wifiStatus());
			}
		}
		
		if (map.containsKey(1)) {
			//以太网
			String ip=map.get(1);
			//Log.d(TAG, "ak eth ip="+ip);
			if (ip!=null) {
				String [] mIp = ip.split("\\.");
				if(mIp != null && mIp.length == 4){
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[0]), SystemAddress.getInstance().eth_ip1());
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[1]), SystemAddress.getInstance().eth_ip2());
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[2]), SystemAddress.getInstance().eth_ip3());
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[3]), SystemAddress.getInstance().eth_ip4());
				}
			}
		}
		
		if (map.containsKey(2)) {
			//3g
			String ip=map.get(2);
			//Log.d(TAG, "ak 3g ip="+ip);
			if (ip!=null) {
				String [] mIp = ip.split("\\.");
				if(mIp != null && mIp.length == 4){
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[0]), SystemAddress.getInstance().tg_ip1());
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[1]), SystemAddress.getInstance().tg_ip2());
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[2]), SystemAddress.getInstance().tg_ip3());
					SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[3]), SystemAddress.getInstance().tg_ip4());
				}
			}
		}
	}
	
	/**
	 * 重新读取ip
	 */
	public void readAllIp(){
		nCount=0;
	}
	
	/**
	 * 获取ip
	 */
	public void getAllIp(){
		if (nCount<12) {
			//Log.d(TAG, "nCount="+nCount);
			nCount++;
			getLocalIp();
		}
	}
	
	/**
	 * wifi信号
	 */
	private void wifiRssi(){
		
		if(wifi_service==null||wifi_service.getConnectionInfo()==null){
			SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().wifiStatus());
			SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().wifiSignal());
			return;
		}
		
		if (!wifiEnabled) {
			//未连接
			SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().wifiStatus());
			SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().wifiSignal());
			return;
		}else{
			SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().wifiStatus());
		}
		
		int signal=wifi_service.getConnectionInfo().getRssi();
		if(signal<-70){
			SystemVariable.getInstance().write16WordAddr(1, SystemAddress.getInstance().wifiSignal());
		}else if (signal<50) {
			SystemVariable.getInstance().write16WordAddr(2, SystemAddress.getInstance().wifiSignal());
		}else {
			SystemVariable.getInstance().write16WordAddr(3, SystemAddress.getInstance().wifiSignal());
		}
	}
	
	/**
	 * 本机IP
	 */
	private void setLocalIpToAddress(String localIp){
		if(null != localIp && !"".equals(localIp)){
			
			String [] mIp = localIp.split("\\.");
			if(mIp != null && mIp.length == 4){
				SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[0]), SystemAddress.getInstance().wifiIp1());
				SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[1]), SystemAddress.getInstance().wifiIp2());
				SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[2]), SystemAddress.getInstance().wifiIp3());
				SystemVariable.getInstance().write16WordAddr(Integer.valueOf(mIp[3]), SystemAddress.getInstance().wifiIp4());
			}
			
		}
	}

	private int[] intToIp(int i)  {
		int []temp=new int[4];
		temp[0]=i & 0xFF;
		temp[1]=(i >> 8 ) & 0xFF;
		temp[2]=(i >> 16 ) & 0xFF;
		temp[3]=(i >> 24 ) & 0xFF;
		Log.d(TAG, "ak ip:"+temp[0]+"."+temp[1]+"."+temp[2]+"."+temp[3]);
		return temp;
	}  
	
	/**
	 * 更新3g状态
	 */
	public void updatePhoneState(Context context){
		 mContext=context;
		 ConnectivityManager connMgr = (ConnectivityManager)  
				                      context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		 NetworkInfo  networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		 if(networkInfo != null){
			 boolean connect = networkInfo.isAvailable();
			 if (connect) {
				updateDataState();
				setSpType();
				setTgSignal(0);
			}else {
				//未知
				if (sType==null||sType.equals("")||sType.equals("未知")) {
					setTgSignal(-113);
					SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().SpType());
				}
			}
		 }
	}
	
	/**
	 * 3g 状态变化
	 */
	PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

		@Override
		public void onDataConnectionStateChanged(int state) {
			updateDataState();
			setSpType();
			Log.d(TAG, "onDataConnectionStateChanged......");
		}

		@Override
		public void onDataActivity(int direction) {
			// updateDataStats2();
			//Log.d(TAG, "onDataActivity......");
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			// updateLocation(location);
			updateDataState();
			setSpType();
			Log.d(TAG, "onCellLocationChanged......");
		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			// mMwiValue = mwi;
			// updateMessageWaiting();
			Log.d(TAG, "onMessageWaitingIndicatorChanged......");
		}

		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi) {
			// mCfiValue = cfi;
			// updateCallRedirect();
			Log.d(TAG, "onCallForwardingIndicatorChanged......");
		}
		
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
			// 3g信息强度

			int dbm=0;
			if (signalStrength.isGsm()) {
				// signalStrength.getEvdoDbm();
				int type=mTelephonyManager.getNetworkType();
				if (type==TelephonyManager.NETWORK_TYPE_EVDO_0
						||type==TelephonyManager.NETWORK_TYPE_EVDO_A
						||type==TelephonyManager.NETWORK_TYPE_EVDO_B) {
					
					dbm=signalStrength.getEvdoDbm();
					
				}else {
					dbm=signalStrength.getGsmSignalStrength();
				}
				
			} else {
				dbm=signalStrength.getCdmaDbm();
			}
			// int signalAsu = signalStrength.
			setTgSignal(dbm);
			
			Log.d(TAG, "onSignalStrengthsChanged dbm="+dbm);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			// TODO Auto-generated method stub
			super.onServiceStateChanged(serviceState);
			Log.d(TAG, "onServiceStateChanged .......");
		}

		@Override
		public void onSignalStrengthChanged(int asu) {
			// TODO Auto-generated method stub
			super.onSignalStrengthChanged(asu);
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			// TODO Auto-generated method stub
			super.onDataConnectionStateChanged(state, networkType);
			Log.d(TAG, "onDataConnectionStateChanged .......");
		}
		
		
	};
	
	private int nDbm=0;
	private void setTgSignal(int dbm){
		if (dbm==nDbm) {
			return;
		}
		int value=1;
		nDbm=dbm;
		int index=0;
		if (dbm<=-113) {
			index=0;
			value=0;
		}else if (dbm<-111) {
			index=1;
		}else if (dbm<-53) {
			index=2;
		}else {
			index=4;
		}
		
		SystemVariable.getInstance().writeBitAddr(value, SystemAddress.getInstance().TgStatus());
		SystemVariable.getInstance().write16WordAddr(index, SystemAddress.getInstance().TgSignal());
	
		//设置3gip
		readAllIp();
		
	}

	/**
	 * 运营商
	 */
	private String sType="";
	private void setSpType(){
		if (mTelephonyManager==null) {
			Context context=SKSceneManage.getInstance().mContext;
			if (context!=null) {
				onStart(context);
			}
		}
		
		if (mTelephonyManager!=null) {
			
			String type=mTelephonyManager.getSimOperator();
			sType=type;
			if (type==null||type.equals("")) {
				//未知
				SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().SpType());
			}else {
				if (type.equals("46000")||type.equals("46002")||type.equals("46007")) {
					//中国移动
					SystemVariable.getInstance().write16WordAddr(2, SystemAddress.getInstance().SpType());
				}else if (type.equals("46001")||type.equals("46006")) {
					//中国联通
					SystemVariable.getInstance().write16WordAddr(1, SystemAddress.getInstance().SpType());
				}else if (type.equals("46003")||type.equals("46005")) {
					//中国电信
					SystemVariable.getInstance().write16WordAddr(3, SystemAddress.getInstance().SpType());
				}else if (type.equals("46020")){
					//中国铁通
					SystemVariable.getInstance().write16WordAddr(4, SystemAddress.getInstance().SpType());
				}else {
					//未知
					SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().SpType());
				}
				//setTgSignal(-10);
			}
			Log.d("PhoneManager", "3g type:"+type);
		}
	}
	
	/**
	 * 3g 连接状态
	 */
	private void updateDataState() {
		if (mTelephonyManager==null) {
			Context context=SKSceneManage.getInstance().mContext;
			if (context!=null) {
				onStart(context);
			}
		}
		
		if(mTelephonyManager!=null){
			int state = mTelephonyManager.getDataState();

			switch (state) {
			case TelephonyManager.DATA_CONNECTED:
				//display = "已连接";
				setTgSignal(20);
				break;
			case TelephonyManager.DATA_CONNECTING:
				//display = "正在连接";
				setTgSignal(20);
				break;
			case TelephonyManager.DATA_DISCONNECTED:
				//display = "已断开";
				setTgSignal(-120);
				break;
			case TelephonyManager.DATA_SUSPENDED:
				//display = "已暂停";
				setTgSignal(-120);
				break;
			}
		}
		
		//Log.d("PhoneManager", "......display:"+display);
	}

	/**
	 * 获取当前IP地址
	 * @throws UnknownHostException 
	 */
	public HashMap<Integer, String> getLocalIpAddress() {  
		
		HashMap<Integer, String> map=new HashMap<Integer, String>();
		try {
			String ipv4;
			List<NetworkInterface> nilist = null;
			try {
				if (NetworkInterface.getNetworkInterfaces() == null) {
					return null;
				}

				nilist = Collections.list(NetworkInterface.getNetworkInterfaces());

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "ak getLocalIpAddress error!!!");
				return null;
			}

			if (nilist == null || nilist.size() == 0) {
				return null;
			}

			for (NetworkInterface ni : nilist) {
				String name = ni.getName();
				//Log.d(TAG, "......name=" + name);
				boolean reulst = false;
				int type=0;
				if (name.equals("wlan0")) {
					reulst = true;
					type=0;
				}else if (name.equals("eth0")) {
					reulst = true;
					type=1;
				}else if (name.equals("ppp0")) {
					reulst = true;
					type=2;
				}

				if (reulst) {
					List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
					for (InetAddress address : ialist) {
						ipv4 = address.getHostAddress();
						//Log.d(TAG, "ipv4=" + ipv4);
						if (ipv4 != null&&!ipv4.equals("")) {
							if (InetAddressUtils.isIPv4Address(ipv4)) {
								map.put(type, ipv4);
							}
						}
					}
				}
			}

		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
		}
		return map;
    } 
	
	public void setIMSI(){
		if(null != mTelephonyManager){
			String imsi = mTelephonyManager.getSubscriberId();
			if(null != imsi){
				if(SystemVariable.getInstance().getImsi()==null){
					SystemVariable.getInstance().writeStringAddr(imsi, SystemAddress.getInstance().imsi());
					SystemVariable.getInstance().setImsi(imsi);
				}else if(!SystemVariable.getInstance().getImsi().equals(imsi)){
					SystemVariable.getInstance().writeStringAddr(imsi, SystemAddress.getInstance().imsi());
					SystemVariable.getInstance().setImsi(imsi);
				}
			}
		}
	}
	
	public void setTgPhoneNum(){
		if(null != mTelephonyManager){
			String num = mTelephonyManager.getLine1Number();
			if(null != num){
				if(SystemVariable.getInstance().getLocalPhoneNum()==null){
					SystemVariable.getInstance().writeStringAddr(num, SystemAddress.getInstance().TG_PhoneNum());
					SystemVariable.getInstance().setLocalPhoneNum(num);
				}else if(!SystemVariable.getInstance().getLocalPhoneNum().equals(num)){
					SystemVariable.getInstance().writeStringAddr(num, SystemAddress.getInstance().TG_PhoneNum());
					SystemVariable.getInstance().setLocalPhoneNum(num);
				}
			}
		}
	}
}
