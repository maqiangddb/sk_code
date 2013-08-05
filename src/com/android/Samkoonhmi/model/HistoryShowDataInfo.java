package com.android.Samkoonhmi.model;

import java.util.ArrayList;

/**
 * 历史数据显示器，数据列
 */
public class HistoryShowDataInfo {

	//语言id
	private int nLanguageId; 
	//控件id
	private int nItemId;
	//数据序号
	private int nCode;
	//数据列名称
	private String sTitleDataName;
	private ArrayList<String> mTitleDataName;
	//是否显示
	private boolean bShow;
	
	public int getnLanguageId() {
		return nLanguageId;
	}
	public void setnLanguageId(int nLanguageId) {
		this.nLanguageId = nLanguageId;
	}
	public int getnItemId() {
		return nItemId;
	}
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	public int getnCode() {
		return nCode;
	}
	public void setnCode(int nCode) {
		this.nCode = nCode;
	}
	public String getsTitleDataName() {
		return sTitleDataName;
	}
	public void setsTitleDataName(String sTitleDataName) {
		this.sTitleDataName = sTitleDataName;
	}
	public boolean isbShow() {
		return bShow;
	}
	public void setbShow(boolean bShow) {
		this.bShow = bShow;
	}
	public ArrayList<String> getmTitleDataName() {
		return mTitleDataName;
	}
	public void setmTitleDataName(ArrayList<String> mTitleDataName) {
		this.mTitleDataName = mTitleDataName;
	}
}
