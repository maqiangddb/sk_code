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
import com.android.Samkoonhmi.util.AddrProp;
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
	 * @param sid-场景id，id=0 全局
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
			
			//为了判断数据有没有变化，所以不管有没有读权限，都需要读取，频繁写入数据，会造成串口阻塞，所以需要比较，没有变化不写入
			boolean read=true;
//			MParamInfo info=mParamList.get(i);
//			if (info.RWPerm==0||info.RWPerm==2) {
//				read=true;
//			}
			
			switch(mParamList.get(i).getDataType()){
			case MPTYPE.BOOL: 			//布尔类型
			{
				mParamList.get(i).mPHolder    = new BoolHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.BIT_1;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), true,sid);
				}
				
			}
				
				break;
			case MPTYPE.BOOLSEQ:    	//布尔类型序列
			{
				mParamList.get(i).mPHolder    = new BoolSeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				BoolSeqHolder param=(BoolSeqHolder)mParamList.get(i).mPHolder;
				boolean data[]=new boolean[mParamList.get(i).getAddrProp().nAddrLen];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.BIT_1;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), true,sid);
				}
				break;
				
			}
				
			case MPTYPE.SHORT:			//16位短整型
			{
				mParamList.get(i).mPHolder    = new ShortHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.INT_16;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.USHORT:			//无符号16位短整型
			{
				mParamList.get(i).mPHolder    = new UShortHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_16;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.BCD16:			//BCD16
			{
				mParamList.get(i).mPHolder    = new Bcd16Holder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.BCD_16;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;	
			}
								
			case MPTYPE.SHORTSEQ:		//16位短整型序列
			{
				mParamList.get(i).mPHolder    = new ShortSeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				ShortSeqHolder param=(ShortSeqHolder)mParamList.get(i).mPHolder;
				short data[]=new short[mParamList.get(i).getAddrProp().nAddrLen];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.INT_16;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.USHORTSEQ:		//无符号16位短整型序列
			{
				mParamList.get(i).mPHolder    = new UShortSeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				UShortSeqHolder param=(UShortSeqHolder)mParamList.get(i).mPHolder;
				int data[]=new int[mParamList.get(i).getAddrProp().nAddrLen];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_16;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.BCD16SEQ:		//BCD16序列
			{
				mParamList.get(i).mPHolder    = new Bcd16SeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				Bcd16SeqHolder param=(Bcd16SeqHolder)mParamList.get(i).mPHolder;
				int data[]=new int[mParamList.get(i).getAddrProp().nAddrLen];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.BCD_16;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;		
			}
							
			case MPTYPE.INT:  			//32位整型数据
			{
				mParamList.get(i).mPHolder    = new IntHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.INT_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.UINT:  			//无符号32位整型数据
			{
				mParamList.get(i).mPHolder    = new UIntHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.BCD32:  	    //BCD32
			{
				mParamList.get(i).mPHolder    = new Bcd32Holder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.BCD_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.INTSEQ: 	    //32位整型数据序列
			{
				mParamList.get(i).mPHolder    = new IntSeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				IntSeqHolder param=(IntSeqHolder)mParamList.get(i).mPHolder;
				int data[]=new int[mParamList.get(i).getAddrProp().nAddrLen/2];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.INT_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.UINTSEQ: 	    //32位正整型数据序列
			{
				mParamList.get(i).mPHolder    = new UIntSeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				UIntSeqHolder param=(UIntSeqHolder)mParamList.get(i).mPHolder;
				long data[]=new long[mParamList.get(i).getAddrProp().nAddrLen/2];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.POSITIVE_INT_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
			
			case MPTYPE.BCD32SEQ: 	    //BCD32序列
			{
				mParamList.get(i).mPHolder    = new Bcd32SeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				Bcd32SeqHolder param=(Bcd32SeqHolder)mParamList.get(i).mPHolder;
				long data[]=new long[mParamList.get(i).getAddrProp().nAddrLen/2];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.BCD_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;		
			}
							
			case MPTYPE.FLOAT:		   //32位单精度浮点数
			{
				mParamList.get(i).mPHolder    = new FloatHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				if (register&&read) {
					item.eDataType = DATA_TYPE.FLOAT_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
			
			case MPTYPE.FLOATSEQ:     //32位单精度浮点数序列		
			{
				mParamList.get(i).mPHolder    = new FloatSeqHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				FloatSeqHolder param=(FloatSeqHolder)mParamList.get(i).mPHolder;
				float data[]=new float[mParamList.get(i).getAddrProp().nAddrLen/2];
				param.v=data;
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.FLOAT_32;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					item.setnAddrLen(mParamList.get(i).getAddrProp().nAddrLen);
					item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
				}
				break;
			}
				
			case MPTYPE.STRING:     //字符串类型
			{
				mParamList.get(i).mPHolder    = new StringHolder();
				mParamList.get(i).mPHolder.setbOffSetAddr(mParamList.get(i).isbOffset());
				mParamList.get(i).mPHolder.setmAddrProp(mParamList.get(i).getAddrProp());
				mParamList.get(i).mPHolder.setmOffSetAddr(mParamList.get(i).getnOffsetAddr());
				mParamList.get(i).mPHolder.setnRWPerm(mParamList.get(i).RWPerm);
				((StringHolder)mParamList.get(i).mPHolder).nCode=mParamList.get(i).CodeType;
				StringHolder param=(StringHolder)mParamList.get(i).mPHolder;
				param.v="";
				
				if (register&&read) {
					item.eDataType = DATA_TYPE.ASCII_STRING;
					item.nType=1;
					item.mPholder=mParamList.get(i).mPHolder;
					item.nRW=mParamList.get(i).RWPerm;
					if (mParamList.get(i).getCodeType()==0) {
						//UNICODE
						item.isUnicode=true;
						item.onRegister(mParamList.get(i).getAddrProp(), false,sid);
					}else {
						//ASCII,地址长度需要除以2
						AddrProp prop=mParamList.get(i).getAddrProp().clone();
						prop.nAddrLen=(prop.nAddrLen+1)/2;
						item.onRegister(prop, false,sid);
					}
					
				}
				break;
				
			}
				
			}//End of: switch
			
			mParamList.get(i).mPHolder.setName(mParamList.get(i).getName());
			mPHMap.put(mParamList.get(i).Name, mParamList.get(i).mPHolder);
		}
	}
}
