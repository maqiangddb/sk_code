package com.android.Samkoonhmi.model;

import java.util.Vector;

import android.graphics.Point;
import android.graphics.PointF;

import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_CLASS;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.POINT_TYPE;

/**
 * 线的实体类
 * 
 * @author Administrator
 * 
 */
public class LineInfo {
	private int Id; // 编号 varchar
	private LINE_CLASS eLineClass; // 线种类
	private LINE_TYPE eLineType; // 线类型 short（枚举类型）
	private int nLineWidth; // 线宽 short（1~9）
	private int nLineColor; // 线颜色 int
	private END_ARROW_TYPE eLineArrow;// 箭头形状 short（枚举类型）
	private int nAlpha; // 透明度 short（0~255）
	private Vector<Point> pointList;// 组成线的点集合 List<Point>
	private Vector<POINT_TYPE> pointTypeList;// 组成线的点集合的类型
	private END_POINT_TYPE endPointType; //折线和自由直线连接的形状
	private int nZvalue ;
	private int nCollidindId ;
	private int nStartX;
	private int nStartY;
	private int nWidth;
	private int nHeight;
	private Vector<PointF> fPointList;

	public LineInfo() {
		super();
	}
  
	/**
	 * 折线
	 * @param id
	 * @param eLineClass
	 * @param eLineType
	 * @param nLineWidth
	 * @param nLineColor
	 * @param eLineArrow
	 * @param nAlpha
	 * @param pointList
	 */
	public LineInfo(int id, LINE_CLASS eLineClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, END_ARROW_TYPE eLineArrow,END_POINT_TYPE endPointType,
			int nAlpha, Vector<Point> pointList,int nZvalue,int nCollidindId,int nStartX,int nStartY,int nWidth,int nHeight) {
		super();
		Id = id;
		this.eLineClass = eLineClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eLineArrow = eLineArrow;
		this.endPointType=endPointType;
		this.nAlpha = nAlpha;
		this.pointList = pointList;
		this.nZvalue=nZvalue;
		this.nCollidindId=nCollidindId;
		this.nStartX=nStartX;
		this.nStartY=nStartY;
		this.nWidth=nWidth;
		this.nHeight=nHeight;
	}

	public LineInfo(int id, LINE_CLASS eLineClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, END_ARROW_TYPE eLineArrow,
			int nAlpha, Vector<Point> pointList, Vector<POINT_TYPE> pointTypeList,int nZvalue,int nCollidindId,int nStartX,int nStartY,int nWidth,int nHeight) {
		super();
		Id = id;
		this.eLineClass = eLineClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eLineArrow = eLineArrow;
		this.nAlpha = nAlpha;
		this.pointList = pointList;
		this.pointTypeList = pointTypeList;
		this.nZvalue=nZvalue;
		this.nCollidindId=nCollidindId;
		this.nStartX=nStartX;
		this.nStartY=nStartY;
		this.nWidth=nWidth;
		this.nHeight=nHeight;
	}

	
	public Vector<PointF> getfPointList() {
		return fPointList;
	}

	public void setfPointList(Vector<PointF> fPointList) {
		this.fPointList = fPointList;
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

	public END_POINT_TYPE getEndPointType() {
		return endPointType;
	}

	public void setEndPointType(END_POINT_TYPE endPointType) {
		this.endPointType = endPointType;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public LINE_CLASS geteLineClass() {
		return eLineClass;
	}

	public void seteLineClass(LINE_CLASS eLineClass) {
		this.eLineClass = eLineClass;
	}

	public LINE_TYPE geteLineType() {
		return eLineType;
	}

	public void seteLineType(LINE_TYPE eLineType) {
		this.eLineType = eLineType;
	}

	public int getnLineWidth() {
		return nLineWidth;
	}

	public void setnLineWidth(int nLineWidth) {
		this.nLineWidth = nLineWidth;
	}

	public int getnLineColor() {
		return nLineColor;
	}

	public void setnLineColor(int nLineColor) {
		this.nLineColor = nLineColor;
	}

	public END_ARROW_TYPE geteLineArrow() {
		return eLineArrow;
	}

	public void seteLineArrow(END_ARROW_TYPE eLineArrow) {
		this.eLineArrow = eLineArrow;
	}

	public int getnAlpha() {
		return nAlpha;
	}

	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}

	public Vector<Point> getPointList() {
		return pointList;
	}

	public void setPointList(Vector<Point> pointList) {
		this.pointList = pointList;
	}

	public Vector<POINT_TYPE> getPointTypeList() {
		return pointTypeList;
	}

	public void setPointTypeList(Vector<POINT_TYPE> pointTypeList) {
		this.pointTypeList = pointTypeList;
	}

}
