package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class ShortSeqHolder extends PHolder{

	public  short[] v = null;
	private MacroDataWR mDataWR;
	public short[] nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public ShortSeqHolder(){
		mDataWR=new MacroDataWR();
	}

	public short setValue(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] = (short) v;
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return (short) this.v[index];
	}
	
	public short[] setValue(int index, short[] v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = v;
			mDataWR.setShortSeqData(this, true, index);
		}
		return this.v;
	}

	public short increase(int index) {
		
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return this.v[index];
	}

	public short decrease(int index) {
		
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return this.v[index];
	}

	public short multiply(int index, long v) {
		
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return this.v[index];
	}

	public short divide(int index, long v) {
		
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return this.v[index];
	}

	public short add(int index, long v) {
		
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return this.v[index];
	}

	public short plus(int index, long v) {
		
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
			mDataWR.setShortSeqData(this, false, index);
		}
		return this.v[index];
	}
}
