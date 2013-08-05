package com.android.Samkoonhmi.databaseinterface;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.SceneMacroInfo;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.MACRO_TYPE;

/**
 * 场景宏指令数据库访问接口
 * */
public class SceneMacroBiz extends DataBase {
	// 数据库句柄
	SKDataBaseInterface mDB = null;

	// 数据表查询语句
	String mMacroQueryStr = new String(
			"select * from macro where MacroID=? and (MacroType=? or MacroType=?)");

	// 控制地址查询语句
	String mSearchAddrStr = new String("select * from addr where nAddrId=?");

	public SceneMacroInfo selectSceneMacro(short macroid) {

		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("SceneMacroBiz", "selectScenelMacro: Get database failed!");
			return null;
		}

		Cursor tmpCursor = null;
		
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr,
				new String[] { Integer.toString(macroid),Integer.toString(MACRO_TYPE.SLOOP),Integer.toString(MACRO_TYPE.SCTRLOOP)});
		if (null == tmpCursor) {// 获取游标失败
				Log.e("SceneMacroBiz", "selectScenelMacro: Get cursor failed!");
		}

		SceneMacroInfo dstInfo = null;

		while (tmpCursor.moveToNext()) {

			dstInfo = new SceneMacroInfo();

			dstInfo.setMacroID(tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID")));
			dstInfo.setMacroLibName(tmpCursor.getString(tmpCursor.getColumnIndex("MacroLibName")));
			dstInfo.setMacroName(tmpCursor.getString(tmpCursor.getColumnIndex("MacroName")));

			dstInfo.setTimeInterval(tmpCursor.getInt(tmpCursor.getColumnIndex("TimeInterval")));

			dstInfo.setSceneID(tmpCursor.getInt(tmpCursor.getColumnIndex("SceneID")));
			
			dstInfo.setRunCount(tmpCursor.getInt(tmpCursor.getColumnIndex("scriptCount")));
			
			dstInfo.setMacroType(tmpCursor.getShort(tmpCursor.getColumnIndex("MacroType")));
			
			dstInfo.setnSid(tmpCursor.getShort(tmpCursor.getColumnIndex("SceneID")));
			
			if (MACRO_TYPE.SCTRLOOP == dstInfo.getMacroType()) {// 当地址编号有效时,填充受控循环宏的字段
				int ctrl_addr = tmpCursor.getInt(tmpCursor.getColumnIndex("ControlAddr"));
				
				//dstInfo.setControlAddr(selectAddrProp(ctrl_addr));// 此时，宏类型自动变为受控宏指令
				dstInfo.setControlAddr(AddrPropBiz.selectById(ctrl_addr));// 此时，宏类型自动变为受控宏指令
				
				short tmp_addr_type = tmpCursor.getShort(tmpCursor.getColumnIndex("ControlAddrType"));

				if (0 == tmp_addr_type) {// 位地址类型
					dstInfo.setControlAddrType(ADDRTYPE.BITADDR);
					dstInfo.setExecCondition(tmpCursor.getShort(tmpCursor.getColumnIndex("ExecCondition")));

				} else if (1 == tmp_addr_type) {// 字地址类型
					dstInfo.setControlAddrType(ADDRTYPE.WORDADDR);
					dstInfo.setCmpFactor(tmpCursor.getShort(tmpCursor.getColumnIndex("nCmpFactor")));
				}// End of:if(0 == tmp_addr_type)

			}// End of:if(0 < ctrl_addr)
		}

		close(tmpCursor);

		if (null == dstInfo) {
			Log.e("SceneMacroBiz", "selectScenelMacro: dstInfo create failed!");
		}
		return dstInfo;
	}
}
