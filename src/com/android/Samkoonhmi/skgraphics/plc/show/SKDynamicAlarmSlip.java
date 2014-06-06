package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.ArrayList;
import java.util.Vector;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.alarm.AlarmDataInfo;
import com.android.Samkoonhmi.model.alarm.AlarmSlipInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.ARRAY_ORDER;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skgraphics.ITimerUpdate;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;

/**
 * 动态报警条
 * 
 * @author 刘伟江
 * @version v 1.0.0.1 创建时间 2012-4-21 最后修改时间 2012-7-13
 */
public class SKDynamicAlarmSlip extends SKGraphCmnShow implements ITimerUpdate,IItem {

	private static final String TAG = "SKDynamicAlarmSlip";
	private static final int HANDLER_ADD=1;
	// 每次移动的距离
	private static final int nMoveWidth = 10;
	private int nMoveTime;
	// 矩形
	private RectItem mRectItem;
	// 控件所在位置
	private Rect mRect;
	// 由于滚动的需要，文字会滚出矩形区域，所以要启用自己 的canvas
	private Canvas mCanvas;
	private Bitmap mBitmap;
	// 控件信息
	private AlarmSlipInfo mInfo;
	private Paint mPaint;
	private int startIndex;
	// 移动时的位置
	private int nMoveX;
	// 总共要移动几次
	private int nMoveCount;
	private ArrayList<AlarmDataInfo> list = null;
	private int nItemId;
	private int nSceneId;
	private SKItems items;
	private boolean init;
	private int nFontHeight;
	// 要显示的消息
	private String message="";
	private String tTaskName;
	private boolean show; // 是否显现
	private boolean showByAddr; // 受地址
	private boolean showByUser; // 受权限
	private boolean isClear;//如果消息为空，是否刷新
	private float nSize;
	private DisplayMetrics dis;
	private boolean flag;
	private int nTaskId;

	public SKDynamicAlarmSlip(int itemId, int scendId,AlarmSlipInfo info) {
		this.nItemId = itemId;
		this.nSceneId = scendId;
		this.tTaskName="";
		this.show = true;
		this.showByAddr = false;
		this.showByUser = false;
		this.init=true;
		this.flag=false;
		this.nMoveX=0;
		this.nMoveTime=0;
		this.mInfo=info;
		this.nTaskId=0;
		
		if (info!=null) {
			mRect = new Rect(mInfo.getnLeftTopX(), mInfo.getnLeftTopY(),
					mInfo.getnLeftTopX() + mInfo.getnWidth(), mInfo.getnLeftTopY()
							+ mInfo.getnHeight());
			
			mBitmap = Bitmap.createBitmap(mInfo.getnTextWidth(), mInfo.getnTextHeight(),
					Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			dis = new DisplayMetrics();
			dis.density = (float) 1.3125;
			dis.densityDpi = 210;
			dis.scaledDensity = (float) 1.3125;
			dis.xdpi = (float) 225.77777;
			dis.ydpi = (float) 225.77777;
			
			Rect rect = new Rect(0, 0, mInfo.getnWidth(), mInfo.getnHeight());
			mRectItem = new RectItem(rect);
			
			// 画矩形
			Rect rects = new Rect(-1, -1, mInfo.getnTextWidth() + 1,
					mInfo.getnTextHeight() + 1);
			mRectItem = new RectItem(rects);
			mRectItem.setLineColor(Color.TRANSPARENT);
			mRectItem.setLineWidth(0);
			mRectItem.setStyle(IntToEnum.getCssType(mInfo.getnStyle()));
			mRectItem.setBackColor(mInfo.getnBackcolor());
			mRectItem.setForeColor(mInfo.getnForecolor());
			
			items = new SKItems();
			items.itemId = nItemId;
			items.nZvalue = mInfo.getnZvalue();
			items.nCollidindId = mInfo.getnCollidindId();
			items.rect = mRect;
			items.sceneId = nSceneId;
			items.mGraphics=this;
			
			// 显现
			if (mInfo.getmShowInfo() != null) {
				if (mInfo.getmShowInfo().getShowAddrProp()!=null) {
					// 受地址控制
					showByAddr = true;
				}
				if (mInfo.getmShowInfo().isbShowByUser()) {
					// 受用户权限控制
					showByUser = true;
				}
			}

			
			//注册显示控制
			if(showByAddr){
				ADDRTYPE addrType = mInfo.getmShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(mInfo.getmShowInfo().getShowAddrProp(),
							showCall, true,scendId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(mInfo.getmShowInfo().getShowAddrProp(),
							showCall, false,scendId);
				}
			}
		}
	}

	public void updateStatus() {
		
	}

	@Override
	public boolean isShow() {
		itemIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return show;
	}

	/**
	 * 控件是否可以显现
	 */
	private void itemIsShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(mInfo.getmShowInfo());
		}
	}

	@Override
	public void initGraphics() {
		init();
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (mInfo==null) {
			return false;
		}
		if(itemId==nItemId&&show){
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			if (init) {
				//外框
				if (IntToEnum.getCssType(mInfo.getnStyle())
						!=CSS_TYPE.CSS_TRANSPARENCE) {
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(mInfo.getnFrameColor()); 
					canvas.drawRect(mInfo.getnLeftTopX(), mInfo.getnLeftTopY(),
							mInfo.getnLeftTopX()+mInfo.getnWidth(),
							mInfo.getnLeftTopY()+mInfo.getnHeight(),mPaint);
				}
				
				//文本
				draw(mPaint,mCanvas,init,message);
				canvas.drawBitmap(mBitmap, mInfo.getnTextLeftTopX(), mInfo.getnTextLeftTopY(),null);
				init=false;
			}else {
				//外框
				if (IntToEnum.getCssType(mInfo.getnStyle())
						!=CSS_TYPE.CSS_TRANSPARENCE) {
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(mInfo.getnFrameColor());
					canvas.drawRect(mInfo.getnLeftTopX(), mInfo.getnLeftTopY(),
							mInfo.getnLeftTopX()+mInfo.getnWidth(),
							mInfo.getnLeftTopY()+mInfo.getnHeight(),mPaint);
				}
				
				//画文本
				draw(mPaint, mCanvas, false, message);
				canvas.drawBitmap(mBitmap, mInfo.getnTextLeftTopX(), mInfo.getnTextLeftTopY(),null);
			}
			return true;
		}
		return false;
	}

	/**
	 * 初始化控件信息
	 */
	private void init() {

		if (mInfo == null){
			return;
		}
		
		nSize=TypedValue.applyDimension(2, mInfo.getnFontSize(), dis); 
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mInfo.getnTextColor());
		mPaint.setTextSize(nSize);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStrokeWidth(0);

		nFontHeight = getFontHeight(mPaint);
		
		//控件显现
		itemIsShow();
		
		nMoveTime = mInfo.getnSpeed();
		if (nMoveTime==0) {
			nMoveTime=5;
		}
		//nTextWidht = getFontWidth(message, mPaint);
		list=new ArrayList<AlarmDataInfo>();

		//注册地址通知
		registNotice();
		
		flag=true;
		message="";
		nTaskId++;
		
		//AlarmGroup.getInstance().saveAlarmData(0, 1,alarmCall,nTaskId, null);
		//延迟启动
		mHandler.postDelayed(runnable, 800); 
		
		
		//刷新
		SKSceneManage.getInstance().onRefresh(items); 

	}
	
	private Handler mHandler = new Handler();
	private Runnable runnable = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			AlarmGroup.getInstance().setNeedUpdateAlarm(true);
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
		}
	};
	
	
	/**
	 * 注册通知
	 */
	private void registNotice(){
		// 注册定时回调
		SKTimer.getInstance().getBinder().onRegister(callback,nMoveTime);
		// 注册报警登录
		AlarmGroup.getInstance().getBinder().onRegister(alarmCall);
		//多语言
		SKLanguage.getInstance().getBinder().onRegister(lCallback);
		
	}

	@Override
	public void realseMemeory() {
		SKTimer.getInstance().getBinder().onDestroy(callback,nMoveTime);
		SKThread.getInstance().getBinder().onDestroy(tCallback, tTaskName);
		AlarmGroup.getInstance().getBinder().onDestroy(alarmCall);
		flag=false;
		init=true;
		tTaskName="";
	}

	@Override
	public void getDataFromDatabase() {
		
	}
	
	@Override
	public void setDataToDatabase() {

	}

	/**
	 * @param init-true 初始化
	 * @param message-报警信息
	 */
	private synchronized void draw(Paint paint, Canvas canvas, boolean init, String message) {
		// 清空
		mRectItem.draw(paint, canvas);
		if (init) {
			initView(true);
			//move();
		} 
		if (message!=null) {
			//绘制
			paint.reset();
			paint.setAntiAlias(true);
			paint.setColor(mInfo.getnTextColor());
			paint.setTextSize(nSize);
			switch (mInfo.geteDirection()) {
			case RIGHT_TO_LEFT:
				//从右到左
				mPaint.setTextAlign(Align.LEFT);
				break;
			case LEFT_TO_RIGHT:
				//左到右
				mPaint.setTextAlign(Align.RIGHT);
				break;
			}
			
			if (message.length() > 2000) {//message 如果很大的话，那么只显示部分
				drawLongMessage(canvas, paint, message);
			}
			else {
				canvas.drawText(message, nMoveX,
						(mInfo.getnHeight() + nFontHeight / 2) / 2, paint);
			}
			
		}
		
	}
	int pos = 0;
	int startPos = 0;
	int endPos = 0;
	private void drawLongMessage(Canvas canvas , Paint paint,  String text){
		
		if (ARRAY_ORDER.RIGHT_TO_LEFT == mInfo.geteDirection()) { //从右到左
			if (nMoveX > 0) {
				startPos = 0;
				pos = nMoveX;
				
			}
			else {
				pos = 0;
				startPos += 4;
			}
			if (startPos >= message.length()) {
				startPos  =0 ;
				initView(true);
				pos = nMoveX;
			}
			endPos = startPos + 100;
			if (endPos >= message.length()) {
				endPos = message.length() -1; 
			}
			
		}
		else{//从左到右
			if (nMoveX < mInfo.getnWidth()) {
				endPos = message.length() -1;
				pos = nMoveX;
			}
			else {
				endPos -= 4;
				if (endPos < 0) {
					endPos = message.length() - 1;
					pos = 0;
					initView(true);
				}
			}
			startPos = endPos - 100;
			if (startPos < 0) {
				startPos = 0;
			}
		}
		canvas.drawText(message, startPos, endPos, pos, (mInfo.getnHeight() + nFontHeight / 2) / 2, paint);
		
	}

	/**
	 * 定时回调
	 */
	SKTimer.ICallback callback = new SKTimer.ICallback() {

		@Override
		public void onUpdate() {
			move();
		}
	};
	
	/**
	 * 处理实时报警数据
	 */
	SKThread.ICallback tCallback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {

		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			if (taskId==TASK.ALARM_SELECT) {
				
				updateAlarmSlip();
			}
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			
		}
	};
	
	private void updateAlarmSlip(){
		
		if (list == null) {
			return ;
		}
		list.clear();

		StringBuffer sBuffer=new StringBuffer();
		ArrayList<AlarmDataInfo> temp  = AlarmGroup.getInstance().getHappenedAlarmData();
		if (mInfo.isbSelectall()) {
			for(int i = 0; i < temp.size(); i++){
				list.add(temp.get(i));
			}
		}
		else {
			if (mInfo.getmGroupId() != null) {
				for(int i = 0; i < mInfo.getmGroupId().size(); i++){
					for(int j = 0; j< temp.size(); j++){
						String mgroup = temp.get(j).getnGroupId() + "";
						if (mInfo.getmGroupId().get(i).equals(mgroup)) {
						
							list.add(temp.get(j));
						}
					}
				}
			}
			}
			
		temp.clear();
		temp = null;
		
		if (list.size() == 0) {
			message =" ";
		}
		else {
			if (mInfo.geteSort()== ARRAY_ORDER.CLOCK_WISE) {
				// 顺时针
				for (int i = list.size() - 1; i >= 0; i--) {
					sBuffer.append("  " + list.get(i).getsMessage() + "  ");
				}
			} else if (mInfo.geteSort() == ARRAY_ORDER.COUNTER_CLOCK_WISE) {
				// 逆时针
				for (int i = 0; i < list.size(); i++) {
					sBuffer.append("  " + list.get(i).getsMessage() + "  ");
				}
			}
			message = sBuffer.toString();
			initView(false);
		}
		
	}
	
	

	/**
	 * 显现地址通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}
	};

	
	/**
	 * 报警通知
	 */
	AlarmGroup.IAlarmCallback alarmCall = new AlarmGroup.IAlarmCallback() {

		@Override
		public void onClose(ArrayList<AlarmDataInfo> list) {			
			if (list==null||list.size()==0||!flag) {
				return;
			}		
			
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
			
		}

		@Override
		public void onAlarm(ArrayList<AlarmDataInfo> list,boolean full) {
			if (list==null||list.size()==0||!flag) {
				return;
			}
			
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
			
		}
		
		@Override
		public void onClear(ArrayList<Integer>delList) {
			// 报警清除or报警确定
			if (delList == null || delList.size() == 0 || !flag) {
				return;
			}
			
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
		}

		@Override
		public void onClearHistory(ArrayList<Integer> hisList) {
			
		}

		@Override
		public void onConfirm(ArrayList<Integer> confirmList) {
			if (confirmList == null || confirmList.size() == 0 || !flag) {
				return;
			}
			
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
			
		}

		@Override
		public void onConfirm(int gid, int aid) {
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
			
		}

		@Override
		public void update(int taskId) {
			if (taskId!=nTaskId) {
				return;
			}
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
		}

		@Override
		public void onDateChange() {
			
		}

	};

	/**
	 * 多语言
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback(){

		@Override
		public void onLanguageChange(int languageId) {
			//获取报警数据
			message="";
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
		}
		
	};
	
	
	/**
	 * 初始化动态报警条 文本显示位置 和画笔
	 */
	private void initView(boolean init) {
	//	nTextWidht=getFontWidth(message, mPaint);		
		int temp=getFontWidth(message,mPaint)+mInfo.getnTextWidth()-4;
		nMoveCount=temp/nMoveWidth;
		switch (mInfo.geteDirection()) {
		case RIGHT_TO_LEFT:
			// 从右到左
			if (init) {
				nMoveX = mInfo.getnTextWidth() - 4;
				startIndex = 0;
			}
			mPaint.setTextAlign(Align.LEFT); 
			break;
		case LEFT_TO_RIGHT:
			// 左到右
			if (init) {
				nMoveX =0;
				startIndex = 0;
			}
			mPaint.setTextAlign(Align.RIGHT);
			break;
		case NOT_MOVE:
			//不移动
			nMoveX=(mInfo.getnTextWidth()-getFontWidth(message, mPaint))/2;
			if (nMoveX < 0) {
				nMoveX = 0;
			}
			break;
		}
	}

	
	/**
	 * 移动报警消息显示位置
	 */
	private void move() {
		if (startIndex < nMoveCount) {
			remove();
			startIndex++;
		} else {
			switch (mInfo.geteDirection()) {
			case RIGHT_TO_LEFT:
				// 从右到左
				nMoveX = mInfo.getnTextWidth() - 4;
				mPaint.setTextAlign(Align.LEFT);
				break;
			case LEFT_TO_RIGHT:
				// 左到右
				nMoveX = 0;
				mPaint.setTextAlign(Align.RIGHT);
				break;
			}
			startIndex = 0;
		}
	}

	/**
	 * 滚动文本
	 */
	private void remove() {
		switch (mInfo.geteDirection()) {
		case RIGHT_TO_LEFT:
			// 从右到左
			nMoveX -= nMoveWidth;
			break;

		case LEFT_TO_RIGHT:
			// 左到右
			nMoveX += nMoveWidth;
			break;
		}
		if (message==null||message.equals("")) {
			if (!isClear) {
				return;
			}
		}
		isClear=false;
		SKSceneManage.getInstance().onRefresh(items);
	}

	/**
	 * 获取字体所占宽度
	 */
	private int getFontWidth(String font, Paint paint) {
		if(font==null||font.equals("")){
			return 0;
		}
		return (int) paint.measureText(font);
	}

	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent);
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
		if (mInfo!=null) {
			return mInfo.getnLeftTopX();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnLeftTopY();
		}
		return -1;
	}


	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnWidth();
		}
		return -1;
	}


	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnHeight();
		}
		return -1;
	}


	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return getColor(mInfo.getnForecolor());
		}
		return null;
	}


	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return getColor(mInfo.getnBackcolor());
		}
		return null;
	}
	

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return getColor(mInfo.getnFrameColor());
		}
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
		return false;
	}


	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		
		if (mInfo != null) {
			if (x == mInfo.getnLeftTopX()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			mInfo.setnLeftTopX((short)x);
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			
			mInfo.setnTextLeftTopX((short)(mInfo.getnTextLeftTopX()+x-l));
			initView(true);
			
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (mInfo != null) {
			if (y == mInfo.getnLeftTopY()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			mInfo.setnLeftTopY((short)y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			mInfo.setnTextLeftTopY((short)(mInfo.getnTextLeftTopY()+y-t));
			initView(true);
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (mInfo != null) {
			if (w == mInfo.getnWidth()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int len=w-mInfo.getnWidth();
			mInfo.setnWidth((short)w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect = new Rect();
			
			mInfo.setnTextWidth((short)(mInfo.getnTextWidth()+len));
			initView(true);
			init=true;
			
			mBitmap = Bitmap.createBitmap(mInfo.getnTextWidth(), mInfo.getnTextHeight(),
					Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (mInfo != null) {
			if (h == mInfo.getnHeight()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int len=h-mInfo.getnHeight();
			mInfo.setnHeight((short)h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect = new Rect();
			
			initView(true);
			init=true;
			mInfo.setnTextHeight((short)(mInfo.getnTextHeight()+len));
			
			mBitmap = Bitmap.createBitmap(mInfo.getnTextWidth(), mInfo.getnTextHeight(),
					Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			int color=Color.rgb(r, g, b);
			if (color==mInfo.getnForecolor()) {
				return true;
			}
			mInfo.setnForecolor(color);
			mRectItem.setForeColor(color);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			int color=Color.rgb(r, g, b);
			if (color==mInfo.getnBackcolor()) {
				return true;
			}
			mInfo.setnBackcolor(color);
			mRectItem.setBackColor(color);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if(mInfo!=null){
			int color=Color.rgb(r, g, b);
			if (color==mInfo.getnFrameColor()) {
				return true;
			}
			mInfo.setnFrameColor(color);
			mRectItem.setLineColor(color);
			SKSceneManage.getInstance().onRefresh(items);
		}
		return false;
	}


	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==show) {
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}


	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
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