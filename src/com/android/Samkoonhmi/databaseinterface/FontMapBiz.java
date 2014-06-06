package com.android.Samkoonhmi.databaseinterface;

import android.database.Cursor;

import com.android.Samkoonhmi.model.NumberDisplayInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class FontMapBiz {
	private SKDataBaseInterface db;
	private NumberDisplayInfo info;

	public FontMapBiz() {
		db = SkGlobalData.getProjectDatabase();
	}

	public String findTtfName(String fontName) {
		if (fontName==null||fontName.equals("")) {
			return "";
		}
		String ttfName = "";
		if (null == db) {
			db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql("select * from fontmap where sFontType=? ",
				new String[] { fontName });
		if (null != cursor) {
			while (cursor.moveToNext()) {
				ttfName = cursor.getString(cursor.getColumnIndex("sFileName"));
			}
			cursor.close();
		}
		return ttfName;
	}
}
