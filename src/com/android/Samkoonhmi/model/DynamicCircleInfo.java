package com.android.Samkoonhmi.model;

import android.util.Log;

import com.android.Samkoonhmi.util.AddrProp;

/**
 * 动态圆形的数据实体类
 * 
 * @author 魏 科
 * @date 2012-06-14
 * */
public class DynamicCircleInfo {

	private int nItemId;
	private short nZvalue; // 控件层序号
	private short nCpXpos; // 圆心X坐标
	private short nCpYpos; // 圆心Y坐标
	private short nRadius; // 圆心半径

	private short nUseFill; // 是否填充
	private int nFillColor; // 圆形填充色

	private int nRimColor; // 边框填充色
	private short nRimWidth; // 边框宽度

	private short nAlpha; // 透明度

	private short nUsePosCtrl;// 位置是否受到地址控制
	private AddrProp mCpXDataAddr; // 圆心X坐标数据控制地址
	private AddrProp mCpYDataAddr; // 圆心Y坐标数据控制地址

	private short nUseSizeCtrl; // 大小是否受地址控制
	private AddrProp mRadiusDataAddr; // 圆心半径数据控制地址

	private int nCollidindId; // 组合ID

	// 显现
	private ShowInfo mShowInfo; // 显现属性

	// 保留代码，不可删除
	// private short nAreaLp; //区域原点X坐标
	// private short nAreaTp; //区域原点Y坐标
	// private short nAreaWidth; //区域宽度
	// private short nAreaHeight; //区域高度
	// private int nAreaColor; //区域背景色

	/**
	 * 设置圆心X坐标信息
	 * */
	public void setCpXpos(short xpos) {
		if (0 > xpos) {
			Log.e("SynamicCircleInfo", "setCpXpos: invalid xpos: " + xpos);
			return;
		}
		this.nCpXpos = xpos;
	}

	/**
	 * 获得圆心X坐标
	 * */
	public short getCpXpos() {
		return this.nCpXpos;
	}

	/**
	 * 设置圆心Y坐标信息
	 * */
	public void setCpYpos(short ypos) {
		if (0 > ypos) {
			Log.e("SynamicCircleInfo", "setCpYpos: invalid ypos: " + ypos);
			return;
		}
		this.nCpYpos = ypos;
	}

	/**
	 * 获得圆心Y坐标
	 * */
	public short getCpYpos() {
		return this.nCpYpos;
	}

	/**
	 * 设置圆形半径
	 * */
	public void setRadius(short radius) {
		if (0 > radius) {
			Log.e("SynamicCircleInfo", "setRadius: invalid radius: " + radius);
			return;
		}
		this.nRadius = radius;
	}

	/**
	 * 获得圆形半径
	 * */
	public short getRadius() {
		return this.nRadius;
	}

	/**
	 * 设置矩形填充颜色
	 * */
	public void setFillColor(int color) {
		this.nFillColor = color;
	}

	/**
	 * 获得圆形填充颜色
	 * */
	public int getFillColor() {
		return this.nFillColor;
	}

	/**
	 * 设置边框填充颜色
	 * */
	public void setRimColor(int color) {
		this.nRimColor = color;
	}

	/**
	 * 获得矩形边框填充颜色
	 * */
	public int getRimColor() {
		return this.nRimColor;
	}

	/**
	 * 设置边框宽度
	 * */
	public void setRimWidh(short rwidth) {
		this.nRimWidth = rwidth;
	}

	/**
	 * 获得边框宽度
	 * */
	public short getRimWidth() {
		return this.nRimWidth;
	}

	/**
	 * 设置透明度
	 * */
	public void setAlpha(short a) {
		this.nAlpha = a;
	}

	/**
	 * 获得透明度
	 * */
	public short getAlpha() {
		return this.nAlpha;
	}

	/**
	 * 设置圆心X坐标数据控制地址
	 * */
	public void setCpXDataAddr(AddrProp addrprop) {
		if (null == addrprop) {
			Log.e("SynamicCircleInfo", "setCpXDataAddr: addrprop is null");
			return;
		}
		this.mCpXDataAddr = addrprop;
	}

	/**
	 * 获得圆心X坐标数据控制地址
	 * */
	public AddrProp getCpXDataAddr() {
		return this.mCpXDataAddr;
	}

	/**
	 * 设置圆心Y坐标数据控制地址
	 * */
	public void setCpYDataAddr(AddrProp addrprop) {
		if (null == addrprop) {
			Log.e("SynamicCircleInfo", "setCpYDataAddr: addrprop is null");
			return;
		}
		this.mCpYDataAddr = addrprop;
	}

	/**
	 * 获得圆心Y坐标数据控制地址
	 * */
	public AddrProp getCpYDataAddr() {
		return this.mCpYDataAddr;
	}

	/**
	 * 设置圆心半径数据控制地址
	 * */
	public void setRadiusDataAddr(AddrProp addrprop) {
		if (null == addrprop) {
			Log.e("SynamicCircleInfo", "setRadiusDataAddr: addrprop is null");
			return;
		}
		this.mRadiusDataAddr = addrprop;
	}

	/**
	 * 获得圆心半径数据控制地址
	 * */
	public AddrProp getRadiusDataAddr() {
		return this.mRadiusDataAddr;
	}

	// 保留代码，不可删除
	// /**
	// * 设置区域原点X坐标
	// * */
	// public void setAreaLp(short alp){
	// if(0 > alp){
	// Log.e("SynamicRectInfo","setAreaLp: Invalid area lp: "+alp);
	// return;
	// }
	// this.nAreaLp = alp;
	// }
	//
	// /**
	// * 获得区域原点X坐标
	// * */
	// public short getAreaLp(){
	// return this.nAreaLp;
	// }
	//
	// /**
	// * 设置区域原点Y坐标
	// * */
	// public void setAreaTp(short atp){
	// if(0 > atp){
	// Log.e("SynamicRectInfo","setAreaTp: Invalid area tp: "+atp);
	// return;
	// }
	// this.nAreaTp = atp;
	// }
	//
	// /**
	// * 获得区域原点Y坐标
	// * */
	// public short getAreaTp(){
	// return this.nAreaTp;
	// }
	//
	// /**
	// * 设置区域宽度
	// * */
	// public void setAreaWidth(short awidth){
	// if(0 > awidth){
	// Log.e("SynamicRectInfo","setAreaWidth: Invalid area width: "+awidth);
	// return;
	// }
	//
	// this.nAreaWidth = awidth;
	// }
	//
	// /**
	// * 获得区域宽度
	// * */
	// public short getAreaWidth(){
	// return this.nAreaWidth;
	// }
	//
	// /**
	// * 设置区域高度
	// * */
	// public void setAreaHeight(short aheight){
	// if(0 > aheight){
	// Log.e("SynamicRectInfo","setAreaHeight: Invalid area height: "+aheight);
	// return;
	// }
	//
	// this.nAreaHeight = aheight;
	// }
	//
	// /**
	// * 获得区域高度
	// * */
	// public short getAreaHeight(){
	// return this.nAreaHeight;
	// }
	//
	// /**
	// * 设置区域背景色
	// * */
	// public void setAreaColor(int acolor){
	// this.nAreaColor = acolor;
	// }
	//
	// /**
	// * 获得区域背景色
	// * */
	// public int getAreaColor(){
	// return this.nAreaColor;
	// }

	/**
	 * 设置位置是否受地址控制
	 * */
	public void setUsePosCtrl(short isctrl) {
		this.nUsePosCtrl = isctrl;
	}

	/**
	 * 获得位置是否受地址控制
	 * */
	public short getUsePosCtrl() {
		return this.nUsePosCtrl;
	}

	/**
	 * 设置大小是否受地址控制
	 * */
	public void setUseSizeCtrl(short isctrl) {
		this.nUseSizeCtrl = isctrl;
	}

	/**
	 * 获得大小是否受地址控制
	 * */
	public short getUseSizeCtrl() {
		return this.nUseSizeCtrl;
	}

	/**
	 * 设置是否填充
	 * */
	public void setUseFill(short isfill) {
		this.nUseFill = isfill;
	}

	/**
	 * 获得获得填充属性
	 * */
	public short getUseFill() {
		return this.nUseFill;
	}

	/**
	 * 设置层序号
	 * */
	public void setZvalue(short zvalue) {
		this.nZvalue = zvalue;
	}

	/**
	 * 获得层序号
	 * */
	public short getZValue() {
		return nZvalue;
	}

	/**
	 * 设置组合ID
	 * */
	public void setCollidindId(int cid) {
		this.nCollidindId = cid;
	}

	/**
	 * 获得组合ID
	 * */
	public int getCollidindId() {
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
}// End of class
