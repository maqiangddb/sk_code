//import SKGraphCmnTouch;
package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.math.BigDecimal;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.AddrPropBiz;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.NumberDisplayInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.INPUT_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skenum.SHOWAREA;
import com.android.Samkoonhmi.skenum.WINDOW_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TextAttribute;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 数值输入显示器
 * 
 * @author 瞿丽平
 * 
 */
public class SKNumInputDisplay extends SKGraphCmnTouch {
	private NumberDisplayInfo info;
	private Rect mRect;
	private Paint mPaint;
	private double sourceMax = 65535; // 源范围最大值
	private double sourceMin = 0; // 源范围最小值
	private double showMax = 65535; // 显示最大值
	private double showMin = 0; // 显示最小值
	private double addressValue = 0; // 监控地址值
	private String showValue = "0";// 显示值
	private double baseNumber = 1;// 根据小数位数得到显示的数值（显示值的偏移）
	private boolean flag = true;
	private StaticTextModel text;
	private boolean flagError = false;// 标志是否显示error
	private SKKeyPopupWindow popKey = null;
	private String inputKeyString;
	private TextItem textItem;
	private Rect textRect;
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean touchByUser;
	private boolean showByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private boolean initFlag;
	private SKItems items;
	private int itemId;
	private int senceId;
	private boolean isOnClick;
	private GlobalPopWindow pop;
	private ImageDrawItem imageItem;
	private Vector<Short> mSData = null;
	private Vector<Integer> mIData = null;
	private Vector<Long> mLData = null;
	private Vector<Float> mFData = null;
	private final static int SOURCEMAX = 1;
	private final static int SOURCEMIN = 2;
	private final static int SHOWMAX = 3;
	private final static int SHOWMIN = 4;
	private final static int NUMBER = 5;
	private final static int INPUTMAX = 6;
	private final static int INPUTMIN = 7;
//	private int gloabledecimalNumber = 0;// 小数位数
	private double inputMax = 65535;
	private double inputMin = 0;
	private Bitmap bgBitmap; // 背景图片
	private Context mContext;
	private myMainHandler hand = null;
	private boolean notTouchOpenKey = false;
	private int currentColor =0;
	private Bitmap mLockBitmap;
	private Paint mBitmapPaint;//用于画图片

	public SKNumInputDisplay(Context context, int itemId, int senceId,
			NumberDisplayInfo info) {
		mContext = context;
		isOnClick = false;
		isTouchFlag = true;
		isShowFlag = true;
		touchByUser = false;
		showByUser = false;
		showByAddr = false;
		touchByAddr = false;
		initFlag = true;
		this.senceId = senceId;
		this.itemId = itemId;
		mPaint = new Paint();
		items = new SKItems();
		this.info = info;
		notTouchOpenKey = false;
		
		if (info!=null) {
			mRect = new Rect();
			mRect.left = info.getnStartX();
			mRect.right = info.getnStartX() + info.getnWidth();
			mRect.top = info.getnStartY();
			mRect.bottom = info.getnStartY() + info.getnHeight();
			
			textRect = new Rect(info.getnTextStartX(), info.getnTextStartY(),
					info.getnTextStartX() + info.getnTextWidth(),
					info.getnTextStartY() + info.getnTextHeight());
			
			text = new StaticTextModel();
			text.setM_eTextAlign(info.geteShowStyle());
			text.setM_nFontColor(info.getnFontColor());
			text.setM_nFontSize(info.getnFontSize());
			text.setM_textPro((short) (info.geteFontCss()));
			text.setM_sFontFamly(info.getsFontType());
			text.setStartX(info.getnTextStartX());
			text.setStartY(info.getnTextStartY());
			text.setRectHeight(textRect.height());
			text.setRectWidth(textRect.width());
			text.setLineWidth(0);
			text.setM_alphaPadding(info.getnTransparent());// 设置透明度
//			if (info.getnTransparent() == 0) {
//				text.setBorderAlpha(255);
//				text.setLineColor(Color.BLACK);
//				text.setLineWidth(1);
//			}

			text.setM_backColorPadding(info.getnBackColor());
			
			textItem = new TextItem(text);
			textItem.initTextPaint();
			textItem.initRectBoderPaint();
			textItem.initRectPaint();
			
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.senceId;
			items.rect = mRect;
			items.mGraphics=this;
		}
		
	}

	/**
	 * 初始化方法
	 */
	private void init() {

		if (null == info) {
			return;
		}

		initFlag = true;
		 currentColor = info.getnBackColor();
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
		
		numberIsShow();
		numberIsTouch();

		// 注册地址接口
		registAddr();
		// 获取小数位数值
		getDecimaNumber();
		// 获取允许输入的最大最小值
		getInputMaxMinValue();
		// 缩放 拿最大最小值
		if (info.isbIsScale()) {
			getMaxMinValue();
		}
		// 没有进行缩放，根据数据类型确定最大，最小值
		else {
			sourceMax = getMaxNumber(info.geteNumberType());
			showMax = sourceMax;
			sourceMin = getMinNumber(info.geteNumberType());
			showMin = sourceMin;
		}
		if (null == popKey) {
			popKey = new SKKeyPopupWindow(SKSceneManage.getInstance().mContext,
					true, info.getnKeyId(), info.geteNumberType());
			popKey.setCallback(callback);
			if ((info.geteFontCss() & TextAttribute.PASSWORD) == TextAttribute.PASSWORD) {
				popKey.setPassWord(true);
			} else {
				popKey.setPassWord(false);
			}
		}
		if ((info.geteFontCss() & TextAttribute.PASSWORD) == TextAttribute.PASSWORD) {
			pop = new GlobalPopWindow(SKSceneManage.getInstance()
					.getCurrentScene(), WINDOW_TYPE.KEYBOARD, 4,
					info.geteNumberType());// 4代表数值用密码的形式显示
		} else {
			pop = new GlobalPopWindow(SKSceneManage.getInstance()
					.getCurrentScene(), WINDOW_TYPE.KEYBOARD, 1,
					info.geteNumberType());// 1 代表数值
		}

		SKSceneManage.getInstance().onRefresh(items);

	}

	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.

	}

	/**
	 * 取输入的最大最小值
	 */
	private void getInputMaxMinValue() {
		// 如果允许输入 并且输入类型是常量 如果是地址 则通过通知取得
		if (info.isbIsInput() && info.geteInputAreaType() == SHOWAREA.CONSTANT) {
			inputMax = info.getnInputMax();
			inputMin = info.getnInputMin();
		}
	}

	/**
	 * 没有进行缩放时，根据数据类型求最小值
	 * 
	 * @return
	 */
	private double getMinNumber(DATA_TYPE dataType) {
		double temp = 0;
		switch (dataType) {
		case INT_16: // 16位整数
			temp = -32768;
			break;
		case POSITIVE_INT_16: // 16位正整数
			temp = 0;
			break;
		case INT_32: // 32位整数
			temp = -2147483648;
			break;
		case POSITIVE_INT_32: // 32位正整数
			temp = 0;
			break;
		case BCD_16:
			temp = 0;
			break;
		case BCD_32:
			temp = 0;
			break;
		case FLOAT_32: // 浮点数
			temp = -2147483648;
			break;
		case OTC_16: // 16位8进制
			temp = 0;
			break;
		case OTC_32: // 32位的8进制
			temp = 0;
			break;
		case HEX_16: // 16位16进制
			temp = 0;
			break;
		case HEX_32: // 32位16进制
			temp = 0;
			break;
		default:
			temp = 0;
			break;
		}
		return temp;
	}

	/**
	 * /没有进行缩放的时候，根据数据类型求最大值
	 * 
	 */
	private double getMaxNumber(DATA_TYPE dataType) {
		double temp = 0;
		switch (dataType) {
		case INT_16: // 16位整数
			temp = 32767;
			break;
		case POSITIVE_INT_16: // 16位正整数
			temp = 65535;
			break;
		case INT_32: // 32位整数
			temp = 2147483647;
			break;
		case POSITIVE_INT_32: // 32位正整数
			temp = 4294967295L;
			break;
		case BCD_16:
			temp = 9999;
			break;
		case BCD_32:
			temp = 99999999;
			break;
		case FLOAT_32: // 浮点数
			temp = 2147483647;
			break;
		case OTC_16: // 16位8进制
			temp = 177777;
			break;
		case OTC_32: // 32位的8进制
			temp = 37777777777L;
			break;
		case HEX_16: // 16位16进制
			temp = 65535;
			break;
		case HEX_32: // 32位16进制
			temp = 4294967295L;
			break;
		default:
			temp = 0;
			break;
		}
		return temp;
	}

	/**
	 * 自定义键盘输入响应事件
	 */
	SKKeyPopupWindow.ICallback callback = new SKKeyPopupWindow.ICallback() {

		@Override
		public void onResult(String result, KEYBOARD_OPERATION type) {
			// TODO Auto-generated method stub
			// 关闭键盘 把背景去掉
			if (drawBack) {
				drawBack = false;
				SKSceneManage.getInstance().onRefresh(items);
			}
			if (type == KEYBOARD_OPERATION.ENTER) {
				inputKeyString = result;

				if (null != result && !"".equals(result)) {
					InputShow(inputKeyString);
				}
			}

		}
	};

	/**
	 * 判断是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern
				.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$|^-\\d+\\.\\d+$");
		if (null == str || "".equals(str)) {
			return false;
		}
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 自定义键盘
	 */
	private void openBoard() {
		popKey.setLastText(showValue);
		long pd = (long) Math.pow(10, decimaNumberGloble);
		double mx = inputMax / pd;
		double mi = inputMin / pd;
		String mxs = Double.toString(mx);
		String mis = Double.toString(mi);
		try {

			BigDecimal b = new BigDecimal(mx).setScale(decimaNumberGloble,
					BigDecimal.ROUND_HALF_UP);
			mxs = b.toPlainString();
			BigDecimal bmi = new BigDecimal(mi).setScale(decimaNumberGloble,
					BigDecimal.ROUND_HALF_UP);
			mis = bmi.toPlainString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (SKKeyPopupWindow.keyFlagIsShow && !GlobalPopWindow.popIsShow) {
			popKey.setShowMax(mxs);
			popKey.setShowMin(mis);
			popKey.setnStartX(info.getnBoardX());
			popKey.setnStartY(info.getnBoardY());
			popKey.initPopUpWindow();
			// 允许输入 并且勾选了输入提示 则更换背景
			if (info.isbIsInput() && info.isbInputSign()) {
				drawBack = true;
				SKSceneManage.getInstance().onRefresh(items);
			}
			popKey.showPopUpWindow();
		}

	}

	/**
	 * 系统键盘
	 */
	private void openKey() {
		if (!GlobalPopWindow.popIsShow && null != pop
				&& SKKeyPopupWindow.keyFlagIsShow) {
			pop.setCallback(keyback);
			pop.initPopupWindow();
			if (null != pop) {
				long pd = (long) Math.pow(10, decimaNumberGloble);
				double mx = inputMax / pd;
				double mi = inputMin / pd;
				String mxs = Double.toString(mx);
				String mis = Double.toString(mi);
				try {

					BigDecimal b = new BigDecimal(mx).setScale(
							decimaNumberGloble, BigDecimal.ROUND_HALF_UP);
					mxs = b.toPlainString();
					BigDecimal bmi = new BigDecimal(mi).setScale(
							decimaNumberGloble, BigDecimal.ROUND_HALF_UP);
					mis = bmi.toPlainString();
				} catch (Exception e) {
					// TODO: handle exception
				}
				pop.setInputMax(mxs);
				pop.setInputMin(mis);
				// 允许输入 并且勾选了输入提示 则更换背景
				if (info.isbIsInput() && info.isbInputSign()) {
					drawBack = true;
					SKSceneManage.getInstance().onRefresh(items);
				}
				pop.showPopupWindow();
			}
		}
	}

	/**
	 * 系统键盘回调函数
	 */
	GlobalPopWindow.ICallBack keyback = new GlobalPopWindow.ICallBack() {

		@Override
		public void inputFinish(String result) {
			// TODO Auto-generated method stub

			if (null != result && !"".equals(result)) {
				InputShow(result);
			}

		}

		// 当关闭popWindow 时 是否 启动定时器
		@Override
		public void onStart() {
			// 关闭键盘 把背景去掉
			if (drawBack) {
				drawBack = false;
				SKSceneManage.getInstance().onRefresh(items);
			}
			SKSceneManage.getInstance().timeOut();
		}

		// 窗口显示的时候，判断定时器是否打开 如果打开则销毁
		@Override
		public void onShow() {
			// TODO Auto-generated method stub
			if (SKTimer.getInstance().getBinder()
					.isRegister(SKSceneManage.getInstance().sCallback)) {
				SKTimer.getInstance().getBinder()
						.onDestroy(SKSceneManage.getInstance().sCallback);
			}
		}
	};
	// 点击输入画输入框提示框
	private boolean drawBack = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// System.out.println("skNumber touch =="+isOnClick);
		SKSceneManage.getInstance().time = 0;
		boolean touch = false;
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (null == info) {
			return false;
		}
		
		if (info.geteInputTypeId() == INPUT_TYPE.BIT) {
			return false;
		}
		if (x < info.getnStartX() || x > info.getnStartX() + info.getnWidth()
				|| y < info.getnStartY()
				|| y > info.getnStartY() + info.getnHeight()) {
			return false;
		} else {
			if(!info.isbIsInput()){ //不允许输入 直接返回
				return false;
			}else{
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
						doTouch(notTouchOpenKey);
					} else {
						if (info.getTouchInfo().isbTimeoutCancel() == true
								&& info.getTouchInfo().getnPressTime() > 0) {
							if (null == hand) {
								hand = new myMainHandler(Looper.getMainLooper());
							}
							hand.sendEmptyMessageDelayed(TOUCHHANDER, info
									.getTouchInfo().getnPressTime() * 1000);
						} else {
							doTouch(notTouchOpenKey);
						}

					}
					touch = true;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				} else if (MotionEvent.ACTION_UP == event.getAction()
						|| MotionEvent.ACTION_CANCEL == event.getAction()) {
					isOnClick = false;
					touch = true;
				}
			}
			}
			
		return touch;

	}

	private static final int TOUCHHANDER = 1;

	private class myMainHandler extends Handler {
		public myMainHandler(Looper loop) {
			super(loop);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == TOUCHHANDER) {
				if (isOnClick || notTouchOpenKey) {
					doTouch(notTouchOpenKey);
					notTouchOpenKey = false;
				}
			}
		}
	}

	private void doTouch(boolean bitFlag) {

		if (info.isbIsInput()) {
			if (info.getnKeyId() == -1) // 为-1 时调用系统键盘
			{
				// 打开系统键盘
				openKey();
			} else {
				// 打开自定义键盘
				openBoard();
			}
			// 如果是由位地址控制键盘打开的 并且勾选了自动复位 则将那个位地址值复位
			if (bitFlag && info.isbAutoChangeBit()) {
				setBit(info.getsBitAddress(), 0);
			}
		}
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub\
		numberIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;
	}

	private void numberIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		numberIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
		return isTouchFlag;

	}

	private void numberIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(info.getTouchInfo());
		}
	}

	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();

	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		// TODO Auto-generated method stub
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

	private Rect bgRect;// 背景矩形框

	private void draw(Paint paint, Canvas canvas) {

		// 画外形
		if (null != info.getsShapId()) {
			if (imageItem == null) {
				imageItem = new ImageDrawItem(info.getsShapId(), mRect);
			}
			imageItem.draw(paint, canvas);
		}
		drawTextValue(paint, canvas);
		if (drawBack) {
			if (bgBitmap == null) {
				bgBitmap = ImageFileTool.getBitmap(R.drawable.rect_red_bg,
						mContext);
				bgRect = new Rect(0, 0, bgBitmap.getWidth(),
						bgBitmap.getHeight());
			}
			if (bgBitmap != null) {
				if (mBitmapPaint==null) {
					mBitmapPaint=new Paint();
					mBitmapPaint.setDither(true);
					mBitmapPaint.setAntiAlias(true);
				}
				canvas.drawBitmap(bgBitmap, bgRect, mRect, mBitmapPaint);
			}
		}
		// 不可触控加上锁图标
		if (!isTouchFlag) {
			if (mLockBitmap == null) {
				mLockBitmap = ImageFileTool
						.getBitmap(R.drawable.lock, mContext);
			}
			if (mLockBitmap != null) {
				if (mBitmapPaint==null) {
					mBitmapPaint=new Paint();
					mBitmapPaint.setDither(true);
					mBitmapPaint.setAntiAlias(true);
				}
				canvas.drawBitmap(mLockBitmap, info.getnStartX(), info.getnStartY(),
						mBitmapPaint);
			}
		}

	}

	/**
	 * 画文本
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void drawTextValue(Paint paint, Canvas canvas) {
		// 画文本
		showValue = getBaseNumber(oldValue, false);
		// 用密码的形式显示输入的数字
		if ((info.geteFontCss() & TextAttribute.PASSWORD) == TextAttribute.PASSWORD) {
			showValue = "******";
		}
		text.setM_sTextStr(showValue);
		textItem.draw(canvas);
	}

	/**
	 * 注册地址接口
	 */
	private void registAddr() {
		// 注册显示值地址
		if (null != info.getnAddress()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getnAddress(),
					call, false);
		}
		
		//地址偏移
		if (null!=info.getmOffSetAddr()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getmOffSetAddr(),
					addOffSet, false);
		}
		
		//注册地址偏移
		if (null!=info) {
			
		}
		// 触控地址
		if (touchByAddr) {
			// Log.d("plc", "SKNumberInput 注册触控通知");
			ADDRTYPE addrType = info.getTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance()
						.addNoticProp(info.getTouchInfo().getTouchAddrProp(),
								touchCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getTouchInfo().getTouchAddrProp(), touchCall,
						false);
			}
		}
		// 显现地址
		if (showByAddr) {
			// Log.d("plc", "SKNumberInput 注册显现通知");
			ADDRTYPE addrType = info.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, false);
			}

		}
		if (info.geteDecimalType() == SHOWAREA.ADDRESS) {
			// 小数位数地址
			if (null != info.getDecimaNumberAddrProp()) {
				// Log.d("plc", "SKNumberInput 注册取小数位数通知");
				SKPlcNoticThread.getInstance()
						.addNoticProp(info.getDecimaNumberAddrProp(),
								decimaNumberCall, false);
			}
		}
		if (info.geteSourceArea() == SHOWAREA.ADDRESS && info.isbIsScale()) {
			// 源范围最大值地址
			if (null != info.getSourceMaxAddrProp()) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getSourceMaxAddrProp(), sourceMaxCall, false);

			}
			// 源范围最小值地址
			if (null != info.getSourceMinAddrProp()) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getSourceMinAddrProp(), sourceMinCall, false);
			}
		}
		if (info.getnShow() == SHOWAREA.ADDRESS && info.isbIsScale()) {
			// 显现最大值地址
			if (null != info.getShowMaxAddrProp()) {
				// Log.d("number", "注册显现最大值地址");
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowMaxAddrProp(), showMaxCall, false);
			}
			// 显现最小值地址
			if (null != info.getShowMinAddrProp()) {
				// Log.d("number", "注册显现最小值地址");
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowMinAddrProp(), showMinCall, false);
			}
		}
		if (info.geteInputTypeId() == INPUT_TYPE.BIT) {
			// 位控制键盘地址
			if (null != info.getsBitAddress()) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getsBitAddress(), bitKeyBoard, true);
			}
		}
		// 输入的最大值
		if (info.isbIsInput() && info.getInputMaxAddr() != null
				&& SHOWAREA.ADDRESS == info.geteInputAreaType()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getInputMaxAddr(),
					inputMaxCall, false);
		}
		// 输入的最小值
		if (info.isbIsInput() && info.getInputMinAddr() != null
				&& SHOWAREA.ADDRESS == info.geteInputAreaType()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getInputMinAddr(),
					inputMinCall, false);
		}
	}

	/**
	 * 允许输入的最大值
	 */
	SKPlcNoticThread.IPlcNoticCallBack inputMaxCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			boolean fvb = converDataTypeFromPlc(nStatusValue, INPUTMAX);
			if (fvb)// 修改成功，通知刷新
			{
				SKSceneManage.getInstance().onRefresh(items);
			}

		}

	};
	/**
	 * 允许输入的最小值
	 */
	SKPlcNoticThread.IPlcNoticCallBack inputMinCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			boolean fvb = converDataTypeFromPlc(nStatusValue, INPUTMIN);
			if (fvb)// 修改成功，通知刷新
			{
				SKSceneManage.getInstance().onRefresh(items);
			}

		}

	};

	/**
	 * 位地址控制键盘是否弹出
	 */
	SKPlcNoticThread.IPlcNoticCallBack bitKeyBoard = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			int addrValue = 0;
			if (null != nStatusValue) {
				if (0 != nStatusValue.size()) {
					addrValue = nStatusValue.get(0);
					// 位地址值为1的时候 键盘可以弹出
					if (addrValue == 1) {
						if (null == hand) {
							hand = new myMainHandler(Looper.getMainLooper());
						}
						hand.sendEmptyMessageDelayed(TOUCHHANDER, 10);
						notTouchOpenKey = true;
						// keyIsShow = true;
					}
				}
			} else {
				//Log.d("number", "bitKeyBoard 通知转换值失败");
			}

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
	
	//地址偏移
	SKPlcNoticThread.IPlcNoticCallBack addOffSet=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				if(info.getnAddress()!=null){
					Vector<Short> mIData = new Vector<Short>();
			       //16位整数
					boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
							mIData);
					if (result) {
						int value=mIData.get(0);
						int temp=info.getnAddress().nAddrValue+value;
						if (temp<0) {
							showValue = "0";
							oldValue = showValue;
							SKPlcNoticThread.getInstance().destoryCallback(call);
							SKSceneManage.getInstance().onRefresh(items);
							return;
						}
						if (info.getmOffSetAddress()==null) {
							AddrProp addrProp = new AddrProp();
							addrProp.eAddrRWprop = info.getnAddress().eAddrRWprop;
							addrProp.eConnectType = info.getnAddress().eConnectType;
							addrProp.nAddrId = info.getnAddress().nAddrId;
							addrProp.nAddrLen = info.getnAddress().nAddrLen;
							addrProp.nAddrValue = temp;
							addrProp.nPlcStationIndex = info.getnAddress().nPlcStationIndex;
							addrProp.nRegIndex = info.getnAddress().nRegIndex;
							addrProp.nUserPlcId = info.getnAddress().nUserPlcId;
							addrProp.sPlcProtocol = info.getnAddress().sPlcProtocol;
							info.setmOffSetAddress(addrProp);
						}else {
							info.getmOffSetAddress().nAddrValue=temp;
						}
						
						SKPlcNoticThread.getInstance().destoryCallback(call);
						SKPlcNoticThread.getInstance().addNoticProp(info.getmOffSetAddress(), call, false);
						
						if (info.getnAddress().eConnectType>1) {
							SKSceneManage.getInstance().updateSceneReadAddrs(senceId, info.getmOffSetAddress());
						}
					}
				}
			}
		}
		
	};

	/**
	 * 根据高位色低位色设置背景颜色
	 */
	
	private synchronized void setRectPaint(double dValue) {
		if (dValue > info.getnHightNumber()) {
			// 没有设置高位色背景 并且显示值大于高位值 设置颜色
			if(currentColor != info.getnHightColor()){
				text.setM_backColorPadding(info.getnHightColor());
				textItem.initRectPaint();
				currentColor = info.getnHightColor();
			}
			
		} else if (dValue < info.getnLowerNumber()) {
			// 没有设置低位色背景 并且显示值小于低位值 设置颜色
			if(currentColor != info.getnLowerColor()){
				text.setM_backColorPadding(info.getnLowerColor());
				textItem.initRectPaint();
				currentColor = info.getnLowerColor();
			}
			
		} else {
			// 如果设置了高位色背景 或者低位色背景
			if(currentColor != info.getnBackColor()){
				text.setM_backColorPadding(info.getnBackColor());
				textItem.initRectPaint();
				currentColor = info.getnBackColor();
			}
			
		}
	}

	/**
	 * plc通知改变值接口
	 */
	private boolean valueBool = false;
	private boolean fv = false;
	private String addressValueFromPlc = "";
	private double showValueDouble = 0;
	private String oldValue = "";
	SKPlcNoticThread.IPlcNoticCallBack call = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			switch (info.geteNumberType()) {
			case INT_16: // 16位整数\
				if (null == mSData) {
					mSData = new Vector<Short>();
				} else {
					mSData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
						mSData);
				if (valueBool && mSData.size() != 0) {
					showValueDouble = showScale(mSData.get(0));
					setRectPaint(showValueDouble);
					showValue = Short.toString((short) showValueDouble);
					fv = true;
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
					showValueDouble = showScale(mIData.get(0));
					setRectPaint(showValueDouble);
					showValue = Integer.toString((int) showValueDouble);
					fv = true;
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
					showValueDouble = showScale(mIData.get(0));
					setRectPaint(showValueDouble);
					showValue = Integer.toString((int) showValueDouble);
					fv = true;
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
					showValueDouble = showScale(mLData.get(0));
					setRectPaint(showValueDouble);
					showValue = Long.toString((long) showValueDouble);
					fv = true;
				}
				break;
			case BCD_16:
				// 调用BCD码转换
				// 16位BCD码只能显示四位数 所以要判断，如果长度大于四位，则显示后四位数
				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
						mIData);
				if (valueBool && 0 != mIData.size()) {
					if (mIData.get(0) < 0) {
						flagError = true;
						showValue = "ERROR";
					} else {
						addressValueFromPlc = String.valueOf(mIData.get(0));
						if ("ERROR".equals(addressValueFromPlc)
								|| "".equals(addressValueFromPlc)) {
							showValue = "ERROR";
						} else {
							if ("ERROR".equals(addressValueFromPlc)
									|| "".equals(addressValueFromPlc)) {
								showValue = "ERROR";
							} else {
								try {
									showValueDouble = showScale(mIData.get(0));
									setRectPaint(showValueDouble);
									if (showValueDouble < 0) {
										flagError = true;
										showValue = "ERROR";
									} else {
										// 调用BCD码转换
										// 16位BCD码只能显示四位数 所以要判断，如果长度大于四位，则显示后四位数
										showValue = DataTypeFormat.intToBcdStr(
												(long) showValueDouble, false);
									}

								} catch (Exception e) {
									e.printStackTrace();
									showValue = addressValueFromPlc;
								}
							}
						}

					}
					fv = true;
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
						showValue = "ERROR";
					} else {
						addressValueFromPlc = String.valueOf(mLData.get(0));
						if ("ERROR".equals(addressValueFromPlc)
								|| "".equals(addressValueFromPlc)) {
							showValue = "ERROR";
						} else {
							try {
								showValueDouble = showScale(mLData.get(0));
								setRectPaint(showValueDouble);
								if (showValueDouble < 0) {
									flagError = true;
									showValue = "ERROR";
								} else {
									// 调用BCD码转换
									// 32位BCD码只能显示八位数 所以要判断，如果长度大于八位，则显示后八位数
									showValue = DataTypeFormat.intToBcdStr(
											(long) showValueDouble, false);
								}

							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
								showValue = addressValueFromPlc;
							}

						}
					}
					fv = true;
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
					float showValueDouble = showScaleFloat(mFData.get(0));
					setRectPaint(showValueDouble);
					showValue = Float.toString(showValueDouble);
					fv = true;
				}
				break;
			case OTC_16: // 16位8进制
				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
						mIData);
				if (valueBool && 0 != mIData.size()) {
					showValueDouble = showScale(mIData.get(0));
					setRectPaint(showValueDouble);
					showValue = DataTypeFormat
							.intToOctStr((long) showValueDouble);
					fv = true;
				}
				break;
			case OTC_32: // 32位的8进制
				if (null == mLData) {
					mLData = new Vector<Long>();
				} else {
					mLData.clear();
				}
				valueBool = PlcRegCmnStcTools
						.bytesToUInts(nStatusValue, mLData);
				if (valueBool && 0 != mLData.size()) {
					showValueDouble = showScale(mLData.get(0));
					setRectPaint(showValueDouble);
					showValue = DataTypeFormat
							.intToOctStr((long) showValueDouble);
					fv = true;
				}
				break;
			case HEX_16: // 16位16进制
				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
						mIData);
				if (valueBool && 0 != mIData.size()) {
					showValueDouble = showScale(mIData.get(0));
					setRectPaint(showValueDouble);
					showValue = DataTypeFormat
							.intToHexStr((long) showValueDouble);

					fv = true;
				}

				break;
			case HEX_32: // 32位16进制
				if (null == mLData) {
					mLData = new Vector<Long>();
				} else {
					mLData.clear();
				}
				valueBool = PlcRegCmnStcTools
						.bytesToUInts(nStatusValue, mLData);
				if (valueBool && 0 != mLData.size()) {
					showValueDouble = showScale(mLData.get(0));
					setRectPaint(showValueDouble);
					showValue = DataTypeFormat
							.intToHexStr((long) showValueDouble);
					fv = true;
				}
				break;
			default:
				if (null == mIData) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}
				valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
						mIData);
				if (valueBool && 0 != mIData.size()) {
					showValueDouble = showScale(mIData.get(0));
					setRectPaint(showValueDouble);
					showValue = Integer.toString((int) showValueDouble);
					fv = true;
				}
				break;
			}
			oldValue = showValue;
			
			if (fv) {// 通知修改成功,通知刷新
				SKSceneManage.getInstance().onRefresh(items);

			}
		}

	};

	@Override
	public void realseMemeory() {
		boolean b = GlobalPopWindow.popIsShow;
		if (b && null != pop) {
			pop.closePop();
		}
		isOnClick = false;
		//自定义键盘
		if(!SKKeyPopupWindow.keyFlagIsShow)
		{
			if(null != popKey)
			{
				popKey.closePop();
			}
		}
		// 销毁注册地址
		SKPlcNoticThread.getInstance().destoryCallback(call);
		if (info.getmOffSetAddr()!=null) {
			SKPlcNoticThread.getInstance().destoryCallback(addOffSet);
		}
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(decimaNumberCall);
		SKPlcNoticThread.getInstance().destoryCallback(sourceMaxCall);
		SKPlcNoticThread.getInstance().destoryCallback(sourceMinCall);
		SKPlcNoticThread.getInstance().destoryCallback(showMaxCall);
		SKPlcNoticThread.getInstance().destoryCallback(showMinCall);
		SKPlcNoticThread.getInstance().destoryCallback(bitKeyBoard);
		SKPlcNoticThread.getInstance().destoryCallback(inputMaxCall);
		SKPlcNoticThread.getInstance().destoryCallback(inputMinCall);

	}

	/**
	 * 只是显示，从地址里面取值→ 缩放（求显示值）→显示值
	 */
	private double showScale(double addressValueSource) {
		if (info.isbIsScale()) {
			if ((sourceMax - sourceMin) != 0) {
				addressValueSource = ((showMax - showMin) / (sourceMax - sourceMin))
						* (addressValueSource - sourceMin) + showMin;
			}
			if (addressValueSource < showMin) {
				addressValueSource = showMin;
			} else if (addressValueSource > showMax) {
				addressValueSource = showMax;
			}
		}
		return addressValueSource;

	}
	private float showScaleFloat(float addressValueSource) {
		if (info.isbIsScale()) {
			if ((sourceMax - sourceMin) != 0) {
				addressValueSource = (float) (((showMax - showMin) / (sourceMax - sourceMin))
						* (addressValueSource - sourceMin) + showMin);
			}
			if (addressValueSource < showMin) {
				addressValueSource = (float) showMin;
			} else if (addressValueSource > showMax) {
				addressValueSource = (float) showMax;
			}
		}
		return addressValueSource;

	}

	/**
	 * param 输入进去的值 输入→缩放（求写入地址值）→写入地址
	 */
	private void InputShow(String inputString) {
		// 完成输入，执行操作通知
		noticeAddr(info.getTouchInfo(), true);

		// 进行输入完成后 执行宏指令
		// 若启用宏指令
		if (true == info.isbIsStartStatement()) {
			// 请求执行控件宏指令
			MacroManager.getInstance(null).Request(MSERV.CALLCM,
					(short) info.getnScriptId());
		}

		boolean boo = isNumeric(inputString);
		if (boo == false) {
			if (info.geteNumberType() == DATA_TYPE.HEX_16) {
				try {
					long inputStringInt = DataTypeFormat.hexStrToInt(
							inputString, 16);
					inputString = Long.toString(inputStringInt);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				setInteger(inputString);
			} else if (info.geteNumberType() == DATA_TYPE.HEX_32) {
				try {
					long inputStringInt = DataTypeFormat.hexStrToInt(
							inputString, 32);
					inputString = Long.toString(inputStringInt);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				setLong(inputString);
			} else {
				return;
			}
		} else {
			if (-1 == inputString.indexOf(".")) {
				if (info.geteNumberType() == DATA_TYPE.FLOAT_32) {
					setDouble(inputString);
				} else if (info.geteNumberType() == DATA_TYPE.HEX_32) {
					try {
						long inputStringInt = DataTypeFormat.hexStrToInt(
								inputString, 32);
						inputString = Long.toString(inputStringInt);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					setLong(inputString);
				} else if (info.geteNumberType() == DATA_TYPE.HEX_16) {
					try {
						long inputStringInt = DataTypeFormat.hexStrToInt(
								inputString, 16);
						inputString = Long.toString(inputStringInt);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					setInteger(inputString);
				} else if (info.geteNumberType() == DATA_TYPE.POSITIVE_INT_32
						|| info.geteNumberType() == DATA_TYPE.OTC_32
						|| info.geteNumberType() == DATA_TYPE.BCD_32) {
					setLong(inputString);
				} else {
					setInteger(inputString);
				}
			} else {
				setDouble(inputString);
			}
		}
	}

	/**
	 * 写入plc int型的值
	 * 
	 * @param inputString
	 */
	private void setInteger(String inputString) {
		int inputStringInt = 0;
		try {
			inputStringInt = Integer.parseInt(inputString);
		} catch (Exception e) {
			e.printStackTrace();
			if (!"".equals(inputString)) {
				if (inputString.indexOf("-") != -1) {
					inputStringInt = 2147483647;

				} else {
					inputStringInt = -2147483648;
				}
			}
		}
		if (info.isbIsScale()) {
			if ((showMax - showMin) != 0)
				inputStringInt = (int) (((sourceMax - sourceMin) / (showMax - showMin))
						* (inputStringInt - showMin) + sourceMin);
		}
		String writeValue = "";// 写入地址值

		// 将输入的数值进行数据类型的转换后，存入地址
		writeValue = convertInputByDataType(Integer.toString(inputStringInt));
		// 不根据数据类型进行转换 直接写入地址

		// 将writeValue写入地址
		Vector<Integer> dataList = new Vector<Integer>();
		inputStringInt = Integer.parseInt(writeValue);
		dataList.add(inputStringInt);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.geteNumberType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		
		if (info.getmOffSetAddress()!=null) {
			PlcRegCmnStcTools
			.setRegIntData(info.getmOffSetAddress(), dataList, mSendData);
		}else {
			PlcRegCmnStcTools
			.setRegIntData(info.getnAddress(), dataList, mSendData);
		}
		
	}

	/**
	 * 写入long值 32位正整数，32位8进制，32位16进制
	 * 
	 * @param inputString
	 */
	private void setLong(String inputString) {
		long inputStringDouble = 0;
		try {
			inputStringDouble = Long.parseLong(inputString);
		} catch (Exception e) {
			e.printStackTrace();

		}
		if (info.isbIsScale()) {
			if ((showMax - showMin) != 0)
				inputStringDouble = (long) (((sourceMax - sourceMin) / (showMax - showMin))
						* (inputStringDouble - showMin) + sourceMin);
		}
		String writeValue = "";// 写入地址值
		if (info.isbRound()) { // 如果有四舍五入
			inputStringDouble = (long) Math.rint(inputStringDouble);
		}
		// 将输入的数值进行数据类型的转换后，存入地址
		writeValue = convertInputByDataType(Long.toString(inputStringDouble));
		// 不根据数据类型进行转换 直接写入地址

		// 将writeValue写入地址
		Vector<Long> dataList = new Vector<Long>();
		inputStringDouble = Long.parseLong(writeValue);
		dataList.add(inputStringDouble);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.geteNumberType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		
		if (info.getmOffSetAddress()!=null) {
			PlcRegCmnStcTools
			.setRegLongData(info.getmOffSetAddress(), dataList, mSendData);
		}else {
			PlcRegCmnStcTools
			.setRegLongData(info.getnAddress(), dataList, mSendData);
		}
	}

	/**
	 * 写入plc double型的值
	 * 
	 * @param inputString
	 */
	private void setDouble(String inputString) {
		double inputStringDouble = 0;
		try {
			inputStringDouble = Double.parseDouble(inputString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (info.isbIsScale()) {
			if ((showMax - showMin) != 0)
				inputStringDouble = ((sourceMax - sourceMin) / (showMax - showMin))
						* (inputStringDouble - showMin) + sourceMin;
		}
		String writeValue = "";// 写入地址值
		if (info.geteNumberType() == DATA_TYPE.FLOAT_32) {
			if (info.isbRound()) { // 如果有四舍五入
				int scale = decimaNumberGloble;// 设置位数
				try {
					BigDecimal b = new BigDecimal(inputStringDouble).setScale(scale,
							BigDecimal.ROUND_HALF_UP);
					inputStringDouble = b.doubleValue();
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		} else {
			try {
				int scale = decimaNumberGloble;// 设置位数
				BigDecimal b = new BigDecimal(inputStringDouble).setScale(scale,
						BigDecimal.ROUND_HALF_UP);
				inputStringDouble = b.doubleValue();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		// 将输入的数值进行数据类型的转换后，存入地址
		writeValue = convertInputByDataType(Double.toString(inputStringDouble));
		// 不根据数据类型进行转换 直接写入地址

		// 将writeValue写入地址
		Vector<Double> dataList = new Vector<Double>();
		inputStringDouble = Double.parseDouble(writeValue);
		dataList.add(inputStringDouble);
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = info.geteNumberType();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		
		if (info.getmOffSetAddress()!=null) {
			PlcRegCmnStcTools
			.setRegDoubleData(info.getmOffSetAddress(), dataList, mSendData);
		}else {
			PlcRegCmnStcTools
			.setRegDoubleData(info.getnAddress(), dataList, mSendData);
		}

	}

	/**
	 * 写入位地址
	 * 
	 * @param prop
	 */
	private void setBit(AddrProp prop, int value) {
		Vector<Integer> mydataListInt = new Vector<Integer>();
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		mSendData.eDataType = DATA_TYPE.BIT_1;
		mydataListInt.add(value);
		PlcRegCmnStcTools.setRegIntData(prop, mydataListInt, mSendData);
	}

	/**
	 * 
	 * @param isScale
	 *            允许缩放 求最大 最小值
	 */
	private void getMaxMinValue() {
		// 如果源范围的指定类型是常量
		if (SHOWAREA.CONSTANT == info.geteSourceArea()) {
			sourceMax = (float) info.getnSourceMax();
			sourceMin = (float) info.getnSourceMin();
		}

		// 如果显示指定类型是常量
		if (SHOWAREA.CONSTANT == info.getnShow()) {
			showMax = (float) info.getnShowMax();
			showMin = (float) info.getnShowMin();
		}
	}

	/**
	 * 
	 * @param nStatusValue
	 *            plc通知的数组
	 * @param type
	 *            类型 ，求源范围/显现最大值，源范围/显现最小值，通知显现值 输入最大/最小值
	 * @return
	 */
	private boolean converDataTypeFromPlc(Vector<Byte> nStatusValue, int type) {
		boolean fvb = false;
		boolean valueBool = false;
		double tempValue = 0;
		switch (info.geteNumberType()) {
		case INT_16: // 16位整数
			if (null == mSData) {
				mSData = new Vector<Short>();
			} else {
				mSData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSData);
			if (valueBool && mSData.size() != 0) {
				tempValue = mSData.get(0);
				fvb = true;
			}
			break;
		case POSITIVE_INT_16: // 16位正整数
			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) {
				tempValue = mIData.get(0);
				fvb = true;
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
				tempValue = mIData.get(0);
				fvb = true;
			}
			break;
		case POSITIVE_INT_32: // 32位正整数
			if (null == mLData) {
				mLData = new Vector<Long>();
			} else {
				mLData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLData);
			if (valueBool && 0 != mLData.size()) {
				tempValue = mLData.get(0);
				fvb = true;
			}
			break;
		case BCD_16:
			// 调用BCD码转换
			// 16位BCD码只能显示四位数 所以要判断，如果长度大于四位，则显示后四位数
			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) {
				if (mIData.get(0) < 0) {
					if (SHOWMIN == type || SOURCEMIN == type) {
						tempValue = 0;
					} else {
						tempValue = 9999; // 非正常情况给默认值
					}
				} else {
					tempValue = mIData.get(0);
				}
				fvb = true;
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
			valueBool = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLData);
			if (valueBool && 0 != mLData.size()) {
				if (mLData.get(0) < 0) {
					if (SHOWMIN == type || SOURCEMIN == type) {
						tempValue = 0;
					} else {
						tempValue = 99999999; // 非正常情况给默认值
					}
				} else {
					tempValue = mLData.get(0);
				}
				fvb = true;
			}
			break;
		case FLOAT_32: // 浮点数
			if (null == mFData) {
				mFData = new Vector<Float>();
			} else {
				mFData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToFloats(nStatusValue, mFData);
			if (valueBool && 0 != mFData.size()) {
				tempValue = mFData.get(0);
				fvb = true;
			}
			break;
		case OTC_16: // 16位8进制
			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) {
				tempValue = mIData.get(0);
				fvb = true;
			}
			break;
		case OTC_32: // 32位的8进制
			if (null == mLData) {
				mLData = new Vector<Long>();
			} else {
				mLData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLData);
			if (valueBool && 0 != mLData.size()) {
				tempValue = mLData.get(0);
				fvb = true;
			}
			break;
		case HEX_16: // 16位16进制
			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) {
				tempValue = mIData.get(0);
				fvb = true;
			}
			break;
		case HEX_32: // 32位16进制
			if (null == mLData) {
				mLData = new Vector<Long>();
			} else {
				mLData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLData);
			if (valueBool && 0 != mLData.size()) {
				tempValue = mLData.get(0);
				fvb = true;
			}
			break;
		default:
			if (null == mSData) {
				mSData = new Vector<Short>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSData);
			if (valueBool && mSData.size() != 0) {
				tempValue = mSData.get(0);
				fvb = true;
			}
			break;
		}
		if (SOURCEMAX == type) {
			double i = getMaxNumber(info.geteNumberType());
			if (tempValue > i) {
				tempValue = i;
			}
			sourceMax = tempValue;
		} else if (SOURCEMIN == type) {
			double i = getMinNumber(info.geteNumberType());
			if (tempValue < i) {
				tempValue = i;
			}
			sourceMin = tempValue;
		} else if (SHOWMAX == type) {
			double i = getMaxNumber(info.geteNumberType());
			if (tempValue > i) {
				tempValue = i;
			}
			showMax = tempValue;
		} else if (SHOWMIN == type) {
			double i = getMinNumber(info.geteNumberType());
			if (tempValue < i) {
				tempValue = i;
			}
			showMin = tempValue;
		} else if (type == INPUTMAX) {
			double i = getMaxNumber(info.geteNumberType());
			if (tempValue > i) {
				tempValue = i;
			}
			inputMax = tempValue;
		} else if (type == INPUTMIN) {
			double i = getMinNumber(info.geteNumberType());
			if (tempValue < i) {
				tempValue = i;
			}
			inputMin = tempValue;
		}
		return fvb;
	}

	/**
	 * 源范围最大值回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack sourceMaxCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			boolean fvb = converDataTypeFromPlc(nStatusValue, SOURCEMAX);
			if (fvb)// 修改成功，通知刷新
			{
				SKSceneManage.getInstance().onRefresh(items);
			}
		}

	};
	/**
	 * 源范围最小值回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack sourceMinCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub

			boolean fvb = converDataTypeFromPlc(nStatusValue, SOURCEMIN);
			if (fvb)// 修改成功，通知刷新
			{
				SKSceneManage.getInstance().onRefresh(items);
			}

		}

	};
	/**
	 * 显示范围最大值回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack showMaxCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			// Log.d("number", "显现最大值通知");
			boolean fvb = converDataTypeFromPlc(nStatusValue, SHOWMAX);
			if (fvb)// 修改成功，通知刷新
			{
				SKSceneManage.getInstance().onRefresh(items);
			}
		}

	};
	/**
	 * 显示范围最小值回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack showMinCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			// Log.d("number", "显现最小值通知");
			boolean fvb = converDataTypeFromPlc(nStatusValue, SHOWMIN);
			if (fvb)// 修改成功，通知刷新
			{
				SKSceneManage.getInstance().onRefresh(items);
			}
		}
	};

	/**
	 * 根据输入的字符串 然后根据数据类型进行转换
	 * 
	 * @param inputString
	 *            输入的字符串
	 * @return 存入地址的值
	 */
	private String convertInputByDataType(String inputString) {
		String returnValue = inputString;
		switch (info.geteNumberType()) {
		case INT_16: // 16位正整数

			returnValue = getBaseNumber(inputString, true);

			break;
		case POSITIVE_INT_16: // 16位整数
			returnValue = getBaseNumber(inputString, true);
			break;
		case INT_32: // 32位正整数

			returnValue = getBaseNumber(inputString, true);

			break;
		case POSITIVE_INT_32: // 32位整数
			returnValue = getBaseNumber(inputString, true);
			break;
		case FLOAT_32: // 浮点数不用进行小数点的转换
			returnValue = inputString;// getBaseNumber(inputString, true);
			break;
		case BCD_16: // 16位bcd码
			returnValue = Long.toString(DataTypeFormat.bcdStrToInt(inputString,
					16));
			break;
		case BCD_32: // 32位bcd码
			returnValue = Long.toString(DataTypeFormat.bcdStrToInt(inputString,
					32));
			break;
		case HEX_16: // 16位16进制
			returnValue = inputString;
			break;
		case HEX_32: // 32位16进制
			returnValue = inputString;
			break;
		case OTC_16: // 16位8进制
			returnValue = Long.toString(DataTypeFormat.octStrToInt(inputString,
					16));
			break;
		case OTC_32: // 32位8进制
			returnValue = Long.toString(DataTypeFormat.octStrToInt(inputString,
					32));
			break;
		default:
			returnValue = inputString;
			break;
		}

		return returnValue;
	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	/**
	 * 根据数据类型进行转换
	 * 
	 * @param addressValue
	 *            进行缩放后的地址值
	 * @return 要显示出来的值
	 */
	// private String convertShowByDataType(double addressValue) {
	// String returnValue = "";
	//
	//
	// switch (info.geteNumberType()) {
	// case INT_16: // 16位整数
	// returnValue = Short.toString((short) addressValue);
	// break;
	// case POSITIVE_INT_16: // 16位正整数
	// returnValue = Integer.toString((int) addressValue);
	// break;
	// case INT_32: // 32位整数
	// returnValue = Integer.toString((int) addressValue);
	// break;
	// case POSITIVE_INT_32: // 32位正整数
	// returnValue = Long.toString((long) addressValue); dddddd
	// break;
	// case BCD_16:
	// if (addressValue < 0) {
	// flagError = true;
	// returnValue = "ERROR";
	// } else {
	// // 调用BCD码转换
	// // 16位BCD码只能显示四位数 所以要判断，如果长度大于四位，则显示后四位数
	// returnValue = DataTypeFormat.intToBcdStr((long) addressValue,
	// false);
	// }
	// break;
	// case BCD_32:
	// if (addressValue < 0) {
	// flagError = true;
	// returnValue = "ERROR";
	// } else {
	// // 调用BCD码转换
	// // 32位BCD码只能显示八位数 所以要判断，如果长度大于八位，则显示后八位数
	// returnValue = DataTypeFormat.intToBcdStr((long) addressValue,
	// false);
	// }
	// break;
	// case FLOAT_32: // 浮点数
	// if (gloabledecimalNumber == 0) {
	// returnValue = Long.toString((long) addressValue);
	// } else {
	// returnValue = Double.toString(addressValue);
	// }
	// break;
	// case OTC_16: // 16位8进制
	// returnValue = DataTypeFormat.intToOctStr((long) addressValue);
	// break;
	// case OTC_32: // 32位的8进制
	// returnValue = DataTypeFormat.intToOctStr((long) addressValue);
	// break;
	// case HEX_16: // 16位16进制
	// returnValue = DataTypeFormat.intToHexStr((long) addressValue);
	// break;
	// case HEX_32: // 32位16进制
	// returnValue = DataTypeFormat.intToHexStr((long) addressValue);
	// break;
	// default:
	// returnValue = Integer.toString((int) addressValue);
	// break;
	// }
	// return returnValue;
	// }

	/**
	 * 根据小数位数得到数值所要偏移的位数 rightOrleft true:右移 放大 false 左移 缩小
	 */
	private String getBaseNumber(String showValueString, boolean rightOrleft) {
		String showValueTemp = showValueString;
		// 如果小数位数为0 则不用偏移 直接返回
		if (decimaNumberGloble == 0) {
			try {
				if (info.isbRound()) {
					BigDecimal b = new BigDecimal(showValueTemp).setScale(0,
							BigDecimal.ROUND_HALF_UP);
					showValueTemp = b.toPlainString();
				} else {
					BigDecimal b = new BigDecimal(showValueTemp).setScale(0,
							BigDecimal.ROUND_DOWN);
					showValueTemp = b.toPlainString();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return showValueTemp;
		} else {
			if (rightOrleft == false) {
				// 显示转换
				showValueTemp = movepoint(showValueTemp, decimaNumberGloble);
			} else {
				// 存入到plc的转换
				showValueTemp = movePointRight(showValueTemp,
						decimaNumberGloble);
			}
			return showValueTemp;
		}

	}

	private void getDecimaNumber() {
		int decimaNumber = 0;
		// 如果小数位数指定是常量，则直接取小数位数长度
		if (SHOWAREA.CONSTANT == info.geteDecimalType()) {
			decimaNumberGloble = info.getnDecimalLength();
		}
//		else {
//			// 否则从地址中取小数位数长度
//
//			decimaNumberGloble = decimaNumberGloble;
//
//		}
//		gloabledecimalNumber = decimaNumber;// 给小数位数赋值，显示convertShowByDataType的时候要用到gloabledecimalNumber
	}

	/**
	 * 从地址中取小数位数
	 */
	int decimaNumberGloble = 0;
	SKPlcNoticThread.IPlcNoticCallBack decimaNumberCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			boolean valueBool = false;
			int nLen = nStatusValue.size();

			if (null == mIData) {
				mIData = new Vector<Integer>();
			} else {
				mIData.clear();
			}
			valueBool = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIData);
			if (valueBool && 0 != mIData.size()) { 
				decimaNumberGloble = mIData.get(0);
			}
			// 小数位数不能超过显示长度
			if (decimaNumberGloble >= info.getnAllbytelength()) {
				decimaNumberGloble = info.getnAllbytelength() - 1;
			}
			SKSceneManage.getInstance().onRefresh(items);

		}
	};

	/**
	 * 偏移小数点
	 * 
	 * @param temp
	 *            要偏移的数值
	 * @param point
	 *            偏移的位数
	 * @return
	 */
	private String movepoint(String temp, int point) {
		if (!"".equals(temp) && null != temp) {
			int pointIndex = temp.indexOf(".");
			if (-1 == pointIndex) {
				// 没有小数点
				int strLength = temp.length();// 字符的长度
				if (temp.indexOf("-") != -1) {
					strLength = strLength - 1; // 字符长度不包括负号
				}
				int showLength = strLength;
				if (temp.indexOf("-") != -1) {
					temp = temp.substring(strLength - showLength + 1,
							strLength + 1);
					temp = "-" + temp;
				} else {
					temp = temp.substring(strLength - showLength, strLength);
				}
				if (point != 0) {
					try {
						double d = Double.valueOf(temp);
						d = d / Math.pow(10, point);
						temp = Double.toString(d);
						BigDecimal b = new BigDecimal(temp).setScale(point,
								BigDecimal.ROUND_HALF_UP);
						temp = b.toPlainString();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			} else {
				// 本身就有小数点的数
				if (point == 0) {
					temp = temp;
				} else {
					// 并且小数位数不为0
					// 判断小数位数跟输入的值的小数位数的大小
					int decimaValueLength = temp.substring(pointIndex + 1,
							temp.length()).length();
					if (decimaValueLength < point) {
						// 要求的小数位数大于本身输入的小数位位数
						int less = point - decimaValueLength;
						for (int i = 0; i < less; i++) {
							temp = temp + "0";
						}
					} else if (decimaValueLength == point) {
						temp = temp;
					} else {
						if (info.isbRound()) {
							int scale = decimaNumberGloble;// 设置位数
							try {
								BigDecimal b = new BigDecimal(temp).setScale(
										scale, BigDecimal.ROUND_HALF_UP);
								temp = b.toPlainString();
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							int k = decimaValueLength - point;
							temp = temp.substring(0, temp.length() - k);
						}

					}
				}
			}
		}
		return temp;
	}

	/**
	 * 非浮点数类型时，输入小数，有小数位数 则实际上输入的值是根据小数位数扩大的数
	 * 
	 * @param value
	 * @param decimaNumber
	 * @return 存入到plc的值根据小数位数进行转换
	 */
	private String movePointRight(String value, int decimaNumber) {
		if (null != value && !"".equals(value)) {
			int pointIndex = value.indexOf(".");
			if (decimaNumber > 0) {
				// 有小数位数 也输入了小数点 判断小数部分的长度跟小数位数的长度
				if (pointIndex != -1) {
					int decimaValueLength = value.substring(
							value.indexOf(".") + 1, value.length()).length();

					if (decimaValueLength < decimaNumber) {
						// 如果输入的小数位数小于要求的小数位数则在后面补0
						int less = decimaNumber - decimaValueLength;
						for (int i = 0; i < less; i++) {
							value = value + "0";
						}
					} else {
						// 大于则截取掉并四舍五入
						try {
							int morethen = decimaValueLength - decimaNumber;
							value = value.substring(0, value.length()
									- morethen + 1);
							try {
								double dt = Double.valueOf(value);
								int scale = decimaNumber;// 设置位数
								BigDecimal b = new BigDecimal(value).setScale(
										scale, BigDecimal.ROUND_HALF_UP);
								value = b.toPlainString();
							} catch (Exception e) {
								// TODO: handle exception
							}

						} catch (Exception e) {

						}
					}
				}
				// 只要有小数位数 存入plc的值都是要乘以偏移量
				try {
					double dValue = Double.valueOf(value);
					//防止double精度损失 所以用四舍五入来保持原来的值
					double tempD =Math.round(dValue * Math.pow(10, decimaNumber)) ;
					long LValue = (long)tempD;
					value = Long.toString(LValue);
				} catch (Exception e) {
                   e.printStackTrace();
				}
			} else {
				// 没有小数位数 但是输入了小数点 直接四舍五入取整
				if (pointIndex != -1) {
					// 输入的数小数位数的长度
					try {
						double d = Double.valueOf(value);
						long d2 = (long) Math.rint(d);
						value = Long.toString(d2);
					} catch (Exception e) {

					}
				}
			}
		}
		return value;
	}
}
