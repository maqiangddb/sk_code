package com.android.Samkoonhmi.util;

import java.util.Vector;

import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryBroadcast extends BroadcastReceiver{
	private static BatteryBroadcast instance = null;
	private int currentBattery = 0;
	public static BatteryBroadcast getInstance(){
		if(null == instance){
			instance = new BatteryBroadcast();
		}
		return instance;
	}
	private static final String action= "android.intent.action.BATTERY_CHANGED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction() == action){
			int current = intent.getExtras().getInt("level");// 获得当前电量
			int total = intent.getExtras().getInt("scale");// 获得总电量
			int percent = current * 100 / total;
			if(currentBattery != percent){
				currentBattery = percent;
				write16WordAddr(currentBattery,SystemAddress.getInstance().BatteryAddr());
			}
		
		}
	}
	private  void write16WordAddr(int value, AddrProp addrprop) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
		mydataListInt.add(value);
		PlcRegCmnStcTools.setRegIntData(addrprop, mydataListInt, mSendData);
	}
	

}
