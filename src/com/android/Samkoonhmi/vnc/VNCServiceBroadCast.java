package com.android.Samkoonhmi.vnc;

import com.android.Samkoonhmi.system.address.SystemAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * VNC服务状态广播
 * 
 * @author Administrator
 * 
 */
public class VNCServiceBroadCast extends BroadcastReceiver {
	public static final String VNC_STATE_CHANGE_ACTION = "com.samkoon.vnc.STATE_CHANGE";

	public static final String EXTRA_STATE = "com.samkoon.vnc.STATE";
	public static final int STATE_STARTED = 0;
	public static final int STATE_STOPED = 1;
	public static boolean VNCSERVICESTATE = false;
	public static final String EXTRA_IP = "com.samkoon.vnc.IP";
	public static final String EXTRA_HTTP_PORT = "com.samkoon.vnc.HTTP_PORT";

	@Override
	public void onReceive(Context context, Intent receive) {
		if (receive.getAction().equals(VNC_STATE_CHANGE_ACTION)) {
			String ip = receive.getStringExtra(EXTRA_IP);
			String httpPort = receive.getStringExtra(EXTRA_HTTP_PORT);
			int state = receive.getIntExtra(EXTRA_STATE, STATE_STOPED);
			switch (state) {
			case STATE_STARTED:
				// 启动
				VNCSERVICESTATE = true;
				break;
			case STATE_STOPED:
				// 停止
				VNCSERVICESTATE = false;
				break;
			default:
				VNCSERVICESTATE = false;
				break;
			}
			Log.d("VNC","VNC service state =" + VNCSERVICESTATE+",Write to inner Addr"+ip);
			if(VNCSERVICESTATE)
			{
				VNCUtil.getInstance().setBit(SystemAddress.getInstance().IsVncOnAddr(), 1);
				if(null != httpPort && !"".equals(httpPort)){
					int port = Integer.parseInt(httpPort);
					VNCUtil.getInstance().setVNCPORToAddr(port);
				}
				if(null != ip && !"".equals(ip)){
					VNCUtil.getInstance().setVNCIpToAddress(ip);
				}
				
			}else{
				VNCUtil.getInstance().setBit(SystemAddress.getInstance().IsVncOnAddr(), 0);
			}
			
		}
		
	}

}
