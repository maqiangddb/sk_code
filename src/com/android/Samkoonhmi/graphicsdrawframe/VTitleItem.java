package com.android.Samkoonhmi.graphicsdrawframe;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.util.TextAlignUtil;

/**
 * 竖标题
 */
public class VTitleItem {

	// 省略号
	private static final String sOut = "..";
	// 省略号所占的长度
	private int nOutLen;
	// 宽
	public int nWidth = 40;
	// 高
	public int nHeight = 300;
	// 第几页
	public int nPageIndex = 0;
	// 页的左顶点X坐标
	public int nLeftX = 0;
	// 页的左顶点Y坐标
	public int nLeftY = 0;
	// 行数
	public int nRow = 4;
	// 列数
	public int nColunm = 1;
	//
	public ArrayList<String> mItems;
	// 字大小
	public int nFontSize = 10;
	// 对齐方式
	public TEXT_PIC_ALIGN mAlign = TEXT_PIC_ALIGN.CENTER;
	// 颜色
	public int nFontColor = Color.BLACK;
	// 背景颜色
	public int nPageBgColor = Color.WHITE;
	private Canvas mCanvas;
	public Bitmap mBitmap;
	private Paint mPaint;
	private int nCellWidth;
	private int nCellHeight;
	private PointF mPoint;
	private Rect mRect;
	private Rect srcRect;
	private Rect mShowRect;
	private Paint mLinePaint;
	private Paint mBitmapPaint;//图片专用
	private TextAlignUtil mTextAlignUtil;
	
	public VTitleItem(int width,int heigth,int row,Rect sRect) {
		this.nWidth=width;
		this.nHeight=heigth;
		this.nRow=row;
		mItems=new ArrayList<String>();
		initData();
		mShowRect=sRect;
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
		mTextAlignUtil=new TextAlignUtil();
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		/**
		 * 初始化所有行和列
		 */
		for (int i = 0; i < nRow; i++) {
			mItems.add("");
		}
		if(nWidth<1){
			nWidth=1;
		}
		if(nHeight<1){
			nHeight=1;
		}
		mBitmap=Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		mCanvas=new Canvas(mBitmap);
		mPaint=new Paint();
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
		nCellWidth=nWidth/nColunm;
		nCellHeight=nHeight/nRow;
		mPoint=new PointF();
		mRect=new Rect();
		mRect.set(0, 0, nCellWidth, nCellHeight);
		nOutLen=getFontWidth(sOut, mPaint);
		srcRect=new Rect(0, 0, nWidth, nHeight);
		mLinePaint=new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStyle(Style.FILL);
		mLinePaint.setStrokeWidth(1);
	}
	
	/**
	 * 画每一小格文本和线
	 */
	private int startY=0;
	private int startX=0;
	public void initPage(){
		startY=0;
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		mCanvas.drawColor(nPageBgColor);
		for (int i = 0; i < nRow; i++) {
			String text=mItems.get(i);
			startX=0;
			int fontWidth = getFontWidth(text, mPaint);
			int fontHeight = getFontHeight(mPaint);
			mTextAlignUtil.getAlign(mPoint,mAlign, mRect.height(),mRect.width(), fontWidth,fontHeight);
			String temp=getText(text, nCellWidth-nOutLen, text.length(), mPaint);
			mCanvas.drawText(temp,mPoint.x+startX, mPoint.y+startY, mPaint);
			startY+=nCellHeight;
			mCanvas.drawLine(0, startY, nWidth, startY, mLinePaint);
			
		}
	}
	
	/**
	 * 
	 */
	private int top=0,bottom=0;
	public void draw(Canvas canvas,Rect dstRect){
		if (mBitmap!=null) {
			if (dstRect==null) {
				canvas.drawBitmap(mBitmap, nLeftX, nLeftY, mBitmapPaint);
			}else {
				if (nLeftY<mShowRect.top) {
					top=Math.abs(mShowRect.top-nLeftY);
					bottom=nHeight;
				}else if(nLeftY>mShowRect.top){
					top=0;
					bottom=nHeight-Math.abs(mShowRect.top-nLeftY);
				}else if (nLeftY>mShowRect.bottom) {
					top=0;
					bottom=mShowRect.bottom;
				}
				
				srcRect.left=0;
				srcRect.right=nWidth;
				srcRect.top=top;
				srcRect.bottom=bottom;
				canvas.drawBitmap(mBitmap, srcRect, dstRect, mBitmapPaint);
			}
			
		}
	}
	
	/**
	 * 更新数据
	 */
	public void updateData(int page,ArrayList<String> data){
	
		this.nPageIndex=page;
		if (data==null||data.size()==0) {
			return;
		}
		
		for (int i = 0; i < data.size(); i++) {
			mItems.set(i, data.get(i));
		}
		
		initPage();
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
}
