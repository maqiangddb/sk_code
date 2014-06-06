package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.ComboxListAdapt;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.FoldLineItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.ComboBoxInfo;
import com.android.Samkoonhmi.model.ComboxItemInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skbutton.ButtonInfo;
import com.android.Samkoonhmi.model.skbutton.WordButtonInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.WORD_OPER_TYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;

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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

//import SKGraphCmnTouch;
/**
 * 多功能选择按钮
 * 
 * @author Administrator
 * 
 */
public class SKCombobox extends SKGraphCmnTouch implements OnTouchListener,IItem{
	private ComboBoxInfo info;
	private Rect rectBoder;// 矩形边框
	private Rect rectBox;// 显示下拉三角形的矩形
	private PopupWindow mPopupWindow; // 显示的下拉窗口
	private View cView; // 下拉窗口的布局
	private LayoutInflater mInflater;
	private Button sureButton1; // 确定按钮1
	private Button sureButton2;
	private Button cancelButton1;// 取消按钮1
	private Button cancelButton2;//
	public boolean popIsShow; // 窗口是否弹出
	private ListView listView;// 下拉列表
	private List<ComboxItemInfo> data;
	private ComboxListAdapt adapter;
	private Vector<Point> pointList;
	private Map<Integer, String> showFunctionName;
	private int rect2Width = 20;// 显示下拉三角形的矩形宽度
	private Paint mPaint;
	private int boxHeight = 0;// 下拉触控的窗口的高度
	private int screenHeight = 0;// 屏幕的高度
	private int selectIndex = -1;
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
	private int pressColor = Color.rgb(244, 183, 87); // 按钮点击下去的背景颜色
	private int upColor = Color.rgb(183, 211, 252); // 按钮弹起的背景颜色

	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean touchByUser;
	private boolean showByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private myMainHandler hand = null;
	private Bitmap mLockBitmap;

	public SKCombobox(int itemId, int sceneId, ComboBoxInfo info) {
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
			data = new ArrayList<ComboxItemInfo>();
			data = info.getFunctionList();
			// 控件的矩形大小
			myRect = new Rect(info.getnStartX(), info.getnStartY(),
					info.getnStartX() + info.getnWidth(), info.getnStartY()
							+ info.getnHeight());
			rect2Width = myRect.width() * 6 / 27;

			// 显示文字的矩形框
			rectBoder = new Rect();
			rectBoder.left = info.getnStartX();
			rectBoder.right = info.getnStartX() + info.getnWidth() - rect2Width;
			rectBoder.top = info.getnStartY();
			rectBoder.bottom = info.getnStartY() + info.getnHeight();

			// 文字对象
			text = new StaticTextModel();
			text.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
			text.setM_nFontColor(info.getnFontColor());
			text.setM_nFontSize(info.getNfontSize());
			text.setM_textPro((short) (info.geteFontCss()));
			text.setM_backColorPadding(info.getnBackColor());
			text.setStartX(info.getnStartX() + 1);
			text.setStartY(info.getnStartY() + 1);
			text.setRectHeight(rectBoder.height() - 2);
			text.setRectWidth(rectBoder.width() - 1);
			text.setLineColor(Color.rgb(183, 211, 252));
			text.setM_sFontFamly(info.getsFontType());
			text.setLineWidth(1);
			text.setM_alphaPadding(info.getnAlpha());
			text.setBorderAlpha(255);

			textItem = new TextItem(text);
			textItem.initTextPaint();
			textItem.initRectBoderPaint();
			textItem.initRectPaint();

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
			rectItems.setAlpha(info.getnAlpha());

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
							info.getShowInfo().getShowAddrProp(), showCall,
							true, sceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getShowInfo().getShowAddrProp(), showCall,
							false, sceneId);
				}

			}
		}

	}

	/**
	 * 初始化数据
	 */
	private void init() {
		if (null == info) {
			return;
		}
		if (null != data) {
			// 初始化显示在矩形框的记录值
			for (int i = 0; i < data.size(); i++) {
				ComboxItemInfo infos = data.get(i);
				if (infos.isbSaveIndex()) {
					Map<Integer, String> functionNames = infos
							.getFunctionNames();
					if (null != functionNames) {
						showFunctionName = functionNames;
					}
					break;
				}
			}
		}

		initFlag = true;
		// boxHeight = 200;

		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			screenHeight = SKSceneManage.getInstance().getCurrentInfo()
					.getnSceneHeight();
		}
		// 注册地址接口
		registAddr();
		comboxIsShow();
		comboxIsTouch();
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

		if (touchX < info.getnStartX()
				|| touchX > info.getnStartX() + info.getnWidth()
				|| touchY < info.getnStartY()
				|| touchY > info.getnStartY() + info.getnHeight()) {
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
								.getTouchInfo().getnPressTime() * 100);
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
	private static final int SUREBUTTONTOUCH=2;

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
			}else if(msg.what == SUREBUTTONTOUCH){
				if(!sureIsOnclick){ //如果在500毫秒内还没点击确定按钮 ，则关闭窗口
					mPopupWindow.dismiss();
					popIsShow = false;
				}
			}
		}
	}

	private void doTouch() {
		if (null == mPopupWindow) {
			initPopupWindow();
		}
		showPopupWindow();
	}

	private void registAddr() {

		// 注册多语言切换接口
		SKLanguage.getInstance().getBinder().onRegister(languageICallback);
	}

	/**
	 * 多语言切换通知刷新
	 */
	SKLanguage.ICallback languageICallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			// TODO Auto-generated method stub

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
			if (info.getnShowNumber() < data.size()) {
				length = info.getnShowNumber();
				boxHeight = info.getnShowNumber() * 40 + 40;
			} else {
				boxHeight = data.size() * 40 + 40;
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
		LinearLayout listLayout = (LinearLayout) cView
				.findViewById(R.id.listLine);
		// 根据窗口显示在矩形框的下方还是上方决定 确定和取消按钮在listView上还是在listView下
		if ((this.screenHeight - rectBoder.bottom) >= boxHeight) {
			// 在下
			downLayout.setVisibility(View.VISIBLE);
		} else {
			// 在上
			upLayout.setVisibility(View.VISIBLE);
		}
		// 设置窗口的颜色
		cView.setBackgroundColor(info.getnBackColor());
		// 获取显示在窗口的数据

		listView = (ListView) cView.findViewById(R.id.listView);
		listView.setFocusable(true);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// 设置listView 的每项点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;
				if (null != data && !data.isEmpty()) {
					for (int i = 0; i < length; i++) {
						if (i == position) {
							ComboxItemInfo itemInfo = data.get(i);
							if (null != itemInfo) {
								itemInfo.setChecked(true);
								// 重新给选中的值赋给矩形显示框
								selectIndex = i;
							} else {
								data.get(i).setChecked(false);
							}
						} else {
							data.get(i).setChecked(false);
						}
					}// 通知adapter里面改变勾选的值
					adapter.notifyDataSetChanged();
				}
			}
		});
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@SuppressLint("NewApi")
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;

			}
		});
		// 获取确定和取消按钮，并加入点击事件
		sureButton1 = (Button) cView.findViewById(R.id.sure);
		sureButton2 = (Button) cView.findViewById(R.id.sure1);
		sureButton1.setBackgroundColor(upColor);
		sureButton2.setBackgroundColor(upColor);
		sureButton1.setOnTouchListener(this);
		sureButton2.setOnTouchListener(this);
		cancelButton1 = (Button) cView.findViewById(R.id.cancalbox);
		cancelButton2 = (Button) cView.findViewById(R.id.cancalbox1);
		cancelButton1.setOnTouchListener(this);
		cancelButton2.setOnTouchListener(this);
		cancelButton1.setBackgroundColor(upColor);
		cancelButton2.setBackgroundColor(upColor);
		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, rectBoder.width()
				+ rectBox.width()-1, boxHeight);
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
	@SuppressLint("NewApi")
	private void showPopupWindow() {
		// 窗口显示时要加上的窗口标题栏的高度
		popIsShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
		// 填充listView
		adapter = new ComboxListAdapt(SKSceneManage.getInstance()
				.getCurrentScene().getContext(), data, info);
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
						myRect.left + 1, myRect.top + info.getnHeight() + 30);
			} else {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top - boxHeight + 30);

			}
		} else {
			// 画面
			if (i >= boxHeight) {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY, myRect.left+1,
						myRect.top + info.getnHeight());
			} else {

				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY, myRect.left+1,
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
		// 初始化文本
		drawTextValue(canvas);
		// 画带下拉三角形的矩形
		rectItems.draw(paint, canvas);
		// 画三角形
		foldLineItem.draw(paint, canvas);
		// 不可触控加上锁图标
		if (!isTouchFlag && SystemInfo.isbLockIcon()) {
			if (SKSceneManage.getInstance().mContext != null) {
				if (mLockBitmap == null) {
					mLockBitmap = ImageFileTool.getBitmap(R.drawable.lock,
							SKSceneManage.getInstance().mContext);
				}
			}

			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getnStartX(),
						info.getnStartY(), null);
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
			showText = showFunctionName.get(SystemInfo.getCurrentLanguageId());
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

	/**
	 * 确定按钮事件
	 */
	boolean sureflag = false;
	boolean sureIsOnclick= false;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		boolean touch = false;
		// 确定执行功能按钮
		if (v.getId() == R.id.sure || v.getId() == R.id.sure1) {
			// 没有选中一个功能
			if (selectIndex == -1) {

				// 按下再松开则提示
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 设置按钮点击下去的背景颜色
					sureButton1.setBackgroundColor(pressColor);
					sureButton2.setBackgroundColor(pressColor);
					sureflag = true;
					touch = true;
					
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| MotionEvent.ACTION_CANCEL == event.getAction()) {
					// 按钮弹起的颜色
					sureButton1.setBackgroundColor(upColor);
					sureButton2.setBackgroundColor(upColor);
				
					if (sureflag) {
						SKToast.makeText(
								SKSceneManage.getInstance().getCurrentScene()
										.getContext(), R.string.selectcontrol,
								Toast.LENGTH_SHORT).show();
						touch = true;
					}
				}

			} else {
				// 功能按钮对象
				ComboxItemInfo comboxItem = info.getFunctionList().get(
						selectIndex);
				SKButton button = comboxItem.getButton();
				ButtonInfo BInfo=button.getInfo();
				boolean bWordLoop= false;//标识是否是字按钮的连加或连减
				if (BInfo.geteButtonType()==BUTTON_TYPE.WORD) {
					WordButtonInfo wInfo=(WordButtonInfo)BInfo;
					if(wInfo.geteOperType()==WORD_OPER_TYPE.ADD_LOOPER||wInfo.geteOperType()==WORD_OPER_TYPE.MINUS_LOOPER){
						bWordLoop = true;
					}
				}

				Log.d("combox", "event.getAction() ="+event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 设置按钮点击下去的背景颜色
					sureButton1.setBackgroundColor(pressColor);
					sureButton2.setBackgroundColor(pressColor);
					SKSceneManage.getInstance().onRefresh(items);
					button.doTouch(true, true,event.getAction());
					touch = true;
					sureIsOnclick = true;
					//记录操作
					setOperate(comboxItem);
					if (true == info.isbIsStartStatement()) {
						// 请求执行控件宏指令
						MacroManager.getInstance(null).Request(MSERV.CALLCM,
								(short) info.getnScriptId());
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| MotionEvent.ACTION_CANCEL == event.getAction()) {

					// 按钮弹起的颜色
					sureButton1.setBackgroundColor(upColor);
					sureButton2.setBackgroundColor(upColor);
					SKSceneManage.getInstance().onRefresh(items);
					button.doTouch(false, true,event.getAction());
					sureIsOnclick = false;
					if(bWordLoop){
						if(null == hand){
							hand = new myMainHandler(Looper.getMainLooper());
						}
						hand.sendEmptyMessageDelayed(SUREBUTTONTOUCH, 500);
					}else{
						mPopupWindow.dismiss();
						popIsShow = false;
					}
					touch = true;
				}

				// 执行完按钮功能，进行操作通知
				noticeAddr(info.getTouchInfo(), true);

			}
		} else if (v.getId() == R.id.cancalbox || v.getId() == R.id.cancalbox1) {
			// 按下再松开则提示
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// 设置按钮点击下去的背景颜色
				cancelButton1.setBackgroundColor(pressColor);
				cancelButton1.setBackgroundColor(pressColor);
				sureflag = true;
				touch = true;
			} else if (event.getAction() == MotionEvent.ACTION_UP
					|| MotionEvent.ACTION_CANCEL == event.getAction()) {
				// 按钮弹起的颜色
				cancelButton1.setBackgroundColor(upColor);
				cancelButton2.setBackgroundColor(upColor);
				if (sureflag) {
					cancelOnclick();
					touch = true;
				}
			}
		}

		return touch;
	}

	/**
	 * 记录操作
	 * 
	 * @param comboxItem
	 */
	private void setOperate(ComboxItemInfo comboxItem) {
		// 修改数据库的标识
		DBTool.getInstance().getmComboxInfoBiz().updateSate(comboxItem);
		// 修改实体类属性
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				ComboxItemInfo in = data.get(i);
				if (in.getFunctionId() != comboxItem.getFunctionId()) {
					in.setbSaveIndex(false);
				} else {
					in.setbSaveIndex(true);
				}
			}
		}
		// 通知界面刷新显示
		Map<Integer, String> functionNames = comboxItem.getFunctionNames();
		if (null != functionNames) {
			showFunctionName = functionNames;
			SKSceneManage.getInstance().onRefresh(items);
		}
	}

	/**
	 * 按下取消按钮要执行的动作
	 */
	private void cancelOnclick() {
		if (popIsShow) {
			mPopupWindow.dismiss();
			popIsShow = false;
		}
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
			return info.getnStartX();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnStartY();
		}
		return -1;
	}


	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnWidth();
		}
		return -1;
	}


	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnHeight();
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
			return getColor(info.getnBackColor());
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
			if (x == info.getnStartX()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnStartX(x);
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.left=info.getnStartX();
			rectBoder.right = info.getnStartX() + info.getnWidth() - rect2Width;
			
			//文本显示区域
			text.setStartX(info.getnStartX());
			
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
			if (y == info.getnStartY()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnStartY(y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.top = info.getnStartY();
			rectBoder.bottom = info.getnStartY() + info.getnHeight();
			
			//文本显示区域
			text.setStartY(info.getnStartY());
			
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
			if (w == info.getnWidth()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnWidth((short)w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect=new Rect();
			
			myRect.right=w-myRect.width()+myRect.right;
			rect2Width = myRect.width() * 6 / 27;
			
			//外边框
			rectBoder.right = info.getnStartX() + info.getnWidth() - rect2Width;
			
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
			if (h == info.getnHeight()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int temp=info.getnHeight();
			info.setnHeight((short)h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.bottom = info.getnStartY() + info.getnHeight();
			
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
			if (color==info.getnBackColor()) {
				return true;
			}
			info.setnBackColor(color);
			textItem.resetColor(color, 2);
			listView.setBackgroundColor(info.getnBackColor());
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