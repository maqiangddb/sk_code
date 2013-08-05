package com.android.Samkoonhmi.util;

/**
 * 定义宏指令的类型
 * */
public class MACRO_TYPE {
	public final static short INVALID    = 0; //类型无效
	public final static short GLOOP      = 1; //全局循环执行宏
	public final static short GCTRLOOP   = 2; //全局受控循环执行宏
	public final static short SLOOP      = 3; //场景循环执行宏
	public final static short SCTRLOOP   = 4; //场景循环执行宏
	public final static short INIT       = 5; //初始化宏
	public final static short COMP       = 6; //控件宏
	public final static short MAX        = 7; //类型上限
}
