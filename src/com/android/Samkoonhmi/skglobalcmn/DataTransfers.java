package com.android.Samkoonhmi.skglobalcmn;

import java.util.Vector;

import com.android.Samkoonhmi.databaseinterface.DataTransBiz;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skglobalcmn.DataTransInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.ProtocolInterfaces;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AddrPropArray;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcCmnDataCtlObj;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DataTransfers {

	/* 资料传输线程 */
	private HandlerThread mDataTransThread = null;
	private DataTransHandler mDataTransHandler = null;
	
	/* plc信息结构体 */
	private PlcSampInfo mPlcInfo = new PlcSampInfo();
	private Vector<Byte > nTmpByteList = new Vector<Byte >();
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
	
	private int nRefreshCircle = 300;
	
	/*保存当前时间*/
	private int nCurrTimes = 0;
	private int nMaxTransInterval = 0;
	private Vector<Boolean > bTransCompleteList = new Vector<Boolean >();
	private Vector<Byte > nTriggerList = new Vector<Byte >();
	
	private DataTransfers() {
	}

	/* 获取资料传输线程的单例 */
	private static DataTransfers mDataTransObj = null;
	public synchronized static DataTransfers getInstance() {
		if (null == mDataTransObj) {
			mDataTransObj = new DataTransfers();
		}
		return mDataTransObj;
	}
	
	public void startDataTrans()
	{
		/*查询数据*/
		DataTransBiz.select();
		
		int nSize = DataTransInfo.getInstance().getmDataTransList().size();
		if(nSize <= 0) return ;
		
		bTransCompleteList.clear();
		for(int i = 0; i < nSize; i++)
		{
			int nTmpInterval = DataTransInfo.getInstance().getmDataTransList().get(i).getnInterval();
			if(nTmpInterval > nMaxTransInterval)
			{
				nMaxTransInterval = nTmpInterval;
			}
			
			bTransCompleteList.add(true);
			nTriggerList.add((byte)0);
		}
		
		getDataTransHandler().sendEmptyMessageDelayed(MODULE.DATA_TRANS_REFRESH, nRefreshCircle);
	}

	/**
	 * 获得通知线程的句柄
	 * 
	 * @return
	 */
	public DataTransHandler getDataTransHandler() {
		if (null == mDataTransHandler) {
			/* 创建和启动线程 */
			if (null == mDataTransThread) {
				mDataTransThread = new HandlerThread("DataTransfers");
				mDataTransThread.start();
			}

			/* 创建通知句柄 */
			mDataTransHandler = new DataTransHandler(mDataTransThread.getLooper());
		}

		return mDataTransHandler;
	}
	
	/**
	 * 资料传输句柄
	 * @author latory
	 *
	 */
	public class DataTransHandler extends Handler {
		public DataTransHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) 
			{
			case MODULE.DATA_TRANS_REFRESH: 
			{
				refreashDataTrans();
				getDataTransHandler().sendEmptyMessageDelayed(MODULE.DATA_TRANS_REFRESH, nRefreshCircle);
				break;
			}
			case MODULE.TRANS_ONCE_DATA:
			{
				transWriteData((Integer)msg.obj);
				break;
			}
			default: {
				break;
			}
			}
		}
	}
	
	/*读写回调*/
	SKCommThread.ICmnCompletedCallback mPlcCmnCallback = new SKCommThread.ICmnCompletedCallback() {
		
		@Override
		public void cmnWriteCompleted(boolean bSuccess, String sErrorInfo,Object mObjMsg) 
		{
		}
		
		@Override
		public void cmnReadCompleted(boolean bSuccess, String sErrorInfo,Object mObjMsg) 
		{
			if(null != mObjMsg)
			{
				getDataTransHandler().obtainMessage(MODULE.TRANS_ONCE_DATA, mObjMsg).sendToTarget();
				
				int nGroupId = (Integer)mObjMsg;
				if(nGroupId >=0 && nGroupId < bTransCompleteList.size())
				{
					/*完成标志*/
					bTransCompleteList.set(nGroupId, true);
				}
			}
		}
	};
	
	/**
	 * 资料传输刷新线程
	 */
	private void refreashDataTrans()
	{
		int times = 0;
		int nSize = 0;
		
		/*触发方式*/
		nSize = DataTransInfo.getInstance().getmDataTransList().size();
		for(int i = 0; i < nSize; i++)
		{
			int nTransType = DataTransInfo.getInstance().getmDataTransList().get(i).getnTransType();
			AddrProp mTriggerAddr = DataTransInfo.getInstance().getmDataTransList().get(i).getmTriggerAddr();
			if(nTransType == 2)
			{
				nTmpByteList.clear();
				mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
				mSendData.eDataType = DATA_TYPE.BIT_1;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;
				PlcRegCmnStcTools.getRegBytesData(mTriggerAddr, nTmpByteList, mSendData);
				if(!nTmpByteList.isEmpty())
				{
					int nAddrValue = nTmpByteList.get(0);
					if(nAddrValue == 1)
					{
						if(nTriggerList.get(i) == 0)
						{
							transReadData(i, 2);
						}
						
						/*是否自动复位*/
						if(DataTransInfo.getInstance().getmDataTransList().get(i).isbAutoReset())
						{
							nTriggerList.set(i,(byte)0);
							mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
							
							byte[] nSetByte = new byte[1];
							nSetByte[0] = 0;
							PlcRegCmnStcTools.setRegBytesData(mTriggerAddr, nSetByte, mSendData);
						}
						else
						{
							nTriggerList.set(i,(byte)1);
						}
					}
					else
					{
						nTriggerList.set(i,(byte)0);
					}
				}//end if nTmpByteList
			}//end if nTransType
		}// end for
		
		boolean bHaveTrans = false;
		nSize = bTransCompleteList.size();
		for(int i = 0; i < nSize; i++)
		{
			if(!bTransCompleteList.get(i))
			{
				bHaveTrans = true;
				break;
			}
		}
		
		/*如果有资料传输在进行*/
		while(bHaveTrans && times < 6)
		{
			bHaveTrans = false;
			for(int i = 0; i < nSize; i++)
			{
				if(!bTransCompleteList.get(i))
				{
					bHaveTrans = true;
					break;
				}
			}
			
			/*休眠150ms*/
			try {
				Thread.sleep(500);  //一次休眠50毫秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nCurrTimes += 500;
			times++;
		}
		
		nCurrTimes += nRefreshCircle;
		
		/*周期资料传输*/
		nSize = DataTransInfo.getInstance().getmDataTransList().size();
		for(int i = 0; i < nSize; i++)
		{
			int nTransType = DataTransInfo.getInstance().getmDataTransList().get(i).getnTransType();
			if(nTransType == 1)
			{
				int nTmpInterval = DataTransInfo.getInstance().getmDataTransList().get(i).getnInterval();
				if(nTmpInterval*1000 <= nCurrTimes)
				{
					bTransCompleteList.set(i, false);
					transReadData(i, 2);
				}
			}
		}
		
		/*如果完成了，则复位时间*/
		if(!bHaveTrans)
		{
			if(nCurrTimes <= 0 || nCurrTimes > nMaxTransInterval*1000)
			{
				nCurrTimes = 1000;
			}
		}
	}
	
	/**
	 * 从源地址读数据回来
	 * @param nGroupIndex
	 * @param nTransType
	 */
	private void transReadData(int nGroupIndex, int nTransType)
	{
		/*判断组下标是否合法*/
		int nSize = DataTransInfo.getInstance().getmDataTransList().size();
		if(nGroupIndex < 0 || nGroupIndex >= nSize) return ;
		
		short nAddrType = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getnAddrType();
		short nTransLen = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getnTransLen();
		
		/*如果源地址和目标地址有一个不存在，则返回*/
		AddrProp mSourceAddr = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getmSourceAddr();
		AddrProp mTargetAddr = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getmTargetAddr();
		if(null == mSourceAddr || null ==mTargetAddr) return;
		
		switch(nAddrType)
		{
		case 1:
		case 2:
		{
			mSourceAddr.nAddrLen = nTransLen;
			break;
		}
		case 3:
		{
			mSourceAddr.nAddrLen = nTransLen*2;
			break;
		}
		default:
		{
			return ;
		}
		}

		/* 取得连接类型 */
		short eConnect = mSourceAddr.eConnectType;
		SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnect);
		if (null == mThreadObj) {
			Log.e("readPlcDataByUser", "Find the connection type interface:" + eConnect
					+ " failed The type of connection may be wrong");
			return ;
		}
		
		/*内部地址，则读取完成*/
		if(eConnect == 1)
		{
			/*完成通知*/
			mPlcCmnCallback.cmnReadCompleted(true, "read local addr completed", nGroupIndex);
			return ;
		}

		/* 在不同连接类型中，按照连接协议来取得连接协议的对象 */
		PlcCmnDataCtlObj mCmnSubProp = new PlcCmnDataCtlObj();
		mCmnSubProp.Icallback = mPlcCmnCallback;
		mCmnSubProp.mDataObj = nGroupIndex;
		mCmnSubProp.mAddrList = new AddrPropArray();
		
		Vector<AddrProp > mTmpAddrList = new Vector<AddrProp >();
		mTmpAddrList.add(mSourceAddr);

		ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
		if (null != mProtocolObj) 
		{
			mPlcInfo.eConnectType = eConnect;
			mPlcInfo.nProtocolIndex = mSourceAddr.nUserPlcId;
			mPlcInfo.sProtocolName = mSourceAddr.sPlcProtocol;
			
			/*如果地址类型不是主站*/
			PROTOCOL_TYPE ePlcType = mProtocolObj.getProtocolType(mPlcInfo);
			if(ePlcType != PROTOCOL_TYPE.MASTER_MODEL)
			{
				/*完成通知*/
				mPlcCmnCallback.cmnReadCompleted(true, "read local addr completed", nGroupIndex);
				return ;
			}

			/* 求最大读写长度 */
			int nConnectSize = SystemInfo.getPlcConnectionList().size();
			PlcConnectionInfo mConnect = null;
			for (int i = 0; i < nConnectSize; i++) 
			{
				if (SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == eConnect) 
				{
					mConnect = SystemInfo.getPlcConnectionList().get(i);
				}
			}
			
			if (mConnect != null) 
			{
				int nMaxRWlen = -1;
				int nProtocolSize = mConnect.getPlcAttributeList().size();
				for (int i = 0; i < nProtocolSize; i++) 
				{
					if (mConnect.getPlcAttributeList().get(i).getnUserPlcId() == mSourceAddr.nUserPlcId) 
					{
						nMaxRWlen = mConnect.getPlcAttributeList().get(i).getnMaxRWLen();
					}
				}

				if (nMaxRWlen > 0) 
				{
					mProtocolObj.sortOutAddrList(mTmpAddrList, mCmnSubProp.mAddrList, mPlcInfo, nMaxRWlen, false);
					
					/*整理后的地址，重新赋值*/
					if (mCmnSubProp.mAddrList.mSortAddrList != null)
					{
						int nAddrLen = mCmnSubProp.mAddrList.mSortAddrList.length;
						for (int i = 0; i < nAddrLen; i++) 
						{
							mCmnSubProp.mAddrList.mSortAddrList[i].eConnectType = (short) mPlcInfo.eConnectType;
							mCmnSubProp.mAddrList.mSortAddrList[i].sPlcProtocol = mPlcInfo.sProtocolName;
						}
					}
				}
			}
		}
		
		mThreadObj.getCmnRefreashHandler().obtainMessage(MODULE.USER_READ, mCmnSubProp).sendToTarget();
	}
	
	/**
	 * 然后把数据写到目标地址
	 * @param nGroupIndex
	 */
	private void transWriteData(int nGroupIndex)
	{
		/*判断组下标是否合法*/
		int nSize = DataTransInfo.getInstance().getmDataTransList().size();
		if(nGroupIndex < 0 || nGroupIndex >= nSize) return ;
		
		short nAddrType = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getnAddrType();
		short nTransLen = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getnTransLen();
		
		/*如果源地址和目标地址有一个不存在，则返回*/
		AddrProp mSourceAddr = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getmSourceAddr();
		AddrProp mTargetAddr = DataTransInfo.getInstance().getmDataTransList().get(nGroupIndex).getmTargetAddr();
		if(null == mSourceAddr || null ==mTargetAddr) return;
		
		int nDataByteLen = 0;
		switch(nAddrType)
		{
		case 1:
		{
			mSourceAddr.nAddrLen = nTransLen;
			mTargetAddr.nAddrLen = nTransLen;
			nDataByteLen = 1;
			break;
		}
		case 2:
		{
			mSourceAddr.nAddrLen = nTransLen;
			mTargetAddr.nAddrLen = nTransLen;
			nDataByteLen = 2;
			break;
		}
		case 3:
		{
			mSourceAddr.nAddrLen = nTransLen*2;
			mTargetAddr.nAddrLen = nTransLen*2;
			nDataByteLen = 2;
			break;
		}
		default:
		{
			return ;
		}
		}
		
		nTmpByteList.clear();
		mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
		mSendData.eDataType = DATA_TYPE.INT_16;
		if(nAddrType == 1)
		{
			mSendData.eDataType = DATA_TYPE.BIT_1;
		}
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;
		PlcRegCmnStcTools.getRegBytesData(mSourceAddr, nTmpByteList, mSendData);
		
		
		/*取得连接类型*/
		SKCommThread mThreadObj = SKCommThread.getComnThreadObj(mTargetAddr.eConnectType);
		if(null == mThreadObj)
		{
			Log.e("getRegBytesData", "查找连接类型的的接口:" + mTargetAddr.eConnectType + " 失败，连接类型可能不对");
			return;
		}

		/*在不同连接类型中，按照连接协议来取得连接协议的对象*/
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
		if (null != mProtocolObj) 
		{
			mPlcInfo.eConnectType = mTargetAddr.eConnectType;
			mPlcInfo.nProtocolIndex = mTargetAddr.nUserPlcId;
			mPlcInfo.sProtocolName = mTargetAddr.sPlcProtocol;
			
			/*如果不是主站，则不需要进行通信*/
			PROTOCOL_TYPE ePlcType = mProtocolObj.getProtocolType(mPlcInfo);
			if(mTargetAddr.eConnectType == 1 || ePlcType != PROTOCOL_TYPE.MASTER_MODEL)
			{
				mSendData.eDataType = DATA_TYPE.INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
				
				int nSetSize = nTmpByteList.size();
				byte[] nSetByteList = new byte[nSetSize];
				for(int k = 0; k < nSetSize; k++)
				{
					nSetByteList[k] = nTmpByteList.get(k);
				}
				
				PlcRegCmnStcTools.setRegBytesData(mTargetAddr, nSetByteList, mSendData);
				
				/*完成通知*/
				mPlcCmnCallback.cmnWriteCompleted(true, "write address completed", nGroupIndex);
				return ;
			}
			
			AddrPropArray mAddrList = new AddrPropArray();
			Vector<AddrProp > mTmpAddrList = new Vector<AddrProp >();
			mTmpAddrList.add(mTargetAddr);
			
			/* 求最大读写长度 */
			int nConnectSize = SystemInfo.getPlcConnectionList().size();
			PlcConnectionInfo mConnect = null;
			for (int i = 0; i < nConnectSize; i++) 
			{
				if (SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == mTargetAddr.eConnectType) 
				{
					mConnect = SystemInfo.getPlcConnectionList().get(i);
				}
			}
			
			if (mConnect != null) 
			{
				int nMaxRWlen = -1;
				int nProtocolSize = mConnect.getPlcAttributeList().size();
				for (int i = 0; i < nProtocolSize; i++) 
				{
					if (mConnect.getPlcAttributeList().get(i).getnUserPlcId() == mTargetAddr.nUserPlcId) 
					{
						nMaxRWlen = mConnect.getPlcAttributeList().get(i).getnMaxRWLen();
					}
				}

				if (nMaxRWlen > 0) 
				{
					mProtocolObj.sortOutAddrList(mTmpAddrList, mAddrList, mPlcInfo, nMaxRWlen, true);
				}
			}
			
			if(mAddrList.mSortAddrList != null)
			{
				/*根据地址长度分发设置值*/
				int nLen = mAddrList.mSortAddrList.length;
				for(int i = 0; i < nLen; i++)
				{
					AddrProp mAddr = mAddrList.mSortAddrList[i];
					Vector<Byte > nDataList = new Vector<Byte >();
					nDataList.clear();
					if(null != mAddr)
					{
						mAddr.eConnectType = (short) mPlcInfo.eConnectType;
						mAddr.sPlcProtocol = mPlcInfo.sProtocolName;
						
						int nSetLen = mAddr.nAddrLen * nDataByteLen;
						for(int k = 0; k < nSetLen; k++)
						{
							if(nTmpByteList.size() > 0)
							{
								nDataList.add(nTmpByteList.get(0));
								nTmpByteList.removeElementAt(0);
							}
							else
							{
								break;
							}
						}
						
						mSendData.eDataType = DATA_TYPE.INT_16;
						mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
						
						int nSetSize = nDataList.size();
						byte[] nSetByteList = new byte[nSetSize];
						for(int k = 0; k < nSetSize; k++)
						{
							nSetByteList[k] = nDataList.get(k);
						}
						PlcRegCmnStcTools.setRegBytesData(mAddr, nSetByteList, mSendData);
					}
				}//end for addr len
				
				/*休眠500ms*/
				try {
					Thread.sleep(500); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}// end mAddrList.mSortAddrList != null
		}// end if (null != mProtocolObj) 
	}// end transWriteData
}
