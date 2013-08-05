package com.android.Samkoonhmi.util;

public class RECEIVE_DATA_STRUCT {
	public int nErrorCode;          // error code or define enum.
}

//
//public static final int CMN_NOMAL_CODE = 0x00;             //通信正常
//public static final int FUN_ERROR_CODE = 0x01;             //功能码错误
//public static final int ADDR_OUT_RANG = 0x02;              //地址越界错误
//public static final int DATA_VALUE_ERROR = 0x03;           //值错误
//public static final int ADDR_UNREAD = 0x04;                //地址不可读
//public static final int ADDR_UNWRITE = 0x05;               //地址不可写
//public static final int DATA_LEN_ERROR = 0x06;             //数据长度不对
//public static final int DATA_CHECK_ERROR = 0x08;           //数据校验错误
//public static final int WRITE_DATA_FAIL = 0x09;            //写数据失败
//public static final int READ_DATA_FAIL = 0x0a;             //读数据时候失败
//public static final int WRITE_CHECK_FAIL = 0x0b;           //写返回后校验失败
//public static final int READ_CHECK_FAIL = 0x0c;            //读取的数据校验失败
//public static final int CMN_CONNECT_FAIL = 0x0d;           //通信失败，检查连线是否正确
//public static final int PROTO_NOT_EXSIT = 0x0e;            //协议不存在
//public static final int PROTO_TYPE_ERROR = 0x0f;           //协议类型出错
//public static final int UNRCV_ALL_DATA = 0x10;             //数据接收未完成，继续接收
//public static final int NO_RCV_DATA = 0x11;                //没有接收到数据
//public static final int STATION_ERROR = 0x12;              //站号不对
//public static final int ADDR_LEN_ERROR = 0x13;             //地址长度不对
//public static final int CALL_PROTO_ERROR = 0x14;           //调用协议出错
//public static final int SEND_DATA_FAILED = 0x15;           //发送数据失败
//public static final int RCV_DATA_FAILED = 0x16;            //接收数据失败
//public static final int CHANGE_BAUD_RATE_9600 = 9600;      //需要修改波特率为9600
//public static final int CHANGE_BAUD_RATE_115200 = 115200;    //需要修改波特率为115200
//public static final int OTHER_ERROR_CODE = 0xff;