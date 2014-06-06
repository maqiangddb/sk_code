package com.android.Samkoonhmi.vnc;


import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.network.PhoneManager;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.SystemParam;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class WIFIAPConnectBroadcast extends BroadcastReceiver {
	private static final String WIFI_SEND_HOSTAP = "com.samkoon.send.wifiap";
          
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("VNC", " 收到wifi host 的广播");
		if (intent.getAction() == WIFI_SEND_HOSTAP) {
			try {
				boolean b = intent.getBooleanExtra("wifiap", false);
				Log.d("VNC", " wifiap =" + b);
				SharedPreferences sharedPreferences = SKSceneManage.getInstance().mContext
						.getSharedPreferences("information", 0);
				if (b && (SystemParam.LONG_INSPECT & SystemInfo.getnSetBoolParam()) == SystemParam.LONG_INSPECT) {
					boolean reulst = sharedPreferences
							.getBoolean("vnc_state", true);
					if (reulst) {
						SKToast.makeText(context, R.string.apopen,Toast.LENGTH_SHORT).show();
						VNCUtil.getInstance().startVNC(context, SystemInfo.getnMonitorPort()+"",
								SystemInfo.getStrMonitor());
					}
				}
				if (b) {
					PhoneManager.getInstance().readAllIp();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("WIFIAPConnectBroadcast", "AK WIFIAPConnectBroadcast error !!");
			}
			
		}
	}

}
