package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.EndPointTypeUtil;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.LineTypeUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Paint.Join;
import android.util.Log;

/**
 * 矩形的画法
 */
public class RectItem extends SquareItem {

	private FillRender fillRender;
	private float strokWidth;
	private boolean init = false;
	private Paint mPaint;
	private Paint mLinePaint;
	private int nAlpha=255;
	private int nLineAlpha=255;

	public RectItem(Rect mRect) {
		super(mRect);
		this.setRect(mRect);
		init = false;
		mPaint = new Paint();
		mLinePaint = new Paint();
		fillRender = new FillRender();
	}
	
	public void init(){
		if (!init) {
			// 初始化
			init = true;
			if (nAlpha>0&&getStyle()!=CSS_TYPE.CSS_TRANSPARENCE) {
				initFill(mPaint);
			}
			
			if (nLineAlpha>0&&getLineWidth()>0) {
				initStrok(mLinePaint);
			}
		}
	}

	public void setRectType(END_POINT_TYPE mRectType) {
		super.setType(mRectType);
	}

	public END_POINT_TYPE getRectType() {
		return super.getType();
	}

	public void draw(Paint paint,Canvas canvas) {
		if (null == fillRender) {
			fillRender = new FillRender();
		}
		if (null == this.getRect()) {
			return;
		} else {
			strokWidth = mPaint.getStrokeWidth();
			if (!init) {
				// 未初始化
				init = true;
				if (nAlpha>0&&getStyle()!=CSS_TYPE.CSS_TRANSPARENCE) {
					initFill(mPaint);
				}
				
				if (nLineAlpha>0&&getLineWidth()>0) {
					initStrok(mLinePaint);
				}
			}

			int tmpLineWidth = getLineWidth();
			// 画线宽
			if (tmpLineWidth>0&&nLineAlpha>0) {
				if (getLineWidth() % 2 == 0) {
					
					Rect tmpRect = new Rect(getRect().left + tmpLineWidth / 2,
							getRect().top + tmpLineWidth / 2, getRect().right
									- tmpLineWidth / 2, getRect().bottom
									- tmpLineWidth / 2);
					
					if (nAlpha>0&&getStyle()!=CSS_TYPE.CSS_TRANSPARENCE) {
						canvas.drawRect(tmpRect, mPaint);
					}

					if (-1 != this.getLineAlpha()) {// 边框是否有独立的Alpha值
						mLinePaint.setAlpha(this.getLineAlpha());
					}
					
					canvas.drawRect(tmpRect, mLinePaint);
				} else {
                   
					Rect tmpRect=null;
					if (getLineWidth()>1) {
						tmpLineWidth = tmpLineWidth + 1;
						tmpRect = new Rect(getRect().left + tmpLineWidth / 2,
								getRect().top + tmpLineWidth / 2, getRect().right
										- tmpLineWidth / 2, getRect().bottom
										- tmpLineWidth / 2);
					}else {
						tmpRect = new Rect(getRect());
					}
					
					if (nAlpha>0&&getStyle()!=CSS_TYPE.CSS_TRANSPARENCE) {
						canvas.drawRect(tmpRect, mPaint);
					}
					
					if (-1 != this.getLineAlpha()) {// 边框是否有独立的Alpha值
						mLinePaint.setAlpha(this.getLineAlpha());
					}
					canvas.drawRect(tmpRect, mLinePaint);
				}// End of:if (getLineWidth() % 2 == 0)

			} else {
				if (nAlpha>0&&getStyle()!=CSS_TYPE.CSS_TRANSPARENCE) {
					canvas.drawRect(this.getRect(), mPaint);
				}
			}// End of:if( 0!= tmpLineWidth)
		}

	}

	/**
	 * 初始化填充的画笔
	 * 
	 * @param paint
	 */
	private PathEffect effect;
	private void initFill(Paint paint) {
		paint.reset();
		paint.setAntiAlias(true);// 去锯齿
		paint.setStrokeWidth(strokWidth);
		// 设置填充的样式
		if (CSS_TYPE.CSS_SOLIDCOLOR == getStyle()) {// 如果是纯色
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (getBackColor() == Color.TRANSPARENT) {
				paint.setAlpha(0);
			} else {
				paint.setColor(getBackColor());// 设矩形的填充颜色
				paint.setAlpha(nAlpha);// 设置透明度
			}
		} else if (CSS_TYPE.CSS_TRANSPARENCE == getStyle()) {// 如果是透明样式
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(getLineColor());
			paint.setStrokeWidth(getLineWidth());
			paint.setAlpha(nAlpha);
		} else {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			Shader shader = fillRender.setRectCss(getStyle(),
					this.getRect().left, this.getRect().top,
					this.getRect().right, this.getRect().bottom,
					getForeColor(), getBackColor());
			paint.setShader(shader);
			paint.setAlpha(nAlpha);
		}
		if (LINE_TYPE.NO_PEN == getLineType())// 不显示的线 即线的颜色为透明
		{
			paint.setStrokeWidth(strokWidth);
		} else {
			if (null == effect) {
				effect = LineTypeUtil.getPathEffect(getLineType(),
						getLineWidth());
			}
			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
	}

	/**
	 * 初始化边框的画笔
	 * @param paint
	 */
	private void initStrok(Paint paint) {
		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor());
		paint.setAlpha(nLineAlpha);
		paint.setStrokeWidth(getLineWidth());
		Join join = EndPointTypeUtil.getJoin(getRectType());// 设置边框的转角的形状
		paint.setStrokeJoin(join);
		if (LINE_TYPE.NO_PEN == getLineType())// 不显示的线 即线的颜色为透明
		{
			paint.setColor(Color.TRANSPARENT);
		} else {
			if (null == effect) {
				effect = LineTypeUtil.getPathEffect(getLineType(),
						getLineWidth());
			}

			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
		/**
		 * 添加会使变成，颜色改变
		 */
		// paint.setShader(myShader);
	}

	
	@Override
	public void setAlpha(int nAlpha) {
		// TODO Auto-generated method stub
		super.setAlpha(nAlpha);
		if(mPaint==null){
			return;
		}
		this.nAlpha=nAlpha;
		mPaint.setAlpha(nAlpha);
	}
	
	@Override
	public void setLineAlpha(int a) {
		// TODO Auto-generated method stub
		super.setLineAlpha(a);
		if (mLinePaint==null) {
			return;
		}
		this.nLineAlpha=a;
		mLinePaint.setAlpha(nLineAlpha);
	}

	@Override
	public void setForeColor(int nForeColor) {
		// TODO Auto-generated method stub
		super.setForeColor(nForeColor);
	    init = false;
	}

	
	@Override
	public void setBackColor(int nBackColor) {
		// TODO Auto-generated method stub
		super.setBackColor(nBackColor);
		init = false;
	}

	@Override
	public void setStyle(CSS_TYPE nStyle) {
		super.setStyle(nStyle);
		init = false;
	}

	public void resetRect(){
		init = false;
	}
}