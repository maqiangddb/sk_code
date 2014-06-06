package com.android.Samkoonhmi.macro;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import com.android.Samkoonhmi.databaseinterface.MParamBiz;
import com.android.Samkoonhmi.databaseinterface.SceneMacroBiz;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.MParamInfo;
import com.android.Samkoonhmi.model.SceneMacroInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.util.MACRO_TYPE;
import android.util.Log;

/**
 * 场景宏指令项
 * */
public class SceneMacroItem extends BaseMacroItem {
	private boolean mDebug = false;
	private String mTag = "SceneMacroItem";
	HashMap<String, PHolder> mPHMap = null;
	private long PrevTimeMillis; // 上次执行的时间
	private boolean needInvoke = true; // 判断当前是否需要调用宏指令
	private boolean mIsCtrlToRun = false; // 是否受控执行
	private SceneMacroInfo mSMI; // 场景宏数据实体类
	private int mRCount; // 运行次数计数器
	private Timer mInnerTimer; // 保存定时器引用
	private int PreState = -1; // 记录上次状态
	protected int addrdata = 0; // 测试代码，发布时需删除
	public int sid = 0;

	private TimerTask timerTask = null;

	public SceneMacroItem(short mid) {
		super();

		getDataFromDatabase(mid);

		if (null == mSMI) {// 数据实体类为空
			Log.e("SceneMacroItem", "SceneMacroItem: mSMI is null");
			return;
		}

		setBMI(mSMI);

		if (MACRO_TYPE.SCTRLOOP == mSMI.getMacroType()) {// 若为受控宏指令
			SKPlcNoticThread.getInstance().addNoticProp(mSMI.getControlAddr(),
					notifyAddrDataCallback, false, mSMI.getnSid()); // 绑定地址数据回调
		}
	}

	/**
	 * 从数据库读取宏指令信息
	 * */
	public void getDataFromDatabase(short mid) {
		SceneMacroBiz tmpMBiz = new SceneMacroBiz();
		mSMI = tmpMBiz.selectSceneMacro(mid);
		if (null == mSMI) {
			Log.e("SceneMacroItem", "getDataFromDatabase: mSMI null!");
			return;
		}

		// 初始化参数列表
		MParamBiz tmpMPBiz = new MParamBiz();
		mParamList = tmpMPBiz.selectMacroParamList(mSMI.getMacroLibName());
		if (null == mParamList) {
			Log.e("getDataFromDatabase",
					"getDataFromDatabase: mParamList is null!");
			return;
		}

		mPHMap = new HashMap<String, PHolder>();

		// 参数设置
		sid = mSMI.getnSid();
		ParamTool.setParam(mParamList, mPHMap, true, sid);
		// 添加到参数列表
		//ParamHelper.addAddrList(mParamList);
		//Log.d(mTag, "getDataFromDatabase ====== "+sid);
	}
	
	public void reset(){
		//Log.d(mTag, "reset ====== "+sid);
		if(mParamList!=null){
			for (int i = 0; i < mParamList.size(); i++) {
				MParamInfo info=mParamList.get(i);
				if(info!=null){
					if (info.getmCallItem()!=null) {
						info.getmCallItem().setCallback(false);
					}
				}
			}
		}
	}
	
	/**
	 * 获得定时器
	 */
	public Timer getTimer() {
		return mInnerTimer;
	}

	/**
	 * 获得数据实体类
	 * 
	 * @return
	 * */
	public SceneMacroInfo getSMI() {
		return mSMI;
	}

	/**
	 * 判断是否到了执行时间
	 * 
	 * @param tcount
	 *            当前计数
	 * */
	public boolean isTimeToRun() {
		long tmpCurTimeMillis = android.os.SystemClock.uptimeMillis();// System.currentTimeMillis();
																		// //获得当前时间
		if ((tmpCurTimeMillis - PrevTimeMillis) >= mSMI.getTimeInterval()) {
			PrevTimeMillis = tmpCurTimeMillis;
			return true;
		}
		return false;
	}

	/**
	 * 判断控制地址是否授权执行
	 * */
	public boolean isCtrlToRun() {
		return mIsCtrlToRun;
	}

	//宏指令是否处于运行状态，为了防止卡死，屏蔽掉未能处理的消息
	private boolean bMacroRunning=false;
	@Override
	public synchronized void run() {

		if (bMacroRunning) {
			Log.e(mTag, "SceneMacroItem, Failure to deal with run() .... ");
			return;
		}
		bMacroRunning=true;
		
		
		needInvoke = true;
		if ((needInvoke == true)
				&& (mSMI.getMacroType() == MACRO_TYPE.SCTRLOOP)) {// 受控检测
			if (false == isCtrlToRun()) {// 受控不执行
				needInvoke = false;
			}
		}// End of: if((needInvoke == false)&&(MacroType == GCTRLOOP))

		if ((needInvoke == true) && (0 < mSMI.getRunCount())
				&& (0 >= this.mRCount)) {
			needInvoke = false;
		} else if ((needInvoke == true) && (0 < mSMI.getRunCount())
				&& (0 < this.mRCount)) {
			mRCount--;
		}

		// 是否需要调用宏
		if (needInvoke) {
			// 每次都需同步地址控件的数据到本地缓存
			//Log.d(mTag, "page_turn >>>>>>>>>>"+mSMI.getMacroLibName());
			ParamHelper.pullParams(mParamList, mPHMap);
			// 调用宏指令
			try {
				mMacroMethod.invoke(mJMLInstance, mPHMap);
			} catch (Exception e) {
				Log.e("SceneMacroItem", "run()! MacroName: "
						+ mSMI.getMacroName());
				e.printStackTrace();
				bMacroRunning=false;
			}
			// 将参数推送到地址空间
			// ParamHelper.pushParams(mParamList, mPHMap);

		}
		bMacroRunning=false;

	}

	/**
	 * 重新执行场景宏
	 * 
	 * @param timer
	 * @return
	 */
	public int reExecute(Timer timer) {

		int timev = 0;
		if (null == timer) {
			return -1;
		}

		if (null == mMacroMethod) {
			return -2;
		}

		if (null == mSMI) {
			return -3;
		}

		mInnerTimer = timer; // 保存定时器引用

		if (0 == mSMI.getTimeInterval()) {
			timev = 100;
		} else {
			timev = mSMI.getTimeInterval();
		}

		timerTask = new TimerTask() {
			@Override
			public void run() {
				SceneMacroItem.this.run();
			}
		};

		timer.schedule(timerTask, 0, timev);

		return 0;

	}

	@Override
	public int execute(Timer timer) {
		int timev = 0;
		if (null == timer) {
			return -1;
		}
		mInnerTimer = timer; // 保存定时器引用
		if (null == mMacroMethod) {
			return -2;
		}

		if (null == mSMI) {
			return -3;
		}

		mRCount = mSMI.getRunCount();// 重新调度时，初始化调度次数
		if (0 == mSMI.getTimeInterval()) {
			timev = 100;
		} else {
			timev = mSMI.getTimeInterval();
		}

		timerTask = new TimerTask() {
			@Override
			public void run() {
				SceneMacroItem.this.run();
			}
		};

		timer.schedule(timerTask, 0, timev);

		return 0;
	}

	/**
	 * 处理地址数据通知回调
	 * */
	private Vector<Short> mCmpNoticeData = null; // 存放通知数据

	public void handleAddrDataCallback(Vector<Byte> nStatusValue) {
		if (ADDRTYPE.BITADDR == mSMI.getControlAddrType()) { // 位地址
			int execCondition = mSMI.getExecCondition();
			switch (execCondition) {
			case 0:// off时执行
			case 1:// on时执行
				if (execCondition == (0x01 & nStatusValue.get(0))) {
					mIsCtrlToRun = true;
				} else {
					mIsCtrlToRun = false;
				}
				break;
			case 2:// off->on时执行
				if ((1 == (0x01 & nStatusValue.get(0))) && (0 == PreState)) {
					mIsCtrlToRun = true;
				} else {
					mIsCtrlToRun = false;
				}
				PreState = (0x01 & nStatusValue.get(0));
				break;
			case 3:// on->off时执行
				if ((0 == (0x01 & nStatusValue.get(0))) && (1 == PreState)) {
					mIsCtrlToRun = true;
				} else {
					mIsCtrlToRun = false;
				}
				PreState = (0x01 & nStatusValue.get(0));
				break;
			case 4:// switch时执行
				if (((0x01 & nStatusValue.get(0)) != PreState)
						&& (-1 != PreState)) {
					mIsCtrlToRun = true;
				} else {
					mIsCtrlToRun = false;
				}
				PreState = (0x01 & nStatusValue.get(0));
				break;
			default:
				mIsCtrlToRun = false;
				break;
			}
		} else if (ADDRTYPE.WORDADDR == mSMI.getControlAddrType()) {// 字地址
			if (mCmpNoticeData == null) {
				mCmpNoticeData = new Vector<Short>();
			} else {
				mCmpNoticeData.clear();
			}

			boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
					mCmpNoticeData);// 将字节转换成短整型

			if (!mCmpNoticeData.isEmpty()) {
				if (mCmpNoticeData.get(0) == mSMI.getCmpFactor()) {// 将地址中的数据和固定值进行比较
					mIsCtrlToRun = true;
				} else {
					mIsCtrlToRun = false;
				}
			}
		}

		// if(mIsCtrlToRun){
		// mRCount = mSMI.getRunCount();
		// }
	}

	/**
	 * 地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyAddrDataCallback = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleAddrDataCallback(nStatusValue);
		}
	};

	@Override
	public int cancle(Timer timer) {
		if (null == timer) {
			return -1;
		}
		timer.cancel();

		if (timerTask != null) {
			timerTask.cancel();
		}

		if (MACRO_TYPE.SCTRLOOP == mSMI.getMacroType()) {// 若为受控宏指令
			SKPlcNoticThread.getInstance().destoryCallback(
					notifyAddrDataCallback, sid);
		}
		return 0;
	}

	@Override
	public short getType() {
		return this.mSMI.getMacroType();
	}

}// End of class
