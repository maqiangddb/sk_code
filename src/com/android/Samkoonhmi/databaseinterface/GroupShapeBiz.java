package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.android.Samkoonhmi.model.GroupShapeModel;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 组合图形
 * @author Administrator
 *
 */
public class GroupShapeBiz extends DataBase{
	private SKDataBaseInterface db = null;
	
	public GroupShapeBiz()
	{
		if(null == db)
		{
			 db = SkGlobalData.getProjectDatabase();
		}
	}
	public ArrayList<GroupShapeModel> getInfo(int sceneId)
	{
		ArrayList<GroupShapeModel> list = new ArrayList<GroupShapeModel>();
		if(null == db)
		{
			 db = SkGlobalData.getProjectDatabase();
		}
		Cursor cursor = db.getDatabaseBySql("select * from userGroup where nSceneId=?", new String []{sceneId+""});
		if(null != cursor)
		{
			while (cursor.moveToNext()) {
				GroupShapeModel info = new GroupShapeModel();
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnLp(cursor.getInt(cursor.getColumnIndex("nLp")));
				info.setnSceneId(cursor.getInt(cursor.getColumnIndex("nSceneId")));
				info.setnTp(cursor.getInt(cursor.getColumnIndex("nTp")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setsPath(cursor.getString(cursor.getColumnIndex("sPath")));
				list.add(info);
			}
			close(cursor);
		}
		return list;
	}
	
}
