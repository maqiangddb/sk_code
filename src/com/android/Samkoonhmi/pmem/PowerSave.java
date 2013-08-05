package com.android.Samkoonhmi.pmem;

import com.android.Samkoonhmi.util.PowerSaveProp;


import android.util.Log;

public class PowerSave {
	private static final String TAG = "PowerSave";


//	private FileDescriptor mFd = null;
//	private PowerSaveProp m_PowerSaveParam = null;
	
	private boolean bPmemOpen = false;
	public int nWriteAddrOffset; 
	public int nReadAddrOffset; 

	//单例
	private static PowerSave sInstance=null;
	public synchronized static PowerSave getInstance(){
		if (sInstance==null) {
			sInstance=new PowerSave();
		}
		return sInstance;
	}

	private PowerSave() {
	}
		
	/**
	 * 打开pmem设备，映射可读写虚拟地址
	 * @return
	 */
	public  boolean openPowerSave()
	{
		if(bPmemOpen) return true;
			 
		Log.e("openPowerSave", "open1");		
		open();
		clearPmem();
		Log.e("openPowerSave", "open2");		
		bPmemOpen = true;
		return true;
	}

	public void clearPmem() //掉电区清0
	{
		nWriteAddrOffset=0;
		nReadAddrOffset=0;
		clear_allmemory();
//		clearlow();
//		clearhigh();
	}	

	public void clear_memory(int index)
	{
		clearmem(index);
	}
	public void clear_allmemory()
	{
		clearall();
	}

	public void setnWriteAddrOffset(int nWriteAddrOffset)
	{
		this.nWriteAddrOffset = nWriteAddrOffset;
	}

	public int getnWriteAddrOffset()
	{
		return nWriteAddrOffset;
	}
	public void setnReadAddrOffset(int nReadAddrOffset)
	{
		this.nReadAddrOffset = nReadAddrOffset;
	}

	public int getnReadAddrOffset()
	{
		return nReadAddrOffset;
	}
	
	public void WritePowerSave(PowerSaveProp m_PowerSaveParam)
	{
		if(bPmemOpen==false)
			openPowerSave();
		write(m_PowerSaveParam);
	}

	public void ReadPowerSave(PowerSaveProp m_PowerSaveParam,byte[] WriteBuff)
	{
		if(bPmemOpen==false)
			openPowerSave();

		read(m_PowerSaveParam,WriteBuff);
	}	
	/**
	 * 关闭pmem设备
	 * @return
	 */
	public boolean closePmem()
	{
		close();
		bPmemOpen = false;
		return true;
	}
	
	/**
	 * 判断pmem设备是否打开
	 * @return
	 */
	public boolean getPmemIsOpened()
	{
		return bPmemOpen;
	}
	
	
	/*JNI方面的接口*/
	private native void open();
	public native void close();
	public native void clearmem(int index);
	public native void clearall();
	public native void write(PowerSaveProp m_PowerSaveParam);
	public native void read(PowerSaveProp m_PowerSaveParam,byte[] nSourceData);

	static {
		System.loadLibrary("pmem");
	}
}
