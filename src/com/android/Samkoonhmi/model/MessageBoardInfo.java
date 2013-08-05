package com.android.Samkoonhmi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;

/**
 * 留言板
 * @author quliping 
 *
 */
public class MessageBoardInfo {
	private int nItemId;//	编号	int 
	private int nStartX;//起点横坐标	short
	private int nStartY;//	起点纵坐标	short
	private int nWidth;//	宽度	short
	private int nHeight;//	高度	short
	private int nAlpha;//	控件透明度	Short
	private int nLineColor;//	边框颜色	Int
//	private CSS_TYPE nFillCss;//	填充样式	Short（枚举）
	private int nBackColor;//	背景颜色	int
	private int nInnerLineColor;//	内部线条色	Int
	private int nFontSize;//	内容字体大小	short
	private String sFontType;//	内容字体类型	varchar
	private int nFontColor;//	内容字体颜色	Int
	private int nTFontSize;//	表头字体大小	short
	private String sTFontType;//	表头字体类型	varchar
	private int nTFontColor;//	表头字体颜色	Int
	private int nTBackColor	;//表头背景颜色	
	private boolean bShowId;//	是否显示编号	Boolean
	private boolean bShowTime;//	是否显示时间	Boolean
	private TIME_FORMAT nTimeType;//	时间格式（枚举）	short
	private boolean bShowDate;//	是否显示日期	Boolean
	private DATE_FORMAT nDateType;//	日期格式（枚举）	Short
	private int nZvalue;//	层id	Int
	private int nCollidindId;//	组合id	Int
	private TouchInfo touchInfo;//触控信息
	private ShowInfo  showInfo;//显现信息
	private Map<Integer, String> sNumberNameList;//编号多语言列表
	private Map<Integer,String> sDateNameList;//日期多语言列表
	private Map<Integer,String> sTimeNameList ;// 时间多语言列表
	private Map<Integer,String> sMessageInfoList;//留言信息多语言列表
	//行的高
	private ArrayList<Double> mRowHeight;
	//行的宽
	private ArrayList<Double> mRowWidht;
	//多少行
	private int nRowCount;
	
	
	
	
	public int getnRowCount() {
		return nRowCount;
	}
	public void setnRowCount(int nRowCount) {
		this.nRowCount = nRowCount;
	}
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
	public Map<Integer, String> getsNumberNameList() {
		return sNumberNameList;
	}
	public void setsNumberNameList(Map<Integer, String> sNumberNameList) {
		this.sNumberNameList = sNumberNameList;
	}
	public Map<Integer, String> getsDateNameList() {
		return sDateNameList;
	}
	public void setsDateNameList(Map<Integer, String> sDateNameList) {
		this.sDateNameList = sDateNameList;
	}
	public Map<Integer, String> getsTimeNameList() {
		return sTimeNameList;
	}
	public void setsTimeNameList(Map<Integer, String> sTimeNameList) {
		this.sTimeNameList = sTimeNameList;
	}
	public Map<Integer, String> getsMessageInfoList() {
		return sMessageInfoList;
	}
	public void setsMessageInfoList(Map<Integer, String> sMessageInfoList) {
		this.sMessageInfoList = sMessageInfoList;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public int getnStartX() {
		return nStartX;
	}
	public void setnStartX(int nStartX) {
		this.nStartX = nStartX;
	}
	public int getnStartY() {
		return nStartY;
	}
	public void setnStartY(int nStartY) {
		this.nStartY = nStartY;
	}
	public int getnWidth() {
		return nWidth;
	}
	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}
	public int getnHeight() {
		return nHeight;
	}
	public void setnHeight(int nHeight) {
		this.nHeight = nHeight;
	}
	public int getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}
	public int getnLineColor() {
		return  nLineColor;
	}
	public void setnLineColor(int privatrnLineColor) {
		this.nLineColor = privatrnLineColor;
	}
//	public CSS_TYPE getnFillCss() {
//		return nFillCss;
//	}
//	public void setnFillCss(CSS_TYPE nFillCss) {
//		this.nFillCss = nFillCss;
//	}
	public int getnBackColor() {
		return nBackColor;
	}
	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}
	public int getnInnerLineColor() {
		return nInnerLineColor;
	}
	public void setnInnerLineColor(int nForeColor) {
		this.nInnerLineColor = nForeColor;
	}
	public int getnFontSize() {
		return nFontSize;
	}
	public void setnFontSize(int nFontSize) {
		this.nFontSize = nFontSize;
	}
	public String getsFontType() {
		return sFontType;
	}
	public void setsFontType(String sFontType) {
		this.sFontType = sFontType;
	}
	public int getnFontColor() {
		return nFontColor;
	}
	public void setnFontColor(int nFontColor) {
		this.nFontColor = nFontColor;
	}
	public int getnTFontSize() {
		return nTFontSize;
	}
	public void setnTFontSize(int nTFontSize) {
		this.nTFontSize = nTFontSize;
	}
	public String getsTFontType() {
		return sTFontType;
	}
	public void setsTFontType(String sTFontType) {
		this.sTFontType = sTFontType;
	}
	public int getnTFontColor() {
		return nTFontColor;
	}
	public void setnTFontColor(int nTFontColor) {
		this.nTFontColor = nTFontColor;
	}
	public int getnTBackColor() {
		return nTBackColor;
	}
	public void setnTBackColor(int nTBackColor) {
		this.nTBackColor = nTBackColor;
	}
	public boolean isbShowId() {
		return bShowId;
	}
	public void setbShowId(boolean bShowId) {
		this.bShowId = bShowId;
	}
	public boolean isbShowTime() {
		return bShowTime;
	}
	public void setbShowTime(boolean bShowTime) {
		this.bShowTime = bShowTime;
	}
	public TIME_FORMAT getnTimeType() {
		return nTimeType;
	}
	public void setnTimeType(TIME_FORMAT nTimeType) {
		this.nTimeType = nTimeType;
	}
	public boolean isbShowDate() {
		return bShowDate;
	}
	public void setbShowDate(boolean bShowDate) {
		this.bShowDate = bShowDate;
	}
	public DATE_FORMAT getnDateType() {
		return nDateType;
	}
	public void setnDateType(DATE_FORMAT nDateType) {
		this.nDateType = nDateType;
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
	
	public TouchInfo getTouchInfo() {
		return touchInfo;
	}
	public void setTouchInfo(TouchInfo touchInfo) {
		this.touchInfo = touchInfo;
	}
	public ShowInfo getShowInfo() {
		return showInfo;
	}
	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}
	
	


}
