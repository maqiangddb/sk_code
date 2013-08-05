package com.android.Samkoonhmi.model.skglobalcmn;

import java.util.Vector;

/**
 * 数据采集的属性类
 * @author Latory
 *
 */
public class CollectDataInfo {

	private CollectDataInfo(){
	}

	/*获取数据采集的单例*/
	private static CollectDataInfo mDataCollectInfo = null;
	public synchronized static CollectDataInfo getInstance(){
		if(null == mDataCollectInfo)
		{
			mDataCollectInfo = new CollectDataInfo();
		}

		return mDataCollectInfo;
	}

	/*历史数据属性集合*/
	private Vector<HistoryDataCollect > mHistoryInfoList = new Vector<HistoryDataCollect >();

	/*实时数据属性集合*/
	private Vector<RealTimeCollect > mRealTimeInfoList = new Vector<RealTimeCollect >();

	public Vector<HistoryDataCollect> getmHistoryInfoList() {
		return mHistoryInfoList;
	}

	public void setmHistoryInfoList(Vector<HistoryDataCollect> mHistoryInfoList) {
		this.mHistoryInfoList = mHistoryInfoList;
	}

	public Vector<RealTimeCollect> getmRealTimeInfoList() {
		return mRealTimeInfoList;
	}

	public void setmRealTimeInfoList(Vector<RealTimeCollect> mRealTimeInfoList) {
		this.mRealTimeInfoList = mRealTimeInfoList;
	}

	
}
