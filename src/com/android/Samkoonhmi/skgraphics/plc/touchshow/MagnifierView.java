package com.android.Samkoonhmi.skgraphics.plc.touchshow;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 滑动块放大镜
 * @author Eisen
 *
 */
public class MagnifierView extends View {
	private Path mPath = new Path();  
    private Matrix matrix = new Matrix();  
    private Bitmap bitmap; 
	public MagnifierView(Context context,AttributeSet attrs) {
		super(context,attrs);
		mPath.addCircle(RADIUS, RADIUS, RADIUS, Direction.CW);  
        matrix.setScale(FACTOR, FACTOR);  
	}
	 
    //放大镜的半径   
    private static final int RADIUS = 40;  
    //放大倍数   
    private static final int FACTOR = 2;  
    private int mCurrentX, mCurrentY;  
        
      
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        mCurrentX = (int) event.getX();  
        mCurrentY = (int) event.getY();  
        invalidate();  
        return true;  
    }  
      
    @Override  
    public void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        //底图   
        canvas.drawBitmap(bitmap, 0, 0, null);  
//        canvas.drawColor(Color.YELLOW);
        
        //剪切   
        canvas.translate(mCurrentX - RADIUS, mCurrentY - RADIUS);  
        canvas.clipPath(mPath);   
        //画放大后的图   
        canvas.translate(RADIUS-mCurrentX*FACTOR, RADIUS-mCurrentY*FACTOR);  
        canvas.drawBitmap(bitmap, matrix, null);          
    }

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	} 
    
}
