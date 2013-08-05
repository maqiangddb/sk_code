package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Paint.Style;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

/**
 * 静态文本实体类
 * 
 * @author Eisen
 * 
 */
public class StaticTextModel {
	// 属性
	private Integer id;
	private String m_sTextStr = "";// 输入文本
	private int m_nFontSize;// 字体大小
	private String m_sFontFamly;// 字形
	private short m_textPro ;// 字体的斜体，粗体和下划线
	private int m_nFontColor;// 字体颜色
	private int m_nFontSpace = -1;// 字距
	private TEXT_PIC_ALIGN m_eTextAlign = TEXT_PIC_ALIGN.CENTER;// 文本对齐方式
	private boolean m_fristLanguage;// 第一种语言
	private String m_textLanguage;// 语言类型
	private int m_textLanguageId;// 语言序号
	private CSS_TYPE m_stylePadding = CSS_TYPE.CSS_SOLIDCOLOR;// 填充的样式
	private int m_alphaPadding = 255;// 填充的透明度
	private int borderAlpha = 255;//边框的透明度
	private int m_backColorPadding = Color.TRANSPARENT;// 填充的背景色
	private int m_foreColorPadding;// 填充的前景色
	private int startX ;// 起始X
	private int startY;// 起始Y
	private float rectWidth;// 文本框的宽度
	private float rectHeight;// 文本框的高度
	private int lineWidth;// 线的宽度
	private int lineColor ;// 线的颜色
	private int nZvalue;
	private int nCollidindId;
	private ShowInfo showInfo;// 显现属性
	private boolean bTextChange;//文本内容改变

	// 每个状态的文本
	private ArrayList<TextInfo> mTextList;

	/**
	 * 构造方法
	 */
	public StaticTextModel() {
		super();
	}

	public int getBorderAlpha() {
		return borderAlpha;
	}

	public void setBorderAlpha(int borderAlpha) {
		this.borderAlpha = borderAlpha;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public float getRectWidth() {
		return rectWidth;
	}

	public void setRectWidth(float rectWidth) {
		this.rectWidth = rectWidth;
	}

	public float getRectHeight() {
		return rectHeight;
	}

	public void setRectHeight(float rectHeight) {
		this.rectHeight = rectHeight;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getM_sTextStr() {
		return m_sTextStr;
	}

	public void setM_sTextStr(String m_sTextStr) {
		this.bTextChange=true;
		this.m_sTextStr = m_sTextStr;
	}

	public int getM_nFontSize() {
		return m_nFontSize;
	}

	public void setM_nFontSize(int m_nFontSize) {
		this.m_nFontSize = m_nFontSize;
	}

	public String getM_sFontFamly() {
		return m_sFontFamly;
	}

	public void setM_sFontFamly(String m_sFontFamly) {
		this.m_sFontFamly = m_sFontFamly;
	}

	public short getM_textPro() {
		return m_textPro;
	}

	public void setM_textPro(short m_textPro) {
		this.m_textPro = m_textPro;
	}

	public int getM_nFontColor() {
		return m_nFontColor;
	}

	public void setM_nFontColor(int m_nFontColor) {
		this.m_nFontColor = m_nFontColor;
	}

	public int getM_nFontSpace() {
		return m_nFontSpace;
	}

	public void setM_nFontSpace(int m_nFontSpace) {
		this.m_nFontSpace = m_nFontSpace;
	}

	public TEXT_PIC_ALIGN getM_eTextAlign() {
		return m_eTextAlign;
	}

	public void setM_eTextAlign(TEXT_PIC_ALIGN m_eTextAlign) {
		this.m_eTextAlign = m_eTextAlign;
	}

	public boolean isM_fristLanguage() {
		return m_fristLanguage;
	}

	public void setM_fristLanguage(boolean m_fristLanguage) {
		this.m_fristLanguage = m_fristLanguage;
	}

	public String getM_textLanguage() {
		return m_textLanguage;
	}

	public void setM_textLanguage(String m_textLanguage) {
		this.m_textLanguage = m_textLanguage;
	}

	public int getM_textLanguageId() {
		return m_textLanguageId;
	}

	public void setM_textLanguageId(int m_textLanguageId) {
		this.m_textLanguageId = m_textLanguageId;
	}

	public CSS_TYPE getM_stylePadding() {
		return m_stylePadding;
	}

	public void setM_stylePadding(CSS_TYPE m_stylePadding) {
		this.m_stylePadding = m_stylePadding;
	}

	public int getM_alphaPadding() {
		return m_alphaPadding;
	}

	public void setM_alphaPadding(int m_alphaPadding) {
		this.m_alphaPadding = m_alphaPadding;
	}

	public int getM_backColorPadding() {
		return m_backColorPadding;
	}

	public void setM_backColorPadding(int m_backColorPadding) {
		this.m_backColorPadding = m_backColorPadding;
	}

	public int getM_foreColorPadding() {
		return m_foreColorPadding;
	}

	public void setM_foreColorPadding(int m_foreColorPadding) {
		this.m_foreColorPadding = m_foreColorPadding;
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

	public ShowInfo getShowInfo() {
		return showInfo;
	}

	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}

	public ArrayList<TextInfo> getmTextList() {
		return mTextList;
	}

	public void setmTextList(ArrayList<TextInfo> mTextList) {
		this.mTextList = mTextList;
	}
	
	public boolean isbTextChange() {
		return bTextChange;
	}

	public void setbTextChange(boolean bTextChange) {
		this.bTextChange = bTextChange;
	}
}
