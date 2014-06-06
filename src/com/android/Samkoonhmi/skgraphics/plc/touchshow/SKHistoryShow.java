package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.graphicsdrawframe.DragTable;
import com.android.Samkoonhmi.graphicsdrawframe.HTitleItem.EnterKeyCallBack;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.HistoryShowInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableLoadInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skenum.TIME_FORMAT;
import com.android.Samkoonhmi.skglobalcmn.DataCollect;
import com.android.Samkoonhmi.skglobalcmn.DataCollect.HistoryDataProp;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalBackThread;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.DateTimeSetting;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKProgress.ShowStyle;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSearchDialog;
import com.android.Samkoonhmi.skwindow.SKTableRowNum;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TextAlignUtil;

public class SKHistoryShow extends SKGraphCmnTouch implements IItem{

	private static final String TAG="SKHistoryShow";
	private static final int SHOW_DIALOG=10;
	private static final int HIDE_DIALOG=11;
	private static final int OUT_TIME=12;
	private static final int RESET_VIEW=13;
	private boolean flag;
	private int nSceneId;//场景id
	private int nItemId;//控件id
	private boolean show; // 是否可显现
	private boolean touch; // 是否可触控
	private boolean showByAddr; // 是否注册显现地址
	private boolean touchByAddr; // 是否注册触控地址
	private boolean showByUser; // 是否受用户权限控件
	private boolean touchByUser; // 是否受用户权限控件
	private SKItems items;//刷新信息包
	private String sTaskName;//后台线程任务名称
	private HistoryShowInfo info;//历史数据显示器对象
	private Context mContext;
	private DragTable dTable;//表格
	private DragTableInfo dInfo;//表格信息
	private short nColum;//实际列数
	private short nShowColum;//显示列数
	private short nRow;//显示行数
	private ArrayList<String> mHList;//标题栏信息
	private Vector<RowCell> mRowCells;//显示文本信息
	private Vector<RowCell> mOldRowCells;//记录文本信息
	private long nStartTime=0;//开始时间
	private long nEndTime=0;//结束时间
	private int nAllCount=0;//数据总数
	private int nOldAllCount=0;//记录数据总数
    private Vector<Integer> mCodeList;//显示通道号集合
	private boolean isLoading;//是否已经读取数据库信息
	private boolean refresh;
	private UIHandler mHandler;
	private boolean bUntreated;//是否由未加载信息
	private int nTop;//滑动块所处顶部行id
	private TableLoadInfo mLoadInfo;//加载数据信息包
	//about search
	private boolean isSearchable=false;//能否搜索
	private String mSearchString=null;//搜索内容
	private boolean isSearching=false;			//是否正在显示搜索结果
	private int nSearchItem;				//点击列
	private int nSearchIndex;				//搜索列
	private boolean bSearchId;				//搜索序号
	private int nSearchIdNumber;			//搜索序号
	
	
	
	public SKHistoryShow(int sceneId,int itemId,Context context,HistoryShowInfo info){
		this.nItemId=itemId;
		this.nSceneId=sceneId;
		this.sTaskName="";
		this.mContext=context;
		
		//默认可以搜索
		this.isSearchable = true;
		
		mLoadInfo=new TableLoadInfo();
		this.info=info;
		mHandler=new UIHandler(mContext.getMainLooper());
		mOldRowCells = new Vector<RowCell>();
		
		if (info!=null) {
			items = new SKItems();
			Rect rect = new Rect(info.getnLeftTopX(),
					info.getnLeftTopY(), info.getnLeftTopX()
							+ info.getnWidth(), info.getnLeftTopY()
							+ info.getnHeight());
			items.itemId = nItemId;
			items.sceneId = nSceneId;
			items.nZvalue = info.getnZvalue();
			items.nCollidindId = info.getnCollidindId();
			items.rect = rect;
			items.mGraphics=this;
			
			//初始化没一小格信息
			initTbaleItem();
			
			nRow=(short)(info.getnLine()+1);
			
			if (dInfo==null) {
				
				dInfo = new DragTableInfo();
				dInfo.setnFrameColor(info.getnFrameColor());
				dInfo.setnLineColor(info.getnFrameColor());
				dInfo.setnTableBackcolor(info.getnBackcolor());
				
				dInfo.setnTitleFontColor(info.getnTitleFontColor());
				if (info.getmTitleFontSizes().size()>SystemInfo.getCurrentLanguageId()) {
					int temp=info.getmTitleFontSizes().get(SystemInfo.getCurrentLanguageId());
					dInfo.setnTitleFontSize((short)temp);
				}else {
					dInfo.setnTitleFontSize((short)10);
				}
				dInfo.setnTitleBackcolor(info.getnTitleBackColor());
				if (info.getmTitleFontType().size()>SystemInfo.getCurrentLanguageId()) {
					dInfo.setmHTypeFace(TextAlignUtil.getTypeFace(info.getmTitleFontType().get(SystemInfo.getCurrentLanguageId())));
				}else {
					dInfo.setmHTypeFace(Typeface.DEFAULT);
				}
				dInfo.setmVTypeFace(Typeface.DEFAULT);
				dInfo.setmTypeFace(Typeface.DEFAULT);
				
				dInfo.setnVTitleFontColor(info.getnTextFontColor());
				dInfo.setnVTitleFontSize((short)info.getnTextFontSize());
				dInfo.setnVTitleBackcolor(info.getnBackcolor());
				
				dInfo.setnTextFontColor(info.getnTextFontColor());
				dInfo.setnTextFontSize((short)info.getnTextFontSize());
				dInfo.setnRow(nRow);
				dInfo.setShowNum(info.isbShowCode());
				nShowColum=nColum;
				int show=0;
				if (info.isbShowCode()) {
					show++;
				}
				if (info.isbShowDate()) {
					show++;
				}
				if (info.isbShowTime()) {
					show++;
				}
				
				if (nColum>4+show) {
					nShowColum=(short)(4+show);
				}
				
				dInfo.setnRank(nShowColum);
				dInfo.setnDataRank(nColum);
				dInfo.setnAlpha(info.getnAlpha());
				dInfo.setnWidth(info.getnWidth());
				dInfo.setnAllColumWidth(info.getnWidth());
				dInfo.setnHeight(info.getnHeight());
				dInfo.setnLeftTopX(info.getnLeftTopX());
				dInfo.setnLeftTopY(info.getnLeftTopY());
				if (nColum>nShowColum) {
					if (info.getmRowWidht()!=null) {
						ArrayList<Double> temp=info.getmRowWidht();
						double colum=(double)info.getnWidth()/nShowColum;
						temp.set(info.getmRowWidht().size()-1, colum);
						double width=0;
						for (int i = 0; i < nColum; i++) {
							if (i<info.getmRowWidht().size()) {
								width+=info.getmRowWidht().get(i);
							}else {
								width+=colum;
							}
						}
						double nwidth=width-info.getnWidth();
						dInfo.setnAllColumWidth((int)width);
						if (nwidth>0) {
							dInfo.setnDiffer((int)nwidth);
						}else {
							dInfo.setnDiffer(0);
						}
						
					}
				}
				
				dInfo.setmRowWidth(info.getmRowWidht());
				dInfo.setmRowHeight(info.getmRowHeight());
				dTable = new DragTable(dInfo, mContext, items,true);
				dTable.setiPageTurning(iTurning);
				dTable.setiClickListener(clickListener);
				dInfo.setnAllCount(nAllCount);
				dTable.init(null);
				dTable.updateDataNum(nAllCount);
				dTable.initData(mRowCells, mHList,0);
				dTable.drawTable();
			}else{
				dTable.updateDataNum(nOldAllCount);
				dTable.moveToBottom();
				if(nOldAllCount<info.getnLine()){
					dTable.initData(mOldRowCells, mHList,0);
				}else{
					dTable.initData(mOldRowCells, mHList,nOldAllCount-mOldRowCells.size()+1);
				}
				dInfo.setnAllCount(nOldAllCount);
			}
			
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
							true,nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(), showCall,
							false,nSceneId);
				}

			}

			// 注册触控地址
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
			
			//注册地址控制
			if(info.isbControl()){
				SKPlcNoticThread.getInstance().addNoticProp(info.getmControlAddr(), contralCall, false,nSceneId);
			}
		}
	}
	
	@Override
	public void getDataFromDatabase() {
		
	}

	@Override
	public void setDataToDatabase() {
		
	}

	@Override
	public void initGraphics() {
		init();
	}

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
		sTaskName="";
		DataCollect.getInstance().destoryDataCollectCallback(dataCallBack);
		SKThread.getInstance().getBinder().onDestroy(tCallback, sTaskName);
		
		isLoading=false;
		refresh=false;
		if (mHandler!=null) {
			mHandler.sendEmptyMessage(HIDE_DIALOG);
		}
		
		if(mSKSearchDialog !=null){
			mSKSearchDialog.hidePopWindow();
			
		}
		
		if(!(mRowCells==null||mRowCells.isEmpty())){
			mOldRowCells.clear();
			if(mRowCells != null){
				mOldRowCells.addAll(mRowCells);
				nOldAllCount = nAllCount;
			}
		}
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		float x=event.getX();
		float y=event.getY();
		
		if (info==null) {
			return false;
		}
		if (info.getnLeftTopX()>x||info.getnLeftTopY()>y
				||(info.getnLeftTopX()+info.getnWidth())<x
				||(info.getnLeftTopY()+info.getnHeight())<y) {
			return false;
		}
		if (show&&touch) {
			if (dTable!=null) {
				dTable.onTouchEvent(event);
			}
		}
		
		return true;
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
	 * 初始化
	 */
	private void init(){
		if (info == null) {
			return;
		}
		this.flag=false;
		this.isLoading=false;
		this.nCacheNum=0;
		this.bFore=false;
		this.bLast=false;
		
		getUserInfo();//获取用户设置

		//初始化没一小格信息
		initTbaleItem();
		
		mLoadInfo.nLoadType=0;
		mLoadInfo.nLoadCount=info.getnLine();
		
		// 注册通知
		register();
		
		itemShow();
		itemTouch();
		
		SKSceneManage.getInstance().onRefresh(items);
		
		refresh=true;
		
		//获取历史数据
		SkGlobalBackThread.getInstance().getGlobalBackHandler()
		.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, iReadCallback).sendToTarget();
	}
	
	/**
	 * 初始化表格每一小项
	 */
	private void initTbaleItem(){
		if (mRowCells==null) {
			mRowCells=new Vector<RowCell>();
		}else {
			mRowCells.clear();
		}
		
		if (mCodeList==null) {
			mCodeList=new Vector<Integer>();
		}else {
			mCodeList.clear();
		}
		
		setTitle();
		
	}
	
	/**
	 * 设置标题
	 */
	private void setTitle(){
		
		if(mHList==null){
			mHList=new ArrayList<String>();
		}else {
			mHList.clear();
		}
		
		nColum=0;
		//显现序号
		if (info.isbShowCode()) {
			if (info.getmTitleNum().size()>SystemInfo.getCurrentLanguageId()) {
				mHList.add(info.getmTitleNum().get(SystemInfo.getCurrentLanguageId()));
			}else {
				mHList.add("");
			}
			nColum++;
		}
		
		//显示时间
		if (info.isbShowTime()) {
			if (info.getmTitleTimeNames().size()>SystemInfo.getCurrentLanguageId()) {
				mHList.add(info.getmTitleTimeNames().get(SystemInfo.getCurrentLanguageId()));
			}else {
				mHList.add("");
			}
			nColum++;
		}
		
		//显示日期
		if (info.isbShowDate()) {
			if (info.getmTitleDateNames().size()>SystemInfo.getCurrentLanguageId()) {
				mHList.add(info.getmTitleDateNames().get(SystemInfo.getCurrentLanguageId()));
			}else {
				mHList.add("");
			}
			nColum++;
		}
		
		//数据列
		mCodeList.clear();
		if (info.getmDataList()!=null) {
			for (int i = 0; i < info.getmDataList().size(); i++) {
				if (info.getmDataList().get(i).isbShow()) {
					mCodeList.add(info.getmDataList().get(i).getnCode());
					if (info.getmDataList().get(i).getmTitleDataName().size()>SystemInfo.getCurrentLanguageId()) {
						mHList.add(info.getmDataList().get(i).getmTitleDataName().get(SystemInfo.getCurrentLanguageId()));
					}else {
						mHList.add("");
					}
					nColum++;
				}
			}
		}
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

	
	ArrayList<ArrayList<String>> dList=new ArrayList<ArrayList<String>>();
	
	/**
	 * 后台线程回调
	 */
	SKThread.ICallback tCallback = new SKThread.ICallback() {

		@Override
		public void onUpdate(Object msg, int taskId) {
			//显示搜索结果的情况下不刷新
			if(isSearchable){
				if(isSearching){
					return;
				}
			}
			
			switch (taskId) {
			case TASK.HISTORY_ADD:
				//新增数据
				HistoryDataProp data=(HistoryDataProp)msg;
				if (data==null||data.nDataList==null) {
					return;
				}
				doData(data);
				break;
			}
		}

		@Override
		public void onUpdate(int msg, int taskId) {
			//显示搜索结果的情况下不刷新
			if(isSearchable){
				if(isSearching){
					return;
				}
			}
		}

		@Override
		public void onUpdate(String msg, int taskId) {
			//显示搜索结果的情况下不刷新
			if(isSearchable){
				if(isSearching){
					return;	
				}
			}
		}
	};
	
	/**
	 * 注册通知
	 */
	private void register() {
		
		//注册语言切换通知
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}
		
		//注册采集通知
		Vector<Integer> gVector=new Vector<Integer>();
		gVector.add(info.getnGroupId());
		DataCollect.getInstance().addNoticProp(2, gVector, dataCallBack);
	}
	
	/**
	 * 历史数据时间范围
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
	 * 历史数据查询时间范围
	 */
	private void doTimeScope(Vector<Integer> data) {
		boolean bNoStart=false,bNoEnd=false;
		if (data.size()<13) {
			return;
		}
		
		int[] time=new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
		//处理获得的数据
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
				if(temp==0){
					bNoStart=true;
				}else if (temp<1970) {
					result=false;
				}else{
					time[0]=temp;
				}
				break;
			case 2:
				//开始时间(月)
				if(temp==0&&bNoStart){
					
				}else if (temp<=0||temp>12||bNoStart) {
					result=false;
				}else{
					time[1]=temp;
				}
				break;
			case 3:
				//开始时间(日)
				if(temp==0&&bNoStart){
					
				}else if (temp<=0||temp>31||bNoStart) {
					result=false;
				}else {
					time[2]=temp;
				}
				break;
			case 4:
				//开始时间(时)
				if(temp==0&&bNoStart){
					
				}else if (temp<0||temp>24||bNoStart) {
					result=false;
				}else{
					time[3]=temp;
				}
				break;
			case 5:
				//开始时间(分)
				if(temp==0&&bNoStart){
					
				}else if (temp<0||temp>60||bNoStart) {
					result=false;
				}else {
					time[4]=temp;
				}
				break;
			case 6:
				//开始时间(秒)
				if(temp==0&&bNoStart){
					
				}else if (temp<0||temp>60||bNoStart) {
					result=false;
				}else{
					time[5]=temp;
				}
				break;
			case 7:
				//结束时间(年)
				if(temp==0){
					bNoEnd=true;
				}else if (temp<1970) {
					result=false;
				}else{
					time[6]=temp;
				}
				break;
			case 8:
				//结束时间(月)
				if(temp == 0&&bNoEnd){
					
				}else if (temp<=0||temp>12||bNoEnd) {
					result=false;
				}else{
					time[7]=temp;
				}
				break;
			case 9:
				//结束时间(日)
				if(temp==0&&bNoEnd){
					
				}else if (temp<=0||temp>31||bNoEnd) {
					result=false;
				}else{
					time[8]=temp;
				}
				break;
			case 10:
				//结束时间(时)
				if(temp==0&&bNoEnd){
					
				}else if (temp<0||temp>24||bNoEnd) {
					result=false;
				}else{
					time[9]=temp;
				}
				break;
			case 11:
				//结束时间(分)
				if(temp==0&&bNoEnd){
					
				}else if (temp<0||temp>60||bNoEnd) {
					result=false;
				}else{
					time[10]=temp;
				}
				break;
			case 12:
				//结束时间(秒)
				if(temp==0&&bNoEnd){
					
				}else if (temp<0||temp>60||bNoEnd) {
					result=false;
				}else{
					time[11]=temp;
				}
				break;
			}
		}
		//设置显示时间
		long start=0,end=0;
		if(result){
			if(type != 0){
				Calendar c = Calendar.getInstance();
				
				if(bNoStart){
					
				}else{
				//开始时间
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
					start = when;
				}
				
				if(bNoEnd){
					
				}else{
					//结束时间
					c.set(Calendar.YEAR,time[6]);
					c.set(Calendar.MONTH,time[7] - 1);
					c.set(Calendar.DAY_OF_MONTH,time[8]);
					c.set(Calendar.HOUR_OF_DAY,time[9]);
					c.set(Calendar.MINUTE, time[10]);
					c.set(Calendar.SECOND, time[11]);
					long when = c.getTimeInMillis();
					if (when < 0) {
						return;
					}
					end = when;
				}
				//结束必须大于开始
				if((!bNoEnd)&&(end<start)){
					return;
				}
			}
			
			//保存用户设置
			String sDate="1970-1-1";
			String sTime="00:00:00";
			String eDate="1970-1-1";
			String eTime="24:00:00";
			boolean selectAll, selectStart, SelectEnd;
			if(start ==0&& end ==0){
				selectAll=true;
				selectStart=false;
				SelectEnd=false;
			}else if(start == 0){
				selectAll=false;
				selectStart=false;
				SelectEnd=true;
				
				eDate = (time[6])+"-"+(time[7])+"-"+(time[8]);
				eTime = time[9]+":"+time[10]+":"+time[11];
			}else if(end ==0){
				selectAll=false;
				selectStart=true;
				SelectEnd=false;
				
				sDate = (time[0])+"-"+(time[1])+"-"+(time[2]);
				sTime = time[3]+":"+time[4]+":"+time[5];
			}else{
				selectAll=false;
				selectStart=true;
				SelectEnd=true;
				
				sDate = (time[0])+"-"+(time[1])+"-"+(time[2]);
				sTime = time[3]+":"+time[4]+":"+time[5];
				eDate = (time[6])+"-"+(time[7])+"-"+(time[8]);
				eTime = time[9]+":"+time[10]+":"+time[11];
			}
			saveDateAndTime(sDate, sTime, eDate, eTime, selectAll, selectStart, SelectEnd);
			
			//更新表格
			if (mLoadInfo!=null) {
				mLoadInfo.bUpdate=true;
			}
			updateData(start,end);
		}
	}
	
	/**
	 * 保存用户设置
	 */
	private void saveDateAndTime(String sDate,String sTime,String eDate,String eTime,boolean selectAll,boolean selectStart,boolean SelectEnd){
		//保存用户数据
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences("information", 0).edit();
		
		shareEditor.putString("history_sTime"+info.getnItemId()+"", sTime);
		shareEditor.putString("history_eTime"+info.getnItemId()+"", eTime);
		shareEditor.putString("history_sDate"+info.getnItemId()+"", sDate);
		shareEditor.putString("history_eDate"+info.getnItemId()+"", eDate);
		
		shareEditor.putBoolean("history_show_all"+info.getnItemId()+"", selectAll);
		shareEditor.putBoolean("history_show_start"+info.getnItemId()+"", selectStart);
		shareEditor.putBoolean("history_show_end"+info.getnItemId()+"", SelectEnd);

		shareEditor.commit();
	}
	/**
	 * 语言切换通知
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback(){

		@Override
		public void onLanguageChange(int languageId) {
			if (dTable!=null) {
				if (info.getmTitleFontSizes().size()>SystemInfo.getCurrentLanguageId()) {
					int temp=info.getmTitleFontSizes().get(SystemInfo.getCurrentLanguageId());
					dInfo.setnTitleFontSize((short)temp);
				}else {
					dInfo.setnTitleFontSize((short)10);
				}
				if (info.getmTitleFontType().size()>SystemInfo.getCurrentLanguageId()) {
					dInfo.setmHTypeFace(TextAlignUtil.getTypeFace(info.getmTitleFontType().get(SystemInfo.getCurrentLanguageId())));
				}else {
					dInfo.setmHTypeFace(Typeface.DEFAULT);
				}
				setTitle();
				dTable.resetTitlePaint();
				dTable.updateTitle(mHList);
				SKSceneManage.getInstance().onRefresh(items);
			}
		}
		
	};
	
	/**
	 * 接受采集通知
	 */
	DataCollect.IPlcNoticCallBack dataCallBack= new DataCollect.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<HistoryDataProp> nValueList,
				int nCollectRate) {
			//显示搜索结果的情况下不刷新
			if(isSearchable){
				if(isSearching){
					return;
				}
			}
			
			//Log.d(TAG, "nCollectRate:"+nCollectRate+",id:"+items.itemId);
			if (!refresh||!flag) {
				return;
			}
			
			if (nValueList==null||nValueList.size()==0) {
				return;
			}
			
			HistoryDataProp mData=nValueList.get(0);
			if (mData==null||mData.nDataList==null||mData.nDataList.size()==0) {
				return;
			}
			
			boolean result=false;
			if (nStartTime==0&&nEndTime==0) {
				result=true;
				//Log.d(TAG, "show all.....");
			}else {
				if (nStartTime==0) {
					if (nEndTime>=mData.nMillisTime) {
						//Log.d(TAG, "show end.....");
						result=true;
					}
				}else if(nEndTime==0) {
					if (nStartTime<=mData.nMillisTime) {
						//Log.d(TAG, "show start.....");
						result=true;
					}
				}else{
					if (nEndTime>=mData.nMillisTime&&nStartTime<=mData.nMillisTime) {
						//Log.d(TAG, "show start and end.....");
						result=true;
					}
				}
				
			}
			
			if (mLoadInfo!=null) {
				if (mLoadInfo.bUpdate) {
					//时间范围改变
					mLoadInfo.nLoadType=0;
					mLoadInfo.nLoadCount=info.getnLine();
					SkGlobalBackThread.getInstance().getGlobalBackHandler()
					.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, iReadCallback).sendToTarget();
					mLoadInfo.bUpdate=false;
				}
			}
			
			if (result) {
				addData(mData);
			}
		}

		@Override
		public void noticDelDatabase(int gid) {
			if (info==null) {
				return;
			}
			if (info.getnGroupId()!=gid) {
				return;
			}
			nCacheNum=0;
			isLoading=true;
			if(mRowCells!=null){
				mRowCells.clear();
			}
			if (dTable!=null) {
				//正常模式
				dTable.updateView(1);
				dTable.updateDataNum(0);
				dTable.moveToTop();
			}
			nAllCount=0;
			mLoadInfo.nLoadType=0;
			mLoadInfo.nLoadCount=info.getnLine();
			addDataByBackThread();
		}
		
	};
	
	/**
	 * 历史数据读取
	 */
	SkGlobalBackThread.IReadHistoryCallback iReadCallback=new SkGlobalBackThread.IReadHistoryCallback() {
		
		@Override
		public void begainReadHistory() {
			/**
			 *  mDatas[0]=类型
			 *  mDatas[1]=第几行开始
			 *  mDatas[2]=加载多少数据
			 */
			if (!refresh) {
				return;
			}
			
			Vector<Integer> temp=new Vector<Integer>();
			for (int i = 0; i < mCodeList.size(); i++) {
				temp.add(mCodeList.get(i));
			}
			
			if (nAllCount==0||mLoadInfo.nLoadType==0) {
				if(isSearchable&&isSearching){//搜索
					
					String sQueryElement = getSearchItem(nSearchIndex);//获得搜索列名称
					
					String sSearchCondition = "";
					if(sQueryElement.equals("id")){
						sSearchCondition = " >= "+mSearchString;
						bSearchId = true;
						try{
							nSearchIdNumber = Integer.parseInt(mSearchString);
						}catch(Exception e){
							e.printStackTrace();
						}
					}else{
						sSearchCondition = " like " + "\'%"+mSearchString + "%\'";
					}
					System.out.println("sSearchCondition:"+sSearchCondition);
					nAllCount=DataCollect.getInstance().getDataCount(info.getnGroupId(), sQueryElement, sSearchCondition);
					dTable.setEnterKeyShow(true);
					dTable.getHTitleItem().SetEnterKeyCallBack(iEnterKeyCallBack);
					//ResetTitleColorofTable(Color.WHITE-info.getnTitleBackColor());//重刷标题背景色，以作区分
				}else{
					//Log.d(TAG, "nStartTime:"+nStartTime+",nEndTime:"+nEndTime);
					nAllCount=DataCollect.getInstance().getDataCount(info.getnGroupId(),nStartTime, nEndTime);
				}
			}
			
			if (mLoadInfo.nLoadType==0) {
				flag=true;
				if (nAllCount>info.getnLine()) {
					mLoadInfo.nRowIndex=nAllCount-info.getnLine()+1;
				}else {
					mLoadInfo.nRowIndex=1;
				}
				mLoadInfo.nEndIndex=nAllCount;
			}
			
			Vector<HistoryDataProp> dataProps;
			
			if(isSearchable&&isSearching){//搜索
				String sQueryElement = getSearchItem(nSearchIndex);//获得搜索列名称
				String sSearchCondition = "";
				if(sQueryElement.equals("id")){
					sSearchCondition = " >= "+mSearchString;
				}else{
					sSearchCondition = " like " + "\'%"+mSearchString + "%\'";
				}
				
				dataProps=DataCollect.getInstance()
						.getHistoryDataByCoditin(info.getnGroupId(),mLoadInfo.nRowIndex, mLoadInfo.nLoadCount,temp,sQueryElement,sSearchCondition);
			}else{
				
				dataProps=DataCollect.getInstance()
					.getHistoryDataByTime(info.getnGroupId(),mLoadInfo.nRowIndex, mLoadInfo.nLoadCount, temp, nStartTime, nEndTime);
			}
			
			//Log.d(TAG, "nAllCount:"+nAllCount+",type:"+mLoadInfo.nLoadType);
			if(bSearchId){
				mLoadInfo.nRowIndex += nSearchIdNumber;
				mLoadInfo.nEndIndex += nSearchIdNumber;
			}
			initData(dataProps,mLoadInfo.nLoadType,mLoadInfo.nRowIndex,mLoadInfo.nEndIndex);
		}
	};	
	
	private synchronized void addData(HistoryDataProp data){
		
		if (mRowCells==null) {
			return;
		}
		
		if(sTaskName.equals("")){
			sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
		}
		SKThread.getInstance().getBinder().onTask(MODULE.HISTORY, TASK.HISTORY_ADD, sTaskName,data);
	}
	
	/**
	 * 处理新增历史数据
	 */
	private int nCacheNum=0;
	private void doData(HistoryDataProp data){
		
		String sData="",sTime="";
		if (nAllCount<=info.getnLine()) {
			bLast=true;
		}
		if (bLast&&!isLoading) {
			
			if (!data.bCollectFull) {
				//没才满
				if (nCacheNum>0) {
					mRowCells.clear();
					dTable.updateView(1);
				}
				nCacheNum=0;
				nAllCount++;
			}
			
			Vector<String> mClounms=new Vector<String>();
			RowCell rowCell=new RowCell();
			rowCell.nClounmCount=nColum;
			rowCell.mClounm=mClounms;
			rowCell.nRowIndex=nAllCount;
			
			//显示序号
			if (info.isbShowCode()) {
				mClounms.add("CODE");
			}
			
			String temp[]=getTime(info.geteTimeFormat(), info.geteDateFormat(), data.nMillisTime);
			
			//显示时间
			if (info.isbShowTime()) {
				if (temp!=null&&temp.length==2) {
					sTime=temp[1];
				}
				mClounms.add(sTime);
			}
			//显示日期
			if (info.isbShowDate()) {
				if (temp!=null&&temp.length==2) {
					sData=temp[0];
				}
				mClounms.add(sData);
			}
			
			//数据列
			for (int i = 0; i < info.getmDataList().size(); i++) {
				if (info.getmDataList().get(i).isbShow()) {
					if (data.nDataList.size()>i) {
						mClounms.add(data.nDataList.get(i));
					}else {
						mClounms.add("");
					}
				}
			}
			
			mRowCells.add(rowCell);
			if (mRowCells.size()>info.getnLine()) {
				for (int i = 0; i < mRowCells.size()-info.getnLine(); i++) {
					mRowCells.remove(0);
				}
			}
			
			if (dTable!=null) {
				
				bFore=false;
				bLast=true;
				if (nAllCount>info.getnLine()) {
					mLoadInfo.nRowIndex=nAllCount-info.getnLine()+1;
				}else {
					mLoadInfo.nRowIndex=1;
				}
				mLoadInfo.nEndIndex=nAllCount;
				
				dTable.updateDataNum(nAllCount);
				dTable.moveToBottom();
				
				if (dTable.isShowBar()&&mRowCells.size()>=info.getnLine()) {
					dTable.updateView(2);
				}else {
					dTable.updateView(1);
				}
				
				if (nAllCount<info.getnLine()) {
					dTable.initData(mRowCells, mHList,0);
				}else{
					dTable.initData(mRowCells, mHList,nAllCount-mRowCells.size()+1);
				}
				SKSceneManage.getInstance().onRefresh(items);
			}
		}else {
			if (!data.bCollectFull) {
				nCacheNum+=1;
				nAllCount++;
				dTable.updateDataNum(nAllCount); 
			}else {
				dTable.updateDataNum(nAllCount);
			}
		}
	}
	
	/**
	 * 更新数据
	 * @param dataProps-显示的数据
	 * @param type-加载的类型，0-初始化,1点击顶部按钮,2点击底部按钮,3向上滑动,4向下滑动
	 * @param top-起始行序号
	 * @param end-结束行序号
	 */
	private synchronized void initData(Vector<HistoryDataProp> dataProps,int type,int top,int end){
		
		int loadNum=0;
		
		mRowCells.clear();
		if (dataProps != null && dataProps.size() > 0) {
			int id=top;
			if (id < 1) {
				id = 1;
			}
			loadNum=dataProps.size();
			for (int i = 0; i < loadNum; i++) {
				HistoryDataProp data = dataProps.get(i);
				addRowCell(data, id++);
			}
		}

		if (dTable != null) {
			if (top+loadNum<info.getnLine()) {
				dTable.initData(mRowCells, mHList,0);
			}else{
				dTable.initData(mRowCells, mHList,top);
			}
		}

		if (type == 0) {
			if (nAllCount>(info.getnLine()-1)){
				bFore=false;
				bLast=true;
			}else{
				bFore=true;
				bLast=false;
			}
			if (dTable != null) {
				dTable.updateDataNum(nAllCount);
			}
		} else {
			if (bUntreated) {
				bUntreated = false;
				mLoadInfo.nLoadType=type;
				if (this.nTop>=nAllCount) {
					mLoadInfo.nRowIndex=nAllCount-info.getnLine()+1;
					mLoadInfo.nEndIndex=nAllCount;
				}else {
					mLoadInfo.nRowIndex=this.nTop;
					mLoadInfo.nEndIndex=this.nTop+info.getnLine();
				}
				mLoadInfo.nLoadCount=info.getnLine();
				addDataByBackThread();
			}
		}
		
		dTable.updateView(1);
		if (bLast&&dTable.isShowBar()) {
			//处于底部，并且滑动块显示
			if (mRowCells.size()>=info.getnLine()) {
				dTable.updateView(2);
			}
			dTable.moveToBottom();
		}
		
		isLoading=false;
		SKSceneManage.getInstance().onRefresh(items);
		
	}
	
	/**
	 * 添加一行数据
	 */
	private void addRowCell(HistoryDataProp mHistoryDataProp,int id){
		Vector<String> mClounms=new Vector<String>();
		RowCell rowCell=new RowCell();
		rowCell.nClounmCount=nColum;
		rowCell.mClounm=mClounms;
		rowCell.nRowIndex=id;
		
		if (mHistoryDataProp!=null) {
			Vector<String > nDataList=mHistoryDataProp.nDataList;
			if (nDataList!=null) {
				//显示序号
				if (info.isbShowCode()) {
					mClounms.add("CODE");
				}
				
				String temp[]=getTime(info.geteTimeFormat(), info.geteDateFormat(),
						mHistoryDataProp.nMillisTime);
				
				//显示时间
				if (info.isbShowTime()) {
					if (temp==null||temp.length!=2) {
						mClounms.add("");
					}else {
						mClounms.add(temp[1]);
					}
				}
				//显示日期
				if (info.isbShowDate()) {
					if (temp==null||temp.length!=2) {
						mClounms.add("");
					}else {
						mClounms.add(temp[0]);
					}
				}
				
				//数据列
				//Log.d(TAG, "c size:"+nDataList.size());
				for (int j = 1; j < nDataList.size(); j++) {
					mClounms.add(nDataList.get(j));
				}
			}
		}
		
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
			if (dTable!=null) {
				dTable.loadItem(null, 0);
			}
		}
		
		@Override
		public void onNext(int page) {
			if (dTable!=null) {
				dTable.loadItem(null, 1);
			}
		}

		@Override
		public void onLoad(int top, int type,int site) {
			dataCenter(top,type,site);
		}
	};
	
	/**
	 * 数据加载处理中心
	 * @param top 当前显示页第一行的序号
	 * @param type 滑动类型
	 *        type-0初始化
	 *        type-1点击顶部按钮
	 *        type-2点击底部按钮
	 *        type-3向上滑动
	 *        type-4向下滑动
	 */
	private boolean bLast=false;
	private boolean bFore=false;
	private synchronized void dataCenter(int top,int type,int site){
		
		
		//Log.d(TAG, ".......top:"+top+",site:"+site+",isLoading:"+isLoading+",bFore:"+bFore);
		if (isLoading) {
			bUntreated=true;
			this.nTop=top;
			return;
		}
		
		if (type==1||type==3) {
			if (bFore) {
				//Log.d(TAG, "已经处于最前面...");
				return;
			}else {
				if (site==0) {
					//处于最顶部
					bFore=true;
					bLast=false;
				}else{
					if (site==1) {
						//处于中间
						bLast=false;
					}
					bFore=false;
				}
			}
		}
		
		if (type==2||type==4) {
			if (bLast) {
				//Log.d(TAG, "已经处于最后面...");
				return;
			}else {
				if(site==2){
					//处于底部
					bLast=true;
					bFore=false;
				}else {
					if (site==1) {
						//处于中间
						bFore=false;
					}
					bLast=false;
				}
			}
		}
		
		isLoading=true;
		if (bLast) {
			if (nAllCount>info.getnLine()) {
				top=nAllCount-info.getnLine()+1;
			}
		}
		
		//Log.d(TAG, "type:"+type+",top:"+top);
		mLoadInfo.nLoadType=type;
		mLoadInfo.nRowIndex=top;
		mLoadInfo.nEndIndex=top+info.getnLine();
		mLoadInfo.nLoadCount=info.getnLine();

		addDataByBackThread();
	}
	
	/**
	 * 启动后台线程加载数据
	 */
	private void addDataByBackThread(){
		if (sTaskName.equals("")) {
			sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
		}
		
		isLoading=true;
		SkGlobalBackThread.getInstance().getGlobalBackHandler()
		.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, iReadCallback).sendToTarget();
	}
	
	/**
	 * 表格的点击事件
	 */
	private DateTimeSetting timeSetting;
	private SKTableRowNum mTableRowNum=null;
	//about search
	private SKSearchDialog mSKSearchDialog;
	private com.android.Samkoonhmi.skwindow.SKSearchDialog.IOperCall SKSearchIOperCall=new com.android.Samkoonhmi.skwindow.SKSearchDialog.IOperCall(){
		
		@Override
		synchronized public void onConfirm(int index, String Scontent) {
			mSearchString = Scontent;//获得搜索条件匹配
			
			if(TextUtils.isEmpty(mSearchString)){
				return;
			}
			nSearchIndex = nSearchItem;
			mLoadInfo.nLoadType=0; //设置初始化标识		
			
			isSearching=true;//标识为正在搜索，界面显示的是搜索结果，停止刷新
			bSearchId = false;
			
			SkGlobalBackThread.getInstance().getGlobalBackHandler()
				.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, iReadCallback).sendToTarget();
		}

		@Override
		public void onCancel() {
		}
	};
	
	DragTable.IClickListener clickListener=new DragTable.IClickListener() {
		
		@Override
		public void onLongClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time=0;
			if (nAllCount<info.getnLine()) {
				if (!isSearchable) return;
			}
			
			//弹出搜索框
			if (isSearchable){
				
				nSearchItem = gid;//获得点击列
				
				CreateSearchWin(gid);
			}else if (mTableRowNum==null||!mTableRowNum.isShow) {
				mTableRowNum=new SKTableRowNum(mContext);
				mTableRowNum.setiOperCall(iOperCall);
				mTableRowNum.showPopWindow(mLoadInfo.nRowIndex,dTable.getShowCount(),240, 180);
			}
		}
		
		@Override
		public void onDoubleClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time=0;
			
			//实现时间搜索，从搜索状态恢复
			
			if(isSearchable){
				if(isSearching) {
					ResetDataFromSearch();
				}
			}
			
			if (timeSetting==null||!timeSetting.showFlag) {
				Activity activity=SKSceneManage.getInstance().getActivity();
				if (activity==null) {
					return;
				}
				timeSetting= new DateTimeSetting(activity);
				timeSetting.setHistoryShowId(info.getnItemId()+"");
				timeSetting.setiDateCallback(dateCallback);
				
				int width=300,height=230;
				
				if(SKSceneManage.getInstance().nSceneWidth >= 800){
					width = 400;
				}
				
				timeSetting.onCreate(DateTimeSetting.TYPE.HISTORY_TIME, width, height);
				timeSetting.showDialog(DateTimeSetting.TYPE.HISTORY_TIME,
						info.getnLeftTopX()+(info.getnWidth()-width)/2, 
						info.getnLeftTopY()+(info.getnHeight()-height)/2,width,height);
			}
		}
		
		@Override
		public void onClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time=0;
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
	 * 改变显示时间范围
	 */
	DateTimeSetting.IDateCallback dateCallback=new DateTimeSetting.IDateCallback() {

		@Override
		public void onChange(long start, long end) {
			//Log.d(TAG, "start:"+start+",end:"+end);
			if (mLoadInfo!=null) {
				mLoadInfo.bUpdate=true;
			}
			updateData(start,end);
		}
	};
	
	/**
	 * 时间改变更新数据
	 */
	public void updateData(long start,long end){
		
		nStartTime=start;
		nEndTime=end;
		saveUserInfo();
		
		mLoadInfo.nLoadType=0;
		
		if (sTaskName.equals("")) {
			sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
		}

		// 获取历史数据
		SkGlobalBackThread
				.getInstance()
				.getGlobalBackHandler()
				.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, iReadCallback)
				.sendToTarget();
		
	}
	
	/**
	 * 获取用户设置信息
	 */
	private void getUserInfo(){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"information", 0);
		nStartTime = sharedPreferences.getLong("history_sTime_long"+info.getnItemId(), 0);
		nEndTime = sharedPreferences.getLong("history_eTime_long"+info.getnItemId(), 0);
	}
	
	/**
	 * 保存用户设置信息
	 */
	private void saveUserInfo(){
		SharedPreferences.Editor shareEditor = mContext.getSharedPreferences(
				"information", 0).edit();
		shareEditor.putLong("history_sTime_long"+info.getnItemId(), nStartTime);
		shareEditor.putLong("history_eTime_long"+info.getnItemId(), nEndTime);
		shareEditor.putBoolean("history_isSet"+info.getnItemId(), true);
		shareEditor.commit();
	}
	
	/**
	 * 用于拖动处理
	 */
	
	public class UIHandler extends Handler{

		public UIHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_DIALOG:
				//显示对话框
				if (info!=null) {
					SKProgress.show(mContext, info.getnWidth()/4, info.getnHeight()/2, 
							info.getnLeftTopX()+3*info.getnWidth()/8, 
							info.getnLeftTopY()+info.getnHeight()/4, ShowStyle.DEFAULT);
				}
				break;
			case HIDE_DIALOG:
				//隐藏对话框
				mHandler.removeMessages(OUT_TIME);
				SKProgress.hide();
				break;
			case OUT_TIME:
				break;
			case RESET_VIEW:
				if (mRowCells.size()>info.getnLine()) {
					for (int i = 0; i < mRowCells.size()-info.getnLine(); i++) {
						mRowCells.remove(0);
					}
					if (dTable!=null) {
						dTable.moveToBottom();
					}
				}
				
				if (dTable!=null) {
					dTable.updateDataNum(nAllCount);
					dTable.initData(mRowCells, mHList,0);
					dTable.moveToTop();
					SKSceneManage.getInstance().onRefresh(items);
				}
				break;
			}
		}
		
	}
	
	/**
	 *格式化日期和时间
	 */

	private String[] getTime(TIME_FORMAT time,DATE_FORMAT date,long dateTime){
		
		String dateTimes[]=new String[]{"",""};
		
		Date mDate=new Date(dateTime);
		String dataFormat= DateStringUtil.convertDate(date, mDate);
		String timeFormat= DateStringUtil.converTime(time, mDate);
		if (dataFormat==null||timeFormat==null) 
		{
			dateTimes[0]="";
			dateTimes[1]="";
		}
		else
		{
			dateTimes[0]=dataFormat;
			dateTimes[1]=timeFormat;
		}
		
		return dateTimes;
	}
	
	/**
	 * 历史记录是否可以搜索
	 * @return true--可搜索；false--不可搜索
	 */
	public boolean GetSKHistoryShowSearchable(){
		return isSearchable;
	}
	/**
	 * 设置历史记录可否搜索
	 * @param searchable：true--可搜索；false--不可搜索
	 */
	public void SetSKHistoryShowSearchable(boolean searchable){
		isSearchable = searchable;
	}
	
	/**
	 * 重置历史数据信息
	 */
	private void ResetDataFromSearch(){
		isSearching=false;
		dTable.setEnterKeyShow(false);
		//ResetTitleColorofTable(info.getnTitleBackColor());

		mLoadInfo.nLoadType=0;
		
		SkGlobalBackThread.getInstance().getGlobalBackHandler()
			.obtainMessage(MODULE.READ_HISTORY_FROM_DATABASE, iReadCallback).sendToTarget();
	}
	
	/**
	 * 创建搜索窗口
	 * @param gid 提示信息下标；提示信息根据Table的标题信息设置
	 */
	private void CreateSearchWin(int gid){
		//时间查询有专用接口
		if(gid<0){
			return;
		}else if(info.isbShowCode()){//有序号
			if(info.isbShowDate()&&info.isbShowTime()){//有日期和时间
				if(gid==1||gid ==2){
					return;
				}
			}else if(info.isbShowDate()||info.isbShowTime()){//只有日期或时间
				if(gid ==1){
					return;
				}
			}
		}else {//无序号
			if(info.isbShowDate()&&info.isbShowTime()){//有日期和时间
				if(gid==0||gid ==1){
					return;
				}
			}else if(info.isbShowDate()||info.isbShowTime()){//只有日期或时间
				if(gid ==0){
					return;
				}
			}
		}
		
		if (mSKSearchDialog==null||!mSKSearchDialog.isShow){
			
			//mSearchString = null;
			
			//设置搜索框位置
			int pngWidth = 205;
			int pngHeight = 36;
			Rect popRect = new Rect();
			
			//居中效果
			int stepX = (items.rect.right - items.rect.left)/2;
			if(pngWidth > items.rect.right - items.rect.left){
				popRect.left = items.rect.left;
				popRect.right = items.rect.right;
			}else{
				popRect.left = items.rect.left + stepX - pngWidth/2;
				popRect.right = popRect.left + pngWidth;
			}
			
			int stepY = (items.rect.bottom - items.rect.top)/2;
			if(pngHeight > items.rect.bottom - items.rect.top){
				popRect.top = items.rect.top;
				popRect.bottom = items.rect.bottom;
			}else{
				popRect.top = items.rect.top + stepY - pngHeight/2;
				popRect.bottom = popRect.top+pngHeight;
			}
			
			
			mSKSearchDialog=new SKSearchDialog(mContext.getApplicationContext(),popRect);
			
			mSKSearchDialog.setHintInfo(mHList.get(gid));
			mSKSearchDialog.setiOperCall(SKSearchIOperCall);
			mSKSearchDialog.showPopWindow();
		}				
	}
	
	/**
	 * 通过点击的列获得搜索列名称
	 * @param gid 点击的列
	 * @return 搜索列名称
	 */
	private String getSearchItem(int gid){
		
		ArrayList<String> sHintElements = new ArrayList<String>();;
		if(info.isbShowCode()){
			sHintElements.add("id");
		}
		if(info.isbShowDate()){
			sHintElements.add("nMillis");
		}
		if(info.isbShowTime()){
			sHintElements.add("nMillis");
		}
		
		try{
			if(gid >= sHintElements.size()){
				return "data"+mCodeList.get(gid-sHintElements.size());
			}else{
				return  sHintElements.get(gid);
			}
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 设置返回键响应
	 */
	EnterKeyCallBack iEnterKeyCallBack = new EnterKeyCallBack(){
		@Override
		public void onPress() {
			ResetDataFromSearch();
		}

		@Override
		public void onLongTouch(int x, int y) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
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
			return getColor(info.getnBackcolor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
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
		
		if (color==info.getnBackcolor()) {
			return true;
		}
		info.setnBackcolor(color);
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
		return false;
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
			dTable.moveRank(type,w);
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
			dTable.moveRow(type, h);
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
		if (info==null||alpha<0||alpha>255) {
			return false;
		}
		if (info.getnAlpha()==alpha) {
			return true;
		}
		info.setnAlpha((short)alpha);
		dTable.resetAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
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
