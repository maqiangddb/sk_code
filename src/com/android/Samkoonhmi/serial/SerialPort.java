package com.android.Samkoonhmi.serial;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP.DEV_OPEN_FLAGS;

import android.util.Log;

public class SerialPort {
	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	private FileDescriptor mFd = null;
	private FileInputStream mFileInputStream = null;
	private FileOutputStream mFileOutputStream = null;
	private COM_PORT_PARAM_PROP m_mSerialParam = null;
	
	private boolean bSerialOpen = false;

	public SerialPort(COM_PORT_PARAM_PROP mSerialParam)
	{
		m_mSerialParam = mSerialParam;
		/* Check access permission */
//		if (!device.canRead() || !device.canWrite()) {
//			try {
//				/* Missing read/write permission, trying to chmod the file */
//				Process su;
//				su = Runtime.getRuntime().exec("/system/bin/su");
//				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
//						+ "exit\n";
//				su.getOutputStream().write(cmd.getBytes());
//				if ((su.waitFor() != 0) || !device.canRead()
//						|| !device.canWrite()) {
//					throw new SecurityException();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new SecurityException();
//			}
//		}
	}
	
	/**
	 * 打开串口，可以多次打开
	 * @return
	 */
	public boolean openSerialPort() throws SecurityException, IOException 
	{
		if(null == m_mSerialParam) return false;
		if(bSerialOpen) return true;
		
		mFd = openJni(m_mSerialParam);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
	//		throw new IOException();
			return false;
		}
		
		if(null == mFileInputStream)
		{
			mFileInputStream = new FileInputStream(mFd);
		}
		
		if(null == mFileOutputStream)
		{
			mFileOutputStream = new FileOutputStream(mFd);
		}
		
		bSerialOpen = true;
		return true;
	}
	
	/**
	 * 关闭串口
	 * @return
	 */
	public boolean closeSerialPort()
	{
		if(null != mFileInputStream)
		{
			try {
				mFileInputStream.close();
				mFileInputStream = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(null != mFileOutputStream)
		{
			try {
				mFileOutputStream.close();
				mFileOutputStream = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(bSerialOpen)
		{
			close();
		}
		bSerialOpen = false;
		return true;
	}
	
	/**
	 * 获得通信参数
	 * @return
	 */
	public COM_PORT_PARAM_PROP getSerialParam() {
		return m_mSerialParam;
	}

	/**
	 * 设置通信参数
	 * @param mSerialParam
	 */
	public void setSerialParam(COM_PORT_PARAM_PROP mSerialParam) {
		this.m_mSerialParam = mSerialParam;
	}
	
	/**
	 * 判断串口是否打开
	 * @return
	 */
	public boolean getSerialIsOpened()
	{
		return bSerialOpen;
	}

	/**
	 * 获得读取数据流接口
	 * @return
	 */
	public InputStream getInputStream() {
		if(null == mFileInputStream)
		{
			if(null == mFd)
			{
				mFd = openJni(m_mSerialParam);
				if(null == mFd)
				{
					return mFileInputStream;
				}
			}
			
			mFileInputStream = new FileInputStream(mFd);
		}
		return mFileInputStream;
	}
	
	

	/**
	 * 获得写入数据流
	 * @return
	 */
	public OutputStream getOutputStream() {
		if(null == mFileOutputStream)
		{
			if(null == mFd)
			{
				mFd = openJni(m_mSerialParam);
				if(null == mFd)
				{
					return mFileOutputStream;
				}
			}
			
			mFileOutputStream = new FileOutputStream(mFd);
		}
		return mFileOutputStream;
	}
	
	/**
	 * 调用Jni的串口打开接口 打开串口
	 * @param mSerialParam
	 * @return
	 */
	private FileDescriptor openJni(COM_PORT_PARAM_PROP mSerialParam)
	{
		if(null == mSerialParam)
		{
			return null;
		}
		
		int nFlags = DEV_OPEN_FLAGS.O_RDWR;      // | DEV_OPEN_FLAGS.O_NOCTTY
		return open(mSerialParam,nFlags);
	//	mSerialPortFinder.getAllDevices();
	//	mSerialPortFinder.getAllDevicesPath();
	//	return open("/dev/ttyO0", 115200,0);
	}

	/*JNI方面的接口*/
	private native static FileDescriptor open(COM_PORT_PARAM_PROP mSerialParam, int flags);
	
//	private native static FileDescriptor open(String path, int baudrate,int flags);

	private native void close();

	static {
		System.loadLibrary("serial_port");
	}
}
