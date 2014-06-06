package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.DragTable;
import com.android.Samkoonhmi.graphicsdrawframe.HTitleItem.EnterKeyCallBack;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableLoadInfo;
import com.android.Samkoonhmi.model.alarm.AlarmDataInfo;
import com.android.Samkoonhmi.model.alarm.AlarmHisShowInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKTableRowNum;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.AlarmSaveThread;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TextAlignUtil;

/**
 * 报警历史显示器
 * @author 刘伟江 创建时间 2012-7-14
 */
public class SKHistoryAlarmShow extends SKGraphCmnTouch implements IItem {

	private static final String TAG = "SKHistoryAlarmShow";
	private static final int DELAY=10;
	private static final int LOAD_DATA=11;
	private static final int HANDLER_ADD=12;
	private static final int RESET_VIEW=13;
	// 列数
	private int mRankCount;
	// 报警控件实体对象
	private AlarmHisShowInfo info;
	// 表格
	private DragTable dTable;
	private Context mContext;
	// 控件id
	private int nItemId;
	// 场景id
	private int nSceneId;
	// 刷新包
	private SKItems items;
	// 后台线程任务名称
	private String tTaskName;
	//数据库处理任务名称
	private String tsaveTaskName;
	// 是否可显现
	private boolean show; 
	// 是否可触控
	private boolean touch; 
	// 是否注册显现地址
	private boolean showByAddr;
	// 是否注册触控地址
	private boolean touchByAddr; 
	// 是否受用户权限控件
	private boolean showByUser; 
	// 是否受用户权限控件
	private boolean touchByUser; 
	private boolean flag;
	// 表格标题
	private ArrayList<String> mHList;
	// 页面信息
	private Vector<RowCell> mRowCells;
	// 开始时间
	private long nStartTime=0;
	// 结束时间
	private long nEndTime=0;
	// 总共数据量
	private int nAllCount=0;
	private boolean bAddIng;
	//是否处于加载数据中
	private boolean isLoading;
	//未处理
	private boolean bUntreated;
	private int nTaskId;
	private TableLoadInfo mLoadInfo;
	private boolean bFull;//是否才满
	private int nShowType;//当显示受地址范围限制时，0-表示所有，1-表示部分

	
	public SKHistoryAlarmShow(int itemId, int sceneId, Context context,AlarmHisShowInfo info) {
		this.nItemId = itemId;
		this.nSceneId = sceneId;
		this.mContext = context;
		this.tTaskName="";
		this.tsaveTaskName ="";
		this.flag = false;
		this.info=info;
		this.nShowType=0;
		mLoadInfo=new TableLoadInfo();
		
		Rect rect = new Rect(info.getnLeftTopX(), info.getnLeftTopY(),
				info.getnLeftTopX() + info.getnWidth(),
				info.getnLeftTopY() + info.getnHeight());
		// 刷新信息
		items = new SKItems();
		items.itemId = nItemId;
		items.sceneId = nSceneId;
		items.nZvalue = info.getnZvalue();
		items.nCollidindId = info.getnCollidindId();
		items.rect = rect;
		items.mGraphics=this;
		
		initTableItem();
		
		if (dTable==null) {
			DragTableInfo dInfo = new DragTableInfo();
			dInfo.setnFrameColor(info.getnFrameColor());
			dInfo.setnLineColor(Color.rgb(192, 192, 192));
			dInfo.setnAlpha(255);
			dInfo.setnTableBackcolor(info.getnTableColor());
			
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
			dInfo.setnDataRank((short)mRankCount);
			dInfo.setnWidth(info.getnWidth());
			dInfo.setnHeight(info.getnHeight());
			dInfo.setnLeftTopX(info.getnLeftTopX());
			dInfo.setnLeftTopY(info.getnLeftTopY());
			dInfo.setmRowWidth(info.getmRowWidht());
			dInfo.setmRowHeight(info.getmRowHeight());
			dTable = new DragTable(dInfo, mContext, items, true);
			dTable.setiClickListener(iListener);
			dTable.setiPageTurning(iTurning);
			dTable.init(null);
			dTable.initData(mRowCells, mHList,0);
			dTable.getHTitleItem().setDrawEnterKey(false);
			dTable.setEnterKeyShow(true);
			dTable.getHTitleItem().SetEnterKeyCallBack(titleKeyCallBack);
			dTable.drawTable();
			
		}
		
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

		// 注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmShowInfo().getShowAddrProp(), showCall, true,
						nSceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getmShowInfo().getShowAddrProp(), showCall, false,
						nSceneId);
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

		if (info.isbControl()) {
			SKPlcNoticThread.getInstance().addNoticProp(info.getmControlAddr(),
					contralCall, false, nSceneId);
		}
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

	/**
	 * 初始化表格每一格的宽高
	 */
	private void init() {
		if (info == null) {
			return; 
		}
		this.bAddIng=false;
		this.flag = false;

		// 初始化表格内容
		initTableItem();
		
		// 注册通知
		register();
		//显现
		itemShow();
		//触控
		itemTouch();
		
		sAlarmDate="";
		sClearDate="";
		nTaskId++;
		
		mLoadInfo.nLoadType=0;
		mLoadInfo.nLoadCount=info.getnRowCount();
		
		
		SKSceneManage.getInstance().onRefresh(items);
		AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId ,null);
		
		
	}
	
	private EnterKeyCallBack titleKeyCallBack = new EnterKeyCallBack(){

		@Override
		public void onPress() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLongTouch(int x, int y) {
			// TODO Auto-generated method stub
			int colum = dTable.getTouchColum(x);
			if (colum >= 0 && colum < mHList.size() ) {
				Toast toast = Toast.makeText(ContextUtl.getInstance(), mHList.get(colum), Toast.LENGTH_SHORT);
				int offsetX = dTable.getHTitleRect(colum).centerX();
				int offsetY = dTable.getHTitleRect(colum).bottom;
				toast.setGravity(Gravity.LEFT| Gravity.TOP, offsetX, offsetY);
				toast.show();
				
			}
		}
		
	};

	/**
	 * 初始化表格
	 */
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

		// 标题行,第一列为空
		if (info.isbNumber()) {
			mHList.add("");
			mRankCount++;
		}

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
			if(info.getsDateStrs().size()>SystemInfo.getCurrentLanguageId()){
				date=info.getsDateStrs().get(SystemInfo.getCurrentLanguageId());
			}
			mHList.add(date);
		}

		if (info.isbClearDate()) {
			mRankCount++;
			String cDate="";
			if (info.getsClearDateStrs().size()>SystemInfo.getCurrentLanguageId()) {
				cDate=info.getsClearDateStrs().get(SystemInfo.getCurrentLanguageId());
			}
			mHList.add(cDate);
		}

		if (info.isbClearTime()) {
			mRankCount++;
			String cTime="";
			if (info.getsClearDateStrs().size()>SystemInfo.getCurrentLanguageId()) {
				cTime=info.getsClearTimeStrs().get(SystemInfo.getCurrentLanguageId());
			}
			mHList.add(cTime);
		}

		mRankCount++;
		String message="";
		if(info.getsMessages().size()>SystemInfo.getCurrentLanguageId()){
			message=info.getsMessages().get(SystemInfo.getCurrentLanguageId());
		}
		mHList.add(message);
		
	}
	
	/**
	 * 处理点击事件
	 */
	private SKTableRowNum mTableRowNum=null;
	DragTable.IClickListener iListener=new DragTable.IClickListener() {

		@Override
		public void onLongClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time=0;
			if (nAllCount<info.getnRowCount() ||!touch) {
				return;
			}
			if (mTableRowNum==null||!mTableRowNum.isShow) {
				mTableRowNum=new SKTableRowNum(mContext);
				mTableRowNum.setiOperCall(iOperCall);
				mTableRowNum.showPopWindow(mLoadInfo.nRowIndex,dTable.getShowCount(),240, 180);
			}
		}

		@Override
		public void onDoubleClick(int index, int gid, int aid, int type) {
 			SKSceneManage.getInstance().time=0;
			if (mRowCells==null||mRowCells.size()==0||index==-1  || !touch) {
				return;
			}
			
			boolean result=true;
			if (mRowCells.size()>index) {
				RowCell item=mRowCells.get(index);
				if (item.nClear==0) {
					item.isSetRowColor=true;
					item.nRowColor=info.getnConfirmColor();
					SKSceneManage.getInstance().onRefresh(items);
					
					if (item.gid==gid&&item.aid==aid) {
						AlarmGroup.getInstance().confirmAlarm(gid, aid);
					}
				}else {
					result=false;
				}
			}
			if (result) {
				SKThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_CONFIRM_REFRESH, tTaskName,gid,aid+"");
			}
			
		}

		@Override
		public void onClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time=0;
			if (mRowCells==null||mRowCells.size()==0||index==-1  || !touch) {
				return;
			}
			
			if (mRowCells.size() > index) {
				RowCell item=mRowCells.get(index);
				if (item.nClear > 0 && item.gid==gid && item.aid==aid) {
					//AlarmGroup.getInstance().deleteAlarm(gid, aid);
					showDelDialog(gid, aid);
				}
			}
		}
		
	};
	
	private AlertDialog delDialog;
	private void showDelDialog( final int gid, final int aid){
		dismissDelDialog();
		
		 DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == DialogInterface.BUTTON_POSITIVE) {
					ArrayList<Integer> bean = new ArrayList<Integer>();
					bean.add(gid);
					bean.add(aid);
					AlarmGroup.getInstance().saveAlarmData(4, 1, alarmCall,nTaskId, bean);
				}
				else {
					dismissDelDialog();
				}
				
			}
		};
		
		delDialog =  new AlertDialog.Builder(SKSceneManage.getInstance().getActivity()).create();
		delDialog.setCanceledOnTouchOutside(false);
		delDialog.setMessage(ContextUtl.getInstance().getText(R.string.confirm_operation));
		delDialog.setButton(DialogInterface.BUTTON_POSITIVE, ContextUtl.getInstance().getText(R.string.email_sure), mListener);
		delDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ContextUtl.getInstance().getText(R.string.email_cancel),mListener);
		delDialog.show();
	}
	
	private void dismissDelDialog(){
		if (delDialog != null && delDialog.isShowing()) {
			delDialog.cancel();
		}
		delDialog = null;
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
	 * 设置显示起始行序号
	 */
	SKTableRowNum.IOperCall iOperCall=new SKTableRowNum.IOperCall() {
		
		@Override
		public void onConfirm(int top) {
			if (dTable!=null) {
				dTable.gotoRow(top);
			}
		}
		
		@Override
		public void onCancel() {
			
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
		
		// Log.d(TAG,
		// ".......top:"+top+",site:"+site+",isLoading:"+isLoading+",bFore:"+bFore);
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

		// Log.d(TAG, "type:"+type+",top:"+top);
		mLoadInfo.nLoadType = type;
		mLoadInfo.nRowIndex = top;
		mLoadInfo.nEndIndex = top + info.getnRowCount();
		mLoadInfo.nLoadCount = info.getnRowCount();
		
		AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId ,null);
	}
	
	
	/**
	 * 初始化
	 * @param type-0 初始化, 1滑动, 2点击顶部按钮, 3点击底部按钮
	 */
	private synchronized void initData(ArrayList<AlarmDataInfo> list,int type,int top,int end){
		
		
		int loadNum=0;
		mRowCells.clear();
		if (list != null && list.size() > 0) {
			int id=top;
			if (id < 1) {
				id = 1;
			}
			loadNum=list.size();
			for (int i = 0; i < list.size(); i++) {
				AlarmDataInfo data = list.get(i);
				addRow(data, id++, mRowCells);
			}
		}

		if (dTable != null) {
			if (top+loadNum<info.getnRowCount()) {
				dTable.initData(mRowCells, mHList,0);
			}else{
				dTable.initData(mRowCells, mHList,top);
			}
			
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
					
					AlarmGroup.getInstance()
							.saveAlarmData(0, 1, alarmCall, nTaskId, null);
				}
			}
			
			dTable.updateView(1);
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
		
	}
	
	/**
	 * 添加一行
	 */
	private void addRow(AlarmDataInfo data,int top,Vector<RowCell> rowCells){
		Vector<String> mClounms=new Vector<String>();
		RowCell rowCell=new RowCell();
		rowCell.nClounmCount=mRankCount;
		rowCell.mClounm=mClounms;
		rowCell.gid=data.getnGroupId();
		rowCell.aid=data.getnAlarmIndex();
		rowCell.nClear=data.getnClear();
		rowCell.nAlarmTime=data.getnDateTime();
		rowCell.nRowIndex=top;
				
		int clear=data.getnClear();
		if (clear==0) {
			//未处理
			rowCell.isSetRowColor=false;
		}else if (clear==1) {
			//已经确定
			rowCell.isSetRowColor=true;
			rowCell.nRowColor=info.getnConfirmColor();
		}else if (clear==2) {
			//已经消除
			rowCell.isSetRowColor=true;
			rowCell.nRowColor=info.getnClearColor();
		}
		
		/**
		 * 显示序号
		 */
		if (info.isbNumber()) {
			mClounms.add("CODE");
		}
		
		sClearDate="";
		sAlarmDate="";
		
		getTime(info.geteTimeFormat(), info.geteDateFormat(), data, 1);//报警时间
		getTime(info.geteClearTimeFormat(), info.geteClearDateFormat(), data, 2);//报警消除时间
		
		sClearDate="";
		sAlarmDate="";
		
		//显示报警发生时间
		if(info.isbShowTime()){
			mClounms.add(data.getsTime());
		}
		
		//显示报警发生日期
		if (info.isbShowDate()) {
			mClounms.add(data.getsDate());
		}
		
		//显示报警消除日期
		if (info.isbClearDate()) {
			mClounms.add(data.getsCDate());
		}
		
		//显示报警消除时间
		if (info.isbClearTime()) {
			mClounms.add(data.getsCTime());
		}
		
		mClounms.add(data.getsMessage());
		rowCells.add(rowCell);
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
		SKSceneManage.getInstance().onRefresh(items);
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
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		if (!show||info==null) {
			return false;
		}
		if (dTable != null) {
			return dTable.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public void getDataFromDatabase() {
		
	}

	private void register() {
		
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}
		
		/**
		 * 注册报警登录
		 */
		AlarmGroup.getInstance().getBinder().onRegister(alarmCall);
	}

	/**
	 * 多语音切换
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback() {
		
		@Override
		public void onLanguageChange(int languageId) {
			
			mLoadInfo.nLoadType=0;
			mLoadInfo.nLoadCount=info.getnRowCount();
			
			initTableItem();
			AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId, null);
		}
	};

	/**
	 * 报警产生
	 */
	private int nCacheNum=0;
	private void addAlarm(ArrayList<AlarmDataInfo> list){
		if (info==null||!flag) {
			return;
		}
		//long start=System.currentTimeMillis();
		
		
		if (nAllCount<=info.getnRowCount()) {
			bLast=true;
		}
		
		if (bLast&&!isLoading) {
			//删除数据
			int index=0;
			if (list.size()+mRowCells.size()>info.getnRowCount()) {
				if(list.size()>info.getnRowCount()){
					index=list.size()-info.getnRowCount();
					mRowCells.clear();
				}else {
					int size=list.size()+mRowCells.size()-info.getnRowCount();
					for (int i = 0; i < size; i++) {
						mRowCells.remove(0);
					}
				}
			}
			
			if (!bFull) {
				nAllCount+=index;
				if(nCacheNum>0){
					mRowCells.clear();
					dTable.updateView(1);
				}
				nCacheNum=0;
			}
			
			for (int i = index; i < list.size(); i++) {
				if (!bFull) {
					nAllCount++;
				}
				AlarmDataInfo dinfo=list.get(i);
				Vector<String> mClounms = new Vector<String>();
				RowCell rowCell = new RowCell();
				rowCell.nRowIndex = nAllCount;
				rowCell.nClounmCount = mRankCount;
				rowCell.mClounm = mClounms;
				rowCell.gid = dinfo.getnGroupId();
				rowCell.aid = dinfo.getnAlarmIndex();
				rowCell.nClear=0;
				rowCell.nAlarmTime=dinfo.getnDateTime();

				getTime(info.geteTimeFormat(), info.geteDateFormat(), dinfo, 1);
				dinfo.setsCDate("");
				dinfo.setsCTime("");
				
				if (info.isbNumber()) {
					mClounms.add("CODE");
				}
				if (info.isbShowTime()) {
					mClounms.add(dinfo.getsTime());
				}
				if (info.isbShowDate()) {
					mClounms.add(dinfo.getsDate());
				}
				if (info.isbClearDate()) {
					mClounms.add(dinfo.getsCDate());
				}
				if (info.isbClearTime()) {
					mClounms.add(dinfo.getsCTime());
				}
				mClounms.add(dinfo.getsMessage());
				mRowCells.add(rowCell);
				
			}
			
			if (dTable!=null) {
				
				bFore=false;
				bLast=true;
				if (nAllCount>info.getnRowCount()) {
					mLoadInfo.nRowIndex=nAllCount-info.getnRowCount()+1;
				}else {
					mLoadInfo.nRowIndex=1;
				}
				mLoadInfo.nEndIndex=nAllCount;
				
				dTable.updateDataNum(nAllCount);
				dTable.moveToBottom();
				
				
				if (dTable.isShowBar()&&mRowCells.size()>=info.getnRowCount()) {
					dTable.updateView(2);
				}else {
					dTable.updateView(1);
				}
				
				if (nAllCount<info.getnRowCount()) {
					dTable.initData(mRowCells, mHList,0);
				}else{
					dTable.initData(mRowCells, mHList,nAllCount-mRowCells.size()+1);
				}
				SKSceneManage.getInstance().onRefresh(items);
			}
		}else {
			if (!bFull) {
				nCacheNum+=1;
				nAllCount++;
				dTable.updateDataNum(nAllCount);
			}else{
				dTable.updateDataNum(nAllCount);
			}
		}
		
		bAddIng=false;
		
	}
	
	/**
	 * 报警消除
	 */
	private String mDateTime[]=new String[]{"",""};
	private void alarmClear(ArrayList<AlarmDataInfo> list){
		if (list == null || list.size() == 0 || mRowCells == null) {
			return;
		}

		if(mRowCells.size() == 0){
			getSynList().addAll(list);
		}
	
		//long start=System.currentTimeMillis();
		for (int j = list.size() - 1; j >= 0; j--) {
			AlarmDataInfo ainfo = list.get(j);
			if (ainfo == null) {
				break;
			}
			getTime(info.geteClearTimeFormat(), info.geteClearDateFormat(),
					null, 2);

			for (int i = 0; i < mRowCells.size(); i++) {
				RowCell item = mRowCells.get(i);
				if (ainfo.getnGroupId() == item.gid) {
					// 报警组
					if (ainfo.getnAlarmIndex() == item.aid&&item.nClear!=2) {
						// 组每一个报警
						RowCell cell = mRowCells.get(i);
						cell.isSetRowColor = true;
						cell.nRowColor = info.getnClearColor();
						cell.nClear = 2;
						int index = 0;
						if (info.isbNumber()) {
							index++;
						}
						if (info.isbShowTime()) {
							index++;
						}
						if (info.isbShowDate()) {
							index++;
						}
						if (info.isbClearDate()) {
							if (cell.mClounm != null) {
								if (cell.mClounm.size() > index) {
									cell.mClounm.set(index, mDateTime[0]);
								}
							}
							index++;
						}
						if (info.isbClearTime()) {
							if (cell.mClounm != null) {
								if (cell.mClounm.size() > index) {
									cell.mClounm.set(index, mDateTime[1]);
								}
							}
						}
						break;
					}
				}
			}
		}
		
		//Log.d("SKScene", "clear time:"+(System.currentTimeMillis()-start));
		if (dTable!=null) {
			dTable.updateData(mRowCells);
			SKSceneManage.getInstance().onRefresh(items);
		}
		
	}
	
	/**
	 * 某个报警确定
	 */
	private void alarmConfirm(Object msg){
			String[] temp=(String[])msg;
			if (temp == null || temp.length <= 0 ) {
				return ;
			}
			if (temp.length>1) {
				if (mRowCells!=null) {
					for (int i = 0; i < mRowCells.size(); i++) {
						RowCell item=mRowCells.get(i);
						if (temp[0].equals(String.valueOf(item.gid)) && item.nClear == 0) {
							//报警组
							if (temp[1].equals(String.valueOf(item.aid))) {
								//组每一个报警
								RowCell cell=mRowCells.get(i);
								cell.isSetRowColor=true;
								cell.nRowColor=info.getnConfirmColor();
								cell.nClear=1;
								if (dTable!=null) {
									dTable.updateData(mRowCells);
									SKSceneManage.getInstance().onRefresh(items);
								}
							}
						}
					}
				}
			}
	}
	
	/**
	 * 所有报警确定
	 */
	private void alarmConfirm(){
		if (mRowCells==null) {
			return;
		}
		for (int i = 0; i < mRowCells.size(); i++) {
			RowCell item=mRowCells.get(i);
			if (item.nClear==0) {
				item.nClear=1;
				item.isSetRowColor=true;
				item.nRowColor=info.getnConfirmColor();
			}
		}
		if (dTable!=null) {
			dTable.updateData(mRowCells);
			SKSceneManage.getInstance().onRefresh(items);
		}
	}
	
	/**
	 * 清除报警
	 */
	private void deleteHistoryAlarm(){
		//Log.d(TAG, "deleteHistoryAlarm......");
		mLoadInfo.nLoadType=0;
		mLoadInfo.nLoadCount=info.getnRowCount();
		
		SKSceneManage.getInstance().onRefresh(items);
		AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId, null);
	}

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
	 * 显隐地址回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}
	};
	
	
	/**
	 * 报警时间范围
	 */
	private Vector<Integer> mIData = null;
	SKPlcNoticThread.IPlcNoticCallBack contralCall=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			boolean result;
			if (nStatusValue==null||nStatusValue.size()==0) {
				return;
			}
			if (mIData==null) {
				mIData=new Vector<Integer>();
			}else {
				mIData.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,
					mIData);
			if (!result && mIData.size() == 0) {
				return;
			}
			doTimeScope(mIData);
		}
	};
	/**
	 * @param list
	 * @return
	 */
	private ArrayList<AlarmDataInfo> getSaveAlarmList(ArrayList<AlarmDataInfo> list){
		ArrayList<AlarmDataInfo> savelist = new ArrayList<AlarmDataInfo>();;
		
		if (list != null && list.size() >0) {
			for(int i = 0; i < list.size(); i++){
				AlarmDataInfo info = list.get(i);
				
				if (info != null && info.getbAddtoDb()) {
					savelist.add(info);
				}
			}
		}
		
		return savelist;
	}
	
	
	//首次启动时，如果有报警消除， 报警消除可能会在界面刷新前调用，导致报警显示状态错误,用此数组进行同步
	private ArrayList<AlarmDataInfo> mSynList = new ArrayList<AlarmDataInfo>();
	private Object synObject = new Object();
	private ArrayList<AlarmDataInfo> getSynList(){
		synchronized (synObject) {
			return mSynList;
		}
	}
	
	/**
	 * 注册报警登录
	 */
	AlarmGroup.IAlarmCallback alarmCall=new AlarmGroup.IAlarmCallback() {
		
		@Override
		public void onClose(ArrayList<AlarmDataInfo> templist) {
			//报警消除
			
			ArrayList<AlarmDataInfo> list = getSaveAlarmList(templist);
			
			if (list==null||list.size()==0) {
				return;
			}	
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder()
			.onTask(MODULE.ALARM, TASK.ALARM_CLEAR_REFRESH, tTaskName,list);
		}

		@Override
		public void onAlarm(ArrayList<AlarmDataInfo> templist,boolean full) {
			//报警产生
			ArrayList<AlarmDataInfo> list = getSaveAlarmList(templist);
			if (list==null||list.size()==0) {
				return;
			}
			if (info.isbControl()) {
				if(nShowType==1){
					long when=System.currentTimeMillis();
					if (when<nStartTime||when>nEndTime) {
						//Log.d(TAG, "不在范围内...");
						return;
					}
				}
			}
			bFull=full;
			
			if (bAddIng) {
				nCacheNum+=list.size();
				dTable.updateDataNum(nAllCount+nCacheNum);
				Message msg=new Message();
				msg.what=HANDLER_ADD;
				msg.obj=list;
				mHandler.removeMessages(HANDLER_ADD);
				mHandler.sendMessageDelayed(msg, 200);
			}
			bAddIng=true;
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder()
			.onTask(MODULE.ALARM, TASK.ALARM_ADD_REFRESH, tTaskName,list);
			
		}
		
		@Override
		public void onClear(ArrayList<Integer> delList) {
			//清除报警
			if (delList == null && delList.size() == 0) {
				return ;
			}
			
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance()
					.getBinder()
					.onTask(MODULE.ALARM, TASK.HISTORY_REFRESH, tTaskName);
		}
		
		@Override
		public void onClearHistory(ArrayList<Integer> hisList) {
			//清除历史报警
			if (hisList == null || hisList.size() == 0) {
				return ;
			}
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder()
			.onTask(MODULE.ALARM, TASK.HISTORY_REFRESH, tTaskName);
		
		}

		@Override
		public void onConfirm(ArrayList<Integer> confrimList) {
			//报警确定
			if (tTaskName.equals("")) {
				tTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance()
					.getBinder()
					.onTask(MODULE.ALARM, TASK.HISTORY_REFRESH, tTaskName);
		}

		@Override
		public void onConfirm(int gid, int aid) {
			
		}

		@Override
		public void update(int taskId) {

			if (!flag) {
				if (taskId != nTaskId) {
					return;
				}
			}
			
			if (tsaveTaskName.equals("")) {
				tsaveTaskName = AlarmSaveThread.getInstance().getBinder().onRegister(alarmCallback);
			}
			AlarmSaveThread.getInstance().getBinder().onTask(MODULE.ALARM, TASK.ALARM_HIS_READ, tsaveTaskName);
		}

		@Override
		public void onDateChange() {
			sAlarmDate="";
			sClearDate="";
		}

	};
	
	/**
	 * 后台线程，处理实时报警信息
	 */
	SKThread.ICallback tCallback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			/**
			 * 报警消除
			 */
			switch (taskId) {
			case TASK.ALARM_CLEAR_REFRESH:
			{
				//报警消除
				ArrayList<AlarmDataInfo> list=(ArrayList<AlarmDataInfo>)msg;
				if (list==null||list.size()==0) {
					return;
				}
				alarmClear(list);
				break;
			}
			case TASK.ALARM_CONFIRM_REFRESH://单个报警
				//报警确定
				alarmConfirm(msg);
				break;
			case TASK.ALARM_ADD_REFRESH://产生报警更新界面
			{
				ArrayList<AlarmDataInfo> list=(ArrayList<AlarmDataInfo>)msg;
				if (list==null||list.size()==0) {
					return;
				}
				addAlarm(list);
				break;
			}
				
			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			/**
			 * 控件属性
			 */
			switch (taskId) {
			case TASK.HISTORY_REFRESH:
				//删除历史数据，更新界面
				deleteHistoryAlarm();
				break;
			}
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			if (taskId== TASK.ALARM_CONFIRM_REFRESH) {//分组报警的情况
				//报警确定，更新界面
				alarmConfirm();
			}
		}
	};
	
	AlarmSaveThread.ICallback  alarmCallback =  new AlarmSaveThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			// TODO Auto-generated method stub
		if (taskId == TASK.ALARM_HIS_READ) {
			if (mLoadInfo.nLoadType==0) {
				flag=true;
				if (nShowType==1) {
					nAllCount=DBTool.getInstance().getmAlarmBiz().selectAlarmCount(-1,1,nStartTime,nEndTime);
				}else {
					nAllCount=DBTool.getInstance().getmAlarmBiz().selectAlarmCount(-1,0,0,0);
				}
				//Log.d(TAG, ".......nAllCount:"+nAllCount);
				if (nAllCount>info.getnRowCount()) {
					mLoadInfo.nRowIndex=nAllCount-info.getnRowCount()+1;
				}else {
					mLoadInfo.nRowIndex=1;
				}
				mLoadInfo.nEndIndex=nAllCount;
			}
			
			ArrayList<AlarmDataInfo> list=null;
			list=DBTool.getInstance().getmAlarmBiz().selectAllMsg(nStartTime,nEndTime,mLoadInfo.nRowIndex,mLoadInfo.nLoadCount);	
			ArrayList<AlarmDataInfo> synList = getSynList();
			if (list != null) {
				for(int i = 0; i < list.size(); i++){
					AlarmDataInfo bean = list.get(i);
					for(int j = 0; j <synList.size(); j++){
						AlarmDataInfo tempBean = synList.get(j);
						if (bean.getnGroupId() == tempBean.getnGroupId() && bean.getnAlarmIndex() == tempBean.getnAlarmIndex()) {
							bean.setnClear((short)2);
							break;
						}
					}
				}
				synList.clear();
			}
			initData(list,mLoadInfo.nLoadType,mLoadInfo.nRowIndex,mLoadInfo.nEndIndex);//数据查询
		}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void setDataToDatabase() {

	}

	@Override
	public void realseMemeory() {
		
		dismissDelDialog();
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onDestroy(lCallback);
		}
		AlarmGroup.getInstance().getBinder().onDestroy(alarmCall);
		SKThread.getInstance().getBinder().onDestroy(tCallback, tTaskName);
		tTaskName = "";
		sAlarmDate="";
		sClearDate="";
		bUntreated=false;
		flag=false;
		if (mRowCells!=null) {
			mRowCells.clear();
		}
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DELAY:
				SKSceneManage.getInstance().onRefresh(items);
				break;
			case LOAD_DATA:
				AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId, null);
				break;
			case HANDLER_ADD:
				ArrayList<AlarmDataInfo> list=(ArrayList<AlarmDataInfo>)msg.obj;
				if (list==null||list.size()==0) {
					return;
				}
				addAlarm(list);
				break;
			case RESET_VIEW:
				if (dTable!=null) {
					//如果用户没有触摸屏幕，把界面移动到最后页面
					if (nAllCount>info.getnRowCount()) {
						dTable.moveToBottom();
					}
				}
				break;
			}
		}

	};
	
	
	/**
	 * 报警查询时间范围
	 */
	private void doTimeScope(Vector<Integer> data){
		if (data.size()<13) {
			return;
		}
		int[] time=new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
		
		int type=0;
		boolean result=true;
		for (int i = 0; i < data.size(); i++) {
			int temp=PlcRegCmnStcTools.intToBCD_16(data.get(i));
			switch (i) {
			case 0:
				//0-显示所有数据,1-显示部分数据
				if (temp>1||temp<0) {
					result=false;
				}
				type=temp;
				break;
			case 1:
				//开始时间(年)
				if (temp<1970) {
					result=false;
				}
				time[0]=temp;
				break;
			case 2:
				//开始时间(月)
				if (temp<=0||temp>12) {
					result=false;
				}
				time[1]=temp;
				break;
			case 3:
				//开始时间(日)
				if (temp<=0||temp>31) {
					result=false;
				}
				time[2]=temp;
				break;
			case 4:
				//开始时间(时)
				if (temp<0||temp>24) {
					result=false;
				}
				time[3]=temp;
				break;
			case 5:
				//开始时间(分)
				if (temp<0||temp>60) {
					result=false;
				}
				time[4]=temp;
				break;
			case 6:
				//开始时间(秒)
				if (temp<0||temp>60) {
					result=false;
				}
				time[5]=temp;
				break;
			case 7:
				//结束时间(年)
				if (temp<1970) {
					result=false;
				}
				time[6]=temp;
				break;
			case 8:
				//结束时间(月)
				if (temp<=0||temp>12) {
					result=false;
				}
				time[7]=temp;
				break;
			case 9:
				//结束时间(日)
				if (temp<=0||temp>31) {
					result=false;
				}
				time[8]=temp;
				break;
			case 10:
				//结束时间(时)
				if (temp<0||temp>24) {
					result=false;
				}
				time[9]=temp;
				break;
			case 11:
				//结束时间(分)
				if (temp<0||temp>60) {
					result=false;
				}
				time[10]=temp;
				break;
			case 12:
				//结束时间(秒)
				if (temp<0||temp>60) {
					result=false;
				}
				time[11]=temp;
				break;
			}
		}
		
		if (result) {
			nShowType=type;
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR,time[0]);
			c.set(Calendar.MONTH,time[1] - 1);
			c.set(Calendar.DAY_OF_MONTH,time[2]);
			c.set(Calendar.HOUR_OF_DAY,time[3]);
			c.set(Calendar.MINUTE, time[4]);
			c.set(Calendar.SECOND, time[5]);
			
			long when = c.getTimeInMillis();
			if (when < 0) {
				return;
			}
			if (nShowType==1) {
				nStartTime=when;
			}else {
				nStartTime=0;
			}
			
			c.set(Calendar.YEAR,time[6]);
			c.set(Calendar.MONTH,time[7] - 1);
			c.set(Calendar.DAY_OF_MONTH,time[8]);
			c.set(Calendar.HOUR_OF_DAY,time[9]);
			c.set(Calendar.MINUTE, time[10]);
			c.set(Calendar.SECOND, time[11]);
			
			when = c.getTimeInMillis();
			if (when < 0) {
				return;
			}
			
			if (nShowType==1) {
				nEndTime=when;
			}else {
				nEndTime=0;
			}
			
			//Log.d(TAG, "change nStartTime:"+nStartTime+",nEndTime:"+nEndTime);
			mLoadInfo.nLoadType=0;
			mLoadInfo.nLoadCount=info.getnRowCount();
			AlarmGroup.getInstance().saveAlarmData(0, 1, alarmCall,nTaskId, null);
		}
	}
	
	/**
	 *格式化日期和时间
	 */
	private String sClearDate="";
	private String sAlarmDate="";
	private void getTime(TIME_FORMAT time,DATE_FORMAT date,AlarmDataInfo info,int type){
		
		if (type==1) 
		{
			Date mDate=new Date(info.getnDateTime());
			if (sAlarmDate.equals("")) 
			{
				sAlarmDate = DateStringUtil.convertDate(date, mDate);
			}
			String times = DateStringUtil.converTime(time, mDate);
			if (sAlarmDate==null||times==null)
			{
				info.setsTime("");
				info.setsDate("");
			}
			else
			{
				info.setsTime(times);
				info.setsDate(sAlarmDate);
			}
		}
		else if (type==2)
		{
			if (info!=null) 
			{
				if (info.getnClearDT()==0)
				{
					info.setsCTime("");
					info.setsCDate("");
				}
				else
				{
					Date mDate=new Date(info.getnClearDT());
					if (sClearDate.equals("")) 
					{
						sClearDate = DateStringUtil.convertDate(date, mDate);
					}
					String times = DateStringUtil.converTime(time, mDate);
					
					if (sClearDate==null||times==null) 
					{
						info.setsCTime("");
						info.setsCDate("");
					}
					else 
					{
						info.setsCTime(times);
						info.setsCDate(sClearDate);
					}
				}
			}
			else
			{
				Date mDate=new Date(System.currentTimeMillis());
				if (sClearDate.equals("")) 
				{
					sClearDate= DateStringUtil.convertDate(date, mDate);
				}
				String times = DateStringUtil.converTime(time, mDate);
				mDateTime[0]=sClearDate;
				mDateTime[1]=times;
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
		return true;
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
		return true;
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
