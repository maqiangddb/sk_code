package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.Samkoonhmi.model.SceneItemPosInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class ScenePosInfoBiz extends DataBase{
	SKDataBaseInterface db = null;
	
	ScenePosInfoBiz(){
		db=SkGlobalData.getProjectDatabase(); 
	}
	
	/**
	 * 获取所有信息
	 * @return
	 */
	
	public ArrayList<SceneItemPosInfo>getALLInfo(){
		ArrayList<SceneItemPosInfo> list = new ArrayList<SceneItemPosInfo>();
		if (db != null){
			String sql ="select * from scenePosInfo order by nPageId asc";
			Cursor mCursor = db.getDatabaseBySql(sql, null);
			if (mCursor != null){
				
				while(mCursor.moveToNext()){
					SceneItemPosInfo info = new SceneItemPosInfo();
					info.mSceneId = mCursor.getInt(mCursor.getColumnIndex("nSceneId"));
					info.mSceneName = mCursor.getString(mCursor.getColumnIndex("nSceneName"));
					info.mScenePath= mCursor.getString(mCursor.getColumnIndex("nScenePic"));
					info.nPageId = mCursor.getInt(mCursor.getColumnIndex("nPageId"));
					info.nPagePos = mCursor.getInt(mCursor.getColumnIndex("nPagePos"));
					info.width = mCursor.getInt(mCursor.getColumnIndex("nItemWidth"));
					info.height = mCursor.getInt(mCursor.getColumnIndex("nItemHeigh"));
					info.nFontSize = mCursor.getInt(mCursor.getColumnIndex("nFontSize"));
			
					list.add(info);
				}
				
				close(mCursor);
			}
		}
		
		return list.size() > 0 ? list : null;
	}
	
	/**
	 * 插入一组信息
	 * @param list
	 */
	public void insertInfo(ArrayList<SceneItemPosInfo> list){
		
		if (db != null && list != null && list.size() > 0) {
			ContentValues contentValues = new ContentValues();
			for(SceneItemPosInfo info: list){
				//删除
				String sql ="delete from scenePosInfo where nSceneId = ?";
				db.m_databaseObj.execSQL(sql, new Object[]{info.mSceneId});
				
				contentValues.put("nSceneId", info.mSceneId);
				contentValues.put("nSceneName", info.mSceneName);
				contentValues.put("nScenePic", info.mScenePath);
				contentValues.put("nPageId", info.nPageId);
				contentValues.put("nPagePos", info.nPagePos);
				contentValues.put("nItemWidth", info.width);
				contentValues.put("nItemHeigh", info.height);
				contentValues.put("nFontSize", info.nFontSize);
				
				db.insertData("scenePosInfo", contentValues);
			}
		}
	}
	/**
	 * 插入一条信息
	 * @param info
	 */
	public void insertItemInfo(SceneItemPosInfo info){
		
		if (db != null && info != null){
			//删除
			String sql ="delete from scenePosInfo where nSceneId = ?";
			db.m_databaseObj.execSQL(sql, new Object[]{info.mSceneId});
			
			ContentValues contentValues = new ContentValues();
			contentValues.put("nSceneId", info.mSceneId);
			contentValues.put("nSceneName", info.mSceneName);
			contentValues.put("nScenePic", info.mScenePath);
			contentValues.put("nPageId", info.nPageId);
			contentValues.put("nPagePos", info.nPagePos);
			contentValues.put("nItemWidth", info.width);
			contentValues.put("nItemHeigh", info.height);
			contentValues.put("nFontSize", info.nFontSize);
			
			db.insertData("scenePosInfo", contentValues);
		}
	}
	
	/**
	 *  进行更新页面的位置信息
	 * @param info
	 */
	public void updateInfo(SceneItemPosInfo info){
		if (db != null && db.m_databaseObj != null && info != null) {
			
			String update =" update scenePosInfo set nPageId =%d,nPagePos = %d where nSceneId = %d" ;
			update = String.format(update, info.nPageId, info.nPagePos, info.mSceneId);
			
			db.m_databaseObj.execSQL(update);
		}
	}

}
