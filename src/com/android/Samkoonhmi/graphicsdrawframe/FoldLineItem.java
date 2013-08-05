package com.android.Samkoonhmi.graphicsdrawframe;

//import LinearDrawItem;
import java.util.Vector;

import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.EndArrowTypeUtil;
import com.android.Samkoonhmi.util.EndPointTypeUtil;
import com.android.Samkoonhmi.util.LineTypeUtil;
//import Vector<Point >;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Paint.Join;

/**
 * 折线的画法
 * 
 * @author Administrator
 * 
 */
public class FoldLineItem extends LinearDrawItem {
	// private Vector<Point> m_pointList;
	private Path path;
	private EndArrowTypeUtil mTypeUtil;

	public FoldLineItem(Vector<Point> pointList) {
		super(pointList);
		// TODO put your implementation here.
		this.setM_pointList(pointList);
		mTypeUtil = new EndArrowTypeUtil();
	}

	public void draw(Paint paint, Canvas canvas) {
		path = new Path();
		// TODO put your implementation here.
		if (null == this.getM_pointList()) {
			return;
		} else {
			if (this.getM_pointList().size() < 2) {
				return;

			} else {
				// 清空画笔
				// clearPaint(paint);
				paint.reset();
				// 画折线
				init(paint);
				path.moveTo(this.getM_pointList().get(0).x, this
						.getM_pointList().get(0).y);
				for (int i = 1; i < this.getM_pointList().size(); i++) {
					path.lineTo(this.getM_pointList().get(i).x, this
							.getM_pointList().get(i).y);
				}
				canvas.drawPath(path, paint);
				// 画箭头
				int pointSize = this.getM_pointList().size();
				// 最后一个端点的横坐标
				float lastX = this.getM_pointList().get(pointSize - 1).x;
				// 最后一个端点的纵坐标
				float lastY = this.getM_pointList().get(pointSize - 1).y;
				// 倒数第二个端点的横坐标
				float lastSecondX = this.getM_pointList().get(pointSize - 2).x;
				// 倒数第二个端点的纵坐标
				float lastSecondY = this.getM_pointList().get(pointSize - 2).y;
				mTypeUtil.drawArrow(paint, canvas, lastSecondX, lastSecondY,
						lastX, lastY, getEndArrowType());
				// 将path清空
				path = null;
			}
		}
	}

	/**
	 * 初始化画笔
	 */
	private PathEffect effect;

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
			if (null == effect) {
				effect = LineTypeUtil.getPathEffect(getLineType(),
						getLineWidth());
			}
			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
		Join join = EndPointTypeUtil.getJoin(getEndPointType());
		paint.setStrokeJoin(join);// 设置端点的形状
	}

}