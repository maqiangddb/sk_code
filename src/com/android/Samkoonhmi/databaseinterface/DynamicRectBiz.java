package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.DynamicRectInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;
/**
 * 动态矩形数据库查询 
 *@author 魏 科
 *@date   2012-06-20
 * */
public class DynamicRectBiz extends DataBase{

	//数据库句柄
	SKDataBaseInterface mDB = null;
	
	//数据表查询语句
	String mSearchTableStr  = new String("select * from dynamicRect where nSceneId=?");
	
	//控制地址查询语句
	String mSearchAddrStr   = new String("select * from addr where nAddrId=?");
		
	public DynamicRectBiz(){
		//Do nothing
	}
	
	/**
	 * 从数据库中查找指定控件的数据实体
	 * */
	public ArrayList<DynamicRectInfo> select(int sid){
		mDB = SkGlobalData.getProjectDatabase();
		if(null == mDB){//获得数据库失败
			Log.e("DynamicRectBiz","select:Get database failed!");
			return null;
		}
		Cursor tmpCursor = null;	
		tmpCursor = mDB.getDatabaseBySql(mSearchTableStr, new String[] { Integer.toString(sid) });
		if(null == tmpCursor){//获取游标失败
			Log.e("DynamicRectBiz","select:Get cursor failed!");
			return null;
		}
		
		//TouchShowInfoBiz tBiz=new TouchShowInfoBiz();
		
		ArrayList<DynamicRectInfo> list=new ArrayList<DynamicRectInfo>();
		while(tmpCursor.moveToNext()){
			DynamicRectInfo dstDRInfo = new DynamicRectInfo();
			
			//层信息
			dstDRInfo.setZvalue(tmpCursor.getShort(tmpCursor.getColumnIndex("nZvalue")));
			dstDRInfo.setnItemId(tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId")));
			
			//设置区域信息，保留代码，不可删除
//			dstDRInfo.setAreaLp(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaLp")));
//			dstDRInfo.setAreaTp(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaTp")));
//			dstDRInfo.setAreaWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaWidth")));
//			dstDRInfo.setAreaHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaHeight")));
//			dstDRInfo.setAreaColor(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaColor")));
			
			//设置控件初始信息
			dstDRInfo.setXPos(tmpCursor.getShort(tmpCursor.getColumnIndex("nXPos")));
			dstDRInfo.setYPos(tmpCursor.getShort(tmpCursor.getColumnIndex("nYPos")));
			dstDRInfo.setWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nWidth")));
			dstDRInfo.setHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nHeight")));
						
			dstDRInfo.setUseFill(tmpCursor.getShort(tmpCursor.getColumnIndex("nUseFill")));
			if(1 == dstDRInfo.getUseFill()){//是否有填充色
				dstDRInfo.setFillColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nFillColor")));
			}
			
			dstDRInfo.setRimColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nRimColor")));
			dstDRInfo.setRimWidh(tmpCursor.getShort(tmpCursor.getColumnIndex("nRimWidth")));
			dstDRInfo.setAlpha(tmpCursor.getShort(tmpCursor.getColumnIndex("nAlpha")));
			dstDRInfo.setCollidindId(tmpCursor.getInt(tmpCursor.getColumnIndex("nCollidindId")));
			dstDRInfo.setRefType(tmpCursor.getShort(tmpCursor.getColumnIndex("nRefType")));
			
			dstDRInfo.setUsePosCtrl(tmpCursor.getShort(tmpCursor.getColumnIndex("nUsePosCtrl")));			
			if(1 == dstDRInfo.getUsePosCtrl()){//若位置受到地址的控制
				int tmpXDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mXDataAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpXDataAddr);
				dstDRInfo.setXDataAddr(mTmpAddr);

				int tmpYDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mYDataAddr"));
				AddrProp mTmpAddrs = SkGlobalData.getProjectDatabase().getAddrById(tmpYDataAddr);
				dstDRInfo.setYDataAddr(mTmpAddrs);
			}
			
			
			dstDRInfo.setUseSizeCtrl(tmpCursor.getShort(tmpCursor.getColumnIndex("nUseSizeCtrl")));
			if(1 == dstDRInfo.getUseSizeCtrl()){//若大小受到地址的控制
				int tmpWDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mWDataAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpWDataAddr);
				dstDRInfo.setWDataAddr(mTmpAddr);

				int tmpHDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mHDataAddr"));
				AddrProp mTmpAddrs = SkGlobalData.getProjectDatabase().getAddrById(tmpHDataAddr);
				dstDRInfo.setHDataAddr(mTmpAddrs);
			}
			
			dstDRInfo.setmShowInfo(TouchShowInfoBiz.getShowInfoById(dstDRInfo.getnItemId()));
			list.add(dstDRInfo);
		}
		close(tmpCursor);
		return list;
	}
}
