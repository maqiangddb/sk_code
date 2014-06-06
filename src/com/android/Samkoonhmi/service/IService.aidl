package com.android.Samkoonhmi.service;

import com.android.Samkoonhmi.service.ICallback;
import android.os.Parcelable;
import com.android.Samkoonhmi.service.AddrInfo;

interface IService{

    //注册回调接口
    void registerCallback(ICallback callback);
    
    //删除回调接口
    void unregisterCallback(ICallback callback);    
    
     //设置所有读写地址
    boolean setAddr(in List<AddrInfo> mAddrList);
    
    //获取位地址的值
    boolean getBytesData(in AddrInfo mAddrProp,out List<String> dataList,int AddrType);
    
    //获取32位字地址的值
    boolean getLongData(in AddrInfo mAddrProp,out List<String> dataList,int AddrType);
    
    //获取16位字地址的值
    boolean getShortData(in AddrInfo mAddrProp,out List<String> dataList,int AddrType);
    
    //获取32位地址浮点数的值
    boolean getFloatData(in AddrInfo mAddrProp,out List<String> dataList,int AddrType);
    
    //写地址值
    boolean setAddrData(in AddrInfo mAddrProp,in String data,int AddrType);
      
}