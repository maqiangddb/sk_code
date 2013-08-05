package com.android.Samkoonhmi.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.BitSceneModle;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TimeSettingInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.GOTO_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKWindowManage;
import com.android.Samkoonhmi.system.SystemVariable;

/**
 * 全局参数设置
 * 
 * @author Administrator
 * 
 */
public class ParameterSet {

	/**
	 * 对话框
	 */
	public AlertDialog dlg = null;

	private int wordValueChangeScene = 0;
	// 画面跳转
	private static final int SCENE_GOTO = 1;
	// 时间同步
	private static final int TIME_SYS = 2;
	private Vector<Integer> dataList = null;
	private Vector<Integer> sceneList = null;
	private SEND_DATA_STRUCT mSendData;
	private boolean first = true;
	private SystemInfoBiz sysBiz;

	// 单例
	private static ParameterSet sInstance = null;

	public synchronized static ParameterSet getInstance() {
		if (sInstance == null) {
			sInstance = new ParameterSet();
		}
		return sInstance;
	}

	/**
	 * 写入当前画面号 scenceId 画面Id
	 */
	public void writeCurrentScence(int scenceId, AddrProp sceneAddr) {
		if (null == sceneAddr) {
			return;
		}
		if (null == sceneList) {
			sceneList = new Vector<Integer>();
		} else {
			sceneList.clear();
		}
		// 根据画面 序号获取画面编号
		int sid = SKSceneManage.getInstance().getSceneBySid(scenceId);
		sceneList.add(sid);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(sceneAddr, sceneList, mSendData);

	}

	/**
	 * 写入当前配方号 recipeGropId 配方组号
	 * @param recipeId 配方Id
	 */
	public void writeCurrentRecipe(int recipeGropId, int recipeId,
			AddrProp recipeAddr) {
		// Log.d("recipeselect", "写入当前配方进plc 组号：" + recipeGropId + ",配方号："
		// + recipeId);
		if (null == recipeAddr) {
			return;
		}
		// Log.d("recipeselect", "地址长度：" + appr.nAddrLen);
		if (null == dataList) {
			dataList = new Vector<Integer>();
		} else {
			dataList.clear();
		}
		dataList.add(recipeGropId);
		dataList.add(recipeId);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(recipeAddr, dataList, mSendData);
	}
	
	/**
	 * 系统设置
	 * plc控制配方地址监视
	 */
	public void setRecipeNotice(AddrProp addrProp){
		if (addrProp==null) {
			return;
		}
		
		SKPlcNoticThread.getInstance().addNoticProp(addrProp,
				recipeCall, false);
	}
	
	/**
	 * 修改当前配方组号
	 */
	private Vector<Integer> dataListInt=null;
	SKPlcNoticThread.IPlcNoticCallBack recipeCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int gid = 0;
			int cid=0;
			if (null == dataListInt) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					dataListInt);
			if (valueBool && dataListInt.size()==2) {
				gid = dataListInt.get(0);
				cid=dataListInt.get(1);
				
				//修改当前配方组, 当前配方号不变
				RecipeDataCentre.getInstance().setCurrRecipe(gid,
						SystemInfo.getCurrentRecipe().getCurrentRecipeId());
				
				//修改当前配方 当前组号不变
				RecipeDataCentre.getInstance().setCurrRecipe(
						SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId(),
						cid);
			}

		}
	};

	/**
	 * 写入当前语言信息
	 * @param languageId-语言Id
	 */
	public void writeCurrentLanguage(int languageId, AddrProp languageAddr) {
		// Log.d("Scene", "写入语言编号，编号id:" + languageId + ",地址id：" + apprId);
		if (null == languageAddr) {
			return;
		}
		if (null == dataList) {
			dataList = new Vector<Integer>();
		} else {
			dataList.clear();
		}
		dataList.add(languageId);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(languageAddr, dataList, mSendData);
	}

	/**
	 * 字地址控制画面切换
	 */
	public void changSceneByWordAddr(AddrProp wordSceneAddr) {
		if (null == wordSceneAddr) {
			return;
		}
		// 判断是否选择了字地址控制画面切换
		if ((SystemParam.WORD_CHANGE_SCENE & SystemInfo.getnSetBoolParam()) == SystemParam.WORD_CHANGE_SCENE) {
			// 字地址切换画面

			if (null != wordSceneAddr) {
				// System.out.println("注册了接口 字切换画面的接口：：：地址类型："
				// + wordAddrProp.nRegIndex);
				SKPlcNoticThread.getInstance().addNoticProp(wordSceneAddr,
						wordCall, false);
			}
		}
	}

	/**
	 * 字地址控制画面切换接口
	 */
	private Vector<Short> mIData = null;
	SKPlcNoticThread.IPlcNoticCallBack wordCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub

			int num = 0;
			if (null == mIData) {
				mIData = new Vector<Short>();
			} else {
				mIData.clear();
			}
			boolean valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
					mIData);
			if (valueBool && 0 != mIData.size()) {
				num = mIData.get(0);
			}
			// System.out.println("字地址通知改变的画面号sceneId::" + sceneId);
			if (num > -1 && wordValueChangeScene != num) {

				if (SystemInfo.getCurrentScenceId() == SKSceneManage
						.getInstance().getSceneByNum(num)) {
					// System.out.println("字地址通知画面号与当前画面号相同");
				} else {
					// System.out.println("字地址通知画面切换：" + sceneId);
					handler.removeMessages(SCENE_GOTO);
					handler.obtainMessage(SCENE_GOTO, num, 0).sendToTarget();
				}
			}
			wordValueChangeScene = num;
		}

	};

	/**
	 * 位地址控制画面切换
	 */
	public void changeSceneByBitAddr() {
		if (!SystemInfo.isbBitScene()) {
			return;
		}
		if (null == SystemInfo.getBitSceneList()) {
			return;
		}
		if (SystemInfo.getBitSceneList().size() == 0) {
			return;
		}
		
		
		//注册地址监视回调
		for (int i = 0; i < SystemInfo.getBitSceneList().size(); i++) {
			BitSceneModle modle=SystemInfo.getBitSceneList().get(i);
			if (modle!=null) {
				CallbackItem item=new CallbackItem();
				item.eDataType= DATA_TYPE.BIT_1;
				item.onRegister(modle.getnBitAddress(), true);
				modle.setItem(item);
			}
		}
		
		Thread mThread=new Thread(){

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				super.run();
				try {
					while (true) {
						SceneByBitAddr();
						sleep(100);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("ParameterSet", "Scene by bitAddr error!");
				}
			}
			
		};
		
		mThread.setName("SceneByBitAddr");
		mThread.start();

		
	}
	
	private void SceneByBitAddr(){
		
		// 地址值
		int addrValue = 0;
		for (int i = 0; i < SystemInfo.getBitSceneList().size(); i++) {
			
			BitSceneModle bit = SystemInfo.getBitSceneList().get(i);
			
			if (bit.getItem()!=null) {
				addrValue=bit.getItem().getnIValue();
			}
			
			// 如果地址值跟约定好的状态值相等 则进去继续判断
			if (addrValue == bit.getnStatus()) {
				//Log.d("ParameterSet", "addrValue:"+addrValue);
				
				// 如果地址值 与 上一次的不相等 说明地址值已经改变
				if (addrValue != bit.getnAddressValue()) {
					
					if (bit.getnSceneId() > -1) {
						int sceneNum = SKSceneManage.getInstance()
								.getSceneByNum(bit.getnSceneId());
						if (SystemInfo.getCurrentScenceId() != sceneNum) {
							//Log.d("ParameterSet", "sceneNum:"+sceneNum);
							handler.removeMessages(SCENE_GOTO);
							handler.obtainMessage(SCENE_GOTO,bit.getnSceneId(), 0).sendToTarget();

						}
					}
					
					// 如果设置了自动复位 设置成之前的状态
					if (bit.isbRest()) {
						if( bit.getnStatus() == 0)
						{
							setBitReset(1, bit.getnBitAddress());
						}else{
							setBitReset(0, bit.getnBitAddress());
						}
					}
				}
			}
			
			// 重新设置取出来的地址值
			bit.setnAddressValue(addrValue);
			
		}
	}

	/**
	 * 设置位地址复位
	 */
	private void setBitReset(int value, AddrProp addrprop) {
		if (null == addrprop) {
			return;
		}
		SEND_DATA_STRUCT sendData = new SEND_DATA_STRUCT();
		sendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		sendData.eDataType = DATA_TYPE.BIT_1;
		Vector<Integer> dataList = new Vector<Integer>();
		dataList.add(value);
		// Log.d("bit", "自动复位写入值："+value);
		PlcRegCmnStcTools.setRegIntData(addrprop, dataList, sendData);
	}

	private int[] type = new int[] { 0, 0 };
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			goScene(msg);
		}

	};
	
	private synchronized void goScene(Message msg){
		type[0] = 0;
		type[1] = 0;
		if (msg.what == SCENE_GOTO) {

			int sid = SKSceneManage.getInstance().getSceneByNum(msg.arg1);

			if (SKSceneManage.getInstance().hasScene(sid)) {
				type[0] = 1;
			} else {
				if (SKSceneManage.getInstance().hasWindow(sid)) {
					type[1] = 1;
				}
			}
			if (type[0] == 1) {
				// 场景
				//Context context = SKSceneManage.getInstance().mContext;
//				if (context != null) {
//					SKWindowManage.getInstance(context).closeWindow(1);
//				}
				//SKSceneManage.getInstance().sceneGoto(sid,true,6);
				SKSceneManage.getInstance().gotoWindow(0, sid, true, 6,GOTO_TYPE.PARAMETER);
			} else if (type[1] == 1) {
				// 窗口
				//Context context = SKSceneManage.getInstance().mContext;
//				if (context != null) {
//					SKWindowManage.getInstance(context).closeWindow(1);
//				}
				SKProgress.hide();// 隐藏等待框
				int id = DBTool.getInstance().getmSceneBiz().getWindowId(sid);
				if (id > -1) {
					//SKWindowManage.getInstance(context).showWindow(id);
					SKSceneManage.getInstance().gotoWindow(1, id, false, 0,GOTO_TYPE.PARAMETER);
				}
			}
		} else if (msg.what == TIME_SYS) {
			// 时间同步
			if (info != null) {
				if (info.getnSynchTime() == 1) {
					// 时间写入PLC
					write(info.getmSynchAddr());
				} else if (info.getnSynchTime() == 2) {
					// 从PLC读取时间
					read(info.getmSynchAddr());
				}
				if (bTimeSys) {
					handler.sendEmptyMessageDelayed(TIME_SYS,
							info.getnTime() * 1000);
				}
			}
		}
	}

	/**
	 * 超时注销用户
	 */
	public void outTimeLogout() {
		//注销将空用户设置为当前用户
		SystemInfo.setGloableUser(getNullUser());
		//将当前用户写入寄存器
		SystemVariable.getInstance().setCurrentUserToAddr();
	}
	
	/**
	 * 获取空用户
	 * @return
	 */
    public UserInfo getNullUser()
    {
    	UserInfo user = new UserInfo();
		user.setGroupId(new ArrayList<Integer>());
		user.setId(-1);
		user.setName(" ");
		user.setPassword("");
		user.setDescript("");
		return user;
    }
	
    /**
	 * 从数据库获取登录用户信息
	 * 
	 * @param userNameValue
	 * @param passwordValue
	 * @return
	 */
	public boolean getUserFromBase(String userNameValue, String passwordValue) {
		// TODO Auto-generated method stub
		UserInfoBiz userBiz = new UserInfoBiz();
		UserInfo info = userBiz.getUser(userNameValue, passwordValue);
		SystemInfo.setGloableUser(info);
		//将当前用户写入寄存器
		SystemVariable.getInstance().setCurrentUserToAddr();
		boolean b = false;
		if (null != SystemInfo.getGloableUser()
				&& null != SystemInfo.getGloableUser().getName()) {

			b = true;
		}
		return b;

	}

	/**
	 * 超出使用时间 使用实效授权
	 */
	public boolean outTimeUse(Context context) {
		boolean flag = false;

		try {
			// 结束使用的日期 
			// Date useTime = new Date();
			// 获取是按照哪种授权方式，bProtectType ;//时效授权方式(按使用时间（false） 按截至日期（true）
			boolean style = SystemInfo.isbProtectType();
			if (null != SystemInfo.getOnePassWord()) {
				String dateString = SystemInfo.getOnePassWord().getsTimeLimit();

				if (style) {
					// 按照截止日期(yyyy/MM/dd hh:mm)
					flag = ParameterSet.getInstance().isOutTimeByDate(
							dateString);
				} else {

					// 按照使用天数
					int number = Integer.parseInt(dateString);
					// 从文件中读取已经使用的天数
					SharedPreferences sharedPreferences = context
							.getSharedPreferences("hmiprotct", 0);
					int dateNumber = sharedPreferences.getInt("dateNumber", -1);
					if (dateNumber > number || number == 0 || dateNumber == number) {// 如果记录的天数大于或者等于可以使用的天数，则超出实效
						flag = true;
					}
				}
			} else {
				// Log.d("pass", "没有实效密码");
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;

	}

	/**
	 * 
	 * @return 返回是否超出使用时间
	 */
	public boolean isOutTimeByDate(String limitTimeString) {
		boolean returnBool = false;
		Date currentDate = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		
		try{
		currentDate = format.parse(format.format(currentDate));
		Date stopDate = format.parse(limitTimeString);
		
		// 如果超时时间小于当前时间 ,
		if (stopDate.getTime() < currentDate.getTime() || stopDate.getTime() == currentDate.getTime()) {
			returnBool = true;
		} else {
			returnBool = false;
		}
		}catch(Exception e)
		{
			
		}

		return returnBool;
	}

	/**
	 * 时间同步
	 */
	private Context mContext;
	private TimeSettingInfo info;
	public boolean bTimeSys = false;
	public void SysTime(Context context) {
		mContext = context.getApplicationContext();

		info = DBTool.getInstance().getmTimeSettingBiz().getTimeSetting();

		if (info != null) {

			/**
			 * nSynchTime=0,不同步 =1,写时间到plc =2,从plc读取时间
			 */
			if (info.getnSynchTime() == 0) {
				return;
			} else {
				if (info.getnSynchTime() == 1) {
					// 时间写入PLC
					if (info.getnExeType() == 1) {
						// 时间间隔触发
						write(info.getmSynchAddr());
						bTimeSys = true;
						handler.sendEmptyMessageDelayed(TIME_SYS,
								info.getnTime() * 1000);
					}
				} else if (info.getnSynchTime() == 2) {
					// 从PLC读取时间
					if (info.getnExeType() == 1) {
						// 时间间隔触发
						read(info.getmSynchAddr());
						bTimeSys = true;
						handler.sendEmptyMessageDelayed(TIME_SYS,
								info.getnTime() * 1000);
					}
				}
			}

			registWatchForTimeSys();
		}
	}

	/**
	 * 注册触发时间同步地址通知
	 */
	private void registWatchForTimeSys() {
		if (info != null) {
			if (info.getnSynchTime() == 0 || info.getnExeType() == 1) {
				return;
			}
			if (info.getmTriggerAddr() != null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmTriggerAddr(), watchCall, true);
			}
		}
	}

	/**
	 * 触发地址监视
	 */
	SKPlcNoticThread.IPlcNoticCallBack watchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {

			if (nStatusValue.size() == 0) {
				return;
			}
			if (nStatusValue.get(0) == 1) {
				if (info != null) {

					if (info.isbAutoReset()) {
						// 自动复位
						Vector<Integer> dataList = new Vector<Integer>();
						SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
						mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
						mSendData.eDataType = DATA_TYPE.BIT_1;
						dataList.add(0);
						if (info.getmTriggerAddr() != null) {
							PlcRegCmnStcTools
									.setRegIntData(info.getmTriggerAddr(),
											dataList, mSendData);
						}
					}

					if (info.getnSynchTime() == 1) {
						// 时间写入PLC
						write(info.getmSynchAddr());
					} else if (info.getnSynchTime() == 2) {
						// 从PLC读取时间
						read(info.getmSynchAddr());
					}

				}
			}
		}
	};

	/**
	 * 时间写入plc
	 */
	public void write(AddrProp addrProp) {

		Vector<Integer> dataList = new Vector<Integer>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;

		Calendar c = Calendar.getInstance();

		// 年
		String year = c.get(Calendar.YEAR) + "";
		year =  Long.toString(DataTypeFormat.bcdStrToInt(year, 16));
		if(year.equals(""))
		{
			return;
		}
		dataList.add(Integer.valueOf(year));

		// 月
		String three = c.get(Calendar.MONTH) + "";
		three = Long.toString(DataTypeFormat.bcdStrToInt(three, 16));
		if (three.equals("")) {
			return;
		}
		dataList.add(Integer.valueOf(three) + 1);

		// 日
		String four = c.get(Calendar.DAY_OF_MONTH) + "";
		four = Long.toString(DataTypeFormat.bcdStrToInt(four, 16));
		if (four.equals("")) {
			return;
		}
		dataList.add(Integer.valueOf(four));

		// 时
		String five = c.get(Calendar.HOUR_OF_DAY) + "";
		five = Long.toString(DataTypeFormat.bcdStrToInt(five, 16));
		if (five.equals("")) {
			return;
		}
		dataList.add(Integer.valueOf(five));

		// 分
		String six = c.get(Calendar.MINUTE) + "";
		six = Long.toString(DataTypeFormat.bcdStrToInt(six, 16));
		if (six.equals("")) {
			return;
		}
		dataList.add(Integer.valueOf(six));

		// 秒
		String seven = c.get(Calendar.SECOND) + "";
		seven = Long.toString(DataTypeFormat.bcdStrToInt(seven, 16));
		if (seven.equals("")) {
			return;
		}
		
		dataList.add(Integer.valueOf(seven));
		//星期 1代表星期日 7代表星期六
		String dayOfWeek = c.get(Calendar.DAY_OF_WEEK)+"";
		dayOfWeek = Long.toString(DataTypeFormat.bcdStrToInt(dayOfWeek, 16));
		if (dayOfWeek.equals("")) {
			return;
		}
		int week=Integer.valueOf(dayOfWeek)-1;
		if (week==0) {
			week=7;
		}
		dataList.add(week);

		if (addrProp != null) {
			addrProp.nAddrLen = 7;
			PlcRegCmnStcTools.setRegIntData(addrProp, dataList,
					mSendData);
		}

	}

	/**
	 * 从plc读取时间
	 */
	public void read(AddrProp addrProp) {

		Vector<Double> dataList = new Vector<Double>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = DATA_TYPE.BCD_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		ArrayList<String> dataTime = new ArrayList<String>();

		try {
			if (addrProp != null) {
				addrProp.nAddrLen = 7;
				boolean bSuccess = PlcRegCmnStcTools.getRegDoubleData(
						addrProp, dataList, mSendData);
				if (bSuccess) {
					if (!dataList.isEmpty()) {
						if (dataList.size() < 7) {
							return;
						}
						for (int i = 0; i < dataList.size(); i++) {
							double addrValue = dataList.get(i);
							String temp = DataTypeFormat.intToBcdStr(
									(long) addrValue, false);
							if (temp.equals("")) {
								return;
							}
						
							if (i == 1) {
								// 月份
								int time = Integer.valueOf(temp);
								if (time <= 0 || time > 12) {
									return;
								}
							} else if (i == 2) {
								// 日
								int time = Integer.valueOf(temp);
								if (time <= 0 || time > 31) {
									return;
								}
							} else if (i == 3) {
								// 时
								int time = Integer.valueOf(temp);
								if (time < 0 || time > 24) {
									return;
								}
							} else if (i == 4) {
								// 分
								int time = Integer.valueOf(temp);
								if (time < 0 || time > 60) {
									return;
								}
							} else if (i == 5) {
								// 秒
								int time = Integer.valueOf(temp);
								if (time < 0 || time > 60) {
									return;
								}
							}
							dataTime.add(temp);
						}

						if (dataTime.size() < 7) {
							return;
						}
						Calendar c = Calendar.getInstance();
						c.set(Calendar.YEAR,
								Integer.valueOf(dataTime.get(0)
										));
						c.set(Calendar.MONTH,
								Integer.valueOf(dataTime.get(1)) - 1);
						c.set(Calendar.DAY_OF_MONTH,
								Integer.valueOf(dataTime.get(2)));
						c.set(Calendar.HOUR_OF_DAY,
								Integer.valueOf(dataTime.get(3)));
						c.set(Calendar.MINUTE, Integer.valueOf(dataTime.get(4)));
						c.set(Calendar.SECOND, Integer.valueOf(dataTime.get(5)));
						c.set(Calendar.MILLISECOND, 0);

						long when = c.getTimeInMillis();
						if (when < 0) {
							return;
						}
						if (when / 1000 < Integer.MAX_VALUE) {
							Intent intent = new Intent();
							intent.setAction("com.samkoon.settime");
							intent.putExtra("time", when);
							mContext.sendBroadcast(intent);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Parameter", "read plc time error!");
		}

	}
	/**
	 * 修改系统当前配方
	 * 
	 * @param reci
	 */
	public void myUpdateCurrentRecipe(int recipeId,int gId) {
		sysBiz = new SystemInfoBiz();
		int sysRecipeId = SystemInfo.getCurrentRecipe().getCurrentRecipeId();// 系统当前配方id
		int sysRecipeGropId = SystemInfo.getCurrentRecipe()
				.getCurrentGroupRecipeId();// 系统当前配方组Id
			// 如果选择的配方组id或者配方Id不与系统当前的配方相同 则改变系统当前配方 防止点击重复的多次写入数据库
			if (sysRecipeId != recipeId
					|| sysRecipeGropId != gId) {
				CurrentRecipe currentRecipe = new CurrentRecipe(
						recipeId, gId);
				// 改变当前配方
				// SystemInfo.setCurrentRecipe(currentRecipe);
				RecipeDataCentre.getInstance().setCurrRecipe(
						gId, recipeId);
				// 把当前配方写入数据库
				sysBiz.updateCurrentRecipe(currentRecipe);

		}
	}
}
