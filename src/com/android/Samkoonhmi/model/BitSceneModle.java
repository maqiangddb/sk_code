package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.util.AddrProp;

public class BitSceneModle {
	private int id;//编号
	private int nStatus;//状态 0 or 1
	private AddrProp nBitAddress;//位地址
	private int nSceneId;//画面编号
	private boolean bRest ;//是否自动复位
	private boolean bClose;//是否自动关闭
	private int nAddressValue=-1; //地址值
	private CallbackItem item;//地址监视回调
	private int nWindowState;//窗口的状态，0-已经关闭，1-打开着
	
	public int getnWindowState() {
		return nWindowState;
	}
	public void setnWindowState(int nWindowState) {
		this.nWindowState = nWindowState;
	}
	public boolean isbClose() {
		return bClose;
	}
	public void setbClose(boolean bClose) {
		this.bClose = bClose;
	}
	
	public CallbackItem getItem() {
		return item;
	}
	public void setItem(CallbackItem item) {
		this.item = item;
	}
	public int getnAddressValue() {
		return nAddressValue;
	}
	public void setnAddressValue(int nAddressValue) {
		this.nAddressValue = nAddressValue;
	}
	public boolean isbRest() {
		return bRest;
	}
	public void setbRest(boolean bRest) {
		this.bRest = bRest;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getnStatus() {
		return nStatus;
	}
	public void setnStatus(int nStatus) {
		this.nStatus = nStatus;
	}
	public AddrProp getnBitAddress() {
		return nBitAddress;
	}
	public void setnBitAddress(AddrProp nBitAddress) {
		this.nBitAddress = nBitAddress;
	}
	public int getnSceneId() {
		return nSceneId;
	}
	public void setnSceneId(int nSceneId) {
		this.nSceneId = nSceneId;
	}
	

}
