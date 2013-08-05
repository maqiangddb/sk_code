package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.MACRO_TYPE;


public class SceneMacroInfo extends BaseMacroInfo{
	
	protected AddrProp   ControlAddr;   //受控地址
	
	protected int TimeInterval;     //执行的时间间隔
	protected short ExecCondition;    //宏指令执行条件
	protected short CmpFactor;        //比较值    
	protected int   scriptCount;      //执行次数

	protected int   SceneID;         //场景名称

	protected ADDRTYPE  ControlAddrType;  //受控地址类型
	
	public SceneMacroInfo(){
		this.setMacroType(MACRO_TYPE.SLOOP);//类型为场景宏
	}
	
	/**
	 * 设置场景脚本的执行次数
	 * */
	public void setRunCount(int n){
		if(n < 0 ){
			n = 0;
		}
		this.scriptCount = n;
	}
	
	public int getRunCount(){
		return  this.scriptCount;
	}

	/**
	 * 设置宏指令执行时间间隔
	 * */
	public void setTimeInterval(int time){
		this.TimeInterval = time;
	}

	/**
	 * 获得宏指令执行时间间隔
	 * */
	public int getTimeInterval(){
		return this.TimeInterval;
	}

	/**
	 * 设置控制地址
	 * */
	public void setControlAddr(AddrProp addr){
		this.ControlAddr = addr;
	}

	/**
	 * 获得控制地址
	 * */
	public AddrProp getControlAddr(){
		return this.ControlAddr;
	}

	/**
	 * 设置控制地址类型
	 * @param addrtype 控制地址类型
	 * */
	public void setControlAddrType(ADDRTYPE addrtype){
		this.ControlAddrType = addrtype;
	}

	/**
	 * 获得控制地址类型
	 * */
	public ADDRTYPE getControlAddrType(){
		return ControlAddrType;
	}

	/**
	 * 设置宏指令执行条件
	 * */
	public void setExecCondition(short condition){

		this.ExecCondition = condition;
	}

	/**
	 * 获得宏指令执行条件
	 * */
	public short getExecCondition(){
		return this.ExecCondition;
	}


	/**
	 * 设置参与运算的关系值
	 * */
	public void setCmpFactor(short factor){
		this.CmpFactor = factor;
	}

	/**
	 * 获得参与运算的关系值
	 * */
	public short getCmpFactor(){
		return this.CmpFactor;
	}
	
    /**
     * 设置场景id
     * */
    public void setSceneID(int sceneid){
    	if(0 != sceneid){
    		SceneID = sceneid;
    	}
    }
    
    /**
     * 获得宏指令所属场景ID
     * */
    public int getSceneName(){
    	return this.SceneID;
    }
}
