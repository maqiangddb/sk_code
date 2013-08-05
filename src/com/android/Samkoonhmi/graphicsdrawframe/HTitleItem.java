package com.android.Samkoonhmi.graphicsdrawframe;

import java.util.ArrayList;

import com.android.Samkoonhmi.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.util.TextAlignUtil;


/**
 * 横标题
 */
public class HTitleItem {

	// 省略号
	private static final String sOut = "..";
	// 省略号所占的长度
	private int nOutLen;
	// 宽
	public int nWidth = 400;
	// 高
	public int nHeight = 40;
	// 页的左顶点X坐标
	public float nLeftX = 0;
	// 页的左顶点Y坐标
	public float nLeftY = 0;
	// 行数
	public int nRow = 1;
	// 实际列数
	public int nColunm = 4;
	//显示的列数
	public int nShowColunm=4;
	//
	public ArrayList<String> mItems;
	// 字大小
	public float nFontSize = 10;
	//字体类型
	public Typeface typeface;
	// 对齐方式
	public TEXT_PIC_ALIGN mAlign = TEXT_PIC_ALIGN.CENTER;
	// 颜色
	public int nFontColor = Color.BLACK;
	// 背景颜色
	public int nPageBgColor = Color.WHITE;
	//线的颜色
	public int nLineColor=Color.BLACK;
	//行的高
	public ArrayList<Double> mRowHeight;
	//行的宽
	public ArrayList<Double> mRowWidth;
	//透明度
	public int nAlpha;
	private Canvas mCanvas;
	public Bitmap mBitmap;
	private Paint mPaint;
	private double nCellWidth;
	private double nCellHeight;
	private PointF mPoint;
	private Rect mRect;
	private Rect mShowRect;
	private Paint mLinePaint;
	private Paint mBitmapPaint;//图片专用
	private TextAlignUtil mTextAlignUtil;
	
	private EnterKey mEnterKey;//返回图标
	public Context mContext;
	//private Bitmap EnterKeyBitmaps;
	

	public HTitleItem(int width, int heigth,int scolunm,int colunm, Rect sRect) {
		this.nWidth = width;
		this.nHeight = heigth;
		this.nColunm=colunm;
		this.nShowColunm=scolunm;
		mItems = new ArrayList<String>();
		mShowRect = sRect;
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
		mTextAlignUtil=new TextAlignUtil();
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		/**
		 * 初始化所有行和列
		 */
		nCellWidth = nWidth / nShowColunm;
		nCellHeight = nHeight;
		if (mRowHeight!=null) {
			if (mRowHeight.size()>0) {
				nCellHeight=mRowHeight.get(0);
			}
		}
		
		if (nColunm>nShowColunm) {
			nShowColunm=nColunm;
		}
		
		mItems.clear();
		
		for (int i = 0; i < nShowColunm; i++) {
			mItems.add("");
		}
		
		mBitmap = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStrokeWidth(0);
		if (mAlign == TEXT_PIC_ALIGN.LEFT) {
			mPaint.setTextAlign(Align.LEFT);// 文本位置
		} else if (mAlign == TEXT_PIC_ALIGN.RIGHT) {
			mPaint.setTextAlign(Align.RIGHT);// 文本位置
		} else {
			mPaint.setTextAlign(Align.CENTER);// 文本位置
		}
		mPaint.setColor(nFontColor);
		mPaint.setTextSize(nFontSize);
		if (typeface!=null) {
			mPaint.setTypeface(typeface);
		}else{
			mPaint.setTypeface(Typeface.DEFAULT);
		}
		mPoint = new PointF();
		mRect = new Rect();
		nOutLen = getFontWidth(sOut, mPaint);
		
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(nLineColor);
		mLinePaint.setStyle(Style.FILL);
		mLinePaint.setStrokeWidth(1);
	}

	/**
	 * 画每一小格文本和线
	 */
	private int startY = 0;
	private int left;
	private int right;
	private boolean drawColum;
	public void initPage() {
		startY = 0;
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		mPaint.setColor(nPageBgColor);
		mPaint.setAlpha(nAlpha);
		if (nAlpha>0) {
			mCanvas.drawRect(0, 0, nWidth, nHeight, mPaint);
		}
		
		right=0;
		left=0;
		
		mPaint.setColor(nFontColor);
		mPaint.setAlpha(255);
		double nTempWidth=0;
		for (int i = 0; i < nShowColunm; i++) {
			
			drawColum=true;
			
			nTempWidth=nCellWidth;
			if (mRowWidth!=null) {
				if (mRowWidth.size()>i) {
					nTempWidth=mRowWidth.get(i);
				}
			}
			
			if (i==0) {
				right=(int)(nLeftX+nTempWidth);
			}else {
				right+=nTempWidth;
			}
			left=(int)(right-nTempWidth);
			
			mRect.set(0, 0, (int)nTempWidth, (int)nCellHeight);
			
			if (right<mShowRect.left) {
				drawColum=false;
			}
			
			if (left>mShowRect.right) {
				break;
			}
			
			if (drawColum) {
				String text = mItems.get(i)==null?"":mItems.get(i);
				int fontWidth = getFontWidth(text, mPaint);
				int fontHeight = getFontHeight(mPaint);
				mTextAlignUtil.getAlign(mPoint,mAlign, mRect.height(),mRect.width(), fontWidth,
						fontHeight);
				String temp=text;
				if (fontWidth>nTempWidth) {
					temp = getText(text, (int)(nTempWidth - nOutLen), text.length(),
							mPaint);
				}
				mCanvas.drawText(temp, mPoint.x+left-mShowRect.left , mPoint.y + startY, mPaint);

				if (i>0&&i<nShowColunm) {
					mCanvas.drawLine(left-mShowRect.left, 0, left-mShowRect.left, nHeight, mLinePaint);
				}
			}
			
		}
				
		//画底线
		mCanvas.drawLine(0, nHeight-1, nWidth, nHeight-1, mLinePaint);
		
		//画返回键
		drawEnterKeyImage();
	}

	public void draw(Canvas canvas) {
		
		initPage();
		
		if (mBitmap!=null) {
			canvas.drawBitmap(mBitmap, mShowRect.left, mShowRect.top, mBitmapPaint);
		}
		
	}
	
	/**
	 * 更新数据
	 */
	public void updateData(ArrayList<String> data){
	
		if (data==null||data.size()==0) {
			return;
		}
		
		for (int i = 0; i < data.size(); i++) {
			if (i<mItems.size()) {
				mItems.set(i, data.get(i));
			}
		}
		
		initPage();
		
	}

	/**
	 * 重新设置paint
	 */
	public void resetPaint(){
		mPaint.setTextSize(nFontSize);
		if (typeface!=null) {
			mPaint.setTypeface(typeface);
		}else{
			mPaint.setTypeface(Typeface.DEFAULT);
		}
	}
	
	/**
	 * 销毁数据
	 */
	public void destory(){
		if(mItems!=null){
			mItems.clear();
		}
	}
	
	/**
	 * 获取字体所占宽度
	 * @param font-文本
	 * @param paint-已经设置大小的画笔
	 */
	private int getFontWidth(String font, Paint paint) {
		if(font==null){ 
			return 0;
		}else{
			return (int) paint.measureText(font);
		}
	}

	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	/**
	 * 字符串截取
	 * @param text-原始文本
	 * @param width-显示区域的宽度
	 * @param len-文本的长度
	 * @param paint-已经设置好大小的画笔
	 */
	private String getText(String text, int width, int len, Paint paint) {
		String mText = "";
		int mLen = 0;
		if(text != null){
			if (!text.equals("")) {
				for (int i = 0; i < len; i++) {
					mLen += getFontWidth(text.charAt(i) + "", paint);
					if (mLen >= width) {
						return mText + sOut;
					}
					mText += text.charAt(i);
				}
			}
		}
		return mText;
	}
	
	/**
	 * 设置是否显示返回键
	 * @param isShow
	 */
	public void setEnterKeyShow(boolean isShow){
		if(isShow){
			if(null == mEnterKey)
				mEnterKey = new EnterKey();
		}else{
			mEnterKey = null;
			System.gc();
		}
	}
	
	/**
	 * 获得是否显示返回键，外部方法
	 * @return
	 */
	public boolean getEnteKeyShow(){
		if(null == mEnterKey){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 画返回键
	 * 返回键位置相对固定，不需传入参数
	 * @param canvas 画布
	 * @param paint 画笔
	 */
	private void drawEnterKeyImage(){

		if(getEnteKeyShow()){
			mEnterKey.OnDraw();
		}
	}
	
	/**
	 * 触摸响应
	 * @param event 触摸事件
	 * @return true--标题栏响应触摸事件；false--标题栏不响应触摸事件
	 */
	public boolean onTouchEvent(MotionEvent event){
		if(getEnteKeyShow()){
			return mEnterKey.OnTouch(event);
		}
		return false;
	}
	
	class EnterKey{
		private EnterKeyCallBack iEnterKeyCallBack;//返回键响应
		private Rect EnterKeyRect;//返回键响应区域
		
		/**
		 * 实列返回键
		 */
		public EnterKey(){
//			int tempHeight = mRowHeight.get(0).intValue();
//			int tempWidth = mRowWidth.get(0).intValue();
//			//防止图片过度变形
////			if(tempHeight*4 > tempWidth*3){
////				tempHeight = tempWidth*3/4;
////			}else{
////				tempWidth = tempHeight*4/3;
////			}
			Double tempDouble = mRowHeight.get(0)>mRowWidth.get(0)?mRowWidth.get(0):mRowHeight.get(0);
			int temp = tempDouble.intValue();
			
			EnterKeyRect = new Rect();
			EnterKeyRect.top =  mShowRect.top;
			EnterKeyRect.left = mShowRect.left;
			EnterKeyRect.bottom = EnterKeyRect.top+temp;
			EnterKeyRect.right = EnterKeyRect.left+temp;
		}
		
		/**
		 * 返回键触摸响应
		 * @param event 触摸事件
		 * @return
		 */
		private boolean OnTouch(MotionEvent event){
			float X = event.getX();
			float Y = event.getY();
			boolean result = false;
			if(null != EnterKeyRect){
				if(EnterKeyRect.contains((int)X,(int) Y)){
					result = true;//响应触摸
					
					switch(event.getAction()){
						case MotionEvent.ACTION_DOWN:	
							break;
							
						case MotionEvent.ACTION_UP:
							if(null != iEnterKeyCallBack){
								iEnterKeyCallBack.onPress();
								return true;
							}
							break;
							
						default:
							break;
					}
				}
			}
			
			return result;
		}
		
		/**
		 * 画返回键
		 */
		private void OnDraw(){
			Bitmap EnterKeyBitmaps = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back);
			
			if(null != EnterKeyBitmaps){
				//取图片和表格首列的最小值
				//int tempHeight = EnterKeyBitmaps.getHeight()>mRowHeight.get(0).intValue()?mRowHeight.get(0).intValue():EnterKeyBitmaps.getHeight();
				//int	tempWidth = EnterKeyBitmaps.getWidth()>mRowWidth.get(0).intValue()?mRowWidth.get(0).intValue():EnterKeyBitmaps.getWidth();
//				int tempHeight = mRowHeight.get(0).intValue();
//				int tempWidth = mRowWidth.get(0).intValue();
//				//防止图片过度变形
//				if(tempHeight*4 > tempWidth*3){
//					tempHeight = tempWidth*3/4;
//				}else{
//					tempWidth = tempHeight*4/3;
//				}

				Double tempDouble = mRowHeight.get(0)>mRowWidth.get(0)?mRowWidth.get(0):mRowHeight.get(0);
				int temp = tempDouble.intValue();
				
				//设置图片显示区域，相对于画布
				Rect dstRect = new Rect(0,0,temp,temp);
				mCanvas.drawBitmap(EnterKeyBitmaps, null, dstRect, mBitmapPaint);
			}
		}
	}
	
	
	/**
	 * 外部接口，设置返回键触摸响应
	 * @author Administrator
	 *
	 */
	public interface EnterKeyCallBack{
		/**
		 * 按下ENTER键后响应
		 */
		void onPress();
	}
	
	/**
	 * 外部方法，设置返回键响应接口
	 * @param iEKCB 接口实列
	 */
	public void SetEnterKeyCallBack(EnterKeyCallBack iEKCB){
		if(getEnteKeyShow()){
			mEnterKey.iEnterKeyCallBack = iEKCB;
		}		
	}
	
	/**
	 * 外部方法，获得标题显示范围，相对于屏
	 * @return
	 */
	public Rect getmShowRect(){
		return mShowRect;
	}
}
