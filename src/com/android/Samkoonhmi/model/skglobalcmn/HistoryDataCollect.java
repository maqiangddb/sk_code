package com.android.Samkoonhmi.model.skglobalcmn;

import java.util.ArrayList;
import java.util.Vector;

import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.FULL_DEAL_TYPE;
import com.android.Samkoonhmi.skenum.SAMP_TYPE;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.util.AddrProp;

public class HistoryDataCollect {

	/*采集的组号*/
	private short nGroupId ;

	/*采集组的名字*/
	private String sName = "";

	/*采样条件（采样方式）*/
	private SAMP_TYPE eSampType = SAMP_TYPE.OTHER_SAMP_TYPE;

	/*采样保存时间*/
	private int nTotalSampNum ;

	/*采样取满处理方式*/
	private FULL_DEAL_TYPE eDealSampFull = FULL_DEAL_TYPE.FULL_OTHER_DEAL_TYPE;

	/*是否取满通知*/
	private boolean bFullNotic;

	/*地址通知的*/
	private AddrProp nNoticAddrId = new AddrProp();

	/*位地址控制是否采样*/
	private boolean bAddrCtlSamp ;

	/*控制采样的地址*/
	private AddrProp nCtlSampAddrId = new AddrProp();

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
	private boolean bAutoReset = false;

	/*是否保存文件*/
	private boolean bSaveToFile ;

	/*保存文件的控制地址*/
	private AddrProp nCtlSaveAddrId = new AddrProp();
	
	/*是否自动保存*/
	private boolean bAutoSave = false; 

	/*自动保存间隔时间*/
	private int nIntervalTime = 8;
	
	/*保存的媒介*/
	private STORAGE_MEDIA eSaveMedium = STORAGE_MEDIA.OTHER_STORAGE_MEDIA;

	/*日期格式*/
	private DATE_FORMAT eDateShowType = DATE_FORMAT.YYYYMMDD_ACROSS;

	/*采集的源地址集合*/
	private Vector<CollectAddrProp > mCollectAddrList = new Vector<CollectAddrProp >();
	
	/*最后一次控制地址的值*/
	private boolean bOnValue = false;
	
	/*自动保存的当前时间*/
	private long nLastTime = 0;
	
	/*上升沿采集标识*/
	private boolean bCollect;
	
	/**
	 * 监视地址变化回调
	 */
	private ArrayList<CallbackItem> mCallbackItems=new ArrayList<CallbackItem>();
	
	// 需要重新注册
	private boolean bResetCallback;

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

	public int getnTotalSampNum() {
		return nTotalSampNum;
	}

	public void setnTotalSampNum(int nTotalSampNum) {
		this.nTotalSampNum = nTotalSampNum;
	}

	public FULL_DEAL_TYPE geteDealSampFull() {
		return eDealSampFull;
	}

	public void seteDealSampFull(FULL_DEAL_TYPE eDealSampFull) {
		this.eDealSampFull = eDealSampFull;
	}

	public boolean isbFullNotic() {
		return bFullNotic;
	}

	public void setbFullNotic(boolean bFullNotic) {
		this.bFullNotic = bFullNotic;
	}

	public AddrProp getnNoticAddrId() {
		return nNoticAddrId;
	}

	public void setnNoticAddrId(AddrProp nNoticAddrId) {
		this.nNoticAddrId = nNoticAddrId;
	}

	public boolean isbAddrCtlSamp() {
		return bAddrCtlSamp;
	}

	public void setbAddrCtlSamp(boolean bAddrCtlSamp) {
		this.bAddrCtlSamp = bAddrCtlSamp;
	}

	public AddrProp getnCtlSampAddrId() {
		return nCtlSampAddrId;
	}

	public void setnCtlSampAddrId(AddrProp nCtlSampAddrId) {
		this.nCtlSampAddrId = nCtlSampAddrId;
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

	public boolean isbSaveToFile() {
		return bSaveToFile;
	}

	public void setbSaveToFile(boolean bSaveToFile) {
		this.bSaveToFile = bSaveToFile;
	}

	public AddrProp getnCtlSaveAddrId() {
		return nCtlSaveAddrId;
	}

	public void setnCtlSaveAddrId(AddrProp nCtlSaveAddrId) {
		this.nCtlSaveAddrId = nCtlSaveAddrId;
	}

	public boolean isbAutoSave() {
		return bAutoSave;
	}

	public void setbAutoSave(boolean bAutoSave) {
		this.bAutoSave = bAutoSave;
	}

	public int getnIntervalTime() {
		return nIntervalTime;
	}

	public void setnIntervalTime(int nIntervalTime) {
		this.nIntervalTime = nIntervalTime;
	}

	public STORAGE_MEDIA geteSaveMedium() {
		return eSaveMedium;
	}

	public void seteSaveMedium(STORAGE_MEDIA eSaveMedium) {
		this.eSaveMedium = eSaveMedium;
	}

	public DATE_FORMAT geteDateShowType() {
		return eDateShowType;
	}

	public void seteDateShowType(DATE_FORMAT eDateShowType) {
		this.eDateShowType = eDateShowType;
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

	public long getnLastTime() {
		return nLastTime;
	}

	public void setnLastTime(long nLastTime) {
		this.nLastTime = nLastTime;
	}
	
	public boolean isbCollect() {
		return bCollect;
	}

	public void setbCollect(boolean bCollect) {
		this.bCollect = bCollect;
	}
}
