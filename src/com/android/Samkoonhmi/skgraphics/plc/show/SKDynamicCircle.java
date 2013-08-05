package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.AddrPropBiz;
import com.android.Samkoonhmi.databaseinterface.DynamicCircleBiz;
import com.android.Samkoonhmi.graphicsdrawframe.EllipseItem;
import com.android.Samkoonhmi.model.DynamicCircleInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TASK;

/**
 * 动态圆形
 * @author 魏 科
 * @date   2012-06-14
 * */
public class SKDynamicCircle extends SKGraphCmnShow{

	private int         mItemID;        //控件ID
	private int         mSceneID;       //画面ID

	private short       mPaintState = 0;//绘制状态，约定：0初始态，1绘制态，2待重画态

	private EllipseItem mDynamicCircle; //动态圆形框架

	private Rect        mCurItemRect;   //当前控件矩形
	private Rect        mPrevItemRect;  //先前控件矩形

	private String  mChangeTaskID;      //移动任务签名

	private Paint   mPaintRef;          //Paint引用

	private DynamicCircleInfo mDCInfo;  //动态圆形数据实体类实例
	

	//private Rect   mAreaRect;     	//区域矩形，保留字段，不可删除

	private SKItems mTheItem;       	//控件层结构

	private short mXData = 0;   //当前圆心X坐标
	private short mYData = 0;   //当前圆心Y坐标
	private short mRData = 0;   //当前圆心半径
	
	private boolean  canRefreshFlag = true;  //当前是否可以刷新
	private boolean  isInitStateOK  = true;  //图形初始化正常
	
	private boolean mIsNewXData;   //是否有新的X数据
	private boolean mIsNewYData;   //是否有新的Y数据
	private boolean mIsNewRData;   //是否有新的半径数据
	
	private boolean  show       = true;   // 是否可显现
	private boolean  showByAddr = false;  // 是否注册显现地址
	private boolean  showByUser = false;  // 是否受用户权限控件
//	private AddrProp showAddr;    //显现地址
//	private AddrPropBiz aBiz;     //地址值查询类

	//以下是模拟数据，需要删除 
	private int xdata = 100; //圆心x坐标移动数据
	private int ydata = 100; //圆心y坐标移动数据
	private int rdata = 50;  //半径大小

	public SKDynamicCircle(int itemid, int sceneid,DynamicCircleInfo info){

		mItemID  = itemid;
		mSceneID = sceneid; 
		isInitStateOK = true;

		mPaintRef  = new Paint();
		mPaintRef.setDither(true);
		mPaintRef.setAntiAlias(true);
		this.mDCInfo=info;

	}

	/**
	 * 初始化半径数据
	 * */
	public void initCircleData(){
		if(null == mDCInfo){
			Log.e("SKDynamicCircle","initCircleData: mDCInfo is null");
			return;
		}
		mXData = mDCInfo.getCpXpos();
		mYData = mDCInfo.getCpYpos();
		mRData = mDCInfo.getRadius();
		
	}

	/**
	 * 初始化描述符
	 * */
	public void initItem(){
		if(null == mTheItem){
			mTheItem = new SKItems();
			if(null == mDCInfo){
				Log.e("SKDynamicCircle","initItem: mDCInfo is null");
				return;
			}
			mTheItem.nZvalue = mDCInfo.getZValue(); 
			mTheItem.itemId  = mItemID;
			mTheItem.sceneId = mSceneID;
			mTheItem.nCollidindId = mDCInfo.getCollidindId();
			mTheItem.mGraphics=this;
		}
	}

//	/**
//	 * 初始化限定区域,保留内容，不可删除
//	 * */
//	public void initArea(){
//		if(null == mAreaRect){
//			if(null == mDCInfo){
//				Log.e("SKDynamicCircle","mDCInfo is null");
//				return;
//			}
//			mAreaRect   = new Rect(mDCInfo.getAreaLp(),mDCInfo.getAreaTp(),mDCInfo.getAreaLp()+mDCInfo.getAreaWidth(),mDCInfo.getAreaTp()+mDCInfo.getAreaHeight());
//		}
//	}

	/**
	 * 初始化控件矩形
	 * */
	public void initItemRect(){

		if(null == mDCInfo){
			Log.e("SKDynamicCircle","initItemRect: mDCInfo is null");
			return;
		}
		
		//新建背景矩形实例
		if(null == mCurItemRect){
			mCurItemRect  = createInitRect();
		}

		if(null == mPrevItemRect){
			mPrevItemRect = new Rect(mCurItemRect);
		}
	}

	/**
	 * 根据圆形数据构造圆形的矩形区域
	 * */
	public Rect createInitRect(){

		short xp  = mDCInfo.getCpXpos();
		short yp  = mDCInfo.getCpYpos();
		short r   = mDCInfo.getRadius();

		Rect tmpRect = new Rect(xp-r,yp-r,xp+r,yp+r);

		return tmpRect;
	}

	/**
	 * 初始化动态圆形信息
	 * */
	public void initDynamicCircle(){

		if(null == mDynamicCircle){
			if(null == mDCInfo){
				Log.e("SKDynamicCircle","initBackRect: mDCInfo is null");
				return;
			}
			Rect tmpRect = createInitRect();

			mDynamicCircle = new EllipseItem(new Rect(tmpRect.left,tmpRect.top,tmpRect.width(),tmpRect.height()));

			if(1 == mDCInfo.getUseFill()){//是否有填充色
				//设置填充色
				mDynamicCircle.setBackColor(mDCInfo.getFillColor());
			}else{
				mDynamicCircle.setStyle(CSS_TYPE.CSS_TRANSPARENCE);
			}

			//设置边框填充色
			mDynamicCircle.setLineColor(mDCInfo.getRimColor());

			//设置边框宽度
			mDynamicCircle.setLineWidth(mDCInfo.getRimWidth());
			
			mDynamicCircle.setAlpha(mDCInfo.getAlpha());
			mDynamicCircle.setLineAlpha(255);
		}
	}

	/**
	 * 处理定时器回调
	 * */
	public void handleTimerUpdate(){		
		SKThread.getInstance().getBinder().onTask(MODULE.SKDYNAMICCIRCLE,TASK.DYNAMICCIRCLE_CHANGE,mChangeTaskID);		
	}

	/**
	 * 定时器回调
	 * */
	SKTimer.ICallback TimerCallback=new SKTimer.ICallback() {	
		@Override
		public void onUpdate() {
			handleTimerUpdate();
		}
	};

	/**
	 * 处理线程回调
	 * */
	public void handleTaskCallback(){	
		
		//模拟代码
		//changeAddrData();

		if( mIsNewXData || mIsNewYData || mIsNewRData){

			updateItemRect();       //更新矩形信息
			
			if(true == mIsNewXData){
				mIsNewXData = false;//X数据不再为新
			}

			if(true == mIsNewYData){
				mIsNewYData = false;//Y数据不再为新
			}

			if(true == mIsNewRData){
				mIsNewRData = false;//R数据不再为新
			}
			
			if(true == canRefreshFlag){
				
				canRefreshFlag     = false;  //标定当前不可再刷新
				
				mTheItem.rect      = new Rect(mPrevItemRect);
				mTheItem.mMoveRect = new Rect(mCurItemRect); 
				
				updateDynamicCircle();  //更新动态圆图像内容
				
				SKSceneManage.getInstance().onRefresh(mTheItem);
			}
		}//End of: if(mIsNewXData || mIsNewYData || mIsNewYData) 

	}

	/**
	 * 线程回调
	 * */
	SKThread.ICallback TaskCallback=new SKThread.ICallback() {

		@Override
		public void onUpdate(String msg, int taskId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpdate(int msg, int taskId) {
			handleTaskCallback();
		}

		@Override
		public void onUpdate(Object msg, int taskId) {
			// TODO Auto-generated method stub
		}	
	};

	/**
	 * 更新动态圆形信息
	 * */
	public void updateItemRect(){
		
		short cx = 0;
		short cy = 0;
		short r = 0;
		//计算新的圆形数据
		short orig_r = (short)(mCurItemRect.width()/2);
		if( mIsNewXData){
			cx = (short) (mDCInfo.getCpXpos() + mXData);
		}else{
			cx = (short) (mCurItemRect.right - orig_r);
		}
		
		if(mIsNewYData){
			cy = (short) (mDCInfo.getCpYpos() + mYData);
		}else{
			cy = (short) (mCurItemRect.bottom - orig_r);
		}
	    
		if(mIsNewRData){
			r  = (short) (mDCInfo.getRadius() + mRData);
			if(0 > r){
				r = 0;
			}
		}else{
			r =  (short) orig_r;
		}
			
		//计算矩形信息
		short left  =  (short)(cx - r); 
		short right =  (short)(cx + r);
		short top   =  (short)(cy - r);
		short bottom=  (short)(cy + r);

		if(null == mPrevItemRect){
			mPrevItemRect = new Rect(mCurItemRect);
		}else{
			mPrevItemRect.set(mCurItemRect);
		}
		mCurItemRect.set(left,top,right,bottom);

	}

	/**
	 * 更新待绘制的圆形信息
	 * */
	public void	updateDynamicCircle(){
		
		if(null == mTheItem.mMoveRect){
			Log.e("SKDynamicCircle","updateDynamicCircle: mTheItem.mMoveRect is null");
			return;
		}
		//更新包裹动态圆的坐标信息
		mDynamicCircle.getRect().set(mTheItem.mMoveRect.left,mTheItem.mMoveRect.top,mTheItem.mMoveRect.right,mTheItem.mMoveRect.bottom);
		
	}

	/**
	 * 绘制动态圆形
	 * */
	public void drawDynamicCircle(Canvas canvas){
		mDynamicCircle.draw(mPaintRef, canvas);
	}

	@Override
	public boolean isShow() {
		itemIsShow();
		SKSceneManage.getInstance().onRefresh(mTheItem);
		return show;
	}

	/**
	 * 控件是否可以显现
	 */
	private void itemIsShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(mDCInfo.getmShowInfo());
		}
	}

	/**
	 * 显现地址改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}
	};

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void realseMemeory() {
		SKTimer.getInstance().getBinder().onDestroy(TimerCallback);
		SKThread.getInstance().getBinder().onDestroy(TaskCallback, mChangeTaskID);
		
		if (showByAddr) {
			SKPlcNoticThread.getInstance().destoryCallback(showCall);
		}
		
		if(null != mDCInfo && 1 == mDCInfo.getUsePosCtrl()){//若位置受到地址控制
			SKPlcNoticThread.getInstance().destoryCallback(notifyCxAddrDataCallback);
			SKPlcNoticThread.getInstance().destoryCallback(notifyCyAddrDataCallback);
		}
		
		if(null != mDCInfo && 1 == mDCInfo.getUseSizeCtrl()){//若大小受到地址控制
			SKPlcNoticThread.getInstance().destoryCallback(notifyRAddrDataCallback);
		}
		
		mChangeTaskID = null;
		
		if(1 == mPaintState){//若当前至少绘制过一次
			mPaintState = 2; //控件进入待重绘状态，下次在initGraph中不会再初始化控件参数
		}else if(2 == mPaintState){
			mPaintState = 2;
		}else{
			mPaintState = 0;
		}
	}

	@Override
	public void getDataFromDatabase() {
	}
	
	public void getDataFromDatabaseDummy() {
//		if(0 > mItemID){//无效的控件ID
//			Log.e("SKDynamicCircle","getDataFromDatabase: invalid mItemID: " + mItemID);
//			return;
//		}
//
//		if(null != mDCInfo){
//			Log.i("SKDynamicCircle","getDataFromDatabase: mDCInfo is not null");
//			return;
//		}
//
//		DynamicCircleBiz tmpDCBiz = new DynamicCircleBiz();
//
//		mDCInfo = tmpDCBiz.select(mItemID); //从数据获取实例
//		if(null == mDCInfo){
//			Log.e("SKDynamicCircle","getDataFromDatabase: mDCInfo create failed!");
//			return;
//		}
//		
//		if(false == isInitStateOK) {//初始化异常
//			initGraphics(); //主动调用控件初始化函数
//		}
	}

	/**
	 * X地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyCxAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleCxAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理x地址数据通知回调
	 * */
	private Vector<Short> mCxAddrNoticeData = null; //存放通知数据
	public void handleCxAddrDataCallback(Vector<Byte> nStatusValue){

		if(nStatusValue.isEmpty()){
			return;
		}
		if (mCxAddrNoticeData == null)
		{
			mCxAddrNoticeData = new Vector<Short>();
		}else{
			mCxAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCxAddrNoticeData);
		if(!mCxAddrNoticeData.isEmpty()){
			mXData       = mCxAddrNoticeData.get(0); //取第一个数据
			mIsNewXData  = true;  
		}	
		
		return;
	} 

	/**
	 * y地址数据通知回调函数
	 * */
	
	SKPlcNoticThread.IPlcNoticCallBack notifyCyAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleCyAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理X地址数据通知回调
	 * */
	private Vector<Short> mCyAddrNoticeData = null; //存放通知数据
	private boolean dummyYData = true;
	public void handleCyAddrDataCallback(Vector<Byte> nStatusValue){
		
		if(nStatusValue.isEmpty()){
			return ;
		}
		if (mCyAddrNoticeData == null)
		{
			mCyAddrNoticeData = new Vector<Short>();
		}else{
			mCyAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCyAddrNoticeData);
		if(!mCyAddrNoticeData.isEmpty()){
			mYData       = mCyAddrNoticeData.get(0); //取第一个数据
			mIsNewYData  = true;  
		}
		return;
	}

	/**
	 * 半径地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyRAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleRAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理半径地址数据通知回调
	 * */
	private Vector<Short> mCrAddrNoticeData = null; //存放通知数据
	public void handleRAddrDataCallback(Vector<Byte> nStatusValue){

		if(nStatusValue.isEmpty()){
			return; 
		}
		if (mCrAddrNoticeData == null)
		{
			mCrAddrNoticeData = new Vector<Short>();
		}else{
			mCrAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCrAddrNoticeData);//将字节转换成短整型
		if(!mCrAddrNoticeData.isEmpty()){
			mRData       = mCrAddrNoticeData.get(0); //取第一个数据
			mIsNewRData  = true;  
		}
		return;
	} 

	@Override
	public void initGraphics() {
		
		if(null == mDCInfo){ //若数据实体为空
			if (null == mChangeTaskID) {
				getDataFromDatabase();
			}
			isInitStateOK = false;
			return;//不做初始操作
		}
		isInitStateOK  = true;
		if( 0 != mPaintState && 2 != mPaintState){//若不是初始态，也不是重绘态
			return;//不做初始操作
		}
		
		if(0 == mPaintState){ //若为初始态
			
			//初始化背景矩形
			initDynamicCircle();

			//初始化矩形控件
			initItemRect();

			//初始化控件层信息
			initItem();
			
			//初始化区域，保留内容，不可删除
			//initArea();  
		}		
		
		mTheItem.rect      = new Rect(mPrevItemRect);
		mTheItem.mMoveRect = new Rect(mCurItemRect); 
		
		updateDynamicCircle();  //更新动态圆图像内容		
		
		
//		if (aBiz==null) {
//			aBiz=new AddrPropBiz();
//		}
		if (mDCInfo.getmShowInfo() != null) {

			if (mDCInfo.getmShowInfo().isbShowByAddr()) {
				if (mDCInfo.getmShowInfo().getnAddrId() > 0) {
					// 受地址控制
					showByAddr = true;
//					showAddr=aBiz.selectById(mDCInfo.getmShowInfo().getnAddrId());
				}
			}
			if (mDCInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
			itemIsShow();
		}		
		
		SKSceneManage.getInstance().onRefresh(mTheItem);
		
		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mDCInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(mDCInfo.getmShowInfo().getShowAddrProp(),showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(mDCInfo.getmShowInfo().getShowAddrProp(),showCall, false);
			}
		}
		
		if(1 == mDCInfo.getUsePosCtrl()){//若位置受到地址控制
			SKPlcNoticThread.getInstance().addNoticProp(mDCInfo.getCpXDataAddr(), notifyCxAddrDataCallback, false);   //绑定X地址数据回调
			SKPlcNoticThread.getInstance().addNoticProp(mDCInfo.getCpYDataAddr(), notifyCyAddrDataCallback, false);   //绑定Y地址数据回调
		}
		
		if(1 == mDCInfo.getUseSizeCtrl()){//若大小受到地址控制
			SKPlcNoticThread.getInstance().addNoticProp(mDCInfo.getRadiusDataAddr(), notifyRAddrDataCallback, false); //绑定半径地址数据回调
		}
		
		
		if( 0 == mPaintState || 2 == mPaintState){//若是初始态或重绘态
			
			mChangeTaskID = SKThread.getInstance().getBinder().onRegister(TaskCallback);
			SKTimer.getInstance().getBinder().onRegister(TimerCallback);
		}
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {

		if (show) {
			if(mItemID != itemId){
				return false;
			}

			drawDynamicCircle(canvas);//绘制动态圆

			mPaintState    = 1;    //进入绘制状态
			canRefreshFlag = true; //可以再次刷新
			return true;
		}else{
			return false;
		}
	}
	
//	/**
//	 * 模拟下位地址变化,模拟代码，发布时请保留
//	 * */
	public void changeAddrData(){

		//以下是模拟代码
		if(100 == xdata){
			xdata = 200;
		}else if(200 == xdata){
			xdata = 300;
		}else if(300 == xdata){
			xdata = 400;
		}else if(400 == xdata){	
			xdata = 100;
		}

		if(100 == ydata){
			ydata = 200;
		}else if(200 == ydata){
			ydata = 300;
		}else if(300 == ydata){
			ydata = 400;
		}else if(400 == ydata){		
			ydata = 100;
		}

		if(rdata == 50){
			rdata = 100;
		}else if(rdata == 100){
			rdata = 150;
		}else if(rdata == 150){
			rdata = 50;
		}
		
		Vector<Integer>  dataList  = null;
		SEND_DATA_STRUCT mSendData = null;

		if(1 == mDCInfo.getUsePosCtrl()){//若位置受到地址控制
			dataList = new Vector<Integer>();
			dataList.add(xdata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDCInfo.getCpXDataAddr(), dataList, mSendData); //写x坐标

			dataList = new Vector<Integer>();
			dataList.add(ydata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDCInfo.getCpYDataAddr(), dataList, mSendData); //写Y坐标
		}

		if(1 == mDCInfo.getUseSizeCtrl()){//若大小受到地址控制
			dataList = new Vector<Integer>();
			dataList.add(rdata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDCInfo.getRadiusDataAddr(), dataList, mSendData); //写半径
		}
	}//End of change

}//End of class

