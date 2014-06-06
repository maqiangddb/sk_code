package com.android.Samkoonhmi.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class SimStateReceive extends BroadcastReceiver {
	private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
	private final static int SIM_VALID = 0;
	private final static int SIM_INVALID = 1;
	private int simState = SIM_INVALID;

	public int getSimState() {
		return simState;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("sim state changed");
		if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			int state = tm.getSimState();
			switch (state) {
			case TelephonyManager.SIM_STATE_READY://良好
				simState = SIM_VALID;
				break;
			case TelephonyManager.SIM_STATE_UNKNOWN://未知状态
			case TelephonyManager.SIM_STATE_ABSENT: //无卡
			case TelephonyManager.SIM_STATE_PIN_REQUIRED://需要PIN解锁
			case TelephonyManager.SIM_STATE_PUK_REQUIRED://需要PUK解锁
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED://需要NetworkPIN解锁
			default:
				simState = SIM_INVALID;
				break;
			}
		}
		System.out.println("sim 卡的状态 = "+simState);
	}

}
