package com.android.Samkoonhmi.plccommunicate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.util.Log;

import com.android.Samkoonhmi.serial.SerialPort;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP;
import com.android.Samkoonhmi.util.MODULE;

public class SerialCmnThread {

	private final String TAG = "SerialCmnThread";

	/*串口的一些属性*/
	protected SerialPort mSerialPort = null;
	private ReadThread mReadThread = null;

	/*串口接收缓存*/
	private Vector<Byte > nRcvBuffList = new Vector<Byte >();
	private byte[] nTmpReadBuff = new byte[64];
	
	private boolean bLocalHaveSlave = false;
	private short nLocalOpenPort = 1;

	/**
	 * 打开串口
	 * @param mSerialParam
	 * @return
	 */
	public boolean openSerial(COM_PORT_PARAM_PROP mSerialParam, boolean bHaveSlave, short nOpenPort)
	{
		/*保存转发的口*/
		bLocalHaveSlave = bHaveSlave;
		nLocalOpenPort = nOpenPort;
		
		if(null == mSerialParam)
		{
			return false;
		}

		/*打开串口*/
		if(mSerialPort == null)
		{
			mSerialPort = new SerialPort(mSerialParam);
		}
		boolean bSuccess = false;
		try {
			bSuccess = mSerialPort.openSerialPort();
		} catch (SecurityException e) {
	//		e.printStackTrace();
		} catch (IOException e) {
	//		e.printStackTrace();
		}
		if(!bSuccess) return bSuccess;

		if(null == mReadThread)
		{
			mReadThread = new ReadThread();
			mReadThread.setName("SerilCmnThread");
			mReadThread.start();
		}

		return bSuccess;
	}

	/**
	 * 关闭串口
	 * @param nPortId ： SERIAL_PORT_NUM.COM_0 COM_1....
	 * @return
	 */
	public boolean closeSerial()
	{
		if(null == mSerialPort)
		{
			Log.e(TAG, "serial port not opened, so close failed");
			return true;
		}

		/*清除缓存*/
		clearRcvBuff();
		return mSerialPort.closeSerialPort();
	}

	/**
	 * 获得通信参数
	 * @return
	 */
	public COM_PORT_PARAM_PROP getSerialParam() {
		
		if(null != mSerialPort)
		{
			return mSerialPort.getSerialParam();
		}
		
		return null;
	}

	/**
	 * 设置通信参数
	 * @param mSerialParam
	 */
	public void setSerialParam(COM_PORT_PARAM_PROP mSerialParam) {
		
		if(null != mSerialPort)
		{
			mSerialPort.closeSerialPort();
			mSerialPort.setSerialParam(mSerialParam);
			
			try {
				mSerialPort.openSerialPort();
			} catch (SecurityException e) {
		//		e.printStackTrace();
			} catch (IOException e) {
	//			e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送数据
	 * @param sSendData
	 * @return
	 */
	public synchronized boolean sendData(byte[] sSendData)
	{
		if(null == mSerialPort) return false;
		if(null == sSendData || sSendData.length <= 0) return false;

		if(null != mSerialPort.getOutputStream())
		{
			try {
				mSerialPort.getOutputStream().write(sSendData);
			} catch (IOException e) {
	//			e.printStackTrace();
				return false;
			}
			return true;
		}
		
		return false;
	}

	/**
	 * 接收数据
	 * @param nGetBuff
	 * @return
	 */
	public synchronized boolean getData(Vector<Byte > nGetBuff)
	{
		nGetBuff.clear();
		
		/*判断接收容器是否存在*/
		if(null == nRcvBuffList)
		{
			Log.e(TAG, "nRcvBuffList new failed, so restart system");
			return false;
		}
		
		int size = nRcvBuffList.size();
		if(size <= 0 || null == nGetBuff) return false;

		for(int i = 0; i < size; i++)
		{
			nGetBuff.add(nRcvBuffList.get(i));
		}

		return true;
	}
	
	/**
	 * 清除接收缓存
	 */
	public synchronized void clearRcvBuff()
	{
		/*判断接收容器是否存在*/
		if(null == nRcvBuffList)
		{
			Log.e(TAG, "nRcvBuffList new failed, so restart system");
			return ;
		}

		nRcvBuffList.clear();
	}

	/**
	 * 串口接收数据的线程
	 * @author Latory
	 *
	 */
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			if (mSerialPort == null) return;

			while(true)
			{
				while (mSerialPort.getSerialIsOpened())// (mSerialPort.getSerialIsOpened()) 
				{
					int size = 0;
					try {
						/**
						 * 一直等待读数据
						 */
						if(null != mSerialPort.getInputStream())
						{
//							int nLen = nTmpReadBuff.length;
//							for(int i = 0; i < nLen; i++)
//							{
//								nTmpReadBuff[i] = 0;
//							}

							size = mSerialPort.getInputStream().read(nTmpReadBuff);
							if (size > 0) {
								
//								long nTime = System.currentTimeMillis();
//						    	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SS");
//						        String s = format.format(new Date(nTime));

//						    	Log.e("nTime = " + s + "  length = " + size,"recive time");
						    	
								/*接收数据的测试*/
//								System.out.println("onDataReceived size = " + size);
//								String sTmpStr = "recerve data is : \n";
//								for(int i = 0; i < size; i++)
//								{
//									sTmpStr += "rcv[" + i + "] = ";
//									sTmpStr += Integer.toHexString(nTmpReadBuff[i]);
//									sTmpStr += ",  ";
//								}
//								System.out.println(sTmpStr);
								/*测试完成*/

								onDataReceived(nTmpReadBuff, size );
							}
						}

						/*休眠几毫秒*/
						try {
							sleep(8);  
						} catch (InterruptedException e) {
				//			e.printStackTrace();
						}
					} catch (IOException e) {
						Log.e("serial read error:", e.getMessage());
				//		e.printStackTrace();
						return;
					}
				}
			}
		}
	}

	/**
	 * 具体接收的数据
	 * @param buffer
	 * @param nSize
	 */
	private synchronized void onDataReceived(byte[] buffer, int nSize)
	{
		if(null == buffer ) return ;
		int nLen = buffer.length;
		if(nLen <= 0) return ;

		if(nSize > nLen)
		{
			nSize = nLen;
		}
		if(nSize <= 0) return ;
		
		/*如果大于2048个字节，则自动清除*/
		if(nRcvBuffList.size() > 65535)
		{
			nRcvBuffList.clear();
		}

		for(int i = 0; i < nSize; i++)
		{
			nRcvBuffList.add(buffer[i]);
		}
		
		/*从站处理*/
		if(bLocalHaveSlave)
		{
			SKCommThread mThreadObj = SKCommThread.getComnThreadObj(nLocalOpenPort);
			if (null != mThreadObj) 
			{
				int nRcvListSize = nRcvBuffList.size();
				byte[] nSlaveList = new byte[nRcvListSize];
				for(int i = 0; i < nRcvListSize; i++)
				{
					nSlaveList[i] = nRcvBuffList.get(i);
				}
				mThreadObj.getCmnRefreashHandler().obtainMessage(MODULE.SYSTEM_SLAVE_READ, nSlaveList).sendToTarget();
			}
		}
	}
}
