package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;


public class BoolSeqHolder extends PHolder{
	public boolean[] v;
	private MacroDataWR mDataWR;
	public boolean[] nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public BoolSeqHolder(){
		mDataWR=new MacroDataWR();
	}

	public boolean setValue(int index, boolean v) {
		
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
			mDataWR.setBooleanSeqData(this, false, index);
		}
		return this.v[index];
	}
	
	public boolean[] setValue(int index, boolean[] v) {
		this.v = v;
		if (getnRWPerm()==1||getnRWPerm()==2){
			mDataWR.setBooleanSeqData(this, true, index);
		}
		return this.v;
	}
}
