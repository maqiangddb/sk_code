package com.android.Samkoonhmi.skwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.SceneItemPosInfo;
import com.android.Samkoonhmi.util.ImageFileTool;

public class SKMenuPageItemView extends View{

	private Paint mPaint;
	private int nTextX;
	private int nTextY;
	private SceneItemPosInfo info;
	private Bitmap bgBitmap;
	private Bitmap mBitmap;
	private boolean flag;
	public int nSceneId;
	private Rect sRect;
	private Rect bRect;

	public SKMenuPageItemView(Context context,SceneItemPosInfo info, Bitmap bitmap) {
		super(context);
		this.info=info;
		this.nSceneId=info.mSceneId;
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0);
		mPaint.setTextSize(info.nFontSize);
		mPaint.setTypeface(Typeface.DEFAULT);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeJoin(Join.ROUND);
		flag=false;
		bgBitmap=ImageFileTool.getBitmap(R.drawable.click_bg, context);
		mBitmap = bitmap;
		
		sRect=new Rect();
		bRect=new Rect();
		
		compute();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (flag) {
			canvas.drawBitmap(bgBitmap, null, bRect, null);
		}
		canvas.drawBitmap(mBitmap, null, sRect, null);
		canvas.drawText(info.mSceneName, nTextX, nTextY, mPaint);
	}

	/**
	 * 控制view 显示的大小
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(MeasureSpec.makeMeasureSpec(info.width, MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(info.height, MeasureSpec.AT_MOST));
	}
	
	/**
	 * 计算显示位置
	 */
	private void compute(){
		int leftPadding=(int)(info.width*0.1);
		int topPadding=(int)(info.height*0.075);
		int fheight=getFontHeight(mPaint)/2;
		double padding=(info.height*0.4-fheight)/3;
		
		bRect.set(leftPadding, topPadding, info.width-leftPadding, info.height-topPadding);
		sRect.set(2*leftPadding,(int)(topPadding+padding),
				(int)(info.width-2*leftPadding),
				(int)(info.height-topPadding-2*padding-fheight));
		
		nTextX=(info.width-getFontWidth(info.mSceneName, mPaint))/2;
		nTextY=(int)(sRect.bottom+padding+10);
	}

	/**
	 * 
	 * @return 返回在页面上的位置
	 */
	public int getPagePos(){
		return info.nPagePos;
	}
	
	/**
	 * 
	 * @param pos 设置页面位置
	 */
	public void setPagePos(int pos){
		info.nPagePos = pos;
	}
		
	/**
	 * 
	 * @return 返回画面的页码
	 */
	public int getPageIndex(){
		return info.nPageId;
	}
	
	/**
	 * 
	 * @param index 设置页码
	 */
	public void setPageIndex(int index){
		info.nPageId = index;
	}
	
	
	public int getSceneId(){
		return nSceneId;
	}
	
	public SceneItemPosInfo getPosInfo(){
		return info;
	}


	/**
	 * 获取字体所占的宽度
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
	 * 设置flag的值
	 * @param value
	 */
	public void setflag(boolean value){
		flag = value;
		invalidate();
	}
	
	/**
	 * 判断位置（x,y）是否在图形区域
	 */
	public boolean isInImageRect(int x, int y){
		//去除坐标偏移
		x -= (info.nPagePos % 4) * info.width;
		y -= (info.nPagePos / 4) * info.height;
		
		return sRect.contains(x, y)? true :false;
	}

}
