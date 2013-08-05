package com.android.Samkoonhmi.system;

import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKThread.ICallback;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.LockInfoBiz;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.model.LockInfo;
import com.android.Samkoonhmi.model.PassWordInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.SYSTEM_OPER_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.SystemParam;
import com.android.Samkoonhmi.util.TASK;

public class SystemControl {
	public static String LOCK="Samkoon.sys.lock";
	public static String UNLOCK="Samkoon.sys.unlock";
//	private static String LockTaskName="";
	
	/**
	 * 锁屏/解锁操作回调
	 */
//	private static ICallback LockCallback=new ICallback(){
//
//		@Override
//		public void onUpdate(String msg, int taskId) {
//			
//		}
//
//		@Override
//		public void onUpdate(int msg, int taskId) {
//			
//		}
//
//		@Override
//		public void onUpdate(Object msg, int taskId) {
//			switch(taskId){
//			case TASK.SYSTEM_LOCK:
//				if(msg==null){
//					break;
//				}
//
//				//清除旧的授权窗口
//				SKSceneManage.getInstance().closeLockWindow();
//				
//				//设置新OnePassWord
//				PassWordInfo psinfo=(PassWordInfo) msg;
//				LockInfoBiz.getInstance().SetbIsLock(true);
//				LockInfoBiz.getInstance().SetPassWord(psinfo.getsPwdStr());
//				LockInfoBiz.getInstance().SetInfo(psinfo.getsTimeOut());
//				SKSceneManage.getInstance().turnToLockWindow(LockInfo.GetInfo(),LockInfo.GetPassWord());
//				break;
//			case TASK.SYSTEM_UNLOCK:
//				LockInfoBiz.getInstance().SetbIsLock(false);
//				SKSceneManage.getInstance().closeLockWindow();
//				break;
//			}
//		}
//		
//	};
	
	/**
	 * 系统操作
	 * @param operType 操作类型
	 * @param msg 操作信息
	 * @return
	 */
	public static boolean peculiarOper(SYSTEM_OPER_TYPE operType,Object msg){
		boolean result=false; 
		switch(operType){
			case SYSTEM_BACKLIGHT_OFF:
				SKSceneManage.getInstance().saveCurrenBright();
				SKSceneManage.getInstance().backlightoff();
				result=true;
				break;
			case SYSTEM_BACKLIGHT_ON:
				SKSceneManage.getInstance().backLightOn();
				result=true;
				break;
			case SYSTEM_RESET:
				SKSceneManage.getInstance().destroy();
				android.os.Process.killProcess(android.os.Process.myPid()); 
				//can it return?
				result=true;
				break;
			case SYSTEM_SET_SCENE_TIME:
				if(msg==null) {
					return false;
				}
				SystemInfoBiz biz = new SystemInfoBiz();
				int time = (Integer) msg;
				SystemInfo.setnScreenTime(time);
				biz.updateScreenSaverTime(time);
				result=true;
				break;
			case SYSTEM_SET_DATE:
				if(msg==null) {
					return false;
				}
				int date = Integer.parseInt((String)msg);
				int year = date/10000;
				int month = (date-year*10000)/100;
				int day = date%100;
				result = setDate(year,month,day);
				break;
			case SYSTEM_SET_TIME:
				if(msg==null) {
					return false;
				}
				int systime = Integer.parseInt((String)msg);
				int syshour = systime/100;
				int sysminute = systime%100;
				result=setTime(syshour,sysminute);
				break;
			case SYSTEM_SET_SOUND_ON:
				SystemInfo.setnSetBoolParam(SystemInfo.getnSetBoolParam()| (SystemParam.USE_TOUCH_SOUND));
				//写入将触摸声音写入地址
				SystemVariable.getInstance().setTouchSoundToAddr(SystemAddress.getInstance().enableBeepAddr());
				result=DBTool.getInstance().getmSystemInfoBiz().updateSysParam(SystemInfo.getnSetBoolParam());
				break;
			case SYSTEM_SET_SOUND_OFF:
				SystemInfo.setnSetBoolParam(SystemInfo.getnSetBoolParam()& (~SystemParam.USE_TOUCH_SOUND));
				//写入将触摸声音写入地址
				SystemVariable.getInstance().setTouchSoundToAddr(SystemAddress.getInstance().enableBeepAddr());
				result=DBTool.getInstance().getmSystemInfoBiz().updateSysParam(SystemInfo.getnSetBoolParam());
				break;
			case SYSTEM_CHANGE_LANGUAGE:
				if(msg == null) {
					return false;
				}
				int languageId = Integer.parseInt((String)msg);
				if(languageId>=SystemInfo.getLanguageNumber()) {
					return false;
				}
				SKLanguage.getInstance().getBinder()
				.onChange(languageId);
				result=true;
				break;
				
			case SYSTEM_LOCK:
				if(msg==null){
					break;
				}

				//清除旧的授权窗口
				SKSceneManage.getInstance().closeLockWindow();
				
				//设置新OnePassWord
				PassWordInfo psinfo=(PassWordInfo) msg;
				LockInfoBiz.getInstance().SetbIsLock(true);
				LockInfoBiz.getInstance().SetPassWord(psinfo.getsPwdStr());
				LockInfoBiz.getInstance().SetInfo(psinfo.getsTimeOut());
				result=SKSceneManage.getInstance().turnToLockWindow(LockInfo.GetInfo(),LockInfo.GetPassWord());
				break;
			case SYSTEM_UNLOCK:
				LockInfoBiz.getInstance().SetbIsLock(false);
				result=SKSceneManage.getInstance().closeLockWindow();
				break;
		}
		
		return result;
	}
	
	/**
	 * 判断年月日是否合法
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private static boolean checkDate(int year, int month, int day){
		if(year<1970||year>9999) {
			return false;
		}
		if(month<0||month>11){
			return false;
		}
		if(day<0) {
			return false;
		}
		//月从0开始
		if(month==1){
			if(year%400==0||(year%100!=0&&year%4==0)){
				if(day>29) {
					return false;
				}
			}else{
				if(day>28) {
					return false;
				}
			}
		}else if(month==0||month==2||month==4||month==6
				||month==7||month==9||month==11){
			if(day>31) {
				return false;
			}
		}else {
			if(day>30) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 设定系统日期
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private static boolean setDate(int year, int month, int day) {
		if(!checkDate(year, month, day)) return false;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			Activity activity=SKSceneManage.getInstance().getActivity();
			if (activity==null) {
				return false;
			}
			// SystemClock.setCurrentTimeMillis(when);
			Intent intent = new Intent();
			intent.setAction("com.samkoon.settime");
			intent.putExtra("time", when);
			activity.sendBroadcast(intent);
			return true;
		}
		return false;
	}
	
	/**
	 * 设定系统时间
	 * @param hourOfDay
	 * @param minute
	 * @return
	 */
	private static boolean setTime(int hourOfDay, int minute) {
		if(hourOfDay<0||hourOfDay>24||minute<0||minute>60) return false;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);		
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			Activity activity=SKSceneManage.getInstance().getActivity();
			if (activity==null) {
				return false;
			}
			// SystemClock.setCurrentTimeMillis(when);
			Intent intent = new Intent();
			intent.setAction("com.samkoon.settime");
			intent.putExtra("time", when);
			activity.sendBroadcast(intent);
			return true;
		}
		return false;
	}
}
