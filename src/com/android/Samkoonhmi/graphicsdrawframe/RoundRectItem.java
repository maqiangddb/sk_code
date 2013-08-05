package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.EndPointTypeUtil;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.LineTypeUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Join;

//import SquareItem;
/**
 * 画圆角矩形
 * 
 * @author Administrator
 * 
 */
public class RoundRectItem extends SquareItem {
	private float m_nRadiuX;
	private float m_nRadiuY;
	private FillRender fillRender;
	// private Rect m_rect;
	private RectF rectF;
	private float strokWidth;
	private Shader myShader;

	public RoundRectItem(Rect mRect) {
		super(mRect);
		// TODO put your implementation here.
		this.m_nRadiuX = 10;
		this.m_nRadiuY = 10;
		this.setRect(mRect);

	}

	public void setBendRadiuX(float nRadiuX) {
		// TODO put your implementation here.
		this.m_nRadiuX = nRadiuX;
	}

	public float getBendRadiuX() {
		return m_nRadiuX;
		// TODO put your implementation here.
	}

	public void setBendRadiuY(float nRadiuY) {
		// TODO put your implementation here.
		this.m_nRadiuY = nRadiuY;
	}

	public float getBendRadiuY() {
		return m_nRadiuY;
		// TODO put your implementation here.
	}

	public void draw(Paint paint, Canvas canvas) {
		if (null == fillRender) {
			fillRender = new FillRender();
		}
		rectF = new RectF();
		// TODO put your implementation here.
		if (null == getRect()) {
			return;
		} else {
			strokWidth = paint.getStrokeWidth();
			myShader = paint.getShader();
			// 清空画笔
			// clearPaint(paint);
			paint.reset();
			rectF.left = getRect().left;// new RectF(m_rect.left, m_rect.top,
										// m_rect.right, m_rect.bottom);
			rectF.right = getRect().right;
			rectF.top = getRect().top;
			rectF.bottom = getRect().bottom;
			int tmpLineWidth = getLineWidth();
			if (0 != getLineWidth()) {
				// 画填充
				initFill(paint);
				if (getLineWidth() % 2 != 0) {

					tmpLineWidth = tmpLineWidth + 1;
				}
				RectF tmpRect = new RectF(rectF.left + tmpLineWidth / 2,
						rectF.top + tmpLineWidth / 2, rectF.right
								- tmpLineWidth / 2, rectF.bottom - tmpLineWidth
								/ 2);
				if(getAlpha() > 0 && CSS_TYPE.CSS_TRANSPARENCE != getStyle())
				{
					canvas.drawRoundRect(tmpRect, getBendRadiuX(), getBendRadiuY(),
							paint);
				}
				paint.reset();
				// 画线宽
				initStrok(paint);
				if(getAlpha() >0)
				{
					canvas.drawRoundRect(tmpRect, getBendRadiuX(), getBendRadiuY(),
							paint);
				}
			} else {
				// 画填充
				initFill(paint);
				if(getAlpha()  > 0 && CSS_TYPE.CSS_TRANSPARENCE != getStyle())
				{
					canvas.drawRoundRect(rectF, getBendRadiuX(), getBendRadiuY(),
							paint);
				}
			}
			// 将rectF置空
			rectF = null;
		}
	}

	/**
	 * 初始化填充的画笔
	 * 
	 * @param paint
	 */
	private PathEffect effect;
	private void initFill(Paint paint) {
        if(getAlpha() == 0 || CSS_TYPE.CSS_TRANSPARENCE == getStyle())
        {
        	return;
        }
		paint.setAntiAlias(true);// 去锯齿
		paint.setStrokeWidth(strokWidth);
		// 设置填充的样式
		if (CSS_TYPE.CSS_SOLIDCOLOR == getStyle()) {// 如果是纯色
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (getBackColor() == Color.TRANSPARENT) {
				paint.setAlpha(0);
			} else {
				paint.setColor(getBackColor());// 设置圆角矩形的填充颜色
				paint.setAlpha(getAlpha());// 设置透明度
			}
		} else if (CSS_TYPE.CSS_TRANSPARENCE == getStyle()) {// 如果是透明样式
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(getLineColor());
			paint.setStrokeWidth(getLineWidth());
			paint.setAlpha(getAlpha());
		} else {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			Shader shader = fillRender.setRectCss(getStyle(), getRect().left,
					getRect().top, getRect().right, getRect().bottom,
					getForeColor(), getBackColor());
			paint.setShader(shader);
			paint.setAlpha(getAlpha());
		}
		if (LINE_TYPE.NO_PEN == getLineType())// 不显示的线 即线的颜色为透明
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
	 * 初始化边框的画笔
	 * 
	 * @param paint
	 */
	private void initStrok(Paint paint) {
	    if(getAlpha() == 0)
	    {
	    	return;
	    }
		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor());
		paint.setAlpha(getAlpha());
		paint.setStrokeWidth(getLineWidth());
		if (LINE_TYPE.NO_PEN == getLineType())// 不显示的线 即线的颜色为透明
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