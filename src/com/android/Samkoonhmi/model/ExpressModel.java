package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.EXPRESS_NUM_TYPE;
import com.android.Samkoonhmi.skenum.EXPRESS_SIGN;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 表达式实体类
 * @author quliping
 *
 */
public class ExpressModel {
	private int Id; //	Int	编号
	private int nItemId;//	Int	表达式Id，跟控件挂钩
	private EXPRESS_SIGN nSign;//	Short	枚举，符号
	private EXPRESS_NUM_TYPE eType;//	Short	枚举，地址还是常量1：地址，2：常量
	private AddrProp mAddProp;//地址
	private double nVaule;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public EXPRESS_SIGN getnSign() {
		return nSign;
	}
	public void setnSign(EXPRESS_SIGN nSign) {
		this.nSign = nSign;
	}
	public EXPRESS_NUM_TYPE geteType() {
		return eType;
	}
	public void seteType(EXPRESS_NUM_TYPE eType) {
		this.eType = eType;
	}
	public AddrProp getmAddProp() {
		return mAddProp;
	}
	public void setmAddProp(AddrProp mAddProp) {
		this.mAddProp = mAddProp;
	}
	public double getnVaule() {
		return nVaule;
	}
	public void setnVaule(double nVaule) {
		this.nVaule = nVaule;
	}

	
	
	


}
