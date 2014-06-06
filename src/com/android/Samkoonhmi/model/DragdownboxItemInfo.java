package com.android.Samkoonhmi.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Rect;

import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.util.AddrProp;

public class DragdownboxItemInfo {	
	private int Id;	//控件Id
	private Rect mRect;//控件位置
	private int nMaxState; //总状态数
	private DATA_TYPE eDateType; // 数据类型
	private AddrProp mBaseAddress; // 监控字地址
	private AddrProp mOffSetAddr; //地址偏移
	private AddrProp mActualAddress; //偏移后监视地址值
	private boolean bScriptSet; //是否启动宏指令
	private int nScriptId; //脚本库id
	private String sBackgroundImg; // 背景图片
	private int nBackgroundColor; //背景颜色
	private int Alpha; //透明度
	private boolean bFirstLan;//多语言文本属性是否使用第一种语言属性
	private ArrayList<TextInfo> mTextAttrList;//多语言文本属性
	//private ArrayList<ArrayList<String>> message;//
	private ArrayList<StakeoutInfo> mStakeoutList; //值
	private TouchInfo touchInfo;// 触控属性
	private ShowInfo showInfo;// 显现属性
	private int nZvalue ;// 层次
	private int nCollidindId ;//组合
	private boolean isChecked;
	
	
	public ArrayList<TextInfo> getmTextAttrList() {
		return mTextAttrList;
	}
	public void setmTextAttrList(ArrayList<TextInfo> mTextAttrList) {
		this.mTextAttrList = mTextAttrList;
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public void setId(int id){
		Id = id;
	}
	public int getId(){
		return Id;
	}
	
	public void setRect(Rect rect){
		mRect = rect;
	}
	public Rect getRect(){
		return mRect;
	}
	
	public void setMaxState(int max){
		nMaxState = max;
	}
	public int getMaxState(){
		return nMaxState;
	}
	
	public void setDateType(DATA_TYPE type){
		eDateType = type;
	}
	public DATA_TYPE getDateType(){
		return eDateType;
	}
	
	public void setBaseAddress(AddrProp address){
		mBaseAddress = address;
	}
	public AddrProp getBaseAddress(){
		return mBaseAddress;
	}
	
	public void setOffSetAddr(AddrProp address){
		mOffSetAddr = address;
	}
	public AddrProp getOffSetAddr(){
		return mOffSetAddr;
	}
	
	public void setActualAddress(AddrProp address){
		mActualAddress = address;
	}
	public AddrProp getActualAddress(){
		return mActualAddress;
	}
	
	public void setScript(boolean set){
		bScriptSet = set;
	}
	public boolean getScript(){
		return bScriptSet;
	}
	
	public void setScriptId(int id){
		nScriptId = id;
	}
	public int getScriptId(){
		return nScriptId;
	}
	
	public void setBackgroundImg(String img){
		sBackgroundImg = img;
	}
	public String getBackgroundImg(){
		return sBackgroundImg;
	}
	
	public void setBackgroundColor(int color){
		nBackgroundColor = color;
	}
	public int getBackgroundColor(){
		return nBackgroundColor;
	}
	
	public void setAlpha(int alpha){
		Alpha = alpha;
	}
	public int getAlpha(){
		return Alpha;
	}
	
	public void setUseFirstLanAttr(boolean set){
		bFirstLan = set;
	}
	public boolean getUseFirstLanAttr(){
		return bFirstLan;
	}
	
		
	public void setTouchInfo(TouchInfo info){
		touchInfo = info;
	}
	public TouchInfo getTouchInfo(){
		return touchInfo;
	}
	
	public void setShowInfo(ShowInfo info){
		showInfo = info;
	}
	public ShowInfo getShowInfo(){
		return showInfo;
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
	
	public void setmStakeoutList(ArrayList<StakeoutInfo> mStakeoutList) {
		this.mStakeoutList = mStakeoutList;
	}
	public ArrayList<StakeoutInfo> getmStakeoutList(){
		return this.mStakeoutList;
	}
	
}
