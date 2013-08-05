package com.android.Samkoonhmi.databaseinterface;

import java.util.HashMap;

import android.database.Cursor;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 高级页面和显现页面查询
 * 
 * @author Administrator
 * 
 */
public class TouchShowInfoBiz extends DataBase {
	private static SKDataBaseInterface db;
	private static HashMap<Integer, TouchInfo> tMap=new HashMap<Integer, TouchInfo>();
	private static HashMap<Integer, ShowInfo> sMap=new HashMap<Integer, ShowInfo>();

	public TouchShowInfoBiz() {

	}
	
	/**
	 * 加载所有控件的触控和显现属性
	 */
	public static void loadTouchAndShow(){
		loadTouch();
		loadShow();
	}
	
	public static void destory(){
		if (tMap!=null) {
			tMap.clear();
		}
		if (sMap!=null) {
			sMap.clear();
		}
	}
	
	/**
	 * 加载触控属性
	 */
	private static void loadTouch(){
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		if (db==null) {
			return;
		}
		Cursor cursor = db.getDatabaseBySql("select * from touchProp ",null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				TouchInfo touchInfo = new TouchInfo();
				touchInfo.setItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				if (cursor.getString(cursor.getColumnIndex("bNoticAddr")) != null) {
					touchInfo
							.setbNoticAddr(cursor.getString(
									cursor.getColumnIndex("bNoticAddr"))
									.equals("true") ? true : false);
				}
				if (cursor.getString(cursor.getColumnIndex("bTimeoutCancel")) != null)
					touchInfo.setbTimeoutCancel(cursor.getString(
							cursor.getColumnIndex("bTimeoutCancel")).equals(
							"true") ? true : false);
				if (null != cursor.getString(cursor
						.getColumnIndex("bTouchByAddr"))) {
					touchInfo.setbTouchByAddr(cursor.getString(
							cursor.getColumnIndex("bTouchByAddr")).equals(
							"true") ? true : false);//
				}
				if (null != cursor.getString(cursor
						.getColumnIndex("bTouchByUser"))) {
					touchInfo.setbTouchByUser(cursor.getString(
							cursor.getColumnIndex("bTouchByUser")).equals(
							"true") ? true : false);//
				}
				int ctlAddrType = cursor.getInt(cursor
						.getColumnIndex("eCtlAddrType"));
				touchInfo.seteCtlAddrType(IntToEnum.getAddrType(ctlAddrType));
				int eDataType = cursor.getInt(cursor
						.getColumnIndex("eDataType"));
				touchInfo.seteDataType(IntToEnum.getDataType(eDataType));
				touchInfo.setnNoticAddrId(AddrPropBiz.selectById(cursor
						.getInt(cursor.getColumnIndex("nNoticeId"))));
				touchInfo.setnAddrId(cursor.getInt(cursor
						.getColumnIndex("nAddrId")));
				if (touchInfo.getnAddrId() != -1 && touchInfo.isbTouchByAddr()) {
					AddrProp touchAddrProp = AddrPropBiz.selectById(touchInfo
							.getnAddrId());
					touchInfo.setTouchAddrProp(touchAddrProp);
				}
				touchInfo.setnGroupValueF(cursor.getInt(cursor
						.getColumnIndex("nGroupValueF")));
				touchInfo.setnGroupValueL(cursor.getInt(cursor
						.getColumnIndex("nGroupValueL")));
				touchInfo.setnNoticValue(cursor.getDouble(cursor
						.getColumnIndex("nNoticValue")));
				touchInfo.setnPressTime(cursor.getShort(cursor
						.getColumnIndex("nPressTime")));
				touchInfo.setnValidStatus(cursor.getShort(cursor
						.getColumnIndex("nValidStatus")));
				touchInfo.setnWordPosition(cursor.getShort(cursor
						.getColumnIndex("nWordPosition")));
				tMap.put(touchInfo.getItemId(), touchInfo);
			}
			cursor.close();
		}
	}
	
	/**
	 * 加载显现属性
	 */
	private static void loadShow(){
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		
		if (db==null) {
			return;
		}
		
		Cursor cursor = db.getDatabaseBySql("select * from showProp ",null);

		if (null != cursor) {
			while (cursor.moveToNext()) {
				ShowInfo showInfo = new ShowInfo();
				showInfo.setItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				if (null != cursor.getString(cursor
						.getColumnIndex("bShowByAddr"))) {
					showInfo.setbShowByAddr(cursor.getString(
							cursor.getColumnIndex("bShowByAddr"))
							.equals("true") ? true : false);
				}
				if (null != cursor.getString(cursor
						.getColumnIndex("bShowByUser"))) {
					showInfo.setbShowByUser(cursor.getString(
							cursor.getColumnIndex("bShowByUser"))
							.equals("true") ? true : false);
				}
				int eAddrType = cursor.getInt(cursor
						.getColumnIndex("eAddrType"));
				showInfo.seteAddrType(IntToEnum.getAddrType(eAddrType));
				showInfo.setnAddrId(cursor.getInt(cursor
						.getColumnIndex("nAddrId")));
				showInfo.setnBitPosition(cursor.getShort(cursor
						.getColumnIndex("nBitPosition")));
				showInfo.setnGroupValueF(cursor.getInt(cursor
						.getColumnIndex("nGroupValueF")));
				if (-1 != showInfo.getnAddrId() && showInfo.isbShowByAddr()) {
					AddrProp showAddrProp = AddrPropBiz.selectById(showInfo
							.getnAddrId());
					showInfo.setShowAddrProp(showAddrProp);
				}
				showInfo.setnGroupValueL(cursor.getInt(cursor
						.getColumnIndex("nGroupValueL")));
				showInfo.setnValidStatus(cursor.getShort(cursor
						.getColumnIndex("nValidStatus")));
				sMap.put(showInfo.getItemId(), showInfo);

			}
			cursor.close();

		}
	}

	/**
	 * 通过触控Id 查找触控对象
	 * @param touchId
	 * @return
	 */
	public static TouchInfo getTouchInfoById(int itemId) {
		TouchInfo touchInfo = null;
		if (tMap.containsKey(itemId)) {
			touchInfo=tMap.get(itemId);
		}
		return touchInfo;
	}

	/**
	 * 通过Id查找显现对象
	 * @param showId
	 * @return
	 */
	public static ShowInfo getShowInfoById(int itemId) {
		ShowInfo showInfo = null;
		if (sMap.containsKey(itemId)) {
			showInfo=sMap.get(itemId);
		}
		return showInfo;
	}

}
