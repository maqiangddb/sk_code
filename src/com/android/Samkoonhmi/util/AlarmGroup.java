package com.android.Samkoonhmi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.renderscript.Element;
import android.util.Log;
import android.widget.Toast;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKSaveThread;
import com.android.Samkoonhmi.SKSaveThread.AlamSaveProp;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.alarm.AlarmConditionInfo;
import com.android.Samkoonhmi.model.alarm.AlarmDataInfo;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;
import com.android.Samkoonhmi.model.alarm.AlarmMessageInfo;
import com.android.Samkoonhmi.network.PhoneManager;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.ConditionType.CONDITION_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.GOTO_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.skwindow.SKWindowManage;
import com.android.Samkoonhmi.system.SystemVariable;

/**
 * 报警登录
 */
public class AlarmGroup {

	private static final String TAG = "SKAlarm";
	private static final int HANDLER_LOOPER = 1;
	private static final int HANDLER_INIT = 2;
	private static final int HANDLER_SOUND = 3;
	private static final int HANDLER_ALARM_SOUND = 4;
	private static final int HANDLER_SAVE=5;
	private static final int OPEN_WINDOW = 6;// 打开窗口
	private static final int CLOSE_WINDOW = 7;// 关闭窗口
	private static final int RESTORE_DATA=8;//数据恢复
	private static final int HANDLER_WRITE=9;
	private static final int SAVE_TIME = 150000;// 2.5分钟保存一次
	private static int ALL_HIS_COUNT = 0;// 所有历史报警数据总数
	private static boolean bFull = false;// 是否清除历史数据，数据存满清除5W条
	public static boolean flag=false;
	// 监视间隔时间
	private static int nTime = 200;
	// 报警组
	private ArrayList<AlarmGroupInfo> list = null;
	// 监视线程
	private HandlerThread mThread;
	// 要监视的地址信息
	private ArrayList<SEND_DATA_STRUCT> mSendList = null;
	// 地址值
	
	private SKAlarmHandler handler;
	private ArrayList<IAlarmCallback> mCallbacks = null;
	private String sTaskName;
	// 报警声音
	private boolean alarmSound;
	private Context mContext;
	//private int alarmSoundCount;
	private Vibrator vibrator;
	/**
	 * key=0 表示报警产生or已经确定
	 * key=1表示报警已经消除了
	 * 把当前缓存区分成两组
	 * 一组是报警组
	 * 一组是消除组
	 * 目的是减少消除匹配时间
	 */
	private HashMap<Integer, Vector<AlarmDataInfo>> mOneList;
	private HashMap<Integer, Vector<AlarmDataInfo>> mTwoList;
	private Vector<AlarmWindowInfo> mWindow;
	private int index;
	private static Vector<AlarmDataInfo> mAlarmData;//存储当前处于报警信息
	private static Vector<AlarmDataInfo>mCloseAlarmData;//存储当前消除的报警
	//private Vector<AlarmDataInfo> mCopyData = new Vector<AlarmDataInfo>();

	private static AlarmGroup sInstance = null;

	public static AlarmGroup getInstance() {
		if (sInstance == null) {
			sInstance = new AlarmGroup();
		}
		return sInstance;
	}
	
	public boolean haveAlarmSound(){
		boolean ret = false;
		try {
			if (mAlarmData != null && mAlarmData .size() > 0) {
				for(int i = 0; i < mAlarmData.size(); i++){
					AlarmDataInfo info = mAlarmData.get(i);
					if (info != null  && info.getnClear() == 0) {
						ret = true;
						break;
					}
				}
			}
			
			if (!ret && mOneList != null && mOneList.get(0) != null) {
				for(int i = 0; i < mOneList.get(0).size(); i++){
					AlarmDataInfo info = mOneList.get(0).get(i);
					if (info != null && info.getnClear() == 0) {
						ret = true;
						break;
					}
				}
			}
			
			if (!ret && mTwoList != null&& mTwoList.get(0) != null) {
				for(int i = 0; i < mTwoList.size(); i++){
					AlarmDataInfo info = mTwoList.get(0).get(i);
					if (info != null && info.getnClear() == 0) {
						ret = true;
						break;
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return ret;
	}

	private AlarmGroup() {
		mThread = new HandlerThread("AlarmGroupThread");
		mThread.start();
		handler = new SKAlarmHandler(mThread.getLooper());
		handleCallback(CREATE_CALLBACKS, null);
	}

	/**
	 * 开始报警登录监视
	 */
	public void startAlarm(Context context) {
		this.mContext = context;
		handler.sendEmptyMessageDelayed(HANDLER_INIT, 10);
	}

	/**
	 * 初始化
	 */
	private void init() {
		bFull=false;
		//alarmSoundCount = 0;
		
		if (mAlarmData==null) {
			mAlarmData=DBTool.getInstance().getmAlarmBiz().setAlarmData();
			DBTool.getInstance().getmAlarmBiz().getAlarmMessage(mAlarmData);
			list = DBTool.getInstance().getmAlarmBiz().selectAlarmGroup(mAlarmData);
		}
		if (mCloseAlarmData == null) {
			mCloseAlarmData = new Vector<AlarmDataInfo>();
		}
		
		if (list != null) {
			
			if (list.size() > 0) {
				flag = true;
				index = 0;
				mOneList=new HashMap<Integer, Vector<AlarmDataInfo>>();
				mTwoList=new HashMap<Integer, Vector<AlarmDataInfo>>();
				mWindow = new Vector<AlarmGroup.AlarmWindowInfo>();
				if(mContext==null){
					mContext=SKSceneManage.getInstance().mContext;
				}
				vibrator = (Vibrator) mContext
						.getSystemService(Service.VIBRATOR_SERVICE);

				sTaskName=AlarmSaveThread.getInstance().getBinder().onRegister(sCallback);
				
				// 是否启动报警声音
				SharedPreferences sharedPreferences = mContext
						.getSharedPreferences("information", 0);
				alarmSound = sharedPreferences.getBoolean("alarm_sound", false);
				handler.sendEmptyMessage(HANDLER_ALARM_SOUND);

				if (alarmSound) {
					handler.sendEmptyMessage(HANDLER_SOUND);
				}

				// 启动保存数据
				handler.sendEmptyMessageAtTime(HANDLER_SAVE, SAVE_TIME);
				
				mSendList = new ArrayList<SEND_DATA_STRUCT>();

				/**
				 * 报警组
				 */
				for (int i = 0; i < list.size(); i++) {
					AlarmGroupInfo info = list.get(i);
					if (info != null) {
						/**
						 * 每一组报警
						 */
						HashMap<Integer, AlarmConditionInfo> map = info
								.getmConditionMap();
						if (map == null) {
							break;
						}
						for (Iterator<Entry<Integer, AlarmConditionInfo>> iters = map
								.entrySet().iterator(); iters.hasNext();) {

							// 每一组报警的报警条件
							Map.Entry<Integer, AlarmConditionInfo> entry = iters
									.next();
							AlarmConditionInfo item = entry.getValue();
							CallbackItem callback=new CallbackItem();
							
							if (item.geteCondition() == CONDITION_TYPE.ALARM_ON
									|| item.geteCondition() == CONDITION_TYPE.ALARM_OFF) {
								// 位
								callback.eDataType=DATA_TYPE.BIT_1;
								callback.onRegister(item.getmAddress(), true);
							} else {
								// 字
								callback.eDataType=item.getmDataType();
								callback.onRegister(item.getmAddress(), false);
							}
							item.setmCallbackItem(callback);
						}
					}
				}
			}
		}

		doWatch();
	}
	
//	private void updateAlarmMessage(){
//		for(int i =0; i < list.size(); i++){
//			AlarmGroupInfo info = list.get(i);
//			if (info == null || info.getmConditionMap() == null|| info.getmConditionMap().size() == 0) {
//				break;
//			}
//			HashMap<Integer, AlarmConditionInfo> map = info.getmConditionMap();
//			Iterator iterator = map.entrySet().iterator();
//			while (iterator.hasNext()) {
//				Map.Entry<Integer, AlarmConditionInfo> alamEntry = (Entry<Integer, AlarmConditionInfo>) iterator.next();
//				AlarmConditionInfo alarm = alamEntry.getValue();
//				if (alarm.getnClear() == 0) {
//					for(AlarmDataInfo bean :mAlarmData){
//						if (bean.getnGroupId() == alarm.getnGroupId() && bean.getnAlarmIndex() == alarm.getnAlarmIndex()) {
//							bean.setsMessage(alarm.)
//						}
//					}
//				}
//			}
//		}
//		
//	}

	public boolean isAlarmSound() {
		return alarmSound;
	}

	public void setAlarmSound(boolean alarmSound) {
		this.alarmSound = alarmSound;
	}

	/**
	 * 报警地址监视
	 */
	private boolean hasAlarm;//有报警触发
	private boolean hasClear;//
	private int nDiffer=0;
	private void doWatch() {

		/**
		 * list-报警组
		 */
		try {
			
			long start=System.currentTimeMillis();
			if (list != null) {
				hasAlarm=false;
				hasClear=false;
				ArrayList<AlarmDataInfo> aList=null;//在这个扫描时段，产生的报警,回调通知界面的信息
				ArrayList<AlarmDataInfo> cList=null;//在这个扫描时段，消除的报警,回调通知界面的信息
				
				for (int i = 0; i < list.size(); i++) {
					// info-存储的是每一组报警信息
					
					AlarmGroupInfo info = list.get(i);
					if (info == null || info.getmConditionMap() == null
							|| info.getmConditionMap().size() == 0) {
						break;
					}
					HashMap<Integer, AlarmConditionInfo> map = info
							.getmConditionMap();
					
					for(int pos = 0 ; pos < map.size(); pos++) {

						// 每一组报警的报警条件
						AlarmConditionInfo item = map.get(pos);
						if (item.getmAddress() != null) {
							
							RECEIVE_DATA_STRUCT plc = PlcRegCmnStcTools
									.getLastCmnInfo();
							
							// 通信状态,0-代表通信正常，其他通信不正常
							int state =0;
							if (plc != null) {
								state = plc.nErrorCode;
							}
							if (item.getmAddress().eConnectType==1) {
								//内部地址 没有通讯状态
								state=0;
							}
							
							float temp = item.getmCallbackItem().getnFValue();
							ArrayList<AlarmMessageInfo> mList = null;
							String message = "";
							if (info.getmMessageMaps().containsKey(item.getnAlarmIndex())) {
								mList = info.getmMessageMaps().get(item.getnAlarmIndex());
							}
							if (mList != null) {
								if (mList.size() > SystemInfo.getCurrentLanguageId()) {
									message = mList.get(SystemInfo.getCurrentLanguageId())
											.getsMessage();
								}
							}

							switch (item.geteCondition()) {
							case ALARM_ON: // on报警
								if (1 == temp && item.getnClear() == -1&&state==0) {
									item.setnClear(0);
								//	alarmSoundCount++;
									if (aList==null) {
										aList=new ArrayList<AlarmDataInfo>();
									}
									sendData(message, info.getnGroupId(), item.getnAlarmIndex(),
											item.isbOpenScene(), item.getnTargetPage(),
											item.getnSceneType(),aList, item.getbSendMsg(), item.getPhoneNum(), item.getbAddtoDb());
								} else if (0 == temp || state != 0) {
									// 报警消除
									if (item.getnClear() > -1) {
										item.setnClear(-1);
										if (cList==null) {
											cList=new ArrayList<AlarmDataInfo>();
										}
										alarmClear(info.getnGroupId(), item.getnAlarmIndex(),
												item.isbOpenScene(), item.getnTargetPage(),
												item.getnSceneType(),cList);
									}
								}
								break;
							case ALARM_OFF: // off报警
								if (0 == temp && item.getnClear() == -1&&state==0) {
									if (state==0) {
										//通信正常
									//	alarmSoundCount++;
										item.setnClear(0);
										if (aList==null) {
											aList=new ArrayList<AlarmDataInfo>();
										}
										sendData(message, info.getnGroupId(), item.getnAlarmIndex(),
												item.isbOpenScene(), item.getnTargetPage(),
												item.getnSceneType(),aList, item.getbSendMsg(), item.getPhoneNum(), item.getbAddtoDb());
									}
								} else if (1 == temp || state != 0) {
									if (item.getnClear() > -1) {
										item.setnClear(-1);
										if (cList==null) {
											cList=new ArrayList<AlarmDataInfo>();
										}
										alarmClear(info.getnGroupId(), item.getnAlarmIndex(),
												item.isbOpenScene(), item.getnTargetPage(),
												item.getnSceneType(),cList);
									}
								}
								break;
							case ALARM_VALUE: // 固定值报警
								if (temp == item.getnRangLow() && item.getnClear() == -1&&state==0) {
						//			alarmSoundCount++;
									item.setnClear(0);
									if (aList==null) {
										aList=new ArrayList<AlarmDataInfo>();
									}
									sendData(message, info.getnGroupId(), item.getnAlarmIndex(),
											item.isbOpenScene(), item.getnTargetPage(),
											item.getnSceneType(),aList, item.getbSendMsg(), item.getPhoneNum(), item.getbAddtoDb());
								} else if (temp != item.getnRangLow() || state != 0) {
									if (item.getnClear() > -1) {
										item.setnClear(-1);
										if (cList==null) {
											cList=new ArrayList<AlarmDataInfo>();
										}
										alarmClear(info.getnGroupId(), item.getnAlarmIndex(),
												item.isbOpenScene(), item.getnTargetPage(),
												item.getnSceneType(),cList);
									}
								}
								break;
							case ALARM_RANGE: // 范围内报警
								if (temp > item.getnRangLow() && temp < item.getnRangHigh()
										&& item.getnClear() == -1&&state==0) {
								//	alarmSoundCount++;
									item.setnClear(0);
									if (aList==null) {
										aList=new ArrayList<AlarmDataInfo>();
									}
									sendData(message, info.getnGroupId(), item.getnAlarmIndex(),
											item.isbOpenScene(), item.getnTargetPage(),
											item.getnSceneType(),aList, item.getbSendMsg(), item.getPhoneNum(), item.getbAddtoDb());
								} else if (temp < item.getnRangLow() || temp > item.getnRangHigh()
										|| state != 0) {
									if (item.getnClear() > -1) {
										item.setnClear(-1);
										if (cList==null) {
											cList=new ArrayList<AlarmDataInfo>();
										}
										alarmClear(info.getnGroupId(), item.getnAlarmIndex(),
												item.isbOpenScene(), item.getnTargetPage(),
												item.getnSceneType(),cList);
									}
								}
								break;
							case ALARM_RANGE_OUT: // 范围外报警
								if (temp < item.getnRangLow() || temp > item.getnRangHigh()) {
									if (item.getnClear() == -1&&state==0) {
							//			alarmSoundCount++;
										item.setnClear(0);
										if (aList==null) {
											aList=new ArrayList<AlarmDataInfo>();
										}
										sendData(message, info.getnGroupId(),
												item.getnAlarmIndex(), item.isbOpenScene(),
												item.getnTargetPage(), item.getnSceneType(),aList, item.getbSendMsg(), item.getPhoneNum(), item.getbAddtoDb());
									}
								} else if ((temp > item.getnRangLow() && temp < item.getnRangHigh())
										|| state != 0) {
									if (item.getnClear() > -1) {
										item.setnClear(-1);
										if (cList==null) {
											cList=new ArrayList<AlarmDataInfo>();
										}
										alarmClear(info.getnGroupId(), item.getnAlarmIndex(),
												item.isbOpenScene(), item.getnTargetPage(),
												item.getnSceneType(),cList);
									}
								}
								break;
							}
						}
					}
				}
				
				/**
				 * 扫描完毕
				 * 通知控件更新
				 */
				doCallback(aList,cList);
			}
			
			nDiffer=(int)(System.currentTimeMillis()-start);
			
			if (flag) {
				// 循环监视
				int time=nTime-nDiffer;
				if (time<10) {
					time=10;
				}else if (time>200) {
					time=200;
				}
				handler.sendEmptyMessageDelayed(HANDLER_LOOPER, time);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "AlarmGroup doWatch error!");
		}
		
	}
	
	/**
	 * @param aList-扫描时间所产生的报警
	 * @param cList-扫描时间所消除的报警
	 */
	private void doCallback(ArrayList<AlarmDataInfo> aList,ArrayList<AlarmDataInfo> cList){
		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		if (hasAlarm) {
			// 有报警产生
			if (callbacks != null) {
				for (int i = 0; i < callbacks.size(); i++) {
					callbacks.get(i).onAlarm(aList,bFull);
				}
			}
		}

		if (hasClear) {
			// 有报警消除
			if (callbacks != null) {
				for (int i = 0; i < callbacks.size(); i++) {
					callbacks.get(i).onClose(cList);
				}
			}
		}
	}

	/**
	 * 报警声音
	 */
	private void sound() {
		if (flag && alarmSound) {
			if (haveAlarmSound()) {
				vibrator.vibrate(150);
			}
			handler.sendEmptyMessageDelayed(HANDLER_SOUND, 500);
		}
	}

	
	/**
	 * 报警消息,写入数据库
	 * @param msg-报警消息
	 * @param gId-报警Id
	 * @param aId-报警序号Id
	 * @param open-是否打开窗口
	 * @param sid-画面or窗口id
	 * @param type-类型 0-画面 1-窗口
	 * @param bSendMsg-是否发送短信
	 * @param phoneNum 进行发送短信的号码
	 */
	private void sendData(String msg, int gId, int aId, boolean open, int sid,
			int type,ArrayList<AlarmDataInfo> list, boolean bSendMsg, String phoneNum, boolean saveDatabase) {
		hasAlarm=true;
		AlarmDataInfo data = new AlarmDataInfo();
		data.setnGroupId(gId);
		data.setnAlarmIndex(aId);
		data.setnDateTime(System.currentTimeMillis());
		data.setnClearDT(0);
		data.setsMessage(msg + "");
		data.setnClear((short) 0);// 0- 报警触发
		data.setbAddtoDB(saveDatabase);
		list.add(data);

		Vector<AlarmDataInfo> dlist = null;
		HashMap<Integer, Vector<AlarmDataInfo>> lists=getList(0);
		if (lists!=null) {
			dlist = lists.get(0);
			if (dlist != null) 
			{
				dlist.add(data);
			}
			else
			{
				dlist=new Vector<AlarmDataInfo>();
				dlist.add(data);
				lists.put(0, dlist);
			}
		}
		
		
		/**
		 * 掉电保存
		 */
//		AlamSaveProp aProp=SKSaveThread.getInstance().new AlamSaveProp();
//		aProp.nGroupId=(short)gId;
//		aProp.nIndex=(short)aId;
//		aProp.nStatus=(short)0;
//		aProp.nAlamTime=data.getnDateTime();
//		aProp.nRemoveAlamTime=0;
//		SKSaveThread.getInstance().saveAlamSaveProp(aProp);
		saveAlamSaveProp((short)gId, (short)aId, (short)0, data.getnDateTime(), 0);
		
		//报警打开窗口
		if (open) {
			AlarmWindowInfo info = new AlarmWindowInfo();
			info.add = 0;
			info.gid = gId;
			info.aid = aId;
			info.sid = sid;
			info.type = type;
			UIHandler.obtainMessage(OPEN_WINDOW, info).sendToTarget();
		}
		
		//报警发送信息
		if(bSendMsg){
			boolean isFirst = true;
			PhoneManager.getInstance().sendMSM(phoneNum, msg, isFirst);
		}
		
		if (!bFull) {
			ALL_HIS_COUNT++;// 所有历史报警数量
		}
		

	}

	/**
	 * 报警数据保存
	 * 
	 * @param type=0 正常信息保存，type=1 确定所有报警，type=2 删除所有报警，type=3 删除历史报警
	 */
	public boolean saveAlarmData(int type,int call,IAlarmCallback callback,int taskId, ArrayList<Integer>dataGroup) {

		
		if (handler==null) {
			return false;
		}
		handler.removeMessages(HANDLER_SAVE);// 已经保存，删除在等待的信息
		handler.sendEmptyMessageDelayed(HANDLER_SAVE, SAVE_TIME);

		if (ALL_HIS_COUNT > 150000) {
			// 数据存满，删除旧数据
			bFull = true;
		} else {
			bFull = false;
		}
		
		AlarmData aData=new AlarmData();
		aData.type=type;
		aData.call=call;
		aData.clear=bFull;
		aData.callback=callback;
		aData.nTaskId=taskId;
		aData.group = dataGroup;
		
		updateAlarmData(type, dataGroup);
		HashMap<Integer, Vector<AlarmDataInfo>> map = new HashMap<Integer, Vector<AlarmDataInfo>>();
		if (mAlarmData != null) {
			Vector<AlarmDataInfo> dataInfos = new Vector<AlarmDataInfo>();
			dataInfos.addAll(mAlarmData);
			//进行帅选要保存数据库的
			for(int i =0; i < dataInfos.size(); i++){
				AlarmDataInfo info = dataInfos.get(i);
				if (info != null && !info.getbAddtoDb()) {
					dataInfos.remove(i);
					i--;
				}
			}
			map.put(0, dataInfos);
		}
		
		{
			if (mCloseAlarmData == null) {
				mCloseAlarmData = new Vector<AlarmDataInfo>();
			}
			
			for(int i = 0; i < mCloseAlarmData.size(); i++){
				AlarmDataInfo info = mCloseAlarmData.get(i);
				if (info != null && !info.getbAddtoDb()) {
					mCloseAlarmData.remove(i);
					i--;
				}
			}
			
			map.put(1, mCloseAlarmData);
			aData.data=map;
		}
		
		
		handler.obtainMessage(HANDLER_WRITE,aData).sendToTarget();
		return true;
	}

	/**
	 * 更新报警状态
	 */
	private synchronized void updateAlarmState(int gid, int aid, short clear,ArrayList<AlarmDataInfo> list) {
		HashMap<Integer, Vector<AlarmDataInfo>> data=getList(1);
		if (data==null) {
			return;
		}
		
		boolean reslut = false;
		if (clear==2) {
			/**
			 * 报警消除
			 */
			Vector<AlarmDataInfo> temp=null;
			if (data.containsKey(0)) {
				temp=data.get(0);
				for (int i = 0; i < temp.size(); i++) {
					AlarmDataInfo info =temp.get(i);
					if (info.getnGroupId() == gid && info.getnAlarmIndex() == aid) {
						reslut = true;
						info.setnClear(clear);
						info.setnClearDT(System.currentTimeMillis());
						
						/**
						 * 把消除的报警，从报警组移动到消除组
						 */
						if (data.containsKey(1)) {
							Vector<AlarmDataInfo> c=data.get(1);
							c.add(info);
						}else {
							Vector<AlarmDataInfo> c=new Vector<AlarmDataInfo>();
							c.add(info);
							data.put(1, c);
						}
						temp.remove(i);
						--i;
						
						/**
						 * 掉电保存
						 */
//						AlamSaveProp aProp=SKSaveThread.getInstance().new AlamSaveProp();
//						aProp.nGroupId=(short)gid;
//						aProp.nIndex=(short)aid;
//						aProp.nStatus=(short)2;
//						aProp.nAlamTime=info.getnDateTime();
//						aProp.nRemoveAlamTime=info.getnClearDT();
//						SKSaveThread.getInstance().saveAlamSaveProp(aProp);
						saveAlamSaveProp((short)gid, (short)aid, (short)2, info.getnDateTime(), info.getnClearDT());
						
						//有报警消除
						if (list!=null) {
							list.add(info);
						}
						break;
					}
				}
			}
			// 防止当个报警的报警 、消除切换平率太高，导致 getList中和mAlarmData都重复添加
			if (reslut) {
				for(int i = 0; i < mAlarmData.size(); i++){
					AlarmDataInfo info = mAlarmData.get(i);
					if (info != null && info.getnGroupId() == gid && info.getnAlarmIndex() == aid) {
						mAlarmData.remove(info);
						break;
					}
				}
			}
			
		}else if (clear==1) {
			/**
			 * 报警确定
			 */
			Vector<AlarmDataInfo> temp=null;
			if (data.containsKey(0)) {
				temp=data.get(0);
				for (int i = 0; i < temp.size(); i++) {
					AlarmDataInfo info =temp.get(i);
					if (info.getnGroupId() == gid && info.getnAlarmIndex() == aid) {
						reslut = true;
						info.setnClear(clear);
						info.setnClearDT(0);
						
						/**
						 * 掉电保存
						 */
//						AlamSaveProp aProp=SKSaveThread.getInstance().new AlamSaveProp();
//						aProp.nGroupId=(short)gid;
//						aProp.nIndex=(short)aid;
//						aProp.nStatus=(short)1;
//						aProp.nAlamTime=info.getnDateTime();
//						aProp.nRemoveAlamTime=0;
//						SKSaveThread.getInstance().saveAlamSaveProp(aProp);
						
						saveAlamSaveProp((short)gid, (short)aid, (short)1, info.getnDateTime(), 0);
						break;
					}
				}
			}
		}


		if (!reslut) {
			if (mAlarmData==null) {
				return;
			}
			for (int i = 0; i < mAlarmData.size(); i++) {
				AlarmDataInfo info=mAlarmData.get(i);
				if (info.getnGroupId()==gid&&info.getnAlarmIndex()==aid) {
					if (clear==2) {
						info.setnClearDT(System.currentTimeMillis());
						info.setnClear(clear);
						list.add(info);
						if (data.containsKey(1)) {
							data.get(1).add(info);
						}else {
							Vector<AlarmDataInfo> c=new Vector<AlarmDataInfo>();
							c.add(info);
							data.put(1, c);
						}
						mAlarmData.remove(i);
						
						/**
						 * 掉电保存
						 */
//						AlamSaveProp aProp=SKSaveThread.getInstance().new AlamSaveProp();
//						aProp.nGroupId=(short)gid;
//						aProp.nIndex=(short)aid;
//						aProp.nStatus=(short)2;
//						aProp.nAlamTime=info.getnDateTime();
//						aProp.nRemoveAlamTime=info.getnClearDT();
//						SKSaveThread.getInstance().saveAlamSaveProp(aProp);
						
						saveAlamSaveProp((short)gid, (short)aid, (short)2, info.getnDateTime(), info.getnClearDT());
						break;
					}else if (clear==1) {
						info.setnClearDT(0);
						info.setnClear(clear);
						if (data.containsKey(0)) {
							data.get(0).add(info);
						}else {					
							Vector<AlarmDataInfo> c=new Vector<AlarmDataInfo>();
							c.add(info);
							data.put(0, c);
						}
						mAlarmData.remove(i);
						
						/**
						 * 掉电保存
						 */
//						AlamSaveProp aProp=SKSaveThread.getInstance().new AlamSaveProp();
//						aProp.nGroupId=(short)gid;
//						aProp.nIndex=(short)aid;
//						aProp.nStatus=(short)1;
//						aProp.nAlamTime=info.getnDateTime();
//						aProp.nRemoveAlamTime=0;
//						SKSaveThread.getInstance().saveAlamSaveProp(aProp);
						saveAlamSaveProp((short)gid, (short)aid, (short)1, info.getnDateTime(), 0);
						break;
					}
					
				}
			}
		}
	}

	/**
	 * @param type=0 插入
	 *        type=1 更新
	 *        type=2 保存
	 */
	private synchronized HashMap<Integer, Vector<AlarmDataInfo>> getList(int type){
		
		if (type==0||type==1) {
			if(index == 0){
				return mOneList;
			}else {
				return mTwoList;
			}
		}else if (type==2) {
			if (index == 0) {
				index = 1;
				return mOneList;
			} else {
				index = 0;
				return mTwoList;
			}
		}
		return null;
	}
	
	/**
	 * 消息队列
	 */
	class SKAlarmHandler extends Handler {

		SKAlarmHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_LOOPER:
				// 监视报警 地址变化
				doWatch();
				break;
			case HANDLER_INIT:
				//Log.d(TAG, "alarm group thread id:"+Thread.currentThread().getId());
				// 初始化数据
				//Log.d(TAG, "HANDLER_INIT.............");
				init();
				break;
			case HANDLER_SOUND:// 报警声音
				sound();
				break;
			case HANDLER_ALARM_SOUND:
				// 查询总共有多少条报警
				AlarmCount();
				break;
			case RESTORE_DATA:
			{
				//Log.d(TAG, "RESTORE_DATA.............");
				//掉电恢复
				Vector<AlamSaveProp> list=(Vector<AlamSaveProp>)msg.obj;
				if (list!=null) {
					restoreData(list);
				}
				break;
			}
			case HANDLER_SAVE:
			{
				if (flag) {
					if (ALL_HIS_COUNT > 150000) {
						// 数据存满，删除旧数据
						bFull = true;
					} else {
						bFull = false;
					}

					handler.removeMessages(HANDLER_SAVE);
					handler.sendEmptyMessageDelayed(HANDLER_SAVE, SAVE_TIME);
					
					//Log.d(TAG, "HANDLER_SAVE....");

					HashMap<Integer, Vector<AlarmDataInfo>> temp = getList(2);
					if (temp != null) {
						if (!temp.containsKey(0)) {
							Vector<AlarmDataInfo> list=new Vector<AlarmDataInfo>();
							temp.put(0, list);
						}
						if (!temp.containsKey(1)) {
							Vector<AlarmDataInfo> list=new Vector<AlarmDataInfo>();
							temp.put(1, list);
						}
						
						if (temp.get(0).size() == 0 && temp.get(1).size() == 0) {
							return;
						}

						AlarmData data = new AlarmData();
						data.type = 0;
						data.call=0;
						data.clear=bFull;
						data.callback=null;
						HashMap<Integer, Vector<AlarmDataInfo>> map = new HashMap<Integer, Vector<AlarmDataInfo>>();
						map.put(0, temp.get(0));
						map.put(1, temp.get(1));
						data.data = map;
						temp.clear();
						
						handler.obtainMessage(HANDLER_WRITE, data).sendToTarget();

					}
				}
				break;
			}
			case HANDLER_WRITE:
			{
				//数据写入数据库
				AlarmData data=(AlarmData)msg.obj;
				if (data==null) {
					return;
				}
				
				AlarmSaveThread
				.getInstance()
				.getBinder()
				.onTask(MODULE.ALARM, TASK.ALARM_WRITE,
						sTaskName, data, 10);
				break;
			}

			}
		}

	}

	/**
	 * 主线程UI队列
	 */
	Handler UIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OPEN_WINDOW:
			{
				// 打开窗口
				AlarmWindowInfo info = (AlarmWindowInfo) msg.obj;
		
				if (info==null) {
					return;
				}
				doWindowOper(0, info);
				break;
			}
			case CLOSE_WINDOW:
			{
				// 关闭窗口
				AlarmWindowInfo info = (AlarmWindowInfo) msg.obj;
				
				if (info==null) {
					return;
				}
				doWindowOper(1, info);
				break;
			}
			
			}
		}

	};

	/**
	 * 同步 处理画面打开和关闭
	 */
	private synchronized void doWindowOper(int type, AlarmWindowInfo winfo) {
		if (type == 0) {
			if (mWindow.size() == 0) {
				// 报警之前画面
				AlarmWindowInfo info1 = new AlarmWindowInfo();
				info1.gid = -1;
				info1.aid = -1;
				info1.sid = SKSceneManage.getInstance().nSceneId;
				if (SKSceneManage.getInstance().geteType() == SHOW_TYPE.DEFAULT) {
					info1.type = 0;
					
					if (winfo.type==0) {
						//弹出的是画面
						info1.bGoto=true;
					}else {
						//弹出的是窗口
						info1.bGoto=false;
					}
					
				} else {
					info1.type = 1;
				}
				
				mWindow.add(info1);
			}

			if (winfo.add == 0) {
				// 添加
				mWindow.add(winfo);
			}
			
			if (winfo.type == 0) {
				// 画面
				SKSceneManage.getInstance().gotoWindow(0, winfo.sid, true, 6,GOTO_TYPE.ALARM);
			} else if (winfo.type == 1) {
				// 窗口
				//Log.d(TAG, "open.....");
				SKSceneManage.getInstance().gotoWindow(1, winfo.sid, false,0,GOTO_TYPE.ALARM);
			}
		} else {
			if (mWindow.size() <= 2) {
				if (mWindow.size()==0) {
					return;
				}
				
				// 所有报警跳转窗口已经全部消除,跳转到之前报警画面
				SKSceneManage.getInstance().gotoWindow(4, winfo.sid, true, 6,GOTO_TYPE.ALARM);
				
				AlarmWindowInfo info1 = mWindow.get(0);
				if (info1.type == 0) {
					if (info1.bGoto) {
						//弹出的画面
						SKSceneManage.getInstance().gotoWindow(0, winfo.sid, true, 6,GOTO_TYPE.ALARM);
					}
				} else {
					SKProgress.hide();// 隐藏等待框
					int id=DBTool.getInstance().getmSceneBiz().getWindowId(info1.sid);
					if (id>-1) {
						SKSceneManage.getInstance().gotoWindow(1, id, false,0,GOTO_TYPE.ALARM);
					}
				}
				mWindow.clear();
			} else {
				for (int i = 0; i < mWindow.size(); i++) {
					AlarmWindowInfo info1 = mWindow.get(i);
					if (info1.aid == winfo.aid && info1.gid == winfo.gid) {
						if (i == mWindow.size() - 1) {
							// 关闭的是最后一个
							mWindow.remove(i);
							AlarmWindowInfo temp = mWindow
									.get(mWindow.size() - 1);
							temp.add = 1;
							UIHandler.obtainMessage(OPEN_WINDOW, temp)
									.sendToTarget();
						} else {
							mWindow.remove(i);
							--i;
						}
					}
				}
			}

		}
	}
	
	/**
	 * 删除画面
	 */
	public void removeWindow(int type,int id){
		if (mWindow!=null) {
			if (mWindow.size()>1) {
				for (int i = 0; i < mWindow.size(); i++) {
					AlarmWindowInfo info=mWindow.get(i);
					if (info.type==type&&info.sid==id) {
						mWindow.remove(i);
						--i;
						break;
					}
				}
			}
		}
	}

	/**
	 * 确定报警
	 */
	public void confirmAlarm() {
		if (list == null || list.size() == 0) {
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			AlarmGroupInfo info = list.get(i);
			if (info.getmConditionMap() != null) {
				for (Iterator<Entry<Integer, AlarmConditionInfo>> iters = info
						.getmConditionMap().entrySet().iterator(); iters
						.hasNext();) {
					// 每一组报警的报警条件
					Map.Entry<Integer, AlarmConditionInfo> entry = iters.next();
					AlarmConditionInfo item = entry.getValue();
					if (item.getnClear() == 0) {
						item.setnClear(1);
					}
				}
			}
		}
		//saveAlarmData(1,0,null,0)
	    //saveAlarmData(1, 1, alarmCall, nTaskId, confirmList);

	}
	
	/**
	 * 确定报警
	 */
	public void confirmAlarm(ArrayList<Integer>ilist){
		if (ilist == null || ilist.size() <=0) {
			return;
		}
		
		
		boolean update = false;
		for(AlarmGroupInfo info : list){
			if (info.getmConditionMap() != null) {
				for (Iterator<Entry<Integer, AlarmConditionInfo>> iters = info.getmConditionMap().entrySet().iterator(); iters.hasNext();) {
					// 每一组报警的报警条件
					Map.Entry<Integer, AlarmConditionInfo> entry = iters.next();
					AlarmConditionInfo item = entry.getValue();
					
					if (item.getnClear() == 0 && ilist.contains(item.getnGroupId())) {
						item.setnClear(1);
					     update = true;
					}
				}
			}
		}
		
		if (update) {
			 saveAlarmData(1, 0, null, 0, ilist);

			updateData(ilist);
		}
	}
	

	/**
	 * 确定报警
	 */
	public void confirmAlarm(int gId, int aId) {
		
		if (list == null || list.size() == 0) { 
			return;
		}
		
		updateAlarmState(gId, aId, (short) 1,null);
		
		for (int i = 0; i < list.size(); i++) {
			AlarmGroupInfo info = list.get(i);
			if (info.getmConditionMap() != null) {
				if (info.getmConditionMap().containsKey(aId)) {
					AlarmConditionInfo item = info.getmConditionMap().get(aId);
					if (item.getnGroupId() == gId
							&& item.getnAlarmIndex() == aId) {
						item.setnClear(1);
						updateData(gId, aId);
						break;
					}
				}
			}
		}

	}
	


	/**
	 * 更新报警确定信息
	 */
	private void updateData(ArrayList<Integer> confirmList) {
		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		if (callbacks != null) {
			for (int i = 0; i < callbacks.size(); i++) {
				callbacks.get(i).onConfirm(confirmList);
			}
		}
	}

	/**
	 * 更新报警确定信息
	 */
	private void updateData(int gid, int aid) {
		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		if (callbacks != null) {
			for (int i = 0; i < callbacks.size(); i++) {
				callbacks.get(i).onConfirm(gid, aid);
			}
		}
	}

	/**
	 * 报警消除
	 */
	private void alarmClear(int gid, int aid, boolean open, int sid, int type,ArrayList<AlarmDataInfo> list) {


		/**
		 * 打开窗口
		 */
		if (open) {
			AlarmWindowInfo info = new AlarmWindowInfo();
			info.add = 0;
			info.gid = gid;
			info.aid = aid;
			info.sid = sid;
			info.type = type;
			UIHandler.obtainMessage(CLOSE_WINDOW, info).sendToTarget();
		}
		
		hasClear=true;
		updateAlarmState(gid, aid, (short) 2,list);


	}

	
	/**
	 * 报警清除 nClear = 0 去除缓存和数据库中的数据
	 */
	public void deleteAlarmData(ArrayList<Integer> list){
		if (mAlarmData == null ) 
			return;
		
		saveAlarmData(2, 0, null, 0, list);
		
		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		if (callbacks != null) {
			for(IAlarmCallback callback : callbacks){
				callback.onClear(list);
			}
		}
	}

	/**
	 * 当前总共有多少条报警
	 */
	private void AlarmCount() {
		ALL_HIS_COUNT = DBTool.getInstance().getmAlarmBiz()
				.selectAlarmCount(-1,0,0,0);
		if (ALL_HIS_COUNT>=150000) {
			bFull=true;
		}else {
			bFull=false;
		}
	}


	
	/**
	 * 清除历史报警数据 清除对象 nClear=1 or nClear = 2
	 */
	public void clearHisData(ArrayList<Integer> clist){	
//		if (mAlarmData == null)
//			return ;
		
		if (clist == null) {
			clist = new ArrayList<Integer>();
			for(int i = 0; i < list.size(); i++ ){
				AlarmGroupInfo info = list.get(i);
				clist.add(info.getnGroupId());
			}
		}
		saveAlarmData(3, 0, null, 0,clist);
		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		for(IAlarmCallback callback: callbacks){
			callback.onClearHistory(clist);
		}
		
	}
	

	/**
	 * 掉电恢复
	 */
	public void restore(Vector<AlamSaveProp> list){
		if(handler!=null){
			handler.obtainMessage(RESTORE_DATA, list).sendToTarget();
		}
	}
	
	private void restoreData(Vector<AlamSaveProp> list){
		HashMap<Long, AlamSaveProp> data=new HashMap<Long, AlamSaveProp>();
		for (int i = 0; i < list.size(); i++) {
			AlamSaveProp prop=list.get(i);
			if (data.containsKey(prop.nAlamTime)) {
				AlamSaveProp temp=data.get(prop.nAlamTime);
				if (temp.nStatus<prop.nStatus) {
					temp.nStatus=prop.nStatus;
					temp.nRemoveAlamTime=prop.nRemoveAlamTime;
				}
			}else {
				data.put(prop.nAlamTime, prop);
			}
		}
		Log.d(TAG, "restore data size:"+data.size());
		DBTool.getInstance().getmAlarmBiz().restoreData(data);
	}
	
	/**
	 * 设置是否启动报警声音
	 */
	public void setSound() {
		if (alarmSound) {
			alarmSound = false;
		} else {
			alarmSound = true;
		}
		saveSound();
	}
	
	public void saveSound(){
		if (mContext == null) {
			mContext = SKSceneManage.getInstance().mContext;
		}
		SystemVariable.getInstance().setIsAlarmBeep();
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences(
				"information", 0).edit();
		shareEditor.putBoolean("alarm_sound", alarmSound);
		shareEditor.commit();
		if (alarmSound) {
			handler.sendEmptyMessage(HANDLER_SOUND);
		}
	}

	/**
	 * 销毁
	 */
	public void destroy() {
		handleCallback(DESTROY_CALLBACKS, null);
		AlarmSaveThread.getInstance().getBinder().onDestroy(sCallback, sTaskName);
	}

	/**
	 * 历史数据导出
	 * @param path-存储路径
	 */
	private String sPath = "";
	public void exportFile(String path) {
		sPath = path;
		AlarmSaveThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_EXPORT, sTaskName, 0);
	}
	private final int REGISTER_CALLBACK = 0; //注册callback
	private final int REMOVE_CALLBACK = 1; //销毁callback
	private final int GET_CALLBACK = 2;     //获取callback
	private final int CREATE_CALLBACKS = 3;  //创建callbacks
	private final int DESTROY_CALLBACKS = 4; //删除callbacks
	private synchronized ArrayList<IAlarmCallback> handleCallback(int handle, IAlarmCallback callback){
		
		if(handle == REGISTER_CALLBACK){
			handleCallback(CREATE_CALLBACKS, null);
			for (int i = 0; i < mCallbacks.size(); i++) {
				if (mCallbacks.get(i) == callback) {
					mCallbacks.remove(i);
					break;
				}
			}
			
			mCallbacks.add(callback);
		}
		else if (handle == REMOVE_CALLBACK) {
			if (mCallbacks != null) {
				for (int i = 0; i < mCallbacks.size(); i++) {
					if (mCallbacks.get(i) == callback) {
						mCallbacks.remove(i);
						break;
					}
				}
			}
		}
		else if (handle == GET_CALLBACK) {
			needUpdataGroup = true;
			return mCallbacks;
		}
		else if (handle == CREATE_CALLBACKS) {
			if (mCallbacks == null) {
				mCallbacks = new ArrayList<AlarmGroup.IAlarmCallback>();
			}
		}
		else if (handle == DESTROY_CALLBACKS) {
			if (mCallbacks != null) {
				mCallbacks.clear();
			}
		}
		
		return null;
	}

	/**
	 * 获取绑定控件
	 */
	AlarmGroup.IAlarmBinder binder = new AlarmGroup.IAlarmBinder() {

		@Override
		public boolean onRegister(IAlarmCallback callback) {
			handleCallback(REGISTER_CALLBACK, callback);
			return true;
		}

		@Override
		public boolean onDestroy(IAlarmCallback callback) {
			handleCallback(REMOVE_CALLBACK, callback);
			return true;
		}
	};

	/**
	 * 绑定接口
	 */
	public interface IAlarmBinder {
		// 注册回调
		boolean onRegister(IAlarmCallback callback);

		// 取消回调
		boolean onDestroy(IAlarmCallback callback);
	}

	/**
	 * 报警触发，通知已注册的报警控件
	 */
	public interface IAlarmCallback {
		/**
		 * 清除报警  数据库中进行清除
		 */
		void onClear(ArrayList<Integer> delList);

		/**
		 * 报警消除     改变报警的状态为nClear = 2
		 * @param gid-报警组id
		 * @param aid-报警组子项id
		 */
		void onClose(ArrayList<AlarmDataInfo> list);

		/**
		 * 报警发生
		 * @param info-报警数据对象
		 */
		void onAlarm(ArrayList<AlarmDataInfo> list,boolean full);

		/**
		 * 删除报警历史数据
		 */
		void onClearHistory(ArrayList<Integer> hisList);

		/**
		 * 报警确定
		 */
		void onConfirm(ArrayList<Integer>confirmList);

		/**
		 * 单个报警确定
		 * @param gid-报警组id
		 * @param aid-报警组子项id
		 */
		void onConfirm(int gid, int aid);
		
		/**
		 * 通知控件更新
		 */
		void update(int taskId);
		
		/**
		 * 日期改变通知
		 */
		void onDateChange();
	}
	
	/**
	 * 日期改变通知
	 */
	public void dateChange(){
		
		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		if (callbacks!=null) {
			for (int i = 0; i < callbacks.size(); i++) {
				callbacks.get(i).onDateChange();
			}
		}
		
	}


	/**
	 * 获取绑定对象
	 */
	public AlarmGroup.IAlarmBinder getBinder() {
		return binder;
	}
	
	
	/**
	 * 报警数据存储
	 */
	AlarmSaveThread.ICallback sCallback=new AlarmSaveThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			if (taskId==TASK.ALARM_WRITE) {
				//报警数据写入数据库
				AlarmData data=(AlarmData)msg;
				if (data==null) {
					return;
				}
				alarmData(data);
			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			if(taskId==TASK.ALARM_EXPORT){
				// 报警历史数据导出
				saveAlarmData(0,2,null,0 ,null);
			}
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			
		}
	};
	
	private synchronized void alarmData(AlarmData data){

		ArrayList<IAlarmCallback>callbacks = handleCallback(GET_CALLBACK, null);
		if (data.data==null) {
			if (data.call==1) {
				if (data.callback!=null) {
					if (callbacks.contains(data.callback)) {
						data.callback.update(data.nTaskId);
					}
					return;
				}
			}
		}else {
			if (data.data.get(0).size()==0&&data.data.get(1).size()==0 ) {
				if (data.call==1) {
					if (data.callback!=null) {
						if (callbacks.contains(data.callback)) {
							data.callback.update(data.nTaskId);
						}
						return;
					}
				}
			}
		}

//		if(data.data!=null){
//			if (data.data.get(0)!=null) {
//				if (data.data.get(0).size()>0) {
//					Vector<AlarmDataInfo> temp=data.data.get(0);
//					mAlarmData.addAll(temp);
//
//				}
//				Vector<AlarmDataInfo> tempData=data.data.get(0);
//				tempData.clear();
//				tempData.addAll(mAlarmData);					
//			}
//		}
//		if (data.type == 1) {
//			
//		}
		
		int changeCount = DBTool.getInstance().getmAlarmBiz().saveAlarmData(data.data, data.clear,data.type, data.group);
		if (data.type == 2 || data.type == 3) {
			ALL_HIS_COUNT -= changeCount;
		}
//		if (data.type == 2 && data.group != null)// 清除报警 同步缓存 去掉nClear =0 的数据   
//		{
//			for(int i = 0; i < mAlarmData.size(); i++){
//				AlarmDataInfo info = mAlarmData.get(i);
//				if (info != null) {
//					if (data.group.contains(info.getnGroupId()) && info.getnClear() == 0) {
//						mAlarmData.remove(i);
//						--i;
//					}
//				}
//			}
//
//			ALL_HIS_COUNT -= changeCount;
//		}
//		else if (data.type == 1 && data.group != null) {//报警确定  将nClear = 0 变为 nClear = 1
//			for(int i = 0; i < mAlarmData.size(); i++){
//				AlarmDataInfo info = mAlarmData.get(i);
//				if (info != null && data.group.contains(info.getnGroupId()) && info.getnClear() == 0) {
//					info.setnClear((short)1);
//				}
//			}
//		}
//		else if( data.type == 3 && data.group != null ){ //清除历史数据， 清除nclear = 1 ,2 
//			for(int i = 0 ; i < mAlarmData.size(); i++){
//				AlarmDataInfo info = mAlarmData.get(i);
//				if (info != null && data.group.contains(info.getnGroupId()) && info.getnClear() > 0) {
//					mAlarmData.remove(i);
//					--i;
//				}
//			}
//			ALL_HIS_COUNT -= changeCount;
//		}
		
		if (data.call==1) {
			if (data.callback!=null) {
				data.callback.update(data.nTaskId);
			}
		}else if (data.call==2) {
			boolean result = DBTool.getInstance().getmAlarmBiz()
					.exportData(sPath);
			if (result) {
				SKToast.makeText(mContext.getString(R.string.export_success), Toast.LENGTH_SHORT).show();
			} else {
				SKToast.makeText(mContext.getString(R.string.export_failed), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	

	
	/**
	 * 
	 * @param type- 确定报警（1）:将nClear=0的状态变为nClear=1 
	 *            - 清除报警（2）：除去nClear=0的报警
	 *            - 清除历史数据（3）：清除nClear=1、2的报警
	 * @param group-进行操作的报警组
	 */
	public synchronized void updateAlarmData(int type ,ArrayList<Integer>group){
		
		HashMap<Integer, Vector<AlarmDataInfo>> data = null;
		data=getList(2);
		
		//添加 有新状态的报警
		if (data!=null) {
			if (data.containsKey(0)) {
				mAlarmData.addAll(data.get(0));
			}
			if ( data.containsKey(1)) {
				mCloseAlarmData.addAll(data.get(1));
			}
			data.clear();
		}
		
		if (type == 1 && group != null) {//确定报警
			for(int i = 0; i < mAlarmData.size(); i++){
				AlarmDataInfo info = mAlarmData.get(i);
				if (info != null && info.getnClear() == 0 && group.contains(info.getnGroupId())) {
					info.setnClear((short)1);
					
//					//同时进行掉电保存
//					AlamSaveProp aProp=SKSaveThread.getInstance().new AlamSaveProp();
//					aProp.nGroupId=(short)info.getnGroupId();
//					aProp.nIndex=(short)info.getnAlarmIndex();
//					aProp.nStatus=(short)1;
//					aProp.nAlamTime=info.getnDateTime();
//					aProp.nRemoveAlamTime=0;
//					SKSaveThread.getInstance().saveAlamSaveProp(aProp);
					saveAlamSaveProp((short)info.getnGroupId(), (short)info.getnAlarmIndex(), (short)1, info.getnDateTime(), 0);
				}
			}
		}
		else if (type == 2 && group != null) {//清除报警
			for(int i = 0; i <mAlarmData.size() ; i++){
				AlarmDataInfo info = mAlarmData.get(i);
				if (info != null && info.getnClear() == 0 && group.contains(info.getnGroupId())) {
					mAlarmData.remove(i);
					i--;
				}
			}
		}
		else if (type == 3 && group != null) {
			for(int i = 0 ; i < mAlarmData.size(); i++){
				AlarmDataInfo info = mAlarmData.get(i);
				if (info != null && info.getnClear() > 0 && group.contains(info.getnGroupId())) {
					mAlarmData.remove(i);
					i--;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param groupId - 报警组
	 * @param alarmIndex - 报警编号
	 * @param alarmState - 报警状态
	 * @param starttime - 报警长生时间
	 * @param endtime - 报警消除时间
	 */
	private void saveAlamSaveProp(short groupId, short alarmIndex, short alarmState, long starttime, long endtime){
		
		AlamSaveProp aProp = SKSaveThread.getInstance().new AlamSaveProp();
		aProp.nGroupId=groupId;
		aProp.nIndex= alarmIndex;
		aProp.nStatus= alarmState;
		aProp.nAlamTime= starttime;
		aProp.nRemoveAlamTime = endtime;
		SKSaveThread.getInstance().saveAlamSaveProp(aProp);
	}
	
	
	public void setNeedUpdateAlarm(boolean ret){
		needUpdataGroup = ret;
	}
	
	private boolean needUpdataGroup = true; 
	private ArrayList<AlarmDataInfo> mAlarmingList = new  ArrayList<AlarmDataInfo>();
	
	/**
	 * 获取已触发的报警信息clear == 0
	 */
	public ArrayList<AlarmDataInfo> getHappenedAlarmData()
	{
		if (needUpdataGroup ) {
			needUpdataGroup = false ;
			mAlarmingList.clear();
			
			try {
				updateAlarmData(0, null);
				for(int i=0; i< mAlarmData.size(); i++){
					AlarmDataInfo info = mAlarmData.get(i);
					if (info != null && info.getnClear() == 0) {
						mAlarmingList.add(info);
					}
				}
				
			} catch (Exception e) {
			Log.e("AlarmGroup", "copy data error!!");	
			}
		}
		ArrayList<AlarmDataInfo> infoList = new ArrayList<AlarmDataInfo>();
		infoList.addAll(mAlarmingList);
		return infoList;
	}
	
	
	public ArrayList<AlarmGroupInfo> getAlarmGroupList()
	{
		if (list == null)
		{
			mAlarmData=DBTool.getInstance().getmAlarmBiz().setAlarmData();
			DBTool.getInstance().getmAlarmBiz().getAlarmMessage(mAlarmData);
			list = DBTool.getInstance().getmAlarmBiz().selectAlarmGroup(mAlarmData);
		}
		
		return list;
	}
	
	class AlarmData{
		int type;//保存类型
		int call;//是否回调，0-不回调，1-回调
		boolean clear;
		IAlarmCallback callback;
		int nTaskId;
		HashMap<Integer, Vector<AlarmDataInfo>> data;
		ArrayList<Integer> group;//指定进行操作的数据组
	}

	/**
	 * 清除历史报警自动复位地址值
	 */
	private Vector<Integer> dataListInt;
	SystemVariable.autoCall autoCall = new SystemVariable.autoCall() {

		@Override
		public void autoChange(AddrProp addr) {
			// TODO Auto-generated method stub
			if (dataListInt == null) {
				dataListInt = new Vector<Integer>();
			} else {
				dataListInt.clear();
			}
			SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
			mSendData.eDataType = DATA_TYPE.BIT_1;
			dataListInt.add(0);
			PlcRegCmnStcTools.setRegIntData(addr, dataListInt, mSendData);

		}

	};

	/**
	 * 报警画面跳转
	 */
	public class AlarmWindowInfo {
		int gid;// 报警组
		int aid;// 报警序号
		int sid;// 跳转id
		int type;// 跳转类型，0-画面，1-窗口
		int add; // 0-添加，1-不添加，只打开
		boolean bGoto;//是否切换到，报警之前的画面，如果弹出的是窗口，则不需要，如果切换的是画面，则需要
		
	}
}
