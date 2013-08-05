package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.LineTypeUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
//import SquareItem;
import android.graphics.Shader;

/**
 * 画扇形
 * @author Administrator
 *
 */
public class SectorItem extends SquareItem{
//	private Rect rect;
	private RectF rectf;
	private FillRender fillRender;
	private float strokWidth;
	private Shader myShader;
    public SectorItem(Rect mRect) {
		super(mRect);
		// TODO Auto-generated constructor stub
		this.setRect(mRect);
		this.m_nSpanAngle=60;
		this.m_nStartAngle=240;
		rectf=new RectF();
	}

	private float m_nStartAngle;

    private float m_nSpanAngle;


    public void setStartAngle(float nStartAngle){
        // TODO put your implementation here.	
    	this.m_nStartAngle=nStartAngle;
    }
	
    public float getStartAngle(){
		return m_nStartAngle;
        // TODO put your implementation here.	
    }
	
    public void setSpanAngle(float nSpanAngle){
        // TODO put your implementation here.
    	this.m_nSpanAngle=nSpanAngle;
    }
	
    public float getSpanAngle(){
		return m_nSpanAngle;
        // TODO put your implementation here.	
    }
	
    public void draw(Paint paint,Canvas canvas){
		if(null==fillRender)
		{
			fillRender=new FillRender();
		}
    	if(null==getRect())
    	{
    		return ;
    	}else{
        // TODO put your implementation here.	
    	strokWidth=paint.getStrokeWidth();
    	myShader=paint.getShader();
    	
    	//清空画笔
//		clearPaint(paint);
    
    	paint.reset();
        rectf.left=getRect().left;
        rectf.right=getRect().right;
        rectf.top=getRect().top;
        rectf.bottom=getRect().bottom;
       
    	//画填充()
    	initFill(paint);
//    	canvas.drawRect(getRect(), paint);
    	canvas.drawArc(rectf,-getStartAngle(),-getSpanAngle(), true, paint);
    	//画边框
    	 if(0!=getLineWidth())
     	{
     		paint.reset();
 	    	initStork(paint);
 	    	canvas.drawArc(rectf, -getStartAngle(), -getSpanAngle(), true, paint);
     	}
    	//将rectF置空
    	//rectf=null;
    
    	}
    	
    	
    }
    /**
	 * 设置填充的画笔属性
	 * @param paint
	 */
    private PathEffect effect;
	private void initFill(Paint paint)
	{
		
		paint.setAntiAlias(true);//去锯齿
		paint.setStrokeWidth(strokWidth);
		//设置填充的样式
		if(CSS_TYPE.CSS_SOLIDCOLOR==getStyle()){//如果是纯色
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if(getBackColor()==Color.TRANSPARENT)
			{
				paint.setAlpha(0);
			}else{
				paint.setColor(getBackColor());// 设置扇形的填充颜色
				paint.setAlpha(getAlpha());// 设置透明度
			}
		}
		else if(CSS_TYPE.CSS_TRANSPARENCE==getStyle()){//如果是透明样式
			  paint.setStyle(Paint.Style.STROKE);
		      paint.setColor(getLineColor());
		      paint.setStrokeWidth(getLineWidth());
		      paint.setAlpha(getAlpha());
		}
		else{
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			Shader shader=fillRender.circle_css(getStyle(), getRect().centerX(),getRect().centerY(), (getRect().width()-2*getLineWidth())/2, getForeColor(), getBackColor());
			paint.setShader(shader);
			paint.setAlpha(getAlpha());
		}
		if ( LINE_TYPE.NO_PEN==getLineType())// 不显示的线 即线的颜色为透明
		{
			paint.setStrokeWidth(strokWidth);
		} else {
			if(null == effect)
			{
				 effect = LineTypeUtil.getPathEffect(getLineType(),getLineWidth());
			}
			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
	
	}
	/**
	 * 设置边框画笔属性
	 * @param paint
	 */
	private void initStork(Paint paint)
	{
		
		paint.setAntiAlias(true);//去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor());
		paint.setAlpha(getAlpha());
		paint.setStrokeWidth(getLineWidth());
			if ( LINE_TYPE.NO_PEN==getLineType())// 不显示的线 即线的颜色为透明
			{
				paint.setColor(Color.TRANSPARENT);
			} else {
				if(null == effect)
				{
					effect = LineTypeUtil.getPathEffect(getLineType(),getLineWidth());
				}
				 
				if (null != effect) {
					paint.setPathEffect(effect);// 设置线的样式
				}
			}
			paint.setShader(myShader);
	}
	
}