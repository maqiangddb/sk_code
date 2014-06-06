package com.android.Samkoonhmi.skglobalcmn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.content.ContentValues;
import android.util.Log;
import com.android.Samkoonhmi.databaseinterface.DataCollectBiz;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;

public class SkGlobalData {

	/* 工程数据库对象 */
	private static SKDataBaseInterface m_projectDatabase;

	/* 数据采集数据库对象 */
	private static SKDataBaseInterface m_dataCollectDatabase;

	/* 配方数据库对象 */
	private static SKDataBaseInterface m_recipeDatabase;

	/**
	 * 报警数据库文件
	 */
	private static SKDataBaseInterface mAlarmDatabase;
	private SkGlobalData() {
		m_projectDatabase = null;
		m_dataCollectDatabase = null;
		m_recipeDatabase = null;
		mAlarmDatabase = null;
	}

	/**
	 * 获得工程数据库对象接口
	 * @return
	 */
	public synchronized static SKDataBaseInterface getProjectDatabase() {

		/* 判断文件是否存在 */
		String sDatabasePath = "/data/data/com.android.Samkoonhmi/databases/sd.dat";
		File mSddbFile = new File(sDatabasePath);
		if (!mSddbFile.exists()) {
			return null;
		}

		if (null == m_projectDatabase) {
			m_projectDatabase = new SKDataBaseInterface();
			m_projectDatabase.openDatabase(sDatabasePath);
		}
		if (!m_projectDatabase.databaseIsOpen()) {
			m_projectDatabase.openDatabase(sDatabasePath);
		}

		return m_projectDatabase;
	}

	/**
	 * 创建画面菜单表
	 */
	public static void createMenuTable() {

		if (m_projectDatabase == null) {
			getProjectDatabase();
		}

		/**
		 * 画面位置信息数据表sceneInfoSql nSceneId-画面Id nSceneName-画面名称 nScenePic-画面路径
		 * nItemWidth-画面的宽度 nItemHeigh-画面的高度 nFontSize-画面的大小
		 * nPageId-画面所在页码，从1开始计数 nPagePos-画面所在页面的位置，从0开始计数；
		 */
		String sceneInfoSql = "Create  TABLE IF NOT EXISTS [scenePosInfo] "
				+ "(id integer PRIMARY KEY AUTOINCREMENT NOT NULL"
				+ ",nSceneId integer" + ",nSceneName text" + ",nScenePic text"
				+ ",nItemWidth integer" + ",nItemHeigh integer"
				+ ",nFontSize integer" + ",nPageId integer"
				+ ",nPagePos integer);";

		if (m_projectDatabase != null) {
			m_projectDatabase.execSql(sceneInfoSql);
		}
	}

	/**
	 * 设置工程数据库对象接口
	 * 
	 * @param projectDatabase
	 */
	private static void setProjectDatabase(SKDataBaseInterface projectDatabase) {
		SkGlobalData.m_projectDatabase = projectDatabase;
	}

	/**
	 * 获取配方数据库的接口
	 * @return
	 */
	public synchronized static SKDataBaseInterface getRecipeDatabase() {
		if (null == m_recipeDatabase) {
			m_recipeDatabase = new SKDataBaseInterface();
			m_recipeDatabase
					.openDatabase("/data/data/com.android.Samkoonhmi/formula/recipe.dat");
		}
		if (!m_recipeDatabase.databaseIsOpen()) {
			m_recipeDatabase
					.openDatabase("/data/data/com.android.Samkoonhmi/formula/recipe.dat");
		}

		return m_recipeDatabase;
	}

	/**
	 * 获得数据采集数据库对象接口
	 * @return
	 */
	public synchronized static SKDataBaseInterface getDataCollectDatabase() {

		String sDatabasePath = "/data/data/com.android.Samkoonhmi/databases/dataCollectSave.db";
		if (null == m_dataCollectDatabase) {
			m_dataCollectDatabase = new SKDataBaseInterface();

			/* 判断文件是否存在 */
			File mdbFile = new File(sDatabasePath);
			if (mdbFile.exists()) {
				Log.d("SKScene", "DataCollectDatabase existe file............");
				m_dataCollectDatabase.openDatabase(sDatabasePath);
			} else {
				/* 创建数据采集 */
				String sExcelSql = "] (id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
						+ "nMillis long";

				boolean bSuccess = true;
				String sTableName = "CREATE TABLE [dataCollect";

				// int nHistorySize =
				// CollectDataInfo.getInstance().getmHistoryInfoList().size();

				ArrayList<Integer> list = DataCollectBiz.getHisAddrSize();
				int nHistorySize = DataCollectBiz.getHistCollentCount();
				Log.d("SKScene", "Create DataColleact table size:"
						+ nHistorySize);
				for (int i = 0; i < nHistorySize; i++) {
					String sTmpSqlStr = sTableName + i + sExcelSql;

					// int nAddrSize =
					// CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getmCollectAddrList().size();
					int nAddrSize = 0;
					if (i < list.size()) {
						nAddrSize = list.get(i);
					}

					/* 创建一个地址对应一个列 */
					String sDataColum = "";
					for (int k = 0; k < nAddrSize; k++) {
						sDataColum += (",data" + k) + " varchar";
					}

					sTmpSqlStr += sDataColum +",power integer " + ");";
					//power-0 正常数据，power-1 为了真实反映掉电区域，手动补上的数据
					bSuccess &= m_dataCollectDatabase.createDatabase(
							sDatabasePath, sTmpSqlStr);
				}

				/* 创建组号和组名的表 */
				ArrayList<String> mList = DataCollectBiz.getHisName();
				sTableName = "CREATE TABLE [collectGroupName] (id integer NOT NULL PRIMARY KEY AUTOINCREMENT,nGroupId integer, sGroupName varchar);";
				bSuccess &= m_dataCollectDatabase.createDatabase(sDatabasePath,
						sTableName);
				if (bSuccess) {
					ContentValues mTmpValues = new ContentValues();
					for (int i = 0; i < nHistorySize; i++) {
						mTmpValues.clear();
						// String sName =
						// CollectDataInfo.getInstance().getmHistoryInfoList().get(i).getsName();
						String sName = "";
						if (mList.size() > i) {
							sName = mList.get(i);
						}
						mTmpValues.put("nGroupId", i);
						mTmpValues.put("sGroupName", sName);
						m_dataCollectDatabase.insertData("collectGroupName",
								mTmpValues);
					}
				}

				if (!bSuccess) {
					return null;
				}
			}
		}

		return m_dataCollectDatabase;
	}

	/**
	 * 获取报警数据库
	 */
	public synchronized static SKDataBaseInterface getAlarmDatabase() {

		String sDatabasePath = "/data/data/com.android.Samkoonhmi/alarm/alarm.db";
		if (mAlarmDatabase == null) {
			mAlarmDatabase = new SKDataBaseInterface();

			File mFile = new File(sDatabasePath);
			if (mFile.exists()) {
				mAlarmDatabase.openDatabase(sDatabasePath);
			} else {
				try {
					File dir = new File(
							"/data/data/com.android.Samkoonhmi/alarm");
					if (!dir.exists()) {
						dir.mkdir();
					}
					mFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				boolean bSuccess = true;
				/**
				 * 报警登录，数据存储表 dataAlarm nGroupId-报警组id nAlarmIndex-组里面每个报警id
				 * nDateTime-报警触发时间和日期 nClearDT-报警消除时间和日期
				 * nClear-报警状态，-1-未触发,0-已经触发,1-已经确定，2-已经消除
				 */
				String alarmSql = "Create  TABLE[dataAlarm](id integer PRIMARY KEY AUTOINCREMENT NOT NULL"
						+ ",nGroupId integer"
						+ ",nAlarmIndex integer"
						+ ",nDateTime long"
						+ ",nClearDT long"
						+ ",nClear smallint);";

				bSuccess &= mAlarmDatabase.createDatabase(sDatabasePath,
						alarmSql);

				/**
				 * 报警信息多语言表 nGroupId-报警组id nAlarmIndex-组里面每个报警id
				 * nLanguageId-语言id sMessage-文本信息
				 */
				String alarmMsgSql = "Create  TABLE[alarmText](id integer PRIMARY KEY AUTOINCREMENT NOT NULL"
						+ ",nGroupId integer"
						+ ",nAlarmIndex integer"
						+ ",nLanguageId integer" + ",sMessage text);";

				bSuccess &= mAlarmDatabase.createDatabase(sDatabasePath,
						alarmMsgSql);

				/**
				 * 当前语言，方便上位导出
				 */
				String alarmlanSql = "Create  TABLE[alarmLan](id integer PRIMARY KEY AUTOINCREMENT NOT NULL"
						+ ",nLanguageId integer);";

				bSuccess &= mAlarmDatabase.createDatabase(sDatabasePath,
						alarmlanSql);

				if (!bSuccess) {
					return null;
				}
			}
		}
		return mAlarmDatabase;
	}

}
