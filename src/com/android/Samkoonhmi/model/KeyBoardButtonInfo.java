package com.android.Samkoonhmi.model;

import android.graphics.Color;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

public class KeyBoardButtonInfo {
	private Integer id;// 编号
	private int nStartX ;// 开始坐标x
	private int nStartY ;// 开始坐标y
	private int nWidth ;// 长度
	private int nHeight ;// 宽度
	private int nUpFrameColor;// 按下前的边框色
	private int nUpForeColor;// 按下前的前景色
	private int nUpBackColor;// 按下前的背景色
	private CSS_TYPE eUpStyle;// 按下前的样式
	private int nDownFrameColor;// 按下后的边框色
	private int nDownForeColor;// 按下后的前景色
	private int nDownBackColor;// 按下后的背景色
	private CSS_TYPE eDownStyle;// 按下后的样式
	private KEYBOARD_OPERATION keyOperation;// 键盘操作
	private String ASCIIStr;
	private int keyNum ;
	private ShowInfo showInfo;// 显现属性
	private TouchInfo touchInfo;// 触控属性

	// 文本
	private String sText = "";// 文本
	private String sFontFamly;// 字体样式
	private int nFontSize ;// 字体大小
	private int nFontColor ;// 字体颜色
	private short nFontPro;// 字体属性(粗体、下划线、斜体、闪烁)
	private TEXT_PIC_ALIGN eFontAlign ;// 字体位置
	private String sImagePath;// 图片路径

	public KeyBoardButtonInfo() {
		super();
	}

	public KeyBoardButtonInfo(int nStartX, int nStartY, String sText,
			KEYBOARD_OPERATION keyOperation) {
		super();
		this.nStartX = nStartX;
		this.nStartY = nStartY;
		this.sText = sText;
		this.keyOperation = keyOperation;
	}

	public KeyBoardButtonInfo(Integer id, int nStartX, int nStartY, int nWidth,
			int nHeight, int nUpFrameColor, int nUpForeColor, int nUpBackColor,
			CSS_TYPE eUpStyle, int nDownFrameColor, int nDownForeColor,
			int nDownBackColor, CSS_TYPE eDownStyle,
			KEYBOARD_OPERATION keyOperation, String aSCIIStr, int keyNum,
			ShowInfo showInfo, TouchInfo touchInfo, String sText,
			String sFontFamly, int nFontSize, int nFontColor, short nFontPro,
			TEXT_PIC_ALIGN eFontAlign, String sImagePath) {
		super();
		this.id = id;
		this.nStartX = nStartX;
		this.nStartY = nStartY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nUpFrameColor = nUpFrameColor;
		this.nUpForeColor = nUpForeColor;
		this.nUpBackColor = nUpBackColor;
		this.eUpStyle = eUpStyle;
		this.nDownFrameColor = nDownFrameColor;
		this.nDownForeColor = nDownForeColor;
		this.nDownBackColor = nDownBackColor;
		this.eDownStyle = eDownStyle;
		this.keyOperation = keyOperation;
		ASCIIStr = aSCIIStr;
		this.keyNum = keyNum;
		this.showInfo = showInfo;
		this.touchInfo = touchInfo;
		this.sText = sText;
		this.sFontFamly = sFontFamly;
		this.nFontSize = nFontSize;
		this.nFontColor = nFontColor;
		this.nFontPro = nFontPro;
		this.eFontAlign = eFontAlign;
		this.sImagePath = sImagePath;
	}

	public TouchInfo getTouchInfo() {
		return touchInfo;
	}

	public void setTouchInfo(TouchInfo touchInfo) {
		this.touchInfo = touchInfo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public int getnUpFrameColor() {
		return nUpFrameColor;
	}

	public void setnUpFrameColor(int nUpFrameColor) {
		this.nUpFrameColor = nUpFrameColor;
	}

	public int getnUpForeColor() {
		return nUpForeColor;
	}

	public void setnUpForeColor(int nUpForeColor) {
		this.nUpForeColor = nUpForeColor;
	}

	public int getnUpBackColor() {
		return nUpBackColor;
	}

	public void setnUpBackColor(int nUpBackColor) {
		this.nUpBackColor = nUpBackColor;
	}

	public CSS_TYPE geteUpStyle() {
		return eUpStyle;
	}

	public void seteUpStyle(CSS_TYPE eUpStyle) {
		this.eUpStyle = eUpStyle;
	}

	public int getnDownFrameColor() {
		return nDownFrameColor;
	}

	public void setnDownFrameColor(int nDownFrameColor) {
		this.nDownFrameColor = nDownFrameColor;
	}

	public int getnDownForeColor() {
		return nDownForeColor;
	}

	public void setnDownForeColor(int nDownForeColor) {
		this.nDownForeColor = nDownForeColor;
	}

	public int getnDownBackColor() {
		return nDownBackColor;
	}

	public void setnDownBackColor(int nDownBackColor) {
		this.nDownBackColor = nDownBackColor;
	}

	public CSS_TYPE geteDownStyle() {
		return eDownStyle;
	}

	public void seteDownStyle(CSS_TYPE eDownStyle) {
		this.eDownStyle = eDownStyle;
	}

	public KEYBOARD_OPERATION getKeyOperation() {
		return keyOperation;
	}

	public void setKeyOperation(KEYBOARD_OPERATION keyOperation) {
		this.keyOperation = keyOperation;
	}

	public int getKeyNum() {
		return keyNum;
	}

	public void setKeyNum(int keyNum) {
		this.keyNum = keyNum;
	}

	public String getsText() {
		return sText;
	}

	public void setsText(String sText) {
		this.sText = sText;
	}

	public int getnFontSize() {
		return nFontSize;
	}

	public void setnFontSize(int nFontSize) {
		this.nFontSize = nFontSize;
	}

	public int getnFontColor() {
		return nFontColor;
	}

	public void setnFontColor(int nFontColor) {
		this.nFontColor = nFontColor;
	}

	public short getnFontPro() {
		return nFontPro;
	}

	public void setnFontPro(short nFontPro) {
		this.nFontPro |= nFontPro;
	}

	public void resetnFontPro(short nFontPro) {
		this.nFontPro &= ~nFontPro;
	}

	public TEXT_PIC_ALIGN geteFontAlign() {
		return eFontAlign;
	}

	public void seteFontAlign(TEXT_PIC_ALIGN eFontAlign) {
		this.eFontAlign = eFontAlign;
	}

	public String getsImagePath() {
		return sImagePath;
	}

	public void setsImagePath(String sImagePath) {
		this.sImagePath = sImagePath;
	}

	public String getsFontFamly() {
		return sFontFamly;
	}

	public void setsFontFamly(String sFontFamly) {
		this.sFontFamly = sFontFamly;
	}

	public String getASCIIStr() {
		return ASCIIStr;
	}

	public void setASCIIStr(String aSCIIStr) {
		ASCIIStr = aSCIIStr;
	}

	public ShowInfo getShowInfo() {
		return showInfo;
	}

	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}

}
