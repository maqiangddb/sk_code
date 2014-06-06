package com.android.Samkoonhmi.databaseinterface;

import android.database.Cursor;

import com.android.Samkoonhmi.model.skglobalcmn.DataTransInfo;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;

public class DataTransBiz {
	public static boolean select()
	{
		/*打开数据*/
		boolean bSuccess = SkGlobalData.getProjectDatabase().openDatabase(null);
		if(false == bSuccess) return false;
		
		/*按照组号的升序查询*/
		Cursor dataProp = SkGlobalData.getProjectDatabase().getDatabaseBySql("select * from dataTrans order by nGroupId ", null);
		if(null == dataProp) return true;
		
		int nTmpValue = 0;
		DataTransInfo.OneDataInfo mOneData = null;
		AddrProp mTmpAddr = null;
		DataTransInfo.getInstance().getmDataTransList().clear();
		while(dataProp.moveToNext())
		{
			mOneData = DataTransInfo.getInstance().new OneDataInfo();
			
			/*资料传输组的ID*/
			mOneData.setnGroupId(dataProp.getShort(dataProp.getColumnIndex("nGroupId")));
			
			/*资料传输的方式*/
			mOneData.setnTransType(dataProp.getShort(dataProp.getColumnIndex("nTransType")));
			
			/*资料传输的周期时间间隔*/
			mOneData.setnInterval(dataProp.getShort(dataProp.getColumnIndex("nInterval")));
			
			/*资料传输的触发地址*/
			nTmpValue = dataProp.getInt(dataProp.getColumnIndex("nTriggerAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpValue);
			mOneData.setmTriggerAddr(mTmpAddr);
			
			/*资料传输的触发地址是否自动复位*/
			nTmpValue = dataProp.getShort(dataProp.getColumnIndex("bAutoReset"));
			if(nTmpValue == 1)
			{
				mOneData.setbAutoReset(true);
			}
			else
			{
				mOneData.setbAutoReset(false);
			}
			
			/*资料传输的地址类型*/
			mOneData.setnAddrType(dataProp.getShort(dataProp.getColumnIndex("nAddrType")));
			
			/*资料传输的长度*/
			mOneData.setnTransLen(dataProp.getShort(dataProp.getColumnIndex("nTransLen")));
			
			/*资料传输的源地址*/
			nTmpValue = dataProp.getInt(dataProp.getColumnIndex("nSourceAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpValue);
			mOneData.setmSourceAddr(mTmpAddr);
			
			/*资料传输的目标地址*/
			nTmpValue = dataProp.getInt(dataProp.getColumnIndex("nTargetAddrId"));
			mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpValue);
			mOneData.setmTargetAddr(mTmpAddr);
			
			/*资料传输的长度是否变量*/
			String bDynLength = dataProp.getString(dataProp.getColumnIndex("bDynLength"));
			if("true".equals(bDynLength)){
				mOneData.setbDynLength(true);
			}else{
				mOneData.setbDynLength(false);
			}
			
			/*资料传输的长度是变量时的地址*/
			if(mOneData.getbDynLength()){
				nTmpValue = dataProp.getInt(dataProp.getColumnIndex("nLengthAddrId"));
				mTmpAddr = SkGlobalData.getProjectDatabase().getAddrById(nTmpValue);
				mOneData.setmLengthAddr(mTmpAddr);
			}
			
			DataTransInfo.getInstance().getmDataTransList().add(mOneData);
		}
		dataProp.close();
			
		return true;	
	}
}
