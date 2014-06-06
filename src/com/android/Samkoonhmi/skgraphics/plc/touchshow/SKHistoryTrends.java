package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.SKTrendsThread;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnClickListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import com.android.Samkoonhmi.graphicsdrawframe.FreeLineItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ShowInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.TrendsDataInfo;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.model.sk_historytrends.HistoryTrendsInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.HISTORYSHOW_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skenum.TIMERANGE_TYPE;
import com.android.Samkoonhmi.skenum.CURVE_TYPE;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skgraphics.ITimerUpdate;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.HistoryCollectProp;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TrendCalendar;
import com.android.Samkoonhmi.util.TrendsWatch;
import com.android.Samkoonhmi.util.ZoomAttr;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.DataCollect.HistoryDataProp;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalBackThread;
import com.android.Samkoonhmi.skwindow.SKProgress.ShowStyle;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.skwindow.SKProgress;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class SKHistoryTrends extends SKGraphCmnTouch implements ITimerUpdate,
		OnClickListener,IItem {

	private static final String TAG = "SKHistoryTrends";
	private static final int DIALOG_SHOW = 1;// 显示
	private static final int DIALOG_HIDE = 2;// 隐藏
	private static final int DIALOG_SEND = 3;// 发送数据
	private static final int DIALOG_FRESH_START = 4;// 刷新开始日期
	private static final int DIALOG_FRESH_END = 5;// 刷新结束日期
	private static final int HIDE_SUBLINE=6;//隐藏辅助线
	private HistoryTrendsInfo mTrendsInfo = null;
	private List<ChannelGroupInfo> channelGroups;
	public static final int TSP_XMARGIN = 40;
	public static final int TSP_YMARGIN = 40;
	private int XLine_Tag;// 13;
	private int YLine_Tag;
	private int XScale_Tag;
	private RectItem TrendsRectItem; // 外框
	private Rect TrendsRect;
	private RectItem TrendsCurveRectItem; // 曲线框
	private Rect TrendsCurveRect;
	private Bitmap bgBitmap; // 背景图片
	private short nCurveX; // 控件曲线区域左上角X坐标
	private short nCurveY; // 控件曲线区域左上角Y坐标
	private short nCurveWd; // 控件曲线区域宽度
	private short nCurveHt; // 控件曲线区域高度

	private myPopupWindow mPopupWindow; // 显示的日期窗口
	private View cView; // 日期窗口的布局
	private LayoutInflater mInflater;
	public boolean popIsShow; // 窗口是否弹出
	private Button sureButton; // 确定按钮
	private Button cancelButton;// 取消按钮

	private short nStart_DateX; // 开始日期左上角X坐标
	private short nStart_DateY; // 开始日期左上角Y坐标
	private short nStart_DateWd; // 开始日期宽度
	private short nStart_DateHt; // 开始日期高度

	private short nStart_TimeX; // 开始时间左上角X坐标
	private short nStart_TimeY; // 开始时间左上角Y坐标
	private short nStart_TimeWd; // 开始时间宽度
	private short nStart_TimeHt; // 开始时间高度

	private short nEnd_DateX; // 结束日期左上角X坐标
	private short nEnd_DateY; // 结束日期左上角Y坐标
	private short nEnd_DateWd; // 结束日期宽度
	private short nEnd_DateHt; // 结束日期高度

	private short nEnd_TimeX; // 结束时间左上角X坐标
	private short nEnd_TimeY; // 结束时间左上角Y坐标
	private short nEnd_TimeWd; // 结束时间宽度
	private short nEnd_TimeHt; // 结束时间高度

	private TYPE nCompile_Flag; // 比较标志
	private short nTemp_Year;
	private short nTemp_Month;
	private short nTemp_Day;
	private short nTemp_Hour;
	private short nTemp_Minute;

	private boolean TrendsCall_Flag;
	private Rect mRect;
	private Paint mPaint;
	private SKItems item;
	private String sTaskName; // 任务签名
	private int nTrendsId = 0;
	private int nSceneId;
	private Vector<String> realdata = null;
	private Vector<Vector<String>> groupdata = null;
	private Calendar start_date;
	private Calendar end_date;
	private TrendCalendar start_list;
	private TrendCalendar end_list;
	private Toast skToast = null;
	private boolean nRealTime_ShowFlag;
	private boolean nRealTime_FirstFlag;
	private boolean bInitShow;// 初始化显示
	private long nLastTime;// 最后采集点时间
	private long nStartTime;// 最先采集点时间；
	private float nRealTime_Scale;
	private FreeLineItem freeLineItem = null;
	private boolean isDrawing;// 正在绘制中
	private CURVE_TYPE mTrendType;// 采集类型

	public CURVE_TYPE getmTrendType() {
		return mTrendType;
	}

	/**
	 * @param mOnDateChangedListener
	 */
	public enum OPRATE {
		OPRATE_NORMAL, OPRATE_LARGE, // 放大
		OPRATE_MOVE_X, // X轴平移
		OPRATE_MOVE_Y, // Y轴平移
		OPRATE_RETURN, // 回到上一状
	}

	private boolean bMoveFlag;// 滑动标识
	//private boolean read_historytime; // 是否设置历史时间
	private OPRATE Zoom_Oprate; // 放大操作模式
	private long Zoom_Len; // 放大的时间范围
	private long old_start_time;

	private Vector<ZoomAttr> mZoomList;// 放大集合
	private Vector<ZoomAttr> mReduceList;// 缩小集合
	private ZoomAttr Current_Zoom;// 当前缩放
	private Vector<Integer> showAddrPropList; // 显现通知地址
	private Vector<Integer> GroupList;
	private ShowInfo showInfo;// 显现属性
	private boolean isShowFlag; // 显现标志
	private boolean showByUser;// 显现受用户控制
	private boolean showByAddr;// 显现受地址控制

	// 曲线计算变量的定义
	private int Real_DataSample; // 实际采样数
	private int Real_ScrollSample; // 实际滚动数
	private float Scroll_Scale;// 实际滚动与实际采样的比例值
	private float Sample_Scale;// 样本采样跟实际显示的比例值
	private boolean Sample_Full_Flag; // 采样满标志
	private float point_x;

	private float point_y; // 采集的点转换的坐标值
	private float total_second;
	private Vector<Vector<String>> historydata;
	private double current_hisdata; //
	HistoryCollectProp Collect_Value;
	private Vector<Integer> nChannelList;
	private boolean RealTimeRefresh_Flag; // 实时曲线刷新标志
	private boolean TimeRange_Flag; // 是否获得开始时间
	private Vector<FreeLineItem> freeLineItemGroups;
	private Vector<FreeLineItem> ShowfreeLineItemGroups = null;
	private int popup_flag; // 转圈
	private FreeLineItem[] freeLineItemList;
	private TimeInfo mTimeInfo;
	private float nUnitLenOfTime;// 每一毫米所占的长度
	private boolean bShowSubline;//是否显示辅助线
	private Paint mSublinePaint;

	public SKHistoryTrends(int id, int sceneId, HistoryTrendsInfo info) {
		// TODO Auto-generated constructor stub
		isShowFlag = true;
		showByUser = false;
		showByAddr = false;
		this.nTrendsId = id;
		this.sTaskName = "";
		this.nSceneId = sceneId;
		this.TrendsCall_Flag = false;
		this.bShowSubline=false;
		end_list = null;
		start_list = null;
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mSublinePaint=new Paint();
		mSublinePaint.setAntiAlias(true);
		mSublinePaint.setColor(Color.WHITE);
		mSublinePaint.setStrokeWidth(1);
		item = new SKItems();
		TrendsWatch.getInstance().startWatch();
		this.mTrendsInfo = info;
		this.showInfo = mTrendsInfo.getmShowInfo();
		mTimeInfo = new TimeInfo();

		if (mTrendsInfo != null) {
			mTrendType = mTrendsInfo.getnCurveType();
			TrendsRect = new Rect();
			TrendsRectItem = new RectItem(TrendsRect);

			TrendsCurveRect = new Rect();
			TrendsCurveRectItem = new RectItem(TrendsCurveRect);

			mRect = new Rect(mTrendsInfo.getnLp()-1, mTrendsInfo.getnTp()-1,
					mTrendsInfo.getnLp() + mTrendsInfo.getnWidth()+1,
					mTrendsInfo.getnTp() + mTrendsInfo.getnHeight()+1);

			item.itemId = nTrendsId;
			item.nZvalue = mTrendsInfo.getnZvalue();
			item.nCollidindId = mTrendsInfo.getnCollidindId();
			item.rect = mRect;
			item.sceneId = nSceneId;
			item.mGraphics = this;
			
			mSublinePaint.setColor(getInverseColor(mTrendsInfo.getnGraphColor()));

			// 根据显现对象判断是受用户控制还是受地址控制
			if (null != showInfo) {
				if (null != showInfo.getShowAddrProp()) {
					showByAddr = true;
				}
				if (showInfo.isbShowByUser()) {
					showByUser = true;
				}
			}

			// 显现受地址控制，注册地址
			if (showByAddr) {
				ADDRTYPE addrType = showInfo.geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance()
							.addNoticProp(showInfo.getShowAddrProp(), showCall,
									true, sceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							showInfo.getShowAddrProp(), showCall, false,
							sceneId);
				}

			}

			// 开始显示时间
			if (mTrendsInfo.getmFromAddr() != null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mTrendsInfo.getmFromAddr(), fromCall, false, sceneId);
			}
			
			// 结束显示时间
			if (mTrendsInfo.getmToAddr() != null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mTrendsInfo.getmToAddr(), toCall, false, sceneId);
			}
		}

	}

	@Override
	public void initGraphics() {

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (mTrendsInfo == null) {
			return;
		}
		bMoveFlag = false;
		RealTimeRefresh_Flag = false;

		// 初始化显现标志
		historyIsShow();

		//read_historytime = true;
		if (sTaskName.equals("")) {
			sTaskName = SKTrendsThread.getInstance().getBinder().onRegister(tCallback);
		}

		// 必须初始化，没初始化，拿到的是旧的时间
		start_date = Calendar.getInstance();
		end_date = Calendar.getInstance();

		channelGroups = mTrendsInfo.getchannelGroups();
		nLastTime = 0;
		nStartTime = 0;

		/**
		 * 初始化缩放
		 */
		Init_ZoomData();
		
		/**
		 * 计算曲线位置
		 */
		CalCurve();
		
		/**
		 * 计算历史曲线显示时间
		 */
		SetHistoryTime();
		
		/**
		 * 数据群组
		 */
		SetGroupData();
		
		//初始化线
		Init_FreeLine();
		
		
		init_curve();

		bInitShow = true;
		nRealTime_ShowFlag = true;
		nRealTime_FirstFlag = false;
		nRealTime_Scale = (float) 0.0;
		popup_flag = 0;

		mTimeInfo.isLoading = false;
		mTimeInfo.isLargeX = false;
		mTimeInfo.isLargeY = false;

		if (GroupList == null) {
			GroupList = new Vector<Integer>();
		}
		GroupList.clear();
		
		// 显示on/off地址注册
		showAddrPropList = null;
		if (showAddrPropList == null) {
			showAddrPropList = new Vector<Integer>();
			for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
				HISTORYSHOW_TYPE show_type = channelGroups.get(i)
						.getnDisplayCondition();
				if (show_type == HISTORYSHOW_TYPE.ALWAYS_SHOW) {
					showAddrPropList.add(1);
				} else {
					AddrProp ChannelAddr = channelGroups.get(i)
							.getnDisplayAddr();
					TrendsWatch.getInstance().addNoticProp(ChannelAddr,
							nSceneId);
					showAddrPropList.add(0xff);
				}
			}
		}

		TrendsWatch.getInstance().getBinder().onRegister(TrendsCall);
		TrendsWatch.getInstance().start();
				

		if (mTrendsInfo.getnCurveType() == CURVE_TYPE.REALTIME_CURVE) {
			// 实时曲线，数据采集注册
			GroupList.add((int) mTrendsInfo.getnGroupNum());
			DataCollect.getInstance().addNoticProp(1, GroupList, call);

		} else if (mTrendsInfo.getnCurveType() == CURVE_TYPE.DATAGROUP_CURVE) {
			// 数据群组，数据采集注册
			for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
				GroupList.add((int) channelGroups.get(i).getnNumOfChannel());
			}
			DataCollect.getInstance().addNoticProp(1, GroupList, groupcall);

		} else {
			if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.RECENT_BEGIN)) {
				GroupList.add((int) mTrendsInfo.getnGroupNum());
				DataCollect.getInstance().addNoticProp(2, GroupList, call);
			} else if (mTrendsInfo.getnTimeRange().equals(
					TIMERANGE_TYPE.RANGE_BEGIN)) {
				GroupList.add((int) mTrendsInfo.getnGroupNum());
				DataCollect.getInstance().addNoticProp(2, GroupList, call);
			} else if (mTrendsInfo.getnTimeRange().equals(
					TIMERANGE_TYPE.ADDR_BEGIN)) {
				GroupList.add((int) mTrendsInfo.getnGroupNum());
				DataCollect.getInstance().addNoticProp(2, GroupList, call);
			}
			SKTrendsThread.getInstance().getBinder()
					.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName); // 历史曲线，数据采集注册

		}

		// 注册定时器
		SKTimer.getInstance().getBinder().onRegister(callback, 600);

		
		TrendsCall_Flag = true;
		SKSceneManage.getInstance().onRefresh(item);
	}

	/**
	 * 曲线绘制到界面上
	 */
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if ((itemId == nTrendsId) && (mTrendsInfo != null)) {
			if (isShowFlag) {
				// 是否能显现 如果能显现，则画
				draw(canvas);
			}
			return true;
		}
		return false;
	}

	/**
	 * 曲线绘制
	 */
	private void draw(Canvas canvas) {
		if (bgBitmap == null) {
			bgBitmap = getBgBitmap(mTrendsInfo.getnWidth(),
					mTrendsInfo.getnHeight());
		}

		// 绘制背景
		canvas.drawBitmap(bgBitmap, mTrendsInfo.getnLp(), mTrendsInfo.getnTp(),null);

		// Y轴时间
		if (mTrendsInfo.isbMainVer()) {
			DrawY_Scale(mPaint, canvas);
		}

		// 画曲线
		DrawCurver(mPaint, canvas);

		// X轴
		if (mTrendsInfo.isbMainHor()) {
			if (mTrendsInfo.getnCurveType() == CURVE_TYPE.DATAGROUP_CURVE) {
				// 绘制数据群组，x轴刻度
				DrawDataGroup(mPaint, canvas);
				return;
			}
		}
		
		// 实时曲线和历史曲线，x轴刻度
		DrawTime(mPaint, canvas);
		
		//绘制辅助线
		if(bShowSubline){
			int top=(int)nSublineY;
			if (top<mTrendsInfo.getnTp()+ mTrendsInfo.getnCurveY()) {
				top=mTrendsInfo.getnTp()+ mTrendsInfo.getnCurveY();
			}
			int left=(int)nSublineX;
			if (left<mTrendsInfo.getnLp()+mTrendsInfo.getnCurveX()) {
				left=mTrendsInfo.getnLp()+mTrendsInfo.getnCurveX();
			}
			canvas.drawLine(mTrendsInfo.getnLp()+mTrendsInfo.getnCurveX(), top, 
					mTrendsInfo.getnLp()+mTrendsInfo.getnCurveX()+mTrendsInfo.getnCurveWd(), top, mSublinePaint);
			
			canvas.drawLine(left,mTrendsInfo.getnTp()+ mTrendsInfo.getnCurveY(), 
					left, mTrendsInfo.getnTp()+mTrendsInfo.getnCurveY()+mTrendsInfo.getnCurveHt(), mSublinePaint);
		}
	}

	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShowFlag = isShow();
		}
	};

	/**
	 * 后台线程,回调接口
	 */
	SKTrendsThread.ICallback tCallback = new SKTrendsThread.ICallback() {

		@Override
		public void onUpdate(String msg, int taskId) {
			// TODO Auto-generated method stub
		}

		public void onUpdate(int msg, int taskId) {
			Vector<FreeLineItem> SendfreeLineItemGroups = new Vector<FreeLineItem>();
			FreeLineItem[] SendfreeLineItemList = null;

			SendfreeLineItemList = new FreeLineItem[mTrendsInfo.getnChannelNum()];
			for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
				Vector<PointF> m_pointList = new Vector<PointF>();
				SendfreeLineItemList[i] = new FreeLineItem(m_pointList,
						END_ARROW_TYPE.STYLE_NONE);
				SendfreeLineItemGroups.add(SendfreeLineItemList[i]);
			}

			if (taskId == TASK.CURVE_CAL_DATA) {

				// Log.d(TAG, "CURVE_CAL_DATA.......");
				if (mTrendsInfo.getnCurveType() == CURVE_TYPE.REALTIME_CURVE) {
					// 实时曲线
					if (RealTimeRefresh_Flag) {
						return;
					}
					RealTimeRefresh_Flag = true;
					HandleRealCurveData(realdata);
					nRealTime_ShowFlag = Sample_Full_Flag;
					nRealTime_Scale = Scroll_Scale;
					//Log.d(TAG, "REALTIME_CURVE RealTimeRefresh_Flag = "+RealTimeRefresh_Flag);
					if (RealTimeRefresh_Flag == true) {
						GetCurFreeLine(SendfreeLineItemGroups);
						handler.removeMessages(DIALOG_SEND);
						handler.obtainMessage(DIALOG_SEND,SendfreeLineItemGroups).sendToTarget();
					}
					RealTimeRefresh_Flag = false;
					mTimeInfo.isLoading = false;
				} else if (mTrendsInfo.getnCurveType() == CURVE_TYPE.HISTORY_CURVE) {
					// 历史曲线
					if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.RECENT_BEGIN)) {

						if (bInitShow) {
							//读取数据库
							SkGlobalBackThread
									.getInstance()
									.getGlobalBackHandler()
									.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE,iReadCallback).sendToTarget();
						} else {
							if (RealTimeRefresh_Flag) {
								return;
							}

							RealTimeRefresh_Flag = true;
							HandleRealCurveData(realdata);
							nRealTime_ShowFlag = Sample_Full_Flag;
							nRealTime_Scale = Scroll_Scale;
							//Log.d(TAG, "RealTimeRefresh_Flag = "+RealTimeRefresh_Flag);
							if (RealTimeRefresh_Flag == true) {
								GetCurFreeLine(SendfreeLineItemGroups);
								handler.removeMessages(DIALOG_SEND);
								handler.obtainMessage(DIALOG_SEND,
										SendfreeLineItemGroups).sendToTarget();
							}
							RealTimeRefresh_Flag = false;
							mTimeInfo.isLoading = false;
						}
					} else {

						SkGlobalBackThread
								.getInstance()
								.getGlobalBackHandler()
								.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE,iReadCallback).sendToTarget();
						mTrendsInfo.setnTimeRange(mTrendsInfo.getnOldTimerange());

					}

				} else if (mTrendsInfo.getnCurveType() == CURVE_TYPE.DATAGROUP_CURVE) {
					// 数据群组
					HandleDataGroup(groupdata);
					GetCurFreeLine(SendfreeLineItemGroups);
					handler.obtainMessage(DIALOG_SEND, SendfreeLineItemGroups)
							.sendToTarget();
				}
			}
		}

		@Override
		public void onUpdate(Object msg, int taskId) {
			// TODO Auto-generated method stub
		}

	};

	/**
	 * 处理UI界面
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == DIALOG_SHOW) {
				SKProgress.show(SKSceneManage.getInstance().mContext, 120, 120,
						start_x, start_y, ShowStyle.DEFAULT);
				popup_flag = 1;
			} else if (msg.what == DIALOG_HIDE) {
				SKProgress.hide();
				popup_flag = 0;
			} else if (msg.what == DIALOG_SEND) {
				Vector<FreeLineItem> handlefreeLineItemGroups = (Vector<FreeLineItem>) msg.obj;
				if (!isDrawing) {
					ShowfreeLineItemGroups = handlefreeLineItemGroups;
					SKSceneManage.getInstance().onRefresh(item);
				}
			}else if (msg.what==HIDE_SUBLINE) {
				//隐藏辅助线
				bShowSubline=false;
				SKSceneManage.getInstance().onRefresh(item);
			}
		}

	};

	public void updateStatus() {
	}

	@Override
	public boolean isShow() {
		historyIsShow();
		SKSceneManage.getInstance().onRefresh(item);
		return isShowFlag;
	}

	/**
	 * 得到是否能显现
	 */
	private void historyIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(showInfo);
		}
	}

	/**
	 * 定时回调接口
	 */
	private boolean bDataChange = true;// 数据有变化
	SKTimer.ICallback callback = new SKTimer.ICallback() {

		@Override
		public void onUpdate() {
			if (mTrendsInfo.getnCurveType() == CURVE_TYPE.HISTORY_CURVE) {
				if ((mTrendsInfo.getnTimeRange()
						.equals(TIMERANGE_TYPE.RANGE_BEGIN))
						|| (mTrendsInfo.getnTimeRange()
								.equals(TIMERANGE_TYPE.STORE_BEGIN))
						|| (mTrendsInfo.getnTimeRange()
								.equals(TIMERANGE_TYPE.ADDR_BEGIN))) {
					//Log.d(TAG, "bDataChange=" + bDataChange);
					if (bDataChange) {
						SKTrendsThread
								.getInstance()
								.getBinder()
								.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA,
										sTaskName);
						bDataChange = false;
					}

				} else {

					if (RealTimeRefresh_Flag) {
						SKTrendsThread
								.getInstance()
								.getBinder()
								.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA,
										sTaskName);
						RealTimeRefresh_Flag = false;
					}
				}

			}
		}
	};

	/**
	 * 数据群组二维容器初始化
	 */
	private void SetGroupData() {
		if (groupdata == null) {
			groupdata = new Vector<Vector<String>>();
			for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
				Vector<String> groups = new Vector<String>();
				groupdata.add(groups);
			}
		}
	}

	/**
	 * 缩放初始化
	 */
	private void Init_ZoomData() {
		if (mZoomList == null) {
			mZoomList = new Vector<ZoomAttr>();
		} else {
			mZoomList.clear();
		}

		if (Current_Zoom == null) {
			Current_Zoom = new ZoomAttr();
		}

		if (mReduceList == null) {
			mReduceList = new Vector<ZoomAttr>();
		} else {
			mReduceList.clear();
		}

		Current_Zoom.zoom_start = (float) 0.0;
		Current_Zoom.zoom_rate = (float) 1.0;
		Current_Zoom.y_start = (float) 0.0;
		Current_Zoom.y_rate = (float) 1.0;
		Zoom_Oprate = OPRATE.OPRATE_NORMAL;
	}

	/**
	 * 初始化线
	 */
	private void Init_FreeLine() {
		if (freeLineItem == null) {
			Vector<PointF> m_pointList = new Vector<PointF>();
			freeLineItem = new FreeLineItem(m_pointList,END_ARROW_TYPE.STYLE_NONE);
		}
	}

	/**
	 * 曲线的初始化
	 */
	private void init_curve() {
		nChannelList = new Vector<Integer>();
		Collect_Value = new HistoryCollectProp();

		freeLineItemGroups = new Vector<FreeLineItem>();
		freeLineItemList = new FreeLineItem[mTrendsInfo.getnChannelNum()];

		if (mTrendsInfo.getnDataSample() < nCurveWd) {
			Real_DataSample = mTrendsInfo.getnDataSample();
			Real_ScrollSample = mTrendsInfo.getnScrollSample();
		} else {
			Real_DataSample = nCurveWd;
			Real_ScrollSample = (short) (mTrendsInfo.getnScrollSample()
					* nCurveWd / mTrendsInfo.getnDataSample());
		}

		if (Real_DataSample < Real_ScrollSample) {
			Real_ScrollSample = Real_DataSample;
		}

		if (Real_DataSample < 2) {
			Real_DataSample = 2;
		}

		Scroll_Scale = (float) Real_ScrollSample / Real_DataSample;

		if (mTrendsInfo.getnCurveType() == CURVE_TYPE.DATAGROUP_CURVE) {
			Sample_Scale = (float) nCurveWd / (Real_DataSample - 1);
		} else {
			Sample_Scale = (float) nCurveWd / Real_DataSample;
		}

		if (Real_ScrollSample == 0) {
			Real_ScrollSample = 1;
		}

		// 1毫秒所占的长度
		nUnitLenOfTime = (float) nCurveWd/(mTrendsInfo.getnRecentMinute() * 60 * 1000);

		for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
			Vector<PointF> m_pointList = new Vector<PointF>();
			freeLineItemList[i] = new FreeLineItem(m_pointList,
					END_ARROW_TYPE.STYLE_NONE);
			freeLineItemGroups.add(freeLineItemList[i]);
		}
		// show_flag = 0;
		isDrawing = false;

		Sample_Full_Flag = false;
		if (mTrendsInfo.getnCurveType() == CURVE_TYPE.REALTIME_CURVE) {
			// 默认单位是秒
			total_second = mTrendsInfo.getnRecentMinute() * 60;
		}
	}

	/**
	 * 数据群组
	 */
	DataCollect.IPlcNoticCallBack groupcall = new DataCollect.IPlcNoticCallBack() {

		public void addrValueNotic(Vector<HistoryDataProp> nValueList,
				int nCollectRate) {

			if ((nValueList == null) || (nValueList.isEmpty())) {
				return;
			}

			int nLen = nValueList.size();
			int newsize;
			// Log.d(TAG, "add call....nLen:"+nLen);

			for (short i = 0; i < nLen; i++) {
				Vector<String> newdata = groupdata.get(i);
				newdata.clear();
				newsize = nValueList.get(i).nDataList.size();
				for (short j = 0; j < newsize; j++) {
					newdata.add(nValueList.get(i).nDataList.get(j));
				}
			}

			if (nLen > 0) {
				SKTrendsThread
				        .getInstance()
						.getBinder()
						.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA,sTaskName);
			}
		}

		public void noticDelDatabase(int gid) {}
	};

	/**
	 * 采集回调
	 */
	DataCollect.IPlcNoticCallBack call = new DataCollect.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<HistoryDataProp> nValueList,
				int nCollectRate) {
			// TODO Auto-generated method stub

			int nLen = 0;

			if ((nValueList == null) || (nValueList.isEmpty())) {
				return;
			}

			// Log.d(TAG, "add..............");
			if (mTrendsInfo.getnCurveType() == CURVE_TYPE.HISTORY_CURVE) {
				// 历史曲线
				if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.RECENT_BEGIN)) {
					if (bInitShow) {
						// 未初始化
						return;
					}
					if (end_date.getTimeInMillis() < nLastTime- (mTrendsInfo.getnRecentMinute() * 60 * 1000)) {
						// 已经移动，不处于最后时间
						return;
					}
					if (mTimeInfo.isLoading) {
						// 正在加载数据中
						return;
					}
					nLastTime = System.currentTimeMillis();
				} else if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.RANGE_BEGIN)) {
					// 设置起始时间+结束时间
					long c = System.currentTimeMillis();
					if (c >= nStartTime && c < nLastTime) {
						bDataChange = true;
					}
					return;
				} else if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.STORE_BEGIN)) {
					// 存盘数据的开始
					bDataChange = true;
					return;
				} else if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.ADDR_BEGIN)) {
					// 受地址范围控制显示
					long c = System.currentTimeMillis();
					if (c >= nStartTime && c < nLastTime) {
						bDataChange = true;
					}
					return;
				}
			}

		    //Log.d(TAG, "add......");
			if (null == realdata) {
				realdata = new Vector<String>();
			} else {
				realdata.clear();
			}

			nLen = nValueList.get(0).nDataList.size();
			for (short i = 0; i < nLen; i++) {
				realdata.add(nValueList.get(0).nDataList.get(i));
			}

			if (nLen > 0) {
				SKTrendsThread
						.getInstance()
						.getBinder()
						.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA,sTaskName);
			}
		}

		public void noticDelDatabase(int gid) {

		}
	};

	/**
	 * 地址触发显示
	 */
	TrendsWatch.ITrendsCallback TrendsCall = new TrendsWatch.ITrendsCallback() {

		@Override
		public void onTrends(TrendsDataInfo info) {
			// 地址触发
			int Addr = info.getnNumofAddr().nAddrValue;
			for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
				HISTORYSHOW_TYPE show_type = channelGroups.get(i)
						.getnDisplayCondition();

				AddrProp ChannelAddr = channelGroups.get(i).getnDisplayAddr();
				if (Addr == ChannelAddr.nAddrValue) {
					if (show_type == HISTORYSHOW_TYPE.OFF_SHOW) {
						if (info.getnNumOfValue().equals(0)) {
							showAddrPropList.set(i, 1);
						} else {
							showAddrPropList.set(i, 0xff);
						}
					} else if (show_type == HISTORYSHOW_TYPE.ON_SHOW) {
						if (info.getnNumOfValue().equals(1)) {
							showAddrPropList.set(i, 1);
						} else {
							showAddrPropList.set(i, 0xff);
						}
					} else
						showAddrPropList.set(i, 1);
				}
			}
			// Log.d(TAG, "onTrends...........");
			SKSceneManage.getInstance().onRefresh(item);
		}
	};

	/**
	 * 历史数据读取
	 */
	private int start_x;// 等待界面X坐标
	private int start_y;// 等待界面Y坐标
	private float nLastTimeForDB=0;
	SkGlobalBackThread.IReadHistoryCallback iReadCallback = new SkGlobalBackThread.IReadHistoryCallback() {

		@Override
		public void begainReadHistory() {

			// Log.d(TAG, "begainReadHistory...........");
			if (bInitShow) {
				// 获取开始点和最后一点的采集时间
				if (mTrendsInfo.getnTimeRange() == TIMERANGE_TYPE.RECENT_BEGIN) {
					long[] time = DataCollect.getInstance().getLastTime(mTrendsInfo.getnGroupNum());
					if (time[0] <= 0) {
						nStartTime = System.currentTimeMillis();
						nLastTime = nStartTime + mTrendsInfo.getnRecentMinute()* 60 * 1000;
						nLastTimeForDB=0;
						// RealTimeRefresh_Flag = true;
					} else {
						nStartTime = time[0];
						nLastTime = time[1];
						nLastTimeForDB=nLastTime;
						// mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);
					}

					if (end_date == null) {
						end_date = Calendar.getInstance();
					}

					if (start_date == null) {
						start_date = Calendar.getInstance();
					}

					end_date.setTimeInMillis(nLastTime);
					start_date.setTimeInMillis(end_date.getTimeInMillis()- mTrendsInfo.getnRecentMinute() * 60 * 1000);

					if (start_date.getTimeInMillis() < nStartTime) {
						nLastTime = nStartTime + mTrendsInfo.getnRecentMinute()* 60 * 1000;
						start_date.setTimeInMillis(nStartTime);
						end_date.setTimeInMillis(nLastTime);
					}
				}
				// Log.d(TAG, "bInitShow="+bInitShow);
			}

			nUnitLenOfTime = (float) nCurveWd/ (end_date.getTimeInMillis() - start_date.getTimeInMillis());
			
			Vector<FreeLineItem> SendfreeLineItemGroups = new Vector<FreeLineItem>();
			FreeLineItem[] SendfreeLineItemList = null;

			SendfreeLineItemList = new FreeLineItem[mTrendsInfo.getnChannelNum()];

			for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
				Vector<PointF> m_pointList = new Vector<PointF>();
				SendfreeLineItemList[i] = new FreeLineItem(m_pointList,
						END_ARROW_TYPE.STYLE_NONE);
				SendfreeLineItemGroups.add(SendfreeLineItemList[i]);
			}

			if (Zoom_Oprate == OPRATE.OPRATE_MOVE_Y) {
				Move_YData();
			} else {
				start_list.C_Date = start_date;
				end_list.C_Date = end_date;
				start_x = nCurveX + nCurveWd / 2 - 60;

				if (start_x < 0) {
					start_x = 0;
				}

				start_y = nCurveY + nCurveHt / 2 - 60;
				if (start_y < 0) {
					start_y = 0;
				}
			
				handler.sendEmptyMessage(DIALOG_SHOW);
				HandleHistoryData(start_list, end_list);
				if (TimeRange_Flag == true) {
					// Log.d(TAG, "TimeRange_Flag="+TimeRange_Flag);
					start_date = start_list.C_Date;
					end_date = Calendar.getInstance();
				}
				handler.sendEmptyMessage(DIALOG_HIDE);// 隐藏等待界面
			}

			GetCurFreeLine(SendfreeLineItemGroups);
			handler.obtainMessage(DIALOG_SEND, SendfreeLineItemGroups)
					.sendToTarget();
			bMoveFlag = false;
			mTimeInfo.isLoading = false;
			bInitShow = false;
		}
	};
	

	// 曲线的计算代码
	@SuppressWarnings("unchecked")
	private void GetCurFreeLine(Vector<FreeLineItem> items) {
		FreeLineItem freeLineItem1;
		Vector<PointF> m_pointList1;

		FreeLineItem freeLineItem2;
		Vector<PointF> m_pointList2;
		// PointF test_point;

		try{
			for (short j = 0; j < mTrendsInfo.getnChannelNum(); j++) {
				freeLineItem1 = freeLineItemGroups.get(j);
				m_pointList1 = freeLineItem1.getM_fpointList();

				freeLineItem2 = items.get(j);
				m_pointList2 = freeLineItem2.getM_fpointList();
				m_pointList2.clear();
				PointF test_point = new PointF();

				if (null == m_pointList1 || m_pointList1.isEmpty()) {
					continue;
				}
				// 克隆数组
				// m_pointList2=(Vector<PointF>)m_pointList1.clone();
				// Log.d(TAG, "m_pointList2:"+m_pointList2.size());

				for (short i = 0; i < m_pointList1.size(); i++) {
					PointF start = new PointF();
					test_point = m_pointList1.get(i);
					start.set((int)test_point.x, (int)test_point.y);
					m_pointList2.add(start);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "SKHistoryTrends GetCurFreeLine() error !!! ");
		}
	
	}

	/**
	 * 线程回调处理函数，计算实时曲线曲线变化值
	 * @param nValueList-数据点集合
	 * @param 采集频率 -nRealCollectRate 单位200ms
	 */
	public void HandleRealCurveData(Vector<String> nValueList) {

		RealTimeRefresh_Flag = false;
		if (null == nValueList || nValueList.isEmpty()) {
			return;
		}
		realCurveCompute(nValueList);
	}

	/**
	 * 实时曲线点的计算
	 */
	private boolean bMovePoint;// 移动点

	private synchronized void realCurveCompute(Vector<String> nValueList) {
		String value = "";
		double convet_value = 0, error_num;
		long time = System.currentTimeMillis();
		bMovePoint = false;

		if (end_date.getTimeInMillis() - start_date.getTimeInMillis()==0) {
			Log.e(TAG, "HistoryTrends no init .....");
			return;
		}
		//计算每毫米所占长度
		nUnitLenOfTime = (float) nCurveWd/ (end_date.getTimeInMillis() - start_date.getTimeInMillis());
		
		for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
			int channel = channelGroups.get(i).getnNumOfChannel();
			if (channel < 0) {
				break;
			}

			if (channel < nValueList.size()) {
				value = nValueList.get(channel);
			} else {
				value = "0";
			}

			try {
				//乘以缩放倍数
				convet_value = Double.valueOf(value)/Current_Zoom.y_rate;
				error_num = 0;
			} catch (NumberFormatException Event) {
				System.err.println("convet_value error");
				error_num = 1;
			}

			if (error_num == 1) {
				point_y = (float) (nCurveY + nCurveHt);
			} else {
				ConvertDataToPoint(convet_value, 0);
			}

			moveRealCurvePoint(i, time);
		}

		if (bMovePoint) {
			//Log.d(TAG, "bMovePoint = "+bMovePoint);
			int nScroll = mTrendsInfo.getnScrollSample() * 2;
			if (nScroll > nCurveWd) {
				nScroll = nCurveWd;
			}

			long len = end_date.getTimeInMillis()- start_date.getTimeInMillis();
			int times = (int) (nScroll / nUnitLenOfTime);
			end_date.setTimeInMillis(time + times);
			start_date.setTimeInMillis(end_date.getTimeInMillis() - len);
		}

	}

	/**
	 * 实时曲线点的移动
	 * @param index-通道号
	 */
	private void moveRealCurvePoint(int index, long time) {
		/**
		 * y | | | |____________ x
		 * 
		 * X轴某一点，在Y轴上最多对应2个点，一个最低，一个最高, 其他可以忽略，两个点，便可以连成一条线
		 */

		FreeLineItem freeLineItem;
		Vector<PointF> m_pointList;
		PointF start = new PointF();

		freeLineItem = freeLineItemGroups.get(index); // 第几通道的曲线
		m_pointList = freeLineItem.getM_fpointList();

		if (end_date.getTimeInMillis() > start_date.getTimeInMillis()) {
			nUnitLenOfTime = (float) nCurveWd/ (end_date.getTimeInMillis() - start_date.getTimeInMillis());
		}

		if (nUnitLenOfTime <= 0) {
			return;
		}

		float nMoveLen =(time - start_date.getTimeInMillis()) * nUnitLenOfTime;
		point_x = nCurveX + nMoveLen;
		//Log.d(TAG, "time = "+time +",s = "+start_date.getTimeInMillis()+", t ="+(time - start_date.getTimeInMillis()));
		if (point_x >= (nCurveX + nCurveWd)) {

			// 删除,滚动样本数*2，减少移动的次数
			int nScroll = mTrendsInfo.getnScrollSample() * 2;
			if (nScroll > nCurveWd) {
				nScroll = nCurveWd;
			}

			if (point_x > (nCurveX + nCurveWd)) {
				nScroll = (int) (nScroll + point_x - nCurveX - nCurveWd);
			}

			bMovePoint = true;
			for (int i = 0; i < m_pointList.size(); i++) {
				float nX = m_pointList.get(i).x;
				if (nX - nScroll < nCurveX) {
					// 删除
					m_pointList.remove(i);
					i--;
				} else {
					m_pointList.get(i).x = nX - nScroll;
				}
			}

			start.set(point_x - nScroll, point_y);
			//Log.d(TAG, "reset x = "+(point_x - nScroll));
		} else {
			start.set(point_x, point_y);
			//Log.d(TAG, "set x = "+point_x);
		}

		// 最后一点X坐标，和现在新产生的X坐标比较，如果相同，则比较，Y轴上的点,
		// Y轴上最多留2个点,最大和最小，其他点忽略
		if (m_pointList.size() == 0) {
			//Log.d(TAG, "size = 0 ...... ");
			m_pointList.add(start);
			RealTimeRefresh_Flag = true;
			
		} else {
			int i = m_pointList.size() - 1;
			//Log.d(TAG, " x="+m_pointList.get(i).x+",px="+point_x);
			if (m_pointList.get(i).x == point_x) {
				// 相等，则比较Y轴上的点
				if (m_pointList.size() > 1) {
					int size = m_pointList.size();

					// 相同点的集合
					ArrayList<PointF> list = new ArrayList<PointF>();
					for (int j = size - 3; j < size; j++) {
						if (j > -1) {
							if (point_x == m_pointList.get(j).x) {
								list.add(m_pointList.get(j));
							}
						}
					}
					switch (list.size()) {
					case 1:
						if (list.get(0).y != point_y) {
							m_pointList.add(start);
							RealTimeRefresh_Flag = true;
						}
						break;
					case 2:
						if (list.get(0).y > list.get(1).y) {
							if (point_y > list.get(0).y) {
								list.get(0).y = point_y;
								RealTimeRefresh_Flag = true;
							}
							if (point_y < list.get(1).y) {
								list.get(1).y = point_y;
								RealTimeRefresh_Flag = true;
							}
						} else {
							if (point_y > list.get(1).y) {
								list.get(1).y = point_y;
								RealTimeRefresh_Flag = true;
							}
							if (point_y < list.get(0).y) {
								list.get(0).y = point_y;
								RealTimeRefresh_Flag = true;
							}
						}
						break;
					}

				} else {
					// 总共一个点
					float y1 = m_pointList.get(i).y;
					if (point_y != y1) {
						m_pointList.add(start);
						RealTimeRefresh_Flag = true;
					}

				}
			} else if (point_x > m_pointList.get(i).x) {
				// 大于，则添加新增点
				m_pointList.add(start);
				RealTimeRefresh_Flag = true;
			}
		}
	}


	/**
	 * 数据群组计算
	 */
	private void HandleDataGroup(Vector<Vector<String>> groupdata) {

		FreeLineItem freeLineItem;
		Vector<PointF> m_pointList;
		Vector<String> nValueList;
		String value;
		int nLen;
		float newx, newy;
		double convet_value = 0;
		int error_num;
		if (null == groupdata || groupdata.isEmpty()) {
			return;
		}

		for (short j = 0; j < groupdata.size(); j++) {
			nValueList = groupdata.get(j);
			if (null == nValueList || nValueList.isEmpty()) {
				continue;
			}
			nLen = nValueList.size();
			if (nLen > Real_DataSample) {
				nLen = Real_DataSample;
			}

			freeLineItem = freeLineItemGroups.get(j); // 第几通道的曲线
			m_pointList = freeLineItem.getM_fpointList();
			m_pointList.clear();

			for (short i = 0; i < nLen; i++) {
				PointF start = new PointF();
				try {
					value = nValueList.get(i);
					if (value == null)
						continue;
					
					convet_value = Double.valueOf(value);
					error_num = 0;
				} catch (Exception Event) {
					System.err.println("convet_value error");
					error_num = 1;
				}
				if (error_num == 1)
					point_y = (float) (nCurveY + nCurveHt);
				else
					ConvertDataToPoint(convet_value, 0);
				newy = point_y;
				newx = (float) (nCurveX + i * Sample_Scale);
				start.set(newx, newy);
				m_pointList.add(start);
			}
		}
		// GetBakFreeLine();
	}

	/**
	 * 计算历史曲线数据
	 */
	private float nCurTimeX;//当前时间所处位置
	public void HandleHistoryData(TrendCalendar start_list,
			TrendCalendar end_list) {
		Calendar start_date = start_list.C_Date;
		Calendar end_date = end_list.C_Date;
		String oldest_date = DateStringUtil.getDateString(
				DATE_FORMAT.YYYYMMDD_ACROSS, start_date.get(Calendar.YEAR),
				start_date.get(Calendar.MONTH),
				start_date.get(Calendar.DAY_OF_MONTH));
		String oldest_time = DateStringUtil.getTimeString(
				TIME_FORMAT.HHMMSS_COLON, start_date.get(Calendar.HOUR_OF_DAY),
				start_date.get(Calendar.MINUTE), 0);
		String oldest_null = " ";
		String oldest_s = oldest_date + oldest_null + oldest_time;

		String recent_date = DateStringUtil.getDateString(
				DATE_FORMAT.YYYYMMDD_ACROSS, end_date.get(Calendar.YEAR),
				end_date.get(Calendar.MONTH),
				end_date.get(Calendar.DAY_OF_MONTH));
		String recent_time = DateStringUtil.getTimeString(
				TIME_FORMAT.HHMMSS_COLON, end_date.get(Calendar.HOUR_OF_DAY),
				end_date.get(Calendar.MINUTE), 0);
		String recent_null = " ";
		String recent_s = recent_date + recent_null + recent_time;

		int channel;

		TimeRange_Flag = false;
		nChannelList.clear();
		for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
			channel = channelGroups.get(i).getnNumOfChannel();
			// System.out.println("channel "+channel);
			nChannelList.add(channel);
		}

		if (mTrendsInfo.getnTimeRange().equals(TIMERANGE_TYPE.STORE_BEGIN))// TIMERANGE_TYPE.RECENT_BEGIN)
		{
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			historydata = DataCollect.getInstance().getGroupCollectData(
					mTrendsInfo.getnGroupNum(), Real_DataSample, nChannelList,
					Collect_Value);
			if (Collect_Value.sFirstTimeStr == null)
				return;

			try {
				date = format.parse(Collect_Value.sFirstTimeStr);
				start_date.set(Calendar.DAY_OF_MONTH, date.getDate());
				start_date.set(Calendar.MONTH, date.getMonth());
				start_date.set(Calendar.YEAR, date.getYear() + 1900);
				start_date.set(Calendar.HOUR_OF_DAY, date.getHours());
				start_date.set(Calendar.MINUTE, date.getMinutes());
				start_list.C_Date = start_date;
				// end_list.C_Date=Calendar.getInstance();
				TimeRange_Flag = true;
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("can not get start time");
				e.printStackTrace();
			}

		} else{
			historydata = DataCollect.getInstance().getGroupCollectDataByTime(
					mTrendsInfo.getnGroupNum(), Real_DataSample, nChannelList,
					oldest_s, recent_s, Collect_Value);
		}

		// System.out.println("mTrendsInfo.getnChannelNum() "+mTrendsInfo.getnChannelNum());

		//计算当前时间的位置
		float nMoveLen =(System.currentTimeMillis() - start_date.getTimeInMillis()) * nUnitLenOfTime;
		nCurTimeX =nCurveX+nMoveLen;
		//Log.d(TAG, "nCurTimeX = "+nCurTimeX);
		for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
			ConvertHistoryDataToPoint(i);
		}
	}

	private void Move_YData() {
		nCurTimeX=0;
		for (short i = 0; i < mTrendsInfo.getnChannelNum(); i++) {
			ConvertHistoryDataToPoint(i);
		}
	}

	/**
	 * 将点的值转换成实际坐标
	 */
	private void ConvertDataToPoint(Double double1, int type) {
		double convertdata; // 实际在曲线中的点
		double Ylen;
		float showMax = mTrendsInfo.getnDisplayMax(); // 显示最大值
		float showMin = mTrendsInfo.getnDisplayMin(); // 显示最小值

		double real_max, real_min, real_len;

		Ylen = showMax - showMin; // Y轴长度
		convertdata = double1;

		if (type == 1) {
			if (Current_Zoom == null)
				return;
			real_len = Ylen * Current_Zoom.y_rate;
			real_min = Ylen * Current_Zoom.y_start + showMin;
			real_max = real_min + real_len;

			if (convertdata > real_max) {
				convertdata = real_max;
			}
			if (convertdata < real_min) {
				convertdata = real_min;
			}
			point_y = (float) (nCurveY + nCurveHt - (convertdata - real_min)
					/ real_len * nCurveHt);
		} else {
			if (convertdata > showMax) {
				convertdata = showMax;
			}
			if (convertdata < showMin) {
				convertdata = showMin;
			}
			point_y = (float) (nCurveY + nCurveHt - (convertdata - showMin)
					/ Ylen * nCurveHt);
		}
	}

	private void ConvertHistoryDataToPoint(short channelnum) {
		FreeLineItem freeLineItem;
		freeLineItem = freeLineItemGroups.get(channelnum); // 第几通道的曲线
		Vector<PointF> mPoint = freeLineItem.getM_fpointList(); // 曲线的点
		float x_aix;
		int size;
		int error_num;
		Vector<String> nValueList;
		String value;

		// System.err.println("historydata"+historydata);
		mPoint.clear();
		if (null == historydata || historydata.isEmpty()){
			return;
		}

		size = historydata.size();
		if (size == 0){
			return;
		}
		
		float nDiffer=0;
		if (nCurTimeX>0) {
			//获取最后一点坐标，并且和当前最后一点比较，然后退出时，比当前的大，则向左平移
			float last=(size-1)*Sample_Scale+nCurveX;
			//Log.d(TAG, "last = "+last+", nCurTimeX = "+nCurTimeX+",size="+(size-1));
			if (last-nCurTimeX>0) {
				nDiffer=last-nCurTimeX;
			}
		}
		
		for (short i = 0; i < size; i++) {
			PointF start = new PointF();
			nValueList = historydata.get(i);
			if (null == nValueList || nValueList.isEmpty())
				continue;
			value = nValueList.get(channelnum);
			if (value == null){
				continue;
			}
				
			try {
				current_hisdata = Double.valueOf(value);
				error_num = 0;
			} catch (NumberFormatException Event) {
				System.err.println("convet_value error");
				error_num = 1;
			}
			if (error_num == 1){
				point_y = (float) (nCurveY + nCurveHt);
			}else{
				ConvertDataToPoint(current_hisdata, 1);
			}
			x_aix = (float) (nCurveX + i * Sample_Scale); // x轴坐标
			x_aix=x_aix-nDiffer;
			if (x_aix<nCurveX) {
				x_aix=nCurveX;
			}
			//Log.d(TAG, "x_aix = "+x_aix +",i="+i);
			start.set(x_aix, point_y);
			mPoint.add(start);
		}
	}

	/**
	 * 获取背景图片
	 */
	private Bitmap getBgBitmap(int width, int height) {
		if (width < 1) {
			width = 1;
		}
		if (height < 1) {
			height = 1;
		}
		Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas bCanvas = new Canvas(mBitmap);
		Paint paint = new Paint();
		DrawFrameAndTag(paint, bCanvas);
		return mBitmap;
	}

	@Override
	public void realseMemeory() {

		DataCollect.getInstance().destoryDataCollectCallback(call);
		DataCollect.getInstance().destoryDataCollectCallback(groupcall);
		SKTimer.getInstance().getBinder().onDestroy(callback);
		mTrendsInfo.setnTimeRange(mTrendsInfo.getnOldTimerange());
		// SKTrendsThread.getInstance().getBinder()
		// .onDestroy(tCallback, sTaskName);

		sTaskName = "";
		if (popup_flag == 1) {
			handler.sendEmptyMessage(DIALOG_HIDE);// 隐藏等待界面
		}

		if (TrendsCall_Flag == true) {
			// 注册才需要注销
			TrendsWatch.getInstance().getBinder().onDestroy(TrendsCall);
			TrendsWatch.getInstance().stop();
		}
		TrendsCall_Flag = false;
		mZoomList = null;
		Current_Zoom = null;
	}

	@Override
	public void getDataFromDatabase() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	private short getCurTextLengthInPixels(Paint this_paint, String this_text) {
		return (short) this_paint.measureText(this_text);
	}

	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) (Math.ceil(fm.descent - fm.ascent));
	}

	/**
	 * 数据群组绘制
	 */
	private void DrawDataGroup(Paint paint, Canvas canvas) {
		short X_TagMainWd;

		double showMax = mTrendsInfo.getnDataSample() - 1; // 显示最大值
		double showMin = 0;// mTrendsInfo.getnDisplayMin(); // 显示最小值
		double y_len;
		double y_scale, Max_Data;
		short nCur_TimeX, nCur_TimeY, nCur_TimeWd, nCur_TimeHt, RealMax_Data;

		y_len = showMax - showMin;
		X_TagMainWd = (short) (nCurveWd / mTrendsInfo.getnHorMajorScale());

		// Log.d(TAG, "y_len:"+y_len);

		for (short i = 0; i <= mTrendsInfo.getnHorMajorScale(); i++) {
			y_scale = (double) (i * y_len / mTrendsInfo.getnHorMajorScale());
			Max_Data = Math.rint(y_scale);
			RealMax_Data = (short) Max_Data;
			StaticTextModel cur_text = new StaticTextModel();
			String max_s;

			max_s = String.format("%d", RealMax_Data);

			paint.reset();
			paint.setTextSize(mTrendsInfo.getnFontSize());

			nCur_TimeX = (short) (nCurveX + X_TagMainWd * i - getCurTextLengthInPixels(
					paint, max_s) / 2);
			nCur_TimeY = (short) (nCurveY + nCurveHt + XLine_Tag + 1);
			nCur_TimeWd = getCurTextLengthInPixels(paint, "   " + max_s + "   ");
			nCur_TimeHt = (short) (getFontHeight(paint));

			cur_text.setM_sTextStr(max_s);
			cur_text.setM_nFontColor(mTrendsInfo.getnMarkColor());
			cur_text.setM_nFontSize(mTrendsInfo.getnFontSize());
			cur_text.setM_backColorPadding(0);
			cur_text.setStartX(nCur_TimeX);
			cur_text.setStartY(nCur_TimeY);
			cur_text.setRectHeight(nCur_TimeHt);
			cur_text.setLineColor(Color.TRANSPARENT);
			cur_text.setLineWidth(0);
			cur_text.setRectWidth(nCur_TimeWd);
			cur_text.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
			TextItem cur_textItem = new TextItem(cur_text);
			// 设置文本边框的画笔属性
			cur_textItem.initRectBoderPaint();
			// 设置文本内容的画笔属性
			cur_textItem.initTextPaint();
			// 设置文本背景的画笔属性
			cur_textItem.initRectPaint();
			cur_textItem.draw(canvas);
		}
	}

	/**
	 * X轴开始结束时间显示
	 */
	private void DrawTime(Paint paint, Canvas canvas) {

		// 开始时间
		long Timelen;
		if ((mTrendsInfo.getnCurveType() == CURVE_TYPE.REALTIME_CURVE)
				|| (mTrendsInfo.getnCurveType() == CURVE_TYPE.HISTORY_CURVE)
				&& mTrendsInfo.getnTimeRange().equals(
						TIMERANGE_TYPE.RECENT_BEGIN)) {
			if (nRealTime_FirstFlag == false) // first show
			{

				if (start_date == null) {
					start_date = Calendar.getInstance();
				}
				//getTime(start_date.getTimeInMillis());
				Timelen = (long) (start_date.getTimeInMillis() + mTrendsInfo
						.getnRecentMinute() * 60 * 1000);
				end_date.setTimeInMillis(Timelen);
				nRealTime_FirstFlag = true;
			}
		}
		
		//不管有没有显示刻度，start_date 和 end_date 都必须赋值
		if (!mTrendsInfo.isbMainHor()) {
			return;
		}
		
		if (mTrendsInfo.getbXmark() == false) {
			// 不显示时间
			return;
		}

		long start_mil, end_mil, bt_mil, cur_mil;
		Calendar cur_date;
		short nCur_TimeX = 0, nCur_TimeY = 0, nCur_TimeWd = 0, nCur_TimeHt = 0, nCur_DateX, nCur_DateY, nCur_DateWd, nCur_DateHt;
		short X_TagMainWd;

		start_mil = start_date.getTimeInMillis();
		end_mil = end_date.getTimeInMillis();
		bt_mil = end_mil - start_mil;
		cur_date = Calendar.getInstance();
		X_TagMainWd = (short) (nCurveWd / mTrendsInfo.getnHorMajorScale());

		for (short i = 0; i <= mTrendsInfo.getnHorMajorScale(); i++) {
			StaticTextModel cur_text = new StaticTextModel();

			cur_mil = i * bt_mil / mTrendsInfo.getnHorMajorScale() + start_mil;
			cur_date.setTimeInMillis(cur_mil);
			String cur_date_s = DateStringUtil.getDateString(
					mTrendsInfo.getnDate(), cur_date.get(Calendar.YEAR),
					cur_date.get(Calendar.MONTH), cur_date.get(Calendar.DATE));
			String cur_time_s = DateStringUtil.getTimeString(
					mTrendsInfo.getnTime(), cur_date.get(Calendar.HOUR_OF_DAY),
					cur_date.get(Calendar.MINUTE),
					cur_date.get(Calendar.SECOND));

			paint.reset();
			paint.setTextSize(mTrendsInfo.getnFontSize());

			if (i == 0) {
				nStart_TimeX = (short) (nCurveX + X_TagMainWd * i - getCurTextLengthInPixels(
						paint, cur_time_s) / 2);
				nStart_TimeY = (short) (nCurveY + nCurveHt + XLine_Tag + 1);
				nStart_TimeWd = (short) (getCurTextLengthInPixels(paint,
						cur_time_s));
				nStart_TimeHt = (short) (getFontHeight(paint));
				nStart_DateX = (short) (nCurveX - getCurTextLengthInPixels(
						paint, cur_date_s) / 2);
				nStart_DateY = (short) (nCurveY + nCurveHt + nStart_TimeHt
						+ XLine_Tag + 1);
				nStart_DateWd = getCurTextLengthInPixels(paint, cur_date_s);
				nStart_DateHt = (short) (getFontHeight(paint));
			} else if (i == mTrendsInfo.getnHorMajorScale()) {
				nEnd_TimeX = (short) (nCurveX + X_TagMainWd * i - getCurTextLengthInPixels(
						paint, cur_time_s) / 2);
				nEnd_TimeY = (short) (nCurveY + nCurveHt + XLine_Tag + 1);
				nEnd_TimeWd = (short) (getCurTextLengthInPixels(paint,
						cur_time_s));
				nEnd_TimeHt = (short) (getFontHeight(paint));
				if ((nEnd_TimeX + nEnd_TimeWd) > (mTrendsInfo.getnLp() + mTrendsInfo
						.getnWidth()))
					nEnd_TimeX = (short) (mTrendsInfo.getnLp()
							+ mTrendsInfo.getnWidth() - nEnd_TimeWd);

				nEnd_DateX = (short) (nCurveX + X_TagMainWd * i - getCurTextLengthInPixels(
						paint, cur_date_s) / 2);
				nEnd_DateY = (short) (nCurveY + nCurveHt + nEnd_TimeHt
						+ XLine_Tag + 1);
				nEnd_DateWd = getCurTextLengthInPixels(paint, cur_date_s);
				nEnd_DateHt = (short) (getFontHeight(paint));
				if ((nEnd_DateX + nEnd_DateWd) > (mTrendsInfo.getnLp() + mTrendsInfo
						.getnWidth()))
					nEnd_DateX = (short) (mTrendsInfo.getnLp()
							+ mTrendsInfo.getnWidth() - nEnd_DateWd);

			}

			nCur_TimeX = (short) (nCurveX + X_TagMainWd * i - getCurTextLengthInPixels(
					paint, cur_time_s) / 2);
			if (i == mTrendsInfo.getnHorMajorScale()) {
				nCur_TimeX = nEnd_TimeX;
			}
			nCur_TimeY = (short) (nCurveY + nCurveHt + XLine_Tag + 1);
			nCur_TimeWd = getCurTextLengthInPixels(paint, "   " + cur_time_s
					+ "   ");
			nCur_TimeHt = (short) (getFontHeight(paint));
			// Log.d(TAG,
			// "nCur_TimeX:"+nCur_TimeX+",nCur_TimeY:"+nCur_TimeY+",nCur_TimeWd:"+nCur_TimeWd+",nCur_TimeHt:"+nCur_TimeHt);
			if ((nCur_TimeX + nCur_TimeWd) > (mTrendsInfo.getnLp() + mTrendsInfo
					.getnWidth())) {
				nCur_TimeWd = (short) (mTrendsInfo.getnLp()
						+ mTrendsInfo.getnWidth() - nCur_TimeX);
			}
			if ((nCur_TimeY + nCur_TimeHt) > (mTrendsInfo.getnTp() + mTrendsInfo
					.getnHeight())) {
				nCur_TimeHt = (short) (mTrendsInfo.getnTp()
						+ mTrendsInfo.getnHeight() - nCur_TimeY);
			}

			if (mTrendsInfo.isbShowTime()) {
				// 显示时间
				cur_text.setM_sTextStr(cur_time_s);
				cur_text.setM_nFontColor(mTrendsInfo.getnMarkColor());
				cur_text.setM_nFontSize(mTrendsInfo.getnFontSize());
				cur_text.setM_backColorPadding(0);
				cur_text.setStartX(nCur_TimeX);
				cur_text.setStartY(nCur_TimeY);
				cur_text.setRectHeight(nCur_TimeHt);
				cur_text.setLineColor(Color.TRANSPARENT);
				cur_text.setLineWidth(0);
				cur_text.setRectWidth(nCur_TimeWd);
				cur_text.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
				TextItem cur_textItem = new TextItem(cur_text);
				// 设置文本边框的画笔属性
				cur_textItem.initRectBoderPaint();
				// 设置文本内容的画笔属性
				cur_textItem.initTextPaint();
				// 设置文本背景的画笔属性
				cur_textItem.initRectPaint();
				cur_textItem.draw(canvas);
			}

			if (mTrendsInfo.getnCurveType() == CURVE_TYPE.HISTORY_CURVE) {
				if (mTrendsInfo.isbShowData()) {
					// 显示日期
					StaticTextModel cur_datetext = new StaticTextModel();
					nCur_DateX = (short) (nCurveX + X_TagMainWd * i - getCurTextLengthInPixels(
							paint, cur_date_s) / 2);

					if (i == mTrendsInfo.getnHorMajorScale())
						nCur_DateX = nEnd_DateX;
					nCur_DateY = (short) (nCurveY + nCurveHt + nCur_TimeHt
							+ XLine_Tag + 1);
					nCur_DateWd = getCurTextLengthInPixels(paint, "    "
							+ cur_date_s + "    ");
					nCur_DateHt = (short) (getFontHeight(paint));

					if ((nCur_DateX + nCur_DateWd) > (mTrendsInfo.getnLp() + mTrendsInfo
							.getnWidth())) {
						nCur_DateWd = (short) (mTrendsInfo.getnLp()
								+ mTrendsInfo.getnWidth() - nCur_DateX);
					}
					if ((nCur_DateY + nCur_DateHt) > (mTrendsInfo.getnTp() + mTrendsInfo
							.getnHeight())) {
						nCur_DateHt = (short) (mTrendsInfo.getnTp()
								+ mTrendsInfo.getnHeight() - nCur_DateY);
					}

					cur_datetext.setM_sTextStr(cur_date_s);
					cur_datetext.setM_nFontColor(mTrendsInfo.getnMarkColor());
					cur_datetext.setM_nFontSize(mTrendsInfo.getnFontSize());
					cur_datetext.setM_backColorPadding(0);
					cur_datetext.setStartX(nCur_DateX);
					cur_datetext.setStartY(nCur_DateY);
					cur_datetext.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
					cur_datetext.setRectHeight(nCur_DateHt);
					cur_datetext.setLineColor(Color.TRANSPARENT);
					cur_datetext.setLineWidth(0);
					cur_datetext.setRectWidth(nCur_DateWd);
					TextItem cur_datetextItem = new TextItem(cur_datetext);
					// 设置文本边框的画笔属性
					cur_datetextItem.initRectBoderPaint();
					// 设置文本背景的画笔属性
					cur_datetextItem.initRectPaint();
					// 设置文本内容的画笔属性
					cur_datetextItem.initTextPaint();
					cur_datetextItem.draw(canvas);
				}

			}
		}
	}

	/**
	 * Y轴刻度
	 */
	private void DrawY_Scale(Paint paint, Canvas canvas) {
		double showMax = mTrendsInfo.getnDisplayMax(); // 显示最大值
		double showMin = mTrendsInfo.getnDisplayMin(); // 显示最小值
		double y_scale, y_len;
		short Start_X, Start_Y, Wd, Ht, fontht;
		float Y_TagMainHt;
		double real_min, real_len;

		Y_TagMainHt = (float) nCurveHt / mTrendsInfo.getnVertMajorScale();
		y_len = showMax - showMin;
		if (Current_Zoom == null)
			return;
		real_len = y_len * Current_Zoom.y_rate;
		real_min = y_len * Current_Zoom.y_start + showMin;

		// Log.d(TAG,
		// "......y_len="+y_len+",showMax="+showMax+",showMin="+showMin);

		for (short i = 0; i <= mTrendsInfo.getnVertMajorScale(); i++) {
			y_scale = (double) (i * real_len / mTrendsInfo.getnVertMajorScale() + real_min);
			StaticTextModel max_text = new StaticTextModel();
			String max_s;
			max_s = String.format("%.2f", y_scale);// format.format(y_scale);
			paint.setTextSize(mTrendsInfo.getnFontSize());
			Wd = getCurTextLengthInPixels(paint, "    " + max_s + "    ");
			Ht = (short) (getFontHeight(paint));
			Start_X = (short) (nCurveX - YLine_Tag - Wd);
			fontht = (short) (Ht * 0.6);
			Start_Y = (short) (nCurveY + nCurveHt - Y_TagMainHt * i - fontht);

			if (Start_X < mTrendsInfo.getnLp()) {
				Wd = (short) (Wd - (mTrendsInfo.getnLp() - Start_X));
				Start_X = mTrendsInfo.getnLp();
			}
			if ((Start_Y + Ht) > (mTrendsInfo.getnTp() + mTrendsInfo
					.getnHeight())) {
				Ht = (short) (mTrendsInfo.getnTp() + mTrendsInfo.getnHeight() - Start_Y);
			}
			max_text.setM_sTextStr(max_s);
			max_text.setM_eTextAlign(TEXT_PIC_ALIGN.CENTER);
			max_text.setM_nFontColor(mTrendsInfo.getnMarkColor());
			max_text.setM_nFontSize(mTrendsInfo.getnFontSize());
			max_text.setM_backColorPadding(0);
			max_text.setStartX(Start_X);
			max_text.setStartY(Start_Y);
			max_text.setRectHeight(Ht);
			max_text.setRectWidth(Wd);
			max_text.setLineColor(Color.TRANSPARENT);
			max_text.setLineWidth(0);
			TextItem max_textItem = new TextItem(max_text);
			// 设置文本边框的画笔属性
			max_textItem.initRectBoderPaint();
			// 设置文本背景的画笔属性
			max_textItem.initRectPaint();
			// 设置文本内容的画笔属性
			max_textItem.initTextPaint();
			max_textItem.draw(canvas);
		}
		// isYTagUpdate=false;
	}

	/**
	 * 画框架
	 */
	private void DrawFrameAndTag(Paint paint, Canvas canvas) {
		TrendsRect.left = 0;// mTrendsInfo.getnLp(); //控件外框
		TrendsRect.right = mTrendsInfo.getnWidth();
		TrendsRect.top = 0;// mTrendsInfo.getnTp();
		TrendsRect.bottom = mTrendsInfo.getnHeight();
		TrendsRectItem.setAlpha(0);
		TrendsRectItem.setLineAlpha(0);
		TrendsRectItem.setBackColor(Color.BLACK);
		TrendsRectItem.setLineWidth(2);
		TrendsRectItem.setLineColor(Color.WHITE);
		TrendsRectItem.setLineType(LINE_TYPE.SOLID_LINE);
		TrendsRectItem.setType(END_POINT_TYPE.FLAT_CAP);
		TrendsRectItem.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
		TrendsRectItem.draw(paint, canvas);
		TrendsCurveRect.left = mTrendsInfo.getnCurveX();// nCurveX; //曲线外框
		TrendsCurveRect.right = mTrendsInfo.getnCurveX() + nCurveWd;
		TrendsCurveRect.top = mTrendsInfo.getnCurveY();// nCurveY;
		TrendsCurveRect.bottom = mTrendsInfo.getnCurveY() + nCurveHt;
		TrendsCurveRectItem.setAlpha(255);
		TrendsCurveRectItem.setBackColor(mTrendsInfo.getnGraphColor());
		TrendsCurveRectItem.setLineColor(mTrendsInfo.getnBoradColor());
		TrendsCurveRectItem.setLineType(LINE_TYPE.SOLID_LINE);
		TrendsCurveRectItem.setType(END_POINT_TYPE.FLAT_CAP);
		TrendsCurveRectItem.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
		TrendsCurveRectItem.draw(paint, canvas);

		if (mTrendsInfo.isbMainHor()) {
			// X轴标尺
			DrawX_Tag(paint, canvas);
		}

		if (mTrendsInfo.isbMainVer()) {
			// Y轴标尺
			DrawY_Tag(paint, canvas);
		}

		DrawNet(paint, canvas); // 网格
	}

	/**
	 * 画线
	 */
	private void DrawMyLine(float Start_X, float Start_Y, float End_X,
			float End_Y, LINE_TYPE nLineType, int Color, Canvas canvas,
			Paint paint) {
		PointF start = new PointF();
		PointF end = new PointF();

		// paint.reset();
		start.set(Start_X, Start_Y);
		end.set(End_X, End_Y);
		Vector<PointF> line_pointList = new Vector<PointF>();
		line_pointList.add(start);
		line_pointList.add(end);
		FreeLineItem myLine = new FreeLineItem(line_pointList,
				END_ARROW_TYPE.STYLE_NONE);
		myLine.setLineColor(Color);
		myLine.setLineType(nLineType);
		myLine.setLineWidth(1);
		myLine.setAlpha(255);
		myLine.draw(paint, canvas);
		start = null;
		end = null;
		line_pointList = null;
		myLine = null;
	}

	/**
	 * X轴标尺
	 */
	private void DrawX_Tag(Paint paint, Canvas canvas) {
		float X_TagMainWd;
		float X_TagSubWd;
		float Line_X, Line_Y, Line_EndX, Line_EndY, newCurveX, newCurveY;

		newCurveX = mTrendsInfo.getnCurveX();
		newCurveY = mTrendsInfo.getnCurveY();
		X_TagMainWd = (float) nCurveWd / mTrendsInfo.getnHorMajorScale();
		if (mTrendsInfo.getbSelectHorMinor() == true)
			X_TagSubWd = (float) X_TagMainWd
					/ (mTrendsInfo.getnHorMinorScale());
		else
			X_TagSubWd = X_TagMainWd;

		Line_X = newCurveX;
		Line_Y = (float) newCurveY + nCurveHt;// +MIN_TAG);
		Line_EndX = (float) (newCurveX);
		Line_EndY = (float) newCurveY + nCurveHt + XLine_Tag;
		DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY, LINE_TYPE.SOLID_LINE,
				mTrendsInfo.getnScaleColor(), canvas, paint);

		for (short i = 0; i < mTrendsInfo.getnHorMajorScale(); i++) {
			Line_X = (float) newCurveX + X_TagMainWd * (i + 1);
			Line_Y = (float) newCurveY + nCurveHt;// +MIN_TAG);
			Line_EndX = (float) newCurveX + X_TagMainWd * (i + 1);
			Line_EndY = (float) newCurveY + nCurveHt + XLine_Tag;
			DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
					LINE_TYPE.SOLID_LINE, mTrendsInfo.getnScaleColor(), canvas,
					paint);
			if (mTrendsInfo.getbSelectHorMinor() == true) // 副标尺
			{
				for (short j = 0; j < mTrendsInfo.getnHorMinorScale() - 1; j++) {
					Line_X = (float) newCurveX + X_TagMainWd * i + X_TagSubWd
							* (j + 1);
					Line_Y = (float) newCurveY + nCurveHt;// +MIN_TAG);
					Line_EndX = (float) newCurveX + X_TagMainWd * i
							+ X_TagSubWd * (j + 1);
					Line_EndY = (float) newCurveY + nCurveHt + XScale_Tag;
					DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
							LINE_TYPE.SOLID_LINE, mTrendsInfo.getnScaleColor(),
							canvas, paint);
				}
			}
		}
	}

	/**
	 * Y轴标尺
	 */
	private void DrawY_Tag(Paint paint, Canvas canvas) {
		float Y_TagMainHt;
		float Y_TagSubHt;
		float Line_X, Line_Y, Line_EndX, Line_EndY, newCurveX, newCurveY;

		newCurveX = mTrendsInfo.getnCurveX();
		newCurveY = mTrendsInfo.getnCurveY();

		Y_TagMainHt = (float) nCurveHt / mTrendsInfo.getnVertMajorScale();
		if (mTrendsInfo.getbSelectVertMinor() == true) {
			Y_TagSubHt = (float) Y_TagMainHt
					/ (mTrendsInfo.getnVertMinorScale());
		} else {
			Y_TagSubHt = Y_TagMainHt;
		}

		Line_X = (float) newCurveX - YLine_Tag;
		Line_Y = newCurveY;
		Line_EndX = (float) (newCurveX);// -MIN_TAG);
		Line_EndY = newCurveY;
		DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY, LINE_TYPE.SOLID_LINE,
				mTrendsInfo.getnScaleColor(), canvas, paint);
		for (short i = 0; i < mTrendsInfo.getnVertMajorScale(); i++) {
			Line_X = (float) newCurveX - YLine_Tag;
			Line_Y = (float) newCurveY + Y_TagMainHt * (i + 1);
			Line_EndX = (float) (newCurveX);// -MIN_TAG);
			Line_EndY = (float) newCurveY + Y_TagMainHt * (i + 1);
			DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
					LINE_TYPE.SOLID_LINE, mTrendsInfo.getnScaleColor(), canvas,
					paint);
			// Log.d(TAG, "Line_Y 2  "+Line_Y);
			if (mTrendsInfo.getbSelectVertMinor() == true) // 副标尺
			{
				for (short j = 0; j < mTrendsInfo.getnVertMinorScale() - 1; j++) {
					Line_X = (float) newCurveX - XScale_Tag;
					Line_Y = (float) newCurveY + Y_TagMainHt * i + Y_TagSubHt
							* (j + 1);
					Line_EndX = (float) (newCurveX);// -MIN_TAG);
					Line_EndY = (float) newCurveY + Y_TagMainHt * i
							+ Y_TagSubHt * (j + 1);
					DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
							LINE_TYPE.SOLID_LINE, mTrendsInfo.getnScaleColor(),
							canvas, paint);
					// Log.d(TAG, "Line_Y 3  "+Line_Y);
				}
			}
		}
	}

	/**
	 * 画网格
	 */
	private void DrawNet(Paint paint, Canvas canvas) {
		float X_TagMainWd;
		float X_TagSubWd;
		float Y_TagMainHt;
		float Y_TagSubHt;
		float Line_X, Line_Y, Line_EndX, Line_EndY, newCurveX, newCurveY;
		// Paint newpaint=new Paint();
		newCurveX = mTrendsInfo.getnCurveX();
		newCurveY = mTrendsInfo.getnCurveY();

		if (mTrendsInfo.getbSelectNet() == false) // 不显示网格
			return;

		X_TagMainWd = (float) nCurveWd / mTrendsInfo.getnHorMajorScale(); // X轴网格
		if (mTrendsInfo.getbSelectHorMinor() == true)
			X_TagSubWd = (float) X_TagMainWd
					/ (mTrendsInfo.getnHorMinorScale());
		else
			X_TagSubWd = X_TagMainWd;

		for (short i = 0; i < mTrendsInfo.getnHorMajorScale(); i++) {

			if (mTrendsInfo.getbSelectHorMinor() == true) // 副标尺
			{
				for (short j = 0; j < mTrendsInfo.getnHorMinorScale() - 1; j++) {
					Line_X = (float) newCurveX + X_TagMainWd * i + X_TagSubWd
							* (j + 1);
					Line_Y = newCurveY;
					Line_EndX = (float) newCurveX + X_TagMainWd * i
							+ X_TagSubWd * (j + 1);
					Line_EndY = (float) newCurveY + nCurveHt;
					DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
							LINE_TYPE.DOT_LINE, mTrendsInfo.getnVertNetColor(),
							canvas, paint);
				}
			}
			// paint.reset();
			if (i != mTrendsInfo.getnHorMajorScale() - 1) {
				Line_X = (float) newCurveX + X_TagMainWd * (i + 1);
				Line_Y = newCurveY;
				Line_EndX = (float) newCurveX + X_TagMainWd * (i + 1);
				Line_EndY = (float) newCurveY + nCurveHt;
				DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
						LINE_TYPE.SOLID_LINE, mTrendsInfo.getnVertNetColor(),
						canvas, paint);
			}
		}

		Y_TagMainHt = (float) nCurveHt / mTrendsInfo.getnVertMajorScale(); // Y轴网格
		if (mTrendsInfo.getbSelectVertMinor() == true)
			Y_TagSubHt = (float) Y_TagMainHt
					/ (mTrendsInfo.getnVertMinorScale());
		else
			Y_TagSubHt = Y_TagMainHt;

		for (short i = 0; i < mTrendsInfo.getnVertMajorScale(); i++) {

			if (mTrendsInfo.getbSelectVertMinor() == true) // 副标尺
			{
				for (short j = 0; j < mTrendsInfo.getnVertMinorScale() - 1; j++) {
					Line_X = newCurveX;
					Line_Y = (float) newCurveY + Y_TagMainHt * i + Y_TagSubHt
							* (j + 1);
					Line_EndX = (float) newCurveX + nCurveWd;
					Line_EndY = (float) newCurveY + Y_TagMainHt * i
							+ Y_TagSubHt * (j + 1);
					DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
							LINE_TYPE.DOT_LINE, mTrendsInfo.getnHorNetColor(),
							canvas, paint);
				}
			}
			// paint.reset();
			if (i != mTrendsInfo.getnVertMajorScale() - 1) {
				Line_X = newCurveX;
				Line_Y = (float) newCurveY + Y_TagMainHt * (i + 1);
				Line_EndX = (float) newCurveX + nCurveWd;
				Line_EndY = (float) newCurveY + Y_TagMainHt * (i + 1);
				DrawMyLine(Line_X, Line_Y, Line_EndX, Line_EndY,
						LINE_TYPE.SOLID_LINE, mTrendsInfo.getnHorNetColor(),
						canvas, paint);
			}
		}
	}

	/**
	 * 界面曲线
	 */
	private void DrawCurver(Paint paint, Canvas canvas) {

		if (null == ShowfreeLineItemGroups || ShowfreeLineItemGroups.isEmpty()) {
			return;
		}

		// show_flag = 1;
		isDrawing = true;
		for (int i = 0; i < ShowfreeLineItemGroups.size(); i++) {
			if (showAddrPropList.get(i).equals(1)) {
				freeLineItem = ShowfreeLineItemGroups.get(i);
				freeLineItem.setLineColor(channelGroups.get(i)
						.getnDisplayColor());
				freeLineItem.setLineWidth(channelGroups.get(i)
						.getnLineThickness());
				freeLineItem.setLineType(channelGroups.get(i).getnLineType());
				freeLineItem.setAlpha(255);
				freeLineItem.draw(paint, canvas);
			}
		}
		isDrawing = false;
		// show_flag = 0;
	}

	/**
	 * 计算曲线位置
	 */
	private void CalCurve() {
		nCurveX = (short) (mTrendsInfo.getnLp() + mTrendsInfo.getnCurveX());
		nCurveY = (short) (mTrendsInfo.getnTp() + mTrendsInfo.getnCurveY());
		nCurveWd = (short) (mTrendsInfo.getnCurveWd());
		nCurveHt = (short) (mTrendsInfo.getnCurveHt());
		XLine_Tag = nCurveHt / 25;
		XScale_Tag = nCurveHt / 50;
		YLine_Tag = nCurveWd / 40;
	}

	private int getLastDayOfMonth(int year, int month) { // 获取某年某月的最后一天
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			return 31;
		}
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		}
		if (month == 2) {
			if (isLeapYear(year)) {
				return 29;
			} else {
				return 28;
			}
		}
		return 0;
	}

	/**
	 * 是否闰年
	 */
	private boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}


	/**
	 * 历史曲线开始结束时间计算
	 */
	private void SetHistoryTime() {
		if (start_date == null) {
			start_date = Calendar.getInstance();
		}

		if (end_date == null) {
			end_date = Calendar.getInstance();
		}

		if (start_list == null) {
			start_list = new TrendCalendar();
			start_list.C_Date = Calendar.getInstance();
		}
		if (end_list == null) {
			end_list = new TrendCalendar();
			end_list.C_Date = Calendar.getInstance();
		}

		if (mTrendsInfo.getnCurveType() != CURVE_TYPE.HISTORY_CURVE) // 不是历史曲线，不用计算
			return;

		switch (mTrendsInfo.getnTimeRange()) {
		case RECENT_BEGIN:
			//最近多少分/时/天/月/年
			long second = 0,l;
			int newyear,newmonth,newday;
			if (mTrendsInfo.getnRecentMonth() != 0) {
				newyear = end_date.get(Calendar.YEAR);
				newmonth = (int) (end_date.get(Calendar.MONTH) - mTrendsInfo
						.getnRecentMonth());
				newday = end_date.get(Calendar.DATE);
				if (newmonth < 0) {
					newyear -= 1;
					newmonth += 12;
				}
				if (newday > getLastDayOfMonth(newyear, newmonth))
					newday = (short) getLastDayOfMonth(newyear, newmonth);
				start_date.set(Calendar.DAY_OF_MONTH, newday);
				start_date.set(Calendar.MONTH, newmonth);
				start_date.set(Calendar.YEAR, newyear);
				l = end_date.getTimeInMillis() - start_date.getTimeInMillis();
				total_second = (int) (l / 1000);
				// Log.d(TAG, "total_second " + total_second);
				return;
			} else if (mTrendsInfo.getnRecentDay() != 0) {
				second = mTrendsInfo.getnRecentDay() * 24 * 60 * 60;
			} else if (mTrendsInfo.getnRecentHour() != 0) {
				second = mTrendsInfo.getnRecentHour() * 60 * 60;
			} else if (mTrendsInfo.getnRecentMinute() != 0) {
				second = mTrendsInfo.getnRecentMinute() * 60;
			}

			total_second = (int) second;
			l = end_date.getTimeInMillis() - second * 1000;
			start_date.setTimeInMillis(l);
			break;
		case RANGE_BEGIN:
			//设置起始时间+结束时间
			start_date.set(Calendar.DAY_OF_MONTH, mTrendsInfo.getnStartDay());
			start_date.set(Calendar.MONTH, mTrendsInfo.getnStartMonth() - 1);
			start_date.set(Calendar.YEAR, mTrendsInfo.getnStartYear());
			start_date.set(Calendar.HOUR_OF_DAY, mTrendsInfo.getnStartHour());
			start_date.set(Calendar.MINUTE, mTrendsInfo.getnStartMinute());
			end_date.set(Calendar.DAY_OF_MONTH, mTrendsInfo.getnEndDay());
			end_date.set(Calendar.MONTH, mTrendsInfo.getnEndMonth() - 1);
			end_date.set(Calendar.YEAR, mTrendsInfo.getnEndYear());
			end_date.set(Calendar.HOUR_OF_DAY, mTrendsInfo.getnEndHour());
			end_date.set(Calendar.MINUTE, mTrendsInfo.getnEndMinute());
			nStartTime = start_date.getTimeInMillis();
			nLastTime = end_date.getTimeInMillis();
			//getTime(nStartTime);
			//getTime(nLastTime);
			break;
		case STORE_BEGIN:
			//存盘数据的开始
			break;
		case ADDR_BEGIN:
			//地址控制显示时间范围
			if (start_date != null) {
				nStartTime = mTrendsInfo.getnFromTime();
				start_date.setTimeInMillis(nStartTime);
			}
			if (end_date != null) {
				nLastTime = mTrendsInfo.getnToTime();
				end_date.setTimeInMillis(nLastTime);
			}
			break;
		default:
			break;
		}
		//read_historytime = false;
	}

	@Override
	public boolean isTouch() {
		return true;
	}

	private float nDownX = 0;
	private float nDownY = 0;
	float X;
	float Y;
	float tan30 = (float) 0.577;
	float tan60 = (float) 1.73;
	float scale;
	private boolean first_down = false;
	private float nSublineX;//辅助线X坐标
	private float nSublineY;//辅助线Y坐标
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time = 0;
		// TODO Auto-generated method stub
		boolean retVal = false;

		if (mTrendsInfo == null){
			return retVal;
		}
		if (!isShowFlag){
			// 如果不可显现则不可触控
			return retVal;
		}

		short touchX = (short) event.getX();
		short touchY = (short) event.getY();

		if (touchX < mTrendsInfo.getnLp() || touchY < mTrendsInfo.getnTp()
				|| touchX > (mTrendsInfo.getnLp() + mTrendsInfo.getnWidth())
				|| touchY > mTrendsInfo.getnTp() + mTrendsInfo.getnHeight()) {
			return retVal;
		}
		if (DisposePopUpWindows(touchX, touchY) == 1)
			return retVal;

		if (first_down == false)
			if (touchX < (mTrendsInfo.getnLp() + mTrendsInfo.getnCurveX())
					|| touchY < (mTrendsInfo.getnTp() + mTrendsInfo
							.getnCurveY()) // 不在曲线框里面
					|| touchX > (mTrendsInfo.getnLp()
							+ mTrendsInfo.getnCurveX() + mTrendsInfo
								.getnCurveWd())
					|| touchY > (mTrendsInfo.getnTp()
							+ mTrendsInfo.getnCurveY() + mTrendsInfo
								.getnCurveHt())) {
				return retVal;
			}

		int X_MOVE_DISTANCES = nCurveWd / 12;
		int Y_MOVE_DISTANCES = nCurveHt / 12;

		X = event.getX();
		Y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (skToast != null) {
				skToast.cancel();
			}
			nSublineX=X;
			nSublineY=Y;
			bShowSubline=true;
			nDownX = X;
			nDownY = Y;
			first_down = true;
			retVal = true;
			bMoveFlag = false;
			SKSceneManage.getInstance().onRefresh(item);
			break;
		case MotionEvent.ACTION_MOVE:
			retVal = true;
			first_down = false;
			if (mTrendsInfo.getnCurveType() == CURVE_TYPE.HISTORY_CURVE){
				// 历史曲线，处理触摸事件
				if (bMoveFlag == true){
					return retVal;
				}
				if ((Math.abs(Y - nDownY) > Y_MOVE_DISTANCES)
						|| (Math.abs(X - nDownX) > X_MOVE_DISTANCES)) {
					if (skToast != null) {
						skToast.cancel();
					}
					bMoveFlag = true;
					scale = Math.abs(Y - nDownY) / Math.abs(X - nDownX);
					if (scale < tan30) {
						retVal = X_DirectMove();
					} else if (scale > tan60) {
						retVal = Y_DirectMove();
					}

					retVal = true;
					nDownX = X;
					nDownY = Y;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			//Log.d(TAG, "action = "+event.getAction());
			handler.removeMessages(HIDE_SUBLINE);
			//5s钟之后隐藏
			handler.sendEmptyMessageDelayed(HIDE_SUBLINE, 2000);
			break;
		
		default:
			break;
		}
		return retVal;

	}

	/**
	 * Y-轴 移动
	 */
	private boolean Y_DirectMove() {

		if (mTimeInfo.isLoading) {
			ShowErrorMessage(ContextUtl.getInstance().getString(R.string.islogining));
			nDownX = X;
			nDownY = Y;
			return false;
		}

		float nMoveLen;
		float nOldMax = (Current_Zoom.y_rate + Current_Zoom.y_start)
				* (mTrendsInfo.getnDisplayMax());
		nMoveLen = Math.abs(Y - nDownY) * nOldMax / nCurveHt;

		if (Y > nDownY) {
			// 向上平移,
			if (nOldMax >= mTrendsInfo.getnDisplayMax()) {
				// ShowErrorMessage("已经最顶!");
				nDownX = X;
				nDownY = Y;
				return false;
			} else {
				if (nOldMax + nMoveLen > mTrendsInfo.getnDisplayMax()) {
					nMoveLen = mTrendsInfo.getnDisplayMax() - nOldMax;
				}
				float temp = nMoveLen / mTrendsInfo.getnDisplayMax();
				Current_Zoom.y_start += temp;
			}
		} else {
			// 向下平移,
			if (Current_Zoom.y_start <= 0) {
				// ShowErrorMessage("已经最底!");
				nDownX = X;
				nDownY = Y;
				return false;
			} else {
				if (Current_Zoom.y_start * mTrendsInfo.getnDisplayMax()
						- nMoveLen < 0) {
					nMoveLen = Current_Zoom.y_start
							* mTrendsInfo.getnDisplayMax();
				}
				float temp = nMoveLen / mTrendsInfo.getnDisplayMax();
				Current_Zoom.y_start = Current_Zoom.y_start - temp;
			}
		}

		nDownX = X;
		nDownY = Y;
		mTimeInfo.isLoading = true;
		SKTrendsThread.getInstance().getBinder()
				.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName);

		return true;
	}

	/**
	 * X-轴 移动
	 */
	private synchronized boolean X_DirectMove() {

		if (mTimeInfo.isLoading) {
			ShowErrorMessage(ContextUtl.getInstance().getString(R.string.islogining));
			nDownX = X;
			nDownY = Y;
			return false;
		}

		boolean bStartTimeX = false, bEndTimeX = false;

		if (Current_Zoom.start_mils <= nStartTime) {
			bStartTimeX = true;
		}

		if (Current_Zoom.end_mils >= nLastTime) {
			bEndTimeX = true;
		}
		// Log.d(TAG, "bStartTimeX:" + bStartTimeX + ",bEndTimeX:" + bEndTimeX);

		if (bStartTimeX && bEndTimeX) {
			return false;
		}

		if (bStartTimeX && bEndTimeX) {
			// 目前已经处于最小范围了，不能左右移动
			nDownX = X;
			nDownY = Y;
			// Log.d(TAG, "small..........");
			ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_show_finished));
			return false;
		}

		boolean result = true;
		if (X > nDownX) {
			// 向右平移,X当前坐标，nDownX以前的值
			if (bStartTimeX) {
				// 已经处于开始时间
				// Log.d(TAG, "start time..........");
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_start_time));
				result = false;
			}

			if (result) {
				long s = start_date.getTimeInMillis();
				long l = end_date.getTimeInMillis()
						- start_date.getTimeInMillis();

				if (s - l < nStartTime) {
					start_date.setTimeInMillis(nStartTime);
					end_date.setTimeInMillis(nStartTime + l);

				} else {
					start_date.setTimeInMillis(s - l);
					end_date.setTimeInMillis(s);
				}
			}

		} else {
			// 向左平移
			if (bEndTimeX) {
				// Log.d(TAG, "last time..........");
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_last_time));
				result = false;
			}

			if (result) {
				long s = end_date.getTimeInMillis();
				long l = end_date.getTimeInMillis()
						- start_date.getTimeInMillis();

				if (s + l > nLastTime) {
					end_date.setTimeInMillis(nLastTime);
					start_date.setTimeInMillis(nLastTime - l);
				} else {
					start_date.setTimeInMillis(s);
					end_date.setTimeInMillis(s + l);
				}
			}

		}

		Current_Zoom.start_mils = start_date.getTimeInMillis();
		Current_Zoom.end_mils = end_date.getTimeInMillis();

		mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);
		Zoom_Oprate = OPRATE.OPRATE_MOVE_X;
		nDownX = X;
		nDownY = Y;

		if (result) {
			SKTrendsThread.getInstance().getBinder()
					.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName);
		}

		return result;

	}

	/**
	 * 放大
	 * @param type =0 X轴放大; type=1 Y轴放大; type=2 XY轴放大;
	 */
	public boolean large(int type) {

		if (mTimeInfo.isLoading) {
			ShowErrorMessage(ContextUtl.getInstance().getString(R.string.islogining));
			// 正在加载数据
			return false;
		}

		boolean isLargeX, isLargeY;
		switch (type) {
		case 0:
			// X轴放大
			isLargeX = largeX();
			if (!isLargeX) {
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_largest));
				return false;
			}
			break;
		case 1:
			// Y轴放大
			isLargeY = largeY();
			if (!isLargeY) {
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_largest));
				return false;
			}
			break;
		case 2:
			// XY轴放大
			isLargeX = largeX();
			isLargeY = largeY();
			if (!isLargeX && !isLargeY) {
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_largest));
				return false;
			}
			break;
		}

		mTimeInfo.isLoading = true;
		SKTrendsThread.getInstance().getBinder()
				.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName);
		return true;
	}

	/**
	 * X-轴放大
	 */
	private synchronized boolean largeX() {

		float zoomrate;
		long l;
		l = end_date.getTimeInMillis() - start_date.getTimeInMillis();
		zoomrate = (float) 0.5;

		if (l < 240 * 1000) {

			if (l > 120 * 1000 && !mTimeInfo.isLargeX) {
				mTimeInfo.isLargeX = true;
				zoomrate = (float) (l - 120 * 1000) / l;
			} else {
				// Log.d(TAG,
				// "l="+l+",mTimeInfo.isLargeX="+(!mTimeInfo.isLargeX));
				return false;
			}
		}

		Current_Zoom.zoom_start = 0;
		Current_Zoom.zoom_rate = zoomrate;

		old_start_time = start_date.getTimeInMillis();
		float len = l * Current_Zoom.zoom_rate;
		long time_start = (long) (old_start_time + len);
		start_date.setTimeInMillis(time_start);

		Current_Zoom.start_mils = start_date.getTimeInMillis();
		Current_Zoom.end_mils = end_date.getTimeInMillis();
		mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);
		// Log.d(TAG, "lardy x");
		//getTime(end_date.getTimeInMillis());

		return true;
	}

	/**
	 * Y-轴放大
	 */
	private synchronized boolean largeY() {
		if (mTrendsInfo == null) {
			return false;
		}
		float nRate = (float) (Current_Zoom.y_rate * 0.5);
		float len = (mTrendsInfo.getnDisplayMax() - mTrendsInfo
				.getnDisplayMin()) * nRate;
		int scale = mTrendsInfo.getnVertMajorScale()
				* mTrendsInfo.getnVertMinorScale();
		float temp = len / scale;
		if (temp <= 1) {
			if (mTimeInfo.isLargeY) {
				return false;
			} else {
				mTimeInfo.isLargeY = true;
				nRate = (float) (scale * 1)
						/ (mTrendsInfo.getnDisplayMax() - mTrendsInfo
								.getnDisplayMin());
			}
		}
		Current_Zoom.y_rate = nRate;
		mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);

		return true;
	}

	/**
	 * 缩小
	 * @param type =0 X轴缩小; type=1 Y轴缩小; type=2 XY轴缩小;
	 */
	public boolean reduce(int type) {

		if (mTimeInfo.isLoading) {
			// 正在加载数据
			ShowErrorMessage(ContextUtl.getInstance().getString(R.string.islogining));
			return false;
		}

		boolean isReduceX, isReduceY;
		switch (type) {
		case 0:
			// X轴缩小
			isReduceX = reduceX();
			if (!isReduceX) {
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_minimum));
				return false;
			}
			break;
		case 1:
			// Y轴缩小
			isReduceY = reduceY();
			if (!isReduceY) {
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_minimum));
				return false;
			}
			break;
		case 2:
			// XY轴缩小
			isReduceX = reduceX();
			isReduceY = reduceY();
			// Log.d(TAG, "......isReduceY="+isReduceY);
			if (!isReduceX && !isReduceY) {
				ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_minimum));
				return false;
			}
			break;
		}

		mTimeInfo.isLoading = true;
		SKTrendsThread.getInstance().getBinder()
				.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName);
		return true;
	}

	/**
	 * X-轴 缩小
	 */
	private synchronized boolean reduceX() {
		// 缩小,以水平时间轴，中心点缩小
		boolean bStartTimeX = false, bEndTimeX = false;
		if (start_date.getTimeInMillis() <= nStartTime) {
			// Log.d(TAG,
			// "r Current_Zoom.start_mils="+Current_Zoom.start_mils+",nStartTime="+nStartTime);
			bStartTimeX = true;
		}

		if (end_date.getTimeInMillis() >= nLastTime) {
			// Log.d(TAG,
			// "r Current_Zoom.end_mils="+Current_Zoom.end_mils+",nLastTime="+nLastTime);
			bEndTimeX = true;
		}
		if (bStartTimeX && bEndTimeX) {
			// Log.d(TAG, "reduceX.....");
			return false;
		}

		Current_Zoom.zoom_start = 0;
		Current_Zoom.zoom_rate = (float) 0.5;

		// 当前的时间差
		long start = start_date.getTimeInMillis();
		long end = end_date.getTimeInMillis();
		long len = end - start;

		// 左边
		if (!bStartTimeX) {
			// 左边，还可以缩小
			long l = len / 2;
			if (bEndTimeX) {
				// 右边已经缩小完毕
				l = len;
			}
			if (start - l < nStartTime) {
				start_date.setTimeInMillis(nStartTime);
			} else {
				start_date.setTimeInMillis(start - l);
			}
		}

		// 右边
		if (!bEndTimeX) {
			// 右边，还可以缩小
			long l = len / 2;
			if (bStartTimeX) {
				// 左边已经缩小完毕
				l = len;
			}
			if (end + l > nLastTime) {
				end_date.setTimeInMillis(nLastTime);
			} else {
				end_date.setTimeInMillis(end + l);
			}
		}

		Current_Zoom.start_mils = start_date.getTimeInMillis();
		Current_Zoom.end_mils = end_date.getTimeInMillis();
		mTimeInfo.isLargeX = false;
		mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);

		return true;
	}

	/**
	 * Y-轴 缩小
	 */
	private synchronized boolean reduceY() {
		if (mTrendsInfo == null) {
			return false;
		}

		float nRate = (float) (Current_Zoom.y_rate * 2);
		float len = mTrendsInfo.getnDisplayMax() - mTrendsInfo.getnDisplayMin();
		float nOldMax = (Current_Zoom.y_rate + Current_Zoom.y_start) * len;
		float max = (nRate + Current_Zoom.y_start) * len;

		if (nOldMax >= mTrendsInfo.getnDisplayMax()) {
			return false;
		} else {
			if (max >= mTrendsInfo.getnDisplayMax()) {
				Current_Zoom.y_start = 0;
				nRate = 1;
			}
		}
		mTimeInfo.isLargeY = false;
		Current_Zoom.y_start = (float) (Current_Zoom.y_start * 0.5);
		Current_Zoom.y_rate = nRate;
		mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);

		return true;
	}

	/**
	 * 时间轴信息
	 */
	public class TimeInfo {
		public boolean isLoading;// 是否处于加载中
		public boolean isLargeX; // X轴是否已经最大
		public boolean isLargeY; // Y轴是否已经最大
	}

	/**
	 * 时间格式转换
	 */
	private void getTime(long dateTime) {

		String sTime = "HH:mm";
		String sDate = "yyyy-MM-dd";

		SimpleDateFormat mDateFormat = new SimpleDateFormat(sDate);
		SimpleDateFormat mTimeFormat = new SimpleDateFormat(sTime);

		Date mDate = new Date(dateTime);
		String dataFormat = mDateFormat.format(mDate);
		String timeFormat = mTimeFormat.format(mDate);

		Log.d(TAG, "time:" + dataFormat + " " + timeFormat);

	}

	/**
	 * 开始显示地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack fromCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			nStartTime = getTime(nStatusValue);
			// Log.d(TAG, "nStartTime="+nStartTime);
			if (nStartTime < 0) {
				return;
			}
			if (start_date != null) {
				start_date.setTimeInMillis(nStartTime);
				mTrendsInfo.setnFromTime(nStartTime);
				if (nStartTime > nLastTime) {
					return;
				}
				mTrendsInfo.setnFromTime(nStartTime);
				SKTrendsThread
						.getInstance()
						.getBinder()
						.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA,
								sTaskName);
				bDataChange = false;
			}
		}
	};

	/**
	 * 结束显示地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack toCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			nLastTime = getTime(nStatusValue);
			// Log.d(TAG, "nStartTime="+nStartTime+",nLastTime="+nLastTime);
			if (end_date != null) {
				end_date.setTimeInMillis(nLastTime);
				if (nLastTime < nStartTime) {
					return;
				}
				mTrendsInfo.setnToTime(nLastTime);
				SKTrendsThread
						.getInstance()
						.getBinder()
						.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA,
								sTaskName);
				bDataChange = false;
			}
		}
	};

	/**
	 * 获取时间
	 */
	private long getTime(Vector<Byte> nStatusValue) {
		if (nStatusValue == null) {
			return 0;
		}
		Vector<Integer> list = new Vector<Integer>();
		boolean result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, list);
		long time = 0;

		if (result) {
			if (list == null || list.size() < 6) {
				return time;
			}

			try {
				int[] array = new int[] { 0, 0, 0, 0, 0, 0 };
				for (int i = 0; i < list.size(); i++) {
					String s = DataTypeFormat.intToBcdStr(list.get(i), false);
					if (s != null && !s.equals("ERROR")) {
						array[i] = Integer.valueOf(s);
					}
					// Log.d(TAG, "i="+i+",time="+array[i]);
				}
				if (array[0] < 1970) {
					time = 0;
					return time;
				}

				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, array[0]);
				c.set(Calendar.MONTH, array[1] - 1);
				c.set(Calendar.DAY_OF_MONTH, array[2]);
				c.set(Calendar.HOUR_OF_DAY, array[3]);
				c.set(Calendar.MINUTE, array[4]);
				c.set(Calendar.SECOND, array[5]);
				time = c.getTimeInMillis();
				if (time < 0) {
					time = 0;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return time;
	}

	
	
	/** 以下部分处理时间窗口弹出 **/

	/**
	 * @param mOnDateChangedListener
	 */
	public enum TYPE {
		START_DATE, // 开始日期
		START_TIME, // 开始时间
		END_DATE, // 结束日期
		END_TIME, // 结束时间
	}

	DatePicker datePicker, datePicker1;
	boolean isFreshStart, isFreshEnd;

	private void initPopupWindow(TYPE flag) {
		isFreshStart = isFreshEnd = false;
		nCompile_Flag = (TYPE) flag;
		switch (flag) {
		case START_DATE:
			mInflater = LayoutInflater
					.from(SKSceneManage.getInstance().mContext);
			cView = mInflater.inflate(R.layout.date_widgets, null);
			cView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SKSceneManage.getInstance().time = 0;
				}
			});
			cView.setBackgroundColor(Color.BLACK);

			nTemp_Year = (short) start_date.get(Calendar.YEAR);
			nTemp_Month = (short) start_date.get(Calendar.MONTH);
			nTemp_Day = (short) start_date.get(Calendar.DATE);

			OnDateChangedListener monDateChangedListener = new DatePicker.OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					nTemp_Year = (short) year;
					nTemp_Month = (short) monthOfYear;
					nTemp_Day = (short) dayOfMonth;
					System.out.println("year" + nTemp_Year + "monthOfYear"
							+ nTemp_Month + "dayOfMonth" + nTemp_Day);
					// TODO Auto-generated method stub

				}
			};
			datePicker = (DatePicker) cView.findViewById(R.id.datePicker);
			// Month is 0 based
			datePicker.init(start_date.get(Calendar.YEAR), 10, 10,
					monDateChangedListener);
			isFreshStart = true;
			break;

		case START_TIME:
			mInflater = LayoutInflater
					.from(SKSceneManage.getInstance().mContext);
			cView = mInflater.inflate(R.layout.time_widgets, null);
			cView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SKSceneManage.getInstance().time = 0;
				}
			});
			cView.setBackgroundColor(Color.BLACK);
			TimePicker timePicker = (TimePicker) cView
					.findViewById(R.id.timePicker);
			timePicker.setCurrentHour((int) start_date
					.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute((int) start_date.get(Calendar.MINUTE));

			nTemp_Hour = (short) start_date.get(Calendar.HOUR_OF_DAY);
			nTemp_Minute = (short) start_date.get(Calendar.MINUTE);
			timePicker
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
							nTemp_Hour = (short) hourOfDay;
							nTemp_Minute = (short) minute;
							System.out.println("hourOfDay" + hourOfDay
									+ "minute" + minute);
							// updateDisplay(hourOfDay, minute);
						}
					});

			break;

		case END_DATE:
			mInflater = LayoutInflater
					.from(SKSceneManage.getInstance().mContext);
			cView = mInflater.inflate(R.layout.date_widgets, null);
			cView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SKSceneManage.getInstance().time = 0;
				}
			});
			cView.setBackgroundColor(Color.BLACK);

			nTemp_Year = (short) end_date.get(Calendar.YEAR);
			nTemp_Month = (short) end_date.get(Calendar.MONTH);
			nTemp_Day = (short) end_date.get(Calendar.DATE);

			OnDateChangedListener monDateChangedListener1 = new DatePicker.OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					nTemp_Year = (short) year;
					nTemp_Month = (short) monthOfYear;
					nTemp_Day = (short) dayOfMonth;
					System.out.println("year" + year + "monthOfYear"
							+ monthOfYear + "dayOfMonth" + dayOfMonth);
					// TODO Auto-generated method stub

				}
			};
			datePicker1 = (DatePicker) cView.findViewById(R.id.datePicker);
			// Month is 0 based
			datePicker1.init(end_date.get(Calendar.YEAR), 10, 10,
					monDateChangedListener1);
			isFreshEnd = true;
			break;

		case END_TIME:
			mInflater = LayoutInflater
					.from(SKSceneManage.getInstance().mContext);
			cView = mInflater.inflate(R.layout.time_widgets, null);
			cView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SKSceneManage.getInstance().time = 0;
				}
			});
			cView.setBackgroundColor(Color.BLACK);
			TimePicker timePicker1 = (TimePicker) cView
					.findViewById(R.id.timePicker);
			timePicker1
					.setCurrentHour((int) end_date.get(Calendar.HOUR_OF_DAY));
			timePicker1.setCurrentMinute((int) end_date.get(Calendar.MINUTE));

			nTemp_Hour = (short) end_date.get(Calendar.HOUR_OF_DAY);
			nTemp_Minute = (short) end_date.get(Calendar.MINUTE);

			timePicker1
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
							nTemp_Hour = (short) hourOfDay;
							nTemp_Minute = (short) minute;
							System.out.println("hourOfDay" + hourOfDay
									+ "minute" + minute);
							// updateDisplay(hourOfDay, minute);
						}
					});

			break;

		default:
			break;
		}

		// 获取确定和取消按钮，并加入点击事件
		sureButton = (Button) cView.findViewById(R.id.date_sure);
		sureButton.setOnClickListener(this);
		cancelButton = (Button) cView.findViewById(R.id.date_cancel);
		cancelButton.setOnClickListener(this);

		// 设置窗口的大小
		mPopupWindow = new myPopupWindow(cView,
				SKSceneManage.getInstance().nSceneWidth - 20,
				SKSceneManage.getInstance().nSceneHeight - 20);
	}

	private void showPopupWindow() {

		try {
			SKScene cView = SKSceneManage.getInstance().getCurrentScene();
			if (null != cView && null != mPopupWindow && SKProgress.onResume
					&& !popIsShow) {
				popIsShow = true;
				mPopupWindow.setFocusable(true);
				mPopupWindow.update();
				mPopupWindow.showAtLocation(cView, Gravity.NO_GRAVITY, 10, 10);

				// 强刷2位的月份
				if (handler != null) {
					if (isFreshStart) {
						datePicker
								.updateDate(start_date.get(Calendar.YEAR), 10, 10);
						mPopupWindow.handler.sendEmptyMessageDelayed(
								DIALOG_FRESH_START, 100);
						isFreshStart = false;
					} else if (isFreshEnd) {
						datePicker1.updateDate(end_date.get(Calendar.YEAR), 10, 10);
						mPopupWindow.handler.sendEmptyMessageDelayed(
								DIALOG_FRESH_END, 100);
						isFreshEnd = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private int DisposePopUpWindows(short touchX, short touchY) {
		int popup_flag = 0;

		if ((touchX > nStart_DateX && touchX < (nStart_DateX + nStart_DateWd)
				&& touchY > nStart_DateY && touchY < (nStart_DateY + nStart_DateHt))) {
			if (null == mPopupWindow) {
				initPopupWindow(TYPE.START_DATE);
			}
			showPopupWindow();
			popup_flag = 1;
		}

		else if ((touchX > nEnd_DateX && touchX < (nEnd_DateX + nEnd_DateWd)
				&& touchY > nEnd_DateY && touchY < (nEnd_DateY + nEnd_DateHt))) {
			if (null == mPopupWindow) {
				initPopupWindow(TYPE.END_DATE);
			}
			showPopupWindow();
			popup_flag = 1;
		}

		else if ((touchX > nStart_TimeX
				&& touchX < (nStart_TimeX + nStart_TimeWd)
				&& touchY > nStart_TimeY && touchY < (nStart_TimeY + nStart_TimeHt))) {
			if (null == mPopupWindow) {
				initPopupWindow(TYPE.START_TIME);
			}
			showPopupWindow();
			popup_flag = 1;
		}

		else if ((touchX > nEnd_TimeX && touchX < (nEnd_TimeX + nEnd_TimeWd)
				&& touchY > nEnd_TimeY && touchY < (nEnd_TimeY + nEnd_TimeHt))) {
			if (null == mPopupWindow) {
				initPopupWindow(TYPE.END_TIME);
			}
			showPopupWindow();
			popup_flag = 1;
		}
		return popup_flag;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		switch (v.getId()) {
		case R.id.date_sure:
			sureOnclick();
			break;
		case R.id.date_cancel:
			cancelOnclick();
			break;

		default:
			break;
		}

	}

	private int CompileTime(int Oldest_Year, int Oldest_Month, int Oldest_Day,
			int Oldest_Hour, int Oldest_Minute, int Recent_Year,
			int Recent_Month, int Recent_Day, int Recent_Hour, int Recent_Minute) {
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		long l1, l2;
		int result;
		c1.set(Calendar.YEAR, Oldest_Year);
		c1.set(Calendar.MONTH, Oldest_Month);
		c1.set(Calendar.DAY_OF_MONTH, Oldest_Day);
		c1.set(Calendar.HOUR_OF_DAY, Oldest_Hour);
		c1.set(Calendar.MINUTE, Oldest_Minute);

		c2.set(Calendar.YEAR, Recent_Year);
		c2.set(Calendar.MONTH, Recent_Month);
		c2.set(Calendar.DAY_OF_MONTH, Recent_Day);
		c2.set(Calendar.HOUR_OF_DAY, Recent_Hour);
		c2.set(Calendar.MINUTE, Recent_Minute);

		l1 = c1.getTimeInMillis();
		l2 = c2.getTimeInMillis();
		if (l1 == l2) {
			System.out.println("c1相等c2");
			result = 0;
		} else if (l1 < l2) {
			System.out.println("c1小于c2");
			result = -1;
		} else {
			System.out.println("c1大于c2");
			result = 1;
		}
		return result;
	}

	private int DisopeCompileTime() {
		int result = 0;
		switch (nCompile_Flag) {
		case START_DATE:
			// Month is 0 based
			result = CompileTime(nTemp_Year, nTemp_Month, nTemp_Day,
					start_date.get(Calendar.HOUR_OF_DAY),
					start_date.get(Calendar.MINUTE),
					end_date.get(Calendar.YEAR), end_date.get(Calendar.MONTH),
					end_date.get(Calendar.DATE),
					end_date.get(Calendar.HOUR_OF_DAY),
					end_date.get(Calendar.MINUTE));
			break;
		case START_TIME:
			result = CompileTime(start_date.get(Calendar.YEAR),
					start_date.get(Calendar.MONTH),
					start_date.get(Calendar.DATE), nTemp_Hour, nTemp_Minute,
					end_date.get(Calendar.YEAR), end_date.get(Calendar.MONTH),
					end_date.get(Calendar.DATE),
					end_date.get(Calendar.HOUR_OF_DAY),
					end_date.get(Calendar.MINUTE));

			break;
		case END_DATE:
			// Month is 0 based
			result = CompileTime(start_date.get(Calendar.YEAR),
					start_date.get(Calendar.MONTH),
					start_date.get(Calendar.DATE),
					start_date.get(Calendar.HOUR_OF_DAY),
					start_date.get(Calendar.MINUTE), nTemp_Year, nTemp_Month,
					nTemp_Day, end_date.get(Calendar.HOUR_OF_DAY),
					end_date.get(Calendar.MINUTE));
			break;
		case END_TIME:
			result = CompileTime(start_date.get(Calendar.YEAR),
					start_date.get(Calendar.MONTH),
					start_date.get(Calendar.DATE),
					start_date.get(Calendar.HOUR_OF_DAY),
					start_date.get(Calendar.MINUTE),
					end_date.get(Calendar.YEAR), end_date.get(Calendar.MONTH),
					end_date.get(Calendar.DATE), nTemp_Hour, nTemp_Minute);
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * 保存时间日期参数保存时间日期参数
	 */
	private void SaveTime() {
		switch (nCompile_Flag) {
		case START_DATE:
			start_date.set(Calendar.YEAR, nTemp_Year);
			// Month is 0 based
			start_date.set(Calendar.MONTH, nTemp_Month);
			start_date.set(Calendar.DAY_OF_MONTH, nTemp_Day);
			break;
		case START_TIME:
			start_date.set(Calendar.HOUR_OF_DAY, nTemp_Hour);
			start_date.set(Calendar.MINUTE, nTemp_Minute);
			break;
		case END_DATE:
			end_date.set(Calendar.YEAR, nTemp_Year);
			// Month is 0 based
			end_date.set(Calendar.MONTH, nTemp_Month);
			end_date.set(Calendar.DAY_OF_MONTH, nTemp_Day);
			break;
		case END_TIME:
			end_date.set(Calendar.HOUR_OF_DAY, nTemp_Hour);
			end_date.set(Calendar.MINUTE, nTemp_Minute);
			break;
		default:
			break;
		}
	}

	/**
	 * 按下确定按钮要执行的动作
	 */
	private void sureOnclick() {
		// 进行选中确定的处理
		// 确定去执行相应按钮的功能
		int flag;
		flag = DisopeCompileTime();
		if (flag < 0) {

			// 开始时间小于结束时间，符合曲线要求
			SaveTime();
			System.out.println("SaveTime");
			SKTrendsThread.getInstance().getBinder()
					.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName);
			// bZoomIn = false;
			Current_Zoom.zoom_start = (float) 0.0;
			Current_Zoom.zoom_rate = (float) 1.0;
			Zoom_Oprate = OPRATE.OPRATE_NORMAL;
			mZoomList.clear();
			mTrendsInfo.setnTimeRange(TIMERANGE_TYPE.RANGE_BEGIN);
		} else {
			ShowErrorMessage(ContextUtl.getInstance().getString(R.string.history_time_error));
			SKSceneManage.getInstance().onRefresh(item);
		}
		mPopupWindow.dismiss();
		popIsShow = false;
		mPopupWindow = null;
	}

	/**
	 * 按下取消按钮要执行的动作
	 */
	private void cancelOnclick() {
		if (popIsShow) { // 取消，不设定时间日期
			mPopupWindow.dismiss();
			mPopupWindow = null;
			popIsShow = false;
		}
	}

	/**
	 * 显示错误显示
	 */
	private void ShowErrorMessage(String Message) {
		Paint newPaint;
		int str_ht, str_wd, start_x, start_y;

		newPaint = new Paint();
		newPaint.setTextSize(16);
		str_wd = getCurTextLengthInPixels(newPaint, Message);
		str_ht = getFontHeight(newPaint);
		start_x = nCurveX + nCurveWd / 2 - str_wd / 2;
		if (start_x < 0)
			start_x = 0;
		start_y = nCurveY + nCurveHt / 2 - str_ht / 2;
		if (start_y < 0)
			start_y = 0;
		skToast = SKToast.makeText(SKSceneManage.getInstance().mContext,
				Message, Toast.LENGTH_SHORT, Gravity.LEFT | Gravity.TOP,
				start_x, start_y);
		if (skToast != null)
			skToast.show();
		// SKToast.hideToast(0);
	}

	/** 时间窗口弹出结束 **/
	class myPopupWindow extends PopupWindow {
		public myPopupWindow(View cView, int nCurveWd, int nCurveHt) {
			// TODO Auto-generated constructor stub
			super(cView, nCurveWd, nCurveHt);
		}

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == DIALOG_FRESH_START) {
					update1(false);
				} else if (msg.what == DIALOG_FRESH_END) {
					update1(true);
				}
			}
		};

		public void update1(boolean isEnd) {
			if (isEnd) {
				if (datePicker1 != null) {
					datePicker1.updateDate(end_date.get(Calendar.YEAR),
							end_date.get(Calendar.MONTH),
							end_date.get(Calendar.DATE));
				}
			} else {
				if (datePicker != null) {
					datePicker.updateDate(start_date.get(Calendar.YEAR),
							start_date.get(Calendar.MONTH),
							start_date.get(Calendar.DATE));
				}
			}
		}
	}
	
	 /**
	 * 颜色取反
	 */
	private int getInverseColor(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		return Color.rgb(255 - r, 255 - g, 255 - b);
	}

	/**
	 * 脚本对外接口
	 */
	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			return mTrendsInfo.getnLp();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			return mTrendsInfo.getnLp();
		}
		return -1;
	}


	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			return mTrendsInfo.getnWidth();
		}
		return -1;
	}


	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			return mTrendsInfo.getnHeight();
		}
		return -1;
	}


	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		//nCurrentState;
		if (mTrendsInfo!=null) {
			return getColor(mTrendsInfo.getnGraphColor());
		}
		return null;
	}
	

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			return getColor(mTrendsInfo.getnBoradColor());
		}
		return null;
	}


	@Override
	public boolean getItemVisible(int id) {
		// TODO Auto-generated method stub
		return isShowFlag;
	}


	@Override
	public boolean getItemTouchable(int id) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		
		if (mTrendsInfo != null) {
			if (x == mTrendsInfo.getnLp()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			mTrendsInfo.setnLp((short)x);
			int l=item.rect.left;
			item.rect.left=x;
			item.rect.right=x-l+item.rect.right;
			item.mMoveRect=new Rect();
			
			mTrendsInfo.setnCurveX((short)(mTrendsInfo.getnCurveX()+x-l));
			nCurveX = (short) (mTrendsInfo.getnLp() + mTrendsInfo.getnCurveX());
			
			SKTrendsThread.getInstance().getBinder()
			.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName); // 历史曲线，数据采集注册
			
			//SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (mTrendsInfo != null) {
			if (y == mTrendsInfo.getnTp()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			mTrendsInfo.setnTp((short)y);
			int t = item.rect.top;
			item.rect.top = y;
			item.rect.bottom = y - t + item.rect.bottom;
			item.mMoveRect=new Rect();
			
			mTrendsInfo.setnCurveY((short)(mTrendsInfo.getnCurveY()+y-t));
			nCurveY = (short) (mTrendsInfo.getnTp() + mTrendsInfo.getnCurveY());
			
			SKTrendsThread.getInstance().getBinder()
			.onTask(MODULE.CAL_CURVE, TASK.CURVE_CAL_DATA, sTaskName); // 历史曲线，数据采集注册
			//SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (mTrendsInfo != null) {
			if (w == mTrendsInfo.getnWidth()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int len=w-mTrendsInfo.getnWidth();
			mTrendsInfo.setnWidth((short)w);
			item.rect.right = w - item.rect.width() + item.rect.right;
			item.mMoveRect=new Rect();
			
			mTrendsInfo.setnCurveWd((short)(mTrendsInfo.getnCurveWd()+len));
			nCurveWd = (short) (mTrendsInfo.getnCurveWd());
			bgBitmap=null;
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (mTrendsInfo != null) {
			if (h == mTrendsInfo.getnHeight()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			
			int len=h-mTrendsInfo.getnHeight();
			mTrendsInfo.setnHeight((short)h);
			item.rect.bottom = h - item.rect.height() + item.rect.bottom;
			item.mMoveRect=new Rect();
			
			mTrendsInfo.setnCurveHt((short)(mTrendsInfo.getnCurveHt()+len));
			nCurveHt = (short) (mTrendsInfo.getnCurveHt());
			bgBitmap=null;
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			int color=Color.rgb(r, g, b);
			if (color==mTrendsInfo.getnGraphColor()) {
				return true;
			}
			mTrendsInfo.setnGraphColor(color);
			bgBitmap=null;
			SKSceneManage.getInstance().onRefresh(item);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mTrendsInfo!=null) {
			int color=Color.rgb(r, g, b);
			if (color==mTrendsInfo.getnBoradColor()) {
				return true;
			}
			mTrendsInfo.setnBoradColor(color);
			bgBitmap=null;
			SKSceneManage.getInstance().onRefresh(item);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==isShowFlag) {
			return true;
		}
		isShowFlag=v;
		SKSceneManage.getInstance().onRefresh(item);
		return true;
	}


	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean setItemPageUp(int id) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemPageDown(int id) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemFlick(int id, boolean v, int time) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemHroll(int id, int w) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemText(int id, int lid, String text) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 颜色取反
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
}
