package com.android.Samkoonhmi.model.skbutton;

import android.R.integer;

import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 字开关
 * @author 刘伟江
 * @version V 1.0.0.4
 * 创建时间 2012-5-23
 */
public class WordButtonInfo extends ButtonInfo{

	//功能
	private BUTTON.WORD_OPER_TYPE eOperType;
	//数据类别
	private DATA_TYPE eDataType;
	//写入地址
	private AddrProp mAddrProp;
	//常数
	private double nFinalValue;
	//最大值
	private double nMax;
	//最小值
	private double nMin;
	//是否绕回
	private boolean bCycle;
	//是否有动态地址
	private boolean bDynamicControl;
	//获取动态地址
	private AddrProp mDynamicAddrProp ;
	public void setbDynamicControl(boolean control){
		bDynamicControl = control;
	}
	public boolean getbDynamicControl(){
		return bDynamicControl;
	}
	public void setmDynamicAddrProp(AddrProp adrrProp){
		mDynamicAddrProp = adrrProp;
	}
	public AddrProp getmDynamicAdddrProp(){
		return mDynamicAddrProp;
	}
	public double getnMax() {
		return nMax;
	}
	public void setnMax(double nMax) {
		this.nMax = nMax;
	}
	public double getnMin() {
		return nMin;
	}
	public void setnMin(double nMin) {
		this.nMin = nMin;
	}
	public boolean isbCycle() {
		return bCycle;
	}
	public void setbCycle(boolean bCycle) {
		this.bCycle = bCycle;
	}
	public BUTTON.WORD_OPER_TYPE geteOperType() {
		return eOperType;
	}
	public void seteOperType(BUTTON.WORD_OPER_TYPE eOperType) {
		this.eOperType = eOperType;
	}
	public DATA_TYPE geteDataType() {
		return eDataType;
	}
	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}
	public AddrProp getmAddrProp() {
		return mAddrProp;
	}
	public void setmAddrProp(AddrProp mAddrProp) {
		this.mAddrProp = mAddrProp;
	}
	public double getnFinalValue() {
		return nFinalValue;
	}
	public void setnFinalValue(double nFinalValue) {
		this.nFinalValue = nFinalValue;
	}

}
