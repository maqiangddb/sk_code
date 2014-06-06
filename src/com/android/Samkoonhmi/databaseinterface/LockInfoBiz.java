package com.android.Samkoonhmi.databaseinterface;

import com.android.Samkoonhmi.model.LockInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

public class LockInfoBiz extends DataBase {
	private static String CREATETABLE="Create TABLE IF NOT EXISTS[systemLockProp] (id integer PRIMARY KEY AUTOINCREMENT NOT NULL"
	        +",bSystemLock text"
			+",password text"
	        +",info text"
	        +")";
	
	private static LockInfoBiz instance;
	private SKDataBaseInterface db;
	
	public LockInfoBiz(){
		db=SkGlobalData.getProjectDatabase();
		checkDB();
	}
	
	/**
	 * 获得实例
	 * @return
	 */
	public static LockInfoBiz getInstance(){
		if(instance == null){
			instance=new LockInfoBiz();
		}
		return instance;
	}
	
	/**
	 * 检查数据库是否打开
	 */
	private void checkDB(){
		db.execSql(CREATETABLE);
	}
	
	/**
	 * 更新锁屏数据
	 * @param lock-是否锁屏
	 * @param pw-锁屏密码
	 * @param info-提示信息
	 */
	public void update(boolean lock,String pw,String info){
		String isLock="";
		if(lock){
			isLock="true";
		}else{
			isLock="false";
		}
		
		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			if (cursor.moveToNext()) {
				String sql="update systemLockProp set bSystemLock='"+isLock
						+"',password='"+pw+"',info='"+info+"'";
				db.execSql(sql);
				cursor.close();
				//Log.d("LockInfoBiz", "lock biz............");
			}else {
				ContentValues lockvalues = new ContentValues();
				lockvalues.put("bSystemLock", isLock);//LOCK
				lockvalues.put("password", pw);//psw
				//lockvalues.put("stime", "100");//stime
				lockvalues.put("info", info);//info
				db.insertData("systemLockProp",lockvalues);
			}
		}else {
			ContentValues lockvalues = new ContentValues();
			lockvalues.put("bSystemLock", isLock);//LOCK
			lockvalues.put("password", pw);//psw
			//lockvalues.put("stime", "100");//stime
			lockvalues.put("info", info);//info
			db.insertData("systemLockProp",lockvalues);
		}
		
		LockInfo.SetbIsLock(lock);
		LockInfo.SetPassWord(pw);
		LockInfo.SetInfo(info);
		
	}
	
	/**
	 * 设定锁屏
	 * @param lock
	 */
	public synchronized void SetbIsLock(boolean lock){
		
		if(LockInfo.GetbIsLock() == lock){
			return;
		}
		
		String isLock="";
		if(lock){
			isLock="true";
		}else{
			isLock="false";
		}

		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				db.execSql("update systemLockProp set bSystemLock="+"'"+isLock+"'");
			}
			cursor.close();
		}
		LockInfo.SetbIsLock(lock);
	}
	
	/**
	 * 判断是否锁屏
	 * @return
	 */
	public synchronized boolean GetbIslock(){
		boolean result=false;
		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				result=cursor.getString(cursor.getColumnIndex("bSystemLock")).equals("false")?false:true;
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 设定锁屏密码
	 * @param password
	 */
	public synchronized void SetPassWord(String password){
		
		//密码为空时默认密码
		if(TextUtils.isEmpty(password)){
			password="samkoon";
		}
		
		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				db.execSql("update systemLockProp set password="+"'"+password+"'");
			}
			cursor.close();
		}
		LockInfo.SetPassWord(password);
	}
	
	/**
	 * 返回锁屏密码
	 * @return
	 */
	public synchronized String GetPassWord(){
		String result="";
		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				result=cursor.getString(cursor.getColumnIndex("password"));
			}
			cursor.close();
		}
		return result;
	}
	
//	/**
//	 * 设定时间段
//	 * @param time
//	 */
//	public synchronized void SetStime(String time){
//		if(!TextUtils.isEmpty(LockInfo.GetStime())){
//			if(LockInfo.GetStime().equals(time)){
//				return;
//			}
//		}
//		
//		Cursor cursor = db.rawQuery("select * from systemLockProp",null);
//		if (null != cursor) {
//			while (cursor.moveToNext()) {
//				db.execSQL("update systemLockProp set stime="+"'"+time+"'");
//			}
//			cursor.close();
//		}
//		LockInfo.SetStime(time);
//	}
//	
//	/**
//	 * 获得时间段
//	 * @return
//	 */
//	public synchronized String GetStime(){
//		String result="";
//		Cursor cursor = db.rawQuery("select * from systemLockProp",null);
//		if (null != cursor) {
//			while (cursor.moveToNext()) {
//				result=cursor.getString(cursor.getColumnIndex("stime"));
//			}
//			cursor.close();
//		}
//		return result;
//	}
//	
	/**
	 * 设定提示信息
	 * @param info
	 */
	public synchronized void SetInfo(String info){
		if(!TextUtils.isEmpty(LockInfo.GetInfo())){
			if(LockInfo.GetInfo().equals(info)){
				return;
			}
		}
		
		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				db.execSql("update systemLockProp set info="+"'"+info+"'");
			}
			cursor.close();
		}
		LockInfo.SetInfo(info);
	}
	
	/**
	 * 获得提示信息
	 * @return
	 */
	public synchronized String GetInfo(){
		String result="";
		Cursor cursor = db.getDatabaseBySql("select * from systemLockProp",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				result=cursor.getString(cursor.getColumnIndex("info"));
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 初始化读取锁屏参数及注册短信回调
	 */
	public synchronized void selectLockInfo(){
		LockInfo.SetbIsLock(GetbIslock());
		LockInfo.SetPassWord(GetPassWord());
//		LockInfo.SetStime(GetStime());
		LockInfo.SetInfo(GetInfo());
//		onRegisterSMS();
	}
	
	/**
	 * 注册短信回调
	 */
//	private void onRegisterSMS(){
//		SmsCall LocksmsCall = new SmsCall(){
//			@Override
//			public void onSmsCall(String fromNum, String content) {
//				if(TextUtils.isEmpty(content)){
//					return;
//				}
//				try{
//					String[] LockMsg = content.split(";", 3);//信息分割符
//					if(LockMsg==null){
//						return;
//					}else if(LockMsg.length!=3){
//						return;
//					}else if((LockMsg[0]==null)||(LockMsg[1]==null)
//							||(LockMsg[2]==null)){
//						return;
//					}
//					//判断是否锁屏命令
//					if(!LockMsg[0].equals("syb")){//be input
//						return;
//					}
//					//设置密码等
//					PassWordInfo msg = new PassWordInfo();
//					msg.setsPwdStr(LockMsg[1]);
//					msg.setsTimeOut(LockMsg[2]);
//					SystemControl.peculiarOper(SYSTEM_OPER_TYPE.SYSTEM_LOCK, msg);
//				}catch(PatternSyntaxException e){
//					e.printStackTrace();
//				}
//			}
//		};
//		SMSBroadcastReceiver.getBinder().onRegister(null, LocksmsCall);
//	}
}
