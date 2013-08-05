package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.MParamInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 宏指令参数数据库访问接口
 * */
public class MParamBiz extends DataBase {

	// 数据库句柄
	SKDataBaseInterface mDB = null;

	// 数据表查询语句
	String mQueryStr = new String(
			"select * from macroProp where sMacroLibName=?");

	// 控制地址查询语句
	String mSearchAddrStr = new String("select * from addr where nAddrId=?");

	public MParamBiz() {
		// Do nothing
	}

	public ArrayList<MParamInfo> selectMacroParamList(String mlibname) {

		if (null == mlibname) {
			Log.e("MParamBiz", "selectMacroParam: macro library name is null!");
			return null;
		}

		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("MParamBiz", "selectMacroParam: Get database failed!");
			return null;
		}

		Cursor tmpCursor = null;

		tmpCursor = mDB.getDatabaseBySql(mQueryStr, new String[] { mlibname });
		if (null == tmpCursor) {// 获取游标失败
			Log.e("MParamBiz", "selectMacroParam: Get cursor failed!");
			return null;
		}

		ArrayList<MParamInfo> dstList = new ArrayList<MParamInfo>();

		AddrProp mTmpAddr = null;

		while (tmpCursor.moveToNext()) {
			MParamInfo tmpMPI = new MParamInfo();
			tmpMPI.setName(tmpCursor.getString(tmpCursor
					.getColumnIndex("sName")));
			tmpMPI.setMacroLibName(tmpCursor.getString(tmpCursor
					.getColumnIndex("sMacroLibName")));
			tmpMPI.setDataType(tmpCursor.getShort(tmpCursor
					.getColumnIndex("nDatatype")));
			tmpMPI.setCodeType(tmpCursor.getShort(tmpCursor
					.getColumnIndex("nCodetype")));
			tmpMPI.setRWPerm(tmpCursor.getShort(tmpCursor
					.getColumnIndex("nRWPerm")));
			int tmpAddrValue = tmpCursor.getInt(tmpCursor
					.getColumnIndex("nAddrValue"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(
					tmpAddrValue);
			tmpMPI.setAddrProp(mTmpAddr);
			if (null != tmpCursor
					.getString(tmpCursor.getColumnIndex("bOffset"))) {
				boolean boo = tmpCursor.getString(
						tmpCursor.getColumnIndex("bOffset")).equals("true") ? true
						: false;
				tmpMPI.setbOffset(boo);
				if (boo) {
					tmpMPI.setnOffsetAddr(SkGlobalData.getProjectDatabase()
							.getAddrById(
									tmpCursor.getInt(tmpCursor
											.getColumnIndex("nOffsetAddr"))));
				}
			}

			dstList.add(tmpMPI);
			mTmpAddr = null;
		}
		close(tmpCursor);

		return dstList;
	}
}// End of class

