package com.android.Samkoonhmi.skenum;

/**
 * 数据类型枚举
 * @author Administrator
 *
 */

public enum DATA_TYPE {
	BIT_1,           // = 0x01,        // 位
	INT_16 ,         //= 0x04,         // 16位整数
	INT_32 ,         //= 0x05,         // 32位整数
	POSITIVE_INT_16 ,//= 0x02,         // 16位正整数
	POSITIVE_INT_32 ,//= 0x03,         // 32位正整数
	BCD_16,          // = 0x06,        // 16位BCD码
	BCD_32,          // = 0x07,        // 32位BCD码
	FLOAT_32 ,       // = 0x08,        // 32位浮点数
	ASCII_STRING ,   //= 0x09,         // ASCII码
	HEX_16,          // =0x0a              //16位16进制
	HEX_32,          // =0x0B                //32位16进制
	OTC_16,          // =0x0C               //16位八进制
	OTC_32 ,         // =0x0D               //32位八进制
	OTHER_DATA_TYPE,  // =0x0E
	UNICODE_STRING		//// =0x0F			//UNICODE
}
