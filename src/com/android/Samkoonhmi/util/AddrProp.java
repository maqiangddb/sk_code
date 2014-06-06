package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;


/**
 * 地址对象
 * 
 * @author Administrator
 * 
 */
public class AddrProp {
	public short eConnectType;         							       // 连接方式（内部，还是com口，net）
	public short nUserPlcId;            							   // 用户定义的PLC号
	public short nRegIndex;              						   // PLC寄存器的索引
	public int nPlcStationIndex;            						   // PLC的站号
	public int nAddrValue;                						   // PLC的起始地址值
	public int nAddrLen;                     						   // 地址的长度
	public int eAddrRWprop = READ_WRITE_COM_TYPE.OTHER_CONTROL_TYPE;    //读写等级
	public String sPlcProtocol = "";       							   // PLC的协议名
	public int nAddrId;                                                //地址id用于偏移
	
	public AddrProp clone(){
		AddrProp addrProp = new AddrProp();
		addrProp.eAddrRWprop = this.eAddrRWprop;
		addrProp.eConnectType = this.eConnectType;
		addrProp.nAddrId = this.nAddrId;
		addrProp.nAddrLen = nAddrLen;
		addrProp.nAddrValue =this.nAddrValue;
		addrProp.nPlcStationIndex = this.nPlcStationIndex;
		addrProp.nRegIndex = this.nRegIndex;
		addrProp.nUserPlcId = this.nUserPlcId;
		addrProp.sPlcProtocol = this.sPlcProtocol;
		return addrProp;
	}
}