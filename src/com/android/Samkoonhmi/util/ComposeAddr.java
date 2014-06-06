package com.android.Samkoonhmi.util;

import java.util.Vector;


import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;

/**
 * 合成新地址
 * @author Administrator
 *
 */
public class ComposeAddr {
	// 单例
	private static ComposeAddr sInstance = null;

	public synchronized static ComposeAddr getInstance() {
		if (sInstance == null) {
			sInstance = new ComposeAddr();
		}
		return sInstance;
	}

	private Vector<Integer> mTemplist;//取值集合
	private SEND_DATA_STRUCT mSendData; 
	private int mOffersetValue =0;
	
	public AddrProp newAddress(AddrProp mAddrProp,AddrProp mOffsetAddr)
	{
		if(mAddrProp == null || mOffsetAddr == null)
		{
			return null;
		}
		
		//根据偏移地址去取值
		if(mTemplist == null)
		{
			mTemplist = new Vector<Integer>();
		}else{
			mTemplist.clear();
		}
		if (mSendData == null) {
			mSendData = new SEND_DATA_STRUCT();
		}
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		mSendData.eDataType = DATA_TYPE.INT_16;
		boolean b = PlcRegCmnStcTools.getRegIntData(mOffsetAddr, mTemplist, mSendData);
		if(b && mTemplist.size()!=0){
			
			mOffersetValue = mTemplist.get(0);
		}
		AddrProp addrProp = new AddrProp();
		addrProp.eAddrRWprop = mAddrProp.eAddrRWprop;
		addrProp.eConnectType = mAddrProp.eConnectType;
		addrProp.nAddrId = mAddrProp.nAddrId;
		addrProp.nAddrLen = mAddrProp.nAddrLen;
		addrProp.nAddrValue = mOffersetValue+mAddrProp.nAddrValue;
		addrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
		addrProp.nRegIndex = mAddrProp.nRegIndex;
		addrProp.nUserPlcId = mAddrProp.nUserPlcId;
		addrProp.sPlcProtocol = mAddrProp.sPlcProtocol;
		return addrProp;
	}
	
}
