package com.android.Samkoonhmi.macro.corba;

import com.android.Samkoonhmi.macro.MacroDataWR;

public class StringHolder extends PHolder{

	public short nCode;
	public String v;
	private MacroDataWR mDataWR;
	public String nLastV;//之前地址值,为了防止频繁写入相同地址值，造成通信阻塞，所以需要进行判断写入
	private short nNum;//相同地址值写入次数，最多3次
	
	public StringHolder(){
		mDataWR=new MacroDataWR();
	}
	
	public String setValue(int index, String v) {
		
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
			mDataWR.setStringData(this);
		}
		return this.v;
	}
	
	public String add(int index, String v) {
		if(v!=null){
			if (getnRWPerm()==1||getnRWPerm()==2){
				this.v+= v;
				if (this.v==nLastV) {
					if (nNum>2) {
						return this.v;
					}
				}else {
					nLastV=this.v;
					nNum=0;
				}
				nNum++;
				mDataWR.setStringData(this);
			}
		}
		return this.v;
	}
}
