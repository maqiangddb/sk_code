package com.android.Samkoonhmi.skglobalcmn;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKSaveThread;
import com.android.Samkoonhmi.SKSaveThread.CollectSaveProp;
import com.android.Samkoonhmi.databaseinterface.DataCollectBiz;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skglobalcmn.CollectDataInfo;
import com.android.Samkoonhmi.model.skglobalcmn.EditDataCollectProp;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.print.AKPrint;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.FULL_DEAL_TYPE;
import com.android.Samkoonhmi.skenum.PRINT_MODEL;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skenum.SAMP_TYPE;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.FileOper;
import com.android.Samkoonhmi.util.HistoryCollectProp;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

/**
 * 数据采集的数据处理中心
 * @author Latory
 */
public class DataCollect {

	private static final String TAG = "DataCollect";
	/* 定义实时采集 */
	private final static int nRealTimeCollect = 1;

	/* 定义历史数据采集 */
	private final static int nHistoryCollect = 2;

	/* 数据库中最大保存数量每组10万条 */
	private long nSampTotalNum = 100000;
	private Vector<Long> nDatabaseNumList = new Vector<Long>();

	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();

	/* get data list */
	private Vector<Integer> tmpDataList = new Vector<Integer>();
	private Vector<Byte> tmpByteList = new Vector<Byte>();
	private Vector<Double> tmpDoubleList = new Vector<Double>();

	private Vector<Boolean> bHistoryFull = new Vector<Boolean>();
	
	private ArrayList<Integer> tmpGroupId;//保持时段删除的组

	/* 控制地址控制是否上升沿导出csv文件 */
	private Vector<Integer> m_bHistorySaveReset = new Vector<Integer>();

	/* 历史保存的数据, 第一个容器存组号，第二个容器存缓存的记录条数 */
	private Vector<Vector<HistoryDataProp>> m_nHistoryDataList = new Vector<Vector<HistoryDataProp>>();

	/* 最后保存的数据，容器为组号 */
	private Vector<HistoryDataProp> mLastSaveList = new Vector<HistoryDataProp>();
	private Vector<Long> mLastHistoryTime = new Vector<Long>();
	private Vector<Long> mLastRealTimeTime = new Vector<Long>();

	/* 一次替换的数量 */
	private int nTmpHistoryDataNum = 300;
	
	private long nLastSaveTime = 0;

	/* 最小采集频率，单位是毫秒每100ms */
	private int nMinCollectCycle = 200;

	/* 插入数据库的集合 */
	private ContentValues m_tmpValues = new ContentValues();

	/* 获得系统时间的对象 */
	private Time systemTime = new Time();

	/* 刷新线程 */
	private HandlerThread mNoticThread = null;
	private CmnHandler mHandler = null;

	private Vector<DataCollectCallBack> m_noticCallBackList = null;
	private boolean m_bThreadLoop = false;

	private DataCollect() {
	}

	public boolean getDataCollectInit() {
		return m_bThreadLoop;
	}

	private int collectState;
	private final int COLLECT_WAIT=0;
	private final int COLLECT_START=1;
	private final int COLLECT_CLEAR=2;
	
	/**
	 * 通知线程的单实例
	 */
	private static DataCollect m_mDataCollectObj = null;
	public static DataCollect getInstance() {
		if (null == m_mDataCollectObj) {
			m_mDataCollectObj = new DataCollect();
		}
		return m_mDataCollectObj;
	}

	/**
	 * 获得通知线程的句柄
	 * @return
	 */
	public CmnHandler getCollectNoticHandler() {
		if (null == mHandler) {
			/* 创建和启动线程 */
			if (null == mNoticThread) {
				mNoticThread = new HandlerThread("datacollect");
				mNoticThread.start();
			}

			/* 创建通知句柄 */
			mHandler = new CmnHandler(mNoticThread.getLooper());
		}

		return mHandler;
	}

	/**
	 * 历史数据的属性，包含时间
	 * @author latory
	 */
	public class HistoryDataProp {
		public Vector<String> nDataList = new Vector<String>(); // 对应每个地址数据
		public long nMillisTime = 0; // 采集的时间
		public boolean bCollectFull = false;
	}

	/**
	 * 回调接口的属性类
	 * @author latory
	 */
	public class DataCollectCallBack {
		int eCollectType; // 1是实时采集，2是历史采集
		IPlcNoticCallBack mCallbackInterface; // 回调接口
		Vector<Integer> nGroupIds = new Vector<Integer>(); // 组号的集合
		Vector<HistoryDataProp> nDataList = new Vector<HistoryDataProp>();
	}

	/**
	 * 回调接口，回调到控件更新，每个有是否显示，是否触控，或者状态变化的控件都要实现这接口。 取得nstatusValue值后
	 * 一定要根据数据类型调用PlcRegCmnStcTools.intToUInt(nStatusValue);等 转换成你需要的数据类型。
	 * @author Latory
	 */
	public interface IPlcNoticCallBack {
		/* 返回的值都存在int型的内存中使用方法参照以上说明 */
		public void addrValueNotic(Vector<HistoryDataProp> nValueList,
				int nCollectRate);

		/* 删除数据库后的通知 */
		public void noticDelDatabase(int gid);
	}

	/**
	 * 线程启动
	 */
	public void start() {
		if (mNoticThread == null) {
			mNoticThread = new HandlerThread("datacollect");
			mNoticThread.start();
		}
		if (null == mHandler) {
			mHandler = new CmnHandler(mNoticThread.getLooper());
		}

		mHandler.sendEmptyMessageDelayed(MODULE.DATA_COLLECT_INIT,
				nMinCollectCycle);
	}

	/**
	 * 停止线程
	 */
	public void stop() {
		if (mHandler == null) {
			return;
		}
		m_bThreadLoop = false;

		mHandler.removeMessages(MODULE.DATA_COLLECT_NOTIC);
		mHandler.removeMessages(MODULE.ADD_CALL_BACK);
		mHandler.sendEmptyMessage(MODULE.NOTIC_CLEAR_CALLBACK);
	}

	/**
	 * 采集数据，包括实时数据和历史数据
	 */
	private void collectData() {
		
		/* 实时数据刷新 */
		collectRealtimeData();

		/* 历史数据刷新 */
		collectHistoryData();

		/* 导出数据 */
		refreshAddrCtlDataCollect();

		/* 如果时间到，则保存一次数据 */
		long nCurrMillis = System.currentTimeMillis() / 1000;
		if (nCurrMillis - nLastSaveTime > nTmpHistoryDataNum
				|| nCurrMillis - nLastSaveTime < 0) {
			nLastSaveTime = nCurrMillis;
			sendMsgWriteDatabase();
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		/* 初始化数据库字段 */
		DataCollectBiz.select();

		/* 初始化历史采集 */
		int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		for (int i = 0; i < size; i++) {
			/* 初始化现存的数据库条数 */
			long nDatabaseNum = 0;
			SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
			if (mCollectDbObj != null) {
				/* 取数据库记录数 */
				Cursor cus = mCollectDbObj.getDatabaseBySql("select count(id) as count from dataCollect" + i, null);
				if (null != cus) {
					if (cus.moveToNext()) {
						nDatabaseNum = cus.getInt(cus.getColumnIndex("count"));
					}
					cus.close();
				}
			}
			nDatabaseNumList.add(nDatabaseNum);
		}

		/* 保存当前时间 */
		long nCurrMillis = System.currentTimeMillis() / 1000;
		nLastSaveTime = nCurrMillis;

		/* 采集的数目 */
		int nGrouSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		if (nGrouSize == 1) {
			/* 一组的时候30万最多 */
			int nAddrSize = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(0).getmCollectAddrList().size();
			if (nAddrSize <= 20) {
				nSampTotalNum = 10 * 10000;
			} else {
				nSampTotalNum = (200 * 10000) / nAddrSize;
			}
		} else if (nGrouSize > 1) {
			/* 总数50万，平均分配到每个组 */
			int nAddrSize = 0;
			for (int i = 0; i < nGrouSize; i++) {
				nAddrSize += CollectDataInfo.getInstance()
						.getmHistoryInfoList().get(i).getmCollectAddrList()
						.size();
			}

			if (nAddrSize < 50) {
				nSampTotalNum = (50 * 10000) / nGrouSize;
			} else {
				nSampTotalNum = (300 * 10000) / (nAddrSize);
			}

			if (nSampTotalNum > 10 * 10000) {
				nSampTotalNum = 10 * 10000;
			}
		}

	}

	/**
	 * 保存数据到数据库
	 */
	private void refreshAddrCtlDataCollect() {
		int nGroupSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (CollectDataInfo.getInstance().getmHistoryInfoList().get(i).isbAutoSave()) {
				long nCurrTime = System.currentTimeMillis();
				long nIntervalTime = CollectDataInfo.getInstance()
						.getmHistoryInfoList().get(i).getnIntervalTime();
				long nLastTime = CollectDataInfo.getInstance()
						.getmHistoryInfoList().get(i).getnLastTime();
				if(nLastTime!=0){
					if ((nCurrTime - nLastTime) >= (nIntervalTime * 60 * 60 * 1000)) {
						
						/* 更新保存时间 */
						CollectDataInfo.getInstance().getmHistoryInfoList().get(i)
								.setnLastTime(nCurrTime);
	
						/* 写文件导出 */
						EditDataCollectProp mEditData = new EditDataCollectProp();
						mEditData.bShowPress = false;
						mEditData.nGroupId = i;
						mEditData.nRecordTimeLen = nIntervalTime;
						mEditData.bAuto=true;
	
						/* 发送消息 */
						SkGlobalBackThread
								.getInstance()
								.getGlobalBackHandler()
								.obtainMessage(MODULE.WRITE_HISTORY_TO_FILE,
										mEditData).sendToTarget();
					}
				}else{
					CollectDataInfo.getInstance().getmHistoryInfoList().get(i)
					.setnLastTime(System.currentTimeMillis());
				}
			}

			/* 是否需要保存 */
			boolean bSave = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(i).isbSaveToFile();
			if (!bSave){
				continue;
			}
				
			AddrProp mCtlSaveAddr = CollectDataInfo.getInstance()
					.getmHistoryInfoList().get(i).getnCtlSaveAddrId();

			/* 清空数据 */
			tmpDataList.clear();

			/* init send data struct */
			mSendData.eDataType = DATA_TYPE.BCD_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			mCtlSaveAddr.nAddrLen = 8;

			/* get data from plc */
			boolean bSuccess = PlcRegCmnStcTools.getRegIntData(mCtlSaveAddr,
					tmpDataList, mSendData);

			if (!bSuccess || tmpDataList.size() < 8){
				continue;
			}

			while (i >= m_bHistorySaveReset.size()) {
				m_bHistorySaveReset.add(0);
			}

			/* 获得控制值1为导出数据到文件 */
			int nCtlValue = tmpDataList.get(0);
			int nOldReset = m_bHistorySaveReset.get(i);
			if (nCtlValue == nOldReset){
				continue;
			}
				
			m_bHistorySaveReset.set(i, nCtlValue);

			/* 读取组号 */
			int nSaveTimeLen = tmpDataList.get(1);
			int nGroupId = i;

			/* 控制地址为1，则保存数据为文件 */
			if (nCtlValue == 1) {
				EditDataCollectProp mEditData = new EditDataCollectProp();
				mEditData.nGroupId = nGroupId;
				mEditData.nRecordTimeLen = nSaveTimeLen;
				mEditData.bAuto=false;
				mEditData.nStartTime=getStartTime(tmpDataList);

				/* 发送消息 */
				SkGlobalBackThread.getInstance().getGlobalBackHandler()
						.obtainMessage(MODULE.WRITE_HISTORY_TO_FILE, mEditData)
						.sendToTarget();
				
				Activity myActivity = SKSceneManage.getInstance().getActivity();

				if (mEditData.bShowPress) {
					/* 显示进度条 */
					String sMsg = " ";
					if (myActivity != null) {
						sMsg = myActivity.getString(R.string.write_database);
					}

					SKPlcNoticThread.getInstance().getMainUIHandler()
							.obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg)
							.sendToTarget();
				}
			}
		}
	}
	
	
	private long getStartTime(Vector<Integer> list){
		long time=0;
		try {
			int [] array=new int[]{0,0,0,0,0,0};
			for (int i = 2; i < list.size(); i++) {
				String s=DataTypeFormat.intToBcdStr(list.get(i), false);
				if (s!=null&&!s.equals("ERROR")) {
					array[i-2]=Integer.valueOf(s);
				}
				//Log.d(TAG, "i="+(i-2)+",time="+array[i-2]);
			}
			if (array[0]<1970) {
				time=0;
				return time;
			}
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR,array[0]);
			c.set(Calendar.MONTH,array[1] - 1);
			c.set(Calendar.DAY_OF_MONTH,array[2]);
			c.set(Calendar.HOUR_OF_DAY,array[3]);
			c.set(Calendar.MINUTE, array[4]);
			c.set(Calendar.SECOND, array[5]);
			time = c.getTimeInMillis();
			if (time<0) {
				time=0;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return time;
	}

	/**
	 * 线程截获消息,处理消息
	 * @author Latory
	 */
	public class CmnHandler extends Handler {
		public CmnHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			long start = System.currentTimeMillis();

			switch (msg.what) {
			case MODULE.DATA_COLLECT_INIT: {
				/* 数据采集的一些数据初始化 */
				init();

				m_bThreadLoop = true;

				/* 恢复掉电保存 */
				SkGlobalBackThread.getInstance().getGlobalBackHandler()
						.sendEmptyMessage(MODULE.WRITE_POWER_SAVE_DATABASE);

				/* 发消息 采集数据 */
				mHandler.sendEmptyMessageDelayed(MODULE.DATA_COLLECT_NOTIC,
						nMinCollectCycle);
				break;
			}
			case MODULE.DATA_COLLECT_NOTIC: // PLC通知
			{
				if (!m_bThreadLoop) {
					break;
				}
				collectData();
				break;
			}
			case MODULE.ADD_CALL_BACK: // 注册接口
			{
				if (!m_bThreadLoop) {
					break;
				}
				msgAddNoticProp((DataCollectCallBack) msg.obj);
				break;
			}
			case MODULE.DATA_COLLECT_DESTORY_NOTIC: // 注销接口
			{
				msgDestoryCallback((IPlcNoticCallBack) msg.obj);
				break;
			}
			case MODULE.NOTIC_CLEAR_CALLBACK: // 清除接口
			{
				if (m_noticCallBackList != null) {
					m_noticCallBackList.clear();
				}
				break;
			}
			case MODULE.DATA_COLLECT_CLEAR: // 清除缓存
			{
				if (!m_bThreadLoop) {
					break;
				}
				if (msg.obj == null) {
					m_nHistoryDataList.clear();
				} else {
					ArrayList<Integer> temp = (ArrayList<Integer>) msg.obj;
					for (int i = 0; i < m_nHistoryDataList.size(); i++) {
						for (int j = 0; j < temp.size(); j++) {
							if (i == temp.get(j)) {
								m_nHistoryDataList.get(i).clear();
								break;
							}
						}
					}
					// m_nHistoryDataList.clear();
					collectState = COLLECT_CLEAR;
				}
				break;
			}
			case MODULE.DATA_COLLECT_CLEAR_PART:
			{
				if(m_bThreadLoop){
					if(tmpGroupId==null||tmpGroupId.size()==0){
						return;
					}
//					if(collectState!=COLLECT_CLEAR){
//						mHandler.sendEmptyMessageDelayed(MODULE.DATA_COLLECT_CLEAR_PART,
//								nMinCollectCycle);
//						return;
//					}
					//再次初始化
					init(tmpGroupId);
					
					/* 回调通知 */
					for (int j = 0; j < tmpGroupId.size(); j++) {
						int nCallbackSize = m_noticCallBackList.size();
						for (int i = 0; i < nCallbackSize; i++) {
							m_noticCallBackList.get(i).mCallbackInterface
									.noticDelDatabase(tmpGroupId.get(j));
						}
					}
					
//					/* 发消息 采集数据 */
//					mHandler.sendEmptyMessageDelayed(MODULE.DATA_COLLECT_NOTIC,
//							nMinCollectCycle);
					
					System.out.println("DATA_COLLECT_CLEAR_PART");
					
					collectState = COLLECT_WAIT;
				}
				
				break;
			}
			default: {
				break;
			}
			}

			/* 刷新线程存在则继续刷新 */
			if (m_bThreadLoop) {
				long time = System.currentTimeMillis() - start;// 扫描所耗时间
				time = nMinCollectCycle - time;
				if (time < 10) {
					time = 10;
				} else if (time > 200) {
					time = 200;
				}
				// Log.d("DataCollect", "time="+time);
				mHandler.removeMessages(MODULE.DATA_COLLECT_NOTIC);
				mHandler.sendEmptyMessageDelayed(MODULE.DATA_COLLECT_NOTIC,
						time);
			}
		}
	}

	/**
	 * 处理回调,通知数据更新
	 * @param eCollectType: 采集的类型， 1代表实时采集，2代表历史数据采集
	 * @param nGroupId
	 * @param nDataList
	 */
	private void dealPlcNotic(int eCollectType, int nGroupId,
			HistoryDataProp nDataList, int nCollectRate) {
		if (null == nDataList || nDataList.nDataList.isEmpty())
			return;
		if (null == m_noticCallBackList || m_noticCallBackList.isEmpty())
			return;

		/* 查找是否在注册的接口中 */
		int nCallbackSize = m_noticCallBackList.size();
		// System.err.println("nCallbackSize"+nCallbackSize);
		for (int i = 0; i < nCallbackSize; i++) {
			if (m_noticCallBackList.get(i).eCollectType == eCollectType) {
				if (m_noticCallBackList.get(i).nGroupIds.contains(nGroupId)) {
					int nIndex = m_noticCallBackList.get(i).nGroupIds
							.indexOf(nGroupId);
					if (nIndex < 0) {
						continue;
					}

					/* 不够容器则添加空容器 */
					while (nIndex >= m_noticCallBackList.get(i).nDataList
							.size()) {
						HistoryDataProp nNewDataList = new HistoryDataProp();
						nNewDataList.nDataList.clear();
						m_noticCallBackList.get(i).nDataList.add(nNewDataList);
					}

					m_noticCallBackList.get(i).nDataList.set(nIndex, nDataList);

					/* 调用回调接口通知 */
					m_noticCallBackList.get(i).mCallbackInterface
							.addrValueNotic(
									m_noticCallBackList.get(i).nDataList,
									nCollectRate);
					// break;
				}
			}
		}
	}

	/**
	 * 重新初始化时段删除的组
	 * */
	private void init(ArrayList<Integer> tmpGroupId2) {
		
		if(CollectDataInfo.getInstance().getmHistoryInfoList()==null||tmpGroupId2==null||tmpGroupId2.size()==0){
			return;
		}
		
		/* 初始化历史采集 */
		int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		for (int i = 0; i < size; i++) {
			for(int j = 0 ;j <tmpGroupId2.size();j++){
				if(tmpGroupId2.get(j) == i){
					/* 初始化现存的数据库条数 */
					long nDatabaseNum = 0;
					SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
					if (mCollectDbObj != null) {
						/* 取数据库记录数 */
						Cursor cus = mCollectDbObj.getDatabaseBySql("select count(id) as count from dataCollect" + i, null);
						if (null != cus) {
							if (cus.moveToNext()) {
								nDatabaseNum = cus.getInt(cus.getColumnIndex("count"));
							}
							cus.close();
						}
					}
					nDatabaseNumList.set(i,nDatabaseNum);
				}
			}
		}		
	}

	/**
	 * 位地址on控制数据采样
	 * @param eCollectType : 采集的类型， 1代表实时采集，2代表历史数据采集
	 * @param index ：采样组的id号
	 * @return
	 */
	private synchronized boolean isBitOnAddrCtlSamps(int eCollectType,int index, boolean history) {
		int nSize = 0;
		boolean result = true;

		if (nRealTimeCollect == eCollectType) {
			nSize = CollectDataInfo.getInstance().getmRealTimeInfoList().size();
			if (index < 0 || index >= nSize) {
				return false;
			}
			if(CollectDataInfo.getInstance().getmRealTimeInfoList().get(index).isbAddrCtlSamp()){
				CallbackItem item=CollectDataInfo.getInstance().getmRealTimeInfoList().get(index).getmCtlSampItem();
				if (item!=null) {
					result=item.isnBValue();
				}
			}
			
		} else if (nHistoryCollect == eCollectType) {
			nSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
			if (index < 0 || index >= nSize) {
				return false;
			}
			if(CollectDataInfo.getInstance().getmHistoryInfoList().get(index).isbAddrCtlSamp()){
				CallbackItem item=CollectDataInfo.getInstance().getmHistoryInfoList().get(index).getmCtlSampItem();
				if (item!=null) {
					result=item.isnBValue();
				}
			}
		}
		
		return result;
	}

	/**
	 * 是否已经取满
	 * @param index
	 * @return
	 */
	private boolean isFullCollect(int eCollectType, int index) {
		if (nHistoryCollect != eCollectType)
			return false;

		int nSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		if (index < 0 || index >= nSize)
			return false;

		// long nCurrTime = System.currentTimeMillis();
		/* 取数据库记录数 */
		while (nDatabaseNumList.size() <= index) {
			nDatabaseNumList.add((long) 0);
		}
		long nDatabaseNum = nDatabaseNumList.get(index);

		int all = 0;
		for (int i = 0; i < nDatabaseNumList.size(); i++) {
			long temp = nDatabaseNumList.get(i);
			all += temp;
		}
		SystemVariable.getInstance().write32WordAddr(all,
				SystemAddress.getInstance().Sys_SampCount());

		if (nDatabaseNum >= nSampTotalNum) {
			boolean bNotic = CollectDataInfo.getInstance()
					.getmHistoryInfoList().get(index).isbFullNotic();
			AddrProp mNoticAddr = CollectDataInfo.getInstance()
					.getmHistoryInfoList().get(index).getnNoticAddrId();

			/* 取满处理 */
			FULL_DEAL_TYPE eSampFullType = CollectDataInfo.getInstance()
					.getmHistoryInfoList().get(index).geteDealSampFull();
			switch (eSampFullType) {
			case FULL_STOP_SAMP: // 采满停止采样
			{
				/* 采满设置标志符 */
				while (index >= bHistoryFull.size()) {
					bHistoryFull.add(false);
				}
				bHistoryFull.set(index, true);

				/* 通知 */
				if (bNotic) {
					/* 清空数据 */
					tmpDataList.clear();
					tmpDataList.add(1);

					/* init send data struct */
					mSendData.eDataType = DATA_TYPE.BIT_1;
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

					/* get data from plc */
					boolean bSuccess = PlcRegCmnStcTools.setRegIntData(
							mNoticAddr, tmpDataList, mSendData);
				}
				return false;
			}
			case FULL_CLEAR_OLD_DATA: // 采满清除老数据从新采样
			{
				EditDataCollectProp mEditData = new EditDataCollectProp();
				mEditData.nGroupId = index;
				mEditData.nRecordTimeLen = -1;

				/* 发送消息 */
				SkGlobalBackThread
						.getInstance()
						.getGlobalBackHandler()
						.obtainMessage(MODULE.DELETE_HISTORY_FROM_DATABASE,
								mEditData).sendToTarget();
				nDatabaseNumList.set(index, (long) 0);
				
				/* 回调通知 */
				int nCallbackSize = m_noticCallBackList.size();
				for (int i = 0; i < nCallbackSize; i++) {
					m_noticCallBackList.get(i).mCallbackInterface
							.noticDelDatabase(index);
				}

				ArrayList<Integer> mList = new ArrayList<Integer>();
				mList.add(index);
				mHandler.obtainMessage(MODULE.DATA_COLLECT_CLEAR, mList)
						.sendToTarget();

				/* 通知 */
				if (bNotic) {
					/* 清空数据 */
					tmpDataList.clear();
					tmpDataList.add(1);

					/* init send data struct */
					mSendData.eDataType = DATA_TYPE.BIT_1;
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

					/* get data from plc */
					boolean bSuccess = PlcRegCmnStcTools.setRegIntData(
							mNoticAddr, tmpDataList, mSendData);
				}
				
				break;
			}
			case FULL_REPLACE_OLD_DATA: // 采满 替换老数据
			{
				/* 采满设置标志符 */
				while (index >= bHistoryFull.size()) {
					bHistoryFull.add(false);
				}
				bHistoryFull.set(index, true);

				/* 通知 */
				if (bNotic) {
					/* 清空数据 */
					tmpDataList.clear();
					tmpDataList.add(1);

					/* init send data struct */
					mSendData.eDataType = DATA_TYPE.BIT_1;
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

					/* get data from plc */
					boolean bSuccess = PlcRegCmnStcTools.setRegIntData(
							mNoticAddr, tmpDataList, mSendData);
				}

				EditDataCollectProp mEditData = new EditDataCollectProp();
				mEditData.nGroupId = index;
				mEditData.nRecordTimeLen = nTmpHistoryDataNum;

				/* 发送消息 */
				SkGlobalBackThread
						.getInstance()
						.getGlobalBackHandler()
						.obtainMessage(MODULE.DELETE_HISTORY_FROM_DATABASE,
								mEditData).sendToTarget();
				nDatabaseNum -= nTmpHistoryDataNum;
				nDatabaseNumList.set(index, nDatabaseNum);
				break;
			}
			default: {
				break;
			}
			}

			/* 写入数据到数据库中 */
			sendMsgWriteDatabase();
		}
		return true;
	}

	/**
	 * 发送消息写数据库
	 */
	public synchronized void sendMsgWriteDatabase() {
		/* 无数据则返回 */
		if (m_nHistoryDataList.isEmpty())
			return;

		EditDataCollectProp mEditData = new EditDataCollectProp();
		mEditData.nHistoryDataList = new Vector<Vector<HistoryDataProp>>();

		int nDataSize = m_nHistoryDataList.size();
		for (int i = 0; i < nDataSize; i++) {
			mEditData.nHistoryDataList.add(m_nHistoryDataList.get(i));
		}

		/* 发送消息 */
		SkGlobalBackThread.getInstance().getGlobalBackHandler()
				.obtainMessage(MODULE.WRITE_HISTORY_TO_DATABASE, mEditData)
				.sendToTarget();

		/* 发消息清除缓存 */
		mHandler.sendEmptyMessage(MODULE.DATA_COLLECT_CLEAR);
	}

	/**
	 * 消息通知增加通知地址属性函数接口
	 * @param eCollectType ：采集的类型， 1代表实时采集，2代表历史数据采集
	 * @param nGroupIdList
	 * @param mCallBack
	 */
	private void msgAddNoticProp(DataCollectCallBack mCallbackInfo) {
		if (null == mCallbackInfo){
			return;
		}

		/* 添加通知 */
		if (null == m_noticCallBackList) {
			m_noticCallBackList = new Vector<DataCollectCallBack>();
		}

		int nSize = m_noticCallBackList.size();
		for (int i = 0; i < nSize; i++) {
			if (m_noticCallBackList.get(i).mCallbackInterface == mCallbackInfo.mCallbackInterface) {
				return;
			}
		}

		m_noticCallBackList.add(mCallbackInfo);
		
	}

	/**
	 * 烧毁回调
	 * @param mCallBack
	 */
	private void msgDestoryCallback(IPlcNoticCallBack mCallBack) {
		if (null == mCallBack || null == m_noticCallBackList){
			return;
		}
		
		/* 移除回调 */
		for (int i = 0; i < m_noticCallBackList.size(); i++) {
			if (m_noticCallBackList.get(i).mCallbackInterface == mCallBack) {
				m_noticCallBackList.remove(i);
				i--;
				continue;
			}
		}
	}

	/**
	 * 从PLC中采样数据一次
	 * @param index: 组号
	 * @return
	 */
	private synchronized boolean readDataFromPlc(int eCollectType, int index,
			int nCollectRate, SAMP_TYPE type) {
		/* 从PLC中读取数据 */
		int addrSize = 0;

		/* init send data struct */
		HistoryDataProp mDataProp = new HistoryDataProp();
		mDataProp.nDataList.clear();

		String sTmpStr = "";
		boolean bNeedCollect = false;
		if (nRealTimeCollect == eCollectType) {
			addrSize = CollectDataInfo.getInstance().getmRealTimeInfoList()
					.get(index).getmCollectAddrList().size();

			for (int i = 0; i < addrSize; i++) {

				DATA_TYPE eDataType = CollectDataInfo.getInstance()
						.getmRealTimeInfoList().get(index)
						.getmCollectAddrList().get(i).eDataType;

				/* 如果是字符串 */
				if (DATA_TYPE.ASCII_STRING == eDataType||DATA_TYPE.UNICODE_STRING == eDataType) {

					/* 是否通信成功 */
					if (PlcRegCmnStcTools.getLastCmnInfo().nErrorCode == 0) {
						bNeedCollect |= true;
					} else {
						bNeedCollect |= false;
					}

					if (type == SAMP_TYPE.BIT_ON_SAMP) {
						// 位ON时控制采样,直接读取
						AddrProp mSampAddr = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCollectAddrList().get(i).mAddrPro;
						sTmpStr = getSValue(mSampAddr, eDataType);

					} else {
						// 其他方式，采用通知回调
						CallbackItem item = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCallbackItems().get(i);
						if (item.hasCallback()) {
							// 注册回调，并且已经回调
							sTmpStr = item.getsValue();
						} else {
							// 未回调，直接读取
							// Log.d("SKScene", "..........null");
							AddrProp mSampAddr = CollectDataInfo.getInstance()
									.getmRealTimeInfoList().get(index)
									.getmCollectAddrList().get(i).mAddrPro;
							sTmpStr = getSValue(mSampAddr, eDataType);
						}

					}
					mDataProp.nDataList.add(sTmpStr);

				} else {
					sTmpStr = "";
					double nValue = 0;
					if (type == SAMP_TYPE.BIT_ON_SAMP) {
						// 位ON时控制采样,直接读取
						AddrProp mSampAddr = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCollectAddrList().get(i).mAddrPro;
						nValue = getNValue(mSampAddr, eDataType);
					} else {
						// 其他方式，采用通知回调
						CallbackItem item = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCallbackItems().get(i);
						if (item.hasCallback()) {
							// 注册的，并且已经回调
							nValue = item.getnFValue();
						} else {
							// 未回调，直接读取
							// Log.d("SKScene", "..........null");
							AddrProp mSampAddr = CollectDataInfo.getInstance()
									.getmRealTimeInfoList().get(index)
									.getmCollectAddrList().get(i).mAddrPro;
							nValue = getNValue(mSampAddr, eDataType);
						}
					}

					/* 是否通信成功 */
					if (PlcRegCmnStcTools.getLastCmnInfo().nErrorCode == 0) {
						bNeedCollect |= true;
					} else {
						bNeedCollect |= false;
					}

					/* 进行缩放处理 */
					boolean bDealData = CollectDataInfo.getInstance()
							.getmRealTimeInfoList().get(index)
							.getmCollectAddrList().get(i).bDealData;
					if (bDealData) {
						double nSourceMin = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCollectAddrList().get(i).nSourceMin;
						double nSourceMax = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCollectAddrList().get(i).nSourceMax;
						double nTargeMin = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCollectAddrList().get(i).nTargeMin;
						double nTargeMax = CollectDataInfo.getInstance()
								.getmRealTimeInfoList().get(index)
								.getmCollectAddrList().get(i).nTargeMax;
						if (nSourceMin <= nSourceMax && nTargeMin <= nTargeMax) {
							if (nValue < nSourceMin) {
								nValue = nSourceMin;
							} else if (nValue > nSourceMax) {
								nValue = nSourceMax;
							}

							nValue = nTargeMin
									+ ((nValue - nSourceMin) / (nSourceMax - nSourceMin))
									* (nTargeMax - nTargeMin);
						}
					}

					/* 保留小数点个数 */
					int nMaxDecLen = CollectDataInfo.getInstance()
							.getmRealTimeInfoList().get(index)
							.getmCollectAddrList().get(i).nDecLength;
					if (!bDealData && eDataType != DATA_TYPE.FLOAT_32) {
						nValue = nValue / (Math.pow(10, nMaxDecLen));
					}
					if (nMaxDecLen <= 0) {
						long nTmpValue = (long) nValue;
						sTmpStr = nTmpValue + "";
					} else {
						if (eDataType == DATA_TYPE.FLOAT_32) {
							boolean bRound = CollectDataInfo.getInstance()
									.getmRealTimeInfoList().get(index)
									.getmCollectAddrList().get(i).bFloatRound;
							if (bRound) {
								sTmpStr = String.format(
										"%." + nMaxDecLen + "f", nValue);
							} else {
								NumberFormat mFormat = NumberFormat
										.getNumberInstance();
								mFormat.setMaximumFractionDigits(nMaxDecLen);
								mFormat.setGroupingUsed(false);
								mFormat.setRoundingMode(RoundingMode.DOWN);
								sTmpStr = mFormat.format(nValue);
							}
						} else {
							sTmpStr = String.format("%." + nMaxDecLen + "f",
									nValue);
						}
					}
					mDataProp.nDataList.add(sTmpStr);
				}
			}

			/* 如果通信失败，则返回 */
			if (bNeedCollect == false) {
				return false;
			}
		} else if (nHistoryCollect == eCollectType) {

			addrSize = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(index).getmCollectAddrList().size();

			for (int i = 0; i < addrSize; i++) {

				DATA_TYPE eDataType = CollectDataInfo.getInstance()
						.getmHistoryInfoList().get(index).getmCollectAddrList()
						.get(i).eDataType;

				/* 如果是字符串 */
				if (DATA_TYPE.ASCII_STRING == eDataType||DATA_TYPE.UNICODE_STRING == eDataType) {
					sTmpStr = "";

					if (type == SAMP_TYPE.BIT_ON_SAMP) {
						// 位ON时控制采样,直接读取
						AddrProp mSampAddr = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCollectAddrList().get(i).mAddrPro;
						sTmpStr = getSValue(mSampAddr, eDataType);
					} else {
						// 其他方式，采用通知回调
						CallbackItem item = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCallbackItems().get(i);
						if (item.hasCallback()) {
							// 注册，并且回调
							sTmpStr = item.getsValue();
						} else {
							// 未回调，直接读取
							AddrProp mSampAddr = CollectDataInfo.getInstance()
									.getmHistoryInfoList().get(index)
									.getmCollectAddrList().get(i).mAddrPro;
							sTmpStr = getSValue(mSampAddr, eDataType);
						}
					}
					/* 是否通信成功 */
					if (PlcRegCmnStcTools.getLastCmnInfo().nErrorCode == 0) {
						bNeedCollect |= true;
					} else {
						bNeedCollect |= false;
					}

					mDataProp.nDataList.add(sTmpStr);
				} else {
					sTmpStr = "";
					double nValue = 0;

					if (type == SAMP_TYPE.BIT_ON_SAMP) {
						// 位ON时控制采样,直接读取
						AddrProp mSampAddr = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCollectAddrList().get(i).mAddrPro;
						nValue = getNValue(mSampAddr, eDataType);
					} else {
						// 其他方式，采用通知回调
						CallbackItem item = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCallbackItems().get(i);
						if (item.hasCallback()) {
							// 注册，并回调
							nValue = item.getnFValue();
						} else {
							// 未回调，直接读取
							AddrProp mSampAddr = CollectDataInfo.getInstance()
									.getmHistoryInfoList().get(index)
									.getmCollectAddrList().get(i).mAddrPro;
							nValue = getNValue(mSampAddr, eDataType);
						}
					}

					/* 是否通信成功 */
					if (PlcRegCmnStcTools.getLastCmnInfo().nErrorCode == 0) {
						bNeedCollect |= true;
					} else {
						bNeedCollect |= false;
					}

					/* 进行缩放处理 */
					boolean bDealData = CollectDataInfo.getInstance()
							.getmHistoryInfoList().get(index)
							.getmCollectAddrList().get(i).bDealData;
					if (bDealData) {
						double nSourceMin = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCollectAddrList().get(i).nSourceMin;
						double nSourceMax = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCollectAddrList().get(i).nSourceMax;
						double nTargeMin = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCollectAddrList().get(i).nTargeMin;
						double nTargeMax = CollectDataInfo.getInstance()
								.getmHistoryInfoList().get(index)
								.getmCollectAddrList().get(i).nTargeMax;
						if (nSourceMin <= nSourceMax && nTargeMin <= nTargeMax) {
							if (nValue < nSourceMin) {
								nValue = nSourceMin;
							} else if (nValue > nSourceMax) {
								nValue = nSourceMax;
							}

							nValue = nTargeMin
									+ ((nValue - nSourceMin) / (nSourceMax - nSourceMin))
									* (nTargeMax - nTargeMin);
						}
					}

					/* 保留小数点个数 */
					int nMaxDecLen = CollectDataInfo.getInstance()
							.getmHistoryInfoList().get(index)
							.getmCollectAddrList().get(i).nDecLength;
					if (!bDealData && eDataType != DATA_TYPE.FLOAT_32) {
						nValue = nValue / (Math.pow(10, nMaxDecLen));
					}

					if (nMaxDecLen <= 0) {
						long nTmpValue = (long) nValue;
						sTmpStr = nTmpValue + "";
					} else {
						if (eDataType == DATA_TYPE.FLOAT_32) {
							boolean bRound = CollectDataInfo.getInstance()
									.getmHistoryInfoList().get(index)
									.getmCollectAddrList().get(i).bFloatRound;
							if (bRound) {
								sTmpStr = String.format(
										"%." + nMaxDecLen + "f", nValue);
							} else {
								NumberFormat mFormat = NumberFormat
										.getNumberInstance();
								mFormat.setMaximumFractionDigits(nMaxDecLen);
								mFormat.setGroupingUsed(false);
								mFormat.setRoundingMode(RoundingMode.DOWN);
								sTmpStr = mFormat.format(nValue);
							}
						} else {
							sTmpStr = String.format("%." + nMaxDecLen + "f",
									nValue);
						}
					}
					mDataProp.nDataList.add(sTmpStr);
				}
			}

			/* 如果通信失败，则返回 */
			if (bNeedCollect == false) {
				return false;
			}

			/* 组数不够 */
			while (index >= m_nHistoryDataList.size()) {
				Vector<HistoryDataProp> nNewList = new Vector<HistoryDataProp>();
				m_nHistoryDataList.add(nNewList);
			}

			/* 取当前时间 */
			long nCurrMillis = System.currentTimeMillis();

			/* 添加一条记录 */
			while (index >= bHistoryFull.size()) {
				bHistoryFull.add(false);
			}
			mDataProp.nMillisTime = nCurrMillis;
			mDataProp.bCollectFull = bHistoryFull.get(index);

			/* 掉电保存数据 */
			Vector<CollectSaveProp> mValueList = new Vector<CollectSaveProp>();
			while (index >= mLastSaveList.size()) {
				HistoryDataProp mHisDataProp = new HistoryDataProp();
				mLastSaveList.add(mHisDataProp);
			}

			/* 添加每个地址对应的数据 */
			int nDataSize = mDataProp.nDataList.size();

			/* 初始化数据 */
			boolean bInit = false;
			if (mLastSaveList.get(index).nDataList.size() == 0) {
				bInit = true;
			}
		
			for (int i = 0; i < nDataSize; i++) {
				String sDataStr = mDataProp.nDataList.get(i);
				if (sDataStr == null) {
					sDataStr = "";
				}

				/* 存掉电保存的数据 */
				if (mLastSaveList.get(index).nDataList == null) {
					/* 数据库没有数据的时候 */
					CollectSaveProp mCollecSave = SKSaveThread.getInstance().new CollectSaveProp();
					mCollecSave.nAddrId = (short) i;
					mCollecSave.nAddrValueLen = (short) 0;
					byte[] sStr = sDataStr.getBytes();
					if (sStr != null) {
						mCollecSave.nAddrValueLen = (short) sStr.length;
					}
					mCollecSave.nValueByteList = sStr;

					mValueList.add(mCollecSave);
				} else if (bInit) {
					mLastSaveList.get(index).nDataList.add(sDataStr);

					CollectSaveProp mCollecSave = SKSaveThread.getInstance().new CollectSaveProp();
					mCollecSave.nAddrId = (short) i;
					mCollecSave.nAddrValueLen = (short) 0;
					byte[] sStr = sDataStr.getBytes();
					if (sStr != null) {
						mCollecSave.nAddrValueLen = (short) sStr.length;
					}
					mCollecSave.nValueByteList = sStr;

					mValueList.add(mCollecSave);
				} else if (mLastSaveList.get(index).nDataList.size() == nDataSize) {
					String sLastStr = mLastSaveList.get(index).nDataList.get(i);
					if (sLastStr != null) {
						if (!sLastStr.equals(sDataStr)) {
							CollectSaveProp mCollecSave = SKSaveThread
									.getInstance().new CollectSaveProp();
							mCollecSave.nAddrId = (short) i;
							mCollecSave.nAddrValueLen = (short) 0;
							byte[] sStr = sDataStr.getBytes();
							if (sStr != null) {
								mCollecSave.nAddrValueLen = (short) sStr.length;
							}
							mCollecSave.nValueByteList = sStr;

							mValueList.add(mCollecSave);
							mLastSaveList.get(index).nDataList.set(i, sDataStr);
						}
					}
				}// 保存结束
				
				
				// 进行打印
				int printState = CollectDataInfo.getInstance().getmHistoryInfoList().get(index).getPrintState();
				if ((printState & 1) > 0) {
					
					printDate.setTime(System.currentTimeMillis());
					String printMessage = "";
					if ((printState & 2) > 0) { //日期
						printMessage += (DateStringUtil.convertDate(DATE_FORMAT.YYYYMMDD_ACROSS, printDate) + "    ");
					}
					if ((printState & 4) > 0) { //时间
						printMessage += (DateStringUtil.converTime(TIME_FORMAT.HHMM_COLON, printDate) + "    ");
					}
					
					StringBuffer printBuffer = new StringBuffer(); //采集出来的数据。
					for(int tt = 0; tt < mDataProp.nDataList.size(); tt++){
						 printBuffer.append(mDataProp.nDataList.get(tt)).append(" ");
					}
					printMessage += printBuffer.toString();
					
					printVector.clear();
					printVector.add(printMessage);
					if (SystemInfo.getmPrintModel()==null) {
						if (SystemInfo.getmPrintModel()==PRINT_MODEL.WHE19) {
							AKPrint.getInstance().printTexts(printVector,1);
						}else if (SystemInfo.getmPrintModel()==PRINT_MODEL.WHA5) {
							AKPrint.getInstance().printTexts(printVector,2);
						}
					}
				}
			}
			m_nHistoryDataList.get(index).add(mDataProp);

			/* 存掉电保存的数据 */
			SKSaveThread.getInstance().saveDataCollect((short) index,
					nCurrMillis, mValueList);
		}

		/* 通知数据 */
		dealPlcNotic(eCollectType, index, mDataProp, nCollectRate);
		return true;
	}

	java.util.Date printDate = new java.util.Date();
	Vector<String> printVector = new Vector<String>();
	
	/**
	 * 注册地址通知
	 */
	private void onRegister(ArrayList<CallbackItem> list) {
		if (list != null) {
			// Log.d("SKScene", "onRegister list:"+list.size());
			for (int i = 0; i < list.size(); i++) {
				list.get(i).onRegister(false);
			}
		}
	}

	/**
	 * 删除绑定通知
	 */
	private void unRegister(ArrayList<CallbackItem> list) {
		if (list != null) {
			// Log.d("SKScene", "unRegister list:"+list.size());
			for (int i = 0; i < list.size(); i++) {
				list.get(i).unRegister(0);
			}
		}
	}

	/**
	 * 读取地址值
	 */
	private double getNValue(AddrProp mSampAddr, DATA_TYPE type) {
		double value = 0;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		mSendData.eDataType = type;
		boolean bSuccess = PlcRegCmnStcTools.getRegDoubleData(mSampAddr,
				tmpDoubleList, mSendData);
		if (bSuccess && !tmpDoubleList.isEmpty()) {
			value = tmpDoubleList.firstElement();
		}
		return value;
	}

	/**
	 * 读取地址值，String类型
	 */
	private String getSValue(AddrProp mSampAddr, DATA_TYPE type) {
		String value = "";
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		mSendData.eDataType = DATA_TYPE.ASCII_STRING;
		boolean bSuccess = PlcRegCmnStcTools.getRegAsciiData(mSampAddr,tmpByteList, mSendData, true);
		if (bSuccess && !tmpByteList.isEmpty()) {
			value = byte2String(tmpByteList,type);
		}
		return value;
	}

	private String byte2String(Vector<Byte> tmpByteList2, DATA_TYPE type) {
		if(type == DATA_TYPE.ASCII_STRING){
			return getAscii(tmpByteList2);
		}else{
			return getUnicode(tmpByteList2);
		}
	}

	/**
	 * byte转成 ASCII编码
	 */
	private String getAscii(Vector<Byte> tmpByteList2) {
		String sTmpStr = "";
		int ascii_act_len = 0;
		for (ascii_act_len = 0; ascii_act_len < tmpByteList2.size(); ascii_act_len++) {
			if (tmpByteList2.get(ascii_act_len) == 0) {
				break;
			}
		}
		if (ascii_act_len == 0) {
			return sTmpStr;
		}

		if (ascii_act_len >= tmpByteList2.size()) {
			ascii_act_len = tmpByteList2.size();
		}

		// 将获得的字符码转存到字节数组中
		byte[] bytearray = new byte[ascii_act_len];

		for (int j = 0, i = 0; j < ascii_act_len; j++) {
			bytearray[i] = tmpByteList2.get(j);
			i++;
		}
		
		try {// 新建ASCII字符串
			sTmpStr = new String(bytearray, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sTmpStr;
	}

	/**
	 * byte转成 UNICODe编码
	 */
	private String getUnicode(Vector<Byte> temp) {
		String sTmpStr = "";
		int nListSize = temp.size();
		byte[] byteArray = new byte[nListSize + 2];
		byteArray[0] = -1;
		byteArray[1] = -2;
		for (int k = 0; k < nListSize; k++) {
			byteArray[k + 2] = temp.get(k);
		}

		/* 转换成字符串 */
		try {
			sTmpStr = new String(byteArray, "UNICODE");
		} catch (UnsupportedEncodingException e) {
			Log.e("getmHistoryInfoList",
					"readDataFromPlc data collect value to string failed");
			e.printStackTrace();
		}
		return sTmpStr;
	}

	/**
	 * 实时数据采集
	 */
	private synchronized void collectRealtimeData() {

		/* 处理实时采集 */
		int size = CollectDataInfo.getInstance().getmRealTimeInfoList().size();
		for (int i = 0; i < size; i++) {
			int nCirle = 0;
			if (CollectDataInfo.getInstance().getmRealTimeInfoList().get(i)
					.isbAddrCtlRate()) {
				/* 清空数据 */
				tmpDataList.clear();

				/* init send data struct */
				mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;

				boolean bSuccess = PlcRegCmnStcTools.getRegIntData(
						CollectDataInfo.getInstance().getmRealTimeInfoList()
								.get(i).getmCtlRateAddr(), tmpDataList,
						mSendData);
				if (bSuccess && !tmpDataList.isEmpty()) {
					nCirle = tmpDataList.get(0);
				}
			} else {
				nCirle = CollectDataInfo.getInstance().getmRealTimeInfoList()
						.get(i).getnSampRate();
			}

			nCirle = nCirle * 2;
			SAMP_TYPE eSampType = CollectDataInfo.getInstance()
					.getmRealTimeInfoList().get(i).geteSampType();

			switch (eSampType) {
			case FIXED_TIME_SAMP: // 固定时间采样
			{
				/* 如果没到这个采集周期则跳过 */
				while (mLastRealTimeTime.size() <= i) {
					mLastRealTimeTime.add((long) 0);
				}

				long nCurrMillis = System.currentTimeMillis();
				if (nCirle <= 0
						|| ((nCurrMillis - mLastRealTimeTime.get(i) < nCirle * 100) && nCurrMillis > mLastRealTimeTime
								.get(i))) {
					break;
				}
				mLastRealTimeTime.set(i, nCurrMillis);

				/* 是否地址控制采样 */
				if (isBitOnAddrCtlSamps(nRealTimeCollect, i, false) == false) {
					if (!CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).setbResetCallback(true);
						unRegister(CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getmCallbackItems());
					}
					break;
				} else {
					if (CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).setbResetCallback(false);
						onRegister(CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getmCallbackItems());
					}
				}

				/* 取得当前系统时间 */
				systemTime.setToNow();

				/* 如果是地址动态控制 */
				if (CollectDataInfo.getInstance().getmRealTimeInfoList().get(i)
						.isbAddrCtlTime()) {
					/* 清空数据 */
					tmpDataList.clear();

					/* init send data struct */
					mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

					int nStartHourTime = 0;
					int nStartMinTime = 0;
					int nEndHourTime = 0;
					int nEndMinTime = 0;

					CollectDataInfo.getInstance().getmRealTimeInfoList().get(i)
							.getmStartTimeAddr().nAddrLen = 2;
					CollectDataInfo.getInstance().getmRealTimeInfoList().get(i)
							.getmEndTimeAddr().nAddrLen = 2;

					/* get data from plc */
					boolean bSuccess = PlcRegCmnStcTools.getRegIntData(
							CollectDataInfo.getInstance()
									.getmRealTimeInfoList().get(i)
									.getmStartTimeAddr(), tmpDataList,
							mSendData);
					if (bSuccess && tmpDataList.size() >= 2) {
						nStartHourTime = tmpDataList.get(0);
						nStartMinTime = tmpDataList.get(1);
					}

					/* get data from plc */
					bSuccess = PlcRegCmnStcTools.getRegIntData(CollectDataInfo
							.getInstance().getmRealTimeInfoList().get(i)
							.getmEndTimeAddr(), tmpDataList, mSendData);
					if (bSuccess && tmpDataList.size() >= 2) {
						nEndHourTime = tmpDataList.get(0);
						nEndMinTime = tmpDataList.get(1);
					}

					int nStartTime = nStartHourTime * 60 + nStartMinTime;
					int nEndTime = nEndHourTime * 60 + nEndMinTime;
					int nNowTime = systemTime.hour * 60 + systemTime.minute;

					if (nNowTime < nStartTime || nNowTime > nEndTime)
						break;
				} else {
					int nStartHour = CollectDataInfo.getInstance()
							.getmRealTimeInfoList().get(i).getnStartHour();
					int nStartMinute = CollectDataInfo.getInstance()
							.getmRealTimeInfoList().get(i).getnStartMinute();
					int nEndHour = CollectDataInfo.getInstance()
							.getmRealTimeInfoList().get(i).getnEndHour();
					int nEndMinute = CollectDataInfo.getInstance()
							.getmRealTimeInfoList().get(i).getnEndMinute();

					int nStartTime = nStartHour * 60 + nStartMinute;
					int nEndTime = nEndHour * 60 + nEndMinute;
					int nNowTime = systemTime.hour * 60 + systemTime.minute;

					if (nNowTime < nStartTime || nNowTime > nEndTime)
						break;
				}

				/* 从PLC采集数据 */
				readDataFromPlc(nRealTimeCollect, i, nCirle / 2, eSampType);
				break;
			}
			case CONSTANT_CYCLE_SAMP: // 固定周期采样
			{
				/* 如果没到这个采集周期则跳过 */
				while (mLastRealTimeTime.size() <= i) {
					mLastRealTimeTime.add((long) 0);
				}

				long nCurrMillis = System.currentTimeMillis();
				if (nCirle <= 0
						|| ((nCurrMillis - mLastRealTimeTime.get(i) < nCirle * 100) && nCurrMillis > mLastRealTimeTime
								.get(i))) {
					break;
				}
				mLastRealTimeTime.set(i, nCurrMillis);

				/* 是否地址控制采样 */
				if (isBitOnAddrCtlSamps(nRealTimeCollect, i, false) == false) {
					if (!CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).setbResetCallback(true);
						unRegister(CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getmCallbackItems());
					}
					break;
				} else {
					if (CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).setbResetCallback(false);
						onRegister(CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getmCallbackItems());
					}
				}

				/* 从PLC采集数据 */
				readDataFromPlc(nRealTimeCollect, i, nCirle / 2, eSampType);
				break;
			}
			case BIT_ON_SAMP: {

				/* 上升沿采样 */
				CallbackItem item=CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getmCtlSampItem();
				if (item!=null) {
					
					//上升沿判断
					boolean isOff2On=false;
					
					if(item.isOffToOn()){
						isOff2On=true;
					}else{ 
						//读取触发地址值
						Vector<Byte> bitdataList = new Vector<Byte>();
						PlcRegCmnStcTools.getRegBytesData(CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getnCtlSampAddr(),
								bitdataList, mSendData);
						
						//防止刷新进程漏掉上升沿过程
						if(bitdataList.size()>0){
							if((!item.isnBValue())&&(bitdataList.get(0)==1)){
								isOff2On=true;
							}
						}
					}
					
					if (isOff2On) {
						//从off-->on 触发
						
						/* 从PLC采集数据 */
						readDataFromPlc(nRealTimeCollect, i, nCirle / 2, eSampType);
						
						item.setOffToOn(false);
						
						/* 是否自动复位 */
						boolean bAutoReset = CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).isbAutoReset();

						/* 自动复位 */
						if (bAutoReset) {
							tmpDataList.clear();
							tmpDataList.add(0);

							mSendData.eDataType = DATA_TYPE.BIT_1;
							mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

							PlcRegCmnStcTools.setRegIntData(
									CollectDataInfo.getInstance().getmRealTimeInfoList().get(i).getnCtlSampAddr(), tmpDataList,mSendData);
							
							item.setBValue(false);
						}
					}
				}
				
				break;
			}
			default: {
				break;
			}
			}
		}
	}

	/**
	 * 历史数据采集
	 */
	private synchronized void collectHistoryData() {

		/* 处理实时采集 */
		int size = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		for (int i = 0; i < size; i++) {
			int nCirle = 0;
			if (CollectDataInfo.getInstance().getmHistoryInfoList().get(i)
					.isbAddrCtlRate()) {
				/* 清空数据 */
				tmpDataList.clear();

				/* init send data struct */
				mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;

				boolean bSuccess = PlcRegCmnStcTools.getRegIntData(
						CollectDataInfo.getInstance().getmHistoryInfoList()
								.get(i).getmCtlRateAddr(), tmpDataList,
						mSendData);
				if (bSuccess && !tmpDataList.isEmpty()) {
					nCirle = tmpDataList.get(0);
				}
			} else {
				nCirle = CollectDataInfo.getInstance().getmHistoryInfoList()
						.get(i).getnSampRate();
			}

			nCirle = nCirle * 2;
			SAMP_TYPE eSampType = CollectDataInfo.getInstance()
					.getmHistoryInfoList().get(i).geteSampType();

			switch (eSampType) {
			case FIXED_TIME_SAMP: // 固定时间采样
			{
				/* 如果没到这个采集周期则跳过 */
				while (mLastHistoryTime.size() <= i) {
					mLastHistoryTime.add((long) 0);
				}

				long nCurrMillis = System.currentTimeMillis();
				if (nCirle <= 0
						|| ((nCurrMillis - mLastHistoryTime.get(i) < nCirle * 100) && nCurrMillis > mLastHistoryTime
								.get(i))) {
					break;
				}
				mLastHistoryTime.set(i, nCurrMillis);

				/* 是否需要继续采集 */
				if (!isFullCollect(nHistoryCollect, i)) {
					break;
				}

				/* 是否地址控制采样 */
				if (isBitOnAddrCtlSamps(nHistoryCollect, i, true) == false) {
					if (!CollectDataInfo.getInstance().getmHistoryInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmHistoryInfoList().get(i).setbResetCallback(true);
						unRegister(CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getmCallbackItems());
					}
					break;
				} else {
					if (CollectDataInfo.getInstance().getmHistoryInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmHistoryInfoList().get(i).setbResetCallback(false);
						onRegister(CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getmCallbackItems());
					}
				}

				/* 取得当前系统时间 */
				systemTime.setToNow();

				/* 如果是地址动态控制 */
				if (CollectDataInfo.getInstance().getmHistoryInfoList().get(i)
						.isbAddrCtlTime()) {
					/* 清空数据 */
					tmpDataList.clear();

					/* init send data struct */
					mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

					int nStartHourTime = 0;
					int nStartMinTime = 0;
					int nEndHourTime = 0;
					int nEndMinTime = 0;
					CollectDataInfo.getInstance().getmHistoryInfoList().get(i)
							.getmStartTimeAddr().nAddrLen = 2;
					CollectDataInfo.getInstance().getmHistoryInfoList().get(i)
							.getmEndTimeAddr().nAddrLen = 2;

					/* get data from plc */
					boolean bSuccess = PlcRegCmnStcTools.getRegIntData(
							CollectDataInfo.getInstance().getmHistoryInfoList()
									.get(i).getmStartTimeAddr(), tmpDataList,
							mSendData);
					if (bSuccess && tmpDataList.size() >= 2) {
						nStartHourTime = tmpDataList.get(0);
						nStartMinTime = tmpDataList.get(1);
					}

					/* get data from plc */
					tmpDataList.clear();
					bSuccess = PlcRegCmnStcTools.getRegIntData(CollectDataInfo
							.getInstance().getmHistoryInfoList().get(i)
							.getmEndTimeAddr(), tmpDataList, mSendData);
					if (bSuccess && tmpDataList.size() >= 2) {
						nEndHourTime = tmpDataList.get(0);
						nEndMinTime = tmpDataList.get(1);
					}

					int nStartTime = nStartHourTime * 60 + nStartMinTime;
					int nEndTime = nEndHourTime * 60 + nEndMinTime;
					int nNowTime = systemTime.hour * 60 + systemTime.minute;
					if (nNowTime < nStartTime || nNowTime > nEndTime) {
						break;
					}
				} else {
					int nStartHour = CollectDataInfo.getInstance()
							.getmHistoryInfoList().get(i).getnStartHour();
					int nStartMinute = CollectDataInfo.getInstance()
							.getmHistoryInfoList().get(i).getnStartMinute();
					int nEndHour = CollectDataInfo.getInstance()
							.getmHistoryInfoList().get(i).getnEndHour();
					int nEndMinute = CollectDataInfo.getInstance()
							.getmHistoryInfoList().get(i).getnEndMinute();

					int nStartTime = nStartHour * 60 + nStartMinute;
					int nEndTime = nEndHour * 60 + nEndMinute;
					int nNowTime = systemTime.hour * 60 + systemTime.minute;

					if (nNowTime < nStartTime || nNowTime > nEndTime)
						break;
				}

				/* 从PLC采集数据 */
				readDataFromPlc(nHistoryCollect, i, nCirle / 2, eSampType);
				break;
			}
			case CONSTANT_CYCLE_SAMP: // 固定周期采样
			{
				/* 如果没到这个采集周期则跳过 */
				while (mLastHistoryTime.size() <= i) {
					mLastHistoryTime.add((long) 0);
				}

				long nCurrMillis = System.currentTimeMillis();
				if (nCirle <= 0
						|| (nCurrMillis - mLastHistoryTime.get(i) < nCirle * 100 && nCurrMillis > mLastHistoryTime
								.get(i))) {
					break;
				}
				mLastHistoryTime.set(i, nCurrMillis);

				/* 是否取满 */
				if (!isFullCollect(nHistoryCollect, i)) {
					break;
				}

				/* 是否地址控制采样 */
				if (isBitOnAddrCtlSamps(nHistoryCollect, i, true) == false) {
					if (!CollectDataInfo.getInstance().getmHistoryInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmHistoryInfoList().get(i).setbResetCallback(true);
						unRegister(CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getmCallbackItems());
					}
					break;
				} else {
					if (CollectDataInfo.getInstance().getmHistoryInfoList().get(i).isbResetCallback()) {
						CollectDataInfo.getInstance().getmHistoryInfoList().get(i).setbResetCallback(false);
						onRegister(CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getmCallbackItems());
					}
				}

				/* 从PLC采集数据 */
				readDataFromPlc(nHistoryCollect, i, nCirle / 2, eSampType);
				break;
			}
			case BIT_ON_SAMP: {
				/* 是否取满 */
				if (!isFullCollect(nHistoryCollect, i)) {
					Log.d(TAG, "full........");
					break;
				}
				
				/* 上升沿采样 */
				CallbackItem item=CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getmCtlSampItem();
				if (item!=null) {
					//上升沿判断
					boolean isOff2On=false;
					
					if(item.isOffToOn()){
						isOff2On=true;
					}else{ 
						//读取触发地址值
						Vector<Byte> bitdataList = new Vector<Byte>();
						PlcRegCmnStcTools.getRegBytesData(CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getnCtlSampAddr(),
								bitdataList, mSendData);
						
						//防止刷新进程漏掉上升沿过程
						if(bitdataList.size()>0){
							if((!item.isnBValue())&&(bitdataList.get(0)==1)){
								isOff2On=true;
							}
						}
					}
					
					if (isOff2On) {
						//从off-->on 触发
						
//						Log.d(TAG, "isOffToOn......");
						/* 从PLC采集数据 */
						readDataFromPlc(nHistoryCollect, i, nCirle / 2, eSampType);
						
						item.setOffToOn(false);
						
						/* 是否自动复位 */
						boolean bAutoReset = CollectDataInfo.getInstance().getmHistoryInfoList().get(i).isbAutoReset();

						/* 自动复位 */
						if (bAutoReset) {
//							tmpDataList.clear();
//							tmpDataList.add(0);

							mSendData.eDataType = DATA_TYPE.BIT_1;
							mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

							byte dataList[] = new byte[1];
							// 根据布尔值传入不同的参数值
							dataList[0] = 0;
							
							boolean ret = PlcRegCmnStcTools.setRegBytesData(CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getnCtlSampAddr(), dataList,mSendData);
							
							item.setBValue(false);
						}
						
					}
					
				}
				
				break;
			}
			default: {
				break;
			}
			}
		}// end for
	}

	/**************************************************************************************************************************************************
	 * 以下方法都是供外面线程调用的接口，可能是多线程调用
	 ***************************************************************************************************************************************************/

	/**
	 * 注册采集一条数据就通知的接口
	 * @param eCollectType：采集的类型， 1代表实时采集，2代表历史数据采集
	 * @param nGroupIdList：数据采集的所有组号
	 * @param mCallBack：回调接口
	 */
	public synchronized void addNoticProp(int eCollectType, Vector<Integer> nGroupIdList,
			IPlcNoticCallBack mCallBack) {
		/* 添加通知 */
		DataCollectCallBack mCallbackInfo = new DataCollectCallBack();
		mCallbackInfo.eCollectType = eCollectType;
		mCallbackInfo.nGroupIds = nGroupIdList;
		mCallbackInfo.mCallbackInterface = mCallBack;
		mCallbackInfo.nDataList.clear();

		getCollectNoticHandler().obtainMessage(MODULE.ADD_CALL_BACK,mCallbackInfo).sendToTarget();
	}

	/**
	 * 删除回调接口
	 * @param mCallBack
	 */
	public void destoryDataCollectCallback(IPlcNoticCallBack mCallBack) {
		if (null == mCallBack)
			return;

		mHandler.removeMessages(MODULE.DATA_COLLECT_NOTIC);
		// mHandler.removeMessages(MODULE.ADD_CALL_BACK);会造成另一个画面注册的也删掉

		getCollectNoticHandler().obtainMessage(MODULE.DATA_COLLECT_DESTORY_NOTIC, mCallBack).sendToTarget();
	}

	/**
	 * 写数据到数据库
	 * @param index
	 * @param dataList
	 * @return
	 */
	public boolean writeDataToDatabase(EditDataCollectProp mEditProp) {
		if (null == mEditProp || null == mEditProp.nHistoryDataList){
			return false;
		}
			
		if (mEditProp.nHistoryDataList==null){
			return false;
		}

		SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
		if (null == mCollectDbObj){
			return false;
		}

		/* 存数据库的时间格式 */
		mCollectDbObj.beginTransaction();
		try {
			int nGroupSize = mEditProp.nHistoryDataList.size();
			while (nDatabaseNumList.size() < nGroupSize) {
				nDatabaseNumList.add((long) 0);
			}

			for (int i = 0; i < nGroupSize; i++) {
				int nDataSize = mEditProp.nHistoryDataList.get(i).size();
				for (int j = 0; j < nDataSize; j++) {
					long nCurrMillis = mEditProp.nHistoryDataList.get(i).get(j).nMillisTime;
					int nAddrSize = mEditProp.nHistoryDataList.get(i).get(j).nDataList
							.size();

					/* 开始插入数据 */
					m_tmpValues.clear();
					m_tmpValues.put("nMillis", nCurrMillis);
					
					for (int k = 0; k < nAddrSize; k++) {
						m_tmpValues.put("data" + k, mEditProp.nHistoryDataList.get(i).get(j).nDataList.get(k));
					}
					m_tmpValues.put("power", 0);
					
					mCollectDbObj.insertData("dataCollect" + i,m_tmpValues);
				}

				/* 保存最后一次存盘数据，方便掉电保存的比较 */
				if (nDataSize > 0) {
					while (i >= mLastSaveList.size()) {
						HistoryDataProp mHisDataProp = new HistoryDataProp();
						mLastSaveList.add(mHisDataProp);
					}

					mLastSaveList.set(i,mEditProp.nHistoryDataList.get(i).get(nDataSize - 1));
				}

				/* 数据库增加的数量 */
				nDatabaseNumList.set(i, nDatabaseNumList.get(i) + nDataSize);
			}
			mCollectDbObj.commitTransaction();
		}catch(Exception e){
			
		}finally {
			mCollectDbObj.endTransaction();
		}

		return true;
	}
	
	/**
     * 获取历史采集数据
     * @param cName-采集组名称
     * @param nTop-起始序号
     * @param num-获取行数
     * @param sTime-开始时间 ，格式为 “2013-11-24 22:12:00”
     * @param eTime-结束时间，格式为“2013-11-25 12:00:00”
     */
	public ArrayList<String[]> getCollectData(String cName,int nTop,int num,String sTime,String eTime){
		ArrayList<String[]> data=null;
		return data;
	}

	/**
	 * 导出数据到cvs文件
	 * @param eCollectType，数据采集类型，实时还是历史
	 * @param nDataGroupId， 组号
	 * @param nSaveTimeLen,保存的时间长度，单位为分钟， nSaveTimeLen为少于等于0则全部保存
	 * @return
	 */
	public boolean writeGroupDataToFile(EditDataCollectProp mEditProp) {

		Log.d(TAG, "writeGroupDataToFile......");
		if (null == mEditProp) {
			// 操作失败
			return false;
		}

		Activity myActivity = SKSceneManage.getInstance().getActivity();

		if (mEditProp.bShowPress) {
			/* 显示进度条 */
			String sMsg = " ";
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.write_database);
			}

			SKPlcNoticThread.getInstance().getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg)
					.sendToTarget();
		}

		if (CollectDataInfo.getInstance().getmHistoryInfoList()==null) {
			return false;
		}
		
		/* 判断nDataGroupId是否超限 */
		int nGroupRecipeSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		if (mEditProp.nGroupId < 0 || mEditProp.nGroupId >= nGroupRecipeSize) {
			if (mEditProp.bShowPress) {
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			}
			// 操作失败
			if (mEditProp.mOperCall != null) {
				mEditProp.mOperCall.result(false, "");
			}
			return false;
		}

		/* 取得这组数据的存储位置，方便取得保存的文件路径 */
		STORAGE_MEDIA eSaveMedia = null;
		if (mEditProp.mMedia != null) {
			// 使用操作路径
			eSaveMedia = mEditProp.mMedia;
		} else {
			// 使用采集默认路径
			eSaveMedia = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(mEditProp.nGroupId).geteSaveMedium();
		}
		String sFilePath = CollectDataInfo.getInstance().getmHistoryInfoList()
				.get(mEditProp.nGroupId).getsName();
		String temp=FileOper.getInstance().readCollectFile(sFilePath);
		if (temp!=null&&!temp.equals("")) {
			sFilePath=temp;
		}
		String sFileName = "data" + mEditProp.nGroupId;

		switch (eSaveMedia) {
		case INSIDE_DISH: {
			// 不能使用采集名称，有可能有中文，造成删完不了.
			sFilePath = "/data/data/com.android.Samkoonhmi/AK_History/data"
					+ mEditProp.nGroupId;
			break;
		}
		case U_DISH: {
			sFilePath = "/mnt/usb2/AK_History/" + sFilePath;
			break;
		}
		case SD_DISH: {
			sFilePath = "/mnt/sdcard/AK_History/" + sFilePath;
			break;
		}
		case OTHER_STORAGE_MEDIA: {//email发送邮件
			sFilePath = "/data/data/com.android.Samkoonhmi/files/email_files/" + sFilePath;
			break;
		}
		default: {
			if (mEditProp.bShowPress) {
				String sMsg = " ";
				if (myActivity != null) {
					sMsg = myActivity.getString(R.string.save_fail_path);
				}

				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
						.sendToTarget();
			}
			return false;
		}
		}

		/* 用当前时间来做文件名 */
		SimpleDateFormat formatTitil = new SimpleDateFormat(
				"yyyy-MM-dd HH.mm.ss");
		long nCurrTime = System.currentTimeMillis();
		Date mCurrDate = new Date(nCurrTime);
		String sCurrDateStr = formatTitil.format(mCurrDate);
		sFilePath = sFilePath + "_" + sCurrDateStr;
		sFileName = sFileName + "_" + sCurrDateStr + ".csv";

		/* 如果数据采集文件不存在则创建文件 */
		File mCollectFile = new File(sFilePath + ".csv");
		File mParantFile = mCollectFile.getParentFile();
		if (null == mParantFile) {
			Log.e("save collect data",
					"save data collect data failed, because file:" + sFilePath
							+ ".csv" + " open failed");
			if (mEditProp.bShowPress) {
				String sMsg = " ";
				if (myActivity != null) {
					sMsg = myActivity.getString(R.string.save_fail_path);
				}

				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
						.sendToTarget();
			}
			// 操作失败
			if (mEditProp.mOperCall != null) {
				mEditProp.mOperCall.result(false, "");
			}
			return false;
		}

		/* 父目录不存在 */
		if (!mParantFile.exists()) {
			mParantFile.mkdirs();
		}

		String sNewFilePath = sFilePath + ".csv";
		while (mCollectFile.exists()) {
			if (mEditProp.bShowPress) {
				String sMsg = " ";
				if (myActivity != null) {
					sMsg = myActivity.getString(R.string.save_fail_exist);
				}

				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
						.sendToTarget();
			}
			// 操作失败
			if (mEditProp.mOperCall != null) {
				mEditProp.mOperCall.result(false, "");
			}
			return false;
		}

		try {
			if (!mCollectFile.createNewFile()) {
				if (mEditProp.bShowPress) {
					String sMsg = " ";
					if (myActivity != null) {
						sMsg = myActivity.getString(R.string.save_fail_creat);
					}

					SKPlcNoticThread.getInstance().getMainUIHandler()
							.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
							.sendToTarget();
				}
				// 操作失败
				if (mEditProp.mOperCall != null) {
					mEditProp.mOperCall.result(false, "");
				}
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (mEditProp.bShowPress) {
				String sMsg = " ";
				if (myActivity != null) {
					sMsg = myActivity.getString(R.string.save_fail_creat);
				}

				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
						.sendToTarget();
			}
			// 操作失败
			if (mEditProp.mOperCall != null) {
				mEditProp.mOperCall.result(false, "");
			}
			return false;
		}

		try {
			/* new write file hand */
			// FileWriter fileWriteHand = new FileWriter(sNewFilePath, false);
			// BufferedWriter writeBuffer = new BufferedWriter(fileWriteHand);
			DataOutputStream fileWriteHand = new DataOutputStream(
					new FileOutputStream(mCollectFile));
			BufferedWriter writeBuffer = new BufferedWriter(
					new OutputStreamWriter(fileWriteHand, "GBK"));

			/* 先写标题 */
			writeBuffer.newLine();
			String sTitle = "date, time ";
			if (myActivity != null) {
				sTitle = myActivity.getString(R.string.save_datetime);
			}

			String sDataTitle = "data";
			if (myActivity != null) {
				sDataTitle = myActivity.getString(R.string.save_data_title);
			}

			int nAddrSize = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(mEditProp.nGroupId).getmCollectAddrList().size();
			for (int i = 1; i <= nAddrSize; i++) {
				String name = CollectDataInfo.getInstance()
						.getmHistoryInfoList().get(mEditProp.nGroupId)
						.getmCollectAddrList().get(i - 1).sName;
				if (name == null || name.equals("")) {
					sDataTitle = "data" + i;
				} else {
					sDataTitle = name;
				}
				sTitle += "," + sDataTitle;
			}

			writeBuffer.write(sTitle);
			// writeBuffer.close();
			// fileWriteHand.close();

			/* 从数据库取得数据 */
			SKDataBaseInterface mCollectDbObj = SkGlobalData
					.getDataCollectDatabase();
			if (null == mCollectDbObj) {
				if (mEditProp.bShowPress) {
					String sMsg = " ";
					if (myActivity != null) {
						sMsg = myActivity.getString(R.string.save_fail_nodata);
					}

					SKPlcNoticThread.getInstance().getMainUIHandler()
							.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
							.sendToTarget();
				}
				// 操作失败
				if (mEditProp.mOperCall != null) {
					mEditProp.mOperCall.result(false, "");
				}
				return false;
			}

			/* 计算最近时间 */
			String sWhereStr = " where  power=0  order by id";
			if (mEditProp.bAuto) {
				//自动导出
				if (mEditProp.nRecordTimeLen > 0) {
					/* 取当前时间 */
					long nCurrMillis = System.currentTimeMillis();
					nCurrMillis = (nCurrMillis - mEditProp.nRecordTimeLen * 3600 * 1000);
					sWhereStr = " where nMillis >= " + nCurrMillis+" and power=0";
				}
			}else{
				//地址控制导出
				
				Log.d("DataCollect", "time="+mEditProp.nStartTime+",len="+mEditProp.nRecordTimeLen);
				if (mEditProp.nStartTime==0) {
					//没有设置开始时间
					if (mEditProp.nRecordTimeLen > 0) {
						/* 取当前时间 */
						long nCurrMillis = System.currentTimeMillis();
						nCurrMillis = (nCurrMillis - mEditProp.nRecordTimeLen * 3600 * 1000);
						sWhereStr = " where nMillis >= " + nCurrMillis+" and power=0";
					}
				}else {
					//设置开始时间
					if (mEditProp.nRecordTimeLen > 0) {
						//获取多少小时
						long c=System.currentTimeMillis();
						long end=mEditProp.nRecordTimeLen * 3600 * 1000+mEditProp.nStartTime;
						sWhereStr = " where nMillis between " + mEditProp.nStartTime+" and "+end+" and power=0";
					}else {
						sWhereStr = " where nMillis >= " + mEditProp.nStartTime+" and power=0";
					}
					
				}
			}

			Cursor result = mCollectDbObj.getDatabaseBySql("select * from dataCollect" + mEditProp.nGroupId+ sWhereStr, null);

			if (null == result) {
				if (mEditProp.bShowPress) {
					String sMsg = " ";
					if (myActivity != null) {
						sMsg = myActivity.getString(R.string.save_fail_nodata);
					}

					SKPlcNoticThread.getInstance().getMainUIHandler()
							.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
							.sendToTarget();
				}

				// 操作失败
				if (mEditProp.mOperCall != null) {
					mEditProp.mOperCall.result(false, "");
				}
				return false;
			}

			/* 取得时间格式 */
			SimpleDateFormat formatStr = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			DATE_FORMAT eDateFormat = CollectDataInfo.getInstance()
					.getmHistoryInfoList().get(mEditProp.nGroupId)
					.geteDateShowType();
			switch (eDateFormat) {
			case YYYYMMDD_SLASH: {
				formatStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				break;
			}
			case MMDDYYYY_SLASH: {
				formatStr = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				break;
			}
			case DDMMYYYY_SLASH: {
				formatStr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				break;
			}
			case YYYYMMDD_POINT: {
				formatStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				break;
			}
			case MMDDYYYY_POINT: {
				formatStr = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
				break;
			}
			case DDMMYYYY_POINT: {
				formatStr = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				break;
			}
			default: {
				break;
			}
			}

			
			/* 取得数据 */
			String sLineDataStr = "";
			int nAddrNum = result.getColumnCount() - 3;
			while (result.moveToNext()) {
				long nCurrMillis = result.getLong(result.getColumnIndex("nMillis"));
				Date mStartDate = new Date(nCurrMillis);
				String sDateStr = formatStr.format(mStartDate);
				StringBuffer resultbuf = new StringBuffer();
				if (sDateStr != null) {
					try {
						String[] sDateTime = sDateStr.split(" ");
						if (sDateTime != null && sDateTime.length >= 2) {
							resultbuf.append(sDateTime[0] + ",");
							resultbuf.append(sDateTime[1] + ",");
						}
					} catch (PatternSyntaxException e) {
						e.printStackTrace();
					}
				}

				/* 根据数据类型来进行缩放输出 */
				for (int i = 0; i < nAddrNum; i++) {
					resultbuf.append(result.getString(i + 2));
					resultbuf.append(",");
				}
				sLineDataStr = resultbuf.toString();

				/* 写一行数据 */
				writeBuffer.newLine();
				writeBuffer.write(sLineDataStr);
				sLineDataStr = "";
			}

			/* close file */
			result.close();
			writeBuffer.flush();
			writeBuffer.close();
			fileWriteHand.close();
		} catch (Exception e) {
			e.printStackTrace();
			
			if (mEditProp.bShowPress) {
				String sMsg = " ";
				if (myActivity != null) {
					sMsg = myActivity.getString(R.string.save_fail_path);
				}

				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
						.sendToTarget();
			}

			// 操作成功
			if (mEditProp.mOperCall != null) {
				mEditProp.mOperCall.result(false, "");
			}
			return false;
		}

		// 操作成功
		if (mEditProp.mOperCall != null) {
			mEditProp.mOperCall.result(true, sFileName);
		}

		if (mEditProp.bShowPress) {
			String sMsg = " ";
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.save_success);
			}

			/* 休眠2秒钟，等文件写入成功 */
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			SKPlcNoticThread.getInstance().getMainUIHandler()
					.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			SKPlcNoticThread
					.getInstance()
					.getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg + sNewFilePath)
					.sendToTarget();
		}
		return true;
	}

	/**
	 * 从数据库中取采集记录
	 * @param nGroupId
	 * @param nGetNum
	 * @param sFirstTime开始时间，传出参数
	 * @return
	 */
	public synchronized Vector<Vector<String>> getGroupCollectData(
			int nGroupId, int nGetNum, Vector<Integer> nChannelList,
			HistoryCollectProp mDataProp) {

		try {

			/* 通道为空则返回 */
			if (null == nChannelList || nChannelList.isEmpty()){
				return null;
			}

			long nCurrMillis = System.currentTimeMillis();

			int nGoupSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
			if (nGroupId < 0 || nGroupId >= nGoupSize){
				return null;
			}

			SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
			if (null == mCollectDbObj) {
				return null;
			}

			Long nFirstMilles = (long) 0;
			Long nLastMilles = (long) 0;
			SimpleDateFormat formatStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			/* 从数据最新和最老的时间 */
			mCollectDbObj.beginTransaction();
			Cursor cus = mCollectDbObj
					.getDatabaseBySql(
							"select COUNT(id) AS nTotalNum, MIN(nMillis) AS min, MAX(nMillis) AS max from dataCollect"
									+ nGroupId, null);
			if (null != cus) {
				if (cus.moveToNext()) {
					/* 从数据取最老的时间 */
					nFirstMilles = cus.getLong(cus.getColumnIndex("min"));

					/* 从数据取最新的时间 */
					nLastMilles = cus.getLong(cus.getColumnIndex("max"));

					/* 起始时间 */
					if (null != mDataProp) {
						Date mStartDate = new Date(nFirstMilles);
						String sStartDate = formatStr.format(mStartDate);
						mDataProp.sFirstTimeStr = sStartDate;

						mStartDate = new Date(nLastMilles);
						sStartDate = formatStr.format(mStartDate);
						mDataProp.sEndTimeStr = sStartDate;
						mDataProp.nTotalRecord = cus.getInt(cus
								.getColumnIndex("nTotalNum"));
					}
				}
				cus.close();
			}
			mCollectDbObj.endTransaction();

			/* 记录条数 */
			if (nGetNum <= 0) {
				nGetNum = 1;
			}

			float nMillesDx = 0;
			if (nGetNum <= 1) {
				nMillesDx = (float) (nLastMilles - nFirstMilles) + 10;
			} else {
				nMillesDx = ((float) (nLastMilles - nFirstMilles))
						/ (nGetNum - 1);
			}

			if (nMillesDx <= 0) {
				nMillesDx = 1;
			}

			// String sQueryStr = "update dataCollect" + nGroupId
			// +" set nTmpPart = round((strftime('%s',mDateTime) - strftime('%s','"
			// + sStartDate + "'))/(" + nMillesDx + "));";

			String sMinList = "";
			String sMaxList = "";
			int nAddrId = 0;
			String sAddrIdColum = "";
			int nAddrLen = nChannelList.size();
			for (int i = 0; i < nAddrLen - 1; i++) {
				nAddrId = nChannelList.get(i);
				sAddrIdColum = "data" + nAddrId;
				sMinList += (" min(" + sAddrIdColum + ") as nMinData" + nAddrId + ", ");
				sMaxList += (" max(" + sAddrIdColum + ") as nMaxData" + nAddrId + ", ");
			}

			nAddrId = nChannelList.get(nAddrLen - 1);
			sAddrIdColum = "data" + nAddrId;
			sMinList += (" min(" + sAddrIdColum + ") as nMinData" + nAddrId + ", ");
			sMaxList += (" max(" + sAddrIdColum + ") as nMaxData" + nAddrId + " ");

			/* 开始事务 */
			mCollectDbObj.beginTransaction();
			String sQueryStr = "select round((nMillis - " + nFirstMilles
					+ ")/(" + nMillesDx + ")) nPart, " + sMinList + sMaxList
					+ " from dataCollect" + nGroupId + " group by nPart;";
			Cursor result = mCollectDbObj.getDatabaseBySql(sQueryStr, null);

			Vector<String> subDataList = null;
			Vector<Vector<String>> m_noticDataList = new Vector<Vector<String>>();
			Vector<Double> nOldDataList = new Vector<Double>();
			for (int i = 0; i < nAddrLen; i++) {
				nOldDataList.add(0.0);
			}

			double nTmpOldData = 0;
			boolean bHaveData = false;
			if (null != result) {
				/* 先取列 */
				int nPartIndex = result.getColumnIndex("nPart");
				Vector<Integer> nMinColunIndex = new Vector<Integer>();
				Vector<Integer> nMaxColunIndex = new Vector<Integer>();
				for (int i = 0; i < nAddrLen; i++) {
					nAddrId = nChannelList.get(i);
					nMinColunIndex.add(result.getColumnIndex("nMinData"
							+ nAddrId));
					nMaxColunIndex.add(result.getColumnIndex("nMaxData"
							+ nAddrId));
				}

				while (result.moveToNext()) {
					bHaveData = true;
					double nId = result.getDouble(nPartIndex);

					/* 没有数据则填充null */
					while (nId > m_noticDataList.size()) {
						m_noticDataList.add(null);
					}

					subDataList = new Vector<String>();
					subDataList.clear();
					if (m_noticDataList.isEmpty()) {
						for (int i = 0; i < nAddrLen; i++) {
							double nMin = result.getDouble(nMinColunIndex
									.get(i));

							subDataList.add(String.valueOf(nMin));
						}
					} else {
						for (int i = 0; i < nAddrLen; i++) {
							double nMin = result.getDouble(nMinColunIndex
									.get(i));
							double nMax = result.getDouble(nMaxColunIndex
									.get(i));
							// double nAvg =
							// result.getDouble(result.getColumnIndex("nAvgData"));

							/* 取差值最大的点 */
							nTmpOldData = nOldDataList.get(i);
							if (Math.abs(nTmpOldData - nMin) > Math.abs(nMax
									- nTmpOldData)) {
								subDataList.add(String.valueOf(nMin));
								nOldDataList.set(i, nMin);
							} else {
								subDataList.add(String.valueOf(nMax));
								nOldDataList.set(i, nMax);
							}
						}
					}

					/* 添加最后的数据 */
					m_noticDataList.add(subDataList);
				}
				result.close();
			}

			if (bHaveData == false) {
				System.out.println("getGroupCollectData :" + nGroupId
						+ ", failed, database have no data");
			}

			/* 关闭事务 */
			mCollectDbObj.endTransaction();

			System.out.println("getGroupCollectData need time :"
					+ (System.currentTimeMillis() - nCurrMillis) + "ms");
			return m_noticDataList;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "getGroupCollectData error!!!");
			return null;
		}
	}

	/**
	 * 按时间段来查询数据库
	 * @param nGroupId
	 * @param nGetNum
	 * @param sDateTimeBegin 起始时间,格式：“2012-5-6 8:23:42" 中间一定用空格隔开
	 * @param sDateTimeEnd 结束时间,格式：“2012-5-6 8:23:42" 中间一定用空格隔开
	 * @param sFirstTime 开始时间，传出参数
	 * @return
	 */
	public synchronized Vector<Vector<String>> getGroupCollectDataByTime(
			int nGroupId, int nGetNum, Vector<Integer> nChannelList,
			String sDateTimeBegin, String sDateTimeEnd,
			HistoryCollectProp mDataProp) {
		/* 通道为空则返回 */
		if (null == nChannelList || nChannelList.isEmpty()){
			return null;
		}

		long nCurrMillis = System.currentTimeMillis();

		int nGoupSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		if (nGroupId < 0 || nGroupId >= nGoupSize){
			return null;
		}

		SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
		if (null == mCollectDbObj) {
			return null;
		}

		Long nFirstMilles = (long) 0;
		Long nLastMilles = (long) 0;
		SimpleDateFormat formatStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		/* 时间限制 */
		try {
			nFirstMilles = formatStr.parse(sDateTimeBegin).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			nLastMilles = formatStr.parse(sDateTimeEnd).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			long nDbFirst = (long) 0;
			long nDbLast = (long) 0;
			String sDateTimeQuery = " where nMillis between " + nFirstMilles
					+ " and " + nLastMilles;

			/* 从数据最新和最老的时间 */
			mCollectDbObj.beginTransaction();
			Cursor cus = mCollectDbObj
					.getDatabaseBySql(
							"select COUNT(id) AS nTotalNum, MIN(nMillis) AS min, MAX(nMillis) AS max from dataCollect"
									+ nGroupId + sDateTimeQuery, null);
			if (null != cus) {
				if (cus.moveToNext()) {
					/* 从数据取最老的时间 */
					nDbFirst = cus.getLong(cus.getColumnIndex("min"));

					/* 从数据取最新的时间 */
					nDbLast = cus.getLong(cus.getColumnIndex("max"));

					/* 起始时间 */
					if (null != mDataProp) {
						Date mStartDate = new Date(nDbFirst);
						String sStartDate = formatStr.format(mStartDate);
						mDataProp.sFirstTimeStr = sStartDate;

						mStartDate = new Date(nDbLast);
						sStartDate = formatStr.format(mStartDate);
						mDataProp.sEndTimeStr = sStartDate;
						mDataProp.nTotalRecord = cus.getInt(cus
								.getColumnIndex("nTotalNum"));
					}
				}
				cus.close();
			}
			mCollectDbObj.endTransaction();

			/* 记录条数 */
			if (nGetNum <= 0) {
				nGetNum = 1;
			}

			float nMillesDx = 0;
			if (nGetNum <= 1) {
				nMillesDx = ((float) (nLastMilles - nFirstMilles)) + 10;
			} else {
				nMillesDx = ((float) (nLastMilles - nFirstMilles))
						/ (nGetNum - 1);
			}

			if (nMillesDx <= 0) {
				nMillesDx = 1;
			}

			String sMinList = "";
			String sMaxList = "";
			int nAddrId = 0;
			String sAddrIdColum = "";
			int nAddrLen = nChannelList.size();
			for (int i = 0; i < nAddrLen - 1; i++) {
				nAddrId = nChannelList.get(i);
				sAddrIdColum = "data" + nAddrId;
				sMinList += (" min(" + sAddrIdColum + ") as nMinData" + nAddrId + ", ");
				sMaxList += (" max(" + sAddrIdColum + ") as nMaxData" + nAddrId + ", ");
			}

			nAddrId = nChannelList.get(nAddrLen - 1);
			sAddrIdColum = "data" + nAddrId;
			sMinList += (" min(" + sAddrIdColum + ") as nMinData" + nAddrId + ", ");
			sMaxList += (" max(" + sAddrIdColum + ") as nMaxData" + nAddrId + " ");

			/* 开始事务 */
			mCollectDbObj.beginTransaction();
			String sQueryStr = "select round((nMillis - " + nFirstMilles
					+ ")/(" + nMillesDx + ")) nPart, " + sMinList + sMaxList
					+ " from dataCollect" + nGroupId + sDateTimeQuery
					+ " group by nPart;";
			Cursor result = mCollectDbObj.getDatabaseBySql(sQueryStr, null);

			Vector<String> subDataList = null;
			Vector<Vector<String>> m_noticDataList = new Vector<Vector<String>>();
			Vector<Double> nOldDataList = new Vector<Double>();
			for (int i = 0; i < nAddrLen; i++) {
				nOldDataList.add(0.0);
			}

			double nTmpOldData = 0;
			boolean bHaveData = false;
			if (null != result) {
				/* 先取列 */
				int nPartIndex = result.getColumnIndex("nPart");
				Vector<Integer> nMinColunIndex = new Vector<Integer>();
				Vector<Integer> nMaxColunIndex = new Vector<Integer>();
				for (int i = 0; i < nAddrLen; i++) {
					nAddrId = nChannelList.get(i);
					nMinColunIndex.add(result.getColumnIndex("nMinData"
							+ nAddrId));
					nMaxColunIndex.add(result.getColumnIndex("nMaxData"
							+ nAddrId));
				}

				while (result.moveToNext()) {
					bHaveData = true;
					double nId = result.getDouble(nPartIndex);

					/* 没有数据则填充null */
					while (nId > m_noticDataList.size()) {
						m_noticDataList.add(null);
					}

					subDataList = new Vector<String>();
					subDataList.clear();
					if (m_noticDataList.isEmpty()) {
						for (int i = 0; i < nAddrLen; i++) {
							double nMin = result.getDouble(nMinColunIndex
									.get(i));

							subDataList.add(String.valueOf(nMin));
						}
					} else {
						for (int i = 0; i < nAddrLen; i++) {
							double nMin = result.getDouble(nMinColunIndex
									.get(i));
							double nMax = result.getDouble(nMaxColunIndex
									.get(i));
							// double nAvg =
							// result.getDouble(result.getColumnIndex("nAvgData"));

							/* 取差值最大的点 */
							nTmpOldData = nOldDataList.get(i);
							if (Math.abs(nTmpOldData - nMin) > Math.abs(nMax
									- nTmpOldData)) {
								subDataList.add(String.valueOf(nMin));
								nOldDataList.set(i, nMin);
							} else {
								subDataList.add(String.valueOf(nMax));
								nOldDataList.set(i, nMax);
							}
						}
					}

					/* 添加最后的数据 */
					m_noticDataList.add(subDataList);
				}
				result.close();
			}

			if (bHaveData == false) {
				System.out.println("getGroupCollectData :" + nGroupId
						+ ", failed, database have no data");
			}

			/* 关闭事务 */
			mCollectDbObj.endTransaction();

			System.out.println("getGroupCollectData need time :"
					+ (System.currentTimeMillis() - nCurrMillis) + "ms");

			return m_noticDataList;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "getGroupCollectDataByTime() error!!");
			return null;
		}
	}

	/**
	 * 按时间段来查询数据库
	 * @param nGroupId
	 * @param top 开始行号
	 * @param nGetNum
	 * @param sDateTimeBegin 起始时间,格式：“2012-5-6 8:23:42" 中间一定用空格隔开
	 * @param sDateTimeEnd 结束时间,格式：“2012-5-6 8:23:42" 中间一定用空格隔开
	 * @param sFirstTime 开始时间，传出参数
	 * @return
	 */
	public synchronized Vector<HistoryDataProp> getHistoryDataByTime(
			int nGroupId, int top, int nGetNum, Vector<Integer> nChannelList,
			long nDateTimeBegin, long nDateTimeEnd) {

		long nCurrMillis = System.currentTimeMillis();

		Long nFirstMilles = nDateTimeBegin;
		Long nLastMilles = nDateTimeEnd;

		String sDateTimeQuery = "";
		if (nFirstMilles == 0 && nLastMilles == 0) {
			// 用户没有设置时间范围，读取全部
			sDateTimeQuery = " where power=0 ";
			//power=1 表示补充数据，掉电时刻两个点
		} else {
			if (nFirstMilles == 0) {
				sDateTimeQuery = " where nMillis <= " + nLastMilles;
			} else if (nLastMilles == 0) {
				sDateTimeQuery = " where nMillis >= " + nFirstMilles;
			} else {
				sDateTimeQuery = " where nMillis between " + nFirstMilles
						+ " and " + nLastMilles;
			}
			sDateTimeQuery+=" and power=0 ";
			//power=1 表示补充数据，掉电时刻两个点
		}

		String sQueryStr = "select * from dataCollect" + nGroupId
				+ sDateTimeQuery + " limit " + (top - 1) + "," + nGetNum;

		Vector<HistoryDataProp> noticDataList = getHistoryDataBySQL(nGroupId,
				nChannelList, sQueryStr);

		System.out.println("getHistoryDataByTime need time :"+ (System.currentTimeMillis() - nCurrMillis) + "ms");

		return noticDataList;
	}

	/**
	 * 
	 * @param nGroupId 数据库标识
	 * @param top 开始行号
	 * @param nGetNum 搜索结果最大值
	 * @param nChannelList 通道
	 * @param sQueryElement 搜索条件目标
	 * @param SearchCondition 搜索条件
	 * @return Vector<HistoryDataProp> noticDataList 搜索结果
	 */
	public Vector<HistoryDataProp> getHistoryDataByCoditin(int nGroupId,
			int top, int nGetNum, Vector<Integer> nChannelList,
			String sQueryElement, String SearchCondition) {
		// TODO Auto-generated method stub

		long nCurrMillis = System.currentTimeMillis();

		String temp=" and power=0 ";
		if (sQueryElement==null||sQueryElement.equals("")) {
			temp=" power=0 ";
		}
		String sQueryStr = "select * from dataCollect" + nGroupId + " where "
				+ sQueryElement + SearchCondition +temp + " limit " + (top - 1) + ","
				+ nGetNum;

		Vector<HistoryDataProp> noticDataList = getHistoryDataBySQL(nGroupId,
				nChannelList, sQueryStr);

		System.out.println("getHistoryDataByCoditin need time :"
				+ (System.currentTimeMillis() - nCurrMillis) + "ms");

		return noticDataList;
	}

	/**
	 * 通过SQL语句查询数据库
	 * @param nGroupId 数据库标识
	 * @param nChannelList 通道
	 * @param sQueryStr 查询语句
	 * @return
	 */
	public Vector<HistoryDataProp> getHistoryDataBySQL(int nGroupId,
			Vector<Integer> nChannelList, String sQueryStr) {

		// TODO Auto-generated method stub
		/* 通道为空则返回 */
		if (null == nChannelList || nChannelList.isEmpty())
			return null;

		int nGoupSize = CollectDataInfo.getInstance().getmHistoryInfoList()
				.size();
		if (nGroupId < 0 || nGroupId >= nGoupSize)
			return null;

		SKDataBaseInterface mCollectDbObj = SkGlobalData
				.getDataCollectDatabase();
		if (null == mCollectDbObj) {
			Log.e("getHistoryDataBySQL", "database not exist");
			return null;
		}

		Cursor result = mCollectDbObj.getDatabaseBySql(sQueryStr, null);

		boolean bHaveData = false;
		Vector<HistoryDataProp> noticDataList = new Vector<HistoryDataProp>();

		if (null != result) {

			int nAddrLen = nChannelList.size();

			while (result.moveToNext()) {

				bHaveData = true;

				HistoryDataProp hisData = new HistoryDataProp();

				try {
					hisData.nMillisTime = result.getLong(1);
				} catch (Exception e) {
					hisData.nMillisTime = 0;
				}
				hisData.nDataList.clear();

				String id = result.getString(0);
				hisData.nDataList.add(id);

				/* 计算列的长度 */
				int nColumnCount = result.getColumnCount();
				if (nAddrLen > (nColumnCount - 2)) {
					nAddrLen = nColumnCount - 2;
				}

				if (nAddrLen > nChannelList.size()) {
					nAddrLen = nChannelList.size();
				}

				for (int i = 0; i < nAddrLen; i++) {
					int nAddrId = nChannelList.get(i);
					String sData = result.getString(nAddrId + 2);
					hisData.nDataList.add(sData);
				}

				/* 添加最后的数据 */
				noticDataList.add(hisData);
			}
			result.close();
		}

		if (bHaveData == false) {
			System.out.println("getGroupCollectData :" + nGroupId
					+ ", failed, database have no data");
		}

		return noticDataList;

	}

	/**
	 * 获取该段日期的数据总数
	 * @param start-开始日期
	 * @param end-结束日期
	 */
	public int getDataCount(int gid, long start, long end) {
		int result = 0;
		SKDataBaseInterface mCollectDbObj = SkGlobalData
				.getDataCollectDatabase();
		if (null == mCollectDbObj) {
			Log.e("getHistoryDataByTime", "database not exist");
			return 0;
		}

		String sql = "";
		if (start == 0 && end == 0) {
			sql = "select count(*) as num From dataCollect" + gid+" where power=0";
		} else {
			if (start == 0) {
				sql = "select count(*) as num From dataCollect" + gid
						+ " where nMillis <= " + end;
			} else if (end == 0) {
				sql = "select count(*) as num From dataCollect" + gid
						+ " where nMillis >= " + start;
			} else {
				sql = "select count(*) as num From dataCollect" + gid
						+ " where nMillis between " + start + " and " + end;
			}
			sql+=" and power=0";
		}

		Cursor cursor = mCollectDbObj.getDatabaseBySql(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				result = cursor.getInt(cursor.getColumnIndex("num"));
			}
			cursor.close();
		}
		return result;
	}

	/**
	 * 获取符合条件的数据总数
	 * @param nGroupId 数据库标号
	 * @param sQueryElement 查询列
	 * @param SearchCondition 查询条件
	 * @return
	 */
	public int getDataCount(int nGroupId, String sQueryElement,
			String SearchCondition) {

		int result = 0;
		SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
		if (null == mCollectDbObj) {
			Log.e("getHistoryDataByCoditin", "database not exist");
			return 0;
		}

		String sql = "";
		String temp=" and power=0 ";
		if (sQueryElement==null||sQueryElement.equals("")) {
			temp=" power=0 ";
		}
		sql = "select count(*) as num From dataCollect" + nGroupId + " where "
				+ sQueryElement + SearchCondition +temp;

		Cursor cursor = mCollectDbObj.getDatabaseBySql(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				result = cursor.getInt(cursor.getColumnIndex("num"));
			}
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 按时段删除数据
	 * @param mList--采集组
	 * @param start--开始时间
	 * @param end----结束时间
	 */
	public void msgClearHistoryByTime(ArrayList<Integer> mList,long start,long end,boolean hasEnd){
		if (mList == null || mList.size() == 0) {
			return;
		}
		if(collectState != COLLECT_WAIT){
			return;
		}
		collectState = COLLECT_START;
		
		/*
		 * 防止mEditData.nEndTime没有正确初始化
		 * mEditData.nEndTime=0为非时段删除
		*/
		if(end==0){
			end=-1;
		}
		
		tmpGroupId = (ArrayList<Integer>) mList.clone();
		
//		//暂停采集
//		mHandler.removeMessages(MODULE.DATA_COLLECT_NOTIC);
		
		
		//刷新数据：先clear再init.init在数据库删除后进行
//		mHandler.obtainMessage(MODULE.DATA_COLLECT_CLEAR, mList).sendToTarget();
		sendMsgWriteDatabase();
		
		//删除数据库
		for (int i = 0; i < mList.size(); i++) {
			
			EditDataCollectProp mEditData = new EditDataCollectProp();
			mEditData.nGroupId = mList.get(i);
			mEditData.nRecordTimeLen=-1;
			mEditData.nStartTime = start;
			mEditData.nEndTime = end;
			mEditData.bHasEndTime = hasEnd;
			
			/* 发送消息 */
			SkGlobalBackThread
					.getInstance()
					.getGlobalBackHandler()
					.obtainMessage(MODULE.DELETE_HISTORY_FROM_DATABASE,
							mEditData).sendToTarget();
		}
		
	}
	
	/**
	 * 发消息清除所有历史数据
	 * @param mGid =采集组id
	 */
	public void msgClearAllHistory(ArrayList<Integer> mList) {

		if (mList == null || mList.size() == 0) {
			return;
		}

		// 清除缓存组id集合
//		int mGroupId[] = new int[mList.size()];

		for (int i = 0; i < mList.size(); i++) {
			EditDataCollectProp mEditData = new EditDataCollectProp();
			mEditData.nGroupId = mList.get(i);
			mEditData.nRecordTimeLen = -1;

//			mGroupId[i] = mList.get(i);

			/* 发送消息 */
			SkGlobalBackThread
					.getInstance()
					.getGlobalBackHandler()
					.obtainMessage(MODULE.DELETE_HISTORY_FROM_DATABASE,
							mEditData).sendToTarget();
			nDatabaseNumList.set(mList.get(i), (long) 0);
		}

		/* 回调通知 */
		for (int j = 0; j < mList.size(); j++) {
			int nCallbackSize = m_noticCallBackList.size();
			for (int i = 0; i < nCallbackSize; i++) {
				m_noticCallBackList.get(i).mCallbackInterface
						.noticDelDatabase(mList.get(j));
			}
		}

		mHandler.obtainMessage(MODULE.DATA_COLLECT_CLEAR, mList)
				.sendToTarget();
		/* 发消息清除缓存 */
		// mHandler.sendEmptyMessage(MODULE.DATA_COLLECT_CLEAR);
	}

	/**
	 * 获取最后采集点的时间，用于历史曲线的显示
	 * @param gid-历史采集组id
	 */
	public long[] getLastTime(int gid) {
		long time[] = new long[] { 0, 0 };
		SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
		if (mCollectDbObj == null) {
			return time;
		}
		String sql = "select min(nMillis) as starttime,max(nMillis) as lasttime from datacollect"+ gid;
		Cursor cursor = mCollectDbObj.getDatabaseBySql(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				time[0] = cursor.getLong(cursor.getColumnIndex("starttime"));
				time[1] = cursor.getLong(cursor.getColumnIndex("lasttime"));
			}
			cursor.close();
		}

		return time;
	}
}
