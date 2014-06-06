package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.DTDHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.skbutton.BitButtonInfo;
import com.android.Samkoonhmi.model.skbutton.ButtonInfo;
import com.android.Samkoonhmi.model.skbutton.PeculiarButtonInfo;
import com.android.Samkoonhmi.model.skbutton.SceneButtonInfo;
import com.android.Samkoonhmi.model.skbutton.WordButtonInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.BUTTON.BIT_OPER_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.PECULIAR_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.WATCH_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.WORD_OPER_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog.IOperCall;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog.IOperCall.CALLTYPE;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 开关按钮
 */
public class SKButton extends SKGraphCmnTouch implements IItem {

	private static final String TAG = "SKButton";
	private static final int HANDLER_CLICK_UP = 0;
	private static final int HANDLER_PRESS = 1;
	private static final int HANDLER_OPER = 2;
	private static final int HANDLER_SCREEN = 3;// 画面跳转
	private static final int nPadding = 2;
	private HashMap<String, Bitmap> mPicMap = null;// 状态图片
	private HashMap<String, Bitmap> mClickBgMap = null;// 点击效果图
	private Context mContext;
	public ButtonInfo info;
	private Paint mPaint;
	private int nCurrentState; // 状态Id
	private int nFlickState; // 闪烁状态id
	private boolean bClickDown; // 点击
	private SKButtonFunction mButtonFunction;// 按钮功能
	private Rect rect;
	private Rect dRect;
	private int nButtonId = 0;
	private int nSceneId;
	private SKItems item;
	private boolean isFlick; // 是否闪烁
	private boolean show; // 是否可显现
	private boolean touch; // 是否可触控
	private boolean showByAddr; // 是否注册显现地址
	private boolean touchByAddr; // 是否注册触控地址
	private boolean showByUser; // 是否受用户权限控件
	private boolean touchByUser; // 是否受用户权限控件
	private SKSwitchOperDialog mOperDialog;
	private Bitmap mOneSlidBitmap;// 滑动块图片1
	private Bitmap mTwoSlidBitmap;// 滑动块图片2
	private boolean bSlid;// 是否启动滑动功能
	private Rect mSlidRect;// 滑动图片所在位置
	private boolean isLevel;// 水平滑动
	private boolean isDrawOne;// 画滑动块图片1
	private boolean isReset;// 是否已经复位了;
	private UIHandler mUIHandler;
	private Bitmap mLockBitmap;// 不可触摸图标
	private Paint mBitmapPaint;// 图片专用
	private Bitmap mRRectBitmap;
	private Bitmap mCRectBitmap;
	private int nColor = Color.argb(255, 255, 199, 0);
	private boolean hasFlick;// 所有状态中，是否有闪烁的，没有不注册
	private int nButtonType;// 开关类型，0-开关本身，1-外部调用
	private int nFlickTime=5;//闪烁频率

	public SKButton(Context context, int id, int sceneId, ButtonInfo info) {

		this.mContext = context;
		this.nButtonId = id;
		this.nSceneId = sceneId;
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		item = new SKItems();
		mButtonFunction = new SKButtonFunction(mContext, sceneId);
		mPicMap = new HashMap<String, Bitmap>();
		mClickBgMap = new HashMap<String, Bitmap>();
		this.info = info;
		bSlid = false;
		nCurrentState = 0;
		nButtonType = 0;
		setSlidBitmap();

		if (info != null) {
			// 控件显示区域
			rect = new Rect(info.getnLp(), info.getnTp(), info.getnLp()
					+ info.getnWidth(), info.getnTp() + info.getnHeight());

			// 点击移位
			dRect = new Rect(rect.left + nPadding, rect.top + nPadding,
					rect.right - nPadding, rect.bottom - nPadding);

			mUIHandler = new UIHandler(Looper.getMainLooper());

			// 刷新信息
			item.sceneId = nSceneId;
			item.itemId = nButtonId;
			item.nZvalue = info.getnZvalue();
			item.nCollidindId = info.getnCollidindId();
			item.rect = rect;
			item.mGraphics = this;
			hasFlick = false;

			this.show = true;
			this.touch = true;
			this.showByAddr = false;
			this.touchByAddr = false;
			this.showByUser = false;
			this.touchByUser = false;

			initState();

			// 显现权限
			if (info.getmShowInfo() != null) {
				if (info.getmShowInfo().getShowAddrProp() != null) {
					// 受地址控制
					showByAddr = true;
				}

				if (info.getmShowInfo().isbShowByUser()) {
					// 受用户权限控制
					showByUser = true;
				}
			}

			// 触控权限
			if (info.getmTouchInfo() != null) {
				if (info.getmTouchInfo().getTouchAddrProp() != null) {
					// 受地址控制
					touchByAddr = true;
				}

				if (info.getmTouchInfo().isbTouchByUser()) {
					// 受用户权限控制
					touchByUser = true;
				}
			}

			// 注册监视地址
			if (info.geteWatchType() != WATCH_TYPE.NONE) {
				if (info.getmWatchAddress() != null) {
					boolean b = false;
					if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
						// 双态
						if (info.isbAddrType()) {
							// 字
							b = false;
						} else {
							// 位 0
							b = true;
						}
					} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
						// 多态
						b = false;
					}
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getmWatchAddress(), watchCall, b, nSceneId);
				}
			} else if (bSlid && info instanceof BitButtonInfo) {// 位滑动开关 进行监视自身
				SKPlcNoticThread.getInstance().addNoticProp(
						((BitButtonInfo) info).getmBitAddress(), watchCall,
						true, nSceneId);
			}

			// 注册显现地址
			if (showByAddr) {
				ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getmShowInfo().getShowAddrProp(), showCall,
							true, nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getmShowInfo().getShowAddrProp(), showCall,
							false, nSceneId);
				}

			}

			// 注册触控地址
			if (touchByAddr) {
				ADDRTYPE addrType = info.getmTouchInfo().geteCtlAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getmTouchInfo().getTouchAddrProp(), touchCall,
							true, nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getmTouchInfo().getTouchAddrProp(), touchCall,
							false, nSceneId);
				}
			}

			if (info.geteButtonType() == BUTTON_TYPE.WORD
					&& ((WordButtonInfo) info).getbDynamicControl()) {

				SKPlcNoticThread.getInstance().addNoticProp(
						((WordButtonInfo) info).getmDynamicAdddrProp(),
						dynamicCall, false, nSceneId);
			}
		}
	}

	/**
	 * 提供给下拉框调用的
	 * 
	 * @param id
	 *            -开关id
	 * @param type
	 *            -开关类型
	 * @param sid
	 *            -场景id
	 */
	public SKButton(int sid, int id, BUTTON_TYPE type) {
		nCurrentState = 0;
		bClickDown = true;
		nButtonType = 1;
		// 下拉框调用的
		switch (type) {
		case BIT:
			info = new BitButtonInfo();
			info.setnFunId(id);
			info.seteButtonType(BUTTON_TYPE.BIT);
			info.setbIsStartStatement(false);
			info.setnStatementId(0);
			break;
		case WORD:
			info = new WordButtonInfo();
			info.setnFunId(id);
			info.seteButtonType(BUTTON_TYPE.WORD);
			info.setbIsStartStatement(false);
			info.setnStatementId(0);

			// 处理动态写入地址的情况
			DBTool.getInstance().getmButtonBiz()
					.getWordSwitchDynAddress(id, (WordButtonInfo) info);
			if (((WordButtonInfo) info).getbDynamicControl()) {
				SKPlcNoticThread.getInstance().addNoticProp(
						((WordButtonInfo) info).getmDynamicAdddrProp(),
						dynamicCall, false, sid);
			}
			break;
		case SCENE:
			info = new SceneButtonInfo();
			info.setnFunId(id);
			info.seteButtonType(BUTTON_TYPE.SCENE);
			info.setbIsStartStatement(false);
			info.setnStatementId(0);
			break;
		case PECULIAR:
			info = new PeculiarButtonInfo();
			info.setnFunId(id);
			info.seteButtonType(BUTTON_TYPE.PECULIAR);
			info.setbIsStartStatement(false);
			info.setnStatementId(0);
			break;
		}

		mButtonFunction = new SKButtonFunction(
				SKSceneManage.getInstance().mContext, sid);
	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {

	}

	/**
	 * 画面管理调用此方法，对控件进行初始化
	 */
	@Override
	public void initGraphics() {
		init();
	}

	// 初始化开关状态
	private void initState() {
		if (info.getmTextList() != null) {
			int c = nCurrentState;
			if (info.getmTextList().size() > 0
					&& info.getmTextList().size() > c) {

				if (info.getmTextList().get(c) instanceof TextInfo) {
					setState(c, info.getmTextList().get(c));
				}

			}
		}
	}

	/**
	 * 状态设置
	 * 
	 * @param i
	 *            -状态
	 * @param mTextInfo
	 *            -状态
	 */
	private void setState(int i, TextInfo mTextInfo) {
		mTextInfo.setnLangugeId((short) SystemInfo.getCurrentLanguageId());
		StaticTextModel model = new StaticTextModel();
		TextItem mTextItem = new TextItem(model);

		TextInfo tInfo = info.getmTextList().get(i);
		StakeoutInfo sInfo = info.getmStakeoutList().get(i);

		if (sInfo.getnShapeType() == 4) {
			bUserBitmap = false;
		} else {
			bUserBitmap = true;
		}

		if (bUserBitmap) {
			// 使用图片
			if (!mPicMap.containsKey(String.valueOf(i))) {
				Bitmap temp = ImageFileTool.getBitmap(sInfo.getsPath());
				if (temp != null) {
					// 状态图片
					mPicMap.put(String.valueOf(i), temp);
					// 状态点击效果
					Bitmap bg = getAlphaBitmap(temp, nColor);
					if (bg != null) {
						mClickBgMap.put(
								i + "_" + info.getnWidth() + "_"
										+ info.getnHeight(), bg);
					}
				}
			}
			model.setLineWidth(0);
			model.setM_backColorPadding(Color.TRANSPARENT);
			model.setM_alphaPadding(0);
		} else {
			// 不使用图片
			model.setM_backColorPadding(sInfo.getnColor());
			model.setLineWidth(0);
			model.setLineColor(Color.BLACK);
			model.setM_alphaPadding(sInfo.getnAlpha());
		}

		// 位置
		if (tInfo.getmStyle().size() > SystemInfo.getCurrentLanguageId()) {
			int style = tInfo.getmStyle()
					.get(SystemInfo.getCurrentLanguageId());
			model.setM_textPro((short) style);
			if (TextAttribute.CENTER == (style & TextAttribute.CENTER)) {
				model.setM_eTextAlign(TEXT_PIC_ALIGN.CENTER);
			} else if (TextAttribute.LEFT == (style & TextAttribute.LEFT)) {
				model.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
			} else if (TextAttribute.RIGHT == (style & TextAttribute.RIGHT)) {
				model.setM_eTextAlign(TEXT_PIC_ALIGN.RIGHT);
			}
			// model.setM_eTextAlign(t)
		}

		// 文本
		String text = "";
		if (tInfo.getmTextList().size() > SystemInfo.getCurrentLanguageId()) {
			text = tInfo.getmTextList().get(SystemInfo.getCurrentLanguageId());
			if (text == null) {
				text = "";
			}
		}

		model.setM_sTextStr(text);

		// 颜色
		if (tInfo.getmColors().size() > SystemInfo.getCurrentLanguageId()) {
			model.setM_nFontColor(tInfo.getmColors().get(
					SystemInfo.getCurrentLanguageId()));
		} else {
			model.setM_nFontColor(Color.BLACK);
		}

		// 字体大小
		if (tInfo.getmSize().size() > SystemInfo.getCurrentLanguageId()) {
			int size = tInfo.getmSize().get(SystemInfo.getCurrentLanguageId());
			model.setM_nFontSize(size);
		} else {
			model.setM_nFontSize(10);
		}

		// 字体类型
		if (tInfo.getmFonts().size() > SystemInfo.getCurrentLanguageId()) {
			model.setM_sFontFamly(tInfo.getmFonts().get(
					SystemInfo.getCurrentLanguageId()));
		} else {
			model.setM_sFontFamly("");
		}

		// 是否闪烁
		if (info.getmStakeoutList().size() >= i) {
			if (info.getmStakeoutList().get(i).geteFlickType() == FLICK_TYPE.NO_FLICK) {
				info.getmStakeoutList().get(i).setbFlick(false);
				info.getmStakeoutList().get(i).setnFlickTime(5);
				if (i == nCurrentState) {
					isFlick = false; // 不闪烁
				}
			} else {
				if (i == nCurrentState) {
					isFlick = true; // 闪烁
				}
				info.getmStakeoutList().get(i).setbFlick(true);
				info.getmStakeoutList().get(i).setnFlickTime(5);
			}
		}

		if (!bUserBitmap) {
			model.setStartX(info.getnLp() + 1);
			model.setStartY(info.getnTp() + 1);
			model.setRectWidth(info.getnWidth() - 2);
			model.setRectHeight(info.getnHeight() - 2);
		} else {
			model.setStartX(info.getnLp());
			model.setStartY(info.getnTp());
			model.setRectWidth(info.getnWidth());
			model.setRectHeight(info.getnHeight());
		}

		mTextItem.initRectBoderPaint();
		mTextItem.initRectPaint();
		mTextItem.initTextPaint();

		info.getmTextList().get(i).setmTextItem(mTextItem);
	}

	/**
	 * 控件初始化
	 */
	public void init() {

		if (info == null) {
			return;
		}

		nFlickState = 0;
		bClickDown = false;
		isFlick = false;

		// 控件是否受权限or地址控制
		itemIsShow();
		itemIsTouch();

		hasFlick = false;
		if (info.getmStakeoutList() != null) {
			for (int j = 0; j < info.getmStakeoutList().size(); j++) {
				if (info.getmStakeoutList().get(j).geteFlickType() != FLICK_TYPE.NO_FLICK) {
					hasFlick = true;
					break;
				}
			}
		}

		// 注册 通知
		registNotice();

	}

	/**
	 * 由画面管理调用
	 * @param canvas-系统画布
	 * @param itemId--控件id，当次参数与控件id相同时 绘制控件，并且返回true，其他情况返回false
	 */
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			// 是否可以显现
			if ((itemId == nButtonId) && (null != info)) {
				// 控件id相等
				if (isFlick) {
					// 闪烁
					drawButton(canvas, nFlickState, isFlick, bClickDown,
							isFlickState);
				} else {
					// 正常显示
					drawButton(canvas, nCurrentState, isFlick, bClickDown,
							false);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 画开关按钮
	 * 
	 * @param init
	 *            -true,初始化
	 * @param state
	 *            ,状态值ID
	 * @param filck
	 *            -true,闪烁--是闪烁刷新，还是状态刷新,如果闪烁刷新，更新model值
	 * @param isBack
	 *            -true,点击效果
	 * @param stateFilck
	 *            -状态切换闪烁
	 */
	private boolean bUserBitmap = true;
	private TextItem mItem;

	public void drawButton(Canvas canvas, int state, boolean filck,
			boolean isBack, boolean stateFilck) {

		if (!filck || stateFilck) {

			for (int i = 0; i < info.getmTextList().size(); i++) {
				TextInfo mTextInfo = info.getmTextList().get(i);
				if (mTextInfo.getnStatusId() == state) {
					if (mTextInfo.getmTextItem() == null
							|| mTextInfo.getnLangugeId() != SystemInfo
									.getCurrentLanguageId()) {
						setState(state, mTextInfo);
						this.mItem = info.getmTextList().get(i).getmTextItem();
					} else {
						// 判断当前状态是否闪烁
						if (info.getmStakeoutList() != null) {
							if (info.getmStakeoutList().size() > nCurrentState) {
								isFlick = info.getmStakeoutList()
										.get(nCurrentState).isbFlick();
							} else {
								isFlick = false;
							}
						} else {
							isFlick = false;
						}

						StakeoutInfo sInfo = info.getmStakeoutList().get(i);
						if (sInfo.getnShapeType() == 4) {
							bUserBitmap = false;
						} else {
							bUserBitmap = true;
						}

						this.mItem = info.getmTextList().get(i).getmTextItem();
					}
					//当前状态闪烁频率
					nFlickTime=info.getmStakeoutList().get(i).getnFlickTime();
					break;
				}
			}
		}

		if (mBitmapPaint == null) {
			mBitmapPaint = new Paint();
			mBitmapPaint.setAntiAlias(true);
			mBitmapPaint.setDither(true);
		}

		// 使用图片
		if (bUserBitmap) {
			// 点击效果
			if (isBack && !bSlid) {
				Bitmap bitmap = mClickBgMap.get(state + "_" + info.getnWidth()
						+ "_" + info.getnHeight());
				if (bitmap != null) {
					Rect bgRect = new Rect(0, 0, bitmap.getWidth(),
							bitmap.getHeight());
					canvas.drawBitmap(bitmap, bgRect, rect, mBitmapPaint);
				}
			}

			// 画状态图片 state=-1,用于闪烁-控件显隐
			Bitmap bitmap = null;
			if (state > -1) {
				if (mPicMap.containsKey(String.valueOf(state))) {
					bitmap = mPicMap.get(String.valueOf(state));
				}
			}

			if (bitmap != null) {
				if (isBack && !bSlid) {
					canvas.drawBitmap(bitmap, null, dRect, mBitmapPaint);
				} else {
					canvas.drawBitmap(bitmap, null, rect, mBitmapPaint);
				}
			}
		}

		// state=-1,表示闪烁
		if (state > -1) {
			mItem.draw(canvas);
		}

		if (!bUserBitmap) {
			// 不使用图片的
			if (isBack && !bSlid) {
				// 点击效果
				if (mCRectBitmap == null) {
					mCRectBitmap = getRectFrame(2, item.rect);
				}
				if (mCRectBitmap != null) {
					canvas.drawBitmap(mCRectBitmap, info.getnLp(),
							info.getnTp(), mBitmapPaint);
				}
			} else {
				// 正常显示
				if (!bSlid && mItem.getmText().getM_alphaPadding() > 0
						&& state > -1) {
					if (mRRectBitmap == null) {
						mRRectBitmap = getRectFrame(1, item.rect);
					}
					if (mRRectBitmap != null) {
						canvas.drawBitmap(mRRectBitmap, info.getnLp(),
								info.getnTp(), mBitmapPaint);
					}
				}
			}
		}

		if (bSlid) {
			// 启动滑动功能的
			if (isDrawOne) {
				canvas.drawBitmap(mOneSlidBitmap, mSlidRect.left,
						mSlidRect.top, mBitmapPaint);
			} else {
				canvas.drawBitmap(mTwoSlidBitmap, mSlidRect.left,
						mSlidRect.top, mBitmapPaint);
			}
		}

		// 不可触控加上锁图标
		if (!touch && SystemInfo.isbLockIcon()) {
			if (mLockBitmap == null) {
				mLockBitmap = ImageFileTool
						.getBitmap(R.drawable.lock, mContext);
			}
			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getnLp(), info.getnTp(),
						mBitmapPaint);
			}
		}
		bRefreshed = true;// 已经刷新

	}

	@Override
	public void realseMemeory() {
		if (hasFlick) {
			SKTimer.getInstance().getBinder().onDestroy(callback, nFlickTime);
		}

		if (SystemInfo.getLanguageNumber() > 1) {
			SKLanguage.getInstance().getBinder().onDestroy(lCallback);
		}

		isFlick = false;
		isClick = false;
		clickDown = false;
		if (mButtonFunction != null) {
			mButtonFunction.isDown = false;

			// 用户管理
			if (mButtonFunction.dialog != null) {
				mButtonFunction.dialog.show = false;
				mButtonFunction.dialog.onExit();
			}

			// 配方
			if (mButtonFunction.sDialog != null) {
				mButtonFunction.sDialog.isShow = false;
			}

			// 时间设置
			if (mButtonFunction.timeSetting != null) {
				mButtonFunction.timeSetting.closePopWindow();
			}

			// 历史数据删除
			if (mButtonFunction.collectSetting != null) {
				mButtonFunction.collectSetting.closePopWindow();
			}

			SKSceneManage.getInstance().closeLoginPop();
		}
		if (mOperDialog != null) {
			mOperDialog.hidePopWindow();
		}

	}

	@Override
	public boolean isShow() {
		itemIsShow();
		SKSceneManage.getInstance().onRefresh(item);
		return show;
	}

	@Override
	public boolean isTouch() {
		itemIsTouch();
		SKSceneManage.getInstance().onRefresh(item);
		return touch;
	}

	/**
	 * 控件是否可以显现
	 */
	private void itemIsShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(info.getmShowInfo());
		}
	}

	/**
	 * 控件是否可以触控
	 */
	private void itemIsTouch() {
		if (touchByAddr || touchByUser) {
			touch = popedomIsTouch(info.getmTouchInfo());
		}
	}

	private boolean isDral; // 是否处理点击事件，如果用户移动超出了控件，就不处理点击事件
	private boolean isClick = false; // 是否已经处理点击事件，防止多次点击。
	private boolean isMove;// 是否可以移动
	private float nStartX;
	private float nStartY;

	// private boolean isJoyEvent = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time = 0;

		if (!show) {
			return false;
		}

		float X = event.getX();
		float Y = event.getY();
		boolean touchEvent = false;

		if (info == null || mUIHandler == null) {
			return false;
		}
		if (info.geteButtonType() == BUTTON_TYPE.BIT_LIGHT) {
			return false;
		}
		if (MotionEvent.ACTION_UP == event.getAction()
				|| MotionEvent.ACTION_CANCEL == event.getAction()) {
			if (mButtonFunction != null) {
				mButtonFunction.isDown = false;
				// 防止连续加or连续减，松开没处理
			}
		}

		if (X < info.getnLp() || Y < info.getnTp()
				|| X > (info.getnLp() + info.getnWidth())
				|| Y > (info.getnTp() + info.getnHeight())) {

			// 点击事件不做处理
			if (isClick) {
				if (clickDown) {
					// 如果是位开关，并且是点动事件，即使点击事件不在控件范围内，只有用户点击了，就要复位
					doTouch(false, true,event.getAction());
				}
				mUIHandler.removeMessages(HANDLER_CLICK_UP);
				mUIHandler.sendEmptyMessageDelayed(HANDLER_CLICK_UP, 100);
				isClick = false;
			}
			isDral = false;
			if (mButtonFunction != null) {
				mButtonFunction.isDown = false;
				// 防止连续加or连续减，松开没处理
			}
			if (bSlid && isReset) {
				reset();
			}
			isMove = false;

			return false;
		} else {
			if (!touch || !show) {
				// 控件不可触摸
				if (!touch) {
					if (info.getmTouchInfo() != null) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							if (info.getmTouchInfo().isbTouchByUser()) {
								IOperCall iCall = null;
								if (info.geteButtonType() == BUTTON_TYPE.SCENE) {
									iCall = iOperCall;
									bClickDown = true;
								}
								SKSceneManage.getInstance().turnToLoginPop(
										iCall, CALLTYPE.OPER);
							}
						}
					}
				}
				return false;
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isClick) {
					isClick = true;
					if (!bSlid) {
						// 没有启用滑动功能的
						touchEvent = true;
						isDral = true;
						mUIHandler.removeMessages(HANDLER_CLICK_UP);
						if (info.getmTouchInfo() == null
								|| !info.getmTouchInfo().isbTimeoutCancel()) {
							doTouchEvent();
						} else {
							// Log.d(TAG, "HANDLER_PRESS..........");
							mUIHandler.removeMessages(HANDLER_PRESS);
							mUIHandler.sendEmptyMessageDelayed(HANDLER_PRESS,
									info.getmTouchInfo().getnPressTime() * 100);
							bClickDown = true;
							SKSceneManage.getInstance().onRefresh(item);
						}

						// 对点动事件 进行特殊处理
						if (invalidJOG) {
							touchEvent = false;
							invalidJOG = false;
						}

					} else {
						// 启动了滑动功能
						mUIHandler.removeMessages(HANDLER_CLICK_UP);
						isMove = isMove(X, Y);
					}

				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				isMove = false;
				touchEvent = true;
				doTouch(false, isDral,event.getAction());
				isDral = true;
				isClick = false;
				isReset = false;
				// reset();
				// if (!bSlid) {
				// mUIHandler.removeMessages(HANDLER_CLICK_UP);
				// mUIHandler.sendEmptyMessageDelayed(HANDLER_CLICK_UP, 100);
				// }

				// if (!bSlid) {
				mUIHandler.removeMessages(HANDLER_CLICK_UP);
				mUIHandler.sendEmptyMessageDelayed(HANDLER_CLICK_UP, 100);
				// }
				break;
			case MotionEvent.ACTION_MOVE:
				touchEvent = true;
				if (isMove) {
					if (isLevel) {
						// 水平滑动
						if (Math.abs(X - nStartX) > 5) {
							move(X, Y, isLevel);
							nStartX = X;
						}
					} else {
						// 垂直滑动
						if (Math.abs(Y - nStartY) > 5) {
							move(X, Y, isLevel);
							nStartY = Y;
						}
					}
				}
				break;
			}
		}
		return touchEvent;
	}

	private boolean bTrigger = false;// 是否触发
	private boolean bChange = false;// 需要改变方向的

	private void move(float x, float y, boolean level) {

		if (bTrigger) {
			// 已经触发返回
			return;
		}

		if (level) {
			// 水平方向
			float len = x - nStartX;
			if (len > 0) {
				// 向右移动
				if (mSlidRect.left + len >= info.getnLp() + info.getnWidth()
						- mSlidRect.width() - 6 * nPadding) {
					// 触发
					if (mSlidRect.right >= info.getnLp() + info.getnWidth()
							- nPadding) {
						return;
					}
					// Log.d(TAG, "---right---");
					bTrigger = true;
					isDrawOne = true;
					isMove = true;
					int width = mSlidRect.width();
					if (bChange) {
						isDrawOne = false;
					}
					mSlidRect.right = info.getnLp() + info.getnWidth()
							- nPadding;
					mSlidRect.left = mSlidRect.right - width;
					trigger();
				} else {
					mSlidRect.left += len;
					mSlidRect.right += len;
					SKSceneManage.getInstance().onRefresh(item);
				}
			} else {
				// 向左移动
				if (!bChange) {
					return;
				}
				if (mSlidRect.left + len < info.getnLp() + 6 * nPadding) {
					// 触发
					if (mSlidRect.left <= info.getnLp() + nPadding) {
						return;
					}
					// Log.d(TAG, "---left---");
					bTrigger = true;
					isDrawOne = true;
					isMove = true;
					int width = mSlidRect.width();
					mSlidRect.left = info.getnLp() + nPadding;
					mSlidRect.right = mSlidRect.left + width;
					trigger();
				} else {
					mSlidRect.left += len;
					mSlidRect.right += len;
					SKSceneManage.getInstance().onRefresh(item);
				}
			}
		} else {
			// 垂直方向
			float len = y - nStartY;
			if (len > 0) {
				// 向下移动
				if (mSlidRect.top + len > info.getnTp() + info.getnHeight()
						- mSlidRect.height() - 6 * nPadding) {
					// 触发
					if (mSlidRect.bottom >= info.getnTp() + info.getnHeight()
							- nPadding) {
						return;
					}
					// Log.d(TAG, "---top---");
					bTrigger = true;
					isDrawOne = true;
					isMove = true;
					int heigth = mSlidRect.height();
					if (bChange) {
						isDrawOne = false;
					}
					mSlidRect.bottom = info.getnTp() + info.getnHeight()
							- nPadding;
					mSlidRect.top = mSlidRect.bottom - heigth;
					trigger();
				} else {
					mSlidRect.top += len;
					mSlidRect.bottom += len;
					SKSceneManage.getInstance().onRefresh(item);
				}
			} else {
				// 向上移动
				if (!bChange) {
					return;
				}
				if (mSlidRect.top + len < info.getnTp() + 6 * nPadding) {
					// 触发
					if (mSlidRect.top <= info.getnTp() + nPadding) {
						return;
					}
					// Log.d(TAG, "---bottom---");
					bTrigger = true;
					isDrawOne = true;
					isMove = true;
					int heigth = mSlidRect.height();
					mSlidRect.top = info.getnTp() + nPadding;
					mSlidRect.bottom = mSlidRect.top + heigth;
					trigger();
				} else {
					mSlidRect.top += len;
					mSlidRect.bottom += len;
					SKSceneManage.getInstance().onRefresh(item);
				}
			}
		}
	}

	/**
	 * 滑动事件触发
	 */
	private void trigger() {
		if (info.getmTouchInfo() == null
				|| !info.getmTouchInfo().isbTimeoutCancel()) {
			doTouchEvent();
		} else {
			mUIHandler.sendEmptyMessageDelayed(HANDLER_PRESS, info
					.getmTouchInfo().getnPressTime() * 100);
		}
	}

	/**
	 * 是否可以移动
	 */
	private boolean isMove(float x, float y) {
		boolean result = false;
		isReset = true;
		if (isLevel) {
			// 水平
			if (x > mSlidRect.left && x < mSlidRect.right && y > info.getnTp()
					&& y < info.getnTp() + info.getnHeight()) {
				nStartX = x;
				nStartY = y;
				result = true;
			}
		} else {
			// 垂直
			if (x > info.getnLp() && x < info.getnLp() + info.getnWidth()
					&& y > mSlidRect.top && y < mSlidRect.bottom) {
				nStartX = x;
				nStartY = y;
				result = true;
			}
		}

		return result;
	}

	private void resetState(long state) {
		if (!bSlid) {
			return;
		}
		if (isLevel) {
			int width = mSlidRect.width();
			if (state == 0) { // off状态 - 滑动到左边
				isDrawOne = true;
				mSlidRect.left = info.getnLp() + nPadding;
				mSlidRect.right = mSlidRect.left + width;
			} else {// on 状态 --滑动到右边
				isDrawOne = false;
				mSlidRect.right = info.getnLp() + info.getnWidth() - nPadding;
				mSlidRect.left = mSlidRect.right - width;
			}
		} else {
			int heigth = mSlidRect.height();
			if (state == 0) { // off状态 -- 滑动到顶部
				isDrawOne = true;
				mSlidRect.top = info.getnTp() + nPadding;
				mSlidRect.bottom = mSlidRect.top + heigth;
			} else { // on状态 --滑动到底部
				isDrawOne = false;
				mSlidRect.bottom = info.getnTp() + info.getnHeight() - nPadding;
				mSlidRect.top = mSlidRect.bottom - heigth;
			}
		}

		SKSceneManage.getInstance().onRefresh(item);
	}

	/**
	 * 滑动图片复位
	 */
	private void reset() {
		// Log.d(TAG, "---reset---");
		if (!bSlid) {
			return;
		}
		if (isLevel) {
			int width = mSlidRect.width();
			if (bChange) {
				if (isDrawOne) {
					// 复位到左边
					if (mSlidRect.right < info.getnLp() + info.getnWidth() - 2
							* nPadding) {
						mSlidRect.left = info.getnLp() + nPadding;
						mSlidRect.right = mSlidRect.left + width;
					}
				} else {
					// 复位到右边
					if (mSlidRect.left > info.getnLp() + 2 * nPadding) {
						mSlidRect.right = info.getnLp() + info.getnWidth()
								- nPadding;
						mSlidRect.left = mSlidRect.right - width;
					}
				}
			} else {
				// 没有改变方向的，直接复位到起始位置
				mSlidRect.left = info.getnLp() + nPadding;
				mSlidRect.right = mSlidRect.left + width;
			}
		} else {
			int heigth = mSlidRect.height();
			if (bChange) {
				if (isDrawOne) {
					// 复位到顶部
					if (mSlidRect.bottom < info.getnTp() + info.getnHeight()
							- 2 * nPadding) {
						mSlidRect.top = info.getnTp() + nPadding;
						mSlidRect.bottom = mSlidRect.top + heigth;
					}
				} else {
					// 复位到底部
					if (mSlidRect.top > info.getnTp() + 2 * nPadding) {
						mSlidRect.bottom = info.getnTp() + info.getnHeight()
								- nPadding;
						mSlidRect.top = mSlidRect.bottom - heigth;
					}
				}
			} else {
				// 没有改变方向的，直接复位到起始位置
				mSlidRect.top = info.getnTp() + nPadding;
				mSlidRect.bottom = mSlidRect.top + heigth;
			}
		}
		SKSceneManage.getInstance().onRefresh(item);
	}

	/**
	 * 操作确定回调
	 */
	SKSwitchOperDialog.IOperCall iOperCall = new SKSwitchOperDialog.IOperCall() {

		@Override
		public void onConfirm(int action) {
			bTrigger = false;
			mUIHandler.removeMessages(HANDLER_OPER);
			if (touch) {
				doTouch(true, true,action);
			} else {
				bClickDown = false;
			}
		}

		@Override
		public void onCancel() {
			bTrigger = false;
			mUIHandler.removeMessages(HANDLER_OPER);
		}

		@Override
		public void onStartMacro(int action) {
			// TODO 自动生成的方法存根
			startMacro();
		}

	};

	/**
	 * 执行宏指令
	 */
	private void startMacro() {
		if (true == info.isbIsStartStatement()) {
			// 请求执行控件宏指令
			if (info.geteButtonType() == BUTTON_TYPE.PECULIAR) {
				// 特殊开关
				PeculiarButtonInfo pInfo = (PeculiarButtonInfo) info;
				if (pInfo.getePeculiarType() == PECULIAR_TYPE.CHANGE_USER) {
					MacroManager.getInstance(null).Request(MSERV.CALLCM,
							(short) info.getnStatementId());
				}
			}
		}
	}

	/**
	 * 定时回调接口
	 */
	SKTimer.ICallback callback = new SKTimer.ICallback() {

		@Override
		public void onUpdate() {

			update();
		}
	};

	/**
	 * 语言改变回调
	 */
	SKLanguage.ICallback lCallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			isFlickState = true;// 重新获取状态的值
			if (info != null) {
				if (info.getmTextList() != null) {
					if (info.getmTextList().size() > nCurrentState) {
						TextInfo mTextInfo = info.getmTextList().get(
								nCurrentState);
						if (mTextInfo.getmTextItem() == null
								|| mTextInfo.getnLangugeId() != SystemInfo
										.getCurrentLanguageId()) {
							setState(nCurrentState, mTextInfo);
							mItem = info.getmTextList().get(nCurrentState)
									.getmTextItem();
						}
					}
				}
			}
			SKSceneManage.getInstance().onRefresh(item);
		}
	};

	/**
	 * 注册显现和触控地址
	 */
	private void registNotice() {
		// 注册定时器
		if (hasFlick) {
			SKTimer.getInstance().getBinder().onRegister(callback, nFlickTime);
		}

		// 注册语言改变通知
		if (SystemInfo.getLanguageNumber() > 1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}

		// 通知画面管理,调用控件的绘制方法
		SKSceneManage.getInstance().onRefresh(item);
	}

	private Vector<Short> mSData;
	private Vector<Float> mFData;
	SKPlcNoticThread.IPlcNoticCallBack dynamicCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue.size() == 0) {
				return;
			}
			double value = 0;

			WordButtonInfo wInfo = (WordButtonInfo) info;

			if (wInfo.geteDataType() == DATA_TYPE.INT_16
					|| wInfo.geteDataType() == DATA_TYPE.BCD_16
					|| wInfo.geteDataType() == DATA_TYPE.HEX_16
					|| wInfo.geteDataType() == DATA_TYPE.OTC_16
					|| wInfo.geteDataType() == DATA_TYPE.POSITIVE_INT_16) {
				if (mSData == null) {
					mSData = new Vector<Short>();
				} else {
					mSData.clear();
				}

				boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,
						mSData);
				if (!result || mSData.size() == 0) {
					return;
				}

				value = mSData.get(0);
			} else if (wInfo.geteDataType() == DATA_TYPE.INT_32
					|| wInfo.geteDataType() == DATA_TYPE.OTC_32
					|| wInfo.geteDataType() == DATA_TYPE.HEX_32
					|| wInfo.geteDataType() == DATA_TYPE.BCD_32
					|| wInfo.geteDataType() == DATA_TYPE.POSITIVE_INT_32) {
				if (mIData == null) {
					mIData = new Vector<Integer>();
				} else {
					mIData.clear();
				}

				boolean result = PlcRegCmnStcTools.bytesToInts(nStatusValue,
						mIData);
				if (!result || mIData.size() == 0) {
					return;
				}
				value = mIData.get(0);
			} else if (wInfo.geteDataType() == DATA_TYPE.FLOAT_32) {
				if (mFData == null) {
					mFData = new Vector<Float>();
				} else {
					mFData.clear();
				}

				boolean result = PlcRegCmnStcTools.bytesToFloats(nStatusValue,
						mFData);
				if (!result || mFData.size() == 0) {
					return;
				}
				value = mFData.get(0);
			}

			if (mButtonFunction != null) {
				mButtonFunction.setnDynamicValue(value);
			}
		}
	};

	/**
	 * 触控地址改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isTouch();
		}

	};

	/**
	 * 显现地址改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}

	};

	/**
	 * 监视地址改变
	 */
	private Vector<Integer> mIData = null;
	private Vector<Long> mLData = null;
	SKPlcNoticThread.IPlcNoticCallBack watchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			double value = 0;
			boolean result;
			if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
				// 双态监视
				if (nStatusValue.size() == 0) {
					return;
				}
				value = nStatusValue.get(0);
				if (info.isbAddrType()) {
					// 字的第几位
					if (mIData == null) {
						mIData = new Vector<Integer>();
					} else {
						mIData.clear();
					}
					result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
							mIData);
					value = mIData.get(0);
					if (result) {
						value = (long) value & (1 << (info.getnBitIndex() - 1));
						if (value == 0) {
							value = 0;
						} else {
							value = 1;
						}
					} else {
						return;
					}
				}
			} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
				// 多态监视
				value = change(nStatusValue);
			} else if (bSlid && info instanceof BitButtonInfo) {
				if (nStatusValue.size() == 0) {
					return;
				}
				value = nStatusValue.get(0);
				resetState((long) value);

				return;
			}
			// Log.d(TAG, "id:"+nButtonId+",value:"+value);
			watch(value);
		}

	};

	/**
	 * 监视地址值发生变化
	 */
	private Vector<Integer> mIDataList = null;
	private Vector<Short> mSDataList = null;
	private Vector<Long> mLDataList = null;
	private Vector<Float> mFDataList = null;

	private double change(Vector<Byte> nStatusValue) {

		double value = 0;
		if (info.geteWatchDataType() == null) {
			return 0;
		}
		boolean result = false;
		switch (info.geteWatchDataType()) {
		case INT_16:
			// 16位整数
			if (mSDataList == null) {
				mSDataList = new Vector<Short>();
			} else {
				mSDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSDataList);
			if (result) {
				if (mSDataList == null || mSDataList.size() == 0) {
					return 0;
				}
				value = mSDataList.get(0);
			}
			break;
		case POSITIVE_INT_16:
			// 16正整数
			if (mIDataList == null) {
				mIDataList = new Vector<Integer>();
			} else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				if (mIDataList == null || mIDataList.size() == 0) {
					return 0;
				}
				value = mIDataList.get(0);
			}
			break;
		case INT_32:
			// 32整数
			if (mIDataList == null) {
				mIDataList = new Vector<Integer>();
			} else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToInts(nStatusValue, mIDataList);
			if (result) {
				if (mIDataList == null || mIDataList.size() == 0) {
					return 0;
				}
				value = mIDataList.get(0);
			}
			break;
		case POSITIVE_INT_32:
			// 32位正整数
			if (mLDataList == null) {
				mLDataList = new Vector<Long>();
			} else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				if (mLDataList == null || mLDataList.size() == 0) {
					return 0;
				}
				value = mLDataList.get(0);
			}
			break;
		case BCD_16:
			// 调用BCD码转换
			if (mIDataList == null) {
				mIDataList = new Vector<Integer>();
			} else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				if (mIDataList.size() > 0) {
					int nV = 0;
					String s = DataTypeFormat.intToBcdStr(
							(long) mIDataList.get(0), false);
					if (s != null && !s.equals("ERROR")) {
						try {
							nV = Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					value = nV;
				}
			}
			break;
		case BCD_32:
			// 调用BCD码转换
			if (mLDataList == null) {
				mLDataList = new Vector<Long>();
			} else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				if (mLDataList.size() > 0) {
					int nV = 0;
					String s = DataTypeFormat.intToBcdStr(
							(long) mLDataList.get(0), false);
					if (s != null && !s.equals("ERROR")) {
						try {
							nV = Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					value = nV;
				}
			}
			break;
		case FLOAT_32:
			// 浮点数
			if (mFDataList == null) {
				mFDataList = new Vector<Float>();
			} else {
				mFDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToFloats(nStatusValue, mFDataList);
			if (result) {
				if (mFDataList == null || mFDataList.size() == 0) {
					return 0;
				}
				if (mFDataList.size() > 0) {
					value = mFDataList.get(0);
				}
			}
			break;
		}
		return value;
	}

	/**
	 * 触控事件处理
	 */
	private void doTouchEvent() {

		/* 操作确定 */
		if (info.geteButtonType() == BUTTON_TYPE.BIT) {
			BitButtonInfo bInfo = (BitButtonInfo) info;
			if (bInfo.isbConfirm()) {
				// 位开关，操作确定
				bClickDown = true;
				SKSceneManage.getInstance().onRefresh(item);
				if (mOperDialog == null || !mOperDialog.isShow) {
					mOperDialog = new SKSwitchOperDialog(mContext);
					mOperDialog.setiOperCall(iOperCall);
					mOperDialog.showPopWindow();
					mUIHandler.sendEmptyMessageDelayed(HANDLER_OPER,
							bInfo.getnTimeOut() * 1000);
				}
				return;
			}
		}

		bClickDown = true;
		doTouch(true, true,MotionEvent.ACTION_DOWN);
		bTrigger = false;
		SKSceneManage.getInstance().onRefresh(item);
	}

	
	private long downTime = 0;
	private boolean clickDown;// 点击事件,已经触发
	private boolean bRefreshed;// 画面切换，等待ui绘制完毕才跳转，不然没有点击效果
	private boolean invalidJOG = false; // true/false : 对应无效点动 / 有效点动
	/**
	 * 提供给外部接口
	 * @param down-true,表示按下，down-false,表示松开;
	 * @param isDeal-是否处理点击事件
	 * @param action-点击事件
	 */
	public synchronized void doTouch(boolean down, boolean isDeal,int action) {
		if (mButtonFunction == null) {
			mButtonFunction = new SKButtonFunction(
					SKSceneManage.getInstance().mContext, nSceneId);
		}

		// 按下的时间间隔必须大于30毫秒,点动的不做处理,添加了判断之后，长按有问题
		// invalidJOG = false;
		if (System.currentTimeMillis() - downTime < 80 && down) {
			clickDown = down;
			invalidJOG = true;
			Log.d(TAG, "button on touch time<80 ms...");
			if (info.geteButtonType() == BUTTON_TYPE.BIT) {
				// 位
				BitButtonInfo bInfo = (BitButtonInfo) info;
				if (bInfo.geteOperType() != BIT_OPER_TYPE.JOG) {
					// 位，除了点动，都需要做误操作判断
					return;
				} else { // 如果是 点动， 则认为是 无效的点动
					invalidJOG = true;
				}
			} else if (info.geteButtonType() == BUTTON_TYPE.WORD) {
				// 字
				WordButtonInfo wInfo = (WordButtonInfo) info;
				if (wInfo.geteOperType() == WORD_OPER_TYPE.ADD
						|| wInfo.geteOperType() == WORD_OPER_TYPE.MINUS
						|| wInfo.geteOperType() == WORD_OPER_TYPE.INPUT_VALUE) {
					// 字，除了了连接加和连接减之后，其他都需要做误操作判断
					return;
				}
			}
		} else if (down) {
			invalidJOG = false;
		}

		// 防止误操作-点击一次变成点击两次
		downTime = System.currentTimeMillis();
		switch (info.geteButtonType()) {
		case BIT: // 位按钮
			clickDown = down;
			mButtonFunction.writeToPlc((BitButtonInfo) info, down);
			break;
		case WORD: // 字按钮
			mButtonFunction.writeToPlc((WordButtonInfo) info, down, true);
			break;
		case SCENE: // 画面按钮
			clickDown = down;
			if (down) {
				if (nButtonType == 0) {
					// 开关本身调用
					if (bClickDown) {
						// 用于按压延迟
						bRefreshed = false;
						// mUIHandler.removeMessages(HANDLER_SCREEN);
						// mUIHandler.sendEmptyMessage(HANDLER_SCREEN);
						mButtonFunction.sceneOper((SceneButtonInfo) info);
					}
				} else {
					// 外部调用
					mButtonFunction.sceneOper((SceneButtonInfo) info);
				}
			}
			break;
		case PECULIAR:// 特殊按钮
			if (down) {
				mButtonFunction.iOperCall = iOperCall;
				mButtonFunction.peculiarOper((PeculiarButtonInfo) info);
			}
			break;
		}

		if (down) {
			if (info.getmTouchInfo() != null) {
				// 操作通知
				noticeAddr(info.getmTouchInfo(), true);
			}
		}

		Log.d(TAG, "action ====== "+action);
		// 若启用宏指令
		// 请求执行控件宏指令
		boolean result = true;
		if (info.geteButtonType() == BUTTON_TYPE.PECULIAR) {
			// 特殊开关
			PeculiarButtonInfo pInfo = (PeculiarButtonInfo) info;
			if (pInfo.getePeculiarType() == PECULIAR_TYPE.CHANGE_USER) {
				// 切换用户,等待操作完成才，执行宏指令
				result = false;
			}
		}

		if (action == MotionEvent.ACTION_DOWN) {
			// 按下
			if (result&&info.isbIsStartStatement()) {
				MacroManager.getInstance(null).Request(MSERV.CALLCM,
						(short) info.getnStatementId());
			}
		} else if (action == MotionEvent.ACTION_UP) {
			// 弹起
			if (result&&info.isUpRun()) {
				MacroManager.getInstance(null).Request(MSERV.CALLCM,
						(short) info.getnStatemenUpId());
			}
		}
		
	}

	public boolean getJoyEvent() {
		return invalidJOG;
	}

	/**
	 * 监视
	 */
	private void watch(double value) {

		if (info != null) {
			ArrayList<StakeoutInfo> list = info.getmStakeoutList();
			if (list != null) {
				boolean reuslt = false;// 是否匹配到状态
				for (int i = 0; i < list.size(); i++) {
					if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
						// Log.d(TAG,
						// "value="+value+",nCurrentState="+nCurrentState+",id="+nButtonId);
						if (value == list.get(i).getnStatusId()
								&& list.get(i).getnStatusId() != nCurrentState) {
							// 状态改变通知UI刷新
							nCurrentState = list.get(i).getnStatusId();
							nFlickState = nCurrentState;
							isFlick = false;
							SKSceneManage.getInstance().onRefresh(item);
							break;
						}
					} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
						if (info.getnCondition() == 1) {
							// 按照寄存器的位切换
							long v = (long) value;
							long temp = v & (1 << i);
							if (temp > 0) {
								reuslt = true;
								if (list.get(i).getnStatusId() != nCurrentState) {
									// 状态改变通知UI刷新
									nCurrentState = list.get(i).getnStatusId();
									nFlickState = nCurrentState;
									isFlick = false;
									SKSceneManage.getInstance().onRefresh(item);
								}
								break;
							}

						} else {
							if (value == list.get(i).getnCmpFactor()) {
								reuslt = true;
								if (list.get(i).getnStatusId() != nCurrentState) {
									// 状态改变通知UI刷新
									nCurrentState = list.get(i).getnStatusId();
									nFlickState = nCurrentState;
									isFlick = false;
									SKSceneManage.getInstance().onRefresh(item);
								}
								break;
							}
						}
					}

				}

				if (!reuslt) {
					// 如果多状态没有匹配到，则显示最后一个状态
					if (info.geteWatchType() == WATCH_TYPE.POLY) {
						int index = list.size() - 1;
						if (list.get(index).getnStatusId() != nCurrentState) {
							// 状态改变通知UI刷新
							nCurrentState = list.get(index).getnStatusId();
							nFlickState = nCurrentState;
							isFlick = false;
							SKSceneManage.getInstance().onRefresh(item);
						}
					}
				}
			}
		}

	}

	/**
	 * 定时更新
	 */
	private void update() {
		// 500毫秒,闪烁一次
		if (isFlick) {
			flick();
		}

	}

	/**
	 * 文字闪烁
	 */
	private boolean isChange = false;// 闪烁状态切换
	private boolean isFlickState = false;

	private void flick() {
		isFlickState = false;
		for (int i = 0; i < info.getmStakeoutList().size(); i++) {
			StakeoutInfo sInfo = info.getmStakeoutList().get(i);
			if (sInfo.getnStatusId() == nCurrentState) {
				switch (sInfo.geteFlickType()) {
				case FLICK_TEXT:
					// 文字显隐
					if (isChange) {
						if (mItem != null) {
							StaticTextModel model = mItem.getmText();
							if (model != null) {
								model.setM_sTextStr("");
							}
						}
						isChange = false;
					} else {
						String text = "";
						StaticTextModel model = null;
						if (info.getmTextList().get(i).getmTextList().size() > SystemInfo
								.getCurrentLanguageId()) {
							if (info.getmTextList().get(i).getmTextItem() != null) {
								model = info.getmTextList().get(i)
										.getmTextItem().getmText();
								if (model != null) {
									text = info
											.getmTextList()
											.get(i)
											.getmTextList()
											.get(SystemInfo
													.getCurrentLanguageId());
									model.setM_sTextStr(text);
								}
							}
						}
						isChange = true;
					}
					nFlickState = nCurrentState;
					isFlick = true;
					SKSceneManage.getInstance().onRefresh(item);
					break;
				case FLICK_STATUS:
					isFlickState = true;
					// 状态切换
					if (isChange) {
						if (nCurrentState == 1) {
							nFlickState = 0;
						} else if (nCurrentState == 0) {
							nFlickState = 1;
						}
						isChange = false;
					} else {
						nFlickState = nCurrentState;
						isChange = true;
					}

					isFlick = true;
					SKSceneManage.getInstance().onRefresh(item);
					break;
				case FLICK_SHOW:
					// 控件显隐
					if (isChange) {
						nFlickState = -1;
						isChange = false;
					} else {
						nFlickState = nCurrentState;
						isChange = true;
					}
					isFlick = true;
					SKSceneManage.getInstance().onRefresh(item);
					break;
				}
			}
		}
	}

	class UIHandler extends Handler {

		public UIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {
			case HANDLER_CLICK_UP:
				if (bSlid) {
					reset();
				}
				if (bClickDown) {
					mUIHandler.removeMessages(HANDLER_PRESS);
					SKSceneManage.getInstance().onRefresh(item);
					bClickDown = false;
				}
				break;
			case HANDLER_PRESS:// 按压
				if (isClick) {
					doTouchEvent();
				}
				break;
			case HANDLER_OPER:
				// 操作确定，关闭对话框
				bTrigger = false;
				if (mOperDialog != null) {
					if (mOperDialog.isShow) {
						mOperDialog.hidePopWindow();
					}
				}
				break;
			case HANDLER_SCREEN:
				if (bRefreshed) {
					bRefreshed = false;
					mButtonFunction.sceneOper((SceneButtonInfo) info);
				} else {
					mUIHandler.sendEmptyMessageDelayed(HANDLER_SCREEN, 20);
				}
				// Log.d(TAG,
				// "ak button HANDLER_SCREEN ...bRefreshed="+bRefreshed);
				break;
			}
		}

	}

	public SKButtonFunction getmButtonFunction() {
		return mButtonFunction;
	}

	/**
	 * 设置滑动块
	 */
	private void setSlidBitmap() {
		if (info == null) {
			return;
		}
		if (info.isbSlid()) {
			// 有滑动功能
			if (info.getnWidth() >= 100 || info.getnHeight() >= 100) {
				// 必须有一边长度大于等于100
				if (info.getnWidth() >= 30 && info.getnHeight() >= 30) {
					// 短边不能少于30

					bChange = false;
					bSlid = true;
					isDrawOne = true;

					if (info.geteButtonType() == BUTTON_TYPE.BIT) {
						BitButtonInfo bInfo = (BitButtonInfo) info;
						if (bInfo.geteOperType() == BIT_OPER_TYPE.REPLACE) {
							bChange = true;// 触发时需要改变方向的
						}
					}

					if (info.getnWidth() > info.getnHeight()) {
						// 水平滑动的
						isLevel = true;
						Bitmap left = ImageFileTool.getBitmap(
								R.drawable.slid_left, mContext);
						Bitmap right = ImageFileTool.getBitmap(
								R.drawable.slid_right, mContext);
						double width = info.getnWidth() * 0.25;
						if (width < 40) {
							width = 40;
						}
						float sx = (float) (width / left.getWidth());
						// float temp=sx*left.getHeight();
						float sy = ((float) (info.getnHeight() - 4 * nPadding))
								/ left.getHeight();
						Matrix matrix = new Matrix();
						matrix.postScale(sx, sy);

						mOneSlidBitmap = Bitmap
								.createBitmap(left, 0, 0, left.getWidth(),
										left.getHeight(), matrix, true);
						mTwoSlidBitmap = Bitmap.createBitmap(right, 0, 0,
								right.getWidth(), right.getHeight(), matrix,
								true);

						int ntop = (int) ((float) (info.getnHeight() - mOneSlidBitmap
								.getHeight()) / 2) + info.getnTp();
						int nleft = info.getnLp() + nPadding;
						mSlidRect = new Rect(nleft, ntop, nleft
								+ mOneSlidBitmap.getWidth(), ntop
								+ mOneSlidBitmap.getHeight());
					} else {
						// 垂直滑动的
						isLevel = false;
						Bitmap top = ImageFileTool.getBitmap(
								R.drawable.slid_top, mContext);
						Bitmap bottm = ImageFileTool.getBitmap(
								R.drawable.slid_bottom, mContext);
						double height = info.getnHeight() * 0.25;
						if (height < 40) {
							height = 40;
						}
						float sx = (float) (height / top.getHeight());
						float temp = sx * top.getWidth();
						if (temp > info.getnWidth()) {
							sx = ((float) (info.getnWidth() - 2 * nPadding))
									/ top.getWidth();
						}
						Matrix matrix = new Matrix();
						matrix.postScale(sx, sx);

						mOneSlidBitmap = Bitmap.createBitmap(top, 0, 0,
								top.getWidth(), top.getHeight(), matrix, true);
						mTwoSlidBitmap = Bitmap.createBitmap(bottm, 0, 0,
								bottm.getWidth(), bottm.getHeight(), matrix,
								true);

						int ntop = info.getnTp() + nPadding;
						int nleft = (int) ((float) (info.getnWidth() - mOneSlidBitmap
								.getWidth()) / 2) + info.getnLp();

						mSlidRect = new Rect(nleft, ntop, nleft
								+ mOneSlidBitmap.getWidth(), ntop
								+ mOneSlidBitmap.getHeight());
					}

				}
			}
		}
	}

	/**
	 * @param type
	 *            -1 凸 type-2 凹
	 */
	private Bitmap getRectFrame(int type, Rect rect) {
		if (rect == null || rect.width() < 6 || rect.height() < 6
				|| mContext == null) {
			return null;
		}

		Bitmap mBitmap = null;
		String name = "B" + type + "_" + rect.width() + "_" + rect.height();
		mBitmap = ImageFileTool.getBitmap(name);
		if (mBitmap != null) {
			return mBitmap;
		}

		mBitmap = Bitmap.createBitmap(rect.width(), rect.height(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);

		Rect hRect = new Rect();
		Rect vRect = new Rect();

		if (type == 1) {

			/**
			 * 凸
			 */

			// 左上
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_lt, mContext),
					0, 0, paint);
			// 左下
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_lb, mContext),
					0, rect.height() - 2, paint);
			// 右上
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_rt, mContext),
					rect.width() - 2, 0, paint);
			// 右下
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_rb, mContext),
					rect.width() - 2, rect.height() - 2, paint);

			/* 水平 */
			// 上
			hRect.left = 2;
			hRect.right = rect.width() - 2;
			hRect.top = 0;
			hRect.bottom = 2;
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_wt, mContext),
					null, hRect, paint);

			// 下
			hRect.top = rect.height() - 2;
			hRect.bottom = rect.height();
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_gb, mContext),
					null, hRect, paint);

			// 垂直
			// 左
			vRect.left = 0;
			vRect.right = 2;
			vRect.top = 2;
			vRect.bottom = rect.height() - 2;
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_wl, mContext),
					null, vRect, paint);

			vRect.left = rect.width() - 2;
			vRect.right = rect.width();
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.protrude_gr, mContext),
					null, vRect, paint);

		} else if (type == 2) {

			/**
			 * 凹
			 */

			// 左上
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_lt, mContext),
					0, 0, paint);
			// 左下
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_lb, mContext),
					0, rect.height() - 2, paint);
			// 右上
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_rt, mContext),
					rect.width() - 2, 0, paint);
			// 右下
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_rb, mContext),
					rect.width() - 2, rect.height() - 2, paint);

			/* 水平 */
			// 上
			hRect.left = 2;
			hRect.right = rect.width() - 2;
			hRect.top = 0;
			hRect.bottom = 2;
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_gt, mContext),
					null, hRect, paint);

			// 下
			hRect.top = rect.height() - 2;
			hRect.bottom = rect.height();
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_wb, mContext),
					null, hRect, paint);

			// 垂直
			// 左
			vRect.left = 0;
			vRect.right = 2;
			vRect.top = 2;
			vRect.bottom = rect.height() - 2;
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_gl, mContext),
					null, vRect, paint);

			vRect.left = rect.width() - 2;
			vRect.right = rect.width();
			canvas.drawBitmap(
					ImageFileTool.getBitmap(R.drawable.concave_wr, mContext),
					null, vRect, paint);

		}

		ImageFileTool.setBitmap(mBitmap, name);

		return mBitmap;
	}

	public ButtonInfo getInfo() {
		return info;
	}

	// 提取图像Alpha位图
	public static Bitmap getAlphaBitmap(Bitmap mBitmap, int mColor) {
		// BitmapDrawable mBitmapDrawable = (BitmapDrawable)
		// mContext.getResources().getDrawable(R.drawable.enemy_infantry_ninja);
		// Bitmap mBitmap = mBitmapDrawable.getBitmap();

		// BitmapDrawable的getIntrinsicWidth（）方法，Bitmap的getWidth（）方法
		// 注意这两个方法的区别
		// Bitmap mAlphaBitmap =
		// Bitmap.createBitmap(mBitmapDrawable.getIntrinsicWidth(),
		// mBitmapDrawable.getIntrinsicHeight(), Config.ARGB_8888);

		if (mBitmap == null) {
			return null;
		}

		Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Config.ARGB_8888);

		Canvas mCanvas = new Canvas(mAlphaBitmap);
		Paint mPaint = new Paint();

		mPaint.setColor(mColor);
		// 从原位图中提取只包含alpha的位图
		Bitmap alphaBitmap = mBitmap.extractAlpha();
		// 在画布上（mAlphaBitmap）绘制alpha位图
		mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

		return mAlphaBitmap;
	}

	/**
	 * 获取控件属性接口
	 */

	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnLp();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnTp();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
			return info.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (info != null) {
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
		if (info != null) {
			return getColor(info.getmStakeoutList().get(nCurrentState).getnColor());
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
		return show;
	}

	@Override
	public boolean getItemTouchable(int id) {
		// TODO Auto-generated method stub
		return touch;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (x == info.getnLp()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnLp((short) x);
			int l = item.rect.left;
			item.rect.left = x;
			item.rect.right = x - l + item.rect.right;
			dRect.left=item.rect.left+nPadding;
			dRect.right=item.rect.right-nPadding;
			item.mMoveRect = new Rect();
			for (int i = 0; i < info.getmTextList().size(); i++) {
				TextItem item=info.getmTextList().get(i).getmTextItem();
				if (item!=null) {
					StaticTextModel model=item.getModel();
					if (model!=null) {
						model.setStartX(x);
						model.setM_sTextStr(model.getM_sTextStr());
					}
				}
			}
			
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (y == info.getnTp()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnTp((short) y);
			int t = item.rect.top;
			item.rect.top = y;
			item.rect.bottom = y - t + item.rect.bottom;
			item.mMoveRect = new Rect();
			dRect.top=item.rect.top+nPadding;
			dRect.bottom=item.rect.bottom-nPadding;
			for (int i = 0; i < info.getmTextList().size(); i++) {
				TextItem item=info.getmTextList().get(i).getmTextItem();
				if (item!=null) {
					StaticTextModel model=item.getModel();
					if (model!=null) {
						model.setStartY(y);
						model.setM_sTextStr(model.getM_sTextStr());
					}
				}
			}
			
			SKSceneManage.getInstance().onRefresh(item);
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
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnWidth((short) w);
			item.rect.right = w - item.rect.width() + item.rect.right;
			item.mMoveRect = new Rect();
			dRect.right=item.rect.right-nPadding;
			mCRectBitmap=null;
			mRRectBitmap=null;
			for (int i = 0; i < info.getmTextList().size(); i++) {
				TextItem item=info.getmTextList().get(i).getmTextItem();
				if (item!=null) {
					StaticTextModel model=item.getModel();
					if (model!=null) {
						model.setRectWidth(w);
						model.setM_sTextStr(model.getM_sTextStr());
					}
				}
			}
			SKSceneManage.getInstance().onRefresh(item);
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
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnHeight((short) h);
			item.rect.bottom = h - item.rect.height() + item.rect.bottom;
			item.mMoveRect = new Rect();
			mCRectBitmap=null;
			mRRectBitmap=null;
			dRect.bottom=item.rect.bottom-nPadding;
			for (int i = 0; i < info.getmTextList().size(); i++) {
				TextItem item=info.getmTextList().get(i).getmTextItem();
				if (item!=null) {
					StaticTextModel model=item.getModel();
					if (model!=null) {
						model.setRectHeight(h);
						model.setM_sTextStr(model.getM_sTextStr());
					}
				}
			}
			
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
		if (info != null) {
			int color = Color.rgb(r, g, b);
			if (color == info.getmStakeoutList().get(nCurrentState).getnColor()) {
				return true;
			}
			info.getmStakeoutList().get(nCurrentState).setnColor(color);
			if (info.getmTextList().get(nCurrentState).getmTextItem()==null) {
				return false;
			}
			StaticTextModel model=info.getmTextList().get(nCurrentState).getmTextItem().getModel();
			if (model==null) {
				return false;
			}
			model.setM_backColorPadding(color);
			info.getmTextList().get(nCurrentState).getmTextItem().resetColor(color, 2);
			SKSceneManage.getInstance().onRefresh(item);
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
		if (v == show) {
			return true;
		}
		show = v;
		SKSceneManage.getInstance().onRefresh(item);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==touch) {
			return true;
		}
		touch = v;
		SKSceneManage.getInstance().onRefresh(item);
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
		if (info!=null) {
			boolean b=info.getmStakeoutList().get(nCurrentState).isbFlick();
			int t=info.getmStakeoutList().get(nCurrentState).getnFlickTime();
			if (v==b&&time==t) {
				return true;
			}
			if(time<1){
				return false;
			}
			SKTimer.getInstance().getBinder().onDestroy(callback, nFlickTime);
			if (v) {
				SKTimer.getInstance().getBinder().onRegister(callback, time);
			}
			nFlickTime=time;
			isFlick=v;
			info.getmStakeoutList().get(nCurrentState).setbFlick(v);
			info.getmStakeoutList().get(nCurrentState).setnFlickTime(time);
			SKSceneManage.getInstance().onRefresh(item);
			return true;
		}
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
		if (info==null||text==null) {
			return false;
		}
		String tmp="";
		if (info.getmTextList().get(nCurrentState).getmTextList().size()>lid) {
			tmp=info.getmTextList().get(nCurrentState).getmTextList().get(lid);
			if (text.equals(tmp)) {
				return true;
			}
			info.getmTextList().get(nCurrentState).getmTextList().set(lid, text);
			info.getmTextList().get(nCurrentState).getmTextItem().getModel().setM_sTextStr(text);
			SKSceneManage.getInstance().onRefresh(item);
		}
		return true;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO Auto-generated method stub
		if(info==null||alpha<0||alpha>255){
			return false;
		}
		if(info.getmStakeoutList().get(nCurrentState).getnAlpha()==alpha){
			return true;
		}
		info.getmStakeoutList().get(nCurrentState).setnAlpha(alpha);
		if (info.getmTextList().get(nCurrentState).getmTextItem()!=null) {
			info.getmTextList().get(nCurrentState).getmTextItem().resetAlpha(alpha);
		}
		SKSceneManage.getInstance().onRefresh(item);
		return true;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 获取RGB颜色
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
}
