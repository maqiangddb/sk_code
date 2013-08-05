package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skzip.AKFileUpdate;
import com.android.Samkoonhmi.skzip.SkLoad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 监听版本写入广播
 */
public class SystemBroadcast extends BroadcastReceiver {

	private final static String ACTION_VERSION="com.android.Samkoonhmi.init.version";
	private final static String DOWNLOAD_START="com.android.Samkoonhmi.download.start";
	private final static String DOWNLOAD_STOP="com.android.Samkoonhmi.download.stop";
	private final static String UPDATE_AK="com.android.Samkoonhmi.update.ak";
	private final static String RELEASE="com.android.Samkoonhmi.release";
	private final static String MODEL="com.samkoon.model";//触摸屏型号
	private final static String KEYBOARD = "android.intent.action.KEYBOARD";
	public static  boolean  ISKEYBOARDOPEN = false;
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		mContext=context;
		
		if (ACTION_VERSION.equals(intent.getAction())) {
			SavaInfo.save();
		}else if (DOWNLOAD_START.equals(intent.getAction())) {
			//开始下载
			
			Log.d("SystemBroadcast", "DOWNLOAD_START...");
//			//设置更新状态
			SavaInfo.setState(1);//开始下载，把ak状态设置为NO
			
		
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(
					"information", 0);
			boolean result = sharedPreferences.getBoolean("update_state", false);
		
			//设置更新状态
			SharedPreferences.Editor shareEditor = mContext.getSharedPreferences(
					"information", 0).edit();
			shareEditor.putBoolean("update_state", false);
			shareEditor.commit();
			
			//停止服务
			Intent server=new Intent();
			server.setClass(mContext, AkZipService.class);
			mContext.stopService(server);
			
			if (result) {
				SKSceneManage.getInstance().closeDB();
				SKSceneManage.getInstance().destroy();
			}
					
		}else if (DOWNLOAD_STOP.equals(intent.getAction())) {
			//下载完成
			Log.d("SKScene", "loadown stop...");
			//updateState(false);
		}else if(intent.getAction().equals(UPDATE_AK)){
			//U盘 更新主态
			Log.d("SKScene", "update ak...");
			//SkLoad.getInstance().update_from_release(context);
			//AKFileUpdate.getInstance(mContext).update(1);
			//SavaInfo.setState(2);//更新完毕，把ak状态设置为YES
			
			Intent intents=new Intent();
			intents.setClass(mContext, AkZipService.class);
			intents.putExtra("update", "true");
			mContext.startService(intents);
			
		}else if (intent.getAction().equals(MODEL)) {
			//触摸屏型号
		}else if (intent.getAction().equals(RELEASE)) {
			//更新主态
			//release();
		}else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
			//日期改变
			//Log.d("SKScene", "ACTION_DATE_CHANGED....");
			AlarmGroup.getInstance().dateChange();
		}else if(intent.getAction().equals(KEYBOARD)){
			String param = intent.getStringExtra("status");
			//Log.d("Number", "键盘状态通知=="+param);
			if(param.equals("on"))
			{
				ISKEYBOARDOPEN = true;
				//键盘打开
				if (SKTimer.getInstance().getBinder()
						.isRegister(SKSceneManage.getInstance().sCallback)) {
				//	Log.d("Number", "键盘打开 屏保定时器开启了 注销定时器=="+param);
					SKTimer.getInstance().getBinder()
							.onDestroy(SKSceneManage.getInstance().sCallback);
				}
			}else if (param.equals("off")){
				//键盘关闭
				//Log.d("Number", "键盘关闭 判断定时器是否有打开=="+param);
				ISKEYBOARDOPEN = false;
				SKSceneManage.getInstance().timeOut();
				
			}
			
		}
	}
	
	/**
	 * 更新下载状态
	 */
	private void updateState(boolean state){
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences(
				"information", 0).edit();
		shareEditor.putBoolean("update", state);
		shareEditor.commit();
	}
	
}
