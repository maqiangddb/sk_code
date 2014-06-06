package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.util.AddrProp;
 
public abstract class PHolder {
	
	protected String Name;
	private int nAllParamIndex;//所有宏指令参数中的序号
    private int nParamIndex;//所在宏指令参数中的序号
    private short nRWPerm;//变量的读写权限
    private AddrProp mAddrProp;//操作地址
    private boolean bOffSetAddr;//是否使用地址偏移
    private AddrProp mOffSetAddr;//操作地址
	
	public AddrProp getmOffSetAddr() {
		return mOffSetAddr;
	}

	public void setmOffSetAddr(AddrProp mOffSetAddr) {
		this.mOffSetAddr = mOffSetAddr;
	}

	public AddrProp getmAddrProp() {
		return mAddrProp;
	}

	public void setmAddrProp(AddrProp mAddrProp) {
		this.mAddrProp = mAddrProp;
	}

	public boolean isbOffSetAddr() {
		return bOffSetAddr;
	}

	public void setbOffSetAddr(boolean bOffSetAddr) {
		this.bOffSetAddr = bOffSetAddr;
	}

	public short getnRWPerm() {
		return nRWPerm;
	}

	public void setnRWPerm(short nRWPerm) {
		this.nRWPerm = nRWPerm;
	}

	public void setName(String name){
	      this.Name = new String(name);
	}
	
	public String getName(){
		return this.Name;
	}
	
	/**
	 * 获取该参数，在所有宏指令参数中的序号
	 */
	public int getnAllParamIndex() {
		return nAllParamIndex;
	}

	/**
	 * 设置该参数，在所有宏指令参数中的序号
	 */
	public void setnAllParamIndex(int nAllParamIndex) {
		this.nAllParamIndex = nAllParamIndex;
	}

	/**
	 * 获取该参数，在所属宏指令参数中的序号
	 */
	public int getnParamIndex() {
		return nParamIndex;
	}

	/**
	 * 设置该参数，在所属宏指令参数中的序号
	 */
	public void setnParamIndex(int nParamIndex) {
		this.nParamIndex = nParamIndex;
	}
}
