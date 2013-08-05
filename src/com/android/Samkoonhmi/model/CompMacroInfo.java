package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.util.MACRO_TYPE;

/**
 * 控件宏指令数据实体类
 * */
public class CompMacroInfo extends BaseMacroInfo{
	
	protected int CompID;      
	
	
	public CompMacroInfo(){
		this.setMacroType(MACRO_TYPE.COMP);//类型为控件宏
	}
	
	/**
	 * 设置宏所在控件的ID
	 * */
	public void setCompID(int id){
		CompID = id;
	}
	
	/**
	 * 获得宏所在的控件ID
	 * */
	public int getCompID(){
		return CompID;
	}
}
