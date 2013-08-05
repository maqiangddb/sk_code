package com.android.Samkoonhmi.model.sk_historytrends;

import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.HISTORYSHOW_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ChannelGroup
 * @author 李镇
 * @version v 1.0.0.1
 * 创建时间：2012-4-25
 * 最后修改时间 2012-4-25
 */

public class ChannelGroupInfo implements Parcelable{
 	private short	nNumOfChannel;	//通道号	 	
	LINE_TYPE nLineType;	//线型	 
	short nLineThickness;	//线条宽度	 
	int nDisplayColor;	//显示颜色	 	
	HISTORYSHOW_TYPE nDisplayCondition;	//显示条件	 	
	AddrProp	nDisplayAddr;	//显示ON/OFF地址	 	
	
	public ChannelGroupInfo() {
		super();
	}
	public ChannelGroupInfo(short	nNumOfChannel, LINE_TYPE nLineType,short nLineThickness,int nDisplayColor,HISTORYSHOW_TYPE nDisplayCondition,AddrProp	nDisplayAddr) {
		super();
		this.nNumOfChannel = nNumOfChannel;
		this.nLineType = nLineType;
		this.nLineThickness = nLineThickness;
		this.nDisplayColor = nDisplayColor;
		this.nDisplayCondition = nDisplayCondition;
		this.nDisplayAddr = nDisplayAddr;
	}

	public short getnNumOfChannel() {
		return nNumOfChannel;
	}
	public void setnNumOfChannel(short nNumOfChannel) {
		this.nNumOfChannel = nNumOfChannel;
	}

	public LINE_TYPE getnLineType() {
		return nLineType;
	}
	public void setnLineType(LINE_TYPE nLineType) {
		this.nLineType = nLineType;
	}
	
	public short getnLineThickness() {
		return nLineThickness;
	}
	public void setnLineThickness(short nLineThickness) {
		this.nLineThickness = nLineThickness;
	}

	public int getnDisplayColor() {
		return nDisplayColor;
	}
	public void setnDisplayColor(int nDisplayColor) {
		this.nDisplayColor = nDisplayColor;
	}

	public HISTORYSHOW_TYPE getnDisplayCondition() {
		return nDisplayCondition;
	}
	public void setnDisplayCondition(HISTORYSHOW_TYPE nDisplayCondition) {
		this.nDisplayCondition = nDisplayCondition;
	}
	
	public AddrProp getnDisplayAddr() {
		return nDisplayAddr;
	}
	public void setnDisplayAddr(AddrProp nDisplayAddr) {
		this.nDisplayAddr = nDisplayAddr;
	}
		
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
