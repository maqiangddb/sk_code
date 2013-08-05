//import SKGraphCmnShow;
package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.Log;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.FlowBlockModel;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.SPEED;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

public class SKFlowBlock extends SKGraphCmnShow {
	private FlowBlockModel flowBlockModel;// 流动块实体类
	private Rect mRect;
	private float everyWidth;// 每一个流动块的宽度
	private int index = 0;// 时间索引
	private int nUpdateTime = 0;// 时间更新值
	private SKItems skItem;
	private int sceneid;
	private int itemId = 1;
	private boolean isFlow;// 是否流动(根据触发地址判断)
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;
	private Paint mLinePaint;
	private DIRECTION eShowWay;//当前的流动方向
	private Flow mFlow;
	private Bitmap mDuctBitmap;
	private Bitmap mFlowBitmap;
	private Bitmap mLineBitmap; 
	private Paint mBitmapPaint;//图片专用

	// 构造方法
	public SKFlowBlock(int sceneid, int itemId,FlowBlockModel model) {
		super();
		isShowFlag = true;
		showByUser = false;
		showByAddr = false;
		this.sceneid = sceneid;
		this.itemId = itemId;
		
		mRect=new Rect();
		this.flowBlockModel=model;
		
		mDuctBitmap=getBitmap();
		mFlowBitmap=getFlowBitmap();
		
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
		
		if (flowBlockModel!=null) {
			skItem=new SKItems();
			Rect mRect=new Rect(flowBlockModel.getStartX(), flowBlockModel.getStartY(),
					flowBlockModel.getStartX() + flowBlockModel.getRectWidth(),
					flowBlockModel.getStartY() + flowBlockModel.getRectHeight());
			
			skItem.nCollidindId = flowBlockModel.getnCollidindId();
			skItem.nZvalue = flowBlockModel.getnZvalue();
			skItem.rect = mRect;
			skItem.itemId = itemId;
			skItem.sceneId = sceneid;
			skItem.mGraphics=this;
		}

	}

	@Override
	public void initGraphics() {
		init();
	}

	private void init() {

		if (null == flowBlockModel) {
			return;
		}

		if (null != flowBlockModel.getShowInfo()) {
			if (null != flowBlockModel.getShowInfo().getShowAddrProp()) {
				showByAddr = true;
			}
			if (flowBlockModel.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}

		isFlow=false;//是否流动
		
		initFlow();
		
		// 初始化显现标志
		flowIsShow();
		// 注册地址
		registerAddr();
		
		SKSceneManage.getInstance().onRefresh(skItem);
	}

	/**
	 * 初始化流动块
	 */
	private void initFlow(){
		
		/**
		 * 把流动块和背景互换
		 * 减少画的次数，提高刷新效率
		 */
		mFlow=new Flow();
		
		if (flowBlockModel.geteShowWay() == DIRECTION.LEVEL) {
			
			mFlow.nStartId=2;
			mFlow.eDirection=DIRECTION.LEVEL;
			mRect.top=flowBlockModel.getStartY();
			mRect.bottom=flowBlockModel.getStartY()+flowBlockModel.getRectHeight();
			everyWidth = (float) flowBlockModel.getRectWidth() / (flowBlockModel.getnFlowNum() * 3);
			if (flowBlockModel.geteFlowDirection() == DIRECTION.TOWARD_LEFT) {
				//向左
				mFlow.add=false;
				eShowWay=DIRECTION.TOWARD_LEFT;
			}else {
				//向右
				mFlow.add=true;
				eShowWay=DIRECTION.TOWARD_RIGHT;
			}
			
		} else {
			
			everyWidth = (float) flowBlockModel.getRectHeight() / (flowBlockModel.getnFlowNum() * 3);
			mFlow.nStartId=2;
			mFlow.eDirection=DIRECTION.VERTICAL;
			mRect.left=flowBlockModel.getStartX();
			mRect.right=flowBlockModel.getStartX()+flowBlockModel.getRectWidth();
			if (flowBlockModel.geteFlowDirection()==DIRECTION.TOWARD_TOP) {
				//向上
				mFlow.add=false;
				eShowWay=DIRECTION.TOWARD_TOP;
			}else {
				//向下
				mFlow.add=true;
				eShowWay=DIRECTION.TOWARD_BOTTOM;
			}
		}
		
		if(!flowBlockModel.isbSizeLine()){
			//有边框
			mLinePaint=new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setColor(flowBlockModel.getnFrameColor());
			mLinePaint.setStrokeWidth(1);
			mLinePaint.setStyle(Style.STROKE);
			mLineBitmap=getLineBitmap();
		}
       
	}
	
	/**
	 * 注册地址
	 */
	private void registerAddr() {
		if (showByAddr) {
			ADDRTYPE addrType = flowBlockModel.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(flowBlockModel.getShowInfo().getShowAddrProp(),
						showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(flowBlockModel.getShowInfo().getShowAddrProp(),
						showCall, false);
			}

		}
		if (null != flowBlockModel.getnTriggerAddress()) {
			SKPlcNoticThread.getInstance().addNoticProp(flowBlockModel.getnTriggerAddress(),
					triggerCall, true);// 触发地址
		}
		if (flowBlockModel.isbTouchAddress() && null != flowBlockModel.getnTouchAddress()) {
			SKPlcNoticThread.getInstance().addNoticProp(flowBlockModel.getnTouchAddress(),
					touchCall, true);// 反方向触控地址
		}

		if (flowBlockModel.geteSpeedType() == SPEED.TRENDFLOWSPEED
				&& null != flowBlockModel.getnTrendFlowAddress()) {
			SKPlcNoticThread.getInstance().addNoticProp(flowBlockModel.getnTrendFlowAddress(),
					trendCall, false);// 动态流动速度地址
		}
		
		setSKTimer();

	}
	
	private void setSKTimer(){

		// 当流动类型为固定流动速度时
		if (flowBlockModel.geteSpeedType() == SPEED.FIXEDFLOWSPEED) {
			if (flowBlockModel.geteFixedFlowSpeed() == SPEED.HIGH) {// 当固定流动速度为高时
				nUpdateTime=2;
			} else if (flowBlockModel.geteFixedFlowSpeed() == SPEED.MIDDLE) {// 当固定流动速度为中时
				nUpdateTime=3;
			} else {// 当固定流动速度为低时
				nUpdateTime=5;
			}

		} else {// 当流动类型为动态流动速度时
			switch(flowBlockModel.getnTrendFlowSpeed()){
			case 1:
				nUpdateTime = 10;
				break;
			case 2:
				nUpdateTime = 9;
				break;
			case 3:
				nUpdateTime = 8;
				break;
			case 4:
				nUpdateTime = 7;
				break;
			case 5:
				nUpdateTime = 6;
				break;
			case 6:
				nUpdateTime = 5;
				break;
			case 7:
				nUpdateTime = 4;
				break;
			case 8:
				nUpdateTime = 3;
				break;
			case 9:
				nUpdateTime = 2;
				break;
			case 10:
				nUpdateTime = 1;
				break;
			default:
				nUpdateTime = 3;	
				break;
			}
		}
		
		SKTimer.getInstance().getBinder().onRegister(callback,nUpdateTime);
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (null==flowBlockModel||this.itemId!=itemId) {
			return false;
		}
		if (isShowFlag) {
			drawGraphics(canvas);
		}
		return true;
	}

	public void drawGraphics(Canvas mcanvas) {
		//当样式为透明时不画
		if(flowBlockModel.geteStyle()==CSS_TYPE.CSS_TRANSPARENCE){
			return;
		}
		
		doFlow(0,mcanvas,0, 0);
		
	}

	/*
	 * 流动块流动方法
	 */
	public void drawFlow(Canvas canvas) {
		
		//画背景
		canvas.drawBitmap(mDuctBitmap, flowBlockModel.getStartX(), flowBlockModel.getStartY(), mBitmapPaint);
		
		
		//画流动块
		float start=0;
		if (mFlow.eDirection==DIRECTION.LEVEL) {
			start=mFlow.nStartId*everyWidth+flowBlockModel.getStartX();
		}else{
			start=mFlow.nStartId*everyWidth+flowBlockModel.getStartY();
		}
		for (int i = 0; i <flowBlockModel.getnFlowNum(); i++) {
			if (mFlow.eDirection==DIRECTION.LEVEL) {
				mRect.left=(int)start;
				mRect.right=(int)(start+everyWidth);
			}else {
				mRect.top=(int)start;
				mRect.bottom=(int)(start+everyWidth);
			}
			
			canvas.drawBitmap(mFlowBitmap, mRect.left, mRect.top, mBitmapPaint);
			start+=3*everyWidth;
		}
		
		//画外框
		if (!flowBlockModel.isbSizeLine()) {
			canvas.drawBitmap(mLineBitmap, flowBlockModel.getStartX(), flowBlockModel.getStartY(), mBitmapPaint);
		}		
		
	}

	

	/**
	 * 定时器
	 */
	SKTimer.ICallback callback = new SKTimer.ICallback() {
		@Override
		public void onUpdate() {
			if (isFlow) {
				doFlow(2, null, 0, 0);
			}
		}
	};
	
	// 设置流动方法
	private void indexs() {
		index++;
		if (index == 3) {
			index = 0;
		}
		
		if (mFlow.add) {
			int start=mFlow.nStartId;
			start++;
			if(start>2){
				start=0;
			}
			mFlow.nStartId=start;
		}else {
			int start=mFlow.nStartId;
			start--;
			if(start<0){
				start=2;
			}
			mFlow.nStartId=start;
		}
		
		SKSceneManage.getInstance().onRefresh(skItem);
	}

	@Override
	public boolean isShow() {
		flowIsShow();
		SKSceneManage.getInstance().onRefresh(skItem);
		return isShowFlag;
	}

	private void flowIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(flowBlockModel.getShowInfo());
		}
	}

	@Override
	public void realseMemeory() {
		SKTimer.getInstance().getBinder().onDestroy(callback);
		isFlow = false;
		/*注销通知接口*/
		SKPlcNoticThread.getInstance().destoryCallback(showCall);//显现
		SKPlcNoticThread.getInstance().destoryCallback(triggerCall);//触发
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);//反向触控
		SKPlcNoticThread.getInstance().destoryCallback(trendCall);//动态速度
	}



	@Override
	public void getDataFromDatabase() {
		// 绑定后台线程

	}

	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShowFlag = isShow();
		}
	};
	private Vector<Integer> mSData=null;
	private boolean result;
	
	/**
	 * 触发地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack triggerCall = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (null != nStatusValue && !nStatusValue.isEmpty()) {
				if (nStatusValue.get(0) == 1) {
					isFlow = true;// 流动
				} else {
					isFlow = false;// 不流动
				}
			}
		}
	};

	/**
	 * 反方向触控地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			//有效值为1才能流动
			if(flowBlockModel.getnValidState()==1){
				if (null != nStatusValue && !nStatusValue.isEmpty()) {
					doFlow(1,null,nStatusValue.get(0),1);
				}
			}else {
				if (null != nStatusValue && !nStatusValue.isEmpty()) {
					doFlow(1,null,nStatusValue.get(0),0);
				}
			}
		}
	};
	
	
	/**
	 * @param type执行某个方法，0=画流动块,
	 *                      1=改变方向
	 *                      2=位置改变
	 * @param canvas 画布
	 * @param value 触发值
	 * @param compare 有效值，0 or 1
	 */
	private void doFlow(int type,Canvas canvas,int value,int compare){
		if (type==0) {
			drawFlow(canvas);
		}else if(type==1){
			if (value == compare) {
				if (flowBlockModel.geteShowWay() == DIRECTION.LEVEL) {
					if (eShowWay==DIRECTION.TOWARD_LEFT) {
						eShowWay=DIRECTION.TOWARD_RIGHT;
						mFlow.add=true;
					}else {
						eShowWay=DIRECTION.TOWARD_LEFT;
						mFlow.add=false;
					}
				}else {
					if(eShowWay==DIRECTION.TOWARD_TOP){
						eShowWay=DIRECTION.TOWARD_BOTTOM;
						mFlow.add=true;
					}else {
						eShowWay=DIRECTION.TOWARD_TOP;
						mFlow.add=false;
					}
				}
			} else {
				eShowWay=flowBlockModel.geteFlowDirection();
				if (eShowWay==DIRECTION.TOWARD_TOP||eShowWay==DIRECTION.TOWARD_LEFT) {
					mFlow.add=false;
				}else {
					mFlow.add=true;
				}
			}
		}else if (type==2) {
			indexs();
		}
	}

	/**
	 * 动态流动速度地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack trendCall = new SKPlcNoticThread.IPlcNoticCallBack() {
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (mSData==null) {
				mSData=new Vector<Integer>();
			}else {
				mSData.clear();
			}
			int nLen = nStatusValue.size();
			byte[] nTmpBytes = new byte[nLen];
			for (int i = 0; i < nLen; i++) {
				nTmpBytes[i] = nStatusValue.get(i);
			}
			
			int temp=300;
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mSData);
			if(result){
				for (int i = 0; i < mSData.size(); i++) {
					temp=mSData.get(i);
				}
			}
			
			if (flowBlockModel.getnTrendFlowSpeed()!=temp&&temp>0) {
				if (temp<=10) {
					SKTimer.getInstance().getBinder().onDestroy(callback, nUpdateTime);
					flowBlockModel.setnTrendFlowSpeed(temp);
					setSKTimer();
				}
			}
			
			if (isFlow) {
				doFlow(2, null, 0, 0);
			}
		}
	};

	@Override
	public void setDataToDatabase() {

	}
	
	class Flow{
		public int nStartId;//起始id
		public boolean add;//true-增加,false-减少,向左or向上=减少，向右or向下=增加
		public DIRECTION eDirection;//方向，水平or垂直
	}
	
	/**
	 * 背景
	 */
	private Bitmap getBitmap(){
		Bitmap mBitmap=Bitmap.createBitmap(flowBlockModel.getRectWidth(), flowBlockModel.getRectHeight(), Config.ARGB_8888);
		Canvas canvas=new Canvas(mBitmap);
		Rect rect=new Rect();
		RectItem ductItem=new RectItem(rect);//管道
		float nLen=0;
		
		if (flowBlockModel.geteShowWay() == DIRECTION.LEVEL){
			//水平
			nLen=((float)flowBlockModel.getRectWidth())/(3*flowBlockModel.getnFlowNum());
			rect.left=0;
			rect.right=0;
			rect.top=0;
			rect.bottom=flowBlockModel.getRectHeight();
		}else {
			//垂直
			nLen=((float)flowBlockModel.getRectHeight())/(3*flowBlockModel.getnFlowNum());
			rect.left=0;
			rect.right=flowBlockModel.getRectWidth();
			rect.top=0;
			rect.bottom=0;
		}
		
		ductItem.setBackColor(flowBlockModel.getnFBackColor());
		ductItem.setForeColor(flowBlockModel.getnFForeColor());
		ductItem.setStyle(flowBlockModel.geteStyle());
		ductItem.setLineWidth(0);
		
		float xy=0;
		
		for (int i = 0; i < flowBlockModel.getnFlowNum()*3; i++) {
			xy+=nLen;
			if (flowBlockModel.geteShowWay() == DIRECTION.LEVEL){
				//水平
				rect.left=rect.right;
				rect.right=(int)xy;
				if (i==flowBlockModel.getnFlowNum()*3-1) {
					rect.right=flowBlockModel.getRectWidth()-1;
				}
			}else {
				//垂直
				rect.top=rect.bottom;
				rect.bottom=(int)xy;
				if (i==flowBlockModel.getnFlowNum()*3-1) {
					rect.bottom=rect.bottom-1;
				}
			}
			ductItem.resetRect();
			ductItem.draw(null, canvas);
		}
		
		return mBitmap;
	}
	
	/**
	 * 流动块
	 */
	private Bitmap getFlowBitmap(){
		
		Rect rect=new Rect();
		Bitmap mBitmap=null;
		float nLen=0;
		if (flowBlockModel.geteShowWay() == DIRECTION.LEVEL){
			//水平
			nLen=((float)flowBlockModel.getRectWidth())/(3*flowBlockModel.getnFlowNum());
			rect.left=0;
			rect.right=(int)nLen;
			rect.top=0;
			rect.bottom=flowBlockModel.getRectHeight();
			
			mBitmap=Bitmap.createBitmap((int)nLen, flowBlockModel.getRectHeight(), Config.ARGB_8888);
		}else {
			//垂直
			nLen=((float)flowBlockModel.getRectHeight())/(3*flowBlockModel.getnFlowNum());
			rect.left=0;
			rect.right=flowBlockModel.getRectWidth();
			rect.top=0;
			rect.bottom=(int)nLen;
			
			mBitmap=Bitmap.createBitmap(flowBlockModel.getRectWidth(), (int)nLen, Config.ARGB_8888);
		}
		
		Canvas canvas=new Canvas(mBitmap);
		RectItem mFlowItem =new RectItem(rect);
		
		mFlowItem.setBackColor(flowBlockModel.getnDBackColor());
		mFlowItem.setForeColor(flowBlockModel.getnDForeColor());
		mFlowItem.setStyle(flowBlockModel.geteStyle());
		mFlowItem.setLineWidth(0);
		
		mFlowItem.draw(null, canvas);
		return mBitmap;
	}

	/**
	 * 获取边框线
	 */
	private Bitmap getLineBitmap(){
		Bitmap mBitmap=Bitmap.createBitmap(flowBlockModel.getRectWidth(), flowBlockModel.getRectHeight(), Config.ARGB_8888);
		Canvas canvas=new Canvas(mBitmap);
		
		int num=flowBlockModel.getnFlowNum()*3;
		canvas.drawRect(1,1, 
				flowBlockModel.getRectWidth()-1, 
				flowBlockModel.getRectHeight()-1, mLinePaint);
		
		float startX=0;
		float startY=0;
		for (int i = 1; i < num; i++) {
			if (flowBlockModel.geteShowWay() == DIRECTION.LEVEL) {
				startX+=everyWidth;
				canvas.drawLine((int)startX, 2, (int)startX,
						flowBlockModel.getRectHeight()-2, mLinePaint);
			}else {
				startY+=everyWidth;
				canvas.drawLine(2, (int)startY,
						flowBlockModel.getRectWidth()-2,
						(int)startY, mLinePaint);
			}
		}
		
		return mBitmap;
	}
}