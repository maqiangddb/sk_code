package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 显现设置
 * @author Administrator
 * @version V 1.0.0.1
 * 创建时间 2012-5-22
 */
public class ShowInfo {
	private int itemId; //控件id

	//是否受地址控制
	private boolean bShowByAddr;
	//控制位
	private ADDRTYPE eAddrType;
	//有效状态
	private short nValidStatus;
	//地址在addr表的编号
	private int nAddrId;
	//显现地址
	private AddrProp showAddrProp;
	//字的第几位
	private short nBitPosition;
	//是否受用户控制
	private boolean bShowByUser;
	//前32组的值，按照组的编号从低到高
	private int nGroupValueF;
	//后32组的值，按照组的编号从低到高
	private int nGroupValueL;
	
	
	public AddrProp getShowAddrProp() {
		return showAddrProp;
	}
	public void setShowAddrProp(AddrProp showAddrProp) {
		this.showAddrProp = showAddrProp;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public boolean isbShowByAddr() {
		return bShowByAddr;
	}
	public void setbShowByAddr(boolean bShowByAddr) {
		this.bShowByAddr = bShowByAddr;
	}
	public ADDRTYPE geteAddrType() {
		return eAddrType;
	}
	public void seteAddrType(ADDRTYPE eAddrType) {
		this.eAddrType = eAddrType;
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
	public short getnBitPosition() {
		return nBitPosition;
	}
	public void setnBitPosition(short nBitPosition) {
		this.nBitPosition = nBitPosition;
	}
	public boolean isbShowByUser() {
		return bShowByUser;
	}
	public void setbShowByUser(boolean bShowByUser) {
		this.bShowByUser = bShowByUser;
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
	
}
