package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.android.Samkoonhmi.model.SliderModel;
import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 进度条
 * 
 * @author Administrator
 * 
 */
public class SlideBiz extends DataBase {
	private SKDataBaseInterface db;

	public SlideBiz() {
		db = SkGlobalData.getProjectDatabase();
	}

	public ArrayList<SliderModel> selectSlide(int sceneId) {
		ArrayList<SliderModel> list = new ArrayList<SliderModel>();
		if(null ==  db)
		{
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql(
				"select * from sliding where nSceneId=?", new String[] { sceneId
						+ "" });
		if (null != cursor) {
			while (cursor.moveToNext()) {
				SliderModel info = new SliderModel();
				info.setbShowText(cursor.getString(cursor.getColumnIndex("bShowText")).equals("true")?true:false);
				DATA_TYPE data_type = IntToEnum.getDataType(cursor.getInt(cursor.getColumnIndex("eDataType")));
				info.setDataType(data_type);
				CALIBRATION_DIRECTION calibration = IntToEnum.getCalibrationDirection(cursor.getInt(cursor.getColumnIndex("nDirection")));
				info.setDirection(calibration);
				info.setFingerBackColor(cursor.getInt(cursor.getColumnIndex("nFingerBackColor")));
				info.setFingerLineColor(cursor.getInt(cursor.getColumnIndex("nFingerLineColor")));
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setMaxTrend(cursor.getDouble(cursor.getColumnIndex("nMaxTrend")));
				info.setmHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setMinTrend(cursor.getDouble(cursor.getColumnIndex("nMinTrend")));
				info.setmWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnCalibrationColor(cursor.getInt(cursor.getColumnIndex("nCalibrationColor")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setnDecimalCount(cursor.getInt(cursor.getColumnIndex("nDecimalCount")));
				
				info.setnMaxNumber(cursor.getInt(cursor.getColumnIndex("nMaxNumber")));
				info.setnMinNumber(cursor.getInt(cursor.getColumnIndex("nMinNumber")));
				CALIBRATION_DIRECTION position = IntToEnum.getCalibrationDirection(cursor.getInt(cursor.getColumnIndex("nPosition")));
				info.setnPosition(position);
				info.setnTextSize(cursor.getInt(cursor.getColumnIndex("nTextSize")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setRectColor(cursor.getInt(cursor.getColumnIndex("nRectColor")));
				info.setShowCalibration(cursor.getString(cursor.getColumnIndex("bShowCalibration")).equals("true") ? true : false);
				info.setSlideBarColor(cursor.getInt(cursor.getColumnIndex("nSlideBarColor")));
				info.setStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				info.setTrend(cursor.getString(cursor.getColumnIndex("bShowTrend")).equals("true") ? true: false);
				AddrProp writeAddress = AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nWirteAddress"))) ;
				info.setWriteKeyAdd(writeAddress);
				info.setnCalibrationMax(cursor.getDouble(cursor.getColumnIndex("nCalibrationMax")));
				info.setnCalibrationMin(cursor.getDouble(cursor.getColumnIndex("nCalibrationMin")));
				info.setnSlideHeight(cursor.getInt(cursor.getColumnIndex("nSlideHeight")));
				info.setnSlideWidth(cursor.getInt(cursor.getColumnIndex("nSlideWidth")));
				if(info.isTrend())
				{
					info.setMaxTrendAdd(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nMaxTrend"))));
					info.setMinTrendAdd(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nMinTrend"))));
				}
				info.setShowInfo(TouchShowInfoBiz.getShowInfoById(info.getId()));
				info.setTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getId()));
				list.add(info);
			}
			close(cursor);
		}
		return list;
	}

}
