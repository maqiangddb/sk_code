package com.android.Samkoonhmi.macro;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import com.android.Samkoonhmi.databaseinterface.MacroManagerBiz;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.MACRO_TYPE;
import com.android.Samkoonhmi.util.MMSTATE;
import com.android.Samkoonhmi.util.MSERV;
import dalvik.system.DexClassLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * 宏指令管理器
 * */
public class MacroManager{

	private static final String TAG="MacroManager";
	private ArrayList<Short> mInitMacroIDList   = null;   //初始宏指令ID列表
	private ArrayList<Short> mGlobalMacroIDList = null;   //全局宏指令ID列表
	private ArrayList<Short> mCompMacroIDList   = null;   //控件宏指令ID列表
	private ArrayList<Short> mSceneMacroIDList  = null;   //场景宏指令ID列表


	private HashMap<Short, Timer>   mSMITimerHMap  = null;   //场景宏定时器哈希表
	private HashMap<Short, Timer>  mGMITimerHMap  = null;   //全局宏定时器哈希表
	
	private HashMap<Integer, ArrayList<Short>> mSceneIdMap=null;//画面宏指令id集合
	private HashMap<Short, ArrayList<SceneMacroItem>> mSceneMacroItemMap=
			new HashMap<Short, ArrayList<SceneMacroItem>>();//画面宏实体集合
	
	private short mState = MMSTATE.INIT;    //表征宏指令管理器的状态

	private MacroManagerHandler mHandler;   //消息处理句柄

	//private Context mContent; //保存上下文环境

	public static final String JMLPName = new String("jml"); // JML包名称前缀
	public static final String MLPath = new String("/data/data/com.android.Samkoonhmi/macro/ml.jar");// ML包名绝对路径

	private DexClassLoader      MLJarHolder = null;   //ML装载器

	private static MacroManager mSelfInstance = null; //自持单例
	private static BroadcastReceiver timeSetBroadcastReceiver;
	private HandlerThread mThread=null;
	private static long nCurrentTime;

	public MacroManager(Context content, String Name) {


		//mContent = content;
		
		MacroManagerBiz biz=new MacroManagerBiz();
		mSceneIdMap=biz.getSceneIdMap();
		mThread=new HandlerThread("MacroManagerThread");
		mThread.start();
		mHandler = new MacroManagerHandler(mThread.getLooper());//绑定消息分发逻辑

		Request(MSERV.INIT); //请求初始化
		Request(MSERV.CALLIM); //请求执行初始宏指令
		Request(MSERV.CALLGM); //请求执行全局宏指令
		nCurrentTime=System.currentTimeMillis();
		
		Log.d("MacroManager", "init macro......");
		timeSetBroadcastReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals("com.samkoon.settime")){
					if(nCurrentTime>System.currentTimeMillis()){
						nCurrentTime=System.currentTimeMillis();
						stopGlobalMacros();
						runGlobalMacros();
						//Log.d(TAG, "onReceive ....... ");
						reRunSceneMacro((short) SKSceneManage.getInstance().nSceneId);
					}
				}
			}
		};
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("com.samkoon.settime");
		ContextUtl.getInstance().registerReceiver(timeSetBroadcastReceiver, mFilter);
	}



	/**
	 * 获取自持单例
	 * */
	public synchronized static MacroManager getInstance(Context content){
		
		if((null ==  mSelfInstance)&&(null == content)){//第一次获取单例需要content
			Log.e("MacroManager", "AK Macro getInstance: content is null !");
			return null;
		}
		if(null ==  mSelfInstance){//第一次获取单例
			mSelfInstance = new MacroManager(content,"MacroManager");
			if(null == mSelfInstance){
				Log.e("MacroManager", "AK Macro getInstance: Instance create failed!");
				return null;	
			}
		}
		
		return mSelfInstance;
	}

	/**
	 * 扫描初始宏指令ID
	 * */
	private boolean scanInitMacroID() {

		MacroManagerBiz tmpMMBiz = new MacroManagerBiz();

		// 获得宏指令ID列表
		mInitMacroIDList = tmpMMBiz.selectInitMacroIDList();
		Log.d(TAG, "scanInitMacroID mInitMacroIDList ="+mInitMacroIDList);
		
		if (null == mInitMacroIDList) {
			Log.e("MacroManager", "AK Macro scanInitMacroID: Create id list failed!");
			return false;
		}
		return true;
	}

	/**
	 * 扫描全局宏指令ID
	 * */
	private boolean scanGlobalMacroID() {

		MacroManagerBiz tmpMMBiz = new MacroManagerBiz();

		// 获得宏指令ID列表
		mGlobalMacroIDList = tmpMMBiz.selectGlobalMacroIDList();
		if (null == mGlobalMacroIDList) {
			Log.e("MacroManager", "AK Macro scanGlobalMacroID: Create id list failed!");
			return false;
		}
		return true;
	}

	/**
	 * 扫描场景宏指令ID
	 **/
	private boolean scanSceneMacroID() {
		MacroManagerBiz tmpMMBiz = new MacroManagerBiz();

		// 获得宏指令ID列表
		mSceneMacroIDList  = tmpMMBiz.selectSceneMacroIDList();
		if (null == mSceneMacroIDList) {
			Log.e("MacroManager", "AK Macro scanSceneMacroID: Create id list failed!");
			return false;
		}
		return true;
	}

	/**
	 * 装载控件宏指令ID列表
	 * */
	private boolean scanCompMacroID() {

		MacroManagerBiz tmpMMBiz = new MacroManagerBiz();

		// 获得宏指令ID列表
		mCompMacroIDList = tmpMMBiz.selectCompMacroIDList();
		if (null == mCompMacroIDList) {
			Log.e("MacroManager", "AK Macro scanCompMacroID: Create id list failed!");
			return false;
		}
		return true;
	}

	/**
	 * 执行初始化宏指令
	 * */
	private void runInitMacros() {

		if(null == mInitMacroIDList){
			Log.e("MacroManager","runInitMacros: mInitMacroIDList is null!");
			return;
		}

		InitMacroItem tmpIMI = null;
		for(short i = 0; i < mInitMacroIDList.size(); i++){//遍历宏指令表
			tmpIMI = new InitMacroItem(mInitMacroIDList.get(i));
			runMacroFunc(tmpIMI, tmpIMI.getType());//执行宏指令
		}
	}

	/**
	 * 执行全局宏指令
	 * */
	private void runGlobalMacros() {
		if(null == mGlobalMacroIDList){
			Log.e("MacroManager","runGlobalMacros: mGlobalMacroIDList is null!");
			return;
		} 
		
		//Log.d(TAG, "runGlobalMacros  ... ");
		GlobalMacroItem tmpGMI = null;
		for(short i = 0; i < mGlobalMacroIDList.size(); i++){//遍历宏指令表
			tmpGMI = new GlobalMacroItem(mGlobalMacroIDList.get(i));
			runMacroFunc(tmpGMI, tmpGMI.getType());   //执行宏指令
		}
	}

	/**
	 * 停止全局宏指令定时器
	 * */
	private void stopGlobalMacros(){
		
		if(null == mGMITimerHMap){
			return;
		}
		Iterator<Entry<Short, Timer>> it = mGMITimerHMap.entrySet().iterator();//哈希表遍历器
		while(it.hasNext()){
			Entry obj = it.next();
			((Timer)obj.getValue()).cancel();
		}
		mGMITimerHMap.clear();
	}

	/**
	 * 重新执行场景宏
	 * @param SceneName  场景名称
	 */
	private void reRunSceneMacro(short mid){
		if(mSceneMacroItemMap == null){
			Log.e("MacroManager","reRunSceneMacro: mSceneMacroItemMap is null!");
			return;
		}
		ScenceInfo curSceneInfo = SKSceneManage.getInstance().getScenceInfo(mid);
		if (curSceneInfo==null) {
			return;
		}
		ArrayList<Short> tmpMIDList = curSceneInfo.getSceneMacroIDList();

		if (null == tmpMIDList || 0 == tmpMIDList.size()) {
			// 不做任何提示
			return;
		}
		
		for(int j=0;j<tmpMIDList.size();j++){
			ArrayList<SceneMacroItem> list = mSceneMacroItemMap.get(tmpMIDList.get(j));
			if(list == null || list.size() == 0){
				Log.e("MacroManager","reRunSceneMacro: no Macro in the scene!");
				return;
			}
			
			for(int i=0;i<list.size();i++){
				list.get(i).cancle(list.get(i).getTimer());
				list.get(i).cancel();
				mSMITimerHMap.remove(list.get(i).getMID());
				Timer timer = new Timer();
				if((0 == list.get(i).reExecute(timer))&& (mSMITimerHMap != null)){
					mSMITimerHMap.put(list.get(i).getMID(),timer);
				}
			}
		}
	}
	/**
	 * 执行场景宏指令
	 * @param SceneName  场景名称
	 * */
	private void runSceneMacro(short mid) {

		if(null == mSceneMacroIDList){
			Log.e("MacroManager","runSceneMacros: mSceneMacroIDList is null!");
			return;
		}
		
		//加载画面宏列表
		if(!mSceneMacroItemMap.containsKey(mid)){
			ArrayList<SceneMacroItem> list = new ArrayList<SceneMacroItem>();
			mSceneMacroItemMap.put(mid, list);
		}else{
			ArrayList<SceneMacroItem> list =mSceneMacroItemMap.get(mid);
			for (int i = 0; i < list.size(); i++) {
				SceneMacroItem tmpSMI=list.get(i);
				tmpSMI.reset();
				runMacroFunc(tmpSMI, tmpSMI.getType()); //执行宏指令
			}
			//从新执行宏指令
			//reRunSceneMacro(mid);
			return ;
		}
		
		boolean isMacroFined = false; //标定是否找到宏指令

		for(short i = 0; i < mSceneMacroIDList.size(); i++){//遍历宏指令表
			if(mid == mSceneMacroIDList.get(i)){        //从场景宏指令ID表中找到指定的控件宏指令
				SceneMacroItem tmpSMI = new SceneMacroItem(mid);
				mSceneMacroItemMap.get(mid).add(tmpSMI);//记录宏指令
				runMacroFunc(tmpSMI, tmpSMI.getType()); //执行宏指令
				isMacroFined  = true;                   //标定已经找到宏指令
				break;                                  //不再继续查找
			}
		}

		if(false == isMacroFined){//若没有找到宏指令
			Log.e("MacroManager","runSceneMacros: Macro not founed, macro id : " + mid);
		}
	}

	/**
	 * 停止场景宏指令线程
	 * @param sid-场景ID
	 * */
	private synchronized void stopSceneMacros(int sid){
		if(null == mSMITimerHMap){
			return;
		}
		ArrayList<Short> list=mSceneIdMap.get(sid);
		
		Iterator<Entry<Short, Timer>> it = mSMITimerHMap.entrySet().iterator();//哈希表遍历器
		while(it.hasNext()){
			Entry obj = it.next();
			if(list!=null){
				boolean result=false;
				for(int i=0;i<list.size();i++){
					short key=(Short)obj.getKey();
					if(key==list.get(i)){
						result=true;
						break;
					}
				}
				if(result){
					((Timer)obj.getValue()).cancel();
				}
			}
		}
		if(list!=null){
			for(int i=0;i<list.size();i++){
				if(mSMITimerHMap.containsKey(list.get(i))){
					mSMITimerHMap.remove(list.get(i));
				}
			}
		}
		//mSMITimerHMap.clear();
	}

	/**
	 * 启用线程执行控件宏指令
	 * @param mid 宏指令ID
	 * */

	private void runCompMacro(short mid) {

		if(null == mCompMacroIDList){
			Log.e("MacroManager","runCompMacro: mCompMacroIDList is null!");
			return;
		}

		boolean isMacroFind = false; //标定是否找到宏指令

		for(short i = 0; i < mCompMacroIDList.size(); i++){//遍历控件宏指令表
			if(mid == mCompMacroIDList.get(i)){            //从控件宏指令ID表中找到指定的控件宏指令
				CompMacroItem tmpCMI = new CompMacroItem(mid);
				runMacroFunc(tmpCMI, tmpCMI.getType()); //执行控件宏指令
				isMacroFind  = true;                   //标定已经找到宏指令
				break;                                  //不再继续查找
			}
		}

		if(false == isMacroFind){//若没有找到宏指令
			Log.e("MacroManager","runCompMacro: Macro not founed, macro id : " + mid);
		}
	}

	/**
	 * 执行指定宏指令
	 * 
	 * @param mitem   宏指令项
	 * @param type    宏指令类型
	 * */
	private int runMacroFunc(BaseMacroItem mitem, short type) {

		if (null == mitem) {
			Log.e("MacroManager", "runMacroFunc: Macro Item is null");
			return -1;
		}
		if(false == mitem.obtainJMLClass(ContextUtl.getInstance(), MLJarHolder)){ //获得JML类
			return -2;
		}
		if(false == mitem.obtainJMLInstance()){ // 获得JML类的一个实例
			return -3;
		}
		if(false == mitem.obtainJMLMethod()){ // 获取方法句柄
			return -4;
		}
		
		Timer timer = new Timer();
		switch(type){
		case MACRO_TYPE.GLOOP:      //若全局不受控循环
		case MACRO_TYPE.GCTRLOOP:   //若全局受控循环
			if((0 == mitem.execute(timer))&& (mGMITimerHMap != null)){ //定时器调度宏指令成功
				mGMITimerHMap.put(mitem.getMID(), timer);
			}
			
			break;
		case MACRO_TYPE.SLOOP:     //若场景不受控循环
		case MACRO_TYPE.SCTRLOOP:  //若场景受控循环
			
			if((0 == mitem.execute(timer))&& (mSMITimerHMap != null)){ //定时器调度宏指令成功
				//mitem.getMID()
				mSMITimerHMap.put(mitem.getMID(), timer);
			}
			
			break;
		case MACRO_TYPE.INIT:
			mitem.execute(timer); // 执行宏指令
			break;
		case MACRO_TYPE.COMP:
			mitem.execute(timer);
			break;
		default:
			//什么也不做
			break;
		}

		return 0;
	}// End of runMacroFunc

	/**
	 * 装载ML库
	 * */
	private boolean loadMLJar() {

		Log.d(TAG, "Macro loadMLJar start...");
		File tmpMLFile = null; // 依赖库的文件句柄

		// 需要使用content参数
		if (null == ContextUtl.getInstance()) {
			Log.e("MacroManager", "loadMLJar: mContent is null");
			return false;
		}

		// 获得ML的.jar文件句柄
		tmpMLFile = new File(MLPath);
		if (false == tmpMLFile.exists()) {
			Log.e("MacroManager", "loadMLJar:" + MLPath + ": not exist!");
			return false;
		}

		// 装载ml.jar文件
		MLJarHolder = new DexClassLoader(MLPath, ContextUtl.getInstance().getFilesDir()
				.getAbsolutePath(), null, ContextUtl.getInstance().getClassLoader());
		if (null == MLJarHolder) {
			Log.e("MacroManager", "loadMLJar: ML Load Failed!");
			return false;
		}
		Log.d(TAG, "Macro loadMLJar end...");
		return true;
	}

	/**
	 * 宏指令消息分发逻辑
	 * */
	class MacroManagerHandler extends Handler{

		public MacroManagerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MServMsg mservmsg = (MServMsg) msg.obj;
			
			switch (mservmsg.stype) {
			case MSERV.INIT:  //初始化请求
				boolean reulst=loadMLJar();
				if(false == reulst){  //装载ML包
					return;
				}
							
				mGMITimerHMap = new  HashMap<Short, Timer>();    //初始化全局宏定时器哈希表
				mSMITimerHMap = new  HashMap<Short, Timer>();    //初始化场景宏定时器哈希表

				//扫描宏指令列表
				scanInitMacroID();
				scanGlobalMacroID();
				scanSceneMacroID();
				scanCompMacroID();

				mState = MMSTATE.READY;   //宏指令管理器准备就绪
				break;

			case MSERV.CALLSM://请求执行场景宏指令
				if(MMSTATE.READY == mState){
					runSceneMacro(mservmsg.param1);
				}
				break;
			case MSERV.ENDRSM://请求终止执行场景宏
				if(MMSTATE.READY == mState){
					stopSceneMacros(mservmsg.param1);
				}
				break;
			case MSERV.CALLCM://请求执行控件宏
				if(MMSTATE.READY == mState){
					runCompMacro(mservmsg.param1);
				}
				break;
			case MSERV.CALLIM://请求执行初始化宏
				if(MMSTATE.READY == mState){
					runInitMacros();
				}
				break;
			case MSERV.CALLGM://请求执行全局宏
				if(MMSTATE.READY == mState){
					runGlobalMacros();
				}
				break;
			case MSERV.ENDRGM://请求终止执行全局宏
				if(MMSTATE.READY == mState){
					stopGlobalMacros();
				}	
				break;
			}
		}//End of: handleMessage(Message msg)
	};

	/**
	 * 请求宏指令服务
	 * @param stype 服务类型MSERV
	 * @param arg1      附加数据，用于存放宏指令ID or场景ID
	 * */
	public boolean Request(short stype, short arg1){		
		//将请求数据打包成消息
		MServMsg msg = new MServMsg();
		msg.stype  = stype;
		msg.param1 = arg1;
		mHandler.obtainMessage(0, msg).sendToTarget(); //发送请求
		return true;
	} 

	/**
	 * 请求宏指令服务
	 * @param stype 服务类型
	 * */
	public boolean Request(short stype){	
		//将请求数据打包成消息
		MServMsg msg = new MServMsg();
		msg.stype  = stype;
		mHandler.obtainMessage(0, msg).sendToTarget(); //发送请求
		return true;
	} 

	/**
	 * 服务消息类型
	 */
	class MServMsg{
		short stype;   //服务类型
		short param1;  //附加数据
	}

	public void Destroy(){
		try {
			if(timeSetBroadcastReceiver!=null){
				ContextUtl.getInstance().unregisterReceiver(timeSetBroadcastReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "MacroManager destroy error !!!");
		}
	}
	
	public Context getContext(){
		return ContextUtl.getInstance();
	}
	
	public HashMap<Short, Timer> getmSMITimerHMap(){
		return mSMITimerHMap;
	}
}
