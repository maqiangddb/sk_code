package com.android.Samkoonhmi.vnc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.android.Samkoonhmi.system.address.SystemAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
/**
 * VNC连接状态广播
 * @author Administrator
 *
 */

public class VNCConnectBroadCast extends BroadcastReceiver{
	public static final String VNC_CONNECT_CHANGE_ACTION = "com.samkoon.vnc.CONNECT_CHANGE";

	public static final String EXTRA_DATA = "com.samkoon.vnc.DATA";
	public static final String KEY_HOST = "com.samkoon.vnc.HOST";
	public static final String KEY_CONNECTED = "com.samkoon.vnc.CONNECTED";
	public static Map<String, Boolean> connectSB = new HashMap<String, Boolean>();//连接设备ip跟状态


	@Override
	public void onReceive(Context context, Intent receive) {
		// TODO Auto-generated method stub
		
        if (receive.getAction().equals(VNC_CONNECT_CHANGE_ACTION)) {
        	Log.d("VNC", "vnc VNC_CONNECT_CHANGE_ACTION broadcast"); 
            Bundle data = receive.getBundleExtra(EXTRA_DATA);
            String host = data.getString(KEY_HOST);
            boolean connected = data.getBoolean(KEY_CONNECTED);
            connectSB.clear();
            connectSB.put(host, connected);
         
        }
        Set<String> keySet = connectSB.keySet(); 
       for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
		String key = (String) iterator.next();
		boolean value = connectSB.get(key);
		Log.d("VNC","VNC connect IP："+key+",Connect State ："+value+",write to inner addr");
		//VNCUtil.getInstance().setVNCIpToAddress(key);
		if(value)
		{
			VNCUtil.getInstance().setBit(SystemAddress.getInstance().IsVncConnectedAddr(), 1);
		}else{
			VNCUtil.getInstance().setBit(SystemAddress.getInstance().IsVncConnectedAddr(), 0);
		}
		
		
	}
	}

}
