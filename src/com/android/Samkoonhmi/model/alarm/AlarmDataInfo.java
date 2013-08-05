package com.android.Samkoonhmi.model.alarm;

/**
 * 报警数据
 */
public class AlarmDataInfo {

	private int id;            //id
	private int nGroupId;      //报警组Id
	private int nAlarmIndex;   //报警在报警组中的序号
	private String sMessage;   //报警消息
	private long nDateTime;    //报警产生的时间日期
	private long nClearDT;     //报警消除的时间日期
	private short  nClear;     //报警确定，-1-未触发，0-已经触发，1-已经确定,2-已经消除
	private String sDate;     
	private String sTime;
	private String sCDate;
	private String sCTime;
	private boolean bAddtoDB; // 报警信息 是否保存数据库

	
	public void setbAddtoDB(boolean add){
		bAddtoDB = add;
	}
	public boolean getbAddtoDb(){
		return bAddtoDB;
	}
	public String getsDate() {
		return sDate;
	}
	public void setsDate(String sDate) {
		this.sDate = sDate;
	}
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	public String getsCDate() {
		return sCDate;
	}
	public void setsCDate(String sCDate) {
		this.sCDate = sCDate;
	}
	public String getsCTime() {
		return sCTime;
	}
	public void setsCTime(String sCTime) {
		this.sCTime = sCTime;
	}
	public int getnGroupId() {
		return nGroupId;
	}
	public void setnGroupId(int nGroupId) {
		this.nGroupId = nGroupId;
	}
	public int getnAlarmIndex() {
		return nAlarmIndex;
	}
	public void setnAlarmIndex(int nAlarmIndex) {
		this.nAlarmIndex = nAlarmIndex;
	}
	
	public short getnClear() {
		return nClear;
	}
	public void setnClear(short nClear) {
		this.nClear = nClear;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getsMessage() {
		return sMessage;
	}
	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}
	public long getnDateTime() {
		return nDateTime;
	}
	public void setnDateTime(long nDateTime) {
		this.nDateTime = nDateTime;
	}
	public long getnClearDT() {
		return nClearDT;
	}
	public void setnClearDT(long nClearDT) {
		this.nClearDT = nClearDT;
	}
}
