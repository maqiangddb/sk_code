package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.util.MACRO_TYPE;

/**
 * 初始化宏指令数据实体类
 * */
public class InitMacroInfo extends BaseMacroInfo{

	public InitMacroInfo(){
		this.setMacroType(MACRO_TYPE.INIT);//设置宏类型为初始化宏指令
	}
}
