package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.MACRO_TYPE;

//宏管理器数据库通信接口
public class MacroManagerBiz extends DataBase{
	// 数据库句柄
	SKDataBaseInterface mDB = null;

	// 数据表查询语句
	String mMacroQueryStr  = new String("select * from macro where MacroType=?");
	String mMacroQueryStr2 = new String("select * from macro where MacroType=? or MacroType=?");
	
	/**
	 * 查询所有初始化宏指令的ID
	 * */
	public ArrayList<Short> selectInitMacroIDList(){
		
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("MacroManagerBiz", "slectInitMacroIDList: Get database failed!");
			return null;
		}

		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr,new String[] { Integer.toString(MACRO_TYPE.INIT)});		
		if(null == tmpCursor){
			Log.e("MacroManagerBiz", "slectInitMacroIDList: Get cursor failed!");
			return null;
		}
		
		ArrayList<Short> IDList =  new ArrayList<Short>();
		while (tmpCursor.moveToNext()) {
			short tmpID = tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID"));
			IDList.add(tmpID);
		}
		close(tmpCursor);
		return IDList;
	}
	
	/**
	 * 查询所有控件宏指令的ID
	 * */
	public ArrayList<Short> selectCompMacroIDList(){
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("MacroManagerBiz", "selectCompMacroIDList: Get database failed!");
			return null;
		}

		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr,new String[] { Integer.toString(MACRO_TYPE.COMP)});		
		if(null == tmpCursor){
			Log.e("MacroManagerBiz", "selectCompMacroIDList: Get cursor failed!");
			return null;
		}
		
		ArrayList<Short> IDList =  new ArrayList<Short>();
		while (tmpCursor.moveToNext()) {
			short tmpID = tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID"));
			IDList.add(tmpID);
		}
		close(tmpCursor);
		return IDList;	
	}
	
	/**
	 * 查询所有场景宏指令的ID
	 * */
	public ArrayList<Short> selectSceneMacroIDList(){
		int subtype = 0; //当先搜索的子类型
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("MacroManagerBiz", "selectSceneMacroIDList: Get database failed!");
			return null;
		}
			
		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr2,new String[] {Integer.toString(MACRO_TYPE.SLOOP),Integer.toString(MACRO_TYPE.SCTRLOOP)});		
		if(null == tmpCursor){
			Log.e("MacroManagerBiz", "selectSceneMacroIDList: Get cursor failed!");
			return null;

		}
		
		ArrayList<Short> IDList =  new ArrayList<Short>();
		
		//搜索宏指令ID
		while (tmpCursor.moveToNext()) {
			short tmpID = tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID"));
			IDList.add(tmpID);
		}
		close(tmpCursor);
		
		return IDList;
	}
	
	/**
	 * 查询所有全局宏指令的ID
	 * */
	public ArrayList<Short> selectGlobalMacroIDList(){
		int subtype = 0; //当先搜索的子类型
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("MacroManagerBiz", "selectGlobalMacroIDList: Get database failed!");
			return null;
		}
		
		Cursor tmpCursor = null;
		tmpCursor = mDB.getDatabaseBySql(mMacroQueryStr2,new String[] {Integer.toString(MACRO_TYPE.GLOOP),Integer.toString(MACRO_TYPE.GCTRLOOP)});		
		if(null == tmpCursor){
			Log.e("MacroManagerBiz", "selectGlobalMacroIDList: Get cursor failed!");
			return null;

		}
		
		ArrayList<Short> IDList =  new ArrayList<Short>();
		
		//搜索宏指令ID
		while (tmpCursor.moveToNext()) {
			short tmpID = tmpCursor.getShort(tmpCursor.getColumnIndex("MacroID"));
			IDList.add(tmpID);
		}
		close(tmpCursor);
		return IDList;
	}
	
	public HashMap<Integer, ArrayList<Short>> getSceneIdMap(){
		HashMap<Integer, ArrayList<Short>> map=new HashMap<Integer, ArrayList<Short>>();
		mDB = SkGlobalData.getProjectDatabase();
		if (null == mDB) {// 获得数据库失败
			Log.e("MacroManagerBiz", "selectGlobalMacroIDList: Get database failed!");
			return map;
		}
		Cursor cursor=mDB.getDatabaseBySql(mMacroQueryStr2,new String[] {Integer.toString(MACRO_TYPE.SLOOP),Integer.toString(MACRO_TYPE.SCTRLOOP)});
		if(cursor!=null){
			int nSceneId=-1;
			while (cursor.moveToNext()) {
				if(nSceneId!=cursor.getInt(cursor.getColumnIndex("SceneID"))){
					nSceneId=cursor.getInt(cursor.getColumnIndex("SceneID"));
					ArrayList<Short> list=new ArrayList<Short>();
					map.put(nSceneId, list);
				}
				map.get(nSceneId).add(cursor.getShort(cursor.getColumnIndex("MacroID")));
			}
		}
		
		return map;
	}
}
