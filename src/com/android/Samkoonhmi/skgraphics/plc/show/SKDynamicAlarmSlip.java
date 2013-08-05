package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.ArrayList;
import java.util.Vector;

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
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
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
public class SKDynamicAlarmSlip extends SKGraphCmnShow implements ITimerUpdate {

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
	// 显示消息所占的宽度
	private int nTextWidht;
	private String tTaskName;
	private boolean show; // 是否显现
	private boolean showByAddr; // 受地址
	private boolean showByUser; // 受权限
	private boolean isClear;//如果消息为空，是否刷新
	private float nSize;
	private DisplayMetrics dis;
	private boolean flag;
	//private boolean bRefreshing;
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

		//控件显现
		itemIsShow();
		
		nMoveTime = mInfo.getnSpeed();
		if (nMoveTime==0) {
			nMoveTime=5;
		}
		nTextWidht = getFontWidth(message, mPaint);
		list=new ArrayList<AlarmDataInfo>();

		//注册地址通知
		registNotice();
		
		flag=true;
		message="";
		nTaskId++;
		
		//AlarmGroup.getInstance().saveAlarmData(0, 1,alarmCall,nTaskId, null);
		AlarmGroup.getInstance().setNeedUpdateAlarm(true);
		if (tTaskName.equals("")) {
			tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
		}
		SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
		
		//刷新
		SKSceneManage.getInstance().onRefresh(items); 
	}

	
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
		
		//注册显示控制
		if(showByAddr){
			ADDRTYPE addrType = mInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(mInfo.getmShowInfo().getShowAddrProp(),
						showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(mInfo.getmShowInfo().getShowAddrProp(),
						showCall, false);
			}
		}

	}

	@Override
	public void realseMemeory() {
		SKTimer.getInstance().getBinder().onDestroy(callback,nMoveTime);
		SKThread.getInstance().getBinder().onDestroy(tCallback, tTaskName);
		AlarmGroup.getInstance().getBinder().onDestroy(alarmCall);
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
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
			canvas.drawText(message, nMoveX,
					(mInfo.getnHeight() + nFontHeight / 2) / 2, paint);
		}
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
//			if (taskId==TASK.ALARM_CLEAR_REFRESH) {
//				/**
//				 * 报警消除
//				 */
//				ArrayList<AlarmDataInfo> list=(ArrayList<AlarmDataInfo>)msg;
//				if (list==null||list.size()==0) {
//					return;
//				}
//				alarm(1,list,0,0);
//			}else if (taskId==TASK.ALARM_ADD_REFRESH) {
//				/**
//				 * 报警产生
//				 */
//				ArrayList<AlarmDataInfo> list=(ArrayList<AlarmDataInfo>)msg;
//				if (list==null||list.size()==0) {
//					return;
//				}
//				addAlarm(list);
//			}else if (taskId == TASK.ALARM_DELETE) {
//				/**
//				 * 报警清除
//				 */
//				ArrayList<Integer> delList = (ArrayList<Integer>) msg;
//				if (delList == null || delList.size() == 0 ) {
//					return ;
//				}
//				
//				clearAlarmByGroup(delList);
//			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			if (taskId==TASK.ALARM_SELECT) {
//				ArrayList<AlarmDataInfo> temp  = AlarmGroup.getInstance().getHappenedAlarmData();
//				if (temp == null ) {
//					return ;
//				}
//	        	   
//					if (temp!=null) {
//						list.clear();
//						list.addAll(temp);
//						temp.clear();
//						temp = null;
//					}
//					
//					if (list != null) {
//						StringBuffer sBuffer=new StringBuffer();
//						sBuffer.append(message);
//						if (mInfo.geteSort()== ARRAY_ORDER.CLOCK_WISE) {
//							// 顺时针
//							for (int i = list.size() - 1; i >= 0; i--) {
//								sBuffer.append("  " + list.get(i).getsMessage() + "  ");
//							}
//						} else if (mInfo.geteSort() == ARRAY_ORDER.COUNTER_CLOCK_WISE) {
//							// 逆时针
//							for (int i = 0; i < list.size(); i++) {
//								sBuffer.append("  " + list.get(i).getsMessage() + "  ");
//							}
//						}
//						message=sBuffer.toString();
//						initView(false);
//					}
				
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
			for(int i = 0; i < mInfo.getmGroupId().size(); i++){
				for(int j = 0; j< temp.size(); j++){
					String mgroup = temp.get(j).getnGroupId() + "";
					if (mInfo.getmGroupId().get(i).equals(mgroup)) {
					
						list.add(temp.get(j));
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
	
//	/**
//	 * 报警消除
//	 */
//	private void clearAlarm(ArrayList<AlarmDataInfo> clist){
//	
//		if (list==null){
//			return;
//		}
//		if (!mInfo.isbSelectall()) {
//			if (mInfo.getmGroupId()==null||mInfo.getmGroupId().size()==0) {
//				return;
//			}
//		}
//		
//		for (int j = 0; j < clist.size(); j++) {
//			AlarmDataInfo dinfo=clist.get(j);
//			for (int i = 0; i < list.size(); i++) {
//				AlarmDataInfo info=list.get(i);;
//				if (dinfo.getnGroupId()==info.getnGroupId()&&info.getnAlarmIndex()==dinfo.getnAlarmIndex()) {
//					list.remove(i);
//					--i;
//					break;
//				}
//			}
//		}
//		
//		message="";
//		StringBuffer sBuffer=new StringBuffer();
//		int size=list.size();
//		for (int i = 0; i < size; i++) {
//			AlarmDataInfo info=list.get(i);
//			sBuffer.append(info.getsMessage()+"  ");
//		}
//		isClear=true;
//		message=sBuffer.toString();
//		initView(false);
//		
//	}
	
//	private void clearAlarmByGroup(ArrayList<Integer> delList){
//		if (list == null) {
//			return ;
//		}
//		if (!mInfo.isbSelectall()) {
//			if (mInfo.getmGroupId() == null || mInfo.getmGroupId().size() == 0) {
//				return ;
//			}
//		}
//
//		for(int i = 0 ; i < list.size(); i++){
//			AlarmDataInfo info = list.get(i);
//			if (delList.contains(info.getnGroupId())) {
//				list.remove(i);
//				--i;
//			}
//		}
//		
//		message="";
//		StringBuffer sBuffer=new StringBuffer();
//		int size=list.size();
//		for (int i = 0; i < size; i++) {
//			AlarmDataInfo info=list.get(i);
//			sBuffer.append(info.getsMessage()+"  ");
//		}
//		isClear=true;
//		message=sBuffer.toString();
//		initView(false);
//	}
//	
//	/**
//	 * 报警产生
//	 */
//	private void addAlarm(ArrayList<AlarmDataInfo> alist){
//		
//		StringBuffer sBuffer=new StringBuffer();
//		
//		
//		if (mInfo.geteSort()== ARRAY_ORDER.CLOCK_WISE) {
//			sBuffer.append(message);
//		}
//		
//		//long start=System.currentTimeMillis();
//		for (int j = 0; j < alist.size(); j++) {
//			
//			AlarmDataInfo dinfo=null;
//			if (mInfo.geteSort()== ARRAY_ORDER.CLOCK_WISE) {
//				dinfo=alist.get(j);
//			}else {
//				dinfo=alist.get(alist.size()-1-j);
//			}
//			
//			boolean result=false;
//			if (!mInfo.isbSelectall()) {
//				if (mInfo.getmGroupId()==null||mInfo.getmGroupId().size()==0) {
//					return;
//				}
//				String gid=dinfo.getnGroupId()+"";
//				for (int i = 0; i < mInfo.getmGroupId().size(); i++) {
//					if (gid.equals(mInfo.getmGroupId().get(i))) {
//						result=true;
//						break;
//					}
//				}
//			}else {
//				result=true;
//			}
//			if (result) {
//				if (list!=null) {
//					list.add(dinfo);
//					sBuffer.append(dinfo.getsMessage()+"  ");
//				}
//			}
//		}
//		
//		if (mInfo.geteSort()== ARRAY_ORDER.COUNTER_CLOCK_WISE) {
//			sBuffer.append(message);
//		}
//		bRefreshing=false;
//		message=sBuffer.toString();
//		initView(false);
//	
//	}

	/**
	 * 显现地址通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}
	};

//	private synchronized void alarm(int type,ArrayList<AlarmDataInfo> clist,int gid,int aid){
//		if (type==0) {
//			message="";
//			isClear=true;
//			if (list!=null) {
//				list.clear();
//			}
//			SKSceneManage.getInstance().onRefresh(items);
//		}else if (type==1) {
//			clearAlarm(clist);
//		}else if (type==2) {
//			boolean result=false;
//			if (!mInfo.isbSelectall()) {
//				if (mInfo.getmGroupId()==null||mInfo.getmGroupId().size()==0) {
//					return;
//				}
//				String id=gid+"";
//				for (int i = 0; i < mInfo.getmGroupId().size(); i++) {
//					if (id.equals(mInfo.getmGroupId().get(i))) {
//						result=true;
//						break;
//					}
//				}
//			}else {
//				result=true;
//			}
//			if (!result) {
//				return;
//			}
//			if (list!=null) {
//				message="";
//				isClear=true;
//				int size=list.size();
//				StringBuffer sBuffer=new StringBuffer();
//				for (int i = 0; i <size; i++) {
//					AlarmDataInfo info=list.get(i);
//					if (info.getnGroupId()==gid&&info.getnAlarmIndex()==aid) {
//						list.remove(i);
//						i--;
//						size--;
//					}else {
//						sBuffer.append("  "+info.getsMessage()+"  ");
//					}
//				}
//				message=sBuffer.toString();
//				initView(false);
//			}
//		}
//	}
//	
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
			
//			if (tTaskName.equals("")) {
//				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//			}
//			SKThread.getInstance().getBinder()
//			.onTask(MODULE.ALARM, TASK.ALARM_CLEAR_REFRESH, tTaskName,list);
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
			
//			if (bRefreshing) {
//				Message msg=new Message();
//				msg.what=HANDLER_ADD;
//				msg.obj=list;
//				mHandler.removeMessages(HANDLER_ADD);
//				mHandler.sendMessageDelayed(msg, 200);
//				return;
//			}
//			bRefreshing=true;
//			if (tTaskName.equals("")) {
//				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//			}
//			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_ADD_REFRESH,tTaskName,list);
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
			
//			if (tTaskName.equals("")) {
//				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//			}
//			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_DELETE,tTaskName,delList);
			
		//	clearAlarmByGroup(delList);
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
			
//			if (tTaskName.equals("")) {
//				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//			}
//			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_DELETE,tTaskName,confirmList);
		}

		@Override
		public void onConfirm(int gid, int aid) {
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName);
			
			//alarm(2,null,gid,aid);
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
			//AlarmGroup.getInstance().saveAlarmData(0, 1,alarmCall,nTaskId ,null);
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
		nTextWidht=getFontWidth(message, mPaint);
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

//	Handler mHandler=new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if(msg.what==HANDLER_ADD){
//				//延迟刷新
//				if (bRefreshing) {
//					return ;
//				}
//				
//				ArrayList<AlarmDataInfo> list=(ArrayList<AlarmDataInfo>)msg.obj;
//				if (list==null||list.size()==0) {
//					return;
//				}
//				
//				bRefreshing=true;
//				if (tTaskName.equals("")) {
//					tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//				}
//				SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_ADD_REFRESH,tTaskName,list);
//			//	addAlarm(list);
//			}
//		}
//		
//	};
	
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
}