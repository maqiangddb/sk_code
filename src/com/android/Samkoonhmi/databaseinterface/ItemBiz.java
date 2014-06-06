package com.android.Samkoonhmi.databaseinterface;

import java.util.HashMap;
import android.database.Cursor;

import com.android.Samkoonhmi.model.ItemsInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;


/**
 * 获取控件和画面对应信息
 */
public class ItemBiz extends DataBase{

	private SKDataBaseInterface db = null;
	public ItemBiz(){
		db = SkGlobalData.getProjectDatabase();
	}
	
	public void select(HashMap<Integer, ItemsInfo> list){
		if (db!=null) {
			Cursor cursor = null;
			String sql = "select nSceneId,nItemId,nId from sceneAndItem   order by  nItemId asc ";
			cursor = db.getDatabaseBySql(sql,null);
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					ItemsInfo info=new ItemsInfo();
					info.nSid=cursor.getInt(0);
					info.nItemId=cursor.getInt(1);
					info.nId=cursor.getInt(2);
					list.put(info.nId, info);
				}
				close(cursor);
			}
		}
	}
}
