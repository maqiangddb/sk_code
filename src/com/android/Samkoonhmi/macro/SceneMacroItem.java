package com.android.Samkoonhmi.macro;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Timer;
import java.util.Vector;
import com.android.Samkoonhmi.databaseinterface.MParamBiz;
import com.android.Samkoonhmi.databaseinterface.SceneMacroBiz;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.SceneMacroInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.util.MACRO_TYPE;

import android.util.Log;

/**
 * 场景宏指令项
 * */
public class SceneMacroItem extends BaseMacroItem{
	private boolean       mDebug = false;
	private String        mTag   = "SceneMacroItem";
	
	HashMap<String, PHolder> mPHMap = null;
	
	private long     PrevTimeMillis;            //上次执行的时间

	private boolean  needInvoke = true;  	    //判断当前是否需要调用宏指令

	private boolean  mIsCtrlToRun    = false;   //是否受控执行

	private boolean  mIsDummyNotify  = true;    //标定第一次通知消息为哑消息

	private SceneMacroInfo   mSMI;              //场景宏数据实体类   

	private int     mRCount;                    //运行次数计数器

	private Timer   mInnerTimer;                //保存定时器引用
	
	private int PreState = -1;				//记录上次状态

	protected int addrdata = 0; //测试代码，发布时需删除
	
	public SceneMacroItem(short mid){
		super();  	

		getDataFromDatabase(mid);

		if(null == mSMI){//数据实体类为空
			Log.e("SceneMacroItem","SceneMacroItem: mSMI is null");
			return;
		}

		setBMI(mSMI);

		if(MACRO_TYPE.SCTRLOOP == mSMI.getMacroType()){//若为受控宏指令
			SKPlcNoticThread.getInstance().addNoticProp(mSMI.getControlAddr(), notifyAddrDataCallback, false);  //绑定地址数据回调
		}
	}

	/**
	 * 从数据库读取宏指令信息
	 * */
	public void getDataFromDatabase(short mid) {
		SceneMacroBiz tmpMBiz = new SceneMacroBiz();
		mSMI = tmpMBiz.selectSceneMacro(mid);
		if(null == mSMI){
			Log.e("SceneMacroItem","getDataFromDatabase: mSMI null!");
			return;
		}

		//初始化参数列表
		MParamBiz  tmpMPBiz = new MParamBiz();
		mParamList  = tmpMPBiz.selectMacroParamList(mSMI.getMacroLibName());
		if(null == mParamList){
			Log.e("getDataFromDatabase","getDataFromDatabase: mParamList is null!");
			return;
		}
		
		mPHMap = new HashMap<String, PHolder>();
		
		//参数设置
		ParamTool.setParam(mParamList, mPHMap, true,mSMI.getnSid());
	}

	/**
	 * 获得数据实体类
	 * @return 
	 * */
	public SceneMacroInfo getSMI(){
		return mSMI;
	}

	/**
	 * 判断是否到了执行时间
	 * @param tcount  当前计数
	 * */
	public boolean isTimeToRun(){
		long tmpCurTimeMillis = android.os.SystemClock.uptimeMillis();//System.currentTimeMillis(); //获得当前时间
		if((tmpCurTimeMillis - PrevTimeMillis) >= mSMI.getTimeInterval()){
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
		if((needInvoke == true)&&(mSMI.getMacroType() == MACRO_TYPE.SCTRLOOP)){//受控检测
			if(false == isCtrlToRun()){//受控不执行
				needInvoke = false;
			}
		}//End of: if((needInvoke == false)&&(MacroType == GCTRLOOP))

		if((needInvoke == true) && (0 < mSMI.getRunCount()) && (0 >= this.mRCount)){
			needInvoke = false;
		}else if((needInvoke == true) && (0 < mSMI.getRunCount()) && (0 < this.mRCount)){
			mRCount--;
		}

		//是否需要调用宏
		if(needInvoke){
			if(mDebug){
				Log.i(mTag,"run: try to call pullParams");
			}
			//每次都需同步地址控件的数据到本地缓存
			ParamHelper.pullParams(mParamList, mPHMap);	    		
			//调用宏指令
			try {
				mMacroMethod.invoke(mJMLInstance, mPHMap);			
			} catch (IllegalArgumentException e) {
				Log.e("SceneMacroItem","run:  Illegal Argument! MacroName: " + mSMI.getMacroName());			
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				Log.e("SceneMacroItem","run:  Illegall Access! MacroName: " + mSMI.getMacroName());
				e.printStackTrace();
				return;
			} catch (InvocationTargetException e) {
				Log.e("SceneMacroItem","run:  Invocation Target Exception! MacroName: " + mSMI.getMacroName());		
				e.printStackTrace();
				return;
			}
			if(mDebug){
				Log.i(mTag,"run: try to call pushParams");
			}
			//将参数推送到地址空间
			ParamHelper.pushParams(mParamList, mPHMap);	

		}//End of: if(needInvoke)

	}

	@Override
	public int execute(Timer timer) {
		int timev = 0;
		if(null == timer){
			return -1;
		}
		mInnerTimer = timer;  //保存定时器引用
		if( null == mMacroMethod){
			return -2;
		}

		if(null == mSMI){
			return -3;
		}

		mRCount = mSMI.getRunCount();//重新调度时，初始化调度次数
		if(0 == mSMI.getTimeInterval()){
			timev = 100;
		}else{
			timev = mSMI.getTimeInterval();
		}
		timer.schedule(this, 0, timev);
		return 0;
	}

	/**
	 * 处理地址数据通知回调
	 * */
	private Vector<Short> mCmpNoticeData = null; //存放通知数据
	public void handleAddrDataCallback(Vector<Byte> nStatusValue){
		if(ADDRTYPE.BITADDR == mSMI.getControlAddrType()){       //位地址
			int execCondition = mSMI.getExecCondition();
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
		}else if(ADDRTYPE.WORDADDR == mSMI.getControlAddrType()){//字地址
			if (mCmpNoticeData == null)
			{
				mCmpNoticeData = new Vector<Short>();
			}else{
				mCmpNoticeData.clear();
			}

			boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCmpNoticeData);//将字节转换成短整型

			if(!mCmpNoticeData.isEmpty()){
				if(mCmpNoticeData.get(0) == mSMI.getCmpFactor()){//将地址中的数据和固定值进行比较
					mIsCtrlToRun = true;
				}else{
					mIsCtrlToRun = false;
				}
			}		
		}
		
//		if(mIsCtrlToRun){
//			mRCount = mSMI.getRunCount();
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
		if(MACRO_TYPE.SCTRLOOP == mSMI.getMacroType()){//若为受控宏指令
			SKPlcNoticThread.getInstance().destoryCallback(notifyAddrDataCallback);
		}
		return 0;
	}

	@Override
	public short getType() {
		return this.mSMI.getMacroType();
	}

	//	/**
	//	 * 模拟下位地址变化,模拟代码，发布时需要删除
	//	 * */
	//	public void changeAddrData(){
	//
	//		if(ADDRTYPE.BITADDR == mSMI.getControlAddrType()){//受位地址控制
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
	//			PlcRegCmnStcTools.setRegIntData(mSMI.getControlAddr(), dataList, mSendData);
	//			
	//		}else if(ADDRTYPE.WORDADDR== mSMI.getControlAddrType()){//受字地址控制
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
	//			PlcRegCmnStcTools.setRegIntData(mSMI.getControlAddr(), dataList, mSendData);
	//
	//		}//End of:if(1 == mIVInfo.getChangeCondition())
	//
	//	}

}//End of class
