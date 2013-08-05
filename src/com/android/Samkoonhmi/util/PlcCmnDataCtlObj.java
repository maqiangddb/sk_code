package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.plccommunicate.SKCommThread.ICmnCompletedCallback;

/**
 * PLC通信的数据操作，发送数据，接收数据等
 * @author latory
 *
 */
public class PlcCmnDataCtlObj {
	public ICmnCompletedCallback Icallback = null;                            //通信完成的回调接口
	public AddrPropArray mAddrList = null;                                    //通信地址的集合
	public Object mDataObj = null;                                            //当前配方
}
