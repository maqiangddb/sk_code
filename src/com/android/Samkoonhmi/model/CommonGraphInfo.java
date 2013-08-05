package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.Graph.SHAPE_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 图表-普通图
 * 
 * @author 刘伟江
 * @version v 1.0.0.1 
 * 创建时间：2012-4-19
 * 最后修改时间 2012-4-23
 */
public class CommonGraphInfo extends GraphBaseInfo{

	//图表形状
	private SHAPE_TYPE eShapeType;
	//显示填充
	private boolean bFill;
	//孔
	private boolean bHole;
	//半径
	private short nRadius;
	//显示开始点
	private boolean bStart;
	//图案
	private short nDesign;
	//图案颜色
	private int nDesignColor;
	//边框颜色
	private int nFrameColor;
	//开始角度 0~360
	private short nStartAngle = -1;
	//跨角度 0~360
	private short nSpanAngle = -1;
	
	public SHAPE_TYPE geteShapeType() {
		return eShapeType;
	}
	public void seteShapeType(SHAPE_TYPE eShapeType) {
		this.eShapeType = eShapeType;
	}
	public boolean isbFill() {
		return bFill;
	}
	public void setbFill(boolean bFill) {
		this.bFill = bFill;
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
	public boolean isbStart() {
		return bStart;
	}
	public void setbStart(boolean bStart) {
		this.bStart = bStart;
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
	
	public void setStartAngle(short startangle){
		this.nStartAngle = startangle;
	}
	public short getStartAngle(){
		return nStartAngle;
	}
	
	public void setSpanAngle(short spanangle){
		this.nSpanAngle = spanangle;
	}
	public short getSpanAngle(){
		return nSpanAngle;
	}
}
