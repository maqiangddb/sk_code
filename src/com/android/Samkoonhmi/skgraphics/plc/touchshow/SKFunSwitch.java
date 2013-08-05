package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.Vector;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.skbutton.FunSwitchInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TextAttribute;

/**
 * 多功能开关
 */
public class SKFunSwitch extends SKGraphCmnTouch{
	
	private static final int nPadding = 2;
	private static final int HANDLER_CLICK_UP = 0;
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
			
			setState();
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
				draw(canvas,bClickDown);
				return true;
			}
		}
		return false;
	}

	@Override
	public void realseMemeory() {
		isClick=false;
		bClickDown=false;
		SKLanguage.getInstance().getBinder().onDestroy(lCallback);
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);
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
						button.doTouch(false,true);
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
					if (!isClick) {
						isClick=true;
						bClickDown=true;
						SKSceneManage.getInstance().onRefresh(item);
						
						for (int i = 0; i < info.getmSkButtons().size(); i++) {
							SKButton button=info.getmSkButtons().get(i);
							button.doTouch(true,true);
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
					}
				}else if(event.getAction()==MotionEvent.ACTION_CANCEL||
						event.getAction()==MotionEvent.ACTION_UP) {
					
					for (int i = 0; i < info.getmSkButtons().size(); i++) {
						SKButton button=info.getmSkButtons().get(i);
						button.doTouch(false,true);
					}
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
		this.show = true;
		this.touch = true;
		this.showByAddr = false;
		this.touchByAddr = false;
		this.showByUser = false;
		this.touchByUser = false;
		if (nLanId!=SystemInfo.getCurrentLanguageId()) {
			nLanId=SystemInfo.getCurrentLanguageId();
			bReset=true;
		}

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

		// 刷新信息
		bClickDown = false;

		// 控件是否受权限or地址控制
		itemIsShow();
		itemIsTouch();

		// 注册后台线程和相关地址
		registNotice();
	}
	
	private void setState(){
		TextInfo tInfo = info.getmTextList().get(0);// 多功能开关只有一个状态
		
		if (info.getnShapeType()==4) {
			bUserBitmap=false;
			model.setM_backColorPadding(info.getnColor());
			model.setLineColor(Color.BLACK);
			model.setM_alphaPadding(info.getnAlpha());
			model.setBorderAlpha(info.getnAlpha());
		}else {
			bUserBitmap=true;
			mBitmap = ImageFileTool.getBitmap(info.getsApeaPath());
			model.setM_backColorPadding(Color.TRANSPARENT);
			
		}
		model.setLineWidth(0);

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
		}

		// 文本
		String text = " ";
		if (tInfo.getmTextList().size() > SystemInfo.getCurrentLanguageId()) {
			text = tInfo.getmTextList().get(SystemInfo.getCurrentLanguageId());
			if (text == null || text.equals("")) {
				text = " ";
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

		model.setStartX(info.getnLp());
		model.setStartY(info.getnTp());
		model.setRectWidth(info.getnWidth());
		model.setRectHeight(info.getnHeight());
		textItem.initTextPaint();
		textItem.initRectBoderPaint();
		textItem.initRectPaint();
	}

	/**
	 * 绘制控件
	 */
	private boolean bUserBitmap;
	private boolean bReset;
	private void draw(Canvas canvas,boolean isBack){
		if (info.getmTextList() == null || info.getmTextList().size() == 0) {
			return;
		}

		if (bReset) {
			bReset=false;
			setState();
		}

		if (bUserBitmap) {

			// 点击效果
			if (isBack) {
				if (bgBitmap == null) {
					bgBitmap=getAlphaBitmap(mBitmap, nColor);
					if(bgBitmap!=null){
						bgRect = new Rect(0, 0, bgBitmap.getWidth(),
								bgBitmap.getHeight());
					}
				}
				if (bgBitmap != null) {
					canvas.drawBitmap(bgBitmap, bgRect, rect, null);
				}
			}
			
			//控件图片
			if (mBitmap != null) {
				if (isBack) {
					canvas.drawBitmap(mBitmap, null, dRect, mPaint);
				} else {
					canvas.drawBitmap(mBitmap, null, rect, mPaint);
				}
			}
		}
		
		textItem.draw(canvas);
		
		if (!bUserBitmap) {
			if (isBack) {
				if (mCRectBitmap==null) {
					mCRectBitmap=getRectFrame(2, item.rect);
				}
				if (mCRectBitmap!=null) {
					canvas.drawBitmap(mCRectBitmap, info.getnLp(), info.getnTp(), mPaint);
				}
			}else {
				if (mRRectBitmap==null) {
					mRRectBitmap=getRectFrame(1, item.rect);
				}
				if (mRRectBitmap!=null&&model.getM_alphaPadding()>0) {
					canvas.drawBitmap(mRRectBitmap, info.getnLp(), info.getnTp(), mPaint);
				}
			}
		}
		
		//不可触控加上锁图标
		if (!touch) {
			if (mLockBitmap == null) {
				mLockBitmap = ImageFileTool.getBitmap(R.drawable.lock, mContext);
			}
			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getnLp(), info.getnTp(),null);
			}
		}
	}
	
	/**
	 * 注册显现和触控地址
	 */
	private void registNotice() {
		// 通知画面管理,调用控件的绘制方法
		SKSceneManage.getInstance().onRefresh(item);
		// 注册定时器
		//SKTimer.getInstance().getBinder().onRegister(callback,5);
		// 注册语言改变通知
		SKLanguage.getInstance().getBinder().onRegister(lCallback);
		
		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(),
						showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(),
						showCall, false);
			}

		}
		
		//注册触控地址
		if (touchByAddr) {
			ADDRTYPE addrType = info.getmTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
						touchCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
						touchCall, false);
			}
		}
	}
	
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
			nLanId=languageId;
			bReset=true;
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
}
