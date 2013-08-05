package com.android.Samkoonhmi.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.SystemParam;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TimeBroadCast  {
	public static TimeBroadCast broadCast;
	private Context mContext;
	private boolean isFiler;

	public static TimeBroadCast getInstance(Context context)
	{
		if(null == broadCast)
		{
			broadCast = new TimeBroadCast(context);
		}
		
		return broadCast;
	}
	
	public TimeBroadCast(Context context){
		isFiler=false;
		mContext=context.getApplicationContext();
	}
	
	public void addFiler(){
		if(mContext!=null){
			isFiler=true;
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(Intent.ACTION_DATE_CHANGED);
			mFilter.addAction(Intent.ACTION_TIME_TICK); // 更新分钟的广播
			mContext.registerReceiver(mReceiver, mFilter);// 注册广播
		}
	}
	
	public void remove(){
		if(null != mContext){
			if(null != mReceiver && isFiler){
				isFiler=false;
				mContext.unregisterReceiver(mReceiver);
			}
		}
		
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
//			Log.d("broad", "--------------分钟广播---------"); 
			//如果启动了时效
			if((SystemInfo.getnSetBoolParam() & SystemParam.HMI_PROTECT) == SystemParam.HMI_PROTECT)
			{
				if (SystemInfo.isbProtectType() == false) {
					// 一天写一次使用天数
					SharedPreferences sharedPreferences = SKSceneManage
							.getInstance().mContext.getSharedPreferences(
							"hmiprotct", 0);
					SharedPreferences.Editor shareEditor = SKSceneManage
							.getInstance().mContext.getSharedPreferences(
							"hmiprotct", 0).edit();
					String dateTime = sharedPreferences.getString("dateTime", null);
					int dateNumber = sharedPreferences.getInt("dateNumber", -1);
					Date date = new Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
					String writeDate = format.format(date);

					shareEditor.putString("dateTime", writeDate);
					if (writeDate.equals(dateTime)) {
						shareEditor.putInt("dateNumber", dateNumber);
					} else {
						shareEditor.putInt("dateNumber", (dateNumber + 1));
					}
					shareEditor.commit();
				}
				boolean bool = ParameterSet.getInstance().outTimeUse(
						SKSceneManage.getInstance().mContext);
				if (bool) {
					// 超出了使用时间
					// 跳转进入密保页面 1秒钟之后跳转
					outtimeHandler.sendEmptyMessageAtTime(111, 2000);

				}
			}
			
		}
		
	};
	
	private Handler outtimeHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			if(msg.what == 111)
				Log.d("SKscene", "111......");
				SKSceneManage.getInstance().turnToOutTime();
		};
	};
}
