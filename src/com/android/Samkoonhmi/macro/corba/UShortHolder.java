package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class UShortHolder extends PHolder{
	public int v;
	private MacroDataWR mDataWR;
	public int nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public UShortHolder(){
		mDataWR=new MacroDataWR();
	}

	public int setValue(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = (int) v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return (int) this.v;
	}

	public int increase(int index) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v ++;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return this.v;
	}

	public int decrease(int index) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v --;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return this.v;
	}

	public int multiply(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v *= v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return this.v;
	}

	public int divide(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v /= v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return this.v;
	}

	public int add(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v += v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return this.v;
	}

	public int plus(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v -= v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUShortData(this);
		}
		return this.v;
	}
}
