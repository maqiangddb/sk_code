package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class FloatSeqHolder extends PHolder{

	public float[] v;
	private MacroDataWR mDataWR;
	public float[] nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public FloatSeqHolder(){
		mDataWR=new MacroDataWR();
	}

	public float setValue(int index, double v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] = (float) v;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return (float) this.v[index];
	}
	
	public float[] setValue(int index, float v[]) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = v;
			mDataWR.setFloatSeqData(this, true, index);
		}
		return  this.v;
	}

	public float increase(int index) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index]++;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return this.v[index];
	}

	public float decrease(int index) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index]--;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return this.v[index];
	}

	public float multiply(int index, double v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] *= v;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return this.v[index];
	}

	public float divide(int index, double v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] /= v;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return this.v[index];
	}

	public float add(int index, double v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] += v;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return this.v[index];
	}

	public float plus(int index, double v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] -= v;
			if (nLastV!=null) {
				if (this.v[index]==nLastV[index]) {
					if (nNum>2) {
						return this.v[index];
					}
				}else {
					nLastV[index]=this.v[index];
					nNum=0;
				}
				nNum++;
			}
			mDataWR.setFloatSeqData(this, false, index);
		}
		return this.v[index];
	}
}
