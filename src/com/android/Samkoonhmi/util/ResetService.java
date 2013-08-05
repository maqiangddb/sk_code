package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.activity.LoginActivity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ResetService extends Service{

	private static final int HANDLER_START=1;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		handler.sendEmptyMessageDelayed(HANDLER_START, 500);
	}

	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_START:
				//重启程序
				Intent startMain = new Intent(ResetService.this,LoginActivity.class);
			    startMain.addCategory(Intent.CATEGORY_HOME);
			    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(startMain);
		        onDestroy();
				break;
			}
		}
		
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
