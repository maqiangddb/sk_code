package com.android.Samkoonhmi.model.skglobalcmn;

import java.util.Vector;

import com.android.Samkoonhmi.util.AddrProp;

public class DataTransInfo {
	
	private DataTransInfo()
	{
	}
	
	/*获取数据采集的单例*/
	private static DataTransInfo mDataTransInfo = null;
	public synchronized static DataTransInfo getInstance()
	{
		if(null == mDataTransInfo)
		{
			mDataTransInfo = new DataTransInfo();
		}

		return mDataTransInfo;
	}

	/*资料传输列表里面的所有组数*/
	private Vector<OneDataInfo > mDataTransList = new Vector<OneDataInfo >();
	
	public Vector<OneDataInfo> getmDataTransList() {
		return mDataTransList;
	}

	public void setmDataTransList(Vector<OneDataInfo> mDataTransList) {
		this.mDataTransList = mDataTransList;
	}

	/**
	 * 资料传输的一组资料数据属性类
	 * @author latory
	 *
	 */
	public class OneDataInfo
	{
		/*资料传输的组号*/
		private short nGroupId = 0;
		
		/*传输方式, 1为周期定时，2为触发*/
		private short nTransType = 0;
		
		/*周期传输间隔，单位为秒*/
		private short nInterval = 60;
		
		/*是否自动复位*/
		private boolean bAutoReset = true;
		
		/*传输的地址类型, 1:位， 2：字， 3：双字*/
		private short nAddrType = 0;
		
		/*传输的长度 1-128*/
		private short nTransLen = 0;
		
		/*触发地址*/
		private AddrProp mTriggerAddr = null;
		
		/*源地址*/
		private AddrProp mSourceAddr = null;
		
		/*目标地址*/
		private AddrProp mTargetAddr = null;

		public short getnGroupId() {
			return nGroupId;
		}

		public void setnGroupId(short nGroupId) {
			this.nGroupId = nGroupId;
		}

		public short getnTransType() {
			return nTransType;
		}

		public void setnTransType(short nTransType) {
			this.nTransType = nTransType;
		}

		public short getnInterval() {
			return nInterval;
		}

		public void setnInterval(short nInterval) {
			this.nInterval = nInterval;
		}

		public boolean isbAutoReset() {
			return bAutoReset;
		}

		public void setbAutoReset(boolean bAutoReset) {
			this.bAutoReset = bAutoReset;
		}

		public short getnAddrType() {
			return nAddrType;
		}

		public void setnAddrType(short nAddrType) {
			this.nAddrType = nAddrType;
		}

		public short getnTransLen() {
			return nTransLen;
		}

		public void setnTransLen(short nTransLen) {
			this.nTransLen = nTransLen;
		}

		public AddrProp getmTriggerAddr() {
			return mTriggerAddr;
		}

		public void setmTriggerAddr(AddrProp mTriggerAddr) {
			this.mTriggerAddr = mTriggerAddr;
		}

		public AddrProp getmSourceAddr() {
			return mSourceAddr;
		}

		public void setmSourceAddr(AddrProp mSourceAddr) {
			this.mSourceAddr = mSourceAddr;
		}

		public AddrProp getmTargetAddr() {
			return mTargetAddr;
		}

		public void setmTargetAddr(AddrProp mTargetAddr) {
			this.mTargetAddr = mTargetAddr;
		}
	}
}
