package com.android.Samkoonhmi.util;

/**
 * 宏指令参数类型
 * */
public class MPTYPE {
	public final static short MIN      = 0;  //下限
	public final static short BOOL     = 1;  //布尔类型
	public final static short BOOLSEQ  = 2;  //批量布尔类型
	public final static short SHORT    = 3;  //16位整型
	public final static short SHORTSEQ = 4;  //批量16位整型数据
	public final static short INT      = 5;  //32位整型
	public final static short INTSEQ   = 6;  //批量32位整型数据
	public final static short FLOAT    = 7;  //单精度浮点数
	public final static short FLOATSEQ = 8;  //批量单精度浮点数
	public final static short STRING   = 9;  //字符串类型
	public final static short CMN      = 10; //通用字节类型
	public final static short UINT     = 11; //无符号32位整型
	public final static short UINTSEQ  = 12; //批量无符号32位整型
	public final static short USHORT   = 13; //无符号16位整型
	public final static short USHORTSEQ= 14; //批量无符号16位整型
	public final static short BCD16    = 15; //BCD16数
	public final static short BCD16SEQ = 16; //批量BCD16数
	public final static short BCD32    = 17; //BCD32数
	public final static short BCD32SEQ = 18; //批量BCD32数
	public final static short MAX      = 20; //上限
}
