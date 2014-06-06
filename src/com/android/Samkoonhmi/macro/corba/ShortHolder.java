package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class ShortHolder extends PHolder{

	public short v;
	private MacroDataWR mDataWR;
	public short nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public ShortHolder(){
		mDataWR=new MacroDataWR();
	}

	public short setValue(int index, long v) {
		if (getnRWPerm()==1||getnRWPerm()==2){
			this.v = (short) v;
			
			if (this.v==nLastV) {
				if (nNum>2) {
					return this.v;
				}
			}else {
				nLastV=this.v;
				nNum=0;
			}
			nNum++;
			mDataWR.setShortData(this);
		}
		return  this.v;
	}

	public short increase(int index) {
		
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
			mDataWR.setShortData(this);
		}
		return this.v;
	}

	public short decrease(int index) {
		
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
			mDataWR.setShortData(this);
		}
		return this.v;
	}

	public short multiply(int index, long v) {
		
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
			mDataWR.setShortData(this);
		}
		return this.v;
	}

	public short divide(int index, long v) {
		
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
			mDataWR.setShortData(this);
		}
		return this.v;
	}

	public short add(int index, long v) {
		
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
			mDataWR.setShortData(this);
		}		
		return this.v;
	}

	public short plus(int index, long v) {
		
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
			mDataWR.setShortData(this);
		}
		return this.v;
	}
}
