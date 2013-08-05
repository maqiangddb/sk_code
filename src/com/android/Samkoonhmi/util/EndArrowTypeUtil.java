package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

/**
 * 箭头类型工具类
 * 
 * @author Administrator
 * 
 */
public class EndArrowTypeUtil {

	/**
	 * @param canvas 画布
	 * @param paint画笔
	 * @param point端点坐标
	 * @param type箭头类型
	 */
	private Path path;
	private boolean init;
	private float firstPointX;
	private float firstPointY;
	private float secondPointX;
	private float secondPointY;
	private float tempCrossX;// 算出的两点与已知线的交点的横坐标
	private float tempCrossY;// 算出的两点与已知线的交点的纵坐标
	private float crossX; // 燕子型的交点横坐标
	private float crossY;// 燕子型的交点纵坐标

	public void drawArrow(Paint paint, Canvas canvas, float p1x, float p1y,
			float p2x, float p2y, END_ARROW_TYPE type) {
		// 没有箭头 直接返回
		if (type == END_ARROW_TYPE.STYLE_NONE) {
			return;
		}
		if (null == path) {
			path = new Path();
			init = true;
		} else {
			init = false;
		}

		if (init) {
			int direct = 0; // 表示原有线段的方向,1表示指向正X,-1表示指向负X
			double slope = 0; // 取原有线段的相对于x轴正方向的夹角
			int LEN = 8; // 定义箭头线段大小
			if (p1x == p2x) {// 对于垂直于y轴直线
				direct = p2y > p1y ? 1 : -1; // 箭头的方向，与x轴相同
				slope = Math.PI / 2;
			} else {
				direct = p2x > p1x ? 1 : -1; // 箭头的方向，与y轴相同
				slope = Math.atan((p2y - p1y) / (p2x - p1x)); // 利用斜率求夹角
			}
			double angle1 = slope - Math.PI / 6; // 计算箭头的偏转角度，设为30
			double angle2 = slope + Math.PI / 6;
			// 计算箭头在x，y轴的偏转量
			double dx1 = direct * LEN * Math.cos(angle1); // 计算箭头在x，y轴的偏转量
			double dy1 = direct * LEN * Math.sin(angle1);
			// 计算箭头在x，y轴的偏转量
			double dx2 = direct * LEN * Math.cos(angle2);
			double dy2 = direct * LEN * Math.sin(angle2);

			firstPointX = (float) (p2x - dx1);
			firstPointY = (float) (p2y - dy1);
			secondPointX = (float) (p2x - dx2);
			secondPointY = (float) (p2y - dy2);

			tempCrossX = (firstPointX + secondPointX) / 2;// 算出的两点与已知线的交点的横坐标
			tempCrossY = (firstPointY + secondPointY) / 2;// 算出的两点与已知线的交点的纵坐标
			crossX = (tempCrossX + p2x) / 2; // 燕子型的交点横坐标
			crossY = (tempCrossY + p2y) / 2;// 燕子型的交点纵坐标
		}

		switch (type) {
		case FILLED_TRIANGLE: // 实心三角形
			if (init) {
				path.moveTo(p2x, p2y);
				path.lineTo(firstPointX, firstPointY);
				path.lineTo(secondPointX, secondPointY);
				path.lineTo(p2x, p2y);
				path.close();
			}
			paint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawPath(path, paint);
			break;
		case FOLD_LINE_TRIANGLE:// 大于号箭头
			paint.setStyle(Style.STROKE);
			// 作组成箭头线段 1
			canvas.drawLine((float) p2x, (float) p2y, firstPointX, firstPointY,
					paint);
			// 作组成箭头线段 2
			canvas.drawLine((float) p2x, (float) p2y, secondPointX,
					secondPointY, paint);
			break;
		case FILLED_SCISSORS: // 3 燕子尾巴形状
			paint.setStyle(Style.FILL_AND_STROKE);
			if (init) {
				path.moveTo(p2x, p2y);
				path.lineTo(firstPointX, firstPointY);
				path.lineTo(crossX, crossY);
				path.lineTo(secondPointX, secondPointY);
				path.lineTo(p2x, p2y);
				path.close();
			}
			canvas.drawPath(path, paint);
			break;
		case FILLED_RECT: // 4 菱形
			paint.setStyle(Style.FILL_AND_STROKE);
			float tempCrossX3 = (2 * tempCrossX) - p2x;
			float tempCrossY3 = (2 * tempCrossY) - p2y;
			if (init) {
				path.moveTo(p2x, p2y);
				path.lineTo(firstPointX, firstPointY);
				path.lineTo(tempCrossX3, tempCrossY3);
				path.lineTo(secondPointX, secondPointY);
				path.lineTo(p2x, p2y);
				path.close();
			}
			canvas.drawPath(path, paint);
			break;
		case FILLED_ELLIPSE: // 实心圆
			paint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawCircle(p2x, p2y, 7, paint);
			break;

		default:
			break;
		}
	}

}
