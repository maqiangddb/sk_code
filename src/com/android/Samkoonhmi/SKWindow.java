package com.android.Samkoonhmi;

import com.android.Samkoonhmi.model.ScenceInfo;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
//import SKScene;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

public class SKWindow extends SKScene{

	private Bitmap mIconBitmap;
	private Bitmap mCloseBitmap;
	private String sTitleName="";
	private boolean bShow;
	private int nTitleHeight;
	private static final int nPadding=10;
	
	public SKWindow(Context context, Activity activity, ScenceInfo info) {
		super(context, activity, info);
		bShow=false;
		nTitleHeight=40;
	}
	
	public void drawTitle(){
		Paint textPaint=new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.rgb(0, 147, 255));
		
		int height=getFontHeight(textPaint);
		mCanvas.drawRect(0, 0, nSceneWidth, nTitleHeight, textPaint);
	    mCanvas.drawBitmap(mIconBitmap, nPadding, (nTitleHeight-mIconBitmap.getHeight())/2, null);
	    if (!sTitleName.equals("")) {
	    	textPaint.setStyle(Style.STROKE);
			textPaint.setStrokeWidth(0);
			textPaint.setStrokeJoin(Join.ROUND);
			textPaint.setTextSize(12);
			mCanvas.drawText(sTitleName, 2*nPadding+mIconBitmap.getWidth(), (nTitleHeight-height)/2, textPaint);
		}
	    if (bShow) {
			mCanvas.drawBitmap(mCloseBitmap, mInfo.getnSceneWidth()-mCloseBitmap.getWidth()-nPadding,
					(nTitleHeight-mCloseBitmap.getHeight())/2, null);
		}
	}
	
    public void setStyle(){
    	
    }
	
    public void setTitleName(String name){
    	sTitleName=name;
    }
    
    public void setTitleButton(boolean show){
    	bShow=show;
    }
    
    /**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint){
		FontMetrics fm=paint.getFontMetrics();
		return (int)Math.ceil(fm.descent-fm.top)+2;
	}
   
}