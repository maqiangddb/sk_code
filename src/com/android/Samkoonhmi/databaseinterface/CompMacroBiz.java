package com.android.Samkoonhmi.databaseinterface;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.CompMacroInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.MACRO_TYPE;

/**
 * 控件宏指令数据库访问接口
 * */
public class CompMacroBiz extends DataBase {
	// 数据库句柄
	SKDataBaseInterface mDB = null;
	
	// 数据表查询语句
	String mMacroQueryStr = new String("select * from macro where MacroID=? and MacroType=?");
	
	public CompMacroBiz(){
		//Do nothing
	}
	
	public CompMacroInfo selectCompMacro(short macroid){
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("CompMacroBiz", "selectCompMacro: Get database failed!");
			return null;
		}
		
		Cursor tmpCursor = null;
	
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr,new String[] { Integer.toString(macroid),Integer.toString(MACRO_TYPE.COMP)});
		if (null == tmpCursor) {// 获取游标失败
			Log.e("CompMacroBiz", "selectCompMacro: Get cursor failed!");
			return null;
		}
		
		CompMacroInfo dstInfo = null;
		
		while (tmpCursor.moveToNext()) {
			dstInfo = new CompMacroInfo();
			dstInfo.setMacroID(tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID")));
			dstInfo.setMacroLibName(tmpCursor.getString(tmpCursor.getColumnIndex("MacroLibName")));
			dstInfo.setMacroName(tmpCursor.getString(tmpCursor.getColumnIndex("MacroName")));
			dstInfo.setCompID(tmpCursor.getInt(tmpCursor.getColumnIndex("nCompID")));
			dstInfo.setnSid(tmpCursor.getInt(tmpCursor.getColumnIndex("SceneID")));
		}
		close(tmpCursor);
		if(null == dstInfo){
			Log.e("CompMacroBiz", "selectCompMacro: dstInfo create failed, macro id : " + macroid);
		}	
		return dstInfo;
	}
	}