package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.GifViewerInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * GIF显示器数据库查询
 *@author 魏 科
 *@date   2012-06-20
 * */
public class GifViewerBiz extends DataBase{


	//数据库句柄
	SKDataBaseInterface mDB = null;

	//数据表查询语句 nOrigWidth 用于区分gifor图片，图片没有原始大小
	String mSearchTableStr  = new String("select * from imageShow where nOrigWidth>0 and nSceneId=?");

	//控制地址查询语句
	String mSearchAddrStr   = new String("select * from addr where nAddrId=?");

	public GifViewerBiz(){
		//Do nothing
	}

	/**
	 * 从数据库中查找指定控件的数据实体
	 * */
	public ArrayList<GifViewerInfo> select(int sid){

		mDB = SkGlobalData.getProjectDatabase();
		if(null == mDB){//获得数据库失败
			Log.e("GifViewerBiz","select:Get database failed!");
			return null;
		}

		ArrayList<GifViewerInfo> list=new ArrayList<GifViewerInfo>();
		Cursor tmpCursor = null;	
		StringBuffer id = new StringBuffer();
		boolean init = true;
		
		tmpCursor = mDB.getDatabaseBySql(mSearchTableStr, new String[] { Integer.toString(sid) });
		if(null == tmpCursor){//获取游标失败
			Log.e("GifViewerBiz","select:Get cursor failed!");
			return null;
		}

		//TouchShowInfoBiz tBiz=new TouchShowInfoBiz();

		while(tmpCursor.moveToNext()){
			GifViewerInfo dstGVInfo = new GifViewerInfo();
			dstGVInfo.setnItemId(tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId")));
			dstGVInfo.setBackColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nBackColor")));
			dstGVInfo.setHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nHeight")));
			dstGVInfo.setWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nWidth")));
			dstGVInfo.setGifWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nOrigWidth")));
			dstGVInfo.setGifHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nOrigHeight")));
			dstGVInfo.setLp(tmpCursor.getShort(tmpCursor.getColumnIndex("nLp")));
			dstGVInfo.setTp(tmpCursor.getShort(tmpCursor.getColumnIndex("nTp")));
			dstGVInfo.setZvalue(tmpCursor.getShort(tmpCursor.getColumnIndex("nZvalue")));
			dstGVInfo.setCollidindId(tmpCursor.getInt(tmpCursor.getColumnIndex("nCollidindId")));
			dstGVInfo.setmShowInfo(TouchShowInfoBiz.getShowInfoById(dstGVInfo.getnItemId()));
			dstGVInfo.setIsBitCtrl(tmpCursor.getShort(tmpCursor.getColumnIndex("nIsBitCtrl")));
			dstGVInfo.setRCount(tmpCursor.getInt(tmpCursor.getColumnIndex("nRCount")));
			
			if(1 == dstGVInfo.getIsBitCtrl()){
				dstGVInfo.setValidBit(tmpCursor.getShort(tmpCursor.getColumnIndex("nValidBit")));
				int CtrlAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("nCtrlAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(CtrlAddr);
				dstGVInfo.setCtrlAddr(mTmpAddr);
			}
			
			list.add(dstGVInfo);
			if (init) {
				id.append(" nItemId in(" + dstGVInfo.getnItemId());
				init = false;
			} else {
				id.append("," + dstGVInfo.getnItemId());
			}
		}
		close(tmpCursor);
		id.append(")");
		String sId=id.toString();
		
		if (list.size()>0) {
			
			//图片表查询语句
			String mSearchGifStr="select * from imagePath where "+sId;
			tmpCursor = mDB.getDatabaseBySql(mSearchGifStr, null);
			if(null == tmpCursor){//获取游标失败
				Log.e("GifViewerBiz","select: Get Gif cursor failed!");
				return null;
			}
			GifViewerInfo dstGVInfo=null;
			int nItemId=-1;
			int index=0;
			while(tmpCursor.moveToNext()){
				if (nItemId != tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"))) {
					nItemId = tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"));
					if (list.get(index).getnItemId()==nItemId) {
						dstGVInfo = list.get(index);
					}else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getnItemId() == nItemId) {
								dstGVInfo = list.get(i);
								break;
							}
						}
					}
					index++;
				}
				dstGVInfo.setGifPath(tmpCursor.getString(tmpCursor.getColumnIndex("sPicPath")));
			}
			close(tmpCursor);
		}
		
		return list;
	}

}//End of class