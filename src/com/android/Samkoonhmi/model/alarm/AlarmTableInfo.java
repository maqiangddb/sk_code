package com.android.Samkoonhmi.model.alarm;

/**
 * 报警表格信息维护
 */
public class AlarmTableInfo {

	/**
	 * 序号id
	 */
	private int nId;
	/**
	 * 表格序号
	 */
	private int nTid;
	/**
	 * 表格的存储数据
	 */
	private int nCount;
	/**
	 * 1-插入
	 * 2-更新
	 * 3-删除
	 */
	private int nType;
	
	public int getnType() {
		return nType;
	}
	public void setnType(int nType) {
		this.nType = nType;
	}
	public int getnId() {
		return nId;
	}
	public void setnId(int nId) {
		this.nId = nId;
	}
	public int getnTid() {
		return nTid;
	}
	public void setnTid(int nTid) {
		this.nTid = nTid;
	}
	public int getnCount() {
		return nCount;
	}
	public void setnCount(int nCount) {
		this.nCount = nCount;
	}
}
