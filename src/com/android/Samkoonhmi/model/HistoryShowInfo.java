package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 历史数据显示器
 * @author 刘伟江
 * @version v 1.0.0.2
 */
public class HistoryShowInfo {

	//组号id，采集组号Id
	private int nGroupId;
	//控件id
	private int nItemId;
	//前景色
	private int nForecolor;
	// 边框颜色
	private int nFrameColor;
	// 标题栏,字体大小 默认10
	private ArrayList<Integer> mTitleFontSizes;
	//标题字体类型
	private ArrayList<String> mTitleFontType;
	//标题背景颜色
	private int nTitleBackColor;
	//标题字体颜色
	private int nTitleFontColor;
	//透明度
	private short nAlpha;
	//序号
	private ArrayList<String> mTitleNum;
	//标题列时间名称
	private ArrayList<String> mTitleTimeNames;
	//标题列日期名称
	private ArrayList<String> mTitleDateNames;
	//是否显示时间
	private boolean bShowTime;
	// 是否显示日期
	private boolean bShowDate;
	//时间格式
	private TIME_FORMAT eTimeFormat;
	//日期格式
	private DATE_FORMAT eDateFormat;
	//是否显示序号
	private boolean bShowCode;
	//是否启用控制地址
	private boolean bControl;
	//控制地址
	private AddrProp mControlAddr;
	//文本字体大小
	private short nTextFontSize;
	//文本字体颜色
	private int nTextFontColor;
	//显示行数
	private short nLine;
	// 控件左上角X坐标
	private short nLeftTopX;
	// 控件左上角Y坐标
	private short nLeftTopY;
	// 控件宽
	private short nWidth;
	// 控件高
	private short nHeight;
	//层id
	private short nZvalue;
	//组合id
	private short nCollidindId;
	//数据列集合
	private ArrayList<HistoryShowDataInfo> mDataList;
	//显现
	private ShowInfo mShowInfo;
	//触控
	private TouchInfo mTouchInfo;
	//行的宽
	private ArrayList<Double> mRowWidht;
	//行的高
	private ArrayList<Double> mRowHeight;
	
	public short getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(short nAlpha) {
		this.nAlpha = nAlpha;
	}
	
	public ArrayList<String> getmTitleNum() {
		return mTitleNum;
	}
	public void setmTitleNum(ArrayList<String> mTitleNum) {
		this.mTitleNum = mTitleNum;
	}
	public ArrayList<Double> getmRowWidht() {
		return mRowWidht;
	}
	public void setmRowWidht(ArrayList<Double> mRowWidht) {
		this.mRowWidht = mRowWidht;
	}
	public ArrayList<Double> getmRowHeight() {
		return mRowHeight;
	}
	public void setmRowHeight(ArrayList<Double> mRowHeight) {
		this.mRowHeight = mRowHeight;
	}
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}
	public TouchInfo getmTouchInfo() {
		return mTouchInfo;
	}
	public void setmTouchInfo(TouchInfo mTouchInfo) {
		this.mTouchInfo = mTouchInfo;
	}
	public int getnGroupId() {
		return nGroupId;
	}
	public void setnGroupId(int nGroupId) {
		this.nGroupId = nGroupId;
	}
	public ArrayList<HistoryShowDataInfo> getmDataList() {
		return mDataList;
	}
	public void setmDataList(ArrayList<HistoryShowDataInfo> mDataList) {
		this.mDataList = mDataList;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public int getnForecolor() {
		return nForecolor;
	}
	public void setnForecolor(int nForecolor) {
		this.nForecolor = nForecolor;
	}
	public int getnFrameColor() {
		return nFrameColor;
	}
	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	public int getnTitleBackColor() {
		return nTitleBackColor;
	}
	public void setnTitleBackColor(int nTitleBackColor) {
		this.nTitleBackColor = nTitleBackColor;
	}
	public int getnTitleFontColor() {
		return nTitleFontColor;
	}
	public void setnTitleFontColor(int nTitleFontColor) {
		this.nTitleFontColor = nTitleFontColor;
	}
	public boolean isbShowTime() {
		return bShowTime;
	}
	public void setbShowTime(boolean bShowTime) {
		this.bShowTime = bShowTime;
	}
	public boolean isbShowDate() {
		return bShowDate;
	}
	public void setbShowDate(boolean bShowDate) {
		this.bShowDate = bShowDate;
	}
	public TIME_FORMAT geteTimeFormat() {
		return eTimeFormat;
	}
	public void seteTimeFormat(TIME_FORMAT eTimeFormat) {
		this.eTimeFormat = eTimeFormat;
	}
	public DATE_FORMAT geteDateFormat() {
		return eDateFormat;
	}
	public void seteDateFormat(DATE_FORMAT eDateFormat) {
		this.eDateFormat = eDateFormat;
	}
	public boolean isbShowCode() {
		return bShowCode;
	}
	public void setbShowCode(boolean bShowCode) {
		this.bShowCode = bShowCode;
	}
	public boolean isbControl() {
		return bControl;
	}
	public void setbControl(boolean bControl) {
		this.bControl = bControl;
	}
	public AddrProp getmControlAddr() {
		return mControlAddr;
	}
	public void setmControlAddr(AddrProp mControlAddr) {
		this.mControlAddr = mControlAddr;
	}
	public short getnTextFontSize() {
		return nTextFontSize;
	}
	public void setnTextFontSize(short nTextFontSize) {
		this.nTextFontSize = nTextFontSize;
	}
	public int getnTextFontColor() {
		return nTextFontColor;
	}
	public void setnTextFontColor(int nTextFontColor) {
		this.nTextFontColor = nTextFontColor;
	}
	public short getnLine() {
		return nLine;
	}
	public void setnLine(short nLine) {
		this.nLine = nLine;
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
	public ArrayList<Integer> getmTitleFontSizes() {
		return mTitleFontSizes;
	}
	public void setmTitleFontSizes(ArrayList<Integer> mTitleFontSizes) {
		this.mTitleFontSizes = mTitleFontSizes;
	}
	public ArrayList<String> getmTitleFontType() {
		return mTitleFontType;
	}
	public void setmTitleFontType(ArrayList<String> mTitleFontType) {
		this.mTitleFontType = mTitleFontType;
	}
	public ArrayList<String> getmTitleTimeNames() {
		return mTitleTimeNames;
	}
	public void setmTitleTimeNames(ArrayList<String> mTitleTimeNames) {
		this.mTitleTimeNames = mTitleTimeNames;
	}
	public ArrayList<String> getmTitleDateNames() {
		return mTitleDateNames;
	}
	public void setmTitleDateNames(ArrayList<String> mTitleDateNames) {
		this.mTitleDateNames = mTitleDateNames;
	}
}
