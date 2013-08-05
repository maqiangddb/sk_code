package com.android.Samkoonhmi.model;

import java.util.List;

import com.android.Samkoonhmi.SKThread;

/**
 * 用户实体类
 * @author Administrator
 */
public class UserInfo {
	//用户ID or 组id
	private int Id;     
	//用户名称 or 组名称
	private String name;
	//用户密码
	private String password;
	//用户拥有的组
	private List<Integer> groupId;
	//用户描述 or 组描述
	private String descript;
	//是否被选中
	private boolean check;
	
	public UserInfo() {
		super();
	}
	
	public UserInfo(int id, String name, String password, List<Integer> groupId) {
		super();
		this.Id = id;
		this.name = name;
		this.password = password;
		this.groupId = groupId;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Integer> getGroupId() {
		return groupId;
	}
	public void setGroupId(List<Integer> groupId) {
		this.groupId = groupId;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
}
