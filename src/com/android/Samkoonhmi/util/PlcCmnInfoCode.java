package com.android.Samkoonhmi.util;

import android.app.Activity;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

public class PlcCmnInfoCode {
	public static final int CMN_NOMAL_CODE = 0;             //通信正常
	public static final int FUN_ERROR_CODE = 1;             //功能码错误
	public static final int ADDR_OUT_RANG = 2;              //地址越界错误
	public static final int DATA_VALUE_ERROR = 3;           //值错误
	public static final int ADDR_UNREAD = 4;                //地址不可读
	public static final int ADDR_UNWRITE = 5;               //地址不可写
	public static final int DATA_LEN_ERROR = 6;             //数据长度不对
	public static final int CHECK_OK_UNRCV_ALL = 7;         //校验成功但是长度不够
	public static final int DATA_CHECK_ERROR = 8;           //数据校验错误
	public static final int WRITE_DATA_FAIL = 9;            //写数据失败
	public static final int READ_DATA_FAIL = 10;             //读数据时候失败
	public static final int WRITE_CHECK_FAIL = 11;           //写返回后校验失败
	public static final int READ_CHECK_FAIL = 12;            //读取的数据校验失败
	public static final int CMN_CONNECT_FAIL = 13;           //通信失败，检查连线是否正确
	public static final int PROTO_NOT_EXSIT = 14;            //协议不存在
	public static final int PROTO_TYPE_ERROR = 15;           //协议类型出错
	public static final int UNRCV_ALL_DATA = 16;             //数据接收未完成，继续接收
	public static final int NO_RCV_DATA = 17;                //没有接收到数据
	public static final int STATION_ERROR = 18;              //站号不对
	public static final int ADDR_LEN_ERROR = 19;             //地址长度不对
	public static final int CALL_PROTO_ERROR = 20;           //调用协议出错
	public static final int SEND_DATA_FAILED = 21;           //发送数据失败
	public static final int RCV_DATA_FAILED = 22;            //接收数据失败
	public static final int CHANGE_BAUD_RATE_9600 = 9600;      //需要修改波特率为9600
	public static final int CHANGE_BAUD_RATE_115200 = 115200;    //需要修改波特率为115200
	public static final int OTHER_ERROR_CODE = 0xff;
	
	public static String getErrorInfo(int nErrorCode)
	{
		String sErrorInfo = "";
		Activity myActivity = SKSceneManage.getInstance().getActivity();
		if(null == myActivity) return "";
		
		switch(nErrorCode)
		{
		case PlcCmnInfoCode.CMN_NOMAL_CODE:
		{
			sErrorInfo = myActivity.getString(R.string.cmn_nomal);
			break;
		}
		case PlcCmnInfoCode.FUN_ERROR_CODE:
		{
			sErrorInfo = myActivity.getString(R.string.fun_error);
			break;
		}
		case PlcCmnInfoCode.ADDR_OUT_RANG:
		{
			sErrorInfo = myActivity.getString(R.string.addr_rang);
			break;
		}
		case PlcCmnInfoCode.DATA_VALUE_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.value_error);
			break;
		}
		case PlcCmnInfoCode.ADDR_UNREAD:
		{
			sErrorInfo = myActivity.getString(R.string.addr_uread);
			break;
		}
		case PlcCmnInfoCode.ADDR_UNWRITE:
		{
			sErrorInfo = myActivity.getString(R.string.addr_uwrite);
			break;
		}
		case PlcCmnInfoCode.DATA_LEN_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.data_len_error);
			break;
		}
		case PlcCmnInfoCode.CHECK_OK_UNRCV_ALL:
		{
			sErrorInfo = myActivity.getString(R.string.check_ok_urcv_all);
			break;
		}
		case PlcCmnInfoCode.DATA_CHECK_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.data_check_error);
			break;
		}
		case PlcCmnInfoCode.WRITE_DATA_FAIL:
		{
			sErrorInfo = myActivity.getString(R.string.write_send_fail);
			break;
		}
		case PlcCmnInfoCode.READ_DATA_FAIL:
		{
			sErrorInfo = myActivity.getString(R.string.read_recive_fail);
			break;
		}
		case PlcCmnInfoCode.WRITE_CHECK_FAIL:
		{
			sErrorInfo = myActivity.getString(R.string.write_check_fail);
			break;
		}
		case PlcCmnInfoCode.READ_CHECK_FAIL:
		{
			sErrorInfo = myActivity.getString(R.string.read_check_fail);
			break;
		}
		case PlcCmnInfoCode.CMN_CONNECT_FAIL:
		{
			sErrorInfo = myActivity.getString(R.string.cmn_connect_fail);
			break;
		}
		case PlcCmnInfoCode.PROTO_NOT_EXSIT:
		{
			sErrorInfo = myActivity.getString(R.string.proto_not_exsit);
			break;
		}
		case PlcCmnInfoCode.PROTO_TYPE_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.proto_type_error);
			break;
		}
		case PlcCmnInfoCode.UNRCV_ALL_DATA:
		{
			sErrorInfo = myActivity.getString(R.string.unrcv_all_data);
			break;
		}
		case PlcCmnInfoCode.NO_RCV_DATA:
		{
			sErrorInfo = myActivity.getString(R.string.no_rcv_data);
			break;
		}
		case PlcCmnInfoCode.STATION_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.station_error);
			break;
		}
		case PlcCmnInfoCode.ADDR_LEN_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.addr_len_error);
			break;
		}
		case PlcCmnInfoCode.CALL_PROTO_ERROR:
		{
			sErrorInfo = myActivity.getString(R.string.call_proto_error);
			break;
		}
		case PlcCmnInfoCode.SEND_DATA_FAILED:
		{
			sErrorInfo = myActivity.getString(R.string.send_data_fail);
			break;
		}
		case PlcCmnInfoCode.RCV_DATA_FAILED:
		{
			sErrorInfo = myActivity.getString(R.string.rcv_data_fail);
			break;
		}
		case PlcCmnInfoCode.CHANGE_BAUD_RATE_9600:
		{
			sErrorInfo = myActivity.getString(R.string.change_rate_9600); 
			break;
		}
		case PlcCmnInfoCode.CHANGE_BAUD_RATE_115200:
		{
			sErrorInfo = myActivity.getString(R.string.change_rate_115200); 
			break;
		}
		default:
		{
			sErrorInfo = myActivity.getString(R.string.default_code); 
			break;
		}
		}
		
		return sErrorInfo;
	}
}
