package com.android.Samkoonhmi.model;

import java.util.ArrayList;
import java.util.Map;

import android.graphics.Color;
import android.graphics.Point;

import com.android.Samkoonhmi.graphicsdrawframe.LineItem;
import com.android.Samkoonhmi.skenum.LINE_TYPE;

/**
 * 表格
 * 
 * @author Eisen
 * 
 */
public class TableModel {

	private Integer id;
	private int nOrientationCount ;// 横向个数
	private int nPortraitCount ;// 纵向个数
	private boolean bShowFrameLine;// 是否显示边框线条
	private boolean bShowOrientationLine;// 是否显示水平线条
	private boolean bShowPortraitCount;// 是否显示垂直线条
	private int nBackColor ;// 表格背景颜色
	private int nTableWidth ;// 表格的宽度
	private int nTableHeight;// 表格的高度
	private double nLeftTopX ;// 起始X轴
	private double nLeftTopY ;// 起始Y轴
	private LINE_TYPE eWLineType;// 表格外边框：线型
	private int nWLineWidth ;// 表格外边框：线条宽度
	private int nWShowColor ;// 表格外边框：显示颜色
	private LINE_TYPE eNLineType ;// 表格内边框：线型
	private int nNLineWidth ;// 表格内边框：线条宽度
	private int nNShowColor ;// 表格内边框：显示颜色
	private int alpha = 255;// 填充的透明度
	private int nZvalue;
	private int nCollidindId;
	private ArrayList<Double> nRows;//行
	private ArrayList<Double> nColums;//列
	private ArrayList<LineItem> mRowItems;
	private ArrayList<LineItem> mColumsItems;

	public ArrayList<LineItem> getmRowItems() {
		return mRowItems;
	}

	public void setmRowItems(ArrayList<LineItem> mRowItems) {
		this.mRowItems = mRowItems;
	}

	public ArrayList<LineItem> getmColumsItems() {
		return mColumsItems;
	}

	public void setmColumsItems(ArrayList<LineItem> mColumsItems) {
		this.mColumsItems = mColumsItems;
	}

	public ArrayList<Double> getnRows() {
		return nRows;
	}

	public void setnRows(ArrayList<Double> nRows) {
		this.nRows = nRows;
	}

	public ArrayList<Double> getnColums() {
		return nColums;
	}

	public void setnColums(ArrayList<Double> nColums) {
		this.nColums = nColums;
	}

	/**
	 * 构造方法
	 */
	public TableModel() {
		super();
	}

	/**
	 * 属性封装
	 * 
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getnOrientationCount() {
		return nOrientationCount;
	}

	public void setnOrientationCount(int nOrientationCount) {
		this.nOrientationCount = nOrientationCount;
	}

	public int getnPortraitCount() {
		return nPortraitCount;
	}

	public void setnPortraitCount(int nPortraitCount) {
		this.nPortraitCount = nPortraitCount;
	}

	public boolean isbShowFrameLine() {
		return bShowFrameLine;
	}

	public void setbShowFrameLine(boolean bShowFrameLine) {
		this.bShowFrameLine = bShowFrameLine;
	}

	public int getnBackColor() {
		return nBackColor;
	}

	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}

	public boolean isbShowOrientationLine() {
		return bShowOrientationLine;
	}

	public void setbShowOrientationLine(boolean bShowOrientationLine) {
		this.bShowOrientationLine = bShowOrientationLine;
	}

	public boolean isbShowPortraitCount() {
		return bShowPortraitCount;
	}

	public void setbShowPortraitCount(boolean bShowPortraitCount) {
		this.bShowPortraitCount = bShowPortraitCount;
	}

	public int getnTableWidth() {
		return nTableWidth;
	}

	public void setnTableWidth(int nTableWidth) {
		this.nTableWidth = nTableWidth;
	}

	public int getnTableHeight() {
		return nTableHeight;
	}

	public void setnTableHeight(int nTableHeight) {
		this.nTableHeight = nTableHeight;
	}

	public double getnLeftTopX() {
		return nLeftTopX;
	}

	public void setnLeftTopX(double nLeftTopX) {
		this.nLeftTopX = nLeftTopX;
	}

	public double getnLeftTopY() {
		return nLeftTopY;
	}

	public void setnLeftTopY(double nLeftTopY) {
		this.nLeftTopY = nLeftTopY;
	}

	public LINE_TYPE geteWLineType() {
		return eWLineType;
	}

	public void seteWLineType(LINE_TYPE eWLineType) {
		this.eWLineType = eWLineType;
	}

	public int getnWLineWidth() {
		return nWLineWidth;
	}

	public void setnWLineWidth(int nWLineWidth) {
		this.nWLineWidth = nWLineWidth;
	}

	public int getnWShowColor() {
		return nWShowColor;
	}

	public void setnWShowColor(int nWShowColor) {
		this.nWShowColor = nWShowColor;
	}

	public LINE_TYPE geteNLineType() {
		return eNLineType;
	}

	public void seteNLineType(LINE_TYPE eNLineType) {
		this.eNLineType = eNLineType;
	}

	public int getnNLineWidth() {
		return nNLineWidth;
	}

	public void setnNLineWidth(int nNLineWidth) {
		this.nNLineWidth = nNLineWidth;
	}

	public int getnNShowColor() {
		return nNShowColor;
	}

	public void setnNShowColor(int nNShowColor) {
		this.nNShowColor = nNShowColor;
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

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

}
