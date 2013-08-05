package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.Vector;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.ImageDrawItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.AcillInputInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.INPUT_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skenum.WINDOW_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AcillCode;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

//import SKGraphCmnShow;
/**
 * ASCII输入显示器
 * 
 * @author 瞿丽平
 * 
 */
public class SKASCIIDisplay extends SKGraphCmnTouch {
	private Paint mPaint;
	private AcillInputInfo info;
	private Rect mRect;
	private String showValue;
	private StaticTextModel text = null;// 静态文本类
	private TextItem textItem;
	private String inputKeyString;
	private SKKeyPopupWindow popKey = null;
	private SKItems items;
	private int itemId;
	private int sceneId;
	// private AddrProp addrPropValue;
	// private AddrProp bitKeyAddrProp; // 位地址控制键盘是否弹出
	// private TouchInfo touchInfo;// 触控属性
	// private ShowInfo showInfo;// 显现属性
	private String sTaskName;
	private boolean initFlag;
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean touchByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	// private boolean keyIsShow;// 键盘是否能弹出，受位地址控制时有可能为false 受触摸控制 一直为true
	private boolean isOnclick;
	private GlobalPopWindow pop;
	private ImageDrawItem imageItem;
	private Bitmap bgBitmap; // 背景图片
	private Rect bgRect;// 背景矩形框
	private Context mContext;
	private myMainHandler hand = null;
	private boolean notTouchOpenKey = false;
	private Bitmap mLockBitmap;
	private Paint mBitmapPaint;//图片专用

	public SKASCIIDisplay(Context context, int itemId, int sceneId,
			AcillInputInfo info) {
		mContext = context;
		isOnclick = false;
		initFlag = true;
		isTouchFlag = true;
		isShowFlag = true;
		showByUser = false;
		touchByUser = false;
		showByAddr = false;
		touchByAddr = false;
		// keyIsShow = true;
		this.sceneId = sceneId;
		this.itemId = itemId;
		// TODO Auto-generated constructor stub
		this.sTaskName = "";
		showValue = "";
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
			
			text = new StaticTextModel();
			text.setM_backColorPadding(info.getnBackColor());
			text.setM_eTextAlign(info.getnShowStyle());
			text.setM_nFontColor(info.getnFontColor());
			text.setM_nFontSize(info.getnFontsize());
			text.setM_sFontFamly(info.getsFontStyle());
			text.setM_textLanguageId(1);
			text.setM_textPro((short) (info.geteFontCss()));
			text.setStartX(info.getnTextStartX());
			text.setStartY(info.getnTextStartY());
			text.setRectHeight(info.getnTextHeight());
			text.setRectWidth(info.getnTextWidth());
			text.setM_alphaPadding(info.getnTransparent());// 设置透明度
//			if (info.getnTransparent() == 0) {
//				text.setBorderAlpha(255);
//				text.setLineColor(Color.BLACK);
//				text.setLineWidth(1);
//			}
			
			textItem = new TextItem(text);
			textItem.initTextPaint();
			textItem.initRectBoderPaint();
			textItem.initRectPaint();
			
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = sceneId;
			items.rect = mRect;
			items.mGraphics=this;
			
		}
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		if (null == info) {
			return;
		}
		initFlag = true;

		if (null != info.getmTouchinInfo()) {
			if (-1 != info.getmTouchinInfo().getnAddrId()
					&& info.getmTouchinInfo().isbTouchByAddr()) {
				touchByAddr = true;
			}
			if (info.getmTouchinInfo().isbTouchByUser()) {
				touchByUser = true;
			}
		}
		if (null != info.getmShowInfo()) {
			if (-1 != info.getmShowInfo().getnAddrId()
					&& info.getmShowInfo().isbShowByAddr()) {
				showByAddr = true;
			}
			if (info.getmShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}		
		
		if (null == popKey) {
			popKey = new SKKeyPopupWindow(SKSceneManage.getInstance().mContext,
					false, info.getnKeyId(), DATA_TYPE.OTHER_DATA_TYPE);
			popKey.setCallback(callback);
			if ((AcillCode.PASSWORD & info.getnCode()) == AcillCode.PASSWORD) {
				popKey.setPassWord(true);
			} else {
				popKey.setPassWord(false);
			}
		}
		if ((AcillCode.PASSWORD & info.getnCode()) == AcillCode.PASSWORD) {
			pop = new GlobalPopWindow(SKSceneManage.getInstance()
					.getCurrentScene(), WINDOW_TYPE.KEYBOARD, 3,
					DATA_TYPE.OTHER_DATA_TYPE);// 3
			// 代表密码
		} else {
			pop = new GlobalPopWindow(SKSceneManage.getInstance()
					.getCurrentScene(), WINDOW_TYPE.KEYBOARD, 2,
					DATA_TYPE.OTHER_DATA_TYPE);// 2代表字母
		}
		// 注册地址值
		registAddr();
		asciiIsShow();
		asciiIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
	}

	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.
	}

	// 点击输入画输入框提示框
	private boolean drawBack = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 允许输入
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
			 if(!info.isbIsinput()){//不允许输入 直接返回
				 return false;
						 
			 }else{
				 if (!isTouchFlag || !isShowFlag) {
						
						if (!isTouchFlag&&info!=null) {
							if (info.getmTouchinInfo()!=null) {
								if (event.getAction()==MotionEvent.ACTION_DOWN) {
									if (info.getmTouchinInfo().isbTouchByUser()) {
										SKSceneManage.getInstance().turnToLoginPop();
									}
								}
							}
						}
						
						return false;
					}
					
					if (event.getAction() == MotionEvent.ACTION_DOWN) {

						isOnclick = true;
						if (info.getmTouchinInfo() == null) {
							doTouch(notTouchOpenKey);
						} else {
							if (info.getmTouchinInfo().isbTimeoutCancel() == true
									&& info.getmTouchinInfo().getnPressTime() > 0) {
								if (null == hand) {
									hand = new myMainHandler(Looper.getMainLooper());
								}
								hand.sendEmptyMessageDelayed(TOUCHHANDER, info
										.getmTouchinInfo().getnPressTime() * 1000);
							} else {
								doTouch(notTouchOpenKey);
							}

						}
						touch = true;
					} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						return true;
					} else if (MotionEvent.ACTION_UP == event.getAction()
							|| MotionEvent.ACTION_CANCEL == event.getAction()) {
						isOnclick = false;
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
				if (isOnclick || notTouchOpenKey) {
					doTouch(notTouchOpenKey);
					notTouchOpenKey = false;
				}
			}
		}
	}

	private void doTouch(boolean bitFlag) {
		if (null == info) {
			return;
		}
		if (!isTouchFlag || !isShowFlag) {
			return;
		}
		if (info.isbIsinput()) {
			if (info.getnKeyId() == -1) // 为-1 时调用系统键盘
			{
				openKey();
			} else {
				openBoard();
			}
			// 如果是由位地址控制键盘打开的 并且勾选了自动复位 则将那个位地址值复位
			if (bitFlag && info.isbAutoChangeBit()) {
				setBit(info.getsBitAddress(), 0);
			}
		}
	}

	/**
	 * 键盘输入响应事件
	 */
	SKKeyPopupWindow.ICallback callback = new SKKeyPopupWindow.ICallback() {

		@Override
		public void onResult(String result, KEYBOARD_OPERATION type) {
			if (drawBack) {
				// 关闭键盘 把背景去掉
				drawBack = false;
				SKSceneManage.getInstance().onRefresh(items);
			}
			if (type == KEYBOARD_OPERATION.ENTER) {
				inputKeyString = result;
				if (null != inputKeyString && !"".equals(inputKeyString)) {

					writeToAddr(inputKeyString);
				}
			}

		}
	};

	/**
	 * 将值写入地址
	 * 
	 * @param inputKeyString
	 */
	private void writeToAddr(String inputKeyString) {
		// 输入完成后，执行通知操作通知
		noticeAddr(info.getmTouchinInfo(), true);
		// 进行输入完成后 执行宏指令
		// 若启用宏指令
		if (true == info.isbIsStartStatement()) {
			// 请求执行控件宏指令
			MacroManager.getInstance(null).Request(MSERV.CALLCM,
					(short) info.getnScriptId());
		}
		// 将输入的值进行数据类型进行转换
		byte[] by = converCodeWrite(inputKeyString);
		// 将输入转换后的数组写入地址
		Vector<Byte> dataList = new Vector<Byte>();

		if (0 != by.length) {
			if ((AcillCode.UNICODE & info.getnCode()) == AcillCode.UNICODE) {
				for (int i = 2; i < by.length; i++) {
					dataList.add(by[i]);
				}
			} else {
				for (int i = 0; i < by.length; i++) {
					dataList.add(by[i]);
				}
			}
		}
		SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
		mSendData.eDataType = DATA_TYPE.ASCII_STRING;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;
		if (null != info.getnAddress()) {
			if (info.getmOffSetAddress()!=null) {
				PlcRegCmnStcTools.setRegAsciiData(info.getmOffSetAddress(), dataList,
						mSendData);
			}else {
				PlcRegCmnStcTools.setRegAsciiData(info.getnAddress(), dataList,
						mSendData);
			}
		}
	}

	/**
	 * 打开自定义键盘
	 */
	private void openBoard() {
		if (SKKeyPopupWindow.keyFlagIsShow && !GlobalPopWindow.popIsShow) {
			popKey.setLastText(showValue);
			popKey.setShowMax(Integer.toString(info.getnShowCharNumber()));
			popKey.setShowMin(Integer.toString(0));
			popKey.setnStartX(info.getnBoardX());
			popKey.setnStartY(info.getnBoardY());
			popKey.initPopUpWindow();

			// 允许输入 并且勾选了输入提示 则更换背景
			if (info.isbIsinput() && info.isbInputSign()) {
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
		// 如果显示类型是密码 则传-1 过去 辨别inputType 是text 还是password

		if (!GlobalPopWindow.popIsShow && SKKeyPopupWindow.keyFlagIsShow) {
			if (null != pop) {
				pop.setCallback(keyback);
				pop.initPopupWindow();
				pop.setInputMax(Integer.toString(info.getnShowCharNumber()));
				pop.setInputMin(Integer.toString(0));
				// 允许输入 并且勾选了输入提示 则更换背景
				if (info.isbIsinput() && info.isbInputSign()) {
					drawBack = true;
					SKSceneManage.getInstance().onRefresh(items);
				}
				pop.showPopupWindow();
			}
		}

	}

	GlobalPopWindow.ICallBack keyback = new GlobalPopWindow.ICallBack() {

		@Override
		public void inputFinish(String result) {
			// TODO Auto-generated method stub
			if (null != result && !"".equals(result)) {
				writeToAddr(result);
			}
		}

		// 当关闭popWindow 时 是否 启动定时器
		@Override
		public void onStart() {
			if (drawBack) {
				// 关闭键盘 把背景去掉
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

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub
		asciiIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;

	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		asciiIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
		return isTouchFlag;
	}

	private void asciiIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(info.getmTouchinInfo());
		}
	}

	private void asciiIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getmShowInfo());
		}
	}

	@Override
	public void initGraphics() {

		init();
	}

	/**
	 * 注册地址值
	 */
	private void registAddr() {
		// TODO Auto-generated method stub
		if (null != info.getnAddress()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getnAddress(),
					valueCall, false);
		}
		
		if (null!=info.getmOffSetAddr()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getmOffSetAddr(),
					addOffSet, false);
		}
		
		// 注册触控地址值
		if (touchByAddr && null != info.getmTouchinInfo().getTouchAddrProp()) {
			ADDRTYPE addrType = info.getmTouchinInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmTouchinInfo().getTouchAddrProp(), touchCall,
						true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmTouchinInfo().getTouchAddrProp(), touchCall,
						false);
			}
		}
		// 注册显现地址值
		if (showByAddr && null != info.getmShowInfo().getShowAddrProp()) {
			ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmShowInfo().getShowAddrProp(), showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmShowInfo().getShowAddrProp(), showCall, false);
			}

		}
		// 位控制键盘是否弹出
		if (null != info.getsBitAddress()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getsBitAddress(),
					bitKeyBoard, true);
		}
	}

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
				}
				// 位地址值为1的时候 键盘可以弹出
				if (addrValue == 1) {
					// keyIsShow = true;
					if (null == hand) {
						hand = new myMainHandler(Looper.getMainLooper());
					}
					hand.sendEmptyMessageDelayed(TOUCHHANDER, 10);
					notTouchOpenKey = true;
				}
			}
		}
	};

	/**
	 * 地址通知值
	 */
	SKPlcNoticThread.IPlcNoticCallBack valueCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if ((AcillCode.UNICODE & info.getnCode()) == AcillCode.UNICODE) {
				byte[] byteValue = new byte[nStatusValue.size() + 2];
				byteValue[0] = -1;
				byteValue[1] = -2;
				for (int i = 0; i < nStatusValue.size(); i++) {
					byteValue[i + 2] = nStatusValue.get(i);
				}
				showValue = converCodeShow(byteValue);
			} else {
				byte[] byteValue = new byte[nStatusValue.size()];
				for (int i = 0; i < nStatusValue.size(); i++) {
					byteValue[i] = nStatusValue.get(i);
				}
				showValue = converCodeShow(byteValue);
			}
			SKSceneManage.getInstance().onRefresh(items);

		}

	};
	
	// 地址偏移
	SKPlcNoticThread.IPlcNoticCallBack addOffSet = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue != null) {
				if (info.getnAddress() != null) {
					Vector<Short> mIData = new Vector<Short>();
					boolean result = PlcRegCmnStcTools.bytesToShorts(
							nStatusValue, mIData);
					if (result) {
						int value = mIData.get(0);
						int temp = info.getnAddress().nAddrValue + value;
						if (temp < 0) {
							SKPlcNoticThread.getInstance().destoryCallback(valueCall);
							showValue ="";
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
						

						SKPlcNoticThread.getInstance().destoryCallback(valueCall);
						SKPlcNoticThread.getInstance().addNoticProp(info.getmOffSetAddress(),
								valueCall, false);

						if (info.getnAddress().eConnectType > 1) {
							SKSceneManage.getInstance().updateSceneReadAddrs(
									sceneId, info.getmOffSetAddress());
						}
					}
				}
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
			isTouch();
		}

	};
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShow();
		}

	};

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

	/**
	 * 具体的画法
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void draw(Paint paint, Canvas canvas) {
		// 外形不为空画外形
		if (null != info.getsShapId()) {
			if (imageItem == null) {
				imageItem = new ImageDrawItem(info.getsShapId(), mRect);
			}
			imageItem.draw(paint, canvas);
		}
		// 画静态文本
		drawTextValue(paint, canvas);
		// 画点击输入背景提示框
		if (drawBack) {
			if (bgBitmap == null) {
				bgBitmap = ImageFileTool.getBitmap(R.drawable.rect_red_bg,
						mContext);
				bgRect = new Rect(0, 0, bgBitmap.getWidth(),
						bgBitmap.getHeight());
			}
			if (bgBitmap != null) {
				canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
						Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
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
		text.setM_sTextStr(showValue);
		textItem.draw(canvas);
	}

	/**
	 * 转换编码 再显示
	 * 
	 * @param temp
	 *            return 转换编码之后的 要显示的字符串
	 */
	private String converCodeShow(byte[] bTemp) {
		String returnValue = new String(bTemp);
		try {
			if ((AcillCode.UNICODE & info.getnCode()) == AcillCode.UNICODE) {
				returnValue = new String(bTemp, "UNICODE");

			} else {
				returnValue = new String(bTemp);
			}

			if (AcillCode.SHOWHEIGHTLOWER == (AcillCode.SHOWHEIGHTLOWER & info
					.getnCode())) {
				returnValue = convertHightLower(returnValue);
			}

			if ((AcillCode.PASSWORD & info.getnCode()) == AcillCode.PASSWORD) {
				returnValue = "******";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return returnValue;
	}

	/**
	 * 写入编码转换
	 * 
	 * @param inputString
	 *            输入的字符
	 * @return 写入的字节数组
	 */
	private byte[] converCodeWrite(String inputString) {
		String tempString = inputString;
		byte[] returnByte = null;
		try {
			returnByte = inputString.getBytes("US-ASCII");
			if ((info.getnCode() & AcillCode.INPUTHEIGHTLOWER) == AcillCode.INPUTHEIGHTLOWER) {
				tempString = convertHightLower(tempString);
				if ((info.getnCode() & AcillCode.UNICODE) == AcillCode.UNICODE) {
					returnByte = tempString.getBytes("UNICODE");
				} else {
					returnByte = tempString.getBytes();
				}
			}

			if ((info.getnCode() & AcillCode.UNICODE) == AcillCode.UNICODE) {
				returnByte = tempString.getBytes("UNICODE");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return returnByte;
	}

	/**
	 * 高低字节互换
	 * 
	 * @param temp
	 * @return
	 */
	private String convertHightLower(String temp) {
		String s = temp;
		StringBuffer stringBuffer = new StringBuffer(s);
		if (1 == s.length() % 2) {
			stringBuffer.append(" ");
		}
		StringBuffer stringBuffer2 = new StringBuffer();
		for (int i = 0; i < stringBuffer.length(); i = i + 2) {
			stringBuffer2.append(stringBuffer.charAt(i + 1));
			stringBuffer2.append(stringBuffer.charAt(i));

		}
		s = stringBuffer2.toString();
		return s;
	}

	@Override
	public void realseMemeory() {
		// TODO Auto-generated method stub
		// info = null;
		// text = null;
		isOnclick = false;
		boolean b = GlobalPopWindow.popIsShow;
		if (b) {
			if (null != pop)
				pop.closePop();
		}
		// initFlag = true;
		sTaskName = "";
		// 销毁注册地址
		if (info.getmOffSetAddr()!=null) {
			SKPlcNoticThread.getInstance().destoryCallback(addOffSet);
		}
		SKPlcNoticThread.getInstance().destoryCallback(valueCall);
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(bitKeyBoard);

	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

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
}