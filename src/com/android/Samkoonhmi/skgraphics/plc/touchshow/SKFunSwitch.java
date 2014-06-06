package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.skbutton.FunSwitchInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skenum.BUTTON.WATCH_TYPE;
import com.android.Samkoonhmi.skenum.FLICK_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 多功能开关
 */
public class SKFunSwitch extends SKGraphCmnTouch implements IItem{
	
	private static final int nPadding = 2;
	private static final int HANDLER_CLICK_UP = 0;
	private static final int HANDLER_PRESS = 1;
	//控件id
	private int nItemId;
	//场景id
	private int nSceneId;
	//上下文
	private Context mContext;
	private Paint mPaint;
	//刷新信息
	private SKItems item;
	//文本对象
	private StaticTextModel model;
	//画文本
	private TextItem textItem;
	//控件信息对象
	private FunSwitchInfo info;
	private boolean show;         // 是否可显现
	private boolean touch;        // 是否可触控
	private boolean showByAddr;   // 是否注册显现地址
	private boolean touchByAddr;  // 是否注册触控地址
	private boolean showByUser;   // 是否受用户权限控件
	private boolean touchByUser;  // 是否受用户权限控件
	private Rect rect;            // 控件位置
	private Rect dRect;           // 控件点击后位置
	private boolean bClickDown;    // 点击
	private Bitmap mBitmap;
	private Bitmap bgBitmap;
	private Rect bgRect;
	private int nLanId;//语言id
	private Bitmap mLockBitmap;
	private Bitmap mRRectBitmap;
	private Bitmap mCRectBitmap;
	private int nColor=Color.argb(255, 255, 199, 0);
	private HashMap<String, Bitmap> mPicMap = null;//状态图片
	private HashMap<String, Bitmap> mClickBgMap=null;//点击效果图
	private boolean hasFlick;//所有状态中，是否有闪烁的，没有不注册
	
	
	public SKFunSwitch(int itemId,int sceneId,Context context,FunSwitchInfo info){
		this.nItemId = itemId;
		this.nSceneId = sceneId;
		this.mContext=context;
		this.info=info;
		this.bReset=true;
		nLanId=SystemInfo.getCurrentLanguageId();
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		item = new SKItems();
		model = new StaticTextModel();
		textItem = new TextItem(model);
		
		mPicMap = new HashMap<String, Bitmap>();
		mClickBgMap=new HashMap<String, Bitmap>();
		
		if (info!=null) {
			
			// 控件显示区域
			rect = new Rect(info.getnLp(), info.getnTp(), info.getnLp()
					+ info.getnWidth(), info.getnTp() + info.getnHeight());

			// 点击移位
			dRect = new Rect(rect.left + nPadding, rect.top + nPadding,
					rect.right - nPadding, rect.bottom - nPadding);
			
			item.sceneId = nSceneId;
			item.itemId = nItemId;
			item.nZvalue = info.getnZvalue();
			item.nCollidindId = info.getnCollidindId();
			item.rect = rect;
			item.mGraphics=this;
			
			initState();
			
			this.show = true;
			this.touch = true;
			this.showByAddr = false;
			this.touchByAddr = false;
			this.showByUser = false;
			this.touchByUser = false;
			
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
			
			//注册显现地址
			if (showByAddr) {
				ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(),
							showCall, true,nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(),
							showCall, false,nSceneId);
				}

			}
			
			//注册触控地址
			if (touchByAddr) {
				ADDRTYPE addrType = info.getmTouchInfo().geteCtlAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
							touchCall, true,nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
							touchCall, false,nSceneId);
				}
			}
				if (info.geteWatchType() != WATCH_TYPE.NONE) {
		if (info.getmWatchAddress() != null) {
				boolean b = false;
				if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
					// 双态
					if (info.isbAddrType()) {
						// 字
						b = false;
					} else {
						// 位
						b = true;
					}
				} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
					// 多态
					b = false;
				}
				SKPlcNoticThread.getInstance().addNoticProp(info.getmWatchAddress(), watchCall, b,nSceneId);
			}
		}
		}
	
	}

	/**
	 * 数据库读取
	 */
	@Override
	public void getDataFromDatabase() {
		
	}

	@Override
	public void setDataToDatabase() {
		
	}

	/**
	 * 控件初始化
	 */
	@Override
	public void initGraphics() {
		init();
	}

	/**
	 * 控件绘制
	 */
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			//是否可以显现
			if ((itemId == nItemId) && (null != info)) {
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

	@Override
	public void realseMemeory() {
		isClick=false;
		isFlick = false;
		bClickDown=false;
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onDestroy(lCallback);
		}
		if (hasFlick) {
			SKTimer.getInstance().getBinder().onDestroy(callback, 5);
		}
		hasFlick = false;
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

	private boolean isClick = false; // 是否已经处理点击事件，防止多次点击
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		if (!show) {
			return false;
		}
		
		float X = event.getX();
		float Y = event.getY();
		
		if (info == null){
			return false;
		}
		
		if (X < info.getnLp() || Y < info.getnTp()
				|| X > (info.getnLp() + info.getnWidth())
				|| Y > (info.getnTp() + info.getnHeight())) {
			if (isClick) {
				isClick=false;
				//在控件范围内没不抓到弹起事件
				if (info.getmSkButtons()!=null) {
					for (int i = 0; i < info.getmSkButtons().size(); i++) {
						SKButton button=info.getmSkButtons().get(i);
						button.doTouch(false,true,MotionEvent.ACTION_UP);
					}
				}
				handler.sendEmptyMessageDelayed(HANDLER_CLICK_UP, 100);
			}
			return false;
		}else {
			
			if (!touch || !show) {
				// 控件不可触摸
				if (!touch&&info!=null) {
					if (info.getmTouchInfo()!=null) {
						if (event.getAction()==MotionEvent.ACTION_DOWN) {
							if (info.getmTouchInfo().isbTouchByUser()) {
								SKSceneManage.getInstance().turnToLoginPop();
							}
						}
					}
				}
				return false;
			}
			
			if (info.getmSkButtons()!=null) {
				if (event.getAction()==MotionEvent.ACTION_DOWN) {
					boolean  invalidJoy = true;
					if (!isClick) {
						isClick=true;
						bClickDown=true;
						SKSceneManage.getInstance().onRefresh(item);
						handler.removeMessages(HANDLER_CLICK_UP);
						
						if (info.getmTouchInfo() != null
								&&info.getmTouchInfo().isbTimeoutCancel()) {
							// Log.d(TAG, "HANDLER_PRESS..........");
							handler.sendEmptyMessageDelayed(HANDLER_PRESS,
									info.getmTouchInfo().getnPressTime() * 100);
							bClickDown = true;
							SKSceneManage.getInstance().onRefresh(item);
							return true;
						}
						
						invalidJoy = doTouchEvent(invalidJoy);
						
					}
					return !invalidJoy;
				}else if(event.getAction()==MotionEvent.ACTION_CANCEL||
						event.getAction()==MotionEvent.ACTION_UP) {
					
					for (int i = 0; i < info.getmSkButtons().size(); i++) {
						SKButton button=info.getmSkButtons().get(i);
						button.doTouch(false,true,MotionEvent.ACTION_UP);
					}
					handler.removeMessages(HANDLER_CLICK_UP);
					handler.sendEmptyMessageDelayed(HANDLER_CLICK_UP, 100);
					isClick=false;
				}else if (event.getAction()==MotionEvent.ACTION_MOVE) {
					return true;
				}
				
			}
			
			return true;
		}
	}

	/**
	 * 初始化
	 */
	private void init(){
		if (info == null) {
			return;
		}
		isClick=false;
		nFlickState = 0;
		
		if (nLanId!=SystemInfo.getCurrentLanguageId()) {
			nLanId=SystemInfo.getCurrentLanguageId();
			bReset=true;
		}

		// 刷新信息
		bClickDown = false;
		isFlick = false;
		hasFlick=false;
		if (info.getmStakeoutList()!=null) {
			for (int j = 0; j < info.getmStakeoutList().size(); j++) {
				if (info.getmStakeoutList().get(j).geteFlickType() != FLICK_TYPE.NO_FLICK) {
					hasFlick=true;
					break;
				} 
			}
		}

		// 控件是否受权限or地址控制
		itemIsShow();
		itemIsTouch();

		// 注册后台线程和相关地址
		registNotice();
	}
	
	/**
	 * 绘制控件
	 */
	private boolean bUserBitmap;
	private boolean bReset;
//	private void draw(Canvas canvas,boolean isBack){
//		if (info.getmTextList() == null || info.getmTextList().size() == 0) {
//			return;
//		}
//
//		if (bReset) {
//			bReset=false;
//			setState();
//		}

//		if (bUserBitmap) {
//
//			// 点击效果
//			if (isBack) {
//				if (bgBitmap == null) {
//					bgBitmap=getAlphaBitmap(mBitmap, nColor);
//					if(bgBitmap!=null){
//						bgRect = new Rect(0, 0, bgBitmap.getWidth(),
//								bgBitmap.getHeight());
//					}
//				}
//				if (bgBitmap != null) {
//					canvas.drawBitmap(bgBitmap, bgRect, rect, null);
//				}
//			}
//			
//			//控件图片
//			if (mBitmap != null) {
//				if (isBack) {
//					canvas.drawBitmap(mBitmap, null, dRect, mPaint);
//				} else {
//					canvas.drawBitmap(mBitmap, null, rect, mPaint);
//				}
//			}
//		}
		
//		textItem.draw(canvas);
		
//		if (!bUserBitmap) {
//			if (isBack) {
//				if (mCRectBitmap==null) {
//					mCRectBitmap=getRectFrame(2, item.rect);
//				}
//				if (mCRectBitmap!=null) {
//					canvas.drawBitmap(mCRectBitmap, info.getnLp(), info.getnTp(), mPaint);
//				}
//			}else {
//				if (mRRectBitmap==null) {
//					mRRectBitmap=getRectFrame(1, item.rect);
//				}
//				if (mRRectBitmap!=null&&model.getM_alphaPadding()>0) {
//					canvas.drawBitmap(mRRectBitmap, info.getnLp(), info.getnTp(), mPaint);
//				}
//			}
//		}
		
//		//不可触控加上锁图标
//		if (!touch && SystemInfo.isbLockIcon()) {
//			if (mLockBitmap == null) {
//				mLockBitmap = ImageFileTool.getBitmap(R.drawable.lock, mContext);
//			}
//			if (mLockBitmap != null) {
//				canvas.drawBitmap(mLockBitmap, info.getnLp(), info.getnTp(),null);
//			}
//		}
//	}
	
	
	private Paint mBitmapPaint;//图片专用
	public  void drawButton(Canvas canvas, int state, boolean filck,
			boolean isBack, boolean stateFilck) {

		if (!filck || stateFilck) {

			for (int i = 0; i < info.getmTextList().size(); i++) {
				TextInfo mTextInfo=info.getmTextList().get(i);
				if (mTextInfo.getnStatusId() == state) {
					if(mTextInfo.getmTextItem()==null||mTextInfo.getnLangugeId()!=SystemInfo.getCurrentLanguageId()){
						setState(state, mTextInfo);
						this.textItem=info.getmTextList().get(i).getmTextItem();
					}else {
						// 判断当前状态是否闪烁
						if (info.getmStakeoutList()!=null) {
							if (info.getmStakeoutList().size()>nCurrentState) {
								isFlick=info.getmStakeoutList().get(nCurrentState).isbFlick();
							}else {
								isFlick = false;
							}
						}else{
							isFlick = false;
						}
						
						StakeoutInfo sInfo = info.getmStakeoutList().get(i);
						if (sInfo.getnShapeType() == 4) {
							bUserBitmap = false;
						} else {
							bUserBitmap = true;
						}
						
						this.textItem=info.getmTextList().get(i).getmTextItem();
					}
					
					break;
				}
			}
		}
		
		if (mBitmapPaint==null) {
			mBitmapPaint=new Paint();
			mBitmapPaint.setAntiAlias(true);
			mBitmapPaint.setDither(true);
		}

		//使用图片
		if (bUserBitmap) {
			// 点击效果
			if (isBack) {
				Bitmap bitmap=mClickBgMap.get(state+"_"+info.getnWidth()+"_"+info.getnHeight());
				if (bitmap!=null) {
					Rect bgRect = new Rect(0, 0, bitmap.getWidth(),bitmap.getHeight());
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
				if (isBack) {
					canvas.drawBitmap(bitmap, null, dRect, mBitmapPaint);
				} else {
					canvas.drawBitmap(bitmap, null, rect, mBitmapPaint);
				}
			}
		}
		

		//state=-1,表示闪烁
		if (state > -1) {
			textItem.draw(canvas);
		}
		
		if (!bUserBitmap) {
			//不使用图片的
			if (isBack) {
				//点击效果
				if (mCRectBitmap==null) {
					mCRectBitmap=getRectFrame(2, item.rect);
				}
				if (mCRectBitmap!=null) {
					canvas.drawBitmap(mCRectBitmap, info.getnLp(), info.getnTp(), mBitmapPaint);
				}
			}else {
				//正常显示
				if (textItem.getmText().getM_alphaPadding()>0&&state>-1) {
					if (mRRectBitmap==null) {
						mRRectBitmap=getRectFrame(1, item.rect);
					}
					if (mRRectBitmap!=null) {
						canvas.drawBitmap(mRRectBitmap, info.getnLp(), info.getnTp(), mBitmapPaint);
					}
				}
			}
		}
		
		
		//不可触控加上锁图标
		if (!touch && SystemInfo.isbLockIcon()) {
			if (mLockBitmap==null) {
				mLockBitmap=ImageFileTool.getBitmap(R.drawable.lock,mContext);
			}
			if (mLockBitmap!=null) {
				canvas.drawBitmap(mLockBitmap, info.getnLp(), info.getnTp(), mBitmapPaint);
			}
		}
	}
	
	/**
	 * 注册显现和触控地址
	 */
	private void registNotice() {
		
		// 注册定时器
		if (hasFlick) {
				SKTimer.getInstance().getBinder().onRegister(callback, 5);
			}
		// 注册语言改变通知
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}
		
		// 通知画面管理,调用控件的绘制方法
		SKSceneManage.getInstance().onRefresh(item);
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
	 * 多语言
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback() {
		
		@Override
		public void onLanguageChange(int languageId) {
			isFlickState=true;//重新获取状态的值
			bReset = true;
			if (info!=null) {
				if (info.getmTextList()!=null) {
					if (info.getmTextList().size()>nCurrentState) {
						TextInfo mTextInfo=info.getmTextList().get(nCurrentState);
						if(mTextInfo.getmTextItem()==null||mTextInfo.getnLangugeId()!=SystemInfo.getCurrentLanguageId()){
							setState(nCurrentState, mTextInfo);
							textItem=info.getmTextList().get(nCurrentState).getmTextItem();
						}
					}
				}
			}
			SKSceneManage.getInstance().onRefresh(item);
		}
	};
	
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
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_CLICK_UP:
				if (bClickDown) {
					SKSceneManage.getInstance().onRefresh(item);
					bClickDown = false;
				}
				break;
			case HANDLER_PRESS:
				if (bClickDown) {
					doTouchEvent(true);
				}
				break;
			}
		}

	};
	
	/**
	 * @param type-1 凸
	 *        type-2 凹
	 */
	private Bitmap getRectFrame(int type,Rect rect){
		if (rect==null||rect.width()<6||rect.height()<6||mContext==null) {
			return null;
		}
		
		Bitmap mBitmap=null;
		String name="B"+type+"_"+rect.width()+"_"+rect.height();
		mBitmap=ImageFileTool.getBitmap(name);
		if (mBitmap!=null) {
			return mBitmap;
		}
		
		mBitmap=Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		Canvas canvas=new Canvas(mBitmap);
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		
		Rect hRect=new Rect();
		Rect vRect=new Rect();
		
		if (type==1) {
			
			/**
			 * 凸
			 */
			
			//左上
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_lt, mContext), 0, 0, paint);
			//左下
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_lb, mContext), 0, rect.height()-2, paint);
			//右上
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_rt, mContext), rect.width()-2, 0, paint);
			//右下
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_rb, mContext),  rect.width()-2, rect.height()-2, paint);
		
			/*水平*/
			//上
			hRect.left=2;
			hRect.right=rect.width()-2;
			hRect.top=0;
			hRect.bottom=2;
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_wt, mContext), 
					null, hRect, paint);
			
			//下
			hRect.top=rect.height()-2;
			hRect.bottom=rect.height();
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_gb, mContext), 
					null, hRect, paint);
			
			//垂直
			//左
			vRect.left=0;
			vRect.right=2;
			vRect.top=2;
			vRect.bottom=rect.height()-2;
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_wl, mContext), 
					null, vRect, paint);
			
			vRect.left=rect.width()-2;
			vRect.right=rect.width();
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.protrude_gr, mContext), 
					null, vRect, paint);
			
		}else if (type==2) {
			
			/**
			 * 凹
			 */
			
			//左上
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_lt, mContext), 0, 0, paint);
			//左下
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_lb, mContext), 0, rect.height()-2, paint);
			//右上
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_rt, mContext), rect.width()-2, 0, paint);
			//右下
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_rb, mContext),  rect.width()-2, rect.height()-2, paint);
		
			/*水平*/
			//上
			hRect.left=2;
			hRect.right=rect.width()-2;
			hRect.top=0;
			hRect.bottom=2;
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_gt, mContext), 
					null, hRect, paint);
			
			//下
			hRect.top=rect.height()-2;
			hRect.bottom=rect.height();
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_wb, mContext), 
					null, hRect, paint);
			
			//垂直
			//左
			vRect.left=0;
			vRect.right=2;
			vRect.top=2;
			vRect.bottom=rect.height()-2;
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_gl, mContext), 
					null, vRect, paint);
			
			vRect.left=rect.width()-2;
			vRect.right=rect.width();
			canvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.concave_wr, mContext), 
					null, vRect, paint);
			
			
		}
		
		ImageFileTool.setBitmap(mBitmap, name);
		
		return mBitmap;
	}
	
	//提取图像Alpha位图  
    public static Bitmap getAlphaBitmap(Bitmap mBitmap,int mColor) {  
//      BitmapDrawable mBitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.enemy_infantry_ninja);  
//      Bitmap mBitmap = mBitmapDrawable.getBitmap();  
          
        //BitmapDrawable的getIntrinsicWidth（）方法，Bitmap的getWidth（）方法  
        //注意这两个方法的区别  
        //Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmapDrawable.getIntrinsicWidth(), mBitmapDrawable.getIntrinsicHeight(), Config.ARGB_8888);  
       
    	if (mBitmap==null) {
			return null;
		}
    	Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Config.ARGB_8888);  
          
        Canvas mCanvas = new Canvas(mAlphaBitmap);  
        Paint mPaint = new Paint();  
          
        mPaint.setColor(mColor);  
        //从原位图中提取只包含alpha的位图  
        Bitmap alphaBitmap = mBitmap.extractAlpha();  
        //在画布上（mAlphaBitmap）绘制alpha位图  
        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);  
          
        return mAlphaBitmap;  
    } 
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
					value=mIData.get(0);
					if (result) {
						value = (long)value & (1 << (info.getnBitIndex()-1));
						if (value ==0) {
							value = 0;
						} else {
							value = 1;
						}
					}else {
						return;
					}
				}
			} else if (info.geteWatchType() == WATCH_TYPE.POLY) {
				// 多态监视
				value  = change(nStatusValue);
			}
			
		    //Log.d(TAG, "id:"+nButtonId+",value:"+value);
			watch(value);
		}

	};
	
	/**
	 * 监视地址值发生变化
	 */
	private Vector<Integer> mIDataList=null;
	private Vector<Short> mSDataList=null;
	private Vector<Long> mLDataList=null;
	private Vector<Float> mFDataList=null;
	private double change(Vector<Byte> nStatusValue){
		
		double value=0;
		if (info.geteWatchDataType()==null) {
			return 0;
		}
		boolean result=false;
		switch (info.geteWatchDataType()) {
		case INT_16:
			//16位整数
			if (mSDataList==null) {
				mSDataList=new Vector<Short>();
			}else {
				mSDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSDataList);
			if (result) {
				if (mSDataList==null||mSDataList.size()==0) {
					return 0;
				}
				value=mSDataList.get(0);
			}
			break;
		case POSITIVE_INT_16:
			//16正整数
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				if(mIDataList==null||mIDataList.size()==0){
					return 0;
				}
				value=mIDataList.get(0);
			}
			break;
		case INT_32:
			//32整数
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToInts(nStatusValue, mIDataList);
			if (result) {
				if (mIDataList==null||mIDataList.size()==0) {
					return 0;
				}
				value=mIDataList.get(0);
			}
			break;
		case POSITIVE_INT_32: 
			// 32位正整数
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				if (mLDataList==null||mLDataList.size()==0) {
					return 0;
				}
				value=mLDataList.get(0);
			}
			break;
		case BCD_16:
			// 调用BCD码转换
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				if (mIDataList.size()>0) {
					int nV=0;
					String s=DataTypeFormat.intToBcdStr((long) mIDataList.get(0), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nV=Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					value=nV;
				}
			}
			break;
		case BCD_32:
			// 调用BCD码转换
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				if (mLDataList.size()>0) {
					int nV=0;
					String s=DataTypeFormat.intToBcdStr((long) mLDataList.get(0), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nV=Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					value=nV;
				}
			}
			break;
		case FLOAT_32: 
			// 浮点数
			if (mFDataList==null) {
				mFDataList=new Vector<Float>();
			}else {
				mFDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToFloats(nStatusValue, mFDataList);
			if (result) {
				if (mFDataList==null||mFDataList.size()==0) {
					return 0;
				}
				if (mFDataList.size()>0) {
					value=mFDataList.get(0);
				}
			}
			break;
		}
		return value;
	}
	
	/**
	 * 监视
	 */
	private int nCurrentState; // 状态Id
	private int nFlickState; // 闪烁状态id
	private boolean isFlick; // 是否闪烁
	private void watch(double value) {
		
		if (info != null) {
			ArrayList<StakeoutInfo> list = info.getmStakeoutList();
			if (list != null) {
				boolean reuslt=false;//是否匹配到状态
				for (int i = 0; i < list.size(); i++) {
					if (info.geteWatchType() == WATCH_TYPE.DOUBLE) {
						//Log.d(TAG, "value="+value+",nCurrentState="+nCurrentState+",id="+nButtonId);
						if (value==list.get(i).getnStatusId()&&
								list.get(i).getnStatusId() != nCurrentState) {
							// 状态改变通知UI刷新
							nCurrentState = list.get(i).getnStatusId();
							nFlickState = nCurrentState;
							isFlick = false;
							SKSceneManage.getInstance().onRefresh(item);
							break;
						}
					}else if (info.geteWatchType() == WATCH_TYPE.POLY) {
						if (info.getnCondition()==1) {
							//按照寄存器的位切换
							long v=(long)value;
							long temp = v & (1 << i);
							if (temp>0) {
								reuslt=true;
								if (list.get(i).getnStatusId() != nCurrentState) {
									// 状态改变通知UI刷新
									nCurrentState = list.get(i).getnStatusId();
									nFlickState = nCurrentState;
									isFlick = false;
									SKSceneManage.getInstance().onRefresh(item);
								}
								break;
							}
							
						}else {
							if (value == list.get(i).getnCmpFactor()) {
								reuslt=true;
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
					//如果多状态没有匹配到，则显示最后一个状态
					if (info.geteWatchType() == WATCH_TYPE.POLY) {
						int index=list.size()-1;
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
	
	//初始化开关状态
	private void initState(){
		if (info.getmTextList()!=null) {
			if (info.getmTextList().size()>0&&info.getmTextList().size()>nCurrentState) {
				setState(nCurrentState,info.getmTextList().get(nCurrentState));
			}
		}
	}
	
	/**
	 * 状态设置
	 * @param i-状态
	 * @param mTextInfo-状态
	 */
	
	private void setState(int i,TextInfo mTextInfo){
		mTextInfo.setnLangugeId((short)SystemInfo.getCurrentLanguageId());
		StaticTextModel model=new StaticTextModel();
		TextItem mTextItem=new TextItem(model);
		
		TextInfo tInfo = info.getmTextList().get(i);
		StakeoutInfo sInfo = info.getmStakeoutList().get(i);

		if (sInfo.getnShapeType() == 4) {
			bUserBitmap = false;
		} else {
			bUserBitmap = true;
		}

		if (bUserBitmap) {
			//使用图片
			if (!mPicMap.containsKey(String.valueOf(i))) {
				Bitmap temp = ImageFileTool.getBitmap(sInfo
						.getsPath());
				if (temp != null) {
					//状态图片
					mPicMap.put(String.valueOf(i), temp);
					//状态点击效果
					Bitmap bg=getAlphaBitmap(temp, nColor);
					if (bg!=null) {
						mClickBgMap.put(i+"_"+info.getnWidth()+"_"+info.getnHeight(), bg);
					}
				}
			}
			model.setLineWidth(0);
			model.setM_backColorPadding(Color.TRANSPARENT);
			model.setM_alphaPadding(0);
		} else {
			//不使用图片
			model.setM_backColorPadding(sInfo.getnColor());
			model.setLineWidth(0);
			model.setLineColor(Color.BLACK);
			model.setM_alphaPadding(sInfo.getnAlpha());
		}

		// 位置
		if (tInfo.getmStyle().size() > SystemInfo
				.getCurrentLanguageId()) {
			int style = tInfo.getmStyle().get(
					SystemInfo.getCurrentLanguageId());
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
		if (tInfo.getmTextList().size() > SystemInfo
				.getCurrentLanguageId()) {
			text = tInfo.getmTextList().get(
					SystemInfo.getCurrentLanguageId());
			if (text == null) {
				text = "";
			}
		}
		
		model.setM_sTextStr(text);

		// 颜色
		if (tInfo.getmColors().size() > SystemInfo
				.getCurrentLanguageId()) {
			model.setM_nFontColor(tInfo.getmColors().get(
					SystemInfo.getCurrentLanguageId()));
		} else {
			model.setM_nFontColor(Color.BLACK);
		}

		// 字体大小
		if (tInfo.getmSize().size() > SystemInfo
				.getCurrentLanguageId()) {
			int size = tInfo.getmSize().get(
					SystemInfo.getCurrentLanguageId());
			model.setM_nFontSize(size);
		} else {
			model.setM_nFontSize(10);
		}

		// 字体类型
		if (tInfo.getmFonts().size() > SystemInfo
				.getCurrentLanguageId()) {
			model.setM_sFontFamly(tInfo.getmFonts().get(
					SystemInfo.getCurrentLanguageId()));
		} else {
			model.setM_sFontFamly("");
		}

		// 是否闪烁
		if (info.getmStakeoutList().size() >= i) {
			if (info.getmStakeoutList().get(i).geteFlickType() == FLICK_TYPE.NO_FLICK) {
				info.getmStakeoutList().get(i).setbFlick(false);
				isFlick = false; // 不闪烁
			} else {
				isFlick = true; // 闪烁
				info.getmStakeoutList().get(i).setbFlick(true);
			}
		}

		if (!bUserBitmap) {
			model.setStartX(info.getnLp()+1);
			model.setStartY(info.getnTp()+1);
			model.setRectWidth(info.getnWidth()-2);
			model.setRectHeight(info.getnHeight()-2);
		}else {
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
						if(textItem!=null){
							StaticTextModel model=textItem.getmText();
							if (model!=null) {
								model.setM_sTextStr("");
							}
						}
						isChange = false;
					} else {
						String text = "";
						StaticTextModel model=null;
						if (info.getmTextList().get(i).getmTextList()
								.size() > SystemInfo.getCurrentLanguageId()) {
							if (info.getmTextList().get(i).getmTextItem()!=null) {
								model=info.getmTextList().get(i).getmTextItem().getmText();
								if (model!=null) {
									text = info.getmTextList().get(i)
											.getmTextList()
											.get(SystemInfo.getCurrentLanguageId());
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
			return info.getnLp();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnTp();
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
			return getColor(info.getmTextList().get(nCurrentState).getnBColor());
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
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnLp((short)x);
			int l=item.rect.left;
			item.rect.left=x;
			item.rect.right=x-l+item.rect.right;
			item.mMoveRect=new Rect();
			
			dRect.left=item.rect.left+nPadding;
			dRect.right=item.rect.right-nPadding;
			
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
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnTp((short)y);
			int t = item.rect.top;
			item.rect.top = y;
			item.rect.bottom = y - t + item.rect.bottom;
			item.mMoveRect=new Rect();
			
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
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnWidth((short)w);
			item.rect.right = w - item.rect.width() + item.rect.right;
			item.mMoveRect=new Rect();
			
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
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnHeight((short)h);
			item.rect.bottom = h - item.rect.height() + item.rect.bottom;
			item.mMoveRect=new Rect();
			
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
		if (v==show) {
			return true;
		}
		show=v;
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
	 * 颜色取反
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
	
	private boolean doTouchEvent(boolean invalidJoy){
		for (int i = 0; i < info.getmSkButtons().size(); i++) {
			SKButton button=info.getmSkButtons().get(i);
			button.doTouch(true,true,MotionEvent.ACTION_DOWN);
			if(invalidJoy)
			{
				invalidJoy = button.getJoyEvent();
			}
			if (button.info.geteButtonType()==BUTTON_TYPE.SCENE) {
				//画面之后所有功能不执行
				break;
			}
		}
		
		if (info.getmTouchInfo()!=null) {
			//操作通知
			noticeAddr(info.getmTouchInfo(), true);
		}
		
		//若启用宏指令
		if(true == info.isbIsStartStatement()){
			//请求执行控件宏指令
			MacroManager.getInstance(null).Request(MSERV.CALLCM, (short)info.getnStatementId());
		}
		
		return invalidJoy;
	}
}
