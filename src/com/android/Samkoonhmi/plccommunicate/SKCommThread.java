package com.android.Samkoonhmi.plccommunicate;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Element;
import android.util.Log;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.CMN_SEND_TYPE;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.NetSlaveProp;
import com.android.Samkoonhmi.util.PlcCmnDataCtlObj;
import com.android.Samkoonhmi.util.PlcCmnInfoCode;
import com.android.Samkoonhmi.util.PlcCmnWriteCtlObj;
import com.android.Samkoonhmi.util.PlcNoticValue;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.PublicMathTools;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.SEND_PACKAGE_JNI;
import com.android.Samkoonhmi.util.SendPkgArray;
import com.android.Samkoonhmi.util.TurnDataProp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SKCommThread {
	
	/*通信和hand对象*/
	private HandlerThread mCmnThread = null ;
	private CmnThreadHandler mCmnHandler = null; 
	
	private short mEConnectPort = CONNECT_TYPE.OTHER;
//	private SendDataToPort mSendPortProp = new SendDataToPort();
	
	/*通信的毫秒数*/
	private int nCmnMsTimes = 10;
	private int nMaxCmnCycle = 10;
	
	/*调试信息的标题*/
	private static final String TAG = "SKCommThread";
	
	private byte[] nTmpCheckBuff = new byte[128];
	private Vector<Byte > nTmpRecList = new Vector<Byte >();
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
	
	private PlcNoticValue nNoticValue = new PlcNoticValue();
	
	private AddrProp mCurrAddr = new AddrProp();
	
	private Vector<Byte > nCtlNoticList = new Vector<Byte >();
	
	/*一个连接的信息*/
	public PlcConnectionInfo m_nConnectObj = null;
	
	/*读通信口缓存时间间隔,单位：毫秒*/
	private final int m_nGetBuffInterval = 5;
	private PlcSampInfo mTmpPlcInfo = new PlcSampInfo();
	
	/*控制线程启动和停止的变量*/
	private boolean m_bThreadLoop = false;
	
	/*是否是本地，是，则不用启动线程*/
	private boolean m_bLocal = false;
	
	private SendPkgArray mPkgListObj = new SendPkgArray();
	private SEND_PACKAGE_JNI sSendData = new SEND_PACKAGE_JNI();
	
	/*通信的状态*/
	private int nErrorCodeInfo = PlcCmnInfoCode.CMN_NOMAL_CODE;
	
	public boolean bHaveSlave = false;
	
	private long nSlaveFirstRcvTime = 0;
	private boolean bFirstRcv = true;
	
	/*屏的类型，为0则是没穿透的，为1则为穿透主屏，为2则为穿透从屏*/
	public int nScreenType = 0;
	
	/*实时写的容器*/
	Vector<PlcCmnWriteCtlObj > mRTWCmnList = new Vector<PlcCmnWriteCtlObj >();
	
	/*实时读的容器*/
	Vector<PlcCmnDataCtlObj > mRTRCmnList = new Vector<PlcCmnDataCtlObj >();
	
	/*直接发送的数据，自由口数据*/
	Vector<TurnDataProp > mFreePortList = new Vector<TurnDataProp >();
	
	/*默认构造函数*/
	private SKCommThread(boolean bLocal)
	{
		m_bLocal = bLocal;
	}
	
	/*获得和创建通信线程的对象，一个通信口对应一个线程对象*/
	private static HashMap<Short, SKCommThread> g_mConnectPortMap = null;
	public synchronized static SKCommThread getComnThreadObj(short eConnectType)
	{
		if(null == g_mConnectPortMap)
		{
			g_mConnectPortMap = new HashMap<Short, SKCommThread>();
		}

		SKCommThread result = null;
		
		/*如果没有连接类型就返回*/
		if(CONNECT_TYPE.OTHER == eConnectType)
		{
			Log.e("getComnThreadObj", "没有连接类型");
			return result;
		}
		
		/*判断通信是否已经创建*/
		if(g_mConnectPortMap.containsKey(eConnectType))
		{
			result = g_mConnectPortMap.get(eConnectType);
			if(eConnectType != CONNECT_TYPE.LOCAL && !result.m_bThreadLoop)
			{
				result.start(false);
			}
			return result;
		}
		
		/*如果是本地，则不用启动线程循环*/
		if(eConnectType == CONNECT_TYPE.LOCAL)
		{
			result = new SKCommThread(true);
			g_mConnectPortMap.put(eConnectType, result);
			 return result;
		}

		/*获取所有连接的大小*/
		if(null == SystemInfo.getPlcConnectionList()) return result;
		int nConnectSize = SystemInfo.getPlcConnectionList().size();
		PlcConnectionInfo mConnect = null;
		
		for(int i = 0; i < nConnectSize; i++)
		{
			if(SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == eConnectType)
			{
				mConnect = SystemInfo.getPlcConnectionList().get(i);
			}
		}
		
		/*找不到连接口，返回null*/
		if(mConnect == null)
		{
			return result;
		}
		
		/*新建通信线程*/
		result = new SKCommThread(false);
		g_mConnectPortMap.put(eConnectType, result);
		
		/*对连接类型赋值*/
		result.m_nConnectObj = mConnect;
		result.mEConnectPort = eConnectType;
		result.mTmpPlcInfo.eConnectType = eConnectType;
		
		/*打开连接口*/
		CmnPortManage.getInstance().openCmnPort(eConnectType);
		
		/*如果没启动线程，则启动*/
//		if(!result.m_bThreadLoop)
//		{
//			result.start();
//		}
		
		if(mConnect.getPlcAttributeList() == null || mConnect.getPlcAttributeList().isEmpty())
		{
			return result;
		}
		
		/*计算最大通信周期*/
		boolean bHaveSlaveTmp = false;
		int nSize = mConnect.getPlcAttributeList().size();
		Vector<Integer > nTmpList = new Vector<Integer >();
		for(int i = 0; i < nSize; i++)
		{
			int nTmpValue = mConnect.getPlcAttributeList().get(i).getnMinCollectCycle();
			if(nTmpValue > 0)
			{
				nTmpList.add(nTmpValue);
			}
			
			if(!bHaveSlaveTmp)
			{
				/*判断是否是slave*/
				result.mTmpPlcInfo.nProtocolIndex = mConnect.getPlcAttributeList().get(i).getnUserPlcId();
				result.mTmpPlcInfo.nSampRate = mConnect.getPlcAttributeList().get(i).getnMinCollectCycle();
				result.mTmpPlcInfo.sProtocolName = mConnect.getPlcAttributeList().get(i).getsPlcServiceType();
				
				PROTOCOL_TYPE eFunType = ProtocolInterfaces.getProtocolInterface().getProtocolType(result.mTmpPlcInfo);
				if(eFunType == PROTOCOL_TYPE.SLAVE_MODEL)
				{
					result.bHaveSlave = true;
				}
			}
		}
		result.nMaxCmnCycle = PublicMathTools.getMinMultiple(nTmpList);
		
		return result;
	}
	
	/**
	 * 启动所有线程
	 */
//	public static void startAllTread()
//	{
//		if(null == g_mConnectPortMap) return ;
//    	
//    	Set<Short> addrList = g_mConnectPortMap.keySet();
//
//		for(Iterator<Short> it = addrList.iterator();  it.hasNext(); )
//		{
//			SKCommThread result = g_mConnectPortMap.get(it.next());
//			if(result != null)
//			{
//				result.start(true);
//			}
//		}
//	}
	
	/**
	 * 停止所有通信线程 
	 */
    public static void stopAllThread(boolean bClosePort)
    {
    	if(null == g_mConnectPortMap) return ;
    	
    	Set<Short> addrList = g_mConnectPortMap.keySet();

		for(Iterator<Short> it = addrList.iterator();  it.hasNext(); )
		{
			SKCommThread result = g_mConnectPortMap.get(it.next());
			if(result != null)
			{
				result.stop(bClosePort);
			}
		}
		
	//	g_mConnectPortMap.clear();
    }

	/**
	 * 获得PLC采样的信息
	 * @param mAddrProp
	 * @return
	 */
	public boolean getPlcSampInfo(AddrProp mAddrProp, PlcSampInfo mPlcInfo)
	{
		if(null == mPlcInfo)
		{
			return false;
		}
		
		boolean bSuccess = false;
		if(mAddrProp.eConnectType <= 1 || mAddrProp.eConnectType > 13)
		{
			mPlcInfo.eConnectType = mAddrProp.eConnectType;
			mPlcInfo.nProtocolIndex = 0;
			mPlcInfo.nSampRate = 200;
			mPlcInfo.sProtocolName = mAddrProp.sPlcProtocol;
			
			bSuccess = true;
		}
		else
		{
			/*取得协议列表*/
			if(null == m_nConnectObj)
			{
				Log.e("getPlcSampInfo", "get PlcConnectionInfo object failed");
				return false;
			}
			if(null == m_nConnectObj.getPlcAttributeList())
			{
				Log.e("getPlcSampInfo", "mConnectInfo.getPlcAttributeList() is null");
				return false;
			}
			
			/*从连接属性类里面取得协议属性*/
			int nPlcListSize = m_nConnectObj.getPlcAttributeList().size();
			for(int i = 0; i < nPlcListSize; i++)
			{
				if(m_nConnectObj.getPlcAttributeList().get(i).getnUserPlcId() == mAddrProp.nUserPlcId)
				{
					mPlcInfo.eConnectType = mAddrProp.eConnectType;
					mPlcInfo.nProtocolIndex = mAddrProp.nUserPlcId;
					mPlcInfo.nSampRate = m_nConnectObj.getPlcAttributeList().get(i).getnMinCollectCycle();
					mPlcInfo.sProtocolName = mAddrProp.sPlcProtocol;
					
					bSuccess = true;
				}
			} 
		}
		
		if(!bSuccess)
		{
			Log.e("getPlcSampInfo", "find protocol:" + mAddrProp.sPlcProtocol + "The protocol library does not exist or protocol name may be wrong");
		}
		
		return bSuccess;
	}

	/**
	 * 启动开始线程
	 */
    public void start(boolean bHaveMast)
    {
    	/*本地地址 则不用启动线程循环*/
    	if(m_bLocal) return ;
    	
    	/*创建通信线程*/
		if(null == mCmnThread)
		{
			mCmnThread = new HandlerThread("SKCommThread");
			mCmnThread.start();
		}

		/*创建通信线程的句柄*/
		if(null == mCmnHandler)
		{
			mCmnHandler = new CmnThreadHandler(mCmnThread.getLooper());
		}
		
		/*刷新线程的开关*/
		m_bThreadLoop = true;
		
		/*开始循环读*/
		if(bHaveMast && SystemInfo.getbSimulator() != 1)
		{
			if(nScreenType == 0)
			{
				mCmnHandler.sendEmptyMessage(MODULE.SYSTEM_MAST_READ);
			}
			else if(nScreenType == 2)
			{
				mCmnHandler.sendEmptyMessage(MODULE.PENETRATE_SLAVE_CTL_MSG);
			}
		}
    }
    
    /**
     * 停止线程
     * @param bClosePort
     */
    public void stop(boolean bClosePort)
    {
    	/*如果有slave，则不停止线程*/
		if(!bHaveSlave)
		{
			m_bThreadLoop = false;
		}
    	if(bClosePort)
    	{
    		m_bThreadLoop = false;
    		CmnPortManage.getInstance().closeCmnPort(mEConnectPort);
    	}
    }
	
    /**
	 * 获得通信线程的句柄
	 * @return
	 */
	public CmnThreadHandler getCmnRefreashHandler()
	{
		if(null == mCmnHandler)
		{
			/*创建和启动线程*/
			if(null == mCmnThread)
			{
				mCmnThread = new HandlerThread("addrNotic");
				mCmnThread.start();
			}
			
			/*创建通知句柄*/
			mCmnHandler = new CmnThreadHandler(mCmnThread.getLooper());
		}
		
		return mCmnHandler;
	}
	
    /**
	 * 主线程截获消息，出现UI刷新
	 * @author Latory
	 *
	 */
	public class CmnThreadHandler extends Handler
	{
		public CmnThreadHandler(Looper looper)
		{
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			
			/*计算通信的时间*/
			long nCurrMillis = System.currentTimeMillis();
			switch (msg.what)
			{
			case MODULE.SYSTEM_MAST_READ:         //实时通信 处理系统主站读数据
			{
	    		/*如果线程在跑 则继续发送系统从站读刷新*/
	    		if(m_bThreadLoop)
				{
	    			/*主站自动读*/
	    			if(nScreenType == 0)               
	    			{
	    				getCmnRefreashHandler().removeMessages(MODULE.SYSTEM_MAST_READ);
	    				if(SystemInfo.getbSimulator() != 1)
	    				{
	    					boolean bNeedSysRead = systemReadData();
	    					if(bNeedSysRead)
	    					{
	    						getCmnRefreashHandler().sendEmptyMessage(MODULE.SYSTEM_MAST_READ);
	    					}
	    				}
	    			}
				}
				break;
			}
			case MODULE.SYSTEM_MAST_WRITE:         //实时通信 处理系统主站写数据
			{
	    		/*如果线程在跑 则继续发送系统主站读刷新*/
	    		if(m_bThreadLoop)
				{
	    			/*主站自动写*/
	    			getCmnRefreashHandler().removeMessages(MODULE.SYSTEM_MAST_WRITE);
	    			if(SystemInfo.getbSimulator() != 1)
	    			{
	    				if(nScreenType == 0)
	    				{
	    					systemWriteData();
	    				}
	    				else if(nScreenType == 1)
	    				{
	    					SKCommCtlCenter.getInstance().getCmnCtlHandler().sendEmptyMessage(MODULE.SYSTEM_MAST_WRITE);
	    				}
	    			}
				}
				break;
			}
			case MODULE.SYSTEM_SLAVE_READ:         //实时通信 处理系统从站读数据
			{
	    		/*如果线程在跑 则继续发送系统主站写刷新*/
	    		if(m_bThreadLoop && msg.obj != null)
				{
	    			if(nScreenType == 0)
	    			{
	    				dealOnceRcvForSlave((byte[])msg.obj);
	    			}
				}
				break;
			}
			case MODULE.SYSTEM_NET_SLAVE:         //网络从站通知，需要通知IP地址
			{
				/*如果线程在跑 则继续发送系统主站写刷新*/
	    		if(m_bThreadLoop && msg.obj != null)
				{
	    			if(nScreenType == 0)
	    			{
	    				dealOnceRcvForNetSlave((NetSlaveProp)msg.obj);
	    			}
				}
				break;
			}
			case MODULE.USER_READ:                //实时通信 处理读数据
			{
				if(msg.obj != null)
				{
					if(nScreenType == 0)
					{
						readPlcDataByMsg((PlcCmnDataCtlObj)msg.obj);
					}
					else if(nScreenType == 1)
					{
						SKCommCtlCenter.getInstance().getCmnCtlHandler().obtainMessage(MODULE.USER_READ, msg.obj);
					}
					else if(nScreenType == 2)
					{
						mRTRCmnList.add((PlcCmnDataCtlObj)msg.obj);
					}
				}
				break;
			}
			case MODULE.USER_WRITE:               //实时通信 处理写数据
			{
				if(msg.obj != null)
				{
					if(nScreenType == 0)
					{
						writePlcDataByMsg((PlcCmnWriteCtlObj)msg.obj);
					}
					else if(nScreenType == 1)
					{
						SKCommCtlCenter.getInstance().getCmnCtlHandler().obtainMessage(MODULE.USER_WRITE, msg.obj);
					}
					else if(nScreenType == 2)
					{
						mRTWCmnList.add((PlcCmnWriteCtlObj)msg.obj);
					}
				}
				break;
			}
			case MODULE.TURN_MSG_DATA:
			{
				if(msg.obj != null && SystemInfo.getbSimulator() != 1)
				{
					if(nScreenType == 0)
					{
						dealTurnData((TurnDataProp)msg.obj);
					}
					else if(nScreenType == 1)
					{
						SKCommCtlCenter.getInstance().getCmnCtlHandler().obtainMessage(MODULE.TURN_MSG_DATA, msg.obj);
					}
					else if(nScreenType == 2)
					{
						mFreePortList.add((TurnDataProp)msg.obj);
					}
				}
				break;
			}
			case MODULE.PENETRATE_SLAVE_CTL_MSG:       //穿透从站屏处理程序
			{
				if(m_bThreadLoop && nScreenType == 2 && SystemInfo.getbSimulator() != 1)
				{
					getCmnRefreashHandler().removeMessages(MODULE.PENETRATE_SLAVE_CTL_MSG);
					penetrateSlaveCtl();
					getCmnRefreashHandler().sendEmptyMessage(MODULE.PENETRATE_SLAVE_CTL_MSG);
				}
			}
			default:
			{
				break;
			}
			}//end switch
			
			try {
				Thread.sleep(1); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			nCmnMsTimes += (System.currentTimeMillis() - nCurrMillis);
			if(nCmnMsTimes > nMaxCmnCycle)
			{
				nCmnMsTimes = 10;
			}
		}
	}
    
    /**
     * 系统自动写函数
     * @return
     */
    public void systemWriteData()
    {
    	if(null == mTmpPlcInfo || null == m_nConnectObj || null == m_nConnectObj.getPlcAttributeList()) return ;
    	
    	/*先发送要写的数据到通信口*/
		int nLoopTime = 0;
		boolean bHaveWriteData = true;
		int nCheckInfo = PlcCmnInfoCode.OTHER_ERROR_CODE;
		
		int nProtocolSize = m_nConnectObj.getPlcAttributeList().size();
		while(bHaveWriteData && nLoopTime < 10)
		{
			bHaveWriteData = false;
			nLoopTime++;
			for(int i = 0; i < nProtocolSize; i++)
			{
				/************通信写操作********************/
				int eCmnType = CMN_SEND_TYPE.CMN_WRITE;
				
				mTmpPlcInfo.nProtocolIndex = m_nConnectObj.getPlcAttributeList().get(i).getnUserPlcId();
				mTmpPlcInfo.nSampRate = m_nConnectObj.getPlcAttributeList().get(i).getnMinCollectCycle();
				mTmpPlcInfo.sProtocolName = m_nConnectObj.getPlcAttributeList().get(i).getsPlcServiceType();
				int nStationId = m_nConnectObj.getPlcAttributeList().get(i).getnPlcNo();
				
				/*不是主站，则跳过*/
				PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
				if(eProType != PROTOCOL_TYPE.MASTER_MODEL)
				{
					continue;
				}
				
				/*帧和帧之间需要休眠*/
				int nTimeout = m_nConnectObj.getPlcAttributeList().get(i).getnReceive_Timeout();
				int nFrameInterval = m_nConnectObj.getPlcAttributeList().get(i).getnIntervalTime();
    			int nRetryTimes = m_nConnectObj.getPlcAttributeList().get(i).getnRetryTime();
						
    			/*先进行握手校验, 失败进行下一个协议*/
				boolean bHandOk = dealHandShack(mTmpPlcInfo, nTimeout, nFrameInterval, nStationId);
				if(!bHandOk) continue;
				
    			boolean bSuccess = ProtocolInterfaces.getProtocolInterface().getSendWriteData(mPkgListObj, eCmnType, mTmpPlcInfo);
		    	if(bSuccess && null != mPkgListObj && null != mPkgListObj.mSendPkgList && mPkgListObj.mSendPkgList.length > 0)
		    	{
		    		/*第一次写数据*/
		    		nCheckInfo = writeOnceData(mTmpPlcInfo, mPkgListObj, nTimeout, nFrameInterval, nRetryTimes);

		    		/*第一次校验返回值失败，则启动重试发送程序*/
		    		if(0 != nCheckInfo)
		    		{
		    			/*重发几次*/
		    			while(nRetryTimes-- > 0 && 0 != nCheckInfo)
		    			{
		    				nCheckInfo = writeOnceData(mTmpPlcInfo, mPkgListObj, nTimeout, nFrameInterval, nRetryTimes);
		    			}

		    			/*通知通信失败*/
		    			if(0 != nCheckInfo)
		    			{
		    				noticCmnFail(nCheckInfo, mTmpPlcInfo);
		    			}
		    		}

		    	}//end if(bSuccess)
				
				bHaveWriteData |= bSuccess;
				
				/*如果是多协议， 则休眠帧间隔时间*/
				if(nProtocolSize > 1)
				{
					try {
	    				Thread.sleep(nFrameInterval);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
				}
			}
		}/*写数据循环结束*/
		
		if (nCheckInfo==0) {
			//通信正常
			PlcRegCmnStcTools.getLastCmnInfo().nErrorCode=0;
		}
		
		if(nLoopTime >= 10)
		{
			getCmnRefreashHandler().sendEmptyMessage(MODULE.SYSTEM_MAST_WRITE);
		}
    }
    
    /**
     * 系统自动读函数
     * @return
     */
    public boolean systemReadData()
    {
    	if(null == mTmpPlcInfo || null == m_nConnectObj || null == m_nConnectObj.getPlcAttributeList()) return true;
    	
    	/*然后发送要读的数据到通信口*/
    	int nCheckInfo = PlcCmnInfoCode.OTHER_ERROR_CODE;
    	int nProtocolSize = m_nConnectObj.getPlcAttributeList().size();
    	boolean bHaveCmn = false;
    	boolean bNeedSysRead = true;
		for(int i = 0; i < nProtocolSize; i++)
		{
			/************通信读操作********************/
			int eCmnType = CMN_SEND_TYPE.CMN_READ;
			
			mTmpPlcInfo.nProtocolIndex = m_nConnectObj.getPlcAttributeList().get(i).getnUserPlcId();
			mTmpPlcInfo.nSampRate = m_nConnectObj.getPlcAttributeList().get(i).getnMinCollectCycle();
			mTmpPlcInfo.sProtocolName = m_nConnectObj.getPlcAttributeList().get(i).getsPlcServiceType();
			int nStationId = m_nConnectObj.getPlcAttributeList().get(i).getnPlcNo();
			
			/*不是主站，则跳过*/
			PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
			if(eProType != PROTOCOL_TYPE.MASTER_MODEL)
			{
				continue;
			}
			
			/*还没到通信时间*/
			if(nCmnMsTimes > 0 && mTmpPlcInfo.nSampRate > 0)
			{
				if(nCmnMsTimes % mTmpPlcInfo.nSampRate != 0)
				{
					/*不是最后一个*/
					if(i != (nProtocolSize -1))
					{
						continue;
					}
					else if(bHaveCmn)  //是最后一个，但是有协议通信过
					{
						continue;
					}
				}
			}
			bHaveCmn = true;
			
			/*帧和帧之间需要休眠*/
			int nTimeout = m_nConnectObj.getPlcAttributeList().get(i).getnReceive_Timeout();
			int nFrameInterval = m_nConnectObj.getPlcAttributeList().get(i).getnIntervalTime();
			
			/*先进行握手校验, 失败进行下一个协议*/
			boolean bHandOk = dealHandShack(mTmpPlcInfo, nTimeout, nFrameInterval, nStationId);
			if(!bHandOk) continue;
			
			/*第一次读数据*/
			boolean bSuccess = ProtocolInterfaces.getProtocolInterface().getSendReadData(mPkgListObj, eCmnType, mTmpPlcInfo);
			if(bSuccess && null != mPkgListObj && null != mPkgListObj.mSendPkgList && mPkgListObj.mSendPkgList.length > 0)
			{
				bNeedSysRead = true;
				nCheckInfo = readOnceData(mTmpPlcInfo, mPkgListObj, nTimeout, nFrameInterval);
				
				/*第一次校验返回值失败，则启动重试发送程序*/
				if(0 != nCheckInfo)
				{
					int nRetryTimes = m_nConnectObj.getPlcAttributeList().get(i).getnRetryTime();
					
					/*重发几次*/
					while(nRetryTimes-- > 0 && 0 != nCheckInfo)
					{
						nCheckInfo = readOnceData(mTmpPlcInfo, mPkgListObj, nTimeout, nFrameInterval);
					}
					
					/*通知通信失败*/
					if(0 != nCheckInfo)
					{
						noticCmnFail(nCheckInfo, mTmpPlcInfo);
					}
				}
			}//if(bSuccess)
			
			/*如果有写消息，则停止读*/
			if(getCmnRefreashHandler().hasMessages(MODULE.SYSTEM_MAST_WRITE))
			{
				break;
			}
			
			/*如果是多协议， 则休眠帧间隔时间*/
			if(nProtocolSize > 1)
			{
				try {
    				Thread.sleep(nFrameInterval);  
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
			}
		}
		
		if (nCheckInfo==0) {
			//通信正常
			PlcRegCmnStcTools.getLastCmnInfo().nErrorCode=0;
		}
		
		
		return bNeedSysRead;
    }
    
    /**
     * 穿透从站处理
     */
    private void penetrateSlaveCtl()
    {
    	if(m_nConnectObj == null || nScreenType != 2) return ;
    	
    	int nScreenNum = m_nConnectObj.getnScreenNo();
    	boolean bGetOk = CmnPortManage.getInstance().getData(mTmpPlcInfo, nTmpRecList);
		if(bGetOk && nTmpRecList.size() == 4 && 
				nTmpRecList.get(0) == 5 &&
				nTmpRecList.get(1) == 1 &&
				nTmpRecList.get(2) == nScreenNum &&
				nTmpRecList.get(3) == 10)
		{
			/*实时写*/
			if(!mRTWCmnList.isEmpty())
			{
				int nSize = mRTWCmnList.size();
				for(int i = 0; i < nSize; i++)
				{
					writePlcDataByMsg(mRTWCmnList.get(i));
				}
				
				mRTWCmnList.clear();
			}
			
			/*实时读*/
			if(!mRTRCmnList.isEmpty())
			{
				int nSize = mRTRCmnList.size();
				for(int i = 0; i < nSize; i++)
				{
					readPlcDataByMsg(mRTRCmnList.get(i));
				}
				
				mRTRCmnList.clear();
			}
			
			if(!mFreePortList.isEmpty())
			{
				int nSize = mFreePortList.size();
				for(int i = 0; i < nSize; i++)
				{
					dealTurnData(mFreePortList.get(i));
				}
				
				mFreePortList.clear();
			}
			
			/*系统自动写*/
			systemWriteData();
			
			/*系统自动读*/
			systemReadData();
			
			/*发送结束标志*/
			byte[] nEndInfo = new byte[]{5, 2, (byte)nScreenNum, 10};
			CmnPortManage.getInstance().sendData(mTmpPlcInfo, nEndInfo);
		}
		else
		{
			CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
		}
    }
    
    /**
     * 通知通信失败
     * @param nCmnInfo
     * @param mPlcInfo
     */
    private void noticCmnFail(int nCmnInfo, PlcSampInfo mPlcInfo)
    {
    	
    	
    	PlcRegCmnStcTools.getLastCmnInfo().nErrorCode=nCmnInfo;
    	ProtocolInterfaces.getProtocolInterface().setCmnInfo(nCmnInfo, mPlcInfo);
    	
    	/*是否弹出提示*/
    	AddrProp mCtlAddr = SystemAddress.getInstance().isComErrAddr();
    	if(mCtlAddr != null)
    	{
    		nCtlNoticList.clear();
    		mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
    		mSendData.eDataType = DATA_TYPE.BIT_1;
    		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
    		
    		PlcRegCmnStcTools.getRegBytesData(mCtlAddr, nCtlNoticList, mSendData);
    		if(!nCtlNoticList.isEmpty())
    		{
    			/*值不是1，则不弹出通信失败对话框*/
    			if(nCtlNoticList.get(0) != 0)
    			{
    				return ;
    			}
    		}
    	}
    	
    	/*弹出错误信息*/
    	if(mPlcInfo != null)
    	{
    		String sErrorInfo = PlcCmnInfoCode.getErrorInfo(nCmnInfo);  
    		String sConnectName = "";
    		String sComInfo = " \n ";
    		String sPlcInfo = "";
    		
			Activity myActivity = SKSceneManage.getInstance().getActivity();
			if(null == myActivity) return ;
			
    		ProtocolInterfaces.getProtocolInterface().getCurrAddr(mPlcInfo, mCurrAddr);
    		if(null != mCurrAddr)
    		{
    			sPlcInfo += " \n ";
    			sPlcInfo += myActivity.getString(R.string.plc_station_id) + mCurrAddr.nPlcStationIndex + 
    					myActivity.getString(R.string.plc_reg_id) + mCurrAddr.nRegIndex + " \n ";
    			sPlcInfo += myActivity.getString(R.string.addr_value) + mCurrAddr.nAddrValue + 
    					myActivity.getString(R.string.addr_len) + mCurrAddr.nAddrLen;
    		}
    		if(mPlcInfo.eConnectType >= 2 && mPlcInfo.eConnectType <= 7)
    		{
    			sConnectName = "COM" + (mPlcInfo.eConnectType -2);
    			if(m_nConnectObj != null)
        		{
        			sComInfo += myActivity.getString(R.string.baud_rate);
        			sComInfo += m_nConnectObj.getnBaudRate();
        			sComInfo += myActivity.getString(R.string.data_bit);
        			sComInfo += m_nConnectObj.getnDataBits();
        			sComInfo += "\n " + myActivity.getString(R.string.stop_bit);
        			sComInfo += m_nConnectObj.getnStopBit();
        			sComInfo += myActivity.getString(R.string.check_type);
        			switch(m_nConnectObj.getnCheckType())
        			{
        			case 0:
        			{
        				sComInfo += myActivity.getString(R.string.none_check);
        				break;
        			}
        			case 1:
        			{
        				sComInfo += myActivity.getString(R.string.even_check);
        				break;
        			}
        			case 2:
        			{
        				sComInfo += myActivity.getString(R.string.odd_check);
        				break;
        			}
        			}
        		}
    		}
    		else if(mPlcInfo.eConnectType >= 8 && mPlcInfo.eConnectType <= 13)
    		{
    			sConnectName = "NET" + (mPlcInfo.eConnectType -8);
    			if(m_nConnectObj != null)
        		{
    				if(m_nConnectObj.getPlcAttributeList() != null)
    				{
    					int nPlcListSize = m_nConnectObj.getPlcAttributeList().size();
    					for(int i = 0; i < nPlcListSize; i++)
    					{
    						if(m_nConnectObj.getPlcAttributeList().get(i).getnUserPlcId() == mPlcInfo.nProtocolIndex)
    						{
    							sComInfo += myActivity.getString(R.string.ip_addr);
    							sComInfo += m_nConnectObj.getPlcAttributeList().get(i).getsIpAddr();
    							sComInfo += "\n " + myActivity.getString(R.string.port_id);
    							sComInfo += m_nConnectObj.getPlcAttributeList().get(i).getnNetPortNum();
    							break;
    						}
    					}
    				}
        		}
    		}
    			
    		String sShowInfo = " \n" + sConnectName+ myActivity.getString(R.string.link_faild) + "   \n\n" + 
    				myActivity.getString(R.string.protocol_name) + mPlcInfo.sProtocolName + "  \n\n"  +
    				myActivity.getString(R.string.error_info) + sErrorInfo + " \n  "  + sPlcInfo + " \n  " + sComInfo + "\n";
    		
    		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sShowInfo).sendToTarget();
    	}
    	
    	/*通信失败，休眠800ms*/
		try {
			Thread.sleep(800); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    /**
     * 写一次数据
     * @param mProtocol
     * @param mPkgListObj
     * @param nTimeOut
     * @return
     */
    private int writeOnceData(PlcSampInfo mPlcInfo, SendPkgArray mPkgListObj, int nTimeOut, int nFrameInterval, int nRetryTimes)
    {
    	/*如果是离线模拟则返回*/
    	if(SystemInfo.getbSimulator() == 1) return 0;
    	
    	/*判断对象是否存在*/
    	boolean bSuccess = false;
    	int nCheckInfo = PlcCmnInfoCode.OTHER_ERROR_CODE;
    	if(null == mPlcInfo || null == mPkgListObj || null == mPkgListObj.mSendPkgList || mPkgListObj.mSendPkgList.length <= 0) return nCheckInfo;
    
    	int nSendPkgLen = mPkgListObj.mSendPkgList.length;
    	SEND_PACKAGE_JNI mTmpSendPkg = mPkgListObj.mSendPkgList[0];
    	if(null == mTmpSendPkg) return nCheckInfo;
    	
    	/*如果是先读后写*/
    	if(mTmpSendPkg.eSendType == CMN_SEND_TYPE.CMN_READ_BEFORE_WRITE) //CMN_READ_BEFORE_WRITE
		{
    		nCheckInfo = readOnceData(mPlcInfo, mPkgListObj, nTimeOut, nFrameInterval);

			/*第一次校验返回值失败，则启动重试发送程序*/
			if(0 != nCheckInfo)
			{
				/*重发几次*/
				while(nRetryTimes-- > 0 && 0 != nCheckInfo)
				{
					nCheckInfo = readOnceData(mPlcInfo, mPkgListObj, nTimeOut, nFrameInterval);
				}

				/*通知通信失败*/
				if(0 != nCheckInfo)
				{
					noticCmnFail(nCheckInfo, mPlcInfo);
				} 
			}
			
			/*如果读取成功， 则从新取写数据*/
			if(0 == nCheckInfo)
			{
				
				/*从新读取数据*/
				bSuccess = ProtocolInterfaces.getProtocolInterface().getSendWriteData(mPkgListObj, CMN_SEND_TYPE.CMN_WRITE_AFTER_READ, mTmpPlcInfo);
				
				nCheckInfo = PlcCmnInfoCode.READ_DATA_FAIL;
				if(!bSuccess) return nCheckInfo;
			}
			else
			{
				/*清除缓存*/
				CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
    			return nCheckInfo;
			}
		}
    	
    	/*如果是先读后写的情况，则判断写发送包是否为空*/
    	if(null == mPkgListObj || null == mPkgListObj.mSendPkgList || mPkgListObj.mSendPkgList.length <= 0) return nCheckInfo;;

    	/*对每个包进行处理*/
    	for(int i = 0; i < nSendPkgLen; i++)
    	{
    		nCheckInfo = PlcCmnInfoCode.RCV_DATA_FAILED;
    		mTmpSendPkg = mPkgListObj.mSendPkgList[i];
    		if(null == mTmpSendPkg) continue;

    		/*清除缓存，开始接受数据*/
    		CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);

    		/*帧和帧之间需要休眠*/
    		if(nFrameInterval > 0)
    		{
    			try {
    				Thread.sleep(nFrameInterval);  
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}

    		/*发送数据*/
    		bSuccess = CmnPortManage.getInstance().sendData(mPlcInfo, mTmpSendPkg.sSendDataList);
        	if(!bSuccess)
        	{
        		/*清除缓存*/
        		CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
        		
        		/*发送失败，标志*/
        		nCheckInfo = PlcCmnInfoCode.SEND_DATA_FAILED;
        		return nCheckInfo;
        	}

    		/*判断是否需要写完等待回读*/
    		int nReturnLen = mTmpSendPkg.nReturnLen;
    		if(nReturnLen != 0)
    		{
    			/*校验第一次取得返回值是否正确*/
    			int nReadTimes = 0;
    			while(nReadTimes * m_nGetBuffInterval < nTimeOut && 0 != nCheckInfo)
    			{
    				try {
    					Thread.sleep(m_nGetBuffInterval);  //一次休眠5毫秒
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    				nReadTimes++;

    				/*接收的数据*/
    				int nRcvLen = 0;
    				boolean bGetOk = CmnPortManage.getInstance().getData(mPlcInfo, nTmpRecList);
    				if(bGetOk)
    				{
    					/*如果长度不够  则重新分配内存*/
    					nRcvLen = nTmpRecList.size();
    					if(nTmpCheckBuff.length < nRcvLen)
    					{
    						nTmpCheckBuff = new byte[nRcvLen];
    					}
    					
    					/*赋值给数组*/
    					for(int nRcv = 0; nRcv < nRcvLen; nRcv++)
    					{
    						nTmpCheckBuff[nRcv] = nTmpRecList.get(nRcv);
    					}
    				}
    				 
    				if(null != nTmpCheckBuff && nRcvLen > 0)
    				{
    					nCheckInfo = PlcCmnInfoCode.DATA_LEN_ERROR;
    					if(nReturnLen > 0)
    					{
    						if(nRcvLen == nReturnLen)
    						{
    							/*先置标识符为false*/
        						nNoticValue.bNeedRefreash = false;
    							nCheckInfo = ProtocolInterfaces.getProtocolInterface().checkWriteData(nTmpCheckBuff, nRcvLen, nReturnLen, i+1, mPlcInfo, nNoticValue);
    							
    							/*通知主线程更新*/
    							if(nCheckInfo == 0 && nNoticValue.bNeedRefreash)
    							{
    								SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
    							}
    							break;
    						}
    					}
    					else
    					{
    						/*先置标识符为false*/
    						nNoticValue.bNeedRefreash = false;
    						nCheckInfo = ProtocolInterfaces.getProtocolInterface().checkWriteData(nTmpCheckBuff, nRcvLen, nReturnLen, i+1, mPlcInfo, nNoticValue);
    						
    						/*通知主线程更新*/
    						if(nCheckInfo == 0 && nNoticValue.bNeedRefreash)
							{
								SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
							}
    					}
    				}
    			}//读取一次能校验的数据结束
    		}//end if(nReturnLen != 0)
    		else
			{
				nCheckInfo = 0;
				
				/*不需要返回 休眠50ms*/
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
    		/*清除缓存*/
    		CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
    		
    		if(0 != nCheckInfo)
        	{
        		/*通信失败，休眠150ms*/
				try {
					Thread.sleep(150);  //一次休眠150毫秒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		break;
        	}
    	}
    	
		nErrorCodeInfo = nCheckInfo;
    	return nCheckInfo;
    }
    
    /**
     * 读一次数据
     * @param mProtocol
     * @param mPkgListObj
     * @param eCmnType
     * @return
     */
    private int readOnceData(PlcSampInfo mPlcInfo, SendPkgArray mPkgListObj, int nTimeout, int nFrameInterval)
    {
    	/*如果是离线模拟则返回*/
    	if(SystemInfo.getbSimulator() == 1) return 0;
    	
    	int nCheckInfo = PlcCmnInfoCode.OTHER_ERROR_CODE;
    	
    	/*判断对象是否存在*/
    	if(null == mPlcInfo || null == mPkgListObj || null == mPkgListObj.mSendPkgList || mPkgListObj.mSendPkgList .length <= 0) return nCheckInfo;
    	
    	int nSendPkgLen = mPkgListObj.mSendPkgList.length;

    	/*对每个包进行处理*/
    	for(int i = 0; i < nSendPkgLen; i++)
    	{
    		nCheckInfo = PlcCmnInfoCode.RCV_DATA_FAILED;
    		SEND_PACKAGE_JNI mTmpSendPkg = mPkgListObj.mSendPkgList[i];
    		if(null == mTmpSendPkg)
    		{
    			break;
    		}

        	/*帧和帧之间需要休眠*/
    		if(nFrameInterval > 0)
    		{
    			try {
    				Thread.sleep(nFrameInterval);  
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}

    		/*清除缓存，开始接受数据*/
        	CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
        	boolean bSuccess = CmnPortManage.getInstance().sendData(mPlcInfo, mTmpSendPkg.sSendDataList);
        	if(!bSuccess)
        	{
        		/*清除缓存*/
        		CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
        		
        		/*发送失败，标志*/
        		nCheckInfo = PlcCmnInfoCode.SEND_DATA_FAILED;
        		return nCheckInfo;
        	}
        	
        	/*校验第一次取得返回值是否正确*/
        	int nReturnLen = mTmpSendPkg.nReturnLen;
        	if(nReturnLen != 0)
        	{
        		int nReadTimes = 0;
        		while(nReadTimes * m_nGetBuffInterval < nTimeout && 0 != nCheckInfo)
        		{
        			try {
        				Thread.sleep(m_nGetBuffInterval);  //一次休眠5毫秒
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
        			nReadTimes++;
        			int nRcvLen = 0;
    				boolean bGetOk = CmnPortManage.getInstance().getData(mPlcInfo, nTmpRecList);
    				if(bGetOk)
    				{
    					/*如果长度不够  则重新分配内存*/
    					nRcvLen = nTmpRecList.size();
    					if(nTmpCheckBuff.length < nRcvLen)
    					{
    						nTmpCheckBuff = new byte[nRcvLen];
    					}
    					
    					/*赋值给数组*/
    					for(int k = 0; k < nRcvLen; k++)
    					{
    						nTmpCheckBuff[k] = nTmpRecList.get(k);
    					}
    				}
  
        			if(null != nTmpCheckBuff && nRcvLen > 0)
        			{
        				nCheckInfo = PlcCmnInfoCode.DATA_LEN_ERROR;
        				if(nReturnLen > 0)
        				{
        					if(nRcvLen == nReturnLen) 
        					{
        						/*先置标识符为false*/
        						nNoticValue.bNeedRefreash = false;
        						nCheckInfo = ProtocolInterfaces.getProtocolInterface().setReadData(nTmpCheckBuff, nRcvLen, nReturnLen, i+1, mTmpSendPkg.eSendType, mPlcInfo, nNoticValue);
        						
        						/*通知主线程更新*/
        						if(nCheckInfo == 0 && nNoticValue.bNeedRefreash)
        						{
        							SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
        						}
        						break;
        					}
        				}
        				else
        				{
        					/*先置标识符为false*/
    						nNoticValue.bNeedRefreash = false;
        					nCheckInfo = ProtocolInterfaces.getProtocolInterface().setReadData(nTmpCheckBuff, nRcvLen, nReturnLen, i+1, mTmpSendPkg.eSendType, mPlcInfo, nNoticValue);
        					
        					/*通知主线程更新*/
    						if(nCheckInfo == 0 && nNoticValue.bNeedRefreash)
    						{
    							SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
    						}
        				}
        			}
        		}   // end while
        	}  //  end if(nReturnLen != 0)
        	else
			{
				nCheckInfo = 0;
				
				/*不需要返回 休眠50ms*/
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
        	
        	/*清除缓存*/
        	CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
        	
        	if(0 != nCheckInfo)
        	{
        		/*通信失败，休眠200ms*/
				try {
					Thread.sleep(200);  //一次休眠150毫秒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		break;
        	}
    	}

    	nErrorCodeInfo = nCheckInfo;
    	return nCheckInfo;
    }

    /**
     * 读一次串口缓存的一次校验
     * @param nProtocolSize
     * @param mPkgListObj
     * @return
     */
    public void dealOnceRcvForSlave(byte[] mRcvData)
    {
    	if(m_nConnectObj == null || m_nConnectObj.getPlcAttributeList() == null) return ;
    	if(mRcvData == null || mRcvData.length < 0) return ;
    	
    	boolean bSuccess = false; 
    	
    	if(bFirstRcv)
    	{
    		/*重新配置当前时间*/
    		nSlaveFirstRcvTime = System.currentTimeMillis();
    	}
//    	long nTime = System.currentTimeMillis();
//    	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SS");
//        String s = format.format(new Date(nTime));
//
//    	Log.e("nTime = " + s + "  length = " + mRcvData.length,"recive time");
    	
    	/*接收到了数据，去协议处理*/
    	int nProtocolSize = m_nConnectObj.getPlcAttributeList().size();
    	for(int i = 0; i < nProtocolSize; i++)
    	{
    		/*获取PLC从站信息*/
			mTmpPlcInfo.nProtocolIndex = m_nConnectObj.getPlcAttributeList().get(i).getnUserPlcId();
			mTmpPlcInfo.nSampRate = m_nConnectObj.getPlcAttributeList().get(i).getnMinCollectCycle();
			mTmpPlcInfo.sProtocolName = m_nConnectObj.getPlcAttributeList().get(i).getsPlcServiceType();
			
			PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
			if(eProType != PROTOCOL_TYPE.SLAVE_MODEL)
			{
				continue;
			}
			
    		/*获取站号*/
    		int nStationId = m_nConnectObj.getnScreenNo();
    		sSendData.sSendDataList = null ; 
    		int nCheckInfo = ProtocolInterfaces.getProtocolInterface().rcvStrForSlave(mRcvData, mRcvData.length, nStationId, sSendData, mTmpPlcInfo);
    		if(0 == nCheckInfo)
    		{
    			/*发消息更新内部地址和slave地址*/
    			SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
    			
    			/*成功 则清除缓存*/
				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);

    			if(null != sSendData.sSendDataList)
    			{
    				bSuccess = CmnPortManage.getInstance().sendData(mTmpPlcInfo, sSendData.sSendDataList);
    			}
    			
    			bSuccess = true;
    			break;
    		}
    		else 
    		{
    			int nMaxTimeout = m_nConnectObj.getPlcAttributeList().get(i).getnReceive_Timeout();
    			
    			/*如果接收失败*/
    			if(nCheckInfo == PlcCmnInfoCode.STATION_ERROR)
    			{
    				/*如果站号不对，休眠一段时间，把剩余的接收完 清除掉*/
//    				try {
//    					Thread.sleep(20);
//    				} catch (InterruptedException e) {
//    					e.printStackTrace();
//    				}
    				bSuccess = true;
    				
    				/*成功 则清除缓存*/
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    			}
    			else if(nCheckInfo == PlcCmnInfoCode.CHECK_OK_UNRCV_ALL)
    			{
    				bSuccess = true;
    				
    				/*成功 则清除缓存*/
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    			}
    			else if((System.currentTimeMillis() - nSlaveFirstRcvTime) >= nMaxTimeout)
    			{
    				/*成功 则清除缓存*/
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    				
    				if(null != sSendData.sSendDataList)
    				{
    					bSuccess = CmnPortManage.getInstance().sendData(mTmpPlcInfo, sSendData.sSendDataList);
    				}
    				bSuccess = true;
    			}
    		}
    	}
    	
    	if(bSuccess)
    	{
			bFirstRcv = true;
    	}
    	else
    	{
    		bFirstRcv = false;
    	}
    }
    
    /**
     * 网口接收的slave信息
     * @param mRcvProp
     */
    public void dealOnceRcvForNetSlave(NetSlaveProp mRcvProp)
    {
    	if(m_nConnectObj == null || m_nConnectObj.getPlcAttributeList() == null) return ;
    	if(mRcvProp == null || mRcvProp.mRcvData == null || mRcvProp.mRcvData.length <= 0) return ;
    	
    	boolean bSuccess = false;
    	
    	if(bFirstRcv)
    	{
    		/*重新配置当前时间*/
    		nSlaveFirstRcvTime = System.currentTimeMillis();
    	}
    	/*接收到了数据，去协议处理*/
    	int nProtocolSize = m_nConnectObj.getPlcAttributeList().size();
    	for(int i = 0; i < nProtocolSize; i++)
    	{
    		/*获取PLC从站信息*/
			mTmpPlcInfo.nProtocolIndex = m_nConnectObj.getPlcAttributeList().get(i).getnUserPlcId();
			mTmpPlcInfo.nSampRate = m_nConnectObj.getPlcAttributeList().get(i).getnMinCollectCycle();
			mTmpPlcInfo.sProtocolName = m_nConnectObj.getPlcAttributeList().get(i).getsPlcServiceType();
			
			PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
			if(eProType != PROTOCOL_TYPE.SLAVE_MODEL)
			{
				continue;
			}
			
			/*匹配IP地址*/
			if(mRcvProp.mNetPram != null)
			{
				int nNetPort = m_nConnectObj.getPlcAttributeList().get(i).getnNetPortNum();
				String sIpAddr = m_nConnectObj.getPlcAttributeList().get(i).getsIpAddr();
				if(mRcvProp.mNetPram.nNetPort != nNetPort || sIpAddr == null ||
						sIpAddr.equals(mRcvProp.mNetPram.sIpAddress) == false)
				{
					continue;
				}
			}
			
    		/*获取站号*/
    		int nStationId = m_nConnectObj.getnScreenNo();
    		sSendData.sSendDataList = null ; 
    		int nCheckInfo = ProtocolInterfaces.getProtocolInterface().rcvStrForSlave(mRcvProp.mRcvData, mRcvProp.mRcvData.length, nStationId, sSendData, mTmpPlcInfo);
    		if(0 == nCheckInfo)
    		{
    			/*发消息更新内部地址和slave地址*/
    			SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
    			
    			/*成功 则清除缓存*/
    			CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);

    			if(null != sSendData.sSendDataList)
    			{
    				bSuccess = CmnPortManage.getInstance().sendData(mTmpPlcInfo, sSendData.sSendDataList);
    			}
    			
    			bSuccess = true;
    			break;
    		}
    		else 
    		{
    			int nMaxTimeout = m_nConnectObj.getPlcAttributeList().get(i).getnReceive_Timeout();

    			/*如果接收失败*/
    			if(nCheckInfo == PlcCmnInfoCode.STATION_ERROR)
    			{
    				/*如果站号不对，休眠一段时间，把剩余的接收完 清除掉*/
//    				try {
//    					Thread.sleep(20);
//    				} catch (InterruptedException e) {
//    					e.printStackTrace();
//    				}

    				bSuccess = true;
    				
    				/*成功 则清除缓存*/
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    			}
    			else if(nCheckInfo == PlcCmnInfoCode.CHECK_OK_UNRCV_ALL)
    			{
    				bSuccess = true;
    				
    				/*成功 则清除缓存*/
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    			}
    			else if((System.currentTimeMillis() - nSlaveFirstRcvTime) >= nMaxTimeout)
    			{
    				/*成功 则清除缓存*/
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    				
    				if(null != sSendData.sSendDataList)
    				{
    					bSuccess = CmnPortManage.getInstance().sendData(mTmpPlcInfo, sSendData.sSendDataList);
    				}
    				bSuccess = true;
    			}
    			else
    			{
    				CmnPortManage.getInstance().clearRcvBuff(mTmpPlcInfo);
    				bSuccess = true;
    			}
    		}
    	}
    	
    	if(bSuccess)
    	{
			bFirstRcv = true;
    	}
    	else
    	{
    		bFirstRcv = false;
    	}
    }
    
    /**
     * 处理一次握手包
     * @param mPlcInfo
     * @param sSendDataProp
     * @param nTimeout
     * @param nFrameInterval
     * @param nPkgIndex
     * @param nBaudRate
     * @return
     */
    private int dealOnceHandShack(PlcSampInfo mPlcInfo, SEND_PACKAGE_JNI sSendDataProp, int nTimeout, int nFrameInterval, int nPkgIndex)
    {
    	int nCheckInfo = PlcCmnInfoCode.OTHER_ERROR_CODE;
    	if(null == mPlcInfo || null == sSendDataProp.sSendDataList || sSendDataProp.sSendDataList.length <= 0) return nCheckInfo;
    	
    	/*开始握手*/
    	/*设置发送数据失败标志*/
    	nCheckInfo = PlcCmnInfoCode.SEND_DATA_FAILED;

    	/*清除缓存，开始接受数据*/
    	CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);

    	/*帧和帧之间需要休眠*/
    	if(nFrameInterval > 0)
    	{
    		try {
    			Thread.sleep(nFrameInterval);  
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}

    	/*发送数据*/
		boolean bSuccess = CmnPortManage.getInstance().sendData(mPlcInfo, sSendDataProp.sSendDataList);
    	if(!bSuccess)
    	{
    		/*清除缓存*/
    		CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
    		
    		/*发送失败，标志*/
    		nCheckInfo = PlcCmnInfoCode.SEND_DATA_FAILED;
    		return nCheckInfo;
    	}

    	/*设置接收数据失败标志*/
    	nCheckInfo = PlcCmnInfoCode.RCV_DATA_FAILED;

    	/*判断是否需要写完等待回读*/
    	int nReturnLen = sSendDataProp.nReturnLen;
    	if(nReturnLen != 0)
    	{
    		/*校验第一次取得返回值是否正确*/
    		int nReadTimes = 0;
    		while(nReadTimes * m_nGetBuffInterval < nTimeout && nCheckInfo != 0)
    		{
    			try {
    				Thread.sleep(m_nGetBuffInterval);  //一次休眠5毫秒
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    			nReadTimes++;

    			int nRcvLen = 0;
    			boolean bGetOk = CmnPortManage.getInstance().getData(mPlcInfo,nTmpRecList);
    			if(bGetOk)
    			{
    				/*如果长度不够  则重新分配内存*/
    				nRcvLen = nTmpRecList.size();
    				if(nTmpCheckBuff.length < nRcvLen)
    				{
    					nTmpCheckBuff = new byte[nRcvLen];
    				}

    				/*赋值给数组*/
    				for(int nRcv = 0; nRcv < nRcvLen; nRcv++)
    				{
    					nTmpCheckBuff[nRcv] = nTmpRecList.get(nRcv);
    				}
    			}

    			if(null != nTmpCheckBuff && nRcvLen > 0)
    			{
    				nCheckInfo = PlcCmnInfoCode.DATA_LEN_ERROR;
    				if(nReturnLen > 0)
    				{
    					if(nRcvLen == nReturnLen)
    					{
    						nCheckInfo = ProtocolInterfaces.getProtocolInterface().checkHandshakePkg(nTmpCheckBuff, nRcvLen, nReturnLen, nPkgIndex, mPlcInfo);
    						break;
    					}
    				}
    				else
    				{
    					nCheckInfo = ProtocolInterfaces.getProtocolInterface().checkHandshakePkg(nTmpCheckBuff, nRcvLen, nReturnLen, nPkgIndex, mPlcInfo);
    				}
    			}
    		}//读取一次能校验的数据结束
    		
    		/*没有成功，最后做次检查*/
    		if(nCheckInfo == PlcCmnInfoCode.RCV_DATA_FAILED)
    		{
    			nCheckInfo = ProtocolInterfaces.getProtocolInterface().checkHandshakePkg(nTmpCheckBuff, 0, nReturnLen, nPkgIndex, mPlcInfo);
    		}
    	}//end if(nReturnLen != 0)
    	else    //不需要返回
    	{
    		nCheckInfo = 0;
    		
    		/*不需要返回 休眠50ms*/
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	if(0 != nCheckInfo)
		{
			noticCmnFail(nCheckInfo, mPlcInfo);
		
			/*握手失败，休眠150ms*/
			try {
				Thread.sleep(150);  //一次休眠150毫秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*清除缓存*/
		CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
		
		nErrorCodeInfo = nCheckInfo;
    	return nCheckInfo;
    }

    /**
     * 握手协议的处理
     * @param mPkgListObj
     * @param mPlcInfo
     * @param nTimeout
     * @param nFrameInterval
     * @return
     */
    public boolean dealHandShack(PlcSampInfo mPlcInfo, int nTimeout, int nFrameInterval, int nStationId) 
    {
    	if(null == mPlcInfo) return false;
    	
    	/*开始握手*/
		boolean bCheckOk = true;
		int nPkgIndex = 1;
		int nCheckInfo = PlcCmnInfoCode.CMN_NOMAL_CODE;
		
		/*记录波特率*/
		int nBaudRate = 0;
		COM_PORT_PARAM_PROP mPortProp = CmnPortManage.getInstance().getSerialParam((short)mTmpPlcInfo.eConnectType);
		if(mPortProp != null)
		{
			nBaudRate = mPortProp.nBaudRate;
		}
		
    	boolean bSuccess = ProtocolInterfaces.getProtocolInterface().getHandshakePkg(sSendData, mPlcInfo, nPkgIndex, nStationId, nBaudRate);
    	if(bSuccess && null != sSendData.sSendDataList && sSendData.sSendDataList.length > 0)
    	{
    		nCheckInfo = dealOnceHandShack(mPlcInfo, sSendData, nTimeout, nFrameInterval, nPkgIndex);
    		if(0 != nCheckInfo)
			{
				/*如果没接收到任何数据*/
				if(PlcCmnInfoCode.CHANGE_BAUD_RATE_9600 == nCheckInfo || PlcCmnInfoCode.CHANGE_BAUD_RATE_115200 == nCheckInfo)
				{
					if(mPortProp != null)
					{
						mPortProp.nBaudRate = nCheckInfo;
						CmnPortManage.getInstance().setSerialParam(mPortProp);
						
						/*设置串口后，休眠200ms*/
						try {
							Thread.sleep(200); 
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						bSuccess = ProtocolInterfaces.getProtocolInterface().getHandshakePkg(sSendData, mPlcInfo, nPkgIndex, nStationId, nBaudRate);
						if(bSuccess && null != sSendData.sSendDataList && sSendData.sSendDataList.length > 0)
						{
							nCheckInfo = dealOnceHandShack(mPlcInfo, sSendData, nTimeout, nFrameInterval, nPkgIndex);
							mPortProp.nBaudRate = nBaudRate;
							CmnPortManage.getInstance().setSerialParam(mPortProp);
							
							/*设置串口后，休眠200ms*/
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							nPkgIndex = 0;
							
							/*失败*/
							if(0 != nCheckInfo)
							{
								noticCmnFail(nCheckInfo, mPlcInfo);
								
								/*握手失败，休眠150ms*/
								try {
									Thread.sleep(150);  //一次休眠150毫秒
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
    		
    		/*再次检查检验是否成功*/
    		if(nCheckInfo != 0)
			{
				bCheckOk = false;
				noticCmnFail(nCheckInfo, mPlcInfo);
			}
			else
			{
				/*完成剩余的握手协议包*/
				bSuccess = true;
				bCheckOk = true;
				while(bSuccess)
				{
					nPkgIndex++;
					bSuccess = ProtocolInterfaces.getProtocolInterface().getHandshakePkg(sSendData, mPlcInfo, nPkgIndex, nStationId, nBaudRate);
			    	if(bSuccess && null != sSendData.sSendDataList && sSendData.sSendDataList.length > 0)
			    	{
			    		bSuccess = true;
			    	}
			    	else
			    	{
			    		bSuccess = false;
			    		break;
			    	}
			    	
			    	/*检查一次握手包*/
			    	nCheckInfo = dealOnceHandShack(mPlcInfo, sSendData, nTimeout, nFrameInterval, nPkgIndex);
			    	
			    	if(0 != nCheckInfo)
	    			{
	    				bCheckOk = false;
	    				noticCmnFail(nCheckInfo, mPlcInfo);
	    			
	    				/*握手失败，休眠150ms*/
	    				try {
							Thread.sleep(150);  //一次休眠150毫秒
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				break;
	    			}
	    			else
	    			{
	    				bCheckOk = true;
	    			}

	    			/*清除缓存*/
	    			CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
				} //end while
			}//  end else
    		
    		/*设置通信标志*/
	    	ProtocolInterfaces.getProtocolInterface().setCmnInfo(nCheckInfo, mPlcInfo);
    	}
    	else
    	{
    		/*设置通信标志*/
	    	ProtocolInterfaces.getProtocolInterface().setCmnInfo(0, mPlcInfo);
    		bCheckOk = true;
    	}

    	return bCheckOk;
    }
    
    /**
	 * 读历史数据的回调接口
	 * @author latory
	 *
	 */
	public interface ICmnCompletedCallback
	{
		public void cmnReadCompleted(boolean bSuccess, String sErrorInfo, Object mObjMsg);
		public void cmnWriteCompleted(boolean bSuccess, String sErrorInfo, Object mObjMsg);
	}
    
    /**
     * 通信线程，消息通知读一个连接口的地址
     * @param mCmnProp
     * @return
     */
    public boolean readPlcDataByMsg(PlcCmnDataCtlObj mCmnProp)
    {
    	/*判断是否为空*/
    	if(null == mCmnProp) return false;
    	
    	if(mCmnProp.mAddrList == null || 
    			mCmnProp.mAddrList.mSortAddrList == null || mCmnProp.mAddrList.mSortAddrList.length <= 0)
    	{
    		mCmnProp.Icallback.cmnReadCompleted(false, "read failed, have no read address ", mCmnProp.mDataObj);
    		return false;
    	}
    	
    	if(m_bLocal || SystemInfo.getbSimulator() == 1)
		{
			/*完成通知*/
			mCmnProp.Icallback.cmnReadCompleted(true, "read local addr completed", mCmnProp.mDataObj);
			return true;
		}
    	
    	if(null == m_nConnectObj)
    	{
    		mCmnProp.Icallback.cmnReadCompleted(false, "read failed, no com port or net port", mCmnProp.mDataObj);
    		return false;
    	}
		
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
		if(null == mProtocolObj)
		{
			mCmnProp.Icallback.cmnReadCompleted(false, "plc_drives_center file not exists", mCmnProp.mDataObj);
			return false;
		}
		
		/*开始通信产生数据*/
		int nTimeout = 32;
		int nFrameInterval = 10;
		int eCmnType = CMN_SEND_TYPE.CMN_READ;
		int nAddrSize = mCmnProp.mAddrList.mSortAddrList.length;
		int nCheckInfo=0;
		for(int i = 0; i < nAddrSize; i++)
		{
			mTmpPlcInfo.eConnectType = mCmnProp.mAddrList.mSortAddrList[i].eConnectType;
			mTmpPlcInfo.nProtocolIndex = mCmnProp.mAddrList.mSortAddrList[i].nUserPlcId;
			mTmpPlcInfo.sProtocolName = mCmnProp.mAddrList.mSortAddrList[i].sPlcProtocol;
			
			int nPlcListSize = m_nConnectObj.getPlcAttributeList().size();
			for(int k = 0; k < nPlcListSize; k++)
			{
				if(m_nConnectObj.getPlcAttributeList().get(k).getnUserPlcId() == mCmnProp.mAddrList.mSortAddrList[i].nUserPlcId)
				{
					nFrameInterval = m_nConnectObj.getPlcAttributeList().get(k).getnIntervalTime();
					nTimeout = m_nConnectObj.getPlcAttributeList().get(k).getnReceive_Timeout();
					break;
				}
			} 
			
			mProtocolObj.makeReadPackage(mCmnProp.mAddrList.mSortAddrList[i], mPkgListObj, eCmnType, mTmpPlcInfo);
			nCheckInfo = readOnceData(mTmpPlcInfo, mPkgListObj, nTimeout, nFrameInterval);
			if(0 != nCheckInfo)
			{
				mCmnProp.Icallback.cmnReadCompleted(false, "read " + mTmpPlcInfo.sProtocolName + " addr = " + mCmnProp.mAddrList.mSortAddrList[i].nAddrValue + " failed", mCmnProp.mDataObj);
				return false;
			}
		}
		
		if (nCheckInfo==0) {
			//通信正常
			PlcRegCmnStcTools.getLastCmnInfo().nErrorCode=0;
		}
		
		/*完成通知*/
		if(mTmpPlcInfo.eConnectType >= 2 && mTmpPlcInfo.eConnectType <= 7)
		{
			mCmnProp.Icallback.cmnReadCompleted(true, "read COM" + (mTmpPlcInfo.eConnectType -2) + " completed", mCmnProp.mDataObj);
		}
		else if(mTmpPlcInfo.eConnectType >= 8 && mTmpPlcInfo.eConnectType <= 13)
		{
			mCmnProp.Icallback.cmnReadCompleted(true, "read NET" + (mTmpPlcInfo.eConnectType - 8) + " completed", mCmnProp.mDataObj);
		}
		
    	return true;
    }

    /**
     * 通信线程写
     * @param mCmnProp
     * @return
     */
    public boolean writePlcDataByMsg(PlcCmnWriteCtlObj mCmnProp)
    {
    	/*判断是否为空*/
    	if(null == mCmnProp) return false;
    	
    	if(mCmnProp.mAddrProp == null || mCmnProp.nDataList == null || mCmnProp.nDataList.length <= 0)
    	{
    		/*完成通知*/
			if(null != mCmnProp.Icallback)
			{
				mCmnProp.Icallback.cmnWriteCompleted(true, "write failed, have no write address", mCmnProp.mDataObj);
			}
    		return false;
    	}
    	
    	/*内部地址，则写完成*/
		if(m_bLocal || SystemInfo.getbSimulator() == 1)
		{
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
			PlcRegCmnStcTools.setRegBytesData(mCmnProp.mAddrProp, mCmnProp.nDataList, mSendData);
			
			/*完成通知*/
			if(null != mCmnProp.Icallback)
			{
				mCmnProp.Icallback.cmnWriteCompleted(true, "write local address completed", mCmnProp.mDataObj);
			}
			return true;
		}
    	
    	if(null == m_nConnectObj)
    	{
    		if(null != mCmnProp.Icallback)
    		{
    			mCmnProp.Icallback.cmnWriteCompleted(false, "write failed, no com port or net port", mCmnProp.mDataObj);
    		}
    		return false;
    	}
		
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
		if(null == mProtocolObj)
		{
			if(null != mCmnProp.Icallback)
			{
				mCmnProp.Icallback.cmnWriteCompleted(false, "plc_drives_center file not exists", mCmnProp.mDataObj);
			}
			return false;
		}
		
		/*开始通信产生数据*/
		int nTimeout = 32;
		int nFrameInterval = 10;
		int nRetryTimes = 3;
		mTmpPlcInfo.eConnectType = mCmnProp.mAddrProp.eConnectType;
		mTmpPlcInfo.nProtocolIndex = mCmnProp.mAddrProp.nUserPlcId;
		mTmpPlcInfo.sProtocolName = mCmnProp.mAddrProp.sPlcProtocol;
		
		/*如果不是主站，则不需要进行通信*/
		PROTOCOL_TYPE ePlcType = mProtocolObj.getProtocolType(mTmpPlcInfo);
		if(ePlcType != PROTOCOL_TYPE.MASTER_MODEL)
		{
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
			PlcRegCmnStcTools.setRegBytesData(mCmnProp.mAddrProp, mCmnProp.nDataList, mSendData);
			
			/*完成通知*/
			if(null != mCmnProp.Icallback)
			{
				mCmnProp.Icallback.cmnWriteCompleted(true, "write slave address completed", mCmnProp.mDataObj);
			}
			return true;
		}
		
		int nPlcListSize = m_nConnectObj.getPlcAttributeList().size();
		for(int k = 0; k < nPlcListSize; k++)
		{
			if(m_nConnectObj.getPlcAttributeList().get(k).getnUserPlcId() == mCmnProp.mAddrProp.nUserPlcId)
			{
				nFrameInterval = m_nConnectObj.getPlcAttributeList().get(k).getnIntervalTime();
				nTimeout = m_nConnectObj.getPlcAttributeList().get(k).getnReceive_Timeout();
				nRetryTimes = m_nConnectObj.getPlcAttributeList().get(k).getnRetryTime();
				break;
			}
		} 
		
		/*实际写地址*/
		int eCmnType = CMN_SEND_TYPE.CMN_WRITE;
		mProtocolObj.makeWritePackage(mCmnProp.mAddrProp, mCmnProp.nDataList, mCmnProp.nDataList.length, mPkgListObj, eCmnType, mTmpPlcInfo);
		int nCheckInfo = writeOnceData(mTmpPlcInfo, mPkgListObj, nTimeout, nFrameInterval, nRetryTimes);
		if(0 != nCheckInfo)
		{
			mCmnProp.Icallback.cmnWriteCompleted(false, "write " + mTmpPlcInfo.sProtocolName + " addr = " + mCmnProp.mAddrProp.nAddrValue + " failed", mCmnProp.mDataObj);
			return false;
		}
		
		/*完成通知*/
		if(mCmnProp.bCallback)
		{
			if(mTmpPlcInfo.eConnectType >= 2 && mTmpPlcInfo.eConnectType <= 7)
			{
				mCmnProp.Icallback.cmnWriteCompleted(true, "write COM" + (mTmpPlcInfo.eConnectType -2) + " completed", mCmnProp.mDataObj);
			}
			else if(mTmpPlcInfo.eConnectType >= 8 && mTmpPlcInfo.eConnectType <= 13)
			{
				mCmnProp.Icallback.cmnWriteCompleted(true, "write NET" + (mTmpPlcInfo.eConnectType - 8) + " completed", mCmnProp.mDataObj);
			}
		}
		
		if (nCheckInfo==0) {
			//通信正常
			PlcRegCmnStcTools.getLastCmnInfo().nErrorCode=0;
		}
    	return true;
    }
    
    /**
     * 获得通信状态
     * @return
     */
    public int getCmnCodeInfo()
    {
    	return nErrorCodeInfo;
    }
    
    /**
     * 转发数据到其他端口
     * @param eConnect
     * @param nSendData
     * @return
     */
    public static void turnDataToOtherPort(TurnDataProp mTurnData)
    {
    	if(null == mTurnData || mTurnData.nSendData == null) return ;
    	
    	SKCommThread mThreadObj = SKCommThread.getComnThreadObj(mTurnData.eConnect);
		if(null == mThreadObj)
		{
			return ;
		}
		
		mThreadObj.getCmnRefreashHandler().obtainMessage(MODULE.TURN_MSG_DATA, mTurnData).sendToTarget();
    }
    
    /**
     * 处理自由口或转发过来的数据
     */
    public void dealTurnData(TurnDataProp mTurnData)
    {
    	if(null == mTurnData) return ;
    	
    	if(null == m_nConnectObj) return ;
    	mTmpPlcInfo.eConnectType = m_nConnectObj.geteConnectPort();
		
		boolean bSuccess = CmnPortManage.getInstance().sendData(mTmpPlcInfo, mTurnData.nSendData);
    	if(!bSuccess)
    	{
    		return ;
    	}
    	
		/*不需要等待，直接返回*/
//		if(!mTurnData.bWaitRcv)
//		{
//			return ;
//		}
//		
//    	int nReadTimes = 0;
//    	int nRcvLen = 0;
//    	boolean bFirst = true;
//    	nTmpRecList.clear();
//		while(nReadTimes * m_nGetBuffInterval < 500)
//		{
//			try {
//				Thread.sleep(m_nGetBuffInterval);  //一次休眠5毫秒
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			nReadTimes++;
//			boolean bGetOk = CmnPortManage.getInstance().getData(mTmpPlcInfo, nTmpRecList);
//			if(!bFirst && nRcvLen == nTmpRecList.size())
//			{
//				break;
//			}
//			if(bGetOk)
//			{
//				nRcvLen = nTmpRecList.size();
//				bFirst = false;
//			}
//		}   // end while
//		
//		int nDataSize = nTmpRecList.size();
//		if(nDataSize > 0)
//		{
//			short nTurnPort = 0;
//			boolean bTurnData = m_nConnectObj.isbUseRelationPort();
//			if(bTurnData)
//			{
//				nTurnPort = m_nConnectObj.geteRelationPort();
//				byte[] nTmpBytes = new byte[nDataSize];
//				for(int i = 0; i < nDataSize; i++)
//				{
//					nTmpBytes[i] = nTmpRecList.get(i);
//				}
//				
//				TurnDataProp mNewTurnData = new TurnDataProp();
//				mNewTurnData.bWaitRcv = false;
//				mNewTurnData.eConnect = nTurnPort;
//				mNewTurnData.nSendData = nTmpBytes;
//				SKCommThread.turnDataToOtherPort(mNewTurnData);
//			}
//		}
    }
}