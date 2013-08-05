package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.ComboBoxInfo;
import com.android.Samkoonhmi.model.ComboxItemInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TextAlignUtil;
import com.android.Samkoonhmi.util.TextAttribute;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;


//import SKGraphCmnTouch;
/**
 * 下拉框
 * 
 * @author Administrator
 * 
 */
public class SKComboImgbox extends SKGraphCmnTouch{
	private ComboBoxInfo info;
	private Paint mPaint;
	private Rect myRect;
	private SKItems items;
	private int itemId;
	private int sceneId;
	private boolean isOnClick;
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean touchByUser;
	private boolean showByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private myMainHandler hand = null;
	private Bitmap mLockBitmap;
	private Bitmap ComboBitmap;//总画布
	private Canvas mCanvas;//关联ComboBitmap
	private Bitmap[] BitmapList;//素图
	private Bitmap mBitmaptemp;//过度画布
	private int eachWidth,eachHeight;
	private List<String> ImageList;//要根据上位数据更改
	private int ShowImgCount=3;//上位设置
	private float moveAngle=0;//共移动角度
	private float moveStepAngle;//每次移动角度
	private float moveScroll=0;//TP_MOVE时移动的距离
	private int position=0;//当前图片
	private float space;
	private final int step=5; 
	private final float eachAngle=30;
	private Rect midRect;
	private int showhalfacount;
	private Typeface typeface;
	public SKComboImgbox(int itemId, int sceneId, ComboBoxInfo info) {
		isOnClick = false;
		isTouchFlag = true;
		isShowFlag = true;
		touchByUser = false;
		showByUser = false;
		showByAddr = false;
		touchByAddr = false;
		this.itemId = itemId;
		this.sceneId = sceneId;
		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);
		this.info = info;
		if(info!=null){		
			// 控件的矩形大小
			myRect = new Rect(info.getnStartX(), info.getnStartY(),
					info.getnStartX() + info.getnWidth(), info.getnStartY()
							+ info.getnHeight());
			
			eachWidth = info.getnWidth()/ShowImgCount;
			eachHeight = info.getnHeight();
			space = eachWidth/step;
			showhalfacount = this.ShowImgCount/2;
			position=(position=(info.getnShowNumber()-1)/2)<0?0:position;
			midRect=new Rect(info.getnStartX()+eachWidth*showhalfacount,info.getnStartY(),
					info.getnStartX()+(showhalfacount+1)*eachWidth,info.getnStartY()+eachHeight);
			items = new SKItems();
			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = this.sceneId;
			items.rect = myRect;
			items.mGraphics=this;
		}

	}

	/**
	 * 初始化数据
	 */
	private void init() {
		if (null == info) {
			return;
		}
		//显示素材初始化 start
		ComboBitmap = Bitmap.createBitmap(info.getnWidth(), info.getnHeight(), Config.ARGB_8888);
		mCanvas = new Canvas(ComboBitmap);
		ImageList = new ArrayList<String>();
		for(int i=0;i<info.getnShowNumber();i++){
			ImageList.add(info.getFunctionList().get(i).getPicPath());
		}
		CreateBitmapList();
		mBitmaptemp = Bitmap.createBitmap(eachWidth, eachHeight,Config.ARGB_8888);
		moveStepAngle = eachAngle/step;
		if (null != info.getsFontType()) {
			String mFontFamly = info.getsFontType();
			if (!TextUtils.isEmpty(mFontFamly)) {
				typeface = TextAlignUtil.getTypeFace(mFontFamly);
			}
		}
		//显示素材初始化end
		if (null != info.getTouchInfo()) {
			if (null != info.getTouchInfo().getTouchAddrProp()) {
				touchByAddr = true;
			}
			if (info.getTouchInfo().isbTouchByUser()) {
				touchByUser = true;
			}
		}
		if (null != info.getShowInfo()) {
			if (null != info.getShowInfo().getShowAddrProp()) {
				showByAddr = true;
			}
			if (info.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}
		
		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			SKSceneManage.getInstance().getCurrentInfo()
					.getnSceneHeight();
		}
		
		// 注册地址接口
		registAddr();
		comboxIsShow();
		comboxIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
	}
	private void CreateBitmapList(){
		if(ImageList == null) return;
		BitmapList = new Bitmap[ImageList.size()];
		for(int i=0;i<ImageList.size();i++){
			Bitmap bimg = ImageFileTool.getBitmap(this.ImageList.get(i));
			if(bimg==null) {
				continue;
			}
//			bimg = Bitmap.createScaledBitmap(bimg, eachWidth, eachHeight, true);
			//倒影效果
			bimg = createReflectedImages(bimg);
			//功能名称
//			drawText(i,bimg);
			
			BitmapList[i]=bimg;
		}
	}
	/**
	 * 倒影效果
	 * @param originalImage
	 * @return
	 */
	private Bitmap createReflectedImages(Bitmap originalImage) {		
		final int reflectionGap = 4;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		//倒影图
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 4*3, width, height / 4, matrix, false);
		//总图
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 4), Config.ARGB_8888);
		//画布关联总图
		Canvas canvas = new Canvas(bitmapWithReflection);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// 在画布左上角（0,0）绘制原始图
		canvas.drawBitmap(originalImage, 0, 0, paint);
		//在画布绘制原图的下方画分界线
		canvas.drawRect(0, height, width, height + reflectionGap,
				paint);
		//在分界线下方绘制倒影图
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, paint);
		// 在倒影图上用带阴影的画笔绘制矩形
		LinearGradient shader = new LinearGradient(0, originalImage
				.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	/**
	 * 写功能名字
	 * @param index
	 * @param bimg
	 */
	private void drawText(int index,Bitmap bimg){
		String text=info.getFunctionList().get(index).getFunctionNames().get(SystemInfo.getCurrentLanguageId());
		if(!TextUtils.isEmpty(text)){
			Paint paint=new Paint();
			/*
			 * 字体设置
			 * START
			 * */
			float textSize = info.getNfontSize();
			paint.setTextSize(textSize);
			paint.setColor(info.getnFontColor());
			paint.setAlpha(info.getnAlpha());
			paint.setTypeface(typeface);
			// 设置字体的粗体、斜体和下划线
			if (TextAttribute.BOLD == (TextAttribute.BOLD & info.geteFontCss())) {
				paint.setFakeBoldText(true); // true为粗体，false为非粗体
			}
	
			if (TextAttribute.UNDERLINE == (TextAttribute.UNDERLINE & info.geteFontCss())) {
				paint.setUnderlineText(true);// 字体是否有下划线
			}
			if (TextAttribute.ITALIC == (TextAttribute.ITALIC & info.geteFontCss())) {// 斜体
				paint.setTextSkewX(-0.25f); // float类型参数，负数表示右斜，整数左斜
			}
			/*
			 * 字体设置
			 * END
			 * */
			Canvas canv=new Canvas(bimg);
			float textWidth=paint.measureText(text);
			float x=(bimg.getWidth()-textWidth)/2,y=bimg.getHeight()/4*3;
			canv.drawText(text,
					x, y, paint);
		}
	}
	
	private void DrawText(int index,Canvas canvas){
		String text=info.getFunctionList().get(index).getFunctionNames().get(SystemInfo.getCurrentLanguageId());
		if(!TextUtils.isEmpty(text)){
			Paint paint=new Paint();
			/*
			 * 字体设置
			 * START
			 * */
			float textSize = info.getNfontSize();
			paint.setTextSize(textSize);
			paint.setColor(info.getnFontColor());
			paint.setAlpha(info.getnAlpha());			
			paint.setTypeface(typeface);
			// 设置字体的粗体、斜体和下划线
			if (TextAttribute.BOLD == (TextAttribute.BOLD & info.geteFontCss())) {
				paint.setFakeBoldText(true); // true为粗体，false为非粗体
			}
	
			if (TextAttribute.UNDERLINE == (TextAttribute.UNDERLINE & info.geteFontCss())) {
				paint.setUnderlineText(true);// 字体是否有下划线
			}
			if (TextAttribute.ITALIC == (TextAttribute.ITALIC & info.geteFontCss())) {// 斜体
				paint.setTextSkewX(-0.25f); // float类型参数，负数表示右斜，整数左斜
			}
			/*
			 * 字体设置
			 * END
			 * */
			float textWidth=paint.measureText(text);
			int RectWidth=this.midRect.right-this.midRect.left;
			String mText="";
			float x=this.midRect.left-info.getnStartX(),y=info.getnHeight()/4*3;;
			if(RectWidth>textWidth){
				x=(RectWidth-textWidth)/2+this.midRect.left-info.getnStartX();
				mText=text;
			}else{
				int mLen = 0;
				for (int i = 0; i < text.length(); i++) {
					mLen += paint.measureText(text.charAt(i) + "");
					if (mLen >= RectWidth) {
						mText += "..";
						break;
					}
					mText += text.charAt(i);
				}
			}
			canvas.drawText(mText,x, y, paint);
		}
	}

	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.
	}
	
	float x1=0,y1=0,x2=0,y2=0;//计算是否画图
	float downX=0,downY=0,upX=0,upY=0;//计算显示图片
	long pressDown;
	boolean isPressOutside=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean result=true;
		//没有显示区域，不响应TP
		if(myRect == null){
			return false;
		}
		// 不可显现或者不可触摸
		if ((myRect.contains((int)event.getX(),(int)event.getY()))&&(!isTouchFlag || !isShowFlag)) {
			if (!isTouchFlag && info != null) {
				if (info.getTouchInfo() != null) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						if (info.getTouchInfo().isbTouchByUser()) {
							SKSceneManage.getInstance().turnToLoginPop();
						}
					}
				}
			}
			return false;
		}
		//不在显示区域
		if(!myRect.contains((int)event.getX(),(int)event.getY())){
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					isPressOutside = true;
					break;
				case MotionEvent.ACTION_MOVE:
					if(!isPressOutside){
						upX=event.getX();
						upY=event.getY();
						if(downX>upX){
							position+=(int)((downX-upX)/eachWidth);
							if((downX-upX)%eachWidth>eachWidth/2){
								position+=1;
							}
						}else if(upX>downX){
							position-=(int)((upX-downX)/eachWidth);
							if((upX-downX)%eachWidth>eachWidth/2){
								position-=1;
							}
						}
						if(position<0){
							position=0;
						}else if(position>=ImageList.size()){
							position=ImageList.size()-1;
						}
						moveAngle=0;
						moveScroll=0;
						SKSceneManage.getInstance().onRefresh(items);
						System.out.println("move to outside");
						isPressOutside=true;
						return true;
					}
					break;
			}
			return false;
		}else{
			//TP响应
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					isPressOutside=false;
					upX=downX=x1=event.getX();
					downY=event.getY();
					pressDown = System.currentTimeMillis();
				break;
				case MotionEvent.ACTION_MOVE:
					if(isPressOutside){
						System.out.println("move to inside");
						downX=x1=event.getX();
						isPressOutside=false;
					}else{
						x2=event.getX();
						if(Math.abs(x2-x1)>=space){
							if(x2>x1){
								moveAngle-=(int)((x2-x1)/space)*moveStepAngle;
								if((x2-x1)%space>space/2){
									moveAngle-=moveStepAngle;
								}
							}else{
								moveAngle+=(int)((x1-x2)/space)*moveStepAngle;
								if((x1-x2)%space>space/2){
									moveAngle+=moveStepAngle;
								}
							}
							moveScroll+=x2-x1;
							x1=x2;
							SKSceneManage.getInstance().onRefresh(items);
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					upX=event.getX();
					upY=event.getY();
					if(downX>upX){
						position+=(int)((downX-upX)/eachWidth);
						if((downX-upX)%eachWidth>eachWidth/2){
							position+=1;
						}
					}else if(upX>downX){
						position-=(int)((upX-downX)/eachWidth);
						if((upX-downX)%eachWidth>eachWidth/2){
							position-=1;
						}
					}
					if(position<0){
						position=0;
					}else if(position>=ImageList.size()){
						position=ImageList.size()-1;
					}
					moveAngle=0;
					moveScroll=0;
					SKSceneManage.getInstance().onRefresh(items);
					//执行功能
					if(
							//(System.currentTimeMillis()-pressDown>500)&&
							(Math.abs(downX-upX)<space)&&(Math.abs(downY-upY)<space)){
						//do long press function
						if(null != midRect){
							if(midRect.contains((int)upX, (int)upY)){
								System.out.println("to do the function:"+position);
								executeComboFunction(position);
							}
						}
					}
					break;
			}
		}
		return result;
	}
	
	/**
	 * 点击响应，长按生效
	 * @param position
	 */
	private void executeComboFunction(int position) {
		ComboxItemInfo comboxItem = info.getFunctionList().get(
				position);
		SKButton button = comboxItem.getButton();
		SKSceneManage.getInstance().onRefresh(items);
		button.doTouch(true, true);
		if (true == info.isbIsStartStatement()) {
			// 请求执行控件宏指令
			Log.d("Number", "宏指令执行");
			MacroManager.getInstance(null).Request(MSERV.CALLCM,
					(short) info.getnScriptId());
		}
		button.doTouch(false, true);
	}
	
	/**
	 * 延迟响应用
	 */
	private static final int TOUCHHANDER = 1;

	private class myMainHandler extends Handler {
		public myMainHandler(Looper loop) {
			super(loop);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == TOUCHHANDER) {
				if (isOnClick) {
					doTouch();
				}
			}
		}
	}

	private void doTouch() {
		
	}

	private void registAddr() {
		// 触控地址
		if (touchByAddr) {
			ADDRTYPE addrType = info.getTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance()
						.addNoticProp(info.getTouchInfo().getTouchAddrProp(),
								touchCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getTouchInfo().getTouchAddrProp(), touchCall,
						false);
			}
		}
		// 显现地址
		if (showByAddr) {
			ADDRTYPE addrType = info.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, false);
			}

		}
		// 注册多语言切换接口
		SKLanguage.getInstance().getBinder().onRegister(languageICallback);
	}

	/**
	 * 多语言切换通知刷新
	 */
	SKLanguage.ICallback languageICallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			// TODO Auto-generated method stub
			CreateBitmapList();
			SKSceneManage.getInstance().onRefresh(items);

		}
	};

	/**
	 * 触控地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isTouchFlag = isTouch();
		}

	};
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShowFlag = isShow();

		}

	};

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub\
		comboxIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;
	}

	private void comboxIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		comboxIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
		return isTouchFlag;

	}

	private void comboxIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(info.getTouchInfo());
		}
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {

		if (null == info) {
			return false;
		}
		if (this.itemId == itemId) {
			if (isShowFlag) {
				draw(mPaint, canvas);
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 控件描绘
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void draw(Paint paint, Canvas canvas) {
		// TODO Auto-generated method stub
//		long start=System.currentTimeMillis();
		getBitmap();//获取将要显示的bitmap
		//画图
		if(null != ComboBitmap){
			canvas.drawBitmap(ComboBitmap, info.getnStartX(), info.getnStartY(), paint);
		}
		// 不可触控加上锁图标
		if (!isTouchFlag) {
			if(SKSceneManage.getInstance().mContext!=null)
			{
				if (mLockBitmap == null) {
					mLockBitmap = ImageFileTool
							.getBitmap(R.drawable.lock, SKSceneManage.getInstance().mContext);
				}
			}
		
			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getnStartX(),
						info.getnStartY(), null);
			}
		}
//		System.out.println("draw expend "+(System.currentTimeMillis()-start));
	}
	/**
	 * 描绘图片
	 */
	private synchronized void getBitmap() {
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		int count=ImageList.size();
		float Scroll = this.moveScroll;
		float Angle=this.moveAngle;
		int position = this.position;
		int textIndex = -1;
		Rect dst;
		
		if(!checkEnvironment()){
			return;
		}
		
		for(int i=0;i<count;i++){
			float moveAngle=-(eachAngle)*(i-position)+Angle;
			
			if((Math.abs(moveAngle)>=eachAngle*(showhalfacount+1))||(Math.abs(moveAngle)>=90)){
				continue;
			}
			
			Bitmap bimg = BitmapList[i];
			if(bimg == null) {
				continue;
			}
			
			mCanvas.save();
			//旋转效果
			if(moveAngle!=0){
				Matrix matrix = Rotation(moveAngle);//旋转矩阵
				//适配目标大小
				matrix.preTranslate(-eachWidth/2,-eachHeight/2);
				matrix.postTranslate(eachWidth/2,eachHeight/2);
				
				Canvas cantemp = new Canvas(mBitmaptemp);
				cantemp.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				RectF dstR = new RectF(0,0,eachWidth,eachHeight);
				RectF deviceR = new RectF();
				matrix.mapRect(deviceR, dstR);
//				mBitmaptemp.setDensity(bimg.getDensity());
				//使图片从左上角开始显示
				cantemp.translate(-deviceR.left, -deviceR.top);
				cantemp.concat(matrix);
				//线性Y轴缩小
				int stepH = (int) (Math.abs(moveAngle)/300*eachHeight);
				dstR.top=stepH;dstR.bottom=dstR.bottom-stepH;
				cantemp.drawBitmap(bimg, null, dstR, mPaint);		
				//缩放
				//作图
				int stepW = (int) (dstR.right+deviceR.left-deviceR.right-dstR.left);//对称
				dst = new Rect((int)((i-position+showhalfacount)*eachWidth+Scroll+stepW/2),0,(int)((i-position+showhalfacount+1)*eachWidth+Scroll+stepW/2),eachHeight);
				mCanvas.drawBitmap(mBitmaptemp, null, dst, mPaint);
			}else{
				dst = new Rect((int)((i-position+showhalfacount)*eachWidth+Scroll),0,(int)((i-position+showhalfacount+1)*eachWidth+Scroll),eachHeight);
				mCanvas.drawBitmap(bimg, null, dst, mPaint);
				textIndex=i;
			}
			mCanvas.restore();
		}
		//画功能名字
		if(textIndex>=0){
			DrawText(textIndex,mCanvas);
		}
	}
	/**
	 * 检查描绘图片所需参数
	 * @return
	 */
	private boolean checkEnvironment(){
		boolean result = true;
		if(null == mBitmaptemp){
			Log.e("SKComboImgbox","checkEnvironment:mBitmaptemp is null!");
			result = false;
		}else if(eachWidth<=0){
			Log.e("SKComboImgbox","checkEnvironment:eachWidth<=0!");
			result = false;
		}else if(eachHeight<=0){
			Log.e("SKComboImgbox","checkEnvironment:eachHeight<=0!");
			result = false;
		}else if(null == mCanvas){
			Log.e("SKComboImgbox","checkEnvironment:mCanvas is null!");
			result = false;
		}
		return result;
	}
	/**
	 * 旋转矩阵
	 * @param moveAngle
	 * @return
	 */
	Camera mCamera = new Camera();
	private Matrix Rotation(float moveAngle) {
    	// 保存相机当前状态
        mCamera.save();
            
        // 获取变换矩阵
        final Matrix imageMatrix = new Matrix();
            
        // 在Y轴上旋转，对应图片竖向向里翻转。如果在X轴上旋转，则对应图片横向向里翻转。
        mCamera.rotateY(moveAngle);
        mCamera.getMatrix(imageMatrix);
            
        // 恢复相机原状态 
        mCamera.restore();
            
        return imageMatrix;
    }

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}


	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();
	}

	@Override
	public void realseMemeory() {
		isOnClick = false;
		// 销毁注册地址
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);
	}
}