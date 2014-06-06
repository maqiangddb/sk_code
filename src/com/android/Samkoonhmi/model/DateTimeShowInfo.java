package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skenum.WEEK_FORMAT;

/**
 * 
 * @author 瞿丽平
 *
 */
public class DateTimeShowInfo {
	private int Id;//编号	Varchar
	private  int nStartX;//	控件横坐标起点	Short
	private int nStartY;//	控件纵坐标起点	Short
	private int nWidth;//	控件宽度	Short
	private int nHeight;//	控件高度       	Short
	private int nTextStartX	;//文本区域起点横坐标	Short
	private int nTextStartY;//	文本区域起点纵坐标	Short
	private int nTextWidth;//	文本区域宽度	Short
	private int  nTextHeight;//	文本区域高度	Short
	private String nShapId	;//外形id	Varchar
	private String sFontStyle;//	字体类型	
	private int nFontSize;//	字体大小	Short
	private short eFontCss;//	字体属性 多选（标准，斜体，粗体，下划线）	Short(静态变量)
	private DATE_FORMAT eShowDate;//	日期	Short (枚举)
	private WEEK_FORMAT eShowWeek;//	星期	Short (枚举)
	private TIME_FORMAT eShowTime;//	时间	Short (枚举)
	private int nFontColor;//	字体颜色	Short
	private int nBackground	;//字体背景颜色	Short
	private int nZvalue ;
	private int nCollidindId ;
	private int nTransparent;//控件透明度
	private ShowInfo showInfo;
	
	
	public ShowInfo getShowInfo() {
		return showInfo;
	}
	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}
	public int getnTransparent() {
		return nTransparent;
	}
	public void setnTransparent(int nTransparent) {
		this.nTransparent = nTransparent;
	}
	public DateTimeShowInfo() {
		super();
	}
	public DateTimeShowInfo(int nStartX, int nStartY, int nWidth, int nHeight,
			int nTextStartX, int nTextStartY, int nTextWidth, int nTextHeight,
			String nShapId, String sFontStyle, int nFontSize, short eFontCss,
			DATE_FORMAT eShowDate, WEEK_FORMAT eShowWeek,
			TIME_FORMAT eShowTime, int nFontColor, int nBackground,int nZvalue,int nCollidindId) {
		super();
		this.nStartX = nStartX;
		this.nStartY = nStartY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nTextStartX = nTextStartX;
		this.nTextStartY = nTextStartY;
		this.nTextWidth = nTextWidth;
		this.nTextHeight = nTextHeight;
		this.nShapId = nShapId;
		this.sFontStyle = sFontStyle;
		this.nFontSize = nFontSize;
		this.eFontCss = eFontCss;
		this.eShowDate = eShowDate;
		this.eShowWeek = eShowWeek;
		this.eShowTime = eShowTime;
		this.nFontColor = nFontColor;
		this.nBackground = nBackground;
		this.nZvalue=nZvalue;
		this.nCollidindId=nCollidindId;
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
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
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
	public int getnTextStartX() {
		return nTextStartX;
	}
	public void setnTextStartX(int nTextStartX) {
		this.nTextStartX = nTextStartX;
	}
	public int getnTextStartY() {
		return nTextStartY;
	}
	public void setnTextStartY(int nTextStartY) {
		this.nTextStartY = nTextStartY;
	}
	public int getnTextWidth() {
		return nTextWidth;
	}
	public void setnTextWidth(int nTextWidth) {
		this.nTextWidth = nTextWidth;
	}
	public int getnTextHeight() {
		return nTextHeight;
	}
	public void setnTextHeight(int nTextHeight) {
		this.nTextHeight = nTextHeight;
	}
	public String getnShapId() {
		return nShapId;
	}
	public void setnShapId(String nShapId) {
		this.nShapId = nShapId;
	}
	public String getsFontStyle() {
		return sFontStyle;
	}
	public void setsFontStyle(String sFontStyle) {
		this.sFontStyle = sFontStyle;
	}
	public int getnFontSize() {
		return nFontSize;
	}
	public void setnFontSize(int nFontSize) {
		this.nFontSize = nFontSize;
	}
	public int geteFontCss() {
		return eFontCss;
	}
	public void seteFontCss(short eFontCss) {
		this.eFontCss |= eFontCss;
	}
	public void reseteFontCss(short eFontCss) {
		this.eFontCss &= ~eFontCss;
	}
	public DATE_FORMAT geteShowDate() {
		return eShowDate;
	}
	public void seteShowDate(DATE_FORMAT eShowDate) {
		this.eShowDate = eShowDate;
	}
	public WEEK_FORMAT geteShowWeek() {
		return eShowWeek;
	}
	public void seteShowWeek(WEEK_FORMAT eShowWeek) {
		this.eShowWeek = eShowWeek;
	}
	public TIME_FORMAT geteShowTime() {
		return eShowTime;
	}
	public void seteShowTime(TIME_FORMAT eShowTime) {
		this.eShowTime = eShowTime;
	}
	public int getnFontColor() {
		return nFontColor;
	}
	public void setnFontColor(int nFontColor) {
		this.nFontColor = nFontColor;
	}
	public int getnBackground() {
		return nBackground;
	}
	public void setnBackground(int nBackground) {
		this.nBackground = nBackground;
	}

	

}
