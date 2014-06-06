package com.android.Samkoonhmi.model;

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
	private boolean hasCallback=false;
	//监视地址
	private AddrProp mAddrProp;
	//是否已经注册
	private boolean isRegister;
	//长度
	private int nAddrLen=1;//默认1
	//注册控件类型
	public short nType=0;//0-代表，采集，报警，1-代表宏指令
	//地址变化
	private boolean bChange=true;
	//如果地址比较宏指令没有变化，再写入2次，因为读取需要时间，比较有误差
	public int nWriteCount;
	//是否产生上升沿
	private boolean isOffToOn;
	//地址引用
	public PHolder mPholder;
	//读写权限
	public int nRW;
	//回调次数，屏蔽掉第一次
	private int nIndex=0;

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
			if (nIndex==0) {
				nIndex++;
			}else {
				hasCallback=true;
			}
			
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
				SKPlcNoticThread.getInstance().destoryCallback(callBack,nSceneId);
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
			
			SKPlcNoticThread.getInstance().destoryCallback(callBack,nSceneId);
			SKPlcNoticThread.getInstance().addNoticProp(mNewAddr, callBack, false,nSceneId,true);
			
			if (mAddrProp.eConnectType>1) {
				SKSceneManage.getInstance().updateSceneReadAddrs(nSceneId, mNewAddr);
			}
			
		}
	};
	
	
	/**
	 * 监视地址值发生变化
	 */
	private boolean bInitData=true;
	private Vector<Integer> mIDataList=null;
	private Vector<Short> mSDataList=null;
	private Vector<Long> mLDataList=null;
	private Vector<Float> mFDataList=null;
	private boolean bLastState;
	public int nAddrId=0;
	private void change(Vector<Byte> nStatusValue){
		
		switch (eDataType) {
		case BIT_1:
			//位
			if (nAddrLen==1) {
				nIValue=nStatusValue.get(0);
				nFValue=nIValue;
				if(nFValue==0){
					nBValue = false;
				}else{
					nBValue = true;
				}
				if ((!bLastState)&&nBValue) {
					//off-->on 上升沿产生
					isOffToOn=true;
				}
				bLastState=nBValue;
				if (mPholder!=null) {
					//脚本地址值
					BoolHolder mHolder=(BoolHolder)mPholder;
					mHolder.nLastV=nBValue;
					if (nRW==0||nRW==2) {
						mHolder.v=nBValue;
					}
				}
				//Log.d("CallbackItem", "nBValue="+nBValue);
			}else {
				//批量
				if (nAddrLen==nStatusValue.size()) {
					if (nType==1) {
						if (mPholder!=null) {
							BoolSeqHolder tmpBSPH=(BoolSeqHolder)mPholder;
							if (tmpBSPH.v==null) {
								tmpBSPH.v=new boolean[nAddrLen];
							}
							if (tmpBSPH.nLastV==null) {
								tmpBSPH.nLastV=new boolean[nAddrLen];
							}
							for (int i = 0; i < nAddrLen; i++) {
								int value=nStatusValue.get(i);
								boolean result=false;
								if (value==1) {
									result=true;
								}
								tmpBSPH.nLastV[i]=result;
								if (nRW==0||nRW==2){
									tmpBSPH.v[i]=result;
								}
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
					
					if (mPholder!=null) {
						//脚本地址值
						ShortHolder mHolder=(ShortHolder)mPholder;	
						mHolder.nLastV=(short)nIValue;
						if (nRW==0||nRW==2) {
							mHolder.v=(short)nIValue;
						}
					}
				}else {
					if (mSDataList.size()==nAddrLen) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								ShortSeqHolder tmpSSEQPH=(ShortSeqHolder)mPholder;
								if (tmpSSEQPH.v==null) {
									tmpSSEQPH.v=new short[nAddrLen];
								}
								if (tmpSSEQPH.nLastV==null) {
									tmpSSEQPH.nLastV=new short[nAddrLen];
								}
								for (int j = 0; j < nAddrLen; j++) {
									tmpSSEQPH.nLastV[j]=mSDataList.get(j);
									if (nRW==0||nRW==2){
										tmpSSEQPH.v[j]=mSDataList.get(j);
									}
								}
								
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
					if (mPholder!=null) {
						//脚本地址值
						UShortHolder mHolder=(UShortHolder)mPholder;
						mHolder.nLastV=nIValue;
						if (nRW==0||nRW==2) {
							mHolder.v=nIValue;
						}
					}
				}else {
					//批量
					if (nAddrLen==mIDataList.size()) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								UShortSeqHolder tmpUSSEQPH=(UShortSeqHolder)mPholder;
								//地址值有变化
								if (tmpUSSEQPH.v==null) {
									tmpUSSEQPH.v=new int[nAddrLen];
								}
								if (tmpUSSEQPH.nLastV==null) {
									tmpUSSEQPH.nLastV=new int[nAddrLen];
								}
								for (int j = 0; j < nAddrLen; j++) {
									tmpUSSEQPH.nLastV[j]=mIDataList.get(j);
									if (nRW==0||nRW==2) {
										tmpUSSEQPH.v[j]=mIDataList.get(j);
									}
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
					if (mPholder!=null) {
						//脚本地址值
						IntHolder mHolder=(IntHolder)mPholder;
						mHolder.nLastV=nIValue;
						if (nRW==0||nRW==2) {
							mHolder.v=nIValue;
						}
					}
				}else {
					//批量
					if (mIDataList.size()==nAddrLen/2) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								IntSeqHolder tmpISPH=(IntSeqHolder)mPholder;
								//地址值有变化
								if (tmpISPH.v==null) {
									tmpISPH.v=new int[nAddrLen/2];
								}
								if (tmpISPH.nLastV==null) {
									tmpISPH.nLastV=new int[nAddrLen/2];
								}
								for (int j = 0; j < nAddrLen/2; j++) {
									tmpISPH.nLastV[j]=mIDataList.get(j);
									if (nRW==0||nRW==2) {
										tmpISPH.v[j]=mIDataList.get(j);
									}
								}
								
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
					
					if (mPholder!=null) {
						//脚本地址值
						UIntHolder mHolder=(UIntHolder)mPholder;
						mHolder.nLastV=nLValue;
						if (nRW==0||nRW==2) {
							mHolder.v=nLValue;
						}
					}
					
				}else {
					//批量
					if (mLDataList.size()==nAddrLen/2) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								//地址值有变化
								UIntSeqHolder tmpUISPH = (UIntSeqHolder)mPholder;
								if (tmpUISPH.v==null) {
									tmpUISPH.v=new long[nAddrLen/2];
								}
								if (tmpUISPH.nLastV==null) {
									tmpUISPH.nLastV=new long[nAddrLen/2];
								}
								for (int j = 0; j < nAddrLen/2; j++) {
									tmpUISPH.nLastV[j]=mLDataList.get(j);
									if (nRW==0||nRW==2) {
										tmpUISPH.v[j]=mLDataList.get(j);
									}
								}
								
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
					String s=DataTypeFormat.intToBcdStr((long) mIDataList.get(0), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nIValue=Integer.valueOf(s);
							nFValue=nIValue;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (mPholder!=null) {
						//脚本地址值
						Bcd16Holder mHolder=(Bcd16Holder)mPholder;
						mHolder.nLastV=nIValue;
						if (nRW==0||nRW==2) {
							mHolder.v=nIValue;
						}
					}
					
				}else {
					//批量
					if (mIDataList.size()==nAddrLen) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								//地址值有变化
								Bcd16SeqHolder tmpBCD16SEQPH = (Bcd16SeqHolder)mPholder;
								if (tmpBCD16SEQPH.v==null) {
									tmpBCD16SEQPH.v=new int[nAddrLen];
								}
								if (tmpBCD16SEQPH.nLastV==null) {
									tmpBCD16SEQPH.nLastV=new int[nAddrLen];
								}
								for (int j = 0; j < nAddrLen; j++) {
									int nV=0;
									String s=DataTypeFormat.intToBcdStr((long) mIDataList.get(j), false);
									if (s!=null&&!s.equals("ERROR")) {
										try {
											nV=Integer.valueOf(s);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									tmpBCD16SEQPH.nLastV[j]=nV;
									if (nRW==0||nRW==2) {
										tmpBCD16SEQPH.v[j]=nV;
									}
									
								}
								
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
					String s=DataTypeFormat.intToBcdStr((long) mLDataList.get(0), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nLValue=Integer.valueOf(s);
							nFValue=nLValue;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (mPholder!=null) {
						//脚本地址值
						Bcd32Holder mHolder=(Bcd32Holder)mPholder;
						mHolder.nLastV=nLValue;
						if (nRW==0||nRW==2) {
							mHolder.v=nLValue;
						}
					}
				}else {
					//批量
					if (mLDataList.size()==nAddrLen/2) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								Bcd32SeqHolder tmpBcd32SeqPH = (Bcd32SeqHolder)mPholder;
								if (tmpBcd32SeqPH.v==null) {
									tmpBcd32SeqPH.v=new long[nAddrLen/2];
								}
								if (tmpBcd32SeqPH.nLastV==null) {
									tmpBcd32SeqPH.nLastV=new long[nAddrLen/2];
								}
								
								for (int j = 0; j < nAddrLen/2; j++) {
									int nV=0;
									String s=DataTypeFormat.intToBcdStr((long) mLDataList.get(j), false);
									if (s!=null&&!s.equals("ERROR")) {
										try {
											nV=Integer.valueOf(s);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									tmpBcd32SeqPH.nLastV[j]=nV;
									if (nRW==0||nRW==2) {
										tmpBcd32SeqPH.v[j]=nV;
									}
								}
								
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
					if (mPholder!=null) {
						//脚本地址值
						FloatHolder mHolder=(FloatHolder)mPholder;
						mHolder.nLastV=nFValue;
						if (nRW==0||nRW==2) {
							mHolder.v=nFValue;
						}
					}
				}else {
					//批量
					if (mFDataList.size()==nAddrLen/2) {
						if (nType==1) {
							if (mPholder!=null) {
								//脚本地址值
								FloatSeqHolder tmpFSPH = (FloatSeqHolder)mPholder;
								//地址值有变化
								if (tmpFSPH.v==null) {
									tmpFSPH.v=new float[nAddrLen/2];
								}
								if (tmpFSPH.nLastV==null) {
									tmpFSPH.nLastV=new float[nAddrLen/2];
								}
								for (int j = 0; j < nAddrLen/2; j++) {
									tmpFSPH.nLastV[j]=mFDataList.get(j);
									if (nRW==0||nRW==2) {
										tmpFSPH.v[j]=mFDataList.get(j);
									}
								}
								
							}
						}
					}
				}
				
			}
			break;
		case ASCII_STRING:
			if (isUnicode) {
				// 若为Unicode编码
				int act_len = 0;
				for (act_len = 0; act_len < (nStatusValue.size() - 2); act_len += 2) {
					if ((nStatusValue.get(act_len) == 0)
							&& (nStatusValue.get(act_len) == 0)) {
						break;
					}
				}

				if (act_len != 0) {
					if (act_len >= nStatusValue.size()) {
						act_len = nStatusValue.size();
					}

					// 将获得的字符码转存到字节数组中
					byte[] bytearray = new byte[act_len + 2];

					// 添加unicode标记
					bytearray[0] = -1;
					bytearray[1] = -2;

					if (bytearray.length > 2) {
						for (int j = 0, i = 2; j < act_len; j++) {
							bytearray[i] = nStatusValue.get(j);
							i++;
						}
					}

					try {
						// 新建unicode字符串
						sValue = new String(bytearray, "UNICODE");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
			} else {
				int ascii_act_len = 0;
				for (ascii_act_len = 0; ascii_act_len < nStatusValue.size(); ascii_act_len++) {
					if (nStatusValue.get(ascii_act_len) == 0) {
						break;
					}
				}
				if (ascii_act_len != 0) {
					if (ascii_act_len >= nStatusValue.size()) {
						ascii_act_len = nStatusValue.size();
					}

					// 将获得的字符码转存到字节数组中
					byte[] bytearray = new byte[ascii_act_len];

					for (int j = 0, i = 0; j < ascii_act_len; j++) {
						bytearray[i] = nStatusValue.get(j);
						i++;
					}
					try {
						// 新建ASCII字符串
						sValue = new String(bytearray, "US-ASCII");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
			}
			if (mPholder!=null) {
				//脚本地址值
				StringHolder mHolder=(StringHolder)mPholder;
				mHolder.nLastV=sValue;
				if (nRW==0||nRW==2) {
					mHolder.v=sValue;
				}
			}
			break;
		}
		
	}
	
	/**
	 * 注册通知
	 * @param addr-需要监视地址
	 * @param bBitAddr-true,表示位; bBitAddr-false,表示字
	 * @param sid-场景id 0-全局
	 */
	public void onRegister(AddrProp addr, boolean bBitAddr,int sid){
		if (addr==null) {
			return;
		}
		hasCallback=false;
		this.mAddrProp=addr;
		isRegister=true;
		SKPlcNoticThread.getInstance().addNoticProp(addr, callBack, bBitAddr,sid,true);
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
		SKPlcNoticThread.getInstance().addNoticProp(mAddrProp, callBack, bBitAddr,0,true);
	}
	
	/**
	 * 删除绑定
	 * @param sid-画面id,0代表全局变量
	 */
	public void unRegister(int sid){
		hasCallback=false;
		SKPlcNoticThread.getInstance().destoryCallback(callBack,sid);
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
		SKPlcNoticThread.getInstance().addNoticProp(addr, addrOffset, false,nSceneId,true);
	}
	
	/**
	 * 删除地址偏移回调
	 * @param sid-画面id,0代表全局变量
	 */
	public void unRegisterAddrOffset(int sid){
		SKPlcNoticThread.getInstance().destoryCallback(addrOffset,sid);
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
	
	public void setnIValue(int value){
		this.nIValue=value;
	}

	public float getnFValue() {
		return nFValue;
	}
	
	public void setnFValue(float value){
		this.nFValue=value;
	}

	public long getnLValue() {
		return nLValue;
	}
	
	public void setnLValue(long value){
		this.nLValue=value;
	}

	public String getsValue() {
		return sValue;
	}
	
	public void setsValue(String value){
		this.sValue=value;
	}
	
	public boolean hasCallback() {
		return hasCallback;
	}
	
	public void setCallback(boolean b) {
		this.hasCallback=b;;
	}
	
	public boolean isRegister() {
		return isRegister;
	}

	public boolean isnBValue() {
		return nBValue;
	}
	
	public void setBValue(boolean v){
		nBValue=v;
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
	
	public boolean isbInitData() {
		return bInitData;
	}

	public void setbInitData(boolean bInitData) {
		this.bInitData = bInitData;
	}
	
	public boolean isOffToOn() {
		return isOffToOn;
	}

	public void setOffToOn(boolean isOffToOn) {
		this.isOffToOn = isOffToOn;
	}

}
