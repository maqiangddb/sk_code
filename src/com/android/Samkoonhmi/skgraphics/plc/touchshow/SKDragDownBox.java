package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.DragdownBoxAdapt;
import com.android.Samkoonhmi.graphicsdrawframe.FoldLineItem;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.DragdownboxItemInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread.IPlcNoticCallBack;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 下拉框
 */
@SuppressLint("InlinedApi")
public class SKDragDownBox extends SKGraphCmnTouch implements IItem {
	private DragdownboxItemInfo info;
	private Rect rectBoder;// 矩形边框
	private Rect rectBox;// 显示下拉三角形的矩形
	private PopupWindow mPopupWindow; // 显示的下拉窗口
	private View cView; // 下拉窗口的布局
	private LayoutInflater mInflater;
	public boolean popIsShow; // 窗口是否弹出
	private ListView listView;// 下拉列表
	private List<TextInfo> data;
	private DragdownBoxAdapt adapter;
	private Vector<Point> pointList;
	private String showFunctionName;
	private int rect2Width = 20;// 显示下拉三角形的矩形宽度
	private Paint mPaint;
	private int boxHeight = 0;// 下拉触控的窗口的高度
	private int screenHeight = 0;// 屏幕的高度
	private int selectIndex = 0;
	private Rect myRect;
	private TextItem textItem;
	private StaticTextModel text;
	private RectItem rectItems;
	// private RectItem borderReceItem;
	private FoldLineItem foldLineItem;
	private SKItems items;
	private int itemId;
	private int sceneId;
	private boolean initFlag;
	private boolean isOnClick;
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean touchByUser;
	private boolean showByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private myMainHandler hand = null;
	private Bitmap mLockBitmap;
	private Vector<Short> mSData = null;// 记录监控的值
	private Vector<Integer> mIData = null;// 记录监控的值
	private Vector<Long> mLData = null;// 记录监控的值
	private Vector<Float> mFData = null;// 记录监控的值
	private ImageDrawItem imageItem;// 画背景用

	// private List<TextInfo> tempList;

	public SKDragDownBox(int itemId, int sceneId, DragdownboxItemInfo info) {
		isOnClick = false;
		isTouchFlag = true;
		isShowFlag = true;
		touchByUser = false;
		showByUser = false;
		showByAddr = false;
		touchByAddr = false;
		initFlag = true;
		this.itemId = itemId;
		this.sceneId = sceneId;
		mPaint = new Paint();
		showFunctionName = null;
		this.info = info;
		if (info != null) {
			// 下拉列表数据
			data = new ArrayList<TextInfo>();
			for (int i = 0; i < info.getmTextAttrList().size() - 1; i++) {
				data.add(info.getmTextAttrList().get(i));
			}
			// 控件的矩形大小
			myRect = info.getRect();
			rect2Width = myRect.width() * 6 / 27;

			// 显示文字的矩形框
			rectBoder = new Rect();
			rectBoder.left = info.getRect().left;
			rectBoder.right = info.getRect().right - rect2Width;
			rectBoder.top = info.getRect().top;
			rectBoder.bottom = info.getRect().bottom;

			// 画三角下拉的矩形框
			rectBox = new Rect();
			rectBox.left = rectBoder.right;
			rectBox.right = rectBoder.right + rect2Width;
			rectBox.top = rectBoder.top;
			rectBox.bottom = rectBoder.bottom;

			rectItems = new RectItem(rectBox);
			rectItems.setLineColor(Color.rgb(183, 211, 252));
			rectItems.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
			rectItems.setBackColor(Color.rgb(183, 211, 252));
			rectItems.setLineWidth(1);
			rectItems.setAlpha(info.getAlpha());

			// 三角下拉的点集合
			int leftPX = rectBox.left + rect2Width * 1 / 4;
			int leftPY = myRect.top + myRect.height() * 7 / 18;
			int rightPX = rectBox.left + rect2Width * 3 / 4;
			int rightPY = leftPY;
			int buttomX = rectBox.left + rect2Width / 2;
			int buttomY = rectBox.top + rectBox.height() * 11 / 18;
			pointList = new Vector<Point>();
			pointList.add(new Point(leftPX, leftPY));
			pointList.add(new Point(buttomX, buttomY));
			pointList.add(new Point(rightPX, rightPY));

			// 三角下拉的折线
			foldLineItem = new FoldLineItem(pointList);
			foldLineItem.setLineColor(Color.rgb(77, 97, 133));
			foldLineItem.setAlpha(255);
			foldLineItem.setLineWidth(2);

			// 初始化文本的属性
			setTextByLanguage();

			items = new SKItems();
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.sceneId;
			items.rect = myRect;
			items.mGraphics = this;

			if (null != info.getTouchInfo()) {
				if (null != info.getTouchInfo().getTouchAddrProp()) {
					touchByAddr = true;
				}
				if (info.getTouchInfo().isbTouchByUser()) {
					touchByUser = true;
				}
			}
			if (null != info.getShowInfo()) {
				if (null != info.getShowInfo().getShowAddrProp()) {
					showByAddr = true;
				}
				if (info.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}

			// 注册地址接口
			registAddr();
		}

	}

	/**
	 * 根据语言设置当前状态下文本对象的属性
	 */
	private void setTextByLanguage() {
		// 文字对象
		if (null == text) {
			text = new StaticTextModel();
		}

		TextInfo tInfo = info.getmTextAttrList().get(
				SystemInfo.getCurrentLanguageId());
		text.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
		text.setM_nFontColor(tInfo.getmColors().get(
				SystemInfo.getCurrentLanguageId()));
		text.setM_nFontSize(tInfo.getmSize().get(
				SystemInfo.getCurrentLanguageId()));
		int i = tInfo.getmStyle().get(SystemInfo.getCurrentLanguageId());
		text.setM_textPro((short) i);
		text.setM_backColorPadding(info.getBackgroundColor());
		text.setStartX(info.getRect().left + 1);
		text.setStartY(info.getRect().top + 1);
		text.setRectHeight(rectBoder.height() - 2);
		// 有图片，文本背景设置为透明，文本边框设置为透明
		if (null != info.getBackgroundImg()
				&& !"".equals(info.getBackgroundImg())) {
			text.setRectWidth(info.getRect().width());
			text.setM_alphaPadding(0);
			text.setBorderAlpha(0);
		} else {
			text.setRectWidth(rectBoder.width() - 1);
			text.setM_alphaPadding(info.getAlpha());
			text.setBorderAlpha(255);
		}

		text.setLineColor(Color.rgb(183, 211, 252));
		text.setM_sFontFamly(tInfo.getmFonts().get(
				SystemInfo.getCurrentLanguageId()));
		text.setLineWidth(1);

		textItem = new TextItem(text);
		textItem.initTextPaint();
		textItem.initRectBoderPaint();
		textItem.initRectPaint();
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		if (null == info) {
			return;
		}
		initFlag = true;
		// boxHeight = 200;

		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			screenHeight = SKSceneManage.getInstance().getCurrentInfo()
					.getnSceneHeight();
		}

		comboxIsShow();
		comboxIsTouch();

		// 注册多语言切换接口
		if (SystemInfo.getLanguageNumber() > 1) {
			SKLanguage.getInstance().getBinder().onRegister(languageICallback);
		}
		SKSceneManage.getInstance().onRefresh(items);
	}

	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		boolean touch = false;
		int touchX = (int) event.getX();
		int touchY = (int) event.getY();
		if (null == info || null == rectBoder) {
			return false;
		}

		if (!info.getRect().contains(touchX, touchY)) {
			if (popIsShow) {
				mPopupWindow.dismiss();
				popIsShow = false;
				touch = true;
			}

		} else {

			// 不可显现或者不可触摸
			if (!isTouchFlag || !isShowFlag) {
				if (!isTouchFlag && info != null) {
					if (info.getTouchInfo() != null) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							if (info.getTouchInfo().isbTouchByUser()) {
								SKSceneManage.getInstance().turnToLoginPop();
							}
						}
					}
				}
				return false;
			}

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				isOnClick = true;
				if (info.getTouchInfo() == null) {
					doTouch();
				} else {
					if (info.getTouchInfo().isbTimeoutCancel() == true
							&& info.getTouchInfo().getnPressTime() > 0) {
						if (null == hand) {
							hand = new myMainHandler(Looper.getMainLooper());
						}
						hand.sendEmptyMessageDelayed(TOUCHHANDER, info
								.getTouchInfo().getnPressTime() * 1000);
					} else {
						doTouch();
					}

				}
				touch = true;
			} else if (MotionEvent.ACTION_UP == event.getAction()
					|| MotionEvent.ACTION_CANCEL == event.getAction()) {
				isOnClick = false;
				touch = true;
			}

		}

		return touch;
	}

	private static final int TOUCHHANDER = 1;

	/**
	 * 按压延时handler
	 * 
	 * @author Administrator
	 * 
	 */
	private class myMainHandler extends Handler {
		public myMainHandler(Looper loop) {
			super(loop);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == TOUCHHANDER) {
				if (isOnClick) {
					doTouch();
				}
			}
		}
	}

	/**
	 * 按压操作
	 */
	private void doTouch() {
		if (null == mPopupWindow) {
			initPopupWindow();
		}
		showPopupWindow();
	}

	/**
	 * 注册地址
	 */
	private void registAddr() {
		// 触控地址
		if (touchByAddr) {
			ADDRTYPE addrType = info.getTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getTouchInfo().getTouchAddrProp(), touchCall,
						true, sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getTouchInfo().getTouchAddrProp(), touchCall,
						false, sceneId);
			}
		}
		// 显现地址
		if (showByAddr) {
			ADDRTYPE addrType = info.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, true,
						sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, false,
						sceneId);
			}

		}

		// 监控地址改变
		SKPlcNoticThread.getInstance().addNoticProp(info.getBaseAddress(),
				monitorCallBack, false, sceneId);

	}

	/**
	 * 多语言切换通知刷新
	 */
	SKLanguage.ICallback languageICallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			// TODO Auto-generated method stub
			setTextByLanguage();
			if (null != data && data.size() > 0) {
				TextInfo itemInfo = data.get(selectIndex);
				showFunctionName = itemInfo.getmTextList().get(
						SystemInfo.getCurrentLanguageId());
			}
			SKSceneManage.getInstance().onRefresh(items);

		}
	};

	/**
	 * 触控地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isTouchFlag = isTouch();
		}

	};
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShowFlag = isShow();

		}

	};
	/**
	 * 初始化窗口内容
	 */
	int length = 0;

	@SuppressLint("NewApi")
	private void initPopupWindow() {
		if (null != data && !data.isEmpty()) {
			length = data.size();
			if (info.getMaxState() < data.size()) {
				length = info.getMaxState();
				boxHeight = info.getMaxState() * 40;
			} else {
				boxHeight = data.size() * 40;
			}
		}

		if (boxHeight > 200) {
			boxHeight = 200;
		}
		SHOW_TYPE showType = SHOW_TYPE.DEFAULT;
		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			showType = SKSceneManage.getInstance().getCurrentInfo().geteType();
		}

		if (showType == SHOW_TYPE.FLOATING) {
			// 窗口
			int iDown = this.screenHeight - rectBoder.bottom;
			int iUp = this.screenHeight - rectBoder.height() - iDown;
			if (iDown > iUp) {
				if (boxHeight > iDown) {
					boxHeight = iDown;
				}
			} else {
				if (boxHeight > iUp) {
					boxHeight = iUp;
				}
			}
		} else {
			// 画面
			int iDown = this.screenHeight - rectBoder.bottom;
			int iUp = this.screenHeight - rectBoder.height() - iDown;
			if (iDown > iUp) {
				if (boxHeight > iDown) {
					boxHeight = iDown;
				}
			} else {
				if (boxHeight > iUp) {
					boxHeight = iUp;
				}
			}
		}
		if (boxHeight > 200) {
			boxHeight = 200;
		}
		mInflater = LayoutInflater.from(SKSceneManage.getInstance()
				.getCurrentScene().getContext());
		// 获取窗口布局view
		cView = mInflater.inflate(R.layout.comboxlist, null);
		// cView.setBackgroundResource(R.drawable.back_comblist);
		LinearLayout upLayout = (LinearLayout) cView.findViewById(R.id.upLine);
		LinearLayout downLayout = (LinearLayout) cView
				.findViewById(R.id.downLine);
		downLayout.setVisibility(View.GONE);
		upLayout.setVisibility(View.GONE);

		// 设置窗口的颜色
		cView.setBackgroundColor(info.getBackgroundColor());
		// 获取显示在窗口的数据

		listView = (ListView) cView.findViewById(R.id.listView);
		listView.setFocusable(true);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAlpha(50);
		// 设置listView 的每项点击事件
		// listView.setSelectionFromTop(2, 80);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;
				if (null != data && !data.isEmpty()) {
					for (int i = 0; i < length; i++) {
						if (i == position) {
							TextInfo itemInfo = data.get(i);
							if (null != itemInfo) {

								// 重新给选中的值赋给矩形显示框
								selectIndex = i;
								int languageId = SystemInfo
										.getCurrentLanguageId();
								showFunctionName = itemInfo.getmTextList().get(
										languageId);
								setStatusToAddr(selectIndex);
								SKSceneManage.getInstance().onRefresh(items);
								if (true == info.getScript()) {
									// 请求执行控件宏指令
									MacroManager.getInstance(null).Request(
											MSERV.CALLCM,
											(short) info.getScriptId());
								}
								// 执行完按钮功能，进行操作通知
								noticeAddr(info.getTouchInfo(), true);
								// 选择之后关掉窗口
								if (null != mPopupWindow && popIsShow) {
									SKSceneManage.getInstance().time = 0;
									mPopupWindow.setFocusable(false);
									mPopupWindow.dismiss();
									popIsShow = false;
								}
							}
						}
					} // 通知adapter里面改变勾选的值
					adapter.notifyDataSetChanged();
				}
			}
		});
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;

			}
		});

		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, rectBoder.width()
				+ rectBox.width(), boxHeight);
		// 做一个不在焦点外的处理事件监听
		mPopupWindow.getContentView().setOnTouchListener(
				new View.OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						SKSceneManage.getInstance().time = 0;
						mPopupWindow.setFocusable(false);
						mPopupWindow.dismiss();
						popIsShow = false;
						return true;
					}
				});
	}

	/**
	 * 显示下拉窗口的位置
	 */
	private void showPopupWindow() {
		// 窗口显示时要加上的窗口标题栏的高度
		popIsShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
		// 填充listView
		adapter = new DragdownBoxAdapt(SKSceneManage.getInstance()
				.getCurrentScene().getContext(), data);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		int i = this.screenHeight - rectBoder.bottom;
		SHOW_TYPE showType = SHOW_TYPE.DEFAULT;
		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			showType = SKSceneManage.getInstance().getCurrentInfo().geteType();
		}

		if (showType == SHOW_TYPE.FLOATING && SKSceneManage.getInstance().getCurrentInfo().isbShowTitle()) {
			// 窗口
			if (i >= boxHeight) {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top + info.getRect().height()
								+ 30);
			} else {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top - boxHeight + 30);

			}
		} else {
			// 画面
			if (i >= boxHeight) {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY, myRect.left,
						myRect.top + info.getRect().height());
			} else {

				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY, myRect.left,
						myRect.top - boxHeight);

			}
		}
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub\
		comboxIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;
	}

	private void comboxIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		comboxIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
		return isTouchFlag;

	}

	private void comboxIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(info.getTouchInfo());
		}
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {

		if (null == info) {
			return false;
		}
		if (this.itemId == itemId) {
			if (isShowFlag) {
				draw(mPaint, canvas);
			}
			initFlag = true;
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 画下拉框矩形外壳
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void draw(Paint paint, Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != info.getBackgroundImg()
				&& !"".equals(info.getBackgroundImg())) {
			if (imageItem == null) {
				imageItem = new ImageDrawItem(info.getBackgroundImg(),
						info.getRect());
			}
			imageItem.draw(paint, canvas);
		} else {
			// 画带下拉三角形的矩形
			rectItems.draw(paint, canvas);
			// 画三角形
			foldLineItem.draw(paint, canvas);
		}

		// 初始化文本
		drawTextValue(canvas);

		// 不可触控加上锁图标
		if (!isTouchFlag && SystemInfo.isbLockIcon()) {
			if (SKSceneManage.getInstance().mContext != null) {
				if (mLockBitmap == null) {
					mLockBitmap = ImageFileTool.getBitmap(R.drawable.lock,
							SKSceneManage.getInstance().mContext);
				}
			}

			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getRect().left,
						info.getRect().top, null);
			}
		}

	}

	/**
	 * 画文本
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void drawTextValue(Canvas canvas) {
		String showText = "";
		if (null != showFunctionName) {
			showText = showFunctionName;
		}
		text.setM_sTextStr(showText);
		textItem.draw(canvas);
	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();
	}

	@Override
	public void realseMemeory() {
		isOnClick = false;
		if (popIsShow && null != mPopupWindow) {
			mPopupWindow.dismiss();
			popIsShow = false;
		}

	}

	private boolean valueBool = false;
	/**
	 * 监控地址改变回调
	 */
	private IPlcNoticCallBack monitorCallBack = new IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			double key = 0;
			switch (info.getDateType()) {
			case INT_16: // 16位整数\

				if (null == mSData) {
					mSData = new Vector<Short>();
				} else {
					mSData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
						mSData);
				if (valueBool && mSData.size() != 0) {
					key = (mSData.get(0));
				}
				break;
			case POSITIVE_INT_16: // 16位正整数

				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
						mIData);
				if (valueBool && 0 != mIData.size()) {
					key = (mIData.get(0));
				}
				break;
			case INT_32: // 32位整数

				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToInts(nStatusValue, mIData);
				if (valueBool && 0 != mIData.size()) {
					key = mIData.get(0);
				}
				break;
			case POSITIVE_INT_32: // 32位正整数

				if (null == mLData) {
					mLData = new Vector<Long>();
				} else {
					mLData.clear();
				}
				valueBool = PlcRegCmnStcTools
						.bytesToUInts(nStatusValue, mLData);
				if (valueBool && 0 != mLData.size()) {
					key = (mLData.get(0));
				}
				break;
			case BCD_16:
				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
						mIData);
				if (valueBool && 0 != mIData.size()) {
					if (mIData.get(0) < 0) {
						key = 0;
					} else {
						String addressValueFromPlc = String.valueOf(mIData
								.get(0));
						if ("ERROR".equals(addressValueFromPlc)
								|| "".equals(addressValueFromPlc)) {
							key = 0;
						} else {
							if ("ERROR".equals(addressValueFromPlc)
									|| "".equals(addressValueFromPlc)) {
								key = 0;
							} else {
								try {
									double showValueDouble = mIData.get(0);
									if (showValueDouble < 0) {
										key = 0;
									} else {
										// 调用BCD码转换
										// 16位BCD码只能显示四位数 所以要判断，如果长度大于四位，则显示后四位数
										key = Double.parseDouble(DataTypeFormat
												.intToBcdStr(
														(long) showValueDouble,
														false));
									}

								} catch (Exception e) {
									e.printStackTrace();
									key = 0;
								}
							}
						}
					}
				}
				break;
			case BCD_32:
				// 调用BCD码转换
				// 32位BCD码只能显示八位数 所以要判断，如果长度大于八位，则显示后八位数
				if (null == mLData) {
					mLData = new Vector<Long>();
				} else {
					mLData.clear();
				}
				valueBool = PlcRegCmnStcTools
						.bytesToUInts(nStatusValue, mLData);
				if (valueBool && 0 != mLData.size()) {
					if (mLData.get(0) < 0) {
						key = 0;
					} else {
						String addressValueFromPlc = String.valueOf(mLData
								.get(0));
						if ("ERROR".equals(addressValueFromPlc)
								|| "".equals(addressValueFromPlc)) {
							key = 0;
						} else {
							try {
								double showValueDouble = mLData.get(0);
								if (showValueDouble < 0) {
									key = 0;
								} else {
									// 调用BCD码转换
									// 32位BCD码只能显示八位数 所以要判断，如果长度大于八位，则显示后八位数
									key = Double.parseDouble(DataTypeFormat
											.intToBcdStr(
													(long) showValueDouble,
													false));
								}

							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
								key = 0;
							}

						}
					}

				}
				break;
			case FLOAT_32: // 浮点数
				if (null == mFData) {
					mFData = new Vector<Float>();
				} else {
					mFData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToFloats(nStatusValue,
						mFData);
				if (valueBool && 0 != mFData.size()) {
					key = (mFData.get(0));
				}
				break;
			default:
				if (null == mSData) {
					mSData = new Vector<Short>();
				} else {
					mSData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
						mSData);
				if (valueBool && mSData.size() != 0) {
					key = (mSData.get(0));
				}
				break;
			}

			boolean flag = false;
			for (int i = 0; i < info.getmStakeoutList().size(); i++) {
				StakeoutInfo sInfo = info.getmStakeoutList().get(i);
				if (key == sInfo.getnCmpFactor()) {
					int nStatusId = sInfo.getnStatusId();
					ArrayList<TextInfo> textList = info.getmTextAttrList();
					if (null != textList) {
						if (i < textList.size()) {
							TextInfo tInfo = textList.get(i);
							if (tInfo.getnStatusId() == nStatusId) {
								showFunctionName = tInfo.getmTextList().get(
										SystemInfo.getCurrentLanguageId());
								flag = true;
								break;
							}
						}
					}
				}
			}
			if (!flag) {
				int size = info.getmStakeoutList().size();
				int nStatusId = info.getmStakeoutList().get(size - 1)
						.getnStatusId();
				ArrayList<TextInfo> textList = info.getmTextAttrList();
				if (null != textList) {
					for (int i = 0; i < textList.size(); i++) {
						if (nStatusId == textList.get(i).getnStatusId()) {
							showFunctionName = textList.get(i).getmTextList()
									.get(SystemInfo.getCurrentLanguageId());
						}
					}
				}
			}
			SKSceneManage.getInstance().onRefresh(items);
		}

	};

	private void setStatusToAddr(int index) {
		List<StakeoutInfo> list = info.getmStakeoutList();
		if (index == list.size() - 1) {
			return;
		}
		double value = list.get(index).getnCmpFactor();
		switch (info.getDateType()) {
		case INT_16: // 16位整数\
			setInt(value);
			break;
		case POSITIVE_INT_16: // 16位正整数
			setInt(value);
			break;
		case INT_32: // 32位整数
			setInt(value);
			break;
		case POSITIVE_INT_32: // 32位正整数
			setLong(value);
			break;
		case BCD_16: // 16位BCD码
			setInt(value);
			break;
		case BCD_32: // 32位BCD码
			setLong(value);
			break;
		case FLOAT_32: // 浮点数
			setDouble(value);
			break;
		default:
			setInt(value);
			break;
		}
	}

	private void setDouble(double value) {
		// 将writeValue写入地址
		Vector<Double> dataList = new Vector<Double>();
		dataList.add(value);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.getDateType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegDoubleData(info.getBaseAddress(), dataList,
				mSendData);
	}

	private void setInt(double value) {
		// 将writeValue写入地址
		Vector<Integer> dataList = new Vector<Integer>();
		int inputStringInt = (int) value;
		dataList.add(inputStringInt);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.getDateType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegIntData(info.getBaseAddress(), dataList,
				mSendData);
	}

	private void setLong(double value) {
		Vector<Long> dataList = new Vector<Long>();
		long inputStringDouble = (long) value;
		dataList.add(inputStringDouble);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.getDateType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		PlcRegCmnStcTools.setRegLongData(info.getBaseAddress(), dataList,
				mSendData);
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
		if (info!=null) {
			return info.getRect().left;
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getRect().top;
		}
		return -1;
	}


	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getRect().width();
		}
		return -1;
	}


	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getRect().height();
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
		if (info!=null) {
			return getColor(info.getBackgroundColor());
		}
		return null;
	}
	

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
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
		return isTouchFlag;
	}


	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		
		if (info != null) {
			if (x == info.getRect().left) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.getRect().left=x;
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			

			//外边框
			rectBoder.left=info.getRect().left;
			rectBoder.right = info.getRect().left + info.getRect().width() - rect2Width;
			
			//文本显示区域
			text.setStartX(info.getRect().left);
			
			//显示三角形区域
			rectBox.left = rectBoder.right;
			rectBox.right = rectBoder.right + rect2Width;
			
			//三角形
			for (int i = 0; i < pointList.size(); i++) {
				pointList.get(i).x=pointList.get(i).x+x-l;
			}
			
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (y == info.getRect().top) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.getRect().top=y;
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.top = info.getRect().top;
			rectBoder.bottom = info.getRect().top + info.getRect().height();
			
			//文本显示区域
			text.setStartY(info.getRect().top);
			
			//显示三角形区域
			rectBox.top = rectBoder.top ;
			rectBox.bottom = rectBoder.bottom ;
			
			//三角形
			for (int i = 0; i < pointList.size(); i++) {
				pointList.get(i).y=pointList.get(i).y+y-t;
			}
			
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (w == info.getRect().width()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int width=info.getRect().width();
			info.getRect().right=info.getRect().right+w-width;
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect=new Rect();
			
			myRect.right=w-myRect.width()+myRect.right;
			rect2Width = myRect.width() * 6 / 27;
			
			//外边框
			rectBoder.right = info.getRect().left + info.getRect().width() - rect2Width;
			
			//文本显示区域
			text.setRectWidth(rectBoder.width()-1);
			
			//显示三角形区域
			rectBox.left = rectBoder.right;
			rectBox.right = rectBoder.right + rect2Width;
			
			// 下拉三角的点集合
			int leftPX = rectBox.left + rect2Width * 1 / 4;
			int leftPY = myRect.top + myRect.height() * 7 / 18;
			int rightPX = rectBox.left + rect2Width * 3 / 4;
			int rightPY = leftPY;
			int buttomX = rectBox.left + rect2Width / 2;
			int buttomY = rectBox.top + rectBox.height() * 11 / 18;
			pointList.clear();
			pointList.add(new Point(leftPX, leftPY));
			pointList.add(new Point(buttomX, buttomY));
			pointList.add(new Point(rightPX, rightPY));
			
			mPopupWindow=null;
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (h == info.getRect().height()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			
			int temp=info.getRect().height();
			int height=info.getRect().height();
			info.getRect().bottom=info.getRect().bottom+h-height;
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.bottom = info.getRect().top+ info.getRect().height();
			
			//文本显示区域
			text.setRectHeight(rectBoder.height()-2);
			
			//显示三角形区域
			rectBox.bottom = rectBoder.bottom ;
			
			//三角形
			for (int i = 0; i < pointList.size(); i++) {
				pointList.get(i).y=pointList.get(i).y+(h-temp)/2;
			}
			
			mPopupWindow=null;
			SKSceneManage.getInstance().onRefresh(items);
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
		if (info!=null) {
			int color=Color.rgb(r, g, b);
			if (color==info.getBackgroundColor()) {
				return true;
			}
			info.setBackgroundColor(color);
			textItem.resetColor(color, 2);
			listView.setBackgroundColor(info.getBackgroundColor());
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==isShowFlag) {
			return true;
		}
		isShowFlag=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}


	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==isTouchFlag) {
			return true;
		}
		isTouchFlag=v;
		SKSceneManage.getInstance().onRefresh(items);
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
		if (info==null||alpha<0||alpha>255) {
			return false;
		}
		if(info.getAlpha()==alpha){
			return true;
		}
		info.setAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
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
