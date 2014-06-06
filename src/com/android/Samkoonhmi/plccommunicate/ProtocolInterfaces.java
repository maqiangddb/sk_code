package com.android.Samkoonhmi.plccommunicate;

import java.io.File;
import java.util.Vector;

import android.widget.Toast;

import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AddrPropArray;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcNoticValue;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.RECEIVE_DATA_STRUCT;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT_JNI;
import com.android.Samkoonhmi.util.SEND_PACKAGE_JNI;
import com.android.Samkoonhmi.util.SendPkgArray;
import com.android.Samkoonhmi.util.StrArray;

public class ProtocolInterfaces {
	
	/**
	 * 一些数据缓存
	 */
	private byte[] nBToSArray = new byte[64];
	private byte[] nBToUSArray = new byte[64];
	private byte[] nBToIArray = new byte[64];
	private byte[] nBToLArray = new byte[64];
	private byte[] nBToFArray = new byte[64];
	
	private short[] nShortArray = new short[64];
	private int[] nUShortArray = new int[64];
	private int[] nIntArray = new int[64];
	private long[] nLongArray = new long[64];
	private float[] nFloatArray = new float[64];
	
	private byte[] nGetRegByteArray = new byte[64];
	
	/*jni 一部分的接口*/
	
	/*取得要写PLC的数据*/
	private native boolean getWriteDataJni(SendPkgArray mPkgListObj, int eCmnType, PlcSampInfo mPlcInfo);
	
	/*取得要读PLC的数据*/
	private native boolean getReadDataJni(SendPkgArray mPkgListObj, int eCmnType, PlcSampInfo mPlcInfo);
	
	/*获得握手协议数据*/
	private native boolean getHandshakePkgJni(SEND_PACKAGE_JNI mPkgListObj, PlcSampInfo mPlcInfo, int nPkgIndex, int nStationId, int nBaudRate);
	
	/*获得当前通信地址*/
	private native boolean getCurrAddrJni(PlcSampInfo mPlcInfo, AddrProp mCurrAddr);
	
	/*从PLC读得数据，拿去协议层校验和保存*/
	private native int setReadDataJni(byte[] sSendData, int nSetSize, int nReturnLen, int nCurrTimes, int eSendType, PlcSampInfo mPlcInfo, PlcNoticValue nNoticValue);
	
	/*校验写数据是否成功*/
	private native int checkWriteDataJni(byte[] sSendData, int nSetSize, int nReturnLen, int nCurrTimes, PlcSampInfo mPlcInfo, PlcNoticValue nNoticValue);
	
	/*校验握手协议校验是否成功*/
	private native int checkHandshakePkgJni(byte[] sSendData, int nSetSize, int nReturnLen, int nCurrTimes, PlcSampInfo mPlcInfo);
	
	/*接收从串口接收到的数据到从站处理，只适合从站*/
	private native int rcvStrForSlaveJni(byte[] sRcvStr, int nSetSize, int nStationId, SendPkgArray sSendDataStr, PlcSampInfo mPlcInfo);
	
	/*取得协议的功能类型，做主站还是做从站*/
	private native int getProtocolFunTypeJni(PlcSampInfo mPlcInfo);
	
	/*关闭协议*/
	private native boolean closeProtocolJni(PlcSampInfo mPlcInfo);
	private native boolean closeAllProtocolJni();
	
	/*设置通信标志*/
	private native boolean setCmnInfoJni(int nCmnInfo, PlcSampInfo mPlcInfo);
	
	/*设置一个场景和窗口的地址*/
	private native boolean setOneSceneAddrsJni(AddrProp[] mAddrList, boolean bCover, PlcSampInfo mPlcInfo,int nMaxRWlen);
	
	private native boolean clearAllReadAddrJni();
	
	/*对对地址进行整理*/
	private native boolean sortOutAddrListJni(AddrProp[] mAddrList, AddrPropArray mSortAddrList, PlcSampInfo mPlcInfo, int nMaxRWlen, boolean bWriteAddr);
	
	/*对写地址进行打包*/
	private native boolean makeWritePackageJni(AddrProp mAddrProp, byte[] dataList, int nSetSize, SendPkgArray mPkgListObj, int eCmnType, PlcSampInfo mPlcInfo);
	
	/*对写地址进行打包*/
	private native boolean makeReadPackageJni(AddrProp mAddrProp, SendPkgArray mPkgListObj, int eCmnType, PlcSampInfo mPlcInfo);
	
	/*读取、设置和转换数据部分的接口*/
	private SEND_DATA_STRUCT_JNI m_tmpSendStruct = new SEND_DATA_STRUCT_JNI();
//	private native boolean setRegDoubleDataJni(AddrProp mAddrProp, double[] dataList, int nSetSize, SEND_DATA_STRUCT_JNI mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo);
//	private native boolean setRegIntDataJni(AddrProp mAddrProp, int[] dataList, int nSetSize, SEND_DATA_STRUCT_JNI mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo);
//	private native boolean setRegLongDataJni(AddrProp mAddrProp, long[] dataList, int nSetSize, SEND_DATA_STRUCT_JNI mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo);
//	private native boolean setRegFloatDataJni(AddrProp mAddrProp, float[] dataList, int nSetSize, SEND_DATA_STRUCT_JNI mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo);
//	private native boolean setRegAsciiDataJni(AddrProp mAddrProp, byte[] dataList, int nSetSize, SEND_DATA_STRUCT_JNI mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo);

	private native boolean setRegBytesDataJni(AddrProp mAddrProp, byte[] dataList, PlcSampInfo mPlcInfo, int nRwType, int nSimulateType);
	private native boolean getRegBytesDataJni(AddrProp mAddrProp, byte[] dataList, PlcSampInfo mPlcInfo, RECEIVE_DATA_STRUCT mRcvProp);
	private native boolean getLocalStrJni(AddrProp mAddrProp, StrArray dataList, PlcSampInfo mPlcInfo);
	private native boolean setLocalStrJni(AddrProp mAddrProp, String[] dataList, int nSetSize, PlcSampInfo mPlcInfo);
	
	
	private native boolean bytesToShortsJni(byte[] nSourceData, int nSourceSize, short[] nTargeData);
	private native boolean bytesToUShortsJni(byte[] nSourceData, int nSourceSize,int[] nTargeData);
	private native boolean bytesToIntsJni(byte[] nSourceData, int nSourceSize, int[] nTargeData);
	private native boolean bytesToUIntsJni(byte[] nSourceData, int nSourceSize,long[] nTargeData);
	private native boolean bytesToFloatsJni(byte[] nSourceData, int nSourceSize,float[] nTargeData);
	
	private native boolean asciiToBytesJni(byte[] nSourceData, int nInterval, int nByteHLPos);
	private native byte[] doublesToBytesJni(double[] nSourceData, SEND_DATA_STRUCT_JNI mSendProp);
	
	private native short intToShortJni(int nSourceData);
	private native float intToFloatJni(int nSourceData);
	private native int intToUShortJni(int nSourceData);
	private native long intToUIntJni(int nSourceData);

	
	/**
	 * 获得驱动接口
	 */
	private static ProtocolInterfaces mProtocolObj = null;
	public synchronized static ProtocolInterfaces getProtocolInterface() {
		if(null == mProtocolObj)
		{
			String sSumsungFile = "/data/sumsung.phone";
			File samsungfile = new File(sSumsungFile);
			if(samsungfile.exists()){
				File mCollectFile = new File(SystemVariable.sSumsungLibPath+"libplc_drives_center.so");
				if(!mCollectFile.exists())
				{
					SKToast.makeText("plc_drives_center file not exists", Toast.LENGTH_SHORT).show();
					return null;
				}
			}else{
			
				File mCollectFile = new File("/data/data/com.android.Samkoonhmi/lib/libplc_drives_center.so");
				if(!mCollectFile.exists())
				{
					SKToast.makeText("plc_drives_center file not exists", Toast.LENGTH_SHORT).show();
					return null;
				}
			}
			mProtocolObj = new ProtocolInterfaces();
		}
		return mProtocolObj;
	}
	
//	/**
//	 * 设置double类型的值到PLC
//	 * @param mAddrProp
//	 * @param dataList
//	 * @param mSendProp
//	 * @param mRcvProp
//	 * @param mPlcInfo
//	 * @return
//	 */
//	public synchronized boolean setRegDoubleData(AddrProp mAddrProp, double[] dataList, int nSetSize, SEND_DATA_STRUCT mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo)
//	{
//		boolean bSuccess = false;
//		bSuccess = setRegDoubleDataJni(mAddrProp, dataList, nSetSize, getSendStructJni(mSendProp), mRcvProp, mPlcInfo);
//		
//		return bSuccess;
//	}
//	
//	/**
//	 * 设置int类型的值到PLC
//	 * @param mAddrProp
//	 * @param dataList
//	 * @param mSendProp
//	 * @param mRcvProp
//	 * @param mPlcInfo
//	 * @return
//	 */
//	public synchronized boolean setRegIntData(AddrProp mAddrProp, int[] dataList, int nSetSize, SEND_DATA_STRUCT mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo)
//	{
//		boolean bSuccess = false;
//		bSuccess = setRegIntDataJni(mAddrProp, dataList, nSetSize, getSendStructJni(mSendProp), mRcvProp, mPlcInfo);
//
//		return bSuccess;
//	}
//	
//	/**
//	 * 设置unsigned int类型的值到PLC
//	 * @param mAddrProp
//	 * @param dataList
//	 * @param mSendProp
//	 * @param mRcvProp
//	 * @param mPlcInfo
//	 * @return
//	 */
//	public synchronized boolean setRegLongData(AddrProp mAddrProp, long[] dataList, int nSetSize, SEND_DATA_STRUCT mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo)
//	{
//		boolean bSuccess = false;
//		bSuccess = setRegLongDataJni(mAddrProp, dataList, nSetSize, getSendStructJni(mSendProp), mRcvProp, mPlcInfo);
//		
//		return bSuccess;
//	}
//	
//	/**
//	 * 设置float类型的值到PLC
//	 * @param mAddrProp
//	 * @param dataList
//	 * @param mSendProp
//	 * @param mRcvProp
//	 * @param mPlcInfo
//	 * @return
//	 */
//	public synchronized boolean setRegFloatData(AddrProp mAddrProp, float[] dataList, int nSetSize, SEND_DATA_STRUCT mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo)
//	{
//		boolean bSuccess = false;
//		bSuccess = setRegFloatDataJni(mAddrProp, dataList, nSetSize, getSendStructJni(mSendProp), mRcvProp, mPlcInfo);
//		
//		return bSuccess;
//	}
//	
//	/**
//	 * 设置ascii类型的值到PLC
//	 * @param mAddrProp
//	 * @param dataList
//	 * @param mSendProp
//	 * @param mRcvProp
//	 * @param mPlcInfo
//	 * @return
//	 */
//	public synchronized boolean setRegAsciiData(AddrProp mAddrProp, byte[] dataList, int nSetSize, SEND_DATA_STRUCT mSendProp, RECEIVE_DATA_STRUCT mRcvProp, PlcSampInfo mPlcInfo)
//	{
//		boolean bSuccess = false;
//		bSuccess = setRegAsciiDataJni(mAddrProp, dataList, nSetSize, getSendStructJni(mSendProp), mRcvProp, mPlcInfo);
//		
//		return bSuccess;
//	}

	/**
	 * 从地址取字节流
	 * @param mAddrProp
	 * @param dataList
	 * @param mSendProp
	 * @return
	 */
	public boolean getRegBytesData(AddrProp mAddrProp, byte[] dataList, PlcSampInfo mPlcInfo, RECEIVE_DATA_STRUCT mRcvProp)
	{
		if(null == dataList) return false;
		
		/*容器不够，从新分配*/
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = getRegBytesDataJni(mAddrProp, dataList, mPlcInfo, mRcvProp);
		}
		return bSuccess;
	}
	
	/**
	 * 设置byte数组到地址
	 * @param mAddrProp
	 * @param dataList
	 * @param mPlcInfo
	 * @param nRwType
	 * @return
	 */
	public boolean setRegBytesData(AddrProp mAddrProp, byte[] dataList, PlcSampInfo mPlcInfo, int nRwType)
	{
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = setRegBytesDataJni(mAddrProp, dataList, mPlcInfo, nRwType, SystemInfo.getbSimulator());
			if(SystemInfo.getbSimulator() == 1)
			{
				SKPlcNoticThread.getInstance().getCmnNoticHandler().sendEmptyMessage(MODULE.LOCAL_ADDR_REFREASH);
			}
		}
		return bSuccess;
	}
	
	/**
	 * 获取内部地址中字符串的接口
	 * @param mAddrProp
	 * @param dataList
	 * @param mPlcInfo
	 * @return
	 */
	public boolean getLocalStr(AddrProp mAddrProp, StrArray dataList, PlcSampInfo mPlcInfo)
	{
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = getLocalStrJni(mAddrProp, dataList, mPlcInfo);
		}
		return bSuccess;
	}
	
	/**
	 * 设置内部地址中字符串的接口
	 * @param mAddrProp
	 * @param dataList
	 * @param mPlcInfo
	 * @return
	 */
	public boolean setLocalStr(AddrProp mAddrProp, String[] dataList, int nSetSize, PlcSampInfo mPlcInfo)
	{
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = setLocalStrJni(mAddrProp, dataList, nSetSize, mPlcInfo);
		}
		return bSuccess;
	}
	
	/**
	 * 字节流转换成short类型数组
	 * @param nSourceData
	 * @return
	 */
	public synchronized boolean bytesToShorts(Vector<Byte > nSourceList, Vector<Short > nResultList)
	{
		/*判断数据合法*/
		if(null == nSourceList || null == nResultList || nSourceList.size() <= 0)
		{
			return false;
		}
		
		/*获取byte数组长度*/
	    int nByteLen = nSourceList.size();
	    
	    int nLen = nByteLen/2;
	    if(nByteLen%2 != 0)
	    {
	    	nLen++;
	    }
	    
	    /*检查容器*/
	    if(nBToSArray.length < nByteLen)
	    {
	    	nBToSArray = new byte[nByteLen];
	    }
	    for(int i = 0; i < nByteLen; i++)
	    {
	    	nBToSArray[i] = nSourceList.get(i);
	    }

	    if(nShortArray.length < nLen)
	    {
	    	nShortArray = new short[nLen];
	    }
	    
	    /*去jni转换*/
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = bytesToShortsJni(nBToSArray, nByteLen, nShortArray);
		}
		if(bSuccess)
		{
			nResultList.clear();
			
			/*赋值*/
			for(int i = 0; i < nLen; i++)
			{
				nResultList.add(nShortArray[i]);
			}
		}
		
		return bSuccess;
	}
	
	/**
	 * 字节流转换成unsigned short类型数组
	 * @param nSourceData
	 * @return
	 */
	public synchronized boolean bytesToUShorts(Vector<Byte > nSourceList, Vector<Integer > nResultList)
	{
		/*判断数据合法*/
		if(null == nSourceList || null == nResultList || nSourceList.size() <= 0)
		{
			return false;
		}

		/*获取byte数组长度*/
	    int nByteLen = nSourceList.size();
	    
	    int nLen = nByteLen/2;
	    if(nByteLen%2 != 0)
	    {
	    	nLen++;
	    }
	    
	    /*检查容器*/
	    if(nBToUSArray.length < nByteLen)
	    {
	    	nBToUSArray = new byte[nByteLen];
	    }
	    for(int i = 0; i < nByteLen; i++)
	    {
	    	nBToUSArray[i] = nSourceList.get(i);
	    }
	    
	    if(nUShortArray.length < nLen)
	    {
	    	nUShortArray = new int[nLen];
	    }
	    
	    /*去jni转换*/
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = bytesToUShortsJni(nBToUSArray, nByteLen, nUShortArray);
		}
		if(bSuccess)
		{
			nResultList.clear();
			
			/*赋值*/
			for(int i = 0; i < nLen; i++)
			{
				nResultList.add(nUShortArray[i]);
			}
		}
		
		return bSuccess;
	}

	/**
	 * 字节流转换成int类型数组
	 * @param nSourceData
	 * @return
	 */
	public synchronized boolean bytesToInts(Vector<Byte > nSourceList, Vector<Integer > nResultList)
	{
		/*判断数据合法*/
		if(null == nSourceList || null == nResultList || nSourceList.size() <= 0)
		{
			return false;
		}

		/*获取byte数组长度*/
	    int nByteLen = nSourceList.size();

	    int nLen = nByteLen/4;
	    if(nByteLen%4 != 0)
	    {
	    	nLen++;
	    }
	    
	    /*检查容器*/
	    if(nBToIArray.length < nByteLen)
	    {
	    	nBToIArray = new byte[nByteLen];
	    }
	    for(int i = 0; i < nByteLen; i++)
	    {
	    	nBToIArray[i] = nSourceList.get(i);
	    }
	    
	    if(nIntArray.length < nLen)
	    {
	    	nIntArray = new int[nLen];
	    }
	    
	    /*去jni转换*/
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = bytesToIntsJni(nBToIArray, nByteLen, nIntArray);
		}
		if(bSuccess)
		{
			nResultList.clear();
			
			/*赋值*/
			for(int i = 0; i < nLen; i++)
			{
				nResultList.add(nIntArray[i]);
			}
		}
		
		return bSuccess;
	}
	
	/**
	 * 字节流转换成unsigned int类型数组
	 * @param nSourceData
	 * @return
	 */
	public synchronized boolean bytesToUInts(Vector<Byte > nSourceList, Vector<Long > nResultList)
	{
		/*判断数据合法*/
		if(null == nSourceList || null == nResultList || nSourceList.size() <= 0)
		{
			return false;
		}

		/*获取byte数组长度*/
	    int nByteLen = nSourceList.size();
	    
	    int nLen = nByteLen/4;
	    if(nByteLen%4 != 0)
	    {
	    	nLen++;
	    }
	    
	    /*检查容器*/
	    if(nBToLArray.length < nByteLen)
	    {
	    	nBToLArray = new byte[nByteLen];
	    }
	    for(int i = 0; i < nByteLen; i++)
	    {
	    	nBToLArray[i] = nSourceList.get(i);
	    }
	    
	    if(nLongArray.length < nLen)
	    {
	    	nLongArray = new long[nLen];
	    }
	    
	    /*去jni转换*/
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = bytesToUIntsJni(nBToLArray, nByteLen, nLongArray);
		}
		if(bSuccess)
		{
			nResultList.clear();
			
			/*赋值*/
			for(int i = 0; i < nLen; i++)
			{
				nResultList.add(nLongArray[i]);
			}
		}
		
		return bSuccess;
	}
	
	/**
	 * 字节流转换成float类型数组
	 * @param nSourceData
	 * @return
	 */
	public synchronized boolean bytesToFloats(Vector<Byte > nSourceList, Vector<Float > nResultList)
	{
		/*判断数据合法*/
		if(null == nSourceList || null == nResultList || nSourceList.size() <= 0)
		{
			return false;
		}

		/*获取byte数组长度*/
	    int nByteLen = nSourceList.size();

	    int nLen = nByteLen/4;
	    if(nByteLen%4 != 0)
	    {
	    	nLen++;
	    }
	    
	    /*检查容器*/
	    if(nBToFArray.length < nByteLen)
	    {
	    	nBToFArray = new byte[nByteLen];
	    }
	    for(int i = 0; i < nByteLen; i++)
	    {
	    	nBToFArray[i] = nSourceList.get(i);
	    }
	    
	    if(nFloatArray.length < nLen)
	    {
	    	nFloatArray = new float[nLen];
	    }
	    
	    /*去jni转换*/
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = bytesToFloatsJni(nBToFArray, nByteLen, nFloatArray);
		}
		if(bSuccess)
		{
			nResultList.clear();
			
			/*赋值*/
			for(int i = 0; i < nLen; i++)
			{
				nResultList.add(nFloatArray[i]);
			}
		}
		
		return bSuccess;
	}
	
	/**
	 * ASCII 转 bytes
	 * @param nSourceData
	 * @param nInterval
	 * @param nByteHLPos
	 * @return
	 */
	public byte[] asciiToBytes(byte[] nSourceData, int nInterval, BYTE_H_L_POS eByteHLPos)
	{
		if(null == nSourceData) return null;
		
		int nByteHLPos= 1;
		if(BYTE_H_L_POS.H_BYTE_FIRST == eByteHLPos)
		{
			nByteHLPos = 2;
		}
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			asciiToBytesJni(nSourceData, nInterval, nByteHLPos);
		}
		
		return nSourceData;
	}
	
	/**
	 * double 类型 转 bytes类型
	 * @param nSourceList
	 * @param nTargeData
	 * @param nByteHLPos
	 * @param nDataType
	 * @return
	 */
	public byte[] doublesToBytes(double[] nSourceList, SEND_DATA_STRUCT mSendProp)
	{
		if(nSourceList == null || mSendProp == null|| nSourceList.length <= 0) return null;
		
		byte[] nByteList = null;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			nByteList = doublesToBytesJni(nSourceList, getSendStructJni(mSendProp));
		}
		return nByteList;
	}
	
	public short intToShort(int nSourceData)
	{
		short result = 0;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			result = intToShortJni(nSourceData);
		}
		return result;
	}
	
	public float intToFloat(int nSourceData)
	{
		float result = 0;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			result = intToFloatJni(nSourceData);
		}
		return result;
	}
	
	public int intToUShort(int nSourceData)
	{
		int result = 0;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			result = intToUShortJni(nSourceData);
		}
		return result;
	}
	
	public long intToUInt(int nSourceData)
	{
		long result = 0;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			result = intToUIntJni(nSourceData);
		}
		return result;
	}
	
	/*供java调用的接口*/
	private PROTOCOL_TYPE m_eProtocolFunType;
	
	private boolean m_bOpenLibSuccess = false;
	
	/**
	 * 取得要写PLC的数据
	 * @param sSendData
	 * @param eCmnType
	 * @param mPlcInfo
	 * @return
	 */
	public boolean getSendWriteData(SendPkgArray sSendData, int eCmnType, PlcSampInfo mPlcInfo)
	{
		if(null == sSendData || null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = getWriteDataJni(sSendData, eCmnType, mPlcInfo);
		}
		return bSuccess;
	}
	
	/**
	 * 取得要读PLC的数据
	 * @param sSendData
	 * @param eCmnType
	 * @param mPlcInfo
	 * @return
	 */
	public boolean getSendReadData(SendPkgArray sSendData, int eCmnType, PlcSampInfo mPlcInfo)
	{
		if(null == sSendData || null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = getReadDataJni(sSendData, eCmnType, mPlcInfo);
		}
		return bSuccess;
	}
	
	/**
	 * 取得要握手协议包的数据
	 * @param sSendData
	 * @param eCmnType
	 * @param mPlcInfo
	 * @return
	 */
	public boolean getHandshakePkg(SEND_PACKAGE_JNI sSendData, PlcSampInfo mPlcInfo, int nPkgIndex, int nStationId, int nBaudRate)
	{
		if(null == sSendData || null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = getHandshakePkgJni(sSendData, mPlcInfo, nPkgIndex, nStationId, nBaudRate);
		}
		return bSuccess;
	}
	
	/**
	 * 获得当前地址
	 * @param mPlcInfo
	 * @param mCurrAddr
	 * @return
	 */
	public boolean getCurrAddr(PlcSampInfo mPlcInfo, AddrProp mCurrAddr)
	{
		if(null == mCurrAddr || null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = getCurrAddrJni(mPlcInfo, mCurrAddr);
		}
		return bSuccess;
	}
	
	/**
	 * 从PLC读得数据，拿去协议层校验和保存
	 * @param sSendData
	 * @param nReturnLen
	 * @param eSendType
	 * @param mPlcInfo
	 * @return
	 */
	public int setReadData(byte[] sSendData, int nSetSize, int nReturnLen, int nCurrTimes, int eSendType, PlcSampInfo mPlcInfo, PlcNoticValue nNoticValue)
	{
		int nResultInfo = -1;
		if(null == sSendData || sSendData.length <= 0 || null == mPlcInfo) return nResultInfo;
		
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			nResultInfo = setReadDataJni(sSendData, nSetSize, nReturnLen, nCurrTimes, eSendType, mPlcInfo, nNoticValue);
		}
		return nResultInfo;
	}
	
	/**
	 * 校验写数据是否成功
	 * @param sSendData
	 * @param nReturnLen
	 * @param mPlcInfo
	 * @return
	 */
	public int checkWriteData(byte[] sSendData, int nSetSize, int nReturnLen, int nCurrTimes, PlcSampInfo mPlcInfo, PlcNoticValue nNoticValue)
	{
		int nResultInfo = -1;
		if(null == sSendData || sSendData.length <= 0 || null == mPlcInfo) return nResultInfo;
		
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			nResultInfo = checkWriteDataJni(sSendData, nSetSize, nReturnLen, nCurrTimes, mPlcInfo, nNoticValue);
		}
		return nResultInfo;
	}
	
	/**
	 * 校验写数据是否成功
	 * @param sSendData
	 * @param nReturnLen
	 * @param mPlcInfo
	 * @return
	 */
	public int checkHandshakePkg(byte[] sSendData, int nSetSize, int nReturnLen, int nCurrTimes, PlcSampInfo mPlcInfo)
	{
		int nResultInfo = -1;
//		if(null == sSendData || sSendData.length <= 0 || null == mPlcInfo) return nResultInfo;
		if(null == mPlcInfo) return nResultInfo;
		
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			nResultInfo = checkHandshakePkgJni(sSendData, nSetSize, nReturnLen, nCurrTimes, mPlcInfo);
		}
		return nResultInfo;
	}
	
	/**
	 * 接收从串口接收到的数据到从站处理，只适合从站
	 * @param sRcvStr
	 * @param nStationId
	 * @param sSendDataStr
	 * @param mPlcInfo
	 * @return
	 */
	public int rcvStrForSlave(byte[] sRcvStr, int nSetSize, int nStationId, SendPkgArray mPkgListObj, PlcSampInfo mPlcInfo)
	{
		int nResultInfo = -1;
		if(null == sRcvStr || sRcvStr.length <= 0 || null == mPkgListObj || null == mPlcInfo) return nResultInfo;
		
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			nResultInfo = rcvStrForSlaveJni(sRcvStr, nSetSize, nStationId, mPkgListObj, mPlcInfo);
		}
		return nResultInfo;
	}
	
	
	/**
	 * 设置一个场景和窗口的地址
	 * @param mAddrList：地址集合
	 * @param bCover：是否覆盖以前设置的地址
	 * @return
	 */
	public synchronized boolean setOneSceneAddrs(Vector<AddrProp > mAddrList, boolean bCover, PlcSampInfo mPlcInfo, int nMaxRWlen)
	{
		if(null == mAddrList || null == mPlcInfo) return false;
		
		/*如果是从站，则不设置读地址*/
		PROTOCOL_TYPE ePlcType = getProtocolType(mPlcInfo);
		if(ePlcType == PROTOCOL_TYPE.SLAVE_MODEL) return false;
		
		boolean bSuccess = false; 
		int nSize = mAddrList.size();
		AddrProp[] mAddrJniList = new AddrProp[nSize];
		for(int i = 0; i < nSize; i++)
		{
			mAddrJniList[i] = mAddrList.get(i);
		}
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = setOneSceneAddrsJni(mAddrJniList,bCover, mPlcInfo,nMaxRWlen);
		}
		return bSuccess;
	}
	
	/**
	 * 清除所有读地址
	 * @return
	 */
	public boolean clearAllReadAddr()
	{
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = clearAllReadAddrJni();
		}
		return bSuccess;
	}
	/**
	 * 对地址的整理
	 * @param mAddrList
	 * @param mSortAddrList
	 * @param mPlcInfo
	 * @param nMaxRWlen
	 * @param bWriteAddr
	 * @return
	 */
	public synchronized boolean sortOutAddrList(Vector<AddrProp > mAddrList, AddrPropArray mSortAddrList, PlcSampInfo mPlcInfo, int nMaxRWlen, boolean bWriteAddr)
	{
		if(null == mAddrList || null == mSortAddrList || null == mPlcInfo) return false;
		
		boolean bSuccess = false; 
		int nSize = mAddrList.size();
		AddrProp[] mAddrJniList = new AddrProp[nSize];
		for(int i = 0; i < nSize; i++)
		{
			mAddrJniList[i] = mAddrList.get(i);
		}
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = sortOutAddrListJni(mAddrJniList,mSortAddrList, mPlcInfo, nMaxRWlen, bWriteAddr);
		}
		return bSuccess;
	}
	
	/**
	 * 对写地址打包
	 * @param mAddrProp
	 * @param dataList
	 * @param nSetSize
	 * @param mPkgListObj
	 * @param eCmnType
	 * @param mPlcInfo
	 * @return
	 */
	public boolean makeWritePackage(AddrProp mAddrProp, byte[] dataList, int nSetSize, SendPkgArray mPkgListObj, int eCmnType, PlcSampInfo mPlcInfo)
	{
		if(null == dataList || null == mPkgListObj || null == mAddrProp || null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = makeWritePackageJni(mAddrProp, dataList, nSetSize, mPkgListObj, eCmnType, mPlcInfo);
		}
		
		return bSuccess;
	}
	
	/**
	 * 对读地址打包
	 * @param mAddrProp
	 * @param dataList
	 * @param nSetSize
	 * @param mPkgListObj
	 * @param eCmnType
	 * @param mPlcInfo
	 * @return
	 */
	public boolean makeReadPackage(AddrProp mAddrProp, SendPkgArray mPkgListObj, int eCmnType, PlcSampInfo mPlcInfo)
	{
		if( null == mPkgListObj || null == mAddrProp || null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = makeReadPackageJni(mAddrProp, mPkgListObj, eCmnType, mPlcInfo);
		}
		
		return bSuccess;
	}
	
	/**
	 * 取得协议的功能类型，做主站还是做从站, 1是主站，2是从站
	 * @param mPlcInfo
	 * @return
	 */
	public synchronized PROTOCOL_TYPE getProtocolType(PlcSampInfo mPlcInfo)
	{
		if(null == mPlcInfo) return PROTOCOL_TYPE.OTHER_MODEL;
		
		int nProtoType = 0;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			nProtoType = getProtocolFunTypeJni(mPlcInfo);
		}
		PROTOCOL_TYPE result = PROTOCOL_TYPE.OTHER_MODEL;
		switch(nProtoType)
		{
		case 1:
		{
			result = PROTOCOL_TYPE.MASTER_MODEL;
			break;
		}
		case 2:
		{
			result = PROTOCOL_TYPE.SLAVE_MODEL;
			break;
		}
		default:
		{
			break;
		}
		}
		return result;
	}
	
	/**
	 * 关闭一个协议
	 * @param mPlcInfo
	 * @return
	 */
	public boolean closeProtocol(PlcSampInfo mPlcInfo)
	{
		if(null == mPlcInfo) return false;
		
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = closeProtocolJni(mPlcInfo);
		}
		return bSuccess;
	}
	
	/**
	 * 关闭所有协议
	 * @return
	 */
	public boolean closeAllProtocol()
	{
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = closeAllProtocolJni();
		}
		return bSuccess;
	}
	
	/**
	 * 设置通信标志，只有通信失败的时候才通知
	 * @param nCmnInfo
	 * @param mPlcInfo 
	 * @return
	 */
	public boolean setCmnInfo(int nCmnInfo, PlcSampInfo mPlcInfo)
	{
		boolean bSuccess = false;
		synchronized(ProtocolInterfaces.getProtocolInterface())
		{
			bSuccess = setCmnInfoJni(nCmnInfo, mPlcInfo);
		}
		return bSuccess;
	}
	
	/**
	 * 发送结构体转换成jni的结构体
	 * @param mSourceStruct
	 * @return
	 */
	private SEND_DATA_STRUCT_JNI getSendStructJni(SEND_DATA_STRUCT mSourceStruct)
	{
		if(null == m_tmpSendStruct || null == mSourceStruct)
		{
			return m_tmpSendStruct;
		}
		m_tmpSendStruct.nByteHLPos = 0;
		m_tmpSendStruct.nDataType = 0;
		m_tmpSendStruct.nReadWriteCtlType = 0;
		
		/*转换数据类型*/
		switch(mSourceStruct.eDataType)
		{
		case BIT_1:
		{
			m_tmpSendStruct.nDataType = 1;
			break;
		}
		case POSITIVE_INT_16:
		{
			m_tmpSendStruct.nDataType = 2;
			break;
		}
		case POSITIVE_INT_32:
		{
			m_tmpSendStruct.nDataType = 3;
			break;
		}
		case INT_16:
		{
			m_tmpSendStruct.nDataType = 4;
			break;
		}
		case INT_32:
		{
			m_tmpSendStruct.nDataType = 5;
			break;
		}
		case BCD_16:
		{
			m_tmpSendStruct.nDataType = 6;
			break;
		}
		case BCD_32:
		{
			m_tmpSendStruct.nDataType = 7;
			break;
		}
		case FLOAT_32:
		{
			m_tmpSendStruct.nDataType = 8;
			break;
		}
		case ASCII_STRING:
		{
			m_tmpSendStruct.nDataType = 9;
			break;
		}
		case HEX_16:
		{
			m_tmpSendStruct.nDataType = 10;
			break;
		}
		case HEX_32:
		{
			m_tmpSendStruct.nDataType = 11;
			break;
		}
		case OTC_16:
		{
			m_tmpSendStruct.nDataType = 12;
			break;
		}
		case OTC_32:
		{
			m_tmpSendStruct.nDataType = 13;
			break;
		}
		case OTHER_DATA_TYPE:
		default:
		{
			m_tmpSendStruct.nDataType = 14;
			break;
		}
		}
		
		/*转换读写等级*/
		m_tmpSendStruct.nReadWriteCtlType = (short)mSourceStruct.eReadWriteCtlType;
		
		/*转换高地位*/
		switch(mSourceStruct.eByteHLPos)
		{
		case L_BYTE_FIRST:
		{
			m_tmpSendStruct.nByteHLPos = 1;
			break;
		}
		case H_BYTE_FIRST:
		{
			m_tmpSendStruct.nByteHLPos = 2;
			break;
		}
		case OTHER_H_L_POS:
		default:
		{
			m_tmpSendStruct.nByteHLPos = 3;
			break;
		}
		}
		return m_tmpSendStruct;
	}
	
	/**
	 * 加载驱动
	 */
	static {
		System.loadLibrary("plc_drives_center");
	}
}
