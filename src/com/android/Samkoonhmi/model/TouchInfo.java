package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 触控设置
 * @author Administrator
 * @version V 1.0.0.1
 * 创建时间 2012-5-22
 */
public class TouchInfo {
    private int itemId;
	//是否受地址控制
	private boolean bTouchByAddr;
	//控制位
	private ADDRTYPE eCtlAddrType;
	//有效状态：0或1
	private short nValidStatus;
	//地址在addr表的编号
	private int nAddrId;
	//触控地址
	private AddrProp touchAddrProp;
	//字的第几位
	private short nWordPosition;
	//是否受用户控制
	private boolean bTouchByUser;
	//前32组的值，按照组的编号从低到高
	private int nGroupValueF;
	//后32组的值，按照组的编号从低到高
	private int nGroupValueL;
	//按压时间
	private short nPressTime;
	//是否超时取消
	private boolean bTimeoutCancel;
	//超时取消的时间
	private short nTimeout;
	//是否需要通知
	private boolean bNoticAddr;
	//通知的数据类型
	private DATA_TYPE eDataType;
	//通知的地址编号
	private AddrProp nNoticAddrId;
	//通知的值
	private double nNoticValue;
	
	
	public AddrProp getTouchAddrProp() {
		return touchAddrProp;
	}
	public void setTouchAddrProp(AddrProp touchAddrProp) {
		this.touchAddrProp = touchAddrProp;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public ADDRTYPE geteCtlAddrType() {
		return eCtlAddrType;
	}
	public void seteCtlAddrType(ADDRTYPE eCtlAddrType) {
		this.eCtlAddrType = eCtlAddrType;
	}
	public short getnValidStatus() {
		return nValidStatus;
	}
	public void setnValidStatus(short nValidStatus) {
		this.nValidStatus = nValidStatus;
	}
	public int getnAddrId() {
		return nAddrId;
	}
	public void setnAddrId(int nAddrId) {
		this.nAddrId = nAddrId;
	}
	public short getnWordPosition() {
		return nWordPosition;
	}
	public void setnWordPosition(short nWordPosition) {
		this.nWordPosition = nWordPosition;
	}

	public boolean isbTouchByAddr() {
		return bTouchByAddr;
	}
	public void setbTouchByAddr(boolean bTouchByAddr) {
		this.bTouchByAddr = bTouchByAddr;
	}
	public boolean isbTouchByUser() {
		return bTouchByUser;
	}
	public void setbTouchByUser(boolean bTouchByUser) {
		this.bTouchByUser = bTouchByUser;
	}
	public int getnGroupValueF() {
		return nGroupValueF;
	}
	public void setnGroupValueF(int nGroupValueF) {
		this.nGroupValueF = nGroupValueF;
	}
	public int getnGroupValueL() {
		return nGroupValueL;
	}
	public void setnGroupValueL(int nGroupValueL) {
		this.nGroupValueL = nGroupValueL;
	}
	public short getnPressTime() {
		return nPressTime;
	}
	public void setnPressTime(short nPressTime) {
		this.nPressTime = nPressTime;
	}
	public boolean isbTimeoutCancel() {
		return bTimeoutCancel;
	}
	public void setbTimeoutCancel(boolean bTimeoutCancel) {
		this.bTimeoutCancel = bTimeoutCancel;
	}
	public short getnTimeout() {
		return nTimeout;
	}
	public void setnTimeout(short nTimeout) {
		this.nTimeout = nTimeout;
	}
	public boolean isbNoticAddr() {
		return bNoticAddr;
	}
	public void setbNoticAddr(boolean bNoticAddr) {
		this.bNoticAddr = bNoticAddr;
	}
	public DATA_TYPE geteDataType() {
		return eDataType;
	}
	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}

	public AddrProp getnNoticAddrId() {
		return nNoticAddrId;
	}
	public void setnNoticAddrId(AddrProp nNoticAddrId) {
		this.nNoticAddrId = nNoticAddrId;
	}
	public double getnNoticValue() {
		return nNoticValue;
	}
	public void setnNoticValue(double nNoticValue) {
		this.nNoticValue = nNoticValue;
	}
}
