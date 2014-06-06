package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.android.Samkoonhmi.model.BitSceneModle;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.LanguageInfo;
import com.android.Samkoonhmi.model.PassWordInfo;
import com.android.Samkoonhmi.model.PlcAttributeInfo;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.HMIMODEL;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.LIGHTENESS;
import com.android.Samkoonhmi.skenum.PRINT_MODEL;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.SystemParam;

public class SystemInfoBiz extends DataBase {
	private SKDataBaseInterface db;

	public SystemInfoBiz() {
		db = SkGlobalData.getProjectDatabase();
	}

	public void selectSystemInfo() {
		if (db == null) {
			db = SkGlobalData.getProjectDatabase();
		}

		Cursor cursor = db.getDatabaseBySql("select * from systemProp", null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				if (null != cursor.getString(cursor
						.getColumnIndex("bProtectType"))) {
					SystemInfo.setbProtectType(cursor.getString(
							cursor.getColumnIndex("bProtectType")).equals(
							"true") ? true : false);
				}
				if (null != cursor.getString(cursor
						.getColumnIndex("bScreensaver"))) {
					SystemInfo.setbScreensaver(cursor.getString(
							cursor.getColumnIndex("bScreensaver")).equals(
							"true") ? true : false);
				}
				LIGHTENESS lighteness = IntToEnum.getlighteness(cursor
						.getInt(cursor.getColumnIndex("nBrightness")));
				int light = getLight(lighteness);
				SystemInfo.setnBrightness(light);
				SystemInfo.setnFlickRate(cursor.getInt(cursor
						.getColumnIndex("nFlickRate")));
				SystemInfo.setNkChangeScreenAddr(AddrPropBiz.selectById(cursor
						.getInt(cursor.getColumnIndex("kChangeScreenAddr"))));
				SystemInfo.setNkRecipeIndexAddr(AddrPropBiz.selectById(cursor
						.getInt(cursor.getColumnIndex("kRecipeIndexAddr"))));
				SystemInfo.setNkWriteLanguageAddr(AddrPropBiz.selectById(cursor
						.getInt(cursor.getColumnIndex("kWriteLanguageAddr"))));
				SystemInfo.setNkWriteScreenAddr(AddrPropBiz.selectById(cursor
						.getInt(cursor.getColumnIndex("kWriteScreenAddr"))));
				SystemInfo.setnModel(cursor.getString(cursor
						.getColumnIndex("nModel")));
				//将读取到的model值截取存入，区分mid 和 ak
				HMIMODEL hmimodel = getModel(SystemInfo.getnModel());
				SystemInfo.setModel(hmimodel);
				SystemInfo.setsScreenIndex(cursor.getString(cursor
						.getColumnIndex("sScreenIndex")));
				SystemInfo.setnScreenTime(cursor.getInt(cursor
						.getColumnIndex("nScreenTime")));
				SystemInfo.setnSetBoolParam(cursor.getInt(cursor
						.getColumnIndex("nSetBoolParam")));
				SystemInfo.setsStartScreen(cursor.getString(cursor
						.getColumnIndex("sStartScreen")));
				// SystemInfo.setsProtectValue(cursor.getString(cursor
				// .getColumnIndex("sProtectValue")));
				SystemInfo.setsUploadPassword(cursor.getString(cursor
						.getColumnIndex("sUploadPassword")));
				List<PlcConnectionInfo> plcConnectionList = getPlcConnectionInfo();
				if (null != plcConnectionList) {
					SystemInfo.setPlcConnectionList(plcConnectionList);
				}
				List<LanguageInfo> languageList = getLanguageInfo();
				if (null == languageList) {
					SystemInfo.setLanguageList(null);
					SystemInfo.setLanguageNumber(0);
				} else {
					SystemInfo.setLanguageList(languageList);
					SystemInfo.setLanguageNumber(languageList.size());
				}
				List<PassWordInfo> passList = getPasswordInfo();
				if (null != passList) {
					SystemInfo.setPassWord(passList);
				}

				SystemInfo.setCurrentLanguageId(cursor.getInt(cursor
						.getColumnIndex("nLanguageIndex")));
				int recipeGroupId = cursor.getInt(cursor
						.getColumnIndex("nRecipeGroupId"));
				if (recipeGroupId<0) {
					recipeGroupId=0;
				}
				int recipeId = cursor.getInt(cursor
						.getColumnIndex("nRecipeIndex"));
				if (recipeId<0) {
					recipeId=0;
				}
				RecipeDataCentre.getInstance().setCurrRecipe(recipeGroupId,
						recipeId);
				int id = DBTool
						.getInstance()
						.getmSceneBiz()
						.getSceneId(
								cursor.getInt(cursor
										.getColumnIndex("nInitScreenId")));
				SystemInfo.setCurrentScenceId(id);
				SystemInfo.setInitSceneId(cursor.getInt(cursor
						.getColumnIndex("nInitScreenId")));// 拿到的是序号
				int addId = cursor.getInt(cursor.getColumnIndex("nstartLB"));
				SystemInfo.setnstartLB(addId);
				SystemInfo.setnlengthLB(cursor.getInt(cursor
						.getColumnIndex("nlengthLB")));
				int addlwId = cursor.getInt(cursor.getColumnIndex("nstartLW"));
				SystemInfo.setnstartLW(addlwId);
				SystemInfo.setnlengthLW(cursor.getInt(cursor
						.getColumnIndex("nlengthLW")));
				boolean bBit = false;
				if (null != cursor
						.getString(cursor.getColumnIndex("bBitScene"))) {
					bBit = cursor.getString(cursor.getColumnIndex("bBitScene"))
							.equals("true") ? true : false;
					SystemInfo.setbBitScene(bBit);
				}
				if (bBit) {
					SystemInfo.setBitSceneList(getBitSceneList());
				}
				SystemInfo.setbSimulator(cursor.getShort(cursor
						.getColumnIndex("bSimulator")));
				boolean bIcon = false;
				if (null != cursor
						.getString(cursor.getColumnIndex("bLockIcon"))) {
					bIcon = cursor
							.getString(cursor.getColumnIndex("bLockIcon"))
							.equals("true") ? true : false;
					SystemInfo.setbLockIcon(bIcon);
				}
				//是否启用了监控密码
				if ((SystemParam.LONG_INSPECT & SystemInfo.getnSetBoolParam()) == SystemParam.LONG_INSPECT) {
					SystemInfo.setStrMonitor(cursor.getString(cursor
							.getColumnIndex("strMonitor")));
				}
				SystemInfo.setnMonitorPort(cursor.getInt(cursor.getColumnIndex("nMonitorPort")));
				SystemInfo.setStrHmiName(cursor.getString(cursor
						.getColumnIndex("strHmiName")));
				SystemInfo.setmPrintModel(getPrintModel(cursor.getInt(cursor.getColumnIndex("nPrinterType"))));

			}
		}
		close(cursor);

	}

	private List<BitSceneModle> getBitSceneList() {
		List<BitSceneModle> list = null;
		if (db == null) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql("select * from bitScene", null);
		if (null != cursor) {
			list = new ArrayList<BitSceneModle>();
			while (cursor.moveToNext()) {
				BitSceneModle bit = new BitSceneModle();
				bit.setId(cursor.getInt(cursor.getColumnIndex("nId")));
				bit.setnBitAddress(AddrPropBiz.selectById(cursor.getInt(cursor
						.getColumnIndex("nAddressId"))));
				bit.setnSceneId(cursor.getInt(cursor.getColumnIndex("nSceneId")));
				bit.setnStatus(cursor.getInt(cursor.getColumnIndex("nStatus")));
				if (null != cursor.getString(cursor.getColumnIndex("bReset"))) {
					bit.setbRest(cursor.getString(
							cursor.getColumnIndex("bReset")).equals("true") ? true
							: false);
				}
				String sClose=cursor.getString(cursor.getColumnIndex("bClose"));
				boolean bClose=false;
				if (sClose!=null) {
					bClose=sClose.equals("true")?true:false;
				}
				bit.setbClose(bClose);
				
				list.add(bit);
			}
			close(cursor);
		}

		return list;
	}

	/**
	 * 密码列表
	 * 
	 * @return
	 */
	private List<PassWordInfo> getPasswordInfo() {
		Cursor cursor = db.getDatabaseBySql("select * from hmiProtect", null);
		List<PassWordInfo> list = null;
		if (null != cursor) {
			list = new ArrayList<PassWordInfo>();
			while (cursor.moveToNext()) {
				PassWordInfo info = new PassWordInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				info.setsPwdStr(cursor.getString(cursor
						.getColumnIndex("sPwdStr")));
				info.setsTimeLimit(cursor.getString(cursor
						.getColumnIndex("sTimeLimit")));
				info.setsTimeOut(cursor.getString(cursor
						.getColumnIndex("sTimeOut")));
				if (null != cursor.getString(cursor.getColumnIndex("bIsUse"))) {
					boolean boo = cursor.getString(
							cursor.getColumnIndex("bIsUse")).equals("true") ? true
							: false;
					info.setUser(boo);
				}

				list.add(info);
			}
			close(cursor);
		}
		return list;
	}

	/**
	 * 查找语言列表
	 * 
	 * @return
	 */
	private List<LanguageInfo> getLanguageInfo() {
		List<LanguageInfo> listInfo = null;
		Cursor cursor = db.getDatabaseBySql("select * from languageList", null);
		if (null != cursor) {
			listInfo = new ArrayList<LanguageInfo>();
			while (cursor.moveToNext()) {
				LanguageInfo info = new LanguageInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				info.setsLanguageName(cursor.getString(cursor
						.getColumnIndex("sLanguageName")));
				listInfo.add(info);
			}
			close(cursor);
		}
		return listInfo;
	}

	/**
	 * plc连接表
	 * 
	 * @return
	 */
	private List<PlcConnectionInfo> getPlcConnectionInfo() {

		List<PlcConnectionInfo> plcInfo = null;
		Cursor cursor = db.getDatabaseBySql(
				"select * from plcConnectProp order by eConnectPort", null);
		if (null != cursor) {
			plcInfo = new ArrayList<PlcConnectionInfo>();

			int nConnectId = 0;
			while (cursor.moveToNext()) {
				PlcConnectionInfo info = new PlcConnectionInfo();
				if (null != cursor.getString(cursor
						.getColumnIndex("bUseRelationPort"))) {

					info.setbUseRelationPort(cursor.getString(
							cursor.getColumnIndex("bUseRelationPort")).equals(
							"true") ? true : false);
				}
				info.seteConnectPort(cursor.getShort(cursor
						.getColumnIndex("eConnectPort")));
				info.seteRelationPort(cursor.getShort(cursor
						.getColumnIndex("eRelationPort")));
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				int baudRate = cursor
						.getInt(cursor.getColumnIndex("nBaudRate"));
				info.setnBaudRate(baudRate);
				info.setnCheckType(cursor.getInt(cursor
						.getColumnIndex("nCheckType")));

				nConnectId = cursor.getInt(cursor.getColumnIndex("nConnectId"));
				info.setnConnectId(nConnectId);
				info.setnDataBits(cursor.getShort(cursor
						.getColumnIndex("nDataBits")));

				info.setnScreenNo(cursor.getShort(cursor
						.getColumnIndex("nScreenNo")));
				info.setnStopBit((short) (cursor.getShort(cursor
						.getColumnIndex("nStopBit")) + 1));

				/* 添加是否是主屏 */
				short nTmpValue = cursor.getShort(cursor
						.getColumnIndex("bMasterScreen"));
				if (nTmpValue == 1) {
					info.setbMasterScreen(true);
				} else {
					info.setbMasterScreen(false);
				}

				/* 添加是否连接从屏口 */
				nTmpValue = cursor.getShort(cursor
						.getColumnIndex("bConnectScreenPort"));
				if (nTmpValue == 1) {
					info.setbConnectScreenPort(true);
				} else {
					info.setbConnectScreenPort(false);
				}

				/* 连接从屏口的数量 */
				nTmpValue = cursor.getShort(cursor
						.getColumnIndex("nSlaveScreenNum"));
				info.setnSlaveScreenNum(nTmpValue);

				List<PlcAttributeInfo> plcAttributeList = getPlcAttributeInfo(nConnectId);
				if (null != plcAttributeList && !plcAttributeList.isEmpty()) {
					info.setPlcAttributeList(plcAttributeList);
				}

				info.setsConnectName(cursor.getString(cursor
						.getColumnIndex("sConnectName")));
				plcInfo.add(info);
			}
			close(cursor);
		}

		return plcInfo;
	}

	/**
	 * 查找com 和以太网的信息的信息 3 com1 4 com2 8以太网
	 * 
	 * @return
	 */
	public PlcConnectionInfo findComInfo(int eConnectPort) {
		PlcConnectionInfo info = null;
		Cursor cursor = db.getDatabaseBySql(
				"select * from plcConnectProp where eConnectPort=?",
				new String[] { eConnectPort + "" });
		if (null != cursor) {
			while (cursor.moveToNext()) {
				info = new PlcConnectionInfo();
				if (null != cursor.getString(cursor
						.getColumnIndex("bUseRelationPort"))) {
					info.setbUseRelationPort(cursor.getString(
							cursor.getColumnIndex("bUseRelationPort")).equals(
							"true") ? true : false);
				}
				info.seteConnectPort(cursor.getShort(cursor
						.getColumnIndex("eConnectPort")));
				info.seteRelationPort(cursor.getShort(cursor
						.getColumnIndex("eRelationPort")));
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				int baudRate = cursor
						.getInt(cursor.getColumnIndex("nBaudRate"));
				info.setnBaudRate(baudRate);
				info.setnCheckType(cursor.getInt(cursor
						.getColumnIndex("nCheckType")));
				info.setnConnectId(cursor.getInt(cursor
						.getColumnIndex("nConnectId")));
				info.setnDataBits(cursor.getShort(cursor
						.getColumnIndex("nDataBits")));
				info.setnScreenNo(cursor.getShort(cursor
						.getColumnIndex("nScreenNo")));
				info.setnStopBit((short) (cursor.getShort(cursor
						.getColumnIndex("nStopBit")) + 1));
				info.setsConnectName(cursor.getString(cursor
						.getColumnIndex("sConnectName")));
			}
			close(cursor);
		}

		return info;
	}

	/**
	 * 获取屏号
	 * 
	 * @return
	 */
	public int getSceenNum() {
		List<PlcConnectionInfo> list = SystemInfo.getPlcConnectionList();
		int sceenNum = 1;
		if (null != list) {
			if (list.size() > 0) {
				PlcConnectionInfo info = list.get(0);
				sceenNum = info.getnScreenNo();
			}
		}
		return sceenNum;
	}

	/**
	 * plc协议属性
	 * 
	 * @return
	 */
	private List<PlcAttributeInfo> getPlcAttributeInfo( int nIndex) {
		List<PlcAttributeInfo> attributeInfo = null;
		Cursor cursor = db.getDatabaseBySql(
				"select * from protocolProp where nConnectIndex = " + nIndex,
				null);
		if (null != cursor) {
			attributeInfo = new ArrayList<PlcAttributeInfo>();
			while (cursor.moveToNext()) {
				PlcAttributeInfo info = new PlcAttributeInfo();
				info.setsPlcServiceType(cursor.getString(cursor
						.getColumnIndex("sPlcServiceType")));
				info.setId(cursor.getInt(cursor.getColumnIndex("id")));
				info.setnConnectIndex(cursor.getInt(cursor
						.getColumnIndex("nConnectIndex")));
				info.setnMaxRWLen(cursor.getInt(cursor
						.getColumnIndex("nMaxRWLen")));
				info.setnPlcNo(cursor.getShort(cursor.getColumnIndex("nPlcNo")));
				info.setnReceive_Timeout(cursor.getShort(cursor
						.getColumnIndex("nReceiveTimeout")));
				info.setnRetryTime(cursor.getShort(cursor
						.getColumnIndex("nRetryTime")));
				info.setnMinCollectCycle(cursor.getInt(cursor
						.getColumnIndex("nMinCollectCycle")));
				info.setnIntervalTime(cursor.getShort(cursor
						.getColumnIndex("nIntervalTime")));
				info.setnUserPlcId(cursor.getShort(cursor
						.getColumnIndex("nUserPlcId")));
				info.setnNetPortNum(cursor.getInt(cursor
						.getColumnIndex("nNetPortNum")));
				info.setsIpAddr(cursor.getString(cursor
						.getColumnIndex("sIpAddr")));
				if (null != cursor
						.getString(cursor.getColumnIndex("bIsNetTcp"))) {
					info.setIsNetTcp(cursor.getString(
							cursor.getColumnIndex("bIsNetTcp")).equals("true") ? false
							: true);
				}
				attributeInfo.add(info);
			}
			close(cursor);
		}
		return attributeInfo;
	}

	/**
	 * \ 屏保亮度转换
	 * 
	 * @param light
	 * @return
	 */
	public int getLight(LIGHTENESS light) {
		int lighteness = 0;
		switch (light) {
		case no_light:
			lighteness = 1;
			break;
		case small_light:
			lighteness = 10;
			break;
		case middle_light:
			lighteness = 127;
			break;
		default:
			break;
		}
		return lighteness;
	}

	/**
	 * 修改密码是否已经使用过
	 * 
	 * @param id
	 * @return
	 */
	public boolean updatePassUse(int id) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("bIsUse", "true");
		String[] whereColoum = new String[] { "" + id };
		// String sql = "update  hmiProtect set bIsuse = 'true' where id = 1";
		int i = db.updateByUserDef("hmiProtect", values, "id=?", whereColoum);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 切换当前语言，写入数据库
	 * 
	 * @param languageId
	 * @return
	 */
	public boolean updateCurrentLanguage(int languageId) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nLanguageIndex", languageId);
		// db.updateByType("systemProp", values, CASE_COLUMN_TYPE.QUERY_ALL,1);
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改系统参数集合
	 * 
	 * @param param
	 * @return
	 */
	public boolean updateSysParam(int param) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nSetBoolParam", param);
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 按钮修改屏保时间 写入数据库
	 * 
	 * @param time
	 * @return
	 */
	public boolean updateScreenSaverTime(int time) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nScreenTime", time);
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {
			// Log.d("update", "修改屏保时间成功");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改当前配方号 写入数据库
	 * 
	 * @param recipeGropId
	 * @param recipeId
	 * @return
	 */
	public boolean updateCurrentRecipe(CurrentRecipe currentRecipe) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nRecipeGroupId", currentRecipe.getCurrentGroupRecipeId());
		values.put("nRecipeIndex", currentRecipe.getCurrentRecipeId());
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改初始画面号
	 * 
	 * @param time
	 * @return
	 */
	public boolean updateInitScene(int sceneId) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nInitScreenId", sceneId);
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {
			Log.d("update", "修改初始画面号成功");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改屏保亮度
	 * 
	 * @param value
	 * @return
	 */
	public boolean updateBrightness(int value) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nBrightness", value);
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {

			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改待机方式
	 * 
	 * @param value
	 * @return
	 */
	public boolean updateSaveType(boolean value) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("bScreensaver", value);
		int i = db.updateByUserDef("systemProp", values, null, null);
		if (i > 0) {

			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改连接参数
	 * 
	 * @param value
	 * @return
	 */
	public boolean updateConnectParaToDataBase(short eConnectPort, int baud,
			int check, int stop, int dataLen) {
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("nBaudRate", baud);
		values.put("nDataBits", dataLen);
		values.put("nCheckType", check);
		values.put("nStopBit", (stop - 1));
		String[] whereStr = new String[] { eConnectPort + "" };
		int i = db.updateByUserDef("plcConnectProp", values,
				"eConnectPort = ?", whereStr);
		boolean b = false;
		if (i < 0) {
			b = false;
		} else {
			b = true;
		}
		return b;
	}
	private HMIMODEL getModel (String model){
		String temp = "";
		if(null != model && !"".equals(model)){
			if(model.indexOf("-")!=-1){
				String [] temps = model.split("-");
				temp = temps[0];
			}
		}
	    if(temp.equals("AK")){
	    	return HMIMODEL.AK;
	    }else if(temp.equals("AKMID")){
	    	return HMIMODEL.MID;
	    }else {
	    	return HMIMODEL.UNKNOWN;
	    }
	}
	
	/**
	 * 获取打印机型号
	 */
	private PRINT_MODEL getPrintModel(int id){
		PRINT_MODEL model=PRINT_MODEL.WHE19;
		if (id==1) {
			model=PRINT_MODEL.WHE19;
		}else if (id==2) {
			model=PRINT_MODEL.WHA5;
		}
		return model;
	}
}
