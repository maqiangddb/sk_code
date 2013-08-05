package com.android.Samkoonhmi;

import java.util.Vector;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.pmem.PowerSave;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.PowerSaveProp;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.model.SystemInfo;

public class SKSaveThread {

	private final String TAG = "SKSaveThread";
	private static final int HANDLER_LOOPER=1;
	private static final int HANDLER_INIT=2;
	public static boolean flag=false;
	public static boolean isAK_035_AET=false;
	
	private int switch_flag=0;

	private int switch_index=0;
	// 保存间隔时间
	private static int nTime = 1000;
	//监视线程
	private HandlerThread mThread;

	private SKTrendHandler handler;

	private PowerSaveProp m_PowerSaveParam;
//	PlcNoticValue nNoticValue;// = new PlcNoticValue();
	
	/*临时容器*/
	private Vector<Byte > tmpList = new Vector<Byte >();	

	private	AddrProp lbaddr=new AddrProp();

	private	AddrProp lwaddr=new AddrProp();

	//要监视的地址信息
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
	
	/*控制线程启动和停止的变量*/
	private boolean m_bThreadLoop = false;
	//单例
	private static SKSaveThread sInstance=null;
	public synchronized static SKSaveThread getInstance(){
		if (sInstance==null) {
			sInstance=new SKSaveThread();
		}
		return sInstance;
	}
	
	private SKSaveThread() {
		if (mThread==null) {
		mThread =new HandlerThread("SKSaveThread");
		mThread.start();
		handler=new SKTrendHandler(mThread.getLooper());
		m_PowerSaveParam=new PowerSaveProp();
		m_PowerSaveParam.WriteBuff=null;//new byte[lbaddr.nAddrLen+lwaddr.nAddrLen*2]; ;
		}
	}

	public void startThread() {
		if (mThread!=null) {
		}
	}

	public void stopThread() {
		if(mThread!=null){
			mThread.stop();
		}
	}	
	/**
	 * 线程启动
	 */
	public void start()
	{
		m_bThreadLoop = true;
		doSave();
	}
	
	public void stop()
	{
		m_bThreadLoop = false; 
//		Log.d(TAG, "stop m_bThreadLoop "+m_bThreadLoop);

	}
	
	/**
	 * 一个地址值的保存属性
	 * @author latory
	 *
	 */
	public class CollectSaveProp{
		public short nAddrId = 0;
		public short nAddrValueLen = 0;
		public byte[] nValueByteList = null;
	}
	
	/**
	 * 数据采集记录属性
	 * @author latory
	 *
	 */
	public class CollectRecordProp{
		public short nGroupId ;
		public long nCurrTime = 0;
		public Vector<CollectSaveProp > nValueList = null;
	}
	
	/**
	 * 报警保存属性
	 * @author latory
	 *
	 */
	public class AlamSaveProp{
		public short nGroupId;
		public short nIndex;
		public short nStatus;
		public long nAlamTime = 0;
		public long nRemoveAlamTime = 0;
	}

	public void saveAlamSaveProp(AlamSaveProp nAlamSave)
	{
		if (isAK_035_AET) {
			return;
		}
	   	short i;
    	int	nAddrLen=0;
 		int compare_index;
    	int nAddrOffset;
    	
    	
		if(nAlamSave==null)
			return;

	 	if(switch_flag==1)
    		return;
    	switch_flag=1;
    	nAddrLen=4+4+1+2+2+2+8+8;//nAddrOffset+nAddrLen+type+nGroupId+nIndex+nStatus+nAlamTime+nRemoveAlamTime

    	compare_index=switch_index+1;
 		nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset()+nAddrLen;

 		if(nAddrOffset>compare_index*10240)
		{
				switch_index++;
				if(switch_index>=12)
					switch_index=0;
				PowerSave.getInstance().clear_memory(switch_index);
				PowerSave.getInstance().setnWriteAddrOffset(switch_index*10240);				
	//			Log.d(TAG, "Comb_PowerSave switch_index "+switch_index+"nAddrOffset "+nAddrOffset);
				nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset()+nAddrLen;
		}
	  	m_PowerSaveParam.nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset();
    	m_PowerSaveParam.nAddrLen=nAddrLen;
    	m_PowerSaveParam.WriteBuff=new byte[m_PowerSaveParam.nAddrLen];  	
  
      	i=0;
    	m_PowerSaveParam.WriteBuff[i++]=2;  //collect 
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen); 	
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nGroupId>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nGroupId); 	
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nIndex>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nIndex); 	
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nStatus>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nStatus); 	  	
     	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>56);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>48);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>40);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>32);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nAlamTime);   	 		
     	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>56);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>48);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>40);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>32);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nAlamSave.nRemoveAlamTime);   	

		PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);

	//	Log.d(TAG, "nAddrOffset "+nAddrOffset);
		PowerSave.getInstance().setnWriteAddrOffset(nAddrOffset);
		m_PowerSaveParam.WriteBuff=null;
    	switch_flag=0;	
	}
	
	/**
	 * 数据采集保存函数
	 * @param nGroupId
	 * @param nCurrTime
	 * @param mValueList
	 */
	public void saveDataCollect(short nGroupId, long nCurrTime,  Vector<CollectSaveProp > mValueList)
	{
		if (isAK_035_AET) {
			return;
		}
	   	short i;
    	int	nAddrLen=0;
 		int compare_index;
    	int nSize=0,nAddrOffset,nHeadLen;
    	CollectSaveProp CurValueList;
    	
		if(mValueList==null)
		{
			return;
		}
	 	if(switch_flag==1)
    		return;
    	switch_flag=1;

    	for(short j=0;j<mValueList.size();j++)
    	{
    		CurValueList=mValueList.get(j);
    		if(CurValueList==null)
    			continue;
    		nSize+=4;   //nAddrId+nAddrValueLen
    		nSize+=CurValueList.nAddrValueLen;
    	}
    	nHeadLen=4+4+1+2+8;//nAddrOffset+nAddrLen+type+groupnum+time;
    	nAddrLen=nHeadLen+nSize;

    	compare_index=switch_index+1;
 		nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset()+nAddrLen;

 		if(nAddrOffset>compare_index*10240)
		{
				switch_index++;
				if(switch_index>=12)
					switch_index=0;
				PowerSave.getInstance().clear_memory(switch_index);
				PowerSave.getInstance().setnWriteAddrOffset(switch_index*10240);				
	//			Log.d(TAG, "Comb_PowerSave switch_index "+switch_index+"nAddrOffset "+nAddrOffset);
				nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset()+nAddrLen;
		}
 
 	  	m_PowerSaveParam.nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset();
    	m_PowerSaveParam.nAddrLen=nAddrLen;
    	m_PowerSaveParam.WriteBuff=new byte[m_PowerSaveParam.nAddrLen]; 
		
    	i=0;
    	m_PowerSaveParam.WriteBuff[i++]=1;  //collect 
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nGroupId>>8);
       	m_PowerSaveParam.WriteBuff[i++]=(byte)(nGroupId); 
      	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>56);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>48);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>40);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>32);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>24);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>16);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime>>8);
    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nCurrTime);   	
  	
       	for(short j=0;j<mValueList.size();j++)
    	{
    		CurValueList=mValueList.get(j);
    		if(CurValueList==null)
    			continue;
           	m_PowerSaveParam.WriteBuff[i++]=(byte)(CurValueList.nAddrId>>8);
           	m_PowerSaveParam.WriteBuff[i++]=(byte)(CurValueList.nAddrId); 
           	m_PowerSaveParam.WriteBuff[i++]=(byte)(CurValueList.nAddrValueLen>>8);
           	m_PowerSaveParam.WriteBuff[i++]=(byte)(CurValueList.nAddrValueLen); 
    		for(short k=0;k<CurValueList.nAddrValueLen;k++)
    			m_PowerSaveParam.WriteBuff[i++]=(byte) CurValueList.nValueByteList[k];
    	}
		PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);

	//	Log.d(TAG, "nAddrOffset "+nAddrOffset);
		PowerSave.getInstance().setnWriteAddrOffset(nAddrOffset); 
		m_PowerSaveParam.WriteBuff=null;
    	switch_flag=0;
	}

//	public void AddnNoticValue(PlcNoticValue nSaveNoticValue)
//	{
//	//	Log.d(TAG, "AddnNoticValue ");
//		nNoticValue=nSaveNoticValue;
//		Comb_PowerSave();
//	}


//    /***
//    ***数据写入掉电保存区
//    ***/
//    private void Comb_PowerSave()
//    {
//    	short i;
//    	int	nAddrLen=0;
// 		int compare_index;
//    	int nSize,nAddrOffset,nAddrPropLen;
//    	long nTime;
//    	if(nNoticValue.nValueList==null)
//    		return;
///*
//    	while(switch_flag==1)
//		{
//			SystemClock.sleep(1);
//		}
// */
//    	if(switch_flag==1)
//    		return;
//    	switch_flag=1;
//    	nSize=nNoticValue.nValueList.length;
//    	nTime=(long) (System.currentTimeMillis()/1000);
//    	nAddrPropLen=2+2+2+4+4+4+4;//+nNoticValue.mAddrProp.sPlcProtocol.length();
//    	nAddrLen=8+4+4+nAddrPropLen+nSize;
// 
// 		compare_index=switch_index+1;
// 		nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset()+nAddrLen;
//			
//		if(nAddrOffset>compare_index*10240)
//		{
//				switch_index++;
//				if(switch_index>=12)
//					switch_index=0;
//				PowerSave.getInstance().clear_memory(switch_index);
//				PowerSave.getInstance().setnWriteAddrOffset(switch_index*10240);				
//	//			Log.d(TAG, "Comb_PowerSave switch_index "+switch_index+"nAddrOffset "+nAddrOffset);
//				nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset()+nAddrLen;
//		}
//			
//    	m_PowerSaveParam.nAddrOffset=PowerSave.getInstance().getnWriteAddrOffset();
//    	m_PowerSaveParam.nAddrLen=nAddrLen;
//    	m_PowerSaveParam.WriteBuff=new byte[m_PowerSaveParam.nAddrLen]; 
//    	
//    	i=0;
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrOffset);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(m_PowerSaveParam.nAddrLen);
//      	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>56);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>48);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>40);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>32);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nTime);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.eConnectType>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.eConnectType);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nUserPlcId>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nUserPlcId);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nRegIndex>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nRegIndex);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nPlcStationIndex>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nPlcStationIndex>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nPlcStationIndex>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nPlcStationIndex);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrValue>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrValue>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrValue>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrValue);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrLen>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrLen>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrLen>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.nAddrLen);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.eAddrRWprop>>24);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.eAddrRWprop>>16);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.eAddrRWprop>>8);
//    	m_PowerSaveParam.WriteBuff[i++]=(byte)(nNoticValue.mAddrProp.eAddrRWprop);
//
//	//		for(short j=0;j<nNoticValue.mAddrProp.sPlcProtocol.length();j++)
//	//			m_PowerSaveParam.WriteBuff[i++]=(byte) nNoticValue.mAddrProp.sPlcProtocol.charAt(j);
//		for(short j=0;j<nNoticValue.nValueList.length;j++)
//			m_PowerSaveParam.WriteBuff[i++]=(byte) nNoticValue.nValueList[j];
//		PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);
//
//	//	Log.d(TAG, "nAddrOffset "+nAddrOffset);
//		PowerSave.getInstance().setnWriteAddrOffset(nAddrOffset);
//		m_PowerSaveParam.WriteBuff=null;
//    	switch_flag=0;
//    }	
	
	/**
	 * 初始化
	 */
	private void init(){
		
		if(flag==false)
		{
			lbaddr.nAddrValue=SystemInfo.getnstartLB();
			lbaddr.eConnectType=1;
			lbaddr.nRegIndex=0;
			lbaddr.nAddrLen=SystemInfo.getnlengthLB();
			lbaddr.eAddrRWprop=READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			lwaddr.nAddrValue=SystemInfo.getnstartLW();
			lwaddr.eConnectType=1;
			lwaddr.nRegIndex=1;
			lwaddr.nAddrLen=SystemInfo.getnlengthLW();
			lwaddr.eAddrRWprop=READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			flag = true;
			switch_index=0;
			PowerSave.getInstance().setnWriteAddrOffset(0);
		}
		start();
	}	

	/**
	 * 开始保存
	 */
	public void startSave() {
		handler.sendEmptyMessageDelayed(HANDLER_INIT, 10);
	}
	
	/**
	 * 内部地址保存
	 */
	private void doSave() {
		
		m_bThreadLoop = true;
		while(switch_flag==1)
		{
			SystemClock.sleep(10);
		}
		
    	switch_flag=1;
		if (isAK_035_AET) {
			//AK_035_AET 内部寄存器掉电保存
			doSave035AET();
		}else {
			//其他型号 内部寄存器掉电保存
			doSaveDefault();
		}
    	switch_flag=0;
	}
	
	/**
	 * 默认内部寄存器保存
	 * 默认是128k
	 */
	private void doSaveDefault(){
		short j = 0;
		try {

			mSendData.eDataType = DATA_TYPE.BIT_1; // LB
			mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			tmpList.clear();
			m_PowerSaveParam.WriteBuff = null;
			if (lbaddr.nAddrLen != 0) {
				boolean bLB_Success = PlcRegCmnStcTools.getRegBytesData(lbaddr,
						tmpList, mSendData);
				if (bLB_Success) {
					m_PowerSaveParam.WriteBuff = new byte[tmpList.size() + 8];
					j = 0;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrValue >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrValue >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrValue >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lbaddr.nAddrValue;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrLen >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrLen >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrLen >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lbaddr.nAddrLen;
					for (short i = 0; i < tmpList.size(); i++) {
						m_PowerSaveParam.WriteBuff[j++] = tmpList.get(i);
					}
					m_PowerSaveParam.nAddrLen = j;// tmpList.size();
					m_PowerSaveParam.nAddrOffset = 120 * 1024; // after 100k
					PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);
				}
			}
			mSendData.eDataType = DATA_TYPE.INT_16; // LW
			mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			tmpList.clear();
			m_PowerSaveParam.WriteBuff = null;
			if (lwaddr.nAddrLen != 0) {
				boolean bLW_Success = PlcRegCmnStcTools.getRegBytesData(lwaddr,
						tmpList, mSendData);
				if (bLW_Success) {
					m_PowerSaveParam.WriteBuff = new byte[tmpList.size() + 8];
					j = 0;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrValue >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrValue >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrValue >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lwaddr.nAddrValue;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrLen >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrLen >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrLen >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lwaddr.nAddrLen;
					for (short i = 0; i < tmpList.size(); i++) {
						m_PowerSaveParam.WriteBuff[j++] = tmpList.get(i);
					}
					m_PowerSaveParam.nAddrLen = j;// tmpList.size();
					m_PowerSaveParam.nAddrOffset = 122 * 1024; // after 102k
					PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);
				}
			}
			if (flag) {
				handler.sendEmptyMessageDelayed(HANDLER_LOOPER, nTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "SKSaveThread doSave error!");
		}
	}
	
	/**
	 * 035AET是8k
	 */
	private void doSave035AET(){
		short j = 0;
		try {

			mSendData.eDataType = DATA_TYPE.BIT_1; // LB
			mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			tmpList.clear();
			m_PowerSaveParam.WriteBuff = null;
			if (lbaddr.nAddrLen != 0) {
				boolean bLB_Success = PlcRegCmnStcTools.getRegBytesData(lbaddr,
						tmpList, mSendData);
				if (bLB_Success) {
					m_PowerSaveParam.WriteBuff = new byte[tmpList.size() + 8];
					j = 0;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrValue >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrValue >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrValue >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lbaddr.nAddrValue;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrLen >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrLen >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lbaddr.nAddrLen >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lbaddr.nAddrLen;
					for (short i = 0; i < tmpList.size(); i++) {
						m_PowerSaveParam.WriteBuff[j++] = tmpList.get(i);
					}
					m_PowerSaveParam.nAddrLen = j;// tmpList.size();
					m_PowerSaveParam.nAddrOffset = 0;
					PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);
				}
			}
			mSendData.eDataType = DATA_TYPE.INT_16; // LW
			mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
			tmpList.clear();
			m_PowerSaveParam.WriteBuff = null;
			if (lwaddr.nAddrLen != 0) {
				boolean bLW_Success = PlcRegCmnStcTools.getRegBytesData(lwaddr,
						tmpList, mSendData);
				if (bLW_Success) {
					m_PowerSaveParam.WriteBuff = new byte[tmpList.size() + 8];
					j = 0;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrValue >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrValue >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrValue >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lwaddr.nAddrValue;
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrLen >> 24);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrLen >> 16);
					m_PowerSaveParam.WriteBuff[j++] = (byte) (lwaddr.nAddrLen >> 8);
					m_PowerSaveParam.WriteBuff[j++] = (byte) lwaddr.nAddrLen;
					for (short i = 0; i < tmpList.size(); i++) {
						m_PowerSaveParam.WriteBuff[j++] = tmpList.get(i);
					}
					m_PowerSaveParam.nAddrLen = j;// tmpList.size();
					m_PowerSaveParam.nAddrOffset = 2 * 1024;
					PowerSave.getInstance().WritePowerSave(m_PowerSaveParam);
				}
			}
			if (flag) {
				handler.sendEmptyMessageDelayed(HANDLER_LOOPER, nTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "SKSaveThread doSave error!");
		}
	}
	
	
	class SKTrendHandler extends Handler{

		SKTrendHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==HANDLER_LOOPER) {
//			Log.d(TAG, "handleMessage m_bThreadLoop "+m_bThreadLoop);
				if(m_bThreadLoop)
						doSave();
			}else if (msg.what==HANDLER_INIT) {
				//初始化数据
				init();
			}
		}
		
	}	
	
}
