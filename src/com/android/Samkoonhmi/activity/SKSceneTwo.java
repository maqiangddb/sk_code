package com.android.Samkoonhmi.activity;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTrendsThread;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.ISKSceneUpdate;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 画面二
 */
public class SKSceneTwo extends Activity implements ISKSceneUpdate{

	private static final String TAG="SKScene";
	public static boolean destroy;
	private boolean onResume;
	public static boolean update;
	private LinearLayout layout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		SKSceneManage.getInstance().setActivity(this);
		onResume=true;
		
		LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout=(LinearLayout)inflate.inflate(R.layout.two_layout,null);
		setContentView(layout);

		Log.d(TAG, "two onCreate...");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (onResume) {
			if (update) {
				update=false;
				SKThread.getInstance();
				SKTrendsThread.getInstance();
				SKPlcNoticThread.getInstance().start();
				MacroManager.getInstance(this.getApplicationContext());
				SKSceneManage.getInstance().setiSceneUpdate(this,SHOW_TYPE.DEFAULT);
				SKSceneManage.getInstance().loadView(this,SKSceneTwo.this,SHOW_TYPE.DEFAULT);
				Log.d(TAG, "two update....");
			}else{
				//系统通知更新，例如3g启动，会杀死所有activity,然后通知所有更新
				SKSceneManage.getInstance().refreshScreen();
				Log.d(TAG, "sytem update two....");
			}
			onResume=false;
			destroy=true;
			SKProgress.onResume=true;
			//Log.d(TAG, "two onResume...");
		}else {
			//从第三方软件，返回ak界面
			SKSceneManage.getInstance().refreshScreen();
			Log.d(TAG, "onResume......");
		}
	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (layout!=null) {
			layout.removeAllViews();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
			// 应用程序退出，不是画面切换的退出
			SKSceneManage.getInstance().destroy();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		return false;
	}

	@Override
	public void onUpdateView(SKScene scene) {
		if(scene!=null){
			if (layout!=null) {
				if(scene.getParent() != null){
					Log.d(TAG, "prient..."+scene);
					
					ViewGroup group=(ViewGroup)scene.getParent();
					if (group!=null) {
						group.removeAllViewsInLayout();
					}
				}
				layout.addView(scene);
			}
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.dispatchKeyEvent(event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void onChange(int in, int out,int type) {
		destroy=false;
		onResume=true;
		
		SKSceneOne.update=true;
		Intent intent=new Intent();
		intent.setClass(this, SKSceneOne.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		if (type==1) {
			overridePendingTransition(in, out);
		}
		
		SKSceneTwo.this.finish();
	}

}
