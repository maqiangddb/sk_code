package com.android.Samkoonhmi.util;

import android.app.Application;
/**
 * Context 工具类， 提供长生命周期的Context
 * @author Administrator
 *
 */

public class ContextUtl extends Application{
	private static ContextUtl instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
	public static ContextUtl getInstance(){
		if (instance == null) {
			instance = new ContextUtl();
		}
		return instance;
	}
}
