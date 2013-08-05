package com.android.Samkoonhmi.model.alarm;

import java.util.ArrayList;

import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.TouchInfo;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;

/**
 * 报警控件显示器实体对象
 * @author 刘伟江
 * @version v 1.0.0.2
 * 创建时间 2012-5-7
 * 最后修改时间 2012-5-7
 */
public class AlarmContolInfo {

	//控件id
	private int nItemId;
	//边框的颜色,默认黑色
	private int nFrameColor;
	//框的背景颜色,默认灰色
	private int nTableColor;
	//标题栏,字体大小,默认10
	private ArrayList<Short> nTitleFontSizes;
	//标题栏,字体类型,默认宋体
	private ArrayList<String> sTitleFontTypes;
	//标题栏,背景颜色,默认黄色
	private int nTitleBackcolor;
	//标题栏,文本颜色,默认黑色
	private int nTitleColor;
	//是否显示时间
	private boolean bShowTime;
	//标题栏,时间列的名字
	private ArrayList<String> sTimeStrs;
	//时间格式
	private TIME_FORMAT eTimeFormat;
	//是否显示日期
	private boolean bShowDate;
	//标题栏,日期列的名字
	private ArrayList<String> sDateStrs;
	//日期格式
	private DATE_FORMAT eDateFormat;
	//消息列的名字
	private ArrayList<String> sMessages;
	//文本字体大小,默认10
	private short nFontSize;
	//文本颜色,默认黑色
	private int nTextColor;
	//表格行数
	private short nRowCount;
	//是否显示报警内容
	private boolean bShowwall;
	//要显示的组id
	private ArrayList<String> mGroupId;
//	//显示报警范围下限
//	private short nRangLow;
//	//显示报警范围上限
//	private short nRangHigh;
	//控件左上角X坐标
	private short nLeftTopX;
	//控件左上角Y坐标
	private short nLeftTopY;
	//控件宽
	private short nWidth;
	//控件高
	private short nHeight;
	//层id
	private int nZvalue;
	//组合id
	private int nCollidindId;
	//显隐
	private ShowInfo mShowInfo;
	//触控
	private TouchInfo mTouchInfo;
	//行的高
	private ArrayList<Double> mRowHeight;
	//行的宽
	private ArrayList<Double> mRowWidht;
	
	public ArrayList<Double> getmRowHeight() {
		return mRowHeight;
	}
	public void setmRowHeight(ArrayList<Double> mRowHeight) {
		this.mRowHeight = mRowHeight;
	}
	public ArrayList<Double> getmRowWidht() {
		return mRowWidht;
	}
	public void setmRowWidht(ArrayList<Double> mRowWidht) {
		this.mRowWidht = mRowWidht;
	}
	public TouchInfo getmTouchInfo() {
		return mTouchInfo;
	}
	public void setmTouchInfo(TouchInfo mTouchInfo) {
		this.mTouchInfo = mTouchInfo;
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
	public int getnZvalue() {
		return nZvalue;
	}
	public void setnZvalue(int nZvalue) {
		this.nZvalue = nZvalue;
	}
	public int getnCollidindId() {
		return nCollidindId;
	}
	public void setnCollidindId(int nCollidindId) {
		this.nCollidindId = nCollidindId;
	}
	public int getnFrameColor() {
		return nFrameColor;
	}
	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	public int getnTableColor() {
		return nTableColor;
	}
	public void setnTableColor(int nTableColor) {
		this.nTableColor = nTableColor;
	}
	public int getnTitleBackcolor() {
		return nTitleBackcolor;
	}
	public void setnTitleBackcolor(int nTitleBackcolor) {
		this.nTitleBackcolor = nTitleBackcolor;
	}
	public int getnTitleColor() {
		return nTitleColor;
	}
	public void setnTitleColor(int nTitleColor) {
		this.nTitleColor = nTitleColor;
	}
	public boolean isbShowTime() {
		return bShowTime;
	}
	public void setbShowTime(boolean bShowTime) {
		this.bShowTime = bShowTime;
	}
	public TIME_FORMAT geteTimeFormat() {
		return eTimeFormat;
	}
	public void seteTimeFormat(TIME_FORMAT eTimeFormat) {
		this.eTimeFormat = eTimeFormat;
	}
	public boolean isbShowDate() {
		return bShowDate;
	}
	public void setbShowDate(boolean bShowDate) {
		this.bShowDate = bShowDate;
	}
	public DATE_FORMAT geteDateFormat() {
		return eDateFormat;
	}
	public void seteDateFormat(DATE_FORMAT eDateFormat) {
		this.eDateFormat = eDateFormat;
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
	public void setnTextColor(int nTextColor) {
		this.nTextColor = nTextColor;
	}
	public short getnRowCount() {
		return nRowCount;
	}
	public void setnRowCount(short nRowCount) {
		this.nRowCount = nRowCount;
	}
	public boolean isbShowwall() {
		return bShowwall;
	}
	public void setbShowwall(boolean bShowwall) {
		this.bShowwall = bShowwall;
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
	public ArrayList<Short> getnTitleFontSizes() {
		return nTitleFontSizes;
	}
	public void setnTitleFontSizes(ArrayList<Short> nTitleFontSizes) {
		this.nTitleFontSizes = nTitleFontSizes;
	}
	public ArrayList<String> getsTitleFontTypes() {
		return sTitleFontTypes;
	}
	public void setsTitleFontTypes(ArrayList<String> sTitleFontTypes) {
		this.sTitleFontTypes = sTitleFontTypes;
	}
	public ArrayList<String> getsTimeStrs() {
		return sTimeStrs;
	}
	public void setsTimeStrs(ArrayList<String> sTimeStrs) {
		this.sTimeStrs = sTimeStrs;
	}
	public ArrayList<String> getsDateStrs() {
		return sDateStrs;
	}
	public void setsDateStrs(ArrayList<String> sDateStrs) {
		this.sDateStrs = sDateStrs;
	}
	public ArrayList<String> getsMessages() {
		return sMessages;
	}
	public void setsMessages(ArrayList<String> sMessages) {
		this.sMessages = sMessages;
	}
	public ArrayList<String> getmGroupId() {
		return mGroupId;
	}
	public void setmGroupId(ArrayList<String> mGroupId) {
		this.mGroupId = mGroupId;
	}
}
