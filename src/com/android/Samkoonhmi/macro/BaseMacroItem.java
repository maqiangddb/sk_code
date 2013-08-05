package com.android.Samkoonhmi.macro;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import android.content.Context;
import android.util.Log;

import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.BaseMacroInfo;
import com.android.Samkoonhmi.model.MParamInfo;

import dalvik.system.DexClassLoader;

/**
 * 基本宏指令项，用于描述单条宏指令的信息
 * */
public abstract class BaseMacroItem extends java.util.TimerTask { //implements Runnable{

	protected boolean mKeepThreadLoop = true; //保持线程循环
	
	private   BaseMacroInfo  mBMI;

	protected ArrayList<MParamInfo>  mParamList; //参数列表

	protected Thread  mInnerThread;           //自持有线程

	protected Method  mMacroMethod;           //函数句柄；

	protected Class<?>  mJMLClass;   //宏指令JML类
	protected Object    mJMLInstance;//宏指令JML类实例


	public BaseMacroItem(){

//		if(null == mInnerThread){
//			mInnerThread = new Thread(this);
//		}
	}

	/**
	 * 设置宏指令基类数据实体类
	 * */
	public void setBMI(BaseMacroInfo bmi){
		if(null == bmi){
			Log.e("BaseMacroItem","setBMI:  BaseMacroInfo is null!");
			return;
		}
		mBMI = bmi; 	
	}


	/**
	 * 获得方法句柄
	 * */
	public boolean obtainJMLMethod(){
		if(null == mBMI){
			Log.e("BaseMacroItem","obtainJMLMethod:  BaseMacroInfo is null!");
			return false;
		}
		if(null == mMacroMethod){ 	
			String tmpMacroName =  mBMI.getMacroName();
			if(null != tmpMacroName){
				try {
					mMacroMethod = mJMLClass.getMethod(tmpMacroName,(new HashMap<String, PHolder>()).getClass());
					//Log.i("BaseMacroItem","obtainJMLMethod: "+ tmpMacroName + " get ok!");
				} catch (SecurityException e) {
					Log.e("BaseMacroItem","obtainJMLMethod: "+ tmpMacroName +" SecurityException!");	   
					e.printStackTrace();
					return false;
				} catch (NoSuchMethodException e) {
					Log.e("BaseMacroItem","obtainJMLMethod: "+ tmpMacroName +" not found!");
					e.printStackTrace();
					return false;
				}
			}else{
				Log.e("BaseMacroItem","obtainJMLMethod:  mBMI get method name failed!");
				return false;
			}

		}//End of:if(null == mMacroMethod)
		return true;
	}

	/**
	 * 获得宏指令库名称
	 * */
	public String getMacroLibName(){
		if(null == mBMI){
			Log.e("BaseMacroItem", "getMacroLibName: mBMI is null");
			return null;
		}

		//宏指令库名称不能为空
		if(null == mBMI.getMacroLibName()){
			Log.e("BaseMacroItem", "getmJMLClass: Macro Library name is null");
			return null;
		}

		return mBMI.getMacroLibName();
	}

	/**
	 * 获得JML类
	 * */
	public boolean obtainJMLClass(Context content, DexClassLoader JarHolder){
		if(null == mJMLClass){

			//需要使用content参数
			if(null == content){
				Log.e("BaseMacroItem","getmJMLClass: content is null");
				return false;
			}

			//宏指令库名称不能为空
			if(null == mBMI.getMacroLibName()){
				Log.e("BaseMacroItem", "getmJMLClass: Macro Library name is null");
				return false;
			}

			//需持有ml.jar包
			if(null == JarHolder){
				Log.e("BaseMacroItem", "getmJMLClass: JarHolder is null");
				return false;
			}

			String tmpmJMLClassName = null;  //宏指令库中主类名称	

			//获得JML库中类的名称
			tmpmJMLClassName = new String("jml"+"."+mBMI.getMacroLibName());		

			try{
				mJMLClass = JarHolder.loadClass(tmpmJMLClassName);
				//Log.i("BaseMacroItem", "getmJMLClass: mJMLClass create ok! : " + tmpmJMLClassName);
			}catch(ClassNotFoundException e){
				Log.e("BaseMacroItem", "getmJMLClass: Class Load failed : " + tmpmJMLClassName);
			}

			if(null == mJMLClass){
				Log.e("BaseMacroItem","runMacroFunc: mJMLClass create failed");
				return false;
			}	
		}//End of: if(null == mJMLClass)
		
		return true;
	}

	/**
	 * 获得JML类的一个实例
	 * */
	public boolean obtainJMLInstance(){
		if(null == mJMLInstance){	
			if(null == mJMLClass){
				Log.e("BaseMacroItem","obtainmJMLInstance:  mJMLClass is null");
				return false;
			}

			try {
				mJMLInstance = (Object)(mJMLClass.newInstance());
				//Log.i("BaseMacroItem","obtainmJMLInstance:  mJMLInstance create ok!");
			} catch (InstantiationException e) {
				Log.e("BaseMacroItem","obtainmJMLInstance:  mJMLInstance create failed!");
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				Log.e("BaseMacroItem","obtainmJMLInstance:  IllegalAccessException!");
				e.printStackTrace();
				return false;
			}
		}//End of:if(null == mJMLInstance)
		return true;
	}
	
	/**
	 * 获得宏指令的ID号
	 * */
	public short getMID() {
		return this.mBMI.getMacroID();
	}
	/**
	 * 执行宏指令
	 * */
	public abstract int execute(Timer timer);
	
	/**
	 * 取消执行宏指令
	 * */
	public abstract int cancle(Timer timer);
	
	
	/**
	 * 获得脚本类型
	 * */
	public abstract short getType();
	


	public void endLoop(){
		mKeepThreadLoop = false;
	}
}//End of class
