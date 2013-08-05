package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import android.graphics.Color;
import android.util.Log;

import com.android.Samkoonhmi.util.AddrProp;

/**
 * 图片显示器数据实体类
 * @author 魏 科
 * @date   2012-06-11
 * */
public class ImageViewerInfo {
	
	//布局属性
	 private int nItemId;       //控件id
	 private short nLp;        //控件左上角X坐标
	 private short nTp;        //控件左上角Y坐标
	 private short nWidth;     //控件宽度
	 private short nHeigth;    //控件高度	 
	 private short nZvalue;    //控件层序号
	 
	 //公共属性
	 private short nFunType;        //图片显示器类型，约定：0单幅固定，1多幅切换
	 private short nChangeCondition;//切换条件，约定：0时间间隔切换，1位值切换，2固定值切换
	 private short nStatusTotal;    //状态总数
	 private short nTimeInterval;   //时间间隔
	 
	 private int   nBackColor;      //背景色
	 private int   nCollidindId;    //组合ID
	 
	 //显现
     private ShowInfo mShowInfo;    //显现属性
	 
	 private AddrProp mWatchAddr;   //监视地址
	 
	 private ArrayList<PictureInfo>  mPicPathArray; //图片路径
	 
	 private ArrayList<StakeoutInfo> mStakeoutList; //监视条件，尽在按固定值切换时有效

	 //单幅固定特有属性
	 private boolean    bUseFlicker;     //是否闪烁
	 
	 //多幅切换特有属性
	 private boolean    bIsLoopType;     //是否为循环切换，仅在按时间间隔切换时有效
	 
	 /**
	  * 设置控件左上角X坐标
	  * */
	 public void setLp(short Lp){
		 if(0 < Lp){
			 this.nLp = Lp;
		 }
	 }
	 
	 /**
	  * 获得控件左上角X坐标
	  * */
	 public short getLp(){
		 return this.nLp;
	 }

	 /**
	  *  设置控件左上角y坐标
	  * */
	 public void setTp(short Tp){
		 if(0 < Tp){
			 this.nTp = Tp;
		 }
	 }
	 
	 /**
	  *  获得控件左上角y坐标
	  * */
	 public short getTp(){
		return this.nTp;
	 }
	 
	 /**
	  * 设置控件宽度
	  * */
	 public void setWidth(short width){
		 if(0 < width){
			 this.nWidth = width;
		 }
	 }
	 
	 /**
	  * 获得控件宽度
	  * */
	 public short getWidth(){
		 return this.nWidth;
	 }
	 
	 /**
	  * 设置控件高度
	  * */
	 public void setHeight(short height){
		 if(0 < height){
			 this.nHeigth = height;
		 }
	 }
	 
	 /**
	  * 获得控件高度
	  * */
	 public short getHeight(){
		return this.nHeigth;
	 }
	 
	 /**
	  * 设置图片显示器的功能类型
	  * */
	 public void setFunType(short funtype){
		 if((0 != funtype) && (1 != funtype)){
			 Log.e("ImageViewerInfo","setFunType: Invalid funtype: " + funtype);
			 return;
		 }
		 this.nFunType = funtype;
	 }
	 
	 /**
	  * 获得图片显示器的功能类型
	  * */
	 public short getFunType(){
		 return this.nFunType;
	 }
	 
	 /**
	  * 设置图片切换条件
	  * */
	 public void setChangeCondition(short chgcond){
		 if( (0 != chgcond) && (1 != chgcond) && (2 != chgcond)){
			 Log.e("ImageViewerInfo","Invalid change conditon: " + chgcond);
			 return;
		 }
		 this.nChangeCondition = chgcond;
	 }
	 
	 /**
	  * 获得图片切换条件
	  * */
	 public short getChangeCondition(){
		 return this.nChangeCondition;
	 }
	 
	 /**
	  * 设置状态总数
	  * */
	 public void setStatusTotal(short stotal){
		 if(0 > stotal){
			 Log.e("ImageViewerInfo","Invalid status total: " + stotal);
			 return;
		 }
		 this.nStatusTotal = stotal;
	 }
	 
	 /**
	  * 获得状态总数
	  * */
	 public short getStatusTotal(){
		 return this.nStatusTotal;
	 }
	 
	 /**
	  * 设置时间间隔
	  * */
	 public void setTimeInterval(short tinterval){
		 if(0 > tinterval){
			 Log.e("ImageViewerInfo","Invalid time interval: " + tinterval);
			 return;
		 }
		 this.nTimeInterval = tinterval;
	 }
	 
	 /**
	  * 获得时间间隔
	  * */
	 public short getTimeInterval(){
		 return this.nTimeInterval;
	 }
	 
	 /**
	  * 设置背景颜色
	  * */
	 public void setBackColor(int color){
		 this.nBackColor = color;
	 }
	 
	 /**
	  * 获得背景颜色
	  * */
	 public int getBackColor(){
		 return this.nBackColor;
	 }
	 
	 
	 /**
	  * 设置闪烁属性
	  * */
	 public void setFlickerAttr(boolean attr){
		this.bUseFlicker = attr; 
	 }
	 
	 
	 /**
	  * 获得闪烁属性
	  * */
	 public boolean isFlicker(){
		return this.bUseFlicker; 
	 }
	 
	 /**
	  * 设置循环属性
	  * */
	 public void setLoopAttr(boolean attr){
		 this.bIsLoopType = attr;
	 }
	 
	 /**
	  * 获得循环属性
	  * */
	 public boolean isLoop(){
		 return this.bIsLoopType;
	 }
	 
	 /**
	  * 设置监视地址
	  * */
	 public void setWatchAddr(AddrProp watchaddr) {
		 this.mWatchAddr = watchaddr;
	 }
	 
	 /**
	  * 获得监视地址
	  * */
	 public AddrProp getWatchAddr() {
		 return this.mWatchAddr;
	 }
	 
	 /**
	  * 设置 图片列表
	  * */
	 public void setPicPathArray(ArrayList<PictureInfo> picinfoarray){
		 if(null == picinfoarray){
			 Log.e("ImageViewerInfo","Picture array is null");
			 return;
		 }
		 this.mPicPathArray = picinfoarray;
	 }
	 
	 /**
	  * 获得图片列表
	  * */
	 public ArrayList<PictureInfo> getPicPathArray(){
		 return  this.mPicPathArray;
	 }
	 
	 /**
	  * 设置监视条件列表
	  * */
	 public void setStakeoutList(ArrayList<StakeoutInfo> stkinfolist){
		 if(null == stkinfolist){
			 Log.e("ImageViewerInfo","Picture array is null");
			 return;
		 }
		 this.mStakeoutList = stkinfolist;
	 }
	 
	 /**
	  * 获得监视条件列表
	  * */
	 public ArrayList<StakeoutInfo> getStakeoutList( ){
		 return  this.mStakeoutList;
	 }
	 
	 /**
	  * 设置层序号
	  * */
	 public void setZvalue(short zvalue){
		 this.nZvalue = zvalue;
	 }
	 
	 /**
	  * 获得层序号
	  * */
	 public short getZValue(){
		 return nZvalue;
	 }
	 
	 /**
	  * 设置组合ID
	  * */
	 public void setCollidindId(int cid){
		 this.nCollidindId = cid;
	 }
	 
	 /**
	  * 获得组合ID
	  * */
	 public int getCollidindId(){
		 return this.nCollidindId;
	 }

	 /**
	  * 获得显现属性
	  * */
	 public ShowInfo getmShowInfo() {
		 return mShowInfo;
	 }
	 /**
	  * 设置显现属性
	  * */
	 public void setmShowInfo(ShowInfo mShowInfo) {
		 this.mShowInfo = mShowInfo;
	 }

	public int getnItemId() {
		return nItemId;
	}

	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
}//End of: class