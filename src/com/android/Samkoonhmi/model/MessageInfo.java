package com.android.Samkoonhmi.model;

import java.util.ArrayList;

import com.android.Samkoonhmi.skenum.ARRAY_ORDER;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 消息显示器实体对象
 * @author 刘伟江
 * @version v 1.0.0.3
 * 创建时间 2012-5-19
 * 最后修改 2012-6-20
 */
public class MessageInfo {

	//控件id
	private int nItemId;
	//层id
	private int nZvalue;
	//控件组合Id
	private int nCollidindId;
	//外形
	private String sShape;
	//数据类型
	private DATA_TYPE eDataType;
	//监视地址
	private AddrProp eAddress;
	//总太数
	private short nStateCount;
	//是否使用第一语言
	private boolean bFirstLanguage;
	//是否使用0状态
	private boolean bStateZero;
	//透明度
	private short nAlpha;
	//控件左顶点X坐标
	private short nLeftTopX;
	//控件左顶点Y坐标
	private short nLeftTopY;
	//控件宽
	private short nWidth;
	//控件高
	private short nHeight;
	//显示左顶点X坐标
	private short nShowLeftTopX;
	//显示左顶点Ｙ坐标
	private short nShowLeftTopY;
	//显示区域宽
	private short nShowWidth;
	//显示区域高
	private short nShowHeight;
	//显现
	private ShowInfo mShowInfo;
	//所有状态文本
	private ArrayList<MsgTextInfo> mTextList;
	
	public short getnAlpha() {
		return nAlpha;
	}
	public void setnAlpha(short nAlpha) {
		this.nAlpha = nAlpha;
	}
	
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}
	
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public int getnZvalue() {
		return nZvalue;
	}
	public void setnZvalue(int nZvalue) {
		this.nZvalue = nZvalue;
	}
	public int getnCollidindId() {
		return nCollidindId;
	}
	public void setnCollidindId(int nCollidindId) {
		this.nCollidindId = nCollidindId;
	}
	public String getsShape() {
		return sShape;
	}
	public void setsShape(String sShape) {
		this.sShape = sShape;
	}
	public DATA_TYPE geteDataType() {
		return eDataType;
	}
	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}
	public AddrProp geteAddress() {
		return eAddress;
	}
	public void seteAddress(AddrProp eAddress) {
		this.eAddress = eAddress;
	}
	public short getnStateCount() {
		return nStateCount;
	}
	public void setnStateCount(short nStateCount) {
		this.nStateCount = nStateCount;
	}
	public boolean isbFirstLanguage() {
		return bFirstLanguage;
	}
	public void setbFirstLanguage(boolean bFirstLanguage) {
		this.bFirstLanguage = bFirstLanguage;
	}
	public boolean isbStateZero() {
		return bStateZero;
	}
	public void setbStateZero(boolean bStateZero) {
		this.bStateZero = bStateZero;
	}
	public short getnLeftTopX() {
		return nLeftTopX;
	}
	public void setnLeftTopX(short nLeftTopX) {
		this.nLeftTopX = nLeftTopX;
	}
	public short getnLeftTopY() {
		return nLeftTopY;
	}
	public void setnLeftTopY(short nLeftTopY) {
		this.nLeftTopY = nLeftTopY;
	}
	public short getnWidth() {
		return nWidth;
	}
	public void setnWidth(short nWidth) {
		this.nWidth = nWidth;
	}
	public short getnHeight() {
		return nHeight;
	}
	public void setnHeight(short nHeight) {
		this.nHeight = nHeight;
	}
	public short getnShowLeftTopX() {
		return nShowLeftTopX;
	}
	public void setnShowLeftTopX(short nShowLeftTopX) {
		this.nShowLeftTopX = nShowLeftTopX;
	}
	public short getnShowLeftTopY() {
		return nShowLeftTopY;
	}
	public void setnShowLeftTopY(short nShowLeftTopY) {
		this.nShowLeftTopY = nShowLeftTopY;
	}
	public short getnShowWidth() {
		return nShowWidth;
	}
	public void setnShowWidth(short nShowWidth) {
		this.nShowWidth = nShowWidth;
	}
	public short getnShowHeight() {
		return nShowHeight;
	}
	public void setnShowHeight(short nShowHeight) {
		this.nShowHeight = nShowHeight;
	}
	public ArrayList<MsgTextInfo> getmTextList() {
		return mTextList;
	}
	public void setmTextList(ArrayList<MsgTextInfo> mTextList) {
		this.mTextList = mTextList;
	}
}
