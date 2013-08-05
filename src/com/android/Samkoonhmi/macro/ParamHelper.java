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

	// 自由口数据发送描述符
	private static TurnDataProp Com1DP = null;
	private static TurnDataProp Com2DP = null;
	private static TurnDataProp Net0DP = null;

	private static boolean mDebug = false;
	private static String mTag = "ParamHelper";

	/**
	 * 设置数据转发描述符
	 * */
	private static void updateCmnDataProp(int port) {

		TurnDataProp tmpDP = null;
		CmnHolder tmpPH = null;
		if (1 == port) {
			if (null == Com1DP) {
				Com1DP = new TurnDataProp();
				Com1DP.eConnect = CONNECT_TYPE.COM1;
			}

			tmpDP = Com1DP;
			tmpPH = CmnCom1;

		} else if (2 == port) {
			if (null == Com2DP) {
				Com2DP = new TurnDataProp();
				Com2DP.eConnect = CONNECT_TYPE.COM2;
			}
			tmpDP = Com2DP;
			tmpPH = CmnCom2;
		} else if (3 == port) {
			if (null == Net0DP) {
				Net0DP = new TurnDataProp();
				Net0DP.eConnect = CONNECT_TYPE.NET0;
			}
			tmpDP = Net0DP;
			tmpPH = CmnNet0;
		}

		byte tmpBytes[] = new byte[tmpPH.getwlen()]; // 获得写入字节长度
		byte srcBytes[] = tmpPH.getwvRef(); // 获得写数据缓存引用

		for (int i = 0; i < tmpBytes.length; i++) {
			tmpBytes[i] = srcBytes[i]; // 转储数据
		}
		tmpDP.nSendData = tmpBytes; // 绑定要写入的数据
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
	 * */
	public final static void pullParams(ArrayList<MParamInfo> mplist,
			HashMap<String, PHolder> PHMap) {
		if (null == mplist) {// 若 宏指令的参数列表为空
			Log.e("ParamHelper", "pullParams: macro param list is null!");
			return;
		}   
		if (null == PHMap) {
			Log.e("ParamHelper", "pullParams: Param Holder Hashmap is null!");
			return;
		}

		
		for (int i = 0; i < mplist.size(); i++) {
			switch (mplist.get(i).getDataType()) {// 不同的数据类型创建不同的持久器
			case MPTYPE.BOOL:// 布尔类型
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					BoolHolder tmpBPH = (BoolHolder) mplist.get(i).mPHolder;
					tmpBPH.setName(mplist.get(i).getName());
					
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpBPH.v = (readBool(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpBPH.v = (readBool(newAddr, mplist.get(i).getRWPerm()));
					}
				}
				
				break;
			case MPTYPE.BOOLSEQ:// 布尔类型序列
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					BoolSeqHolder tmpBSPH = (BoolSeqHolder) mplist.get(i).mPHolder;
					tmpBSPH.setName(mplist.get(i).getName());
					tmpBSPH.v = (readBoolSeq(mplist.get(i).getAddrProp(), mplist
							.get(i).getRWPerm()));
				}
				
				break;
			case MPTYPE.SHORT:// 16位短整型
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					ShortHolder tmpSPH = (ShortHolder) mplist.get(i).mPHolder;
					tmpSPH.setName(mplist.get(i).getName());
					
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpSPH.v = (readShort(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpSPH.v = (readShort(newAddr, mplist.get(i)
								.getRWPerm()));
					}
				} 
				break;
			case MPTYPE.USHORT:// 无符号16位短整型
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					UShortHolder tmpUSPH = (UShortHolder) mplist.get(i).mPHolder;
					tmpUSPH.setName(mplist.get(i).getName());
					
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpUSPH.v = (readUShort(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpUSPH.v = (readUShort(newAddr, mplist.get(i)
								.getRWPerm()));
					}
				}
				break;
			case MPTYPE.BCD16:// BCD16
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					Bcd16Holder tmpBCD16PH = (Bcd16Holder) mplist.get(i).mPHolder;
					tmpBCD16PH.setName(mplist.get(i).getName());
					
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpBCD16PH.v = (readBCD16(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpBCD16PH.v = (readBCD16(newAddr, mplist.get(i)
								.getRWPerm()));
					}
				}

				break;
			case MPTYPE.SHORTSEQ:
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					ShortSeqHolder tmpSSEQPH = (ShortSeqHolder) mplist.get(i).mPHolder;
					tmpSSEQPH.setName(mplist.get(i).getName());
					tmpSSEQPH.v = (readShortSeq(mplist.get(i).getAddrProp(), mplist
							.get(i).getRWPerm()));
				}
				break;
			case MPTYPE.USHORTSEQ:
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					UShortSeqHolder tmpUSSEQPH = (UShortSeqHolder) mplist.get(i).mPHolder;
					tmpUSSEQPH.setName(mplist.get(i).getName());
					tmpUSSEQPH.v = (readUShortSeq(mplist.get(i).getAddrProp(),
							mplist.get(i).getRWPerm()));
				}
				break;
			case MPTYPE.BCD16SEQ:// BCD16序列
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					Bcd16SeqHolder tmpBCD16SEQPH = (Bcd16SeqHolder) mplist.get(i).mPHolder;
					tmpBCD16SEQPH.setName(mplist.get(i).getName());
					tmpBCD16SEQPH.v = (readBcd16Seq(mplist.get(i).getAddrProp(),
							mplist.get(i).getRWPerm()));
				}
				break;
			case MPTYPE.INT: // 32位整型
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					IntHolder tmpIPH = (IntHolder) mplist.get(i).mPHolder;
					tmpIPH.setName(mplist.get(i).getName());
					
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpIPH.v = (readInt(mplist.get(i).getAddrProp(), mplist
								.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						
						tmpIPH.v = (readInt(newAddr, mplist.get(i).getRWPerm()));
					}
					
				}

				break;
			case MPTYPE.UINT: // 无符号32位整型
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					UIntHolder tmpUIPH = (UIntHolder) mplist.get(i).mPHolder;
					tmpUIPH.setName(mplist.get(i).getName());
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpUIPH.v = (readUInt(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpUIPH.v = (readUInt(newAddr, mplist.get(i)
								.getRWPerm()));
					}
				}

				break;
			case MPTYPE.BCD32: // BCD32
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					Bcd32Holder tmpBCD32PH = (Bcd32Holder) mplist.get(i).mPHolder;
					tmpBCD32PH.setName(mplist.get(i).getName());
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpBCD32PH.v = (readBCD32(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpBCD32PH.v = (readBCD32(newAddr, mplist.get(i)
								.getRWPerm()));

					}
				}
				break;
			case MPTYPE.INTSEQ: // 32位整型序列
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					IntSeqHolder tmpISPH = (IntSeqHolder) mplist.get(i).mPHolder;
					tmpISPH.setName(mplist.get(i).getName());
					tmpISPH.v = (readIntSeq(mplist.get(i).getAddrProp(), mplist
							.get(i).getRWPerm()));
				}
				
				break;
			case MPTYPE.UINTSEQ: // 无符号32位整型序列
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					UIntSeqHolder tmpUISPH = (UIntSeqHolder) mplist.get(i).mPHolder;
					tmpUISPH.setName(mplist.get(i).getName());
					tmpUISPH.v = (readUIntSeq(mplist.get(i).getAddrProp(), mplist
							.get(i).getRWPerm()));
				}
				
				break;
			case MPTYPE.BCD32SEQ: // BCD32序列
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					Bcd32SeqHolder tmpBcd32SeqPH = (Bcd32SeqHolder) mplist.get(i).mPHolder;
					tmpBcd32SeqPH.setName(mplist.get(i).getName());
					tmpBcd32SeqPH.v = (readBcd32Seq(mplist.get(i).getAddrProp(),
							mplist.get(i).getRWPerm()));
				}
				
				break;
			case MPTYPE.FLOAT:// 32位单精度浮点
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					FloatHolder tmpFPH = (FloatHolder) mplist.get(i).mPHolder;
					tmpFPH.setName(mplist.get(i).getName());
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpFPH.v = (readFloat(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpFPH.v = (readFloat(newAddr, mplist.get(i)
								.getRWPerm()));

					}
				}
				break;
			case MPTYPE.FLOATSEQ:// 32位单精度浮点序列
				if (null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())) {
					FloatSeqHolder tmpFSPH = (FloatSeqHolder) mplist.get(i).mPHolder;
					tmpFSPH.setName(mplist.get(i).getName());
					tmpFSPH.v = (readFloatSeq(mplist.get(i).getAddrProp(), mplist
							.get(i).getRWPerm()));
				}
				
				break;
			case MPTYPE.STRING: // 字符串类型
				if(null ==mplist.get(i).getmCallItem() || (!mplist.get(i).getmCallItem().hasCallback())){
					StringHolder tmpSTRP = (StringHolder) mplist.get(i).mPHolder;
					tmpSTRP.setName(mplist.get(i).getName());
					// 没有地址偏移的
					if (!mplist.get(i).isbOffset()) {
						tmpSTRP.v = (readString(mplist.get(i).getAddrProp(),
								mplist.get(i).getRWPerm(), mplist.get(i)
										.getCodeType()));
					} else {
						// 有地址偏移的,
						// 取得合成后的地址
						AddrProp newAddr = ComposeAddr.getInstance()
								.newAddress(mplist.get(i).getAddrProp(),
										mplist.get(i).getnOffsetAddr());
						SKSceneManage.getInstance().updateSceneReadAddrs(mplist.get(i).getnSid(), newAddr);
						tmpSTRP.v = (readString(newAddr, mplist.get(i)
								.getRWPerm(), mplist.get(i).getCodeType()));
					}
				}
				break;
			}// End of: switch

		}

		// 设置自由口参数
		CmnCheck();
		// 从自由口读数据到读缓存
		readCMN(1, CmnCom1.BufLen);
		// 将自由口缓存数据放入哈希表
		if (!PHMap.containsKey(CmnCom1.getName())) {
			PHMap.put(CmnCom1.getName(), CmnCom1);
		}

		readCMN(2, CmnCom2.BufLen);
		if (!PHMap.containsKey(CmnCom2.getName())) {
			PHMap.put(CmnCom2.getName(), CmnCom2);
		}

		readCMN(3, CmnNet0.BufLen);
		if (!PHMap.containsKey(CmnNet0.getName())) {
			PHMap.put(CmnNet0.getName(), CmnNet0);
		}
	}

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


	/**
	 * 将参数列表中的数据同步到地址空间中
	 * */
	public final static boolean pushParams(ArrayList<MParamInfo> mplist,
			HashMap<String, PHolder> phmap) {

		if (null == mplist) {
			Log.e("ParamHelper", "pushParams: macro param list is null!");
			return false;
		}
		if (null == phmap) {
			Log.e("ParamHelper", "pushParams: param holder map is null!");
			return false;
		}

		boolean reuslt;
		boolean change;
		
		for (int i = 0; i < mplist.size(); i++) {
			PHolder tmpPH = mplist.get(i).mPHolder;
			MParamInfo tmpPI = mplist.get(i);
			if (1 == tmpPI.getRWPerm() || 2 == tmpPI.getRWPerm()) {// 若参数具有写权限
				if (mDebug) {
					Log.i(mTag,
							"pushParams: the Param type is: "
									+ tmpPI.getDataType());
				}
				
				reuslt=true;
				CallbackItem item=mplist.get(i).getmCallItem();
				
				switch (tmpPI.getDataType()) {
				case MPTYPE.BOOL: // 布尔类型
					if (null!=item) {
						if (item.isnBValue()== ((BoolHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if (tmpPI.isbOffset()) {
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeBool(newAddr, ((BoolHolder) tmpPH).v);
						} else {
							writeBool(tmpPI.getAddrProp(), ((BoolHolder) tmpPH).v);
						}

					}
					break;
				case MPTYPE.BOOLSEQ: // 布尔类型序列
					if (item!=null) {
						reuslt=false;
						change=false;
						
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							boolean []data=((BoolSeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mBDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mBDatas[j]) {
									change=true;
									break;
								}
							}
						}
						
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeBoolSeq(tmpPI.getAddrProp(), ((BoolSeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.SHORT: // 16位短整型
					if (mDebug) {
						Log.i(mTag, "pushParams: ShortHolder tmpPH.v is: "
								+ ((ShortHolder) tmpPH).v);
					}
					if (null!=item) {
						if (item.getnIValue()== ((ShortHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if (tmpPI.isbOffset()) {
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeShort(newAddr, ((ShortHolder) tmpPH).v);
						} else {
							writeShort(tmpPI.getAddrProp(), ((ShortHolder) tmpPH).v);
						}
					}
					break;
				case MPTYPE.USHORT: // 无符号16位短整型
					if (null!=item) {
						if (item.getnIValue()== ((UShortHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if(tmpPI.isbOffset())
						{
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeUShort(newAddr, ((UShortHolder) tmpPH).v);
						}else{
							writeUShort(tmpPI.getAddrProp(), ((UShortHolder) tmpPH).v);
						}
					}
					
					break;
				case MPTYPE.BCD16: // BCD16
					if (null!=item) {
						if (item.getnIValue()== ((Bcd16Holder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if(tmpPI.isbOffset())
						{
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeBCD16(newAddr, ((Bcd16Holder) tmpPH).v);
						}else{
							writeBCD16(tmpPI.getAddrProp(), ((Bcd16Holder) tmpPH).v);
						}
					}
					
					break;
				case MPTYPE.SHORTSEQ: // 16位短整型序列
					if (item!=null) {
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							short []data=((ShortSeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mSDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mSDatas[j]) {
									change=true;
									break;
								}
							}
						}
						
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeShortSeq(tmpPI.getAddrProp(),
								((ShortSeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.USHORTSEQ: // 无符号16位短整型序列
					if (item!=null) {
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							int []data=((UShortSeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mIDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mIDatas[j]) {
									change=true;
									break;
								}
							}
						}
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeUShortSeq(tmpPI.getAddrProp(),
								((UShortSeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.BCD16SEQ: // BCD16序列
					if (item!=null) {
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							int []data=((Bcd16SeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mIDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mIDatas[j]) {
									change=true;
									break;
								}
							}
						}
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeBcd16Seq(tmpPI.getAddrProp(),
								((Bcd16SeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.INT: // 32位整型数据
					if (null!=item) {
						if (item.getnIValue()== ((IntHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if(tmpPI.isbOffset())
						{
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeInt(newAddr, ((IntHolder) tmpPH).v);
						}else{
							writeInt(tmpPI.getAddrProp(), ((IntHolder) tmpPH).v);
						}
					}
					
					break;
				case MPTYPE.UINT: // 无符号32位整型数据
					if (null!=item) {
						if (item.getnLValue()== ((UIntHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if(tmpPI.isbOffset())
						{
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeUInt(newAddr, ((UIntHolder) tmpPH).v);
						}else{
							writeUInt(tmpPI.getAddrProp(), ((UIntHolder) tmpPH).v);
						}
					}
					break;
				case MPTYPE.BCD32: // BCD32
					if (null!=item) {
						if (item.getnLValue()== ((Bcd32Holder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					if (reuslt) {
						if(tmpPI.isbOffset())
						{
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeBCD32(newAddr, ((Bcd32Holder) tmpPH).v);
						}else{
							writeBCD32(tmpPI.getAddrProp(), ((Bcd32Holder) tmpPH).v);
						}
					}
					
					break;
				case MPTYPE.INTSEQ: // 32位整型数据序列
					if(item!=null){
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							int []data=((IntSeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mIDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mIDatas[j]) {
									change=true;
									break;
								}
							}
						}
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeIntSeq(tmpPI.getAddrProp(), ((IntSeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.UINTSEQ: // 32位整型数据序列
					if (item!=null) {
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							long []data=((UIntSeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mLDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mLDatas[j]) {
									change=true;
									break;
								}
							}
						}
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeUIntSeq(tmpPI.getAddrProp(), ((UIntSeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.BCD32SEQ: // BCD32序列
					if (item!=null) {
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							long []data=((Bcd32SeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mLDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mLDatas[j]) {
									change=true;
									break;
								}
							}
						}
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeBcd32Seq(tmpPI.getAddrProp(),
								((Bcd32SeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.FLOAT: // 32位单精度浮点数
					if (null!=item) {
						if (item.getnFValue()== ((FloatHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					
					if (reuslt) {
						if(tmpPI.isbOffset())
						{
							// 有地址偏移
							// 取得新地址，写入值
							AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
							writeFloat(newAddr, ((FloatHolder) tmpPH).v);
						}else{
							writeFloat(tmpPI.getAddrProp(), ((FloatHolder) tmpPH).v);
						}
					}
					
					break;
				case MPTYPE.FLOATSEQ: // 32位单精度浮点数序列
					if (item!=null) {
						change=false;
						reuslt=false;
						if (!item.isbChange()) {
							//地址值没改变，判断脚本有没有修改变量值
							float []data=((FloatSeqHolder) tmpPH).v;
							for (int j = 0; j < data.length; j++) {
								if (item.mFDatas==null) {
									change=true;
									break;
								}
								if (data[j]!=item.mFDatas[j]) {
									change=true;
									break;
								}
							}
						}
						if (change||item.isbChange()) {
							reuslt=true;
							item.setbChange(false);
						}
					}
					
					if (reuslt) {
						writeFloatSeq(tmpPI.getAddrProp(),
								((FloatSeqHolder) tmpPH).v);
					}
					break;
				case MPTYPE.STRING: // 字符串类型
					if (null!=item) {
						if (item.getsValue()== ((StringHolder) tmpPH).v&&(!item.isbChange())) {
							//监视地址没有变化，宏指令也没修改该参数，
							reuslt=false;
						}
						item.setbChange(false);
					}
					
					if (reuslt) {
						if (null != ((StringHolder) tmpPH).v) {
							if(tmpPI.isbOffset())
							{
								// 有地址偏移
								// 取得新地址，写入值
								AddrProp newAddr = ComposeAddr.getInstance().newAddress(tmpPI.getAddrProp(),tmpPI.getnOffsetAddr());
								writeString(newAddr,
										((StringHolder) tmpPH).v, tmpPI.getCodeType());
							}else{
								writeString(tmpPI.getAddrProp(),
										((StringHolder) tmpPH).v, tmpPI.getCodeType());
							}
							
						}
					}
					
					break;
				}// End of: switch
			}// End of: if
		}// End of : for

		CmnCheck();

		if (0 != CmnCom1.gett()) {
			updateCmnDataProp(1);
			writeCMN(1, CmnCom1.getwlen()); // 将缓存写入自由口

		} else if (0 < CmnCom1.getPortClear()) {// 仅清除自由口缓存
			CmnCom1.resetPortClear(); // 复位清除标识
			updateCmnDataProp(1);
			CmnPortManage.getInstance().clearFreePort(Com1DP.eConnect);
		}

		if (0 != CmnCom2.gett()) {
			updateCmnDataProp(2);
			writeCMN(2, CmnCom2.getwlen());
		} else if (0 < CmnCom2.getPortClear()) {
			CmnCom2.resetPortClear();
			updateCmnDataProp(2);
			CmnPortManage.getInstance().clearFreePort(Com2DP.eConnect);
		}

		if (0 != CmnNet0.gett()) {
			updateCmnDataProp(3);
			writeCMN(3, CmnNet0.getwlen());
		} else if (0 < CmnNet0.getPortClear()) {
			CmnNet0.resetPortClear();
			updateCmnDataProp(3);
			CmnPortManage.getInstance().clearFreePort(Net0DP.eConnect);
		}

		return true;
	}

	/**
	 * 将缓存中的数据写出到自由口
	 * */
	private static void writeCMN(int port, int len) {
		switch (port) {
		case 1:
			if (CmnCom1.getPortClear() > 0) {// 需要刷新自由口缓存
				CmnCom1.resetPortClear();
				CmnPortManage.getInstance().clearFreePort(Com1DP.eConnect);
			}
			SKCommThread.turnDataToOtherPort(Com1DP);
			CmnCom1.sett(0); // 清除写标记
			CmnCom1.setwlen(0); // 清除写入长度
			CmnCom1.wclear(); // 清除写出缓存
			break;
		case 2:
			if (CmnCom2.getPortClear() > 0) {// 需要刷新自由口缓存
				CmnCom2.resetPortClear();
				CmnPortManage.getInstance().clearFreePort(Com2DP.eConnect);
			}
			SKCommThread.turnDataToOtherPort(Com2DP);
			CmnCom2.sett(0); // 清除写标记
			CmnCom2.setwlen(0); // 清除写入长度
			CmnCom2.wclear(); // 清除写出缓存
			break;
		case 3:
			if (CmnNet0.getPortClear() > 0) {// 需要刷新自由口缓存
				CmnNet0.resetPortClear();
				CmnPortManage.getInstance().clearFreePort(Net0DP.eConnect);
			}
			SKCommThread.turnDataToOtherPort(Net0DP);
			CmnNet0.sett(0); // 清除写标记
			CmnNet0.setwlen(0); // 清除写入长度
			CmnNet0.wclear(); // 清除写出缓存
			break;
		}
	}

	/**
	 * 读取指定地址中的一个Bool数据
	 * */
	private static boolean readBool(AddrProp addr, int rw) {
		if (null == addr) {
			Log.e("ParamHelper", "readBool: addr is null!");
			return false;
		}

		if (0 == rw || 2 == rw) {

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
		return false;
	}

	/**
	 * 写指定地址中的一个Bool数据
	 * */
	private static boolean writeBool(AddrProp addr, boolean data) {
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
		PlcRegCmnStcTools.setRegBytesData(addr, dataList, QuestInfo);
		return true;
	}

	/**
	 * 读取指定地址中的Bool数据序列
	 * */
	private static boolean[] readBoolSeq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readBoolSeq: addr is null!");
			return null;
		}

		// 将数据转存到缓冲区
		boolean[] value = new boolean[addr.nAddrLen];

		if (0 == rw || 2 == rw) {// 只对具有读权限的数据进行操作
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
		}
		return value;
	}

	/**
	 * 写指定地址中的Bool数据序列
	 * */
	private static boolean writeBoolSeq(AddrProp addr, boolean[] data) {
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
		}
		// 批量写数据，数据个数为addr.nAddrLen
		PlcRegCmnStcTools.setRegBytesData(addr, dataList, QuestInfo);
		return true;
	}

	/**
	 * 读取指定地址中的一个短整型数据
	 * */
	private static short readShort(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readShort: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		} else {
			return 0;
		}
	}

	/**
	 * 读取指定地址中的一个无符号短整型数据
	 * */
	private static int readUShort(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readUShort: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		} else {
			return 0;
		}
	}

	/**
	 * 读取指定地址中的BCD16数据
	 * */
	private static int readBCD16(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readBCD16: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
			SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
			QuestInfo.eDataType = DATA_TYPE.BCD_16; // 设置数据类型
			QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
			QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

			Vector<Integer> dataList = new Vector<Integer>();

			PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

			if (0 == dataList.size()) {
				Log.e("ParamHelper", "readBCD16: dataList size is 0");
				return 0;
			}
			return dataList.get(0);
		} else {
			return 0;
		}
	}

	/**
	 * 向指定地址中写入短整型数据
	 * */
	private static boolean writeShort(AddrProp addr, int data) {
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
	private static boolean writeUShort(AddrProp addr, int data) {
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
	private static boolean writeBCD16(AddrProp addr, int data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeBCD16: addr is null!");
			return false;
		}

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
	 * 
	 * @param addr
	 *            批量变量的首地址
	 * @param num
	 *            批量变量的元素个数
	 * */
	private static short[] readShortSeq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readShortSeq: addr is null!");
			return null;
		}

		short[] value = new short[addr.nAddrLen];

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
				;
			}
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
	private static int[] readUShortSeq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readUShortSeq: addr is null!");
			return null;
		}

		int[] value = new int[addr.nAddrLen];

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		}
		return value;
	}

	private static int[] readBcd16Seq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readBcd16Seq: addr is null!");
			return null;
		}

		int[] value = new int[addr.nAddrLen];

		if (0 == rw || 2 == rw) {
			// 新建请求命令
			SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
			QuestInfo.eDataType = DATA_TYPE.BCD_16; // 设置数据类型
			QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
			QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

			// 批量读取数据，数据个数是addr.nAddrLen
			Vector<Integer> dataList = new Vector<Integer>();
			PlcRegCmnStcTools.getRegIntData(addr, dataList, QuestInfo);

			if (0 == dataList.size()) {
				Log.e("ParamHelper", "readBcd16Seq: dataList size is 0");
				return null;
			}

			// 将数据转存到缓冲区
			for (short i = 0; i < (addr.nAddrLen); i++) {
				value[i] = dataList.get(i);
			}
		}
		return value;
	}

	/**
	 * 向指定地址中写入短整型数据序列
	 * */
	private static boolean writeShortSeq(AddrProp addr, short[] data) {
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
	private static boolean writeUShortSeq(AddrProp addr, int[] data) {
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
	private static boolean writeBcd16Seq(AddrProp addr, int[] data) {
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
			dataList.add(data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen
		PlcRegCmnStcTools.setRegIntData(addr, dataList, QuestInfo);

		return true;
	}

	/**
	 * 读取指定地址中的一个整型数据
	 * */
	private static int readInt(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readInt: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		} else {
			return 0;
		}
	}

	/**
	 * 读取指定地址中的一个无符号32位整型数据
	 * */
	private static long readUInt(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readInt: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		} else {
			return 0;
		}
	}

	/**
	 * 读取指定地址中的一个BCD32
	 * */
	private static long readBCD32(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readBCD32: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
			SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
			QuestInfo.eDataType = DATA_TYPE.BCD_32; // 设置数据类型
			QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
			QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;// 设置请求访问类型

			Vector<Long> dataList = new Vector<Long>();

			PlcRegCmnStcTools.getRegLongData(addr, dataList, QuestInfo);

			if (0 == dataList.size()) {
				Log.e("ParamHelper", "readBCD32: dataList size is 0");
				return 0;
			}
			return dataList.get(0);
		} else {
			return 0;
		}
	}

	/**
	 * 向指定地址中写入整型数据
	 * */
	private static boolean writeInt(AddrProp addr, int data) {
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
	private static boolean writeUInt(AddrProp addr, long data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeUInt: addr is null!");
			return false;
		}

		if (mDebug) {
			Log.i(mTag, "writeUInt: the data is: " + data);
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
	private static boolean writeBCD32(AddrProp addr, long data) {
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

		dataList.add(data); // 将数据暂存

		PlcRegCmnStcTools.setRegLongData(addr, dataList, QuestInfo);

		// int v = readUInt(addr); 调试代码
		return true;
	}

	/**
	 * 读取指定地址中的整型数据序列
	 * */
	private static int[] readIntSeq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readIntSeq: addr is null!");
			return null;
		}

		int[] value = new int[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		}
		return value;
	}

	/**
	 * 读取指定地址中的无符号整型数据序列
	 * */
	private static long[] readUIntSeq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readUIntSeq: addr is null!");
			return null;
		}

		long[] value = new long[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		}
		return value;
	}

	/**
	 * 读取指定地址中的无符号整型数据序列
	 * */
	private static long[] readBcd32Seq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readBcd32Seq: addr is null!");
			return null;
		}

		long[] value = new long[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
				value[i] = dataList.get(i);
			}
		}
		return value;
	}

	/**
	 * 向指定地址中写入整型数据序列
	 * */
	private static boolean writeIntSeq(AddrProp addr, int[] data) {
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
	private static boolean writeUIntSeq(AddrProp addr, long[] data) {
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
	private static boolean writeBcd32Seq(AddrProp addr, long[] data) {
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
			dataList.add(data[i]);
		}

		// 批量写数据，数据个数为addr.nAddrLen/2
		PlcRegCmnStcTools.setRegLongData(addr, dataList, QuestInfo);

		// int[] seq = readUIntSeq(addr);//调试代码
		return true;
	}

	/**
	 * 读取指定地址中的一个整型数据
	 * */
	private static float readFloat(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readFloat: addr is null!");
			return -1;
		}

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		} else {
			return 0;
		}
	}

	/**
	 * 向指定地址中写入整型数据
	 * */
	private static boolean writeFloat(AddrProp addr, float data) {
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
	private static float[] readFloatSeq(AddrProp addr, int rw) {

		if (null == addr) {
			Log.e("ParamHelper", "readFloatSeq: addr is null!");
			return null;
		}

		float[] value = new float[addr.nAddrLen / 2]; // 新建存放读取数据的缓冲区

		if (0 == rw || 2 == rw) {
			// 新建请求命令
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
		}
		return value;
	}

	/**
	 * 向指定地址中写入单精度浮点数数据序列
	 * */
	private static boolean writeFloatSeq(AddrProp addr, float[] data) {
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
	private static String readString(AddrProp addr, int rw, short ctype) {

		if (mDebug) {
			Log.i(mTag, "readString: ++++++++++++++++++++++++++");
		}

		if (null == addr) {
			Log.e("ParamHelper", "readString: addr is null!");
			return "";
		}

		// 构成字符串
		String str = "";

		if (0 == rw || 2 == rw) {

			// 新建请求命令
			SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
			QuestInfo.eDataType = DATA_TYPE.ASCII_STRING; // 请求数据类型
			QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;// 请求访问类型为读

			short isASCII = ctype;
			Vector<Byte> dataList = new Vector<Byte>();

			if (mDebug) {
				Log.i(mTag, "readString: addr.nAddrLen is: " + addr.nAddrLen);
			}
			PlcRegCmnStcTools.getRegAsciiData(addr, dataList, QuestInfo, true);
			if (0 == dataList.size()) {
				Log.e("ParamHelper", "readString: dataList size is 0");
				return str;
			}
			if (mDebug) {
				Log.i(mTag,
						"readString: the dataList size is: " + dataList.size());
			}

			if (0 == isASCII) {// 若为Unicode编码
				int act_len = 0;
				for (act_len = 0; act_len < (dataList.size() - 2); act_len += 2) {
					if ((dataList.get(act_len) == 0)
							&& (dataList.get(act_len) == 0)) {
						break;
					}
				}
				if (mDebug) {
					Log.i(mTag, "readString: unicode act_len is: " + act_len);
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
						if (mDebug) {
							Log.i(mTag, "readString: unicode copy " + j
									+ " is: " + dataList.get(j));
						}
						i++;
					}
				}

				try {// 新建unicode字符串
					str = new String(bytearray, "UNICODE");
					if (mDebug) {
						Log.i(mTag, "readString: new unicode string length: "
								+ str.length());
						Log.i(mTag, "readString: new unicode string is: " + str);
					}
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
				if (mDebug) {
					Log.i(mTag,
							"readString: ascii act_len is: " + dataList.size());
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
					if (mDebug) {
						Log.i(mTag, "readString: ascii copy " + j + " is: "
								+ dataList.get(j));
					}
					i++;
				}
				try {// 新建ASCII字符串
					str = new String(bytearray, "US-ASCII");
					if (mDebug) {
						Log.i(mTag, "readString: new ascii string length: "
								+ str.length());
						Log.i(mTag, "readString: new ascii string is: " + str);
					}
				} catch (UnsupportedEncodingException e) {
					Log.e("ParamHelper",
							"writeString: get ascii String failed!");
					e.printStackTrace();
				}
			} else {
				return str;
			}
		}
		return str;
	}

	/**
	 * 向指定地址中写字符串
	 * */
	private static boolean writeString(AddrProp addr, String data, short ctype) {

		if (mDebug) {
			Log.i(mTag, "writeString: +++++++++++++++++++++++++");
		}

		if (null == addr) {
			Log.e("ParamHelper", "writeString: addr is null!");
			return false;
		}

		if (null == data) {
			if (mDebug) {
				Log.i(mTag, "writeString: data is null");
			}
			data = new String("");
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.ASCII_STRING; // 设置数据类型
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		if (mDebug) {
			Log.i(mTag, "writeString: addr.nAddrLen is: " + addr.nAddrLen);
		}

		short isASCII = ctype; // 是否为ASCII编码

		// 将字符串转换为字节数组后存入列表

		byte[] bytearray = null;
		Vector<Byte> dataList = new Vector<Byte>();
		if (0 == isASCII) {// 若是Unicode
			if (data.length() * 2 > addr.nAddrLen) {// 实际字符串所占空间大于了字符串地址长度
				if (mDebug) {
					Log.i(mTag, "writeString: data.length: " + data.length()
							* 2 + "  cut to nAddrLen: " + addr.nAddrLen);
				}
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
				if (mDebug) {
					Log.i(mTag, "writeString: " + i + " unicode copy "
							+ bytearray[i]);
				}
			}
			if (mDebug) {
				Log.i(mTag, "writeString: unicode length is: "
						+ bytearray.length);
			}
			if ((bytearray.length - 2) < addr.nAddrLen) {// 实际字符串长度不够的填入0
				if (mDebug) {
					Log.i(mTag, "WriteString: fill 0 to "
							+ (bytearray.length - addr.nAddrLen - 2)
							+ "empty byte");
				}
				for (int i = 0; i < (addr.nAddrLen - 2 - bytearray.length); i++) {
					dataList.add((byte) 0);
				}
			}
		} else if (1 == isASCII) {// 若是ASCII编码
			if (data.length() > addr.nAddrLen) {// 实际字符串所占空间大于了字符串地址长度
				if (mDebug) {
					Log.i(mTag, "writeString: data.length: " + data.length()
							+ "  cut to nAddrLen: " + addr.nAddrLen);
				}
				data = data.substring(0, (addr.nAddrLen));// 只获取规定长度的数据
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
			if (mDebug) {
				Log.i(mTag, "ascii length is: " + bytearray.length);
			}
			for (int i = 0; i < bytearray.length; i++) {// 逐个拷贝
				dataList.add(bytearray[i]);
				if (mDebug) {
					Log.i(mTag, "writeString: " + i + " ascii copy "
							+ bytearray[i]);
				}
			}
			if ((bytearray.length) < addr.nAddrLen) {// 实际字符串长度不够的填入0
				if (mDebug) {
					Log.i(mTag, "writeString: ascii: fill 0 to "
							+ (bytearray.length - addr.nAddrLen)
							+ " empty byte");
				}
				for (int i = 0; i < (addr.nAddrLen - bytearray.length); i++) {
					dataList.add((byte) 0);
				}
			}
		} else {
			return false;
		}

		PlcRegCmnStcTools.setRegAsciiData(addr, dataList, QuestInfo);

		// String str = readString(addr); //调试语句
		return true;
	}

}// End of class

