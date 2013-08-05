package com.android.Samkoonhmi.model.skbutton;

import com.android.Samkoonhmi.skenum.BUTTON;


/**
 * 画面开关
 * @author 刘伟江
 * @version v 1.0.0.4
 * 创建时间 2012-5-23
 */
public class SceneButtonInfo extends ButtonInfo {

	//界面操作
	private BUTTON.OPER_SCENE eOperScene;
	//目标画面号
	private int nTargetPage;
	//注销用户
	private boolean bLogout;
	//画面类型,画面-0,窗口-1
	private short nSceneType;
	//画面进入效果
	private int nEnterType;
	
	public int getnEnterType() {
		return nEnterType;
	}
	public void setnEnterType(int nEnterType) {
		this.nEnterType = nEnterType;
	}
	public short getnSceneType() {
		return nSceneType;
	}
	public void setnSceneType(short nSceneType) {
		this.nSceneType = nSceneType;
	}
	public BUTTON.OPER_SCENE geteOperScene() {
		return eOperScene;
	}
	public void seteOperScene(BUTTON.OPER_SCENE eOperScene) {
		this.eOperScene = eOperScene;
	}
	public int getnTargetPage() {
		return nTargetPage;
	}
	public void setnTargetPage(int nTargetPage) {
		this.nTargetPage = nTargetPage;
	}
	public boolean isbLogout() {
		return bLogout;
	}
	public void setbLogout(boolean bLogout) {
		this.bLogout = bLogout;
	}
	
}
