package com.android.Samkoonhmi.macro;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Timer;
import java.util.Vector;
import com.android.Samkoonhmi.databaseinterface.GlobalMacroBiz;
import com.android.Samkoonhmi.databaseinterface.MParamBiz;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.GlobalMacroInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.util.MACRO_TYPE;


import android.util.Log;

/**
 * 全局宏指令
 * */
public class GlobalMacroItem extends BaseMacroItem{

	private long    PrevTimeMillis;            //上次执行的时间

	private boolean needInvoke      = false;  //判断当前是否需要调用宏指令

	private boolean mIsCtrlToRun   = false;   //是否受控执行

	private boolean mIsDummyNotify  = true;   //标定第一次通知消息为哑消息

	private int PreState = -1;				//记录上次状态
	
	private Timer   mInnerTimer;                //保存定时器引用
	
	private GlobalMacroInfo  mGMI;
	HashMap<String, PHolder> mPHMap = null;
	
	private boolean       mDebug = false;
	private String        mTag   = "GlobalMacroItem";

	protected int     addrdata   = 0;
	
	public GlobalMacroItem(short mid){
		super();  	

		getDataFromDatabase(mid);

		if(null == mGMI){//若数据实体类为空
			Log.e("GlobalMacroItem","GlobalMacroItem: mGMI is null");
			return;
		}

		setBMI(mGMI);
		if(MACRO_TYPE.GCTRLOOP == mGMI.getMacroType()){//若为受控宏指令
			SKPlcNoticThread.getInstance().addNoticProp(mGMI.getControlAddr(), notifyAddrDataCallback, false);  //绑定地址数据回调
		}
		
		
	}

	/**
	 * 从数据库读取宏指令信息
	 * */
	public void getDataFromDatabase(short mid) {		
		GlobalMacroBiz tmpMBiz = new GlobalMacroBiz();		
		mGMI = tmpMBiz.selectGlobalMacro(mid);
		if(null == mGMI){
			Log.e("GlobalMacroItem","getDataFromDatabase: mGMI is null!");
			return;
		}

		//初始化参数列表
		MParamBiz  tmpMPBiz = new MParamBiz();
		mParamList  = tmpMPBiz.selectMacroParamList(mGMI.getMacroLibName());
		if(null == mParamList){
			Log.e("GlobalMacroItem","getDataFromDatabase: mParamList is null!");
			return;
		}
		mPHMap = new HashMap<String, PHolder>();
		
		//参数设置
		ParamTool.setParam(mParamList, mPHMap, true,mGMI.getnSid());
	}

	/**
	 * 判断是否到了执行时间
	 * @param tcount  当前计数
	 * */
	public boolean isTimeToRun(){

		long tmpCurTimeMillis = android.os.SystemClock.uptimeMillis();//System.currentTimeMillis(); //获得当前时间

		if((tmpCurTimeMillis - PrevTimeMillis) >=  mGMI.getTimeInterval()){
			PrevTimeMillis = tmpCurTimeMillis;
			return true;		
		}
		return false;
	}

	/**
	 * 判断控制地址是否授权执行
	 * */
	public boolean isCtrlToRun(){
		return mIsCtrlToRun;
	}

	@Override
	public void run() {
		needInvoke = true;
		if((needInvoke == true)&&(mGMI.getMacroType() == MACRO_TYPE.GCTRLOOP)){//受控检测
			if(false == isCtrlToRun()){//受控不执行
				needInvoke = false;
			}
		}//End of: if((needInvoke == false)&&(MacroType == GCTRLOOP))

		//是否需要调用宏
		if(needInvoke){
			//每次都需同步地址控件的数据到本地缓存
			if(mDebug){
				Log.i(mTag,"run: try to call pullParams");
			}
			ParamHelper.pullParams(mParamList, mPHMap);
			//调用宏指令
			try {
				mMacroMethod.invoke(mJMLInstance, mPHMap);			
			} catch (IllegalArgumentException e) {
				Log.e("GlobalMacroItem","run:  Illegal Argument! MacroName: " + mGMI.getMacroName());			
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				Log.e("GlobalMacroItem","run:  Illegall Access! MacroName: " + mGMI.getMacroName());
				e.printStackTrace();
				return;
			} catch (InvocationTargetException e) {
				Log.e("GlobalMacroItem","run:  Invocation Target Exception! MacroName: " + mGMI.getMacroName());		
				e.printStackTrace();
				return;
			}
			//将参数推送到地址空间
			if(mDebug){
				Log.i(mTag,"run: try to call pushParams");
			}
			ParamHelper.pushParams(mParamList, mPHMap);					
		}//End of: if(needInvoke)
	}

	@Override
	public int execute(Timer timer) {
		if(null == timer){
			return -1;
		}

		if( null == mMacroMethod){
			return -2;
		}

		if(null == mGMI){
			return -3;
		}
		mInnerTimer = timer;
		int tv = mGMI.getTimeInterval();
		if(0 == tv){
			tv = 100;
		}

		//使用定时器立即调度，调度周期为执行时间间隔
		timer.schedule(this, 0, tv);
		return 0;
	}


	/**
	 * 处理地址数据通知回调
	 * */
	private Vector<Short> mCmpNoticeData = null; //存放通知数据
	public void handleAddrDataCallback(Vector<Byte> nStatusValue){
	//	if(false == mIsDummyNotify){//屏蔽第一次哑回调
			if(ADDRTYPE.BITADDR == mGMI.getControlAddrType()){       //位地址
				mIsCtrlToRun = false;
				int execCondition = mGMI.getExecCondition();
				switch(execCondition){
					case 0://off时执行
					case 1://on时执行
						if(execCondition == (0x01&nStatusValue.get(0))){
							mIsCtrlToRun = true;
						}else{
							mIsCtrlToRun = false;
						}
						break;
					case 2://off->on时执行
						if((1 == (0x01&nStatusValue.get(0)))&& (0 == PreState)){
							mIsCtrlToRun = true;
						}else{
							mIsCtrlToRun = false;
						}
						PreState=(0x01&nStatusValue.get(0));
						break;
					case 3://on->off时执行
						if((0 == (0x01&nStatusValue.get(0))) && (1 == PreState)){
							mIsCtrlToRun = true;
						}else{
							mIsCtrlToRun = false;
						}
						PreState=(0x01&nStatusValue.get(0));
						break;
					case 4://switch时执行
						if(((0x01&nStatusValue.get(0)) != PreState)&&(-1 != PreState)){
							mIsCtrlToRun = true;
						}else{
							mIsCtrlToRun = false;
						}
						PreState=(0x01&nStatusValue.get(0));
						break;
					default:
						mIsCtrlToRun = false;
						break;
				}			
			}else if(ADDRTYPE.WORDADDR == mGMI.getControlAddrType()){//字地址
				if (mCmpNoticeData == null)
				{
					mCmpNoticeData = new Vector<Short>();
				}else{
					mCmpNoticeData.clear();
				}

				boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCmpNoticeData);//将字节转换成短整型

				if(!mCmpNoticeData.isEmpty()){
					if(mCmpNoticeData.get(0) == mGMI.getCmpFactor()){//将地址中的数据和固定值进行比较
						mIsCtrlToRun = true;
					}else{
						mIsCtrlToRun = false;
					}
				}
			}
//		}else{
//			mIsDummyNotify = false;//回调函数第一次调用后不再是哑回调
//		}
	} 

	/**
	 * 地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleAddrDataCallback(nStatusValue);
		}
	};

	@Override
	public int cancle(Timer timer) {
		if(null == timer){
			return -1;
		}
		timer.cancel();
		if(MACRO_TYPE.GCTRLOOP == mGMI.getMacroType()){//若为受控宏指令
			SKPlcNoticThread.getInstance().destoryCallback(notifyAddrDataCallback);
		}
		return 0;
	}

	@Override
	public short getType() {
		return this.mGMI.getMacroType();
	}

	//	/**
	//	 * 模拟下位地址变化,模拟代码，发布时需要删除
	//	 * */
	//	public void changeAddrData(){
	//
	//		if(ADDRTYPE.BITADDR == mGMI.getControlAddrType()){//受位地址控制
	//			if(0 == addrdata){
	//				addrdata = 1;
	//			}else{
	//				addrdata = 0;
	//			}
	//
	//			Vector<Integer> dataList = new Vector<Integer>();
	//			dataList.add(addrdata); //保存新的数据
	//
	//			SEND_DATA_STRUCT mSendData  = new SEND_DATA_STRUCT();
	//			mSendData.eDataType         = DATA_TYPE.BIT_1;                   //数据类型为位
	//			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W; //本次为写操作
	//			PlcRegCmnStcTools.setRegIntData(mGMI.getControlAddr(), dataList, mSendData);
	//			
	//		}else if(ADDRTYPE.WORDADDR== mGMI.getControlAddrType()){//受字地址控制
	//
	//			if(0 == addrdata){
	//				addrdata = 15;
	//			}else if(15 == addrdata){
	//				addrdata = 20;
	//			}else if(20 == addrdata){
	//				addrdata = 25;
	//			}else if(25 == addrdata){
	//				addrdata = 30;
	//			}else if(30 == addrdata){
	//				addrdata = 35;
	//			}else if(35 == addrdata){
	//				addrdata = 15;
	//			}
	//		
	//			Vector<Integer> dataList = new Vector<Integer>();
	//			dataList.add(addrdata); //保存新的数据
	//
	//			SEND_DATA_STRUCT mSendData  = new SEND_DATA_STRUCT();
	//			mSendData.eDataType         = DATA_TYPE.INT_16;                  //数据类型为位
	//			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W; //本次为写操作
	//			PlcRegCmnStcTools.setRegIntData(mGMI.getControlAddr(), dataList, mSendData);
	//
	//		}//End of:if(1 == mIVInfo.getChangeCondition())
	//
	//	}

}//End of class
