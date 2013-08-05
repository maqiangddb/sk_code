package com.android.Samkoonhmi.model.alarm;

/**
 * 报警信息
 */
public class AlarmMessageInfo {
	// 报警组序号
	private int nGroupId;
	// 报警在组中的序号
	private int nAlarmIndex;
	// 语言id
	private int nLanguage;
	// 报警消息
	private String sMessage;
	
	public int getnGroupId() {
		return nGroupId;
	}
	public void setnGroupId(int nGroupId) {
		this.nGroupId = nGroupId;
	}
	public int getnAlarmIndex() {
		return nAlarmIndex;
	}
	public void setnAlarmIndex(int nAlarmIndex) {
		this.nAlarmIndex = nAlarmIndex;
	}
	public int getnLanguage() {
		return nLanguage;
	}
	public void setnLanguage(int nLanguage) {
		this.nLanguage = nLanguage;
	}
	public String getsMessage() {
		return sMessage;
	}
	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}
}
