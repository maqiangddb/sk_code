package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class FloatHolder extends PHolder{

	public float v;
	private MacroDataWR mDataWR;
	public float nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public FloatHolder(){
		mDataWR=new MacroDataWR();
	}

	public float setValue(int index, double v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = (float) v;
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setFloatData(this);
		}
		return (float) this.v;
	}

	public float increase(int index) {
		
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
			mDataWR.setFloatData(this);
		}
		return this.v;
	}

	public float decrease(int index) {
		
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
			mDataWR.setFloatData(this);
		}
		return this.v;
	}

	public float multiply(int index, double v) {
		
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
			mDataWR.setFloatData(this);
		}
		return this.v;
	}

	public float divide(int index, double v) {
		
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
			mDataWR.setFloatData(this);
		}
		return this.v;
	}

	public float add(int index, double v) {
		
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
			mDataWR.setFloatData(this);
		}
		return this.v;
	}

	public float plus(int index, double v) {
		
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
			mDataWR.setFloatData(this);
		}
		return this.v;
	}
}
