package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import com.android.Samkoonhmi.model.CalibrationModel;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

import android.database.Cursor;

public class CalibrationBiz extends DataBase {
	SKDataBaseInterface db=null;
	Cursor cursor=null;
	
	public CalibrationBiz(){
		db=SkGlobalData.getProjectDatabase();
	}
	
	public ArrayList<CalibrationModel> getCalibration(int sceneId){
		ArrayList<CalibrationModel> list = new ArrayList<CalibrationModel>();
		if(null == db)
		{
			db=SkGlobalData.getProjectDatabase();
		}
		cursor = db.getDatabaseBySql("select * from calibration where nSceneId=? ", new String[]{sceneId+""});
		if(null!=cursor){
			while(cursor.moveToNext()){
				CalibrationModel info=new CalibrationModel();
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				CALIBRATION_DIRECTION calibrationDirection=IntToEnum.getCalibrationDirection(cursor.getInt(cursor.getColumnIndex("nCalibrationDirection")));
				info.setCalibrationDirection(calibrationDirection);
				info.setCalibrationHeight(cursor.getInt(cursor.getColumnIndex("nCalibrationHeight")));
				info.setCalibrationWidth(cursor.getInt(cursor.getColumnIndex("nCalibrationWidth")));
				info.setDecimalCount(cursor.getInt(cursor.getColumnIndex("nDecimalCount")));
				info.setLineColor(cursor.getInt(cursor.getColumnIndex("nLineColor")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				CALIBRATION_DIRECTION numberIncreaseDirection=IntToEnum.getCalibrationDirection(cursor.getInt(cursor.getColumnIndex("nNumberIncreaseDirection")));
				info.setNumberIncreaseDirection(numberIncreaseDirection);
				double i = cursor.getDouble(cursor.getColumnIndex("nMaxNumber"));
				double j = cursor.getDouble(cursor.getColumnIndex("nMinNumber"));
				if(i < j)
				{
				info.setMaxNumber(j);
				info.setMinNumber(i);
				}else{
					info.setMaxNumber(i);
					info.setMinNumber(j);
				}
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setTextSize(cursor.getInt(cursor.getColumnIndex("nTextSize")));
				info.setTextColor(cursor.getInt(cursor.getColumnIndex("nTextColor")));
				info.setStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				info.setShowText(cursor.getString(cursor.getColumnIndex("bShowText")).equals("true")?true:false);
			//	info.setShowLateral(cursor.getString(cursor.getColumnIndex("bShowLeteral")).equals("true")?true:false);
				info.setMainNumberCount(cursor.getInt(cursor.getColumnIndex("nMainNumberCount")));
				info.setNextNumberCount(cursor.getInt(cursor.getColumnIndex("nNextNumberCount")));
				ShowInfo showInfo = TouchShowInfoBiz.getShowInfoById(info.getId());
				info.setShowInfo(showInfo);
				list.add(info);
			}
		}
		close(cursor);
		return list;
	}
}
