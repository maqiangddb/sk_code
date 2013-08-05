package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.Graph.SHAPE_TYPE;

/**
 * 图表-统计图
 * @author 刘伟江
 * @version v 1.0.0.1 
 * 创建时间 2012-4-19
 * 最后修改时间 2012-4-23
 */
public class StatisticsGraphInfo extends GraphBaseInfo {

	// 图表形状
	private SHAPE_TYPE eShapeType;
	// 数据分割线
	private short nParting;
	// 显示颜色
	private int nTextColor;
	// 图案
	private short nDesign;
	// 图案颜色
	private int nDesignColor;
	// 边框颜色
	private int nFrameColor;
	// 是否显示孔
	private boolean bHole;
	// 孔半径
	private short nRadius;
	
	public SHAPE_TYPE geteShapeType() {
		return eShapeType;
	}
	public void seteShapeType(SHAPE_TYPE eShapeType) {
		this.eShapeType = eShapeType;
	}
	public short getnParting() {
		return nParting;
	}
	public void setnParting(short nParting) {
		this.nParting = nParting;
	}
	public int getnTextColor() {
		return nTextColor;
	}
	public void setnTextColor(int nTextColor) {
		this.nTextColor = nTextColor;
	}
	public short getnDesign() {
		return nDesign;
	}
	public void setnDesign(short nDesign) {
		this.nDesign = nDesign;
	}
	public int getnDesignColor() {
		return nDesignColor;
	}
	public void setnDesignColor(int nDesignColor) {
		this.nDesignColor = nDesignColor;
	}
	public int getnFrameColor() {
		return nFrameColor;
	}
	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	public boolean isbHole() {
		return bHole;
	}
	public void setbHole(boolean bHole) {
		this.bHole = bHole;
	}
	public short getnRadius() {
		return nRadius;
	}
	public void setnRadius(short nRadius) {
		this.nRadius = nRadius;
	}
	
}
