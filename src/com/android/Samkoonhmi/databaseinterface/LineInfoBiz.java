package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.Vector;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PointF;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_CLASS;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.POINT_TYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 线的业务转换类
 * 
 * @author 瞿丽平
 * 
 */
public class LineInfoBiz extends DataBase {
	private SKDataBaseInterface db = null;

	public LineInfoBiz() {
		db = SkGlobalData.getProjectDatabase();
	}

	public ArrayList<LineInfo> select(LINE_CLASS lineClass, int sid) {
		
		ArrayList<LineInfo> list=new ArrayList<LineInfo>();
		Cursor cursor = null;
        if(null == db)
        {
        	db = SkGlobalData.getProjectDatabase();
        }
        String id = "";
		boolean init = true;
        int line_Class = IntToEnum.convertLineClass(lineClass);
		cursor = db.getDatabaseBySql(
				"select * from line where eLineClass =? and nSceneId=? ",
				new String[] { line_Class + "", sid + "" });
		if (null != cursor) {
			while (cursor.moveToNext()) {
				LineInfo line = new LineInfo();
				int arrow = cursor.getInt(cursor.getColumnIndex("eLineArrow"));
				END_ARROW_TYPE arrowType = IntToEnum.convertEndArrowType(arrow);
				line.seteLineArrow(arrowType);
				line.seteLineClass(lineClass);
				LINE_TYPE lineType = IntToEnum.getLineType(cursor.getInt(cursor
						.getColumnIndex("eLineType")));
				line.seteLineType(lineType);
				line.setId(cursor.getInt(cursor.getColumnIndex("nItemNumber")));
				line.setnAlpha(cursor.getInt(cursor.getColumnIndex("nAlpha")));
				line.setnLineColor(cursor.getInt(cursor
						.getColumnIndex("nLineColor")));
				line.setnLineWidth(cursor.getInt(cursor
						.getColumnIndex("nLineWidth")));
				line.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				line.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				END_POINT_TYPE endPointType=IntToEnum.getEndPointType(cursor.getInt(cursor.getColumnIndex("eLinePointType")));
				line.setEndPointType(endPointType);
				line.setnStartX(cursor.getInt(cursor.getColumnIndex("nStartX")));
				line.setnStartY(cursor.getInt(cursor.getColumnIndex("nStartY")));
				line.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				line.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				list.add(line);
				
				if (init) {
					id += " nItemId=" + line.getId();
					init = false;
				} else {
					id += " or nItemId=" + line.getId();
				}
			}
			close(cursor);
		}
		
		
		// 查找属于这根线的普通点
		if (list.size()>0) {
			cursor = db.getDatabaseBySql("select * from point where "+id+" order by nItemId, nOrder asc",null);
			LineInfo line=null;
			int nItemId=-1;
			if (null != cursor) {
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == nItemId) {
								line = list.get(i);
								Vector<Point> listPoint = new Vector<Point>();
								Vector<PointF> listfPoint = new Vector<PointF>();
								Vector<POINT_TYPE> pointTypeList = new Vector<POINT_TYPE>();
								line.setPointTypeList(pointTypeList);
								line.setPointList(listPoint);
								line.setfPointList(listfPoint);
								break;
							}
						}

					}
					int pointX = cursor.getInt(cursor.getColumnIndex("nPosX"));
					int pointY = cursor.getInt(cursor.getColumnIndex("nPosY"));
					int pointType=cursor.getInt(cursor.getColumnIndex("ePointType"));
					POINT_TYPE ePointType=IntToEnum.getPointType(pointType);
					line.getPointTypeList().add(ePointType);
					Point points = new Point(pointX, pointY);
					PointF pointF = new PointF(pointX,pointY);
					line.getPointList().add(points);
					line.getfPointList().add(pointF);

				}
			}
			close(cursor);
		}
		
		return list;
	}

}
