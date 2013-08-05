package com.android.Samkoonhmi.graphicsdrawframe;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.POINT_TYPE;
//import GraphicsDrawBase;
import java.util.Vector;
//import Vector<Point >;
import android.graphics.Point;

public class LinearDrawItem extends GraphicsDrawBase {
	/**
	 * 点集合
	 */
	private Vector<Point> m_pointList;
	private Vector<PointF> m_fpointList;

	/**
	 * 
	 * 点的功能
	 */
	private Vector<POINT_TYPE> pointTypeList;

	public Vector<POINT_TYPE> getPointTypeList() {
		return pointTypeList;
	}

	public void setPointTypeList(Vector<POINT_TYPE> pointTypeList) {
		this.pointTypeList = pointTypeList;
	}

	/**
	 * 箭头类型
	 */
	private END_ARROW_TYPE m_eEndArrowType;
	/**
	 * 端点形状
	 */
	private END_POINT_TYPE m_eEndPointType;

	public LinearDrawItem(Vector<Point> pointList) {
		// TODO put your implementation here.
		this.m_eEndArrowType = END_ARROW_TYPE.STYLE_NONE;
		this.m_eEndPointType = END_POINT_TYPE.FLAT_CAP;
	}

	public LinearDrawItem() {
		// TODO put your implementation here.
		this.m_eEndArrowType = END_ARROW_TYPE.STYLE_NONE;
		this.m_eEndPointType = END_POINT_TYPE.FLAT_CAP;
	}

	public void setAlpha(int nAlpha) {
		// TODO put your implementation here.
		super.setAlpha(nAlpha);
	}

	public int getAlpha() {
		return super.getAlpha();
		// TODO put your implementation here.
	}

	public Vector<Point> getM_pointList() {
		return m_pointList;
	}

	public void setM_pointList(Vector<Point> m_pointList) {
		this.m_pointList = m_pointList;
	}

	public Vector<PointF> getM_fpointList() {
		return m_fpointList;
	}

	public void setM_fpointList(Vector<PointF> m_fpointList) {
		this.m_fpointList = m_fpointList;
	}

	public void setEndArrowType(END_ARROW_TYPE eArrowType) {
		// TODO put your implementation here.
		this.m_eEndArrowType = eArrowType;
	}

	public END_ARROW_TYPE getEndArrowType() {
		return m_eEndArrowType;
		// TODO put your implementation here.
	}

	public void setEndPointType(END_POINT_TYPE ePointType) {
		// TODO put your implementation here.
		this.m_eEndPointType = ePointType;
	}

	public END_POINT_TYPE getEndPointType() {
		return m_eEndPointType;
		// TODO put your implementation here.
	}

	public void draw(Paint paint, Canvas canvas) {
		// TODO put your implementation here.
	}

}