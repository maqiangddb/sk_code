package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import android.graphics.Typeface;

import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

/**
 * 表格实体对象
 * @author 刘伟江
 * @version V 1.0.0.1
 * 创建时间 2012-5-9
 * 最后修改时间 2012-5-9
 */
public class DragTableInfo {

	//表格的宽
	private short nWidth;
	//表格的高
	private short nHeight;
	//表格的左上角X坐标
	private short nLeftTopX;
	//表格的左上Y坐标
	private short nLeftTopY;
	//表格的边框颜色
	private int nFrameColor;
	//线的颜色
	private int nLineColor;
	//表格的背景颜色
	private int nTableBackcolor;
	//横标题的背景颜色
	private int nTitleBackcolor;
	//横标题的字体颜色
	private int nTitleFontColor;
	//横标题的字体大小
	private short nTitleFontSize;
	//横标题字体类型
	private Typeface mHTypeFace;
	//竖标题的背景颜色
	private int nVTitleBackcolor;
	//竖标题的字体颜色
	private int nVTitleFontColor;
	//竖标题字体类型
	private Typeface mVTypeFace;
	//竖标题的字体大小
	private short nVTitleFontSize;
	//正文的字体大小
	private short nTextFontSize;
	//字体类型
	private Typeface mTypeFace;
	//正文的字体颜色
	private int nTextFontColor;
	//表格的行数
	private short nRow;
	//表格显示列数
	private short nRank;
	//实际列数
	private short nDataRank;
	//是否显示序号
	private boolean showNum;
	//是否显示添加按钮
	private boolean addBtn;
	//是否可以删除
	private boolean delete;
	//是否可以修改
	private boolean update;
	//总页数
	private int nPageCount;
	//当前处于第几页
	private int nPageIndex;
	//总的数据量
	private int nAllCount;
	//已经加载的数量
	private int nLoadNum;
	//一次加载的数据量
	private int nLoadCount;
	//滚动的总长度
	private int nAllRollLen;
	//水平滚动条长度
	private int nHBarLen;
	//垂直滚动条长度
	private int nVBarLen;
	//对齐方式
	private TEXT_PIC_ALIGN mAlign;
	//行的高
	private ArrayList<Double> mRowHeight;
	//行的宽
	private ArrayList<Double> mRowWidth;
	//透明度
	private int nAlpha;
	//相差值
	private int nDiffer;
	//实际所有列宽的总和
	private int nAllColumWidth;
	
	public int getnAllColumWidth() {
		return nAllColumWidth;
	}
	public void setnAllColumWidth(int nAllColumWidth) {
		this.nAllColumWidth = nAllColumWidth;
	}
	public int getnDiffer() {
		return nDiffer;
	}
	public void setnDiffer(int nDiffer) {
		this.nDiffer = nDiffer;
	}
	public int getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}
	public ArrayList<Double> getmRowHeight() {
		return mRowHeight;
	}
	public void setmRowHeight(ArrayList<Double> mRowHeight) {
		this.mRowHeight = mRowHeight;
	}
	public ArrayList<Double> getmRowWidth() {
		return mRowWidth;
	}
	public void setmRowWidth(ArrayList<Double> mRowWidth) {
		this.mRowWidth = mRowWidth;
	}
	public TEXT_PIC_ALIGN getmAlign() {
		return mAlign;
	}
	public void setmAlign(TEXT_PIC_ALIGN mAlign) {
		this.mAlign = mAlign;
	}
	public int getnHBarLen() {
		return nHBarLen;
	}
	public void setnHBarLen(int nHBarLen) {
		this.nHBarLen = nHBarLen;
	}
	public int getnVBarLen() {
		return nVBarLen;
	}
	public void setnVBarLen(int nVBarLen) {
		this.nVBarLen = nVBarLen;
	}
	public int getnAllRollLen() {
		return nAllRollLen;
	}
	public void setnAllRollLen(int nAllRollLen) {
		this.nAllRollLen = nAllRollLen;
	}
	public int getnPageIndex() {
		return nPageIndex;
	}
	public void setnPageIndex(int nPageIndex) {
		this.nPageIndex = nPageIndex;
	}
	public int getnPageCount() {
		return nPageCount;
	}
	public void setnPageCount(int nPageCount) {
		this.nPageCount = nPageCount;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isUpdate() {
		return update;
	}
	public void setUpdate(boolean update) {
		this.update = update;
	}
	public boolean isAddBtn() {
		return addBtn;
	}
	public void setAddBtn(boolean addBtn) {
		this.addBtn = addBtn;
	}
	public boolean isShowNum() {
		return showNum;
	}
	public void setShowNum(boolean showNum) {
		this.showNum = showNum;
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
	public int getnFrameColor() {
		return nFrameColor;
	}
	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	public int getnTableBackcolor() {
		return nTableBackcolor;
	}
	public void setnTableBackcolor(int nTableBackcolor) {
		this.nTableBackcolor = nTableBackcolor;
	}
	public int getnTitleBackcolor() {
		return nTitleBackcolor;
	}
	public void setnTitleBackcolor(int nTitleBackcolor) {
		this.nTitleBackcolor = nTitleBackcolor;
	}
	public int getnTitleFontColor() {
		return nTitleFontColor;
	}
	public void setnTitleFontColor(int nTitleFontColor) {
		this.nTitleFontColor = nTitleFontColor;
	}
	public short getnTitleFontSize() {
		return nTitleFontSize;
	}
	public void setnTitleFontSize(short nTitleFontSize) {
		this.nTitleFontSize = nTitleFontSize;
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
	public short getnRow() {
		return nRow;
	}
	public void setnRow(short nRow) {
		this.nRow = nRow;
	}
	public short getnRank() {
		return nRank;
	}
	public void setnRank(short nRank) {
		this.nRank = nRank;
	}
	public int getnVTitleBackcolor() {
		return nVTitleBackcolor;
	}
	public void setnVTitleBackcolor(int nVTitleBackcolor) {
		this.nVTitleBackcolor = nVTitleBackcolor;
	}
	public int getnVTitleFontColor() {
		return nVTitleFontColor;
	}
	public void setnVTitleFontColor(int nVTitleFontColor) {
		this.nVTitleFontColor = nVTitleFontColor;
	}
	public short getnVTitleFontSize() {
		return nVTitleFontSize;
	}
	public void setnVTitleFontSize(short nVTitleFontSize) {
		this.nVTitleFontSize = nVTitleFontSize;
	}
	public short getnDataRank() {
		return nDataRank;
	}
	public void setnDataRank(short nDataRank) {
		this.nDataRank = nDataRank;
	}
	public int getnAllCount() {
		return nAllCount;
	}
	public void setnAllCount(int nAllCount) {
		this.nAllCount = nAllCount;
	}
	public int getnLoadNum() {
		return nLoadNum;
	}
	public void setnLoadNum(int nLoadNum) {
		this.nLoadNum = nLoadNum;
	}
	public int getnLoadCount() {
		return nLoadCount;
	}
	public void setnLoadCount(int nLoadCount) {
		this.nLoadCount = nLoadCount;
	}
	public Typeface getmHTypeFace() {
		return mHTypeFace;
	}
	public void setmHTypeFace(Typeface mHTypeFace) {
		this.mHTypeFace = mHTypeFace;
	}
	public Typeface getmVTypeFace() {
		return mVTypeFace;
	}
	public void setmVTypeFace(Typeface mVTypeFace) {
		this.mVTypeFace = mVTypeFace;
	}
	public Typeface getmTypeFace() {
		return mTypeFace;
	}
	public void setmTypeFace(Typeface mTypeFace) {
		this.mTypeFace = mTypeFace;
	}
	public int getnLineColor() {
		return nLineColor;
	}
	public void setnLineColor(int nLineColor) {
		this.nLineColor = nLineColor;
	}
}
