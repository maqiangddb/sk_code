package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;

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
	private static final String TAG="TouchShowInfoBiz";
	private static SKDataBaseInterface db;
	
	public TouchShowInfoBiz() {

	}
	
	private static TouchInfo loadTouch(int id){
		
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		if (db==null) {
			return null;
		}

		TouchInfo touchInfo=null;
		Cursor cursor = db.getDatabaseBySql("select * from touchProp where nItemId = "+id,null);
		if (null != cursor) {
			if (cursor.moveToNext()) {
				touchInfo = new TouchInfo();
				//nItemId
				touchInfo.setItemId(cursor.getInt(1));
				//bTouchByAddr
				String sTouchByAddr=cursor.getString(2);
				if (null != sTouchByAddr) {
					touchInfo.setbTouchByAddr(sTouchByAddr.equals("true") ? true : false);
				}
				//eCtlAddrType
				int ctlAddrType = cursor.getInt(3);
				touchInfo.seteCtlAddrType(IntToEnum.getAddrType(ctlAddrType));
				//nValidStatus
				touchInfo.setnValidStatus(cursor.getShort(4));
				//nAddrId
				touchInfo.setnAddrId(cursor.getInt(5));
				if (touchInfo.getnAddrId() != -1 && touchInfo.isbTouchByAddr()) {
					AddrProp touchAddrProp = AddrPropBiz.selectById(touchInfo.getnAddrId());
					touchInfo.setTouchAddrProp(touchAddrProp);
				}
				//nWordPosition
				touchInfo.setnWordPosition(cursor.getShort(6));
				//bTouchByUser
				String sTouchByUser=cursor.getString(7);
				if (null !=sTouchByUser ) {
					touchInfo.setbTouchByUser(sTouchByUser.equals("true") ? true : false);//
				}
				//nGroupValueF
				touchInfo.setnGroupValueF(cursor.getInt(8));
				//nGroupValueL
				touchInfo.setnGroupValueL(cursor.getInt(9));
				//nPressTime
				touchInfo.setnPressTime(cursor.getShort(10));
				//bTimeoutCancel
				String sTimeoutCancel=cursor.getString(11);
				if (sTimeoutCancel != null){
					touchInfo.setbTimeoutCancel(sTimeoutCancel.equals("true") ? true : false);
				}
				//bNoticAddr
				String sNoticAddr=cursor.getString(12);
				if (sNoticAddr != null) {
					touchInfo.setbNoticAddr(sNoticAddr.equals("true") ? true : false);
				}
				//eDataType
				int eDataType = cursor.getInt(13);
				touchInfo.seteDataType(IntToEnum.getDataType(eDataType));
				//nNoticeId
				touchInfo.setnNoticAddrId(AddrPropBiz.selectById(cursor.getInt(14)));
				//nNoticValue
				touchInfo.setnNoticValue(cursor.getDouble(15));
			}
			cursor.close();
		}
		
		return touchInfo;
	}
	
	private static ShowInfo loadShow(int id){
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		
		if (db==null) {
			return null;
		}
		
		ShowInfo showInfo=null;
		Cursor cursor = db.getDatabaseBySql("select * from showProp where nItemId  = "+id,null);

		if (null != cursor) {
			if(cursor.moveToNext()) {
				showInfo = new ShowInfo();
				//nItemId
				showInfo.setItemId(cursor.getInt(1));
				//bShowByAddr
				String sShowByAddr=cursor.getString(2);
				if (null !=sShowByAddr ) {
					showInfo.setbShowByAddr(sShowByAddr.equals("true") ? true : false);
				}
				//eAddrType
				int eAddrType = cursor.getInt(3);
				showInfo.seteAddrType(IntToEnum.getAddrType(eAddrType));
				//nValidStatus
				showInfo.setnValidStatus(cursor.getShort(4));
				//nAddrId
				showInfo.setnAddrId(cursor.getInt(5));
				if (-1 != showInfo.getnAddrId() && showInfo.isbShowByAddr()) {
					AddrProp showAddrProp = AddrPropBiz.selectById(showInfo.getnAddrId());
					showInfo.setShowAddrProp(showAddrProp);
				}
				//nBitPosition
				showInfo.setnBitPosition(cursor.getShort(6));
				//bShowByUser
				String sShowByUser=cursor.getString(7);
				if (null !=sShowByUser ) {
					showInfo.setbShowByUser(sShowByUser.equals("true") ? true : false);
				}
				//nGroupValueF
				showInfo.setnGroupValueF(cursor.getInt(8));
				//nGroupValueL
				showInfo.setnGroupValueL(cursor.getInt(9));

			}
			cursor.close();
		}
		
		return showInfo;
	}

	/**
	 * 通过触控Id 查找触控对象
	 * @param touchId
	 * @return
	 */
	public static TouchInfo getTouchInfoById(int itemId) {
		if (itemId<=0) {
			return null;
		}
		
		return loadTouch(itemId);
	}

	/**
	 * 通过Id查找显现对象
	 * @param showId
	 * @return
	 */
	public static ShowInfo getShowInfoById(int itemId) {
		if (itemId<=0) {
			return null;
		}
		return loadShow(itemId);
	}
	
	/**
	 * 触控分段存储
	 * 根据不同需要
	 * 分段存储，加快查询速度
	 * 30个为一段
	 */
	public class TouchTableInfo {

		//用于区分段之间的表示值
		public int nV;
		//存储某一段触控对象
		public ArrayList<TouchInfo> mList;
	}

	/**
	 * 触控分段存储
	 * 根据不同需要
	 * 分段存储，加快查询速度
	 * 30个为一段
	 */
	public class ShowTableInfo {

		//用于区分段之间的表示值
		public int nV;
		//存储某一段显现对象
		public ArrayList<ShowInfo> mList;
	}

}
