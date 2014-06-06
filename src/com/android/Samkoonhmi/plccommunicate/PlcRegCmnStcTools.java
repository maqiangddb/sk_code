package com.android.Samkoonhmi.plccommunicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.util.Log;

import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AddrPropArray;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.RECEIVE_DATA_STRUCT;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.StrArray;

public class PlcRegCmnStcTools {
	/**
	 * save each set Register data Interface return info, you can call
	 * getLastCmnInfo() to get this info.
	 */
	private static RECEIVE_DATA_STRUCT m_mRcvProp = new RECEIVE_DATA_STRUCT();

	/* plc信息结构体 */
	private static PlcSampInfo mPlcInfo = new PlcSampInfo();

	/**
	 * 一些数据缓存
	 */
	private static Vector<Byte> nTmpByteList = new Vector<Byte>();

	private static Vector<Short> nShortList = new Vector<Short>();
	private static Vector<Integer> nIntList = new Vector<Integer>();
	private static Vector<Long> nLongList = new Vector<Long>();
	private static Vector<Float> nFloatList = new Vector<Float>();

	/**
	 * get last Communicate result.
	 * 
	 * @return
	 */
	public static RECEIVE_DATA_STRUCT getLastCmnInfo() {
		if (null == m_mRcvProp) {
			m_mRcvProp = new RECEIVE_DATA_STRUCT();
		}
		return m_mRcvProp;
	}

	/**
	 * set double list to start mAddrProp. call this function you shout check
	 * mAddrProp.nAddrLen is equal dataList.size(). call this function you must
	 * set mSendProp.eReadWriteCtlType and mSendProp.eDataType; if not check may
	 * make error.
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in eDataType
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegDoubleData(AddrProp mAddrProp,
			Vector<Double> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList
				|| dataList.isEmpty()) {
			Log.e("setRegDoubleData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegDoubleData", "get jni object failed");
			return false;
		}

		/* 开始设置数据 */
		int nDataSize = dataList.size();
		double[] nSourceByteList = new double[nDataSize];
		for (int i = 0; i < nDataSize; i++) {
			nSourceByteList[i] = dataList.get(i);
		}

		byte[] nByteList = mProtocolObj.doublesToBytes(nSourceByteList,
				mSendProp);
		if (null == nByteList)
			return false;

		boolean bSuccess = PlcRegCmnStcTools.setRegBytesData(mAddrProp,
				nByteList, mSendProp);
		return bSuccess;
	}

	/**
	 * set int list to start mAddrProp. int : BIT_1, POSITIVE_INT_16,
	 * INT_16,INT_32, BCD_16, BCD_32. call this function you shout check
	 * mAddrProp.nAddrLen is equal dataList.size(). call this function you must
	 * set mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegIntData(AddrProp mAddrProp,
			Vector<Integer> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList
				|| dataList.isEmpty()) {
			Log.e("setRegIntData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegIntData", "get jni object failed");
			return false;
		}

		/* 开始设置数据 */
		int nDataSize = dataList.size();
		double[] nSourceByteList = new double[nDataSize];
		for (int i = 0; i < nDataSize; i++) {
			nSourceByteList[i] = dataList.get(i);
		}

		byte[] nByteList = mProtocolObj.doublesToBytes(nSourceByteList,
				mSendProp);
		if (null == nByteList)
			return false;

		boolean bSuccess = PlcRegCmnStcTools.setRegBytesData(mAddrProp,
				nByteList, mSendProp);
		return bSuccess;
	}

	/**
	 * set Long list to start mAddrProp. long : POSITIVE_INT_32 HEX_32 OTC_32
	 * call this function you shout check mAddrProp.nAddrLen is equal
	 * dataList.size(). call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType just is
	 * POSITIVE_INT_32;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegLongData(AddrProp mAddrProp,
			Vector<Long> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList
				|| dataList.isEmpty()) {
			Log.e("setRegLongData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegLongData", "get jni object failed");
			return false;
		}
		/* 开始设置数据 */
		int nDataSize = dataList.size();
		double[] nSourceByteList = new double[nDataSize];
		for (int i = 0; i < nDataSize; i++) {
			nSourceByteList[i] = dataList.get(i);
		}

		byte[] nByteList = mProtocolObj.doublesToBytes(nSourceByteList,
				mSendProp);
		if (null == nByteList)
			return false;

		boolean bSuccess = PlcRegCmnStcTools.setRegBytesData(mAddrProp,
				nByteList, mSendProp);
		return bSuccess;
	}

	/**
	 * set float list to start mAddrProp. call this function you shout check
	 * mAddrProp.nAddrLen is equal dataList.size(). call this function you must
	 * set mSendProp.eReadWriteCtlType
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegFloatData(AddrProp mAddrProp,
			Vector<Float> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList
				|| dataList.isEmpty()) {
			Log.e("setRegFloatData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegFloatData", "get jni object failed");
			return false;
		}
		/* 开始设置数据 */
		int nDataSize = dataList.size();
		double[] nSourceByteList = new double[nDataSize];
		for (int i = 0; i < nDataSize; i++) {
			nSourceByteList[i] = dataList.get(i);
		}

		byte[] nByteList = mProtocolObj.doublesToBytes(nSourceByteList,
				mSendProp);
		if (null == nByteList)
			return false;

		boolean bSuccess = PlcRegCmnStcTools.setRegBytesData(mAddrProp,
				nByteList, mSendProp);
		return bSuccess;
	}

	/**
	 * set asccii list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen . call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegAsciiData(AddrProp mAddrProp,
			Vector<Byte> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList
				|| dataList.isEmpty()) {
			Log.e("setRegAsciiData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegAsciiData", "get jni object failed");
			return false;
		}

		/* 开始设置数据 */
		int nDataSize = dataList.size();
		byte[] nSourceByteList = new byte[nDataSize];
		for (int i = 0; i < nDataSize; i++) {
			nSourceByteList[i] = dataList.get(i);
		}

		byte[] nByteList = mProtocolObj.asciiToBytes(nSourceByteList, 2,
				mSendProp.eByteHLPos);
		if (null == nByteList)
			return false;

		boolean bSuccess = PlcRegCmnStcTools.setRegBytesData(mAddrProp,
				nByteList, mSendProp);
		return bSuccess;
	}

	/**
	 * set bytes list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen . call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegBytesData(AddrProp mAddrProp,
			byte[] dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList
				|| dataList.length <= 0) {
			Log.e("setRegAsciiData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取得连接类型 */
		SKCommThread mThreadObj = SKCommThread
				.getComnThreadObj(mAddrProp.eConnectType);
		if (null == mThreadObj) {
			Log.e("setRegAsciiData", "Find the connection type interface:"
					+ mAddrProp.eConnectType
					+ " failed maybe connect type error");
			return false;
		}

		/* 获得PLC属性 */
		boolean bGetInfo = mThreadObj.getPlcSampInfo(mAddrProp, mPlcInfo);
		if (!bGetInfo) {
			Log.e("getPlcSampInfo", "get plc object failed");
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegAsciiData", "get jni object failed");
			return false;
		}

		boolean bSuccess = true;
		PROTOCOL_TYPE eFunType = mProtocolObj.getProtocolType(mPlcInfo);

		/* 先地址整理 */
		if (mThreadObj.m_nConnectObj != null
				&& eFunType == PROTOCOL_TYPE.MASTER_MODEL) {
			int nMaxRWlen = 1;
			int nProtocolSize = mThreadObj.m_nConnectObj.getPlcAttributeList()
					.size();
			for (int i = 0; i < nProtocolSize; i++) {
				if (mThreadObj.m_nConnectObj.getPlcAttributeList().get(i)
						.getnUserPlcId() == mAddrProp.nUserPlcId) {
					nMaxRWlen = mThreadObj.m_nConnectObj.getPlcAttributeList()
							.get(i).getnMaxRWLen();
				}
			}

			AddrPropArray mSortAddrList = new AddrPropArray();
			Vector<AddrProp> mTmpAddrList = new Vector<AddrProp>();
			mTmpAddrList.add(mAddrProp);
			mProtocolObj.sortOutAddrList(mTmpAddrList, mSortAddrList, mPlcInfo,
					nMaxRWlen, true);

			/* 整理后的地址，重新赋值 */
			if (mSortAddrList.mSortAddrList != null) {
				int nAddrSize = mSortAddrList.mSortAddrList.length;
				
				/* 如果长度等于1，则写原来的地址，否则写整理后的地址 */
				if (nAddrSize == 1) {
					bSuccess = mProtocolObj.setRegBytesData(mAddrProp,
							dataList, mPlcInfo, mSendProp.eReadWriteCtlType);
				} else if (nAddrSize > 1) {
					/* 确定地址的长度 */
					int nIndex = 0;

					/* 判断是否为位地址 */
					boolean bBitAddr = true;
					if (mSendProp.eDataType != DATA_TYPE.BIT_1) {
						bBitAddr = false;
					}
					
					for (int i = 0; i < nAddrSize; i++) {
						mSortAddrList.mSortAddrList[i].eConnectType = mAddrProp.eConnectType;
						mSortAddrList.mSortAddrList[i].sPlcProtocol = mAddrProp.sPlcProtocol;

						byte[] nTmpList = null;
						if (bBitAddr) {
							/* 位地址 */
							int nAddrLen = mSortAddrList.mSortAddrList[i].nAddrLen;
							nTmpList = new byte[nAddrLen];
							for (int k = 0; k < nAddrLen; k++) {
								if (nIndex < dataList.length) {
									nTmpList[k] = dataList[nIndex];
									nIndex++;
								}
							}
						} else {
							/* 字地址 */
							int nAddrLen = mSortAddrList.mSortAddrList[i].nAddrLen;
							nAddrLen = nAddrLen * 2;
							nTmpList = new byte[nAddrLen];
							for (int k = 0; k < nAddrLen; k++) {
								if (nIndex < dataList.length) {
									nTmpList[k] = dataList[nIndex];
									nIndex++;
								}
							}
						}

						bSuccess &= mProtocolObj.setRegBytesData(
								mSortAddrList.mSortAddrList[i], nTmpList,
								mPlcInfo, mSendProp.eReadWriteCtlType);
					}
				}
			} else {
				bSuccess = false;
			}
		} else {
			bSuccess = mProtocolObj.setRegBytesData(mAddrProp, dataList,
					mPlcInfo, mSendProp.eReadWriteCtlType);
		}

		/* 如果不是主站，则立即通知更新 */
		if (eFunType != PROTOCOL_TYPE.MASTER_MODEL) {
			// /*发送消息更新*/
			// PlcNoticValue nTmpNoticValue = new PlcNoticValue();
			// nTmpNoticValue.mAddrProp = mAddrProp;
			// nTmpNoticValue.nValueList = dataList;
			// SKPlcNoticThread.getInstance().getCmnNoticHandler().obtainMessage(MODULE.PLC_NOTIC_VALUE_CHANGE,
			// nTmpNoticValue).sendToTarget();

			/* 发消息更新内部地址和slave地址 */
			SKPlcNoticThread.getInstance().getCmnNoticHandler().removeMessages(MODULE.LOCAL_ADDR_REFREASH);
			SKPlcNoticThread.getInstance().getCmnNoticHandler()
					.sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
		} else {
			mThreadObj.getCmnRefreashHandler().removeMessages(MODULE.SYSTEM_MAST_WRITE);
			mThreadObj.getCmnRefreashHandler().sendEmptyMessage(
					MODULE.SYSTEM_MAST_WRITE);
		}
		return bSuccess;
	}

	/**
	 * get double list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType; if not check may
	 * make error.
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in eDataType
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean getRegDoubleData(AddrProp mAddrProp,
			Vector<Double> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList) {
			Log.e("getRegDoubleData",
					"Address object or send structure object does not exist");
			return false;
		}

		dataList.clear();
		switch (mSendProp.eDataType) {
		case BIT_1: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegDoubleData", "address value：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			int len = nTmpByteList.size();
			for (int i = 0; i < len; i++) {
				dataList.add((double) nTmpByteList.get(i));
			}
			return true;
		}
		case INT_16: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegDoubleData", "address value：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToShorts(nTmpByteList, nShortList);
			if (bSuccess && null != nShortList) {
				int len = nShortList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((double) nShortList.get(i));
				}
			}
			return true;
		}
		case POSITIVE_INT_16:
		case BCD_16:
		case HEX_16:
		case OTC_16: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegDoubleData", "address value：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToUShorts(nTmpByteList, nIntList);
			if (bSuccess && null != nIntList) {
				int len = nIntList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((double) nIntList.get(i));
				}
			}
			return true;
		}
		case INT_32:
		case BCD_32: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegDoubleData", "addr value is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToInts(nTmpByteList, nIntList);
			if (bSuccess && null != nIntList) {
				int len = nIntList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((double) nIntList.get(i));
				}
			}
			return true;
		}
		case POSITIVE_INT_32:
		case HEX_32:
		case OTC_32: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegDoubleData", "address value is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToUInts(nTmpByteList, nLongList);
			if (bSuccess && null != nLongList) {
				int len = nLongList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((double) nLongList.get(i));
				}
			}
			return true;
		}
		case FLOAT_32: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegDoubleData", "address value is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToFloats(nTmpByteList, nFloatList);
			if (bSuccess && null != nFloatList) {
				int len = nFloatList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((double) nFloatList.get(i));
				}
			}
			return true;
		}
		case ASCII_STRING:
		default: {
			break;
		}
		}
		Log.e("getRegDoubleData", "address is：sPlcProtocol:"
				+ mAddrProp.sPlcProtocol + ", nRegIndex:" + mAddrProp.nRegIndex
				+ ", nAddrValue:" + mAddrProp.nAddrValue + " data type error");
		return false;
	}

	/**
	 * get int list to start mAddrProp. int : BIT_1, POSITIVE_INT_16,
	 * INT_16,INT_32, BCD_16, BCD_32. call this function you must set
	 * mAddrProp.nAddrLen call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean getRegIntData(AddrProp mAddrProp,
			Vector<Integer> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList) {
			Log.e("getRegIntData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 开始读取数据 */
		dataList.clear();
		switch (mSendProp.eDataType) {
		case BIT_1: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegIntData", "address is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			int len = nTmpByteList.size();
			for (int i = 0; i < len; i++) {
				dataList.add((int) nTmpByteList.get(i));
			}
			return true;
		}
		case INT_16: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegIntData", "address is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToShorts(nTmpByteList, nShortList);
			if (bSuccess && null != nShortList) {
				int len = nShortList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((int) nShortList.get(i));
				}
			}
			return true;
		}
		case POSITIVE_INT_16:
		case BCD_16:
		case HEX_16:
		case OTC_16: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegIntData", "address is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToUShorts(nTmpByteList, nIntList);
			if (bSuccess && null != nIntList) {
				int len = nIntList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((int) nIntList.get(i));
				}
			}
			return true;
		}
		case INT_32:
		case BCD_32: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegIntData", "address is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToInts(nTmpByteList, nIntList);
			if (bSuccess && null != nIntList) {
				int len = nIntList.size();
				for (int i = 0; i < len; i++) {
					dataList.add((int) nIntList.get(i));
				}
			}
			return true;
		}
		case ASCII_STRING:
		default: {
			break;
		}
		}

		Log.e("getRegIntData", "address is：sPlcProtocol:"
				+ mAddrProp.sPlcProtocol + ", nRegIndex:" + mAddrProp.nRegIndex
				+ ", nAddrValue:" + mAddrProp.nAddrValue + " data type error");
		return false;
	}

	/**
	 * get Long list to start mAddrProp. long : POSITIVE_INT_32 call this
	 * function you must set mAddrProp.nAddrLen call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType just is
	 * POSITIVE_INT_32;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean getRegLongData(AddrProp mAddrProp,
			Vector<Long> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList) {
			Log.e("getRegLongData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取数值 */
		dataList.clear();
		switch (mSendProp.eDataType) {
		case POSITIVE_INT_32:
		case HEX_32:
		case OTC_32:
		case BCD_32: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegLongData", "address is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data from plc failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToUInts(nTmpByteList, nLongList);
			if (bSuccess && null != nLongList) {
				int len = nLongList.size();
				for (int i = 0; i < len; i++) {
					dataList.add(nLongList.get(i));
				}
			}
			return true;
		}
		default: {
			break;
		}
		}

		Log.e("getRegLongData", "address is：sPlcProtocol:"
				+ mAddrProp.sPlcProtocol + ", nRegIndex:" + mAddrProp.nRegIndex
				+ ", nAddrValue:" + mAddrProp.nAddrValue + " data type error");
		return false;
	}

	/**
	 * get float list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen call this function you must set
	 * mSendProp.eReadWriteCtlType
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean getRegFloatData(AddrProp mAddrProp,
			Vector<Float> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList) {
			Log.e("getRegFloatData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 开始读取数据 */
		dataList.clear();
		switch (mSendProp.eDataType) {
		case FLOAT_32: {
			/* 取得数值 */
			boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
					nTmpByteList, mSendProp);
			if (false == bSuccess) {
				Log.e("getRegFloatData", "address is：sPlcProtocol:"
						+ mAddrProp.sPlcProtocol + ", nRegIndex:"
						+ mAddrProp.nRegIndex + ", nAddrValue:"
						+ mAddrProp.nAddrValue + " read data from plc failed");
				return false;
			}

			/* 转换值 */
			bSuccess = bytesToFloats(nTmpByteList, nFloatList);
			if (bSuccess && null != nFloatList) {
				int len = nFloatList.size();
				for (int i = 0; i < len; i++) {
					dataList.add(nFloatList.get(i));
				}
			}
			return true;
		}
		default: {
			break;
		}
		}

		Log.e("getRegFloatData", "address is：sPlcProtocol:"
				+ mAddrProp.sPlcProtocol + ", nRegIndex:" + mAddrProp.nRegIndex
				+ ", nAddrValue:" + mAddrProp.nAddrValue + " data type error");
		return false;
	}

	/**
	 * get asccii list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen . call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean getRegAsciiData(AddrProp mAddrProp,
			Vector<Byte> dataList, SEND_DATA_STRUCT mSendProp, boolean bUnicode) {
		if (mSendProp.eDataType != DATA_TYPE.ASCII_STRING) {
			Log.e("getRegAsciiData", "data type is not ASCII_STRING");
			return false;
		}

		/* 取得数值 */
		boolean bSuccess = PlcRegCmnStcTools.getRegBytesData(mAddrProp,
				dataList, mSendProp);
		return bSuccess;
	}

	/**
	 * get local string list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen . call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean getRegLocalStr(AddrProp mAddrProp,
			Vector<String> dataList) {
		if (null == mAddrProp) {
			Log.e("getRegLocalStr", "addr object is null");
			return false;
		}

		if (null == dataList) {
			Log.e("getRegLocalStr", "Data storage container is not initialized");
			return false;
		}

		/* 取得连接类型 */
		if (mAddrProp.eConnectType != CONNECT_TYPE.LOCAL) {
			Log.e("getRegLocalStr",
					"Is not an internal address can not call for String Functions");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("getRegLocalStr", "get jni object failed");
			return false;
		}

		/* 初始化PLC信息结构体 */
		mPlcInfo.eConnectType = mAddrProp.eConnectType;
		mPlcInfo.nProtocolIndex = 0;
		mPlcInfo.nSampRate = 200;
		mPlcInfo.sProtocolName = mAddrProp.sPlcProtocol;

		/* 取得数值 */
		StrArray setValues = new StrArray();
		boolean bSuccess = mProtocolObj.getLocalStr(mAddrProp, setValues,
				mPlcInfo);

		if (null == setValues || null == setValues.sStrList
				|| setValues.sStrList.length <= 0) {
			Log.e("getRegLocalStr",
					"Access to the internal address string failed");
			return false;
		}

		/* 添加字符串到容器 */
		dataList.clear();
		int len = setValues.sStrList.length;
		for (int i = 0; i < len; i++) {
			dataList.add(setValues.sStrList[i]);
		}

		return bSuccess;
	}

	/**
	 * set local string list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen . call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	public synchronized static boolean setRegLocalStr(AddrProp mAddrProp,
			Vector<String> dataList) {
		if (null == mAddrProp) {
			Log.e("setRegLocalStr", "address object is null");
			return false;
		}

		if (null == dataList) {
			Log.e("setRegLocalStr", "Data storage container is not initialized");
			return false;
		}

		/* 取得连接类型 */
		if (mAddrProp.eConnectType != CONNECT_TYPE.LOCAL) {
			Log.e("setRegLocalStr",
					"Is not an internal address can not call for String Functions");
			return false;
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("setRegLocalStr", "get jni object failed");
			return false;
		}

		/* 初始化PLC信息结构体 */
		mPlcInfo.eConnectType = mAddrProp.eConnectType;
		mPlcInfo.nProtocolIndex = 0;
		mPlcInfo.nSampRate = 200;
		mPlcInfo.sProtocolName = mAddrProp.sPlcProtocol;

		int nSize = dataList.size();
		String[] sSetStrArray = new String[nSize];
		for (int i = 0; i < nSize; i++) {
			sSetStrArray[i] = dataList.get(i);
		}

		/* 取得数值 */
		boolean bSuccess = mProtocolObj.setLocalStr(mAddrProp, sSetStrArray,
				nSize, mPlcInfo);

		return bSuccess;
	}

	/**
	 * get asccii list to start mAddrProp. call this function you must set
	 * mAddrProp.nAddrLen . call this function you must set
	 * mSendProp.eReadWriteCtlType and mSendProp.eDataType;
	 * 
	 * @param @in mAddrProp
	 * @param @in dataList
	 * @param @in mSendProp
	 * @return
	 */
	private static RECEIVE_DATA_STRUCT mData_STRUCT = new RECEIVE_DATA_STRUCT();

	public synchronized static boolean getRegBytesData(AddrProp mAddrProp,
			Vector<Byte> dataList, SEND_DATA_STRUCT mSendProp) {
		if (null == mAddrProp || null == mSendProp || null == dataList) {
			Log.e("getRegBytesData",
					"Address object or send structure object does not exist");
			return false;
		}

		/* 取得连接类型 */
		SKCommThread mThreadObj = SKCommThread
				.getComnThreadObj(mAddrProp.eConnectType);
		if (null == mThreadObj) {
			Log.e("getRegBytesData", "Find the connection type interface:"
					+ mAddrProp.eConnectType
					+ " failed, maybe connect type error");
			return false;
		}

		/* 获得PLC属性 */
		boolean bGetInfo = mThreadObj.getPlcSampInfo(mAddrProp, mPlcInfo);
		if (!bGetInfo) {
			Log.e("getPlcSampInfo", "get sPlcProtocol:"
					+ mAddrProp.sPlcProtocol + " PLC info failed");
		}

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("getRegBytesData", "get jni object failed");
			return false;
		}

		/* 开始读取数据 */
		int nAddrLen = mAddrProp.nAddrLen;
		int nGetSize = 0;
		dataList.clear();
		switch (mSendProp.eDataType) {
		case BIT_1: {
			nGetSize = nAddrLen;
			break;
		}
		default: {
			nGetSize = nAddrLen * 2;
			break;
		}
		}
		if (nGetSize == 0) {
			Log.e("getRegBytesData", "start addr value is: sPlcProtocol:"
					+ mAddrProp.sPlcProtocol + ", nRegIndex:"
					+ mAddrProp.nRegIndex + ", nAddrValue:"
					+ mAddrProp.nAddrValue + " addr len = 0");
			return false;
		}

		mData_STRUCT.nErrorCode = m_mRcvProp.nErrorCode;

		/* 取得数值 */
		byte[] nGetBytes = new byte[nGetSize];
		boolean bSuccess = mProtocolObj.getRegBytesData(mAddrProp, nGetBytes,
				mPlcInfo, mData_STRUCT);
		if (false == bSuccess || null == nGetBytes) {
			Log.e("getRegBytesData", "start addr value is: sPlcProtocol:"
					+ mAddrProp.sPlcProtocol + ", nRegIndex:"
					+ mAddrProp.nRegIndex + ", nAddrValue:"
					+ mAddrProp.nAddrValue + " read data from plc failed");
			return false;
		}

		/* 取得长度 */
		for (int i = 0; i < nGetSize; i++) {
			dataList.add(nGetBytes[i]);
		}
		return true;
	}

	/**
	 * 设置一个画面的所有地址
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static boolean setOneSceneReadAddr(
			Vector<AddrProp> mAddrList, boolean bCover) {
		/* 停止通信线程 */
		SKCommThread.stopAllThread(false);

		if (null == mAddrList || mAddrList.isEmpty()) {
			// Log.e("setOneSceneReadAddr",
			// "Address object or address is empty");
			if (ProtocolInterfaces.getProtocolInterface() != null) {
				ProtocolInterfaces.getProtocolInterface().clearAllReadAddr();
			}
			return false;
		}

		int nAddrSize = mAddrList.size();
		HashMap<Short, Vector<AddrProp>> eConnectTypeMap = new HashMap<Short, Vector<AddrProp>>();

		Vector<AddrProp> tmpAddrList = null;
		AddrProp mTmpAddr = null;

		/* 按照连接类型把所有地址分类添加到eConnectTypeMap容器中 */
		for (int i =  0; i < nAddrSize; i++) {
			mTmpAddr = mAddrList.get(i);
			if (null == mTmpAddr)
				continue;
			if (mTmpAddr.eConnectType == 1)
				continue;

			/* 判断通信是否已经创建 */
			if (eConnectTypeMap.containsKey(mTmpAddr.nUserPlcId)) {
				tmpAddrList = eConnectTypeMap.get(mTmpAddr.nUserPlcId);
				if (null == tmpAddrList)
					continue;

				tmpAddrList.add(mTmpAddr);
				eConnectTypeMap.put(mTmpAddr.nUserPlcId, tmpAddrList);
			} else {
				tmpAddrList = new Vector<AddrProp>();
				tmpAddrList.clear();
				tmpAddrList.add(mTmpAddr);
				eConnectTypeMap.put(mTmpAddr.nUserPlcId, tmpAddrList);
			}
		}

		/* 在eConnectTypeMap容器中，按照连接类型取得协议 */
		Set<Short> connectList = eConnectTypeMap.keySet();
		for (Iterator<Short> it = connectList.iterator(); it.hasNext();) {
			short nUserPlcId = it.next();
			if (eConnectTypeMap.get(nUserPlcId).isEmpty())
				continue;

			short eConnect = eConnectTypeMap.get(nUserPlcId).get(0).eConnectType;

			/* 取得连接类型 */
			SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnect);
			if (null == mThreadObj) {
				Log.e("setOneSceneReadAddr",
						"Find the connection type interface:" + eConnect
								+ " failed，The type of connection may be wrong");
				continue;
			}

			ProtocolInterfaces mProtocolObj = ProtocolInterfaces
					.getProtocolInterface();
			if (mProtocolObj != null) {
				mPlcInfo.eConnectType = eConnect;
				mPlcInfo.nProtocolIndex = nUserPlcId;
				// mPlcInfo.nSampRate = 200;
				mPlcInfo.sProtocolName = eConnectTypeMap.get(nUserPlcId).get(0).sPlcProtocol;
						
				//获取最大读取长度begin
				PlcConnectionInfo mConnect = null;
				int nMaxRWlen = -1;
				if(!bCover)
				{
					for (int i = 0; i < SystemInfo.getPlcConnectionList().size(); i++) {
						if (SystemInfo.getPlcConnectionList().get(i)
								.geteConnectPort() == mPlcInfo.eConnectType) {
							mConnect = SystemInfo.getPlcConnectionList().get(i);
							break;
						}
					}

					if (mConnect != null) {
						int nProtocolSize = mConnect.getPlcAttributeList()
								.size();
						for (int i = 0; i < nProtocolSize; i++) {
							if (mConnect.getPlcAttributeList().get(i)
									.getnUserPlcId() == mTmpAddr.nUserPlcId) {
								nMaxRWlen = mConnect.getPlcAttributeList()
										.get(i).getnMaxRWLen();
								break;
							}
						}
					}
				}
					//获取最大读取长度end
				mProtocolObj.setOneSceneAddrs(eConnectTypeMap.get(nUserPlcId),
						bCover, mPlcInfo, nMaxRWlen);
			}
				
			/* 开启线程 */
			mThreadObj.start(true);

		}// 按连接取地址结束

		return true;
	}

	/**
	 * 存放在bytes类型中的字节流转换成short
	 * 
	 * @param nSourceList
	 *            : 要转换的byte类型的容器
	 * @param nResultList
	 *            : 转换后的Short类型的容器
	 * @return
	 */
	public synchronized static boolean bytesToShorts(Vector<Byte> nSourceList,
			Vector<Short> nResultList) {
		/* 不为空，则清除老数据 */
		if (null == nSourceList || null == nResultList) {
			return false;
		}
		nResultList.clear();

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("bytesToShorts", "get jni object failed");
			return false;
		}

		boolean bSuccess = mProtocolObj.bytesToShorts(nSourceList, nResultList);
		return bSuccess;
	}

	/**
	 * 存放在bytes类型中的字节流转换成unsigned short
	 * 
	 * @param nSourceList
	 *            : 要转换的byte类型的容器
	 * @param nResultList
	 *            : 转换后的Integer类型的容器
	 * @return
	 */
	public synchronized static boolean bytesToUShorts(Vector<Byte> nSourceList,
			Vector<Integer> nResultList) {
		/* 不为空，则清除老数据 */
		if (null == nSourceList || null == nResultList) {
			return false;
		}
		nResultList.clear();

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("bytesToUShorts", "get jni object failed");
			return false;
		}

		boolean bSuccess = mProtocolObj
				.bytesToUShorts(nSourceList, nResultList);
		return bSuccess;
	}

	/**
	 * 存放在bytes类型中的字节流转换成int
	 * 
	 * @param nSourceList
	 *            : 要转换的byte类型的容器
	 * @param nResultList
	 *            : 转换后的Integer类型的容器
	 * @return
	 */
	public synchronized static boolean bytesToInts(Vector<Byte> nSourceList,
			Vector<Integer> nResultList) {
		/* 不为空，则清除老数据 */
		if (null == nSourceList || null == nResultList) {
			return false;
		}
		nResultList.clear();

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("bytesToInts", "get jni object failed");
			return false;
		}

		boolean bSuccess = mProtocolObj.bytesToInts(nSourceList, nResultList);
		return bSuccess;
	}

	/**
	 * 存放在bytes类型中的字节流转换成unsigned int
	 * 
	 * @param nSourceList
	 *            : 要转换的byte类型的容器
	 * @param nResultList
	 *            : 转换后的Long 类型的容器
	 * @return
	 */
	public synchronized static boolean bytesToUInts(Vector<Byte> nSourceList,
			Vector<Long> nResultList) {
		/* 不为空，则清除老数据 */
		if (null == nSourceList || null == nResultList) {
			return false;
		}
		nResultList.clear();

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("bytesToUInts", "get jni object failed");
			return false;
		}

		boolean bSuccess = mProtocolObj.bytesToUInts(nSourceList, nResultList);
		return bSuccess;
	}

	/**
	 * 存放在bytes类型中的字节流转换成float
	 * 
	 * @param nSourceList
	 *            : 要转换的byte类型的容器
	 * @param nResultList
	 *            : 转换后的Float 类型的容器
	 * @return
	 */
	public synchronized static boolean bytesToFloats(Vector<Byte> nSourceList,
			Vector<Float> nResultList) {
		/* 不为空，则清除老数据 */
		if (null == nSourceList || null == nResultList) {
			return false;
		}
		nResultList.clear();

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("bytesToFloats", "get jni object failed");
			return false;
		}

		boolean bSuccess = mProtocolObj.bytesToFloats(nSourceList, nResultList);
		return bSuccess;
	}

	/**
	 * 存放在int类型中的字节流转换成short
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static short intToShort(int nSourceData) {
		short nTarget = 0;

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("intToShort", "get jni object failed");
			return nTarget;
		}

		nTarget = mProtocolObj.intToShort(nSourceData);
		return nTarget;
	}

	/**
	 * 存放在int类型中的字节流转换成float
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static float intToFloat(int nSourceData) {
		float nTarget = 0;

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("intToFloat", "get jni object failed");
			return nTarget;
		}

		nTarget = mProtocolObj.intToFloat(nSourceData);
		return nTarget;
	}

	/**
	 * 存放在int类型中的字节流转换成unsigned short
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static int intToUShort(int nSourceData) {
		int nTarget = 0;

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("intToUShort", "get jni object failed");
			return nTarget;
		}

		nTarget = mProtocolObj.intToUShort(nSourceData);
		return nTarget;
	}

	/**
	 * 存放在int类型中的字节流转换成unsigned int
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static long intToUInt(int nSourceData) {
		long nTarget = 0;

		/* 取jni接口对象 */
		ProtocolInterfaces mProtocolObj = ProtocolInterfaces
				.getProtocolInterface();
		if (null == mProtocolObj) {
			Log.e("intToUInt", "get jni object failed");
			return nTarget;
		}

		nTarget = mProtocolObj.intToUInt(nSourceData);
		return nTarget;
	}

	/**
	 * 存放在int类型中的字节流转换成bit：1或0
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static int intToBit(int nSourceData) {
		int nTarget = 0;

		if (nSourceData > 0) {
			nTarget = 1;
		}
		return nTarget;
	}

	/**
	 * 存放在int类型中的字节流转换成BCD码
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static int intToBCD_16(int nSourceData) {
		int nTarget = 0;
		int nBcdValue = 0;
		for (int i = 0; i < 4; i++) {
			nBcdValue = (nSourceData >> i * 4) % 16;
			if (nBcdValue >= 10) {
				return 0;
			}

			nTarget += nBcdValue * Math.pow(10, i);
		}
		return nTarget;
	}

	/**
	 * 存放在int类型中的字节流转换成BCD码
	 * 
	 * @param nSourceData
	 * @return
	 */
	public synchronized static int intToBCD_32(int nSourceData) {
		int nTarget = 0;
		int nBcdValue = 0;
		for (int i = 0; i < 8; i++) {
			nBcdValue = (nSourceData >> i * 4) % 16;
			if (nBcdValue >= 10) {
				return 0;
			}

			nTarget += nBcdValue * Math.pow(10, i);
		}
		return nTarget;
	}
}
