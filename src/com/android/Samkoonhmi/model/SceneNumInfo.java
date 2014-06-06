package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;

/**
 * 场景编号和唯一id
 */
public class SceneNumInfo {

	private int id;//表id
	private int num;//场景编号，可以编辑
	private int sid;//场景唯一编号
	private String name;//场景名称
	private SHOW_TYPE eType;//类型
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public SHOW_TYPE geteType() {
		return eType;
	}
	public void seteType(SHOW_TYPE eType) {
		this.eType = eType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
}
