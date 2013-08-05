package com.android.Samkoonhmi.skenum;

/**
 * 触发画面or窗口跳转的类型
 */
public enum GOTO_TYPE {

	BUTTON(1),//开关
	SIDE(2),//滑动
	PARAMETER(3),//内部地址
	ALARM(4),//报警
	SYSTEM(5);//系统
	
	int value;
	GOTO_TYPE(int value){
		this.value=value;
	}
	
	public int getValue(){
		return value;
	}
}
