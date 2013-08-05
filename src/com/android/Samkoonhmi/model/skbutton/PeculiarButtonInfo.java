package com.android.Samkoonhmi.model.skbutton;

import com.android.Samkoonhmi.skenum.BUTTON;

/**
 * 特殊按钮
 * @author 刘伟江
 * @version V 1.0.0.4
 * 创建时间 2012-5-23
 */
public class PeculiarButtonInfo extends ButtonInfo {

	//按钮功能
	private BUTTON.PECULIAR_TYPE ePeculiarType;
	//每个功能对应的操作Id
	private BUTTON.PECULIAR_OPER eActionId;
	//截屏图片存在的位置路径
	private String sScenePicture;
	//语言
	private String sLanguage;
	//打开窗口Id
	private int nWindowID;
	//配方组ID
	private int nFormulaGroupID;
	//采样组ID
	private int nSampleGroupID;
	//水平方向缩放
	private boolean bZoomX;
	//垂直方向缩放
	private boolean bZoomY;
	
	public boolean isbZoomX() {
		return bZoomX;
	}
	public void setbZoomX(boolean bZoomX) {
		this.bZoomX = bZoomX;
	}
	public boolean isbZoomY() {
		return bZoomY;
	}
	public void setbZoomY(boolean bZoomY) {
		this.bZoomY = bZoomY;
	}
	public BUTTON.PECULIAR_TYPE getePeculiarType() {
		return ePeculiarType;
	}
	public void setePeculiarType(BUTTON.PECULIAR_TYPE ePeculiarType) {
		this.ePeculiarType = ePeculiarType;
	}
	public BUTTON.PECULIAR_OPER geteActionId() {
		return eActionId;
	}
	public void setnActionId(BUTTON.PECULIAR_OPER eActionId) {
		this.eActionId = eActionId;
	}
	public String getsScenePicture() {
		return sScenePicture;
	}
	public void setsScenePicture(String sScenePicture) {
		this.sScenePicture = sScenePicture;
	}
	public String getsLanguage() {
		return sLanguage;
	}
	public void setsLanguage(String sLanguage) {
		this.sLanguage = sLanguage;
	}
	public int getnWindowID() {
		return nWindowID;
	}
	public void setnWindowID(int nWindowID) {
		this.nWindowID = nWindowID;
	}
	public int getnFormulaGroupID() {
		return nFormulaGroupID;
	}
	public void setnFormulaGroupID(int nFormulaGroupID) {
		this.nFormulaGroupID = nFormulaGroupID;
	}
	public int getnSampleGroupID() {
		return nSampleGroupID;
	}
	public void setnSampleGroupID(int nSampleGroupID) {
		this.nSampleGroupID = nSampleGroupID;
	}
}
