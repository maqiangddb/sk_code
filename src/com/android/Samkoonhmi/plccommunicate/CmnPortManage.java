package com.android.Samkoonhmi.plccommunicate;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP;
import com.android.Samkoonhmi.util.NetPramObj;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP.DATA_FLOW_CTL;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP.SERIAL_PORT_NUM;


public class CmnPortManage {
	private final String TAG = "CmnPortManage";
	
	private SerialCmnThread mSerialPort1 = null;
	private SerialCmnThread mSerialPort2 = null;
	private SerialCmnThread mSerialPort3 = null;
	private NetCmnThread mNetPort0 = null;
	private PlcSampInfo mTmpPlcInfo = new PlcSampInfo();
	
	private HashMap<Integer, NetPramObj> mNetPlcMap = new HashMap<Integer, NetPramObj>();

	/**
	 * 获得实例对象
	 */
	private static CmnPortManage mCmnManageObj = null;
	public static CmnPortManage getInstance()
	{
		if(null == mCmnManageObj)
		{
			mCmnManageObj = new CmnPortManage();
		}
		
		return mCmnManageObj;
	}
    
    /**
	 * 接收自由口的数据
	 * @param sSendData
	 * @return
	 */
	public synchronized boolean getFreePortData(short eConnect, Vector<Byte > nGetBuff)
	{
		boolean bGetOk = false;
		if(null == nGetBuff) return bGetOk;

		switch(eConnect)
		{
		case CONNECT_TYPE.COM1:
		{
			if(null == mSerialPort1)
			{
				openCmnPort(eConnect);
			}
			if(mSerialPort1 != null)
			{
				bGetOk = mSerialPort1.getData(nGetBuff);
			}
			break;
		}
		case CONNECT_TYPE.COM2:
		{
			if(null == mSerialPort2)
			{
				openCmnPort(eConnect);
			}
			if(mSerialPort2 != null)
			{
				bGetOk = mSerialPort2.getData(nGetBuff);
			}
			break;
		}
		case CONNECT_TYPE.COM3:
		{
			if(null == mSerialPort3)
			{
				openCmnPort(eConnect);
			}
			if(mSerialPort3 != null)
			{
				bGetOk = mSerialPort3.getData(nGetBuff);
			}
			break;
		}
		case CONNECT_TYPE.NET0:
		{
			break;
		}
		default:
		{
			break;
		}
		}
		
		return bGetOk;
	}
	
	/**
	 * 发送以太从站应答数据
	 * @param mPlcInfo
	 * @param sHostIp
	 * @param nHostPrt
	 * @param sSendData
	 * @return
	 */
	public boolean sendNetSlaveReponse(PlcSampInfo mPlcInfo ,int nNetPort,String sHostIp, int nHostPort, byte[] sSendData){
		boolean bOpenSuccess = false;
		if(null == sSendData || sSendData.length <= 0 || sHostIp == null || mPlcInfo.eConnectType != CONNECT_TYPE.NET0 ){
			return bOpenSuccess;
		}else{
			Pattern pattern = Pattern
					.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");

			Matcher matcher = pattern.matcher(sHostIp); // 以验证127.400.600.2为例

			if(! matcher.matches()){
				return bOpenSuccess;
			}
		}
		
		if(null == mNetPort0)
		{
			openCmnPort((short)mPlcInfo.eConnectType);
		}
		if(mNetPort0 != null)
		{
			NetPramObj nNetPramObj = new NetPramObj();
			nNetPramObj.bServer = true;
			nNetPramObj.bTcpNet = true;
			nNetPramObj.nHostPort = nHostPort;
			nNetPramObj.nNetPort = nNetPort;
			nNetPramObj.sHostIpAddress = sHostIp;
			nNetPramObj.sIpAddress = "";
			bOpenSuccess = mNetPort0.sendData(sSendData, nNetPramObj);
		}
		
		return bOpenSuccess;
	}
	
	/**
	 * 发送数据
	 * @param sSendData
	 * @return
	 */
	public  boolean  sendData(PlcSampInfo mPlcInfo, byte[] sSendData)
	{
		boolean bOpenSuccess = false;
		if(null == mPlcInfo || null == sSendData || sSendData.length <= 0) return bOpenSuccess;

		switch(mPlcInfo.eConnectType)
		{
		case CONNECT_TYPE.COM1:      //串口1
		{
			if(null == mSerialPort1)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort1 != null)
			{
				bOpenSuccess = mSerialPort1.sendData(sSendData);
			}
			break;
		}
		case CONNECT_TYPE.COM2:      //串口2
		{
			if(null == mSerialPort2)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort2 != null)
			{
				bOpenSuccess = mSerialPort2.sendData(sSendData);
			}
			break;
		}
		case CONNECT_TYPE.COM3:      //串口3
		{
			if(null == mSerialPort3)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort3 != null)
			{
				bOpenSuccess = mSerialPort3.sendData(sSendData);
			}
			break;
		}
		case CONNECT_TYPE.NET0:      //网口1
		{
			if(null == mNetPort0)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mNetPort0 != null)
			{
				bOpenSuccess = mNetPort0.sendData(sSendData, mNetPlcMap.get(mPlcInfo.nProtocolIndex));
			}
			break;
		}
		default:
		{
			break;
		}
		}
		
		return bOpenSuccess;
	}
	
	/**
	 * 接收数据
	 * @param sSendData
	 * @return
	 */
	public  boolean getData(PlcSampInfo mPlcInfo, Vector<Byte > nGetBuff)
	{
		boolean bGetOk = false;
		if(null == mPlcInfo || null == nGetBuff) return bGetOk;

		switch(mPlcInfo.eConnectType)
		{
		case CONNECT_TYPE.COM1:
		{
			if(null == mSerialPort1)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort1 != null)
			{
				bGetOk = mSerialPort1.getData(nGetBuff);
			}
			break;
		}
		case CONNECT_TYPE.COM2:
		{
			if(null == mSerialPort2)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort2 != null)
			{
				bGetOk = mSerialPort2.getData(nGetBuff);
			}
			break;
		}
		case CONNECT_TYPE.COM3:
		{
			if(null == mSerialPort3)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort3 != null)
			{
				bGetOk = mSerialPort3.getData(nGetBuff);
			}
			break;
		}
		case CONNECT_TYPE.NET0:
		{
			if(null == mNetPort0)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mNetPort0 != null)
			{
				bGetOk = mNetPort0.getData(nGetBuff, mNetPlcMap.get(mPlcInfo.nProtocolIndex));
			}
			break;
		}
		default:
		{
			break;
		}
		}
		
		return bGetOk;
	}

	/**
	 * 清除以太口缓存
	 * @param mPlcInfo
	 * @param sHostIp
	 * @param nHostPort
	 */
	public synchronized void clearNetRcvBuff(PlcSampInfo mPlcInfo,String sHostIp,int nHostPort){
		if(sHostIp == null){
			return;
		}else{
			Pattern pattern = Pattern
					.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");

			Matcher matcher = pattern.matcher(sHostIp); // 以验证127.400.600.2为例

			if(! matcher.matches()){
				return;
			}
		}
		NetPramObj nNetPramObj = new NetPramObj();
		nNetPramObj.bServer = mNetPlcMap.get(mPlcInfo.nProtocolIndex).bServer;
		nNetPramObj.bTcpNet = mNetPlcMap.get(mPlcInfo.nProtocolIndex).bTcpNet;
		nNetPramObj.nHostPort = nHostPort;
		nNetPramObj.nNetPort = mNetPlcMap.get(mPlcInfo.nProtocolIndex).nNetPort;
		nNetPramObj.sHostIpAddress = sHostIp;
		nNetPramObj.sIpAddress = mNetPlcMap.get(mPlcInfo.nProtocolIndex).sIpAddress;
		mNetPort0.clearRcvBuff(nNetPramObj);
	}
	
	/**
	 * 清除本机服务端以太口缓存
	 * @param mPlcInfo
	 * @param sHostIp
	 * @param nHostPort
	 */
	public synchronized void clearNetServerRcvBuff(int nNetPort,String sHostIp,int nHostPort){
		if(sHostIp == null){
			return;
		}else{
			Pattern pattern = Pattern
					.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");

			Matcher matcher = pattern.matcher(sHostIp); // 以验证127.400.600.2为例

			if(! matcher.matches()){
				return;
			}
		}
		NetPramObj nNetPramObj = new NetPramObj();
		nNetPramObj.bServer = true;
		nNetPramObj.bTcpNet = true;
		nNetPramObj.nNetPort = nNetPort;
		nNetPramObj.nHostPort = nHostPort;
		nNetPramObj.sHostIpAddress = sHostIp;
		mNetPort0.clearRcvBuff(nNetPramObj);
	}
	
	/**
	 * 清除缓存
	 * @param eConnectType
	 */
	public synchronized void clearRcvBuff(PlcSampInfo mPlcInfo)
	{
		if(null == mPlcInfo) return ;
		
		switch(mPlcInfo.eConnectType)
		{
		case CONNECT_TYPE.COM1:
		{
			if(null == mSerialPort1)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort1 != null)
			{
				mSerialPort1.clearRcvBuff();
			}
			break;
		}
		case CONNECT_TYPE.COM2:
		{
			if(null == mSerialPort2)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort2 != null)
			{
				mSerialPort2.clearRcvBuff();
			}
			break;
		}
		case CONNECT_TYPE.COM3:
		{
			if(null == mSerialPort3)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mSerialPort3 != null)
			{
				mSerialPort3.clearRcvBuff();
			}
			break;
		}
		case CONNECT_TYPE.NET0:
		{
			if(null == mNetPort0)
			{
				openCmnPort((short)mPlcInfo.eConnectType);
			}
			if(mNetPort0 != null)
			{
				mNetPort0.clearRcvBuff(mNetPlcMap.get(mPlcInfo.nProtocolIndex));
			}
			break;
		}
		default:
		{
			break;
		}
		}
	}
	
	/**
	 * 清除缓存
	 * @param eConnectType
	 */
	public synchronized void clearFreePort(short eConnect)
	{
		switch(eConnect)
		{
		case CONNECT_TYPE.COM1:
		{
			if(null == mSerialPort1)
			{
				openCmnPort(eConnect);
			}
			if(mSerialPort1 != null)
			{
				mSerialPort1.clearRcvBuff();
			}
			break;
		}
		case CONNECT_TYPE.COM2:
		{
			if(null == mSerialPort2)
			{
				openCmnPort(eConnect);
			}
			if(mSerialPort2 != null)
			{
				mSerialPort2.clearRcvBuff();
			}
			break;
		}
		case CONNECT_TYPE.COM3:
		{
			if(null == mSerialPort3)
			{
				openCmnPort(eConnect);
			}
			if(mSerialPort3 != null)
			{
				mSerialPort3.clearRcvBuff();
			}
			break;
		}
		case CONNECT_TYPE.NET0:
		{
			break;
		}
		default:
		{
			break;
		}
		}
	}
	
	public boolean updateNetProtocol(String ip,String protocolName,int nPort)
	{
		boolean bReturn  = false;
		short eConnectType = 8;
		if(SystemInfo.getbSimulator() == 1) return false;
		
		SKCommThread result = SKCommThread.getComnThreadObj(eConnectType);
		if(null != result)
		{
			result.stop(true);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(mNetPort0 != null)
		{
			mNetPort0.removeCilentNet(nPort, ip);
		}
		
		PlcSampInfo mPlcInfo = new PlcSampInfo();
		/*获取所有连接的大小*/
		if(null == SystemInfo.getPlcConnectionList()) return false;
		
		int nConnectSize = SystemInfo.getPlcConnectionList().size();
		PlcConnectionInfo mConnect = null;
		for(int i = 0; i < nConnectSize; i++)
		{
			if(SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == eConnectType)
			{
				mConnect = SystemInfo.getPlcConnectionList().get(i);
				break;
			}
		}
		if(mConnect == null)
		{
			Log.e(TAG, "this connect is :" + eConnectType + " not exsit, so updateNetProtocol failed");
			return false;
		}
		
		
		if(mConnect.getPlcAttributeList() != null)
		{
			int nPlcListSize = mConnect.getPlcAttributeList().size();
			for(int i = 0; i < nPlcListSize; i++)
			{
				mPlcInfo.eConnectType = eConnectType;
				mPlcInfo.nProtocolIndex = mConnect.getPlcAttributeList().get(i).getnUserPlcId();
				mPlcInfo.nSampRate = mConnect.getPlcAttributeList().get(i).getnMinCollectCycle();
				mPlcInfo.sProtocolName = mConnect.getPlcAttributeList().get(i).getsPlcServiceType();
				
				PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mPlcInfo);
				if(eProType == PROTOCOL_TYPE.SLAVE_MODEL)
				{
					continue;
				}
				if(protocolName.equals(mPlcInfo.sProtocolName))
				{
					NetPramObj mNetPram = mNetPlcMap.get(mPlcInfo.nProtocolIndex);
					if(mNetPram != null)
					{
						mNetPram.nNetPort = nPort;
						mNetPram.sIpAddress = ip;
						mNetPlcMap.remove(mPlcInfo.nProtocolIndex);
						mNetPlcMap.put(mPlcInfo.nProtocolIndex, mNetPram);
						bReturn = true;
						break;
					}
				}
			}
		}
		result.start(true);
		return bReturn;
	}
	
	/**
	 * 打开通信口
	 * @param nConnectPort
	 * @return
	 */
	public synchronized boolean openCmnPort(short eConnectType)
	{
		if(SystemInfo.getbSimulator() == 1) return false;
		
		/*获取所有连接的大小*/
		if(null == SystemInfo.getPlcConnectionList()) return false;
		
		int nConnectSize = SystemInfo.getPlcConnectionList().size();
		PlcConnectionInfo mConnect = null;
		for(int i = 0; i < nConnectSize; i++)
		{
			if(SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == eConnectType)
			{
				mConnect = SystemInfo.getPlcConnectionList().get(i);
				break;
			}
		}
		if(mConnect == null)
		{
			Log.e(TAG, "this connect is :" + eConnectType + " not exsit, so openCmnPort failed");
			return false;
		}
		
		boolean bOpenSuccess = false;
		COM_PORT_PARAM_PROP mSerialParam = new COM_PORT_PARAM_PROP();
		
		/*判断是否有slave*/
		boolean bHaveSlave = false;
		boolean bbMasterScreen = true;
		if(mConnect.getPlcAttributeList() != null)
		{
			int nPlcListSize = mConnect.getPlcAttributeList().size();
			for(int i = 0; i < nPlcListSize; i++)
			{
				mTmpPlcInfo.eConnectType = eConnectType;
				mTmpPlcInfo.nProtocolIndex = mConnect.getPlcAttributeList().get(i).getnUserPlcId();
				mTmpPlcInfo.nSampRate = mConnect.getPlcAttributeList().get(i).getnMinCollectCycle();
				mTmpPlcInfo.sProtocolName = mConnect.getPlcAttributeList().get(i).getsPlcServiceType();
				
				PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
				if(eProType == PROTOCOL_TYPE.SLAVE_MODEL)
				{
					bHaveSlave = true;
					
					if(eConnectType > 1 && eConnectType < 8)
					{
						break;
					}
				}
				
				/*保存网络的IP地址等信息*/
				if(eConnectType >= 8 && eConnectType <= 13)   //是网口
				{
					NetPramObj mNetPram = new NetPramObj();
					mNetPram.nNetPort = mConnect.getPlcAttributeList().get(i).getnNetPortNum();
					mNetPram.sIpAddress = mConnect.getPlcAttributeList().get(i).getsIpAddr();
					mNetPram.bTcpNet = mConnect.getPlcAttributeList().get(i).getIsNetTcp();
					bbMasterScreen = mConnect.isbMasterScreen();
					
					if(eProType == PROTOCOL_TYPE.SLAVE_MODEL || !bbMasterScreen)
					{
						mNetPram.bServer = true;
					}
					else
					{
						mNetPram.bServer = false;
					}
					mNetPlcMap.put(mTmpPlcInfo.nProtocolIndex, mNetPram);
				}
			}
		}
		
		/*是串口*/
		if(eConnectType > 1 && eConnectType < 8)
		{
			mSerialParam.nSerialPortNum = eConnectType - 3;
			mSerialParam.nBaudRate = mConnect.getnBaudRate();
			mSerialParam.nDataBits = mConnect.getnDataBits();
			mSerialParam.nParityType = mConnect.getnCheckType();
			mSerialParam.nStopBit = mConnect.getnStopBit();
			mSerialParam.nFlowType = DATA_FLOW_CTL.FLOW_OFF;
			
			bOpenSuccess = openSerialPort(mSerialParam, bHaveSlave, eConnectType);
		}
		else if(eConnectType >= 8 && eConnectType <= 13)   //是网口
		{
			if(!bbMasterScreen)
			{
				bHaveSlave = true;
			}
			bOpenSuccess = openNetPort(bHaveSlave, eConnectType, mConnect);
		}
		
		return bOpenSuccess;
	}
	
	/**
	 * 关闭通信口
	 * @param nConnectPort
	 * @return
	 */
	public synchronized boolean closeCmnPort(short eConnectType)
	{
		boolean bOpenSuccess = false;
		switch(eConnectType)
		{
		case CONNECT_TYPE.COM1:
		{
			if(mSerialPort1 != null)
			{
				bOpenSuccess = mSerialPort1.closeSerial();
			}
			break;
		}
		case CONNECT_TYPE.COM2:
		{
			if(mSerialPort2 != null)
			{
				bOpenSuccess = mSerialPort2.closeSerial();
			}
			break;
		}
		case CONNECT_TYPE.COM3:
		{
			if(mSerialPort3 != null)
			{
				bOpenSuccess = mSerialPort3.closeSerial();
			}
			break;
		}
		case CONNECT_TYPE.NET0:
		{
			if(mNetPort0 != null)
			{
				bOpenSuccess = mNetPort0.closeNet();
			}
			break;
		}
		default:
		{
			break;
		}
		}
		return bOpenSuccess;
	}
	
	/**
	 * 获得通信口的参数
	 * @param nConnectPort
	 * @return
	 */
	public synchronized COM_PORT_PARAM_PROP getSerialParam(short eConnectType)
	{
		switch(eConnectType)
		{
		case CONNECT_TYPE.COM1:
		{
			if(null == mSerialPort1)
			{
				openCmnPort(eConnectType);
			}
			if(mSerialPort1 != null)
			{
				return mSerialPort1.getSerialParam();
			}
			break;
		}
		case CONNECT_TYPE.COM2:
		{
			if(null == mSerialPort2)
			{
				openCmnPort(eConnectType);
			}
			if(mSerialPort2 != null)
			{
				return mSerialPort2.getSerialParam();
			}
			break;
		}
		case CONNECT_TYPE.COM3:
		{
			if(null == mSerialPort3)
			{
				openCmnPort(eConnectType);
			}
			if(mSerialPort3 != null)
			{
				return mSerialPort3.getSerialParam();
			}
			break;
		}
		default:
		{
			break;
		}
		}
		
		return null;
	}
	
	/**
	 * 设置通信参数
	 * @param mSerialParam
	 */
	public synchronized void setSerialParam(COM_PORT_PARAM_PROP mSerialParam)
	{
		if(null == mSerialParam) return ;
		
		switch(mSerialParam.nSerialPortNum)
		{
		case SERIAL_PORT_NUM.COM_1:
		{
			if(null == mSerialPort1)
			{
				mSerialPort1 = new SerialCmnThread();
			}
			if(mSerialPort1 != null)
			{
				mSerialPort1.setSerialParam(mSerialParam);
			}
			break;
		}
		case SERIAL_PORT_NUM.COM_2:
		{
			if(null == mSerialPort2)
			{
				mSerialPort2 = new SerialCmnThread();
			}
			if(mSerialPort2 != null)
			{
				mSerialPort2.setSerialParam(mSerialParam);
			}
			break;
		}
		case SERIAL_PORT_NUM.COM_3:
		{
			if(null == mSerialPort3)
			{
				mSerialPort3 = new SerialCmnThread();
			}
			if(mSerialPort3 != null)
			{
				mSerialPort3.setSerialParam(mSerialParam);
			}
			break;
		}
		default:
		{
			break;
		}
		}
	}
	
	/**
	 * 打开串口
	 * @param mSerialParam
	 * @return
	 */
	private synchronized boolean openSerialPort(COM_PORT_PARAM_PROP mSerialParam, boolean bHaveSlave, short nOpenPort)
	{
		boolean bOpenSuccess = false;
		switch(mSerialParam.nSerialPortNum)
		{
		case SERIAL_PORT_NUM.COM_1:
		{
			if(null == mSerialPort1)
			{
				mSerialPort1 = new SerialCmnThread();
			}
			if(mSerialPort1 != null)
			{
				mSerialParam.nSerialPortNum = SERIAL_PORT_NUM.COM_1;
				bOpenSuccess = mSerialPort1.openSerial(mSerialParam, bHaveSlave, nOpenPort);
			}
			break;
		}
		case SERIAL_PORT_NUM.COM_2:
		{
			if(null == mSerialPort2)
			{
				mSerialPort2 = new SerialCmnThread();
			}
			if(mSerialPort2 != null)
			{
				mSerialParam.nSerialPortNum = SERIAL_PORT_NUM.COM_2;
				bOpenSuccess = mSerialPort2.openSerial(mSerialParam, bHaveSlave, nOpenPort);
			}
			break;
		}
		case SERIAL_PORT_NUM.COM_3:
		{
			if(null == mSerialPort3)
			{
				mSerialPort3 = new SerialCmnThread();
			}
			if(mSerialPort3 != null)
			{
				mSerialParam.nSerialPortNum = SERIAL_PORT_NUM.COM_3;
				bOpenSuccess = mSerialPort3.openSerial(mSerialParam, bHaveSlave, nOpenPort);
			}
			break;
		}
		default:
		{
			break;
		}
		}
		return bOpenSuccess; 
	}
	
	/**
	 * 打开网口
	 * @return
	 */
	public synchronized boolean openNetPort(boolean bHaveSlave, short nOpenPort, PlcConnectionInfo mConnectInfo)
	{
		boolean bSuccess = false;
		if(null == mNetPort0)
		{
			mNetPort0 = new NetCmnThread();
		}
		if(mNetPort0 != null)
		{
			bSuccess = mNetPort0.openNet(bHaveSlave, nOpenPort, mConnectInfo);
		}
		return bSuccess;
	}
	
	public void setmSerialPort2(SerialCmnThread mSerialPort2) {
		this.mSerialPort2 = mSerialPort2;
	}
}
