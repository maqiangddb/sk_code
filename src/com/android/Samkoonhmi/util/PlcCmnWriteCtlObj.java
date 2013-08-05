package com.android.Samkoonhmi.util;

import java.util.Vector;

import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.plccommunicate.SKCommThread.ICmnCompletedCallback;

public class PlcCmnWriteCtlObj {
	public boolean bCallback = false;
	public ICmnCompletedCallback Icallback = null;                            //通信完成的回调接口
	public AddrProp mAddrProp = null;                                         //通信地址的集合
	public byte[] nDataList = null;                                    //通信数据的byte集合
	public Object mDataObj = null;                                         //当前配方
}
