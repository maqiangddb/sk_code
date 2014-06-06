package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class IntSeqHolder extends PHolder{

	public  int[] v = null;
	private MacroDataWR mDataWR;
	public int[] nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public IntSeqHolder(){
		mDataWR=new MacroDataWR();
	}

	public int setValue(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] = (int) v;
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return (int) this.v[index];
	}
	
	public int[] setValue(int index, int[] v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = v;
			mDataWR.setIntSeqData(this, true, index);
		}
		return this.v;
	}

	public int increase(int index) {
		
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return this.v[index];
	}

	public int decrease(int index) {
		
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return this.v[index];
	}

	public int multiply(int index, long v) {
		
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return this.v[index];
	}

	public int divide(int index, long v) {
		
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return this.v[index];
	}

	public int add(int index, long v) {
		
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return this.v[index];
	}

	public int plus(int index, long v) {
		
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
			mDataWR.setIntSeqData(this, false, index);
		}
		return this.v[index];
	}
}
