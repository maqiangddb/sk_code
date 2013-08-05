package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import com.android.Samkoonhmi.model.HistoryShowDataInfo;
import com.android.Samkoonhmi.model.HistoryShowInfo;
import com.android.Samkoonhmi.model.RecipeShowInfo;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;


public class HistoryShowBiz extends DataBase{

	SKDataBaseInterface db = null;
	public ArrayList<HistoryShowInfo> select(int sid){
		
		ArrayList<HistoryShowInfo> list=new ArrayList<HistoryShowInfo>();
		
		db = SkGlobalData.getProjectDatabase();
		Cursor cursor = null;
		if (db==null) {
			return null;
		}
		
		String id = "";
		boolean init = true;
		String sGroupName="";
		String sql = "select * from historyShow where nSceneId="+sid;
		cursor = db.getDatabaseBySql(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				HistoryShowInfo info=new HistoryShowInfo();
				info.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
				info.setnAlpha(cursor.getShort(cursor.getColumnIndex("nAlpha")));
				sGroupName=cursor.getString(cursor.getColumnIndex("sGroupName"));
				info.setnForecolor(cursor.getInt(cursor.getColumnIndex("nForecolor")));
				info.setnFrameColor(cursor.getInt(cursor.getColumnIndex("nFrameColor")));
				info.setnTextFontSize(cursor.getShort(cursor.getColumnIndex("nTextFontSize")));
				info.setnTitleBackColor(cursor.getInt(cursor.getColumnIndex("nTitleBackColor")));
				info.setnTitleFontColor(cursor.getInt(cursor.getColumnIndex("nTitleFontColor")));
				boolean b=cursor.getString(cursor.getColumnIndex("bShowTime")).equals("true")?true:false;
				info.setbShowTime(b);
				boolean bb=cursor.getString(cursor.getColumnIndex("bShowDate")).equals("true")?true:false;
				info.setbShowDate(bb);
				info.seteTimeFormat(IntToEnum.getTimeType(
						cursor.getShort(cursor.getColumnIndex("eTimeFormat"))));
				info.seteDateFormat(IntToEnum.getDateType(
						cursor.getShort(cursor.getColumnIndex("eDateFormat"))));
				boolean bbb=cursor.getString(cursor.getColumnIndex("bShowCode")).equals("true")?true:false;
				info.setbShowCode(bbb);
				info.setnTextFontColor(cursor.getInt(cursor.getColumnIndex("nTextFontColor")));
				info.setnLine(cursor.getShort(cursor.getColumnIndex("nLine")));
				info.setnLeftTopX(cursor.getShort(cursor.getColumnIndex("nLeftTopX")));
				info.setnLeftTopY(cursor.getShort(cursor.getColumnIndex("nLeftTopY")));
				info.setnWidth(cursor.getShort(cursor.getColumnIndex("nWidth")));
				info.setnHeight(cursor.getShort(cursor.getColumnIndex("nHeight")));
				info.setnZvalue(cursor.getShort(cursor.getColumnIndex("nZvalue")));
				info.setnCollidindId(cursor.getShort(cursor.getColumnIndex("nCollidindId")));
				info.setmShowInfo(TouchShowInfoBiz.getShowInfoById(info.getnItemId()));
				info.setmTouchInfo(TouchShowInfoBiz.getTouchInfoById(info.getnItemId()));
				setGroupId(sGroupName,info);
				
				boolean bc = cursor.getString(
						cursor.getColumnIndex("bAddr")).equals("true") ? true: false;
				info.setbControl(bc);
				if (bc) {
					info.setmControlAddr(AddrPropBiz.selectById(cursor.getInt(cursor.getColumnIndex("mAddress"))));
				}
				
				list.add(info);
				
				if (init) {
					id += " nItemId=" + info.getnItemId();
					init = false;
				} else {
					id += " or nItemId=" + info.getnItemId();
				}
			}
			close(cursor);
			if (list.size()>0) {
				setTitleText(list,id);
				setTitleData(list,id);
				getTable(list,id);
			}
			
		}
	    return list;
	}
	
	/**
	 * 根据组名获取组号
	 */
	private void setGroupId(String name,HistoryShowInfo info){
		if(info!=null){
			int gid=-1;
			String sql = "select nGroupId from dataCollect where sName=?";
			Cursor cursor;
			cursor = db.getDatabaseBySql(sql, new String[] {name});
			if(cursor!=null){
				while (cursor.moveToNext()) {
					gid=cursor.getInt(cursor.getColumnIndex("nGroupId"));
				}
			}
			info.setnGroupId(gid);
			close(cursor);
		}
	}
	
	/**
	 * 获取文本
	 */
	public void setTitleText(ArrayList<HistoryShowInfo> list,String id){

        String sql = "select * from historyShowText where "+id;
		Cursor cursor;
		int nItemId=-1;
		HistoryShowInfo info=null;
		cursor = db.getDatabaseBySql(sql, null);
		
		if(cursor!=null){
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId()== nItemId) {
							info = list.get(i);
							ArrayList<Integer> mSize=new ArrayList<Integer>();
							ArrayList<String> mTimeName=new ArrayList<String>();
							ArrayList<String> mType=new ArrayList<String>();
							ArrayList<String> mDateName=new ArrayList<String>();
							ArrayList<String> mTitleNum=new ArrayList<String>();
							
							info.setmTitleDateNames(mDateName);
							info.setmTitleTimeNames(mTimeName);
							info.setmTitleFontType(mType);
							info.setmTitleFontSizes(mSize);
							info.setmTitleNum(mTitleNum);
							break;
						}
					}
				}
				
				info.getmTitleDateNames().add(cursor.getString(cursor.getColumnIndex("sTitleDateName")));
				info.getmTitleTimeNames().add(cursor.getString(cursor.getColumnIndex("sTitleTimeName")));
				info.getmTitleFontSizes().add(cursor.getInt(cursor.getColumnIndex("nTitleFontSize")));
				info.getmTitleFontType().add(cursor.getString(cursor.getColumnIndex("sTitleFontType")));
				info.getmTitleNum().add(cursor.getString(cursor.getColumnIndex("sTitleNumber")));
			}
		}
		close(cursor);
	}
	
	/**
	 * 获取数据列
	 */
	public void setTitleData(ArrayList<HistoryShowInfo> list,String id){
		String sql = "select * from historyShowData where "+id+" order by nItemId,nCode";
		Cursor cursor;
		cursor = db.getDatabaseBySql(sql,null);
		int nItemId = -1;
		HistoryShowInfo info=null;
		if(cursor!=null){
			int nCode=-1;
			int index=0;
			while (cursor.moveToNext()) {
				if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
					nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getnItemId() == nItemId) {
							info = list.get(i);
							ArrayList<HistoryShowDataInfo> mDataList=new ArrayList<HistoryShowDataInfo>();
							info.setmDataList(mDataList);
							nCode=-1;
							index=0;
							break;
						}
					}

				}
				
				if (nCode!=cursor.getInt(cursor.getColumnIndex("nCode"))) {
					nCode=cursor.getInt(cursor.getColumnIndex("nCode"));
					HistoryShowDataInfo data=new HistoryShowDataInfo();
					data.setnItemId(cursor.getInt(cursor.getColumnIndex("nItemId")));
					data.setnLanguageId(cursor.getShort(cursor.getColumnIndex("nLanguageId")));
					data.setnCode(nCode);
					ArrayList<String> mTitleName=new ArrayList<String>();
					String name=cursor.getString(cursor.getColumnIndex("sTitleDataName"));
					mTitleName.add(name);
					data.setmTitleDataName(mTitleName);
					boolean bShow=cursor.getString(cursor.getColumnIndex("bShowTitleDataName")).equals("true")?true:false;
					data.setbShow(bShow);
					index=info.getmDataList().size();
					info.getmDataList().add(data);
				}else {
					HistoryShowDataInfo  dataInfo=info.getmDataList().get(index);
					String name=cursor.getString(cursor.getColumnIndex("sTitleDataName"));
					dataInfo.getmTitleDataName().add(name);
				}
			}
			
		}
		close(cursor);
	}
	
	/**
	 * 获取表格信息
	 */
	private void getTable(ArrayList<HistoryShowInfo> list,String id){
		if (db != null) {
			Cursor cursor = null;
			String sql = "select * from tableProp where "+id;
			cursor = db.getDatabaseBySql(sql, null);
			
			int nItemId=-1;
			HistoryShowInfo info=null;
			double width=0;
			double height=0;
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					if (nItemId != cursor.getInt(cursor.getColumnIndex("nItemId"))) {
						nItemId = cursor.getInt(cursor.getColumnIndex("nItemId"));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								info = list.get(i);
								//行的高
								ArrayList<Double> mRowHeight=new ArrayList<Double>();
								//行的宽
								ArrayList<Double> mRowWidth=new ArrayList<Double>();
								info.setmRowWidht(mRowWidth);
								info.setmRowHeight(mRowHeight);
								width=0;
								height=0;
								break;
							}
						}

					}
					if (cursor.getInt(cursor.getColumnIndex("nIsRow"))==0) {
						//行的宽
						width+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowWidht().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}else {
						//行的高
						height+=cursor.getDouble(cursor.getColumnIndex("nWidth"));
						info.getmRowHeight().add(cursor.getDouble(cursor.getColumnIndex("nWidth")));
					}
					if (width>0) {
						info.setnWidth((short)width);
					}
					if(height>0){
						info.setnHeight((short)height);
					}
				}
				cursor.close();
			}
		}
	}
}

