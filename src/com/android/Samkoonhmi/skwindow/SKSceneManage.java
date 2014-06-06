package com.android.Samkoonhmi.skwindow;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKSaveThread;
import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.SKTrendsThread;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.macro.corba.IAKDraw;
import com.android.Samkoonhmi.macro.corba.IListener;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.activity.TimeBroadCast;
import com.android.Samkoonhmi.activity.SKSceneMenu;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.TouchShowInfoBiz;
import com.android.Samkoonhmi.model.AcillInputInfo;
import com.android.Samkoonhmi.model.AnimationViewerInfo;
import com.android.Samkoonhmi.model.CalibrationModel;
import com.android.Samkoonhmi.model.ComboBoxInfo;
import com.android.Samkoonhmi.model.DateTimeShowInfo;
import com.android.Samkoonhmi.model.DragdownboxItemInfo;
import com.android.Samkoonhmi.model.DynamicCircleInfo;
import com.android.Samkoonhmi.model.DynamicRectInfo;
import com.android.Samkoonhmi.model.FlowBlockModel;
import com.android.Samkoonhmi.model.GifViewerInfo;
import com.android.Samkoonhmi.model.GraphBaseInfo;
import com.android.Samkoonhmi.model.GroupShapeModel;
import com.android.Samkoonhmi.model.HistoryShowInfo;
import com.android.Samkoonhmi.model.ImageViewerInfo;
import com.android.Samkoonhmi.model.LineInfo;
import com.android.Samkoonhmi.model.MessageBoardInfo;
import com.android.Samkoonhmi.model.MessageInfo;
import com.android.Samkoonhmi.model.NumberDisplayInfo;
import com.android.Samkoonhmi.model.PriorLoadInfo;
import com.android.Samkoonhmi.model.RecipeSelectInfo;
import com.android.Samkoonhmi.model.RecipeShowInfo;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.SceneNumInfo;
import com.android.Samkoonhmi.model.ShapInfo;
import com.android.Samkoonhmi.model.SliderModel;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableModel;
import com.android.Samkoonhmi.model.XYCurveInfo;
import com.android.Samkoonhmi.model.alarm.AlarmContolInfo;
import com.android.Samkoonhmi.model.alarm.AlarmHisShowInfo;
import com.android.Samkoonhmi.model.alarm.AlarmSlipInfo;
import com.android.Samkoonhmi.model.sk_historytrends.HistoryTrendsInfo;
import com.android.Samkoonhmi.model.skbutton.ButtonInfo;
import com.android.Samkoonhmi.model.skbutton.FunSwitchInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.ProtocolInterfaces;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.print.AKPrint;
import com.android.Samkoonhmi.skenum.GOTO_TYPE;
import com.android.Samkoonhmi.skenum.HMIMODEL;
import com.android.Samkoonhmi.skenum.LINE_CLASS;
import com.android.Samkoonhmi.skenum.SHAP_CLASS;
import com.android.Samkoonhmi.skenum.WINDOW_TYPE;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skgraphics.SKGraphics;
import com.android.Samkoonhmi.skgraphics.noplc.GroupShape;
import com.android.Samkoonhmi.skgraphics.noplc.SKCalibration;
import com.android.Samkoonhmi.skgraphics.noplc.SKCurveArc;
import com.android.Samkoonhmi.skgraphics.noplc.SKEllipse;
import com.android.Samkoonhmi.skgraphics.noplc.SKFoldLine;
import com.android.Samkoonhmi.skgraphics.noplc.SKFreeLine;
import com.android.Samkoonhmi.skgraphics.noplc.SKLine;
import com.android.Samkoonhmi.skgraphics.noplc.SKPolygon;
import com.android.Samkoonhmi.skgraphics.noplc.SKRect;
import com.android.Samkoonhmi.skgraphics.noplc.SKRoundRect;
import com.android.Samkoonhmi.skgraphics.noplc.SKSector;
import com.android.Samkoonhmi.skgraphics.noplc.SKTable;
import com.android.Samkoonhmi.skgraphics.noplc.SKTimeShow;
import com.android.Samkoonhmi.skgraphics.plc.show.AKXYCurve;
import com.android.Samkoonhmi.skgraphics.plc.show.SKAnimation;
import com.android.Samkoonhmi.skgraphics.plc.show.SKDynamicAlarmSlip;
import com.android.Samkoonhmi.skgraphics.plc.show.SKDynamicCircle;
import com.android.Samkoonhmi.skgraphics.plc.show.SKDynamicRect;
import com.android.Samkoonhmi.skgraphics.plc.show.SKFlowBlock;
import com.android.Samkoonhmi.skgraphics.plc.show.SKGifViewer;
import com.android.Samkoonhmi.skgraphics.plc.show.SKGraph;
import com.android.Samkoonhmi.skgraphics.plc.show.SKMsgDisplay;
import com.android.Samkoonhmi.skgraphics.plc.show.SKStaticText;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.MessageBoard;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKASCIIDisplay;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKAlarmContol;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKButton;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKComboImgbox;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKCombobox;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKDragDownBox;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKFunSwitch;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKHistoryAlarmShow;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKHistoryShow;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKHistoryTrends;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKImageViewer;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKKeyPopupWindow;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKNumInputDisplay;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKRecipeSelect;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKRecipeShow;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKSlide2;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog.IOperCall;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog.IOperCall.CALLTYPE;
import com.android.Samkoonhmi.system.StorageStateManager;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.AlarmSaveThread;
import com.android.Samkoonhmi.util.BatteryBroadcast;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.ITEM_TABLE_TYPE;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.ParameterSet;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.ScreenBrightness;
import com.android.Samkoonhmi.util.SystemBroadcast;
import com.android.Samkoonhmi.util.SystemParam;
import com.android.Samkoonhmi.util.TASK;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * 场景管理
 * @author 刘伟江
 * @version v 1.0.0.1 创建时间 2012-6-8
 */
public class SKSceneManage {
	private static final String TAG = "SKSceneManage";
	private static final int TIME_OUT = 3;
	private static final int LOAD_SCENE_ITEM=5;
	private static final int ON_GO_SCENE = 6;
	private static final int LOAD_OUT_TIME = 7;
	private static final int SET_LIGHT=8;
	private static final int SHOW_LONGIN=9;//显示切换用户界面
	private static final int RESET=10;//
	private static final int LOCK_WINDOW=11;
	private static final int GOTO_WINDOW=12;
	private static final int BEEP=13;//蜂鸣器
	// 切换动画
	int animation[] = new int[12];
	// 每个画面控件集合
	private HashMap<Integer, List<ItemInfo>> mSceneItemMap;
	// 每个画面属性集合
	public ArrayList<ScenceInfo> mSceneInfoMap=null;
	// 画面上有哪些控件类型
	private HashMap<Integer, ArrayList<Integer>> mSceneTypeMap;
	public Context mContext;
	private Activity activity;
	private ContentResolver contentResolver;
	private ISKSceneUpdate mBaseIUpdate;
	// 底部场景id，
	private int nBaseSceneId;
	public int getnBaseSceneId() {
		return nBaseSceneId;
	}


	// 当前画面Id，可能是底部，也可能是窗口
	public int nSceneId;
	// 最大画面编号
	private int nSceneMaxNum;
	// 任务名称
	private String sTaskName = "";
	// 回调更新界面
	private ISKSceneUpdate iSceneUpdate;
	public static int nSceneWidth = 800;
	public static int nSceneHeight = 480;
	// 屏幕上的场景
	private HashMap<Integer, SKScene> mSceneViewMap;
	public int time;
	// 屏幕初始进入亮度
	public int initLight;
	private boolean lightFlag = false;
	// 计时，判断是否超出使用时间
	public int countTime;
	// 是否已经初始化
	private static boolean flag;
	private SHOW_TYPE eType;
	private boolean isIniting;// 是否处于初始化控件中，true-表示处于初始化控件中
	public  boolean isStarting;// 画面是否处于启动中，如果处于启动中，不能切换画面,true-表示处于启动中
	private boolean firstLoad;
	private int nFirstItemSize;
	private IGotoCallback iGotoCallback;
	// 所有画面的id集合,用于跳转判断,
	private ArrayList<Integer> mSceneIdList;// 画面id
	private ArrayList<Integer> mWindowIdList;// 窗口id
	private ArrayList<SceneNumInfo> mSceneNums;// 场景序号
	private ArrayList<Integer> mLoadScene;// 需要加载的画面信息
	private ArrayList<PriorLoadInfo> mPriorLoad;//需要优先加载的画面信息
	public boolean bLoginSuccess;//是否登录成功
	private GlobalPopWindow lockWindow;
	private boolean bHmiLock;//人机界面已经被锁定不能跳转画面
	private boolean bWindowFocus;//窗口焦点是否可见

	// 单例
	private static SKSceneManage sInstance = null;
	public synchronized static SKSceneManage getInstance() {
		if (sInstance == null) {
			sInstance = new SKSceneManage();
		}
		return sInstance;
	}

	private SKSceneManage() {
		this.nSceneMaxNum = 0;
		this.sTaskName = "";
		this.bHmiLock=false;
		flag = true;
		mSceneItemMap = new HashMap<Integer, List<ItemInfo>>();
		mSceneViewMap = new HashMap<Integer, SKScene>();
		mSceneIdList = new ArrayList<Integer>();
		mWindowIdList = new ArrayList<Integer>();
		mSceneNums = new ArrayList<SceneNumInfo>();
		mPriorLoad=new ArrayList<PriorLoadInfo>();
		countTime = 0;
		nFirstItemSize = 0;
		animation[0] = R.anim.dialog_left_enter;
		animation[1] = R.anim.dialog_out;
		animation[2] = R.anim.dialog_enter;
		animation[3] = R.anim.dialog_right_out;
		animation[4] = R.anim.dialog_left_enter;
		animation[5] = R.anim.dialog_left_out;
		animation[6] = R.anim.dialog_right_enter;
		animation[7] = R.anim.dialog_right_out;
		animation[8] = R.anim.rotate_enter;
		animation[9] = R.anim.rotate_out;
		animation[10] = R.anim.translate_enter;
		animation[11] = R.anim.translate_out;

	}

	/**
	 * 执行场景宏指令
	 */
	public void execSceneMacros() {

		ScenceInfo curSceneInfo =getScenceInfo(nSceneId);// 获得当前场景数据实体
		if (curSceneInfo==null) {
			return;
		}
		ArrayList<Short> tmpMIDList = curSceneInfo.getSceneMacroIDList();

		if (null == tmpMIDList || 0 == tmpMIDList.size()) {
			// 不做任何提示
			return;
		}

		for (short i = 0; i < tmpMIDList.size(); i++) {
			MacroManager.getInstance(null).Request(MSERV.CALLSM,
					tmpMIDList.get(i));
		}
	}

	/**
	 * 终止场景宏指令
	 * */
	public void exitSceneMacros(int sid) {
		MacroManager macro = MacroManager.getInstance(null);
		if (macro != null) {
			macro.Request(MSERV.ENDRSM,(short)sid);
		}
	}

	/**
	 * 终止场景宏指令
	 * */
	public void exitGlobalMacros() {
		MacroManager macro = MacroManager.getInstance(null);
		if (macro != null) {
			macro.Request(MSERV.ENDRGM);
		}
	}


	/**
	 * 加载画面
	 */
	public synchronized void loadView(Context context, Activity activity, SHOW_TYPE type) {
		
		if(context!=null){
			this.mContext = context;
		}
		this.eType = type;

		// 画面or窗口
		if (eType == SHOW_TYPE.DEFAULT) {
			// 画面
			this.activity = activity;
			nBaseSceneId = nSceneId;
			SystemInfo.setCurrentScenceId(nSceneId);

		}

		// 最大画面号
		if (nSceneMaxNum == 0) {
			nSceneMaxNum = DBTool.getInstance().getmSceneBiz().getSceneMaxNum();
		}

		if (containsKey(nSceneId)) {
			// 场景信息已经存在
			if (!mSceneViewMap.containsKey(nSceneId)) {
				SKScene scene = new SKScene(mContext, activity,
						getScenceInfo(nSceneId));
				mSceneViewMap.put(nSceneId, scene);
				//Log.d(TAG, "new view ........."+nSceneId);
			}
			
			if (!mSceneItemMap.containsKey(nSceneId)) {
				//画面控件不存在，加载画面信息，并回调刷新
				LoadInfo info=new LoadInfo();
				info.nSid=nSceneId;
				info.call=true;
				//Log.d(TAG, "no item for scene sid="+nSceneId);
				mHandler.removeMessages(LOAD_SCENE_ITEM);
				mHandler.obtainMessage(LOAD_SCENE_ITEM, info).sendToTarget();
				
			}
			initScene(getScenceInfo(nSceneId), nSceneId);
			
		} else {
			// 场景信息不存在,
			ScenceInfo info = DBTool.getInstance().getmSceneBiz().select(nSceneId);
			if (info != null) {
				if (mSceneInfoMap==null) {
					initInfo();
				}
				if (!containsKey(nSceneId)) {
					mSceneInfoMap.add(info);
				}

				if (!mSceneViewMap.containsKey(nSceneId)) {
					SKScene scene = new SKScene(mContext, activity, info);
					mSceneViewMap.put(nSceneId, scene);
				}
				
				if (!mSceneItemMap.containsKey(nSceneId)) {
					//画面控件不存在，加载画面信息，并回调刷新
					LoadInfo linfo=new LoadInfo();
					linfo.nSid=nSceneId;
					linfo.call=true;
					mHandler.removeMessages(LOAD_SCENE_ITEM);
					mHandler.obtainMessage(LOAD_SCENE_ITEM, linfo).sendToTarget();
				}
				
				if (eType == SHOW_TYPE.DEFAULT) {
					initScene(getScenceInfo(nSceneId), nSceneId);
				}
			}
		}

		if (eType == SHOW_TYPE.DEFAULT) {
			initValue();
			if ((SystemInfo.getnSetBoolParam() & SystemParam.HMI_PROTECT) == SystemParam.HMI_PROTECT) {
				mHandler.sendEmptyMessageDelayed(TIME_OUT, 200);
			} else {
				// 判断是否启用定时器
				if ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER) {
					// 注册定时器
					time = 0;
					if (!SKTimer.getInstance().getBinder()
							.isRegister(sCallback)) {
						SKTimer.getInstance().getBinder().onRegister(sCallback);
					}
				}
			}
		}
		
		isStarting = false;
	}
	
	/**
	 * 加载画面控件
	 */
	public void loadItem(LoadInfo info){
		
		if(info==null){
			return;
		}
		
		//Log.d(TAG, "load item sid = "+info.nSid);
		
		List<ItemInfo> items = new ArrayList<ItemInfo>();
		ArrayList<Integer> list=null;
		if (mSceneTypeMap!=null) {
			if (mSceneTypeMap.containsKey(info.nSid)) {
				
				//画面上拥有的控件类型
				list=mSceneTypeMap.get(info.nSid);
				if (list!=null) {
					for (int i = 0; i < list.size(); i++) {
						switch (list.get(i)) {
						case ITEM_TABLE_TYPE.SWITCH: // 开关
						{
							ArrayList<PriorLoadInfo> mList=new ArrayList<PriorLoadInfo>();
							ArrayList<ButtonInfo> bList=DBTool.getInstance().getmButtonBiz().selectBySid(info.nSid,mList);
							if (mList.size()>0) {
								//设置加载画面
								for (int j = 0; j < mSceneInfoMap.size(); j++) {
									ScenceInfo sInfo=mSceneInfoMap.get(j);
									if (sInfo.getnSceneId()==info.nSid) {
										sInfo.setmLoadScene(mList);
										break;
									}
								}
							}
							//Log.d(TAG, "data bt="+(System.currentTimeMillis()-bt));
							if (bList!=null) {
								for (int j = 0; j < bList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKButton button = new SKButton(mContext, bList.get(j).getnButtonId(), info.nSid,bList.get(j));
									item.nItemId= bList.get(j).getnButtonId();
									item.mItem=button;
							        items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.GRAPH: // 图表
						{
							ArrayList<GraphBaseInfo> gList=DBTool.getInstance().getmGraphBiz().select(info.nSid);
							if (gList!=null) {
								for (int j = 0; j < gList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKGraph graph = new SKGraph(gList.get(j).getnItemId(),
											info.nSid,gList.get(j));
									item.nItemId= gList.get(j).getnItemId();
									item.mItem=graph;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.VALUE: // 数值显示
						{
							ArrayList<NumberDisplayInfo> nList=DBTool.getInstance()
									.getmNumberInputBiz().selectNumberDisplayInfo(info.nSid);
							if (nList!=null) {
								for (int j = 0; j < nList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKNumInputDisplay numDisplay = new SKNumInputDisplay(mContext,
											nList.get(j).getId(), info.nSid,nList.get(j));
									item.nItemId= nList.get(j).getId();
									item.mItem=numDisplay;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.GROUPDATA: // 数据群组
						case ITEM_TABLE_TYPE.TREND: // 趋势图
							ArrayList<HistoryTrendsInfo> tList=DBTool.getInstance()
							.getmHistoryTrendsBiz().select(info.nSid);
							if (tList!=null) {
								for (int j = 0; j < tList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKHistoryTrends trends = new SKHistoryTrends(tList.get(j).getId(),
											info.nSid,tList.get(j));
									item.nItemId= tList.get(j).getId();
									item.mItem=trends;
									items.add(item);
								}
							}
							break;
						case ITEM_TABLE_TYPE.IMAGE: // 图片显示
						{
							ArrayList<ImageViewerInfo> aList = DBTool.getInstance().getmImageViewerBiz().select(info.nSid);
							if (aList != null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item = new ItemInfo();
									SKImageViewer imgv = new SKImageViewer(aList.get(j).getnItemId(),
											info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=imgv;
									items.add(item);
								}
							}
							break;
							
						}
						case ITEM_TABLE_TYPE.ALARM_ITEM: // 报警显示器
						{
							ArrayList<AlarmContolInfo> aList=DBTool.getInstance().getmAlarmBiz().selectControl(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKAlarmContol contol = new SKAlarmContol(mContext, 
											aList.get(j).getnItemId(), info.nSid,
											aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=contol;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.ALARM_SHLIP:// 报警条
						{
							ArrayList<AlarmSlipInfo> aList=DBTool.getInstance().getmAlarmBiz().selectAlarmSlip(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKDynamicAlarmSlip alarmSlip = new SKDynamicAlarmSlip(
											aList.get(j).getnItemId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=alarmSlip;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.HISTORY_ALARM://历史报警显示器
						{
							ArrayList<AlarmHisShowInfo> aList=DBTool.getInstance().getmAlarmBiz().selectHistoryAlarm(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKHistoryAlarmShow alarmShow = new SKHistoryAlarmShow(
											aList.get(j).getnItemId(), info.nSid, mContext,
											aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=alarmShow;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.RECIPE_SHOW:// 配方显示器
						{
							ArrayList<RecipeShowInfo> aList=DBTool.getInstance().getmRecipeShowBiz().select(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKRecipeShow rShow = new SKRecipeShow(mContext, info.nSid,
											aList.get(j).getnItemId(),aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=rShow;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.HISTORY_SHOW:// 历史数据显示器
						{
							ArrayList<HistoryShowInfo> aList=DBTool.getInstance().getmHistoryShowBiz().select(info.nSid);
							if(aList!=null){
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKHistoryShow hisShow = new SKHistoryShow(info.nSid, 
											aList.get(j).getnItemId(), mContext,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=hisShow;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.FLOW:// 流动块
						{
							ArrayList<FlowBlockModel> aList=DBTool.getInstance().getmFlowBlockBiz().getFlowBlock(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKFlowBlock flowBlock = new SKFlowBlock(info.nSid, 
											aList.get(j).getId(),aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=flowBlock;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.STATIE_TEXT: // 静态文本
						{
							//long time=System.currentTimeMillis();
							ArrayList<StaticTextModel> aList=DBTool.getInstance().getmStaticTextBiz().getStaticText(info.nSid);
							//long end=System.currentTimeMillis()-time;
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKStaticText text = new SKStaticText(info.nSid, aList.get(j)
											.getId(),aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=text;
									items.add(item);
								}
							}
							//Log.d("DataBase", "STATIE_TEXT time="+end+",all="+(System.currentTimeMillis()-time));
							break;
						}
						case ITEM_TABLE_TYPE.ASCII: // ascii显示
						{
							ArrayList<AcillInputInfo> aList=DBTool.getInstance().getmAcillInputBiz().selectAcillInputInfo(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKASCIIDisplay ascii = new SKASCIIDisplay(mContext,
											aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=ascii;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.TIME: // 时间显示
						{
							ArrayList<DateTimeShowInfo> aList=DBTool.getInstance()
									.getmTimeShowBiz().selectTimeShowInfo(info.nSid);
							if(aList!=null){
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKTimeShow timeShow = new SKTimeShow(aList.get(j).getId(), info.nSid,
											this.mContext.getApplicationContext(),aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=timeShow;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.GROUP_SHAPE:// 组合
						{
							ArrayList<GroupShapeModel> aList=DBTool.getInstance().getGroupShape().getInfo(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									GroupShape gShape = new GroupShape(
											aList.get(j).getnItemId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=gShape;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.CALIBRATION:// 刻度
						{
							ArrayList<CalibrationModel> aList=DBTool.getInstance().getmCalibrationBiz().getCalibration(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKCalibration calibration = new SKCalibration(info.nSid, 
											aList.get(j).getId(),aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=calibration;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.SLIDE:// 滑动块
						{
							ArrayList<SliderModel> aList=DBTool.getInstance().getmSlideBiz().selectSlide(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKSlide2 slide = new SKSlide2(info.nSid, aList.get(j)
											.getId(),aList.get(j));
									item.nItemId=aList.get(j).getId();
									item.mItem=slide;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.RECT: // 矩形
						{
							ArrayList<ShapInfo> aList=DBTool.getInstance().getmShapInfoBiz().getShapInfo(SHAP_CLASS.RECT, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKRect rect = new SKRect(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=rect;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.ELIPSE: // 椭圆
						{
							ArrayList<ShapInfo> aList=DBTool.getInstance().getmShapInfoBiz().getShapInfo(SHAP_CLASS.ELLIPSE, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKEllipse ellipse  = new SKEllipse(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=ellipse;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.LINE: // 直线
						{
							ArrayList<LineInfo> aList=DBTool.getInstance().getmLineInfoBiz().select(LINE_CLASS.BEELINE, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKLine line  = new SKLine(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=line;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.POLYGON: // 多边形
						{
							ArrayList<ShapInfo> aList=DBTool.getInstance().getmShapInfoBiz().getShapInfo(SHAP_CLASS.POLYGON, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKPolygon poly  = new SKPolygon(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=poly;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.FOLDLING: // 折线
						{
							ArrayList<LineInfo> aList=DBTool.getInstance().getmLineInfoBiz().select(LINE_CLASS.FOLDLINE, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKFoldLine foldline  = new SKFoldLine(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=foldline;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.FREELING: // 自由直线
						{
							ArrayList<LineInfo> aList=DBTool.getInstance().getmLineInfoBiz().select(LINE_CLASS.FREELINE, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKFreeLine freeLine  = new SKFreeLine(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=freeLine;
									items.add(item);
								}
							}
							break;
							
						}
						case ITEM_TABLE_TYPE.LINECIRCLE:// 圆弧
						{
							ArrayList<LineInfo> aList=DBTool.getInstance().getmLineInfoBiz().select(LINE_CLASS.CURVEARCLINE, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKCurveArc curveArc  = new SKCurveArc(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=curveArc;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.ROUNDEDRECT:// 圆角矩形
						{
							ArrayList<ShapInfo> aList=DBTool.getInstance().getmShapInfoBiz().getShapInfo(SHAP_CLASS.CIRCLE_RECT, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKRoundRect roundRect  = new SKRoundRect(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=roundRect;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.ARC: // 扇形
						{
							ArrayList<ShapInfo> aList=DBTool.getInstance().getmShapInfoBiz().getShapInfo(SHAP_CLASS.SECTOR, info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKSector sector  = new SKSector(aList.get(j).getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=sector;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.ANIMATION://动画显示器
						{
							ArrayList<AnimationViewerInfo> aList=DBTool.getInstance().getmAnimationViewerBiz().select(info.nSid);
							if(aList!=null){
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKAnimation an = new SKAnimation(aList.get(j).getnItemId(),
											info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=an;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.DYNCIRCLE:// 动态圆
						{
							ArrayList<DynamicCircleInfo> aList=DBTool.getInstance().getmDynamicCircleBiz().select(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKDynamicCircle dc = new SKDynamicCircle(aList.get(j)
											.getnItemId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=dc;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.FUN_SWITCH:// 多功能按钮
						{
							ArrayList<FunSwitchInfo> aList=DBTool.getInstance().getmButtonBiz().getFunSwitch(info.nSid, mContext);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKFunSwitch funSwitch = new SKFunSwitch(aList.get(j)
											.getnItemId(), info.nSid, mContext,aList.get(j));
									item.nItemId=aList.get(j).getnItemId();
									item.mItem=funSwitch;
									items.add(item);
								}
							}
							break;

						}
						case ITEM_TABLE_TYPE.GIF://动画GIF
						{
							ArrayList<GifViewerInfo> aList=DBTool.getInstance().getmGifViewerBiz().select(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKGifViewer tgif = new SKGifViewer(
											aList.get(j).getnItemId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=tgif;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.DYNRECT: // 动态矩形
						{
							ArrayList<DynamicRectInfo> aList=DBTool.getInstance().getmDynamicRectBiz().select(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKDynamicRect dr = new SKDynamicRect(aList.get(j).getnItemId(),
											info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=dr;
									items.add(item);
									
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.RECIPE_SELECT:// 配方选择器
						{
							ArrayList<RecipeSelectInfo> aList=DBTool.getInstance().getmRecipeSelectBiz().selectRecipeSelectInfo(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKRecipeSelect select = new SKRecipeSelect(aList.get(j)
											.getId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=select;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.DROP_DOWN:// 下拉框
						{
							ArrayList<ComboBoxInfo> aList=DBTool.getInstance().getmComboxInfoBiz().select(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									if(aList.get(j).getbIsUsePic()){
										SKComboImgbox combobox = new SKComboImgbox(aList.get(j).getId(), info.nSid,
												aList.get(j));
											item.nItemId= aList.get(j).getId();
											item.mItem=combobox;
											items.add(item);
									}else{
										SKCombobox combobox = new SKCombobox(aList.get(j).getId(), info.nSid,
											aList.get(j));
										item.nItemId= aList.get(j).getId();
										item.mItem=combobox;
										items.add(item);
									}
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.MSG_DISPLAY:// 消息显示器
						{
							ArrayList<MessageInfo> aList=DBTool.getInstance().getmMessageDisplayBiz().select(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKMsgDisplay msgDisplay = new SKMsgDisplay(aList.get(j)
											.getnItemId(), info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=msgDisplay;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.TABLE:// 表格
						{
							ArrayList<TableModel> aList=DBTool.getInstance().getmTableBiz().selectTableById(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKTable skTable = new SKTable(aList.get(j).getId(),
											info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getId();
									item.mItem=skTable;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.MESSAGE_BOARD:// 留言板
						{
							ArrayList<MessageBoardInfo> aList=DBTool.getInstance().getMessageBoard().selectMessageBoard(info.nSid);
							if (aList!=null) {
								for (int j = 0; j < aList.size(); j++) {
									ItemInfo item=new ItemInfo();
									MessageBoard mBoard = new MessageBoard(mContext, aList.get(j).getnItemId(),
											info.nSid,aList.get(j));
									item.nItemId= aList.get(j).getnItemId();
									item.mItem=mBoard;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.DRAGDOWN_BOX://下拉框
						{
							ArrayList<DragdownboxItemInfo> dList=DBTool.getInstance().getmDragdownBoxBiz().selectDragdownInfo(info.nSid);
							if (dList!=null) {
								for (int j = 0; j < dList.size(); j++) {
									ItemInfo item=new ItemInfo();
									SKDragDownBox mDragdownBox = new SKDragDownBox(dList.get(j).getId(),
											info.nSid,dList.get(j));
									item.nItemId= dList.get(j).getId();
									item.mItem=mDragdownBox;
									items.add(item);
								}
							}
							break;
						}
						case ITEM_TABLE_TYPE.XY_CURVE:{
							//XY曲线
							ArrayList<XYCurveInfo> dList=DBTool.getInstance().getmXYCurveBiz().select(info.nSid);
							if (dList!=null) {
								for (int j = 0; j < dList.size(); j++) {
									ItemInfo item=new ItemInfo();
									AKXYCurve curve=new AKXYCurve(dList.get(j).getnItemId(), info.nSid, dList.get(j));
									item.nItemId= dList.get(j).getnItemId();
									item.mItem=curve;
									items.add(item);
								}
							}
							break;
						}
							
						}
					
					}
				}
			}
		}
		
		//long time=System.currentTimeMillis()-start;
		//Log.d(TAG, "load all time="+time+",bt="+bt+",gt="+gt+", sid="+info.nSid);
		//Log.d(TAG, "load all time="+allTime+",time="+time+",sid="+info.nSid+",tB="+tB+",tS="+tS+",tT="+tT);
	
		/**
		 * 程序第一次启动时
		 * 加载启动画面
		 */
		
		boolean result=true;
		if (firstLoad) {
			ScenceInfo sInfo=getCurrentInfo();
			if (sInfo!=null) {
				priorLoadScene(sInfo);
			}
			if (mPriorLoad.size()>0) {
				if(mPriorLoad.get(0).nSid==SystemInfo.getInitSceneId()){
					result=false;
				}
			}
			if (result) {
				mHandler.sendEmptyMessage(ON_GO_SCENE);
			}
		}

		// 保存画面控件
		if (!mSceneItemMap.containsKey(info.nSid)) {
			mSceneItemMap.put(info.nSid, items);
		}
		
		/**
		 * 切换到下一个页面时，这时候画面控件信息未加载，则先显示画面信息，然后通过
		 * 后台线程加载该画面控件，并且回调初始化控件。
		 */
		if (info.call) {
			if (info.nSid==nSceneId) {
				if (mSceneViewMap.containsKey(nSceneId)) {
					SKScene scene=mSceneViewMap.get(nSceneId);
					if (mSceneItemMap.containsKey(nSceneId)) {
						scene.setmSkGraphicsList(mSceneItemMap.get(nSceneId));
					}
					scene.drawGraphics();
					
					//加载当前画面上，需要跳转的画面信息
					ScenceInfo sInfo=getCurrentInfo();
					if (sInfo!=null) {
						priorLoadScene(sInfo);
					}
				}
			}
		}
		
		if (mPriorLoad.size() > 0||mLoadScene.size() > 0) {
			if (result) {
				mHandler.sendEmptyMessageDelayed(LOAD_SCENE_ITEM, 500);
			}else {
				mHandler.sendEmptyMessage(LOAD_SCENE_ITEM);
			}
			
		}
	}
	
	//加载当前画面，需要跳转的画面信息
	private void priorLoadScene(ScenceInfo info){
		ArrayList<PriorLoadInfo> mList=info.getmLoadScene();
		if (mList!=null&&mList.size()>0) {
			//清空之前
			mPriorLoad.clear();
			for (int i = 0; i < mList.size(); i++) {
				mPriorLoad.add(mList.get(i));
			}
			mList.clear();
		}
	}
	
	
	/**
	 * 加载画面和控件信息
	 */
	private void initInfo(){
		
		long time=System.currentTimeMillis();
		mSceneInfoMap=DBTool.getInstance().getmSceneBiz().getAllSceneInfo();
		mSceneTypeMap=DBTool.getInstance().getmSceneBiz().getSceneType();
		mSceneIdList = DBTool.getInstance().getmSceneBiz().getAllSceneId();// 获取所有画面id
		mWindowIdList = DBTool.getInstance().getmSceneBiz().getWindowId();// 获取窗口的id集合
		mSceneNums = DBTool.getInstance().getmSceneBiz().getSceneNum();// 获取画面序号
		nSceneMaxNum = DBTool.getInstance().getmSceneBiz().getSceneMaxNum();
		SKWindowManage.getInstance(mContext).loadWindow(mSceneInfoMap);
		SKKeyPopupWindow.getKeyBroadId();//获取该工程的所有键盘id;
		
		mLoadScene = new ArrayList<Integer>();
		
		if (mWindowIdList != null) {
			for (int i = 0; i < mWindowIdList.size(); i++) {
				mLoadScene.add(mWindowIdList.get(i));
			}
		}
		
		if (mSceneIdList != null) {
			for (int i = 0; i < mSceneIdList.size(); i++) {
				mLoadScene.add(mSceneIdList.get(i));
			}
		}
		
		Log.d("DataBase", "init Info time="+(System.currentTimeMillis()-time));

	}

	/**
	 * 初始化画面
	 */
	private void initScene(ScenceInfo info, int sceneId) {
		if (mSceneViewMap.containsKey(sceneId)) {
			SKScene scene = mSceneViewMap.get(sceneId);
			scene.setiSceneDestory(iDestory);
			if (mSceneItemMap.containsKey(sceneId)) {
				scene.setmSkGraphicsList(mSceneItemMap.get(sceneId));
			}
			
			if (iSceneUpdate != null) {
				iSceneUpdate.onUpdateView(scene);
			}

			if (eType == SHOW_TYPE.DEFAULT) {
				nSceneWidth = info.getnSceneWidth();
				nSceneHeight = info.getnSceneHeight();
				X_MOVE_DISTANCES = nSceneWidth / 8;
				Y_MOVE_DISTANCES = nSceneHeight / 6;
			}

			/* 重新添加场景的地址 */
			setOneSceneReadAddrs(sceneId);
			
			//添加刷新画面
			SKPlcNoticThread.getInstance().addRefreshId(sceneId);

			execSceneMacros();
			
			//加载当前画面上，需要跳转的画面信息
			priorLoadScene(info);
		}
	}


	/**
	 * 控件刷新
	 */
	private SKScene mRefreshScene=null;
	private int nRefreshId;
	public synchronized void onRefresh(SKItems item) {
		if (item == null) {
			return;
		}

		if (!firstLoad) {
			if (mRefreshScene==null) {
				mRefreshScene=mSceneViewMap.get(item.sceneId);
				nRefreshId=item.sceneId;
			}else {
				if (item.sceneId!=nRefreshId) {
					mRefreshScene=mSceneViewMap.get(item.sceneId);
					nRefreshId=item.sceneId;
				}
			}
			if (mRefreshScene != null) {
				mRefreshScene.onRefresh(item);
			}
		}
	}
	
	//对外点击事件
	private IListener mIListener=null;
	public void setIListener(IListener mIListener){
		if (mIListener==null) {
			return ;
		}
		this.mIListener=mIListener;
	}
	
	/**
	 * 对外接口
	 * 点击事件
	 */
	private Vibrator vibrator;
	public boolean onTouch(MotionEvent event){
		
		if (mIListener==null) {
			return false;
		}
		if (vibrator==null) {
			vibrator = (Vibrator) ContextUtl.getInstance()
					.getSystemService(Service.VIBRATOR_SERVICE);
		}
		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			vibrator.vibrate(150/* new long[]{1000,50,1000,50}, 0 */);
		}
	
		mIListener.onTouchEvent(event);
		return true;
	}

	/**
	 * 对外接口
	 * 刷新界面
	 */
	public void refresh(){
		if (mSceneViewMap.containsKey(nSceneId)) {
			//刷新界面
			mSceneViewMap.get(nSceneId).refresh();
		}
	}
	
	/**
	 * 对外接口
	 * 回调绘制
	 */
	public void setIAKDraw(IAKDraw idraw){
		if (mSceneViewMap.containsKey(nSceneId)) {
			//回调绘制
			mSceneViewMap.get(nSceneId).setIAKDraw(idraw);
		}
	}
	
	/**
	 * 对外接口
	 * 设置背景图片
	 */
	public void setBackground(Bitmap bitmap){
		if (mSceneViewMap.containsKey(nSceneId)) {
			mSceneViewMap.get(nSceneId).setBackground(bitmap);
		}
	}
	
	/**
	 * 获取当前背景
	 */
	public Bitmap getSceneView(){
		if (mSceneViewMap.containsKey(nSceneId)) {
			return mSceneViewMap.get(nSceneId).getSceneView();
		}
		return null;
	}
	
	/**
	 * 对外接口
	 * 设置背景颜色
	 */
	public void setBackground(int color){
		if (mSceneViewMap.containsKey(nSceneId)) {
			mSceneViewMap.get(nSceneId).setBackground(color);
		}
	}
	
	/**
	 * 滑动切换画面
	 */
	private boolean isMove;
	private float nDownX = 0;
	private float nDownY = 0;
	private static int X_MOVE_DISTANCES = nSceneWidth / 8;
	private static int Y_MOVE_DISTANCES = nSceneHeight / 6;
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		boolean result = false;
		float X = event.getX();
		float Y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			time = 0;
			click();
			nDownX = X;
			nDownY = Y;
			result = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(X - nDownX) > X_MOVE_DISTANCES
					|| Math.abs(Y - nDownY) > Y_MOVE_DISTANCES) {
				isMove = true;
			} else {
				isMove = false;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (isIniting) {
				return false;
			} else {
				// 如果可以滑动,打开窗口时是不能滑动切换画面
				if (isDestory && !SKWindowManage.getInstance(mContext).show) {
					if (!isMove) {
						return false;
					}

					if (X - nDownX > X_MOVE_DISTANCES) {
						// 从左到右,切换到前一个画面
						if (!isStarting) {
							nLoadSceneNum = getSceneBySid(nSceneId);
							preScene();
						}
					} else if (X - nDownX < -X_MOVE_DISTANCES) {
						// 从右到左,切换到下一个画面
						if (!isStarting) {
							nLoadSceneNum = getSceneBySid(nSceneId);
							nextScene();
						}
					} else if (Y - nDownY > Y_MOVE_DISTANCES) {
						// 进入画面管理
						onStartSceneMenu();
					}
					nDownX = X;
					nDownY = Y;
				}
			}
			result = true;
			break;
		}
		return result;
	}

	/**
	 * 更新起始位置
	 */
	public void updateXY(float x, float y) {
		nDownX = x;
		nDownY = y;
	}

	/**
	 * 返回上一个画面
	 */
	private int nLoadSceneNum = 0;
	private void preScene() {
		if (iSceneUpdate != null) {
			ScenceInfo info =getScenceInfo(nSceneId);
			if (info==null) {
				return;
			}
			if (info.getnTowardRIghtId() < 0) {
				// 没启动滑动
				return;
			}
			if (info.getnTowardRIghtId() > 0) {
				if (info.getnTowardRIghtId() != nSceneId) {
					if (hasScene(info.getnTowardRIghtId())) {
						removeSKcene(nSceneId, 0);// 删除旧的数据
						info.setnBeforeSId(nSceneId);//记录进入当前场景的场景id
						gotoWindow(3, info.getnTowardRIghtId(), true, info.getnSlideStyle(),GOTO_TYPE.SIDE);
					}
				}
			} else {
				if (nLoadSceneNum > -1) {
					nLoadSceneNum--;
					int sid = getGotoSceneId(nSceneId,1);
					if (hasScene(sid)) {
						removeSKcene(nSceneId, 0);// 删除旧的数据
						info.setnBeforeSId(nSceneId);//记录进入当前场景的场景id
						gotoWindow(3, sid, true, info.getnSlideStyle(),GOTO_TYPE.SIDE);
					}else {
						SKToast.makeText(mContext, R.string.secen_hint_start,
								Toast.LENGTH_SHORT).show();
					}

				} else {
					SKToast.makeText(mContext, R.string.secen_hint_start,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 进入下一个画面
	 */
	private void nextScene() {
		if (iSceneUpdate != null) {
			ScenceInfo info = getScenceInfo(nSceneId);
			if (info==null) {
				return;
			}
			if (info.getnTowardLeftId() < 0) {
				// 没启动滑动
				return;
			}
			
			if (info.getnTowardLeftId() > 0) {
				if (info.getnTowardLeftId() != nSceneId) {
					if (hasScene(info.getnTowardLeftId())) {
						removeSKcene(nSceneId, 0);// 删除旧的数据
						setBeforeSceneId(nSceneId, info.getnTowardLeftId());
						gotoWindow(3, info.getnTowardLeftId(), false, info.getnSlideStyle(),GOTO_TYPE.SIDE);
					}
				}
			} else {
				if (nLoadSceneNum < nSceneMaxNum) {
					nLoadSceneNum++;
					int sid = getGotoSceneId(nSceneId,2);
					if (hasScene(sid)) {
						removeSKcene(nSceneId, 0);
						setBeforeSceneId(nSceneId, sid);
						gotoWindow(3, sid, false, info.getnSlideStyle(),GOTO_TYPE.SIDE);
					}else{
						SKToast.makeText(mContext, R.string.secen_hint_end,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					SKToast.makeText(mContext, R.string.secen_hint_end,
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	}
	
	/**
	 * 设置进入当前场景的场景id
	 * @param cid-当前场景id
	 * @param tid-目标场景id
	 */
	private void setBeforeSceneId(int cid,int tid){
		if (mSceneInfoMap!=null) {
			ScenceInfo info=null;
			for (int i = 0; i < mSceneInfoMap.size(); i++) {
				if (mSceneInfoMap.get(i).getnSceneId()==tid) {
					info=mSceneInfoMap.get(i);
					break;
				}
			}
			if (info!=null) {
				info.setnBeforeSId(cid);
			}
		}
	}
	
	/**
	 * 获取跳转画面id
	 * @param sid当前画面id
	 * @param type-1 进入前一个画面
	 *        type-2 进入下一个画面
	 * @return 返回值，画面id
	 */
	private int getGotoSceneId(int sid,int type){
		int id=0;
		boolean result=false;
		if (mSceneInfoMap!=null) {
			/*画面信息集合已经按照画面序号排好序*/
			for (int i = 0; i <mSceneInfoMap.size(); i++) {
				ScenceInfo info = mSceneInfoMap.get(i);
				if (info.getnSceneId()==sid) {
					if (type==1) {
						return id;
					}else {
						result = true;
					}
				}else {
					id=info.getnSceneId();
					nLoadSceneNum=info.getnNum();
					if (result) {
						return id;
					}
				}
			}
		}
		return 0;
	}
	

	/**
	 * 启动画面跳转
	 */
	private  void startScene(int type, boolean pre) {
		SKProgress.hide();// 隐藏等待框
		if (type == 1) {
			// 正常显示
			iSceneUpdate.onChange(animation[2], animation[3], 0);
		} else if (type == 2) {
			// 层叠
			if (pre) {
				iSceneUpdate.onChange(animation[2], animation[3], 1);
			} else {
				iSceneUpdate.onChange(animation[0], animation[1], 1);
			}
		} else if (type == 3) {
			// 淡入淡出
			if (pre) {
				iSceneUpdate.onChange(animation[6], animation[5], 1);
			} else {
				iSceneUpdate.onChange(animation[4], animation[7], 1);
			}
		} else if (type == 4) {
			// 平移
			iSceneUpdate.onChange(animation[10], animation[11], 1);
		} else if (type == 5) {
			// 平面旋转
			if (pre) {
				iSceneUpdate.onChange(animation[9], animation[3], 1);
			} else {
				iSceneUpdate.onChange(animation[8], animation[0], 1);
			}
		}else if(type == 6){
			//开关跳转
			iSceneUpdate.onChange(animation[2], animation[3], 1);
		}else {
			iSceneUpdate.onChange(animation[2], animation[3], 1);
		}
	}
	

	/**
	 * 是否存在这个场景
	 */
	public boolean hasScene(int sceneId) {
		boolean has = false;
		if (mSceneIdList != null) {
			for (int i = 0; i < mSceneIdList.size(); i++) {
				if (mSceneIdList.get(i) == sceneId) {
					return true;
				}
			}
		}
		return has;
	}

	/**
	 * 是否存在这个窗口
	 */
	public boolean hasWindow(int sceneId) {
		boolean has = false;
		if (mWindowIdList != null) {
			for (int i = 0; i < mWindowIdList.size(); i++) {
				if (mWindowIdList.get(i) == sceneId) {
					return true;
				}
			}
		}
		return has;
	}
	
	public ArrayList<Integer> getWindowList(){
		return mWindowIdList;
	}

	/**
	 * 根据画面序号获取画面id
	 *
	 */
	public int getSceneByNum(int num) {
		if (mSceneNums == null) {
			mSceneNums = DBTool.getInstance().getmSceneBiz().getSceneNum();// 获取画面序号
		}
		if (mSceneNums != null) {
			for (int i = 0; i < mSceneNums.size(); i++) {
				if (mSceneNums.get(i).getNum() == num) {
					return mSceneNums.get(i).getSid();
				}
			}
		}
		return 0;
	}
	
	/**
	 * 根据表id获取场景id
	 */
	public int getSidById(SHOW_TYPE type,int id){
		
		if (mSceneNums != null) {
			for (int i = 0; i < mSceneNums.size(); i++) {
			    SceneNumInfo info=mSceneNums.get(i);
				if (info.getId() == id&&info.geteType()==type) {
					return info.getSid();
				}
			}
		}
		return -1;
	}
	
	public SceneNumInfo getSceneInfoBySid(int sid) {
		if (mSceneNums == null) {
			mSceneNums = DBTool.getInstance().getmSceneBiz().getSceneNum();// 获取画面序号
		}
		if (mSceneNums != null) {
			for (int i = 0; i < mSceneNums.size(); i++) {
				if (mSceneNums.get(i).getSid() == sid) {
					return mSceneNums.get(i);
				}
			}
		}
		return null;
	}

	/**
	 *  根据画面id获取画面序号
	 */
	public int getSceneBySid(int sid) {
		if (mSceneNums == null) {
			mSceneNums = DBTool.getInstance().getmSceneBiz().getSceneNum();// 获取画面序号
		}
		if (mSceneNums != null) {
			for (int i = 0; i < mSceneNums.size(); i++) {
				if (mSceneNums.get(i).getSid() == sid) {
					return mSceneNums.get(i).getNum();
				}
			}
		}
		return 0;
	}
	
	/**
	 * 根据画面名称获取画面id
	 */
	public int getSceneIdByName(String name) {
		if(name==null||name.equals("")){
			return 0;
		}
		if (mSceneNums == null) {
			mSceneNums = DBTool.getInstance().getmSceneBiz().getSceneNum();// 获取画面序号
		}
		if (mSceneNums != null) {
			for (int i = 0; i < mSceneNums.size(); i++) {
				if (mSceneNums.get(i).getName().equals(name)) {
					return mSceneNums.get(i).getSid();
				}
			}
		}
		return 0;
	}

	/**
	 * 画面和窗口跳转
	 * @param type-跳转类型,0-画面,1-窗口,2-刷新界面,3-启动画面,4-关闭窗口
	 * @param id-跳转目标画面或窗口的id
	 * @param record-是否记录跳转之前的画面id，只用于画面,or 表示-切换类型，next、pre
	 * @param enterType-切换效果，只用于，画面跳转
	 * @param eType-触发源类型
	 */
	public synchronized void gotoWindow(int type,int id,boolean record,int enterType,GOTO_TYPE eType){
		
		if(bHmiLock){
			//机器已经被锁定，不可跳转画面
			return;
		}
		
		if (isStarting) {
			return;
		}
		
		GotoInfo info=new GotoInfo();
		info.type=type;
		info.id=id;
		info.record=record;
		info.enterType=enterType;
		info.eType=eType;
		
		mHandler.removeMessages(GOTO_WINDOW);
		mHandler.obtainMessage(GOTO_WINDOW, info).sendToTarget();
	}
	
	/**
	 * 窗口跳转
	 */
	private void window(int type,int id,boolean record,int enterType,GOTO_TYPE eType){
		
		if(type==4){
			//关闭窗口
			if (id==SKWindowManage.getInstance(mContext).nWindowId) {
				SKWindowManage.getInstance(mContext).closeWindow(eType==GOTO_TYPE.ALARM?0:1);
			}
			return;
		}else if (type==0) {
			if (id == nSceneId) {
				return;
			}
		}else if(type==1) {
			if (id==SKWindowManage.getInstance(mContext).nWindowId) {
				return;
			}
		}
		
		//跳转之前关闭窗口
		SKWindowManage.getInstance(mContext).closeWindow(eType==GOTO_TYPE.ALARM?0:1);
		//跳转之前屏保画面
		if (outTimePop!=null) {
			outTimePop.closePopWindow();
		}
		
		//关闭打印提示窗口
		AKPrint.getInstance().closeAKPrintWindow();
		
		//关闭登录界面
		closeLoginPop();
		
		isStarting = true;
		
		if (type==0) {
			
			//画面跳转
			if (record) {
				setBeforeSceneId(nSceneId, id);
			}
			if (hasScene(id)) {
				removeSKcene(nSceneId, 0);
				if (iSceneUpdate != null) {
					nSceneId = id;
					nBaseSceneId = nSceneId;// 画面ID 不是窗口
					startScene(enterType, true);
				}
			}
			
		}else if(type==1){
			//窗口
			SKWindowManage.getInstance(mContext).showWindows(id);
		}else if (type==2) {
			//界面刷新
			if (hasScene(nBaseSceneId)) {
				SKProgress.hide();// 隐藏等待框
				if (mSceneViewMap.containsKey(nBaseSceneId)) {
					mSceneViewMap.get(nBaseSceneId).clearData();// 清空数据
					//mSceneViewMap.remove(nBaseSceneId);
				}

				SKProgress.hide();// 画面切换隐藏等待圈

				unBinder(nBaseSceneId);
				if (iSceneUpdate != null) {
					iSceneUpdate.onChange(animation[0], animation[1], 1);
				}
			}
		}else if (type==3) {
			//启动画面
			nSceneId=id;
			startScene(enterType,record);
		}
		
	}
	
	/**
	 * 主要是当ak启动第三方软件，重新进入ak界面时，刷新
	 * 刷新当前界面
	 */
	public synchronized void refreshScreen() {
		gotoWindow(2,0,false,0,GOTO_TYPE.SYSTEM);
	}
	
	/**
	 * 预先加载启动画面控件
	 */
	public void loadSceneItem(int sceneId, Context context) {
		if (!mSceneItemMap.containsKey(sceneId)) {
			// 读取画面控件
			mReadAddr.clear();
			this.mContext = context;
			nSceneId = sceneId;
			firstLoad = true;// 第一次加载
			LoadInfo info=new LoadInfo();
			info.nSid=nSceneId;
			info.call=false;
			//超时设置
			mHandler.sendEmptyMessageDelayed(LOAD_OUT_TIME, 30000);
			sTaskName=SKThread.getInstance().getBinder().onRegisterForScene(call);
			SKThread.getInstance().getBinder().onTask(MODULE.SCENE, TASK.SCENE_AND_ITEM, sTaskName, info);
			
		}else {
			Log.d(TAG, "loadSceneItem ,but info exist ...");
			//已经存在，直接跳转
			if (iGotoCallback != null) {
				iGotoCallback.onGoto();// 启动画面没有控件，从登陆界面跳转到显示场景
			}
		}
	}

	/**
	 * 获取启动画面item的个数
	 */
	public int getFirstSceneItemSize() {
		return nFirstItemSize;
	}

	/**
	 * 启动画面管理
	 */
	private void onStartSceneMenu() {
		if (!SKMenuManage.getInstance().isbTouch()) {
			return;
		}
		if (!isStarting) {
			isStarting = true;
			SKProgress.hide();// 隐藏等待框
			
			removeSKcene(nSceneId, 0);
			activity.finish();
			Intent intent = new Intent();
			intent.setClass(mContext, SKSceneMenu.class);
			try {
				mContext.startActivity(intent);
			} catch (Exception e) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		}
	}

	/**
	 * 添加场景信息
	 */
	public void addSceneInfo(ScenceInfo info) {
		if (!containsKey(info.getnSceneId())) {
			mSceneInfoMap.add(info);
		}
	}

	Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TIME_OUT:
				timeOut();
				break;
			case ON_GO_SCENE:
				// 画面跳转
				firstLoad = false;
				if (iGotoCallback != null) {
					iGotoCallback.onGoto();// 启动画面没有控件，从登陆界面跳转到显示场景
				}
				break;
			case LOAD_OUT_TIME:
				// 超时跳转
				if (firstLoad) {
					// 超时了
					Log.d(TAG, "LOAD_OUT_TIME");
					mHandler.removeMessages(ON_GO_SCENE);
					mHandler.sendEmptyMessage(ON_GO_SCENE);
				}
				break;
			case LOAD_SCENE_ITEM:
				if (msg.obj==null) {
					loadSceneAndItem();
				}else{
					LoadInfo info=(LoadInfo)msg.obj;
					if (info!=null) {
						if (sTaskName.equals("")) {
							sTaskName=SKThread.getInstance().getBinder().onRegisterForScene(call);
						}
						SKThread.getInstance().getBinder().onTask(MODULE.SCENE, TASK.SCENE_AND_ITEM, sTaskName, info);
					}
				}
				break;
			case SET_LIGHT: //设置亮度
				// 触摸屏幕超时
				// 使用屏保，注销定时器
				SKTimer.getInstance().getBinder().onDestroy(sCallback);
				// 显示指定画面(true)，亮度变化(false)
				if (true == SystemInfo.isbScreensaver()) {
					outTimeTurn(SystemInfo.getsScreenIndex());
				} else {
					outTimeLight(SystemInfo.getnBrightness());
				}
				// 判断进入屏保就注销用户
				if ((SystemParam.LOGOUT & SystemInfo.getnSetBoolParam()) == SystemParam.LOGOUT) {
					ParameterSet.getInstance().outTimeLogout();
					// 注销用户通知刷新界面显现和触控
					SKSceneManage.getInstance().updateState();
				}
				break;
			case SHOW_LONGIN:
				IOperCall iCall=null;
				if (msg.obj!=null) {
					iCall=(IOperCall)msg.obj;
				}
				CALLTYPE type=CALLTYPE.OPER;
				if (msg.arg1==1) {
					type=CALLTYPE.MACRO;
				}
				//Log.d(TAG, "SHOW_LONGIN iCall="+iCall);
				turnToLogin(iCall,type);
				break;
			case RESET:
				//画面跳转
				isStarting=false;
				break;
			case LOCK_WINDOW:
				//跳出锁屏界面
				String []s=(String[])msg.obj;
				closeLockWindow();
				lockWindow = new GlobalPopWindow(
						getCurrentScene(), WINDOW_TYPE.LOCK, 0, 0,
						SKSceneManage.nSceneWidth, SKSceneManage.nSceneHeight,
						s[1],s[0], null);
				lockWindow.initPopupWindow();
				lockWindow.showPopupWindow();	
				
				break;
			case GOTO_WINDOW:
				//画面跳转
				GotoInfo info=(GotoInfo)msg.obj;
				if(info!=null){
					window(info.type, info.id, info.record, info.enterType, info.eType);
				}
				break;
			case BEEP:
				if (vibrator==null) {
					vibrator = (Vibrator) ContextUtl.getInstance()
							.getSystemService(Service.VIBRATOR_SERVICE);
				}
				if (nBeepCount>0) {
					nBeepCount--;
					vibrator.vibrate(150/* new long[]{1000,50,1000,50}, 0 */);
					mHandler.sendEmptyMessageDelayed(BEEP, nBeepTime);
				}
				break;
			}
		}

	};

	/**
	 * 预加载画面控件信息
	 */
	private void loadSceneAndItem() {
		if (mLoadScene != null) {
			//需要优先加载的画面信息
			if (mPriorLoad.size()>0) {
				try {
					int sid = mPriorLoad.get(0).nLoadSid;
					mPriorLoad.remove(0);
					if (mSceneItemMap.containsKey(sid)) {
						if (mPriorLoad.size()>0||mLoadScene.size()>0) {
							loadSceneAndItem();
						}
					}else {
						LoadInfo info=new LoadInfo();
						info.nSid=sid;
						info.call=false;
						if (sTaskName.equals("")) {
							sTaskName=SKThread.getInstance().getBinder().onRegisterForScene(call);
						}
						SKThread.getInstance().getBinder().onTask(MODULE.SCENE, TASK.SCENE_AND_ITEM, sTaskName,info);
					}
				} catch (Exception e) {
					Log.e(TAG, "ak priorload error !!! ");
					//继续加载画面
					loadSceneAndItem();
				}
				
			}else{
				if (mLoadScene.size() > 0) {
					int sid = mLoadScene.get(0);
					mLoadScene.remove(0);
					if (mSceneItemMap.containsKey(sid)) { 
						if (mLoadScene.size()>0) {
							loadSceneAndItem();
						}
					}else {
						LoadInfo info=new LoadInfo();
						info.nSid=sid;
						info.call=false;
						if (sTaskName.equals("")) {
							sTaskName=SKThread.getInstance().getBinder().onRegisterForScene(call);
						}
						SKThread.getInstance().getBinder().onTask(MODULE.SCENE, TASK.SCENE_AND_ITEM, sTaskName,info);
					}
					
				}
			}
			
		}
	}

	/**
	 * 加载画面信息
	 */
	public void loadSceneInfo(){
		if (sTaskName.equals("")) {
			sTaskName=SKThread.getInstance().getBinder().onRegisterForScene(call);
		}
		SKThread.getInstance().getBinder().onTask(MODULE.SCENE, TASK.SCENE_INFO, sTaskName, null);
	}
	
	SKThread.ICallback call=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			if (taskId==TASK.SCENE_AND_ITEM) {
				if (msg==null) {
					return;
				}
				LoadInfo info=(LoadInfo)msg;
				if (firstLoad) {
					//第一次加载
					nFirstItemSize=DBTool.getInstance().getmSceneBiz().getInitSceneNum(info.nSid);
				}
				
				if (info.call) {
					//需要回调，并且控件已经存在的，直接回调
					if (mSceneItemMap.containsKey(info.nSid)) {
						if (info.nSid==nSceneId) {
							if (mSceneViewMap.containsKey(nSceneId)) {
								SKScene scene=mSceneViewMap.get(nSceneId);
								if (mSceneItemMap.containsKey(nSceneId)) {
									scene.setmSkGraphicsList(mSceneItemMap.get(nSceneId));
								}
								scene.drawGraphics();
								
								//加载当前画面上，需要跳转的画面信息
								ScenceInfo mInfo=getCurrentInfo();
								if (mInfo!=null) {
									priorLoadScene(mInfo);
								}
								return;
							}
						}
					}
				}
				
				loadItem(info);
			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			if (taskId==TASK.SCENE_INFO) {
				//获取画面信息
				initInfo();
			} 
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			
		}
	};
	

	/**
	 * 定时器
	 */
	public SKTimer.ICallback sCallback = new SKTimer.ICallback() {

		@Override
		public void onUpdate() {
			time += 100;
			//Log.d(TAG, "time:" + time);
			// 因为屏保值随时都可能改变
			boolean b = ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER);
			if (SystemInfo.getnScreenTime() == 0
					|| SystemInfo.getnScreenTime() < 0) {
				SystemInfo.setnScreenTime(1);
			}
			if (time > SystemInfo.getnScreenTime() * 60 * 1000 && b) {
				mHandler.sendEmptyMessage(SET_LIGHT);
			}
		}
	};

	/**
	 * 超时进入屏保界面 screnceId 画面号
	 */
	private GlobalPopWindow outTimePop;
	private void outTimeTurn(String screnceIdPath) {
		boolean boo = SKTimer.getInstance().getBinder().isRegister(sCallback);
		if (boo) {
			SKTimer.getInstance().getBinder().onDestroy(sCallback);
		}

		outTimePop = new GlobalPopWindow(mSceneViewMap.get(nSceneId),
				WINDOW_TYPE.SCREENSAVER, 0, 0, nSceneWidth, nSceneHeight,
				screnceIdPath, null);

		if (!GlobalPopWindow.popIsShow) {
			boolean b = SKTimer.getInstance().getBinder().isRegister(sCallback);
			if (b) {
				SKTimer.getInstance().getBinder().onDestroy(sCallback);
			}
			outTimePop.setCallback(pCallBack);
			outTimePop.initPopupWindow();
			outTimePop.showPopupWindow();
			//处于屏保状态
			SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().SceneSaver());
		}

	}

	/**
	 * 超时设置屏幕亮度 lightness 超时屏幕亮度
	 */
	private void outTimeLight(int lightness) {
		boolean boo = SKTimer.getInstance().getBinder().isRegister(sCallback);
		if (boo) {
			SKTimer.getInstance().getBinder().onDestroy(sCallback);
		}
		lightFlag = true;
		//关闭背光灯之前先保存亮度
		saveCurrenBright();
		// 如果超时，则设置亮度为当前亮度的一半
		ScreenBrightness.setBrightness(activity, lightness);
		// 保存亮度的设置状态
		ScreenBrightness.saveBrightness(contentResolver, lightness);
		
		//处于屏保状态
		SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().SceneSaver());
		
		bBackLightOff = true;
		
	}
	
	/**
	 * 打开背光
	 */
	public void backLightOn(){
		
		if (bBackLightOff) {
			if (activity!=null&&contentResolver!=null) {
				SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().SceneSaver());
				// 如果超时，则设置亮度为当前亮度的一半
				ScreenBrightness.setBrightness(activity, nCurrentBright);
				// 保存亮度的设置状态
				ScreenBrightness.saveBrightness(contentResolver, nCurrentBright);
				bBackLightOff=false;
			}
		}
	}
		
	/**
	 * 关闭背光
	 */
	private boolean bBackLightOff;
	public void backlightoff(){
		//如果已经注册了定时器 ，关闭背光的时候就要销毁定时器
		boolean boo = SKTimer.getInstance().getBinder().isRegister(sCallback);
		if (boo) {
			SKTimer.getInstance().getBinder().onDestroy(sCallback);
		}
		if (activity!=null&&contentResolver!=null) {
			//处于屏保状态
			SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().SceneSaver());
			// 如果超时，则设置亮度为当前亮度的一半
			ScreenBrightness.setBrightness(activity, 1);
			// 保存亮度的设置状态
			ScreenBrightness.saveBrightness(contentResolver, 1);
			bBackLightOff=true;
		}
		
	}
	
	private int nCurrentBright;
	public void saveCurrenBright(){
		if (activity!=null) {
			nCurrentBright=ScreenBrightness.getScreenBrightness(activity);
			if(nCurrentBright == SystemInfo.getnBrightness())
			{
				nCurrentBright = 255;
			}
		}else {
			nCurrentBright=255;
		}
	}

	/**
	 * 跳时效窗口
	 */
	public void turnToOutTime() {
		if (SKTimer.getInstance().getBinder().isRegister(sCallback)) {
			SKTimer.getInstance().getBinder().onDestroy(sCallback);
		}
		// 时效窗口还没弹出
		if (GlobalPopWindow.outTimeWindow == false) {
			GlobalPopWindow window = new GlobalPopWindow(
					mSceneViewMap.get(nSceneId), WINDOW_TYPE.OUTTIME, 0, 0,
					SKSceneManage.nSceneWidth, SKSceneManage.nSceneHeight,
					SystemInfo.getOnePassWord().getsPwdStr(), SystemInfo
							.getOnePassWord().getsTimeOut(), null);
			window.setCallback(pCallBack);
			window.initPopupWindow();
			window.showPopupWindow();
			GlobalPopWindow.outTimeWindow = true; // 设置窗口已经弹出
			bHmiLock=true;//人机界面锁定
		}

	}

	/**
	 * 3G 短信锁屏
	 */
	public boolean turnToLockWindow(String info, String psw) {
		try{
			if (getCurrentScene()==null) {
				return false;
			}
			String []msg=new String[]{info,psw};
			mHandler.obtainMessage(LOCK_WINDOW, msg).sendToTarget();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "ak lock window error!!!");
			return false;
		}
	}
	
	public boolean closeLockWindow(){
		try{
			if(lockWindow != null){
				lockWindow.closePop();
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public void timeOut() {
		
		if (!bLoginSuccess) {
			//未登录成功,直接返回
			return;
		}
		boolean bool = ParameterSet.getInstance().outTimeUse(mContext);
		if (bool) {
			// 超出了使用时间
			// 跳转进入密保窗口 1秒钟之后执行 因为时间写的是等于
			outtimeHandler.sendEmptyMessageAtTime(111, 2000);

		} else {
			// 判断是否启用定时器
			if ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER) {
				boolean boo = SKTimer.getInstance().getBinder()
						.isRegister(sCallback);
				// 定时器还没注册
				if (!boo) {
					time = 0;
					SKTimer.getInstance().getBinder().onRegister(sCallback);
				}
			}
		}
	}

	private Handler outtimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 111){
				turnToOutTime();
			}
		};
	};

	private void initValue() {
		if (flag) {
			flag = false;
			contentResolver = activity.getContentResolver();
			// 判断是否是自动调节屏幕亮度
			if (ScreenBrightness.isAutoBrightness(contentResolver)) {
				ScreenBrightness.stopAutoBrightness(activity);
			}
			// 获取当前屏幕的亮度
			initLight = 255;
			if (initLight <= 0) {
				initLight = 1;
			}
			ScreenBrightness.setBrightness(activity, initLight);
			// 保存亮度的设置状态
			ScreenBrightness.saveBrightness(contentResolver, initLight);
		}
	}

	/**
	 * 点击,屏保时间
	 */
	private void click() {
		if (lightFlag == true) {
			if ((SystemInfo.getnSetBoolParam() & SystemParam.USE_SAVER) == SystemParam.USE_SAVER) {
				SKSceneManage.getInstance().time = 0;
				SKTimer.getInstance().getBinder().onRegister(sCallback);
			}
			ScreenBrightness.setBrightness(activity, initLight);
			ScreenBrightness.saveBrightness(contentResolver, initLight);
			lightFlag = false;

		}
	}

	GlobalPopWindow.ICallBack pCallBack = new GlobalPopWindow.ICallBack() {

		// 当关闭popWindow 时 是否 启动定时器
		@Override
		public void onStart() {
			timeOut();
		}

		// 窗口显示的时候，判断定时器是否打开 如果打开则销毁
		@Override
		public void onShow() {
			if (SKTimer.getInstance().getBinder().isRegister(sCallback)) {
				SKTimer.getInstance().getBinder().onDestroy(sCallback);
			}
		}

		@Override
		public void inputFinish(String result) {

		}
	};

	/**
	 * 跳登录窗口
	 */
	private GlobalPopWindow pop = null;
	public void turnToLoginPop() {
		mHandler.sendEmptyMessage(SHOW_LONGIN);
	}
	
	public void turnToLoginPop(IOperCall iCall,CALLTYPE type) {
		int temp=0;
		if (type==CALLTYPE.MACRO) {
			temp=1;
		}
		//Log.d(TAG, "iCall="+iCall);
		mHandler.obtainMessage(SHOW_LONGIN,temp,0,iCall).sendToTarget();
	}
	
	private void turnToLogin(IOperCall iCall,CALLTYPE type){
		int startX = 0, startY = 0, width = SKSceneManage.nSceneWidth, height = SKSceneManage.nSceneHeight;
		int nWidth = 800;
		int nHeigth = 480;
		if (width < 600) {
			nWidth = 250;
		} else if (width * 3 / 4 >= 600) {
			nWidth = 350;
		} else {
			nWidth = 300;
		}
        if(height==240)
        {
        	nHeigth = 220;
        }else if (height < 360) {
			nHeigth = 250;
		} else if (height * 3 / 4 >= 360) {
			nHeigth = 300;
		} else {
			nHeigth = 300;
		}
		pop = new GlobalPopWindow(mSceneViewMap.get(nSceneId),
				WINDOW_TYPE.LOGIN, startX, startY, nWidth, nHeigth, activity);
		if (iCall!=null) {
			pop.setiOperCall(iCall,type);
		}
		
		// 如果窗口没有显示
		if (!GlobalPopWindow.popIsShow) {
			boolean boo = SKTimer.getInstance().getBinder()
					.isRegister(sCallback);
			if (boo) {
				SKTimer.getInstance().getBinder().onDestroy(sCallback);
			}
			pop.setCallback(pCallBack);
			pop.initPopupWindow();
			pop.showPopupWindow();
		}
	}

	public void closeLoginPop() {
		if (pop != null) {
			pop.closePop();
		}
	}

	/**
	 * 销毁
	 */
	public void destroy() {
		
		try {
			if (SKTimer.getInstance().getBinder().isRegister(sCallback)) {
				SKTimer.getInstance().getBinder().onDestroy(sCallback);
			}
			//注销电量广播
			if(SystemInfo.getModel()== HMIMODEL.MID){
				ContextUtl.getInstance().unregisterReceiver(BatteryBroadcast.getInstance());
		     }
            
			// 关闭存储介质状态管理器
			StorageStateManager.getInstance().destroyStateMntRecv();

			/* 然后通知通知线程 */
			SKPlcNoticThread.getInstance().stop();

			// 数据采集
			DataCollect.getInstance().stop();

			/* 先停止所有通信线程 */
			SKCommThread.stopAllThread(true);

			// 报警
			AlarmGroup.getInstance().destroy();

			// 配方
			RecipeDataCentre.getInstance().stop();

			// 时间显示器秒钟线程
			SKTimeShow.mFlag = false;
			// 退出报警登录监视
			AlarmGroup.flag = false;
			// 保存历史报警数据
			//AlarmGroup.getInstance().saveAlarmData(0,0,null,0);
			// 时间同步
			ParameterSet.getInstance().bTimeSys = false;
			//系统键盘是否弹出
			SystemBroadcast.ISKEYBOARDOPEN = false;
			//自定义键盘是否弹出
			SKKeyPopupWindow.keyFlagIsShow=true;
			//是否登录成功
			bLoginSuccess=false;

			SKTimer.getInstance().destroy();
			SKThread.getInstance().destory();
			SKLanguage.getInstance().destory();
			SKTrendsThread.getInstance().destory();
			SKSceneManage.getInstance().exitSceneMacros(nSceneId);
			SKSceneManage.getInstance().exitGlobalMacros();
			AlarmSaveThread.getInstance().destory();
			if (MacroManager.getInstance(null)!=null) {
				MacroManager.getInstance(null).Destroy();
			}
			
			// 移除时效的分钟日期广播
			TimeBroadCast.getInstance(ContextUtl.getInstance()).remove();

			// 将pop弹出的参数复位
			GlobalPopWindow.popIsShow = false;// 设置窗口已经关闭
			GlobalPopWindow.outTimeWindow = false; // 设置窗口已经关闭
			/**
			 * 关闭数据库
			 */
			// SkGlobalData.getDataCollectDatabase().closeDatabase();
			// SkGlobalData.getProjectDatabase().closeDatabase();

			// 清除图片
			ImageFileTool tool = new ImageFileTool();
			tool.clearBitmap();

			this.sTaskName = "";
			iGotoCallback = null;
			flag = true;
			firstLoad = true;
			nFirstItemSize = 0;
			if (mSceneViewMap!=null) {
				mSceneViewMap.clear();
			}
			if(mSceneInfoMap!=null){
				mSceneInfoMap.clear();
			}
			if (mSceneItemMap!=null) {
				mSceneItemMap.clear();
			}
			SystemVariable.isSysThread = false;
			SKWindowManage.getInstance(mContext).show = false;
			mHandler.removeMessages(LOAD_OUT_TIME);
			
			if (mLoadScene!=null) {
				mLoadScene.clear();
			}
			mHandler.removeMessages(LOAD_SCENE_ITEM);

			
			/* 检查文件是否存在 */
			String sSumsungFile = "/data/sumsung.phone";
			File samsungfile = new File(sSumsungFile);
			if(samsungfile.exists()){//三星手机
				File mCollectFileSum = new File(
						SystemVariable.sSumsungLibPath+"libplc_drives_center.so");
				if (mCollectFileSum.exists()) {
					/* 最后关闭协议 */
					ProtocolInterfaces.getProtocolInterface().closeAllProtocol();
				}
			}else{
				File mCollectFile = new File(
						"/data/data/com.android.Samkoonhmi/lib/libplc_drives_center.so");
				if (mCollectFile.exists()) {
					/* 最后关闭协议 */
					ProtocolInterfaces.getProtocolInterface().closeAllProtocol();
				}
			}
			// 掉电保存，最后关闭
			SKSaveThread.getInstance().stop();

			int lastLight = ScreenBrightness.getScreenBrightness(activity);
			if (lastLight != initLight) {
				// 如果最后获取的亮度跟初始进入画面的亮度不相同，则设为原来的初始亮度
				ScreenBrightness.setBrightness(activity, initLight);
				// 保存亮度的设置状态
				ScreenBrightness.saveBrightness(contentResolver, initLight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Log.d(TAG, "app destory...");
			if (activity != null) {
				activity.finish();
			}
			//System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid()); 
		}

	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB(){
		Log.d(TAG, "close all db");
		
		File dataCollectSave = new File(
				"/data/data/com.android.Samkoonhmi/databases/dataCollectSave.db");
		if (dataCollectSave.exists()) {
			SkGlobalData.getDataCollectDatabase().closeDatabase();
		}
		
		File sd = new File(
				"/data/data/com.android.Samkoonhmi/databases/sd.dat");
		if (sd.exists()) {
			SkGlobalData.getProjectDatabase().closeDatabase();
		}
		
		File alarm = new File(
				"/data/data/com.android.Samkoonhmi/alarm/alarm.db");
		if (alarm.exists()) {
			SkGlobalData.getAlarmDatabase().closeDatabase();
		}
		
		File recipe = new File(
				"/data/data/com.android.Samkoonhmi/formula/recipe.dat");
		if (recipe.exists()) {
			SkGlobalData.getRecipeDatabase().closeDatabase();
		}
		
	}
	
	/**
	 * 画面加载信息
	 */
	public class LoadInfo {
		public int nSid;//加载的画面
		public boolean call;//是否回调，如果没数据，先显示界面，再加载控件，然后回调
		public boolean bLoading;//已经处于加载状态
	}


	/**
	 * 更新画面
	 */
	public interface ISKSceneUpdate {

		/**
		 * 更新画面
		 */
		void onUpdateView(SKScene scene);

		/**
		 * 画面切换
		 * @param in -进入动画
		 * @param out-除去动画
		 * @param type=0,不启用动画，type=1 启动动画
		 */
		void onChange(int in, int out, int type);
	}

	public ISKSceneUpdate getiSceneUpdate() {
		return iSceneUpdate;
	}

	public void setiSceneUpdate(ISKSceneUpdate iSceneUpdate, SHOW_TYPE type) {
		if (type == SHOW_TYPE.DEFAULT) {
			mBaseIUpdate = iSceneUpdate;
		}
		this.iSceneUpdate = iSceneUpdate;
	}

	/**
	 * 获取当前画面or画面
	 */
	public SKScene getCurrentScene() {
		return mSceneViewMap.get(nSceneId);
	}

	/**
	 * 获取底层画面
	 */
	public SKScene getBaseScene() {
		return mSceneViewMap.get(nBaseSceneId);
	}

	/**
	 * 获取画面对象
	 */
	public ScenceInfo getSceneInfo() {
		if (containsKey(nBaseSceneId)) {
			return getScenceInfo(nBaseSceneId);
		}
		return null;
	}
	

	/**
	 * 获取当前画面or窗口对象
	 */
	public ScenceInfo getCurrentInfo() {
		if (containsKey(nSceneId)) {
			return getScenceInfo(nSceneId);
		}
		return null;
	}
	
	/**
	 * 获取画面控件
	 */
	public List<ItemInfo> getItemList(int sid){
		if (mSceneItemMap.containsKey(sid)) {
			List<ItemInfo> list=mSceneItemMap.get(sid);
			return list;
		}
		return null;
	}
	
	public SKGraphics getItemId(int sid,int iid){
		SKGraphics mItem=null;
		if (mSceneItemMap.containsKey(sid)) {
			List<ItemInfo> list=mSceneItemMap.get(sid);
			for (int i = 0; i < list.size(); i++) {
				ItemInfo info=list.get(i);
				if (info.nItemId==iid) {
					return info.mItem;
				}
			}
		}
		return mItem;
	}

	private int x;
	public int getX() {
		ScenceInfo info = getScenceInfo(nSceneId);
		if (info != null) {
			if (info.geteType() == SHOW_TYPE.DEFAULT) {
				x = 0;
			} else {
				x = info.getnLeftX();
				if (x == 0) {
					x = (nSceneWidth - info.getnSceneWidth()) / 2;
				}
			}

		}
		return x;
	}

	private int y;
	public int getY() {
		ScenceInfo info = getScenceInfo(nSceneId);
		if (info != null) {
			if (info.geteType() == SHOW_TYPE.DEFAULT) {
				y = 0;
			} else {
				y = info.getnLeftY();
				if (y == 0) {
					y = (nSceneHeight - info.getnSceneHeight()) / 2;
				}
				y += 30;
			}
		}
		return y;
	}

	/**
	 * 移除画面
	 * @param sceneId-画面or窗口id
	 * @param type=0-表示画面,type=1-表示窗口 说明移除窗口时，并不需要删除绑定,画面切换时才需要解除绑定
	 */
	public void removeSKcene(int sceneId, int type) {
		if (mSceneViewMap.containsKey(sceneId)) {
			isDestory = false;
			nSceneId = nBaseSceneId;
			iSceneUpdate = mBaseIUpdate;
			//mSceneViewMap.get(sceneId).setSamkoonHmi(false);
			mSceneViewMap.get(sceneId).clearData();
			SKProgress.hide();// 画面切换隐藏等待圈

			/**
			 * 切换注销用户
			 */
			if (containsKey(sceneId)) {
				ScenceInfo info = getScenceInfo(sceneId);
				if (info != null) {
					if (info.isbLogout()) {
						// 注销用户
						ParameterSet.getInstance().outTimeLogout();
					}
				}
			}
			
			if (type == 0) {
				unBinder(sceneId);
			}else {
				SystemInfo.setCurrentScenceId(nSceneId);
			}

		}
	}

	/**
	 * 解除所有绑定
	 */
	public void unBinder(int sid) {
		SKTimer.getInstance().clear();
		SKThread.getInstance().destory();
		SKLanguage.getInstance().destory();
		SKTrendsThread.getInstance().destory();
		// SKPlcNoticThread.getInstance().stop();
		exitSceneMacros(sid);
		
		SKPlcNoticThread.getInstance().removeRefreshId(sid);
	}
	
	//设置蜂鸣声
	private static int nBeepCount=0;
	private static int nBeepTime=100;
	/**
	 * @param count
	 * @param time
	 * @return
	 */
	public boolean beep(int count,int time){
		if (count<0||time<0) {
			return false;
		}
		nBeepCount=count;
		nBeepTime=time*100;
		mHandler.sendEmptyMessage(BEEP);
		return true;
	}

	/**
	 * 切换用户 更新状态
	 */
	public void updateState() {
		if (mSceneViewMap.get(nSceneId) != null) {
			mSceneViewMap.get(nSceneId).updateView();
		}
	}
	
	private boolean containsKey(int sid){
		boolean result=false;
		if (mSceneInfoMap!=null) {
			for (int i = 0; i < mSceneInfoMap.size(); i++) {
				if (mSceneInfoMap.get(i).getnSceneId()==sid) {
					return true;
				}
			}
		}
		return result;
	}
	
	public ScenceInfo getScenceInfo(int sid){
		ScenceInfo info=null;
		if (mSceneInfoMap!=null) {
			for (int i = 0; i < mSceneInfoMap.size(); i++) {
				if (mSceneInfoMap.get(i).getnSceneId()==sid) {
					return mSceneInfoMap.get(i);
				}
			}
		}
		return info;
	}

	private boolean isDestory = true;
	public interface ISceneDestory {
		void destory(boolean result);
	}

	/**
	 * skscene 销毁通知
	 */
	ISceneDestory iDestory = new ISceneDestory() {

		@Override
		public void destory(boolean result) {
			isDestory = result;
		}
	};

	/**
	 * 回调通知登录界面，跳转
	 */
	public interface IGotoCallback {
		/**
		 * 跳转
		 */
		void onGoto();
	}

	public void setmGotoCallback(IGotoCallback mGotoCallback) {
		this.iGotoCallback = mGotoCallback;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public HashMap<Integer, SKScene> getmSceneViewMap() {
		return mSceneViewMap;
	}

	/**
	 * 场景显示类型
	 */
	public enum SHOW_TYPE {
		FLOATING, // 漂浮
		DEFAULT, // 默认
	}

	public SHOW_TYPE geteType() {
		return eType;
	}
	
	/**
	 * 画面控件
	 */
	public class ItemInfo{
		public int nItemId;//
		public SKGraphics mItem;
	}
	
	public void setmSceneViewMap(HashMap<Integer, SKScene> mSceneViewMap) {
		this.mSceneViewMap = mSceneViewMap;
	}
	
	/**
	 * 初始化所有读的地址到PLC中去
	 */
	private HashMap<Integer, Vector<AddrProp>> mReadAddr = new HashMap<Integer, Vector<AddrProp>>();
	private HashMap<Integer, Vector<AddrProp>> mOffsetReadAddr = new HashMap<Integer, Vector<AddrProp>>();
	public void setOneSceneReadAddrs(int nSceneId) {

		boolean bCover = true;
		Vector<AddrProp> mAddrList = null;
		Vector<AddrProp> mAddrOffsetList = null;
		ScenceInfo info = getScenceInfo(nSceneId);
		if (null != info) {
			if (SHOW_TYPE.DEFAULT == info.geteType()) {
				//画面
				if (mReadAddr.containsKey(nSceneId)) {
					mAddrList = mReadAddr.get(nSceneId);
				}else {
					mAddrList = SkGlobalData.getProjectDatabase().getAddrListBySql(
							"select * from arrangeAddr where nSceneId = "
									+ nSceneId, null);
					mReadAddr.put(nSceneId, mAddrList);
				}
				
				if (mOffsetReadAddr.containsKey(nSceneId)) {
					mAddrOffsetList = mOffsetReadAddr.get(nSceneId);
					if (null != mAddrOffsetList && null != mAddrList) {
						for (int i = 0; i < mAddrOffsetList.size(); i++) {
							mAddrList.add(mAddrOffsetList.get(i));
						}
					}
				}
			} else {
				//窗口
				bCover = false;
				if (mReadAddr.containsKey(nSceneId)) {
					mAddrList = mReadAddr.get(nSceneId);
				}else {
					mAddrList = SkGlobalData.getProjectDatabase().getAddrListBySql(
							"select * from arrangeAddr where nSceneId = "
									+ nSceneId, null);
					mReadAddr.put(nSceneId, mAddrList);
				}
			}
		}
		PlcRegCmnStcTools.setOneSceneReadAddr(mAddrList, bCover);
	}

	/**
	 * 更新画面读取地址
	 */
	public void updateSceneReadAddrs(int sid, AddrProp addr) {

		if (sid == -1) {
			sid = nSceneId;// 全局宏，
		}
		if (mOffsetReadAddr.containsKey(sid)) {
			Vector<AddrProp> list = mOffsetReadAddr.get(sid);

			if (list != null) {
				int listSize = list.size();
				for (int k = 0; k < listSize; k++) {
					if (addr.nAddrId == list.get(k).nAddrId
							&& addr.nRegIndex == list.get(k).nRegIndex) {
						list.remove(k);
						break;
					}
				}
				list.add(addr);
				setOneSceneReadAddrs(sid);
			}
		} else {
			Vector<AddrProp> list = new Vector<AddrProp>();
			list.add(addr);
			mOffsetReadAddr.put(sid, list);
			setOneSceneReadAddrs(sid);
		}
	}
	
	/**
	 * 画面跳转参数
	 */
	class GotoInfo{
		int type;
		int id;
		boolean record;
		int enterType;
		GOTO_TYPE eType;
	}

	/**
	 * 获取锁屏标示
	 */
	public boolean isbHmiLock() {
		return bHmiLock;
	}

	/**
	 * 设置锁屏标示
	 */
	public void setbHmiLock(boolean bHmiLock) {
		this.bHmiLock = bHmiLock;
	}
	
	public boolean isbWindowFocus() {
		return bWindowFocus;
	}

	public void setbWindowFocus(boolean bWindowFocus) {
		this.bWindowFocus = bWindowFocus;
	}
}
