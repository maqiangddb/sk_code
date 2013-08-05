package com.android.Samkoonhmi.model;

import android.util.Log;

import com.android.Samkoonhmi.util.AddrProp;

/**
 * 动态矩形的数据实体类
 * @author 魏 科
 * @date   2012-06-14
 * */
public class DynamicRectInfo {
	 //布局属性
	 private int nItemId;
	private short nXPos;      //原点X坐标
	 private short nYPos;      //原点Y坐标
	 private short nWidth;     //控件宽度
	 private short nHeight;    //控件高度
	 private short nZvalue;    //控件层序号
	 
	 private short nUseFill;   //是否填充
	 private int   nFillColor; //矩形填充色
	 
	 private int   nRimColor;  //边框填充色
	 private short nRimWidth;  //边框宽度
	 private short nAlpha;     //透明度
	 
	 private short    nUsePosCtrl;//位置是否受到地址控制
	 private AddrProp mXDataAddr; //X坐标控制地址
	 private AddrProp mYDataAddr; //Y坐标控制地址
	 
	 private short    nUseSizeCtrl; //大小是否受地址控制
	 private short    nRefType;     //大小变化参考点，约定：0左上，1右上，2右下，3左下
	 private AddrProp mWDataAddr;   //矩形宽度控制地址
	 private AddrProp mHDataAddr;   //矩形高度控制地址
	 
	 private int   nCollidindId;    //组合ID 
	 
	 //显现
	 private ShowInfo mShowInfo;    //显现属性
	 
	 //保留代码，不可删除
//	 private short nAreaLp;     //区域原点X坐标
//	 private short nAreaTp;     //区域原点Y坐标
//	 private short nAreaWidth;  //区域宽度
//	 private short nAreaHeight; //区域高度
//	 private int   nAreaColor;  //区域背景色
	 
	 
	 /**
	  * 设置透明度
	  * */
	 public void setAlpha(short a){
		 this.nAlpha = a;
	 }
	 /**
	  * 获得透明度
	  * */
	 public short getAlpha(){
		 return this.nAlpha;
	 }
	 
	 
	 /**
	  * 设置原点左上角X坐标
	  * */
	 public void setXPos(short xp){
		 if(0 > xp){
			 Log.e("SynamicRectInfo","setXPos: Invalid xp: "+xp);
			 return;
		 }
		 this.nXPos = xp;
	 }
	 
	 /**
	  * 获得原点X坐标
	  * */
	 public short getXPos(){
		 return this.nXPos;
	 }
	 
	 
	 /**
	  * 设置原点Y坐标
	  * */
	 public void setYPos(short yp){
		 if(0 > yp){
			 Log.e("SynamicRectInfo","setYPos: Invalid yp: "+yp);
			 return;
		 }
		 this.nYPos = yp;
	 }
	 
	 /**
	  * 获得原点Y坐标
	  * */
	 public short getYPos(){
		 return this.nYPos;
	 }
	 
	 /**
	  * 设置控件初始宽度
	  * */
	 public void setWidth(short width){
		 if(0 > width){
			 Log.e("SynamicRectInfo","setWidth: Invalid width: "+width);
			 return;
		 }
		 
		 this.nWidth = width;
	 }
	 
	 /**
	  * 获得控件宽度
	  * */
	 public short getWidth(){
		 return this.nWidth;
	 }
	 
	 /**
	  * 设置控件初始高度
	  * */
	 public void setHeight(short height){
		 if(0 > height){
			 Log.e("SynamicRectInfo","setHeight: Invalid height: "+height);
			 return;
		 }
		 
		 this.nHeight = height;
	 }
	 
	 /**
	  * 获得控件高度
	  * */
	 public short getHeight(){
		 return this.nHeight;
	 }
	 
	 /**
	  * 设置矩形填充颜色
	  * */
	 public void setFillColor(int  color){
		 this.nFillColor = color;
	 }
	 
	 /**
	  * 获得矩形填充颜色
	  * */
	 public int getFillColor(){
		 return this.nFillColor;
	 }
	 
	 /**
	  * 设置矩形边框填充颜色
	  * */
	 public void setRimColor(int  color){
		 this.nRimColor = color;
	 }
	 
	 /**
	  * 获得矩形边框填充颜色
	  * */
	 public int getRimColor(){
		 return this.nRimColor;
	 }
	 
	 /**
	  * 设置边框宽度
	  * */
	 public void setRimWidh(short rwidth){
		 this.nRimWidth = rwidth;
	 }
	 /**
	  * 获得边框宽度
	  * */
	 public short getRimWidth(){
		 return this.nRimWidth;
	 }
	 
	 /**
	  * 设置位置是否受地址控制
	  * */
	 public void setUsePosCtrl(short isctrl){
		 this.nUsePosCtrl =  isctrl;
	 }
	 
	 /**
	  * 获得位置是否受地址控制
	  * */
	 public short getUsePosCtrl(){
		 return this.nUsePosCtrl;
	 }
	 
	 /**
	  * 设置大小是否受地址控制
	  * */
	 public void setUseSizeCtrl(short isctrl){
		 this.nUseSizeCtrl =  isctrl;
	 }
	 
	 /**
	  * 获得大小是否受地址控制
	  * */
	 public short getUseSizeCtrl(){
		 return this.nUseSizeCtrl;
	 }
	 
	 /**
	  * 设置是否填充
	  * */
	 public void setUseFill(short isfill){
		 this.nUseFill =  isfill;
	 }
	 
	 /**
	  * 获得获得填充属性
	  * */
	 public short getUseFill(){
		 return this.nUseFill;
	 }
	 
	  
	 /**
	  * 设置X坐标控制地址
	  * */
	 public void setXDataAddr(AddrProp addrprop){
		 if(null ==  addrprop){
			 Log.e("DynamicRectInfo","setXDataAddr: addrprop is null");
			 return;
		 }
		 this.mXDataAddr = addrprop;
	 }
	 
	 /**
	  * 获得X坐标控制地址
	  * */
	 public AddrProp getXDataAddr(){
		 return this.mXDataAddr;
	 }
	 
	 /**
	  * 设置Y坐标控制地址
	  * */
	 public void setYDataAddr(AddrProp addrprop){
		 if(null ==  addrprop){
			 Log.e("DynamicRectInfo","setYDataAddr: addrprop is null");
			 return;
		 }
		 this.mYDataAddr = addrprop;
	 }
	 
	 /**
	  * 获得Y坐标控制地址
	  * */
	 public AddrProp getYDataAddr(){
		 return this.mYDataAddr;
	 }
	 
	 /**
	  * 设定矩形大小变化参考点
	  * */
	 public void setRefType(short rtype){
		 if( (0 != rtype) && (1 != rtype) && (2 != rtype) && (3 != rtype)){
			 Log.e("DynamicRectInfo","setRefType: invalid reftype:"+rtype);
			 return;
		 }
		 this.nRefType = rtype;
	 }
	 
	 /**
	  * 获得矩形大小变化参考点
	  */
	 public short getRefType(){
		 return this.nRefType;
	 }
	 
	 /**
	  * 设置矩形宽度控制地址
	  * */
	 public void setWDataAddr(AddrProp addrprop){
		 if(null ==  addrprop){
			 Log.e("DynamicRectInfo","setWDataAddr: addrprop is null");
			 return;
		 }
		 this.mWDataAddr = addrprop;
	 }
	 
	 /**
	  * 获得矩形宽度控制地址
	  * */
	 public AddrProp getWDataAddr(){
		 return this.mWDataAddr;
	 }
	 
	 /**
	  * 设置矩形高度控制地址
	  * */
	 public void setHDataAddr(AddrProp addrprop){
		 if(null ==  addrprop){
			 Log.e("DynamicRectInfo","setHDataAddr: addrprop is null");
			 return;
		 }
		 this.mHDataAddr = addrprop;
	 }
	 
	 /**
	  * 获得矩形高度控制地址
	  * */
	 public AddrProp getHDataAddr(){
		 return this.mHDataAddr;
	 }
	 
	 //保留代码不可删除
//	 /**
//	  * 设置区域原点X坐标
//	  * */
//	 public void setAreaLp(short alp){
//		 if(0 > alp){
//			 Log.e("SynamicRectInfo","setAreaLp: Invalid area lp: "+alp);
//			 return;
//		 }
//		 this.nAreaLp = alp;
//	 }
//	 
//	 /**
//	  * 获得区域原点X坐标
//	  * */
//	 public short getAreaLp(){
//		 return this.nAreaLp;
//	 }
//	 
//	 /**
//	  * 设置区域原点Y坐标
//	  * */
//	 public void setAreaTp(short atp){
//		 if(0 > atp){
//			 Log.e("SynamicRectInfo","setAreaTp: Invalid area tp: "+atp);
//			 return;
//		 }
//		 this.nAreaTp = atp;
//	 }
//	 
//	 /**
//	  * 获得区域原点Y坐标
//	  * */
//	 public short getAreaTp(){
//		 return this.nAreaTp;
//	 }
//	 
//	 /**
//	  * 设置区域宽度
//	  * */
//	 public void setAreaWidth(short awidth){
//		 if(0 > awidth){
//			 Log.e("SynamicRectInfo","setAreaWidth: Invalid area width: "+awidth);
//			 return;
//		 }
//		 
//		 this.nAreaWidth = awidth;
//	 }
//	 
//	 /**
//	  * 获得区域宽度
//	  * */
//	 public short getAreaWidth(){
//		 return this.nAreaWidth;
//	 }
//	 
//	 /**
//	  * 设置区域高度
//	  * */
//	 public void setAreaHeight(short aheight){
//		 if(0 > aheight){
//			 Log.e("SynamicRectInfo","setAreaHeight: Invalid area height: "+aheight);
//			 return;
//		 }
//		 
//		 this.nAreaHeight = aheight;
//	 }
//	 
//	 /**
//	  * 获得区域高度
//	  * */
//	 public short getAreaHeight(){
//		 return this.nAreaHeight;
//	 }
//	 
//	 /**
//	  * 设置区域背景色
//	  * */
//	 public void setAreaColor(int acolor){
//		 this.nAreaColor = acolor;
//	 }
//	 
//	 /**
//	  * 获得区域背景色
//	  * */
//	 public int getAreaColor(){
//		 return this.nAreaColor;
//	 }
	 
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
	 
}//End of class
