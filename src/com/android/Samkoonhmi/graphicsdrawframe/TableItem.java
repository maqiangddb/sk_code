package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.TextAlignUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Join;
import android.view.MotionEvent;

/**
 * DragTable 表格里面每一小格
 * @author 刘伟江
 * @version V 1.0.0.1
 * 创建时间 2012-5-15
 * 最后修改时间 2012-5-15
 */
public class TableItem {
	// 省略号
	private static final String sOut = "...";
	// 省略号所占的长度
	private int nOutLen;
	// 实际文本所占的长度
	private int nTextLen;
	// 实际文本的个数
	private int nTextCount;
	//距离边框的距离
	private short nPadding;
	//字体的大小
	private short nFontSize;
	//字体的高度
	private short nFontHeight;
	//线的颜色
	private int nFrameColor;
	//字体的颜色
	private int nTextColor;
	//背景颜色
	private int nBackcolor;
	//宽度
	private int nWidth;
	//高度
	private int nHeight;
	//左顶点X坐标
	private int nLeftX;
	//左顶点Ｙ坐标
	private int nLeftY;
	//文本内容
	private String sText;
	//每小格的画布
	private boolean isChange;
	private Bitmap mBitmap;
	private Paint paint;
	private boolean init;
	private Rect srcRect;
	private Rect dstRect;
	//private int nHMoveLen;
	//private int nVMoveLen;
	private int left;
	private int top;
	private int right;
	private int bottom;
	public Point mLpPoint;
	public Point mRbPoint;
	private PointF mPoint;
	private TEXT_PIC_ALIGN mAlign;
	public boolean drawClickBg;
	private static Bitmap mClickBitmap;
	private Canvas mCanvas;
	private TextAlignUtil mTextAlignUtil;
	
	public TableItem(Context context) {
		this.nPadding = 2;
		this.nFontSize = 15;
		this.nWidth = 80;
		this.nHeight = 40;
		this.nLeftX = 0;
		this.nLeftY = 0;
		this.nTextColor = Color.BLACK;
		this.nTextLen = 0;
		this.nTextCount = 0;
		this.nOutLen = 0;
		this.init=true;
		this.nFontHeight=15;
		//this.nHMoveLen=0;
		//this.nVMoveLen=0;
		this.left=0;
		this.top=0;
		this.mAlign=TEXT_PIC_ALIGN.CENTER;
		mLpPoint=new Point(0,0);
		mRbPoint=new Point(0,0);
		srcRect=new Rect();
		dstRect=new Rect();
		mPoint=new PointF();
		mTextAlignUtil=new TextAlignUtil();
		if (mClickBitmap==null) {
			mClickBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.item_click_bg);
		}
	}

	/**
	 * 
	 */
	public void draw(Paint paint, Canvas canvas,Rect rRect) {
		
		if (init) {
			this.paint = paint;
			if (mBitmap==null) {
				mBitmap = Bitmap.createBitmap(nWidth , nHeight, Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			mCanvas.drawColor(nBackcolor);
			
			paint.setAntiAlias(true);
			paint.setStrokeJoin(Join.ROUND);
			paint.setStrokeWidth(0);
			paint.setTextSize(nFontSize);
			paint.setColor(nTextColor);
			paint.setStyle(Style.FILL);
			
			srcRect.set(0, 0, nWidth, nHeight);
			dstRect.set(nLeftX,nLeftY,nLeftX+nWidth,nLeftY+nHeight);
			// 文本位置
			if (mAlign == TEXT_PIC_ALIGN.LEFT) {
				paint.setTextAlign(Align.LEFT);// 文本位置
			} else if (mAlign == TEXT_PIC_ALIGN.RIGHT) {
				paint.setTextAlign(Align.RIGHT);// 文本位置
			} else {
				paint.setTextAlign(Align.CENTER);// 文本位置
			}
			
			nTextLen = getFontWidth(sText, paint);
			nOutLen = getFontWidth(sOut, paint);
			nTextCount = sText.length();
			if (nTextLen > nWidth) {
				String temp=getText(sText, nWidth - nOutLen, nTextCount, paint);
				int fontWidth=getFontWidth(sText, paint);
				int fontHeight=getFontHeight(paint);
				mTextAlignUtil.getAlign(mPoint,mAlign, srcRect.height(),srcRect.width(),fontWidth,fontHeight);
				mCanvas.drawText(temp, mPoint.x, mPoint.y, paint);
				
			} else {
				int fontWidth=getFontWidth(sText, paint);
				int fontHeight=getFontHeight(paint);
				mTextAlignUtil.getAlign(mPoint,mAlign, srcRect.height(),srcRect.width(),fontWidth,fontHeight);
				mCanvas.drawText(sText, mPoint.x, mPoint.y, paint);
			}
			init=false;
		}
		
		//Log.d(sText, "sText:"+sText+",init:"+init);
		if (rRect==null) {
			canvas.drawBitmap(mBitmap, nLeftX , nLeftY, null);
		}else {
			left=0;
			right=nWidth;
			top=0;
			bottom=nHeight;
			
			if (nLeftX<mLpPoint.x) {
				left=mLpPoint.x-nLeftX;
			}
			
			if (nLeftX>mRbPoint.x) {
				right=nWidth-(nLeftX+nWidth-mRbPoint.x);
			}
			
			if (nLeftY<mLpPoint.y) {
				top=mLpPoint.y-nLeftY;
				if (top==nHeight) {
					top=0;
				}
			}
			
			if (nLeftY+nHeight>mRbPoint.y) {
				bottom=nHeight-(nLeftY+nHeight-mRbPoint.y);
			}
			
			/**
			 * @param src 与 dst 的长宽必须一样长，不然会造成压缩
			 */
			srcRect.set(left, top, right, bottom);
			canvas.drawBitmap(mBitmap, srcRect, rRect, null);
		}
		
		if (drawClickBg) {
			if(mClickBitmap!=null){
				canvas.drawBitmap(mClickBitmap, null, dstRect, null);
				drawClickBg=false;
			}
		}
	}

	/**
	 * 重新设置大小,更新位置
	 */
	public void reset() {
		
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		mCanvas.drawColor(nBackcolor);
		
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeWidth(0);
		paint.setTextSize(nFontSize);
		paint.setColor(nTextColor);
		paint.setStyle(Style.FILL);
		
		// 文本位置
		if (mAlign == TEXT_PIC_ALIGN.LEFT) {
			paint.setTextAlign(Align.LEFT);// 文本位置
		} else if (mAlign == TEXT_PIC_ALIGN.RIGHT) {
			paint.setTextAlign(Align.RIGHT);// 文本位置
		} else {
			paint.setTextAlign(Align.CENTER);// 文本位置
		}

		nTextLen = getFontWidth(sText, paint);
		nOutLen = getFontWidth(sOut, paint);
		nTextCount = sText.length();
		if (nTextLen > nWidth) {
			String temp = getText(sText, nWidth - nOutLen, nTextCount, paint);
			int fontWidth = getFontWidth(sText, paint);
			int fontHeight = getFontHeight(paint);
			mTextAlignUtil.getAlign(mPoint,mAlign, srcRect.height(),srcRect.width(), fontWidth,
					fontHeight);
			mCanvas.drawText(temp, mPoint.x, mPoint.y, paint);

		} else {
			int fontWidth = getFontWidth(sText, paint);
			int fontHeight = getFontHeight(paint);
			mTextAlignUtil.getAlign(mPoint,mAlign, srcRect.height(),srcRect.width(), fontWidth,
					fontHeight);
			mCanvas.drawText(sText, mPoint.x, mPoint.y, paint);
		}
	}
	
	/**
	 * 触摸事件
	 */
	public boolean onTouchEvent(MotionEvent event){
		SKSceneManage.getInstance().time=0;
		return false;
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
		if (!text.equals("")) {
			for (int i = 0; i < len; i++) {
				mLen += getFontWidth(text.charAt(i) + "", paint);
				if (mLen >= width) {
					return mText + sOut;
				}
				mText += text.charAt(i);
			}
		}
		return mText;
	}

	/**
	 * 获取字体所占宽度
	 * @param font-文本
	 * @param paint-已经设置大小的画笔
	 */
	private int getFontWidth(String font, Paint paint) {
		return (int) paint.measureText(font);
	}
	
	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint){
		FontMetrics fm=paint.getFontMetrics();
		return (int)Math.ceil(fm.descent-fm.top)+2;
	}
	
	public short getnFontSize() {
		return nFontSize;
	}

	public void setnFontSize(short nFontSize) {
		this.nFontSize = nFontSize;
	}

	public short getnFontHeight() {
		return nFontHeight;
	}

	public void setnFontHeight(short nFontHeight) {
		this.nFontHeight = nFontHeight;
	}

	public int getnFrameColor() {
		return nFrameColor;
	}

	public void setnFrameColor(int nFrameColor) {
		this.nFrameColor = nFrameColor;
	}
	
	public int getnTextColor() {
		return nTextColor;
	}

	public void setnTextColor(int nTextColor) {
		this.nTextColor = nTextColor;
	}

	public int getnBackcolor() {
		return nBackcolor;
	}

	public void setnBackcolor(int nBackcolor) {
		this.nBackcolor = nBackcolor;
	}

	public int getnWidth() {
		return nWidth;
	}

	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}

	public int getnHeight() {
		return nHeight;
	}

	public void setnHeight(int nHeight) {
		this.nHeight = nHeight;
	}

	public int getnLeftX() {
		return nLeftX;
	}

	public void setnLeftX(int nLeftX) {
		this.nLeftX = nLeftX;
	}

	public int getnLeftY() {
		return nLeftY;
	}

	public void setnLeftY(int nLeftY) {
		this.nLeftY = nLeftY;
	}

	public String getsText() {
		return sText;
	}

	public void setsText(String sText) {
		this.sText = sText;
	}
	
	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}
	
//	public void setnHMoveLen(int len) {
//		this.nHMoveLen -= len;
//	}
//	
//	public void setnVMoveLen(int len) {
//		this.nVMoveLen -= len;
//	}
	public TEXT_PIC_ALIGN getmAlign() {
		return mAlign;
	}

	public void setmAlign(TEXT_PIC_ALIGN mAlign) {
		this.mAlign = mAlign;
	}
}
