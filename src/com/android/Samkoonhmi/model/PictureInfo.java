package com.android.Samkoonhmi.model;

/**
 * 按钮每个状态对应的图片
 * @author 刘伟江
 * @version v 1.0.0.1
 * 创建时间 2012-5-22
 * 最后修改时间 2012-5-22
 */
public class PictureInfo {

	//状态ID
	private short nStatusId;
	//图片路径
	private String sPath;
	
	public short getnStatusId() {
		return nStatusId;
	}
	public void setnStatusId(short nStatusId) {
		this.nStatusId = nStatusId;
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
