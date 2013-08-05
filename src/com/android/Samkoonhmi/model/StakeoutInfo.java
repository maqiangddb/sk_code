package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.FLICK_TYPE;

/**
 * 多态监视添加
 */
public class StakeoutInfo {

	//状态Id
	private short nStatusId;
	//状态Id 关联的值
	private short nCmpFactor;
	//图片路径
	private String sPath;
	//闪烁
	private FLICK_TYPE eFlickType;
	//控件外形，1-图片，2-图库图片，3-外部图片，4-不使用图片
	private short nShapeType;
	//透明度
	private int nAlpha;
	//颜色
	private int nColor;
	//闪烁
	private boolean bFlick;
	
	public boolean isbFlick() {
		return bFlick;
	}
	public void setbFlick(boolean bFlick) {
		this.bFlick = bFlick;
	}
	public short getnShapeType() {
		return nShapeType;
	}
	public void setnShapeType(short nShapeType) {
		this.nShapeType = nShapeType;
	}
	public int getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(int nAlpha) {
		this.nAlpha = nAlpha;
	}
	public int getnColor() {
		return nColor;
	}
	public void setnColor(int nColor) {
		this.nColor = nColor;
	}
	public FLICK_TYPE geteFlickType() {
		return eFlickType;
	}
	public void seteFlickType(FLICK_TYPE eFlickType) {
		this.eFlickType = eFlickType;
	}
	public short getnStatusId() {
		return nStatusId;
	}
	public void setnStatusId(short nStatusId) {
		this.nStatusId = nStatusId;
	}
	public short getnCmpFactor() {
		return nCmpFactor;
	}
	public void setnCmpFactor(short nCmpFactor) {
		this.nCmpFactor = nCmpFactor;
	}
	public String getsPath() {
		if (sPath==null) {
			sPath="";
		}
		return sPath;
	}
	public void setsPath(String sPath) {
		this.sPath = sPath;
	}
}
