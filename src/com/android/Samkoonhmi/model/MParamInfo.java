package com.android.Samkoonhmi.model;

import android.util.Log;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 宏指令参数描述符
 * */
public class MParamInfo {
	public String   Name;         //参数名称
	public String   MacroLibName; //参数所属脚本库名称
	private int nSid;// 场景id
	public PHolder   mPHolder    = null;//变量值持有器
    
    public short    DataType;      //数据类型
    public short    CodeType;      //编码类型(Unicode/US-ASCII)
    public short    RWPerm;        //读写权限
    public short    ElemNum  = 1;  //元素个数,默认为1
    public AddrProp AddrVlaue;     //地址属性
    private CallbackItem mCallItem;//地址监视回调
    private boolean bOffset;//是否有偏移
    private AddrProp nOffsetAddr;//偏移地址
    

	/**
	 * 设置参数名称
	 * */
	public void setName(String pname){
		if(null == pname){
			Log.e("MParamInfo","setName: pname is null");
			return;
		}
		this.Name = pname;
	}
	
	/**
	 * 获得参数名称
	 * */
	public String getName(){
		return this.Name;
	}
	
	/**
	 * 设置参数所属宏指令库名称
	 * */
	public void setMacroLibName(String libname){
		if(null == libname){
			Log.e("MParamInfo","setMacroLibName: libname is null");
			return;
		}
		this.MacroLibName = libname;
	}
	
	/**
	 * 获得参数所属宏指令库名称
	 * */
	public String getMacroLibName(){
		return this.MacroLibName;
	}
	
//	/**
//	 * 设置参数所属宏指令ID
//	 * */
//	public void setMacroID(short id){
//		this.MacroID = id;
//	}
	
//	/**
//	 * 获得参数所属宏指令ID
//	 * */
//	public short getMacroID(){
//		return this.MacroID;
//	}
	
	/**
	 * 设置参数数据类型
	 * */
	public void setDataType(short dtype){
		this.DataType = dtype;
	}
	
	/**获得参数数据类型*/
	public short getDataType(){
		return this.DataType;
	}
	
	/**
	 * 设置读取权限
	 * */
	public void setRWPerm(short perm){
		this.RWPerm = perm;
	}
	
	/**
	 * 获得读取权限
	 * */
	public short getRWPerm(){
		return this.RWPerm;
	}
	
	/**
	 * 设置地址属性
	 * */
	public void setAddrProp(AddrProp addrprop){
		if(null == addrprop){
			Log.e("MParamInfo","setAddrProp: addrprop is null");
			return;
		}
		this.AddrVlaue = addrprop;
	}
	
	/**
	 * 获得地址属性
	 * */
	public AddrProp getAddrProp(){
		return this.AddrVlaue;
	}
	
	/**
	 * 设置参数元素个数
	 * */
	public void setElemNum(short num){
		this.ElemNum = num;
	}
	
	/**
	 * 获得参数元素个数
	 * */
	public short getElemNum(){
		return this.ElemNum;
	}
	/**
	 * 设置参数编码属性,
	 * @param  0,代表unicode，1代表US-ASCII
	 * */
	public void setCodeType(short ctype){
		this.CodeType = ctype;
	}
	
	/**
	 * 获得参数编码属性
	 * @return 0,代表unicode，1代表US-ASCII
	 * */
	public short getCodeType(){
		return this.CodeType;
	}

	public boolean isbOffset() {
		return bOffset;
	}

	public void setbOffset(boolean bOffset) {
		this.bOffset = bOffset;
	}

	public AddrProp getnOffsetAddr() {
		return nOffsetAddr;
	}

	public void setnOffsetAddr(AddrProp nOffsetAddr) {
		this.nOffsetAddr = nOffsetAddr;
	}

	public CallbackItem getmCallItem() {
		return mCallItem;
	}

	public void setmCallItem(CallbackItem mCallItem) {
		this.mCallItem = mCallItem;
	}
	
	public int getnSid() {
		return nSid;
	}

	public void setnSid(int nSid) {
		this.nSid = nSid;
	}
	
}
