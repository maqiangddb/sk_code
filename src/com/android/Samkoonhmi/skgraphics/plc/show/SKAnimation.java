package com.android.Samkoonhmi.skgraphics.plc.show;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.databaseinterface.AddrPropBiz;
import com.android.Samkoonhmi.databaseinterface.AnimationViewerBiz;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.AnimationViewerInfo;
import com.android.Samkoonhmi.model.PictureInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StakeoutInfo;
import com.android.Samkoonhmi.model.StateBmpCache;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.TPMoveInfo;
import com.android.Samkoonhmi.model.TextInfo;
import com.android.Samkoonhmi.model.TrackPointInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;
import com.android.Samkoonhmi.util.TASK;



/**
 * 动画显示类，用于在场景中显示动画
 * @author 魏 科
 * @date   2012-06-06
 * */
public class SKAnimation extends SKGraphCmnShow{

	private int   mItemID;           //控件ID
	private int   mSceneID;          //场景ID

	private Rect  mCurItemRect;      //当前物体矩形位置
	private Rect  mPrevItemRect;     //先前物体矩形位置

	private short mPaintState  = 0;  //绘制状态，约定：0初始态，1绘制态，2待重画态
	
	private short mCurTPNum;         //当前轨迹点序号	
	private short mMTripDirect = 0;  //轨迹点移动方向，约定：0，升序；1，降序
	private short mMIntervalCount;   //移动时间间隔计数器

	private short mSIntervalCount;   //切换时间间隔计数器
	private short mCurStateNum;      //当前状态号
	private short mSTripDirect = 0;  //当前往返方向

	private StaticTextModel  mTextData;    //文本数据实体类
	private TextItem         mTextItem;    //图片显示上的文本控件

	private ArrayList<StateBmpCache>   mImgCacheList;  //图片缓存列表

	private String  mTheTaskID;   //状态任务签名

	private Paint   mPaintRef;    //Paint引用  

	private AnimationViewerInfo mAVInfo;   //动画显示器数据实体类

	private Rect     mAreaRect;     //位图矩形实例,保留代码，不可删除

	private SKItems  mTheItem;

	private boolean  mValid           = true;  //位图是否有效
	private boolean  canRefreshFlag   = true;  //当前是否可以刷新
	private boolean  isInitStateOK    = false;  //图形初始化正常
	
	private short mSCmpData;   //状态数据
	private short mMCmpData;   //移动数据，限散点轨迹

	private short mXData;      //轨迹点X坐标数据，限区域轨迹
	private short mYData;      //轨迹点Y坐标数据，限区域轨迹

	private boolean mIsNewSCmpData;   //是否有新的状态数据
	private boolean mIsNewMCmpData;   //是否有新的移动数据

	private boolean mIsNewXData;     //是否有新的X数据
	private boolean mIsNewYData;     //是否有新的Y数据

	private boolean  show       = true;   // 是否可显现
	private boolean  showByAddr = false;  // 是否注册显现地址
	private boolean  showByUser = false;  // 是否受用户权限控件
	

	//调试代码，以下是模拟地址中的数据，在发布时，需要删除
	private int bytedata  = 0;
	private int moveshortdata  = 15;
	private int stateshortdata = 15;
	private int mXPosCtrlAddr  = 40;
	private int mYPosCtrlAddr  = 40;

	public SKAnimation(int ItemID, int sceneid,AnimationViewerInfo info){

		mItemID  = ItemID;
		mSceneID = sceneid; 
		
		isInitStateOK = true;

		mPaintRef  = new Paint(); 
		mPaintRef.setDither(true);
		mPaintRef.setAntiAlias(true);

		//初始化图片缓存列表
		if(null == mImgCacheList){
			mImgCacheList = new ArrayList<StateBmpCache>();
		}	
		this.mAVInfo=info;


	}

	/**
	 * 判断一定图形是否在设定的区域内
	 * */
	public boolean isInArea(Rect rect){

		if(null == rect){
			Log.e("SKAnimation","isInArea: rect is null!");
			return false;
		}

		if((rect.left >= mAreaRect.left)
				&&(rect.top >= mAreaRect.top)
				&&(rect.right <= mAreaRect.right)
				&&(rect.bottom <= mAreaRect.bottom)){
			return true;
		}
		return false;
	}
	
	public boolean isInArea(int left, int top, int right, int bottom){

		if((left >= mAreaRect.left)
				&&(top >= mAreaRect.top)
				&&(right <= mAreaRect.right)
				&&(bottom <= mAreaRect.bottom)){
			return true;
		}
		return false;
	}

	/**
	 * 初始化动画基本数据
	 * */
	public void initAnimationData(){

		if(null == mAVInfo){
			Log.e("SKAnimation","initAnimationData: mAVInfo is null");
			return;
		}
		mSCmpData = mAVInfo.getStartState();

		if(0 == mAVInfo.getTrackType()){
			mMCmpData = mAVInfo.getStartTrackPoint();
			mCurTPNum = 0;
		}else if(1 == mAVInfo.getTrackType()){
			mXData = 0;
			mYData = 0;
		}

	}

	/**
	 * 初始化描述符
	 * */
	public void initItem(){

		if(null == mTheItem){
			mTheItem = new SKItems();
			if(null == mAVInfo){
				Log.e("SKAnimation","initItem: mAVInfo is null");
				return;
			}
			mTheItem.nZvalue = mAVInfo.getZValue(); 
			mTheItem.itemId  = mItemID;
			mTheItem.sceneId = mSceneID;
			mTheItem.nCollidindId = mAVInfo.getCollidindId();
			mTheItem.mGraphics=this;
		}
	}

	/**
	 * 初始化移动区域，保留代码，不可删除
	 * */
	public void initArea(){

		if(null == mAreaRect){
			if(null == mAVInfo){
				Log.e("SKAnimation","mDRInfo is null");
				return;
			}
			//图形只能在这个区域移动
			mAreaRect   = new Rect(mAVInfo.getAreaOrigXPos(),mAVInfo.getAreaOrigYPos(),mAVInfo.getAreaOrigXPos()+mAVInfo.getAreaWidth(),mAVInfo.getAreaOrigYPos()+mAVInfo.getAreaHeight());
		}	
	}

	/**
	 * 初始化图片显示背景矩形
	 * */
	public void initItemRect(){

		if(null == mCurItemRect){
			if(null == mAVInfo){
				Log.e("SKAnimation","initItemRect: mAVInfo is null");
				return;
			}	
			//新建背景矩形实例
			mCurItemRect  = new Rect(mAVInfo.getLp(),mAVInfo.getTp(),mAVInfo.getLp()+mAVInfo.getWidth(),mAVInfo.getTp()+mAVInfo.getHeight());
			if(null == mPrevItemRect){
				mPrevItemRect =  new Rect(mCurItemRect);
			}
		}
	}

	/**
	 * 初始化文本信息
	 * */
	public void initText(){
		if(null == mTextData){
			mTextData = new StaticTextModel();   //新建文本数据实体类实例
		}

		if(null == mTextItem){
			mTextItem = new TextItem(mTextData); 
		}
	}

	/**
	 * 根据状态号改变文本框坐标信息
	 * @param idx 状态序号
	 * */
	public void updateTextBySID(short sid){

		if((0>sid)){			
			Log.e("SKAnimation","updateTextBySID: Invalid state id:"+sid);
			return;
		}

		if(null == mAVInfo.getTextInfoList()){
			return;
		}

		if(sid >= mAVInfo.getTextInfoList().size()){
			Log.e("SKAnimation","updateTextBySID: Invalid state id : " + sid + " , too big");
			return;
		}


		//更新文本坐标信息	
		mTextData.setStartX(mTheItem.mMoveRect.left);
		mTextData.setStartY(mTheItem.mMoveRect.top);
		mTextData.setRectWidth(mTheItem.mMoveRect.width());
		mTextData.setRectHeight(mTheItem.mMoveRect.height());

		TextInfo tmpTInfo = null;
		tmpTInfo = findTextInfoBySID(sid);
		if(null == tmpTInfo.getsText()){
			Log.e("SKAnimation","updateTextBySID: find TextInfo failed, state id:  " + sid);
			return;	
		}

		//初始化文本基本属性

		mTextData.setLineWidth(0);
		mTextData.setM_backColorPadding(Color.TRANSPARENT);

		mTextData.setM_textPro(tmpTInfo.getnStyle());
		mTextData.setM_sTextStr(tmpTInfo.getsText());
		mTextData.setM_nFontColor(tmpTInfo.getnColor());
		mTextData.setM_nFontSize(tmpTInfo.getnSize());
		mTextData.setM_sFontFamly(tmpTInfo.getsFontFamily());
		mTextData.setM_eTextAlign(tmpTInfo.geteAlign());
		
		//设置文本边框的画笔属性
		mTextItem.initRectBoderPaint();
		//设置文本背景矩形的画笔属性
		mTextItem.initRectPaint();
		//设置文本内容画笔的属性
		mTextItem.initTextPaint();
		
	}

	/**
	 * 绘制动画的文本信息
	 * */
	public void drawTextInfo(Canvas canvas){

		if(null == canvas){
			Log.e("SKAnimation", "drawTextInfo: canvas is null");
			return;
		}

		if(null == this.mPaintRef){
			Log.e("SKAnimation", "drawTextInfo: paint is null");
		}
		
		if(false == mTextData.getM_sTextStr().equals("")){
			mTextItem.draw(canvas);
		}
	}

	/**
	 * 在图片显示区域绘制图片
	 * @param sid 状态序号
	 * */
	public void drawImage(Canvas canvas, short sid)
	{
		boolean needcache = false; //判断是否需要缓存图片

		Bitmap tmpImg = null;

		if(0 > sid){//处理异常状态号
			Log.e("SKAnimation", "drawImage: Invalid sid: " + sid);
			return;
		}

		if( (mAVInfo.getStateTotal()>0) && (sid >= mAVInfo.getStateTotal())){//处理异常状态号
			Log.e("SKAnimation", "drawImage: Invalid sid: " + sid);
			return;			
		} 

		//试图从缓存中找到对应的图片
		tmpImg = findCacheImageBySID(sid);

		if(null == tmpImg){//没有在缓存中找到图片
		//	Log.i("SKAnimation","drawImage: Image not cached,try to decode");
			tmpImg = ImageFileTool.getBitmap(findImagePathBySID(sid));
			needcache = true;
		}

		if(null == tmpImg){//图片解码失败
			Log.e("SKAnimation","drawImage: Image decode failed");
			return;
		}

		//绘制图片
		canvas.drawBitmap(tmpImg,null, mTheItem.mMoveRect, mPaintRef);

		//缓存图片
		if(true == needcache){
			cacheStatusImage(sid, tmpImg);
		}
	}


	/**
	 * 缓存图片到缓存列表
	 * @param sid 状态号
	 * @param img 图片
	 * */
	public void cacheStatusImage(short sid, Bitmap img ){
		if(0 > sid ){
			Log.e("SKAnimation","cacheStatusImage: Invalid  sid: " + sid);
			return;
		}

		if(null == img){
			Log.e("SKAnimation","cacheStatusImage: Image is null");
			return;
		}

		if(null == mImgCacheList){//图片缓存列表为空 
			//Log.i("SKAnimation","cacheStatusImage: mImgCacheList is null");
			//Log.i("SKAnimation","cacheStatusImage: Create new mImgCacheList");
			//新建缓存列表
			mImgCacheList = new ArrayList<StateBmpCache>();
		}

		//新建一个状态图片缓存实例
		StateBmpCache tmpSBCache = new StateBmpCache();
		tmpSBCache.setStatusID(sid);
		tmpSBCache.cacheBitmap(img);

		//将缓存实例添加到图片缓存列表中
		mImgCacheList.add(tmpSBCache);
	}

	/**
	 * 根据图片路径解码图片信息
	 * @param  imgpath 图片路径
	 * @return         解码后的图片实例
	 * */ 
	public Bitmap decodeImgByPath(String imgpath){
		if(null == imgpath){
			Log.e("SKAnimation","decodeImgByPath: Image path is null");
			return null;
		}

		File tmpfile = new File(imgpath);
		if(false == tmpfile.exists()){//处理图片文件不存在
			Log.e("SKAnimation","decodeImgByPath: Image file not exist");
			return null;
		}

		//对图片文件解码
		Bitmap tmpBitmap = BitmapFactory.decodeFile(imgpath);
		if(null == tmpBitmap){//处理解码失败处理
			Log.e("SKAnimation", "decodeImgByPath: Image decode failed");
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
		if(null == mAVInfo.getPicPathArray()){
			Log.e("SKAnimation","findImagePathBySID: PicPathArray is null");
			return imgpath;
		}
		ArrayList<PictureInfo> tmpList = null;
		tmpList = mAVInfo.getPicPathArray();
		if(null == tmpList){
			return null;
		}

		for(short i = 0; i< tmpList.size(); i++){
			if(sid == tmpList.get(i).getnStatusId()){
				imgpath = tmpList.get(i).getsPath();
			}
		}//End of: for(short i = 0; i< mIVInfo.getPicPathArray().size(); i++)
		return imgpath;
	}


	/**
	 * 从图片缓存列表中查找状态号对应的图片
	 * @param  sid 状态号
	 * @return 状态号对应的图片实例，否则返回空
	 * */
	public Bitmap findCacheImageBySID(short sid){
		Bitmap tmpImg = null;

		if(null == mImgCacheList){
			Log.i("SKAnimation","findCacheImageBySID: mImgCacheList is null");
			return tmpImg;
		}

		for(short i = 0; i < mImgCacheList.size(); i++){
			if(sid == mImgCacheList.get(i).getStatusID()){
				tmpImg = mImgCacheList.get(i).getBitmap();
			}
		}//End of: for(short i = 0; i < mImgCacheList.size(); i++)
		return tmpImg;
	}

	/**
	 * 查找状态号对应的文本信息
	 * @param sid 状态号
	 * @return    文本信息
	 * */
	public TextInfo findTextInfoBySID(short sid){

		TextInfo tmpTInfo = null;

		if(null == mAVInfo.getTextInfoList()){
			Log.e("SKAnimation","findTextInfoBySID: TextInfoList is null");
			return tmpTInfo;
		}
		ArrayList<TextInfo> tmpList = null;
		tmpList = mAVInfo.getTextInfoList();
		if(null == tmpList){
			return null;
		}

		for(short i = 0; i< tmpList.size(); i++){
			if(sid == tmpList.get(i).getnStatusId()){
				tmpTInfo = tmpList.get(i);
			}
		}

		return tmpTInfo;
	}


	/**
	 * 根据轨迹点序号查找对应的轨迹点信息
	 * @param tid轨迹点号
	 * */
	public TrackPointInfo findTPInfoByTID(short tid){
		TrackPointInfo tmpTPI = null;
		if(null == mAVInfo.getTrackPointArray()){
			Log.e("SKAnimation","findTPInfoByTID: track point info list is null");
			return tmpTPI;
		}

		ArrayList<TrackPointInfo> tmpList = null;
		tmpList = mAVInfo.getTrackPointArray();
		if(null == tmpList){
			return null;
		}

		for(short i = 0; i< tmpList.size(); i++){
			if(tid == tmpList.get(i).getID()){
				tmpTPI = tmpList.get(i);
			}
		}//End of: for(short i = 0; i< mIVInfo.getPicPathArray().size(); i++)
		return tmpTPI;
	}

	/**
	 * 查找与值对应状态ID
	 * @param  value 值
	 * @return 状态号
	 * */
	public short findSIDByValue(short value){
		short sid = -1;
		if(null == mAVInfo.getSPreSetVList()){
			Log.e("SKAnimation","findSIDByValue: SPreSetVList is null");
			return sid;
		}
		ArrayList<StakeoutInfo> tmpList = null;
		tmpList = mAVInfo.getSPreSetVList();
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

	/**
	 * 查找与值对应轨迹点序号
	 * @param  value 值
	 * @return 轨迹点序号
	 * */
	public short findTIDByValue(short value){
		short tid = -1;
		if(null == mAVInfo.getTPMoveList()){
			Log.e("SKAnimation","findTIDByValue: TPMoveList is null");
			return tid;
		}

		ArrayList<TPMoveInfo> tmpList = null;
		tmpList = mAVInfo.getTPMoveList();
		if(null == tmpList){
			Log.e("SKAnimation","findTIDByValue: mAVInfo.getTPMoveList() is null");
			return tid;
		}

		for(short i = 0; i<tmpList.size(); i++){	
			if(value == tmpList.get(i).getCmpFactor()){			
				tid = tmpList.get(i).getTPNo();
			}
		}
		return tid;
	}


	/**
	 * 定时器回调
	 * */
	SKTimer.ICallback theTimerCallback=new SKTimer.ICallback() {	
		@Override
		public void onUpdate() {
			//changeAddrData(); //调试代码
			SKThread.getInstance().getBinder().onTask(MODULE.SKANIMATION,TASK.SKANIMATION_CHANGE,mTheTaskID);	
		}//End of onUpdate
	};

	/**
	 * 处理线程回调
	 * */
	public void handleTheTaskCallback(){

		//changeAddrData();  //调试代码

		if(null == mAVInfo){
			Log.e("SKAnimation","handleTheTaskCallback: mAVInfo is null");
			return;
		}
		if(0 == mAVInfo.getChangeCondition()){//若按时间切换状态
			mSIntervalCount += SKTimer.UPDATE_TIME;
			if(mSIntervalCount == mAVInfo.getChangeTimeinterval()){//定时周期达到
				handleStTaskUpdate();
				mValid = false;
				mSIntervalCount = 0;
			}
		}else{//按控制地址切换
			if(true == mIsNewSCmpData){ //有新的数据到来 
				mIsNewSCmpData = false; //标定数据不再是新
				handleStTaskUpdate();
				mValid = false;
			}
		}//End of: f(0 == mAVInfo.getChangeCondition()) 

		if((0 == mAVInfo.getTrackType())&& (0 == mAVInfo.getMoveCondition())){//散点轨迹按时间间隔移动
			mMIntervalCount += SKTimer.UPDATE_TIME;
			if(mMIntervalCount == mAVInfo.getMoveTimeInterval()){//定时周期达到
				handlePosTaskUpdate();
				mMIntervalCount = 0;
				mValid = false;
			}
		}//End of: if((0 == mAVInfo.getTrackType())&& (0 == mAVInfo.getMoveCondition()))

		if((0 == mAVInfo.getTrackType())&& (1 == mAVInfo.getMoveCondition()) ){//散点轨迹按地址控制移动
			if(true == mIsNewMCmpData){//有新数据到来时
				mIsNewMCmpData = false;
				handlePosTaskUpdate();
				mValid = false;
			}
		}//End of:if((0 == mAVInfo.getTrackType())&& (1 == mAVInfo.getMoveCondition()) && (true == mIsNewMCmpData)) 

		if(1 == mAVInfo.getTrackType()){
			if( (true==mIsNewXData) || (true==mIsNewYData)){ //有新数据到来时
				if(mIsNewXData){ 
					mIsNewXData = false; //标定数据不再是新
				}
				if(mIsNewYData){
					mIsNewYData = false; //标定数据不再是新
				}
				handlePosTaskUpdate();
				mValid = false;
			}//End of:if( (true==mIsNewXData) || (true==mIsNewYData))
		}

		if(false == mValid){//画布内容无效时需要刷新
			if(true == canRefreshFlag){//若可以允许刷新
				canRefreshFlag      = false;   //标定当前不可再刷新
				mValid              = true;    //标定当前位图有效

				mTheItem.rect       = new Rect(mPrevItemRect);
				mTheItem.mMoveRect  = new Rect(mCurItemRect);
				updateTextBySID(mCurStateNum);//更新文本信息
				SKSceneManage.getInstance().onRefresh(mTheItem);				
			}
		}
	}

	/**
	 * 线程回调
	 * */
	SKThread.ICallback theTaskCallback=new SKThread.ICallback() {

		@Override
		public void onUpdate(String msg, int taskId) {
		}

		@Override
		public void onUpdate(int msg, int taskId) {
			handleTheTaskCallback();
		}	

		@Override
		public void onUpdate(Object msg, int taskId) {

		}
	};//End of: PosTaskCallback

	/**
	 * 处理线程回调
	 * */
	public void handlePosTaskUpdate(){
		if(null == mAVInfo){
			Log.e("SKAnimation","handlePosTaskUpdate: mAVInfo is null");
			return;
		}
		if(0 == mAVInfo.getTrackType()){//散点轨迹
			short prevtp = setNextTPAsCurTP();
			if(prevtp == mCurTPNum){//前后两个轨迹点相同
				return;
			}		
			updateItemRect(mCurTPNum);//更新外围轮廓的左上角坐标
		}else if(1 == mAVInfo.getTrackType()){ //区域轨迹
				updateItemRect(0);					
		}//End of: if(0 == mAVInfo.getTrackType()		
	}

	/**
	 * 处理线程回调函数
	 * */
	public void handleStTaskUpdate(){
		if(null == mAVInfo){
			Log.e("SKAnimation","handleStTaskUpdate: mAVInfo is null");
			return;
		}
		short oldsid = setNextStAsCurSt();	
	}

	/**
	 * 根据轨迹点序号更新动画显示器轮廓坐标信息
	 * @param tpidx 轨迹点序号
	 * */
	public void updateItemRect(int tpidx){
		TrackPointInfo tmpTPInfo = null;
		if(0 == mAVInfo.getTrackType()){//散点轨迹
			if((0>tpidx)){			
				Log.e("SKAnimation","updateItemRect: Invalid tpidx:"+tpidx);
				return;
			}
			if(tpidx >=  mAVInfo.getTrackPointArray().size()){
				Log.e("SKAnimation","updateItemRect: Invalid tpidx:"+tpidx);
				return;
			}
			tmpTPInfo = findTPInfoByTID((short)tpidx);
		}else if(1 == mAVInfo.getTrackType()){//区域轨迹

			tmpTPInfo = new TrackPointInfo();
			
			short  x = (short) (mAVInfo.getAreaOrigXPos() + mXData * mAVInfo.getXMoveStepScale());			
			if( (x >= mAVInfo.getAreaOrigXPos())&&( (x+mCurItemRect.width()) <=  (mAVInfo.getAreaOrigXPos()+ mAVInfo.getAreaWidth()))){
				tmpTPInfo.setXPos(x);
			}else{
				tmpTPInfo.setXPos((short)mCurItemRect.left);
			}
			
			short y = (short) (mAVInfo.getAreaOrigYPos() + mYData * mAVInfo.getYMoveStepScale());
			if( (y >= mAVInfo.getAreaOrigYPos())&&( (y+mCurItemRect.height()) <=  (mAVInfo.getAreaOrigYPos()+ mAVInfo.getAreaHeight()))){
				tmpTPInfo.setYPos(y);
			}else{
				tmpTPInfo.setYPos((short)mCurItemRect.top);
			}
			//预留更新代码，不可删除
//			short  x = (short) (mAVInfo.getLp()+ mXData * mAVInfo.getXMoveStepScale());			
//			if( (x >= mAVInfo.getAreaOrigXPos())&&( (x+mCurItemRect.width()) <=  (mAVInfo.getAreaOrigXPos()+ mAVInfo.getAreaWidth()))){
//				tmpTPInfo.setXPos(x);
//			}else{
//				tmpTPInfo.setXPos((short)mCurItemRect.left);
//			}
//			
//			short y = (short) (mAVInfo.getTp() + mYData * mAVInfo.getYMoveStepScale());
//			if( (y >= mAVInfo.getAreaOrigYPos())&&( (y+mCurItemRect.height()) <=  (mAVInfo.getAreaOrigYPos()+ mAVInfo.getAreaHeight()))){
//				tmpTPInfo.setYPos(y);
//			}else{
//				tmpTPInfo.setYPos((short)mCurItemRect.top);
//			}

		}	
		
		//保存当前图片的的绘制信息
		if(null == mPrevItemRect){
			mPrevItemRect = new Rect(mCurItemRect);
		}else{
			mPrevItemRect.set(mCurItemRect);
		}
		
		mCurItemRect.set(tmpTPInfo.getXPos(),tmpTPInfo.getYPos(),tmpTPInfo.getXPos()+mCurItemRect.width(),tmpTPInfo.getYPos()+mCurItemRect.height());
		
	}

	/**
	 * 获得下一个需要移动的轨迹点号
	 * @return 返回旧的轨迹点序号
	 * */
	public short setNextTPAsCurTP(){
		short prevtp;
		prevtp = mCurTPNum;  //保存前一TP序号
		if(0 == mAVInfo.getMoveCondition()){//时间间隔移动
			if(0 == mAVInfo.getMoveType()){       //循环移动
				mCurTPNum ++;
				if(mCurTPNum >= mAVInfo.getTrackPointTotal()){
					mCurTPNum = 0;
				}
			}else if(1 == mAVInfo.getMoveType()){ //往返移动
				if(0 == mMTripDirect){     //升序
					mCurTPNum++;
					if((mCurTPNum) >= mAVInfo.getTrackPointTotal()){//超过上限
						mCurTPNum  = (short)(mAVInfo.getTrackPointTotal() - 2); 
						mMTripDirect = 1;   //启用降序
					}				
				}else if(1 == mMTripDirect){//降序
					mCurTPNum--;
					if(0  > mCurTPNum){   //序号小于0
						mCurTPNum  = (short)(mCurTPNum + 2);
						mMTripDirect = 0;   //启用升序
					}
				}//End of:if(0 == mTripDirect) 			
			}
		}else if(1 == mAVInfo.getMoveCondition()){//按预设值移动	
			mCurTPNum = findTIDByValue(mMCmpData); 
		}//End of:if(0 == mAVInfo.getMoveCondition())

		if(0 > mCurTPNum){//没有找到轨迹点
			mCurTPNum = prevtp;
		}

		if(mCurTPNum >= mAVInfo.getTrackPointTotal()){ //大于轨迹点总数
			mCurTPNum = prevtp;
		}

		return prevtp;
	}

	/**
	 * 将下一个状态号设定为当前状态号
	 * @return 旧的状态号
	 * */
	public short setNextStAsCurSt(){

		short prevst = mCurStateNum;

		if(0 == mAVInfo.getChangeCondition()){       //时间间切换
			if(0 == mAVInfo.getChangeCondition()){   //定时切换		
				if(1 == mAVInfo.getChangeType()){    //循环切换
					mCurStateNum++;
					if((mCurStateNum) >=  mAVInfo.getStateTotal()){//下一状态超过状态上限
						mCurStateNum = 0;         //重设为初始状态号，初始状态号默认为0
					}
				}else if(2 == mAVInfo.getChangeType()){//往返切换
					if(0 == mSTripDirect){     //升序
						mCurStateNum++;
						if((mCurStateNum) >= mAVInfo.getStateTotal()){//下一状态超过状态上限
							mCurStateNum  = (short)( mAVInfo.getStateTotal() - 2); 
							mSTripDirect = 1;   //启用降序
						}				
					}else if(1 == mSTripDirect){//降序
						mCurStateNum--;
						if(0  > mCurStateNum){   //下一状态小于0
							mCurStateNum  = (short)(mCurStateNum + 2);
							mSTripDirect = 0;   //启用升序
						}
					}//End of:if(0 == mTripDirect) 
				}//End of: if(0 == mLoopType) 
			}        
		}else if(1 == mAVInfo.getChangeCondition()){  //按预设值切换			
			mCurStateNum = findSIDByValue(mSCmpData); //查找对应的状态号			
		}		

		if(0>mCurStateNum){//没有找到下一个状态
			mCurStateNum = prevst;
		}

		if(mCurStateNum >= mAVInfo.getStateTotal()){//状态超出了状态总数
			mCurStateNum = prevst;
		}
		return prevst;
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
			show = popedomIsShow(mAVInfo.getmShowInfo());
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

	@Override
	public void realseMemeory() {

		SKTimer.getInstance().getBinder().onDestroy(theTimerCallback);
		SKThread.getInstance().getBinder().onDestroy(theTaskCallback, mTheTaskID);
		
		
		//注册显现地址
		if (showByAddr) {
			SKPlcNoticThread.getInstance().destoryCallback(showCall);
		}

		if(null != mAVInfo && 0 != mAVInfo.getChangeCondition()){//受地址控制切换状态
			SKPlcNoticThread.getInstance().destoryCallback(notifyStateAddrDataCallback);
		}

		if((null != mAVInfo) && (0==mAVInfo.getTrackType())&&(0!= mAVInfo.getMoveCondition())){//散点轨迹受地址控制移动
			SKPlcNoticThread.getInstance().destoryCallback(notifyMoveAddrDataCallback);
		}

		if(null != mAVInfo && 1==mAVInfo.getTrackType()){//区域轨迹受地址控制移动
			SKPlcNoticThread.getInstance().destoryCallback(notifyXAddrDataCallback);
			SKPlcNoticThread.getInstance().destoryCallback(notifyYAddrDataCallback);
		}
		
		mTheTaskID      = null;
		mSIntervalCount = 0;
		mMIntervalCount = 0;
		if(1 == mPaintState){//若当前至少绘制过一次
			mPaintState = 2; //控件进入待重绘状态，下次在initGraph中不会再初始化控件参数
		}else if(2 == mPaintState){
			mPaintState = 2;
		}else{
			mPaintState = 0;
		}
	}

	/**
	 * 状态地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyStateAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleStateAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理状态地址数据通知回调
	 * */
	private Vector<Short> mSAddrNoticeData = null; //存放通知数据
	public void handleStateAddrDataCallback(Vector<Byte> nStatusValue){
		if(nStatusValue.isEmpty()){
			return ;
		}
		if (mSAddrNoticeData == null)
		{
			mSAddrNoticeData = new Vector<Short>();
		}else{
			mSAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mSAddrNoticeData);//将字节转换成短整型
		if(!mSAddrNoticeData.isEmpty()){
			mSCmpData       = mSAddrNoticeData.get(0); //取第一个数据
			mIsNewSCmpData  = true;  
		}
		return;
	}

	/**
	 * 移动地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyMoveAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleMoveAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理移动地址数据通知回调
	 * */
	private Vector<Short> mMAddrNoticeData = null; //存放通知数据
	public void handleMoveAddrDataCallback(Vector<Byte> nStatusValue){
		
		if(nStatusValue.isEmpty()){
			return;
		}
		if (mMAddrNoticeData == null)
		{
			mMAddrNoticeData = new Vector<Short>();
		}else{
			mMAddrNoticeData.clear();
		}
		
		boolean result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mMAddrNoticeData);//将字节转换成短整型
		if(!mMAddrNoticeData.isEmpty()){
			mMCmpData       = mMAddrNoticeData.get(0); //取第一个数据
			mIsNewMCmpData  = true;  
		}
		return;
	}

	/**
	 * 区域轨迹点X地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyXAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleXAddrDataCallback(nStatusValue);
		}
	};

	/**
	 * 处理区域轨迹点X地址数据通知回调
	 * */
	private Vector<Short> mXAddrNoticeData = null; //存放通知数据
	public void handleXAddrDataCallback(Vector<Byte> nStatusValue){

		if(nStatusValue.isEmpty()){
			return;
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
	}

	/**
	 * 区域轨迹点X地址数据通知回调函数
	 * */
	SKPlcNoticThread.IPlcNoticCallBack notifyYAddrDataCallback=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			handleYAddrDataCallback(nStatusValue);
		}

	};

	/**
	 * 处理区域轨迹点Y地址数据通知回调
	 * */
	private Vector<Short> mYAddrNoticeData = null; //存放通知数据
	public void handleYAddrDataCallback(Vector<Byte> nStatusValue){
		
		if(nStatusValue.isEmpty()){
			return ;
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
	}

	@Override
	public void initGraphics() {

		if(null == mAVInfo){ //若数据实体为空
			return;//不做初始操作
		}
		isInitStateOK = true;
		if( 0 != mPaintState && 2 != mPaintState){//若不是初始态，也不是重绘态
			return;//不做初始操作
		}

		if(0 == mPaintState){//若是初始态
			//原始数据初始化					
			initItemRect();     //初始化背景矩形 			
			initText();         //初始化文本数据信息			
			initItem();   		//初始化层信息

			mCurTPNum    = mAVInfo.getStartTrackPoint();    //保存当前轨迹点号
			mCurStateNum = mAVInfo.getStartState();         //保存当前状态号
		}
			
		updateItemRect(mCurTPNum);                    //获得当前轨迹点矩形
		
		if(0 == mPaintState){//若是初始态
			initArea();                                   //初始化移动限制区域
		}//End of:if(0 == mPaintState)
		
		mTheItem.rect       = new Rect(mPrevItemRect);
		mTheItem.mMoveRect  = new Rect(mCurItemRect);

		updateTextBySID(mCurStateNum);//更新文本信息

		if (mAVInfo.getmShowInfo() != null) {

			if (mAVInfo.getmShowInfo().isbShowByAddr()) {
				if (mAVInfo.getmShowInfo().getnAddrId() > 0) {
					// 受地址控制
					showByAddr = true;
				}
			}
			if (mAVInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
			itemIsShow();
		}
		SKSceneManage.getInstance().onRefresh(mTheItem);

		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mAVInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(mAVInfo.getmShowInfo().getShowAddrProp(),showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(mAVInfo.getmShowInfo().getShowAddrProp(),showCall, false);
			}
		}

		if(0 != mAVInfo.getChangeCondition()){//受地址控制切换状态
			SKPlcNoticThread.getInstance().addNoticProp(mAVInfo.getChangeCtrlAddr(), notifyStateAddrDataCallback, false);
		}

		if((0==mAVInfo.getTrackType())&&(0!= mAVInfo.getMoveCondition())){//散点轨迹受地址控制移动
			SKPlcNoticThread.getInstance().addNoticProp(mAVInfo.getMoveCtrlAddr(),   notifyMoveAddrDataCallback, false);
		}

		if(1==mAVInfo.getTrackType()){//区域轨迹受地址控制移动
			SKPlcNoticThread.getInstance().addNoticProp(mAVInfo.getXPosCtrlAddr(),notifyXAddrDataCallback, false);   
			SKPlcNoticThread.getInstance().addNoticProp(mAVInfo.getYPosCtrlAddr(),notifyYAddrDataCallback, false);
		}

		
		if( 0 == mPaintState || 2 == mPaintState){//若是初始态或重绘态
			SKTimer.getInstance().getBinder().onRegister(theTimerCallback);
			mTheTaskID = SKThread.getInstance().getBinder().onRegister(theTaskCallback);
		}
	}


	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		
		if(null == mAVInfo){
			Log.e("SKAnimation","drawGraphics: mAVInfo is null");
			return false;
		}

		if (show) {
			if(this.mItemID != itemId){
				return false;
			}

			if(1 == mAVInfo.getTrackType()){//区域轨迹
				if(isInArea(mTheItem.mMoveRect)){  //若当前动画不在限定区域
					drawImage(canvas, mCurStateNum);
					drawTextInfo(canvas);
				}else{
					
				}
			}else{//散点轨迹
				drawImage(canvas, mCurStateNum);
				drawTextInfo(canvas);
			}

			canRefreshFlag = true;//标定当前可以再次刷新
			mPaintState    = 1;   //控件进入绘制态，即至少绘制过一次
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 模拟下位地址变化,模拟代码，发布时需要删除
	 * */
	public void changeAddrData(){

		//以下是模拟代码	
		if(15 == moveshortdata){
			moveshortdata = 25;
		}else if(25 == moveshortdata){
			moveshortdata = 35;
		}else if(35 == moveshortdata){
			moveshortdata = 15;
		}

		//以下是模拟代码，模拟数据的变化
		if(15 == stateshortdata){
			stateshortdata = 25;
		}else if(25 == stateshortdata){
			stateshortdata = 35;
		}else if(35 == stateshortdata){
			stateshortdata = 15;
		}

		//以下是模拟代码
		if(40 == mXPosCtrlAddr){
			mXPosCtrlAddr = 50;
		}else if(50 == mXPosCtrlAddr){
			mXPosCtrlAddr = 60;
		}else if(60 == mXPosCtrlAddr){
			mXPosCtrlAddr = 70;
		}else if(70 == mXPosCtrlAddr){
			mXPosCtrlAddr = 80;
		}else if(80 == mXPosCtrlAddr){					
			mXPosCtrlAddr = 90;
		}else if(90 == mXPosCtrlAddr){
			mXPosCtrlAddr = 100;
		}else if(100 == mXPosCtrlAddr){
			mXPosCtrlAddr = 110;
		}else if(110 == mXPosCtrlAddr){
			mXPosCtrlAddr = 120;
		}else if(120 == mXPosCtrlAddr){
			mXPosCtrlAddr = 130;
		}else if(130 == mXPosCtrlAddr){
			mXPosCtrlAddr = 140;
		}else if(140 == mXPosCtrlAddr){
			mXPosCtrlAddr = 150;
		}else if(150 == mXPosCtrlAddr){
			mXPosCtrlAddr = 160;
		}else if(160 == mXPosCtrlAddr){
			mXPosCtrlAddr = 170;
		}else if(170 == mXPosCtrlAddr){
			mXPosCtrlAddr = 180;
		}else if(180 == mXPosCtrlAddr){
			mXPosCtrlAddr = 190;
		}else if(190 == mXPosCtrlAddr){
			mXPosCtrlAddr = 200;
		}else if(200 == mXPosCtrlAddr){
			mXPosCtrlAddr = 210;
		}else if(210 == mXPosCtrlAddr){
			mXPosCtrlAddr = 220;
		}else if(220 == mXPosCtrlAddr){
			mXPosCtrlAddr = 230;
		}else if(230 == mYPosCtrlAddr){
			mXPosCtrlAddr = 240;
		}else if(240 == mXPosCtrlAddr){
			mXPosCtrlAddr = 250;
		}else if(250 == mXPosCtrlAddr){
			mXPosCtrlAddr = 260;
		}else if(260 == mXPosCtrlAddr){
			mXPosCtrlAddr = 270;
		}else if(270 == mXPosCtrlAddr){
			mXPosCtrlAddr = 280;
		}else if(280 == mXPosCtrlAddr){
			mXPosCtrlAddr = 290;
		}else if(290 == mXPosCtrlAddr){
			mXPosCtrlAddr = 40;
		}

		if(40 == mYPosCtrlAddr){
			mYPosCtrlAddr = 50;
		}else if(50 == mYPosCtrlAddr){
			mYPosCtrlAddr = 60;
		}else if(60 == mYPosCtrlAddr){
			mYPosCtrlAddr = 70;
		}else if(70 == mYPosCtrlAddr){
			mYPosCtrlAddr = 80;
		}else if(80 == mYPosCtrlAddr){					
			mYPosCtrlAddr = 90;
		}else if(90 == mYPosCtrlAddr){
			mYPosCtrlAddr = 100;
		}else if(100 == mYPosCtrlAddr){
			mYPosCtrlAddr = 110;
		}else if(110 == mYPosCtrlAddr){
			mYPosCtrlAddr = 120;
		}else if(120 == mYPosCtrlAddr){
			mYPosCtrlAddr = 130;
		}else if(130 == mYPosCtrlAddr){
			mYPosCtrlAddr = 140;
		}else if(140 == mYPosCtrlAddr){
			mYPosCtrlAddr = 150;
		}else if(150 == mYPosCtrlAddr){
			mYPosCtrlAddr = 160;
		}else if(160 == mYPosCtrlAddr){
			mYPosCtrlAddr = 170;
		}else if(170 == mYPosCtrlAddr){
			mYPosCtrlAddr = 180;
		}else if(180 == mYPosCtrlAddr){
			mYPosCtrlAddr = 190;
		}else if(190 == mYPosCtrlAddr){
			mYPosCtrlAddr = 200;
		}else if(200 == mYPosCtrlAddr){
			mYPosCtrlAddr = 210;
		}else if(210 == mYPosCtrlAddr){
			mYPosCtrlAddr = 220;
		}else if(220 == mYPosCtrlAddr){
			mYPosCtrlAddr = 230;
		}else if(230 == mYPosCtrlAddr){
			mYPosCtrlAddr = 240;
		}else if(240 == mYPosCtrlAddr){
			mYPosCtrlAddr = 250;
		}else if(250 == mYPosCtrlAddr){
			mYPosCtrlAddr = 260;
		}else if(260 == mYPosCtrlAddr){
			mYPosCtrlAddr = 270;
		}else if(270 == mYPosCtrlAddr){
			mYPosCtrlAddr = 280;
		}else if(280 == mYPosCtrlAddr){
			mYPosCtrlAddr = 290;
		}else if(290 == mYPosCtrlAddr){
			mYPosCtrlAddr = 40;
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
		
		if(null != mAVInfo.getmShowInfo())
		PlcRegCmnStcTools.setRegIntData(mAVInfo.getmShowInfo().getShowAddrProp(), dataList, mSendData);
		

		//		Vector<Integer> dataList1 = new Vector<Integer>();
		//		dataList1.add(moveshortdata); //保存新的数据
		//		SEND_DATA_STRUCT mSendData1  = new SEND_DATA_STRUCT();
		//		mSendData1.eDataType         = DATA_TYPE.INT_16;                   //数据类型
		//		mSendData1.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
		//		PlcRegCmnStcTools.setRegIntData(mAVInfo.getMoveCtrlAddr(), dataList1, mSendData1); 		

//		Vector<Integer> dataList2 = new Vector<Integer>();
//		dataList2.add(stateshortdata); //保存新的数据
//		SEND_DATA_STRUCT mSendData2  = new SEND_DATA_STRUCT();
//		mSendData2.eDataType         = DATA_TYPE.INT_16;                   //数据类型
//		mSendData2.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
//		PlcRegCmnStcTools.setRegIntData(mAVInfo.getChangeCtrlAddr(), dataList2, mSendData2);
//
//		Vector<Integer> dataList3 = new Vector<Integer>();
//		dataList3.add(mXPosCtrlAddr); //保存新的数据
//		SEND_DATA_STRUCT mSendData3  = new SEND_DATA_STRUCT();
//		mSendData3.eDataType         = DATA_TYPE.INT_16;                   //数据类型
//		mSendData3.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
//		PlcRegCmnStcTools.setRegIntData(mAVInfo.getXPosCtrlAddr(), dataList3, mSendData3);	
//
//		Vector<Integer> dataList4 = new Vector<Integer>();
//		dataList4.add(mYPosCtrlAddr); //保存新的数据
//		SEND_DATA_STRUCT mSendData4  = new SEND_DATA_STRUCT();
//		mSendData4.eDataType         = DATA_TYPE.INT_16;                   //数据类型
//		mSendData4.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_W;  //本次为写操作
//		PlcRegCmnStcTools.setRegIntData(mAVInfo.getYPosCtrlAddr(), dataList4, mSendData4);

	}//End of change

	@Override
	public void getDataFromDatabase() {
		// TODO Auto-generated method stub
		
	}



}//End of class
