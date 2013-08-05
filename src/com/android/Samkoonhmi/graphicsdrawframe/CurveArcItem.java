package com.android.Samkoonhmi.graphicsdrawframe;

import java.util.Vector;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.POINT_TYPE;
import com.android.Samkoonhmi.util.EndArrowTypeUtil;
import com.android.Samkoonhmi.util.LineTypeUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;

/**
 * 画曲线圆弧
 * 
 * @author Administrator
 * 
 */
public class CurveArcItem extends LinearDrawItem {
	// private Vector<Point> pointList;
	// private Vector<POINT_TYPE> pointTypeList;
	private Path path;
	private EndArrowTypeUtil mTypeUtil;

	public CurveArcItem(Vector<Point> pointList,
			Vector<POINT_TYPE> pointTypeList) {
		super(pointList);
		// TODO put your implementation here.
		this.setM_pointList(pointList);
		this.setPointTypeList(pointTypeList);
		mTypeUtil=new EndArrowTypeUtil();

	}

	public void draw(Paint paint, Canvas canvas) {
		// TODO put your implementation here.
		path = new Path();
		if (null == this.getPointTypeList() || null == this.getM_pointList()) {
			return;
		} else {
			if (this.getM_pointList().size() < 2
					|| this.getPointTypeList().size() < 2) {
				return;
			} else {
				// 清空画笔
				// clearPaint(paint);
				paint.reset();
				paint=init(paint);
				float startX = this.getM_pointList().get(0).x;
				float startY = this.getM_pointList().get(0).y;
				float stopX = this.getM_pointList().get(
						this.getM_pointList().size() - 1).x;
				float stopY = this.getM_pointList().get(
						this.getM_pointList().size() - 1).y;
				path.moveTo(startX, startY);
				for (int i = 1; i < this.getM_pointList().size() - 1; i++) {

					if (POINT_TYPE.GENERALPOINT == this.getPointTypeList().get(
							i)) {// 普通点 直接连接
						path.lineTo(this.getM_pointList().get(i).x, this
								.getM_pointList().get(i).y);
					} else { // 控制点
						float p1x = this.getM_pointList().get(i).x;
						float p1y = this.getM_pointList().get(i).y;
						float p2x = this.getM_pointList().get(i + 1).x;
						float p2y = this.getM_pointList().get(i + 1).y;
						path.quadTo(p1x, p1y, p2x, p2y);
					}
				}
				path.lineTo(stopX, stopY);

				canvas.drawPath(path, paint);

				mTypeUtil.drawArrow(
						paint,
						canvas,
						this.getM_pointList().get(
								this.getM_pointList().size() - 2).x,
						this.getM_pointList().get(
								this.getM_pointList().size() - 2).y, stopX,
						stopY, getEndArrowType());
				// 将path置空
				path = null;
			}
		}
	}

	/**
	 * 初始化画笔
	 */
	private PathEffect effect;
	public Paint init(Paint paint) {
		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor()); // 线的颜色
		paint.setAlpha(getAlpha()); // 透明度
		paint.setStrokeWidth(getLineWidth()); // 线宽度
		if (getLineType() == LINE_TYPE.NO_PEN)// 不显示的线 即线的颜色为透明
		{
			paint.setColor(Color.TRANSPARENT);
		} else {
			if (effect==null) {
				effect = LineTypeUtil.getPathEffect(getLineType(),getLineWidth());
			}
			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
		return paint;
	}

}