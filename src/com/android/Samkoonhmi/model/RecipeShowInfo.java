package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

/**
 * 配方显示器
 * @author 刘伟江
 * 创建时间 2012-7-28
 */
public class RecipeShowInfo {

	//控件id
	private int nItemId;
	//控件左顶点X坐标
	private short nStartPosX;
	//控件左顶点Y坐标
	private short nStartPosY;
	//控件宽
	private short nWidth;
	//控件高
	private short nHeight;
	//要显示的配方组Id
	private int nRecipeGroupId;
	//配方名称
	private ArrayList<String> mRecipeNames;
	//是否显示配方ID
	private boolean bShowRecipeID;
	//是否显示陪描述
	private boolean bShowDescrip;
	//文本的对齐方式
	private TEXT_PIC_ALIGN eType;
	//语言id
	private int nLanguagId;
	//行数
	private short nRow;
	//列数
	private short nColum;
	//线的颜色
	private int nLineColor;
	//横标题的文本颜色
	private int nHTitleTextColor;
	//横标题背景颜色
	private int nHTitleBackColor;
	//横标题字体大小
	private int nHTitleFontSize;
	//横标题字体
	private String sHTitleFont;
	//竖标题的文本颜色
	private int nVTitleTextColor;
	//竖标题背景颜色
	private int nVTitleBackColor;
	//竖标题字体大小
	private int nVTitleFontSize;
	//竖标题字体
	private String sVTitleFont;
	//文本颜色
	private int nTextColor;
	//文本背景
	private int nTextBackColor;
	//文本字体大小
	private int nTextFontSize;
	//文本字体
	private String sTextFont;
	//透明度
	private short nAlpha;
	//层id
	private int nZvalue;
	//组合id
	private int nCollidindId;
	//显隐
	private ShowInfo mShowInfo;
	//触控
	private TouchInfo mTouchInfo;
	//行的宽
	private ArrayList<Double> mRowWidht;
	//行的高
	private ArrayList<Double> mRowHeight;
	
	public ArrayList<String> getmRecipeNames() {
		return mRecipeNames;
	}
	public void setmRecipeNames(ArrayList<String> mRecipeNames) {
		this.mRecipeNames = mRecipeNames;
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
	public TouchInfo getmTouchInfo() {
		return mTouchInfo;
	}
	public void setmTouchInfo(TouchInfo mTouchInfo) {
		this.mTouchInfo = mTouchInfo;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public short getnStartPosX() {
		return nStartPosX;
	}
	public void setnStartPosX(short nStartPosX) {
		this.nStartPosX = nStartPosX;
	}
	public short getnStartPosY() {
		return nStartPosY;
	}
	public void setnStartPosY(short nStartPosY) {
		this.nStartPosY = nStartPosY;
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
	public int getnRecipeGroupId() {
		return nRecipeGroupId;
	}
	public void setnRecipeGroupId(int nRecipeGroupId) {
		this.nRecipeGroupId = nRecipeGroupId;
	}
	public boolean isbShowRecipeID() {
		return bShowRecipeID;
	}
	public void setbShowRecipeID(boolean bShowRecipeID) {
		this.bShowRecipeID = bShowRecipeID;
	}
	public boolean isbShowDescrip() {
		return bShowDescrip;
	}
	public void setbShowDescrip(boolean bShowDescrip) {
		this.bShowDescrip = bShowDescrip;
	}
	public TEXT_PIC_ALIGN geteType() {
		return eType;
	}
	public void seteType(TEXT_PIC_ALIGN eType) {
		this.eType = eType;
	}
	public int getnLanguagId() {
		return nLanguagId;
	}
	public void setnLanguagId(int nLanguagId) {
		this.nLanguagId = nLanguagId;
	}
	public short getnRow() {
		return nRow;
	}
	public void setnRow(short nRow) {
		this.nRow = nRow;
	}
	public short getnColum() {
		return nColum;
	}
	public void setnColum(short nColum) {
		this.nColum = nColum;
	}
	public int getnHTitleTextColor() {
		return nHTitleTextColor;
	}
	public void setnHTitleTextColor(int nHTitleTextColor) {
		this.nHTitleTextColor = nHTitleTextColor;
	}
	public int getnHTitleBackColor() {
		return nHTitleBackColor;
	}
	public void setnHTitleBackColor(int nHTitleBackColor) {
		this.nHTitleBackColor = nHTitleBackColor;
	}
	public int getnHTitleFontSize() {
		return nHTitleFontSize;
	}
	public void setnHTitleFontSize(int nHTitleFontSize) {
		this.nHTitleFontSize = nHTitleFontSize;
	}
	public String getsHTitleFont() {
		return sHTitleFont;
	}
	public void setsHTitleFont(String sHTitleFont) {
		this.sHTitleFont = sHTitleFont;
	}
	public int getnVTitleTextColor() {
		return nVTitleTextColor;
	}
	public void setnVTitleTextColor(int nVTitleTextColor) {
		this.nVTitleTextColor = nVTitleTextColor;
	}
	public int getnVTitleBackColor() {
		return nVTitleBackColor;
	}
	public void setnVTitleBackColor(int nVTitleBackColor) {
		this.nVTitleBackColor = nVTitleBackColor;
	}
	public int getnVTitleFontSize() {
		return nVTitleFontSize;
	}
	public void setnVTitleFontSize(int nVTitleFontSize) {
		this.nVTitleFontSize = nVTitleFontSize;
	}
	public String getsVTitleFont() {
		return sVTitleFont;
	}
	public void setsVTitleFont(String sVTitleFont) {
		this.sVTitleFont = sVTitleFont;
	}
	public int getnTextColor() {
		return nTextColor;
	}
	public void setnTextColor(int nTextColor) {
		this.nTextColor = nTextColor;
	}
	public int getnTextBackColor() {
		return nTextBackColor;
	}
	public void setnTextBackColor(int nTextBackColor) {
		this.nTextBackColor = nTextBackColor;
	}
	public int getnTextFontSize() {
		return nTextFontSize;
	}
	public void setnTextFontSize(int nTextFontSize) {
		this.nTextFontSize = nTextFontSize;
	}
	public String getsTextFont() {
		return sTextFont;
	}
	public void setsTextFont(String sTextFont) {
		this.sTextFont = sTextFont;
	}
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
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
	public int getnLineColor() {
		return nLineColor;
	}
	public void setnLineColor(int nLineColor) {
		this.nLineColor = nLineColor;
	}
	public short getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(short nAlpha) {
		this.nAlpha = nAlpha;
	}
}
