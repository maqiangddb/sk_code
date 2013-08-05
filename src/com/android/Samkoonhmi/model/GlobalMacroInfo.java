package com.android.Samkoonhmi.model;

import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.MACRO_TYPE;

public class GlobalMacroInfo extends BaseMacroInfo{

	protected int    TimeInterval;     //执行的时间间隔

	protected AddrProp    ControlAddr;      //受控地址
	
	protected short  ExecCondition;    //宏指令执行条件
	protected short  CmpFactor;        //比较值

	protected ADDRTYPE ControlAddrType;  //受控地址类型
	
	
	public GlobalMacroInfo(){
		this.setMacroType(MACRO_TYPE.GLOOP);//类型为全局宏
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
     * @param addrtype 地址类型
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
    

}
