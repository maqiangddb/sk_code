package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import com.android.Samkoonhmi.model.CommonGraphInfo;
import com.android.Samkoonhmi.model.GraphBaseInfo;
import com.android.Samkoonhmi.skenum.Direction;
import com.android.Samkoonhmi.skenum.Graph;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * 图表
 */
public class GraphBiz extends DataBase{

	private SKDataBaseInterface db = null;
	
	public ArrayList<GraphBaseInfo> select(int sid){
		
		ArrayList<GraphBaseInfo> list=new ArrayList<GraphBaseInfo>();
		
		Graph.GRAPH_TYPE type=Graph.GRAPH_TYPE.COMMON;
		String sql = "select * from graphShow where nSceneId="+sid;
		Cursor cursor = null;
		String common="",meter="";
		boolean [] init=new boolean[]{true,true};
		db = SkGlobalData.getProjectDatabase();
		int nItemId=0;
		
		if (db!=null) {
			cursor = db.getDatabaseBySql(sql, null);
			if (cursor!=null) {
				while (cursor.moveToNext()) {
				    GraphBaseInfo info=null;
				    type=Graph.getGraphType(
							cursor.getInt(cursor.getColumnIndex("eGraphType")));
				    nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					switch (type) {
					case COMMON:
						info=new CommonGraphInfo();
						if(init[0]){
							common+=" nItemId="+nItemId;
							init[0]=false;
						}else {
							common+=" or nItemId="+nItemId;
						}
						break;
					case STATISTICS:
						break;
					case METER:
						info=new GraphBaseInfo();
						if(init[1]){
							meter+=" nItemId="+nItemId;
							init[1]=false;
						}else {
							meter+=" or nItemId="+nItemId;
						}
						break;
					}
					info.setnItemId(nItemId);
					info.seteGraphType(type);
					info.setnShapeId(cursor.getInt(cursor.getColumnIndex("eShapeId")));
					info.setsPic(cursor.getString(cursor.getColumnIndex("sPic")));
					info.setmAddress(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("mAddress"))));
					info.seteDataType(IntToEnum.getDataType(
							cursor.getShort(cursor.getColumnIndex("eDataType"))));
					info.seteDirection(Direction.getDirection(
							cursor.getShort(cursor.getColumnIndex("eDirection"))));
					info.setnTextColor(cursor.getInt(cursor.getColumnIndex("nTextColor")));
					info.setnBackColor(cursor.getInt(cursor.getColumnIndex("nBackcolor")));
					info.setnLeftTopX(cursor.getShort(cursor.getColumnIndex("nLeftTopX")));
					info.setnLeftTopY(cursor.getShort(cursor.getColumnIndex("nLeftTopY")));
					info.setnWidth(cursor.getShort(cursor.getColumnIndex("nWidth")));
					info.setnHeigth(cursor.getShort(cursor.getColumnIndex("nHeight")));
					info.setnMainRuling(cursor.getShort(cursor.getColumnIndex("nMainRuling")));
					
					boolean b=false;
					String ruling=cursor.getString(cursor.getColumnIndex("bShowRuling"));
					if (ruling!=null) {
						b=ruling.equals("true")?true:false;
					}
					info.setbShowRuling(b);
					info.setnRuling((cursor.getShort(cursor.getColumnIndex("nRuling"))));
					
					boolean bbb=false;
					String value=cursor.getString(cursor.getColumnIndex("bShowRuleValue"));
					if (value!=null) {
						bbb=value.equals("true")?true:false;
					}
					info.setbShowRuleValue(bbb);
					info.setnRulingColor(cursor.getInt(cursor.getColumnIndex("nRulingColor")));
					info.setnPointerType(cursor.getShort(cursor.getColumnIndex("nPointType")));
					info.setnShowLeftTopX(cursor.getShort(cursor.getColumnIndex("nShowLeftTopX")));
					info.setnShowLeftTopY(cursor.getShort(cursor.getColumnIndex("nShowLeftTopY")));
					info.setnShowWidth(cursor.getShort(cursor.getColumnIndex("nShowWidth")));
					info.setnShowHigth(cursor.getShort(cursor.getColumnIndex("nShowHigth")));
					info.setnRulerLeftTopX(cursor.getShort(cursor.getColumnIndex("nRulerLeftTopX")));
					info.setnRulerLeftTopY(cursor.getShort(cursor.getColumnIndex("nRulerLeftTopY")));
					info.setnRulerWidth(cursor.getShort(cursor.getColumnIndex("nRulerWidth")));
					info.setnRulerHigth(cursor.getShort(cursor.getColumnIndex("nRulerHigth")));
					info.seteRulerDirection(Direction.getDirection(
							cursor.getShort(cursor.getColumnIndex("eRulerDirectio"))));
				
					boolean bb=false;
					String alarm=cursor.getString(cursor.getColumnIndex("bAlarm"));
					if (alarm!=null) {
						bb=alarm.equals("true")?true:false;
					}
					info.setbAlarm(bb);
					info.setnType(cursor.getShort(cursor.getColumnIndex("nType")));
					info.setnMin(cursor.getShort(cursor.getColumnIndex("nMin")));
					info.setnMax(cursor.getShort(cursor.getColumnIndex("nMax")));
					info.setnAlarmTextColor(cursor.getInt(cursor.getColumnIndex("nAlarmTextColor")));
					info.setnDesignColor(cursor.getInt(cursor.getColumnIndex("nDesignColor")));
					info.setnZvalue(cursor.getShort(cursor.getColumnIndex("nZvalue")));
					info.setnColidindId(cursor.getShort(cursor.getColumnIndex("nCollidindId")));
					info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(nItemId));
					if (info.getnType()==1) {
						info.setmAlarmMinAddr(AddrPropBiz.selectById((int)info.getnMin()));
						info.setmAlarmMaxAddr(AddrPropBiz.selectById((int)info.getnMax()));
					}
					list.add(info);
					
				}
				close(cursor);
			}
			
			if (list.size()>0) {
				if (common.length()>0) {
					selectCommon(list,common);
				}
				if (meter.length()>0) {
					selectMeter(list, meter);
				}
			}
		}
		return list;
	}
    
	/**
	 * 普通图表
	 */
	public void selectCommon(ArrayList<GraphBaseInfo> list,String id){
		
		int nItemId=-1;
		CommonGraphInfo info=null;
		Cursor cursor = null;
		String sql = "select * from commonGraph where "+id;
		cursor = db.getDatabaseBySql(sql, null);
		
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()==nItemId) {
							info=(CommonGraphInfo)list.get(i);
							break;
						}
					}
					
				}
				if (info!=null) {
					info.setnSourceRang(cursor.getShort(cursor.getColumnIndex("nSourceRang")));
					info.setnBitLength(cursor.getShort(cursor.getColumnIndex("nBitLength")));
					boolean b=cursor.getString(cursor.getColumnIndex("bSourceMark")).equals("true")?true:false;
					info.setbSourceMark(b);
					info.setnSourceMin(cursor.getDouble(cursor.getColumnIndex("eSourceMin")));
					info.setnSourceMax(cursor.getDouble(cursor.getColumnIndex("eSourceMax")));
					boolean bl=cursor.getString(cursor.getColumnIndex("bShowMark")).equals("true")?true:false;
					info.setbShowMark(bl);
					info.setnShowMin(cursor.getDouble(cursor.getColumnIndex("eShowMin")));
					info.setnShowMax(cursor.getDouble(cursor.getColumnIndex("eShowMax")));
					info.seteShapeType(Graph.getShapeType(
							cursor.getShort(cursor.getColumnIndex("eShapeType"))));
					boolean bb=cursor.getString(cursor.getColumnIndex("bFill")).equals("true")?true:false;
					info.setbFill(bb);
					boolean bbb=cursor.getString(cursor.getColumnIndex("bHole")).equals("true")?true:false;
					info.setbHole(bbb);
					info.setnRadius(cursor.getShort(cursor.getColumnIndex("nRadius")));
					float temp=(float)info.getnRadius()/10*info.getnShowWidth()/2;
					info.setnRadius((short)temp);
					boolean bbbb=cursor.getString(cursor.getColumnIndex("bStart")).equals("true")?true:false;
					info.setbStart(bbbb);
					info.setnDesign(cursor.getShort(cursor.getColumnIndex("nDesign")));
					info.setnDesignColor(cursor.getInt(cursor.getColumnIndex("nDesignColor")));
					info.setnFrameColor(cursor.getInt(cursor.getColumnIndex("nFrameColor")));
					if (info.getnSourceRang()==1) {
						info.setmMinAddrProp(AddrPropBiz.selectById((int)info.getnSourceMin()));
						info.setmMaxAddrProp(AddrPropBiz.selectById((int)info.getnSourceMax()));
					}
					info.setStartAngle(cursor.getShort(cursor.getColumnIndex("nStartAngle")));
					info.setSpanAngle(cursor.getShort(cursor.getColumnIndex("nSpanAngle")));
					info.setnAlpha(cursor.getShort(cursor.getColumnIndex("nTransparent")));
					bb = cursor.getString(cursor.getColumnIndex("bShowFrame")).equals("true")?true:false;
					info.setShowFrame(bb);
				}
			}
			close(cursor);
		}
	}
	
	/**
	 * 仪表
	 */
	public void selectMeter(ArrayList<GraphBaseInfo> list,String id){
		int nItemId=-1;
		GraphBaseInfo info=null;
		Cursor cursor = null;
		String sql = "select * from meterGraph where "+id;
		cursor = db.getDatabaseBySql(sql,null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (nItemId!=cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()==nItemId) {
							info=(GraphBaseInfo)list.get(i);
							break;
						}
					}
					
				}
				if (info!=null) {
					info.setnSourceRang(cursor.getShort(cursor.getColumnIndex("nSourceRang")));
					info.setnBitLength(cursor.getShort(cursor.getColumnIndex("nBitLength")));
					info.setnSourceMin(cursor.getDouble(cursor.getColumnIndex("eSourceMin")));
					info.setnSourceMax(cursor.getDouble(cursor.getColumnIndex("eSourceMax")));
					boolean b=cursor.getString(cursor.getColumnIndex("bShowMark")).equals("true")?true:false;
					info.setbShowMark(b);
					info.setnShowMin(cursor.getDouble(cursor.getColumnIndex("eShowMin")));
					info.setnShowMax(cursor.getDouble(cursor.getColumnIndex("eShowMax")));
					if (info.getnSourceRang()==1) {
						info.setmMinAddrProp(AddrPropBiz.selectById((int)info.getnSourceMin()));
						info.setmMaxAddrProp(AddrPropBiz.selectById((int)info.getnSourceMax()));
					}
					info.setnAlpha(cursor.getShort(cursor.getColumnIndex("nTransparent")));
					boolean bb = cursor.getString(cursor.getColumnIndex("bShowFrame")).equals("true")?true:false;
					info.setShowFrame(bb);
				}
			}
			close(cursor);
		}
	}
}
