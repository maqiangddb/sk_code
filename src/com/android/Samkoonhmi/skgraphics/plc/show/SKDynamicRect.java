//import SKGraphCmnShow;
package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.Vector;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.AddrPropBiz;
import com.android.Samkoonhmi.databaseinterface.DynamicRectBiz;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.DynamicRectInfo;
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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
/**
 * 动态矩形
 * @author 魏 科
 * @date   2012-06-14
 * */
public class SKDynamicRect extends SKGraphCmnShow {

	private int   mItemID;           //控件ID
	private int   mSceneID;          //画面ID

	private short mPaintState = 0;   //绘制状态，约定：0初始态，1绘制态，2待重画态

	private RectItem mDynamicRect;   //动态矩形框架

	private Rect     mCurItemRect;   //当前的矩形框
	private Rect     mPrevItemRect;  //先前的矩形框

	private String   mChangeTaskID;  //移动任务签名

	private Paint    mPaintRef;      //Paint引用

	private DynamicRectInfo mDRInfo; //动态矩形的数据实体类

	//private Rect   mAreaRect;      //区域矩形，保留代码，不可删除

	private SKItems mTheItem;

	private short mXData;   //当前左上角X坐标
	private short mYData;   //当前左上角Y坐标
	private short mWData;   //当前宽度
	private short mHData;   //当前高度

	private boolean mIsNewXData;   //是否有新的X数据
	private boolean mIsNewYData;   //是否有新的Y数据
	private boolean mIsNewWData;   //是否有新的宽度数据
	private boolean mIsNewHData;   //是否有新的高度数据

	private boolean  canRefreshFlag = true;  //当前是否可以刷新
	private boolean  isInitStateOK  = true;  //图形初始化正常
	
	private boolean  show       = true;   // 是否可显现
	private boolean  showByAddr = false;  // 是否注册显现地址
	private boolean  showByUser = false;  // 是否受用户权限控件
	
	//private AddrProp showAddr;    //显现地址
	//private AddrPropBiz aBiz;     //地址值查询类
	

	//以下是模拟数据，调试保留
	private int xmovedata = 100; //x坐标移动数据
	private int ymovedata = 100; //y坐标移动数据
	private int wmovedata = 50; //宽度
	private int hmovedata = 50; //高度

	public SKDynamicRect(int itemid, int sceneid,DynamicRectInfo info) {

		mItemID  = itemid;
		mSceneID = sceneid; 
		isInitStateOK = true;

		mPaintRef  = new Paint();
		mPaintRef.setDither(true);
		mPaintRef.setAntiAlias(true);
		this.mDRInfo=info;

	}

	/**
	 * 初始化动态矩形基本信息
	 * */
	public void initRectData(){

		if(null == mDRInfo){
			Log.e("SKDynamicRect","initRectData: mDRInfo is null");
			return;
		}

		mWData = mDRInfo.getWidth();
		mHData = mDRInfo.getHeight();
		mXData = mDRInfo.getXPos();
		mYData = mDRInfo.getYPos();
	}

	/**
	 * 初始化控件信息
	 * */
	public void initItem(){
		if(null == mTheItem){
			mTheItem = new SKItems();
			if(null == mDRInfo){
				Log.e("SKDynamicRect","initItem: mDRInfo is null");
				return;
			}
			mTheItem.nZvalue = mDRInfo.getZValue(); 
			mTheItem.itemId  = mItemID;
			mTheItem.sceneId = mSceneID;
			mTheItem.nCollidindId = mDRInfo.getCollidindId();
			mTheItem.mGraphics=this;
		}
	}

//  保留代码不可删除
//	/**
//	 * 初始限定区域
//	 * */
//	public void initArea(){
//		if(null == mDRInfo){
//			Log.e("SKDynamicRect","initArea: mDRInfo is null");
//			return;
//		}
//		mAreaRect   = new Rect(mDRInfo.getAreaLp(),mDRInfo.getAreaTp(),mDRInfo.getAreaLp()+mDRInfo.getAreaWidth(),mDRInfo.getAreaTp()+mDRInfo.getAreaHeight());
//	}

	/**
	 * 处理定时器回调
	 * */
	public void handleTimerUpdate(){		
		SKThread.getInstance().getBinder().onTask(MODULE.SKDYNAMICRECT,TASK.DYNAMICRECT_CHANGE,mChangeTaskID);		
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

		//changeAddrData(); //模拟数据
		
		if(mIsNewXData || mIsNewYData || mIsNewWData || mIsNewHData){


			updateItemRect();
			
			if(mIsNewXData){
				mIsNewXData = false;	
			} 
			if(mIsNewYData){
				mIsNewYData = false;
			}
			if(mIsNewWData){
				mIsNewWData = false;
			}
			if(mIsNewHData){
				mIsNewHData = false;
			}

			if(true == canRefreshFlag){
				canRefreshFlag = false; //标定当前不可再刷新
				
				mTheItem.rect       = new Rect(mPrevItemRect);
				mTheItem.mMoveRect  = new Rect(mCurItemRect);
				
				updateDynamicRect(); //更新动态矩形信息
				
				SKSceneManage.getInstance().onRefresh(mTheItem);
			}
		}
	}

	/**
	 * 线程回调
	 * */
	SKThread.ICallback TaskCallback=new SKThread.ICallback() {

		@Override
		public void onUpdate(String msg, int taskId) {
		}

		@Override
		public void onUpdate(int msg, int taskId) {
			handleTaskCallback();
		}

		@Override
		public void onUpdate(Object msg, int taskId) {
		}	
	};

	/**
	 * 根据参考点类型获得矩形
	 * */
	public Rect creatRectByRefType(){

		short width  = mDRInfo.getWidth();
		short height = mDRInfo.getHeight();
		short ox     = mDRInfo.getXPos();
		short oy     = mDRInfo.getYPos();

		Rect tmpRect = null;
		short reftype = mDRInfo.getRefType();
		switch(reftype){
		case 3://左下角
			tmpRect =  new Rect(ox,oy-height,ox+width,oy);
			break;
		case 2://右下角
			tmpRect =  new Rect(ox-width,oy-height,ox,oy);
			break;
		case 1://右上角
			tmpRect =  new Rect(ox-width,oy,ox,oy+height);
			break;
		case 0: //左上角
		default://默认使用左上角
			tmpRect =  new Rect(ox,oy,ox+width,oy+height);
			break;
		}

		return tmpRect;
	}

	/**
	 * 绘制动态矩形
	 * */
	public void drawDynamicRect(Canvas canvas){
		if(null == canvas){
			Log.e("SKAnimation","drawDynamicRect: canvas is null");
		}
		
		if( (mTheItem.mMoveRect.left >= mTheItem.mMoveRect.right)
		        || (mTheItem.mMoveRect.top >= mTheItem.mMoveRect.bottom) ){//矩形坐标异常
				//不绘制矩形
			}else{//正常矩形
				mDynamicRect.draw(mPaintRef, canvas);
			}
		
	}

	/**
	 * 初始化控件矩形
	 * */
	public void initItemRect(){

		if(null == mDRInfo){
			Log.e("SKAnimation","initItemRect: mAVInfo is null");
			return;
		}	
		
		//新建背景矩形实例
		if(null == mCurItemRect){
			mCurItemRect  = new Rect(creatRectByRefType());
		}

		if(null == mPrevItemRect){
			mPrevItemRect = new Rect(mCurItemRect);
		}
	}

	/**
	 * 初始化动态矩形信息
	 * */
	public void initDynamicRect(){

		if(null ==  mDynamicRect){
			if(null == mDRInfo){
				Log.e("SKDynamicRect","initDynamicRect: mAVInfo is null");
				return;
			}

			//新建矩形实例
			Rect tmpRect = creatRectByRefType();
			mDynamicRect = new RectItem(new Rect(tmpRect.left,tmpRect.top,tmpRect.width(),tmpRect.height()));

			if(1 == mDRInfo.getUseFill()){//若需填充矩形
				//设置矩形填充色
				mDynamicRect.setBackColor(mDRInfo.getFillColor());
			}else{
				mDynamicRect.setStyle(CSS_TYPE.CSS_TRANSPARENCE);
			}

			//设置边框填充色
			mDynamicRect.setLineColor(mDRInfo.getRimColor());

			//设置边框宽度
			mDynamicRect.setLineWidth(mDRInfo.getRimWidth());
			
			//设置透明度
			mDynamicRect.setAlpha(mDRInfo.getAlpha());
			
			//设置边框透明度
			mDynamicRect.setLineAlpha(255);
		}
	}

	/**
	 * 更新待绘制矩形信息
	 * */
	public void updateDynamicRect(){
		if(null == mTheItem.mMoveRect){
			Log.e("SKDynamicRect","updateDynamicRect: mTheItem.mMoveRect is null");
			return;
		}
		
		mDynamicRect.getRect().set(mTheItem.mMoveRect.left, mTheItem.mMoveRect.top, mTheItem.mMoveRect.right, mTheItem.mMoveRect.bottom);

	}

	/**
	 * 当前矩形项目
	 * */
	public void updateItemRect(){

		short width  = 0; 
		short height = 0;
		short ox     = 0;
		short oy     = 0;
		
		
		if(mIsNewHData){
			height = (short)(mDRInfo.getHeight() + mHData);
		}else{
			height = (short)(mCurItemRect.height());
		}
		
		if(0 > (height)){
			height = 0;
		}
		
		if(mIsNewWData){
			width  = (short)(mDRInfo.getWidth()  + mWData);
		}else{
			width  = (short)(mCurItemRect.width()); 
		}
		
		if(0 > (width)){
			width = 0;
		}
		
		Rect tmpRect = null;

		short reftype = mDRInfo.getRefType();
		switch(reftype){
		case 3://左下角
			if(mIsNewXData){
				ox = (short)(mDRInfo.getXPos()   - mXData);	
			}else{
				ox = (short)(mCurItemRect.left); 
			} 
			
			if(mIsNewYData){
				oy = (short)(mDRInfo.getYPos()   + mYData);
			}else{
				oy = (short)(mCurItemRect.bottom);
			}
			
			tmpRect =  new Rect(ox,oy-height,ox+width,oy);
			break;
		case 2://右下角
			if(mIsNewXData){
				ox = (short)(mDRInfo.getXPos()   - mXData);	
			}else{
				ox = (short)(mCurItemRect.right); 
			} 
			
			if(mIsNewYData){
				oy = (short)(mDRInfo.getYPos()   + mYData);
			}else{
				oy = (short)(mCurItemRect.bottom);
			}
			tmpRect =  new Rect(ox-width,oy-height,ox,oy);
			break;
		case 1://右上角
			if(mIsNewXData){
				ox = (short)(mDRInfo.getXPos()   - mXData);	
			}else{
				ox = (short)(mCurItemRect.right); 
			} 
			
			if(mIsNewYData){
				oy = (short)(mDRInfo.getYPos()   + mYData);
			}else{
				oy = (short)(mCurItemRect.top);
			}
			tmpRect =  new Rect(ox-width,oy,ox,oy+height);
			break;
		case 0: //左上角
		default://默认使用左上角
			if(mIsNewXData){
				ox = (short)(mDRInfo.getXPos()   + mXData);	
			}else{
				ox = (short)(mCurItemRect.left); 
			} 
			
			if(mIsNewYData){
				oy = (short)(mDRInfo.getYPos()   + mYData);
			}else{
				oy = (short)(mCurItemRect.top);
			}
			tmpRect =  new Rect(ox,oy,ox+width,oy+height);
			break;
		}

		//保存当前控件位置
		if(null == mPrevItemRect){
			mPrevItemRect = new Rect(mCurItemRect);
		}else{
			mPrevItemRect.set(mCurItemRect);
		}
		//更新当前控件位置
		mCurItemRect.set(tmpRect);
	}



	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.
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
			show = popedomIsShow(mDRInfo.getmShowInfo());
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
	public void realseMemeory() {
		SKTimer.getInstance().getBinder().onDestroy(TimerCallback);
		SKThread.getInstance().getBinder().onDestroy(TaskCallback, mChangeTaskID);
		
		if (showByAddr) {
			SKPlcNoticThread.getInstance().destoryCallback(showCall);
		}
		
		if(null != mDRInfo && 1 == mDRInfo.getUsePosCtrl()){//若位置受到地址控制
			SKPlcNoticThread.getInstance().destoryCallback(notifyXAddrDataCallback);
			SKPlcNoticThread.getInstance().destoryCallback(notifyYAddrDataCallback);
		}
		if(null != mDRInfo && 1 == mDRInfo.getUseSizeCtrl()){//若大小受到地址控制
			SKPlcNoticThread.getInstance().destoryCallback(notifyWAddrDataCallback);
			SKPlcNoticThread.getInstance().destoryCallback(notifyHAddrDataCallback);
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
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getDataFromDatabase() {
//		mChangeTaskID = SKThread.getInstance().getBinder().onRegister(TaskCallback);
//		//发送读取控件数据命令
//		SKThread.getInstance().getBinder().onTask(MODULE.SKDYNAMICRECT, TASK.READ_ITEM_DATA, mChangeTaskID);
	}
	
	public void getDataFromDatabaseDummy() {

//		if(0 > mItemID){//无效的控件ID
//			Log.e("SKDynamicRect","getDataFromDatabase: invalid mItemID: " + mItemID);
//			return;
//		}
//
//		if(null != mDRInfo){
//			Log.i("SKDynamicRect","getDataFromDatabase: mDRInfo is not null");
//			return;
//		}
//
//		DynamicRectBiz tmpDRBiz = new DynamicRectBiz();
//
//		mDRInfo = tmpDRBiz.select(mItemID); //从数据获取实例
//		if(null == mDRInfo){
//			Log.e("SKDynamicRect","getDataFromDatabase: mDRInfo create failed!");
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
	SKPlcNoticThread.IPlcNoticCallBack notifyXAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleXAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理x地址数据通知回调
	 * */
	private Vector<Short> mXAddrNoticeData = null; //存放通知数据
	public void handleXAddrDataCallback(Vector<Byte> nStatusValue){
		
		if(nStatusValue.isEmpty()){
			return ;
		}
		if (mXAddrNoticeData == null)
		{
			mXAddrNoticeData = new Vector<Short>();
		}else{
			mXAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mXAddrNoticeData);//将字节转换成短整型
		if(!mXAddrNoticeData.isEmpty()){
			mXData       = mXAddrNoticeData.get(0); //取第一个数据
			mIsNewXData  = true;  
		}
		return;
//		byte[] nTmpBytes = new byte[nStatusValue.size()];
//		for(int i = 0; i < nStatusValue.size(); i++){//取出所有字节
//			nTmpBytes[i] = nStatusValue.get(i);
//		}
//		short[] value = null;
//		value = PlcRegCmnStcTools.bytesToShorts(nTmpBytes);//将字节转换成短整型
//		
//		mXData       = value[0]; //取第一个数据
//		mIsNewXData  = true;     //标的有新的X数据
//		return;
	} 	

	/**
	 * Y地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyYAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleYAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理Y地址数据通知回调
	 * */
	private Vector<Short> mYAddrNoticeData = null; //存放通知数据
	public void handleYAddrDataCallback(Vector<Byte> nStatusValue){
		
		if(nStatusValue.isEmpty()){
			return;
		}
		
		if (mYAddrNoticeData == null)
		{
			mYAddrNoticeData = new Vector<Short>();
		}else{
			mYAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mYAddrNoticeData);//将字节转换成短整型
		if(!mYAddrNoticeData.isEmpty()){
			mYData       = mYAddrNoticeData.get(0); //取第一个数据
			mIsNewYData  = true;  
		}
		return;
//		byte[] nTmpBytes = new byte[nStatusValue.size()];
//		for(int i = 0; i < nStatusValue.size(); i++){//取出所有字节
//			nTmpBytes[i] = nStatusValue.get(i);
//		}
//		short[] value = null;
//		value = PlcRegCmnStcTools.bytesToShorts(nTmpBytes);//将字节转换成短整型
//		
//		mYData       = value[0]; //取第一个数据
//		mIsNewYData  = true;     //标的有新的数据
//
//		return;
	} 

	/**
	 * W地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyWAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleWAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理W地址数据通知回调
	 * */
	private Vector<Short> mWAddrNoticeData = null; //存放通知数据
	public void handleWAddrDataCallback(Vector<Byte> nStatusValue){

		if(nStatusValue.isEmpty()){
			return;
		}
		if (mWAddrNoticeData == null)
		{
			mWAddrNoticeData = new Vector<Short>();
		}else{
			mWAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mWAddrNoticeData);//将字节转换成短整型
		if(!mWAddrNoticeData.isEmpty()){
			mWData       = mWAddrNoticeData.get(0); //取第一个数据
			mIsNewWData  = true;  
		}
		return;
				
//		byte[] nTmpBytes = new byte[nStatusValue.size()];
//		for(int i = 0; i < nStatusValue.size(); i++){//取出所有字节
//			nTmpBytes[i] = nStatusValue.get(i);
//		}
//		short[] value = null;
//		value = PlcRegCmnStcTools.bytesToShorts(nTmpBytes);//将字节转换成短整型
//		
//		mWData       = value[0]; //取第一个数据
//		mIsNewWData  = true;     //标的有新的数据
//
//		return;
	} 

	/**
	 * H地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyHAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleHAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理W地址数据通知回调
	 * */
	private Vector<Short> mHAddrNoticeData = null; //存放通知数据
	public void handleHAddrDataCallback(Vector<Byte> nStatusValue){
		
		if(nStatusValue.isEmpty()){
			return;
		}

		if (mHAddrNoticeData == null)
		{
			mHAddrNoticeData = new Vector<Short>();
		}else{
			mHAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mHAddrNoticeData);//将字节转换成短整型
		if(!mHAddrNoticeData.isEmpty()){
			mHData       = mHAddrNoticeData.get(0); //取第一个数据
			mIsNewHData  = true;  
		}
		return;
		
//		byte[] nTmpBytes = new byte[nStatusValue.size()];
//		for(int i = 0; i < nStatusValue.size(); i++){//取出所有字节
//			nTmpBytes[i] = nStatusValue.get(i);
//		}
//		short[] value = null;
//		value = PlcRegCmnStcTools.bytesToShorts(nTmpBytes);//将字节转换成短整型
//
//		mHData       = value[0]; //取第一个数据
//		mIsNewHData  = true;     //标的有新的数据
//		return;
	}


	@Override
	public void initGraphics() {
		
		if(null == mDRInfo){ //若数据实体为空
			if (null == mChangeTaskID) {
				getDataFromDatabase();
			}
			isInitStateOK = false;
			return;//不做初始操作
		}
		isInitStateOK = true;
		if( 0 != mPaintState && 2 != mPaintState){//若不是初始态，也不是重绘态
			return;//不做初始操作
		}
		
		if(0 == mPaintState){ 

			//初始化待绘制的矩形
			initDynamicRect(); 

			//初始化矩形控件
			initItemRect();

			//初始化控件信息
			initItem();
			
			//初始化区域，保留代码，不可删除
			//initArea();   
		}
		
		mTheItem.rect       = new Rect(mPrevItemRect);
		mTheItem.mMoveRect  = new Rect(mCurItemRect);
		
		updateDynamicRect();
		
		
//		if (aBiz==null) {
//			aBiz=new AddrPropBiz();
//		}
		if (mDRInfo.getmShowInfo() != null) {

			if (mDRInfo.getmShowInfo().isbShowByAddr()) {
				if (mDRInfo.getmShowInfo().getnAddrId() > 0) {
					// 受地址控制
					showByAddr = true;
					//showAddr=aBiz.selectById(mDRInfo.getmShowInfo().getnAddrId());
				}
			}
			if (mDRInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
			itemIsShow();
		}	
		
		SKSceneManage.getInstance().onRefresh(mTheItem);
		
		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mDRInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				//SKPlcNoticThread.getInstance().addNoticProp(showAddr,showCall, true);
				SKPlcNoticThread.getInstance().addNoticProp(mDRInfo.getmShowInfo().getShowAddrProp(),showCall, true);
			} else {
				//SKPlcNoticThread.getInstance().addNoticProp(showAddr,showCall, false);
				SKPlcNoticThread.getInstance().addNoticProp(mDRInfo.getmShowInfo().getShowAddrProp(),showCall, false);
			}
		}
		
		if(1 == mDRInfo.getUsePosCtrl()){//若位置受到地址控制
			SKPlcNoticThread.getInstance().addNoticProp(mDRInfo.getXDataAddr(), notifyXAddrDataCallback, false);   //绑定X地址数据回调
			SKPlcNoticThread.getInstance().addNoticProp(mDRInfo.getYDataAddr(), notifyYAddrDataCallback, false);   //绑定Y地址数据回调
		}
		if(1 == mDRInfo.getUseSizeCtrl()){//若大小受到地址控制
			SKPlcNoticThread.getInstance().addNoticProp(mDRInfo.getWDataAddr(), notifyWAddrDataCallback, false);   //绑定W地址数据回调
			SKPlcNoticThread.getInstance().addNoticProp(mDRInfo.getHDataAddr(), notifyHAddrDataCallback, false);   //绑定H地址数据回调
		}

		
		if( 0 == mPaintState || 2 == mPaintState){//若是初始态或重绘态
			mChangeTaskID = SKThread.getInstance().getBinder().onRegister(TaskCallback);
			SKTimer.getInstance().getBinder().onRegister(TimerCallback);
		}
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			if(mItemID != itemId){//控件ID匹配失败
				return false;
			}

			drawDynamicRect(canvas);//绘制动态矩形

			mPaintState    = 1;    //进入绘制态

			canRefreshFlag = true; //标定当前可以再次刷新

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
		if(100 == xmovedata){
			xmovedata = 200;
		}else if(200 == xmovedata){
			xmovedata = 300;
		}else if(300 == xmovedata){
			xmovedata = 100;
		}

		if(100 == ymovedata){
			ymovedata = 200;
		}else if(200 == ymovedata){
			ymovedata = 300;
		}else if(300 == ymovedata){
			ymovedata = 100;
		}


		if(wmovedata == 50){
			wmovedata = 100;
		}else if(wmovedata == 100){
			wmovedata = 150;
		}else if(wmovedata == 150){
			wmovedata = 50;
		}

		if(hmovedata == 50){
			hmovedata = 100;
		}else if(hmovedata == 100){
			hmovedata = 150;
		}else if(hmovedata == 150){
			hmovedata = 50;
		}
		
		
		Vector<Integer> dataList   = null;
		SEND_DATA_STRUCT mSendData = null;
		
		if(1 == mDRInfo.getUsePosCtrl()){//位置数据变化
			dataList = new Vector<Integer>();
			dataList.add(xmovedata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDRInfo.getXDataAddr(), dataList, mSendData); //写x坐标

			dataList = new Vector<Integer>();
			dataList.add(ymovedata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDRInfo.getYDataAddr(), dataList, mSendData); //写Y坐标
		}

		if(1 == mDRInfo.getUseSizeCtrl()){//若大小受到地址控制
			dataList = new Vector<Integer>();
			dataList.add(wmovedata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDRInfo.getWDataAddr(), dataList, mSendData); //写宽度数据

			dataList = new Vector<Integer>();
			dataList.add(hmovedata); //保存新的数据
			mSendData  = new SEND_DATA_STRUCT();
			mSendData.eDataType         = DATA_TYPE.INT_16;                   //数据类型
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
			PlcRegCmnStcTools.setRegIntData(mDRInfo.getHDataAddr(), dataList, mSendData); //写高度数据
		}

	}//End of change
	
}