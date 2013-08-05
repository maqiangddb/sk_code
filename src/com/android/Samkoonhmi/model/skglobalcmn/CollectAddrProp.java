package com.android.Samkoonhmi.model.skglobalcmn;

import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;


/**
 * 采集地址的属性，包括数据类型和缩放
 * @author latory
 *
 */
public class CollectAddrProp {

	/*地址*/
	public AddrProp mAddrPro = null;

	/*数据类型*/
	public DATA_TYPE eDataType = DATA_TYPE.OTHER_DATA_TYPE;

	/*是否数据处理*/
	public boolean bDealData = false; 
	
	/*是否四舍五入*/
	public boolean bFloatRound = true; 
	
	/*小数的最大位数*/
	public int nDecLength = 0;

	/*缩放源数据最小值*/
	public double nSourceMin ;

	/*缩放源数据最大值*/
	public double nSourceMax ;

	/*缩放目标数据最小值*/
	public double nTargeMin ;

	/*缩放目标数据最大值*/
	public double nTargeMax ;
	
	/**
	 * 导出列名称
	 */
	public String sName;

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public AddrProp getmAddrPro() {
		return mAddrPro;
	}

	public void setmAddrPro(AddrProp mAddrPro) {
		this.mAddrPro = mAddrPro;
	}

	public DATA_TYPE geteDataType() {
		return eDataType;
	}

	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}

	public boolean isbDealData() {
		return bDealData;
	}

	public void setbDealData(boolean bDealData) {
		this.bDealData = bDealData;
	}

	public boolean isbFloatRound() {
		return bFloatRound;
	}

	public void setbFloatRound(boolean bFloatRound) {
		this.bFloatRound = bFloatRound;
	}

	public int getnDecLength() {
		return nDecLength;
	}

	public void setnDecLength(int nDecLength) {
		this.nDecLength = nDecLength;
	}

	public double getnSourceMin() {
		return nSourceMin;
	}

	public void setnSourceMin(double nSourceMin) {
		this.nSourceMin = nSourceMin;
	}

	public double getnSourceMax() {
		return nSourceMax;
	}

	public void setnSourceMax(double nSourceMax) {
		this.nSourceMax = nSourceMax;
	}

	public double getnTargeMin() {
		return nTargeMin;
	}

	public void setnTargeMin(double nTargeMin) {
		this.nTargeMin = nTargeMin;
	}

	public double getnTargeMax() {
		return nTargeMax;
	}

	public void setnTargeMax(double nTargeMax) {
		this.nTargeMax = nTargeMax;
	}
	
	
}
