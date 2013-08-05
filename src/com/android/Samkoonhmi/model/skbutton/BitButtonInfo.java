package com.android.Samkoonhmi.model.skbutton;

import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 位开关
 * @author 刘伟江
 * @version v 1.0.0.4
 * 创建时间 2012-5-22
 */
public class BitButtonInfo extends ButtonInfo {
	
	//功能
	private BUTTON.BIT_OPER_TYPE eOperType;
	//按下为0
	private boolean bDownZero;
	//写入地址
	private AddrProp mBitAddress;
	//是否需要确定
	private boolean bConfirm;
	//等待时间
	private int nTimeOut;
	
	public boolean isbConfirm() {
		return bConfirm;
	}
	public void setbConfirm(boolean bConfirm) {
		this.bConfirm = bConfirm;
	}
	public int getnTimeOut() {
		return nTimeOut;
	}
	public void setnTimeOut(int nTimeOut) {
		this.nTimeOut = nTimeOut;
	}
	public BUTTON.BIT_OPER_TYPE geteOperType() {
		return eOperType;
	}
	public void seteOperType(BUTTON.BIT_OPER_TYPE eOperType) {
		this.eOperType = eOperType;
	}
	public boolean isbDownZero() {
		return bDownZero;
	}
	public void setbDownZero(boolean bDownZero) {
		this.bDownZero = bDownZero;
	}
	public AddrProp getmBitAddress() {
		return mBitAddress;
	}
	public void setmBitAddress(AddrProp mBitAddress) {
		this.mBitAddress = mBitAddress;
	}
	
}
