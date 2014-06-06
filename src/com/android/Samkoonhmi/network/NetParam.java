package com.android.Samkoonhmi.network;

public class NetParam {

	//测试连接
	public static final char TEST=0x0;
	//上载，从屏上传数据到pc or 其他客户端
	public static final char UPLOAD=0x01;
	//下载，从pc or 其他客户端 下载数据到屏
	public static final char DOWN=0x02;
	
	//上载历史数据
	public static final char UP_COLLENT=0x01;
	//上载配方数据
	public static final char UP_COLLENT_REC=0x02;
}
