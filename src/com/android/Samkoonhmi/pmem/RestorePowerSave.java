package com.android.Samkoonhmi.pmem;

import java.io.File;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.SKSaveThread;
import com.android.Samkoonhmi.SKSaveThread.AlamSaveProp;
import com.android.Samkoonhmi.SKSaveThread.CollectRecordProp;
import com.android.Samkoonhmi.SKSaveThread.CollectSaveProp;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.model.skglobalcmn.CollectDataInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.PowerSaveProp;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

public class RestorePowerSave {

	public static int nTotalSize = 128;
	public static boolean isAK_035_AET = false;
	private static final String TAG="RestorePowerSave";

	/**
	 * 还原所有数据
	 */
	public static void restoreAllPowerData() {
		Log.d("SKScene", "restore all power data......");

		if (!isAK_035_AET) {
			PowerSaveProp mSaveProp = new PowerSaveProp();
			mSaveProp.nAddrOffset = 0;
			mSaveProp.nAddrLen = nTotalSize * 1024;

			byte[] nSaveBuff = new byte[mSaveProp.nAddrLen];
			PowerSave.getInstance().ReadPowerSave(mSaveProp, nSaveBuff);

			/* 恢复历史数据和报警区 */
			restoreSave(nSaveBuff);
		}

		/* 清除掉电保存区 */
		PowerSave.getInstance().clearPmem();

		/* 删除掉电保存文件 */
		File mSaveFile = new File("/powersave.bin");
		if (mSaveFile.exists()) {
			mSaveFile.delete();
		}

		/* 创建和启动掉电保存线程 */
		SKSaveThread.getInstance().startSave();
	}

	/**
	 * 恢复内存存储区
	 */
	public static void restoreLocal() {
		
		try {
			
			File mSaveFile = new File("/powersave.bin");
			if (!mSaveFile.exists()) {
				Log.e(TAG, "no powersave.bin...");
				return;
			}

			PowerSaveProp mSaveProp = new PowerSaveProp();
			mSaveProp.nAddrOffset = 0;
			mSaveProp.nAddrLen = nTotalSize * 1024;

			byte[] nSaveBuff = new byte[mSaveProp.nAddrLen];
			PowerSave.getInstance().ReadPowerSave(mSaveProp, nSaveBuff);

			/* 读出起始地址 */
			int nStartPos = (nTotalSize - 8) * 1024;
			int nIndex = 0;
			int nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			int nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			int nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			int nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			int nStartAddr = (nTmpByte3 << 24) + (nTmpByte2 << 16)
					+ (nTmpByte1 << 8) + nTmpByte0;

			nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			int nAddrLen = (nTmpByte3 << 24) + (nTmpByte2 << 16) + (nTmpByte1 << 8)
					+ nTmpByte0;

			if (nAddrLen >= 2 * 1024 - 8) {
				nAddrLen = 2 * 1024 - 8;
			}

			if (nAddrLen > 0) {
				byte[] nLocalB = new byte[nAddrLen];
				for (int i = 0; i < nAddrLen; i++) {
					nLocalB[i] = nSaveBuff[nStartPos + i + 8];
				}

				/* 初始化地址 */
				AddrProp mAddr = new AddrProp();
				mAddr.eAddrRWprop = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
				mAddr.eConnectType = 1;
				mAddr.nAddrLen = nAddrLen;
				mAddr.nAddrValue = nStartAddr;
				mAddr.nPlcStationIndex = 1;
				mAddr.nRegIndex = 0;
				mAddr.nUserPlcId = 0;
				mAddr.sPlcProtocol = "local";

				/* 初始化发送结构体 */
				SEND_DATA_STRUCT mSendProp = new SEND_DATA_STRUCT();
				mSendProp.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
				mSendProp.eDataType = DATA_TYPE.BIT_1;
				mSendProp.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

				if (nStartAddr > -1 && nStartAddr < 65536) {
					if (nAddrLen <= 1024) {
						PlcRegCmnStcTools
								.setRegBytesData(mAddr, nLocalB, mSendProp);
					}
				}else {
					Log.e(TAG, "nStartAddr:" + nStartAddr + ",nAddrLen:"+ nAddrLen);
				}

			}

			/* 读出起始地址 */
			nStartPos = (nTotalSize - 6) * 1024;
			nIndex = 0;
			nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nStartAddr = (nTmpByte3 << 24) + (nTmpByte2 << 16) + (nTmpByte1 << 8)
					+ nTmpByte0;

			nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
			nAddrLen = (nTmpByte3 << 24) + (nTmpByte2 << 16) + (nTmpByte1 << 8)
					+ nTmpByte0;

			int nSetLen = nAddrLen * 2;
			if (nSetLen >= 6 * 1024 - 8) {
				nAddrLen = (6 * 1024 - 8) / 2;
				return;
			} 

			if (nAddrLen > 0 && nSetLen>0) {
				byte[] nLocalW = new byte[nSetLen];
				for (int i = 0; i < nSetLen; i++) {
					nLocalW[i] = nSaveBuff[nStartPos + i + 8];
				}

				/* 初始化地址 */
				AddrProp mAddr = new AddrProp();
				mAddr.eAddrRWprop = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
				mAddr.eConnectType = 1;
				mAddr.nAddrLen = nAddrLen;
				mAddr.nAddrValue = nStartAddr;
				mAddr.nPlcStationIndex = 1;
				mAddr.nRegIndex = 1;
				mAddr.nUserPlcId = 0;
				mAddr.sPlcProtocol = "local";

				/* 初始化发送结构体 */
				SEND_DATA_STRUCT mSendProp = new SEND_DATA_STRUCT();
				mSendProp.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
				mSendProp.eDataType = DATA_TYPE.INT_16;
				mSendProp.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

				if (nStartAddr > -1 && nStartAddr < 65536) {
					if (nAddrLen <= 1024) {
						PlcRegCmnStcTools
								.setRegBytesData(mAddr, nLocalW, mSendProp);
					}
				}else {
					Log.e(TAG, "nStartAddr:" + nStartAddr + ",nAddrLen:"+ nAddrLen);
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			/* 清除掉电保存区 */
			PowerSave.getInstance().clearPmem();
		}
		
	}

	/**
	 * 掉电保存恢复函数
	 */
	private static void restoreSave(byte[] nSaveBuff) {
		// Log.d("SKScene", "restoreSave......");

		if (nSaveBuff == null || nSaveBuff.length < (nTotalSize - 8) * 1028)
			return;

		/* 数据采集还原的容器 */
		Vector<CollectRecordProp> mCollectRestorList = new Vector<CollectRecordProp>();

		/* 报警还原的容器 */
		Vector<AlamSaveProp> mAlamRestorList = new Vector<AlamSaveProp>();

		for (int i = 0; i < ((nTotalSize - 8) / 10); i++) {
			int nStartPos = i * 10240;
			while (nStartPos < (10240 * (i + 1) - 4)) {
				int nIndex = 0;

				/* 读出类型 */
				int nPowSaveType = nSaveBuff[nStartPos + nIndex++] & 0xff;

				nIndex += 4;

				/* 读出长度 */
				long nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
				long nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
				long nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
				long nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
				long nBytesLen = (nTmpByte3 << 24) + (nTmpByte2 << 16)
						+ (nTmpByte1 << 8) + nTmpByte0;

				/* 如果存储区不够存，则跳到下个存储区 */
				if (nBytesLen <= 0 || nStartPos + nBytesLen >= 10240) {
					break;
				}

				if (nPowSaveType == 1) {
					/* 数据采集还原 */
					CollectRecordProp mDataProp = SKSaveThread.getInstance().new CollectRecordProp();

					/* 读取组号 */
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nGroupId = (nTmpByte1 << 8) + nTmpByte0;

					/* 读出时间 */
					long nTmpByte7 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTmpByte6 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTmpByte5 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTmpByte4 = nSaveBuff[nStartPos + nIndex++] & 0xff;

					nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTime = (nTmpByte7 << 56) + (nTmpByte6 << 48)
							+ (nTmpByte5 << 40) + (nTmpByte4 << 32)
							+ (nTmpByte3 << 24) + (nTmpByte2 << 16)
							+ (nTmpByte1 << 8) + nTmpByte0;

					mDataProp.nGroupId = (short) nGroupId;
					mDataProp.nCurrTime = nTime;
					mDataProp.nValueList = new Vector<CollectSaveProp>();

					int nValueLen = (int) (nBytesLen - 19);
					while (nValueLen >= 4) {
						/* 读取地址号 */
						nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
						nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
						int nAddrId = (int) ((nTmpByte1 << 8) + nTmpByte0);

						/* 读取地址值的长度 */
						nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
						nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
						int nAddrValueLen = (int) ((nTmpByte1 << 8) + nTmpByte0);

						CollectSaveProp mValue = SKSaveThread.getInstance().new CollectSaveProp();
						mValue.nAddrId = (short) nAddrId;
						mValue.nAddrValueLen = (short) nAddrValueLen;

						if (nAddrValueLen > 0 && nAddrValueLen < 20) {
							mValue.nValueByteList = new byte[nAddrValueLen];
							for (int n = 0; n < nAddrValueLen; n++) {
								mValue.nValueByteList[n] = nSaveBuff[nStartPos
										+ nIndex++];
							}
						}

						nValueLen -= (2 + 2 + nAddrValueLen);

						mDataProp.nValueList.add(mValue);
					}

					mCollectRestorList.add(mDataProp);
				} else if (nPowSaveType == 2) {
					/* 报警还原 */
					AlamSaveProp aSaveProp = SKSaveThread.getInstance().new AlamSaveProp();

					/* 组号 */
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					int gid = (int) ((nTmpByte1 << 8) + nTmpByte0);

					/* 序号 */
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					int aid = (int) ((nTmpByte1 << 8) + nTmpByte0);

					/* 状态 */
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					int status = (int) ((nTmpByte1 << 8) + nTmpByte0);

					/* 报警时间 */
					long nTmpByte7 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTmpByte6 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTmpByte5 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long nTmpByte4 = nSaveBuff[nStartPos + nIndex++] & 0xff;

					nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long aTime = (nTmpByte7 << 56) + (nTmpByte6 << 48)
							+ (nTmpByte5 << 40) + (nTmpByte4 << 32)
							+ (nTmpByte3 << 24) + (nTmpByte2 << 16)
							+ (nTmpByte1 << 8) + nTmpByte0;

					/* 报警时间 */
					nTmpByte7 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte6 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte5 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte4 = nSaveBuff[nStartPos + nIndex++] & 0xff;

					nTmpByte3 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte2 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte1 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					nTmpByte0 = nSaveBuff[nStartPos + nIndex++] & 0xff;
					long cTime = (nTmpByte7 << 56) + (nTmpByte6 << 48)
							+ (nTmpByte5 << 40) + (nTmpByte4 << 32)
							+ (nTmpByte3 << 24) + (nTmpByte2 << 16)
							+ (nTmpByte1 << 8) + nTmpByte0;

					aSaveProp.nGroupId = (short) gid;
					aSaveProp.nIndex = (short) aid;
					aSaveProp.nStatus = (short) status;
					aSaveProp.nAlamTime = aTime;
					aSaveProp.nRemoveAlamTime = cTime;
					mAlamRestorList.add(aSaveProp);

					// Log.d("SKScene",
					// "gid:"+gid+",aid:"+aid+",atime:"+aTime+",ctime:"+cTime);

				}

				nStartPos += nBytesLen;
			} // end while
		} // end for

		// 恢复报警数据
		AlarmGroup.getInstance().restore(mAlamRestorList);

		/* 先按时间排序 */
		bubbleSort(mCollectRestorList);

		/* 然后还原数据采集的数据 */
		restoreDataCollect(mCollectRestorList);

	}

	/**
	 * 还原数据采集
	 * 
	 * @param mAllRestorList
	 */
	private static void restoreDataCollect(
			Vector<CollectRecordProp> mAllRestorList) {
		if (null == mAllRestorList || mAllRestorList.isEmpty())
			return;

		/**
		 * 获取历史采集组数
		 */
		int nCollectSize = CollectDataInfo.getInstance().getmHistoryInfoList().size();
		if (nCollectSize <= 0){
			return;
		}

		SKDataBaseInterface mCollectDbObj = SkGlobalData.getDataCollectDatabase();
		if (null == mCollectDbObj) {
			return;
		}

		int nRestorSize = mAllRestorList.size();

		//Log.d("AAAAAAA", "=============== nRestorSize="+nRestorSize+",nCollectSize="+nCollectSize);
		
		/* 添加所有记录的最后时间 */
		String sSqlStr = "";
		for (int i = 0; i < nCollectSize; i++) {
			int nAddrSize = CollectDataInfo.getInstance().getmHistoryInfoList()
					.get(i).getmCollectAddrList().size();
			ContentValues mTmpValues = new ContentValues();

			/* 初始化插入数据 */
			for (int j = 0; j < nAddrSize; j++) {
				mTmpValues.put("data" + j, "0");
			}

			sSqlStr = "select * from dataCollect" + i+ " order by id DESC limit 1";

			long nPmenTime=0;//掉电时最后一个数据的时间
			long nLastTime = (long) 0;
			Cursor cus = mCollectDbObj.getDatabaseBySql(sSqlStr, null);
			if (cus != null) {
				/* 初始化数据 */
				int nColumnSize = cus.getColumnCount();
				if (nColumnSize - 3 == nAddrSize) {
					/* 取数据库最后的数据作为原始数据 */
					if (cus.moveToNext()) {
						nLastTime = cus.getLong(cus.getColumnIndex("nMillis"));
						for (int j = 0; j < nAddrSize; j++) {
							String sValue = cus.getString(cus
									.getColumnIndex("data" + j));
							mTmpValues.put("data" + j, sValue);
						}
					}
				}
				cus.close();
			}

			//Log.d(TAG, "......nLastTime="+nLastTime);
			nPmenTime=nLastTime;
			
			/* 从记录中查找所有这组的数据，插入到数据库中 */
			for (int k = 0; k < nRestorSize; k++) {
				CollectRecordProp mCRprop = mAllRestorList.get(k);
				if (null == mCRprop){
					continue;
				}

				if (mCRprop.nCurrTime > nLastTime && mCRprop.nGroupId == i) {
					/* 插入时间 */
					mTmpValues.put("nMillis", mCRprop.nCurrTime);
					nPmenTime=mCRprop.nCurrTime;
					
					/* 插入数据 */
					if (mCRprop.nValueList != null) {
						int nValueSize = mCRprop.nValueList.size();
						for (int m = 0; m < nValueSize; m++) {
							String sValueStr = "";
							int nAddrId = mCRprop.nValueList.get(m).nAddrId;
							if (nAddrId < nAddrSize) {
								byte[] nList = mCRprop.nValueList.get(m).nValueByteList;
								if (nList != null && nList.length < 20) {
									sValueStr = new String(nList);
									mTmpValues.put("data" + nAddrId, sValueStr);
								}
							}
						}
					}
					mTmpValues.put("power", "0");
					mCollectDbObj.insertData("dataCollect" + i, mTmpValues);
				}
			}
			
			//补上两个数据点，一个是最后掉电，一个是恢复掉电，这样才能真实反映掉电时间
			if (nPmenTime>0) {
				Log.d(TAG, "nPmenTime="+nPmenTime);
				addData(i,nPmenTime,nAddrSize);//最后一点
				addData(i,System.currentTimeMillis(), nAddrSize);//恢复开始点
			}
			
		}
	}
	
	/**
	 * 掉电数据补上
	 * @param index-历史数据序号id
	 * @param time-时间
	 * @param size-
	 */
	private static void addData(int index,long time,int size){
		SKDataBaseInterface mCollectDbObj = SkGlobalData
				.getDataCollectDatabase();
		//Log.d("AAAAAAA", "index="+index+",time="+time+",size="+size);
		
		ContentValues mTmpValues = new ContentValues();
		mTmpValues.put("nMillis", time);
		for (int i = 0; i < size; i++) {
			mTmpValues.put("data"+i, 0);
		}
		mTmpValues.put("power", 1);
		
		mCollectDbObj.insertData("dataCollect" + index, mTmpValues);
	}

	/**
	 * 按从时间小到大排序
	 * 
	 * @param mAllRestorList
	 * @return
	 */
	private static boolean bubbleSort(
			Vector<CollectRecordProp> mCollectRestorList) {
		if (null == mCollectRestorList || mCollectRestorList.isEmpty())
			return false;

		CollectRecordProp addrHeadValue;
		CollectRecordProp addrCompareValue;
		CollectRecordProp tmpAddrValue;

		int size = mCollectRestorList.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				addrHeadValue = mCollectRestorList.get(i);
				addrCompareValue = mCollectRestorList.get(j);

				/* 如果对象为空，则挑过 */
				if (addrHeadValue == null || addrCompareValue == null) {
					continue;
				}

				if (addrHeadValue.nCurrTime > addrCompareValue.nCurrTime) {
					/* 值交换 */
					tmpAddrValue = addrHeadValue;
					addrHeadValue = addrCompareValue;
					addrCompareValue = tmpAddrValue;

					mCollectRestorList.set(i, addrHeadValue);
					mCollectRestorList.set(j, addrCompareValue);
				}
			}
		}

		return true;
	}
}
