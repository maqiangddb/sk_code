package com.android.Samkoonhmi.model;
/**
 * plc协议属性
 * @author Administrator
 *
 */
public class PlcAttributeInfo {
	private int id;
	private int nConnectIndex;//连接Id,与plcConnectionInfo中的id关联
	private String sPlcServiceType;//	连接服务的
	private short nPlcNo;//	Plc站号
	private short nReceive_Timeout;//	接收超时（ms）
    private int 	nMaxRWLen;//	最大读写长度
	private short nRetryTime;//	重试次数
	private short nIntervalTime;//	帧间隔时间（ms）
	private int nMinCollectCycle;//最小采集周期
	private int nUserPlcId;//用户自定义PLC编号
	private String sIpAddr;//	Ip地址中间用“.”分割;
	private int nNetPortNum;//	网络通信的端口号
	private boolean bTcpNet;//true :TCP协议   false: UDP协议
	
	public boolean getIsNetTcp() {
		return bTcpNet;
	}
	
	public void setIsNetTcp(boolean bIsTcpNet) {
		this.bTcpNet = bIsTcpNet;
	}
	
	public String getsIpAddr() {
		return sIpAddr;
	}

	public void setsIpAddr(String sIpAddr) {
		this.sIpAddr = sIpAddr;
	}

	public int getnNetPortNum() {
		return nNetPortNum;
	}

	public void setnNetPortNum(int nNetPortNum) {
		this.nNetPortNum = nNetPortNum;
	}

	public PlcAttributeInfo() {
		super();
	}
	
	public int getnUserPlcId() {
		return nUserPlcId;
	}

	public void setnUserPlcId(int nUserPlcId) {
		this.nUserPlcId = nUserPlcId;
	}

	public short getnIntervalTime() {
		return nIntervalTime;
	}

	public void setnIntervalTime(short nIntervalTime) {
		this.nIntervalTime = nIntervalTime;
	}

	public int getnMinCollectCycle() {
		return nMinCollectCycle;
	}

	public void setnMinCollectCycle(int nMinCollectCycle) {
		this.nMinCollectCycle = nMinCollectCycle;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getnConnectIndex() {
		return nConnectIndex;
	}
	public void setnConnectIndex(int nConnectIndex) {
		this.nConnectIndex = nConnectIndex;
	}
	public String getsPlcServiceType() {
		return sPlcServiceType;
	}
	public void setsPlcServiceType(String sPlcServiceType) {
		this.sPlcServiceType = sPlcServiceType;
	}
	public short getnPlcNo() {
		return nPlcNo;
	}
	public void setnPlcNo(short nPlcNo) {
		this.nPlcNo = nPlcNo;
	}
	public short getnReceive_Timeout() {
		return nReceive_Timeout;
	}
	public void setnReceive_Timeout(short nReceive_Timeout) {
		this.nReceive_Timeout = nReceive_Timeout;
	}
	public int getnMaxRWLen() {
		return nMaxRWLen;
	}
	public void setnMaxRWLen(int nMaxRWLen) {
		this.nMaxRWLen = nMaxRWLen;
	}
	public short getnRetryTime() {
		return nRetryTime;
	}
	public void setnRetryTime(short nRetryTime) {
		this.nRetryTime = nRetryTime;
	}



}
