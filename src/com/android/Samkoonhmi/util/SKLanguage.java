package com.android.Samkoonhmi.util;
import java.util.Vector;

import android.util.Log;

import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.system.SystemVariable;

/**
 * 语言改变广播类
 * @author 刘伟江
 * @version v 1.0.0.1
 * 创建时间 2012-6-6
 */
public class SKLanguage {

	private static int nLanId=0;
	private Vector<ICallback> callbacks;
	private SystemInfoBiz sysBiz;
	// 单例
	private static SKLanguage sInstance = null;
	public synchronized static SKLanguage getInstance() {
		if (sInstance == null) {
			sInstance = new SKLanguage();
		}
		return sInstance;
	}
	
	public SKLanguage(){
		callbacks=new Vector<SKLanguage.ICallback>();
	}
	
	SKLanguage.IBinder binder=new IBinder() {
		
		@Override
		public void onRegister(ICallback callback) {
			if (SystemInfo.getLanguageNumber()<2) {
				//没有多语言
				return;
			}
			if (callbacks!=null) {
				//deleteRegister(callback);
				callbacks.add(callback);
			}
		}
		
		@Override
		public void onDestroy(ICallback callback) {
			if (SystemInfo.getLanguageNumber()<2) {
				//没有多语言
				return;
			}
			if (callbacks!=null) {
				callbacks.remove(callback);
			}
		}

		@Override
		public void onChange(int languageId) {
			if (languageId!=SystemInfo.getCurrentLanguageId()) {
				SystemInfo.setCurrentLanguageId(languageId);
				if(null == sysBiz)
				{
					sysBiz = new SystemInfoBiz();
				}
				//将当前语言写入数据库
				sysBiz.updateCurrentLanguage(languageId);
				//当前语言写入到内部寄存器地址
				SystemVariable.getInstance().setSystemLanugageToAddr();
				nLanId=languageId;
				
				Thread mThread=new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						if (callbacks!=null) {
							for (int i = 0; i < callbacks.size(); i++) {
								callbacks.get(i).onLanguageChange(nLanId);
							}
						}
						DBTool.getInstance().getmAlarmBiz().setLanguageId(nLanId);
					}
					
				};
				
				mThread.start();
				
			}
		}
	};
	
	/**
	 * 删除相同的注册接口
	 */
	private void deleteRegister(ICallback callback){
		for (int i = 0; i < callbacks.size(); i++) {
			if (callbacks.get(i)==callback) {
				callbacks.remove(callback);
			}
		}
		
	}
	
	/**
	 * 删除绑定
	 */
	public void destory(){
		if (callbacks!=null) {
			callbacks.clear();
		}
	}
	
	public SKLanguage.IBinder getBinder() {
		return binder;
	}
	
	/**
	 * 语言改变回调接口
	 */
	public interface ICallback{
		/**
		 * @param languageId-语言Id
		 */
		void onLanguageChange(int languageId);
	}
	
	/**
	 * 绑定需要语言改变的控件
	 */
	public interface IBinder{
		//注册
		void onRegister(ICallback callback);
		//移除
		void onDestroy(ICallback callback);
		//语言改变
		void onChange(int languageId);
	}
}
