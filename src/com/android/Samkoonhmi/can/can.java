package com.android.Samkoonhmi.can;

import com.android.Samkoonhmi.can.model.bittiming;
import com.android.Samkoonhmi.can.model.canframe;

import android.util.Log;

public class can {
	private static final String TAG="CAN";

	//单例
	private static can sInstance = null;
	public synchronized static can getInstance() {
		if (sInstance == null) {
			sInstance = new can();
		}
		return sInstance;
	}
	
	public int onRestart(int candeviceid){
		return restart(candeviceid);
	}
	
	public int onStart(int candeviceid){
		return start(candeviceid);
	}
	
	public int setBitrate(bittiming bTiming,int candeviceid){
		return setbitrate(bTiming, candeviceid);
	}
	
	public int onSend(canframe frame,int index, int candeviceid){
		Log.i(TAG, "onSend:"+frame.data[0]+","+frame.data[1]+","+frame.data[2]+","+frame.data[3]+","+frame.data[4]+","+frame.data[5]+","+frame.data[6]+","+frame.data[7]);
		return send(frame, index, candeviceid);
	}

	public int onDump(canframe frame, int candeviceid){
		return dump(frame, candeviceid);
	}

	//重启
	private native int restart(int candeviceid);
	//启动
	private native int start(int candeviceid);
	//设置波特率
	private native int setbitrate(bittiming bitTiming, int candeviceid);
	//发送数据
	private native int send(canframe frame, int index, int candeviceid);

	private native int dump(canframe frame, int candeviceid);
	
	private native int addfilter(int id, int mask, int candeviceid);
	
	static {
		System.loadLibrary("can_port");
	}
}
