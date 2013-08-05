package com.android.Samkoonhmi.model.sk_historytrends;

import java.util.List;

import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skenum.CURVE_TYPE;
import com.android.Samkoonhmi.skenum.TIMERANGE_TYPE;

/**
 * 曲线
 * @author 李镇
 * @version v 1.0.0.1
 * 创建时间：2012-4-24
 * 最后修改时间 2012-4-24
 */
public class HistoryTrendsInfo {
	
	private int sId;	//控件Id
	private	short nLp;	//控件左上角X坐标	 
	private short nTp;	//控件左上角Y坐标	 
	private short nWidth;	//控件宽度	 
	private short nHeight;	//控件高度	 
	private	short nCurveX;	//控件曲线区域左上角X坐标	 
	private short nCurveY;	//控件曲线区域左上角Y坐标	 
	private short nCurveWd;	//控件曲线区域宽度	 
	private short nCurveHt;	//控件曲线区域高度	 
	
	private CURVE_TYPE nCurveType;	//图表类型	 实时 历史	
	private short nGroupNum;	//组号	 	
	private short nChannelNum;	//通道数	 	
	private float nDisplayMin;	//显示符号最小值	 	
	private float nDisplayMax;	//显示符号最大值	 	

	private List<ChannelGroupInfo> channelGroups;
	
	private short nDataSample;	//数据样本数	 	
	private short nScrollSample;	//滚动样本数	 	
	private short nVertMajorScale;	//垂直主标尺	 	
	private boolean bSelectVertMinor;	//是否显示垂直次标尺	 	
	private short nVertMinorScale;	//垂直次标尺	 	
	private short nHorMajorScale;	//水平主标尺	 	
	private boolean bSelectHorMinor;	//是否显示水平次标尺	 	
	private short nHorMinorScale;	//水平次标尺	 	

	private int nBoradColor;	//边框颜色	 
	private int nScaleColor;	//标尺颜色	 
	private int nGraphColor;	//图表区颜色	 
	private boolean bSelectNet;	//是否显示网格
	private int nVertNetColor;	//垂直网格颜色
	private int nHorNetColor;	//水平网格颜色

	private int nMarkColor;	//下标文字颜色	
	private boolean	bXmark;	//是否显示X轴时间
	private TIMERANGE_TYPE nTimeRange;	//曲线时间范围选择	
	private TIMERANGE_TYPE nOldTimerange;
	
	private short nRecentYear;	//最近多少年的采样变化		
	private short nRecentMonth;	//最近多少个月的采样变化		
	private short nRecentDay;	//最近多少天的采样变化
	private short nRecentHour;	//最近多少小时的变化
	private short nRecentMinute;//最近多少分钟的采样变化
  
	private short nStartYear;	//开始时间-年		
	private short nStartMonth;	//开始时间-月		
	private short nStartDay;	//开始时间-日	
	private short nStartHour;	//开始时间-小时	
	private short nStartMinute;	//开始时间-分钟		
	private short nEndYear;	//结束时间-年		
	private short nEndMonth;	//结束时间-月		
	private short nEndDay;	//结束时间-日	
	private short nEndHour;	//结束时间-小时	
	private short nEndMinute;	//结束时间-分钟		

	private String sFontType;	//字体类型	 
	private short nFontSize;	//字体大小	 
//	private short nFontAttri;	//文本属性	 
	private DATE_FORMAT nDate;	//日期	 
	private TIME_FORMAT nTime;	//时间	 
	private int nZvalue;
	private int nCollidindId;
	private ShowInfo mShowInfo;
	private short nCurveAlpha;	//透明度	
	private boolean bMainVer;//垂直
	private boolean bMainHor;//水平
	private boolean bShowData=true;//显示日期
	private boolean bShowTime =true;//显示时间

	public boolean isbShowData() {
		return bShowData;
	}
	public void setbShowData(boolean bShowData) {
		this.bShowData = bShowData;
	}
	public boolean isbShowTime() {
		return bShowTime;
	}
	public void setbShowTime(boolean bShowTime) {
		this.bShowTime = bShowTime;
	}
	public boolean isbMainVer() {
		return bMainVer;
	}
	public void setbMainVer(boolean bMainVer) {
		this.bMainVer = bMainVer;
	}
	public boolean isbMainHor() {
		return bMainHor;
	}
	public void setbMainHor(boolean bMainHor) {
		this.bMainHor = bMainHor;
	}
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}
	public int getId() {   //id
		return sId;
	}
	public void setId(int sId) {
		this.sId = sId;
	}

	public short getnLp() {   //nlp
		return nLp;
	}
	public void setnLp(short nLp) {
		this.nLp = nLp;
	}
		
	public short getnTp() {  //nTp
		return nTp;
	}
	public void setnTp(short nTp) { 
		this.nTp = nTp;
	}

	public short getnWidth() { //nWidth
		return nWidth;
	}
	public void setnWidth(short nWidth) {
		this.nWidth = nWidth;
	}

	public short getnHeight() { //nHeight
		return nHeight;
	}
	public void setnHeight(short nHeight) {
		this.nHeight = nHeight;
	}

	public short getnCurveX() {   //nCurveX
		return nCurveX;
	}
	public void setnCurveX(short nCurveX) {
		this.nCurveX = nCurveX;
	}
		
	public short getnCurveY() {  //nCurveY
		return nCurveY;
	}
	public void setnCurveY(short nCurveY) { 
		this.nCurveY = nCurveY;
	}

	public short getnCurveWd() { //nCurveWd
		return nCurveWd;
	}
	public void setnCurveWd(short nCurveWd) {
		this.nCurveWd = nCurveWd;
	}

	public short getnCurveHt() { //nCurveHt
		return nCurveHt;
	}
	public void setnCurveHt(short nCurveHt) {
		this.nCurveHt = nCurveHt;
	}
	
	public CURVE_TYPE getnCurveType() {  //nCurveType
		return nCurveType;
	}
	public void setnCurveType(CURVE_TYPE nCurveType) {
		this.nCurveType = nCurveType;
	}
	
	public short getnGroupNum() {   //nGroupNum
		return nGroupNum;
	}
	public void setnGroupNum(short nGroupNum) {
		this.nGroupNum = nGroupNum;
	}	

	public float getnDisplayMin() {
		return nDisplayMin;
	}
	public void setnDisplayMin(float nDisplayMin) {
		this.nDisplayMin = nDisplayMin;
	}

	public float getnDisplayMax() {
		return nDisplayMax;
	}
	public void setnDisplayMax(float nDisplayMax) {
		this.nDisplayMax = nDisplayMax;
	}
	
	public short getnChannelNum() {  //nChannelNum
		return nChannelNum;
	}
	public void setnChannelNum(short nChannelNum) {
		this.nChannelNum = nChannelNum;
	}	




	public List<ChannelGroupInfo> getchannelGroups() {
		return channelGroups;
	}
	public void setchannelGroups(List<ChannelGroupInfo> channelGroups) {
		this.channelGroups = channelGroups;
	}
	
	/**
	 * 数据样本数
	 */
	public short getnDataSample() {  //nDataSample
		return nDataSample;
	}
	
	/**
	 * 数据样本数
	 */
	public void setnDataSample(short nDataSample) {
		this.nDataSample = nDataSample;
	}
		
	public short getnScrollSample() {  //nScrollSample
		return nScrollSample;
	}
	public void setnScrollSample(short nScrollSample) {
		this.nScrollSample = nScrollSample;
	}

	public short getnVertMajorScale() {  //nVertMajorScale
		return nVertMajorScale;
	}
	public void setnVertMajorScale(short nVertMajorScale) {
		this.nVertMajorScale = nVertMajorScale;
	}

	public boolean getbSelectVertMinor() {   //bSelectVertMinor
		return bSelectVertMinor;
	}
	public void setbSelectVertMinor(boolean bSelectVertMinor) {
		this.bSelectVertMinor = bSelectVertMinor;
	}

	public short getnVertMinorScale() { //nVertMinorScale
		return nVertMinorScale;
	}
	public void setnVertMinorScale(short nVertMinorScale) {
		this.nVertMinorScale = nVertMinorScale;
	}
	
	public short getnHorMajorScale() {  //nHorMajorScale
		return nHorMajorScale;
	}
	public void setnHorMajorScale(short nHorMajorScale) {
		this.nHorMajorScale = nHorMajorScale;
	}	

	public boolean getbSelectHorMinor() {  //bSelectHorMinor
		return bSelectHorMinor;
	}
	public void setbSelectHorMinor(boolean bSelectHorMinor) {
		this.bSelectHorMinor = bSelectHorMinor;
	}	

	public short getnHorMinorScale() {  //nHorMinorScale
		return nHorMinorScale;
	}
	public void setnHorMinorScale(short nHorMinorScale) {
		this.nHorMinorScale = nHorMinorScale;
	}	
	
	public int getnBoradColor() {  //nBoradColor
		return nBoradColor;
	}
	public void setnBoradColor(int nBoradColor) {
		this.nBoradColor = nBoradColor;
	}	

	public int getnScaleColor() {  //nScaleColor
		return nScaleColor;
	}
	public void setnScaleColor(int nScaleColor) {
		this.nScaleColor = nScaleColor;
	}	

	public int getnGraphColor() {  //nGraphColor
		return nGraphColor;
	}
	public void setnGraphColor(int nGraphColor) {
		this.nGraphColor = nGraphColor;
	}	

	public boolean getbSelectNet() {  //bSelectNet
		return bSelectNet;
	}
	public void setbSelectNet(boolean bSelectNet) {
		this.bSelectNet = bSelectNet;
	}	

	public int getnVertNetColor() {  //nVertNetColor
		return nVertNetColor;
	}
	public void setnVertNetColor(int nVertNetColor) {
		this.nVertNetColor = nVertNetColor;
	}	

	public int getnHorNetColor() {  //nHorNetColor
		return nHorNetColor;
	}
	public void setnHorNetColor(int nHorNetColor) {
		this.nHorNetColor = nHorNetColor;
	}	

public int getnMarkColor() {  //nMarkColor
		return nMarkColor;
	}
	public void setnMarkColor(int nMarkColor) {
		this.nMarkColor = nMarkColor;
	}	

	public boolean getbXmark() {  //bXmark
		return bXmark;
	}
	public void setbXmark(boolean bXmark) {
		this.bXmark = bXmark;
	}	

	public TIMERANGE_TYPE getnTimeRange() {  //nTimeRange
		return nTimeRange;
	}
	public void setnTimeRange(TIMERANGE_TYPE nTimeRange) {
		this.nTimeRange = nTimeRange;
	}	

	public short getnRecentYear() { //nRecentYear
		return nRecentYear;
	}
	public void setnRecentYear(short nRecentYear) {
		this.nRecentYear = nRecentYear;
	}

	public short getnRecentMonth() { //nRecentMonth
		return nRecentMonth;
	}
	public void setnRecentMonth(short nRecentMonth) {
		this.nRecentMonth = nRecentMonth;
	}

	public short getnRecentDay() { //nRecentDay
		return nRecentDay;
	}
	public void setnRecentDay(short nRecentDay) {
		this.nRecentDay = nRecentDay;
	}

	public short getnRecentHour() { //nRecentHour
		return nRecentHour;
	}
	public void setnRecentHour(short nRecentHour) {
		this.nRecentHour = nRecentHour;
	}

	public short getnRecentMinute() { //nRecentMinute
		return nRecentMinute;
	}
	public void setnRecentMinute(short nRecentMinute) {
		this.nRecentMinute = nRecentMinute;
	}

	public short getnStartYear() { //nStartYear
		return nStartYear;
	}
	public void setnStartYear(short nStartYear) {
		this.nStartYear = nStartYear;
	}

	public short getnStartMonth() { //nStartMonth
		return nStartMonth;
	}
	public void setnStartMonth(short nStartMonth) {
		this.nStartMonth = nStartMonth;
	}

	public short getnStartDay() { //nStartDay
		return nStartDay;
	}
	public void setnStartDay(short nStartDay) {
		this.nStartDay = nStartDay;
	}

	public short getnStartHour() { //nStartHour
		return nStartHour;
	}
	public void setnStartHour(short nStartHour) {
		this.nStartHour = nStartHour;
	}

	public short getnStartMinute() { //nStartMinute
		return nStartMinute;
	}
	public void setnStartMinute(short nStartMinute) {
		this.nStartMinute = nStartMinute;
	}

	public short getnEndYear() { //nEndYear
		return nEndYear;
	}
	public void setnEndYear(short nEndYear) {
		this.nEndYear = nEndYear;
	}

	public short getnEndMonth() { //nEndMonth
		return nEndMonth;
	}
	public void setnEndMonth(short nEndMonth) {
		this.nEndMonth = nEndMonth;
	}

	public short getnEndDay() { //nEndDay
		return nEndDay;
	}
	public void setnEndDay(short nEndDay) {
		this.nEndDay = nEndDay;
	}

	public short getnEndHour() { //nEndHour
		return nEndHour;
	}
	public void setnEndHour(short nEndHour) {
		this.nEndHour = nEndHour;
	}

	public short getnEndMinute() { //nEndMinute
		return nEndMinute;
	}
	public void setnEndMinute(short nEndMinute) {
		this.nEndMinute = nEndMinute;
	}
	
	public String getsFontType() {  //nFontType
		return sFontType;
	}
	public void setsFontType(String sFontType) {
		this.sFontType = sFontType;
	}	

	public short getnFontSize() {  //nFontSize
		return nFontSize;
	}
	public void setnFontSize(short nFontSize) {
		this.nFontSize = nFontSize;
	}	
/*
	public short getnFontAttri() {  //nFontAttri
		return nFontAttri;
	}
	public void setnFontAttri(short nFontAttri) {
		this.nFontAttri = nFontAttri;
	}	
*/

	public DATE_FORMAT getnDate() {  //nDate
		return nDate;
	}
	public void setnDate(DATE_FORMAT nDate) {
		this.nDate = nDate;
	}	

	public TIME_FORMAT getnTime() {  //nTime
		return nTime;
	}
	public void setnTime(TIME_FORMAT nTime) {
		this.nTime = nTime;
	}
	
	public void setnZvalue(int nZvalue) {
		this.nZvalue = nZvalue;
	}	
	
	public int getnZvalue() {
		// TODO Auto-generated method stub
		return nZvalue;
	}
	
	public void setnCollidindId(int nCollidindId) {
		this.nCollidindId = nCollidindId;
	}
	public int getnCollidindId() {
		return nCollidindId;
	}	
	
	public short getnCurveAlpha() {
		return nCurveAlpha;
	}
	public void setnCurveAlpha(short nCurveAlpha) {
		this.nCurveAlpha = nCurveAlpha;
	}
	
	public TIMERANGE_TYPE getnOldTimerange() {
		return nOldTimerange;
	}
	public void setnOldTimerange(TIMERANGE_TYPE nOldTimerange) {
		this.nOldTimerange = nOldTimerange;
	}
}
