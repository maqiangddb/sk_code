package com.android.Samkoonhmi.util;

/**
 * 宏指令管理器服务类型
 * */
public class MSERV {
	public final static short DUMMY  = 0;  //哑服务
	public final static short INIT   = 1;  //请求初始化
	
	public final static short CALLIM = 2;  //请求执行初始宏指令
	public final static short CALLGM = 3;  //请求执行全局宏指令
	public final static short CALLSM = 4;  //请求执行场景宏指令
	public final static short CALLCM = 5;  //请求执行控件宏指令
	
	public final static short ENDRGM = 6;  //请求停止执行全局宏指令
	public final static short ENDRSM = 7;  //请求停止执行场景宏指令
	
	public final static short MAX    = 8; //服务上限
}
