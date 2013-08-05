package com.android.Samkoonhmi.system;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.macro.corba.ShortHolder;
import com.android.Samkoonhmi.macro.corba.ShortSeqHolder;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.network.PhoneManager;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.LIGHTENESS;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AcillCode;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.SystemBroadcast;
import com.android.Samkoonhmi.util.SystemParam;

/**
 * 系统变量
 * 
 * @author Administrator
 * 
 */
public class SystemVariable {
	//private static final String TAG = "system";
	private static int currentScond = 0; // 当前秒
	private static int currentMinute = 0;// 当前分
	private static int currentHour = 0; // 当前小时
	private static int currentDay = 0;// 当前日
	private static int currentMonth = 0;// 当前月
	private static int currentYear = 0;// 当前年
	private static int currentWeek = 0;// 当前周

	private static int addrSecond = 0;// 地址秒数
	private static int addrMinute = 0;// 地址分
	private static int addrHour = 0;// 地址时
	private static int addrDate = 0;// 地址日
	private static int addrMonth = 0;// 地址月
	private static int addrYear = 0;// 地址年
	private SystemThread thread = null;
	public static boolean isSysThread = false;
	private Vector<Integer> dataListInt = null;
	private Vector<Long> dataListLong = null;
	private Vector<Short> mIData = null;
	private Vector<Byte> byteList = null;
	private int runTimeDay = 0;// 系统运行天
	private int runTimeHour = 0;// 系统运行时
	private int runTimeMin = 0;// 系统运行分
	private int runTimeSec = 0;// 系统运行秒

	private short triSetTimeAddrValue = 0;// 记录上一次触发时间设置的地址值
	private short tri_ClrBoardAddrValue = 0;// 记录上一次触发清除留言板信息的地址值
	private SystemInfoBiz systemInfoBiz = null;
	private boolean registerAddrB = false;
	private boolean isInitAddrValue = false;
	private int addrLanguageIndex = 0;// 地址当前系统语言值
	private SEND_DATA_STRUCT mSendData = null;

	private int myCom1Buad = 0; // com1波特率
	private int myCom1DataLen = 0;// com1数据长度
	private int myCom1Check = 0; // com1校验位
	private int myCom1Stop = 0; // com1停止位

	private int myCom2Buad = 0; // com2波特率
	private int myCom2DataLen = 0;// com2数据长度
	private int myCom2Check = 0; // com2校验位
	private int myCom2Stop = 0; // com2停止位
    private myMainHandler handler=null;
	// 单例
	private static SystemVariable sInstance = null;

	public synchronized static SystemVariable getInstance() {
		if (sInstance == null) {
			sInstance = new SystemVariable();
		}
		return sInstance;
	}

	public void startSystemThread() {
		if (null == thread) {
			thread = new SystemThread();
			isSysThread = true;
			thread.start();
		}
	}

	public void stopSystemThread() {
		if (null != thread) {
			isSysThread = false;
		}

	}

	/**
	 * 线程跟踪地址，改变值
	 * 
	 * @author Administrator
	 * 
	 */
	private boolean isSetCTimeToAddr = true;// 是否把当前时间写入地址

	private class SystemThread extends Thread {
		@Override
		public void run() {

			while (isSysThread) {
				// long startRunMethodTime = System.currentTimeMillis();
				// 把当前系统时间写入地址
				if (isSetCTimeToAddr) {
					setCurrentTimeToAddr();
				}
				// 初始化地址值
				if (!isInitAddrValue) {
					initTime();
					initAddrValue();
					isInitAddrValue = true;
				}
				// 注册地址 当地址没注册的时候
				if (!registerAddrB) {
					registerAddr();
					registerAddrB = true;
				}
				// 判断是否进入屏保
				if ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER) {
					if(null == handler)
					{
						handler = new myMainHandler(Looper.getMainLooper());
					}
					handler.sendEmptyMessage(11);
				}
				// 获取各个通讯口的通信状态写入地址
				setCommunicateStatus();
				// 设置当前画面编号
				setCurrenSceneId();
				// long stopRunMethodTime = System.currentTimeMillis();
				// 读取是否有实时报警发生
				setReadAlarmOpen();
				// 获得系统运行时间
				getRunTime();
				
				//ip获取
				PhoneManager.getInstance().wifiSetting();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	private class myMainHandler extends Handler {
		public myMainHandler(Looper loop) {
			super(loop);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 11) {
				boolean boo = SKTimer.getInstance().getBinder()
						.isRegister(SKSceneManage.getInstance().sCallback);
				// 定时器还没注册
				if (!boo && !GlobalPopWindow.popIsShow && !SystemBroadcast.ISKEYBOARDOPEN) {
					SKSceneManage.getInstance().time = 0;
					SKTimer.getInstance()
							.getBinder()
							.onRegister(
									SKSceneManage.getInstance().sCallback);
				}
			}
		}
	}
	private void initTime() {
		Calendar ca = Calendar.getInstance();
		currentScond = ca.get(Calendar.SECOND);// 秒
		currentDay = ca.get(Calendar.DAY_OF_MONTH);// 日
		currentHour = ca.get(Calendar.HOUR_OF_DAY);// 小时数
		currentMinute = ca.get(Calendar.MINUTE);// 分
		currentMonth = ca.get(Calendar.MONTH);// 月
		currentWeek = ca.get(Calendar.WEEK_OF_YEAR);// 周
		currentYear = ca.get(Calendar.YEAR);// 年

	}

	/**
	 * 根据系统参数设置的值将相应的值写入地址 只写入地址一次
	 */
	private void initAddrValue() {
		// 是否开启触摸声音
		setTouchSoundToAddr(SystemAddress.getInstance().enableBeepAddr());
		// 待机是否注销用户
		setLogoutUserToAddr(SystemAddress.getInstance().is_LogOutAddr());
		// 设置待机保护时间
		setSaveTimeToAddr(SystemAddress.getInstance().sys_SaveTimeAddr());
		// 设置待机方式0 不待机 1亮度待机 2画面待机
		setSaveTypeToAddr(SystemAddress.getInstance().sys_ScrSavAddr());
		// 写入待机亮度 0 没有亮度 1 最小亮度
		setSaveLightToAddr(SystemAddress.getInstance().sys_SavBriAddr());
		// 写入初始画面号
		setInitSceneIdToAddr(SystemAddress.getInstance().sys_InitNumAddr());
		// 写入当前配方组号
		setCurrentRecipeGidToAddr();
		// 写入当前配方号
		setCurrentRecipeidToAddr();
		// 写入com1的波特率
		setcom1BaudToAddr(SystemAddress.getInstance().cOM1_BuadAddr());
		// 写入com2的波特率
		setcom2BaudToAddr(SystemAddress.getInstance().cOM2_BuadAddr());
		// 写入com1的数据长度
		setcom1DataBitsToAddr(SystemAddress.getInstance().cOM1_LenAddr());
		// 写入com2的数据长度
		setcom2DataBitsToAddr(SystemAddress.getInstance().cOM2_LenAddr());
		// 写入com1的校验位
		setcom1CheckToAddr(SystemAddress.getInstance().cOM1_ChkAddr());
		// 写入com2的校验位
		setcom2CheckToAddr(SystemAddress.getInstance().cOM2_ChkAddr());
		// 写入com1的停止位
		setcom1StopToAddr(SystemAddress.getInstance().cOM1_StopAddr());
		// 写入com2的停止位
		setcom2StopToAddr(SystemAddress.getInstance().cOM2_StopAddr());
		// 写入系统当前语言到地址
		setSystemLanugageToAddr();
		// 写入报警是否有声音
		setIsAlarmBeep();
		// 设置com1的名字 因为是只读 所以只要读一次写入地址就可以了
		setCom1Name();
		// 设置com2的名字 所以只要读一次写入地址就可以了
		setCom2Name();
		// 设置com1的编号 所以只要读一次写入地址就可以了
		setCom1Number();
		// 设置com2的编号 所以只要读一次写入地址就可以了
		setCom2Number();
		// 设置以太网的名字 所以只要读一次写入地址就可以了
		setethName();
		// 设置以太网的编号 所以只要读一次写入地址就可以了
		setethNumber();
		// 将当前用户写入地址
		setCurrentUserToAddr();
		

	}

	/**
	 * 注册触发地址
	 */
	private void registerAddr() {
		// 触发注销用户
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().Tri_LogOutAddr(), triLogoutCall,
				true);
		// 时间设置触发
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().tri_SetTimeAddr(), setTimeCall,
				true);
		// 触控是否有声音
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().enableBeepAddr(), enableBeepCall,
				true);
		// 系统语言改变
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_LanIndexAddr(),
				systemLanguageCall, false);
		// 留言板清除信息触发
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().tri_ClrBoardAddr(),
				tri_ClrBoardCall, true);
		// 待机注销用户
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().is_LogOutAddr(), is_LogOutCall,
				true);
		// 待机保护时间
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_SaveTimeAddr(), saveTimeCall,
				false);
		// 初始画面号设置
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_InitNumAddr(), initSceneCall,
				false);
		// 设置待机亮度
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_SavBriAddr(), sys_SavBriCall,
				true);
		// 画面待机方式
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_ScrSavAddr(), sys_ScrSavCall,
				false);
		// 修改当前配方组号
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_CurRcpGrpAddr(),
				sys_CurRcpGrpCall, false);
		// 修改当前配方组号
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().sys_CurRcpAddr(), sys_CurRcpCall,
				false);
		// 连接设置参数确认修改触发通知
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().tri_MdParaAddr(), tri_mdParaCall,
				true);
		// 报警是否有声音
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().isAlarmBeepAddr(), isAlarmBeepCall,
				true);
		// 历史报警记录清除
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().tri_ClrAlarmAddr(),
				Tri_ClrAlarmCall, true);
		
		// 报警确定
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().mAlarmComfirm(),
				alarmConfirmCall, true);
				
		// 历史数据记录清除
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().tri_ClrSampAddr(), tri_ClrSampCall,
				true);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().TriSms(), triSmsCall,
				true);
		
//		SKPlcNoticThread.getInstance().addNoticProp(
//				SystemAddress.getInstance().isSmsSend(), null,
//				true);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().wifiIp(), wifiIpCall,false);
		
//		SKPlcNoticThread.getInstance().addNoticProp(
//				SystemAddress.getInstance().wifiStatus(), null,
//				false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().wifiSignal(), wifiSignalCall,false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().TgIp(), tgIpCall,
				false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().TgStatus(), tgStatusCall,
				false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().TgSignal(), tgSignalCall,
				false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().SpType(), spTypeCall,
				false);
		
//		SKPlcNoticThread.getInstance().addNoticProp(
//				SystemAddress.getInstance().TransProg(), null,
//				false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().smsMsg(), smsMsgCall,
				false);
		
		SKPlcNoticThread.getInstance().addNoticProp(
				SystemAddress.getInstance().TGNum(), tgNumCall,
				false);
				
	}
	/**
	 * 连接设置参数确认修改 触发
	 */
	private int mdParaAddrValue = 0;
	SKPlcNoticThread.IPlcNoticCallBack tri_mdParaCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}
			}
			if (addrValue == 1) // 地址值为1
			{
				if (mdParaAddrValue == 0) {
					// 如果上一次的地址值为0
					// 这一次的地址值为1 则去进行参数修改
					updateConnectPara();
					// 修改完之后自动复位
					writeBitAddr(0, SystemAddress.getInstance()
							.tri_MdParaAddr());

				}
			}
			mdParaAddrValue = addrValue;
		}
	};
	/**
	 * 历史数据记录清除
	 */
	private int historyAddrValue = 0;
	SKPlcNoticThread.IPlcNoticCallBack tri_ClrSampCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}

			}
			if (addrValue == 1) // 地址值为1
			{
				if (historyAddrValue == 0) {
					// 如果上一次的地址值为0
					// 地址值由 0 变为1 触发历史数据记录清除
					DataCollect.getInstance().msgClearAllHistory(null);
					writeBitAddr(0, SystemAddress.getInstance().tri_ClrSampAddr());
				}
			}
			historyAddrValue = addrValue;

		}
	};
	/**
	 * 历史报警记录清除
	 */
	private int alarmAddrValue = 0;
	SKPlcNoticThread.IPlcNoticCallBack Tri_ClrAlarmCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}

			}
			if (addrValue == 1) // 地址值为1
			{
				if (alarmAddrValue == 0) {
					// 如果上一次的地址值为0
					// 地址值由 0 变为1 触发历史报警记录清除
					AlarmGroup.getInstance().clearHisData(null);
					writeBitAddr(0, SystemAddress.getInstance().tri_ClrAlarmAddr());
				}
			}
			alarmAddrValue = addrValue;

		}
	};
	/**
	 * 报警是否有声音
	 */
	SKPlcNoticThread.IPlcNoticCallBack isAlarmBeepCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}
			}
			// 设置报警是否有声音
			if (addrValue == 1) {
				AlarmGroup.getInstance().setAlarmSound(true);
			} else if (addrValue == 0) {
				AlarmGroup.getInstance().setAlarmSound(false);
			}
			AlarmGroup.getInstance().saveSound();

		}
	};
	
	/**
	 * 报警确定
	 */
	SKPlcNoticThread.IPlcNoticCallBack alarmConfirmCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}
			}
			// 报警确定
			if (addrValue == 1) {
				AlarmGroup.getInstance().confirmAlarm();
				//自动复位
				writeBitAddr(0, SystemAddress.getInstance().mAlarmComfirm());
			} 

		}
	};


	/**
	 * 修改当前配方号
	 */
	SKPlcNoticThread.IPlcNoticCallBack sys_CurRcpCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int rId = 0;
			if (null == dataListInt) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					dataListInt);
			if (valueBool && 0 != dataListInt.size()) {
				rId = dataListInt.get(0);
			}
			// 修改当前配方号
			setCurrentRecipeId(rId);

		}
	};
	/**
	 * 修改当前配方组号
	 */
	SKPlcNoticThread.IPlcNoticCallBack sys_CurRcpGrpCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int Gid = 0;
			if (null == dataListInt) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					dataListInt);
			if (valueBool && 0 != dataListInt.size()) {
				Gid = dataListInt.get(0);
			}
			// 修改当前配方组号
			setCurrentRecipeGId(Gid);

		}
	};
	/**
	 * 画面待机方式
	 */
	SKPlcNoticThread.IPlcNoticCallBack sys_ScrSavCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int type = 0;
			if (null == dataListInt) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					dataListInt);
			if (valueBool && 0 != dataListInt.size()) {
				type = dataListInt.get(0);
			}
			// 设置画面待机方式
			setSaveType(type);

		}
	};
	/**
	 * 初始画面号设置
	 */
	SKPlcNoticThread.IPlcNoticCallBack initSceneCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int myAddrInitScene = 0;
			if (null == dataListInt) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					dataListInt);
			if (valueBool && 0 != dataListInt.size()) {
				myAddrInitScene = dataListInt.get(0);
			}

			setInitSceneId(myAddrInitScene);

		}
	};
	/**
	 * 待机保护时间设置
	 */
	SKPlcNoticThread.IPlcNoticCallBack saveTimeCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			//Log.d(TAG, "设置待机保护时间地址通知");
			int myAddrTime = 0;
			if (null == dataListInt) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					dataListInt);
			if (valueBool && 0 != dataListInt.size()) {
				myAddrTime = dataListInt.get(0);
			}

			setSaveTime(myAddrTime);

		}
	};
	/**
	 * 留言板信息清除触发
	 */
	SKPlcNoticThread.IPlcNoticCallBack tri_ClrBoardCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}

			}
			if (addrValue == 1) // 地址值为1
			{
				if (tri_ClrBoardAddrValue == 0) {
					// 如果上一次的地址值为0
					// 地址值由 0 变为1 触发设置系统时间
					boolean b = DBTool.getInstance().getMessageBoard().deleteMessage();
					for (int i = 0; i < mMessageList.size(); i++) {
						mMessageList.get(i).clearMessage(b);
					}
					//自动复位
					writeBitAddr(0, SystemAddress.getInstance().tri_ClrBoardAddr());
				}
			}
			tri_ClrBoardAddrValue = (short) addrValue;
		}
	};
	/**
	 * 设置待机亮度
	 * 
	 */
	SKPlcNoticThread.IPlcNoticCallBack sys_SavBriCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}
			}
			// 设置屏保亮度
			setSaveLight(addrValue);

		}
	};
	/**
	 * 待机是否注销用户
	 */
	SKPlcNoticThread.IPlcNoticCallBack is_LogOutCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}
			}
			// 设置注销用户
			logoutUser(addrValue);
		}
	};
	/**
	 * 系统语言设置
	 */
	SKPlcNoticThread.IPlcNoticCallBack systemLanguageCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int myAddrLanguage = 0;
			if (null == mIData) {
				mIData = new Vector<Short>();
			} else {
				mIData.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
					mIData);
			if (valueBool && 0 != mIData.size()) {
				myAddrLanguage = mIData.get(0);
			}
			if (myAddrLanguage != addrLanguageIndex) {
				setSystemLanguage(myAddrLanguage);
				addrLanguageIndex = myAddrLanguage;
				//把当前语言号写入报警表，方便上位导出
				DBTool.getInstance().getmAlarmBiz().setLanguageId(addrLanguageIndex);
			}

		}
	};
	/**
	 * 触控是否有声音
	 */
	SKPlcNoticThread.IPlcNoticCallBack enableBeepCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}
			}
			// 设置触控声音
			Sys_ControlBuzzer(addrValue);
		}
	};
	/**
	 * 触发设置时间通知 地址值从0 变为 1 的时候设置系统时间
	 */
	SKPlcNoticThread.IPlcNoticCallBack setTimeCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}

			}
			if (addrValue == 1) // 地址值为1
			{
				if (triSetTimeAddrValue == 0) {
					// 如果上一次的地址值为0
					// 地址值由 0 变为1 触发设置系统时间
					isSetCTimeToAddr = false; // 认为改变系统时间 将写入当前时间进地址的停止
					setSystemTimeFromAddr();
					isSetCTimeToAddr = true;// 系统时间修改完成，将当前时间写入地址继续开启
				}
			}
			triSetTimeAddrValue = (short) addrValue;
		}
	};
	/**
	 * 触发注销用户
	 */
	SKPlcNoticThread.IPlcNoticCallBack triLogoutCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}

			}
			if (addrValue == 1) // 地址值为1
			{
				if (triSetTimeAddrValue == 0) {
					// 如果上一次的地址值为0
					// 地址值由 0 变为1 触发注销用户
					ParameterSet.getInstance().outTimeLogout();
					SKSceneManage.getInstance().updateState();
					// 触发之后自动复位
					writeBitAddr(0, SystemAddress.getInstance()
							.Tri_LogOutAddr());
				}
			}
			triSetTimeAddrValue = (short) addrValue;
		}
	};

	SKPlcNoticThread.IPlcNoticCallBack tgIpCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			
		}
	};
	
	SKPlcNoticThread.IPlcNoticCallBack tgStatusCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	SKPlcNoticThread.IPlcNoticCallBack tgSignalCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	SKPlcNoticThread.IPlcNoticCallBack tgNumCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue==null||nStatusValue.size()==0) {
				return;
			}
			byte[] byteValue = new byte[nStatusValue.size()];
			for (int i = 0; i < nStatusValue.size(); i++) {
				byteValue[i] = nStatusValue.get(i);
			}
			String showValue = converCodeShow(byteValue,false);
			if (showValue==null||showValue.equals("")) {
				return;
			}
			
			showValue=showValue.trim();
			boolean result=isMobileNO(showValue);
			if(result){
				Log.d("SystemVariable", "showValue="+showValue);
				SystemInfo.setsTgNum(showValue);
			}
			
			
		}
	};
	
	SKPlcNoticThread.IPlcNoticCallBack smsMsgCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if(nStatusValue==null||nStatusValue.size()==0){
				return ;
			}
			byte[] byteValue = new byte[nStatusValue.size() + 2];
			byteValue[0] = -1;
			byteValue[1] = -2;
			for (int i = 0; i < nStatusValue.size(); i++) {
				byteValue[i + 2] = nStatusValue.get(i);
			}
			SystemInfo.setsSmsMsg( converCodeShow(byteValue,true));
		}
	};
	
	SKPlcNoticThread.IPlcNoticCallBack triSmsCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
				}

			}
			if (addrValue == 1){
				 // 地址值为1
				Log.d("SKScene", ".......addrValue="+addrValue);
				PhoneManager.getInstance().sendMSM();
				
				//自动复位
				//writeBitAddr(0, SystemAddress.getInstance().TriSms());
			}
		}
	};
	
	SKPlcNoticThread.IPlcNoticCallBack spTypeCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * wifi ip
	 */
	SKPlcNoticThread.IPlcNoticCallBack wifiIpCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue==null||nStatusValue.size()==0) {
				return;
			}
			read16WordsAddr(nStatusValue, 4);
		}
	};
	
	/**
	 * wifi 信号
	 */
	SKPlcNoticThread.IPlcNoticCallBack wifiSignalCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	/**
	 * 把当前时间写入地址 监控的地址就可以及时读取出来
	 */
	private void setCurrentTimeToAddr() {
		Calendar ca = Calendar.getInstance();
		// 把当前秒写入地址
		int second = ca.get(Calendar.SECOND);// 秒
		if (second != currentScond) {
			Sys_CurrentSecond(second);
			currentScond = second;// 给当前秒赋值
		}
		// 把当前分写入地址
		int minute = ca.get(Calendar.MINUTE);// 分
		if (minute != currentMinute) {
			Sys_CurrentMinute(minute);
			currentMinute = minute;// 给当前分赋值
		}
		// 把当前小时数写入地址
		int hour = ca.get(Calendar.HOUR_OF_DAY);// 小时
		if (hour != currentHour) {
			Sys_CurrentHour(hour);
			currentHour = hour;// 给当前小时赋值
		}
		// 把当前日写入地址
		int day = ca.get(Calendar.DATE);// 获取日
		if (day != currentDay) {
			Sys_CurrentDay(day);
			currentDay = day; // 给当前日赋值
		}
		// 把当前月写入地址
		int month = ca.get(Calendar.MONTH) + 1;// 获取月份 少一个月
		if (month != currentMonth) {
			Sys_CurrentMonth(month);
			currentMonth = month; // 给当前月赋值
		}
		// 把当前年写入地址
		int year = ca.get(Calendar.YEAR);// 获取年份
		if (year != currentYear) {
			Sys_CurrentYear(year);
			currentYear = year; // 给当前年赋值
		}
		// 把当前周写入地址
		Sys_CurrentWeek(ca);
	}

	/**
	 * 设置系统时间 从地址中读取地址时间
	 */
	private boolean  setSystemTimeFromAddr() {
		boolean flag = false;
		// 获取地址秒数 设置系统秒 0-59
		int myAddrSecond = read_16BCD_Addr(SystemAddress.getInstance()
				.Sys_SetSecAddr());
		// 获取地址分钟数，设置系统分 0-59
		int myAddrMinute = read_16BCD_Addr(SystemAddress.getInstance()
				.sys_CurMinAddr());
		// 获取地址小时数，设置系统小时 0-23
		int myAddrHour = read_16BCD_Addr(SystemAddress.getInstance()
				.sys_CurHourAddr());
		// 获取地址日期 设置系统日  1-31
		int myAddrDay = read_16BCD_Addr(SystemAddress.getInstance()
				.sys_CurDateAddr());
		// 获取地址月份，设置系统月 1~12
		int myAddrMonth = read_16BCD_Addr(SystemAddress.getInstance()
				.sys_CurMonAddr());
		// 获取地址中的年，设置系统年份
		int myAddrYear = read_16BCD_Addr(SystemAddress.getInstance()
				.sys_CurYearAddr());
		
		if (myAddrSecond < 0 || myAddrSecond > 59) {
			SKToast.makeText("秒钟设置超出了范围，请输入0~59的数据", Toast.LENGTH_SHORT).show();
			// 执行完自动复位地址值
			writeBitAddr(0, SystemAddress.getInstance().tri_SetTimeAddr());
			return false;
		}else if (myAddrMinute < 0 || myAddrMinute > 59) {
			SKToast.makeText("分钟设置超出了范围，请输入0~59的数据", Toast.LENGTH_SHORT).show();
			// 执行完自动复位地址值
			writeBitAddr(0, SystemAddress.getInstance().tri_SetTimeAddr());
			return false;
		}else if (myAddrHour < 0 || myAddrHour > 23) {
			SKToast.makeText("小时设置超出了范围，请输入0~23的数据", Toast.LENGTH_SHORT).show();
			// 执行完自动复位地址值
			writeBitAddr(0, SystemAddress.getInstance().tri_SetTimeAddr());
			return false;
		}else if (myAddrDay < 1 || myAddrDay > 31) {
			SKToast.makeText("日期设置超出了范围，请输入1~31的数据", Toast.LENGTH_SHORT).show();
			// 执行完自动复位地址值
			writeBitAddr(0, SystemAddress.getInstance().tri_SetTimeAddr());
			return false;
		}else if (myAddrMonth < 1 || myAddrMonth > 12) {
			SKToast.makeText("月份设置超出了范围，请输入1~12的数据", Toast.LENGTH_SHORT).show();
			// 执行完自动复位地址值
			writeBitAddr(0, SystemAddress.getInstance().tri_SetTimeAddr());
			return false;
		}else{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.SECOND, myAddrSecond);
			c.set(Calendar.MINUTE, myAddrMinute);
			c.set(Calendar.HOUR_OF_DAY, myAddrHour);
			c.set(Calendar.DAY_OF_MONTH, myAddrDay);
			c.set(Calendar.MONTH, (myAddrMonth - 1));
			c.set(Calendar.YEAR, myAddrYear);
			long when = c.getTimeInMillis();
			
			// 修改系统时间秒
			if (when / 1000 < Integer.MAX_VALUE) {
				Intent	intent = new Intent();
				intent.setAction("com.samkoon.settime");
				intent.putExtra("time", when);
				SKSceneManage.getInstance().mContext.sendBroadcast(intent);
			}
			// 执行完自动复位地址值
			writeBitAddr(0, SystemAddress.getInstance().tri_SetTimeAddr());
			return true;
		}
	
	}

	/**
	 * 读取16位BCD码地址值
	 */
	private int read_16BCD_Addr(AddrProp addrProp) {
		if (dataListInt == null) {
			dataListInt = new Vector<Integer>();
		} else {
			dataListInt.clear();
		}
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		boolean b = PlcRegCmnStcTools.getRegIntData(addrProp, dataListInt,
				mSendData);
		int addrValue = 0;
		if (b) {
			if (!dataListInt.isEmpty()) {
				addrValue = dataListInt.get(0);
			}
			String temp = DataTypeFormat.intToBcdStr((long) addrValue, false);
			if (!temp.equals("") && !"ERROR".equals(temp)) {
				try{
					addrValue = Integer.valueOf(temp);
				}catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
		return addrValue;
	}

	/**
	 * 读取位地址值
	 * 
	 * @param addrProp
	 * @return
	 */
	private int readBitAddr(AddrProp addrProp) {
		if (dataListInt == null) {
			dataListInt = new Vector<Integer>();
		} else {
			dataListInt.clear();
		}
		mSendData = getMSendData();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		mSendData.eDataType = DATA_TYPE.BIT_1;

		PlcRegCmnStcTools.getRegIntData(addrProp, dataListInt, mSendData);
		int addrValue = 0;
		if (!dataListInt.isEmpty()) {
			addrValue = dataListInt.get(0);
		}
		return addrValue;
	}

	/**
	 * 当前秒
	 * 
	 * @param addrProp
	 */
	private void Sys_CurrentSecond(int second) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		String secondStr = second + "";
		secondStr = Long.toString(DataTypeFormat.bcdStrToInt(secondStr, 16));
		if (secondStr.equals("")) {
			return;
		}
		try{
			mydataListInt.add(Integer.valueOf(secondStr));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurSecAddr(), mydataListInt, mSendData);
	}

	/**
	 * 当前分
	 * 
	 * @param addr
	 */

	private void Sys_CurrentMinute(int minute) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		String minuteStr = minute + "";
		minuteStr = Long.toString(DataTypeFormat.bcdStrToInt(minuteStr, 16));
		if (minuteStr.equals("")) {
			return;
		}
		try{
			mydataListInt.add(Integer.valueOf(minuteStr));
		}catch (Exception e) {
			// TODO: handle exception
		}
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurMinAddr(), mydataListInt, mSendData);
	}

	/**
	 * 当前时
	 * 
	 * @param addrProp
	 */
	private void Sys_CurrentHour(int hour) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		String hourStr = hour + "";
		hourStr = Long.toString(DataTypeFormat.bcdStrToInt(hourStr, 16));
		if (hourStr.equals("")) {
			return;
		}
		try{
			mydataListInt.add(Integer.valueOf(hourStr));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurHourAddr(), mydataListInt, mSendData);
	}

	/**
	 * 当前日
	 * 
	 * @param addrProp
	 */
	private void Sys_CurrentDay(int day) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		String dayStr = day + "";
		dayStr = Long.toString(DataTypeFormat.bcdStrToInt(dayStr, 16));
		if (dayStr.equals("")) {
			return;
		}
		try{
			mydataListInt.add(Integer.valueOf(dayStr));
		}catch (Exception e) {
			// TODO: handle exception
		}
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurDateAddr(), mydataListInt, mSendData);
	}

	/**
	 * 当前月
	 * 
	 * @param addrProp
	 */
	private void Sys_CurrentMonth(int month) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		String monthStr = month + "";
		monthStr = Long.toString(DataTypeFormat.bcdStrToInt(monthStr, 16));
		if (monthStr.equals("")) {
			return;
		}
		try{
			mydataListInt.add(Integer.valueOf(monthStr));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurMonAddr(), mydataListInt, mSendData);
	}

	/**
	 * 当前年
	 */
	private void Sys_CurrentYear(int year) {

		Vector<Integer> mydataListInt = new Vector<Integer>();
		String yearStr = year + "";
		yearStr = Long.toString(DataTypeFormat.bcdStrToInt(yearStr, 16));
		if (yearStr.equals("")) {
			return;
		}
		try{
			mydataListInt.add(Integer.valueOf(yearStr));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurYearAddr(), mydataListInt, mSendData);
	}

	/**
	 * 当前周
	 */
	private void Sys_CurrentWeek(Calendar c) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		//星期 1代表星期日 7代表星期六
		String dayOfWeek = c.get(Calendar.DAY_OF_WEEK)+"";
		dayOfWeek = Long.toString(DataTypeFormat.bcdStrToInt(dayOfWeek, 16));
		if (dayOfWeek.equals("")) {
			return;
		}
		try{
			int week=Integer.valueOf(dayOfWeek)-1;
			if (week==0) {
				week=7;
			}
			if(week != currentWeek)
			{
				mydataListInt.add(Integer.valueOf(week));
				mSendData = getMSendData();
				mSendData.eDataType = DATA_TYPE.BCD_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
						.sys_CurWeekAddr(), mydataListInt, mSendData);
				currentWeek = week;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	/**
	 * 获取运行时间
	 * 
	 * @return
	 */
	private void getRunTime() {
		runTimeSec += 1;// 秒数加1
		if (runTimeSec == 59) {
			runTimeMin += 1;// 分钟加1
			runTimeSec = 0;
			if (runTimeMin == 59) {
				runTimeHour += 1;// 小时加1
				runTimeMin = 0;
				if (runTimeHour == 23) {
					runTimeDay += 1;// 天数加1
					runTimeHour = 0;
				}
			}
		}
		// 运行秒写入地址
		setRunSecToAddr(runTimeSec);
		// 运行分写入地址
		setRunMinToAddr(runTimeMin);
		// 运行小时写入地址
		setRunHourToAddr(runTimeHour);
		// 运行天数写入地址
		setRunDayToAddr(runTimeDay);

	}

	/**
	 * 系统运行秒数写入地址
	 * 
	 * @param sec
	 */
	private void setRunSecToAddr(int sec) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(sec);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_RunSecAddr(), mydataListInt, mSendData);
	}

	/**
	 * 系统运行分数写入地址
	 * 
	 * @param sec
	 */
	private void setRunMinToAddr(int min) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(min);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_RunMinAddr(), mydataListInt, mSendData);
	}

	/**
	 * 系统运行小时数写入地址
	 * 
	 * @param sec
	 */
	private void setRunHourToAddr(int hour) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(hour);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_RunHourAddr(), mydataListInt, mSendData);
	}

	/**
	 * 系统运行天数写入地址
	 * 
	 * @param sec
	 */
	private void setRunDayToAddr(int day) {

		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(day);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_RunDayAddr(), mydataListInt, mSendData);
	}

	/**
	 * 设置触控声音 改变系统参数设置 改变数据库 1触摸有声音 0 触摸没声音
	 */
	private void Sys_ControlBuzzer(int addrValue) {
		boolean isTouchSound = (SystemParam.USE_TOUCH_SOUND & SystemInfo
				.getnSetBoolParam()) == SystemParam.USE_TOUCH_SOUND;// 为true
																	// 开启触摸声音
		if (null == systemInfoBiz) {
			systemInfoBiz = new SystemInfoBiz();
		}
		// 如果位地址值为1 并且没用开启触摸声音 则设置开启触摸声音
		if (addrValue == 1) {
			if (!isTouchSound) {
				SystemInfo.setnSetBoolParam(SystemInfo.getnSetBoolParam()
						| (SystemParam.USE_TOUCH_SOUND));
				// 改变数据库值
				systemInfoBiz.updateSysParam(SystemInfo.getnSetBoolParam());
			}

		} else if (addrValue == 0) // 如果地址值为0 但是已经开启了触摸声音，则关掉
		{
			if (isTouchSound) {
				SystemInfo.resetnSetBoolParam(SystemInfo.getnSetBoolParam()
						& (~SystemParam.USE_TOUCH_SOUND));
				// 改变数据库值
				systemInfoBiz.updateSysParam(SystemInfo.getnSetBoolParam());
			}
		}

	}

	/**
	 * 设置触摸是否有声音
	 */
	public void setTouchSoundToAddr(AddrProp addrProp) {
		boolean isTouchSound = (SystemParam.USE_TOUCH_SOUND & SystemInfo
				.getnSetBoolParam()) == SystemParam.USE_TOUCH_SOUND;// 为true
																	// 开启触摸声音
		if (isTouchSound) {
			// 如果系统参数设置开启了触摸声音 则将1 写入地址
			writeBitAddr(1, addrProp);
		} else {
			writeBitAddr(0, addrProp);
		}
	}

	/**
	 * 设置系统语言
	 * 
	 * @param languageIndex
	 */
	private void setSystemLanguage(int languageIndex) {
		// 语言下标最小为0
		if (languageIndex >= 0) {
			SKLanguage.getInstance().getBinder().onChange(languageIndex);
		}
	}

	/**
	 * 根据系统参数设置当前语言写到地址
	 */
	public void setSystemLanugageToAddr() {
		int languageId = SystemInfo.getCurrentLanguageId();

		Vector<Integer> mydataListInt = new Vector<Integer>();

		mydataListInt.add(languageId);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_LanIndexAddr(), mydataListInt, mSendData);
	}

	/**
	 * 待机是否注销用户 位地址值 1 注销 0 不注销
	 */
	private void logoutUser(int addrValue) {

		if (null == systemInfoBiz) {
			systemInfoBiz = new SystemInfoBiz();
		}
//		Log.d("system", "设置前的值为：" + (SystemInfo.getnSetBoolParam()));
		boolean isLogout = ((SystemInfo.getnSetBoolParam() & SystemParam.LOGOUT) == SystemParam.LOGOUT);
		if (addrValue == 1) {
			// 地址值要求注销用户
			if (!isLogout) {
//				Log.d("system",
//						"地址值为1 设置注销用户，设置进去的值为："
//								+ (SystemInfo.getnSetBoolParam() | (SystemParam.LOGOUT)));
				SystemInfo.setnSetBoolParam(SystemInfo.getnSetBoolParam()
						| (SystemParam.LOGOUT));
//				Log.d("system", "设置后的值为：" + (SystemInfo.getnSetBoolParam()));
				systemInfoBiz.updateSysParam(SystemInfo.getnSetBoolParam());
			}
		} else {
			// 如果要求不注销用户
			if (isLogout) {
//				Log.d("system",
//						"地址值为0 设置不注销用户，设置进去的值为："
//								+ (SystemInfo.getnSetBoolParam() & (~SystemParam.LOGOUT)));
				SystemInfo.resetnSetBoolParam(SystemInfo.getnSetBoolParam()
						& (~SystemParam.LOGOUT));
//				Log.d("system", "设置后的值为：：" + (SystemInfo.getnSetBoolParam()));
				systemInfoBiz.updateSysParam(SystemInfo.getnSetBoolParam());
			}
		}

	}

	/**
	 * 根据系统参数的是否注销用户将值写入地址
	 */
	private void setLogoutUserToAddr(AddrProp addrProp) {
		boolean isLogout = ((SystemInfo.getnSetBoolParam() & SystemParam.LOGOUT) == SystemParam.LOGOUT);

		if (isLogout) {
			// 如果选择了注销用户，则将1写入地址
			writeBitAddr(1, addrProp);
//			Log.d("system", "设置注销用户地址值1");

		} else {
			writeBitAddr(0, addrProp);
//			Log.d("system", "设置注销用户地址值0");
		}
	}

	/**
	 * 设置待机保护时间
	 * 
	 * @param addrValue
	 */
	private void setSaveTime(int addrValue) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		// 待机保护时间最小为1
		if (addrValue > 0) {
		//	Log.d(TAG, "设置待机保护时间：" + addrValue);
			SystemInfo.setnScreenTime(addrValue);
			systemInfoBiz.updateScreenSaverTime(addrValue);
		}
	}

	/**
	 * 设置屏保待机时间到地址 从系统参数中读取值
	 * 
	 * @param addrProp
	 */
	private void setSaveTimeToAddr(AddrProp addrProp) {

		Vector<Integer> mydataListInt = new Vector<Integer>();

		mydataListInt.add(SystemInfo.getnScreenTime());
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
	}

	/**
	 * 写入当前画面编号
	 */
	private int mCurrentSceneNum = 0; // 画面序号

	private void setCurrenSceneId() {

		Vector<Integer> mydataListInt = new Vector<Integer>();

		// 根据画面 序号获取画面编号
		int num = SystemInfo.getCurrentScenceId();
		if (mCurrentSceneNum != num) {
			int sid = SKSceneManage.getInstance().getSceneBySid(num);
			mydataListInt.add(sid);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
					.sys_CurSceneAddr(), mydataListInt, mSendData);
			mCurrentSceneNum = num;
		}
	}

	/**
	 * 设置初始画面号 重启之后有效 所以只要改变数据库的值就行
	 * 
	 * @param addrValue
	 */
	private void setInitSceneId(int addrValue) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		// 根据画面编号获取画面序号 地址里面的值是序号
		int sceneId = SKSceneManage.getInstance().getSceneByNum(addrValue);
		SystemInfo.setInitSceneId(addrValue);
		systemInfoBiz.updateInitScene(sceneId);
	}

	/**
	 * 将系统参数中的初始画面号写入地址 写入地址的是编号
	 * 
	 * @param addrProp
	 */
	private void setInitSceneIdToAddr(AddrProp addrProp) {

		int sceneId = SKSceneManage.getInstance().getSceneBySid(
				SystemInfo.getInitSceneId());
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(sceneId);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);

	}

	/**
	 * 设置屏保亮度 0 没有亮度 1 最小亮度
	 * 
	 * @param addrValue
	 */
	private void setSaveLight(int addrValue) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		int brightness = 1;
		if (addrValue == 0) {
			brightness = 1;
			SystemInfo.setnBrightness(systemInfoBiz
					.getLight(LIGHTENESS.no_light));

		} else {
			brightness = 2;
			SystemInfo.setnBrightness(systemInfoBiz
					.getLight(LIGHTENESS.small_light));
		}
		// 写入到数据库的值是枚举的下标
		systemInfoBiz.updateBrightness(brightness);
	}

	/**
	 * 从系统参数设置值中读取亮度设置 写入到地址 0 没有亮度 1 最小亮度
	 */
	private void setSaveLightToAddr(AddrProp addrProp) {
		int light = SystemInfo.getnBrightness();
		if (light == 1) {
			// 没有亮度
			writeBitAddr(0, addrProp);
		} else if (light == 10) {
			// 最小亮度
			writeBitAddr(1, addrProp);
		}
	}

	/**
	 * 画面待机方式 0为不使用待机 1为亮度变化待机
	 * 
	 * @param addrValue
	 */
	private void setSaveType(int addrValue) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		// 是否使用待机
		boolean useBool = (SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER;
		if (addrValue == 0) {
			if (useBool) {
				// Log.d(TAG, "地址值通知为0 并且已经设置了待机 则设置为不待机");
				// 如果已经使用了 则设置为非
				SystemInfo.resetnSetBoolParam(SystemInfo.getnSetBoolParam()
						& (~SystemParam.USE_SAVER));
				// Log.d(TAG,
				// "++++设置之后的值："
				// + ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) ==
				// SystemParam.USE_SAVER));
				systemInfoBiz.updateSysParam(SystemInfo.getnSetBoolParam());
			}
		} else if (addrValue == 1) {
			if (!useBool) {
				// 设置亮度待机方式 则先打开开启待机
				// Log.d(TAG, "地址值通知为1设置亮度待机");
				SystemInfo.setnSetBoolParam(SystemInfo.getnSetBoolParam()
						| (SystemParam.USE_SAVER));
				// Log.d(TAG,
				// "---设置之后的值："
				// + ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) ==
				// SystemParam.USE_SAVER));
				systemInfoBiz.updateSysParam(SystemInfo.getnSetBoolParam());
			}
			// 显示指定画面(true)，亮度变化(false)
			SystemInfo.setbScreensaver(false);
			systemInfoBiz.updateSaveType(false);
		}
	}

	/**
	 * 设置待机方式去地址 0为不使用待机 1亮度变化待机 2 画面待机
	 * 
	 * @param addrProp
	 */
	private void setSaveTypeToAddr(AddrProp addrProp) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		// 是否使用待机
		boolean useBool = (SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER;
		if (!useBool) {
			mydataListInt.add(0);
		} else {
			boolean light = SystemInfo.isbScreensaver();
			if (!light) // 亮度待机
			{
				// 使用待机 并且是亮度待机 则将地址值写入1
				mydataListInt.add(1);
			} else {
				// 使用待机 并且是画面待机 则将地址值写入2
				mydataListInt.add(2);
			}
		}
		if (mydataListInt.size() != 0) {
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
	}

	/**
	 * 修改当前配方
	 * 
	 * @param addrValue
	 */
	private void setCurrentRecipeId(int addrValue) {
		// 当前组号不变
		// 调用小唐的修改配方的方法
		RecipeDataCentre.getInstance().setCurrRecipe(
				SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId(),
				addrValue);
	}

	/**
	 * 修改当前配方组号
	 * 
	 * @param addrValue
	 */
	private void setCurrentRecipeGId(int addrValue) {
		// 当前配方号不变
		// 调用小唐的修改配方的方法
		RecipeDataCentre.getInstance().setCurrRecipe(addrValue,
				SystemInfo.getCurrentRecipe().getCurrentRecipeId());
	}

	/**
	 * 从系统参数里面获取当前配方组号 写入地址
	 */
	public void setCurrentRecipeGidToAddr() {
		int Gid = SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId();
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(Gid);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurRcpGrpAddr(), mydataListInt, mSendData);
	}

	/**
	 * 从系统参数里面获取当前配方号 写入地址
	 */
	public void setCurrentRecipeidToAddr() {
		int rId = SystemInfo.getCurrentRecipe().getCurrentRecipeId();
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(rId);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
				.sys_CurRcpAddr(), mydataListInt, mSendData);
	}

	/**
	 * 写入com1的名称
	 */
	private String com1Name = "";
	private PlcConnectionInfo com1Info = null;

	private void setCom1Name() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com1Info) {
			com1Info = systemInfoBiz.findComInfo(3);
		}

		if (com1Info != null) {
			String com1Name2 = com1Info.getsConnectName();
			if (null != com1Name2) {
				if (com1Name != com1Name2) {
				//	Log.d(TAG, "com1名字写入地址：" + com1Name2);
					setUnicodeToAddr(SystemAddress.getInstance()
							.cOM1_NameAddr(), com1Name2);
					com1Name = com1Name2;
				}
			}
		}
	}

	/**
	 * 写入com2的名称
	 */
	private String com2Name = "";
	private PlcConnectionInfo com2Info = null;

	private void setCom2Name() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com2Info) {
			com2Info = systemInfoBiz.findComInfo(4);
		}
		if (com2Info != null) {

			String com2Name2 = com2Info.getsConnectName();
			if (null != com2Name) {
				if (com2Name != com2Name2) {
				//	Log.d(TAG, "com2名字写入地址：" + com2Name2);
					setUnicodeToAddr(SystemAddress.getInstance()
							.cOM2_NameAddr(), com2Name2);
					com2Name = com2Name2;
				}
			}
		}

	}

	/**
	 * 写入com1的本机编号
	 */
	private int com1Number = 0;

	private void setCom1Number() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com1Info) {
			com1Info = systemInfoBiz.findComInfo(3);
		}
		if (null != com1Info) {
			int comId = com1Info.getnScreenNo();
			if (comId != com1Number) {
			//	Log.d(TAG, "com1编号：" + comId);
				Vector<Integer> mydataListInt = new Vector<Integer>();

				mydataListInt.add(comId);
				mSendData = getMSendData();
				mSendData.eDataType = DATA_TYPE.INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
						.cOM1_NumAddr(), mydataListInt, mSendData);
				com1Number = comId;
			}
		}
	}

	/**
	 * 写入com2的本机编号
	 */
	private int com2Number = 0;

	private void setCom2Number() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com2Info) {
			com2Info = systemInfoBiz.findComInfo(4);
		}
		if (null != com2Info) {
			int comId = com2Info.getnScreenNo();
			if (comId != com2Number) {
			//	Log.d(TAG, "com2编号：" + comId);
				Vector<Integer> mydataListInt = new Vector<Integer>();
				mydataListInt.add(comId);
				mSendData = getMSendData();
				mSendData.eDataType = DATA_TYPE.INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
						.cOM2_NumAddr(), mydataListInt, mSendData);
				com2Number = comId;
			}
		}
	}

	/**
	 * 写入以太网的名称
	 */
	private String ethName = "";
	private PlcConnectionInfo eth = null;

	private void setethName() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == eth) {
			eth = systemInfoBiz.findComInfo(8);
		}
		if (eth != null) {
			String ethName2 = eth.getsConnectName();
			if (null != ethName2) {
				setUnicodeToAddr(SystemAddress.getInstance().eth_NameAddr(),
						ethName2);
				ethName = ethName2;
			}
		}
	}

	/**
	 * 写入以太网的本机编号
	 */
	private int ethNumber = 0;

	private void setethNumber() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == eth) {
			eth = systemInfoBiz.findComInfo(8);
		}
		if (null != eth) {
			int ethId = eth.getnScreenNo();
			if (ethId != ethNumber) {
			//	Log.d(TAG, "以太网编号：" + ethId);
				Vector<Integer> mydataListInt = new Vector<Integer>();
				mydataListInt.add(ethId);
				mSendData = getMSendData();
				mSendData.eDataType = DATA_TYPE.INT_16;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
				PlcRegCmnStcTools.setRegIntData(SystemAddress.getInstance()
						.eth_NumAddr(), mydataListInt, mSendData);
				ethNumber = ethId;
			}
		}
	}

	/**
	 * 修改连接参数
	 */
	private void updateConnectPara() {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		int com1Baud = readInt_32_WordAddr(SystemAddress.getInstance()
				.cOM1_BuadAddr()); // com1的波特率
		int com1Stop = readWordAddr(SystemAddress.getInstance().cOM1_StopAddr());// com1停止位
		int com1DataLen = readWordAddr(SystemAddress.getInstance()
				.cOM1_LenAddr());// com1数据长度
		int com1Check = readWordAddr(SystemAddress.getInstance().cOM1_ChkAddr());// com1校验位
		int com2Baud = readInt_32_WordAddr(SystemAddress.getInstance()
				.cOM2_BuadAddr());// com2的波特率
		int com2Stop = readWordAddr(SystemAddress.getInstance().cOM2_StopAddr());// com2的停止位
		int com2DataLen = readWordAddr(SystemAddress.getInstance()
				.cOM2_LenAddr());// com1数据长度
		int com2Check = readWordAddr(SystemAddress.getInstance().cOM2_ChkAddr());// com1校验位

		List<PlcConnectionInfo> list = SystemInfo.getPlcConnectionList();
		if (null != list) {
			if (list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					PlcConnectionInfo info = list.get(i);
					if (info.geteConnectPort() == 3) {
						// 连接端口为 3 说明是com1
						info.setnBaudRate(com1Baud);
						info.setnStopBit((short) com1Stop);
						info.setnCheckType(com1Check);
						info.setnDataBits((short) com1DataLen);
					} else if (info.geteConnectPort() == 4) {
						// 连接端口为4 说明是com2
						info.setnBaudRate(com2Baud);
						info.setnStopBit((short) com2Stop);
						info.setnCheckType(com2Check);
						info.setnDataBits((short) com2DataLen);
					}
				}
			}
		}
		// 修改com1到plc
		COM_PORT_PARAM_PROP com_port = CmnPortManage.getInstance()
				.getSerialParam(CONNECT_TYPE.COM1);
		if (com_port != null) {
			com_port.nBaudRate = com1Baud;
			com_port.nDataBits = com1DataLen;
			com_port.nStopBit = com1Stop;
			com_port.nParityType = com1Check;
			CmnPortManage.getInstance().setSerialParam(com_port);
		}
		// 修改com1到plc
		COM_PORT_PARAM_PROP com_port2 = CmnPortManage.getInstance()
				.getSerialParam(CONNECT_TYPE.COM2);
		if (com_port2 != null) {
			com_port2.nBaudRate = com2Baud;
			com_port2.nDataBits = com2DataLen;
			com_port2.nStopBit = com2Stop;
			com_port2.nParityType = com2Check;
			CmnPortManage.getInstance().setSerialParam(com_port2);
		}
		// 修改com1 连接参数 到数据库
		systemInfoBiz.updateConnectParaToDataBase((short) 3, com1Baud,
				com1Check, com1Stop, com1DataLen);
		// 修改com2 连接参数 到数据库
		systemInfoBiz.updateConnectParaToDataBase((short) 4, com2Baud,
				com2Check, com2Stop, com2DataLen);
	}

	/**
	 * 从系统参数里面读取com1波特率写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom1BaudToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com1Info) {
			com1Info = systemInfoBiz.findComInfo(3);
		}
		if (com1Info!=null) {
			int com1baud = com1Info.getnBaudRate();
			Vector<Integer> mydataListInt = new Vector<Integer>();

			mydataListInt.add(com1baud);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_32;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
		
	}

	/**
	 * 从系统参数里面读取com2波特率写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom2BaudToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com2Info) {
			com2Info = systemInfoBiz.findComInfo(4);
		}
		
		if (com2Info!=null) {
			int com2baud = com2Info.getnBaudRate();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com2baud);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_32;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);  
		}
		
	}

	/**
	 * 从系统参数里面读取com1数据长度写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom1DataBitsToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com1Info) {
			com1Info = systemInfoBiz.findComInfo(3);
		}
		
		if (com1Info!=null) {
			int com1DataBit = com1Info.getnDataBits();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com1DataBit);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
		
	}

	/**
	 * 从系统参数里面读取com2数据长度写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom2DataBitsToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com2Info) {
			com2Info = systemInfoBiz.findComInfo(4);
		}
		
		if (com2Info!=null) {
			int com2databit = com2Info.getnDataBits();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com2databit);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
		
	}

	/**
	 * 从系统参数里面读取com1的校验位写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom1CheckToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com1Info) {
			com1Info = systemInfoBiz.findComInfo(3);
		}
		if (com1Info!=null) {
			int com1Check = com1Info.getnCheckType();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com1Check);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
		
	}

	/**
	 * 从系统参数里面读取com2的校验位写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom2CheckToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com2Info) {
			com2Info = systemInfoBiz.findComInfo(4);
		}
		if (com2Info!=null) {
			int com2Check = com2Info.getnCheckType();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com2Check);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
		
	}

	/**
	 * 从系统参数里面读取com1的停止位写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom1StopToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		if (null == com1Info) {
			com1Info = systemInfoBiz.findComInfo(3);
		}
		if (com1Info!=null) {
			int com1stop = com1Info.getnStopBit();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com1stop);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
		
	}

	/**
	 * 从系统参数里面读取com2的停止位写入地址
	 * 
	 * @param addrProp
	 */
	private void setcom2StopToAddr(AddrProp addrProp) {
		if (systemInfoBiz == null) {
			systemInfoBiz = new SystemInfoBiz();
		}
		
		if (null == com2Info) {
			com2Info = systemInfoBiz.findComInfo(4);
		}
		
		if (com2Info!=null) {
			int com2stop = com2Info.getnStopBit();
			Vector<Integer> mydataListInt = new Vector<Integer>();
			mydataListInt.add(com2stop);
			mSendData = getMSendData();
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			PlcRegCmnStcTools.setRegIntData(addrProp, mydataListInt, mSendData);
		}
	
	}

	/**
	 * 获取各个通信口的通信状态写入地址
	 */
	private int myCom1Status = 0;
	private int myCom2Status = 0;
	private int myEthStatus = 0;

	private void setCommunicateStatus() {
		// com1的通信状态
		SKCommThread thread1 = SKCommThread.getComnThreadObj(CONNECT_TYPE.COM1);
		if (null != thread1) {
			int com1Status = thread1.getCmnCodeInfo();
				wirteStatus(SystemAddress.getInstance().cOM1_StatusAddr(),
						com1Status);
		}
		// com2的通信状态
		SKCommThread thread2 = SKCommThread.getComnThreadObj(CONNECT_TYPE.COM2);
		if (null != thread2) {
			int com2Status = thread2.getCmnCodeInfo();
				wirteStatus(SystemAddress.getInstance().cOM2_StatusAddr(),
						com2Status);
		}
		// 以太网的通信状态
		SKCommThread eth = SKCommThread.getComnThreadObj(CONNECT_TYPE.NET0);
		if (null != eth) {
			int ethStatus = eth.getCmnCodeInfo();
				wirteStatus(SystemAddress.getInstance().eth_StatusAddr(),
						ethStatus);
		}
	}

	/**
	 * 写入通信口的状态
	 * 
	 * @param addr
	 */
	private void wirteStatus(AddrProp addr, int value) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mydataListInt.add(value);
		mSendData = getMSendData();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(addr, mydataListInt, mSendData);
	}

	/**
	 * 写入当前用户名去地址
	 */
	private String currentUserName = "";

	public void setCurrentUserToAddr() {
		AddrProp addr = SystemAddress.getInstance().Sys_CurrentUserAddr();
		UserInfo info = SystemInfo.getGloableUser();
		if (null != info) {
			String currentUserName2 = info.getName();
			if (null != currentUserName2) {
				if (currentUserName != currentUserName2) {
				//	Log.d(TAG, "写入地址的用户名：" + currentUserName2);
					setUnicodeToAddr(addr, currentUserName2);
					currentUserName = currentUserName2;
				}
			}
		}
	}

	/**
	 * 写入ascII到地址
	 * 
	 * @param addr
	 * @param value
	 */
	private void setUnicodeToAddr(AddrProp addr, String value) {
		Vector<Byte> mybyteList = new Vector<Byte>();
		byte[] bytes = null;
		try {
			bytes = value.getBytes("UNICODE");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		if (bytes != null) {
			if (bytes.length != 0) {
				for (int i = 2; i < bytes.length; i++) {
					mybyteList.add(bytes[i]);
				}
			}
			if (mybyteList.size() != 0) {
				mSendData = getMSendData();
				mSendData.eDataType = DATA_TYPE.ASCII_STRING;
				mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;

				PlcRegCmnStcTools.setRegAsciiData(addr, mybyteList, mSendData);
			}
		}
	}

	/**
	 * 获取 SEND_DATA_STRUCT
	 * 
	 * @return
	 */
	private SEND_DATA_STRUCT getMSendData() {
		if (null == mSendData) {
			mSendData = new SEND_DATA_STRUCT();
		}
		return mSendData;
	}

	/**
	 * 将值写入位地址
	 * 
	 * @param value
	 * @param addrprop
	 */
	public void writeBitAddr(int value, AddrProp addrprop) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mSendData = getMSendData();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		mSendData.eDataType = DATA_TYPE.BIT_1;
		mydataListInt.add(value);
		PlcRegCmnStcTools.setRegIntData(addrprop, mydataListInt, mSendData);
	}
	
	/**
	 * 将值写入16位地址
	 * 
	 * @param value
	 * @param addrprop
	 */
	public void write16WordAddr(int value, AddrProp addrprop) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		mSendData = getMSendData();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		mSendData.eDataType = DATA_TYPE.INT_16;
		mydataListInt.add(value);
		PlcRegCmnStcTools.setRegIntData(addrprop, mydataListInt, mSendData);
	}


	/**
	 * 更新U盘的挂载状态
	 * 
	 * @param U盘当前是否已经挂载
	 * */
	public void setUDiskMntState(boolean isMounted) {
		int value = (isMounted ? 1 : 0);
		writeBitAddr(value, SystemAddress.getInstance().isUdiskAddr());
	}

	/**
	 * 返回当前U盘是否已经挂载
	 * 
	 * @return 当前是否挂载了U盘
	 * */
	public boolean isUDiskMounted() {
		int ret = readBitAddr(SystemAddress.getInstance().isUdiskAddr());
		return (ret == 0 ? false : true);
	}

	/**
	 * 更新SD卡的挂载状态
	 * 
	 * @param SD卡当前是否已经挂载
	 * */
	public void setSDCardMntState(boolean isMounted) {
		int value = (isMounted ? 1 : 0);
		writeBitAddr(value, SystemAddress.getInstance().isSDCardAddr());
	}

	/**
	 * 返回当前SD卡是否已经挂载
	 * 
	 * @return 当前是否挂载了SD卡
	 * */
	public boolean isSDCardMounted() {
		int ret = readBitAddr(SystemAddress.getInstance().isSDCardAddr());
		return (ret == 0 ? false : true);
	}

	/**
	 * 读取报警数据 查看是否有声音 1 有声音 0 没声音
	 */
	public void setIsAlarmBeep() {
		// 是否有声音
		boolean boo = AlarmGroup.getInstance().isAlarmSound();
		if (boo) {
			writeBitAddr(1, SystemAddress.getInstance().isAlarmBeepAddr());
		} else {
			writeBitAddr(0, SystemAddress.getInstance().isAlarmBeepAddr());
		}
	}

	/**
	 * 读取是否有实时报警发生 写入地址 1 有 0 没有
	 */
	private void setReadAlarmOpen() {
		if (AlarmGroup.getInstance().isAlarmSound()) {
			writeBitAddr(1, SystemAddress.getInstance().isAlarmAddr());

		} else {
			writeBitAddr(0, SystemAddress.getInstance().isAlarmAddr());
		}
	}

	private ICallBack clearCall;

	public ICallBack getClearCall() {
		return clearCall;
	}

	public void setClearCall(ICallBack clearCall) {
		if (clearCall==null) {
			return;
		}
		remove(clearCall);
		mMessageList.add(clearCall);
	}

	public interface ICallBack {

		void clearMessage(boolean reuslt); // 清空所有的留言消息

	};
	
	private static Vector<ICallBack> mMessageList=new Vector<SystemVariable.ICallBack>();
	private void remove(ICallBack callBack){
		for (int i = 0; i < mMessageList.size(); i++) {
			if (mMessageList.get(i)==callBack) {
				mMessageList.remove(i);
			}
		}
	}

	/**
	 * 清除自动复位
	 * 
	 * @author Administrator
	 * 
	 */
	public interface autoCall {

		void autoChange(AddrProp addr); // 清空所有的留言消息

	};


	/**
	 * 读取int类型的字地址值，
	 * 
	 * @param addrProp
	 * @param
	 * @return
	 */
	private int readWordAddr(AddrProp addrProp) {
		if (dataListInt == null) {
			dataListInt = new Vector<Integer>();
		} else {
			dataListInt.clear();
		}
		mSendData = getMSendData();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		mSendData.eDataType = DATA_TYPE.INT_16;
		boolean b = PlcRegCmnStcTools.getRegIntData(addrProp, dataListInt,
				mSendData);
		int addrValue = 0;
		if (b) {
			if (!dataListInt.isEmpty()) {
				addrValue = dataListInt.get(0);
			}
		}
		return addrValue;
	}
	
	private Vector<Short> mSDataList;
	private Vector<Short> read16WordsAddr(Vector<Byte> nStatusValue,int nAddrLen){
		Vector<Short> temps=new Vector<Short>();
		
		if (mSDataList==null) {
			mSDataList=new Vector<Short>();
		}else {
			mSDataList.clear();
		}
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSDataList);
		if (result) {
			if (mSDataList.size()==nAddrLen) {
				for (int i = 0; i < mSDataList.size(); i++) {
					temps.add(mSDataList.get(i));
				}
			}
		}
		
		return temps;
	}
	
	/**
	 * 
	 * @param addrProp
	 * @return
	 */
	private int readInt_32_WordAddr(AddrProp addrProp) {
		if (dataListInt == null) {
			dataListInt = new Vector<Integer>();
		} else {
			dataListInt.clear();
		}
		mSendData = getMSendData();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
		mSendData.eDataType = DATA_TYPE.INT_32;
		PlcRegCmnStcTools.getRegIntData(addrProp, dataListInt, mSendData);
		int addrValue = 0;
		if (!dataListInt.isEmpty()) {
			addrValue = dataListInt.get(0);
		}
		return addrValue;
	}
	
	/**
	 * 转换编码 再显示
	 * @param temp return 转换编码之后的 要显示的字符串
	 */
	private String converCodeShow(byte[] bTemp,boolean unicode) {
		String returnValue = new String(bTemp);
		try {
			if (unicode) {
				returnValue = new String(bTemp, "UNICODE");

			} else {
				returnValue = new String(bTemp);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return returnValue;
	}
	
	public static boolean isMobileNO(String mobiles){  
		  
		if (mobiles==null||mobiles.equals("")) {
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Matcher m = p.matcher(mobiles);  
		  
		return m.matches();  
		  
	}  
	
}
