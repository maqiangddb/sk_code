package com.android.Samkoonhmi.graphicsdrawframe;

import java.util.Vector;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.EndArrowTypeUtil;
import com.android.Samkoonhmi.util.EndPointTypeUtil;
import com.android.Samkoonhmi.util.LineTypeUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.PathEffect;
import android.graphics.Point;

/**
 * 线的画法
 * @author Administrator
 */
public class LineItem extends LinearDrawItem {
	private float[] pointsFloat;
	private boolean init;
	private EndArrowTypeUtil mTypeUtil;

	public LineItem(Vector<Point> pointList) {
		super(pointList);
		this.setM_pointList(pointList);
		init=true;
		mTypeUtil=new EndArrowTypeUtil();
	}

	public void draw(Paint paint, Canvas canvas) {
		if (null == this.getM_pointList()) {
			return;
		} else {
			if (2 > this.getM_pointList().size()) {
				return;
			} else {
				// 设置画笔的属性
				if (init) {
					pointsFloat = paseToFloatArray();
					init(paint);
					init=false;
				}
				// 转换点
				canvas.drawLines(pointsFloat, paint);
				// 画箭头
				mTypeUtil.drawArrow(paint, canvas, pointsFloat[0],
						pointsFloat[1], pointsFloat[pointsFloat.length - 2],
						pointsFloat[pointsFloat.length - 1], getEndArrowType());
			}
		}
	}

	/**
	 * 初始化画笔
	 */
	private PathEffect effect ;
	public void init(Paint paint) {
		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor()); // 线的颜色
		paint.setAlpha(getAlpha()); // 透明度
		paint.setStrokeWidth(getLineWidth()); // 线宽度
		if (getLineType() == LINE_TYPE.NO_PEN)// 不显示的线 即线的颜色为透明
		{
			paint.setColor(Color.TRANSPARENT);
		} else {
			if(null ==effect)
			{
			 effect = LineTypeUtil.getPathEffect(getLineType(),getLineWidth());
			}
			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
		Join join = EndPointTypeUtil.getJoin(getEndPointType());
		paint.setStrokeJoin(join);// 设置端点的形状
	}

	private float[] paseToFloatArray() {
		int size = this.getM_pointList().size() * 2;
		float[] pointsFloat = new float[size];
		int k = 0;
		for (int i = 0; i < this.getM_pointList().size(); i++) {
			pointsFloat[k] = this.getM_pointList().get(i).x;
			k++;
			pointsFloat[k] = this.getM_pointList().get(i).y;
			k++;
		}
		return pointsFloat;
	}

}