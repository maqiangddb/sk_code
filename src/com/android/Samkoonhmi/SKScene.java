package com.android.Samkoonhmi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.android.Samkoonhmi.model.LockInfo;
import com.android.Samkoonhmi.model.PassWordInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skcommon.SkCommon;
import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.SYSTEM_OPER_TYPE;
import com.android.Samkoonhmi.skgraphics.SKGraphics;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.ISceneDestory;
import com.android.Samkoonhmi.skwindow.SKSceneManage.ItemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.skwindow.SKWindowManage;
import com.android.Samkoonhmi.skwindow.SKWindowManage.ITitleListener;
import com.android.Samkoonhmi.system.SystemControl;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.SystemParam;

public class SKScene extends SurfaceView implements Callback {

	private static String TAG = "SKScene";
	private DrawThread mDrawThread;// 刷新线程
	private SurfaceHolder sfh;
	protected Canvas canvas;
	protected Paint paint;
	protected Canvas mCanvas;
	public Bitmap mBitmap; // 保证两个buffer 内容一致
	private Bitmap mTitleBitmap; // 标题
	private Bitmap mBackBitmap;
	// 控件集合
	private List<ItemInfo> mSkGraphicsList;
	// 画面信息
	public ScenceInfo mInfo;
	private Context mContext;
	private boolean SamkoonHmi;
	public int nSceneWidth;
	public int nSceneHeight;
	// 存储更新控件
	private ArrayList<SKItems> mItems;
	// 存储除移动控件外的所有控件
	private HashMap<Integer, Vector<SKItems>> mAllItems;
	// 由于HashMap在遍历时，不能添加和删除，所以需要添加一个全部，可以遍历的数组
	private Vector<SKItems> mAllItem;
	private int nItemCount;
	private int nAllCount;
	private Vibrator vibrator;
	private Bitmap mIconBitmap;
	private Bitmap mCloseBitmap;
	private String sTitleName = "";
	private int nTitleHeight = 0;
	private static final int nPadding = 10;
	private ITitleListener listener;
	private ISceneDestory iSceneDestory;
	private boolean hasMoveItem;// 是否有移动控件
	private FillRender fillRender;
	private Shader myShader;
	private Paint mClearPaint;
	private boolean clearBitmap = true;
	private boolean bTouch;// 防止多次滑动切换画面点击到下一个画面的控件，等待100毫秒
	

	public SKScene(Context context, Activity activity, ScenceInfo info) {
		super(context);
		this.setKeepScreenOn(true);
		this.mInfo = info;
		//nSceneId = mInfo.getnSceneId();
		mContext = context;
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		mClearPaint = new Paint();
		mClearPaint.setAntiAlias(true);
		this.setLongClickable(true);
		nSceneWidth = info.getnSceneWidth();
		nSceneHeight = info.getnSceneHeight();
		if (nSceneWidth == 0) {
			nSceneWidth = 800;
		}
		if (nSceneHeight == 0) {
			nSceneHeight = 480;
		}
		nTitleHeight = SKWindowManage.nTitleHeight;
		if (nTitleHeight == 0) {
			nTitleHeight = 30;
		}
		if (mInfo.isbShowTitle()) {
			nSceneHeight += nTitleHeight;
		}

		mItems = new ArrayList<SKItems>();
		mAllItems = new HashMap<Integer, Vector<SKItems>>();
		mAllItem = new Vector<SKItems>();
		nItemCount = 0;
		nAllCount = 0;
		vibrator = (Vibrator) mContext
				.getSystemService(Service.VIBRATOR_SERVICE);
	}

	/**
	 * 画场景画面背景
	 */
	private Rect rect = null;
	private void drawBackGround(Canvas canvas) {
		
		if (mInfo != null) {                                                                                                                                                                                     
			if (rect == null) {
				if (mInfo.geteType() == SHOW_TYPE.FLOATING) {
					if (mInfo.isbShowTitle()) {
						rect = new Rect(0, nTitleHeight, mInfo.getnSceneWidth(),
								mInfo.getnSceneHeight() + nTitleHeight);
					}else {
						rect = new Rect(0, 0, mInfo.getnSceneWidth(),
								mInfo.getnSceneHeight());
					}
				} else {
					rect = new Rect(0, 0, mInfo.getnSceneWidth(),
							mInfo.getnSceneHeight());
				}
			}
			
			if (mInfo.geteBackType() == BACKCSS.BACK_IMG) {
				if (mBackBitmap == null) {
					mBackBitmap = ImageFileTool.getBitmap(mInfo
							.getsPicturePath());
				}
				if (mBackBitmap != null) {
					canvas.drawBitmap(mBackBitmap, null, rect, paint);
				} else {
					// 图片不存在
					mInfo.seteBackType(BACKCSS.BACK_CSS);
					mInfo.seteDrawStyle(CSS_TYPE.CSS_SOLIDCOLOR);
					mInfo.setnBackColor(Color.rgb(180, 180, 180));
					canvas.drawColor(mInfo.getnBackColor());
				}
			} else {
				if (mInfo.geteDrawStyle() == CSS_TYPE.CSS_TRANSPARENCE
						|| mInfo.geteDrawStyle() == CSS_TYPE.CSS_SOLIDCOLOR) {
					canvas.drawColor(mInfo.getnBackColor());
				} else {
					if (fillRender == null) {
						fillRender = new FillRender();
					}
					if (myShader == null) {
						myShader = fillRender.setRectCss(mInfo.geteDrawStyle(),
								0, 0, mInfo.getnSceneWidth(),
								mInfo.getnSceneHeight(), mInfo.getnForeColor(),
								mInfo.getnBackColor());
					}
					
					paint.setShader(myShader);
					paint.setStyle(Style.FILL);
					canvas.drawRect(rect, paint);
				}
			}
			
			if (mInfo.geteType() == SHOW_TYPE.FLOATING) {
				// 窗口
				if(mInfo.geteBackType() != BACKCSS.BACK_IMG){
					paint.setColor(Color.BLACK);
					paint.setStyle(Style.STROKE);
					if (!mInfo.isbShowTitle()) {
						canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
					}
					canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
					canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
					canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
				}
			}
			
			//绘制title
			if (mInfo.isbShowTitle()) {
				canvas.drawBitmap(mTitleBitmap, 0, 0, paint);
			}
			
			//绘制控件
			if (null != mBitmap) {
				int topY = mInfo.isbShowTitle()?nTitleHeight:0;
				canvas.drawBitmap(mBitmap, 0, topY, paint);
			}
		}
	}

	/**
	 * 画标题
	 */
	public void drawTitle() {
		mTitleBitmap = ImageFileTool.getTitleBgBitmap(nSceneWidth, nSceneHeight, mContext);
		if (mTitleBitmap == null) {
			return;
		}

		if (mIconBitmap == null) {
			mIconBitmap  = ImageFileTool.getBitmap(R.drawable.samkoon, mContext);
		}
		if (mCloseBitmap == null) {
			mCloseBitmap = ImageFileTool.getBitmap(R.drawable.title_close, mContext);
		}

		
		Canvas tCanvas = new Canvas(mTitleBitmap);
		tCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setDither(true);

		int height = getFontHeight(textPaint);
		if (sTitleName != null) {
			textPaint.setStyle(Style.STROKE);
			textPaint.setStrokeWidth(0);
			textPaint.setStrokeJoin(Join.ROUND);
			textPaint.setTextSize(14);
			textPaint.setColor(Color.BLACK);
			tCanvas.drawText(sTitleName, 2 * nPadding + mIconBitmap.getWidth(),
					nTitleHeight / 2 + height / 4, textPaint);
		}
		
		Bitmap tBitmap = ImageFileTool.getBitmap(R.drawable.title_row_bg,
				mContext);
		if (tBitmap != null) {
			tCanvas.drawBitmap(tBitmap, null, new Rect(0, 0, nSceneWidth, nTitleHeight), paint);
		}
		
		if (mInfo.isbShowTitle()) {
			tCanvas.drawBitmap(mCloseBitmap, mInfo.getnSceneWidth()
					- mCloseBitmap.getWidth() - nPadding,
					(nTitleHeight - mCloseBitmap.getHeight()) / 2, paint);
		}

	}

	public void setTitleName(String name) {
		sTitleName = name;
	}

	public void setTitleButton(boolean show) {
		// bShowBtn = show;
	}

	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	/**
	 * 初始化控件
	 */
	public void drawGraphics() {
		if (mSkGraphicsList != null) {
			nAllCount = mSkGraphicsList.size();
			
			for (int i = 0; i < nAllCount; i++) {
				ItemInfo iInfo = mSkGraphicsList.get(i);
				SKGraphics sk = iInfo.mItem;
				// 初始化之前先清空数据
				sk.realseMemeory();
				// 初始化
				sk.initGraphics();
			}
		}
	}

	/**
	 * 切换用户 更新控件显现和触控
	 */
	public void updateView() {
		//long start=System.currentTimeMillis();
		if (mSkGraphicsList != null) {
			nAllCount = mSkGraphicsList.size();
			for (int i = 0; i < nAllCount; i++) {
				ItemInfo iInfo = mSkGraphicsList.get(i);
				SKGraphics sk = iInfo.mItem;
				sk.isShow();
				sk.isTouch();
			}
		}
		//Log.d(TAG, "....time:"+(System.currentTimeMillis()-start));
	}

	/**
	 * 触摸事件
	 */
	private boolean isMove;
	private boolean isUp = false;
	private boolean isTouch;
	public boolean onTouchEvent(MotionEvent event) {
		
		SKSceneManage.getInstance().time=0;
		if (!bTouch) {
			return false;
		}
		float X = event.getX();
		float Y = event.getY();
		SystemInfo.setCurrentTouchX((int) X);
		SystemInfo.setCurrentTouchY((int) Y);
		if (mInfo.isbShowTitle()) {
			if (Y < SKWindowManage.nTitleHeight) {
				// 点击标题关闭按钮
				if (mCloseBitmap != null) {
					if (X > mInfo.getnSceneWidth() - mCloseBitmap.getWidth()
							- nPadding) {
						if (listener != null) {
							// 关闭窗口
							listener.onClose();
						}
					}
				}
			} else {
				// 如果是窗口,并且有标题的,加上标题的高;
				event.setLocation(X, Y - SKWindowManage.nTitleHeight);
			}
		}

		isTouch = false;
		isMove = false;

		boolean isTouchSound = (SystemParam.USE_TOUCH_SOUND & SystemInfo
				.getnSetBoolParam()) == SystemParam.USE_TOUCH_SOUND;// 为true
																	// 开启触摸声音
		// 蜂鸣声
		// vibrator.vibrate(new long[]{1000,50,1000,50}, 0 );
		// System.out.println("vibrator");

		// 如果背光关闭，点击屏幕时打开背光
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			SKSceneManage.getInstance().backLightOn();
		}
		
		if (mSkGraphicsList != null) {
			for (int i = 0; i < mSkGraphicsList.size(); i++) {
				ItemInfo iInfo = mSkGraphicsList.get(i);
				SKGraphics sk = iInfo.mItem;
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					if (sk.onTouchEvent(event)) {
						isMove = true;
					}
					break;
				default:
					isTouch = sk.onTouchEvent(event);
					break;
				}
				// 点击中了控件
				if (isTouch) {
					if (event.getAction() == MotionEvent.ACTION_DOWN
							&& isTouchSound) {
						vibrator.vibrate(150/* new long[]{1000,50,1000,50}, 0 */);
					}
				}
			}
		}

		if (mInfo.geteType() == SHOW_TYPE.DEFAULT) {
			// 只有处于场景才可用切换，处于窗口不可以切换
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				SKSceneManage.getInstance().onTouchEvent(event);
				break;
			case MotionEvent.ACTION_MOVE:
				if (!isMove) {
					// view 里面没有控件滑动，则分配给scene
					SKSceneManage.getInstance().onTouchEvent(event);
					isUp = true;
				} else {
					// view 有控件滑动,则更新起始位置
					isUp = false;
					SKSceneManage.getInstance().updateXY(X, Y);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (isUp) {
					if (event.getAction() == MotionEvent.ACTION_UP
							|| event.getAction() == MotionEvent.ACTION_CANCEL) {
						isUp = false;
						SKSceneManage.getInstance().onTouchEvent(event);
					}
				}
				break;
			}
		}

		return true;
	}

	/**
	 * 控件初始化
	 */
	private void init() {
		hasMoveItem = false;
		drawGraphics();
		draw();
	}

	/**
	 * 初始化刷新，
	 */
	private void draw() {
		//登录成功判断是否锁屏
		if(LockInfo.GetbIsLock()){
			PassWordInfo msg=new PassWordInfo();
			msg.setsPwdStr(LockInfo.GetPassWord());
			msg.setsTimeOut(LockInfo.GetInfo());
			SystemControl.peculiarOper(SYSTEM_OPER_TYPE.SYSTEM_LOCK, msg);
		}
		canvas = sfh.lockCanvas();
		if (canvas != null) {
			drawBackGround(canvas);
			sfh.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 更新特定控件
	 */
	private Xfermode xMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private void draw(ArrayList<SKItems> items) {

		if (mCanvas == null) {
			int type=0;
			if (mInfo.geteType()==SHOW_TYPE.FLOATING) {
				type=1;
			}
			mBitmap = ImageFileTool.getBitmap(nSceneWidth, nSceneHeight,
					type,mContext);
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(nSceneWidth, nSceneHeight,
						Config.ARGB_8888);
			}
			mCanvas = new Canvas(mBitmap);
		}

		if (mCanvas != null) {
			if (clearBitmap) {
				// 由于相同尺寸的画面使用同一张画布，所以第一次绘制画面要清空之前内容
				mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				clearBitmap = false;
			}

			ArrayList<SKItems> list = doInfo(0, items);			
			if (hasMoveItem) {//先清除
				mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			}else {
				for (int i = 0; i < list.size(); i++) {
					
					SKItems item = list.get(i);
					mCanvas.drawRect(item.rect, mClearPaint);
				}
			}
			
			// 重绘
			for (int i = 0; i < list.size(); i++) {
				SKItems item = list.get(i);
				SKGraphics sk = item.mGraphics;
				if (sk!=null) {
					sk.drawGraphics(mCanvas, item.itemId);
				}
			}
			
			items.clear();
			mItemList.clear();

			if (sfh != null) {
				canvas = sfh.lockCanvas();
				if (canvas != null) {
					//进行重绘背景以及控件
					drawBackGround(canvas);
					sfh.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	


	/**
	 * 设置画面大小
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(
				MeasureSpec.makeMeasureSpec(nSceneWidth, MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(nSceneHeight, MeasureSpec.AT_MOST));
	}

	/**
	 * 刷新线程
	 */
	private int nSleepTime=100;
	class DrawThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (SamkoonHmi) {
				
				long start=System.currentTimeMillis();
				if (oneRefresh || twoRefresh) {
					addItem();
					draw(mItems);
					if (index == 1) {
						oneRefresh = false;
					} else {
						twoRefresh = false;
					}
					
				}
				
				//刷新所耗的时间
				int time=(int)(System.currentTimeMillis()-start);
				nSleepTime=100-time;
				if (nSleepTime<5) {
					nSleepTime=5;
				}else if (nSleepTime>100) {
					nSleepTime=100;
				}
				
				//Log.d(TAG, "time:"+time+",nSleepTime:"+nSleepTime);
				try {
					sleep(nSleepTime);
					/**
					 * 防止用户滑动在第一个画面拼命滑动 这个时候画面已经进入画面二了，造成误点击到画面二的控件
					 */
					bTouch = true;

				} catch (InterruptedException e) {
					SamkoonHmi = false;
					e.printStackTrace();
					Log.e(TAG, "draw thread error!");
				}
			}
		}

	}

	/**
	 * 添加要刷新的控件
	 */
	private void addItem() {
		if (index == 0) {
			mItems = mOne;
			index = 1;
		} else {
			mItems = mTwo;
			index = 0;
		}
	}

	/**
	 * 用于刷新 一个存当前刷新控件，一个存要刷新控件 index 用来切换
	 * 必须用同步，不然会有些漏掉
	 */
	private boolean oneRefresh = true;// 表示容器一是否要刷新
	private boolean twoRefresh = false;// 表示容器二是否要刷新
	private int index = 0;
	private ArrayList<SKItems> mOne = new ArrayList<SKItems>();
	private ArrayList<SKItems> mTwo = new ArrayList<SKItems>();
	private synchronized void updateItem(SKItems sMsg) {

		// 画面上有移动控件
		if (sMsg.mMoveRect != null) {
			hasMoveItem = true;
		}

		// 添加需要更新的控件
		if (index == 0) {
			oneRefresh = true;
			if (!hasMoveItem) {
				if (!indexOf(mOne, sMsg.nCollidindId)) {
					mOne.add(sMsg);
				}
			}
		} else {
			twoRefresh = true;
			if (!hasMoveItem) {
				if (!indexOf(mTwo, sMsg.nCollidindId)) {
					mTwo.add(sMsg);
				}
			}
		}

		// 添加所有控件信息,按组合分组
		if (nItemCount < nAllCount) {
			if (mAllItems.containsKey(sMsg.nCollidindId)) {
				// 组合存在
				Vector<SKItems> msgs = mAllItems.get(sMsg.nCollidindId);
				if (msgs!=null) {
					if (!isRepeat(msgs, sMsg.itemId)) {
						// 控件不存在
						SKItems skMsg = new SKItems();
						skMsg.itemId = sMsg.itemId;
						skMsg.nZvalue = sMsg.nZvalue;
						skMsg.nCollidindId = sMsg.nCollidindId;
						skMsg.rect = sMsg.rect;
						skMsg.mGraphics=sMsg.mGraphics;
						msgs.add(skMsg);
						mAllItem.add(skMsg);
						nItemCount++;
					}
				}
			} else {
				// 组合不存在
				Vector<SKItems> msgs = new Vector<SKItems>();
				SKItems skMsg = new SKItems();
				skMsg.itemId = sMsg.itemId;
				skMsg.nZvalue = sMsg.nZvalue;
				skMsg.nCollidindId = sMsg.nCollidindId;
				skMsg.rect = sMsg.rect;
				skMsg.mGraphics=sMsg.mGraphics;
				msgs.add(skMsg);
				mAllItem.add(skMsg);
				mAllItems.put(sMsg.nCollidindId, msgs);
				nItemCount++;
			}
		}
	}

	/**
	 * 控件刷新
	 */
	public void onRefresh(SKItems item) {
		if(item==null||mInfo==null){
			return;
		}
		if (item.sceneId!=mInfo.getnSceneId()) {
			return;
		}
		updateItem(item);
	}

	/**
	 * 排序
	 */
	private SkComparator comparator = new SkComparator();
	private void sort(ArrayList<SKItems> list) {
		Collections.sort(list, comparator);
	}

	/**
	 * 集合排序方法
	 */
	private final class SkComparator implements Comparator<Object> {

		@Override
		public int compare(Object lhs, Object rhs) {
			SKItems obj1 = (SKItems) lhs;
			SKItems obj2 = (SKItems) rhs;
			return obj1.nZvalue - obj2.nZvalue;
		}
	}

	// 需要更新的控件，加上需要更新的控件所关联的控件
	ArrayList<SKItems> mItemList = new ArrayList<SKItems>();
	private ArrayList<SKItems> getUpdateItems(ArrayList<SKItems> items) {

		//long start=System.currentTimeMillis();
		mItemList.clear();
		if (hasMoveItem) {
			/**
			 * 如果有移动控件，全屏刷新
			 */
			for (int i = 0; i < mAllItem.size(); i++) {
				SKItems msg = mAllItem.get(i);
				mItemList.add(msg);
			}
		} else {
			/**
			 * 画面上没有移动的控件，局部刷新
			 */
			for (int k = 0; k < items.size(); k++) {
				
				SKItems item= items.get(k);
				if (mAllItems.containsKey(item.nCollidindId)) {
					Vector<SKItems> itemList = mAllItems
							.get(item.nCollidindId);
					if (itemList!=null) {
						for (int i = 0; i < itemList.size(); i++) {
							SKItems msg = itemList.get(i);
							mItemList.add(msg);
						}
					}
				}		

			}
		}

		sort(mItemList);
		//Log.d(TAG, "--time:"+(System.currentTimeMillis()-start));

		return mItemList;
	}

	/**
	 * 判断添加控件是否已经添加
	 */
	private boolean isRepeat(List<SKItems> list, int itemId) {
		if (list==null) {
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			SKItems msg = (SKItems) list.get(i);
			if (msg.itemId == itemId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断组合是否存在
	 */
	private boolean indexOf(ArrayList<SKItems> items,int nCollidindId){
		if (items==null) {
			return false;
		}
		for (int i = 0; i < items.size(); i++) {
			SKItems item=items.get(i);
			if (item.nCollidindId==nCollidindId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		SamkoonHmi = true;
		mDrawThread = new DrawThread();
		mDrawThread.setName("skscene_thread");
		mDrawThread.start();
		clearBitmap = true;
		bTouch = false;
		index=0;
		
		mClearPaint.setStyle(Style.FILL);
		mClearPaint.setAntiAlias(true);
		mClearPaint.setXfermode(xMode);
		mClearPaint.setColor(Color.TRANSPARENT);
		
		init();
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (SamkoonHmi) {
			clearData();
		}
		SamkoonHmi = false;
		if (iSceneDestory != null) {
			iSceneDestory.destory(true);
		}

		//doInfo(1, null);

		if (sfh != null) {
			sfh.getSurface().release();
		}
	}

	/**
	 * 集合处理
	 * @param type=0 获取 type=1 清除
	 */
	private synchronized ArrayList<SKItems> doInfo(int type,
			ArrayList<SKItems> items) {
		ArrayList<SKItems> list = null;
		if (type == 0) {
			list = getUpdateItems(items);
		} else if (type == 1) {
			mItems.clear();
			mAllItems.clear();
			mAllItem.clear();
			mItemList.clear();
		} 
		return list;
	}

	/**
	 * 清空数据
	 */
	public void clearData() {
		SamkoonHmi = false;
		if (mSkGraphicsList != null) {
			for (int i = 0; i < mSkGraphicsList.size(); i++) {
				ItemInfo iInfo = mSkGraphicsList.get(i);
				SKGraphics sk = iInfo.mItem;
				sk.realseMemeory();
			}
		}
	}

	public void setmSkGraphicsList(List<ItemInfo> mSkGraphicsList) {
		this.mSkGraphicsList = mSkGraphicsList;
	}

	public ITitleListener getListener() {
		return listener;
	}

	public void setListener(ITitleListener listener) {
		this.listener = listener;
	}

	public void setiSceneDestory(ISceneDestory iSceneDestory) {
		this.iSceneDestory = iSceneDestory;
	}
}
