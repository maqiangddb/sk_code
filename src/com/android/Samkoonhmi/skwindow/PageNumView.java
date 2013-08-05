package com.android.Samkoonhmi.skwindow;
import com.android.Samkoonhmi.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PageNumView extends View{

	private int mCount=0;    //总共的页数
	private int mIndex=0;    //当前处于第几页
	private int mLeft;       //左边有几页
	private int mRight;      //右边有几页
	private Bitmap offBitmap;
	private Bitmap onBitmap;
	private Paint mPaint;
	private static int mWidth=15;
	private static int mStart=0;
	private static final int mPadding=10;
	
	public PageNumView(Context context) {
		super(context);
		init();
	}

	public PageNumView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageNumView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		draw(canvas);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mStart=getWidth();
	}

	private void init(){
		offBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.point_off);
		onBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.point_on);
		mWidth=offBitmap.getWidth();
		mPaint=new Paint();
	    mPaint.setAntiAlias(true);
	}

	//
	public void draw(Canvas canvas){
		mLeft=mIndex-1;
		mRight=mCount-mIndex;
		int startX=mStart/2-((mCount*mWidth)+((mCount-1)*mPadding))/2;
		
		//左边
		if (mLeft>0) {
			for (int i = 0; i < mLeft; i++) {
				canvas.drawBitmap(offBitmap, startX+i*(mWidth+mPadding), 0, mPaint);
			}
		}
		
		//当前
		if (mCount>0) {
			canvas.drawBitmap(onBitmap, startX+(mIndex-1)*(mWidth+mPadding), 0, mPaint);
		}
		
		//右边
		if (mRight>0) {
			for (int i = 0; i < mRight; i++) {
				canvas.drawBitmap(offBitmap, startX+(mIndex+i)*(mWidth+mPadding), 0, mPaint);
			}
		}
	}
	
	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}
	
}

