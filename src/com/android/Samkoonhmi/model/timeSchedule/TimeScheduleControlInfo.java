package com.android.Samkoonhmi.model.timeSchedule;

import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * @info 时间表执行信息
 * @author 苏逸邦
 * @version 1.0.0
 * @time 2014.03.18
 *
 */
public class TimeScheduleControlInfo {
	//所属星期
	private short weekDate;
	//执行时间是否地址控制
	private boolean bTimeControl;
	//常数型执行时间
	private int timeStamp;
	//地址型执行时间
	private AddrProp addrTime;
	//操作类型
	private short actionType;
	//数据类型（针对字操作）
	private DATA_TYPE dataType;
	//写入数据是否受地址控制
	private boolean bValueControl;
	//写入常数型数据
	private double constValue;
	//写入地址型数据
	private AddrProp addrValue;
	//操作地址
	private AddrProp actionAddr;
	//执行是否受地址控制
	private boolean bActionControl;
	//执行控制地址
	private AddrProp actionControlAddr;
	//所在行
	private int actionIndex;
	//上次执行时间
	private long lastTime;
	
	public void setWeekDate(short wDate){
		weekDate = wDate;
	}
	
	public short getWeekDate(){
		return weekDate;
	}
	
	public void setTimeControl(boolean control){
		bTimeControl = control;
	}
	
	public boolean getTimeControl(){
		return bTimeControl;
	}
	
	public void setAddrTime(AddrProp time){
		addrTime = time;
	}
	
	public AddrProp getAddrTime(){
		return addrTime;
	}
	
	public void setActionType(short type){
		actionType = type;
	}
	
	public short getActionType(){
		return actionType;
	}
	
	public void setDataType(DATA_TYPE type){
		dataType = type;
	}
	
	public DATA_TYPE getDataType(){
		return dataType;
	}
	
	public void setValueControl(boolean control){
		bValueControl = control;
	}
	
	public boolean getValueControl(){
		return bValueControl;
	}
	
	public void setConstValue(double value){
		constValue = value;
	}
	
	public double getConstValue(){
		return constValue;
	}
	
	public void setAddrValue(AddrProp addr){
		addrValue = addr;
	}
	
	public AddrProp getAddrValue(){
		return addrValue;
	}
	
	public void setActionAddr(AddrProp addr){
		actionAddr = addr;
	}
	
	public AddrProp getActionAddr(){
		return actionAddr;
	}
	
	public void setActionControl(boolean control){
		bActionControl = control;
	}
	
	public boolean getActionControl(){
		return bActionControl;
	}
	
	public void setActionControlAddr(AddrProp addr){
		actionControlAddr = addr;
	}
	
	public AddrProp getActionControlAddr(){
		return actionControlAddr;
	}
	
	public void setActionIndex(int index){
		this.actionIndex = index;
	}
	
	public int getActionIndex(){
		return this.actionIndex;
	}
	
	public void setActionTimeStamp(int timeStamp){
		this.timeStamp = timeStamp;
	}
	
	public int getActionTimeStamp(){
		return this.timeStamp;
	}
	
	public void setLastTime(long lastTime){
		this.lastTime = lastTime;
	}
	
	public long getLastTime(){
		return this.lastTime;
	}
	
}
