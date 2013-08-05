package com.android.Samkoonhmi.model;

import java.util.List;

import com.android.Samkoonhmi.skenum.TEXT_LANGUAGE;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 下拉框
 * @author Administrator
 *
 */
public class ComboBoxInfo {
	//控件Id
	private int Id;
	//起始x坐标
	private int nStartX;
	//起始y坐标
	private int nStartY;
	//控件宽度
    private int nWidth;
    //控件高度
    private int nHeight;
    //显示下来的数量
    private int nShowNumber;
    //功能列表的值集合
    private List<ComboxItemInfo> functionList;
    //字体
    private String sFontType;
    //字大小
    private int nfontSize;
    //语言种类
	private TEXT_LANGUAGE nLanguageTypeId;
	//字颜色
	private int nFontColor;
	//文本属性
	private int eFontCss;
	//背景颜色
	private int nBackColor;
	private int nTouchPropId;//触控Id
	private int nShowPropId;//显隐Id
	private int nZvalue ;
	private int nCollidindId ;
	private boolean bIsStartStatement;//是否启动宏指令
	private int nScriptId; //脚本库id
	private TouchInfo touchInfo;// 触控属性
	private ShowInfo showInfo;// 显现属性
	private int nAlpha;//透明度
	//是否使用图片
	private boolean bIsUsePic;
	
	public int getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}
	public ComboBoxInfo() {
		super();
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


	public ComboBoxInfo(int id, int nStartX, int nStartY, int nWidth,
			int nHeight, int nShowNumber, List<ComboxItemInfo> functionList, String sFontType,
			int nfontSize, TEXT_LANGUAGE nLanguageTypeId, int nFontColor,
			int eFontCss, int nBackColor,int nZvalue,int nCollidindId) {
	 	super();
		Id = id;
		this.nStartX = nStartX;
		this.nStartY = nStartY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nShowNumber = nShowNumber;
		this.functionList = functionList;
		this.sFontType = sFontType;
		this.nfontSize = nfontSize;
		this.nLanguageTypeId = nLanguageTypeId;
		this.nFontColor = nFontColor;
		this.eFontCss = eFontCss;
		this.nBackColor = nBackColor;
		this.nZvalue=nZvalue;
		this.nCollidindId=nCollidindId;
	}
	
	

	public ComboBoxInfo(int nStartX, int nStartY, int nWidth, int nHeight,
			int nShowNumber, List<ComboxItemInfo> functionList, String sFontType,
			int nfontSize, TEXT_LANGUAGE nLanguageTypeId, int nFontColor,
			short eFontCss, int nBackColor, int nTouchPropId, int nShowPropId,int nZvalue,int nCollidindId) {
		super();
		this.nStartX = nStartX;
		this.nStartY = nStartY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nShowNumber = nShowNumber;
		this.functionList = functionList;
		this.sFontType = sFontType;
		this.nfontSize = nfontSize;
		this.nLanguageTypeId = nLanguageTypeId;
		this.nFontColor = nFontColor;
		this.eFontCss = eFontCss;
		this.nBackColor = nBackColor;
		this.nTouchPropId = nTouchPropId;
		this.nShowPropId = nShowPropId;
		this.nZvalue=nZvalue;
		this.nCollidindId=nCollidindId;
	}

	public boolean isbIsStartStatement() {
		return bIsStartStatement;
	}

	public void setbIsStartStatement(boolean bIsStartStatement) {
		this.bIsStartStatement = bIsStartStatement;
	}

	public int getnScriptId() {
		return nScriptId;
	}

	public void setnScriptId(int nScriptId) {
		this.nScriptId = nScriptId;
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


	public int getnTouchPropId() {
		return nTouchPropId;
	}


	public void setnTouchPropId(int nTouchPropId) {
		this.nTouchPropId = nTouchPropId;
	}


	public int getnShowPropId() {
		return nShowPropId;
	}


	public void setnShowPropId(int nShowPropId) {
		this.nShowPropId = nShowPropId;
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
	public int getnShowNumber() {
		return nShowNumber;
	}
	public void setnShowNumber(int nShowNumber) {
		this.nShowNumber = nShowNumber;
	}
	public List<ComboxItemInfo> getFunctionList() {
		return functionList;
	}
	public void setFunctionList(List<ComboxItemInfo> functionList) {
		this.functionList = functionList;
	}
	public String getsFontType() {
		return sFontType;
	}
	public void setsFontType(String sFontType) {
		this.sFontType = sFontType;
	}
	public int getNfontSize() {
		return nfontSize;
	}
	public void setNfontSize(int nfontSize) {
		this.nfontSize = nfontSize;
	}
	public TEXT_LANGUAGE getnLanguageTypeId() {
		return nLanguageTypeId;
	}
	public void setnLanguageTypeId(TEXT_LANGUAGE nLanguageTypeId) {
		this.nLanguageTypeId = nLanguageTypeId;
	}
	public int getnFontColor() {
		return nFontColor;
	}
	public void setnFontColor(int nFontColor) {
		this.nFontColor = nFontColor;
	}
	public int geteFontCss() {
		return eFontCss;
	}
	public void seteFontCss(int eFontCss) {
		this.eFontCss |= eFontCss;
	}
	public void reseteFontCss(short eFontCss) {
		this.eFontCss &= ~eFontCss;
	}
	public int getnBackColor() {
		return nBackColor;
	}
	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}
	
	public void setbIsUsePic(boolean isUse){
		this.bIsUsePic = isUse;
	}
	public boolean getbIsUsePic(){
		return this.bIsUsePic;
	}
}
