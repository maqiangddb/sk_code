package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.EllipseItem;
import com.android.Samkoonhmi.model.DynamicCircleInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TASK;

/**
 * 动态圆形
 * @author 魏 科
 * @date   2012-06-14
 * */
public class SKDynamicCircle extends SKGraphCmnShow implements IItem{

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
	
	private boolean mIsNewXData;   //是否有新的X数据
	private boolean mIsNewYData;   //是否有新的Y数据
	private boolean mIsNewRData;   //是否有新的半径数据
	
	private boolean  show       = true;   // 是否可显现
	private boolean  showByAddr = false;  // 是否注册显现地址
	private boolean  showByUser = false;  // 是否受用户权限控件
//	private AddrProp showAddr;    //显现地址
//	private AddrPropBiz aBiz;     //地址值查询类

	public SKDynamicCircle(int itemid, int sceneid,DynamicCircleInfo info){

		mItemID  = itemid;
		mSceneID = sceneid; 

		mPaintRef  = new Paint();
		mPaintRef.setDither(true);
		mPaintRef.setAntiAlias(true);
		this.mDCInfo=info;
		
		mTheItem = new SKItems();
		mTheItem.nZvalue = mDCInfo.getZValue(); 
		mTheItem.itemId  = mItemID;
		mTheItem.sceneId = mSceneID;
		mTheItem.nCollidindId = mDCInfo.getCollidindId();
		mTheItem.mGraphics=this;
		
		//新建背景矩形实例
		if(null == mCurItemRect){
			mCurItemRect  = createInitRect();
		}

		if(null == mPrevItemRect){
			mPrevItemRect = new Rect(mCurItemRect);
		}

		if (mDCInfo.getmShowInfo() != null) {

			if (mDCInfo.getmShowInfo().isbShowByAddr()) {
				if (mDCInfo.getmShowInfo().getnAddrId() > 0) {
					// 受地址控制
					showByAddr = true;
				}
			}
			if (mDCInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
		}		
		
		mTheItem.rect      = new Rect(mPrevItemRect);
		mTheItem.mMoveRect = new Rect(mCurItemRect); 
		
		initDynamicCircle();
		
		// 注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mDCInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mDCInfo.getmShowInfo().getShowAddrProp(), showCall,
						true,sceneid);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						mDCInfo.getmShowInfo().getShowAddrProp(), showCall,
						false,sceneid);
			}
		}

		if (1 == mDCInfo.getUsePosCtrl()) {// 若位置受到地址控制
			SKPlcNoticThread.getInstance().addNoticProp(
					mDCInfo.getCpXDataAddr(), notifyCxAddrDataCallback, false,sceneid); // 绑定X地址数据回调
			SKPlcNoticThread.getInstance().addNoticProp(
					mDCInfo.getCpYDataAddr(), notifyCyAddrDataCallback, false,sceneid); // 绑定Y地址数据回调
		}

		if (1 == mDCInfo.getUseSizeCtrl()) {// 若大小受到地址控制
			SKPlcNoticThread.getInstance()
					.addNoticProp(mDCInfo.getRadiusDataAddr(),
							notifyRAddrDataCallback, false,sceneid); // 绑定半径地址数据回调
		}
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
	 * 根据圆形数据构造圆形的矩形区域
	 * */
	public Rect createInitRect(){

		short xp  = mDCInfo.getnAreaLp();
		short yp  = mDCInfo.getnAreaTp();
		short width   = mDCInfo.getnAreaWidth();
		short height = mDCInfo.getnAreaHeight();
		Rect tmpRect = new Rect(xp,yp,xp+width,yp+height);

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
		short rWidth = 0;
		short rHeight = 0;
		//计算新的圆形数据
		if( mIsNewXData){
			cx = (short) (mDCInfo.getCpXpos() + mXData);
		}else{
			cx = (short) (mCurItemRect.centerX());
		}
		
		if(mIsNewYData){
			cy = (short) (mDCInfo.getCpYpos() + mYData);
		}else{
			cy = (short) (mCurItemRect.centerY());
		}
		if(mIsNewRData){
			rWidth  = (short)  mRData;
			rHeight = (short)   mRData;
			if(0 > rWidth || 0> rHeight){
				rHeight = 0;
				rWidth = 0 ;
			}
		}else{
			rWidth  = (short) (mCurItemRect.width()/2);
			rHeight = (short) (mCurItemRect.height()/2);
		}
			
		//计算矩形信息
		short left  =  (short)(cx - rWidth); 
		short right =  (short)(cx + rWidth);
		short top   =  (short)(cy - rHeight);
		short bottom=  (short)(cy + rHeight);

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
	 * 
	 **/
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
		
		PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCxAddrNoticeData);
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
		
		PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCyAddrNoticeData);
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
		
		PlcRegCmnStcTools.bytesToShorts(nStatusValue,mCrAddrNoticeData);//将字节转换成短整型
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
			return;//不做初始操作
		}
		if( 0 != mPaintState && 2 != mPaintState){//若不是初始态，也不是重绘态
			return;//不做初始操作
		}
		
		if(0 == mPaintState){ //若为初始态
			
			//初始化背景矩形
			initDynamicCircle();
  
		}		
		
		mTheItem.rect      = new Rect(mPrevItemRect);
		mTheItem.mMoveRect = new Rect(mCurItemRect); 
		
		updateDynamicCircle();  //更新动态圆图像内容		
		
		
		SKSceneManage.getInstance().onRefresh(mTheItem);
		
		itemIsShow();
		
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

	/**
	 * 获取控件属性接口
	 */
	public IItem getIItem(){
		return this;
	}
	
	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if(mDCInfo!=null){
			return mDCInfo.getnAreaLp();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if(mDCInfo!=null){
			return mDCInfo.getnAreaTp();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if(mDCInfo!=null){
			return mDCInfo.getnAreaWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if(mDCInfo!=null){
			return mDCInfo.getnAreaHeight();
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
		if(mDCInfo!=null){
			return getColor(mDCInfo.getFillColor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if(mDCInfo!=null){
			return getColor(mDCInfo.getRimColor());
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
		if (mDCInfo != null) {
			if (x == mDCInfo.getnAreaLp()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo().getnSceneWidth()) {
				return false;
			}
			int l=mDCInfo.getnAreaLp();
			mDCInfo.setnAreaLp((short)x);
			mDCInfo.setCpXpos((short)(mDCInfo.getCpXpos()+x-l));
			mCurItemRect.left=x;
			mCurItemRect.right=mCurItemRect.right+x-l;
			
			updateItemRect();
			mTheItem.rect      = new Rect(mPrevItemRect);
			mTheItem.mMoveRect = new Rect(mCurItemRect); 
			updateDynamicCircle();  //更新动态圆图像内容
			
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if(mDCInfo!=null){
			if(y==mDCInfo.getnAreaTp()){
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int t = mDCInfo.getnAreaTp();
			mDCInfo.setnAreaTp((short)y);
			mDCInfo.setCpYpos((short)(mDCInfo.getCpYpos()+y-t));
			mCurItemRect.top=y;
			mCurItemRect.bottom=mCurItemRect.bottom+y-t;
			
			updateItemRect();
			mTheItem.rect      = new Rect(mPrevItemRect);
			mTheItem.mMoveRect = new Rect(mCurItemRect); 
			updateDynamicCircle();  //更新动态圆图像内容
			
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (mDCInfo != null) {
			if (w == mDCInfo.getnAreaWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int len=w-mDCInfo.getnAreaWidth();
			mDCInfo.setnAreaWidth((short)w);
			mDCInfo.setCpXpos((short)(mDCInfo.getCpXpos()+len));
			mCurItemRect.right=mCurItemRect.right+len;
			
			updateItemRect();
			mTheItem.rect      = new Rect(mPrevItemRect);
			mTheItem.mMoveRect = new Rect(mCurItemRect); 
			updateDynamicCircle();  //更新动态圆图像内容
			
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}
	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (mDCInfo != null) {
			if (h == mDCInfo.getnAreaHeight()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int len=h-mDCInfo.getnAreaHeight();
			mDCInfo.setnAreaHeight((short)h);
			mDCInfo.setCpYpos((short)(mDCInfo.getCpYpos()+len));
			mCurItemRect.bottom=mCurItemRect.bottom+len;
			
			updateItemRect();
			mTheItem.rect      = new Rect(mPrevItemRect);
			mTheItem.mMoveRect = new Rect(mCurItemRect); 
			updateDynamicCircle();  //更新动态圆图像内容
			
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemForecolor(int id,short r,short g,short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id,short r,short g,short b) {
		// TODO Auto-generated method stub
		if (mDCInfo==null) {
			return false;
		}
		
		int color=Color.rgb(r, g, b);
		
		if (color==mDCInfo.getFillColor()) {
			return true;
		}
		mDCInfo.setFillColor(color);
		mDynamicCircle.setBackColor(color);
		SKSceneManage.getInstance().onRefresh(mTheItem);
		return false;
	}

	@Override
	public boolean setItemLineColor(int id,short r,short g,short b) {
		// TODO Auto-generated method stub
		if(mDCInfo==null){
			return false;
		}
		
		int color=Color.rgb(r, g, b);
		
		if (color==mDCInfo.getRimColor()) {
			return true;
		}
		mDCInfo.setRimColor(color);
		mDynamicCircle.setLineColor(color);
		SKSceneManage.getInstance().onRefresh(mTheItem);
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if(v==show){
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(mTheItem);
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
	public boolean setItemText(int id, int lid,String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO Auto-generated method stub
		if (mDCInfo!=null) {
			if (mDCInfo.getAlpha()==alpha) {
				return true;
			}
			mDCInfo.setAlpha((short)alpha);
			mDynamicCircle.setAlpha(alpha);
			SKSceneManage.getInstance().onRefresh(mTheItem);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 获取RGB颜色
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
}//End of class

