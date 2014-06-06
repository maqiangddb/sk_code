//import SKGraphCmnTouch;
package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.Vector;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.KeyBoardButtonInfo;
import com.android.Samkoonhmi.model.KeyBoardInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skgraphics.SKGraphics;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.SystemParam;

public class SKKeyButton extends View{
	private KeyBoardButtonInfo keybuttonInfo;// 键盘按钮实体类
	private KeyBoardInfo keyInfo;// 键盘实体类
	private StaticTextModel textModels;// 文本实体类
	private TextItem textItem;// 文本基类
	private Rect rectButton;// 矩形按钮
	private RectItem item;// 矩形基类
	private Paint mpaint;// 画笔
	private Paint tpaint;
	private Context context;//上下文
	private Bitmap bitmaps;// 图片
	private Rect rects;//背景图片矩形
	private KEYBOARD_OPERATION keyOperation;// 按钮功能键
	public String inputText = "";// 输出文本
	private int nBackColor;// 总背景色
	private int nForeColor;// 总前景色
	private CSS_TYPE nStyle;// 总样式
	private int nFrameColor;// 总边框色
	private int nFontSize;//字体大小
	private int index;// 时间索引
	private SKKeyBoard.ICallback callback;
	//显现
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean showByAddr;
	//触控
	private boolean isTouchFlag;
	private boolean touchByUser;
	private boolean touchByAddr;
	private SKGraphics graphics;
	private SKToast toast;
	private String textShow="";
	//Toast
	private int toastX;
	private int toastY;
	private Vibrator vibrator; //蜂鸣器

	/**
	 * 构造方法
	 */
	public SKKeyButton(Context context) {
		super(context);
		this.context = context;
		
	}

	public SKKeyButton(KeyBoardButtonInfo key, Context context) {
		super(context);
		this.context = context;
		this.keybuttonInfo = key;
		textModels = new StaticTextModel();// 文本实体类
		rectButton = new Rect();// 按钮矩形
		nBackColor = keybuttonInfo.getnUpBackColor();// 背景色
		nForeColor = keybuttonInfo.getnUpForeColor();// 前景色
		nStyle = keybuttonInfo.geteUpStyle();// 样式
		nFrameColor = keybuttonInfo.getnUpFrameColor();// 边框色
		nFontSize=keybuttonInfo.getnFontSize();
		index=1;// 时间索引
		// 初始化按钮
		mpaint = new Paint();//按钮矩形画笔
		tpaint=new Paint();//文字画笔
		isShowFlag=true;
		showByUser=false;
		showByAddr=false;
		isTouchFlag = true;
		touchByUser=false;
		touchByAddr=false;
		keyOperation=keybuttonInfo.getKeyOperation();
		textShow=keybuttonInfo.getsText();
		initButton();
		vibrator = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
	}
	

	/**
	 * 初始化键盘按钮
	 */
	public void initButton() {
		// 设置矩形按钮坐标
		rectButton.left = keybuttonInfo.getnStartX()-1;// x1
		rectButton.top = keybuttonInfo.getnStartY()-1;// y1
		rectButton.right = keybuttonInfo.getnStartX() + keybuttonInfo.getnWidth()+1;// x2
		rectButton.bottom = keybuttonInfo.getnStartY() + keybuttonInfo.getnHeight()+1;// y2
		//显现权限
		if (null != keybuttonInfo.getShowInfo()) {
			if (null != keybuttonInfo.getShowInfo().getShowAddrProp()) {
				showByAddr = true;
			}
			if (keybuttonInfo.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}
		//触控权限
		if (null != keybuttonInfo.getTouchInfo()) {
			if (null != keybuttonInfo.getTouchInfo().getTouchAddrProp()) {
				touchByAddr = true;
			}
			if (keybuttonInfo.getTouchInfo().isbTouchByUser()) {
				touchByUser = true;
			} 
		}
		if(keybuttonInfo.getId()!=null){
			initSKGraphics();
			graphics.isShow();
		}
		// 文本
		textModels.setM_sTextStr(keybuttonInfo.getsText());// 字符
		textModels.setM_sFontFamly(keybuttonInfo.getsFontFamly());
		textModels.setM_stylePadding(CSS_TYPE.CSS_SOLIDCOLOR);
		textModels.setStartX(rectButton.left);// x坐标
		textModels.setStartY(rectButton.top);// y坐标
		textModels.setRectWidth(keybuttonInfo.getnWidth());// 宽度
		textModels.setRectHeight(keybuttonInfo.getnHeight());// 高度
		textModels.setM_nFontColor(keybuttonInfo.getnFontColor());// 字体颜色
		textModels.setM_nFontSize(nFontSize);// 字体大小
		textModels.setM_backColorPadding(0);// 填充背景颜色
		textModels.setM_nFontSpace(1);// 字距
		textModels.setLineColor(Color.TRANSPARENT);//文本框颜色
		textModels.setLineWidth(0);//文本框线宽
		textModels.setM_textPro(keybuttonInfo.getnFontPro());// 字体属性(粗体、斜体、下划线和闪烁)
		textModels.setM_eTextAlign(keybuttonInfo.geteFontAlign());// 文本位置
		if (textItem == null) {
			textItem = new TextItem(textModels);
			//设置画笔
			textItem.initRectBoderPaint();
			textItem.initRectPaint();
			
		}
		textItem.initTextPaint();
		// 注册地址
		registerAddr();
		// 初始化显现标志
		buttonIsShow();
		// 初始化触控标志
		buttonIsTouch();
	}

	@Override
	public void onDraw(Canvas canvas) {
		
			drawButton(canvas);// 画按钮
		
	}

	/**
	 * 画按钮
	 */
	private int nOldColor;
	public void drawButton(Canvas canvas) {
		if (isShowFlag){ 
		// 调用键盘按钮实体类方法
		initButton();
		if(item==null)
			item = new RectItem(rectButton);
			// 设置属性
		
		item.setLineColor(nFrameColor);// 边框色
		item.setLineWidth(3);//线条宽度
		if(isCaps){
			textModels.setM_nFontColor(Color.RED);
			textItem.initTextPaint();
		}else{
			textModels.setM_nFontColor(keybuttonInfo.getnFontColor());
			textItem.initTextPaint();
		}
		item.setBackColor(nBackColor);// 背景色
		item.setForeColor(nForeColor);// 前景色
		item.setStyle(nStyle);// 样式
		item.setAlpha(255);
		item.setLineAlpha(255);
		if(nStyle!=CSS_TYPE.CSS_TRANSPARENCE)
			item.draw(null,canvas);// 画按钮		
		//加图片
		if(keybuttonInfo.getsImagePath()!=null && !"".equals(keybuttonInfo.getsImagePath())){
			bitmaps = ImageFileTool.getBitmap(keybuttonInfo.getsImagePath());
			if(rects==null){
				rects=new Rect();
				rects.set(rectButton.left+3, rectButton.top+3, rectButton.left+rectButton.width()-4, rectButton.top+rectButton.height()-4);
			}
			if(bitmaps!=null)
				canvas.drawBitmap(bitmaps, null, rects, null);
		}
		textItem.draw( canvas);// 画文本
		}
	}
	
	public SKKeyBoard.ICallback getCallback() {
		return callback;
	}

	public void setCallback(SKKeyBoard.ICallback callback) {
		this.callback = callback;
	}
	
	public void setToast(SKToast toast) {
		this.toast = toast;
	}

	private boolean bClickDown;
	private long nStart=0;
	private boolean isCaps=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		//如果键盘按钮实体类为空，则返回false
		if(keybuttonInfo==null){
			return false;
		}		
		int action = event.getAction();
		boolean touchEvent = false;
		if (!isTouchFlag || !isShowFlag) {
			return false;
		}
		
		if (event.getX()<rectButton.left+3||event.getX()>rectButton.right-3
				||event.getY() < rectButton.top+3||event.getY() > rectButton.bottom-3) {
			
			if (bClickDown) {
				
				bClickDown=false;
				nStyle = keybuttonInfo.geteUpStyle();// 按下前样式
				nFrameColor = keybuttonInfo.getnUpFrameColor();// 按下前边框色
				nBackColor = keybuttonInfo.getnUpBackColor();// 按下前背景色
				nForeColor = keybuttonInfo.getnUpForeColor();// 按下前前景色
				nFontSize=keybuttonInfo.getnFontSize();// 字体大小
				if (callback != null) {
					callback.onResult(null, null,true);
				}
			}
			return false;
		}
		
		// 按钮按下后的事件处理
		if (action == MotionEvent.ACTION_DOWN ) {
			
			boolean isTouchSound = (SystemParam.USE_TOUCH_SOUND & SystemInfo
					.getnSetBoolParam()) == SystemParam.USE_TOUCH_SOUND;// 为true
																		// 开启触摸声音
			if (isTouchSound) {
				vibrator.vibrate(150/* new long[]{1000,50,1000,50}, 0 */);
			}
			
			bClickDown=true;
			touchEvent = true;
			nStyle = keybuttonInfo.geteDownStyle();// 按下后样式
			nFrameColor = keybuttonInfo.getnDownFrameColor();// 按下后边框色
			nBackColor = keybuttonInfo.getnDownBackColor();// 按下后背景色
			nForeColor = keybuttonInfo.getnDownForeColor();// 按下后前景色
			if(keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.TEXT){
				nFontSize=keybuttonInfo.getnFontSize()*3;// 字体大小
			}else{
				nFontSize=keybuttonInfo.getnFontSize()*2;// 字体大小
			}
			inputText = keybuttonInfo.getASCIIStr();
			if (callback != null) {
				if(keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.ENTER || keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.ESC
						|| keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.CLR || keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.DEL){
					callback.onResult(null, null,true);
				}else if(keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.TEXT){
					callback.onResult(inputText, keyOperation,true);
				}
			}
			int keywidth=keybuttonInfo.getnWidth()+10;
			int keyheight=keybuttonInfo.getnHeight()+10;
			if(keywidth<37){
				keywidth=36;
			}else if(keyheight<37){
				keyheight=36;
			}
			
			//Log.d("SKScene", "ACTION_DOWN..."+inputText);
		}
		if (action == MotionEvent.ACTION_UP) {// 按钮按上和移动事件处理
			//按钮范围
			bClickDown=false;
			touchEvent = true;
			if(toast!=null){
				if(keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.ENTER || keybuttonInfo.getKeyOperation()==KEYBOARD_OPERATION.ESC)
					toast.hideToast(0);
				else
					toast.hideToast(1);
			}
			nStyle = keybuttonInfo.geteUpStyle();// 按下前样式
			nFrameColor = keybuttonInfo.getnUpFrameColor();// 按下前边框色
			nBackColor = keybuttonInfo.getnUpBackColor();// 按下前背景色
			nForeColor = keybuttonInfo.getnUpForeColor();// 按下前前景色
			nFontSize = keybuttonInfo.getnFontSize();// 字体大小
			// 得到按钮字符
			inputText = keybuttonInfo.getASCIIStr();
			keyOperation = keybuttonInfo.getKeyOperation();
			
			if(keyOperation == KEYBOARD_OPERATION.CAPS){	//大小写键
			    if(isCaps){
			    	isCaps = false;
			    }else{
			    	isCaps = true;
			    }
			}
			
			if (callback != null) {
				callback.onResult(inputText, keyOperation,false);
			}
		}
		return touchEvent;
	}
	/**
	 * 注册显现地址
	 */
	private void registerAddr() {
		// TODO Auto-generated method stub
		// 注册触控通知
		if (touchByAddr) {
			ADDRTYPE addrType = keybuttonInfo.getTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						keybuttonInfo.getTouchInfo().getTouchAddrProp(), touchCall, true,0);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						keybuttonInfo.getTouchInfo().getTouchAddrProp(), touchCall, false,0);
			}
		}		
		// 注册显现通知
		if (showByAddr) {
			ADDRTYPE addrType = keybuttonInfo.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(keybuttonInfo.getShowInfo().getShowAddrProp(),
						showCall, true,0);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(keybuttonInfo.getShowInfo().getShowAddrProp(),
						showCall, false,0);
			}
		}
	}
	
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			Log.d("flow", "显现改变");
			isShowFlag = graphics.isShow();
		}
	};
	
	/**
	 * 触控地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isTouchFlag = graphics.isTouch();
		}

	};
	/**
	 * 按钮是否显示
	 */
	private void buttonIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = graphics.popedomIsShow(keybuttonInfo.getShowInfo());
		}
	}

	/**
	 * 按钮是否触控
	 */
	private void buttonIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = graphics.popedomIsTouch(keybuttonInfo.getTouchInfo());
		}
	}
	public void initSKGraphics(){
		if(graphics==null){
			graphics=new SKGraphics() {
				
				@Override
				public void realseMemeory() {
					// TODO Auto-generated method stub
					/*注销通知接口*/
					//SKPlcNoticThread.getInstance().destoryCallback(showCall);//显现
					//SKPlcNoticThread.getInstance().destoryCallback(touchCall);//触控
				}
				
				@Override
				public boolean onTouchEvent(MotionEvent event) {
					// TODO Auto-generated method stub
					SKSceneManage.getInstance().time=0;
					return false;
				}
				
				@Override
				public boolean isTouch() {
					// TODO Auto-generated method stub
					buttonIsTouch();
					return isTouchFlag;
				}
				
				@Override
				public boolean isShow() {
					// TODO Auto-generated method stub
					buttonIsShow();
					return isShowFlag;
				}
				
				@Override
				public void initGraphics() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean drawGraphics(Canvas canvas, int itemId) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void getDataFromDatabase() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void setDataToDatabase() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public IItem getIItem() {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
	}

	public int getToastX() {
		return toastX;
	}

	public void setToastX(int toastX) {
		this.toastX = toastX;
	}

	public int getToastY() {
		return toastY;
	}

	public void setToastY(int toastY) {
		this.toastY = toastY;
	}

	
	
}