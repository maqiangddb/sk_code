package com.android.Samkoonhmi.util;

/**
 * 宏指令管理器运行状态
 * */
public class MMSTATE {
	public final static short INIT   = 0; //初始态，仅响应初始化请求
	public final static short READY  = 1; //就绪态，可响应全部请求
	public final static short STOP   = 2; //停止态, 不响应请求
}
