package com.android.Samkoonhmi.model;

/**
 * 画面控件
 * @author 刘伟江
 * 创建时间 2012-6-19
 */
public class SceneItemInfo {

	//画面id
	private int nSceneId;
	//控件id
	private int nItemId;
	//控件类型
	private int nItemTableType;
	
	public int getnSceneId() {
		return nSceneId;
	}
	public void setnSceneId(int nSceneId) {
		this.nSceneId = nSceneId;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public int getnItemTableType() {
		return nItemTableType;
	}
	public void setnItemTableType(int nItemTableType) {
		this.nItemTableType = nItemTableType;
	}
	
}
