//import SKGraphCmnShow;
package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.AddrPropBiz;
import com.android.Samkoonhmi.databaseinterface.ImageViewerBiz;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.ImageViewerInfo;
import com.android.Samkoonhmi.model.PictureInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.StateBmpCache;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.HMIMODEL;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.ImageShowDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TASK;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;


/**
 * 图片显示类，用于在场景中显示图片
 * @author 魏 科
 * @date   2012-06-04
 * */
public class SKImageViewer  extends SKGraphCmnTouch implements IItem{

	private int      mItemID;          //控件ID 
	private int      mSceneID;         //场景ID

	private int      mPaintState    = 0;   //绘制状态，约定：0初始态，1绘制态，2待重画态

	private int      mFlickerState  = 0;   //当前闪烁状态

	private short    mCurSID;          //当前状态号
	private short    mIntervalCount;   //时间间隔计数器
	private short    mTripDirect = 0;  //往返循环的方向，约定：0，状态号升序方向；1，状态号降序方向

	private String   mTaskID=null;      //任务签名

	private Paint    mPaintRef;    //Paint引用     

	private ImageViewerInfo  mIVInfo;    //图片显示器数据实体


	private Rect    mAreaRect;      //位图矩形实例

	private SKItems mTheItem;       //控件层结构

	private boolean isInitStateOK  = true;  //图形初始化正常
	private boolean mIsCtrlToShow;  //控制是否需要显示
	private boolean mIsCtrlNewData; //控制 是否有新数据
	private short   mStateValue;    //从地址空间获取的状态值

	private boolean  show       = true;   // 是否可显现
	private boolean  showByAddr = false;  // 是否注册显现地址
	private boolean  showByUser = false;  // 是否受用户权限控件


	//以下是模拟数据
	private int   bytedata  = 0;
	private short shortdata = 15;

	public SKImageViewer(int itemid, int sceneid,ImageViewerInfo info) {

		mItemID  = itemid;
		mSceneID = sceneid; 

		isInitStateOK = true;

		mPaintRef  = new Paint();
		mPaintRef.setDither(true);
		mPaintRef.setAntiAlias(true);
		this.mIVInfo=info;
		
		mTheItem = new SKItems();
		mTheItem.nZvalue = mIVInfo.getZValue(); 
		mTheItem.itemId  = this.mItemID;
		mTheItem.sceneId = this.mSceneID;
		mTheItem.nCollidindId = mIVInfo.getCollidindId(); 
		mTheItem.mGraphics=this;
		
		mAreaRect   = new Rect(mIVInfo.getLp(),mIVInfo.getTp(),mIVInfo.getLp()+mIVInfo.getWidth(),mIVInfo.getTp()+mIVInfo.getHeight());
		mTheItem.rect   = mAreaRect;      //绑定矩形区域
		
		if(0 != mIVInfo.getChangeCondition()){//受地址控制
			if(null == mIVInfo.getWatchAddr()){
				Log.e("SKImageViewer","initGraphics: AddrProp is null");
				return;
			}
			SKPlcNoticThread.getInstance().addNoticProp(mIVInfo.getWatchAddr(), notifyAddrDataCallback, false,sceneid);//绑定地址数据回调
		}

		if (mIVInfo.getmShowInfo() != null) {

			if (mIVInfo.getmShowInfo().isbShowByAddr()) {
				if (mIVInfo.getmShowInfo().getnAddrId() > 0) {
					// 受地址控制
					showByAddr = true;
				}
			}
			if (mIVInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
			
		}
		
		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mIVInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mIVInfo.getmShowInfo().getShowAddrProp(), showCall,
						true,sceneid);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						mIVInfo.getmShowInfo().getShowAddrProp(), showCall,
						false,sceneid);

			}
		}

	}


	/**
	 * 擦除区域矩形
	 * */
	public void wipeArea(Canvas canvas){
		mPaintRef.setColor(mIVInfo.getBackColor());
		canvas.drawRect(mAreaRect, mPaintRef);
		mPaintRef.reset();
	}

	/**
	 * 根据图片路径解码图片信息
	 * @param  imgpath 图片路径
	 * @return         解码后的图片实例
	 * */ 
	public Bitmap decodeImgByPath(String imgpath){
		if(null == imgpath){
			Log.e("SKImageViewer","decodeImgByPath: Image path is null");
			return null;
		}

		File tmpfile = new File(imgpath);
		if(false == tmpfile.exists()){//处理图片文件不存在
			Log.e("SKImageViewer","decodeImgByPath: Image file not exist");
			return null;
		}

		//对图片文件解码
		Bitmap tmpBitmap = BitmapFactory.decodeFile(imgpath);
		if(null == tmpBitmap){//处理解码失败处理
			Log.e("SKImageViewer", "decodeImgByPath: Image decode failed");
			return null;
		}
		return tmpBitmap;
	}


	/**
	 * 查找与状态号对应的图片的路径信息
	 * @param  sid 状态号
	 * @return 图片路径
	 * */
	public String findImagePathBySID(short sid){
		String imgpath = null;
		if(null == mIVInfo.getPicPathArray()){
			Log.e("SKImageViewer","findImagePathBySID: PicPathArray is null");
			return imgpath;
		}

		ArrayList<PictureInfo> tmpList = null;
		tmpList = mIVInfo.getPicPathArray();
		if(null == tmpList){
			return imgpath;
		}

		for(short i = 0; i< tmpList.size(); i++){
			if(sid == tmpList.get(i).getnStatusId()){
				imgpath = tmpList.get(i).getsPath();
			}
		}//End of: for(short i = 0; i< mIVInfo.getPicPathArray().size(); i++)
		return imgpath;
	}

	/**
	 * 查找与值对应状态ID
	 * @param  value 值
	 * @return 状态号
	 * */
	public short findSIDByValue(short value){
		short sid = -1;

		if(null == mIVInfo.getStakeoutList()){
			Log.e("SKImageViewer","findSIDByValue: StakeoutList is null");
			return sid;
		}

		ArrayList<StakeoutInfo> tmpList = null;
		tmpList = mIVInfo.getStakeoutList();
		if(null == tmpList){
			return sid;
		}

		for(short i = 0; i<tmpList.size(); i++){
			if(value == tmpList.get(i).getnCmpFactor()){
				sid = tmpList.get(i).getnStatusId();	
			}
		}
		return sid;
	}
	private final Matrix mMatrix = new Matrix();
	private final Matrix mInverse = new Matrix();
	private int mLastWarpX = -9999; // don't match a touch coordinate
	private int mLastWarpY;
	private final float[] mVerts = new float[COUNT*2];
	private final float[] mOrig = new float[COUNT*2];
	private static final int WIDTH = 70;
	private static final int HEIGHT = 70;
	private static final int COUNT = (WIDTH + 1) * (HEIGHT + 1);
	private int mTouchX = -1;
	private int mTouchY = -1;
	private int mRenderHoldTime =0;


	private void WrapImg(float cx, float cy) {
		final float K = 10000;
		float[] src = mOrig;
		float[] dst = mVerts;
		for (int i = 0; i < COUNT*2; i += 2) {
			float x = src[i+0];
			float y = src[i+1];
			float dx = cx - x;
			float dy = cy - y;
			float dd = dx*dx + dy*dy;
			float d = FloatMath.sqrt(dd);
			float pull = K / (dd + 0.000001f);

			pull /= (d + 0.000001f);

			if (pull >= 1) {
				dst[i+0] = cx;
				dst[i+1] = cy;
			} else {
				dst[i+0] = x + dx * pull;
				dst[i+1] = y + dy * pull;
			}
		}
	}

	private static void setXY(float[] array, int index, float x, float y) {
		array[index*2 + 0] = x;
		array[index*2 + 1] = y;
	}

	/**
	 * 在图片显示区域绘制图片
	 * @param sid 状态序号
	 * */
	public void drawImage(Canvas canvas, short sid)
	{
		Bitmap tmpImg = null;

		if(0 > sid){//处理异常状态号
			Log.e("SKImageViewer", "drawImage: Invalid sid: " + sid);
			return;
		}

		if( (mIVInfo.getStatusTotal()>0) && (sid >= mIVInfo.getStatusTotal())){//处理异常状态号
			Log.e("SKImageViewer", "drawImage: Invalid sid: " + sid);
			return;			
		} 

		if(null == tmpImg){
			tmpImg = ImageFileTool.getBitmap(findImagePathBySID(sid));			
		}

		if(null == tmpImg){//图片解码失败
			//Log.e("SKImageViewer","drawImage: Image decode failed, mItemID: " + this.mItemID);
			return;
		}

		canvas.drawBitmap(tmpImg, null, mAreaRect, mPaintRef);
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
			show = popedomIsShow(mIVInfo.getmShowInfo());
			//Log.d("Image", "show="+show);
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

	}

	/**
	 * 定时器处理函数
	 * */
	public void handleSChangeTimerUpdate(){

		if (mTaskID==null) {
			mTaskID = SKThread.getInstance().getBinder().onRegister(SChangeTaskCallback);
		}
		SKThread.getInstance().getBinder().onTask(MODULE.IMAGEVIEWER,TASK.IMAGEVIEWER_CHANGE,mTaskID);

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
		//changeAddrData(); //调试代码，不可删除
		if(0 == mIVInfo.getChangeCondition()){//按时间间隔变化
			mIntervalCount += SKTimer.UPDATE_TIME;
			if(mIntervalCount == mIVInfo.getTimeInterval()){
				mIntervalCount = 0;
			}else{
				return;
			}
		}

		if((0 == mIVInfo.getFunType()) && (0 == mIVInfo.getChangeCondition())){//单幅按时间间隔闪烁
			if(1 == accessFlickerSstate(0,0)){ //当前为显示
				accessFlickerSstate(1,2);      //切换为隐藏
			}else if((2 == accessFlickerSstate(0,0)) || (0 == accessFlickerSstate(0,0)) ){ //当前为隐藏
				accessFlickerSstate(1,1);            //切换为显示
			}//End of:if(1 == mFlickerState)
			if (show) {
				SKSceneManage.getInstance().onRefresh(mTheItem);
			}
			return;
		}

		if((1 == mIVInfo.getFunType()) &&  0 == (mIVInfo.getChangeCondition())){//多幅按时间切换
			setNextAsCurSIDTime();  //将下一个状态设置为当前状态
			if (show) {
				SKSceneManage.getInstance().onRefresh(mTheItem);
			}
			return;
		}//End of:if(0 == mIVInfo.getFunType())


		if((0 == mIVInfo.getFunType()) && (1 == mIVInfo.getChangeCondition())){//单幅按位地址闪烁
			if(true == mIsCtrlNewData){    //新数据到来
				mIsCtrlNewData = false;    //不再是新数据
				if(true == isCtrlToShow()){//控制显示
					accessFlickerSstate(1,1);	   //标定当前需要显示图片				
				}else{//控制不显示
					accessFlickerSstate(1,2);      //标定当前需要隐藏图片
				}
				if (show) {
					SKSceneManage.getInstance().onRefresh(mTheItem);
				}
			}//End of:if(true == mIsCtrlNewData) 
			return;
		}

		if((1 == mIVInfo.getFunType()) && (0 != mIVInfo.getChangeCondition())){//多幅按位地址/字地址切换

			if(true == mIsCtrlNewData){ //新数据到来
				mIsCtrlNewData = false; //不再是新数据
				short oldSID = setNextAsCurSIDAddr(mStateValue);  //用新数据设定状态
				if(oldSID != mCurSID){  //前后状态不同则刷新
					if (show) {
						SKSceneManage.getInstance().onRefresh(mTheItem);
					}
				}//End of: if(oldSID != mCurSID)
			}
			return;
		}
	}//End of: handleSChangeTaskUpdate()



	/**
	 * 状态变化线程回调接口
	 */
	SKThread.ICallback  SChangeTaskCallback =new SKThread.ICallback() {

		@Override
		public void onUpdate(String msg,int taskId){
		}

		@Override
		public void onUpdate(int msg, int taskId) {
			handleSChangeTaskUpdate();
		}

		@Override
		public void onUpdate(Object msg,int taskId) {

		}
	};//End of: SChangeTaskCallback

	/**
	 * 判断控制是否显示
	 * */
	public boolean isCtrlToShow(){

		return mIsCtrlToShow;
	}

	/**
	 * 当按时间切换时切换到下一个状态
	 * */
	public short setNextAsCurSIDTime(){
		short oldSID = mCurSID;   //保存旧的状态号
		if(0 == mIVInfo.getChangeCondition()){//按时间间隔切换
			if(true == mIVInfo.isLoop()){//循环切换

				mCurSID++;
				if( mCurSID >= mIVInfo.getStatusTotal()){//超过了状态总数
					mCurSID = 0;
				}
			}else{//往返切换
				if(0 == mTripDirect){//升序
					mCurSID++;
					if((mCurSID) >= mIVInfo.getStatusTotal()){//下一状态超过状态上限
						mCurSID  = (short)(mIVInfo.getStatusTotal() - 2); 
						mTripDirect = 1;    //启用降序
					} 
				}else if(1 == mTripDirect){ //降序
					mCurSID--;
					if(0  > mCurSID){       //下一状态小于0
						mCurSID  = (short)(mCurSID + 2);
						mTripDirect = 0;    //启用升序
					}
				}//End of:if(0 == mTripDirect)
			}//End of: if(true == mIVInfo.isLoop())
		}
		return oldSID;
	}
	/**
	 * 设置下一状态号为当前状态号
	 * @param  stvalue 待设定的状态值
	 * @return 旧的状态号
	 * */
	public short setNextAsCurSIDAddr(short stvalue){	
		short oldSID = mCurSID;   //保存旧的状态号

		if(1 == mIVInfo.getChangeCondition()){      //受位地址控制切换
			mCurSID = (short)(0x01 & stvalue);      //通过1位确定状态序号
		}else if(2 == mIVInfo.getChangeCondition()){//受字地址控制切换
			mCurSID = findSIDByValue(stvalue);
			if(0 > mCurSID){//没有找到对应的状态序号
				mCurSID = oldSID;
			}
		}//End of:if(0 == mIVInfo.getChangeCondition())

		if(mCurSID >= mIVInfo.getStatusTotal()){ //状态值超出了上限
			mCurSID = oldSID;
		}
		
		return oldSID;
	}

	@Override
	public void realseMemeory() {

		SKTimer.getInstance().getBinder().onDestroy(SChangeTimerCallback);
		SKThread.getInstance().getBinder().onDestroy(SChangeTaskCallback, mTaskID);

		mTaskID       = null;

		mTouchX = -1;
		mTouchY = -1;
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
	 * 处理地址数据通知回调
	 * */
	private Vector<Short> mNoticeData = null; //存放通知数据
	public void handleAddrDataCallback(Vector<Byte> nStatusValue){

		if(nStatusValue.isEmpty()){
			return ;
		}
		
		if((0 == mIVInfo.getFunType()) && (1 == mIVInfo.getChangeCondition())){//单幅按位地址闪烁
			if(1 == (0x01&nStatusValue.get(0))){ //判断位是否为1
				mIsCtrlToShow = true; 
			}else{
				mIsCtrlToShow = false;
			}
			mIsCtrlNewData  = true;                     //标定有新的数据到来
			return;
		}//End of: if((0 == mIVInfo.getFunType()) && (1 == mIVInfo.getChangeCondition()))
		
		if((1 == mIVInfo.getFunType()) && (1 == mIVInfo.getChangeCondition())){//多幅按位地址切换，仅2个状态
			mStateValue     = (short) (0x01&nStatusValue.get(0)); //仅取1位
			mIsCtrlNewData  = true;                     //标定有新的数据到来 
			return;
		}

		if((1 == mIVInfo.getFunType()) && (2 == mIVInfo.getChangeCondition())){//多幅按字地址切换

			if (mNoticeData==null) {
				mNoticeData=new Vector<Short>();
			}else {
				mNoticeData.clear();
			}			

			boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mNoticeData);
			if(!mNoticeData.isEmpty()){
				mStateValue     = mNoticeData.get(0); //取第一个数据
				mIsCtrlNewData  = true;  
			}
			return;
		}
	} 

	/**
	 * 地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 初始化矩阵信息
	 * */
	private void initMatrix(){
		if(null == mAreaRect){
			Log.e("SKImageViewer","initMatrix: mAreaRect is null");
			return;
		}
		float w = mAreaRect.width();
		float h = mAreaRect.height();
		// construct our mesh
		int index = 0;
		for (int y = 0; y <= HEIGHT; y++) {
			float fy = h * y / HEIGHT;
			for (int x = 0; x <= WIDTH; x++) {
				float fx = w * x / WIDTH;
				setXY(mVerts, index, fx, fy);
				setXY(mOrig, index, fx, fy);
				index += 1;
			}
		}
		mMatrix.setTranslate(mIVInfo.getLp(), mIVInfo.getTp()); //设置起始点坐标

		mMatrix.invert(mInverse);
	}


	@Override
	public void initGraphics() {

		if(null == this.mIVInfo){

			isInitStateOK = false; //图形初始化异常，initGraphics需要重新调用
			return;
		}
		
		isInitStateOK = true;
		if( 0 != mPaintState && 2 != mPaintState){//若不是初始态，也不是重绘态
			return;//不做初始操作
		}

		if(0 == mPaintState){
			initMatrix();
			mCurSID = 0;  //初始化当前状态号
		}

		itemIsShow();
		
		SKSceneManage.getInstance().onRefresh(mTheItem);

		
		if( (1 == mIVInfo.getFunType())||((0 == mIVInfo.getFunType()) && (true == mIVInfo.isFlicker()))){//地址控制或按时间闪烁

			if( 0 == mPaintState || 2 == mPaintState){//若是初始态或重绘态
				SKTimer.getInstance().getBinder().onRegister(SChangeTimerCallback);
			}
		}
		

	}

	public synchronized int accessFlickerSstate(int type, int arg){

		if(0 == type){//读操作
			return mFlickerState;
		}else if(1 == type){//写操作
			mFlickerState = arg;
		}
		return 0;
	} 

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			if(this.mItemID != itemId){ //控件ID不匹配
				return false;
			}

			if((0 == mIVInfo.getFunType()) && (true == mIVInfo.isFlicker())){//单幅闪烁
				if(1 == accessFlickerSstate(0,0)){//显示图片
					drawImage(canvas, (short)0);
				}else if(2 == accessFlickerSstate(0,0)){//不显示图片
					//Do Nothing
				}
			}else{
				drawImage(canvas, mCurSID);
			}

			mPaintState = 1; //进入绘制态

			return true;
		}
		return false;
	}

	/**
	 * 模拟下位地址变化,模拟代码，发布时需要删除
	 * */
	public void changeAddrData(){

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//模拟位地址控制显现属性
		if(0 == bytedata){
			bytedata = 1;
		}else{
			bytedata = 0;
		}

		Vector<Integer> dataList = new Vector<Integer>();
		dataList.add(bytedata); //保存新的数据

		SEND_DATA_STRUCT mSendData  = new SEND_DATA_STRUCT();
		mSendData.eDataType         = DATA_TYPE.BIT_1;                   //数据类型为位
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W; //本次为写操作

		if(null != mIVInfo.getmShowInfo())
			PlcRegCmnStcTools.setRegIntData(mIVInfo.getmShowInfo().getShowAddrProp(), dataList, mSendData);


		//		if(1 == mIVInfo.getChangeCondition()){//受位地址控制
		//			if(0 == bytedata){
		//				bytedata = 1;
		//			}else{
		//				bytedata = 0;
		//			}
		//
		//			Vector<Integer> dataList = new Vector<Integer>();
		//			dataList.add(bytedata); //保存新的数据
		//
		//			SEND_DATA_STRUCT mSendData  = new SEND_DATA_STRUCT();
		//			mSendData.eDataType         = DATA_TYPE.BIT_1;                   //数据类型为位
		//			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W; //本次为写操作
		//			PlcRegCmnStcTools.setRegIntData(mIVInfo.getWatchAddr(), dataList, mSendData);
		//			
		//		}else if(2 == mIVInfo.getChangeCondition()){//受字地址控制
		//
		//			if(0 == bytedata){
		//				bytedata = 15;
		//			}else if(15 == bytedata){
		//				bytedata = 20;
		//			}else if(20 == bytedata){
		//				bytedata = 25;
		//			}else if(25 == bytedata){
		//				bytedata = 30;
		//			}else if(30 == bytedata){
		//				bytedata = 35;
		//			}else if(35 == bytedata){
		//				bytedata = 15;
		//			}
		//		
		//			Vector<Integer> dataList = new Vector<Integer>();
		//			dataList.add(bytedata); //保存新的数据
		//
		//			SEND_DATA_STRUCT mSendData  = new SEND_DATA_STRUCT();
		//			mSendData.eDataType         = DATA_TYPE.INT_16;                  //数据类型为位
		//			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W; //本次为写操作
		//			PlcRegCmnStcTools.setRegIntData(mIVInfo.getWatchAddr(), dataList, mSendData);
		//
		//		}//End of:if(1 == mIVInfo.getChangeCondition())

	}

	@Override
	public boolean isTouch() {
		return true;
	}

	ImageShowDialog dialog;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (!show||SystemInfo.getModel()==HMIMODEL.MID) {
			return false;
		}
		SKSceneManage.getInstance().time=0;
		boolean result=false;
		
		float x=event.getX(0);
		float y=event.getY(0);
		if (x>=mIVInfo.getLp()&&x<(mIVInfo.getTp()+mIVInfo.getWidth())
				&&y>mIVInfo.getTp()&&y<(mIVInfo.getTp()+mIVInfo.getHeight())) {
			
			if (event.getPointerCount()>1) {
				result=true;
				if (dialog==null||!dialog.isShow) {
					dialog=new ImageShowDialog(SKSceneManage.getInstance().mContext);
					dialog.setCanceledOnTouchOutside(false);
					dialog.onCreate();
					dialog.showDialog(ImageFileTool.getBitmap(findImagePathBySID(mCurSID)));
				}
			}
		}
		
		
		return result;
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
		if (mIVInfo!=null) {
			return mIVInfo.getLp();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (mIVInfo!=null) {
			return mIVInfo.getTp();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (mIVInfo!=null) {
			return mIVInfo.getWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (mIVInfo!=null) {
			return mIVInfo.getHeight();
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
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
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
		if (mIVInfo != null) {
			if (x == mIVInfo.getLp()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			mIVInfo.setLp((short) x);
			int l = mTheItem.rect.left;
			mTheItem.rect.left = x;
			mTheItem.rect.right = x - l + mTheItem.rect.right;
			mTheItem.mMoveRect=new Rect();
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (mIVInfo != null) {
			if (y == mIVInfo.getTp()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			mIVInfo.setTp((short) y);
			int t = mTheItem.rect.top;
			mTheItem.rect.top = y;
			mTheItem.rect.bottom = y - t + mTheItem.rect.bottom;
			mTheItem.mMoveRect=new Rect();
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (mIVInfo != null) {
			if (w == mIVInfo.getWidth()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			mIVInfo.setWidth((short) w);
			mTheItem.rect.right = w - mTheItem.rect.width() + mTheItem.rect.right;
			mTheItem.mMoveRect=new Rect();
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (mIVInfo != null) {
			if (h == mIVInfo.getHeight()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			mIVInfo.setHeight((short) h);
			mTheItem.rect.bottom = h - mTheItem.rect.height() + mTheItem.rect.bottom;
			mTheItem.mMoveRect=new Rect();
			SKSceneManage.getInstance().onRefresh(mTheItem);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==show) {
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

}//End of class