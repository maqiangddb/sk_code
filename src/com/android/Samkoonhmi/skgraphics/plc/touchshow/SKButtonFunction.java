package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SceneBiz;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skbutton.BitButtonInfo;
import com.android.Samkoonhmi.model.skbutton.PeculiarButtonInfo;
import com.android.Samkoonhmi.model.skbutton.SceneButtonInfo;
import com.android.Samkoonhmi.model.skbutton.WordButtonInfo;
import com.android.Samkoonhmi.model.skglobalcmn.RecipeDataProp;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skcommon.SkCommon;
import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.BUTTON.BIT_OPER_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.PECULIAR_OPER;
import com.android.Samkoonhmi.skenum.BUTTON.WATCH_TYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.GOTO_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.EditRecipeInfo;
import com.android.Samkoonhmi.skwindow.DateTimeSetting;
import com.android.Samkoonhmi.skwindow.DateTimeSetting.TYPE;
import com.android.Samkoonhmi.skwindow.IPSet;
import com.android.Samkoonhmi.skwindow.SKEditUserDialog;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKRecipeDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.skwindow.SKUserOperDialog;
import com.android.Samkoonhmi.skwindow.SKWindowManage;
import com.android.Samkoonhmi.system.StorageStateManager;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.ResetService;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.SystemParam;
import com.android.Samkoonhmi.util.TASK;

/**
 * 开关功能
 */
public class SKButtonFunction {

	private Context mContext;
	private int sid=0;

	public SKButtonFunction(Context context,int sid) {
		this.mContext = context;
		this.sid=sid;
	}

	/**
	 * 位开关按钮 把值写入plc
	 * @param info-位开关实体对象
	 */
	public void writeToPlc(BitButtonInfo info, boolean down) {

		if (info == null) {
			return;
		}
		
		if (info.geteOperType() == null) {
			Log.e("SKButtonFunction", "bit button OPerType=null");
			return;
		}

		if (!down && (info.geteOperType() != BIT_OPER_TYPE.JOG)) {
			// 松开，并且不是点动
			return;
		}
		
		/**
		 * 点动松开时，延迟执行
		 */
		if (!down&&info.geteOperType()==BIT_OPER_TYPE.JOG) {
			Message msg=new Message();
			msg.what=JOD_UP;
			msg.obj=info;
			handler.sendMessageDelayed(msg, 150);
			//Log.d("SKScene", "job up ....");
			return;
		}

		int value = 0;
		Vector<Integer> dataList = new Vector<Integer>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
			mSendData.eDataType = DATA_TYPE.BIT_1;
		} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
			mSendData.eDataType = info.geteWatchDataType();
		} else {
			mSendData.eDataType = DATA_TYPE.BIT_1;
		}

		switch (info.geteOperType()) {
		case SET_BIT: // 置位
			value = 1;
			break;
		case RESET: // 复位
			value = 0;
			break;
		case JOG: // 点动
			if (down) {
				// 点下
				if (info.isbDownZero()) {
					value = 0;
				} else {
					value = 1;
				}
			} else {
				// 松开
				if (info.isbDownZero()) {
					value = 1;
				} else {
					value = 0;
				}
			}
			break;
		case REPLACE: // 交替
			if (info.getmBitAddress() != null) {
				boolean bSuccess = PlcRegCmnStcTools.getRegIntData(
						info.getmBitAddress(), dataList, mSendData);
				if (!bSuccess || dataList.isEmpty()) {
					return;
				}
				int temp = dataList.elementAt(0);
				if (temp <= 0) {
					value = 1;
				} else {
					value = 0;
				}
				dataList.clear();
			}
			break;
		}
		
		//Log.d("Button", "value="+value);
		dataList.add(value);
		if (info.getmBitAddress() != null) {
			//Log.d("SKScene", "click down :"+info.geteOperType()+",value:"+value);
			PlcRegCmnStcTools.setRegIntData(info.getmBitAddress(), dataList,
					mSendData);
		}

	}

	/**
	 * 字开关按钮
	 * @param info-字开关按钮实体对象
	 * @param init-true,表示先读取plc数据
	 */
	public boolean isDown;
	private WordButtonInfo wInfo;
	private double value = 0;
	private boolean isRegister=false;
	public void writeToPlc(WordButtonInfo info, boolean down,boolean init) {

		this.wInfo = info;
		this.isDown = down;
		if (!isDown || info == null) {
			//handler.removeMessages(WORD_DATE);
			isDown=false;
			if (isRegister) {
				isRegister=false;
				SKTimer.getInstance().getBinder().onDestroy(timeCall, 1);
			}
			return;
		}

		if (info == null || info.getmAddrProp() == null) {
			Log.e("SKButtonFunction", "word button data=null");
			return;
		}
		

		boolean loop = false;
		double min = 0;// 最小值
		double max = 0;// 最大值

		Vector<Double> dataList = new Vector<Double>();
		dataList.add(info.getnFinalValue());
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.geteDataType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;

		// 先读取plc里面的值
		if (init) {
			boolean bSuccess = PlcRegCmnStcTools.getRegDoubleData(
					info.getmAddrProp(), dataList, mSendData);
			if (bSuccess) {
				if (!dataList.isEmpty()) {
					value = dataList.elementAt(0);
				}
			}
		}
		
		dataList.clear();
		
		if (info.geteDataType() == null) {
			Log.e("SKButtonFunction", "word button DataType=null");
			return;
		}

		min=info.getnMin();
		max=info.getnMax();
		
		if (info.geteOperType() == null) {
			Log.e("SKButtonFunction", "word button OperType=null");
			return;
		}
		
		double temp = 0;
		temp = info.getnFinalValue();
		if (info.getbDynamicControl()) {
			// 从PLC中读出值
			temp = nDynamicValue;
		}
		switch (info.geteOperType()) {
		case INPUT_VALUE: // 数据写入
			if (temp < min || temp > max) {
				Log.d("SKButtonFunction", "word button, write data error");
				return;
			}
			
			break;
		case ADD: // 数值加
			if ((temp + value) < min
					|| (temp + value) > max) {
				if (value<=min||value>=max) {
					if (info.isbCycle()) {
						//绕回
						if (value<=min) {
							value=max;
							temp=value;
						}else {
							value=min;
							temp=value;
						}
					}else {
						Log.d("SKButtonFunction", "word button, write data error");
						return;
					}
				}else {
					if (temp>0) {
						if (info.isbCycle()) {
							//绕回
							double tt=value+temp-max;
							value=min+tt;
							temp=value;
						}else {
							value=max;
							temp=value;
						}
					}else {
						if (info.isbCycle()) {
							//绕回
							double tt=value+temp-min;
							value=max+tt;
							temp=value;
						}else{
							value=min;
							temp=value;
						}
					}
				}
			}else {
				temp = value + temp;
				value=temp;
			}
			break;
		case MINUS: // 数值减
			if ((value - temp) < min
					|| (value - temp) > max) {
				if (value<=min||value>=max) {
					if (info.isbCycle()) {
						//绕回
						if (value<=min) {
							value=max;
							temp=value;
						}else {
							value=min;
							temp=value;
						}
					}else {
						Log.d("SKButtonFunction", "word button, write data error");
						return;
					}
				}else {
					if (temp>0) {
						if (info.isbCycle()) {
							//绕回
							double tt=value-temp-min;
							value=max+tt;
							temp=value;
						}else {
							value=min;
							temp=value;
						}
					}else {
						if (info.isbCycle()) {
							//绕回
							double tt=value-temp-max;
							value=min+tt;
							temp=value;
						}else{
							value=max;
							temp=value;
						}
					}
				}
			}else {
				temp = value - temp;
				value=temp;
			}
			break;
		case ADD_LOOPER: // 连续加
			if ((temp + value) < min
					|| (temp + value) > max) {
				if (value<=min||value>=max) {
					if (info.isbCycle()) {
						//绕回
						if (value<=min) {
							value=max;
							temp=value;
						}else {
							value=min;
							temp=value;
						}
						loop = true;
					}else {
						isDown=false;
						Log.d("SKButtonFunction", "word button, write data error");
						return;
					}
				}else {
					if (temp>0) {
						if (info.isbCycle()) {
							//绕回
							double tt=value+info.getnFinalValue()-max;
							value=min+tt;
							temp=value;
							loop = true;
						}else {
							value=max;
							temp=value;
							isDown=false;
						}
					}else {
						if (info.isbCycle()) {
							//绕回
							double tt=value+temp-min;
							value=max+tt;
							temp=value;
							loop = true;
						}else{
							value=min;
							temp=value;
							isDown=false;
						}
					}
				}
			}else {
				temp = value + temp;
				value=temp;
				loop = true;
			}
			break;
		case MINUS_LOOPER: // 连续减
			if ((value - temp) < min
					|| (value - temp) > max) {
				if (value<=min||value>=max) {
					if (info.isbCycle()) {
						//绕回
						if (value<=min) {
							value=max;
							temp=value;
						}else {
							value=min;
							temp=value;
						}
						loop = true;
					}else {
						isDown=false;
						Log.d("SKButtonFunction", "word button, write data error");
						return;
					}
				}else {
					if (temp>0) {
						if (info.isbCycle()) {
							//绕回
							double tt=value-temp-min;
							value=max+tt;
							temp=value;
							loop = true;
						}else {
							value=min;
							temp=value;
							isDown=false;
						}
					}else {
						if (info.isbCycle()) {
							//绕回
							double tt=value-temp-max;
							value=min+tt;
							temp=value;
							loop = true;
						}else{
							value=max;
							temp=value;
							isDown=false;
						}
					}
				}
			}else {
				temp = value - temp;
				value=temp;
				loop = true;
			}
			break;
		}

		//Log.d("SKScene", ".....value:"+value);
		
		if (info.geteDataType()==DATA_TYPE.INT_16||info.geteDataType()==DATA_TYPE.INT_32
				||info.geteDataType()==DATA_TYPE.POSITIVE_INT_16
				||info.geteDataType()==DATA_TYPE.BCD_16) {
			int data=(int)temp;
			Vector<Integer> list = new Vector<Integer>();
			list.add(data);
			PlcRegCmnStcTools.setRegIntData(info.getmAddrProp(), list,
					mSendData);
		}else if(info.geteDataType()==DATA_TYPE.POSITIVE_INT_32
				|| info.geteDataType()==DATA_TYPE.BCD_32){
			long data=(long)temp;
			Vector<Long> list = new Vector<Long>();
			list.add(data);
			PlcRegCmnStcTools.setRegLongData(info.getmAddrProp(), list,
					mSendData);
		}else {
			dataList.add(temp);
			PlcRegCmnStcTools.setRegDoubleData(info.getmAddrProp(), dataList,
					mSendData);
		}
		
		if (loop) {
			// 按下连续加or连续减
			if (isDown) {
				if (!isRegister) {
					isRegister=true;
					SKTimer.getInstance().getBinder().onRegister(timeCall, 2);
				}
			}else {
				if (isRegister) {
					isRegister=false;
					SKTimer.getInstance().getBinder().onDestroy(timeCall, 2);
				}
			}
		}
	}
	
	private double nDynamicValue = 0;
	
	public void setnDynamicValue(double value2){
		nDynamicValue = value2;
	}
	


	private static final int JOD_UP=1;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case JOD_UP:
				//点动松开
				BitButtonInfo info=(BitButtonInfo)msg.obj;
				if (info!=null) {
					jobUp(info);
				}
				break;
			}
		}

	};
	
	/**
	 * 定时器回调
	 */
	SKTimer.ICallback timeCall=new SKTimer.ICallback() {
		
		@Override
		public void onUpdate() {
			if (isDown) {
				writeToPlc(wInfo, isDown,false);
			}
		}
	};

	/**
	 * 点动松开
	 */
	private void jobUp(BitButtonInfo info){
		
		int value = 0;
		Vector<Integer> dataList = new Vector<Integer>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
			mSendData.eDataType = DATA_TYPE.BIT_1;
		} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
			mSendData.eDataType = info.geteWatchDataType();
		} else {
			mSendData.eDataType = DATA_TYPE.BIT_1;
		}

		// 松开
		if (info.isbDownZero()) {
			value = 1;
		} else {
			value = 0;
		}
		
		dataList.add(value);
		if (info.getmBitAddress() != null) {
			//Log.d("SKScene", "click down :"+info.geteOperType()+",value:"+value);
			PlcRegCmnStcTools.setRegIntData(info.getmBitAddress(), dataList,
					mSendData);
		}
	}
	
	/**
	 * 画面开关按钮
	 * @param info-画面开关按钮实体对象
	 */
	public void sceneOper(SceneButtonInfo info) {
		if (info == null || info.geteOperScene() == null) {
			Log.e("SKButtonFunction", "SceneButton,OperScene=null");
			return;
		}
		if (mContext==null) {
			mContext=SKSceneManage.getInstance().mContext;
		}
		
		if (info.isbLogout()) {
			//注销用户
			ParameterSet.getInstance().outTimeLogout();
			SKSceneManage.getInstance().updateState();
		}
		if (info.getnSceneType()==0) {
		
			switch (info.geteOperScene()) {
			case  NEXT:
				//跳转到下个画面
				int cid = SKSceneManage.getInstance().nSceneId;
				int id = DBTool.getInstance().getmSceneBiz().getNextSceneId(cid);
				if (id > 0) {
					SKSceneManage.getInstance().gotoWindow(0, id, true, info.getnEnterType(),GOTO_TYPE.BUTTON);
				}
				break;
			case OPEN:
				// 跳转画面
				int sid = DBTool.getInstance().getmSceneBiz().getSceneId(info.getnTargetPage() + 1);
				if (sid > 0) {
					//跳转之前，关闭已经打开的窗口
					SKSceneManage.getInstance().gotoWindow(0, sid, true, info.getnEnterType(),GOTO_TYPE.BUTTON);
				}
				break;
			case BACK:
				//返回之前画面
				if (SKSceneManage.getInstance().hasScene(SKSceneManage.getInstance().getCurrentInfo().getnBeforeSId())) {
					SKSceneManage.getInstance().gotoWindow(0, SKSceneManage.getInstance().getCurrentInfo().getnBeforeSId(),false,info.getnEnterType(),GOTO_TYPE.BUTTON);
				}
				break;
				
			}
			
		}else if (info.getnSceneType()==1) {
			switch (info.geteOperScene()) {
			case  OPEN_WINDOW:
				if (info.getnTargetPage()+1==SKWindowManage.getInstance(mContext).nWindowId) {
					return;
				}
				// 跳转窗口
				SKProgress.hide();//隐藏等待框
				SKSceneManage.getInstance().gotoWindow(1, info.getnTargetPage() + 1, false, 0,GOTO_TYPE.BUTTON);
				break;
			case COSE_WINDOW:
				//关闭当前窗口
				SKWindowManage.getInstance(mContext).closeWindow(1);
				break;
			}
		}
	}

	/**
	 * 特殊开关按钮
	 * @param info-特殊开关按钮实体对象
	 */
	public SKEditUserDialog dialog;
	public DateTimeSetting timeSetting;
	public SKUserOperDialog uDialog;
	public DateTimeSetting collectSetting;
	public IPSet mIpSet;
	private SKHistoryTrends mTrends=null;
	public void peculiarOper(PeculiarButtonInfo info) {

		if (info == null || info.getePeculiarType() == null) {
			Log.e("SKButtonFunction", "PeculiarButton,PeculiarType=null");
			return;
		}
		
		if (mContext==null) {
			mContext=SKSceneManage.getInstance().mContext;
		}
		
		switch (info.getePeculiarType()) {
		case BACKLIGHT: // 背景灯开关
			// 调用接口
			SKSceneManage.getInstance().saveCurrenBright();
			SKSceneManage.getInstance().backlightoff();
			
			break;
		case RESET: // 系统重启
			if (mOperDialog==null||!mOperDialog.isShow) {
				mOperDialog=new SKSwitchOperDialog(mContext);
				mOperDialog.setiOperCall(new SKSwitchOperDialog.IOperCall() {
					
					@Override
					public void onConfirm() {
						mOperDialog.isShow=false;
						mOperDialog.hidePopWindow();
						reset();
					}
					
					@Override
					public void onCancel() {
						mOperDialog.isShow=false;
						mOperDialog.hidePopWindow();
					}
				});
				mOperDialog.showPopWindow();
			}
			break;
		case SET_SCENE_TIME: // 设置屏保时间
		{
			if (timeSetting==null||!timeSetting.showFlag) {
				Activity activity=SKSceneManage.getInstance().getActivity();
				if (activity==null) {
					return;
				}
				timeSetting= new DateTimeSetting(activity);
				timeSetting.setCanceledOnTouchOutside(false);
				timeSetting.onCreate(DateTimeSetting.TYPE.SCENE_TIME, 200, 230);
				timeSetting.showDialog(DateTimeSetting.TYPE.SCENE_TIME,
						info.getnLp(), info.getnTp(),200,230);
			}
			break;
		}
		case SET_SYSTEM_TIME: // 设置系统时间
		{
			if ((SystemInfo.getnSetBoolParam() & SystemParam.HMI_PROTECT) == SystemParam.HMI_PROTECT) {
				if (null != SystemInfo.getPassWord()) {
					if (0 != SystemInfo.getPassWord().size()) {
						SKToast.makeText("时间不可修改!", Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			if (timeSetting==null||!timeSetting.showFlag) {
				Activity activity=SKSceneManage.getInstance().getActivity();
				if (activity==null) {
					return;
				}
				timeSetting= new DateTimeSetting(activity);
				timeSetting.setCanceledOnTouchOutside(false);
				timeSetting.onCreate(DateTimeSetting.TYPE.SYSTEM_TIME, 200, 230);
				timeSetting.showDialog(DateTimeSetting.TYPE.SYSTEM_TIME,
						info.getnLp(), info.getnTp(),200,230);
			}
			break;
		}
		case CHANGE_USER: // 切换当前用户
			// 调用接口
			SKSceneManage.getInstance().turnToLoginPop();
			break;
		case USER_MANAGE: // 用户管理
			if (dialog==null||!dialog.show) {
				dialog= new SKEditUserDialog(SKSceneManage.getInstance()
						.getActivity());
				dialog.showDialog();
			}
			break;
		case TOUCH_SOUND: // 触摸声音开关
			// 调用接口
			boolean isTouchSound = (SystemParam.USE_TOUCH_SOUND & SystemInfo
					.getnSetBoolParam()) == SystemParam.USE_TOUCH_SOUND;// 为true
																		// 开启触摸声音
			if (isTouchSound) {
				// 关闭触摸声音
				SystemInfo.resetnSetBoolParam(SystemInfo.getnSetBoolParam()& (~SystemParam.USE_TOUCH_SOUND));
				
			} else {
				// 启动触摸声音
				SystemInfo.setnSetBoolParam(SystemInfo.getnSetBoolParam()| (SystemParam.USE_TOUCH_SOUND));
			}
			//写入将触摸声音写入地址
			SystemVariable.getInstance().setTouchSoundToAddr(SystemAddress.getInstance().enableBeepAddr());
			DBTool.getInstance().getmSystemInfoBiz().updateSysParam(SystemInfo.getnSetBoolParam());
			break;
		// case COPYRIGHT: // 产品授权
		// break;
		case CHANGE_LANGUAGE: // 语言切换
			SKLanguage.getInstance().getBinder()
			.onChange(info.getnLanguageId());
			break;
		case SCREENSHOT: // 屏幕截图
			if (loading) {
				return;
			}
			loading=true;
			SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK,1, null, callback, 0);
			break;
		case OPER_WINDOW: // 窗口操作
			windowOper(info);
			break;
		case SAMPLING: // 采样数据
			//清除历史数据 collectSetting
			//DataCollect.getInstance().msgClearAllHistory();
			
			if (collectSetting==null||!collectSetting.showFlag) {
				Activity activity=SKSceneManage.getInstance().getActivity();
				if (activity==null) {
					return;
				}
				collectSetting= new DateTimeSetting(activity);
				collectSetting.setCanceledOnTouchOutside(false);
				collectSetting.onCreate(DateTimeSetting.TYPE.COLLECT_CLEAR, 200, 200);
				collectSetting.showDialog(DateTimeSetting.TYPE.COLLECT_CLEAR,
						0, 0,200,200);
			}
			break;
		case FORMULA: // 配方操作
			formula(info);
			break;
		case ALARM: // 报警操作
			alarm(info);
			break;
		case EDIT_USER_PWD://修改用户密码
			if (uDialog==null||!uDialog.isShow) {
				uDialog=new SKUserOperDialog(mContext);
				uDialog.showPopWindow(0,320,260);
			}
			break;
		case LOGOUT_USER://注销用户
			if (uDialog==null||!uDialog.isShow) {
				uDialog=new SKUserOperDialog(mContext);
				uDialog.showPopWindow(1,220,160);
			}
			break;
		case WIFI://wifi
			//启动第三方apk
			if (mContext!=null) {
				for (PackageInfo pack : mContext.getPackageManager()
						.getInstalledPackages(PackageManager.GET_ACTIVITIES)) {
					
					//Log.d("SKScene", "name:"+pack.packageName);
					if (pack.packageName.equals("com.samkoon.setting")) {
						// 包存在了，才启动
						Log.d("SKScene", "start activity...");
						PackageManager packageManager = mContext.getPackageManager();
						Intent intent2 = packageManager
								.getLaunchIntentForPackage("com.samkoon.setting");
						mContext.startActivity(intent2);
						break;
					}
				}
				
			}
			
			break;
		case IP_SET://IP设置
			if (mContext!=null) {
				if (mIpSet==null||!mIpSet.isShow) {
					mIpSet=new IPSet(mContext);
					mIpSet.showPopWindow();
				}
			}
			break;
		case ZOOM_IN:
			//放大
			if (mTrends==null) {
				SceneBiz biz=DBTool.getInstance().getmSceneBiz();
				int id=biz.getItemId(sid,13);
				mTrends=(SKHistoryTrends)SKSceneManage.getInstance().getItemId(sid, id);
			}
			if (mTrends!=null) {
				if (info.isbZoomX()&&info.isbZoomY()) {
					//水平和垂直，都放大
					mTrends.large(2);
				}else {
					if (info.isbZoomX()) {
						//水平放大
						mTrends.large(0);
					}else {
						//垂直放大
						mTrends.large(1);
					}
				}
				
			}
			break;
		case REDUCE:
			//缩小
			if (mTrends==null) {
				SceneBiz biz=DBTool.getInstance().getmSceneBiz();
				int id=biz.getItemId(sid,13);
				mTrends=(SKHistoryTrends)SKSceneManage.getInstance().getItemId(sid, id);
			}
			if (mTrends!=null) {
				if (info.isbZoomX()&&info.isbZoomY()) {
					//水平和垂直都缩小
					mTrends.reduce(2);
				}else {
					if (info.isbZoomX()) {
						//水平缩小
						mTrends.reduce(0);
					}else{
						//垂直缩小
						mTrends.reduce(1);
					}
				}
				
			}
			break;
		}
	}

	/**
	 * 窗口操作
	 */
	private void windowOper(PeculiarButtonInfo info) {
		if (mContext==null) {
			mContext=SKSceneManage.getInstance().mContext;
		}
		switch (info.geteActionId()) {
		case OPEN_WINDOW: // 打开窗口
			SKProgress.hide();//隐藏等待框
			SKSceneManage.getInstance().gotoWindow(1, info.getnWindowID()+1, false, 0,GOTO_TYPE.BUTTON);
			break;
		case CLOSE_WINDOW: // 关闭窗口
			SKWindowManage.getInstance(mContext).closeWindow(1);
			break;
		}
	}

	/**
	 * 报警操作
	 */
	private void alarm(PeculiarButtonInfo info) {
		if (mContext==null) {
			mContext=SKSceneManage.getInstance().mContext;
		}
		switch (info.geteActionId()) {
		case ALRAM_SWITCH: // 报警声音开关
			AlarmGroup.getInstance().setSound();
			break;
		case CONFIRM_ALRAM:
		case CLEAR_ALRAM:
		case CLEAR_HISTORY_ALRAM:
			TYPE type ;
			if (info.geteActionId() == PECULIAR_OPER.CONFIRM_ALRAM) {
				type = DateTimeSetting.TYPE.ALARM_CONFIRM;
			}
			else if (info.geteActionId() == PECULIAR_OPER.CLEAR_ALRAM) {
				type = DateTimeSetting.TYPE.ALARM_CLEAR;
			}
			else {
				type = DateTimeSetting.TYPE.ALARM_CLEAR_HISTOTY;
			}
			
			try {
				if (collectSetting == null || !collectSetting.showFlag){
					collectSetting= new DateTimeSetting(SKSceneManage.getInstance().getActivity());
					collectSetting.setCanceledOnTouchOutside(false);
					collectSetting.onCreate(type, 200, 200);
					collectSetting.showDialog(type,0, 0,200,200);
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("SKButtonFunction", "create dialog error");
			}
			
			break;
		case EXPORT_FILE://历史报警数据导出
			if (sDialog==null||!sDialog.isShow) {
				sDialog=new SKRecipeDialog(mContext, 6);
				sDialog.initPopWindow();
				sDialog.showPopWindow();
			}
			break;
		}
	}

	/**
	 * 配方操作
	 */
	public SKRecipeDialog sDialog;
	private SKSwitchOperDialog mOperDialog;
	private void formula(PeculiarButtonInfo info) {
		if (mContext==null) {
			mContext=SKSceneManage.getInstance().mContext;
		}
		switch (info.geteActionId()) {
		case EDIT_FORMULA: // 编辑当前配方
			if (sDialog==null||!sDialog.isShow) {
				sDialog=new SKRecipeDialog(mContext, 2);
				boolean result=sDialog.initPopWindow();
				if (result) {
					sDialog.showPopWindow();
				}
			}
			break;
		case ADD_FORMULA: // 新建配方
			if (sDialog==null||!sDialog.isShow) {
				sDialog=new SKRecipeDialog(mContext, 1);
				sDialog.initPopWindow();
				sDialog.showPopWindow();
			}
			break;
		case DELETE_FORMULA: // 删除当前配方
			if (mOperDialog==null||!mOperDialog.isShow) {
				mOperDialog=new SKSwitchOperDialog(mContext);
				mOperDialog.setiOperCall(new SKSwitchOperDialog.IOperCall() {
					
					@Override
					public void onConfirm() {
						CurrentRecipe ginfo=SystemInfo.getCurrentRecipe();
						if (ginfo!=null) {
							if (ginfo.getCurrentRecipeId()>-1) {
								RecipeDataCentre.getInstance().msgDeleteRecipe(ginfo);
							}
						}
						mOperDialog.isShow=false;
						mOperDialog.hidePopWindow();
					}
					
					@Override
					public void onCancel() {
						mOperDialog.isShow=false;
						mOperDialog.hidePopWindow();
					}
				});
				mOperDialog.showPopWindow();
			}
			break;
			
		case DELETE_ALL_FORMULA:// 删除全组配方
			if (sDialog==null||!sDialog.isShow) {
				sDialog=new SKRecipeDialog(mContext, 3);
				sDialog.initPopWindow();
				sDialog.showPopWindow();
			}
			break;
		case WRITE_FORMULA: // 当前配方写入PLC
			RecipeDataCentre.getInstance().msgWriteRecipeToPlc(SystemInfo.getCurrentRecipe());
			break;
		case READ_FORMULA: // 从PLC读取当前配方
			RecipeDataCentre.getInstance().msgReadRecipeFromPlc(SystemInfo.getCurrentRecipe());
			break;
		case EXPORT_FORMULA: // 配方组导出为文件
			if (sDialog==null||!sDialog.isShow) {
				sDialog=new SKRecipeDialog(mContext, 4);
				sDialog.initPopWindow();
				sDialog.showPopWindow();
			}
			break;
		case INTO_FORMULA: // 文件导入为配方组
			if (sDialog==null||!sDialog.isShow) {
				sDialog=new SKRecipeDialog(mContext, 5);
				sDialog.initPopWindow();
				sDialog.showPopWindow();
			}
			break;
		case SAVE_CURRENT_FORMULA: // 保存
		{
			
			CurrentRecipe cRecipe=RecipeDataCentre.getInstance().getCurrRecipe();
			RecipeDataProp.recipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(cRecipe.getCurrentGroupRecipeId());
			if (oGprop==null) {
				return;
			}
			
			RecipeOprop data=null;
			
			Vector<RecipeOprop>  mRecipeLists=oGprop.getmRecipePropList();
			if (mRecipeLists!=null) {
				for (int i = 0; i < mRecipeLists.size(); i++) {
					if (mRecipeLists.get(i).getnRecipeId()==cRecipe.getCurrentRecipeId()) {
						data=mRecipeLists.get(i);
						break;
					}
				}
			}
			if (data==null) {
				return;
			}
            EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
            eInfo.mRecipeData=data;
			eInfo.mRecipeInfo=cRecipe;
			eInfo.sValueList=RecipeDataCentre.getInstance().getRecipeData(cRecipe.getCurrentGroupRecipeId(), 
					cRecipe.getCurrentRecipeId(), false);;
			RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
			break;
		}
		}
	}

	/**
	 * 重启软件
	 */
	private void reset() {
		
		SKSceneManage.getInstance().destroy();
//		Intent intent = new Intent(mContext, ResetService.class);
//		mContext.startService(intent);
//		Intent stoptMain = new Intent(Intent.ACTION_MAIN);
//		stoptMain.addCategory(Intent.CATEGORY_HOME);
//		stoptMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		mContext.startActivity(stoptMain);
		android.os.Process.killProcess(android.os.Process.myPid()); 

	}

	SKThread.ICallback callback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			screenShots();
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			
		}
	};
	
	/**
	 * 截屏
	 */
	private static boolean loading;
	private void screenShots() {
		
		if (mContext==null) {
			mContext=SKSceneManage.getInstance().mContext;
		}
		String time=System.currentTimeMillis()+"";
		String sd = "/mnt/sdcard/";
		String name = "";
		String path = "";
		String sPath="SD/";//保存路径
		if (!StorageStateManager.getInstance().isSDMounted()) {
			sd="/mnt/usb2/";
			sPath=mContext.getString(R.string.screenshot_upan);
			if (!StorageStateManager.getInstance().isUSBMounted()) {
				SKToast.makeText(mContext.getString(R.string.screenshot_error), Toast.LENGTH_SHORT).show();
				loading=false;
				return;
			}
		}
		
		SKToast.makeText(mContext.getString(R.string.screenshoting), Toast.LENGTH_SHORT).show();
		ScenceInfo info = SKSceneManage.getInstance().getSceneInfo();
		if (info == null) {
			SKToast.makeText(mContext.getString(R.string.screenshot_fail), Toast.LENGTH_SHORT).show();
			loading=false;
			return;
		}
		
		Calendar c = Calendar.getInstance();
		time=c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)
				+"_"+c.get(Calendar.HOUR)+"-"+c.get(Calendar.MINUTE)+"-"+c.get(Calendar.SECOND);
		
		name = info.getsScreenName();
		path = sd + "Samkoon/" + name+"_"+time + ".png";
		sPath+="Samkoon/" + name + ".png";

		Bitmap bitmap=Bitmap.createBitmap(info.getnSceneWidth(), info.getnSceneHeight(), Config.ARGB_8888);;
		
		if (bitmap==null) {
			loading=false;
			return;
		}
		
		Bitmap scene=SKSceneManage.getInstance().getCurrentScene().mBitmap;
		if (scene==null) {
			loading=false;
			return;
		}
		
		Paint paint=new Paint();
		FillRender fillRender;
		Shader myShader;
		Canvas canvas=new Canvas(bitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Rect rect=new Rect(0, 0, info.getnSceneWidth(), info.getnSceneHeight());
		
		if (info.geteBackType() == BACKCSS.BACK_IMG) {
			Bitmap bg = ImageFileTool
					.getBitmap(info.getsPicturePath());
			if (bg != null) {
				canvas.drawBitmap(bg, null, rect, null);
			}
		} else {
			if (info.geteDrawStyle() == CSS_TYPE.CSS_TRANSPARENCE
					|| info.geteDrawStyle() == CSS_TYPE.CSS_SOLIDCOLOR) {
				canvas.drawColor(info.getnBackColor());
			} else {
				fillRender = new FillRender();
				myShader = fillRender.setRectCss(info.geteDrawStyle(),
						0, 0, info.getnSceneWidth(),
						info.getnSceneHeight(), info.getnForeColor(),
						info.getnBackColor());
				paint.setShader(myShader);
				paint.setStyle(Style.FILL_AND_STROKE);
				canvas.drawRect(rect, paint);
			}
		}
		
		canvas.drawBitmap(scene, null, rect, null);

		File dirFile = new File(sd + "Samkoon/");
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		
		File mFile = new File(path);
//		if (mFile.exists()) {
//			mFile.delete();
//		}
		
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(
					new FileOutputStream(mFile));
			
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		}catch (IOException e) {
			e.printStackTrace();
			SKToast.makeText(mContext.getString(R.string.screenshot_fail), Toast.LENGTH_SHORT).show();
			loading=false;
			return;
		}
		loading=false;
		SKToast.makeText(mContext.getString(R.string.screenshot_path)+sPath, Toast.LENGTH_LONG).show();
	}

}
