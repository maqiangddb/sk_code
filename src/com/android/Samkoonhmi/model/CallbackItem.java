package com.android.Samkoonhmi.model;

import java.util.Vector;

import android.R.integer;

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
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread.IPlcNoticCallBack;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.DataTypeFormat;

/**
 * 不固定数量的地址,
 * 地址值变化,回调通知item
 */
public class CallbackItem {

	private boolean nBValue;
	private int nIValue=0;
	private float nFValue=0;
	private long nLValue=0;
	private String sValue="";
	private boolean result;
	//地址数据类型
	public DATA_TYPE eDataType;
	//是否是UNICODE编码
	public boolean isUnicode;
	//是否已经回调
	private boolean hasCallback;
	//监视地址
	private AddrProp mAddrProp;
	//是否已经注册
	private boolean isRegister;
	//长度
	private int nAddrLen=1;//默认1
	//宏指令参数
	public PHolder   mPHolder=null;
	//注册控件类型
	public short nType=0;//0-代表，采集，报警，1-代表宏指令
	//地址变化
	private boolean bChange=true;

	/**
	 * 地址监视
	 */
	IPlcNoticCallBack callBack=new IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue==null||nStatusValue.size() == 0) {
				return;
			}
			change(nStatusValue);
			bChange=true;
			hasCallback=true;
		}
	};
	
	/**
	 * 地址偏移
	 */
	private Vector<Short> mSData=null;
	private int nOffsetValue=0;
	private AddrProp mNewAddr;
	IPlcNoticCallBack addrOffset=new IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue==null||nStatusValue.size() == 0) {
				return;
			}
			if (mSData==null) {
				mSData=new Vector<Short>();
			}else {
				mSData.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSData);
			if (result) {
				nOffsetValue=mSData.get(0);
			}
			if (nOffsetValue==0&&bInit) {
				return;
			}
			if (mAddrProp==null) {
				return;
			}
			bInit=false;
			int temp=mAddrProp.nAddrValue+nOffsetValue;
			if (temp<0) {
				SKPlcNoticThread.getInstance().destoryCallback(callBack);
				Vector<Byte> nValue=new Vector<Byte>();
				nValue.add((byte)0);
				change(nValue);
				return;
			}
			if (mNewAddr==null) {
				AddrProp addrProp = new AddrProp();
				addrProp.eAddrRWprop = mAddrProp.eAddrRWprop;
				addrProp.eConnectType = mAddrProp.eConnectType;
				addrProp.nAddrId = mAddrProp.nAddrId;
				addrProp.nAddrLen = mAddrProp.nAddrLen;
				addrProp.nAddrValue = temp;
				addrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
				addrProp.nRegIndex = mAddrProp.nRegIndex;
				addrProp.nUserPlcId = mAddrProp.nUserPlcId;
				addrProp.sPlcProtocol = mAddrProp.sPlcProtocol;
				mNewAddr=addrProp;
			}else {
				mNewAddr.nAddrValue=temp;
			}
			
			SKPlcNoticThread.getInstance().destoryCallback(callBack);
			SKPlcNoticThread.getInstance().addNoticProp(mNewAddr, callBack, false);
			
			if (mAddrProp.eConnectType>1) {
				SKSceneManage.getInstance().updateSceneReadAddrs(nSceneId, mNewAddr);
			}
			
		}
	};
	
	
	/**
	 * 监视地址值发生变化
	 */
	public boolean []mBDatas=null;//宏指令批量boolean类型
	public short []mSDatas=null;//宏指令批量short类型
	public int []mIDatas=null;//宏指令批量int类型
	public long []mLDatas=null;//宏指令批量long类型
	public float []mFDatas=null;//宏指令批量float类型
	
	private Vector<Integer> mIDataList=null;
	private Vector<Short> mSDataList=null;
	private Vector<Long> mLDataList=null;
	private Vector<Float> mFDataList=null;
	private void change(Vector<Byte> nStatusValue){
		
		switch (eDataType) {
		case BIT_1:
			//位
			if (nAddrLen==1) {
				nIValue=nStatusValue.get(0);
				nFValue=nIValue;
				if(nFValue==0)
				{
					nBValue = false;
				}else{
					nBValue = true;
				}
				if (nType==1&&mPHolder!=null) {
					BoolHolder temp=(BoolHolder)mPHolder;
					temp.v=nBValue;
				}
			}else {
				//批量
				if (nAddrLen==nStatusValue.size()) {
					if (nType==1&&mPHolder!=null) {
						BoolSeqHolder temps=(BoolSeqHolder)mPHolder;
						if (temps.v==null) {
							temps.v=new boolean[nAddrLen];
						}
						if (mBDatas==null) {
							mBDatas=new boolean[nAddrLen];
						}
						for (int i = 0; i < temps.v.length; i++) {
							if(nStatusValue.get(i)==1){
								temps.v[i]=true;
								mBDatas[i]=true;
							}else {
								temps.v[i]=false;
								mBDatas[i]=false;
							}
						}
					}
					
				}
			}
			break;
		case INT_16:
			//16位整数
			if (mSDataList==null) {
				mSDataList=new Vector<Short>();
			}else {
				mSDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSDataList);
			if (result) {
				if (nAddrLen==1) {
					nIValue=mSDataList.get(0);
					nFValue=nIValue;
					if (nType==1&&mPHolder!=null) {
						ShortHolder temp=(ShortHolder)mPHolder;
						temp.v=(short)nIValue;
					}
					
				}else {
					if (mSDataList.size()==nAddrLen) {
						if (nType==1&&mPHolder!=null) {
							ShortSeqHolder temps=(ShortSeqHolder)mPHolder;
							if (temps.v==null) {
								temps.v=new short[nAddrLen];
							}
							if(mSDatas==null){
								mSDatas=new short[nAddrLen];
							}
							for (int i = 0; i < temps.v.length; i++) {
								temps.v[i]=mSDataList.get(i);
								mSDatas[i]=mSDataList.get(i);
							}
						}
						
					}
				}
				
			}
			break;
		case POSITIVE_INT_16:
			//16正整数
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				if (nAddrLen==1) {
					nIValue=mIDataList.get(0);
					nFValue=nIValue;
					if (nType==1&&mPHolder!=null) {
						UShortHolder temp=(UShortHolder)mPHolder;
						temp.v=(short)nIValue;
					}
				}else {
					//批量
					if (nAddrLen==mIDataList.size()) {
						if (nType==1&&mPHolder!=null) {
							if (nType==1&&mPHolder!=null) {
								UShortSeqHolder temps=(UShortSeqHolder)mPHolder;
								if (temps.v==null) {
									temps.v=new int[nAddrLen];
								}
								if (mIDatas==null) {
									mIDatas=new int[nAddrLen];
								}
								for (int i = 0; i < temps.v.length; i++) {
									temps.v[i]=mIDataList.get(i);
									mIDatas[i]=mIDataList.get(i);
								}
							}
							
						}
					}
				}
				
			}
			break;
		case INT_32:
			//32整数
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToInts(nStatusValue, mIDataList);
			if (result) {
				if (nAddrLen==1) {
					nIValue=mIDataList.get(0);
					nFValue=nIValue;
					if (nType==1&&mPHolder!=null) {
						IntHolder temp=(IntHolder)mPHolder;
						temp.v=nIValue;
					}
				}else {
					//批量
					if (mIDataList.size()==nAddrLen/2) {
						if (nType==1&&mPHolder!=null) {
							IntSeqHolder temps=(IntSeqHolder)mPHolder;
							if (temps.v==null) {
								temps.v=new int[nAddrLen/2];
							}
							if (mIDatas==null) {
								mIDatas=new int[nAddrLen/2];
							}
							for (int i = 0; i < nAddrLen/2; i++) {
								temps.v[i]=mIDataList.get(i);
								mIDatas[i]=mIDataList.get(i);
							}
						}
					}
				}
			}
			break;
		case POSITIVE_INT_32: 
			// 32位正整数
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				if (nAddrLen==1) {
					nLValue=mLDataList.get(0);
					nFValue=nLValue;
					if (nType==1&&mPHolder!=null) {
						UIntHolder temp=(UIntHolder)mPHolder;
						temp.v=nLValue;
					}
				}else {
					//批量
					if (mLDataList.size()==nAddrLen/2) {
						if (nType==1&&mPHolder!=null) {
							UIntSeqHolder temps=(UIntSeqHolder)mPHolder;
							if (temps.v==null) {
								temps.v=new long[nAddrLen/2];
							}
							if (mLDatas==null) {
								mLDatas=new long[nAddrLen/2];
							}
							for (int i = 0; i < nAddrLen/2; i++) {
								temps.v[i]=mLDataList.get(i);
								mLDatas[i]=mLDataList.get(i);
							}
						}
					}
					
				}
				
			}
			break;
		case BCD_16:
			// 调用BCD码转换
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				if (nAddrLen==1) {
					nIValue=mIDataList.get(0);
					nFValue=nIValue;
					if (nType==1&&mPHolder!=null) {
						Bcd16Holder temp=(Bcd16Holder)mPHolder;
						temp.v=(short)nIValue;
					}
				}else {
					//批量
					if (mIDataList.size()==nAddrLen) {
						if (nType==1&&mPHolder!=null) {
							Bcd16SeqHolder temps=(Bcd16SeqHolder)mPHolder;
							if (temps.v==null) {
								temps.v=new int[nAddrLen];
							}
							if (mIDatas==null) {
								mIDatas=new int[nAddrLen];
							}
							for (int i = 0; i < temps.v.length; i++) {
								temps.v[i]=mIDataList.get(i);
								mIDatas[i]=mIDataList.get(i);
							}
						}
					}
				}
				
			}
			break;
		case BCD_32:
			// 调用BCD码转换
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				if (nAddrLen==1) {
					nLValue=mLDataList.get(0);
					nFValue=nLValue;
					if (nType==1&&mPHolder!=null) {
						Bcd32Holder temp=(Bcd32Holder)mPHolder;
						temp.v=nLValue;
					}
				}else {
					//批量
					if (mLDataList.size()==nAddrLen/2) {
						if (nType==1&&mPHolder!=null) {
							Bcd32SeqHolder temps=(Bcd32SeqHolder)mPHolder;
							if (temps.v==null) {
								temps.v=new long[nAddrLen/2];
							}
							if (mLDatas==null) {
								mLDatas=new long[nAddrLen/2];
							}
							for (int i = 0; i < nAddrLen/2; i++) {
								temps.v[i]=mLDataList.get(i);
								mLDatas[i]=mLDataList.get(i);
							}
						}
					}
				}
				
			}
			break;
		case FLOAT_32: 
			// 浮点数
			if (mFDataList==null) {
				mFDataList=new Vector<Float>();
			}else {
				mFDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToFloats(nStatusValue, mFDataList);
			if (result) {
				if (nAddrLen==1) {
					nFValue=mFDataList.get(0);
					if (nType==1&&mPHolder!=null){
						FloatHolder temp=(FloatHolder)mPHolder;
						temp.v=nFValue;
					}
					
				}else {
					//批量
					if (mFDataList.size()==nAddrLen/2) {
						if (nType==1&&mPHolder!=null) {
							FloatSeqHolder temps=(FloatSeqHolder)mPHolder;
							if (temps.v==null) {
								temps.v=new float[nAddrLen/2];
							}
							if (mFDatas==null) {
								mFDatas=new float[nAddrLen/2];
							}
							for (int i = 0; i < nAddrLen/2; i++) {
								temps.v[i]=mFDataList.get(i);
								mFDatas[i]=mFDataList.get(i);
							}
						}
					}
				}
				
			}
			break;
		case OTC_16:
			// 16位8进制
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					mIDataList);
			if (result && 0 != mIDataList.size()) {
				String	temp = DataTypeFormat
						.intToOctStr(mIDataList.get(0));
			    nIValue = Integer.valueOf(temp);
			  
			}
			break;
		case OTC_32: 
			// 32位的8进制
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools
					.bytesToUInts(nStatusValue, mLDataList);
			if (result && 0 != mLDataList.size()) {
				String temp = DataTypeFormat
						.intToOctStr(mLDataList.get(0));
				nLValue = Long.valueOf(temp);
				
			}
			break;
		case HEX_16: 
			// 16位16进制
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					mIDataList);
			if (result && 0 != mIDataList.size()) {
				String temp  = DataTypeFormat
						.intToHexStr(mIDataList.get(0));
                nIValue =  Integer.valueOf(temp);
				
			}
			break;
		case HEX_32: 
			// 32位16进制
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools
					.bytesToUInts(nStatusValue, mLDataList);
			if (result && 0 != mLDataList.size()) {
				String temp = DataTypeFormat
						.intToHexStr((long) mLDataList.get(0));
				nLValue = Long.valueOf(temp);
				
			}
			break;
		case ASCII_STRING:
			if (isUnicode) {
				byte[] byteValue = new byte[nStatusValue.size() + 2];
				byteValue[0] = -1;
				byteValue[1] = -2;
				for (int i = 0; i < nStatusValue.size(); i++) {
					byteValue[i + 2] = nStatusValue.get(i);
				}
				sValue = converCodeShow(byteValue);
			} else {
				byte[] byteValue = new byte[nStatusValue.size()];
				for (int i = 0; i < nStatusValue.size(); i++) {
					byteValue[i] = nStatusValue.get(i);
				}
				sValue = converCodeShow(byteValue);
			}
			if (nType==1&&mPHolder!=null) {
				StringHolder temp=(StringHolder)mPHolder;
				temp.v=sValue;
			}
			break;
		}
		
	}
	
	/**
	 * 注册通知
	 * @param addr-需要监视地址
	 * @param bBitAddr-true,表示位; bBitAddr-false,表示字
	 */
	public void onRegister(AddrProp addr, boolean bBitAddr){
		if (addr==null) {
			return;
		}
		hasCallback=false;
		this.mAddrProp=addr;
		isRegister=true;
		SKPlcNoticThread.getInstance().addNoticProp(addr, callBack, bBitAddr);
	}
	
	/**
	 * 注册通知
	 * @param bBitAddr-true,表示位; bBitAddr-false,表示字
	 */
	public void onRegister(boolean bBitAddr){
		if (mAddrProp==null) {
			return;
		}
		hasCallback=false;
		isRegister=true;
		SKPlcNoticThread.getInstance().addNoticProp(mAddrProp, callBack, bBitAddr);
	}
	
	/**
	 * 删除绑定
	 */
	public void unRegister(){
		hasCallback=false;
		SKPlcNoticThread.getInstance().destoryCallback(callBack);
		isRegister=false;
	}

	/**
	 * 注册地址偏移回调
	 */
	private boolean bInit;
	private int nSceneId;
	public void onRegisterAddrOffset(AddrProp addr,int sid){
		if (addr==null) {
			return;
		}
		nSceneId=sid;
		bInit=true;
		SKPlcNoticThread.getInstance().addNoticProp(addr, addrOffset, false);
	}
	
	/**
	 * 删除地址偏移回调
	 */
	public void unRegisterAddrOffset(){
		SKPlcNoticThread.getInstance().destoryCallback(addrOffset);
	}
	
	/**
	 * 转换编码 再显示
	 * @param temp return 转换编码之后的 要显示的字符串
	 */
	private String converCodeShow(byte[] bTemp) {
		String returnValue = new String(bTemp);
		try {
			if (isUnicode) {
				returnValue = new String(bTemp, "UNICODE");

			} else {
				returnValue = new String(bTemp);
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnValue;
	}
	
	public int getnIValue() {
		return nIValue;
	}

	public float getnFValue() {
		return nFValue;
	}

	public long getnLValue() {
		return nLValue;
	}

	public String getsValue() {
		return sValue;
	}
	
	public boolean hasCallback() {
		return hasCallback;
	}
	
	public boolean isRegister() {
		return isRegister;
	}

	public boolean isnBValue() {
		return nBValue;
	}
	
	public void setnAddrLen(int nAddrLen){
		this.nAddrLen=nAddrLen;
	}

	public boolean isbChange() {
		return bChange;
	}

	public void setbChange(boolean bChange) {
		this.bChange = bChange;
	}
}
