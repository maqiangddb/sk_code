package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;
import com.android.Samkoonhmi.model.XYCurveInfo;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.HISTORYSHOW_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;

/**
 * XY曲线数据库查询
 */
public class XYCurveBiz {

	private SKDataBaseInterface db = null;
	
	public ArrayList<XYCurveInfo> select(int sid){
		ArrayList<XYCurveInfo> list=new ArrayList<XYCurveInfo>();
		db = SkGlobalData.getProjectDatabase();
		
		if (db!=null) {
			String sql = "select * from xytrends where nSceneId=" + sid;
			Cursor cursor = db.getDatabaseBySql(sql, null);
			if (null == cursor) {
				Log.e("AKXYCurve", "AKXYCurve select db error !");
				return list;
			}
			
			boolean init=true;
			String id="";
			while (cursor.moveToNext()) {
				XYCurveInfo info=new XYCurveInfo();
				
				info.setnId(cursor.getInt(cursor.getColumnIndex("id")));
				int nItemId=cursor.getInt(cursor.getColumnIndex("nItemId"));
				if (init) {
					id+=" nItemId in("+nItemId;
				}else{
					id+=","+nItemId;
				}
				info.setnItemId(nItemId);
				info.setnSceneId(cursor.getInt(cursor.getColumnIndex("nSceneId")));
				info.setnTopLeftX(cursor.getInt(cursor.getColumnIndex("nTopLeftX")));
				info.setnTopLeftY(cursor.getInt(cursor.getColumnIndex("nTopLeftY")));
				info.setnWidth(cursor.getInt(cursor.getColumnIndex("nWidth")));
				info.setnHeight(cursor.getInt(cursor.getColumnIndex("nHeight")));
				info.setnCurveX(cursor.getInt(cursor.getColumnIndex("nCurveX")));
				info.setnCurveY(cursor.getInt(cursor.getColumnIndex("nCurveY")));
				info.setnCurveWidth(cursor.getInt(cursor.getColumnIndex("nCurveWd")));
				info.setnCurveHeight(cursor.getInt(cursor.getColumnIndex("nCurveHt")));
				info.setnSampleCount(cursor.getInt(cursor.getColumnIndex("nAddrLength")));
				info.setnChannelNum(cursor.getInt(cursor.getColumnIndex("nChannelNum")));
				info.setmControlAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nTrigAddr"))));
				String autoReset=cursor.getString(cursor.getColumnIndex("bAutoReset"));
				boolean bAutoReset=false;
				if (autoReset!=null) {
					bAutoReset=autoReset.equals("true")?true:false;
				}
				info.setbAutoReset(bAutoReset);
				info.seteDataType(IntToEnum.getDataType(cursor.getInt(cursor.getColumnIndex("eDataType"))));
				info.setnXShowMax(cursor.getDouble(cursor.getColumnIndex("nDisplayMaxX")));
				info.setnXShowMin(cursor.getDouble(cursor.getColumnIndex("nDisplayMinX")));
				info.setnYShowMax(cursor.getDouble(cursor.getColumnIndex("nDisplayMaxY")));
				info.setnYShowMin(cursor.getDouble(cursor.getColumnIndex("nDisplayMinY")));
				info.setnXShowType(cursor.getInt(cursor.getColumnIndex("bDisplayConst")));
				info.setnYShowType(cursor.getInt(cursor.getColumnIndex("bDisplayConstY")));
				if (info.getnXShowType()==1) {
					info.setmXShowMaxAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nDisplayMaxX"))));
					info.setmXShowMinAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nDisplayMinX"))));
				}
				if (info.getnYShowType()==1) {
					info.setmYShowMaxAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nDisplayMaxY"))));
					info.setmYShowMinAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nDisplayMinY"))));
				}
				boolean bScale=false;
				String scale=cursor.getString(cursor.getColumnIndex("bScale"));
				if(scale!=null){
					bScale=scale.equals("true")?true:false;
				}
				info.setbScale(bScale);
				info.setnXScaleType(cursor.getInt(cursor.getColumnIndex("eNumberTypeX")));
				info.setnYScaleType(cursor.getInt(cursor.getColumnIndex("eNumberTypeY")));
				info.setnXTargetMax(cursor.getDouble(cursor.getColumnIndex("nSourceMaxX")));
				info.setnXTargetMin(cursor.getDouble(cursor.getColumnIndex("nSourceMinX")));
				if (bScale&&info.getnXScaleType()==1) {
					info.setmXTargetMaxAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nSourceMaxX"))));
					info.setmXTargetMinAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nSourceMinX"))));
				}
				info.setnYScaleType(cursor.getInt(cursor.getColumnIndex("eNumberTypeY")));
				info.setnYTargetMax(cursor.getDouble(cursor.getColumnIndex("nSourceMaxY")));
				info.setnYTargetMin(cursor.getDouble(cursor.getColumnIndex("nSourceMinY")));
				if(bScale&&info.getnYScaleType()==1){
					info.setmYTargetMaxAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nSourceMaxY"))));
					info.setmYTargetMinAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("nSourceMinY"))));
				}
				
				boolean bShowVScale=false;
				String s1=cursor.getString(cursor.getColumnIndex("bMainVer"));
				if (s1!=null) {
					bShowVScale = s1.equals("true") ? true : false;
				}
				info.setbShowVScale(bShowVScale);
				info.setnVMajorNum(cursor.getInt(cursor.getColumnIndex("nVertMajorScale")));
				
				boolean bShowVMinorScale=false;
				String minor=cursor.getString(cursor.getColumnIndex("bSelectVertMinor"));
				if (minor!=null) {
					bShowVMinorScale=minor.equals("true")?true:false;
				}
				info.setbShowVMinorScale(bShowVMinorScale);
				info.setnVMinorNum(cursor.getInt(cursor.getColumnIndex("nVertMinorScale")));
				
				
				boolean bShowHScale=false;
				String s2=cursor.getString(cursor.getColumnIndex("bMainHor"));
				if (s2!=null) {
					bShowHScale = s2.equals("true") ? true : false;
				}
				info.setbShowHScale(bShowHScale);
				info.setnHMajorNum(cursor.getInt(cursor.getColumnIndex("nHorMajorScale")));
				
				boolean bShowHMinorScale=false;
				String hminor=cursor.getString(cursor.getColumnIndex("bSelectHorMinor"));
				if (hminor!=null) {
					bShowHMinorScale=hminor.equals("true")?true:false;
				}
				info.setbShowHMinorScale(bShowHMinorScale);
				info.setnHMinorNum(cursor.getInt(cursor.getColumnIndex("nHorMinorScale")));
				
				boolean bShowNet=false;
				String s3=cursor.getString(cursor.getColumnIndex("bSelectNet"));
				if (s3!=null) {
					bShowNet = s3.equals("true") ? true : false;
				}
				info.setbShowNet(bShowNet);
				info.setnVNetColor(cursor.getInt(cursor.getColumnIndex("nHorNetColor")));
				info.setnHNetColor(cursor.getInt(cursor.getColumnIndex("nVertNetColor")));
				info.setnBoradColor(cursor.getInt(cursor.getColumnIndex("nBoradColor")));
				info.setnScaleColor(cursor.getInt(cursor.getColumnIndex("nScaleColor")));
				info.setnGraphColor(cursor.getInt(cursor.getColumnIndex("nGraphColor")));
				info.setnFontSize(cursor.getInt(cursor.getColumnIndex("nFontSize")));
				info.setnFontColor(cursor.getInt(cursor.getColumnIndex("nMarkColor")));
				info.setnZvalue(cursor.getInt(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getInt(cursor.getColumnIndex("nCollidindId")));
				info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(cursor.getInt(cursor.getColumnIndex("nItemId"))));
				list.add(info);
				
			}
			cursor.close();
			
			//查找通道
			if(list.size()>0){
				selectChannel(list,id+")");
			}
		}
		return list;
	}

	//获取通道信息
	private void selectChannel(ArrayList<XYCurveInfo> list,String id){
		String sql = "select * from trendsChannelSet where " + id;
		Cursor result = db.getDatabaseBySql(sql, null);
		if (null == result) {
			Log.e("HistoryTrendsBiz", "trends: cursor failed!");
		}

		int nItemId = -1;
		XYCurveInfo mCurveInfo = null;

		/* 取记录条数放到容器中 */
		if (result != null) {
			while (result.moveToNext()) {
				if (nItemId != result.getInt(result.getColumnIndex("nItemId"))) {
					nItemId = result.getInt(result.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId() == nItemId) {
							mCurveInfo = list.get(i);
							ArrayList<ChannelGroupInfo> channelGroups = new ArrayList<ChannelGroupInfo>();
							mCurveInfo.setmChannelList(channelGroups);
							break;
						}
					}

				}

				ChannelGroupInfo Channel = new ChannelGroupInfo();
				
				int xaddr=result.getInt(result.getColumnIndex("nStartAddrX"));
				if (xaddr>0) {
					Channel.setmXAddrProp(AddrPropBiz.selectById(xaddr));
				}
				int yaddr=result.getInt(result.getColumnIndex("nStartAddrY"));
				if (yaddr>0) {
					Channel.setmYAddrProp(AddrPropBiz.selectById(yaddr));
				}
				short nNumOfChannel;
				nNumOfChannel = (short) result.getInt(result.getColumnIndex("nNumOfChannel"));
				Channel.setnNumOfChannel(nNumOfChannel);
				int nDisplayCondition = result.getInt(result.getColumnIndex("nDisplayCondition"));
				Channel.setnDisplayCondition(getChanelType(nDisplayCondition));
				Channel.setnDisplayAddr(AddrPropBiz.selectById(result.getShort(result.getColumnIndex("nDisplayAddr"))));
				int nLineType = result.getInt(result.getColumnIndex("nLineType"));
				Channel.setnLineType(IntToEnum.getLineType(nLineType + 1));
				Channel.setnLineThickness((short) result.getInt(result.getColumnIndex("nLineThickness")));
				Channel.setnDisplayColor(result.getInt(result.getColumnIndex("nDisplayColor")));
				mCurveInfo.getmChannelList().add(Channel);
			}
			
			result.close();
			
		}

	}
	
	
	/**
	 * 历史曲线显示类型
	 */
	public HISTORYSHOW_TYPE getChanelType(int num) {
		switch (num) {
		case 1:
			return HISTORYSHOW_TYPE.ALWAYS_SHOW;
		case 2:
			return HISTORYSHOW_TYPE.ON_SHOW;
		case 3:
			return HISTORYSHOW_TYPE.OFF_SHOW;
		}
		return HISTORYSHOW_TYPE.ALWAYS_SHOW;
	}
}
