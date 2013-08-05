package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.graphics.Point;

import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.SHAP_CLASS;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 多边形数据转换类
 * @author Administrator
 *
 */
public class ShapInfoBiz  extends DataBase{
	private SKDataBaseInterface db;
	private Cursor cursor;
	public ShapInfoBiz ()
	{
		db=SkGlobalData.getProjectDatabase();
		
	}
	public ArrayList<ShapInfo> getShapInfo(SHAP_CLASS  shapClass,int sid)
	{
		if(null == db)
		{
			db=SkGlobalData.getProjectDatabase();
		}
		ArrayList<ShapInfo> list=new ArrayList<ShapInfo>();
		String id = "";
		boolean init = true;
		int shapClassValue=IntToEnum.getShapTypeValue(shapClass);
		cursor=db.getDatabaseBySql("select * from polygon where ePolygonClass =? and nSceneId =?", new String []{shapClassValue+"",sid+""});
		if(null != cursor)
		{
			
			while(cursor.moveToNext())
			{
				ShapInfo info=new ShapInfo();
				END_POINT_TYPE eCornerType= IntToEnum.getEndPointType(cursor.getInt(cursor.getColumnIndex("eCornerType")));
				info.seteCornerType(eCornerType);
				LINE_TYPE eLineType=IntToEnum.getLineType(cursor.getInt(cursor.getColumnIndex("eLineType")));
				info.seteLineType(eLineType);
				info.setePolygonClass(shapClass);
				info.setId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				CSS_TYPE eStyle=IntToEnum.getCssType(cursor.getInt(cursor.getColumnIndex("eStyle")));
				info.seteStyle(eStyle);
//				info.setListPoint(listPoint);
				info.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
				info.setnBackColor(cursor.getInt(cursor.getColumnIndex("nBackColor")));
				info.setnForeColor(cursor.getInt(cursor.getColumnIndex("nForeColor")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setnLineColor(cursor.getInt(cursor.getColumnIndex("nLineColor")));
				info.setnLineWidth(cursor.getInt(cursor.getColumnIndex("nLineWidth")));
				info.setnPointX(cursor.getInt(cursor.getColumnIndex("nPointX")));
				info.setnPointY(cursor.getInt(cursor.getColumnIndex("nPointY")));
				info.setnRadius(cursor.getInt(cursor.getColumnIndex("nRadius")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setRoundRectRadiusY(cursor.getInt(cursor.getColumnIndex("eCornerType")));
				list.add(info);
				
				if(SHAP_CLASS.POLYGON==shapClass){
					if (init) {
						id += " nItemId=" + info.getId();
						init = false;
					} else {
						id += " or nItemId=" + info.getId();
					}
				}
			}
			close(cursor);
		}
		//如果是多边形 读取点集合
		if(SHAP_CLASS.POLYGON==shapClass)
		{
			if (id.length()>0) {
				// 查找属于这根线的普通点
				cursor = db.getDatabaseBySql("select * from point where "+id+"  and ePointType = 1", null);
				ShapInfo info=null;
				int nItemId=-1;
				
				if (null != cursor) {
					while (cursor.moveToNext()) {
						if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
							nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getId() == nItemId) {
									info = list.get(i);
									Vector<Point> listPoint=new Vector<Point>();
									info.setListPoint(listPoint);
									break;
								}
							}

						}
						int pointX = cursor.getInt(cursor.getColumnIndex("nPosX"));
						int pointY = cursor.getInt(cursor.getColumnIndex("nPosY"));
						Point point=new Point(pointX,pointY);
						info.getListPoint().add(point);
						
					}
					
				}
				close(cursor);
			}
			
		}
		return list;
	}

}
