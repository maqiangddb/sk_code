package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class Bcd32SeqHolder extends PHolder{

	public  long[] v = null;
	private MacroDataWR mDataWR;
	public long[] nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public Bcd32SeqHolder(){
		mDataWR=new MacroDataWR();
	}

	public long setValue(int index, long v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] = v;
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}
	
	public long[] setValue(int index, long[] v) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = v;
			mDataWR.setBCD32SeqData(this, true, index);
		}
		return this.v;
	}

	public long increase(int index) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] ++;
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}

	public long decrease(int index) {
		
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v[index] --;
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}

	public long multiply(int index, long v) {
		
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}

	public long divide(int index, long v) {
		
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}

	public long add(int index, long v) {
		
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}

	public long plus(int index, long v) {
		
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
			mDataWR.setBCD32SeqData(this, false, index);
		}
		return this.v[index];
	}
}
