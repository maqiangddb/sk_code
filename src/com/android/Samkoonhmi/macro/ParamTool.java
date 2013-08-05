package com.android.Samkoonhmi.macro;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.Samkoonhmi.macro.corba.Bcd16Holder;
import com.android.Samkoonhmi.macro.corba.Bcd16SeqHolder;
import com.android.Samkoonhmi.macro.corba.Bcd32Holder;
import com.android.Samkoonhmi.macro.corba.Bcd32SeqHolder;
import com.android.Samkoonhmi.macro.corba.BoolHolder;
import com.android.Samkoonhmi.macro.corba.BoolSeqHolder;
import com.android.Samkoonhmi.macro.corba.FloatHolder;
import com.android.Samkoonhmi.macro.corba.FloatSeqHolder;
import com.android.Samkoonhmi.macro.corba.IntHolder;
import com.android.Samkoonhmi.macro.corba.IntSeqHolder;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.macro.corba.ShortHolder;
import com.android.Samkoonhmi.macro.corba.ShortSeqHolder;
import com.android.Samkoonhmi.macro.corba.StringHolder;
import com.android.Samkoonhmi.macro.corba.UIntHolder;
import com.android.Samkoonhmi.macro.corba.UIntSeqHolder;
import com.android.Samkoonhmi.macro.corba.UShortHolder;
import com.android.Samkoonhmi.macro.corba.UShortSeqHolder;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.MParamInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.MPTYPE;

/**
 * 宏指令，参数设置
 */
public class ParamTool {

	/**
	 * 参数设置
	 * @param mParamList-参数列表
	 * @param mPHMap-参数名称对应参数列表
	 * @param register-注册通知
	 */
	public static void setParam(ArrayList<MParamInfo> mParamList,HashMap<String, PHolder> mPHMap,boolean register,int sid){
		
		for(int i = 0; i< mParamList.size(); i++){
			CallbackItem item=null;
			mParamList.get(i).setnSid(sid);
			if (register) {
				item=new CallbackItem();
				if(mParamList.get(i).isbOffset())
				{
					item.onRegisterAddrOffset(mParamList.get(i).getnOffsetAddr(), sid);
				}
				mParamList.get(i).setmCallItem(item); //将注册item放入参数列表的每个对象中
			}
			
			switch(mParamList.get(i).getDataType()){
			case MPTYPE.BOOL: 			//布尔类型
				mParamList.get(i).mPHolder    = new BoolHolder();
				if (register) {
					item.eDataType = DATA_TYPE.BIT_1;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), true);
				}
				break;
			case MPTYPE.BOOLSEQ:    	//布尔类型序列
				mParamList.get(i).mPHolder    = new BoolSeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.BIT_1;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), true);
				}
				break;
			case MPTYPE.SHORT:			//16位短整型
				mParamList.get(i).mPHolder    = new ShortHolder();
				if (register) {
					item.eDataType = DATA_TYPE.INT_16;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.USHORT:			//无符号16位短整型
				mParamList.get(i).mPHolder    = new UShortHolder();
				if (register) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_16;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.BCD16:			//BCD16
				mParamList.get(i).mPHolder    = new Bcd16Holder();
				if (register) {
					item.eDataType = DATA_TYPE.BCD_16;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;					
			case MPTYPE.SHORTSEQ:		//16位短整型序列
				mParamList.get(i).mPHolder    = new ShortSeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.INT_16;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.USHORTSEQ:		//无符号16位短整型序列
				mParamList.get(i).mPHolder    = new UShortSeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_16;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.BCD16SEQ:		//BCD16序列
				mParamList.get(i).mPHolder    = new Bcd16SeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.BCD_16;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;					
			case MPTYPE.INT:  			//32位整型数据
				mParamList.get(i).mPHolder    = new IntHolder();
				if (register) {
					item.eDataType = DATA_TYPE.INT_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.UINT:  			//无符号32位整型数据
				mParamList.get(i).mPHolder    = new UIntHolder();
				if (register) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.BCD32:  	    //BCD32
				mParamList.get(i).mPHolder    = new Bcd32Holder();
				if (register) {
					item.eDataType = DATA_TYPE.BCD_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.INTSEQ: 	    //32位整型数据序列
				mParamList.get(i).mPHolder    = new IntSeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.INT_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.UINTSEQ: 	    //32位正整型数据序列
				mParamList.get(i).mPHolder    = new UIntSeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.BCD32SEQ: 	    //BCD32序列
				mParamList.get(i).mPHolder    = new Bcd32SeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.BCD_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;					
			case MPTYPE.FLOAT:		   //32位单精度浮点数
				mParamList.get(i).mPHolder    = new FloatHolder();
				if (register) {
					item.eDataType = DATA_TYPE.FLOAT_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.FLOATSEQ:     //32位单精度浮点数序列			
				mParamList.get(i).mPHolder    = new FloatSeqHolder();
				if (register) {
					item.eDataType = DATA_TYPE.FLOAT_32;
					item.nType=1;
					item.mPHolder=mParamList.get(i).mPHolder;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			case MPTYPE.STRING:     //字符串类型
				mParamList.get(i).mPHolder    = new StringHolder();
				if (register) {
					item.eDataType = DATA_TYPE.ASCII_STRING;
					item.nType=1;
					if (mParamList.get(i).getCodeType()==0) {
						item.isUnicode=true;
					}
					item.mPHolder=mParamList.get(i).mPHolder;
					item.onRegister(mParamList.get(i).getAddrProp(), false);
				}
				break;
			}//End of: switch
			mPHMap.put(mParamList.get(i).Name, mParamList.get(i).mPHolder);
		}
	}
}
