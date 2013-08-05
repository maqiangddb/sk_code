package com.android.Samkoonhmi.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKSaveThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.LockInfoBiz;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.databaseinterface.TouchShowInfoBiz;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.PassWordInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.network.PhoneManager;
import com.android.Samkoonhmi.network.TcpServerManager;
import com.android.Samkoonhmi.plccommunicate.CmnPortManage;
import com.android.Samkoonhmi.plccommunicate.SKCommCtlCenter;
import com.android.Samkoonhmi.pmem.RestorePowerSave;
import com.android.Samkoonhmi.skenum.CONNECT_TYPE;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.DataTransfers;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skwindow.SKMenuManage;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.StorageStateManager;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.AlarmSaveThread;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.SavaInfo;
import com.android.Samkoonhmi.util.SystemParam;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	public static int readPassCount = 0;
	// 用户名编辑框
	private Spinner userName;
	// 用户名集合
	private List<String> userNameList;
	//密码编辑框
	private EditText passWord;
	//登录按钮
	private Button loginButton;
	//对话框
	public AlertDialog dlg = null;
	private final static int LOGIN_WAIT = 0;
	private final static int LOGIN_VALIDATE = 1;
	private final static int LOGIN_SUCCESS = 2;
	private final static int HOLD_LOGIN=4;
	private final static int LOAD_DATA=5;
	private static Context myContext = null;
	private SystemInfoBiz sysBiz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (check()) {
			this.finish();
		}else { 
			loginView();
		}
	}
	
	
	/**
	 * 登录画面
	 */
	private void loginView(){
	
		setContentView(R.layout.login);
		userName = (Spinner) findViewById(R.id.userName);
		passWord = (EditText) findViewById(R.id.passWord);
		loginButton = (Button) findViewById(R.id.login);
		loginButton.setOnClickListener(this);
		userName.setOnItemSelectedListener(this);

		//将登陆界面的控件隐藏
		TextView textName = (TextView) findViewById(R.id.userNameText);
		TextView textPass = (TextView) findViewById(R.id.passwordtext);
		userName.setVisibility(View.GONE);
		passWord.setVisibility(View.GONE);
		loginButton.setVisibility(View.GONE);
		textName.setVisibility(View.GONE);
		textPass.setVisibility(View.GONE);

		//发送消息加载数据
		mHandler.sendEmptyMessageDelayed(LOAD_DATA, 100);
	}
	
	/**
	 * 加载数据
	 */
	private void loadData(){
		SKSceneManage.getInstance().mContext = getApplicationContext();
		SKSceneManage.nSceneWidth = getWindowManager().getDefaultDisplay().getWidth();
		SKSceneManage.nSceneHeight = getWindowManager().getDefaultDisplay().getHeight();
		myContext = this;
		sysBiz = new SystemInfoBiz();
		
		/* 检查文件是否存在 */
		File mCollectFile = new File("/data/data/com.android.Samkoonhmi/lib/libserial_port.so");
		if (!mCollectFile.exists()) {
			SKToast.makeText("serial_port file not exists", Toast.LENGTH_LONG).show();
			return;
		}

		mCollectFile = new File("/data/data/com.android.Samkoonhmi/lib/libplc_drives_center.so");
		if (!mCollectFile.exists()) {
			SKToast.makeText("plc_drives_center file not exists",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		initValue();
		SKProgress.onResume = false;
	}

	/** 
	 * 初始化参数
	 */
	private void initValue() { 
		// 从数据库取系统数据
		if (isDataBaseExist()) {
			getDataFromDataBae();
			UserInfoBiz biz = new UserInfoBiz();
			// 将登录用户改为默认用户
			UserInfo defaultInfo = biz.selectDefaultUser();
			SystemInfo.setDefaultUser(defaultInfo);
			userNameList = biz.getUserNameList();
			SystemInfo.setUserNameList(userNameList);
		}

		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 格式化时间
		String loginTime = d.format(new Date());// 按以上格式 将当前时间转换成字符串
		SystemInfo.setLoginTime(loginTime);
	
		if (SystemInfo.getnModel()!=null) {
			if (SystemInfo.getnModel().equals("AK-035AET")||
					SystemInfo.getnModel().equals("AK-035A-T")) {
				RestorePowerSave.isAK_035_AET=true;
				RestorePowerSave.nTotalSize=8;
				SKSaveThread.isAK_035_AET=true;
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		//Log.d("SKScene", "onResume...");
		
		if (!reboot) {
			if(null !=passWord){
				passWord.setEnabled(false);
			}
			if(null != userName)
			{
				userName.setEnabled(false);
			}
			if(null != loginButton)
			{
				loginButton.setEnabled(false);
			}
			if(null != loginButton)
			{
				loginButton.setClickable(false);// 自动登录 ，登录按钮不可点击
			}
			if(null != mHandler)
			{
				mHandler.sendEmptyMessageDelayed(HOLD_LOGIN, 400);
			}
			
		}
			
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {

		autoClick(v);
	}

	private void autoClick(View v) {
		// TODO Auto-generated method stub
		if(null == v) return ;
		
		switch (v.getId()) {
		case R.id.login:
			boolean dataExist = isDataBaseExist();
			if (dataExist) {
				mHandler.sendEmptyMessage(LOGIN_WAIT);
			} else {
				SKToast.makeText(this, R.string.filenotexist,
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}
	
	
	
	/**
	 * handler 发送消息
	 */
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HOLD_LOGIN:
				autoClick(loginButton);
				break;
			case LOGIN_WAIT:
				// 这个实现转圈
				showDialog(true,getResources().getString(R.string.islogining));
				mHandler.sendEmptyMessage(LOGIN_VALIDATE);
				break;
			case LOGIN_VALIDATE:
				//将空用户设置为当前用户
				SystemInfo.setGloableUser(ParameterSet.getInstance().getNullUser());
				mHandler.sendEmptyMessage(LOGIN_SUCCESS);
				break;
			case LOGIN_SUCCESS:
				// 登录成功 判读是否超出使用时效
				if ((SystemInfo.getnSetBoolParam() & SystemParam.HMI_PROTECT) == SystemParam.HMI_PROTECT) {
					// 将登陆的日期和使用的天数写入shareparefarence

					setHmiProctPass();
					boolean b = ParameterSet.getInstance()
							.outTimeUse(myContext);
					if (b) {
						if (null != dlg) {
							dlg.dismiss();
						}
						Intent intent = new Intent(LoginActivity.this,
								OutTimeActivity.class);
						startActivity(intent);
						finish();
					} else {
						loginSuccess();
					}
				} else {
					loginSuccess();
				}
				break;
			case LOAD_DATA:
				//加载数据
				loadData();
				break;
			}
		}
	};

	/**
	 * 登录成功
	 */
	private boolean isGoto;
	private void loginSuccess() {

		isGoto = true;
		// 测试模式，背光等
		/*
		 * SkCommon.getInstance().ReadMode0(CommonInfo);
		 * System.out.println("mode0:" +CommonInfo.module);
		 * 
		 * SkCommon.getInstance().ReadMode1(CommonInfo);
		 * System.out.println("mode1:" +CommonInfo.module);
		 * 
		 * SkCommon.getInstance().ReadMode2(CommonInfo);
		 * System.out.println("mode2:" +CommonInfo.module);
		 * 
		 * SkCommon.getInstance().BackLight_Off(); SystemClock.sleep(5000);
		 * SkCommon.getInstance().BackLight_On();
		 */
	
		
		File mCollectFile = new File("/data/data/com.android.Samkoonhmi/lib/libpmem.so");
		if (!mCollectFile.exists()) {
			if (null != dlg) {
				dlg.dismiss();
			}
			SKToast.makeText("libpmem.so file not exists", Toast.LENGTH_LONG)
					.show();
			return;
		}

		//登录成功
		SKSceneManage.getInstance().bLoginSuccess=true;
		
		/*恢复掉电保存*/
		RestorePowerSave.restoreLocal();
		
		/*加载地址*/
		SKDataBaseInterface skData=new SKDataBaseInterface();
		skData.loadAddr();
		
		/*加载触控和显现*/
		TouchShowInfoBiz.loadTouchAndShow();
		
		/*加载画面信息*/
		SKSceneManage.getInstance().loadSceneInfo();
		
		/* 打开通信口 */
		CmnPortManage.getInstance().openCmnPort(CONNECT_TYPE.COM1);
		CmnPortManage.getInstance().openCmnPort(CONNECT_TYPE.COM2);
		
		/* 创建和启动数据采集 */
		DataCollect.getInstance().start();

		/* 创建和打开配方 */
		RecipeDataCentre.getInstance().start();
		
		// 预先加载启动画面item
		int id = 1;
		if (SystemInfo.getCurrentScenceId() > 0) {
			id = SystemInfo.getCurrentScenceId();
		} else {
			SystemInfo.setCurrentScenceId(id);
		}
		SKSceneManage.getInstance().nSceneId = id;
		SKSceneManage.getInstance().loadSceneItem(id, LoginActivity.this);

		// 加载完启动画面所有控件，后回调跳转
		SKSceneManage.getInstance().setmGotoCallback(gotoCallback);
	}

	/**
	 * 加载数据成功 跳转画面
	 */
	SKSceneManage.IGotoCallback gotoCallback = new SKSceneManage.IGotoCallback() {

		@Override
		public void onGoto() {
			goToScene();
		}
	};

	/**
	 * 画面跳转
	 */
	private void goToScene() {
		if (null != dlg) {
			dlg.dismiss();
		}
		
		 if (isGoto) {
				Log.d("LoginActivity", "go to scene......");
				SKSceneOne.update=true;
				SKProgress.onResume = false;
				Intent intent = new Intent(LoginActivity.this, SKSceneOne.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				// 加载画面管理信息
				SKMenuManage.getInstance().loadData();
		}
	
		//注册时效广播
		TimeBroadCast.getInstance(getApplicationContext()).addFiler();
		//启动定时器
		SKTimer.getInstance().startTimer();

		//时间同步
		ParameterSet.getInstance().SysTime(getApplicationContext());
		
		// 如果设置了字地址切换画面，则注册字地址值改变接口
		ParameterSet.getInstance().changSceneByWordAddr(SystemInfo.getNkChangeScreenAddr());
		
		//系统设置，地址控制，切换配方
		ParameterSet.getInstance().setRecipeNotice(SystemInfo.getNkRecipeIndexAddr());
		
		//启动位控制画面跳转监听
        ParameterSet.getInstance().changeSceneByBitAddr();
		
		//初始化存储介质状态管理器,注册了一个广播接收器
		StorageStateManager.getInstance().initStateMntRecv(this.getApplicationContext());
		
		/*初始化通信线程*/
	    //SKCommThread.initCmnSlave();
		SKCommCtlCenter.getInstance().initCmnSlave();
		
		/*启动资料传输*/
		DataTransfers.getInstance().startDataTrans();
		
		//启动Tcp服务
        TcpServerManager.getInstance().onStart(this);
        
        //3g 服务
        PhoneManager.getInstance().onStart(getApplicationContext());
        
        //启动报警登录监视
     	startAlarm();
        
    	//启动内部寄存器的线程
		SystemVariable.isSysThread=true;
		SystemVariable.getInstance().startSystemThread();
        
	    finish();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isGoto = false;
	}

	/**
	 * 从数据库读取数据
	 */
	private boolean reboot;
	private void getDataFromDataBae() {
		
		reboot=false;
		sysBiz.selectSystemInfo();
		
		//将是否启用时效保护写入文件 方便系统设置apk中读取判断是否允许设置时间
		SavaInfo.saveTimeInfo();
		//获得锁屏信息
		LockInfoBiz.getInstance().selectLockInfo();
		
		Intent newintent = new Intent();
		newintent.setAction("com.samkoon.pdw");
		newintent.putExtra("wwdd", SystemInfo.getsUploadPassword());//上载密码
		if((SystemParam.UP_LOAD & SystemInfo.getnSetBoolParam())== SystemParam.UP_LOAD)
		{
			//允许上载
			newintent.putExtra("upload", "true");//是否允许上载
		}else{
			newintent.putExtra("upload", "false");//是否允许上载
		}
		sendBroadcast(newintent);
		
		//0 水平 1 垂直
		SharedPreferences modelsharedPreferences = getSharedPreferences(
				"modelinfo", 0);
		SharedPreferences.Editor modelshareEditor = getSharedPreferences(
				"modelinfo", 0).edit();
		Intent intent = new Intent();

		int model = modelsharedPreferences.getInt("model", 0);
		
		//Log.d("SKScene", "==========model:"+model);
		if(model == 1 && (SystemParam.VERTICAL_SHOW & SystemInfo.getnSetBoolParam()) != SystemParam.VERTICAL_SHOW)
		{
			//如果从文件中读取出来的值为1垂直 ，但是下载下来的工程是水平 则重启屏
			intent.putExtra("reboot",true);
			reboot=true;
			
		}else if(model == 0 && ((SystemParam.VERTICAL_SHOW & SystemInfo.getnSetBoolParam()) == SystemParam.VERTICAL_SHOW))
		{
			//如果从文件中读取出来的值是0 水平 但是下载下来的工程是垂直 则重启屏
			intent.putExtra("reboot",true);	
			reboot=true;
		}
		else{
			intent.putExtra("reboot",false);	
		}
		if((SystemParam.VERTICAL_SHOW & SystemInfo.getnSetBoolParam()) == SystemParam.VERTICAL_SHOW)
		{
			//Log.d("SKScene", "----------model 1");
			modelshareEditor.putInt("model", 1);
			//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			//Log.d("SKScene", "----------model 0");
			modelshareEditor.putInt("model", 0);
			//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		modelshareEditor.commit();
		
		if((SystemParam.VERTICAL_SHOW & SystemInfo.getnSetBoolParam()) == SystemParam.VERTICAL_SHOW)
		{
			//垂直
			if((SystemInfo.getnModel().indexOf("043")!=-1)||(SystemInfo.getnModel().indexOf("050")!=-1)
					||(SystemInfo.getnModel().indexOf("040")!=-1)){
				intent.setAction("com.samkoon.rotate90");
			}else{
				intent.setAction("com.samkoon.rotate270");
			}
			SKSceneManage.getInstance().mContext.sendBroadcast(intent);

		}else{
			//水平
			if((SystemInfo.getnModel().indexOf("043")!=-1)||(SystemInfo.getnModel().indexOf("050")!=-1)
					||(SystemInfo.getnModel().indexOf("040")!=-1)){
				intent.setAction("com.samkoon.rotate180");
			}else{
				intent.setAction("com.samkoon.rotate0");
			}

			SKSceneManage.getInstance().mContext.sendBroadcast(intent);
		}
		
		//Log.d("SKScene", "........reboot:"+reboot);
		
	}

	/**
	 * 显示等待对话框
	 * 
	 * @param isShow
	 * @param message
	 *            提示信息
	 */
	public void showDialog(boolean isShow, String message) {  

		if (isShow) {
			dlg = new AlertDialog.Builder(this).create();
			dlg.show();
			dlg.setCanceledOnTouchOutside(false);

			LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.hold_alert, null);
			if (null != message) {
				TextView textView = (TextView) layout
						.findViewById(R.id.hold_title);
				textView.setText(message);
			}
			dlg.getWindow().setContentView(layout);
		} else {
			if (dlg != null) {
				dlg.dismiss();
			}

		}
	}

	/**
	 * 判断数据库文件是否存在
	 * 
	 * @return
	 */
	public static boolean isDataBaseExist() {
		String path = "/data/data/com.android.Samkoonhmi/databases/sd.dat";
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 启动报警登录
	 */
	private void startAlarm() {
		AlarmSaveThread.getInstance();
		AlarmGroup.getInstance().startAlarm(this.getApplicationContext());
	}
	
	private boolean check(){
		boolean result=false;
		File file=new File("/data/data/com.android.Samkoonhmi/samkoonhmi.akz");
		if (file.exists()) {
			//没有解压成功，可能是由于vFiHpd这个文件的权限不对，造成的
			File dir=new File("/data/data/com.samkoon.sklauncher/vFiHpd/");
			if (dir.exists()) {
				dir.delete();
			}
			
//			Intent intent=new Intent();
//			intent.setClass(this, AkZipService.class);
//			intent.putExtra("update", "true");
//			startService(intent);
//			result=true;
		}
		return result; 
	}

	/**
	 * 设置实效验证密码
	 */
	private void setHmiProctPass() {
		// 如果是采用使用天数记录实效
		if (SystemInfo.isbProtectType() == false) {
			writeDayIntoPref();
			readPassCount = getPassIndexByDay();

		} else {
			readPassCount = getPassIndexByDate();
		}
		if (readPassCount + 1 > SystemInfo.getPassWord().size()) {
			SystemInfo.setOnePassWord(null);
		} else {
			SystemInfo.setOnePassWord(SystemInfo.getPassWord().get(
					readPassCount));
			// 将已用过的密码标记设为true
			SystemInfo.getPassWord().get(readPassCount).setUser(true);
		 
			boolean modifyBoo = sysBiz.updatePassUse(SystemInfo
					.getPassWord().get(readPassCount).getId());
		}
	}

	/**
	 * 通过实效日期寻找实效验证密码索引
	 * 
	 * @return
	 */
	private int getPassIndexByDate() {
		int passIndex = 6;
		List<PassWordInfo> passList = SystemInfo.getPassWord();
		Date loginDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String loginDateString = format.format(loginDate);
		loginDate = new Date(loginDateString);// .parse(loginDateString);
		long loginDatelong = loginDate.getTime();
		if (null != passList && !passList.isEmpty()) {
			for (int i = 0; i < passList.size(); i++) {
				PassWordInfo info = passList.get(i);
				String passDateString = info.getsTimeLimit();
				boolean passUse = info.isUser();
				Date passDate = new Date(passDateString);// format.parse(passDateString);
				long passDatelong = passDate.getTime();
				if (passUse == false) {
					// if (loginDatelong > passDatelong ) {
					passIndex = i;
					break;
					// }
				}

			}
		}
		return passIndex;
	}
	
	/**
	 * 通过使用天数查找实效验证密码的索引
	 * 
	 * @return
	 */
	private int getPassIndexByDay() {
		int passIndex = 6; 
		SharedPreferences sharedPreferences = getSharedPreferences(
				"hmiprotct", 0);
		passIndex = sharedPreferences.getInt("passIndex", 6);
		return passIndex;
	}

	/**
	 * 如果实效采用使用天数 则将记录使用天数到文件
	 */
	private void writeDayIntoPref() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"hmiprotct", 0);
		SharedPreferences.Editor shareEditor = getSharedPreferences(
				"hmiprotct", 0).edit();

		String dateTime = sharedPreferences.getString("dateTime", null);
		int dateNumber = sharedPreferences.getInt("dateNumber", -1);

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String writeDate = format.format(date);
		if (null == dateTime && -1 == dateNumber) {
			shareEditor.putString("dateTime", writeDate);
			shareEditor.putInt("dateNumber", 0);
			shareEditor.putInt("passIndex", 0);
			shareEditor.commit();
		} else {
			shareEditor.putString("dateTime", writeDate);
			if (writeDate.equals(dateTime)) {
				shareEditor.putInt("dateNumber", dateNumber);
			} else {
				shareEditor.putInt("dateNumber", dateNumber + 1);
			}
			shareEditor.commit();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
//		userNameValue = userNameAdapter.getItem(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
