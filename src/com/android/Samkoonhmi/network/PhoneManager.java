package com.android.Samkoonhmi.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
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
	private boolean isWifi;
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
		
		
	}
	
	
	private final String SENT_SMS_ACTION = "com.samkoon.send.sms";
	//private static final String DELIVERED_SMS_ACTION = "com.samkoon.delivered.sms";
	public void sendMSM() {

		if (SystemInfo.getsTgNum()==null||SystemInfo.getsTgNum().equals("")||SystemInfo.getsTgNum().length()!=11) {
			return;
		}
	
		String message = SystemInfo.getsSmsMsg();
		String phoneNumber = SystemInfo.getsTgNum();
		
		Log.d("SKScene", "sendMSM:"+message);
		Log.d("SKScene", "sendNum:"+phoneNumber);
		
		if (message!=null) {
			message=message.trim();
		}

		SmsManager sms = SmsManager.getDefault();
		PendingIntent mPi = PendingIntent.getBroadcast(mContext, 0, new Intent(), 0);
		

		// 如果短信内容超过70个字符 将这条短信拆成多条短信发送出去
		//Log.d("AKPhone", "send:" + message);
		
		if (message.length() > 70) {
			ArrayList<String> msgs = sms.divideMessage(message);
			for (String msg : msgs) {
				sms.sendTextMessage(phoneNumber, null, msg+"", mPi, null);
			}
		} else {
			sms.sendTextMessage(phoneNumber, null, message+"", mPi, null);
		}
		
	}
	
	public void sendMSM(String phoneNum, String message, boolean isFirst) {
		
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
	    	if (getResultCode() == Activity.RESULT_OK) {
	    		SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().isSmsSend());
			}
	    	else {
	    		if (intent.getBooleanExtra("sendMSM", false)) {
	    			String phoneNUM =  intent.getStringExtra("sendMSM_NUM");
	    			String phoneMess = intent.getStringExtra("sendMSM_MSG");
	    			sendMSM(phoneNUM, phoneMess, false);
				}
	    		else {
	    			SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().isSmsSend());
				}
			}
	    }  

	    };

	
	private int ip=0;
	private int signal=0;
	private boolean wifiEnabled;
	public void wifiSetting(){
		
		if (mContext==null) {
			return ;
		}
	
		WifiManager wifimanage=(WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);//获取WifiManager  
		
		if (!wifimanage.isWifiEnabled()) {
			if (wifiEnabled) {
				SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().wifiSignal());
				wifiEnabled=true;
			}else{
				String localIp = getLocalIpAddress();
				setLocalIpToAddress(localIp);
				wifiEnabled=false;
			}
		}else{
			String localIp = getLocalIpAddress();
			
			setLocalIpToAddress(localIp);
			wifiEnabled=false;
			
		}
		
		
		if(wifi_service==null||wifi_service.getConnectionInfo()==null){
			SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().wifiSignal());
			return;
		}
		
		int ips=wifi_service.getConnectionInfo().getIpAddress();
		int signal=wifi_service.getConnectionInfo().getRssi();
		if (ip!=ips) {
			//
			ip=ips;
			int[] mIp=intToIp(ip);
			SystemVariable.getInstance().write16WordAddr(mIp[0], SystemAddress.getInstance().wifiIp1());
			SystemVariable.getInstance().write16WordAddr(mIp[1], SystemAddress.getInstance().wifiIp2());
			SystemVariable.getInstance().write16WordAddr(mIp[2], SystemAddress.getInstance().wifiIp3());
			SystemVariable.getInstance().write16WordAddr(mIp[3], SystemAddress.getInstance().wifiIp4());
		}
		
		if(signal<-70){
			SystemVariable.getInstance().write16WordAddr(1, SystemAddress.getInstance().wifiSignal());
		}else if (signal<50) {
			SystemVariable.getInstance().write16WordAddr(2, SystemAddress.getInstance().wifiSignal());
		}else {
			SystemVariable.getInstance().write16WordAddr(3, SystemAddress.getInstance().wifiSignal());
		}
	}
	
	private void setLocalIpToAddress(String localIp)
	{
		if(null != localIp && !"".equals(localIp))
		{
			
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
	 * 3g 状态变化
	 */
	PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

		@Override
		public void onDataConnectionStateChanged(int state) {
			updateDataState();
			setSpType();
			// updateDataStats();
			// updatePdpList();
			// updateNetworkType();
		}

		@Override
		public void onDataActivity(int direction) {
			// updateDataStats2();
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			// updateLocation(location);
			setSpType();
		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			// mMwiValue = mwi;
			// updateMessageWaiting();
		}

		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi) {
			// mCfiValue = cfi;
			// updateCallRedirect();
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
		}

	};
	
	private int nDbm=0;
	private void setTgSignal(int dbm){
		if (dbm==nDbm) {
			return;
		}
		nDbm=dbm;
		int index=0;
		if (dbm<=-113) {
			index=0;
		}else if (dbm<-111) {
			index=1;
		}else if (dbm<-53) {
			index=2;
		}else {
			index=4;
		}
		SystemVariable.getInstance().write16WordAddr(index, SystemAddress.getInstance().TgSignal());
	}

	/**
	 * 运营商
	 */
	private String sType="";
	private void setSpType(){
		String type=mTelephonyManager.getSimOperator();
		
		if (type!=null) {
			if (type.equals(sType)) {
				return;
			}
			sType=type;
		}
		
		if (type==null||type.equals("")) {
			//未知
			SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().SpType());
		}else {
			if (type.equals("46000")||type.equals("46002")) {
				//中国移动
				SystemVariable.getInstance().write16WordAddr(2, SystemAddress.getInstance().SpType());
			}else if (type.equals("46001")) {
				//中国联通
				SystemVariable.getInstance().write16WordAddr(1, SystemAddress.getInstance().SpType());
			}else if (type.equals("46003")) {
				//中国电信
				SystemVariable.getInstance().write16WordAddr(3, SystemAddress.getInstance().SpType());
			}else {
				//未知
				SystemVariable.getInstance().write16WordAddr(0, SystemAddress.getInstance().SpType());
			}
		}
		Log.d("PhoneManager", "3g type:"+type);
	}
	
	/**
	 * 3g 连接状态
	 */
	private void updateDataState() {
		int state = mTelephonyManager.getDataState();
		//Resources r = getResources();
		String display = "未知";

		switch (state) {
		case TelephonyManager.DATA_CONNECTED:
			display = "已连接";
			setTgSignal(20);
			break;
		case TelephonyManager.DATA_CONNECTING:
			display = "正在连接";
			setTgSignal(20);
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			display = "已断开";
			setTgSignal(-120);
			break;
		case TelephonyManager.DATA_SUSPENDED:
			display = "已暂停";
			setTgSignal(-120);
			break;
		}
		
		//Log.d("PhoneManager", "......display:"+display);
	}

	/**
	 * 获取当前IP地址
	 * @throws UnknownHostException 
	 */
	public String getLocalIpAddress() {  
        try {  
            String ipv4;  
          
            List<NetworkInterface>  nilist=null;
            try {
            	if (NetworkInterface.getNetworkInterfaces()==null) {
    				return null;
    			}
            	
            	nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
            	
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "ak getLocalIpAddress error!!!");
				return null;
			}
            
            
          
            if (nilist==null||nilist.size()==0) {
				return null;
			}
            
            for (NetworkInterface ni: nilist)   
            {  
                List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
                for (InetAddress address: ialist){  
                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
                    {   
                        return ipv4;  
                    }  
                }  
   
            }  
   
        } catch (Exception ex) {  
            Log.e(TAG, ex.toString());  
        }  
        return null;  
    } 
	
}
