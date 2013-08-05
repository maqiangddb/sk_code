package com.android.Samkoonhmi.model;

/**
 * 留言信息类
 * @author Administrator
 *
 */
public class MessageDetailInfo {
     private int nId;//	信息编号
     private int nItemId;//	留言板编号
     private long nTime;//	留言时间
     private String sMessage;//	留言信息
//     private String sTitle;//	标题
	public int getnId() {
		return nId;
	}
	public void setnId(int nId) {
		this.nId = nId;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public long getnTime() {
		return nTime;
	}
	public void setnTime(long nTime) {
		this.nTime = nTime;
	}
	public String getsMessage() {
		return sMessage;
	}
	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}
//	public String getsTitle() {
//		return sTitle;
//	}
//	public void setsTitle(String sTitle) {
//		this.sTitle = sTitle;
//	}

	
}
