package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.ARRAY_ORDER;

/**
 * 消息显示器文本
 */
public class MsgTextInfo extends TextInfo {
	// 移动方向
	private ARRAY_ORDER eRemove;
	// 移动速度
	private short nSpeed;
	// 边框颜色
	private int nFrameColor;
	// 前景色
	private int nForecolor;
	// 背景色
	private int nBackcolor;
	
	public ARRAY_ORDER geteRemove() {
		return eRemove;
	}
	public void seteRemove(ARRAY_ORDER eRemove) {
		this.eRemove = eRemove;
	}
	public short getnSpeed() {
		return nSpeed;
	}
	public void setnSpeed(short nSpeed) {
		this.nSpeed = nSpeed;
	}
	public int getnFrameColor() {
		return nFrameColor;
	}
	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	public int getnForecolor() {
		return nForecolor;
	}
	public void setnForecolor(int nForecolor) {
		this.nForecolor = nForecolor;
	}
	public int getnBackcolor() {
		return nBackcolor;
	}
	public void setnBackcolor(int nBackcolor) {
		this.nBackcolor = nBackcolor;
	}
}
