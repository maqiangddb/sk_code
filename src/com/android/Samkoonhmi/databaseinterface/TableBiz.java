package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import com.android.Samkoonhmi.model.TableModel;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

public class TableBiz extends DataBase{
	
	private SKDataBaseInterface db = null;

	public TableBiz() {
		db = SkGlobalData.getProjectDatabase();
	}
	public ArrayList<TableModel> selectTableById(int sid)
	{
		Cursor cursor = null;
		String id = "";
		boolean init = true;
		if(null == db)
		{
			db = SkGlobalData.getProjectDatabase();
		}
		ArrayList<TableModel> list=new ArrayList<TableModel>();
		cursor=db.getDatabaseBySql("select * from tableShow where nSceneId="+sid,null);
		if(null != cursor)
		{
			while (cursor.moveToNext()) {
				TableModel info=new TableModel();
				info.setbShowFrameLine(cursor.getString(cursor.getColumnIndex("bShowFrameLine")).equals("true")?true:false);
				info.setbShowOrientationLine(cursor.getString(cursor.getColumnIndex("bShowOrientationLine")).equals("true")?true:false);
				info.setbShowPortraitCount(cursor.getString(cursor.getColumnIndex("bShowPortraitCount")).equals("true")?true:false);
				LINE_TYPE lineType=IntToEnum.getLineType(cursor.getInt(cursor.getColumnIndex("eNLineType")));
				info.seteNLineType(lineType);
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnBackColor(cursor.getInt(cursor.getColumnIndex("nBackColor")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setnLeftTopX(cursor.getInt(cursor.getColumnIndex("nLeftTopX")));
				info.setnLeftTopY(cursor.getInt(cursor.getColumnIndex("nLeftTopY")));
				info.setnNLineWidth(2);
				info.setnNShowColor(cursor.getInt(cursor.getColumnIndex("nNShowColor")));
				info.setnOrientationCount(cursor.getInt(cursor.getColumnIndex("nOrientationCount")));
				info.setnPortraitCount(cursor.getInt(cursor.getColumnIndex("nPortraitCount")));
				info.setnTableHeight(cursor.getInt(cursor.getColumnIndex("nTableHeight")));
				info.setnTableWidth(cursor.getInt(cursor.getColumnIndex("nTableWidth")));
				info.setAlpha(cursor.getShort(cursor.getColumnIndex("nTransparent")));
				info.setnWLineWidth(1);
				info.setnWShowColor(cursor.getInt(cursor.getColumnIndex("nWShowColor")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				list.add(info);
				
				if (init) {
					id += " nItemId=" + info.getId();
					init = false;
				} else {
					id += " or nItemId=" + info.getId();
				}
			}
			close(cursor);
		}
		
		if(list.size()>0){
			TableModel info=null;
			int nItemId=-1;
			cursor=db.getDatabaseBySql("select * from tableProp where "+id, null);
			if(null != cursor)
			{
				double tableItemType;
				double itemWidth;
				
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == nItemId) {
								info = list.get(i);
								ArrayList<Double> nRows=new ArrayList<Double>();
								ArrayList<Double> nColums=new ArrayList<Double>();
								info.setnRows(nRows);
								info.setnColums(nColums);
								break;
							}
						}

					}
					tableItemType=cursor.getDouble(cursor.getColumnIndex("nIsRow"));
					itemWidth=cursor.getDouble(cursor.getColumnIndex("nWidth"));
					if(1==tableItemType){//行
						info.getnRows().add(itemWidth+0.02);
					}else if(0==tableItemType){//列
						info.getnColums().add(itemWidth+0.02);
					}
				}
				close(cursor);
			}
		}
		
		return list;
	}
	

}
