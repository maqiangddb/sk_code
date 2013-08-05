package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 时间同步设置
 */
public class TimeSettingInfo {

	/**
	 * 是否下载时间到HMI
	 */
	private boolean bDownloadTime;
	/**
	 * 时间同步类型
	 * 0-不同步时间
	 * 1-写时间到PLC
	 * 2-从PLC读时间
	 */
	private short nSynchTime;
	/**
	 * 数据类型
	 */
	private DATA_TYPE eDataType;
	/**
	 * 地址长度
	 */
	private int nLenth;
	/**
	 * 操作地址
	 */
	private AddrProp mSynchAddr;
	/**
	 * 启动方式
	 * 1-计时
	 * 2-触发
	 */
	private int nExeType;
	/**
	 * 时间间隔
	 */
	private int nTime;
	/**
	 * 触发地址
	 */
	private AddrProp mTriggerAddr;
	/**
	 * 是否自动复位
	 */
	private boolean bAutoReset;
	
	public boolean isbDownloadTime() {
		return bDownloadTime;
	}
	public void setbDownloadTime(boolean bDownloadTime) {
		this.bDownloadTime = bDownloadTime;
	}
	public short getnSynchTime() {
		return nSynchTime;
	}
	public void setnSynchTime(short nSynchTime) {
		this.nSynchTime = nSynchTime;
	}
	public DATA_TYPE geteDataType() {
		return eDataType;
	}
	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}
	public int getnLenth() {
		return nLenth;
	}
	public void setnLenth(int nLenth) {
		this.nLenth = nLenth;
	}
	public AddrProp getmSynchAddr() {
		return mSynchAddr;
	}
	public void setmSynchAddr(AddrProp mSynchAddr) {
		this.mSynchAddr = mSynchAddr;
	}
	public int getnExeType() {
		return nExeType;
	}
	public void setnExeType(int nExeType) {
		this.nExeType = nExeType;
	}
	public int getnTime() {
		return nTime;
	}
	public void setnTime(int nTime) {
		this.nTime = nTime;
	}
	public AddrProp getmTriggerAddr() {
		return mTriggerAddr;
	}
	public void setmTriggerAddr(AddrProp mTriggerAddr) {
		this.mTriggerAddr = mTriggerAddr;
	}
	public boolean isbAutoReset() {
		return bAutoReset;
	}
	public void setbAutoReset(boolean bAutoReset) {
		this.bAutoReset = bAutoReset;
	}
	
}
