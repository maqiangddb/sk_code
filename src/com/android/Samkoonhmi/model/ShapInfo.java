package com.android.Samkoonhmi.model;

import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;

import android.graphics.Point;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.SHAP_CLASS;

/**
 * 几何图形实体类
 * 
 * @author Administrator
 * 
 */
public class ShapInfo {
	private int id; // integer NOT NULL PRIMARY KEY,
	private SHAP_CLASS ePolygonClass; // 形状类型
	private LINE_TYPE eLineType; // 线类型,
	private int nLineWidth; // 线宽,
	private int nLineColor; // 线颜色
	private CSS_TYPE eStyle; // 填充样式
	private int nBackColor; // 背景色
	private int nForeColor; // 前景色
	private int nAlpha; // 透明度
	private int nPointX; // 圆心横坐标
	private int nPointY; // 圆心纵坐标
	private int nWidth; // 宽度
	private int nHeight; // 高度
	private int nRadius; // 半径
	private END_POINT_TYPE eCornerType;// 端点类型 （圆角，直角，截角）
	private Vector<Point> listPoint; // 点集合
	private int roundRectRadiusY; // 圆角矩形纵向弯度
	private int nZvalue;
	private int nCollidindId;

	/**
	 * 多边形
	 * 
	 * @param id
	 * @param ePolygonClass
	 * @param eLineType
	 * @param nLineWidth
	 * @param nLineColor
	 * @param eStyle
	 * @param nBackColor
	 * @param nForeColor
	 * @param nAlpha
	 * @param listPoint
	 */
	public ShapInfo(int id, SHAP_CLASS ePolygonClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, CSS_TYPE eStyle, int nBackColor,
			int nForeColor, int nAlpha, Vector<Point> listPoint, int nZvalue,
			int nCollidindId) {
		super();
		this.id = id;
		this.ePolygonClass = ePolygonClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eStyle = eStyle;
		this.nBackColor = nBackColor;
		this.nForeColor = nForeColor;
		this.nAlpha = nAlpha;
		this.listPoint = listPoint;
		this.nZvalue = nZvalue;
		this.nCollidindId = nCollidindId;
	}

	/**
	 * 圆角矩形
	 * 
	 * @param id
	 * @param ePolygonClass
	 * @param eLineType
	 * @param nLineWidth
	 * @param nLineColor
	 * @param eStyle
	 * @param nBackColor
	 * @param nForeColor
	 * @param nAlpha
	 * @param nPointX
	 * @param nPointY
	 * @param nWidth
	 * @param nHeight
	 * @param nRadius
	 * @param roundRectRadiusY
	 */
	public ShapInfo(int id, SHAP_CLASS ePolygonClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, CSS_TYPE eStyle, int nBackColor,
			int nForeColor, int nAlpha, int nPointX, int nPointY, int nWidth,
			int nHeight, int nRadius, int roundRectRadiusY, int nZvalue,
			int nCollidindId) {
		super();
		this.id = id;
		this.ePolygonClass = ePolygonClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eStyle = eStyle;
		this.nBackColor = nBackColor;
		this.nForeColor = nForeColor;
		this.nAlpha = nAlpha;
		this.nPointX = nPointX;
		this.nPointY = nPointY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nRadius = nRadius;
		this.roundRectRadiusY = roundRectRadiusY;
		this.nZvalue = nZvalue;
		this.nCollidindId = nCollidindId;
	}

	public ShapInfo() {
		super();
	}

	/**
	 * 矩形
	 * 
	 * @param id
	 * @param ePolygonClass
	 * @param eLineType
	 * @param nLineWidth
	 * @param nLineColor
	 * @param eStyle
	 * @param nBackColor
	 * @param nForeColor
	 * @param nAlpha
	 * @param nPointX
	 * @param nPointY
	 * @param nWidth
	 * @param nHeight
	 * @param eCornerType
	 */
	public ShapInfo(int id, SHAP_CLASS ePolygonClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, CSS_TYPE eStyle, int nBackColor,
			int nForeColor, int nAlpha, int nPointX, int nPointY, int nWidth,
			int nHeight, END_POINT_TYPE eCornerType, int nZvalue,
			int nCollidindId) {
		super();
		this.id = id;
		this.ePolygonClass = ePolygonClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eStyle = eStyle;
		this.nBackColor = nBackColor;
		this.nForeColor = nForeColor;
		this.nAlpha = nAlpha;
		this.nPointX = nPointX;
		this.nPointY = nPointY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.eCornerType = eCornerType;
		this.nZvalue = nCollidindId;
	}

	/**
	 * 圆和椭圆的构造函数
	 * 
	 * @param id
	 * @param ePolygonClass
	 * @param eLineType
	 * @param nLineWidth
	 * @param nLineColor
	 * @param eStyle
	 * @param nBackColor
	 * @param nForeColor
	 * @param nAlpha
	 * @param nPointX
	 * @param nPointY
	 * @param nWidth
	 * @param nHeight
	 */
	public ShapInfo(int id, SHAP_CLASS ePolygonClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, CSS_TYPE eStyle, int nBackColor,
			int nForeColor, int nAlpha, int nPointX, int nPointY, int nWidth,
			int nHeight, int nZvalue, int nCollidindId) {
		super();
		this.id = id;
		this.ePolygonClass = ePolygonClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eStyle = eStyle;
		this.nBackColor = nBackColor;
		this.nForeColor = nForeColor;
		this.nAlpha = nAlpha;
		this.nPointX = nPointX;
		this.nPointY = nPointY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nZvalue = nZvalue;
		this.nCollidindId = nCollidindId;
	}

	public ShapInfo(int id, SHAP_CLASS ePolygonClass, LINE_TYPE eLineType,
			int nLineWidth, int nLineColor, CSS_TYPE eStyle, int nBackColor,
			int nForeColor, int nAlpha, int nPointX, int nPointY, int nWidth,
			int nHeight, int nRadius, END_POINT_TYPE eCornerType,
			Vector<Point> listPoint, int nZvalue, int nCollidindId) {
		super();
		this.id = id;
		this.ePolygonClass = ePolygonClass;
		this.eLineType = eLineType;
		this.nLineWidth = nLineWidth;
		this.nLineColor = nLineColor;
		this.eStyle = eStyle;
		this.nBackColor = nBackColor;
		this.nForeColor = nForeColor;
		this.nAlpha = nAlpha;
		this.nPointX = nPointX;
		this.nPointY = nPointY;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.nRadius = nRadius;
		this.eCornerType = eCornerType;
		this.listPoint = listPoint;
		this.nZvalue = nZvalue;
		this.nCollidindId = nCollidindId;
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

	public int getRoundRectRadiusY() {
		return roundRectRadiusY;
	}

	public void setRoundRectRadiusY(int roundRectRadiusY) {
		this.roundRectRadiusY = roundRectRadiusY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SHAP_CLASS getePolygonClass() {
		return ePolygonClass;
	}

	public void setePolygonClass(SHAP_CLASS ePolygonClass) {
		this.ePolygonClass = ePolygonClass;
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

	public CSS_TYPE geteStyle() {
		return eStyle;
	}

	public void seteStyle(CSS_TYPE eStyle) {
		this.eStyle = eStyle;
	}

	public int getnBackColor() {
		return nBackColor;
	}

	public void setnBackColor(int nBackColor) {
		this.nBackColor = nBackColor;
	}

	public int getnForeColor() {
		return nForeColor;
	}

	public void setnForeColor(int nForeColor) {
		this.nForeColor = nForeColor;
	}

	public int getnAlpha() {
		return nAlpha;
	}

	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}

	public int getnPointX() {
		return nPointX;
	}

	public void setnPointX(int nPointX) {
		this.nPointX = nPointX;
	}

	public int getnPointY() {
		return nPointY;
	}

	public void setnPointY(int nPointY) {
		this.nPointY = nPointY;
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

	public int getnRadius() {
		return nRadius;
	}

	public void setnRadius(int nRadius) {
		this.nRadius = nRadius;
	}

	public END_POINT_TYPE geteCornerType() {
		return eCornerType;
	}

	public void seteCornerType(END_POINT_TYPE eCornerType) {
		this.eCornerType = eCornerType;
	}

	public Vector<Point> getListPoint() {
		return listPoint;
	}

	public void setListPoint(Vector<Point> listPoint) {
		this.listPoint = listPoint;
	}

}
