package com.android.Samkoonhmi.model.alarm;

import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.skenum.ConditionType;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 报警条件和信息
 */
public class AlarmConditionInfo {

	//报警组序号
	private int nGroupId;
	//报警在组中的序号
	private int nAlarmIndex;
	//报警监视地址
	private AddrProp mAddress;
	//报警条件
	private ConditionType.CONDITION_TYPE eCondition;
	//数据类型
	private DATA_TYPE mDataType;
	//上限
	private double nRangLow;
	//下限
	private double nRangHigh;
	//跳转画面id
	private int nTargetPage;
	//类型，画面or窗口
	private short nSceneType;
	//是否打开画面or窗口
	private boolean bOpenScene;
	//报警已经触发了，除非该报警已经被确认 了，否则不再写入数据库
	//private boolean bTouchOff;
	//报警状态 -1-未发生报警， 0-报警产生，1-报警已经确定，2-报警已经消除
	private int nClear=-1;
	private CallbackItem mCallbackItem;
	
	//是否发送报警信息
	private boolean bSendMsg ;
	//接受报警信息的号码
	private String mPhoneNum;
	
	private boolean bAddtoDB; // 报警信息 是否保存数据库
	
	public void setbAddtoDB(boolean add){
		bAddtoDB = add;
	}
	
	public boolean getbAddtoDb(){
		return bAddtoDB;
	}
	
	public void setbSendMsg(boolean sendMsg){
		bSendMsg = sendMsg;
	}
	public boolean getbSendMsg(){
		return bSendMsg;
	}
	
	public void setmPhoneNum(String phonenum){
		mPhoneNum = phonenum;
	}
	public String getPhoneNum(){
		return mPhoneNum;
	}

	public CallbackItem getmCallbackItem() {
		return mCallbackItem;
	}
	public void setmCallbackItem(CallbackItem mCallbackItem) {
		this.mCallbackItem = mCallbackItem;
	}
	public int getnClear() {
		return nClear;
	}
	public void setnClear(int nClear) {
		this.nClear = nClear;
	}
	public DATA_TYPE getmDataType() {
		return mDataType;
	}
	public void setmDataType(DATA_TYPE mDataType) {
		this.mDataType = mDataType;
	}
	public double getnRangLow() {
		return nRangLow;
	}
	public void setnRangLow(double nRangLow) {
		this.nRangLow = nRangLow;
	}
	public double getnRangHigh() {
		return nRangHigh;
	}
	public void setnRangHigh(double nRangHigh) {
		this.nRangHigh = nRangHigh;
	}
	public int getnTargetPage() {
		return nTargetPage;
	}
	public void setnTargetPage(int nTargetPage) {
		this.nTargetPage = nTargetPage;
	}
	public short getnSceneType() {
		return nSceneType;
	}
	public void setnSceneType(short nSceneType) {
		this.nSceneType = nSceneType;
	}
	public boolean isbOpenScene() {
		return bOpenScene;
	}
	public void setbOpenScene(boolean bOpenScene) {
		this.bOpenScene = bOpenScene;
	}
	
//	public boolean isbTouchOff() {
//		return bTouchOff;
//	}
//	public void setbTouchOff(boolean bTouchOff) {
//		this.bTouchOff = bTouchOff;
//	}
	public int getnGroupId() {
		return nGroupId;
	}
	public void setnGroupId(int nGroupId) {
		this.nGroupId = nGroupId;
	}
	public int getnAlarmIndex() {
		return nAlarmIndex;
	}
	public void setnAlarmIndex(int nAlarmIndex) {
		this.nAlarmIndex = nAlarmIndex;
	}
	public AddrProp getmAddress() {
		return mAddress;
	}
	public void setmAddress(AddrProp mAddress) {
		this.mAddress = mAddress;
	}
	public ConditionType.CONDITION_TYPE geteCondition() {
		return eCondition;
	}
	public void seteCondition(ConditionType.CONDITION_TYPE eCondition) {
		this.eCondition = eCondition;
	}
}
