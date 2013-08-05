package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.android.Samkoonhmi.model.DateTimeShowInfo;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skenum.WEEK_FORMAT;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 日期时间显示器
 * 
 * @author 瞿丽平
 * 
 */
public class TimeShowBiz extends DataBase {
	private SKDataBaseInterface db;

	public TimeShowBiz() {
		db = SkGlobalData.getProjectDatabase();
		
	}

	public ArrayList<DateTimeShowInfo> selectTimeShowInfo(int sceneId) {
		ArrayList<DateTimeShowInfo> list = new ArrayList<DateTimeShowInfo>();
		String id = "";
		boolean init = true;
	   if(null == db)
	   {
		   db=SkGlobalData.getProjectDatabase();
	   }
		Cursor cursor = db.getDatabaseBySql(
				"select * from dataShow where eItemType=3 and nSceneId=?", new String[] { sceneId
						+ "" });
		if (null != cursor) {
			while (cursor.moveToNext()) {
				DateTimeShowInfo info = new DateTimeShowInfo();
				info.seteFontCss(cursor.getShort(cursor
						.getColumnIndex("eFontCss")));
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnFontSize(cursor.getInt(cursor
						.getColumnIndex("nFontSize")));
				info.setsFontStyle(cursor.getString(cursor
						.getColumnIndex("sFontStyle")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setnShapId(cursor.getString(cursor
						.getColumnIndex("sShapId")));
				info.setnStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				info.setnStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				info.setnTextHeight(cursor.getInt(cursor
						.getColumnIndex("nTextHeight")));
				info.setnTextStartX(cursor.getInt(cursor
						.getColumnIndex("nTextStartX")));
				info.setnTextStartY(cursor.getInt(cursor
						.getColumnIndex("nTextStartY")));
				info.setnTextWidth(cursor.getInt(cursor
						.getColumnIndex("nTextWidth")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor
						.getColumnIndex("nCollidindId")));
				info.setnTransparent(cursor.getInt(cursor.getColumnIndex("nTransparent")));
				list.add(info);
				if (init) {
					id += " nItemId=" + info.getId();
					init = false;
				} else {
					id += " or nItemId=" + info.getId();
				}

			}
		}
		close(cursor);
		DateTimeShowInfo info = null;
		int nItemId = -1;
		cursor = db.getDatabaseBySql("select * from  time where " + id, null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getId() == nItemId) {
							info = list.get(i);
							break;
						}
					}

				}
				int dateShow = cursor
						.getInt(cursor.getColumnIndex("eShowDate"));
				if (-1 == dateShow) {
					info.seteShowDate(null);
				} else {
					DATE_FORMAT showDate = IntToEnum.getDateType(dateShow);
					info.seteShowDate(showDate);
				}

				int timeShow =cursor.getInt(cursor.getColumnIndex("eShowTime"));
				if (-1 == timeShow) {
					info.seteShowTime(null);
				} else {
					TIME_FORMAT showTime = IntToEnum.getTimeType(timeShow);
					info.seteShowTime(showTime);
				}
				int weekShow = cursor
						.getInt(cursor.getColumnIndex("eShowWeek"));
				if (-1 == weekShow) {
					info.seteShowWeek(null);
				} else {
					WEEK_FORMAT showWeek = IntToEnum.getWeekType(weekShow);
					info.seteShowWeek(showWeek);
				}
				info.setnFontColor(cursor.getInt(cursor
						.getColumnIndex("nFontColor")));
				info.setnBackground(cursor.getInt(cursor
						.getColumnIndex("nBackground")));

			}
		}
		close(cursor);
		return list;
	}

}
