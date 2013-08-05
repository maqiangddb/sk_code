package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.model.ImageViewerInfo;
import com.android.Samkoonhmi.model.PictureInfo;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.skbutton.ButtonInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 图片显示器数据库查询
 *@author 魏 科
 *@date   2012-06-20
 * */
public class ImageViewerBiz extends DataBase{

	//数据库句柄
	SKDataBaseInterface mDB = null;

	//数据表查询语句,//nOrigWidth 用于区分gifor图片，图片没有原始大小
	String mSearchTableStr  = new String("select * from imageShow where nOrigWidth<=0 and nSceneId=?");

	//图片表查询语句
	//String mSearchImgStr    = new String("select * from imagePath where nItemId=?");

	//监视表查询语句
	//String mSearchStkoStr  = new String("select * from imagePath where nItemId=?");
	
	//控制地址查询语句
	//String mSearchAddrStr  = new String("select * from addr where nAddrId=?");


	public ImageViewerBiz(){
		//Do nothing
	}

	/**
	 * 从数据库中查找指定的图片显示器数据
	 * @param  itemID 控件ID
	 * @return 查找得到的图片显示器数据实体
	 * */
	public ArrayList<ImageViewerInfo> select(int sid){

		mDB = SkGlobalData.getProjectDatabase();
		if(null == mDB){//获得数据库失败
			Log.e("ImageViewerBiz","select:Get database failed!");
			return null;
		}

		String id = "";
		boolean init = true;
		Cursor tmpCursor = null;	
		tmpCursor = mDB.getDatabaseBySql(mSearchTableStr, new String[] { Integer.toString(sid) });
		if(null == tmpCursor){//获取游标失败
			Log.e("ImageViewerBiz","select:Get cursor failed!");
			return null;
		}
		
		ArrayList<ImageViewerInfo> list=new ArrayList<ImageViewerInfo>();
		
		while(tmpCursor.moveToNext()){
			ImageViewerInfo dstIVInfo = new ImageViewerInfo();
			
			dstIVInfo.setnItemId(tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId")));
			dstIVInfo.setLp(tmpCursor.getShort(tmpCursor.getColumnIndex("nLp")));
			dstIVInfo.setTp(tmpCursor.getShort(tmpCursor.getColumnIndex("nTp")));

			dstIVInfo.setWidth(tmpCursor.getShort(tmpCursor.getColumnIndex("nWidth")));
			dstIVInfo.setHeight(tmpCursor.getShort(tmpCursor.getColumnIndex("nHeight")));

			dstIVInfo.setFunType(tmpCursor.getShort(tmpCursor.getColumnIndex("nFunType")));

			
			String sUseFlicker = tmpCursor.getString(tmpCursor.getColumnIndex("bUseFlicker"));
			if (sUseFlicker==null||sUseFlicker.equals("")) {
				dstIVInfo.setFlickerAttr(false);
			}else{
				if(sUseFlicker.equals("true")){
					dstIVInfo.setFlickerAttr(true);				
				}else{
					dstIVInfo.setFlickerAttr(false);
				}
			}
			

			dstIVInfo.setBackColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nBackColor")));

			dstIVInfo.setChangeCondition(tmpCursor.getShort(tmpCursor.getColumnIndex("nChangeCondition")));

			dstIVInfo.setBackColor(tmpCursor.getInt(tmpCursor.getColumnIndex("nBackColor")));
			
			dstIVInfo.setZvalue(tmpCursor.getShort(tmpCursor.getColumnIndex("nZvalue")));
			
			if(0 != dstIVInfo.getChangeCondition()){
				int tmpAddrID =  tmpCursor.getInt(tmpCursor.getColumnIndex("nWatchAddr"));
				AddrProp mTmpAddr = null;
				mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(tmpAddrID);
				dstIVInfo.setWatchAddr(mTmpAddr);
			}
			
			dstIVInfo.setStatusTotal(tmpCursor.getShort(tmpCursor.getColumnIndex("nStatusTotal")));

			dstIVInfo.setTimeInterval(tmpCursor.getShort(tmpCursor.getColumnIndex("nTimeInterval")));
			
			dstIVInfo.setCollidindId(tmpCursor.getInt(tmpCursor.getColumnIndex("nCollidindId")));
			
			String sIsLoopType = tmpCursor.getString(tmpCursor.getColumnIndex("bIsLoopType"));
			if (sIsLoopType==null||sIsLoopType.equals("")) {
				dstIVInfo.setLoopAttr(false);
			}else {
				if(sIsLoopType.equals("true")){
					dstIVInfo.setLoopAttr(true);				
				}else{
					dstIVInfo.setLoopAttr(false);
				}
			}
			
			
			dstIVInfo.setmShowInfo(TouchShowInfoBiz.getShowInfoById(dstIVInfo.getnItemId()));
			list.add(dstIVInfo);
			if (init) {
				id += " nItemId=" + dstIVInfo.getnItemId();
				init = false;
			} else {
				id += " or nItemId=" + dstIVInfo.getnItemId();
			}

		}//End of while(tmpCursor.moveToNext())		
		close(tmpCursor);	
		
		//设置图片列表
		if (list.size()>0) {
			selectImgList(list,id);
		}
		return list;
	}

	/**
	 * 根据控件ID查询图片列表
	 * @param  itemID 控件ID
	 * */
	private void selectImgList(ArrayList<ImageViewerInfo> list,String id){

		Cursor    tmpCursor = null;	
		String sql="select * from imagePath where "+id;
		tmpCursor = mDB.getDatabaseBySql(sql, null);
		int nItemId=-1;
		ImageViewerInfo info=null;
		while(tmpCursor.moveToNext()){

			if (nItemId != tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"))) {
				nItemId = tmpCursor.getInt(tmpCursor.getColumnIndex("nItemId"));
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getnItemId() == nItemId) {
						info = list.get(i);
						ArrayList<PictureInfo> imglist = new ArrayList<PictureInfo>(); 
						ArrayList<StakeoutInfo> stkolist = new ArrayList<StakeoutInfo>(); 
						info.setPicPathArray(imglist);
						info.setStakeoutList(stkolist);
						break;
					}
				}

			}
			if(info!=null){
				//新建实例
				PictureInfo pi = new PictureInfo();

				//获得图片对应的状态号
				pi.setnStatusId(tmpCursor.getShort(tmpCursor.getColumnIndex("nStatusId")));

				//获得图片的路径信息
				pi.setsPath(tmpCursor.getString(tmpCursor.getColumnIndex("sPicPath")));

				//将实例添加到图片列表
				info.getPicPathArray().add(pi);
				
				if(0 != info.getChangeCondition()){//按位/字地址切换时
					//新建实例
					StakeoutInfo si = new StakeoutInfo();

					//获得图片对应的状态号
					si.setnStatusId(tmpCursor.getShort(tmpCursor.getColumnIndex("nStatusId")));

					//获得监视条件比较值
					si.setnCmpFactor(tmpCursor.getShort(tmpCursor.getColumnIndex("nCmpFactor")));

					//将实例添加到图片列表
					info.getStakeoutList().add(si);
				}
			}

		}//End of while(tmpCursor.moveToNext())		
		close(tmpCursor);
	}

}//End of class
