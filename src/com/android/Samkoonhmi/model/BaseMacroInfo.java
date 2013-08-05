package com.android.Samkoonhmi.model;

/**
 * 宏指令基类数据实体
 * */
public class BaseMacroInfo {
	
	protected short  MacroID;  		    //宏指令ID
    protected String MacroName;  		//宏指令名称
    protected String MacroLibName;  	//宏指令库名称
    protected short  MacroType;         //宏指令类型  
    private int nSid;//场景id
    
    public int getnSid() {
		return nSid;
	}

	public void setnSid(int nSid) {
		this.nSid = nSid;
	}

	/**
     * 设置宏指令ID
     * */
    public void setMacroID(short ID){
    	
    	this.MacroID =  ID;
    	
    }
    
    /**
     * 获得宏指令ID
     * */
    public short getMacroID(){
    	return this.MacroID;
    }
    
    /**
     * 设置宏指令名称
     * */
    public void setMacroName(String name){
    	if(null != name){
    		this.MacroName = new String(name);
    	}
    }
    
    /**
     * 获得宏指令名称
     * */
    public String getMacroName(){
    	return this.MacroName;
    }
    
    /**
     * 设置宏指令库名称
     * */
    public void setMacroLibName(String name){
    	if(null != name){
    		this.MacroLibName = new String(name);
    	}
    }
    
    /**
     * 获得宏指令库名称
     * */
    public String getMacroLibName(){
    	return this.MacroLibName;
    }   
    
    /**
     * 设置宏指令类型
     * */
    public void setMacroType(short type){
    	this.MacroType = type;
    }
    
    /**
     * 获得宏指令类型
     * */
    public short getMacroType(){
    	return this.MacroType;
    }
}
