package com.android.Samkoonhmi.databaseinterface;

import android.database.Cursor;

import com.android.Samkoonhmi.model.TimeSettingInfo;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 时间同步设置数据库读取
 */
public class TimeSettingBiz {

	private SKDataBaseInterface db = null;
	
	public TimeSettingBiz(){
		db = SkGlobalData.getProjectDatabase();
	}
	
	public TimeSettingInfo getTimeSetting(){
		TimeSettingInfo info=null;
		if (db!=null) {
			String sql="select * from timesetting";
			Cursor cursor=db.getDatabaseBySql(sql, null);
			if (cursor!=null) {
				info=new TimeSettingInfo();
				while (cursor.moveToNext()) {
					String downLoadTime=cursor.getString(cursor.getColumnIndex("bDownloadTime"));
					boolean bDownloadTime=false;
					if (downLoadTime!=null) {
						bDownloadTime=downLoadTime.equals("true")? true : false;
					}
					info.setbDownloadTime(bDownloadTime);
					info.seteDataType(IntToEnum.
							getDataType(cursor.getInt(cursor.getColumnIndex("eDataType"))));
					info.setnLenth(cursor.getInt(cursor.getColumnIndex("nLength")));
					info.setmSynchAddr(AddrPropBiz.
							selectById(cursor.getInt(cursor.getColumnIndex("nSynchAddr"))));
					info.setnExeType(cursor.getShort(cursor.getColumnIndex("eExeType")));
					info.setnTime(cursor.getInt(cursor.getColumnIndex("nTime")));
					info.setmTriggerAddr(AddrPropBiz.
							selectById(cursor.getInt(cursor.getColumnIndex("nTriggerAddr"))));
					String autoReset=cursor.getString(cursor.getColumnIndex("bAutoReset"));
					boolean bAutoReset=false;
					if (autoReset!=null) {
						bAutoReset=autoReset.equals("true")? true : false;
					}
					info.setbAutoReset(bAutoReset);
					info.setnSynchTime(cursor.getShort(cursor.getColumnIndex("eSynchTime")));
					
				}
				cursor.close();
			}
		}
		return info;
	}
}
