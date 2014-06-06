package com.android.Samkoonhmi.macro;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import android.util.Log;
import com.android.Samkoonhmi.macro.corba.BoolSeqHolder;
import com.android.Samkoonhmi.macro.corba.FloatSeqHolder;
import com.android.Samkoonhmi.macro.corba.IntSeqHolder;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.macro.corba.BoolHolder;
import com.android.Samkoonhmi.macro.corba.ShortHolder;
import com.android.Samkoonhmi.macro.corba.IntHolder;
import com.android.Samkoonhmi.macro.corba.FloatHolder;
import com.android.Samkoonhmi.macro.corba.ShortSeqHolder;
import com.android.Samkoonhmi.macro.corba.StringHolder;
import com.android.Samkoonhmi.macro.corba.CmnHolder;
import com.android.Samkoonhmi.macro.corba.UIntHolder;
import com.android.Samkoonhmi.macro.corba.UIntSeqHolder;
import com.android.Samkoonhmi.macro.corba.UShortHolder;
import com.android.Samkoonhmi.macro.corba.UShortSeqHolder;
import com.android.Samkoonhmi.macro.corba.Bcd16Holder;
import com.android.Samkoonhmi.macro.corba.Bcd16SeqHolder;
import com.android.Samkoonhmi.macro.corba.Bcd32Holder;
import com.android.Samkoonhmi.macro.corba.Bcd32SeqHolder;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.MParamInfo;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.ComposeAddr;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.MPTYPE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TurnDataProp;

/**
 * 参数处理辅助类
 * */
public class ParamHelper {

	// 自由口参数持久器
	private static CmnHolder CmnCom1 = null;
	private static CmnHolder CmnCom2 = null;
	private static CmnHolder CmnNet0 = null;

	//private static boolean mDebug = false;
	//private static String mTag = "ParamHelper";

	/**
	 * 设置数据转发描述符
	 * */
	private static TurnDataProp updateCmnDataProp(byte[] data, int port) {

		TurnDataProp tmpDP = null;
		if (1 == port) {
			tmpDP = new TurnDataProp();
			tmpDP.eConnect = CONNECT_TYPE.COM1;

		} else if (2 == port) {
			tmpDP = new TurnDataProp();
			tmpDP.eConnect = CONNECT_TYPE.COM2;
		} else if (3 == port) {
			tmpDP = new TurnDataProp();
			tmpDP.eConnect = CONNECT_TYPE.NET0;
		}

		byte tmpBytes[] = new byte[data.length]; // 获得写入字节长度

		for (int i = 0; i < tmpBytes.length; i++) {
			tmpBytes[i] = data[i]; // 转储数据
		}
		if (tmpDP!=null) {
			tmpDP.nSendData = tmpBytes; // 绑定要写入的数据
		}
		
		return tmpDP;
	}

	/**
	 * 初始化常驻的自由口参数
	 * */
	private static void CmnCheck() {

		if (null == CmnCom1) {
			CmnCom1 = new CmnHolder();
			CmnCom1.setName("CmnCom1");
		}

		if (null == CmnCom2) {
			CmnCom2 = new CmnHolder();
			CmnCom2.setName("CmnCom2");
		}

		if (null == CmnNet0) {
			CmnNet0 = new CmnHolder();
			CmnNet0.setName("CmnNet0");
		}
	}

	/**
	 * 将参数从地址空间同步到变量中
	 */
	public synchronized static void pullParams(ArrayList<MParamInfo> mplist,
			HashMap<String, PHolder> PHMap) {
		if (null == mplist) {// 若 宏指令的参数列表为空
			Log.e("ParamHelper", "pullParams: macro param list is null!");
			return;
		}   
		if (null == PHMap) {
			Log.e("ParamHelper", "pullParams: Param Holder Hashmap is null!");
			return;
		}

		boolean read=false;
		for (int i = 0; i < mplist.size(); i++) {
			read=false;
			MParamInfo info=mplist.get(i);
			if (info.RWPerm==0||info.RWPerm==2) {
				read=true;
			}
			
			if (!read) {
				//不是读取权限，直接跳过
				//Log.d("ParamHelper", "......no read ");
				continue;
			}
			
			switch (mplist.get(i).getDataType()) {// 不同的数据类型创建不同的持久器
			case MPTYPE.BOOL:// 布尔类型
				BoolHolder tmpBPH = (BoolHolder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpBPH.v = readBool(mplist.get(i).getAddrProp());
					} else { 
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpBPH.v = (readBool(newAddr));
					}
				}	
				break;
			case MPTYPE.BOOLSEQ:// 布尔类型序列
				BoolSeqHolder tmpBSPH = (BoolSeqHolder) mplist.get(i).mPHolder;
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpBSPH.v = (readBoolSeq(mplist.get(i).getAddrProp()));
				}
				break;
			case MPTYPE.SHORT:// 16位短整型
				ShortHolder tmpSPH = (ShortHolder) mplist.get(i).mPHolder;
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					//Log.d("ParamHelper", "<><><><><><><><>");
					if (!mplist.get(i).isbOffset()) {
						tmpSPH.v = readShort(mplist.get(i).getAddrProp());
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpSPH.v = (readShort(newAddr));
					}
				}else {
					CallbackItem item=mplist.get(i).getmCallItem();
					//Log.d("ParamHelper", "<<<<<<<<<<<<< item change ="+item.isbChange()+",id ="+item.nAddrId);
					if (item.isbChange()) {
						item.setbChange(false);
						//地址值有变化
						tmpSPH.v=(short)item.getnIValue();
					}
				}
				break;
			case MPTYPE.USHORT:// 无符号16位短整型
				UShortHolder tmpUSPH = (UShortHolder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpUSPH.v = (readUShort(mplist.get(i).getAddrProp()));
						
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpUSPH.v = (readUShort(newAddr));
					}
				}
				break;
			case MPTYPE.BCD16:// BCD16
				Bcd16Holder tmpBCD16PH = (Bcd16Holder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpBCD16PH.v = (readBCD16(mplist.get(i).getAddrProp()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpBCD16PH.v = (readBCD16(newAddr));
					}
				}
				break;
			case MPTYPE.SHORTSEQ:
				//16位整数序列
				ShortSeqHolder tmpSSEQPH = (ShortSeqHolder) mplist.get(i).mPHolder;
				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpSSEQPH.v = (readShortSeq(mplist.get(i).getAddrProp()));
				}
				break;
			case MPTYPE.USHORTSEQ:
				//16位正整数序列
				UShortSeqHolder tmpUSSEQPH = (UShortSeqHolder) mplist.get(i).mPHolder;
				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpUSSEQPH.v = (readUShortSeq(mplist.get(i).getAddrProp()));
				}
				break;
			case MPTYPE.BCD16SEQ:// BCD16序列
				Bcd16SeqHolder tmpBCD16SEQPH = (Bcd16SeqHolder) mplist.get(i).mPHolder;
				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpBCD16SEQPH.v = (readBcd16Seq(mplist.get(i).getAddrProp()));
				}
				
				break;
			case MPTYPE.INT: // 32位整型
				IntHolder tmpIPH = (IntHolder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpIPH.v = (readInt(mplist.get(i).getAddrProp()));
						
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						
						tmpIPH.v = (readInt(newAddr));
					}
					
				}
				break;
			case MPTYPE.UINT: // 无符号32位整型
				UIntHolder tmpUIPH = (UIntHolder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpUIPH.v = (readUInt(mplist.get(i).getAddrProp()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpUIPH.v = (readUInt(newAddr));
					}
				}

				break;
			case MPTYPE.BCD32: // BCD32
				Bcd32Holder tmpBCD32PH = (Bcd32Holder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpBCD32PH.v = (readBCD32(mplist.get(i).getAddrProp()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpBCD32PH.v = (readBCD32(newAddr));

					}
				}
				break;
			case MPTYPE.INTSEQ: // 32位整型序列
				IntSeqHolder tmpISPH = (IntSeqHolder) mplist.get(i).mPHolder;
				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpISPH.v = (readIntSeq(mplist.get(i).getAddrProp()));
				}
				break;
			case MPTYPE.UINTSEQ: // 无符号32位整型序列
				UIntSeqHolder tmpUISPH = (UIntSeqHolder) mplist.get(i).mPHolder;
				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpUISPH.v = (readUIntSeq(mplist.get(i).getAddrProp()));
				}
				break;
			case MPTYPE.BCD32SEQ: // BCD32序列
				Bcd32SeqHolder tmpBcd32SeqPH = (Bcd32SeqHolder) mplist.get(i).mPHolder;
				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpBcd32SeqPH.v = (readBcd32Seq(mplist.get(i).getAddrProp()));
				}
				
				break;
			case MPTYPE.FLOAT:// 32位单精度浮点
				FloatHolder tmpFPH = (FloatHolder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpFPH.v = (readFloat(mplist.get(i).getAddrProp()));
						
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpFPH.v = (readFloat(newAddr));

					}
				}
				break;
			case MPTYPE.FLOATSEQ:// 32位单精度浮点序列
				FloatSeqHolder tmpFSPH = (FloatSeqHolder) mplist.get(i).mPHolder;

				
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					tmpFSPH.v = (readFloatSeq(mplist.get(i).getAddrProp()));
				}
				break;
			case MPTYPE.STRING: // 字符串类型
				StringHolder tmpSTRP = (StringHolder) mplist.get(i).mPHolder;
				
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpSTRP.v = (readString(mplist.get(i).getAddrProp(),mplist.get(i).getCodeType()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpSTRP.v = (readString(newAddr, mplist.get(i).getCodeType()));
					}
				}
				break;
			}// End of: switch

		}
		
		// 设置自由口参数
		CmnCheck();
		
		// 将自由口缓存数据放入哈希表
		if (!PHMap.containsKey(CmnCom1.getName())) {
			PHMap.put(CmnCom1.getName(), CmnCom1);
		}

		if (!PHMap.containsKey(CmnCom2.getName())) {
			PHMap.put(CmnCom2.getName(), CmnCom2);
		}
		
		if (!PHMap.containsKey(CmnNet0.getName())) {
			PHMap.put(CmnNet0.getName(), CmnNet0);
		}
	}
	
	/**
	 * 获取自由口数据
	 */
	public synchronized static void getFreeComData(){
	
		// 从串口读取数据，并赋值到自由口缓存中
		readCMN(1, CmnHolder.BufLen);	
		readCMN(2, CmnHolder.BufLen);
		readCMN(3, CmnHolder.BufLen);
		
	}

	/**
	 * 读取串口数据
	 * @param port-串口
	 * @param length-数据长度
	 */
	private static void readCMN(int port, int length) {
		// 从自由口取数据到读缓存

		Vector<Byte> tmpBVect = null;
		switch (port) {
		case 1:
			tmpBVect = CmnCom1.getrvRef();
			CmnPortManage.getInstance().getFreePortData(CONNECT_TYPE.COM1,
					tmpBVect); // 读自由口数据读缓存
			break;
		case 2:
			tmpBVect = CmnCom2.getrvRef();
			CmnPortManage.getInstance().getFreePortData(CONNECT_TYPE.COM2,
					tmpBVect);
			break;
		case 3:
			tmpBVect = CmnNet0.getrvRef();
			CmnPortManage.getInstance().getFreePortData(CONNECT_TYPE.NET0,
					tmpBVect);
			break;
		}
	}


//	/**
//	 * 将参数列表中的数据同步到地址空间中
//	 * */
//	public synchronized static boolean pushParams(ArrayList<MParamInfo> mplist,
//			HashMap<String, PHolder> phmap) {
//
//		return true;
//	}

	
	//private static HashMap<Integer, ArrayList<MParamInfo>> mAddrMap=new HashMap<Integer, ArrayList<MParamInfo>>();
	
//	public static HashMap<Integer, ArrayList<MParamInfo>> getAddrMap(){
//		return mAddrMap;
//	}
	
	/**
	 * 添加地址
	 */
//	private static int nIndex=0;//地址存放序号
//	public static void addAddrList( ArrayList<MParamInfo> list){
//		if (list==null||list.size()==0) {
//			return;
//		}
//		
//		for (int i = 0; i < list.size(); i++) {
//			MParamInfo info=list.get(i);
//			info.mPHolder.setnAllParamIndex(nIndex);
//			info.mPHolder.setnParamIndex(i);
//			info.mPHolder.setnRWPerm(info.RWPerm);
//			info.mPHolder.setmAddrProp(info.getAddrProp());
//			if (info.isbOffset()) {
//				info.mPHolder.setbOffSetAddr(true);
//			}else {
//				info.mPHolder.setbOffSetAddr(false);
//			}
//			if (info.DataType==MPTYPE.STRING) {
//				StringHolder mPHolder=(StringHolder)info.mPHolder;
//				mPHolder.nCode=info.CodeType;
//			}
//		}
//		mAddrMap.put(nIndex, list);
//		nIndex++;
//	}
	

	/**
	 * 自由口数据写入串口
	 */
	public static void setComData(byte[] data){
		
		CmnCheck();
		
		
		if (0 != CmnCom1.gett()) {
			TurnDataProp prop=updateCmnDataProp(data,1);
			if (prop!=null) {
				writeCMN(prop,1, prop.nSendData.length); // 将缓存写入自由口
			}

		} else if (0 < CmnCom1.getPortClear()) {// 仅清除自由口缓存
			CmnCom1.resetPortClear(); // 复位清除标识
			TurnDataProp prop=updateCmnDataProp(data,1);
			if (prop!=null) {
				CmnPortManage.getInstance().clearFreePort(prop.eConnect);
			}
		}

		if (0 != CmnCom2.gett()) {
			TurnDataProp prop=updateCmnDataProp(data,2);
			if(prop!=null){
				writeCMN(prop,2, prop.nSendData.length);
			}
		} else if (0 < CmnCom2.getPortClear()) {
			CmnCom2.resetPortClear();
			TurnDataProp prop=updateCmnDataProp(data,2);
			if (prop!=null) {
				CmnPortManage.getInstance().clearFreePort(prop.eConnect);
			}
		}

		if (0 != CmnNet0.gett()) {
			TurnDataProp prop=updateCmnDataProp(data,3);
			if (prop!=null) {
				writeCMN(prop,3, prop.nSendData.length);
			}
		} else if (0 < CmnNet0.getPortClear()) {
			CmnNet0.resetPortClear();
			TurnDataProp prop=updateCmnDataProp(data,3);
			if (prop!=null) {
				CmnPortManage.getInstance().clearFreePort(prop.eConnect);
			}
		}
	}
	
	/**
	 * 清除串口缓存
	 */
	public synchronized static void clearComData(){
		
		CmnCheck();
		
		if (CmnCom1.getPortClear() > 0) {// 需要刷新自由口缓存
			CmnCom1.resetPortClear();
			CmnPortManage.getInstance().clearFreePort(CONNECT_TYPE.COM1);
		}

		if (CmnCom2.getPortClear() > 0) {// 需要刷新自由口缓存
			CmnCom2.resetPortClear();
			CmnPortManage.getInstance().clearFreePort(CONNECT_TYPE.COM2);
		}

		if (CmnNet0.getPortClear() > 0) {// 需要刷新自由口缓存
			CmnNet0.resetPortClear();
			CmnPortManage.getInstance().clearFreePort(CONNECT_TYPE.NET0);
		}
	}
	
	/**
	 * 将缓存中的数据写出到自由口
	 * */
	private static void writeCMN(TurnDataProp prop, int port, int len) {
		switch (port) {
		case 1:
			if (CmnCom1.getPortClear() > 0) {// 需要刷新自由口缓存
				CmnCom1.resetPortClear();
				CmnPortManage.getInstance().clearFreePort(prop.eConnect);
			}
			SKCommThread.turnDataToOtherPort(prop);
			CmnCom1.sett(0); // 清除写标记
			break;
		case 2:
			if (CmnCom2.getPortClear() > 0) {// 需要刷新自由口缓存
				CmnCom2.resetPortClear();
				CmnPortManage.getInstance().clearFreePort(prop.eConnect);
			}
			SKCommThread.turnDataToOtherPort(prop);
			CmnCom2.sett(0); // 清除写标记
			break;
		case 3:
			if (CmnNet0.getPortClear() > 0) {// 需要刷新自由口缓存
				CmnNet0.resetPortClear();
				CmnPortManage.getInstance().clearFreePort(prop.eConnect);
			}
			SKCommThread.turnDataToOtherPort(prop);
			CmnNet0.sett(0); // 清除写标记
			break;
		}
	}

	/**
	 * 读取指定地址中的一个Bool数据
	 * */
	private static boolean readBool(AddrProp addr) {
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
	 * 读取指定地址中的Bool数据序列
	 * */
	private static boolean[] readBoolSeq(AddrProp addr) {

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
	 * 读取指定地址中的一个短整型数据
	 * */
	private static short readShort(AddrProp addr) {

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
	private static int readUShort(AddrProp addr) {

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
	private static int readBCD16(AddrProp addr) {

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
	 * 读取指定地址中的短整型数据序列
	 * 
	 * @param addr
	 *            批量变量的首地址
	 * @param num
	 *            批量变量的元素个数
	 * */
	private static short[] readShortSeq(AddrProp addr) {

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
	private static int[] readUShortSeq(AddrProp addr) {

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

	private static int[] readBcd16Seq(AddrProp addr) {

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
	 * 读取指定地址中的一个整型数据
	 * */
	private static int readInt(AddrProp addr) {

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
	private static long readUInt(AddrProp addr) {

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
	private static long readBCD32(AddrProp addr) {

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
	 * 读取指定地址中的整型数据序列
	 * */
	private static int[] readIntSeq(AddrProp addr) {

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
	private static long[] readUIntSeq(AddrProp addr) {

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
	private static long[] readBcd32Seq(AddrProp addr) {

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
	 * 读取指定地址中的一个整型数据
	 * */
	private static float readFloat(AddrProp addr) {

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
	 * 读取指定地址中的单精度浮点数数据序列
	 * */
	private static float[] readFloatSeq(AddrProp addr) {

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
	 * 读取指定地址中的 字符串
	 * */
	private static String readString(AddrProp addr, short ctype) {

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


		if (isASCII==1) {
			//ASCII，地址长度除以2
			AddrProp prop=addr.clone();
			prop.nAddrLen=(prop.nAddrLen+1)/2;
			PlcRegCmnStcTools.getRegAsciiData(prop, dataList, QuestInfo, true);
		}else {
			//UNICODE
			PlcRegCmnStcTools.getRegAsciiData(addr, dataList, QuestInfo, true);
		}
		
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
						"readString: get unicode String failed!");
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
						"readString: get ascii String failed!");
				e.printStackTrace();
			}
			//Log.d("Item", "str = "+str);
		} else {
			return str;
		}
		
		return str;
	}


}// End of class

