package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skwindow.SKSceneManage;

/**
 * 记录锁屏参数，除初始化读取数据库，其他时候使用这个类的信息
 * 写数据库同时修改这个类的信息
 * @author Administrator
 *
 */
public class LockInfo {
	private static boolean bIsLock=false;//是否锁屏
	private static String PassWord="";//锁屏密码
//	private static String stime="";//时间段
	private static String info="";//提示信息
	
	public static void SetbIsLock(boolean lock){
		
		//设置锁屏标示
		SKSceneManage.getInstance().setbHmiLock(lock);
		bIsLock = lock;
	}
	
	public static boolean GetbIsLock(){
		return bIsLock;
	}
	
	public static void SetPassWord(String psw){
		PassWord=psw;
	}
	
	public static String GetPassWord(){
		return PassWord;
	}
	
//	public static void SetStime(String time){
//		stime=time;
//	}
//	
//	public static String GetStime(){
//		return stime;
//	}
//	
	public static void SetInfo(String sinfo){
		info=sinfo;
	}
	
	public static String GetInfo(){
		return info;
	}
}
