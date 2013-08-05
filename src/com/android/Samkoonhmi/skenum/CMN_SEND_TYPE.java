package com.android.Samkoonhmi.skenum;

public class CMN_SEND_TYPE {
	public static final int CMN_READ = 0x01;               //通信读操作
	public static final int CMN_WRITE = 0x02;              //通信写操作
	public static final int CMN_READ_BEFORE_WRITE = 0x03;         //写之前的读操作
	public static final int CMN_WRITE_AFTER_READ = 0x04;          //读之后的写操作
	public static final int CMN_OTHER_TYPE = 0x05;                 //其他方式
}
