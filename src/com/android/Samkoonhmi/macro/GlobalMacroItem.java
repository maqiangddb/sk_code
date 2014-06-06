package com.android.Samkoonhmi.macro;

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
public class GlobalMacroItem extends BaseMacroItem {

	private long PrevTimeMillis; // 上次执行的时间

	private boolean needInvoke = false; // 判断当前是否需要调用宏指令

	private boolean mIsCtrlToRun = false; // 是否受控执行

	private int PreState = -1; // 记录上次状态


	private GlobalMacroInfo mGMI;
	HashMap<String, PHolder> mPHMap = null;

	private boolean mDebug = false;
	private String mTag = "GlobalMacroItem";

	protected int addrdata = 0;
	private int nRunCount = 0;
	private boolean bRun;

	public GlobalMacroItem(short mid) {
		super();

		getDataFromDatabase(mid);

		if (null == mGMI) {// 若数据实体类为空
			Log.e("GlobalMacroItem", "GlobalMacroItem: mGMI is null");
			return;
		}

		setBMI(mGMI);
		if (MACRO_TYPE.GCTRLOOP == mGMI.getMacroType()) {// 若为受控宏指令
			SKPlcNoticThread.getInstance().addNoticProp(mGMI.getControlAddr(),
					notifyAddrDataCallback, false, 0); // 绑定地址数据回调
		}

	}

	/**
	 * 从数据库读取宏指令信息
	 * */
	public void getDataFromDatabase(short mid) {
		GlobalMacroBiz tmpMBiz = new GlobalMacroBiz();
		mGMI = tmpMBiz.selectGlobalMacro(mid);
		if (null == mGMI) {
			Log.e("GlobalMacroItem", "getDataFromDatabase: mGMI is null!");
			return;
		}

		// 初始化参数列表
		MParamBiz tmpMPBiz = new MParamBiz();
		mParamList = tmpMPBiz.selectMacroParamList(mGMI.getMacroLibName());
		if (null == mParamList) {
			Log.e("GlobalMacroItem", "getDataFromDatabase: mParamList is null!");
			return;
		}
		mPHMap = new HashMap<String, PHolder>();

		// 参数设置
		ParamTool.setParam(mParamList, mPHMap, true, 0);
		// 添加到参数列表
		//ParamHelper.addAddrList(mParamList);
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

		if ((tmpCurTimeMillis - PrevTimeMillis) >= mGMI.getTimeInterval()) {
			PrevTimeMillis = tmpCurTimeMillis;
			return true;
		}
		return false;
	}

	/**
	 * 判断控制地址是否授权执行
	 * */
	private boolean isCtrlToRun() {
		return mIsCtrlToRun;
	}

	//宏指令是否处于运行状态，为了防止卡死，屏蔽掉未能处理的消息
	private boolean bMacroRunning=false;
	@Override
	public synchronized void run() {

		
		if (bMacroRunning) {
			Log.e(mTag, "GlobalMacroItem, Failure to deal with run() .... ");
			return;
		}
		bMacroRunning=true;
		
		// 降低宏指令的优先级
		//android.os.Process.setThreadPriority(19);

		needInvoke = true;
		if ((needInvoke == true)
				&& (mGMI.getMacroType() == MACRO_TYPE.GCTRLOOP)) {// 受控检测
			if (false == isCtrlToRun()) {
				// 受控不执行
				needInvoke = false;
			}
		}

		bRun = true;

		// 是否需要调用宏
		if (needInvoke) {
			// 每次都需同步地址控件的数据到本地缓存
			if (mGMI.getRunCount() == 0) {
				// 一直执行
				bRun = true;
			} else {
				if (nRunCount >= mGMI.getRunCount()) {
					// 已经超过执行次数,不执行
					bRun = false;
				} else {
					nRunCount++;
				}
			}

			if (bRun) {
				ParamHelper.pullParams(mParamList, mPHMap);
				// 调用宏指令
				try {
					mMacroMethod.invoke(mJMLInstance, mPHMap);
				} catch (Exception e) {
					Log.e("GlobalMacroItem",
							"run() error,  MacroName: "+ mGMI.getMacroName());
					e.printStackTrace();
					bMacroRunning=false;
				} 
				// 将参数推送到地址空间
//				if (mDebug) {
//					Log.i(mTag, "run: try to call pushParams");
//				}
				//ParamHelper.pushParams(mParamList, mPHMap);
			}

		}
		bMacroRunning=false;
	}

	@Override
	public int execute(Timer timer) {
		if (null == timer) {
			return -1;
		}

		if (null == mMacroMethod) {
			return -2;
		}

		if (null == mGMI) {
			return -3;
		}
		int tv = mGMI.getTimeInterval();
		if (0 == tv) {
			tv = 100;
		}

		// 使用定时器立即调度，调度周期为执行时间间隔
		timer.schedule(this, 0, tv);
		return 0;
	}

	/**
	 * 处理地址数据通知回调
	 * */
	private Vector<Short> mCmpNoticeData = null; // 存放通知数据

	public void handleAddrDataCallback(Vector<Byte> nStatusValue) {
		if (ADDRTYPE.BITADDR == mGMI.getControlAddrType()) { // 位地址
			// Log.d(mTag, "-------------"+mGMI.getMacroLibName());
			mIsCtrlToRun = false;
			int execCondition = mGMI.getExecCondition();
			switch (execCondition) {
			case 0:// off时执行
			case 1:// on时执行
				if (execCondition == (0x01 & nStatusValue.get(0))) {
					mIsCtrlToRun = true;
					nRunCount = 0;
				} else {
					mIsCtrlToRun = false;
				}
				// Log.d(mTag,
				// "name="+mGMI.getMacroLibName()+",mIsCtrlToRun="+mIsCtrlToRun);
				break;
			case 2:// off->on时执行
				if ((1 == (0x01 & nStatusValue.get(0))) && (0 == PreState)) {
					mIsCtrlToRun = true;
					nRunCount = 0;
				} else {
					mIsCtrlToRun = false;
				}
				PreState = (0x01 & nStatusValue.get(0));
				break;
			case 3:// on->off时执行
				if ((0 == (0x01 & nStatusValue.get(0))) && (1 == PreState)) {
					mIsCtrlToRun = true;
					nRunCount = 0;
				} else {
					mIsCtrlToRun = false;
				}
				PreState = (0x01 & nStatusValue.get(0));
				break;
			case 4:// switch时执行
				if (((0x01 & nStatusValue.get(0)) != PreState)
						&& (-1 != PreState)) {
					mIsCtrlToRun = true;
					nRunCount = 0;
				} else {
					mIsCtrlToRun = false;
				}
				PreState = (0x01 & nStatusValue.get(0));
				break;
			default:
				mIsCtrlToRun = false;
				break;
			}
		} else if (ADDRTYPE.WORDADDR == mGMI.getControlAddrType()) {// 字地址
			// Log.d(mTag, "------------- w "+mGMI.getMacroLibName());
			if (mCmpNoticeData == null) {
				mCmpNoticeData = new Vector<Short>();
			} else {
				mCmpNoticeData.clear();
			}

			boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
					mCmpNoticeData);// 将字节转换成短整型

			if (!mCmpNoticeData.isEmpty()) {
				if (mCmpNoticeData.get(0) == mGMI.getCmpFactor()) {// 将地址中的数据和固定值进行比较
					mIsCtrlToRun = true;
					nRunCount = 0;
				} else {
					mIsCtrlToRun = false;
				}
			}
		}
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
		if (MACRO_TYPE.GCTRLOOP == mGMI.getMacroType()) {// 若为受控宏指令
			SKPlcNoticThread.getInstance().destoryCallback(
					notifyAddrDataCallback, 0);
		}
		return 0;
	}

	@Override
	public short getType() {
		return this.mGMI.getMacroType();
	}

}// End of class
