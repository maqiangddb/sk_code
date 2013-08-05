package com.android.Samkoonhmi.model;

import java.util.List;

import com.android.Samkoonhmi.skenum.CONNECT_TYPE;

/**
 * 连接属性类
 * @author 瞿丽平
 *
 */
public class PlcConnectionInfo {
	private int id;
	private int nConnectId ;//连接Id
	private String sConnectName;//	连接名称
	private short eConnectPort;//连接端口
	private boolean bUseRelationPort = false;//	是否需要直接转发数据;
	private short eRelationPort;//关联口
	private short nScreenNo;//屏号
	private int nBaudRate;//	波特率
	private short nDataBits;//	数据位数
	private int nCheckType	;//校验方式 0:NONE, 1:EVEN, 2:ODD
	private short nStopBit;//	停止位 1-2
	
	private boolean bMasterScreen = false;
	private boolean bConnectScreenPort = false;
	private int nSlaveScreenNum = 0;

	public boolean isbMasterScreen() {
		return bMasterScreen;
	}

	public void setbMasterScreen(boolean bMasterScreen) {
		this.bMasterScreen = bMasterScreen;
	}

	public boolean isbConnectScreenPort() {
		return bConnectScreenPort;
	}

	public void setbConnectScreenPort(boolean bConnectScreenPort) {
		this.bConnectScreenPort = bConnectScreenPort;
	}

	public int getnSlaveScreenNum() {
		return nSlaveScreenNum;
	}

	public void setnSlaveScreenNum(int nSlaveScreenNum) {
		this.nSlaveScreenNum = nSlaveScreenNum;
	}
	private  List<PlcAttributeInfo> plcAttributeList;    // plc不同协议属性
	
	public PlcConnectionInfo() {
		super();
	}

	public  List<PlcAttributeInfo> getPlcAttributeList() {
		return plcAttributeList;
	}

	public  void setPlcAttributeList(List<PlcAttributeInfo> plcAttributeList) {
		this.plcAttributeList = plcAttributeList;
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getnConnectId() {
		return nConnectId;
	}
	public void setnConnectId(int nConnectId) {
		this.nConnectId = nConnectId;
	}
	public String getsConnectName() {
		return sConnectName;
	}
	public void setsConnectName(String sConnectName) {
		this.sConnectName = sConnectName;
	}
	public short geteConnectPort() {
		return eConnectPort;
	}
	public void seteConnectPort(short eConnectPort) {
		this.eConnectPort = eConnectPort;
	}
	public boolean isbUseRelationPort() {
		return bUseRelationPort;
	}
	public void setbUseRelationPort(boolean bUseRelationPort) {
		this.bUseRelationPort = bUseRelationPort;
	}
	public short geteRelationPort() {
		return eRelationPort;
	}
	public void seteRelationPort(short eRelationPort) {
		this.eRelationPort = eRelationPort;
	}
	public short getnScreenNo() {
		return nScreenNo;
	}
	public void setnScreenNo(short nScreenNo) {
		this.nScreenNo = nScreenNo;
	}
	public int getnBaudRate() {
		return nBaudRate;
	}
	public void setnBaudRate(int nBaudRate) {
		this.nBaudRate = nBaudRate;
	}
	public short getnDataBits() {
		return nDataBits;
	}
	public void setnDataBits(short nDataBits) {
		this.nDataBits = nDataBits;
	}
	public int getnCheckType() {
		return nCheckType;
	}
	public void setnCheckType(int nCheckType) {
		this.nCheckType = nCheckType;
	}
	public short getnStopBit() {
		return nStopBit;
	}
	public void setnStopBit(short nStopBit) {
		this.nStopBit = nStopBit;
	}
	

}
