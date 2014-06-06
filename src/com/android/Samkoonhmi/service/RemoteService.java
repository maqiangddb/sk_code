package com.android.Samkoonhmi.service;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

public class RemoteService extends Service {

	private static final String TAG="RemoteService";
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "RemoteService onCreate ..............");
		
		SKSceneManage.getInstance().mContext = this.getApplicationContext();
		
		DBTool.getInstance().getmSystemInfoBiz().selectSystemInfo();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "RemoteService onStart ..............");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCallback.kill();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private RemoteCallbackList<ICallback> mCallback = new RemoteCallbackList<ICallback>();
	public IService.Stub binder = new IService.Stub() {

		@Override
		public void registerCallback(ICallback callback) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterCallback(ICallback callback)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean setAddr(List<AddrInfo> mAddrList) throws RemoteException {
			// TODO Auto-generated method stub
			boolean bRcv = false;
			if (null == mAddrList || mAddrList.size() <= 0) {
				return bRcv;
			}
			
			bRcv = CmnPortManage.getInstance().openCmnPort((short)mAddrList.get(0).eConnectType);
			if(!bRcv)
			{
				return bRcv;
			}
			int nSize = mAddrList.size();
			
			Vector<AddrProp> AddrList = new Vector<AddrProp>();
			AddrList.clear();
			
			for(int i = 0; i < nSize; i++)
			{
				AddrProp AddrProp = new AddrProp();
				AddrProp.eConnectType = (short) mAddrList.get(i).eConnectType;
				if(3 == AddrProp.eConnectType)
				{
					AddrProp.nUserPlcId = 1;
				}
				else{
					AddrProp.nUserPlcId = 2;
				}
				AddrProp.nRegIndex = (short) mAddrList.get(i).nRegIndex;
				AddrProp.nPlcStationIndex = mAddrList.get(i).nPlcStationIndex;
				AddrProp.nAddrValue = mAddrList.get(i).nAddrValue;
				AddrProp.nAddrLen = mAddrList.get(i).nAddrLen;
				AddrProp.eAddrRWprop = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
				AddrProp.sPlcProtocol = "delta_dvp_master";
				AddrList.add(AddrProp);
			}
			
			bRcv = PlcRegCmnStcTools.setOneSceneReadAddr(AddrList, true);

			return bRcv;
		}

		@Override
		public boolean getBytesData(AddrInfo mAddrProp, List<String> dataList,
				int AddrType) throws RemoteException {
			// TODO Auto-generated method stub
			boolean bRcv = false;
			if (null == mAddrProp) {
				return bRcv;
			}
			dataList.clear();
	
			AddrProp AddrProp = new AddrProp();
			AddrProp.eConnectType = (short) mAddrProp.eConnectType;
			if(3 == AddrProp.eConnectType)
			{
				AddrProp.nUserPlcId = 1;
			}
			else{
				AddrProp.nUserPlcId = 2;
			}
			AddrProp.nRegIndex = (short) mAddrProp.nRegIndex;
			AddrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
			AddrProp.nAddrValue = mAddrProp.nAddrValue;
			AddrProp.nAddrLen = mAddrProp.nAddrLen;
			AddrProp.eAddrRWprop = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			AddrProp.sPlcProtocol = "delta_dvp_master";
	
			Vector<Byte> dataresult = new Vector<Byte>();
			dataresult.clear();
	
			SEND_DATA_STRUCT mSendProp = new SEND_DATA_STRUCT();
			mSendProp.eReadWriteCtlType = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
	
			switch (AddrType) {
			case 1: {
				mSendProp.eDataType = DATA_TYPE.BIT_1;
				break;
			}
			case 2: {
				mSendProp.eDataType = DATA_TYPE.INT_16;
				break;
			}
			case 3: {
				mSendProp.eDataType = DATA_TYPE.INT_32;
				break;
			}
			case 4: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_16;
				break;
			}
			case 5: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_32;
				break;
			}
			case 6: {
				mSendProp.eDataType = DATA_TYPE.BCD_16;
				break;
			}
			case 7: {
				mSendProp.eDataType = DATA_TYPE.BCD_32;
				break;
			}
			case 8: {
				mSendProp.eDataType = DATA_TYPE.FLOAT_32;
				break;
			}
			case 9: {
				mSendProp.eDataType = DATA_TYPE.ASCII_STRING;
				break;
			}
			case 10:{
				mSendProp.eDataType = DATA_TYPE.HEX_16;
				break;
			}
			case 11:{
				mSendProp.eDataType = DATA_TYPE.HEX_32;
				break;
			}
			case 12:{
				mSendProp.eDataType = DATA_TYPE.OTC_16;
				break;
			}
			case 13:{
				mSendProp.eDataType = DATA_TYPE.OTC_32;
				break;
			}
			default:
				return bRcv;
			}
			bRcv = PlcRegCmnStcTools.getRegBytesData(AddrProp, dataresult,
					mSendProp);
	
			if (!bRcv) {
				return bRcv;
			}
	
			for (int i = 0; i < dataresult.size(); i++) {
				String str = dataresult.elementAt(i).toString();
				dataList.add(str);
			}
	
			return bRcv;
		}

		@Override
		public boolean getLongData(AddrInfo mAddrProp, List<String> dataList,
				int AddrType) throws RemoteException {
			// TODO Auto-generated method stub
			boolean bRcv = false;
			if (null == mAddrProp) {
				return bRcv;
			}
			dataList.clear();
	
			AddrProp AddrProp = new AddrProp();
			AddrProp.eConnectType = (short) mAddrProp.eConnectType;
			if(3 == AddrProp.eConnectType)
			{
				AddrProp.nUserPlcId = 1;
			}
			else{
				AddrProp.nUserPlcId = 2;
			}
			AddrProp.nRegIndex = (short) mAddrProp.nRegIndex;
			AddrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
			AddrProp.nAddrValue = mAddrProp.nAddrValue;
			AddrProp.nAddrLen = mAddrProp.nAddrLen;
			AddrProp.eAddrRWprop = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			AddrProp.sPlcProtocol = "delta_dvp_master";
	
			Vector<Long> dataresult = new Vector<Long>();
			dataresult.clear();
	
			SEND_DATA_STRUCT mSendProp = new SEND_DATA_STRUCT();
			mSendProp.eReadWriteCtlType = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			switch (AddrType) {
			case 1: {
				mSendProp.eDataType = DATA_TYPE.BIT_1;
				break;
			}
			case 2: {
				mSendProp.eDataType = DATA_TYPE.INT_16;
				break;
			}
			case 3: {
				mSendProp.eDataType = DATA_TYPE.INT_32;
				break;
			}
			case 4: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_16;
				break;
			}
			case 5: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_32;
				break;
			}
			case 6: {
				mSendProp.eDataType = DATA_TYPE.BCD_16;
				break;
			}
			case 7: {
				mSendProp.eDataType = DATA_TYPE.BCD_32;
				break;
			}
			case 8: {
				mSendProp.eDataType = DATA_TYPE.FLOAT_32;
				break;
			}
			case 9: {
				mSendProp.eDataType = DATA_TYPE.ASCII_STRING;
				break;
			}
			case 10:{
				mSendProp.eDataType = DATA_TYPE.HEX_16;
				break;
			}
			case 11:{
				mSendProp.eDataType = DATA_TYPE.HEX_32;
				break;
			}
			case 12:{
				mSendProp.eDataType = DATA_TYPE.OTC_16;
				break;
			}
			case 13:{
				mSendProp.eDataType = DATA_TYPE.OTC_32;
				break;
			}
			default:
				return bRcv;
			}
			bRcv = PlcRegCmnStcTools.getRegLongData(AddrProp, dataresult, mSendProp);
	
			if (!bRcv) {
				return bRcv;
			}
	
			for (int i = 0; i < dataresult.size(); i++) {
				String str = dataresult.elementAt(i).toString();
				dataList.add(str);
			}
	
			return bRcv;
		}

		@Override
		public boolean getShortData(AddrInfo mAddrProp, List<String> dataList,
				int AddrType) throws RemoteException {
			// TODO Auto-generated method stub
			boolean bRcv = false;
			if (null == mAddrProp) {
				return bRcv;
			}
			dataList.clear();
	
			AddrProp AddrProp = new AddrProp();
			AddrProp.eConnectType = (short) mAddrProp.eConnectType;
			if(3 == AddrProp.eConnectType)
			{
				AddrProp.nUserPlcId = 1;
			}
			else{
				AddrProp.nUserPlcId = 2;
			}
			AddrProp.nRegIndex = (short) mAddrProp.nRegIndex;
			AddrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
			AddrProp.nAddrValue = mAddrProp.nAddrValue;
			AddrProp.nAddrLen = mAddrProp.nAddrLen;
			AddrProp.eAddrRWprop = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			AddrProp.sPlcProtocol = "delta_dvp_master";
	
			Vector<Integer> dataresult = new Vector<Integer>();
			dataresult.clear();
	
			SEND_DATA_STRUCT mSendProp = new SEND_DATA_STRUCT();
			mSendProp.eReadWriteCtlType = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			switch (AddrType) {
			case 1: {
				mSendProp.eDataType = DATA_TYPE.BIT_1;
				break;
			}
			case 2: {
				mSendProp.eDataType = DATA_TYPE.INT_16;
				break;
			}
			case 3: {
				mSendProp.eDataType = DATA_TYPE.INT_32;
				break;
			}
			case 4: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_16;
				break;
			}
			case 5: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_32;
				break;
			}
			case 6: {
				mSendProp.eDataType = DATA_TYPE.BCD_16;
				break;
			}
			case 7: {
				mSendProp.eDataType = DATA_TYPE.BCD_32;
				break;
			}
			case 8: {
				mSendProp.eDataType = DATA_TYPE.FLOAT_32;
				break;
			}
			case 9: {
				mSendProp.eDataType = DATA_TYPE.ASCII_STRING;
				break;
			}
			case 10:{
				mSendProp.eDataType = DATA_TYPE.HEX_16;
				break;
			}
			case 11:{
				mSendProp.eDataType = DATA_TYPE.HEX_32;
				break;
			}
			case 12:{
				mSendProp.eDataType = DATA_TYPE.OTC_16;
				break;
			}
			case 13:{
				mSendProp.eDataType = DATA_TYPE.OTC_32;
				break;
			}
			default:
				return bRcv;
			}
			bRcv = PlcRegCmnStcTools.getRegIntData(AddrProp, dataresult, mSendProp);
	
			if (!bRcv) {
				return bRcv;
			}
	
			for (int i = 0; i < dataresult.size(); i++) {
				String str = dataresult.elementAt(i).toString();
				dataList.add(str);
			}
			return bRcv;
		}

		/**
		 * 判断是否是数字
		 * 
		 * @param str
		 * @return
		 */
		private boolean isNumeric(String str) {
			Pattern pattern = Pattern
					.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$|^-\\d+\\.\\d+$");
			if (null == str || "".equals(str)) {
				return false;
			}
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
			return true;
		}
		
		@Override
		public boolean setAddrData(AddrInfo mAddrProp, String data, int AddrType)
				throws RemoteException {
			String inputString = data;
			boolean bRcv = isNumeric(inputString);
			
			AddrProp AddrProp = new AddrProp();
			AddrProp.eConnectType = (short) mAddrProp.eConnectType;
			if(3 == AddrProp.eConnectType)
			{
				AddrProp.nUserPlcId = 1;
			}
			else{
				AddrProp.nUserPlcId = 2;
			}
			AddrProp.nRegIndex = (short) mAddrProp.nRegIndex;
			AddrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
			AddrProp.nAddrValue = mAddrProp.nAddrValue;
			AddrProp.nAddrLen = mAddrProp.nAddrLen;
			AddrProp.eAddrRWprop = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			AddrProp.sPlcProtocol = "delta_dvp_master";
			
			SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
			
			DATA_TYPE eType;
			bRcv = false;
			switch (AddrType) {
			case 1: {
				eType = DATA_TYPE.BIT_1;
				byte[] dataList = new byte[2];
				if(Integer.parseInt(inputString) > 0)
				{
					dataList[0] = 1;
				}
				else
				{
					dataList[0] = 0;
				}
				mSendData.eDataType = DATA_TYPE.BIT_1;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				bRcv = PlcRegCmnStcTools
						.setRegBytesData(AddrProp, dataList, mSendData);
				break;
				}
			case 2: {
				Vector<Integer > dataList = new Vector<Integer>();
				dataList.clear();
				eType = DATA_TYPE.INT_16;
				mSendData.eDataType = DATA_TYPE.INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				dataList.add(Integer.parseInt(inputString));
				bRcv = PlcRegCmnStcTools
				.setRegIntData(AddrProp, dataList, mSendData);
				break;
				}
			case 3: {
				eType = DATA_TYPE.INT_32;
				break;
				}
			case 4: {
				Vector<Integer > dataList = new Vector<Integer>();
				dataList.clear();
				eType = DATA_TYPE.POSITIVE_INT_16;
				mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				dataList.add(Integer.parseInt(inputString));
				bRcv = PlcRegCmnStcTools
				.setRegIntData(AddrProp, dataList, mSendData);
				break;
				}
			case 5: {
				Vector<Long > dataList = new Vector<Long>();
				dataList.clear();
				eType = DATA_TYPE.POSITIVE_INT_32;
				mSendData.eDataType = DATA_TYPE.POSITIVE_INT_32;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				dataList.add(Long.parseLong(inputString));
				bRcv = PlcRegCmnStcTools
				.setRegLongData(AddrProp, dataList, mSendData);
				break;
				}
			case 6: {
				eType = DATA_TYPE.BCD_16;
				break;
				}
			case 7: {
				eType = DATA_TYPE.BCD_32;
				break;
				}
			case 8: {
				eType = DATA_TYPE.FLOAT_32;
				Vector<Double > dataList = new Vector<Double>();
				dataList.clear();
				eType = DATA_TYPE.FLOAT_32;
				mSendData.eDataType = DATA_TYPE.FLOAT_32;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				dataList.add(Double.parseDouble(inputString));
				bRcv = PlcRegCmnStcTools
				.setRegDoubleData(AddrProp, dataList, mSendData);
				break;
				}
			case 9: {
				eType = DATA_TYPE.ASCII_STRING;
				break;
				}
			case 10:{
				eType = DATA_TYPE.HEX_16;
				break;
				}
			case 11:{
				eType = DATA_TYPE.HEX_32;
				break;
				}
			case 12:{
				eType = DATA_TYPE.OTC_16;
				break;
				}
			case 13:{
				eType = DATA_TYPE.OTC_32;
				break;
				}
			default:{
				return false;
				}
			}
			return bRcv;
			}

		@Override
		public boolean getFloatData(AddrInfo mAddrProp, List<String> dataList,
				int AddrType) throws RemoteException {
			// TODO Auto-generated method stub
			boolean bRcv = false;
			if (null == mAddrProp) {
				return bRcv;
			}
			dataList.clear();
	
			AddrProp AddrProp = new AddrProp();
			AddrProp.eConnectType = (short) mAddrProp.eConnectType;
			if(3 == AddrProp.eConnectType)
			{
				AddrProp.nUserPlcId = 1;
			}
			else{
				AddrProp.nUserPlcId = 2;
			}
			AddrProp.nRegIndex = (short) mAddrProp.nRegIndex;
			AddrProp.nPlcStationIndex = mAddrProp.nPlcStationIndex;
			AddrProp.nAddrValue = mAddrProp.nAddrValue;
			AddrProp.nAddrLen = mAddrProp.nAddrLen;
			AddrProp.eAddrRWprop = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			AddrProp.sPlcProtocol = "delta_dvp_master";
	
			Vector<Float> dataresult = new Vector<Float>();
			dataresult.clear();
	
			SEND_DATA_STRUCT mSendProp = new SEND_DATA_STRUCT();
			mSendProp.eReadWriteCtlType = READ_WRITE_COM_TYPE.SCENE_CONTROL_LOOP_R;
			switch (AddrType) {
			case 1: {
				mSendProp.eDataType = DATA_TYPE.BIT_1;
				break;
			}
			case 2: {
				mSendProp.eDataType = DATA_TYPE.INT_16;
				break;
			}
			case 3: {
				mSendProp.eDataType = DATA_TYPE.INT_32;
				break;
			}
			case 4: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_16;
				break;
			}
			case 5: {
				mSendProp.eDataType = DATA_TYPE.POSITIVE_INT_32;
				break;
			}
			case 6: {
				mSendProp.eDataType = DATA_TYPE.BCD_16;
				break;
			}
			case 7: {
				mSendProp.eDataType = DATA_TYPE.BCD_32;
				break;
			}
			case 8: {
				mSendProp.eDataType = DATA_TYPE.FLOAT_32;
				break;
			}
			case 9: {
				mSendProp.eDataType = DATA_TYPE.ASCII_STRING;
				break;
			}
			case 10:{
				mSendProp.eDataType = DATA_TYPE.HEX_16;
				break;
			}
			case 11:{
				mSendProp.eDataType = DATA_TYPE.HEX_32;
				break;
			}
			case 12:{
				mSendProp.eDataType = DATA_TYPE.OTC_16;
				break;
			}
			case 13:{
				mSendProp.eDataType = DATA_TYPE.OTC_32;
				break;
			}
			default:
				return bRcv;
			}
			bRcv = PlcRegCmnStcTools.getRegFloatData(AddrProp, dataresult, mSendProp);
	
			if (!bRcv) {
				return bRcv;
			}
	
			for (int i = 0; i < dataresult.size(); i++) {
				String str = dataresult.elementAt(i).toString();
				dataList.add(str);
			}
	
			return bRcv;
		}

//		@Override
//		public boolean setSeralPort(int nPort) throws RemoteException {
//			// TODO Auto-generated method stub
//			boolean bRcv = false;
//			 List<PlcConnectionInfo> PlcConnectArray = SystemInfo.getPlcConnectionList();
//			return bRcv;
//		}
		};

}
