package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.skglobalcmn.CollectAddrProp;
import com.android.Samkoonhmi.model.skglobalcmn.CollectDataInfo;
import com.android.Samkoonhmi.model.skglobalcmn.HistoryDataCollect;
import com.android.Samkoonhmi.model.skglobalcmn.RealTimeCollect;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.FULL_DEAL_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.SAMP_TYPE;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

public class DataCollectBiz {

	public static boolean select() {
		/* 打开数据 */
		boolean bSuccess = SkGlobalData.getProjectDatabase().openDatabase(null);
		if (false == bSuccess) {
			return false;
		}

		/* 按照组号的升序查询， nDataSampType = 1是实时采集，nDataSampType = 2 是历史采集 */
		Cursor dataProp = SkGlobalData
				.getProjectDatabase()
				.getDatabaseBySql(
						"select * from dataCollect where nDataSampType = 1 order by nGroupId ",
						null);
		if (null == dataProp) {
			return true;
		}

		RealTimeCollect mRealTimeInfo = null;
		CollectDataInfo.getInstance().getmRealTimeInfoList().clear();
		int nTmpEnum = 0;
		AddrProp mTmpAddr = null;
		while (dataProp.moveToNext()) {
			mRealTimeInfo = new RealTimeCollect();

			/* 采集组的ID */
			mRealTimeInfo.setnGroupId(dataProp.getShort(dataProp
					.getColumnIndex("nGroupId")));

			/* 采集组的名字 */
			mRealTimeInfo.setsName(dataProp.getString(dataProp
					.getColumnIndex("sName")));

			/* 采样条件（采样方式） */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("eSampType"));
			switch (nTmpEnum) {
			case 1: {
				mRealTimeInfo.seteSampType(SAMP_TYPE.FIXED_TIME_SAMP);
				break;
			}
			case 2: {
				mRealTimeInfo.seteSampType(SAMP_TYPE.CONSTANT_CYCLE_SAMP);
				break;
			}
			case 3: {
				mRealTimeInfo.seteSampType(SAMP_TYPE.BIT_ON_SAMP);
				break;
			}
			default: {
				break;
			}
			}

			/* 位地址控制是否采样 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("bAddrCtlSamp"));
			if (nTmpEnum == 1) {
				mRealTimeInfo.setbAddrCtlSamp(true);
			} else {
				mRealTimeInfo.setbAddrCtlSamp(false);
			}

			/* 控制采样的地址ID号 */
			nTmpEnum = dataProp.getInt(dataProp
					.getColumnIndex("nCtlSampAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mRealTimeInfo.setnCtlSampAddr(mTmpAddr);
			if (mTmpAddr!=null) {
				CallbackItem item=new CallbackItem();
				item.eDataType=DATA_TYPE.BIT_1;
				item.onRegister(mTmpAddr, true, 0);
				mRealTimeInfo.setmCtlSampItem(item);
			}

			/* 开始的时（0-23） */
			mRealTimeInfo.setnStartHour(dataProp.getShort(dataProp
					.getColumnIndex("nStartHour")));

			/* 开始的分（0-59） */
			mRealTimeInfo.setnStartMinute(dataProp.getShort(dataProp
					.getColumnIndex("nStartMinute")));

			/* 结束的时 */
			mRealTimeInfo.setnEndHour(dataProp.getShort(dataProp
					.getColumnIndex("nEndHour")));

			/* 结束的分 */
			mRealTimeInfo.setnEndMinute(dataProp.getShort(dataProp
					.getColumnIndex("nEndMinute")));

			/* 是否地址控制时间范围 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("bAddrCtlTime"));
			mRealTimeInfo.setbAddrCtlTime(nTmpEnum == 1 ? true : false);

			/* 开始时间地址 */
			nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nStartAddr"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mRealTimeInfo.setmStartTimeAddr(mTmpAddr);

			/* 结束时间地址 */
			nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nEndAddr"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mRealTimeInfo.setmEndTimeAddr(mTmpAddr);

			/* 是否地址控制采样频率 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("bAddrCtlRate"));
			if (nTmpEnum == 1) {
				mRealTimeInfo.setbAddrCtlRate(true);

				/* 控制频率的地址 */
				nTmpEnum = dataProp
						.getInt(dataProp.getColumnIndex("nSampRate"));
				mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(
						nTmpEnum);
				mRealTimeInfo.setmCtlRateAddr(mTmpAddr);
			} else {
				mRealTimeInfo.setbAddrCtlRate(false);

				/* 采样的频率 */
				mRealTimeInfo.setnSampRate(dataProp.getInt(dataProp
						.getColumnIndex("nSampRate")));
			}

			/* 是否自动复位 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bAutoReset"));
			if (nTmpEnum == 1) {
				mRealTimeInfo.setbAutoReset(true);
			} else {
				mRealTimeInfo.setbAutoReset(false);
			}

			CollectDataInfo.getInstance().getmRealTimeInfoList()
					.add(mRealTimeInfo);
		}
		dataProp.close();

		/* 查询采集地址集 */
		int collectDataSize = CollectDataInfo.getInstance()
				.getmRealTimeInfoList().size();
		int nGroupId = 0;
		CollectAddrProp mAddrProp = null;
		for (int i = 0; i < collectDataSize; i++) {
			nGroupId = CollectDataInfo.getInstance().getmRealTimeInfoList()
					.get(i).getnGroupId();
			dataProp = SkGlobalData.getProjectDatabase().getDatabaseBySql(
					"select * from dataCollectAddr where nDataSampType = 1 and  nGroupId = "
							+ nGroupId + " order by nArrayId", null);
			if (null == dataProp) {
				continue;
			}

			while (dataProp.moveToNext()) {
				mAddrProp = new CollectAddrProp();

				/* 取地址值 */
				nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nAddrId"));
				mAddrProp.mAddrPro = SkGlobalData.getProjectDatabase()
						.getAddrById(nTmpEnum);

				/* 取数据类型 */
				nTmpEnum = dataProp.getShort(dataProp
						.getColumnIndex("eDataType"));
				mAddrProp.eDataType = IntToEnum.getDataType(nTmpEnum);

				/* 小数位数 */
				mAddrProp.nDecLength = dataProp.getInt(dataProp
						.getColumnIndex("nDecLength"));

				/* 是否四舍五入 */
				mAddrProp.bFloatRound = dataProp.getShort(dataProp
						.getColumnIndex("bRound")) == 1 ? true : false;

				/* 取是否数据处理 */
				mAddrProp.bDealData = dataProp.getShort(dataProp
						.getColumnIndex("bDealData")) == 1 ? true : false;

				/* 源采集最小值 */
				mAddrProp.nSourceMin = dataProp.getDouble(dataProp
						.getColumnIndex("nSourceMin"));

				/* 源采集最大值 */
				mAddrProp.nSourceMax = dataProp.getDouble(dataProp
						.getColumnIndex("nSourceMax"));

				/* 目标采集最小值 */
				mAddrProp.nTargeMin = dataProp.getDouble(dataProp
						.getColumnIndex("nTargeMin"));

				/* 目标采集最大值 */
				mAddrProp.nTargeMax = dataProp.getDouble(dataProp
						.getColumnIndex("nTargeMax"));

				CollectDataInfo.getInstance().getmRealTimeInfoList()
						.elementAt(i).getmCollectAddrList().add(mAddrProp);

				SAMP_TYPE type = CollectDataInfo.getInstance()
						.getmRealTimeInfoList().get(i).geteSampType();

				/* 导出列名称 */
				mAddrProp.sName = dataProp.getString(dataProp
						.getColumnIndex("sName"));

				// 位ON时控制采样,不采用回调通知，直接读取
				if (mAddrProp.mAddrPro != null
						&& (type != SAMP_TYPE.BIT_ON_SAMP)) {
					// 创建回调通知对象
					CallbackItem item = new CallbackItem();
					item.eDataType = mAddrProp.eDataType;
					if (DATA_TYPE.UNICODE_STRING == item.eDataType) {
						item.eDataType = DATA_TYPE.ASCII_STRING;
						item.isUnicode = true;
					}
					// 注册回调通知
					item.onRegister(mAddrProp.mAddrPro, false, 0);
					// 加入实时采集集合
					CollectDataInfo.getInstance().getmRealTimeInfoList()
							.elementAt(i).getmCallbackItems().add(item);
				}

			}

			dataProp.close();
		}

		/* 按照组号的升序查询， nDataSampType = 1是实时采集，nDataSampType = 2 是历史采集 */
		dataProp = SkGlobalData
				.getProjectDatabase()
				.getDatabaseBySql(
						"select * from dataCollect where nDataSampType = 2 order by nGroupId ",
						null);
		if (null == dataProp) {
			return true;
		}

		HistoryDataCollect mHistoryInfo = null;
		CollectDataInfo.getInstance().getmHistoryInfoList().clear();
		while (dataProp.moveToNext()) {
			mHistoryInfo = new HistoryDataCollect();

			/* 采集组的ID */
			mHistoryInfo.setnGroupId(dataProp.getShort(dataProp
					.getColumnIndex("nGroupId")));

			/* 采集组的名字 */
			mHistoryInfo.setsName(dataProp.getString(dataProp
					.getColumnIndex("sName")));

			/* 采样条件（采样方式） */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("eSampType"));
			switch (nTmpEnum) {
			case 1: {
				mHistoryInfo.seteSampType(SAMP_TYPE.FIXED_TIME_SAMP);
				break;
			}
			case 2: {
				mHistoryInfo.seteSampType(SAMP_TYPE.CONSTANT_CYCLE_SAMP);
				break;
			}
			case 3: {
				mHistoryInfo.seteSampType(SAMP_TYPE.BIT_ON_SAMP);
				break;
			}
			default: {
				break;
			}
			}

			/* 采样保存时间 */
			mHistoryInfo.setnTotalSampNum(dataProp.getInt(dataProp
					.getColumnIndex("nSaveTime")));

			/* 取满处理方式 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("eDealSampFull"));
			switch (nTmpEnum) {
			case 1: {
				mHistoryInfo
						.seteDealSampFull(FULL_DEAL_TYPE.FULL_REPLACE_OLD_DATA);
				break;
			}
			case 2: {
				mHistoryInfo
						.seteDealSampFull(FULL_DEAL_TYPE.FULL_CLEAR_OLD_DATA);
				break;
			}
			case 3: {
				mHistoryInfo.seteDealSampFull(FULL_DEAL_TYPE.FULL_STOP_SAMP);
				break;
			}
			default: {
				break;
			}
			}

			/* 是否取满通知 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bFullNotic"));
			if (nTmpEnum == 1) {
				mHistoryInfo.setbFullNotic(true);
			} else {
				mHistoryInfo.setbFullNotic(false);
			}

			/* 地址通知的ID号 */
			nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nNoticAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mHistoryInfo.setnNoticAddrId(mTmpAddr);

			/* 位地址控制是否采样 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("bAddrCtlSamp"));
			if (nTmpEnum == 1) {
				mHistoryInfo.setbAddrCtlSamp(true);
			} else {
				mHistoryInfo.setbAddrCtlSamp(false);
			}

			/* 控制采样的地址ID号 */
			nTmpEnum = dataProp.getInt(dataProp
					.getColumnIndex("nCtlSampAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mHistoryInfo.setnCtlSampAddr(mTmpAddr);
			if (mTmpAddr!=null) {
				CallbackItem item=new CallbackItem();
				item.eDataType=DATA_TYPE.BIT_1;
				item.onRegister(mTmpAddr, true, 0);
				mHistoryInfo.setmCtlSampItem(item);
			}

			/* 开始的时（0-23） */
			mHistoryInfo.setnStartHour(dataProp.getShort(dataProp
					.getColumnIndex("nStartHour")));

			/* 开始的分（0-60） */
			mHistoryInfo.setnStartMinute(dataProp.getShort(dataProp
					.getColumnIndex("nStartMinute")));

			/* 结束的时 */
			mHistoryInfo.setnEndHour(dataProp.getShort(dataProp
					.getColumnIndex("nEndHour")));

			/* 结束的分 */
			mHistoryInfo.setnEndMinute(dataProp.getShort(dataProp
					.getColumnIndex("nEndMinute")));

			/* 是否地址控制时间范围 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("bAddrCtlTime"));
			mHistoryInfo.setbAddrCtlTime(nTmpEnum == 1 ? true : false);

			/* 开始时间地址 */
			nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nStartAddr"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mHistoryInfo.setmStartTimeAddr(mTmpAddr);

			/* 结束时间地址 */
			nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nEndAddr"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mHistoryInfo.setmEndTimeAddr(mTmpAddr);

			/* 是否地址控制采样频率 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("bAddrCtlRate"));
			if (nTmpEnum == 1) {
				mHistoryInfo.setbAddrCtlRate(true);

				/* 控制频率的地址 */
				nTmpEnum = dataProp
						.getInt(dataProp.getColumnIndex("nSampRate"));
				mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(
						nTmpEnum);
				mHistoryInfo.setmCtlRateAddr(mTmpAddr);
			} else {
				mHistoryInfo.setbAddrCtlRate(false);

				/* 采样的频率 */
				mHistoryInfo.setnSampRate(dataProp.getInt(dataProp
						.getColumnIndex("nSampRate")));
			}

			/* 是否自动复位 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bAutoReset"));
			if (nTmpEnum == 1) {
				mHistoryInfo.setbAutoReset(true);
			} else {
				mHistoryInfo.setbAutoReset(false);
			}

			/* 是否保存文件 */
			nTmpEnum = dataProp
					.getShort(dataProp.getColumnIndex("bSaveToFile"));
			if (nTmpEnum == 1) {
				mHistoryInfo.setbSaveToFile(true);
			} else {
				mHistoryInfo.setbSaveToFile(false);
			}

			/* 保存文件的控制地址 */
			nTmpEnum = dataProp.getInt(dataProp
					.getColumnIndex("nCtlSaveAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpEnum);
			mHistoryInfo.setnCtlSaveAddrId(mTmpAddr);

			/* 是否自动保存CSV */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bAutoSave"));
			if (nTmpEnum == 1) {
				mHistoryInfo.setbAutoSave(true);
			} else {
				mHistoryInfo.setbAutoSave(false);
			}

			/* 自动保存的时间间隔 */
			nTmpEnum = dataProp
					.getInt(dataProp.getColumnIndex("nIntervalTime"));
			mHistoryInfo.setnIntervalTime(nTmpEnum);

			/* 保存的媒介 */
			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("bPrint"));
			if (nTmpEnum == 0) {
				mHistoryInfo.setPrintState(0);
			} else {
				int state = 1;
				nTmpEnum = dataProp.getShort(dataProp
						.getColumnIndex("bPrintDate"));
				if (nTmpEnum > 0) {
					state += 2;
				}
				nTmpEnum = dataProp.getShort(dataProp
						.getColumnIndex("bPrintTime"));
				if (nTmpEnum > 0) {
					state += 4;
				}
				mHistoryInfo.setPrintState(state);
			}

			nTmpEnum = dataProp.getShort(dataProp.getColumnIndex("eSaveMedium"));
			switch (nTmpEnum) {
			case 2: {
				mHistoryInfo.seteSaveMedium(STORAGE_MEDIA.INSIDE_DISH);
				break;
			}
			case 1: {
				mHistoryInfo.seteSaveMedium(STORAGE_MEDIA.U_DISH);
				break;
			}
			case 3: {
				mHistoryInfo.seteSaveMedium(STORAGE_MEDIA.SD_DISH);
				break;
			}
			default: {
				break;
			}
			}

			/* 日期格式 */
			nTmpEnum = dataProp.getShort(dataProp
					.getColumnIndex("eDateShowType"));
			mHistoryInfo.seteDateShowType(IntToEnum.getDateType(nTmpEnum));

			CollectDataInfo.getInstance().getmHistoryInfoList()
					.add(mHistoryInfo);
		}
		dataProp.close();

		/* 查询采集地址集 */
		collectDataSize = CollectDataInfo.getInstance().getmHistoryInfoList()
				.size();
		for (int i = 0; i < collectDataSize; i++) {
			nGroupId = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(i).getnGroupId();
			dataProp = SkGlobalData.getProjectDatabase().getDatabaseBySql(
					"select * from dataCollectAddr where nDataSampType = 2 and nGroupId = "
							+ nGroupId + " order by nArrayId", null);
			if (null == dataProp) {
				continue;
			}

			while (dataProp.moveToNext()) {
				mAddrProp = new CollectAddrProp();

				/* 取地址值 */
				nTmpEnum = dataProp.getInt(dataProp.getColumnIndex("nAddrId"));
				mAddrProp.mAddrPro = SkGlobalData.getProjectDatabase()
						.getAddrById(nTmpEnum);

				/* 取数据类型 */
				nTmpEnum = dataProp.getShort(dataProp
						.getColumnIndex("eDataType"));
				mAddrProp.eDataType = IntToEnum.getDataType(nTmpEnum);

				/* 小数位数 */
				mAddrProp.nDecLength = dataProp.getInt(dataProp
						.getColumnIndex("nDecLength"));

				/* 是否四舍五入 */
				mAddrProp.bFloatRound = dataProp.getShort(dataProp
						.getColumnIndex("bRound")) == 1 ? true : false;

				/* 取是否数据处理 */
				mAddrProp.bDealData = dataProp.getShort(dataProp
						.getColumnIndex("bDealData")) == 1 ? true : false;

				/* 源采集最小值 */
				mAddrProp.nSourceMin = dataProp.getDouble(dataProp
						.getColumnIndex("nSourceMin"));

				/* 源采集最大值 */
				mAddrProp.nSourceMax = dataProp.getDouble(dataProp
						.getColumnIndex("nSourceMax"));

				/* 目标采集最小值 */
				mAddrProp.nTargeMin = dataProp.getDouble(dataProp
						.getColumnIndex("nTargeMin"));

				/* 目标采集最大值 */
				mAddrProp.nTargeMax = dataProp.getDouble(dataProp
						.getColumnIndex("nTargeMax"));

				CollectDataInfo.getInstance().getmHistoryInfoList()
						.elementAt(i).getmCollectAddrList().add(mAddrProp);
				SAMP_TYPE type = CollectDataInfo.getInstance()
						.getmHistoryInfoList().get(i).geteSampType();

				/* 导出列名称 */
				mAddrProp.sName = dataProp.getString(dataProp
						.getColumnIndex("sName"));

				// 位ON时控制采样,不采用回调通知，直接读取
				if (mAddrProp.mAddrPro != null
						&& (type != SAMP_TYPE.BIT_ON_SAMP)) {
					// 创建回调通知对象
					CallbackItem item = new CallbackItem();
					item.eDataType = mAddrProp.eDataType;
					if (DATA_TYPE.UNICODE_STRING == item.eDataType) {
						item.eDataType = DATA_TYPE.ASCII_STRING;
						item.isUnicode = true;
					}
					// 注册回调通知
					item.onRegister(mAddrProp.mAddrPro, false, 0);
					// 加入历史采集集合
					CollectDataInfo.getInstance().getmHistoryInfoList()
							.elementAt(i).getmCallbackItems().add(item);
				}

			}

			dataProp.close();
		}

		return true;
	}

	/**
	 * 获取历史采集组数
	 */
	public static int getHistCollentCount() {
		int count = 0;

		/* 打开数据 */
		boolean bSuccess = SkGlobalData.getProjectDatabase().openDatabase(null);
		if (false == bSuccess) {
			return count;
		}

		/* 按照组号的升序查询， nDataSampType = 1是实时采集，nDataSampType = 2 是历史采集 */
		Cursor dataProp = SkGlobalData
				.getProjectDatabase()
				.getDatabaseBySql(
						"select count(*) as count from dataCollect where nDataSampType = 2",
						null);
		if (null != dataProp) {
			if (dataProp.moveToNext()) {
				count = dataProp.getInt(dataProp.getColumnIndex("count"));
			}
			return count;
		}
		return count;
	}

	/**
	 * 历史采集地址列表
	 */
	public static ArrayList<Integer> getHisAddrSize() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		String sql = "select count(*) as count from dataCollectAddr  where nDataSampType = 2   group by  nGroupId ";

		boolean bSuccess = SkGlobalData.getProjectDatabase().openDatabase(null);
		if (false == bSuccess) {
			return list;
		}

		/* 按照组号的升序查询， nDataSampType = 1是实时采集，nDataSampType = 2 是历史采集 */
		Cursor dataProp = SkGlobalData.getProjectDatabase().getDatabaseBySql(
				sql, null);
		if (null != dataProp) {
			while (dataProp.moveToNext()) {
				int size = dataProp.getInt(dataProp.getColumnIndex("count"));
				list.add(size);
			}
			return list;
		}
		return list;
	}

	/**
	 * 历史采集名称列表
	 */
	public static ArrayList<String> getHisName() {
		ArrayList<String> list = new ArrayList<String>();
		String sql = "select sName from dataCollect where nDataSampType = 2 ";

		boolean bSuccess = SkGlobalData.getProjectDatabase().openDatabase(null);
		if (false == bSuccess) {
			return list;
		}

		/* 按照组号的升序查询， nDataSampType = 1是实时采集，nDataSampType = 2 是历史采集 */
		Cursor dataProp = SkGlobalData.getProjectDatabase().getDatabaseBySql(
				sql, null);

		if (null != dataProp) {
			while (dataProp.moveToNext()) {
				String name = dataProp.getString(dataProp
						.getColumnIndex("sName"));
				list.add(name);
			}

			return list;
		}
		return list;
	}
}
