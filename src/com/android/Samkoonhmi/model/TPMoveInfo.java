package com.android.Samkoonhmi.model;

import android.util.Log;

/**
 * 轨迹点移动信息
 * @author 魏 科
 * @date   2012-06-12
 * */
public class TPMoveInfo {
	private short nTrackPointNo;  //关联的轨迹点序号
	private short nMCmpFactor;    //预设值
	
	/**
	 * 设置关联的轨迹点序号
	 * */
	public void setTPNo(short tpno){
   	 if(0>tpno){
		 Log.e("TPMoveInfo","setTPNo: Invalid track pint number:"+tpno);
		 return;
	 }
   	 this.nTrackPointNo = tpno;
	}
	
	/**
	 * 获得关联的轨迹点序号
	 * */
	public short getTPNo(){
		return this.nTrackPointNo;
	}
	
	/**
	 * 设置预设值
	 * */
	public void setCmpFactor(short value){
		this.nMCmpFactor = value;
	}
	
	/**
	 * 获得预设值
	 * */
	public short getCmpFactor(){
		return this.nMCmpFactor;
	}
}//End of class
