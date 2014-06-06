package com.android.Samkoonhmi.skgraphics.plc.show;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.GifViewerInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;


/**
 * GIF动画显示类，用于在场景中显示GIF动画
 * @author 魏 科
 * @date   2012-06-04
 * */
public class SKGifViewer  extends SKGraphCmnShow implements IItem{

	private int      mItemID;          //控件ID
	private int      mSceneID;         //场景ID

	private short    mPaintState = 0;  //绘制状态，约定：0初始态，1绘制态，2待重画态

	private RectItem mBackRect;        //图片显示器背景矩形

	private String   mTaskID="";          //任务签名

	private GifViewerInfo  mGVInfo;    //图片显示器数据实体

	private  Rect    mAreaRect;        //位图矩形实例

	private  Movie   mMovie;           //GIF播放器
	private  long    mMovieStart;      //起始播放时间
	private  long    mPlayCount;         //当前帧组播放次数
	private  boolean mNeedReset = false; //是否需要重置初始播放时间
	private  int     mFrameTime;         //帧组内相对播放时间
	private  int     mFrameDur;          //帧组总时间
	private  long     mLastAbsPTime;      //追后一次播放的时间

	private  Canvas  mInnerCanvas;     //用于缩放
	private  Bitmap  mInnerBitmap;     //用于存放原帧
	private Paint    mPaintRef;          //Paint引用

	private SKItems mTheItem;

	private boolean isInitStateOK  = true;  //图形初始化正常

	private boolean  show       = true;   // 是否可显现
	private boolean  showByAddr = false;  // 是否注册显现地址
	private boolean  showByUser = false;  // 是否受用户权限控件

	private boolean mIsCtrlToPlay ;        //当前是否允许播放
	private boolean mIsNewCtrlData;        //是否有新的控制数据
	
	private boolean  mDebug = false;

	//private AddrProp showAddr;    //显现地址
	//private AddrPropBiz aBiz;     //地址值查询类


	public SKGifViewer(int itemid, int sceneid,GifViewerInfo info) {

		mItemID  = itemid;
		mSceneID = sceneid;
		isInitStateOK = true;
		mPaintRef     = new Paint();
		mPaintRef.setDither(true);
		mPaintRef.setAntiAlias(true);
		this.mGVInfo=info;
		
		mTheItem = new SKItems();
		mTheItem.nZvalue = mGVInfo.getZValue();
		mTheItem.itemId  = mItemID;
		mTheItem.sceneId = mSceneID;
		mTheItem.nCollidindId = mGVInfo.getCollidindId();
		mTheItem.mGraphics=this;
		
		mAreaRect   = new Rect(mGVInfo.getLp(),mGVInfo.getTp(),mGVInfo.getLp()+mGVInfo.getWidth(),mGVInfo.getTp()+mGVInfo.getHeight());

		mTheItem.rect   = mAreaRect; //绑定矩形位置
		
		if (mGVInfo.getmShowInfo() != null) {

			if (mGVInfo.getmShowInfo().isbShowByAddr()) {
				if (mGVInfo.getmShowInfo().getnAddrId() > 0) {
					// 受地址控制
					showByAddr = true;
					//showAddr=aBiz.selectById(mGVInfo.getmShowInfo().getnAddrId());
				}
			}
			if (mGVInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
		}


		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mGVInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				//SKPlcNoticThread.getInstance().addNoticProp(showAddr,showCall, true);
				SKPlcNoticThread.getInstance().addNoticProp(mGVInfo.getmShowInfo().getShowAddrProp(),showCall, true,sceneid);
			} else {
				//SKPlcNoticThread.getInstance().addNoticProp(showAddr,showCall, false);
				SKPlcNoticThread.getInstance().addNoticProp(mGVInfo.getmShowInfo().getShowAddrProp(),showCall, false,sceneid);
			}
		}

		if(1 == mGVInfo.getIsBitCtrl()){//若受位控制
			SKPlcNoticThread.getInstance().addNoticProp(mGVInfo.getCtrlAddr(), notifyCtrlAddrDataCallback, false,sceneid); //绑定半径地址数据回调
		}
	}



	/**
	 * 初始化GIF播放器
	 * */
	public void InitMovie(){

		
		if(null == mGVInfo){
			Log.e("SKGifViewer","InitMovie: mGVInfo is null");
			return;
		}

		mMovie = SKGifBufPool.getInstance().getFromPool(mGVInfo.getGifPath());//尝试从缓存池中取数据
	
		if(null == mMovie){
			
			String tmpGifPath = mGVInfo.getGifPath();
			if(null == tmpGifPath){
				Log.e("SKGifViewer","InitMovie:Gif file path is null");
				return;
			}

			File file=new File(tmpGifPath);
			if(!file.exists()) {
				Log.e("SKGifViewer","InitMovie:Gif file is not exist!");
				return;
			}

			if(file.isDirectory()){
				Log.e("SKGifViewer","InitMovie:Gif file path is directory!");
				return;				
			}

			FileInputStream  is = null;
			try {
				is =  new FileInputStream(tmpGifPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			if(null == is){
				Log.e("SKGifViewer","InitMovie:input stream is null");
				return;
			}

			ByteArrayOutputStream baos=new ByteArrayOutputStream(8192);
			byte[] buffer = new byte[8192];
			int len;
			try {
				while ((len = is.read(buffer)) >= 0) {
					baos.write(buffer, 0, len);
				}
			} catch (java.io.IOException e) {
			}
			byte byteseq[]=baos.toByteArray();
			mMovie = Movie.decodeByteArray(byteseq, 0, byteseq.length);
			SKGifBufPool.getInstance().addToPool(mGVInfo.getGifPath(), mMovie);
		}

		if(null == mInnerBitmap){//设置内置缩放位图/画布
			if((0 != mGVInfo.getGifWidth()) && (0 != mGVInfo.getGifHeight())){
				mInnerBitmap  = Bitmap.createBitmap(mGVInfo.getGifWidth(), mGVInfo.getGifHeight(),Config.ARGB_8888);//原帧位图
				mInnerCanvas  = new Canvas(mInnerBitmap);
			}else{
				Log.e("SKGifViewer","InitMovie:Invalid Gif image size, width: "+ mGVInfo.getGifWidth()+ " height: " + mGVInfo.getGifHeight());
			}
		}
	}

	/**
	 * 绘制动画的一帧
	 * */
	public void drawFrame(Canvas canvas)
	{
		if(null == mGVInfo){
			Log.e("SKGifViewer","drawFrame: mGVInfo is null!");
			return;
		}

		if(mMovie != null) {
			if(mDebug){
				Log.i("SKGifViewer"," drawFrame ItemID: " + this.mItemID);
			}
			if(0 == mFrameDur){
				mFrameDur = mMovie.duration(); 
				if (mFrameDur == 0) {
					mFrameDur = 1000;
				}
			}
			mLastAbsPTime = android.os.SystemClock.uptimeMillis();

			if(0 == mMovieStart){//若需要重置播放起始时间
				mMovieStart = mLastAbsPTime;
			}

			if(1 == mGVInfo.getIsBitCtrl()){//若受位控制
				if(true == isCtrlToPlay() || 0 == mPaintState){
					//更新帧组相对时间
					mFrameTime = (int)((mLastAbsPTime - mMovieStart) % mFrameDur);
					if(0 < mGVInfo.getRCount()){//若设置了播放次数
						mPlayCount = Math.abs((mLastAbsPTime-mMovieStart)/mFrameDur);
						if(mPlayCount < mGVInfo.getRCount()){//若播放次数小于限定次数
							mInnerBitmap.eraseColor(Color.argb(0, 255, 255, 255));
							mMovie.setTime(mFrameTime); 	
							mMovie.draw(mInnerCanvas, 0, 0);
						}else {
							mNeedReset = true; //需要重置播放起始时间
						}//End of:if(mPlayCount < mGVInfo.getRCount()) 				
					}else{ 
						mInnerBitmap.eraseColor(Color.argb(0, 255, 255, 255));
						mMovie.setTime(mFrameTime);
						mMovie.draw(mInnerCanvas, 0, 0);
					}//End of: if(0 < mGVInfo.getRCount())
				}
			}else{//不受控
				//更新帧组相对时间
				mFrameTime = (int)((mLastAbsPTime - mMovieStart) % mFrameDur);
				if(null == mInnerBitmap){
					return ;
				}
				if(0 < mGVInfo.getRCount()){//若设置了播放次数
					mPlayCount = Math.abs((mLastAbsPTime-mMovieStart)/mFrameDur);
					if(mPlayCount < mGVInfo.getRCount()){//若播放次数小于限定次数
						mInnerBitmap.eraseColor(Color.argb(0, 255, 255, 255));
						mMovie.setTime(mFrameTime);
						mMovie.draw(mInnerCanvas, 0, 0);
					}else{
						mNeedReset = true;
					}//End of:if(mPlayCount < mGVInfo.getRCount()) 				
				}else{
					mInnerBitmap.eraseColor(Color.rgb(0, 0, 0));
					mMovie.setTime(mFrameTime);
					mMovie.draw(mInnerCanvas, 0, 0);
				}//End of: if(0 < mGVInfo.getRCount())

			}

			//将缩放后的位图显示画面  
			canvas.drawBitmap(mInnerBitmap, null, mAreaRect, mPaintRef);

		}

	}

	@Override
	public boolean isShow() {
		itemIsShow();
		if(mDebug){
			Log.i("SKGifViewer",mTaskID+" isShow : isShow : " + show);
		}
		SKSceneManage.getInstance().onRefresh(mTheItem);
		return show;
	}

	/**
	 * 控件是否可以显现
	 */
	private void itemIsShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(mGVInfo.getmShowInfo());
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
	public void getDataFromDatabase() {

	}

	/**
	 * 在后台读取控件信息
	 * */
	public void getDataFromDatabaseDummy() {

	}

	@Override
	public void setDataToDatabase() {

	}

	/**
	 * 定时器处理函数
	 * */
	public void handleSChangeTimerUpdate(){
		if (mTaskID.equals("")) {
			mTaskID = SKThread.getInstance().getBinder().onRegister(SChangeTaskCallback);
		}
		SKThread.getInstance().getBinder().onTask(MODULE.SKGIFVIEWER,TASK.GIFVIEWER_CHANGE,mTaskID);

	}

	/**
	 * 状态变化的定时器回调函数
	 */
	SKTimer.ICallback SChangeTimerCallback=new SKTimer.ICallback() {

		@Override
		public void onUpdate() {
			handleSChangeTimerUpdate();
		}
	};//End of: SChangeTimerCallback

	/**
	 * 线程回调处理函数 
	 * */
	public void handleSChangeTaskUpdate(){
		if(mDebug){
			Log.i("SKGifViewer",mItemID+" handleSChangeTaskUpdate---------");
		}
		SKSceneManage.getInstance().onRefresh(mTheItem);
	}

	/**
	 * 状态变化线程回调接口
	 */
	SKThread.ICallback  SChangeTaskCallback =new SKThread.ICallback() {

		@Override
		public void onUpdate(String msg,int taskId){
			//Log.d(TAG, msg);
		}

		@Override
		public void onUpdate(int msg, int taskId) {
			if(TASK.READ_ITEM_DATA == taskId){
				getDataFromDatabaseDummy();
			}else{
				handleSChangeTaskUpdate();
			}
		}

		@Override
		public void onUpdate(Object msg,int taskId) {
			// TODO Auto-generated method stub

		}
	};//End of: SChangeTaskCallback

	@Override
	public void realseMemeory() {
		SKTimer.getInstance().getBinder().onDestroy(SChangeTimerCallback);
		SKThread.getInstance().getBinder().onDestroy(SChangeTaskCallback, mTaskID);
		
		if(mDebug){
			Log.i("SKGifViewer", mItemID + " realseMemeory destroy mTaskID: " + mTaskID);
		}

		mTaskID = "";
		if(1 == mPaintState){//若当前至少绘制过一次
			mPaintState = 2; //控件进入待重绘状态，下次在initGraph中不会再初始化控件参数
		}else if(2 == mPaintState){
			mPaintState = 2;
		}else{
			mPaintState = 0;
		}
	}


	@Override
	public void initGraphics() {

		if(null == mGVInfo){
			return;
		}

		mTaskID="";
		isInitStateOK   = true; 
		if( 0 != mPaintState && 2 != mPaintState){//若不是初始态，也不是重绘态
			return;//不做初始操作
		}
		
		if(null == mMovie){
			InitMovie();
		}

		if(0 == this.mPaintState){ //若第一次绘制
			
		}else if(null != mGVInfo && 0 < mGVInfo.getRCount()&& true == mIsCtrlToPlay){//按次数播放的GIF，每次进来都重新播放
			mInnerBitmap.eraseColor(Color.argb(0, 255, 255, 255));//消除尾帧
			mMovieStart = 0;
			mFrameTime  = 0;
			if(true == mNeedReset){
				mNeedReset = false;
			}
		}else if(null != mGVInfo && 0 < mGVInfo.getRCount()&& false == mIsCtrlToPlay){//按次数播放的GIF，每次进来都重新播放
			mMovieStart = 0;
			mFrameTime  = 0;
		}

		mTheItem.rect   = mAreaRect; //绑定矩形位置

		if( 0 == mPaintState || 2 == mPaintState){//若是初始态或重绘态
			SKTimer.getInstance().getBinder().onRegister(SChangeTimerCallback);
			if(mDebug){
				Log.i("SKGifViewer","Register SChangeTaskCallback Item id: " + mItemID + " mTaskID: "+mTaskID);
			}
		}
		
		itemIsShow();
		
		//初始化时无条件刷新一次
		SKSceneManage.getInstance().onRefresh(mTheItem);

	}


	/**
	 * 地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyCtrlAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleCtrlAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理地址数据通知回调
	 * */
	public void handleCtrlAddrDataCallback(Vector<Byte> nStatusValue){

		if(nStatusValue.isEmpty()){
			return ;
		}

		if(null == mGVInfo){
			return ;
		}
		if(mGVInfo.getValidBit() == (0x01&nStatusValue.get(0))){ //判断是否播放
			mIsCtrlToPlay = true; 
		}else{
			mIsCtrlToPlay = false;
		}
		mIsNewCtrlData  = true;                     //标定有新的数据到来

		if((true==mIsCtrlToPlay)&&(true == mNeedReset)){
			mMovieStart = 0; //重置播放时间
			mNeedReset = false;
		}else if((0 != mFrameDur) && (true==mIsCtrlToPlay)){//补帧操作
			adjustFrame();
		}
		return;
	}

	/**
	 * 补帧函数
	 * */
	public void adjustFrame(){
		
		long now = android.os.SystemClock.uptimeMillis();
		mMovieStart = now - mFrameTime;
	}

	/**
	 * 判断控制是否显示
	 * */
	public boolean isCtrlToPlay(){
		return mIsCtrlToPlay;
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {

		if (show) {
			if(mItemID != itemId){//控件ID不匹配
				return false;
			}
			drawFrame(canvas);//在传入的画布上绘制帧
			mPaintState = 1;//进入绘制态
			return true;
		}else{
			return false;
		}
	}

	@Override
	public IItem getIItem(){
		return this;
	}
	
	@Override
	public int getItemLeft(int id) {
		// TODO 自动生成的方法存根
		if (mGVInfo!=null) {
			return mGVInfo.getLp();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO 自动生成的方法存根
		if (mGVInfo!=null) {
			return mGVInfo.getTp();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO 自动生成的方法存根
		if(mGVInfo!=null){
			return mGVInfo.getWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO 自动生成的方法存根
		if(mGVInfo!=null){
			return mGVInfo.getHeight();
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
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO 自动生成的方法存根
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
		return false;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO 自动生成的方法存根
		if (mGVInfo!=null) {
			if (x<0||x>SKSceneManage.getInstance().getCurrentScene().nSceneWidth) {
				return false;
			}
			if (x==mGVInfo.getLp()) {
				return true;
			}
			mGVInfo.setLp((short)x);
			int l=mTheItem.rect.left;
			mTheItem.rect.left=x;
			mTheItem.rect.right=x-l+mTheItem.rect.right;
			mTheItem.mMoveRect=new Rect();
			
			mAreaRect.left=x;
			mAreaRect.right=mTheItem.rect.right;
			
			SKSceneManage.getInstance().onRefresh(mTheItem);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO 自动生成的方法存根
		if (mGVInfo!=null) {
			if (y<0||y>SKSceneManage.getInstance().getCurrentScene().nSceneHeight) {
				return false;
			}
			if (y==mGVInfo.getTp()) {
				return true;
			}
			mGVInfo.setTp((short)y);
			int t = mTheItem.rect.top;
			mTheItem.rect.top = y;
			mTheItem.rect.bottom = y - t + mTheItem.rect.bottom;
			mTheItem.mMoveRect=new Rect();
			
			mAreaRect.top=y;
			mAreaRect.bottom=mTheItem.rect.bottom;
			
			SKSceneManage.getInstance().onRefresh(mTheItem);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO 自动生成的方法存根
		if (mGVInfo!=null) {
			if(w<0||w>SKSceneManage.getInstance().getCurrentScene().nSceneWidth){
				return false;
			}
			if(w==mGVInfo.getWidth()){
				return true;
			}
			mGVInfo.setWidth((short)w);
			mTheItem.rect.right = w - mTheItem.rect.width() + mTheItem.rect.right;
			mTheItem.mMoveRect = new Rect();
			mAreaRect.right=mTheItem.rect.right;
			SKSceneManage.getInstance().onRefresh(mTheItem);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO 自动生成的方法存根
		if (mGVInfo!=null) {
			if (h<0||h>SKSceneManage.getInstance().getCurrentScene().nSceneHeight) {
				return false;
			}
			if(h==mGVInfo.getHeight()){
				return true;
			}
			mGVInfo.setHeight((short)h);
			mTheItem.rect.bottom = h - mTheItem.rect.height() + mTheItem.rect.bottom;
			mTheItem.mMoveRect = new Rect();
			mAreaRect.bottom=mTheItem.rect.bottom;
			SKSceneManage.getInstance().onRefresh(mTheItem);
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
		return false;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO 自动生成的方法存根
		if (v==show) {
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(mTheItem);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemPageUp(int id) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemPageDown(int id) {
		// TODO 自动生成的方法存根
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
		return false;
	}

	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO 自动生成的方法存根
		if (v==mIsCtrlToPlay) {
			return true;
		}
		mIsCtrlToPlay=v;
		SKSceneManage.getInstance().onRefresh(mTheItem);
		return true;
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
	
	

}//End of class