package com.android.Samkoonhmi.skgraphics.plc.show;
import java.util.ArrayList;
import java.util.Vector;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.MessageInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.ARRAY_ORDER;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TextAlignUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
/**
 * 消息显示器
 * @author 刘伟江
 * @version V 1.0.0.3
 * 创建时间 2012-5-19
 * 最后修改时间 2012-5-19
 */
public class SKMsgDisplay extends SKGraphCmnShow {

	private static final String TAG="SKMsgDisplay";
	//每次移动的距离
	private static final int nMoveWidth=10;
	//移动的时间单位 100ms
	private int nMoveTime=5;
	//矩形
	private RectItem mRectItem;
	private RectItem mRectFrame;
	private Rect mRect;
	//由于滚动的需要，文字会滚出矩形区域，所以要启用自己 的canvas
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private MessageInfo mInfo;
	private Paint mPaint;
	private int startIndex;
	//移动是的位置
	private int nMoveX;
	//总共要移动几次
	private int nMoveCount;
	//测试用
	private Vector<String> sTextList;
	private SKItems item;
	//控件id
	private int nItemId;
	//画面id
	private int nSceneId;
	//状态id
	private int nStateId;
	//显示消息所占的宽度
	//private int nTextWidht;
	//字体高度
	private int nFontHeight;
	private boolean show; // 是否可显现
	private boolean showByAddr; // 是否注册显现地址
	private boolean showByUser; // 是否受用户权限控件
	private boolean flag;
	private Typeface mTypeface;
	private float nSize=10;
	private int nColor;
	private DisplayMetrics dis;
	private boolean bReset;//重新赋值，主要是状态改变和第一次初始化
	private int nLanId;
	private short nAlpha=255;
	
	public SKMsgDisplay(int itemId,int sceneId,MessageInfo info) {
		nItemId=itemId;
		nSceneId=sceneId;
		sTextList=new Vector<String>();
		flag=true;
		bReset=true;
		nStateId=0;
		nLanId=SystemInfo.getCurrentLanguageId();
		this.mInfo=info;
	}

	public void addrNoticStatus(double nStatus) {
		
	}

	@Override
	public boolean isShow() {
		itemShow();
		if (flag) {
			SKSceneManage.getInstance().onRefresh(item);
		}
		return show;
	}
	
	/**
	 * 显现
	 */
	private void itemShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(mInfo.getmShowInfo());
		}
	}


	@Override
	public void realseMemeory() {
		//消除注册
		SKTimer.getInstance().getBinder().onDestroy(callback,nMoveTime);
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(watchCall);
		//flag=true;
		if (sTextList!=null) {
			sTextList.clear();
		}
		
	}

	@Override
	public void getDataFromDatabase() {
		
	}

	@Override
	public void setDataToDatabase() {

	}
	
	/**
	 * 初始化控件
	 */
	@Override
	public void initGraphics() {
		init();
	}

	/**
	 * 画面管理调用
	 */
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (mInfo==null) {
			return false;
		}
		if(itemId==nItemId&&show){
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			if (flag) {
				//外框
				mRectFrame.draw(mPaint, canvas);
				//文本
				draw(mPaint,mCanvas,flag,sTextList);
				canvas.drawBitmap(mBitmap, mInfo.getnShowLeftTopX(), mInfo.getnShowLeftTopY(),null);
				flag=false;
			}else {
				//外框
				mRectFrame.draw(mPaint, canvas);
				//画文本
				draw(mPaint, mCanvas, false, sTextList);
				canvas.drawBitmap(mBitmap, mInfo.getnShowLeftTopX(), mInfo.getnShowLeftTopY(),null);
			}
			return true;
		}
		return false;
	}

	/**
	 * 初始化
	 */
	private void init(){
		if(mInfo==null){
			return;
		}
		
		//状态号
		flag=true;
		show=true;
		if(nLanId!=SystemInfo.getCurrentLanguageId()){
			nLanId=SystemInfo.getCurrentLanguageId();
			bReset=true;
		}
		
		if (sTextList==null) {
			sTextList=new Vector<String>();
		}
		
		if (mBitmap==null||mCanvas==null) {
			mBitmap=Bitmap.createBitmap(mInfo.getnShowWidth(), mInfo.getnShowHeight(), Config.ARGB_8888);
			mCanvas=new Canvas(mBitmap);
		}
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		
		if (bReset) {
			bReset=false;
			if (mPaint==null) {
				mPaint=new Paint();
				mPaint.setAntiAlias(true);
			}
			
			if (mInfo.getmTextList().get(nStateId).getmColors().size()>SystemInfo.getCurrentLanguageId()) {
				nColor=mInfo.getmTextList().get(nStateId).getmColors().get(SystemInfo.getCurrentLanguageId());
			}else{
				nColor=Color.BLACK;
			}
			mPaint.setColor(nColor);
			if (mInfo.getmTextList().get(nStateId).getmSize().size()>SystemInfo.getCurrentLanguageId()) {
				nSize=mInfo.getmTextList().get(nStateId).getmSize().get(SystemInfo.getCurrentLanguageId());
			}else{
				nSize=10;
			}
			if (mInfo.getmTextList().get(nStateId).getmFonts().size()>SystemInfo.getCurrentLanguageId()) {
				mTypeface=TextAlignUtil.getTypeFace(mInfo.getmTextList()
						.get(nStateId).getmFonts().get(SystemInfo.getCurrentLanguageId()));
			}else {
				mTypeface=Typeface.DEFAULT;
			}
			if (dis==null) {
				dis=new DisplayMetrics();
				dis.density = (float) 1.3125;
				dis.densityDpi = 210;
				dis.scaledDensity = (float) 1.3125;
				dis.xdpi = (float) 225.77777;
				dis.ydpi = (float) 225.77777;
				nSize=TypedValue.applyDimension(1, nSize, dis); 
			}
			
			mPaint.setTextSize(nSize);
			mPaint.setStrokeJoin(Join.ROUND);
			mPaint.setStrokeWidth(0);
		
			//字体高度
			nFontHeight=getFontHeight(mPaint);
			
			//控件在屏幕上的位置
			if (mRect==null) {
				mRect=new Rect(mInfo.getnLeftTopX(), mInfo.getnLeftTopY(),
						mInfo.getnLeftTopX()+mInfo.getnWidth(),
						mInfo.getnLeftTopY()+mInfo.getnHeight());
			}
			
			//外框
			if (mRectFrame==null) {
				mRectFrame=new RectItem(mRect);
			}
			
			if(IntToEnum.getCssType(mInfo.getmTextList().get(nStateId).getnStyle())==CSS_TYPE.CSS_TRANSPARENCE){
				nAlpha=0;
			}else {
				nAlpha=mInfo.getnAlpha();
			}
			mRectFrame.setLineColor(Color.TRANSPARENT);
			mRectFrame.setLineWidth(0);
			mRectFrame.setAlpha(nAlpha);
			mRectFrame.setLineAlpha(nAlpha);
			mRectFrame.setStyle(IntToEnum.getCssType(mInfo.getmTextList().get(nStateId).getnStyle()));
			mRectFrame.setBackColor(mInfo.getmTextList().get(nStateId).getnFrameColor());
			//mRectFrame.setForeColor(mInfo.getmTextList().get(nStateId).getnForecolor());
			
			//画矩形
			if (mRectItem==null) {
				Rect rect=new Rect(0, 0,mInfo.getnShowWidth(),mInfo.getnShowHeight());
				mRectItem=new RectItem(rect);
			}
			mRectItem.setLineColor(Color.TRANSPARENT);
			mRectItem.setLineWidth(0);
			mRectItem.setAlpha(nAlpha);
			mRectItem.setLineAlpha(nAlpha);
			mRectItem.setStyle(IntToEnum.getCssType(mInfo.getmTextList().get(nStateId).getnStyle()));
			mRectItem.setBackColor(mInfo.getmTextList().get(nStateId).getnBackcolor());
			mRectItem.setForeColor(mInfo.getmTextList().get(nStateId).getnForecolor());
		}
		
		//控件刷新信息
		if (item==null) {
			item=new SKItems();
			item.itemId=nItemId;
			item.rect=mRect;
			item.nZvalue=mInfo.getnZvalue();
			item.nCollidindId=mInfo.getnCollidindId();
			item.sceneId=nSceneId;
			item.mGraphics=this;
		}
		
		flag=true;
		String temp="";
		ArrayList<String> mTexts=mInfo.getmTextList().get(nStateId).getmTextList();
		if (mTexts!=null) {
			if (mTexts.size()>SystemInfo.getCurrentLanguageId()) {
				temp=mTexts.get(SystemInfo.getCurrentLanguageId());
			}
		}
		if (temp!=null) {
			if (!temp.equals("")) {
				sTextList.clear();
				String array[]=temp.split("\n");
				for (int i = 0; i < array.length; i++) {
					sTextList.add(array[i]);
				}
			}
		}
		
		// 显现权限
		if (mInfo.getmShowInfo() != null) {
			if (mInfo.getmShowInfo().getShowAddrProp() != null) {
				// 受地址控制
				showByAddr = true;
			}

			if (mInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
		}
		itemShow();
		
		setMoveTime();
		SKSceneManage.getInstance().onRefresh(item);
		
		//注册通知
		registerNotic();
	}
	
	private void setMoveTime(){
		nMoveTime=mInfo.getmTextList().get(nStateId).getnSpeed();
		switch (nMoveTime) {
		case 1:
			nMoveTime=10;
			break;
		case 2:
			nMoveTime=9;
			break;
		case 3:
			nMoveTime=8;
			break;
		case 4:
			nMoveTime=7;
			break;
		case 5:
			nMoveTime=6;
			break;
		case 6:
			nMoveTime=5;
			break;
		case 7:
			nMoveTime=4;
			break;
		case 8:
			nMoveTime=3;
			break;
		case 9:
			nMoveTime=2;
			break;
		case 10:
			nMoveTime=1;
			break;
		default:
			nMoveTime=5;
			break;
		}
		
		//注册定时回调
		SKTimer.getInstance().getBinder().onRegister(callback,nMoveTime);
	}
	
	/**
	 * @param init-true 初始化
	 * @param message-报警信息
	 */
	private float top=0;
	private int nAllHeight=0;
	private void draw(Paint paint,Canvas canvas,boolean init,Vector<String> message){
		//清空
		mRectItem.draw(paint, canvas);
		if (init) {
			initView();
		}
		
		if (message==null) {
			return;
		}
		
		if (init) {
			//绘制
			paint.reset();
			paint.setAlpha(255);
			paint.setAntiAlias(true);
			paint.setColor(nColor);
			paint.setTextSize(nSize);
			paint.setTypeface(mTypeface);
			nFontHeight=getFontHeight(paint);
			switch (mInfo.getmTextList().get(nStateId).geteRemove()) {
			case RIGHT_TO_LEFT:
				//从右到左
				paint.setTextAlign(Align.LEFT);
				break;
			case LEFT_TO_RIGHT:
				//左到右
				paint.setTextAlign(Align.RIGHT);
				break;
			}
		}
		
		if (message.size()>1) {
			nAllHeight=message.size()*nFontHeight;
			top=(mInfo.getnShowHeight()-nAllHeight)/2+3*nFontHeight/4;
		}else {
			top=mInfo.getnShowHeight()/2+nFontHeight/4+2;
		}
		if (top<0) {
			top=0;
		}
		for (int i = 0; i < message.size(); i++) {
			canvas.drawText(message.get(i), nMoveX,top, paint);
			top+=nFontHeight;
		}
	}
	
	/**
	 * 注册通知
	 */
	private void registerNotic(){
		//注册语言改变通知
		SKLanguage.getInstance().getBinder().onRegister(lCallback);
		
		// 注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(mInfo.getmShowInfo().getShowAddrProp(), showCall,true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(mInfo.getmShowInfo().getShowAddrProp(), showCall,false);
			}

		}
		
		// 注册监视地址
		if (mInfo.geteAddress() != null) {
			SKPlcNoticThread.getInstance().addNoticProp(
					mInfo.geteAddress(), watchCall, false);
		}
	}
	
	/**
	 * 定时回调
	 */
	SKTimer.ICallback callback=new SKTimer.ICallback() {
		
		@Override
		public void onUpdate() {
			
			if (mInfo.getmTextList()==null||mInfo.getmTextList().size()==0) {
			}else {
				//执行滚动
				if (mInfo.getmTextList().get(nStateId).geteRemove()!=ARRAY_ORDER.NOT_MOVE) {
					move();
				}
			}
		}
	};
	
	
	/**
	 * 显现
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}

	};
	
	/**
	 * 监视地址
	 */
	private Vector<Integer> data=null;
	SKPlcNoticThread.IPlcNoticCallBack watchCall=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			long value = 0;
			if (data==null) {
				data=new Vector<Integer>();
			}else{
				data.clear();
			}
			if (mInfo.geteDataType()== DATA_TYPE.POSITIVE_INT_16) {
				// 16位正整数
				boolean result=PlcRegCmnStcTools.bytesToUShorts(nStatusValue, data);
				if (!result||data.size()==0) {
					return;
				}
				value = data.get(0);
			} else if (mInfo.geteDataType() == DATA_TYPE.POSITIVE_INT_32) {
				// 32位正整数
				boolean result = PlcRegCmnStcTools.bytesToInts(nStatusValue,data);
				if (!result||data.size()==0) {
					return;
				}
				value=data.get(0);
			}
			doWatch(value);
			
		}
	};
	
	/**
	 * 监视值变化处理
	 */
	private void doWatch(long value){
		if(nStateId!=value&&value>=0){
			if (value<mInfo.getmTextList().size()) {
				nStateId=(int)value;
				resetValue();
			}
		}
	}
	
	/**
	 * 状态or语言改变时，重新设置值
	 */
	private void resetValue(){
		
		SKTimer.getInstance().getBinder().onDestroy(callback, nMoveTime);
		setMoveTime();
		
		if(IntToEnum.getCssType(mInfo.getmTextList().get(nStateId).getnStyle())==CSS_TYPE.CSS_TRANSPARENCE){
			nAlpha=0;
		}else {
			nAlpha=mInfo.getnAlpha();
		}
		
		bReset=true;
		flag=true;
		String temp="";
		ArrayList<String> mTexts=mInfo.getmTextList().get(nStateId).getmTextList();
		if (mTexts!=null) {
			if (mTexts.size()>SystemInfo.getCurrentLanguageId()) {
				temp=mTexts.get(SystemInfo.getCurrentLanguageId());
			}
		}
		sTextList.clear();
		if (temp!=null) {
			if (!temp.equals("")) {
				String array[]=temp.split("\n");
				for (int i = 0; i < array.length; i++) {
					sTextList.add(array[i]);
				}
			}
		}
		//字体大小
		if (mInfo.getmTextList().get(nStateId).getmSize().size()>SystemInfo.getCurrentLanguageId()) {
			nSize=mInfo.getmTextList().get(nStateId).getmSize().get(SystemInfo.getCurrentLanguageId());
		}else{
			nSize=10;
		}
		
		if (mInfo.getmTextList().get(nStateId).getmColors().size()>SystemInfo.getCurrentLanguageId()) {
			nColor=mInfo.getmTextList().get(nStateId).getmColors().get(SystemInfo.getCurrentLanguageId());
		}else{
			nColor=Color.BLACK;
		}
		
		if (null == dis) {
			dis=new DisplayMetrics();
			dis.density = (float) 1.3125;
			dis.densityDpi = 210;
			dis.scaledDensity = (float) 1.3125;
			dis.xdpi = (float) 225.77777;
			dis.ydpi = (float) 225.77777;
		}
		nSize=TypedValue.applyDimension(2, nSize,dis); 

		
		
		//字体类型
		if (mInfo.getmTextList().get(nStateId).getmFonts().size()>SystemInfo.getCurrentLanguageId()) {
			mTypeface=TextAlignUtil.getTypeFace(mInfo.getmTextList()
					.get(nStateId).getmFonts().get(SystemInfo.getCurrentLanguageId()));
		}else {
			mTypeface=Typeface.DEFAULT;
		}
		mRectItem.setAlpha(nAlpha);
		mRectItem.setLineAlpha(nAlpha);
		mRectItem.setStyle(IntToEnum.getCssType(mInfo.getmTextList().get(nStateId).getnStyle()));
		mRectItem.setBackColor(mInfo.getmTextList().get(nStateId).getnBackcolor());
		mRectItem.setForeColor(mInfo.getmTextList().get(nStateId).getnForecolor());
		
		mRectFrame.setAlpha(nAlpha);
		mRectFrame.setLineAlpha(nAlpha);
		mRectFrame.setStyle(IntToEnum.getCssType(mInfo.getmTextList().get(nStateId).getnStyle()));
		mRectFrame.setBackColor(mInfo.getmTextList().get(nStateId).getnFrameColor());
			
		startIndex=0;
		SKSceneManage.getInstance().onRefresh(item);
		
	}

	/**
	 * 语言切换
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback(){

		@Override
		public void onLanguageChange(int languageId) {
			nLanId=languageId;
			resetValue();
			SKSceneManage.getInstance().onRefresh(item);
		}
		
	};
	
	/**
	 * 初始化消息显示器
	 * 文本显示位置
	 * 和画笔
	 */
	private void initView(){
		//nTextWidht=getFontWidth(sTextList, mPaint);
		int width=getFontWidth(sTextList,mPaint)+mInfo.getnShowWidth()-4;
		nMoveCount=width/nMoveWidth;
		switch (mInfo.getmTextList().get(nStateId).geteRemove()) {
		case RIGHT_TO_LEFT:
			//从右到左
			nMoveX=mInfo.getnShowWidth()-4;
			startIndex=0;
			mPaint.setTextAlign(Align.LEFT);
			break;
		case LEFT_TO_RIGHT:
			//左到右
			nMoveX=0;
			startIndex=0;
			mPaint.setTextAlign(Align.RIGHT);
			break;
		case NOT_MOVE:
			//不移动
			nMoveX=(mInfo.getnShowWidth()-getFontWidth(sTextList, mPaint))/2;
			break;
		}
	}
	
	/**
	 * 移动消息显示位置
	 */
	private void move(){
		if (startIndex<nMoveCount) {
			remove();
			startIndex++;
		}else {
			switch (mInfo.getmTextList().get(nStateId).geteRemove()) {
			case RIGHT_TO_LEFT:
				//从右到左
				nMoveX=mInfo.getnShowWidth()-4;
				mPaint.setTextAlign(Align.LEFT);
				break;
			case LEFT_TO_RIGHT:
				//左到右
				nMoveX=0;
				mPaint.setTextAlign(Align.RIGHT);
				break;
			}
			startIndex=0;
		}
	}
	
	/**
	 * 滚动文本
	 */
	private void remove(){
		switch (mInfo.getmTextList().get(nStateId).geteRemove()) {
		case RIGHT_TO_LEFT:
			//从右到左
			nMoveX-=nMoveWidth;
			break;
		case LEFT_TO_RIGHT:
			//左到右
			nMoveX+=nMoveWidth;
			break;
		}
		SKSceneManage.getInstance().onRefresh(item);
	}
	
	/**
	 * 获取字体所占宽度
	 */
	private int getFontWidth(Vector<String> sList,Paint paint){
		if (sList==null||sList.size()==0) {
			return 0;
		}
		int max=0;
		int temp=0;
		for (int i = 0; i < sList.size(); i++) {
			temp=(int)paint.measureText(sList.get(i));
			if(temp>=max){
				max=temp;
			}
		}
		return max;
	}

	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint){
		FontMetrics fm=paint.getFontMetrics();
		return (int)Math.ceil(fm.descent-fm.ascent);
	}
	
	private int decent(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) fm.descent;
	}
}