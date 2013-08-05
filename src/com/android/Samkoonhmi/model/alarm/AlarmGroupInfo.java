package com.android.Samkoonhmi.model.alarm;
import java.util.ArrayList;
import java.util.HashMap;

public class AlarmGroupInfo {
	
	//报警组序号
	private int nGroupId;
	//报警组名称
	private String sName;
	//报警组采集时间
	private short nTime;
	//报警条件,第一个参数为:nAlarmIndex,
	private HashMap<Integer, AlarmConditionInfo> mConditionMap;
	//报警消息,第一个参数为:nAlarmIndex,第二个参数，多语言文本
	private HashMap<Integer,ArrayList<AlarmMessageInfo>> mMessageMaps;
	
	public int getnGroupId() {
		return nGroupId;
	}
	public void setnGroupId(int nGroupId) {
		this.nGroupId = nGroupId;
	}
	public String getsName() {
		return sName;
	}
	public void setsName(String sName) {
		this.sName = sName;
	}
	public int getnTime() {
		return nTime;
	}
	public void setnTime(short nTime) {
		this.nTime = nTime;
	}
	public HashMap<Integer, AlarmConditionInfo> getmConditionMap() {
		return mConditionMap;
	}
	public void setmConditionMap(HashMap<Integer, AlarmConditionInfo> mConditionMap) {
		this.mConditionMap = mConditionMap;
	}
	public HashMap<Integer, ArrayList<AlarmMessageInfo>> getmMessageMaps() {
		return mMessageMaps;
	}
	public void setmMessageMaps(
			HashMap<Integer, ArrayList<AlarmMessageInfo>> mMessageMaps) {
		this.mMessageMaps = mMessageMaps;
	}
}
