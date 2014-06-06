package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.DynamicCircleInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
* 动态圆形数据库查询 
* @author 魏 科
* @date   2012-06-20
* */
public class DynamicCircleBiz   extends DataBase{

	//数据库句柄
	SKDataBaseInterface mDB = null;
	
	//数据表查询语句
	String mSearchTableStr  = new String("select * from dynamicRound where nSceneId=?");
	
	//控制地址查询语句
	String mSearchAddrStr   = new String("select * from addr where nAddrId=?");
	
	public DynamicCircleBiz(){
		//Do nothing
	}

	/**
	 * 从数据库中查找指定控件的数据实体
	 * */
	public ArrayList<DynamicCircleInfo> select(int sid){
		
		mDB = SkGlobalData.getProjectDatabase();
		if(null == mDB){//获得数据库失败
			Log.e("DynamicCircleBiz","select:Get database failed!");
			return null;
		}
		
		ArrayList<DynamicCircleInfo> list=new ArrayList<DynamicCircleInfo>();
		Cursor tmpCursor = null;	
		tmpCursor = mDB.getDatabaseBySql(mSearchTableStr, new String[] { Integer.toString(sid) });
		if(null == tmpCursor){//获取游标失败
			Log.e("DynamicCircleBiz","select:Get cursor failed!");
			return null;
		}
		
		while(tmpCursor.moveToNext()){
			DynamicCircleInfo dstDCInfo = new DynamicCircleInfo();
			
			dstDCInfo.setnItemId(tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId")));
			//设置层信息
			dstDCInfo.setZvalue(tmpCursor.getShort(tmpCursor.getColumnIndex("nZvalue")));
			
			//设置控件大小及起始坐标
			dstDCInfo.setnAreaLp(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaLp")));
			dstDCInfo.setnAreaTp(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaTp")));
			dstDCInfo.setnAreaWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaWidth")));
			dstDCInfo.setnAreaHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaHeight")));
//			dstDCInfo.setAreaColor(tmpCursor.getShort(tmpCursor.getColumnIndex("nAreaColor")));
			
			//设置控件初始信息
			dstDCInfo.setCpXpos(tmpCursor.getShort(tmpCursor.getColumnIndex("nCpXpos")));
			dstDCInfo.setCpYpos(tmpCursor.getShort(tmpCursor.getColumnIndex("nCpYpos")));
			dstDCInfo.setRadius(tmpCursor.getShort(tmpCursor.getColumnIndex("nRadius")));
						
			dstDCInfo.setUseFill(tmpCursor.getShort(tmpCursor.getColumnIndex("nUseFill")));
			if(1 == dstDCInfo.getUseFill()){//是否有填充色
				dstDCInfo.setFillColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nFillColor")));
			}
			
			dstDCInfo.setRimColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nRimColor")));
			dstDCInfo.setRimWidh(tmpCursor.getShort(tmpCursor.getColumnIndex("nRimWidth")));
			dstDCInfo.setAlpha(tmpCursor.getShort(tmpCursor.getColumnIndex("nAlpha")));
					
			dstDCInfo.setUsePosCtrl(tmpCursor.getShort(tmpCursor.getColumnIndex("nUsePosCtrl")));	
			dstDCInfo.setCollidindId(tmpCursor.getInt(tmpCursor.getColumnIndex("nCollidindId")));
			
			if(1 == dstDCInfo.getUsePosCtrl()){//若位置受到地址的控制
				int tmpCpXDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mCpXDataAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpCpXDataAddr);
				dstDCInfo.setCpXDataAddr(mTmpAddr);

				int tmpCpYDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mCpYDataAddr"));
				AddrProp mTmpAddrs = SkGlobalData.getProjectDatabase().getAddrById(tmpCpYDataAddr);
				dstDCInfo.setCpYDataAddr(mTmpAddrs);
			}
			
			dstDCInfo.setUseSizeCtrl(tmpCursor.getShort(tmpCursor.getColumnIndex("nUseSizeCtrl")));
			if(1 == dstDCInfo.getUseSizeCtrl()){//若大小受到地址的控制			
				int tmpRadiusDataAddr = tmpCursor.getInt(tmpCursor.getColumnIndex("mRadiusDataAddr"));
				AddrProp mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpRadiusDataAddr);
				dstDCInfo.setRadiusDataAddr(mTmpAddr);
			}
			dstDCInfo.setmShowInfo(TouchShowInfoBiz.getShowInfoById(dstDCInfo.getnItemId()));
			list.add(dstDCInfo);
		}
		close(tmpCursor);
		
		return list;
	}
}
