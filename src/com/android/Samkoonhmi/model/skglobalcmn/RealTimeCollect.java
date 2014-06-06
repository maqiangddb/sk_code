package com.android.Samkoonhmi.model.skglobalcmn;

import java.util.ArrayList;
import java.util.Vector;

import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.skenum.SAMP_TYPE;
import com.android.Samkoonhmi.util.AddrProp;


/**
 * 实时数据采集
 * @author latory
 *
 */
public class RealTimeCollect {

	/*采集的组号*/
	private short nGroupId ;

	/*采集组的名字*/
	private String sName = "";

	/*采样条件（采样方式）*/
	private SAMP_TYPE eSampType = SAMP_TYPE.OTHER_SAMP_TYPE;

	/*位地址控制是否采样*/
	private boolean bAddrCtlSamp ;

	/*控制采样的地址*/
	private AddrProp nCtlSampAddr = null;

	/*开始的时（0-23）*/
	private short nStartHour ;

	/*开始的分（0-60）*/
	private short nStartMinute ;

	/*结束的时*/
	private short nEndHour ;

	/*结束的分*/
	private short nEndMinute ;
	
	/*是否地址控制时间范围*/
	private boolean bAddrCtlTime;
	
	/*开始时间地址*/
	private AddrProp mStartTimeAddr = null;

	/*结束时间地址*/
	private AddrProp mEndTimeAddr = null;
	
	/*是否地址控制采样频率*/
	private boolean bAddrCtlRate;

	/*采样的频率*/
	private int nSampRate ;

	/*控制采样频率的地址*/
	private AddrProp mCtlRateAddr = null;

	/*是否自动复位*/
	private boolean bAutoReset ;

	/*采集的源地址集合*/
	private Vector<CollectAddrProp > mCollectAddrList = new Vector<CollectAddrProp >();
	
	/*最后一次控制地址的值*/
	private boolean bOnValue = false;
	
	/*上升沿采集标识,需要采集的次数*/
	private int nCollectCount;
	
	/**
	 * 监视地址变化回调
	 */
	private ArrayList<CallbackItem> mCallbackItems=new ArrayList<CallbackItem>();
	
	//受控地址注册
	private CallbackItem mCtlSampItem;

	public CallbackItem getmCtlSampItem() {
		return mCtlSampItem;
	}

	public void setmCtlSampItem(CallbackItem mCtlSampItem) {
		this.mCtlSampItem = mCtlSampItem;
	}

	//需要重新注册
	private boolean bResetCallback;
	
	public int getnCollectCount() {
		return nCollectCount;
	}

	public void setnCollectCount(int nCollectCount) {
		this.nCollectCount = nCollectCount;
	}
	
	public boolean isbResetCallback() {
		return bResetCallback;
	}

	public void setbResetCallback(boolean bResetCallback) {
		this.bResetCallback = bResetCallback;
	}

	public ArrayList<CallbackItem> getmCallbackItems() {
		return mCallbackItems;
	}

	public void setmCallbackItems(ArrayList<CallbackItem> mCallbackItems) {
		this.mCallbackItems = mCallbackItems;
	}

	public short getnGroupId() {
		return nGroupId;
	}

	public void setnGroupId(short nGroupId) {
		this.nGroupId = nGroupId;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public SAMP_TYPE geteSampType() {
		return eSampType;
	}

	public void seteSampType(SAMP_TYPE eSampType) {
		this.eSampType = eSampType;
	}

	public boolean isbAddrCtlSamp() {
		return bAddrCtlSamp;
	}

	public void setbAddrCtlSamp(boolean bAddrCtlSamp) {
		this.bAddrCtlSamp = bAddrCtlSamp;
	}

	public AddrProp getnCtlSampAddr() {
		return nCtlSampAddr;
	}

	public void setnCtlSampAddr(AddrProp nCtlSampAddr) {
		this.nCtlSampAddr = nCtlSampAddr;
	}

	public short getnStartHour() {
		return nStartHour;
	}

	public void setnStartHour(short nStartHour) {
		this.nStartHour = nStartHour;
	}

	public short getnStartMinute() {
		return nStartMinute;
	}

	public void setnStartMinute(short nStartMinute) {
		this.nStartMinute = nStartMinute;
	}

	public short getnEndHour() {
		return nEndHour;
	}

	public void setnEndHour(short nEndHour) {
		this.nEndHour = nEndHour;
	}

	public short getnEndMinute() {
		return nEndMinute;
	}

	public void setnEndMinute(short nEndMinute) {
		this.nEndMinute = nEndMinute;
	}

	public boolean isbAddrCtlTime() {
		return bAddrCtlTime;
	}

	public void setbAddrCtlTime(boolean bAddrCtlTime) {
		this.bAddrCtlTime = bAddrCtlTime;
	}

	public AddrProp getmStartTimeAddr() {
		return mStartTimeAddr;
	}

	public void setmStartTimeAddr(AddrProp mStartTimeAddr) {
		this.mStartTimeAddr = mStartTimeAddr;
	}

	public AddrProp getmEndTimeAddr() {
		return mEndTimeAddr;
	}

	public void setmEndTimeAddr(AddrProp mEndTimeAddr) {
		this.mEndTimeAddr = mEndTimeAddr;
	}

	public boolean isbAddrCtlRate() {
		return bAddrCtlRate;
	}

	public void setbAddrCtlRate(boolean bAddrCtlRate) {
		this.bAddrCtlRate = bAddrCtlRate;
	}

	public int getnSampRate() {
		return nSampRate;
	}

	public void setnSampRate(int nSampRate) {
		this.nSampRate = nSampRate;
	}

	public AddrProp getmCtlRateAddr() {
		return mCtlRateAddr;
	}

	public void setmCtlRateAddr(AddrProp mCtlRateAddr) {
		this.mCtlRateAddr = mCtlRateAddr;
	}

	public boolean isbAutoReset() {
		return bAutoReset;
	}

	public void setbAutoReset(boolean bAutoReset) {
		this.bAutoReset = bAutoReset;
	}

	public Vector<CollectAddrProp> getmCollectAddrList() {
		return mCollectAddrList;
	}

	public void setmCollectAddrList(Vector<CollectAddrProp> mCollectAddrList) {
		this.mCollectAddrList = mCollectAddrList;
	}
	
	public boolean isbOnValue() {
		return bOnValue;
	}

	public void setbOnValue(boolean bOnValue) {
		this.bOnValue = bOnValue;
	}
}
