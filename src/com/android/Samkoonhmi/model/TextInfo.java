package com.android.Samkoonhmi.model;

import java.util.ArrayList;
import java.util.Vector;

import android.R.integer;

import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

/**
 * 按钮,文本
 * 
 * @author 刘伟江
 * @version v 1.0.0.4 创建时间 2012-5-22 最后修改时间 2012-5-22
 */
public class TextInfo {

	// 状态ID
	private short nStatusId;
	// 状态对应文本内容
	private String sText;
	// 字体类型
	private String nFontFamily;
	// 字体大小
	private int nSize;
	// 字体颜色
	private int nColor;
	// 文本背景
	private int nBColor;
	// 文本显示位置
	private TEXT_PIC_ALIGN eAlign;
	/**
	 * 文本效果 B0000 : 无效果 B0001 : 粗体 B0010 : 斜体 B0100 : 闪烁 B1000 : 下划线
	 */
	private short nStyle;
	// 字体间距
	private short nSpace;
	// 闪烁类型
	private FLICK_TYPE eFlickType;
	// 语言id
	private short nLangugeId;
	
	// 多语言文本
	private ArrayList<String> mTextList;
	// 字体类型
	private ArrayList<String> mFonts;
	// 字体大小
	private ArrayList<Integer> mSize;
	// 颜色
	private ArrayList<Integer> mColors;
	// 位置
	private ArrayList<Integer> mStyle;
	//画文本
	private TextItem mTextItem;
		
	public TextItem getmTextItem() {
		return mTextItem;
	}

	public void setmTextItem(TextItem mTextItem) {
		this.mTextItem = mTextItem;
	}

	public ArrayList<Integer> getmStyle() {
		return mStyle;
	}

	public void setmStyle(ArrayList<Integer> mStyle) {
		this.mStyle = mStyle;
	}

	public ArrayList<Integer> getmColors() {
		return mColors;
	}

	public void setmColors(ArrayList<Integer> mColors) {
		this.mColors = mColors;
	}

	public short getnLangugeId() {
		return nLangugeId;
	}

	public void setnLangugeId(short nLangugeId) {
		this.nLangugeId = nLangugeId;
	}

	public short getnStatusId() {
		return nStatusId;
	}

	public void setnStatusId(short nStatusId) {
		this.nStatusId = nStatusId;
	}

	public String getsText() {
		return sText;
	}

	public void setsText(String sText) {
		this.sText = sText;
	}

	public String getsFontFamily() {
		return nFontFamily;
	}

	public void setsFontFamily(String nFontFamily) {
		this.nFontFamily = nFontFamily;
	}

	public int getnSize() {
		return nSize;
	}

	public void setnSize(int nSize) {
		this.nSize = nSize;
	}

	public int getnColor() {
		return nColor;
	}

	public void setnColor(int nColor) {
		this.nColor = nColor;
	}

	public int getnBColor() {
		return nBColor;
	}

	public void setnBColor(int nBColor) {
		this.nBColor = nBColor;
	}

	public TEXT_PIC_ALIGN geteAlign() {
		return eAlign;
	}

	public void seteAlign(TEXT_PIC_ALIGN eAlign) {
		this.eAlign = eAlign;
	}

	public short getnStyle() {
		return nStyle;
	}

	public void setnStyle(short nStyle) {
		this.nStyle = nStyle;
	}

	public short getnSpace() {
		return nSpace;
	}

	public void setnSpace(short nSpace) {
		this.nSpace = nSpace;
	}

	public FLICK_TYPE geteFlickType() {
		return eFlickType;
	}

	public void seteFlickType(FLICK_TYPE eFlickType) {
		this.eFlickType = eFlickType;
	}

	public ArrayList<String> getmTextList() {
		return mTextList;
	}

	public void setmTextList(ArrayList<String> mTextList) {
		this.mTextList = mTextList;
	}

	public String getnFontFamily() {
		return nFontFamily;
	}

	public void setnFontFamily(String nFontFamily) {
		this.nFontFamily = nFontFamily;
	}

	public ArrayList<String> getmFonts() {
		return mFonts;
	}

	public void setmFonts(ArrayList<String> mFonts) {
		this.mFonts = mFonts;
	}

	public ArrayList<Integer> getmSize() {
		return mSize;
	}

	public void setmSize(ArrayList<Integer> mSize) {
		this.mSize = mSize;
	}
}
