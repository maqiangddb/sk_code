package com.android.Samkoonhmi.skcommon;

import com.android.Samkoonhmi.util.Sk_CommonInfo;


import android.util.Log;

public class SkCommon {
	private static final String TAG = "SkCommon";

	
	private boolean bSkcommonOpen = false;
	private boolean isLightOpen=true;//背光打开着

	//单例
	private static SkCommon sInstance=null;
	public synchronized static SkCommon getInstance(){
		if (sInstance==null) {
			sInstance=new SkCommon();
		}
		return sInstance;
	}

	private SkCommon() {
	}
		
	/**
	 * 打开pmem设备，映射可读写虚拟地址
	 * @return
	 */
	public  boolean openSkCommon()
	{
		if(bSkcommonOpen) return true;
			 
		Log.e("openSkCommon", "open");		
		open();
		bSkcommonOpen = true;
		return true;
	}


	public void BackLight_On()
	{
		if(bSkcommonOpen==false)
			openSkCommon();		
		backlighton();
		isLightOpen=true;
	}
	
	public void BackLight_Off()
	{
		if(bSkcommonOpen==false)
			openSkCommon();
		backlightoff();
		isLightOpen=false;
	}

	public void ReadMode0(Sk_CommonInfo m_CommonInfo)
	{
		if(bSkcommonOpen==false)
			openSkCommon();
		
	//	Log.e("ReadMode0", "getmode0");		
		getmode0(m_CommonInfo);
	}	

	public void ReadMode1(Sk_CommonInfo m_CommonInfo)
	{
		if(bSkcommonOpen==false)
			openSkCommon();

		getmode1(m_CommonInfo);
	}		

	public void ReadMode2(Sk_CommonInfo m_CommonInfo)
	{
		if(bSkcommonOpen==false)
			openSkCommon();

		getmode2(m_CommonInfo);
	}		
	/**
	 * 关闭Skcommon设备
	 * @return
	 */
	public boolean closePmem()
	{
		close();
		bSkcommonOpen = false;
		return true;
	}
	
	/**
	 * 判断Skcommon设备是否打开
	 * @return
	 */
	public boolean getSkcommonIsOpened()
	{
		return bSkcommonOpen;
	}
	
	/**
	 * @return true-背光打开着，false-背光已经关闭
	 */
	public boolean getLightState(){
		return isLightOpen;
	}
	
	/*JNI方面的接口*/
	private native void open();
	public native void close();
	public native void backlighton();
	public native void backlightoff();
	public native void getmode0(Sk_CommonInfo m_CommonInfo);
	public native void getmode1(Sk_CommonInfo m_CommonInfo);
	public native void getmode2(Sk_CommonInfo m_CommonInfo);

	static {
		System.loadLibrary("skcommon");
	}
}
