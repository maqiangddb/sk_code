package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Vector;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.MotionEvent;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.graphicsdrawframe.DragTable;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableLoadInfo;
import com.android.Samkoonhmi.model.alarm.AlarmContolInfo;
import com.android.Samkoonhmi.model.alarm.AlarmDataInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TextAlignUtil;

/**
 * 报警控件显示器
 * 
 * @author 刘伟江
 * @version V 1.0.0.2 创建时间 2012-5-7 最后修改时间 2012-5-7
 */
public class SKAlarmContol extends SKGraphCmnTouch implements IItem {

	// 表格内容
	private int mRankCount;
	// 报警控件实体对象
	private AlarmContolInfo info;
	// 表格
	private DragTable dTable;
	private Context mContext;
	private int nItemId;
	private int nSceneId;
	private SKItems items;
	private boolean show; // 是否可显现
	private boolean touch; // 是否可触控
	private boolean showByAddr; // 是否注册显现地址
	private boolean touchByAddr; // 是否注册触控地址
	private boolean showByUser; // 是否受用户权限控件
	private boolean touchByUser; // 是否受用户权限控件
	private boolean flag;
	private String tTaskName;
	private ArrayList<String> mHList;
	private Vector<RowCell> mRowCells;
	private int nAllCount=0;
	private int nRowIndex;
	private String sTodayDate="";
	private int nTaskId;
	private boolean isLoading;
	private boolean bUntreated;//有未加载的任务
	private TableLoadInfo mLoadInfo;//加载数据信息包

	public SKAlarmContol(Context context, int itemId, int sceneId,AlarmContolInfo info) {
		this.nItemId = itemId;
		this.nSceneId = sceneId;
		this.mContext = context;
		this.flag = false;
		tTaskName="";
		nRowIndex=0;
		this.info=info;
		mLoadInfo=new TableLoadInfo();
		
		if (info!=null) {
			Rect rect = new Rect(info.getnLeftTopX(), info.getnLeftTopY(),
					info.getnLeftTopX() + info.getnWidth(),
					info.getnLeftTopY() + info.getnHeight());
			
			items = new SKItems();
			items.itemId = nItemId;
			items.sceneId = nSceneId;
			items.nZvalue = info.getnZvalue();
			items.nCollidindId = info.getnCollidindId();
			items.rect = rect;
			items.mGraphics=this;
			
			this.show = true;
			this.touch = true;
			this.showByAddr = false;
			this.touchByAddr = false;
			this.showByUser = false;
			this.touchByUser = false;
			
			// 显现权限
			if (info.getmShowInfo() != null) {
				if (info.getmShowInfo().getShowAddrProp()!=null) {
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
				if (info.getmTouchInfo().getTouchAddrProp()!=null) {
					// 受地址控制
					touchByAddr = true;
				}
				if (info.getmTouchInfo().isbTouchByUser()) {
					// 受用户权限控制
					touchByUser = true;
				}
			}

			// 注册显现地址
			if (showByAddr) {
				ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(), showCall,
							true,sceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(), showCall,
							false,sceneId);
				}

			}

			// 注册触控地址
			if (touchByAddr) {
				ADDRTYPE addrType = info.getmTouchInfo().geteCtlAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
							touchCall, true,sceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
							touchCall, false,sceneId);
				}
			}
		}
	}

	@Override
	public void getDataFromDatabase() {
		// 后台线程读取控件信息
	}

	@Override
	public void setDataToDatabase() {

	}

	@Override
	public void initGraphics() {
		init();
	}

	// 画面管理调用控件刷新方法
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			if (itemId == nItemId && (info != null)) {
				dTable.draw(canvas);
				return true;
			}
		}
		return false;
	}

	@Override
	public void realseMemeory() {
		//  清除表格中的报警信息
		flag = false;
		SKThread.getInstance().getBinder().onDestroy(tCallback, tTaskName);
		AlarmGroup.getInstance().getBinder().onDestroy(alarmCall);
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onDestroy(lCallback);
		}
		tTaskName="";
		sTodayDate="";
	}

	@Override
	public boolean isShow() {
		itemShow();
		SKSceneManage.getInstance().onRefresh(items);
		return show;
	}

	@Override
	public boolean isTouch() {
		itemTouch();
		return touch;
	}

	/**
	 * 显现
	 */
	private void itemShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(info.getmShowInfo());
		}
	}

	/**
	 * 触控
	 */
	private void itemTouch() {
		if (touchByAddr || touchByUser) {
			touch = popedomIsTouch(info.getmTouchInfo());
		}
	}

	/**
	 * 注册通知
	 */
	private void register() {
		
		/**
		 * 注册报警登录
		 */
		AlarmGroup.getInstance().getBinder().onRegister(alarmCall);
		
		/**
		 * 多语言
		 */
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}
	}

	/**
	 * 显隐地址回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}
	};

	/**
	 * 触摸地址回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isTouch();
		}
	};
	
	/**
	 * 多语言
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback() {
		
		@Override
		public void onLanguageChange(int languageId) {
			
			initTableItem();
			//AlarmGroup.getInstance().saveAlarmData(0, 1,alarmCall,nTaskId ,null);
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
		}
	};

	
	/**
	 * 处理实时报警数据
	 */
	SKThread.ICallback tCallback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			if (taskId==TASK.ALARM_CLEAR_REFRESH) {
//				/**
//				 * 报警消除
//				 */
			}else if (taskId==TASK.ALARM_ADD_REFRESH) {
//				/**
//				 * 报警产生
//				 */
			}
			else if (taskId == TASK.ALARM_UPDATE) {
			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			if (TASK.ALARM_SELECT==taskId) {
				ArrayList<AlarmDataInfo> list  = AlarmGroup.getInstance().getHappenedAlarmData();
				
				if (!info.isbShowwall())
				{
					if(info.getmGroupId() != null && info.getmGroupId().size() != 0)
					{
						for(int i = 0; i < list.size(); i++){
							AlarmDataInfo data = list.get(i);
							if (data != null  && !info.getmGroupId().contains(data.getnGroupId()+"")) {
								list.remove(data);
								i--;
							}
						}
					}
					else
					{
						list.clear();
					}
				   
				}
				
				//表格显示的开始位置
				int startPos = 0;
				int totalCount = list.size();
				boolean beSlide = true;
				if (list.size() > info.getnRowCount())
				{
					startPos = list.size() - info.getnRowCount();
				}
				
				if(mLoadInfo.nLoadType==0){
					nAllCount= totalCount;
					beSlide = false;
					
					flag=true;
					if (nAllCount>info.getnRowCount()) {
						mLoadInfo.nRowIndex=nAllCount-info.getnRowCount()+1;
					}else {
						mLoadInfo.nRowIndex=1;
					}
					mLoadInfo.nEndIndex=nAllCount;
				}
				
				//初始化表格
				initData(list,startPos,mLoadInfo.nLoadType,mLoadInfo.nRowIndex, beSlide);

			}
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			
		}
		
	};
	
	
	/**
	 * 
	 */
	private boolean isNeedUpdate(ArrayList<Integer> group){
		boolean ret = false;
		if (group != null && group.size() != 0 ){
			if (!info.isbShowwall()) {
				if (info.getmGroupId() != null && info.getmGroupId().size() != 0) {
					for(int i = 0; i < group.size(); i++){
						if (info.getmGroupId().contains(group.get(i)+"")) {
							ret = true;
							break;
						}
					}
				}
			}
			else {
				ret = true;
			}
		}
		if (ret ) {
			mLoadInfo.nLoadType=0;
			mLoadInfo.nLoadCount=info.getnRowCount();
		}
		
		return ret;
	}
	
	
	/**
	 * 报警触发回调
	 */
	AlarmGroup.IAlarmCallback alarmCall = new AlarmGroup.IAlarmCallback() {

		@Override
		public void onClose(ArrayList<AlarmDataInfo> list) {
			//报警消除
			if (list==null||list.size()==0||!flag) {
				return;
			}
			ArrayList<Integer> tempList =  new ArrayList<Integer>();
			for(int i = 0; i < list.size(); i++){
				tempList.add(list.get(i).getnGroupId());
			}
			
			boolean needupdate = isNeedUpdate(tempList);
			tempList.clear();
			tempList = null;
			if (! needupdate) {
				return ;
			}
			
			
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
//			if(tTaskName.equals("")){
//				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//			}
//			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_CLEAR_REFRESH, tTaskName,list);
		}

		@Override
		public void onAlarm(ArrayList<AlarmDataInfo> list,boolean full) {
//			if (list==null||list.size()==0||!flag) {
//				return;
//			}
//			if (bRefreshing) {
//				Message msg=new Message();
//				msg.what=HANDLER_ADD;
//				msg.obj=list;
//				mHandler.removeMessages(HANDLER_ADD);
//				mHandler.sendMessageDelayed(msg, 200);
//				return;
//			}
//			bRefreshing=true;
//			if(tTaskName.equals("")){
//				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
//			}
//			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_ADD_REFRESH,tTaskName,list);
			if (list==null||list.size()==0||!flag) {
				return;
			}
			ArrayList<Integer> tempList =  new ArrayList<Integer>();
			for(int i = 0; i < list.size(); i++){
				tempList.add(list.get(i).getnGroupId());
			}
			
			boolean needupdate = isNeedUpdate(tempList);
			tempList.clear();
			tempList = null;
			if (! needupdate) {
				return ;
			}
			
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
		}
		
		@Override
		public void onClear(ArrayList<Integer>delList) {
			// 清除报警
			
			if (!flag || !isNeedUpdate(delList)) {
				return;
			}
			
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
		}

		@Override
		public void onClearHistory(ArrayList<Integer> hisList) {
		
		}

		@Override
		public void onConfirm(ArrayList<Integer>confrimList) {
			if (!flag || !isNeedUpdate(confrimList)) {
				return;
			}
			
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
		}

		@Override
		public void onConfirm(int gid, int aid) {
			
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			tempList.add(gid);
			boolean needupdate = isNeedUpdate(tempList);
			tempList.clear();
			tempList = null;
			if (! needupdate) {
				return ;
			}
			
			
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
			//alarm(2,null,gid,aid);
		}

		@Override
		public void update(int taskId) {
			
			if (taskId!=nTaskId) {
				return;
			}
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName,taskId);
		}

		@Override
		public void onDateChange() {
			sTodayDate="";
		}
		
	};
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		if (show&&touch) {
			if (dTable != null) {
				return dTable.onTouchEvent(event);
			}
		}
		return false;
	}

	/**
	 * 初始化表格每一格的宽高
	 */
	private void init() {
		if (info == null) {
			return;
		}
		nTaskId++;

		// 初始化表格内容
		initTableItem();
		

		if (dTable==null) {
			DragTableInfo dInfo = new DragTableInfo();
			dInfo.setnFrameColor(info.getnFrameColor());
			dInfo.setnLineColor(Color.rgb(192, 192, 192));//上位
			dInfo.setnTableBackcolor(info.getnTableColor());
			dInfo.setnAlpha(255);
			
			dInfo.setnTitleFontColor(info.getnTitleColor());
			short size=10;
			if (info.getnTitleFontSizes().size()>SystemInfo.getCurrentLanguageId()) {
				size=info.getnTitleFontSizes().get(SystemInfo.getCurrentLanguageId());
			}
			dInfo.setnTitleFontSize(size);
			dInfo.setnTitleBackcolor(info.getnTitleBackcolor());
			
			dInfo.setnVTitleFontColor(info.getnTextColor());
			dInfo.setnVTitleFontSize(info.getnFontSize());
			dInfo.setnVTitleBackcolor(info.getnTableColor());
			
			dInfo.setnTextFontColor(info.getnTextColor());
			dInfo.setnTextFontSize(info.getnFontSize());
			dInfo.setmTypeFace(Typeface.DEFAULT);
			dInfo.setmVTypeFace(Typeface.DEFAULT);
			Typeface mType=Typeface.DEFAULT;
			if (info.getsTitleFontTypes().size()>SystemInfo.getCurrentLanguageId()) {
				mType=TextAlignUtil.getTypeFace(info.getsTitleFontTypes().get(SystemInfo.getCurrentLanguageId()));
			}
			dInfo.setmHTypeFace(mType);
			dInfo.setnRow((short) (info.getnRowCount()+1));
			dInfo.setnRank((short) mRankCount);
			dInfo.setnWidth(info.getnWidth());
			dInfo.setnHeight(info.getnHeight());
			dInfo.setmRowHeight(info.getmRowHeight());
			dInfo.setmRowWidth(info.getmRowWidht());
			dInfo.setnLeftTopX(info.getnLeftTopX());
			dInfo.setnLeftTopY(info.getnLeftTopY());
			dInfo.setnPageIndex(1);
			dInfo.setmAlign(TEXT_PIC_ALIGN.CENTER);
			dInfo.setShowNum(false);
			dTable = new DragTable(dInfo, mContext, items,true);
			dTable.init(null);
			dTable.setiPageTurning(iTurning);
			dTable.initData(mRowCells, mHList,0);
			dTable.drawTable();
		}
		
		// 注册回调
		register();
		// 显现
		itemShow();
		// 触控
		itemTouch();
		
		sTodayDate="";
		mLoadInfo.nLoadType=0;
		mLoadInfo.nLoadCount=info.getnRowCount();
		
		flag=true;
		// 进行更新报警信息
	//	AlarmGroup.getInstance().saveAlarmData(0, 1,alarmCall,nTaskId, null);
		//延迟启动
		mHandler.postDelayed(runnable, 800);
		
				
		// 通知画面管理刷新控件
		SKSceneManage.getInstance().onRefresh(items);
	}
	
	private Handler mHandler = new Handler();
	private Runnable runnable = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			AlarmGroup.getInstance().setNeedUpdateAlarm(true);
			if(tTaskName.equals("")){
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}	
			SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
		}
	};

	private void initTableItem() {

		if (mHList==null) {
			mHList=new ArrayList<String>();
		}else {
			mHList.clear();
		}
		
		if (mRowCells==null) {
			mRowCells=new Vector<RowCell>();
		}
		
		mRankCount = 0;
		if (info.isbShowTime()) {
			mRankCount++;
			String time="";
			if (info.getsTimeStrs().size()>SystemInfo.getCurrentLanguageId()) {
				time=info.getsTimeStrs().get(SystemInfo.getCurrentLanguageId());
			}
			mHList.add(time);
		}
		
		if (info.isbShowDate()) {
			mRankCount++;
			String date="";
			if (info.getsDateStrs().size()>SystemInfo.getCurrentLanguageId()) {
				date=info.getsDateStrs().get(SystemInfo.getCurrentLanguageId());
			}
			mHList.add(date);
		}
		
		mRankCount++;
		String msg="";
		if (info.getsMessages().size()>SystemInfo.getCurrentLanguageId()) {
			msg=info.getsMessages().get(SystemInfo.getCurrentLanguageId());
		}
		mHList.add(msg);
		
	}
	
	/**
	 * 初始化数据
	 */
	private void initData( ArrayList<AlarmDataInfo> list,int startPos, int type,int top, boolean beSlide){
		
		//数据查询
		if (info==null) {
			return;
		}
		
		mRowCells.clear();
		if(list!=null&&list.size()>0){
			int id=top;
			if (id < 1) {
				id = 1;
			}
			if (beSlide) {// 滚动到固定位置
				int count  =0 ;
				for (int i = top; i < list.size(); i++) {
					
					AlarmDataInfo aInfo=list.get(i);
					addRow(aInfo,id++);
					count ++;
					if (count > info.getnRowCount()) {
						break;
					}
				}
			}
			else {
				for (int i = startPos; i < list.size(); i++) {
					AlarmDataInfo aInfo=list.get(i);
					addRow(aInfo,id++);
				}
			}
			
			
			
		}
		
		if (dTable != null) {
			
			dTable.updateView(1);
			dTable.updateRowIndex(0);
			dTable.initData(mRowCells, mHList, 0);
			
			if (type == 0) {
				bFore=false;
				bLast=true;
				dTable.updateDataNum(nAllCount);
			} else {
				if (bUntreated) {
					bUntreated = false;
					mLoadInfo.nLoadType=type;
					if (this.nTop>=nAllCount) {
						mLoadInfo.nRowIndex=nAllCount-info.getnRowCount()+1;
						mLoadInfo.nEndIndex=nAllCount;
					}else {
						mLoadInfo.nRowIndex=this.nTop;
						mLoadInfo.nEndIndex=this.nTop+info.getnRowCount();
					}
					mLoadInfo.nLoadCount=info.getnRowCount();
					
//					AlarmGroup.getInstance()
//							.saveAlarmData(0, 1, alarmCall, nTaskId, null);
					if(tTaskName.equals("")){
						tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
					}	
					SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
				}
			}
			
			if (bLast&&dTable.isShowBar()) {
				//处于底部，并且滑动块显示
				if (mRowCells.size()>=info.getnRowCount()) {
					dTable.updateView(2);
				}
				dTable.moveToBottom();
			}
			
		}

		isLoading = false;
		SKSceneManage.getInstance().onRefresh(items);
		
		if (list != null) {
			list.clear();
			list = null;
		}
	}
	
	/**
	 * 添加一行
	 */
	private void addRow(AlarmDataInfo aInfo,int top ){
		Vector<String> mClounms=new Vector<String>();
		RowCell rowCell=new RowCell();
		rowCell.nRowIndex=top;
		rowCell.nClounmCount=mRankCount;
		rowCell.mClounm=mClounms;
		rowCell.gid=aInfo.getnGroupId();
		rowCell.aid=aInfo.getnAlarmIndex();
		nRowIndex=top;
		
		getTime(info.geteTimeFormat(), info.geteDateFormat(), aInfo, 1);//报警时间
		getTime(info.geteTimeFormat(), info.geteDateFormat(), aInfo, 2);//报警消除时间
		
		if(info.isbShowTime()){
			mClounms.add(aInfo.getsTime());
		}
		if (info.isbShowDate()) {
			mClounms.add(aInfo.getsDate());
		}
		mClounms.add(aInfo.getsMessage());
		mRowCells.add(rowCell);
	}
	
	/**
	 * 翻页
	 */
	DragTable.IPageTurning iTurning=new DragTable.IPageTurning() {
		
		@Override
		public void onUpdate(int len) {
			
		}
		
		@Override
		public void onPre(int page) {
			
		}
		
		@Override
		public void onNext(int page) {
			
		}
		
		@Override
		public void onLoad(int top, int type,int site) {
			dataCenter(top, type,site);
		}
	};
	
	/**
	 * @param top-当前显示页第一行的序号
	 * @param type 1-加载前一页数据，2-加载后一页数据
	 */
	private int nTop=0;
	private boolean bLast=false;
	private boolean bFore=false;
	private void dataCenter(int top,int type,int site){
		
		//Log.d(TAG,".......top:"+top+",site:"+site+",isLoading:"+isLoading+",bFore:"+bFore);
		if (isLoading) {
			bUntreated = true;
			this.nTop = top;
			return;
		}

		if (type == 1 || type == 3) {
			if (bFore) {
				// Log.d(TAG, "已经处于最前面...");
				return;
			} else {
				if (site == 0) {
					// 处于最顶部
					bFore = true;
					bLast = false;
				} else {
					if (site == 1) {
						// 处于中间
						bLast = false;
					}
					bFore = false;
				}
			}
		}

		if (type == 2 || type == 4) {
			if (bLast) {
				// Log.d(TAG, "已经处于最后面...");
				return;
			} else {
				if (site == 2) {
					// 处于底部
					bLast = true;
					bFore = false;
				} else {
					if (site == 1) {
						// 处于中间
						bFore = false;
					}
					bLast = false;
				}
			}
		}

		isLoading = true;
		if (bLast) {
			if (nAllCount > info.getnRowCount()) {
				top = nAllCount - info.getnRowCount() + 1;
			}
		}

		//Log.d(TAG, ".....type:"+type+",top:"+top);
		mLoadInfo.nLoadType = type;
		mLoadInfo.nRowIndex = top;
		mLoadInfo.nEndIndex = top + info.getnRowCount();
		mLoadInfo.nLoadCount = info.getnRowCount();
				
		//AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId, null);
		if(tTaskName.equals("")){
			tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
		}	
		SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_SELECT, tTaskName, nTaskId);
	}
	
	/**
	 *格式化日期和时间
	 */
	private void getTime(TIME_FORMAT timeformat,DATE_FORMAT dateformat,AlarmDataInfo info,int type){
		
		if (type==1)
		{
			Date mDate=new Date(info.getnDateTime());
			if (sTodayDate.equals(""))
			{
				sTodayDate = DateStringUtil.convertDate(dateformat, mDate);
			}
			String times= DateStringUtil.converTime(timeformat, mDate);
			if (sTodayDate==null||times==null)
			{
				info.setsTime("");
				info.setsDate("");
			}
			else 
			{
				info.setsDate(sTodayDate);
				info.setsTime(times);
			}
		}
		
	}

	
	/**
	 * 控件对外接口
	 */
	
	public IItem getIItem(){
		return this;
	}
	
	@Override
	public int getItemLeft(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return info.getnLeftTopX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return info.getnLeftTopY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO 自动生成的方法存根
		if(info!=null){
			return info.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO 自动生成的方法存根
		if(info!=null){
			return info.getnHeight();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return getColor(info.getnTableColor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO 自动生成的方法存根
		if(info!=null){
			return getColor(info.getnFrameColor());
		}
		return null;
	}

	@Override
	public boolean getItemVisible(int id) {
		// TODO 自动生成的方法存根
		return show;
	}

	@Override
	public boolean getItemTouchable(int id) {
		// TODO 自动生成的方法存根
		return touch;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if (x<0||x>SKSceneManage.getInstance().getCurrentScene().nSceneWidth) {
				return false;
			}
			if (x==info.getnLeftTopX()) {
				return true;
			}
			info.setnLeftTopX((short)x);
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			dTable.resetLeftTopX(x);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if (y<0||y>SKSceneManage.getInstance().getCurrentScene().nSceneHeight) {
				return false;
			}
			if (y==info.getnLeftTopY()) {
				return true;
			}
			info.setnLeftTopY((short)y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			dTable.resetLeftTopY(y);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if(w<0||w>SKSceneManage.getInstance().getCurrentScene().nSceneWidth){
				return false;
			}
			if(w==info.getnWidth()){
				return true;
			}
			info.setnWidth((short)w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect=new Rect();
			dTable.resetWidth(w);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if (h<0||h>SKSceneManage.getInstance().getCurrentScene().nSceneHeight) {
				return false;
			}
			if(h==info.getnHeight()){
				return true;
			}
			info.setnHeight((short)h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect=new Rect();
			dTable.resetHeigth(h);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		if (info==null) {
			return false;
		}
		
		int color=Color.rgb(r, g, b);
		
		if (color==info.getnTableColor()) {
			return true;
		}
		info.setnTableColor(color);
		dTable.resetBackcolor(color);
		SKSceneManage.getInstance().onRefresh(items);
		return false;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		if (info==null) {
			return false;
		}
		
		int color=Color.rgb(r, g, b);
		
		if (color==info.getnFrameColor()) {
			return true;
		}
		info.setnFrameColor(color);
		dTable.resetLinecolor(color);
		SKSceneManage.getInstance().onRefresh(items);
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO 自动生成的方法存根
		if (v==show) {
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO 自动生成的方法存根
		if (v==touch) {
			return true;
		}
		touch=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemPageUp(int id) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			dTable.turnPage(0);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemPageDown(int id) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			dTable.turnPage(1);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemFlick(int id, boolean v, int time) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemHroll(int id, int w) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			int type=0;
			if (w<0) {
				type=1;
			}
			dTable.moveRank(type,Math.abs(w));
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			int type=0;
			if(h<0){
				type=1;
			}
			dTable.moveRow(type, Math.abs(h));
			return true;
		}
		return false;
	}

	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemText(int id, int lid, String text) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO 自动生成的方法存根
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