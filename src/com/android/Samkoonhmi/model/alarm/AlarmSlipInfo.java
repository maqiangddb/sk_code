package com.android.Samkoonhmi.model.alarm;

import java.util.ArrayList;

import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.skenum.ARRAY_ORDER;

/**
 * 动态报警条实体类
 * @author 刘伟江
 * @version V 1.0.0.2
 * 创建时间 2012-5-4
 * 最后修改时间 2012-5-4
 */
public class AlarmSlipInfo {

	//控件Id
	private int nItemId;
	//层id
	private short nZvalue;
	//控件组合Id
	private short nCollidindId;
	//样式
	private short nStyle;
	//外形
	private int nApperIndex;
	//文本背景颜色
	private int nBackcolor;
	//边框颜色
	private int nFrameColor;
	//前景颜色
	private int nForecolor;
	//文本字体大小,默认10
	private short nFontSize;
	//文本字体颜色,默认黑色
	private int nTextColor;
	//是否显示所有报警内容,默认true
	private boolean bSelectall;
	// 要显示的组
	private ArrayList<String> mGroupId;
//	//显示报警范围下限
//	private short nRangLow;
//	//显示报警范围上限
//	private short nRangHigh;
	//移动速度,默认1
	private short nSpeed;
	//移动方向,默认向左
	private ARRAY_ORDER eDirection;
	//报警排序,默认按顺时针顺序
	private ARRAY_ORDER eSort;
	//控件左上角x坐标
	private short nLeftTopX;
	//控件左上角y坐标
	private short nLeftTopY;
	//控件宽度
	private short nWidth;
	//控件高度
	private short nHeight;
	//文本左边顶点X坐标
	private short nTextLeftTopX;
	//文本左边顶点Y坐标
	private short nTextLeftTopY;
	//文本宽
	private short nTextWidth;
	//文本高
	private short nTextHeight;
	// 显现
	private ShowInfo mShowInfo;
	
	public int getnFrameColor() {
		return nFrameColor;
	}
	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	public int getnForecolor() {
		return nForecolor;
	}
	public void setnForecolor(int nForecolor) {
		this.nForecolor = nForecolor;
	}
	public short getnStyle() {
		return nStyle;
	}
	public void setnStyle(short nStyle) {
		this.nStyle = nStyle;
	}
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public short getnZvalue() {
		return nZvalue;
	}
	public void setnZvalue(short nZvalue) {
		this.nZvalue = nZvalue;
	}
	public short getnCollidindId() {
		return nCollidindId;
	}
	public void setnCollidindId(short nCollidindId) {
		this.nCollidindId = nCollidindId;
	}
	
	public int getnBackcolor() {
		return nBackcolor;
	}
	public void setnBackcolor(int nBackcolor) {
		this.nBackcolor = nBackcolor;
	}
	public short getnFontSize() {
		return nFontSize;
	}
	public void setnFontSize(short nFontSize) {
		this.nFontSize = nFontSize;
	}
	public int getnTextColor() {
		return nTextColor;
	}
	public void setnTextColor(int white) {
		this.nTextColor = white;
	}
	public boolean isbSelectall() {
		return bSelectall;
	}
	public void setbSelectall(boolean bSelectall) {
		this.bSelectall = bSelectall;
	}
//	public short getnRangLow() {
//		return nRangLow;
//	}
//	public void setnRangLow(short nRangLow) {
//		this.nRangLow = nRangLow;
//	}
//	public short getnRangHigh() {
//		return nRangHigh;
//	}
//	public void setnRangHigh(short nRangHigh) {
//		this.nRangHigh = nRangHigh;
//	}
	public short getnSpeed() {
		return nSpeed;
	}
	public void setnSpeed(short nSpeed) {
		this.nSpeed = nSpeed;
	}
	public ARRAY_ORDER geteDirection() {
		return eDirection;
	}
	public void seteDirection(ARRAY_ORDER eDirection) {
		this.eDirection = eDirection;
	}
	public ARRAY_ORDER geteSort() {
		return eSort;
	}
	public void seteSort(ARRAY_ORDER eSort) {
		this.eSort = eSort;
	}
	public short getnLeftTopX() {
		return nLeftTopX;
	}
	public void setnLeftTopX(short nLeftTopX) {
		this.nLeftTopX = nLeftTopX;
	}
	public short getnLeftTopY() {
		return nLeftTopY;
	}
	public void setnLeftTopY(short nLeftTopY) {
		this.nLeftTopY = nLeftTopY;
	}
	public short getnWidth() {
		return nWidth;
	}
	public void setnWidth(short nWidth) {
		this.nWidth = nWidth;
	}
	public short getnHeight() {
		return nHeight;
	}
	public void setnHeight(short nHeight) {
		this.nHeight = nHeight;
	}
	public short getnTextLeftTopX() {
		return nTextLeftTopX;
	}
	public void setnTextLeftTopX(short nTextLeftTopX) {
		this.nTextLeftTopX = nTextLeftTopX;
	}
	public short getnTextLeftTopY() {
		return nTextLeftTopY;
	}
	public void setnTextLeftTopY(short nTextLeftTopY) {
		this.nTextLeftTopY = nTextLeftTopY;
	}
	public short getnTextWidth() {
		return nTextWidth;
	}
	public void setnTextWidth(short nTextWidth) {
		this.nTextWidth = nTextWidth;
	}
	public short getnTextHeight() {
		return nTextHeight;
	}
	public void setnTextHeight(short nTextHeight) {
		this.nTextHeight = nTextHeight;
	}
	public int getnApperIndex() {
		return nApperIndex;
	}
	public void setnApperIndex(int nApperIndex) {
		this.nApperIndex = nApperIndex;
	}
	public ArrayList<String> getmGroupId() {
		return mGroupId;
	}
	public void setmGroupId(ArrayList<String> mGroupId) {
		this.mGroupId = mGroupId;
	}
}
