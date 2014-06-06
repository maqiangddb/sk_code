package com.android.Samkoonhmi.skglobalcmn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKSaveThread.CollectRecordProp;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skglobalcmn.EditDataCollectProp;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.ProtocolInterfaces;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.pmem.RestorePowerSave;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skglobalcmn.DataCollect.HistoryDataProp;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AddrPropArray;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcCmnDataCtlObj;
import com.android.Samkoonhmi.util.PlcCmnWriteCtlObj;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SkGlobalBackThread {

	/* 数据库读取线程 */
	private HandlerThread mBackThread = null;
	private GlobalBackHandler mBackHandler = null;

	/* plc信息结构体 */
	private static PlcSampInfo mPlcInfo = new PlcSampInfo();

	private SkGlobalBackThread() {
	}

	/* 获取全局后台线程的单例 */
	private static SkGlobalBackThread m_mGlobalBackObj = null;

	public synchronized static SkGlobalBackThread getInstance() {
		if (null == m_mGlobalBackObj) {
			m_mGlobalBackObj = new SkGlobalBackThread();
		}
		return m_mGlobalBackObj;
	}

	/**
	 * 获得通知线程的句柄
	 * 
	 * @return
	 */
	public GlobalBackHandler getGlobalBackHandler() {
		if (null == mBackHandler) {
			/* 创建和启动线程 */
			if (null == mBackThread) {
				mBackThread = new HandlerThread("GlobalBackHandler");
				mBackThread.start();
			}

			/* 创建通知句柄 */
			mBackHandler = new GlobalBackHandler(mBackThread.getLooper());
		}

		return mBackHandler;
	}

	/**
	 * 线程截获消息,处理消息
	 * 
	 * @author Latory
	 * 
	 */
	public class GlobalBackHandler extends Handler {
		public GlobalBackHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MODULE.READ_HISTORY_FROM_DATABASE: {
				
				/*如果数据采集还没初始化，则等待*/
				if(!DataCollect.getInstance().getDataCollectInit())
				{
					try {
	    				Thread.sleep(500);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
					
					getGlobalBackHandler().obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, msg.obj).sendToTarget();
					break;
				}
				
				/* 通知读数据库 */
				IReadHistoryCallback mReadCallback = (IReadHistoryCallback) msg.obj;
				if (mReadCallback != null) {
					DataCollect.getInstance().sendMsgWriteDatabase();
					getGlobalBackHandler().obtainMessage(MODULE.AFTER_SAVE_DATABASE, mReadCallback).sendToTarget();
				}
				break;
			}
			case MODULE.WRITE_HISTORY_TO_DATABASE: {
				
				/*如果数据采集还没初始化，则等待*/
				if(!DataCollect.getInstance().getDataCollectInit())
				{
					try {
	    				Thread.sleep(500);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
					
					getGlobalBackHandler().obtainMessage(MODULE.WRITE_HISTORY_TO_DATABASE, msg.obj).sendToTarget();
					break;
				}
				
				/* 通知写数据到数据库 */
				DataCollect.getInstance().writeDataToDatabase((EditDataCollectProp) msg.obj);
				break;
			}
			case MODULE.WRITE_POWER_SAVE_DATABASE:
			{
				/*还原数据采集的掉电保存*/
				RestorePowerSave.restoreAllPowerData();
				break;
			}
			case MODULE.WRITE_HISTORY_TO_FILE: {
				
				/*如果数据采集还没初始化，则等待*/
				if(!DataCollect.getInstance().getDataCollectInit())
				{
					try {
	    				Thread.sleep(500);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
					
					getGlobalBackHandler().obtainMessage(MODULE.WRITE_HISTORY_TO_FILE, msg.obj).sendToTarget();
					break;
				}
				
				DataCollect.getInstance().sendMsgWriteDatabase();

				/* 通知写数据到数据库 */
				getGlobalBackHandler().obtainMessage(MODULE.BEGAI_SAVE_HISTORY_TO_FILE, msg.obj).sendToTarget();
				break;
			}
			case MODULE.BEGAI_SAVE_HISTORY_TO_FILE: {
				
				/*如果数据采集还没初始化，则等待*/
				if(!DataCollect.getInstance().getDataCollectInit())
				{
					try {
	    				Thread.sleep(500);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
					
					getGlobalBackHandler().obtainMessage(MODULE.BEGAI_SAVE_HISTORY_TO_FILE, msg.obj).sendToTarget();
					break;
				}
				
				/* 通知写数据到数据库 */
				boolean result = DataCollect.getInstance().writeGroupDataToFile((EditDataCollectProp) msg.obj);
				if(result){
					SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().historySave());
				}else{
					SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().historySave());
				}
				break;
			}
			case MODULE.DELETE_HISTORY_FROM_DATABASE: {
				
				/*如果数据采集还没初始化，则等待*/
				if(!DataCollect.getInstance().getDataCollectInit())
				{
					try {
	    				Thread.sleep(500);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
					
					getGlobalBackHandler().obtainMessage(MODULE.DELETE_HISTORY_FROM_DATABASE, msg.obj).sendToTarget();
					break;
				}
				
				/* 删除数据库中的数据 */
				deleteDatabase((EditDataCollectProp) msg.obj);
				
				/* 如果是删除时间段，需要重新init */
				EditDataCollectProp data = (EditDataCollectProp) msg.obj;
				//
				if(data!=null){
					if(data.nEndTime!=0){
						DataCollect.getInstance().getCollectNoticHandler().
							obtainMessage(MODULE.DATA_COLLECT_CLEAR_PART).sendToTarget();
					}
				}
				break;
			}
			case MODULE.AFTER_SAVE_DATABASE: {
				
				/*如果数据采集还没初始化，则等待*/
				if(!DataCollect.getInstance().getDataCollectInit())
				{
					try {
	    				Thread.sleep(500);  
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
					
					getGlobalBackHandler().obtainMessage(MODULE.AFTER_SAVE_DATABASE, msg.obj).sendToTarget();
					break;
				}
				
				/* 通知读数据库 */
				IReadHistoryCallback mReadCallback = (IReadHistoryCallback) msg.obj;
				if (mReadCallback != null) {
					mReadCallback.begainReadHistory();
				}
				break;
			}
			case MODULE.USER_READ_PLC: {
				/* 读PLC */
				readPlcDataByUser((PlcCmnDataCtlObj) msg.obj);
				break;
			}
			case MODULE.USER_WRITE_PLC: {
				/* 写PLC */
				writePlcDataByUser((PlcCmnWriteCtlObj) msg.obj);
				break;
			}
			default: {
				break;
			}
			}
		}
	}

	/**
	 * 读历史数据的回调接口
	 * 
	 * @author latory
	 * 
	 */
	public interface IReadHistoryCallback {
		public void begainReadHistory();
	}



	/**
	 * 删除数据库
	 * 
	 * @param mEditData
	 */
	private void deleteDatabase(EditDataCollectProp mEditData) {
		if (null == mEditData)
			return;

		/* 取总数 */
		SKDataBaseInterface mCollectDbObj = SkGlobalData
				.getDataCollectDatabase();
		if (null == mCollectDbObj)
			return;

		String sSqlStr = "delete from dataCollect" + mEditData.nGroupId;
		if (mEditData.nRecordTimeLen > 0) {
			sSqlStr += " where id in(select id from dataCollect"
					+ mEditData.nGroupId + " order by nMillis limit "
					+ mEditData.nRecordTimeLen + ")";
		}else if(mEditData.nEndTime!=0){
			if(mEditData.bHasEndTime){
				sSqlStr += " where nMillis>="+mEditData.nStartTime
						+" and nMillis<="+mEditData.nEndTime;
			}else {
				sSqlStr += " where nMillis>="+mEditData.nStartTime;
			}
		}
		//Log.d("SKScene", "delete gid:"+mEditData.nGroupId);

		mCollectDbObj.beginTransaction();
		mCollectDbObj.execSql(sSqlStr);
		mCollectDbObj.commitTransaction();
		mCollectDbObj.endTransaction();
	}

	/**
	 * 用户自己读地址
	 * 
	 * @param mCmnProp
	 * @return
	 */
	private boolean readPlcDataByUser(PlcCmnDataCtlObj mCmnProp) {
		if (null == mCmnProp || mCmnProp.mAddrList == null)
			return false;

		int nAddrSize = mCmnProp.mAddrList.mSortAddrList.length;
		HashMap<Short, Vector<AddrProp>> eConnectTypeMap = new HashMap<Short, Vector<AddrProp>>();

		Vector<AddrProp> tmpAddrList = null;
		AddrProp mTmpAddr = null;

		/* 按照连接类型把所有地址分类添加到eConnectTypeMap容器中 */
		for (int i = 0; i < nAddrSize; i++) {
			mTmpAddr = mCmnProp.mAddrList.mSortAddrList[i];
			if (null == mTmpAddr)
				continue;

			/* 判断通信是否已经创建 */
			if (eConnectTypeMap.containsKey(mTmpAddr.nUserPlcId)) {
				tmpAddrList = eConnectTypeMap.get(mTmpAddr.nUserPlcId);
				if (null == tmpAddrList)
					continue;

				tmpAddrList.add(mTmpAddr);
				eConnectTypeMap.put(mTmpAddr.nUserPlcId, tmpAddrList);
			} else {
				tmpAddrList = new Vector<AddrProp>();
				tmpAddrList.clear();
				tmpAddrList.add(mTmpAddr);
				eConnectTypeMap.put(mTmpAddr.nUserPlcId, tmpAddrList);
			}
		}

		/* 在eConnectTypeMap容器中，按照连接类型取得协议 */
		Set<Short> connectList = eConnectTypeMap.keySet();
		for (Iterator<Short> it = connectList.iterator(); it.hasNext();) {
			short nUserPlcId = it.next();
			if (eConnectTypeMap.get(nUserPlcId).isEmpty())
				continue;

			short eConnect = eConnectTypeMap.get(nUserPlcId).get(0).eConnectType;

			/* 取得连接类型 */
			SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnect);
			if (null == mThreadObj) {
				Log.e("readPlcDataByUser", "Find the connection type interface:" + eConnect
						+ " failed The type of connection may be wrong");
				continue;
			}
			
			/*内部地址，则读取完成*/
			if(eConnect == 1)
			{
				/*完成通知*/
				mCmnProp.Icallback.cmnReadCompleted(true, "read local addr completed", mCmnProp.mDataObj);
				continue;
			}
			
			/* 在不同连接类型中，按照连接协议来取得连接协议的对象 */
			PlcCmnDataCtlObj mCmnSubProp = new PlcCmnDataCtlObj();
			mCmnSubProp.Icallback = mCmnProp.Icallback;
			mCmnSubProp.mDataObj = mCmnProp.mDataObj;
			mCmnSubProp.mAddrList = new AddrPropArray();

			ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
			if (null != mProtocolObj) {
				/*如果地址类型不是主站*/
				mPlcInfo.eConnectType = eConnect;
				mPlcInfo.nProtocolIndex = nUserPlcId;
				mPlcInfo.sProtocolName = eConnectTypeMap.get(nUserPlcId).get(0).sPlcProtocol;
				PROTOCOL_TYPE ePlcType = mProtocolObj.getProtocolType(mPlcInfo);
				if(ePlcType != PROTOCOL_TYPE.MASTER_MODEL)
				{
					/*完成通知*/
					mCmnProp.Icallback.cmnReadCompleted(true, "read not mast plc", mCmnProp.mDataObj);
					continue ;
				}
				
				/* 求最大读写长度 */
				int nConnectSize = SystemInfo.getPlcConnectionList().size();
				PlcConnectionInfo mConnect = null;
				for (int i = 0; i < nConnectSize; i++) {
					if (SystemInfo.getPlcConnectionList().get(i)
							.geteConnectPort() == eConnect) {
						mConnect = SystemInfo.getPlcConnectionList().get(i);
					}
				}

				if (mConnect != null) {
					int nMaxRWlen = -1;
					int nProtocolSize = mConnect.getPlcAttributeList().size();
					for (int i = 0; i < nProtocolSize; i++) {
						if (mConnect.getPlcAttributeList().get(i)
								.getnUserPlcId() == nUserPlcId) {
							nMaxRWlen = mConnect.getPlcAttributeList().get(i)
									.getnMaxRWLen();
						}
					}

					if (nMaxRWlen > 0) {
						mProtocolObj.sortOutAddrList(
								eConnectTypeMap.get(nUserPlcId),
								mCmnSubProp.mAddrList, mPlcInfo, nMaxRWlen,
								false);

						if (mCmnSubProp.mAddrList.mSortAddrList != null) {
							int nSize = mCmnSubProp.mAddrList.mSortAddrList.length;
							for (int i = 0; i < nSize; i++) {
								mCmnSubProp.mAddrList.mSortAddrList[i].eConnectType = (short) mPlcInfo.eConnectType;
								mCmnSubProp.mAddrList.mSortAddrList[i].sPlcProtocol = mPlcInfo.sProtocolName;
							}
						}
					}
				}
			}

			mThreadObj.getCmnRefreashHandler()
					.obtainMessage(MODULE.USER_READ, mCmnSubProp)
					.sendToTarget();

		}// 按连接取地址结束

		return true;
	}

	/**
	 * 用户自己写地址, 这个函数一定要先不地址按照用户自定义协议号整理好
	 * 
	 * @param mAddr
	 * @param nDataList
	 * @param bShowPragress
	 * @param nCtlCmnType
	 * @return
	 */
	private boolean writePlcDataByUser(PlcCmnWriteCtlObj mCmnProp) {
		if (null == mCmnProp || mCmnProp.mAddrProp == null)
			return false;

		/* 取得连接类型 */
		short eConnect = mCmnProp.mAddrProp.eConnectType;
		SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnect);
		if (null == mThreadObj) {
			Log.e("writePlcDataByUser", "Find the connection type interface:" + eConnect
					+ " failed The type of connection may be wrong");
			return false;
		}

		mThreadObj.getCmnRefreashHandler()
				.obtainMessage(MODULE.USER_WRITE, mCmnProp).sendToTarget();
		return true;
	}
}
