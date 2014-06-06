package com.android.Samkoonhmi.macro;

import java.io.UnsupportedEncodingException;
import java.util.Vector;
import android.util.Log;
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
import com.android.Samkoonhmi.model.MParamInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.ComposeAddr;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

public class MacroDataWR {

	//private static  boolean mDebug = false;
	//private static  String mTag = "ParamHelper";
	
	/*********单个变量  开始***********/
	/**
	 * 地址值写入PLC
	 * boolean 类型
	 * @param v-地址值
	 */
	public void setBooleanData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeBool(v.getmAddrProp(), ((BoolHolder)v).v);
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeBool(newAddr, ((BoolHolder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * short 类型,16整数
	 * @param v-地址值
	 */
	public void setShortData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeShort(v.getmAddrProp(), ((ShortHolder) v).v);
			}else{
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeShort(newAddr, ((ShortHolder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * short 类型,16无符号整数
	 * @param v-地址值
	 */
	public void setUShortData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeUShort(v.getmAddrProp(), ((UShortHolder) v).v);
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeUShort(newAddr, ((UShortHolder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * BCD16 类型,
	 * @param v-地址值
	 */
	public  void setBCD16Data(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeBCD16(v.getmAddrProp(), ((Bcd16Holder) v).v);
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeBCD16(newAddr, ((Bcd16Holder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * int 32位 类型,
	 * @param v-地址值
	 */
	public  void setIntData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeInt(v.getmAddrProp(), ((IntHolder) v).v);
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeInt(newAddr, ((IntHolder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * int 32位 无符号 类型,
	 * @param v-地址值
	 */
	public  void setUIntData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeUInt(v.getmAddrProp(), ((UIntHolder) v).v);
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeUInt(newAddr, ((UIntHolder) v).v);
				} 
			}
			
		}
	}
	
	/**
	 * 地址值写入PLC
	 * BCD 32位  类型,
	 * @param v-地址值
	 */
	public  void setBCD32Data(PHolder v){
		if (v != null) {
			if(!v.isbOffSetAddr()){
				writeBCD32(v.getmAddrProp(), ((Bcd32Holder) v).v);
			}else{
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeBCD32(newAddr, ((Bcd32Holder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * float 32位  浮点数 类型,
	 * @param v-地址值
	 */
	public  void setFloatData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeFloat(v.getmAddrProp(), ((FloatHolder) v).v);
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeFloat(newAddr, ((FloatHolder) v).v);
				} 
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * String 字符串 类型,
	 * @param v-地址值
	 */
	public  void setStringData(PHolder v){
		if (v != null) {
			if (!v.isbOffSetAddr()) {
				writeString(v.getmAddrProp(),
						((StringHolder) v).v, ((StringHolder) v).nCode);
			}else{
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					AddrProp newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
					writeString(newAddr,
							((StringHolder) v).v, ((StringHolder) v).nCode);
				} 
			}
		}
	}
	/*********单个变量  结束***********/
	
	
	/*********变量变量  开始***********/
	/**
	 * 地址值写入PLC
	 * boolean[] 类型
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized  void setBooleanSeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if (!v.isbOffSetAddr()) {
				newAddr = v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			if (seq) {
				//批量赋值
				writeBoolSeq(newAddr, ((BoolSeqHolder) v).v);
			}else {
				//单个赋值
				BoolSeqHolder holder=((BoolSeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewAddr(newAddr, index);
				boolean data=holder.v[index];
				writeBool(addrProp, data);
			}
		}
	}
	
	
	/**
	 * 地址值写入PLC
	 * short[] 类型,16位 整数 数组
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized void setShortSeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if (!v.isbOffSetAddr()) {
				newAddr=v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			if (seq) {
				//批量赋值
				writeShortSeq(newAddr,((ShortSeqHolder) v).v);
			}else {
				//单个赋值
				ShortSeqHolder holder=((ShortSeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewAddr(newAddr, index);
				short data=holder.v[index];
				writeShort(addrProp, data);
			}
			
		}
	}
	
	/**
	 * 地址值写入PLC
	 * short[] 类型,16位 无符号整数数组
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized void setUShortSeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if(!v.isbOffSetAddr()){
				newAddr=v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			if (seq) {
				//批量赋值
				writeUShortSeq(newAddr,((UShortSeqHolder) v).v);
			}else {
				//单个赋值
				UShortSeqHolder holder=((UShortSeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewAddr(newAddr, index);
				int data=holder.v[index];
				writeUShort(addrProp, data);
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * BCD16[] 类型,
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized void setBCD16SeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if (!v.isbOffSetAddr()) {
				newAddr=v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			
			if (seq) {
				//批量赋值
				writeBcd16Seq(newAddr,((Bcd16SeqHolder) v).v);
			}else {
				//单个赋值
				Bcd16SeqHolder holder=((Bcd16SeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewAddr(newAddr, index);
				int data=holder.v[index];
				writeBCD16(addrProp, data);
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * int[] 32位 类型,
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized  void setIntSeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if (!v.isbOffSetAddr()) {
				newAddr=v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			if (seq) {
				//批量赋值
				writeIntSeq(newAddr, ((IntSeqHolder) v).v);
			}else {
				//单个赋值
				IntSeqHolder holder=((IntSeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewDWordAddr(newAddr, index);
				int data=holder.v[index];
				writeInt(addrProp, data);
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * int[] 32位 无符号 类型,
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized void setUIntSeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if(!v.isbOffSetAddr()){
				newAddr=v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			
			if (seq) {
				//批量赋值
				writeUIntSeq(newAddr, ((UIntSeqHolder) v).v);
			}else {
				//单个赋值
				UIntSeqHolder holder=((UIntSeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewDWordAddr(newAddr, index);
				long data=holder.v[index];
				writeUInt(addrProp, data);
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * BCD[] 32位  类型,
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized  void setBCD32SeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if (!v.isbOffSetAddr()) {
				newAddr=v.getmAddrProp();
			}else{
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			if (seq) {
				//批量赋值
				writeBcd32Seq(newAddr,((Bcd32SeqHolder) v).v);
			}else {
				//单个赋值
				Bcd32SeqHolder holder=((Bcd32SeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewDWordAddr(newAddr, index);
				long data=holder.v[index];
				writeBCD32(addrProp, data);
			}
		}
	}
	
	/**
	 * 地址值写入PLC
	 * float[] 32位  浮点数 类型,
	 * @param v-地址值
	 * @param seq-true 表示全部写入,seq-false 写入单个
	 */
	public synchronized void setFloatSeqData(PHolder v,boolean seq,int index){
		if (v != null) {
			AddrProp newAddr=null;
			if (!v.isbOffSetAddr()) {
				newAddr=v.getmAddrProp();
			}else {
				if (v.isbOffSetAddr()) {
					// 有地址偏移
					// 取得新地址，写入值
					newAddr = ComposeAddr.getInstance().newAddress(
							v.getmAddrProp(), v.getmOffSetAddr());
				} 
			}
			
			if (seq) {
				//批量赋值
				writeFloatSeq(newAddr,((FloatSeqHolder) v).v);
			}else {
				//单个赋值
				FloatSeqHolder holder=((FloatSeqHolder) v);
				if (holder==null|index<0||holder.v.length<index) {
					return;
				}
				AddrProp addrProp=getNewDWordAddr(newAddr, index);
				float data=holder.v[index];
				writeFloat(addrProp, data);
			}
		}
	}
	
	/*********变量变量  结束***********/
	
	/**
	 * 把批量地址转换成单个地址
	 */
	private  AddrProp getNewAddr(AddrProp mAddrProp,int index){
		AddrProp addrProp = new AddrProp();
		addrProp.eAddrRWprop = mAddrProp.eAddrRWprop;
		addrProp.eConnectType = mAddrProp.eConnectType;
		addrProp.nAddrId = mAddrProp.nAddrId;
		addrProp.nAddrLen = 1;
		addrProp.nAddrValue =mAddrProp.nAddrValue+index;
		addrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
		addrProp.nRegIndex = mAddrProp.nRegIndex;
		addrProp.nUserPlcId = mAddrProp.nUserPlcId;
		addrProp.sPlcProtocol = mAddrProp.sPlcProtocol;
		
		return addrProp;
	}
	
	/**
	 * 把批量地址转换成单个地址
	 */
	private  AddrProp getNewDWordAddr(AddrProp mAddrProp,int index){
		AddrProp addrProp = new AddrProp();
		addrProp.eAddrRWprop = mAddrProp.eAddrRWprop;
		addrProp.eConnectType = mAddrProp.eConnectType;
		addrProp.nAddrId = mAddrProp.nAddrId;
		addrProp.nAddrLen = 2;
		addrProp.nAddrValue =mAddrProp.nAddrValue+index*2;
		addrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
		addrProp.nRegIndex = mAddrProp.nRegIndex;
		addrProp.nUserPlcId = mAddrProp.nUserPlcId;
		addrProp.sPlcProtocol = mAddrProp.sPlcProtocol;
		
		return addrProp;
	}
	
	
	/**
	 * 读取指定地址中的一个Bool数据
	 * */
	private  boolean readBool(AddrProp addr) {
		if (null == addr) {
			Log.e("ParamHelper", "readBool: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BIT_1; // 请求数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 请求数据为小端模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 请求访问类型为读

		Vector<Byte> dataList = new Vector<Byte>();
		PlcRegCmnStcTools.getRegBytesData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readBool: dataList size is 0");
			return false;
		}

		if (1 == dataList.get(0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 写指定地址中的一个Bool数据
	 * */
	private  boolean writeBool(AddrProp addr, boolean data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeBool: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BIT_1; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		byte dataList[] = new byte[1];
		// 根据布尔值传入不同的参数值
		if (true == data) {
			dataList[0] = 1;
		} else {
			dataList[0] = 0;
		}
		//Log.d(mTag, "......writeBool "+dataList[0]+"  addr="+addr.nAddrValue);
		return PlcRegCmnStcTools.setRegBytesData(addr, dataList, QuestInfo);
	}

	/**
	 * 读取指定地址中的Bool数据序列
	 * */
	private  boolean[] readBoolSeq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readBoolSeq: addr is null!");
			return null;
		}

		// 将数据转存到缓冲区
		boolean[] value = new boolean[addr.nAddrLen];

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BIT_1; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// //批量读取数据，数据个数是 addr.nAddrLen
		Vector<Byte> dataList = new Vector<Byte>();
		PlcRegCmnStcTools.getRegBytesData(addr, dataList, QuestInfo);
		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readBoolSeq: dataList size is 0");
			return null;
		}
		for (short i = 0; i < (addr.nAddrLen); i++) {
			if (0 == dataList.get(i)) {
				value[i] = false;
			} else {
				value[i] = true;
			}
		}
		return value;
	}

	/**
	 * 写指定地址中的Bool数据序列
	 * */
	private  boolean writeBoolSeq(AddrProp addr, boolean[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeFloatSeq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeFloatSeq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BIT_1; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		byte dataList[] = new byte[data.length];

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			if (true == data[i]) {
				dataList[i] = 1;
			} else {
				dataList[i] = 0;
			}
			//Log.d(mTag, "list result="+dataList[i]+",i="+i);
		}
		// 批量写数据，数据个数为addr.nAddrLen
		PlcRegCmnStcTools.setRegBytesData(addr, dataList, QuestInfo);
		return true;
	}

	/**
	 * 读取指定地址中的一个短整型数据
	 * */
	private  short readShort(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readShort: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readShort: dataList size is 0");
			return 0;
		}

		return PlcRegCmnStcTools.intToShort(dataList.get(0));
	}

	/**
	 * 读取指定地址中的一个无符号短整型数据
	 * */
	private  int readUShort(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readUShort: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readUShort: dataList size is 0");
			return 0;
		}
		return dataList.get(0);
	}

	/**
	 * 读取指定地址中的BCD16数据
	 * */
	private  int readBCD16(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readBCD16: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();
		boolean reuslt=PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (!reuslt) {
			return 0;
		}
		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readBCD16: dataList size is 0");
			return 0;
		}
		int value=0;
		String s=DataTypeFormat.intToBcdStr(dataList.get(0), false);
		if (s!=null&&!s.equals("ERROR")) {
			try {
				value=Integer.valueOf(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 向指定地址中写入短整型数据
	 * */
	private  boolean writeShort(AddrProp addr, int data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeShort: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		return true;
	}

	/**
	 * 向指定地址中写入无符号短整型数据
	 * */
	private  boolean writeUShort(AddrProp addr, int data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeUShort: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		return true;
	}

	/**
	 * 向指定地址中写入BCD16数据
	 * */
	private  boolean writeBCD16(AddrProp addr, int data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeBCD16: addr is null!");
			return false;
		}

		String dataStr = data + "";
		data=(int)DataTypeFormat.bcdStrToInt(dataStr, 16);
		
		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		return true;
	}

	/**
	 * 读取指定地址中的短整型数据序列
	 * @param addr 批量变量的首地址
	 * @param num 批量变量的元素个数
	 * */
	public  short[] readShortSeq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readShortSeq: addr is null!");
			return null;
		}

		short[] value = new short[addr.nAddrLen];

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是addr.nAddrLen
		Vector<Integer> dataList = new Vector<Integer>();
		PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readShortSeq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen); i++) {
			value[i] = PlcRegCmnStcTools.intToShort(dataList.get(i));
		}
		return value;
	}

	/**
	 * 读取指定地址中的无符号短整型数据序列
	 * 
	 * @param addr
	 *            批量变量的首地址
	 * @param num
	 *            批量变量的元素个数
	 * */
	public  int[] readUShortSeq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readUShortSeq: addr is null!");
			return null;
		}

		int[] value = new int[addr.nAddrLen];

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是addr.nAddrLen
		Vector<Integer> dataList = new Vector<Integer>();
		PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readUShortSeq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen); i++) {
			value[i] = dataList.get(i);
		}
		return value;
	}

	public  int[] readBcd16Seq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readBcd16Seq: addr is null!");
			return null;
		}

		int[] value = new int[addr.nAddrLen];

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是addr.nAddrLen
		Vector<Integer> dataList = new Vector<Integer>();
		boolean result=PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (!result) {
			return null;
		}
		
		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readBcd16Seq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen); i++) {
			int values=0;
			String s=DataTypeFormat.intToBcdStr(dataList.get(i), false);
			if (s!=null&&!s.equals("ERROR")) {
				try {
					values=Integer.valueOf(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			value[i] = values;
		}
		
		return value;
	}

	/**
	 * 向指定地址中写入短整型数据序列
	 * */
	public  boolean writeShortSeq(AddrProp addr, short[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeShortSeq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeShortSeq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			dataList.add((int) data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen
		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		// short[] seq = readShortSeq(addr);//调试代码
		return true;
	}

	/**
	 * 向指定地址中写入无符号16位短整型数据序列
	 * */
	public  boolean writeUShortSeq(AddrProp addr, int[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeUShortSeq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeUShortSeq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			dataList.add(data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen
		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		return true;
	}

	/**
	 * 向指定地址中写入BCD16数据序列
	 * */
	public  boolean writeBcd16Seq(AddrProp addr, int[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeBcd16Seq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeBcd16Seq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_16; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			int value=0;
			String dataStr = data[i] + "";
			value=(int)DataTypeFormat.bcdStrToInt(dataStr, 16);
			dataList.add(value);
		}

		// 批量写数据，数据个数为addr.nAddrLen
		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		return true;
	}

	/**
	 * 读取指定地址中的一个整型数据
	 * */
	public  int readInt(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readInt: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readInt: dataList size is 0");
			return 0;
		}
		return dataList.get(0);
	}

	/**
	 * 读取指定地址中的一个无符号32位整型数据
	 * */
	public  long readUInt(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readInt: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Long> dataList = new Vector<Long>();

		PlcRegCmnStcTools.getRegLongData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readInt: dataList size is 0");
			return 0;
		}
		return dataList.get(0);
	}

	/**
	 * 读取指定地址中的一个BCD32
	 * */
	public  long readBCD32(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readBCD32: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Long> dataList = new Vector<Long>();

		boolean reuslt=PlcRegCmnStcTools.getRegLongData(addr, dataList, QuestInfo);

		if (!reuslt) {
			return 0;
		}
		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readBCD32: dataList size is 0");
			return 0;
		}
		
		int value=0;
		String s=DataTypeFormat.intToBcdStr(dataList.get(0), false);
		if (s!=null&&!s.equals("ERROR")) {
			try {
				value=Integer.valueOf(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 向指定地址中写入整型数据
	 * */
	public  boolean writeInt(AddrProp addr, int data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeInt: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		// int v = readInt(addr); 调试代码
		return true;
	}

	/**
	 * 向指定地址中写入无符号32位整型数据
	 * */
	public  boolean writeUInt(AddrProp addr, long data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeUInt: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Long> dataList = new Vector<Long>();

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegLongData(addr, dataList, QuestInfo);

		// int v = readUInt(addr); 调试代码
		return true;
	}

	/**
	 * 向指定地址中写入BCD32
	 * */
	public  boolean writeBCD32(AddrProp addr, long data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeUInt: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Long> dataList = new Vector<Long>();

		long value=0;
		String dataStr = data+ "";
		value=DataTypeFormat.bcdStrToInt(dataStr, 32);
		dataList.add(value); // 将数据暂存

		PlcRegCmnStcTools.setRegLongData(addr, dataList, QuestInfo);

		// int v = readUInt(addr); 调试代码
		return true;
	}

	/**
	 * 读取指定地址中的整型数据序列
	 * */
	public  int[] readIntSeq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readIntSeq: addr is null!");
			return null;
		}

		int[] value = new int[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是 addr.nAddrLen/2
		Vector<Integer> dataList = new Vector<Integer>();
		PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readIntSeq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen / 2); i++) {
			value[i] = dataList.get(i);
		}
		
		return value;
	}

	/**
	 * 读取指定地址中的无符号整型数据序列
	 * */
	public  long[] readUIntSeq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readUIntSeq: addr is null!");
			return null;
		}

		long[] value = new long[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是 addr.nAddrLen/2
		Vector<Long> dataList = new Vector<Long>();
		PlcRegCmnStcTools.getRegLongData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readUIntSeq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen / 2); i++) {
			value[i] = dataList.get(i);
		}
		
		return value;
	}

	/**
	 * 读取指定地址中的无符号整型数据序列
	 * */
	public  long[] readBcd32Seq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readBcd32Seq: addr is null!");
			return null;
		}

		long[] value = new long[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是 addr.nAddrLen/2
		Vector<Long> dataList = new Vector<Long>();
		PlcRegCmnStcTools.getRegLongData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readBcd32Seq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen / 2); i++) {
			int values=0;
			String s=DataTypeFormat.intToBcdStr(dataList.get(i), false);
			if (s!=null&&!s.equals("ERROR")) {
				try {
					values=Integer.valueOf(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			value[i] = values;
		}
		
		return value;
	}

	/**
	 * 向指定地址中写入整型数据序列
	 * */
	public  boolean writeIntSeq(AddrProp addr, int[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeIntSeq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeIntSeq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Integer> dataList = new Vector<Integer>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			dataList.add(data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen/2
		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		// int[] seq = readIntSeq(addr);//调试代码
		return true;
	}

	/**
	 * 向指定地址中写入整型数据序列
	 * */
	public  boolean writeUIntSeq(AddrProp addr, long[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeIntSeq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeIntSeq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.POSITIVE_INT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Long> dataList = new Vector<Long>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			dataList.add(data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen/2
		PlcRegCmnStcTools.setRegLongData(addr, dataList, QuestInfo);

		// int[] seq = readUIntSeq(addr);//调试代码
		return true;
	}

	/**
	 * 向指定地址中写入BCD32序列
	 * */
	public  boolean writeBcd32Seq(AddrProp addr, long[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeBcd32Seq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeBcd32Seq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BCD_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Long> dataList = new Vector<Long>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			long value=0;
			String dataStr = data[i] + "";
			value=DataTypeFormat.bcdStrToInt(dataStr, 32);
			dataList.add(value);
		}

		// 批量写数据，数据个数为addr.nAddrLen/2
		PlcRegCmnStcTools.setRegLongData(addr, dataList, QuestInfo);

		// int[] seq = readUIntSeq(addr);//调试代码
		return true;
	}

	/**
	 * 读取指定地址中的一个整型数据
	 * */
	public  float readFloat(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readFloat: addr is null!");
			return -1;
		}

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.FLOAT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		Vector<Float> dataList = new Vector<Float>();

		PlcRegCmnStcTools.getRegFloatData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readFloat: dataList size is 0");
			return 0;
		}

		return dataList.get(0);
	}

	/**
	 * 向指定地址中写入整型数据
	 * */
	public  boolean writeFloat(AddrProp addr, float data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeFloat: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.FLOAT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Float> dataList = new Vector<Float>();

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegFloatData(addr, dataList, QuestInfo);

		// float f = readFloat(addr); 调试代码

		return true;
	}

	/**
	 * 读取指定地址中的单精度浮点数数据序列
	 * */
	public  float[] readFloatSeq(AddrProp addr) {

		if (null == addr) {
			Log.e("ParamHelper", "readFloatSeq: addr is null!");
			return null;
		}

		float[] value = new float[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.FLOAT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

		// 批量读取数据，数据个数是 addr.nAddrLen/2
		Vector<Float> dataList = new Vector<Float>();
		PlcRegCmnStcTools.getRegFloatData(addr, dataList, QuestInfo);

		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readFloatSeq: dataList size is 0");
			return null;
		}

		// 将数据转存到缓冲区
		for (short i = 0; i < (addr.nAddrLen / 2); i++) {
			value[i] = dataList.get(i);
		}
		
		return value;
	}

	/**
	 * 向指定地址中写入单精度浮点数数据序列
	 * */
	public  boolean writeFloatSeq(AddrProp addr, float[] data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeFloatSeq: addr is null!");
			return false;
		}

		if (null == data) {
			Log.e("ParamHelper", "writeFloatSeq: data is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.FLOAT_32; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		Vector<Float> dataList = new Vector<Float>();

		// 将数据添加到列表中
		for (short i = 0; i < data.length; i++) {
			dataList.add(data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen/2
		PlcRegCmnStcTools.setRegFloatData(addr, dataList, QuestInfo);

		// float[] seq = readFloatSeq(addr);//调试代码
		return true;
	}

	/**
	 * 读取指定地址中的 字符串
	 * */
	public  String readString(AddrProp addr, short ctype) {

		if (null == addr) {
			Log.e("ParamHelper", "readString: addr is null!");
			return "";
		}

		// 构成字符串
		String str = "";

		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.ASCII_STRING; // 请求数据类型
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;// 请求访问类型为读

		short isASCII = ctype;
		Vector<Byte> dataList = new Vector<Byte>();

		PlcRegCmnStcTools.getRegAsciiData(addr, dataList, QuestInfo, true);
		if (0 == dataList.size()) {
			Log.e("ParamHelper", "readString: dataList size is 0");
			return str;
		}

		if (0 == isASCII) {// 若为Unicode编码
			int act_len = 0;
			for (act_len = 0; act_len < (dataList.size() - 2); act_len += 2) {
				if ((dataList.get(act_len) == 0)
						&& (dataList.get(act_len) == 0)) {
					break;
				}
			}

			if (act_len == 0) {
				return str;
			}

			if (act_len >= dataList.size()) {
				act_len = dataList.size();
			}

			// 将获得的字符码转存到字节数组中
			byte[] bytearray = new byte[act_len + 2];

			// 添加unicode标记
			bytearray[0] = -1;
			bytearray[1] = -2;

			if (bytearray.length > 2) {
				for (int j = 0, i = 2; j < act_len; j++) {
					bytearray[i] = dataList.get(j);
					i++;
				}
			}

			try {// 新建unicode字符串
				str = new String(bytearray, "UNICODE");
			} catch (UnsupportedEncodingException e) {
				Log.e("ParamHelper",
						"writeString: get unicode String failed!");
				e.printStackTrace();
			}
		} else if (1 == isASCII) {
			int ascii_act_len = 0;
			for (ascii_act_len = 0; ascii_act_len < dataList.size(); ascii_act_len++) {
				if (dataList.get(ascii_act_len) == 0) {
					break;
				}
			}
			if (ascii_act_len == 0) {
				return str;
			}

			if (ascii_act_len >= dataList.size()) {
				ascii_act_len = dataList.size();
			}

			// 将获得的字符码转存到字节数组中
			byte[] bytearray = new byte[ascii_act_len];

			for (int j = 0, i = 0; j < ascii_act_len; j++) {
				bytearray[i] = dataList.get(j);
				i++;
			}
			try {// 新建ASCII字符串
				str = new String(bytearray, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				Log.e("ParamHelper",
						"writeString: get ascii String failed!");
				e.printStackTrace();
			}
		} else {
			return str;
		}
		
		return str;
	}

	/**
	 * 向指定地址中写字符串
	 * */
	public  boolean writeString(AddrProp addr, String data, short ctype) {


		if (null == addr) {
			Log.e("ParamHelper", "writeString: addr is null!");
			return false;
		}

		if (null == data) {
			data = new String("");
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.ASCII_STRING; // 设置数据类型
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型


		short isASCII = ctype; // 是否为ASCII编码

		// 将字符串转换为字节数组后存入列表

		byte[] bytearray = null;
		Vector<Byte> dataList = new Vector<Byte>();
		if (0 == isASCII) {// 若是Unicode
			if (data.length() * 2 > addr.nAddrLen) {// 实际字符串所占空间大于了字符串地址长度
				data = data.substring(0, (addr.nAddrLen / 2));// 只获取规定长度的数据
			}

			try {
				bytearray = data.getBytes("UNICODE");
			} catch (UnsupportedEncodingException e) {
				Log.e("ParamHelper",
						"writeString: get unicode bytearray failed!");
				e.printStackTrace();
				return false;
			}

			if (null == bytearray) {
				return false;
			}

			for (int i = 2; i < bytearray.length; i++) {// 丢弃掉开始字节-1和-2
				dataList.add(bytearray[i]);
			}
			if ((bytearray.length - 2) < addr.nAddrLen) {// 实际字符串长度不够的填入0
				for (int i = 0; i < (addr.nAddrLen - 2 - bytearray.length); i++) {
					dataList.add((byte) 0);
				}
			}
			
			PlcRegCmnStcTools.setRegAsciiData(addr, dataList, QuestInfo);
			
		} else if (1 == isASCII) {// 若是ASCII编码
			
			AddrProp prop=addr.clone();
			prop.nAddrLen=(prop.nAddrLen+1)/2;
			int len=prop.nAddrLen*2;
			
			if (data.length() > addr.nAddrLen) {// 实际字符串所占空间大于了字符串地址长度
				data = data.substring(0, addr.nAddrLen);// 只获取规定长度的数据
			}
			try {
				bytearray = data.getBytes("US-ASCII");
			} catch (UnsupportedEncodingException e) {
				Log.e("ParamHelper", "writeString: get ascii bytearray failed!");
				e.printStackTrace();
				return false;
			}
			if (null == bytearray) {
				return false;
			}
			for (int i = 0; i < bytearray.length; i++) {// 逐个拷贝
				dataList.add(bytearray[i]);
			}
			if ((bytearray.length) < len) {// 实际字符串长度不够的填入0
				for (int i = 0; i < (len - bytearray.length); i++) {
					dataList.add((byte) 0);
				}
			}
			
			PlcRegCmnStcTools.setRegAsciiData(prop, dataList, QuestInfo);
		} else {
			return false;
		}

		// String str = readString(addr); //调试语句
		return true;
	}
}
