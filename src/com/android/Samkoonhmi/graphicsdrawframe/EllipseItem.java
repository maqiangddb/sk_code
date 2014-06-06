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
import android.graphics.Shader;

//import SquareItem;

/**
 * 画圆
 * 
 * @author Administrator
 * 
 */
public class EllipseItem extends SquareItem {

	private final String CIRCLE_SHAPE = "1"; // 标识是圆还是椭圆 1 圆 2 椭圆
	private final String OVal_SHAPE = "2";
	private RectF rectf = null;
	private Shader myShader;
	private float strokWidth;
	private FillRender fillrender;

	public EllipseItem(Rect mRect) {
		super(mRect);
		// TODO Auto-generated constructor stub
		this.setRect(mRect);

	}

	public void draw(Paint paint, Canvas canvas) {
		rectf = new RectF();
		if (null == fillrender) {
			fillrender = new FillRender();
		}
		if (null == this.getRect()) {
			return;
		} else {
			// 清空画笔
			// clearPaint(paint);
			paint.reset();
			myShader = paint.getShader();
			strokWidth = paint.getStrokeWidth();
			// 判断是椭圆还是圆
			float width = this.getRect().width();
			float height = this.getRect().height();
			rectf.left = this.getRect().left;
			rectf.top = this.getRect().top;
			rectf.right = this.getRect().right;
			rectf.bottom = this.getRect().bottom;
			if (width != height) { // 说明不是圆
				// 初始化椭圆填充画笔
				initFill(paint, OVal_SHAPE);
				canvas.drawOval(rectf, paint);
				// 初始化椭圆的边框
				if (0 != getLineWidth() && getLineType() != LINE_TYPE.NO_PEN) {
					paint.reset();
					initStork(paint);
					float temp = getLineWidth() /2;
					if(getLineWidth() == 1)
					{
						temp = 1;
					}
						rectf.left = rectf.left +temp;
						rectf.top = rectf.top +temp;
						rectf.right = rectf.right - temp;
						rectf.bottom = rectf.bottom  - temp;
					canvas.drawOval(rectf, paint);
					
				}

				// 将rectf置空
				rectf = null;

			} else {
				float cx = this.getRect().centerX();
				float cy = this.getRect().centerY();
				int radius = this.getRect().width() / 2;

				// //画圆的填充
				initFill(paint, CIRCLE_SHAPE);

				if (0 != getLineWidth()) {// 带有边框
					if (getLineWidth() % 2 == 0) {
						canvas.drawCircle(cx, cy, radius - 1, paint);// 绘制填充圆
						paint.reset();
						initStork(paint);
						if(-1 != this.getLineAlpha()){//边框是否有独立的Alpha值
							paint.setAlpha(this.getLineAlpha());
						}
						canvas.drawCircle(cx, cy,
								(radius - getLineWidth() / 2), paint);// 绘制边框圆形

					} else {
						canvas.drawCircle(cx, cy, radius - 1, paint);// 绘制填充圆
						paint.reset();
						initStork(paint);
						if(-1 != this.getLineAlpha()){////边框是否有独立的Alpha值
							paint.setAlpha(this.getLineAlpha());
						}
						canvas.drawCircle(cx, cy,
								(radius - getLineWidth() / 2) - 1, paint);// 绘制边框圆形
					}// End of:if (getLineWidth() % 2 == 0)
				} else {// 没有带边框
					canvas.drawCircle(cx, cy, radius, paint);
				}// End of:if(0 !== getLineWidth)
			}
		}
	}

	/**
	 * 设置填充的画笔属性
	 * 
	 * @param paint
	 */
	private PathEffect effect;

	private void initFill(Paint paint, String shape) {

		paint.setAntiAlias(true);// 去锯齿
		paint.setStrokeWidth(strokWidth);
		// 设置填充的样式
		if (CSS_TYPE.CSS_SOLIDCOLOR == getStyle()) {// 如果是纯色
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (getBackColor() == Color.TRANSPARENT) {
				paint.setAlpha(0);
			} else {
				paint.setColor(getBackColor());// 设置圆的填充颜色
				paint.setAlpha(getAlpha());// 设置透明度
			}
		} else if (CSS_TYPE.CSS_TRANSPARENCE == getStyle()) {// 如果是透明样式
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(getLineColor());
			// paint.setStrokeWidth(getLineWidth());
			paint.setAlpha(getAlpha());
		} else {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			Shader shader = null;
			if (CIRCLE_SHAPE == shape) {
				shader = fillrender.circle_css(getStyle(), this.getRect()
						.centerX(), this.getRect().centerY(), this.getRect()
						.width() / 2, getForeColor(), getBackColor());
			} else {
				shader = fillrender.setRectCss(getStyle(), this.getRect().left,
						this.getRect().top, this.getRect().right,
						this.getRect().bottom, getForeColor(), getBackColor());
			}
			paint.setShader(shader);
			paint.setAlpha(getAlpha());
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
	 * 设置边框画笔属性
	 * 
	 * @param paint
	 */
	private void initStork(Paint paint) {

		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor());
		paint.setAlpha(getAlpha());
		paint.setStrokeWidth(getLineWidth());
		// Join join=EndPointTypeUtil.getJoin(getRectType());//设置边框的转角的形状
		// paint.setStrokeJoin(join);
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
		paint.setShader(myShader);
	}

}