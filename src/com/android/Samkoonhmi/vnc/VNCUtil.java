package com.android.Samkoonhmi.vnc;

import java.util.Vector;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.SystemParam;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class VNCUtil {
	// 单例
	private static VNCUtil sInstance = null;

	public synchronized static VNCUtil getInstance() {
		if (sInstance == null) {
			sInstance = new VNCUtil();
		}
		return sInstance;
	}

	// 打开服务
	public static final String START_VNC_ACTION = "com.samkoon.vnc.START";
	// 关闭服务
	public static final String STOP_VNC_ACTION = "com.samkoon.vnc.STOP";

	public static final String EXTRA_SETTINGS_DATA = "com.samkoon.vnc.SETTINGS";
	public static final String KEY_PASSWORD = "com.samkoon.vnc.PASSWORD";
	public static final String KEY_PORT = "com.samkoon.vnc.PORT";
	public static final String KEY_SHARE = "com.samkoon.vnc.SHARE";
	// 网络连接状态
	public static boolean netState = false;
	// 请求当前连接状态广播
	public static final String REQUST_STATE_ACTION = "com.samkoon.vnc.REQUEST_STATE";
	private static final String WIFI_READ_HOSTAP="com.samkoon.get.wifiap";
	public static boolean hostIsOpen = false;


	/**
	 * 启动服务
	 * 
	 * @param context
	 * @param port
	 * @param password
	 */
	public void startVNC(Context context, String port, String password) {
		Intent send = new Intent(START_VNC_ACTION);
		Bundle settings = new Bundle();
		settings.putString(KEY_PORT, port);
		Log.d("VNC","VNC password =="+password);
		settings.putString(KEY_PASSWORD, password);
		//如果选择了允许多人访问
		if((SystemParam.VNC_CONNECT_MUCH & SystemInfo.getnSetBoolParam()) == SystemParam.VNC_CONNECT_MUCH){
			settings.putBoolean(KEY_SHARE, true);
		}else{
			settings.putBoolean(KEY_SHARE, false);
		}
		send.putExtra(EXTRA_SETTINGS_DATA, settings);
		if(null != context){
			context.sendBroadcast(send);
		}
	}

	/**
	 * 关闭服务
	 * 
	 * @param context
	 */
	public void stopVNC(Context context) {
		// 服务已经启动 发送关闭广播
		Log.d("VNC","关闭VNC-------------");
		Intent send = new Intent(STOP_VNC_ACTION);
		if(context!= null){
			context.sendBroadcast(send);
		}
//		requestState(context);

	}

	public void start(Context context) {
		
		// 判断网络状态是否可用
		boolean bNet = isNetworkAvailable(context);
		Log.d("VNC","Network ==：" + bNet);
		if (!bNet) {
			SKToast.makeText(context, R.string.networkisunable,Toast.LENGTH_SHORT).show();
			//如果网络不可用，则去请求host是否已经打开
			requestWifiApState(context);
			return;
		}else{
			Log.d("VNC","  启动VNC---------  ------------");
			startVNC(context, SystemInfo.getnMonitorPort()+"", SystemInfo.getStrMonitor());
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.i("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Log.i("NetWorkState", "Availabel"+info[i]);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 请求当前连接状态广播
	 */
	public void requestState(Context context) {
		Intent send = new Intent(REQUST_STATE_ACTION);
		context.sendBroadcast(send);

	}
/**
 * 请求host是否打开
 * @param context
 */
	public void requestWifiApState(Context context){
		Log.d("VNC", "请求wifi host 的广播--------- ------");
		Intent send = new Intent(WIFI_READ_HOSTAP);
		context.sendBroadcast(send);
	}
	/**
	 * 写入位地址
	 * 
	 * @param prop
	 */
	public void setBit(AddrProp prop, int value) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		mSendData.eDataType = DATA_TYPE.BIT_1;
		mydataListInt.add(value);
		PlcRegCmnStcTools.setRegIntData(prop, mydataListInt, mSendData);
	}

	/**
	 * 连接屏的PC Ip地址写入到地址
	 */
	public void setVNCIpToAddress(String pcIp) {
		if (null != pcIp && !"".equals(pcIp)) {

			String[] mIp = pcIp.split("\\.");
			if (mIp != null && mIp.length == 4) {
				SystemVariable.getInstance().write16WordAddr(
						Integer.valueOf(mIp[0]),
						SystemAddress.getInstance().VNC_Ip1Addr());
				SystemVariable.getInstance().write16WordAddr(
						Integer.valueOf(mIp[1]),
						SystemAddress.getInstance().VNC_Ip2Addr());
				SystemVariable.getInstance().write16WordAddr(
						Integer.valueOf(mIp[2]),
						SystemAddress.getInstance().VNC_Ip3Addr());
				SystemVariable.getInstance().write16WordAddr(
						Integer.valueOf(mIp[3]),
						SystemAddress.getInstance().VNC_Ip4Addr());
			}

		}
	}
	/**
	 * vnc端口写入地址
	 * @param port
	 */
	public void setVNCPORToAddr(int port){
		SystemVariable.getInstance().write16WordAddr(port, SystemAddress.getInstance().VNC_PORTAddr());
	}
	private int STARTFLAG = 1;
   Handler myHandler = new Handler(){
	   public void handleMessage(Message msg) {
		   
		   if(msg.what ==STARTFLAG )
		   {
			   requestState(SKSceneManage.getInstance().mContext);
		   }
	   };
   };
}
