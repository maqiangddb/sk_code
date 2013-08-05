package com.android.Samkoonhmi.databaseinterface;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.InitMacroInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.MACRO_TYPE;


/**
 * 初始化宏指令数据库接口
 * */
public class InitMacroBiz extends DataBase  {
	
	// 数据库句柄
	SKDataBaseInterface mDB = null;
	
	// 数据表查询语句
	String mMacroQueryStr = new String("select * from macro where MacroID=? and MacroType=?");
	
	public InitMacroBiz(){
		//Do nothing
	}
	
	public InitMacroInfo selectInitMacro(short macroid){
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("InitMacroBiz", "selectInitMacro: Get database failed!");
			return null;
		}
		
		Cursor tmpCursor = null;
	
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr,new String[] { Integer.toString(macroid),Integer.toString(MACRO_TYPE.INIT)});
		if (null == tmpCursor) {// 获取游标失败
			Log.e("InitMacroBiz", "selectInitMacro: Get cursor failed!");
			return null;
		}
		
		InitMacroInfo dstInfo = null;
		
		while (tmpCursor.moveToNext()) {
			dstInfo = new InitMacroInfo();
			dstInfo.setMacroID(tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID")));
			dstInfo.setMacroLibName(tmpCursor.getString(tmpCursor.getColumnIndex("MacroLibName")));
			dstInfo.setMacroName(tmpCursor.getString(tmpCursor.getColumnIndex("MacroName")));
			dstInfo.setnSid(tmpCursor.getShort(tmpCursor.getColumnIndex("SceneID")));
		}
		close(tmpCursor);
		
		if(null == dstInfo){
			Log.e("InitMacroBiz", "selectInitMacro: dstInfo create failed!");
		}
		
		return dstInfo;
	}
}
