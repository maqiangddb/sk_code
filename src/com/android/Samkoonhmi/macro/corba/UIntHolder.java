package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class UIntHolder extends PHolder{

	public long v;
	private MacroDataWR mDataWR;
	public long nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public UIntHolder(){
		mDataWR=new MacroDataWR();
	}

	public long setValue(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setUIntData(this);
		}
		return this.v;
	}

	public long increase(int index) {
		
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
			mDataWR.setUIntData(this);
		}
		return this.v;
	}

	public long decrease(int index) {
		
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
			mDataWR.setUIntData(this);
		}
		return this.v;
	}

	public long multiply(int index, long v) {
		
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
			mDataWR.setUIntData(this);
		}
		return this.v;
	}

	public long divide(int index, long v) {
		
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
			mDataWR.setUIntData(this);
		}
		return this.v;
	}

	public long add(int index, long v) {
		
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
			mDataWR.setUIntData(this);
		}
		return this.v;
	}

	public long plus(int index, long v) {
		
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
			mDataWR.setUIntData(this);
		}
		return this.v;
	}
}
