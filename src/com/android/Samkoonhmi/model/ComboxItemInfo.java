package com.android.Samkoonhmi.model;

import java.util.Map;

import com.android.Samkoonhmi.skenum.BUTTON;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKButton;

import android.R.integer;
import android.os.Parcel;
import android.os.Parcelable;

public class ComboxItemInfo implements Parcelable {
	private int id;// 下拉框Id
	private int functionId;// 功能Id
	private BUTTON.BUTTON_TYPE eFunctionType;
	private int functionNameId;// 功能名字多语言Id
	private boolean isChecked;
	private SKButton button;// 按钮对象
	private String picPath;//图片路径
	private Map<Integer, String> functionNames;
	private boolean bSaveIndex;//是否执行功能了
	


	public boolean isbSaveIndex() {
		return bSaveIndex;
	}

	public void setbSaveIndex(boolean bSaveIndex) {
		this.bSaveIndex = bSaveIndex;
	}

	public Map<Integer, String> getFunctionNames() {
		return functionNames;
	}

	public void setFunctionNames(Map<Integer, String> functionNames) {
		this.functionNames = functionNames;
	}

	public ComboxItemInfo() {
		super();
	}

	public SKButton getButton() {
		return button;
	}

	public void setButton(SKButton button) {
		this.button = button;
	}



	public int getFunctionNameId() {
		return functionNameId;
	}

	public void setFunctionNameId(int functionNameId) {
		this.functionNameId = functionNameId;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFunctionId() {
		return functionId;
	}

	public void setFunctionId(int functionId) {
		this.functionId = functionId;
	}

	public BUTTON.BUTTON_TYPE geteFunctionType() {
		return eFunctionType;
	}

	public void seteFunctionType(BUTTON.BUTTON_TYPE eFunctionType) {
		this.eFunctionType = eFunctionType;
	}
	
	public void setPicPath(String path){
		this.picPath=path;
	}
	
	public String getPicPath(){
		return this.picPath;
	}
}
