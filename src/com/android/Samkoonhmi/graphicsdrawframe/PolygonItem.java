package com.android.Samkoonhmi.graphicsdrawframe;

//import PolygonDrawItem;
/**
 * 多边形的画法
 * @author Administrator
 *
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.LineTypeUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;

/**
 * 多边形
 * 
 * @author Administrator
 * 
 */
public class PolygonItem extends PolygonDrawItem {
	private Vector<Point> m_pointList;

	private Path pathStrok;
	private Path pathFill;
	private FillRender fillRender;
	private float strokWidth; // 边框的默认值
	private Shader myShader; // 颜色渲染的默认值
	private Map<String, String> map;

	public Vector<Point> getM_pointList() {
		return m_pointList;
	}

	public void setM_pointList(Vector<Point> m_pointList) {
		this.m_pointList = m_pointList;
	}

	public PolygonItem(Vector<Point> pointList) {
		// TODO put your implementation here.
		this.setM_pointList(pointList);
	}

	public void draw(Paint paint, Canvas canvas) {
		// TODO put your implementation here.
		pathStrok = new Path();
		pathFill = new Path();
		if (null == fillRender) {
			fillRender = new FillRender();
		}
		map = new HashMap<String, String>();
		if (null == getM_pointList()) {
			return;
		} else {
			if (2 > getM_pointList().size()) {
				return;
			} else {
				strokWidth = paint.getStrokeWidth();
				myShader = paint.getShader();
				// 清空画笔
				// clearPaint(paint);
				paint.reset();
				// 画填充
				initFill(paint);
				pathFill.moveTo(getM_pointList().get(0).x, getM_pointList()
						.get(0).y);
				for (int i = 1; i < getM_pointList().size(); i++) {
					pathFill.lineTo(getM_pointList().get(i).x, getM_pointList()
							.get(i).y);
				}
				pathFill.close();
				canvas.drawPath(pathFill, paint);
				// 画边框
				if (0 != getLineWidth()) {
					paint.reset();
					iniStrok(paint);
					pathStrok.moveTo(getM_pointList().get(0).x,
							getM_pointList().get(0).y);
					for (int i = 1; i < getM_pointList().size(); i++) {
						pathStrok.lineTo(getM_pointList().get(i).x,
								getM_pointList().get(i).y);
					}
					pathStrok.close();
					canvas.drawPath(pathStrok, paint);
				}
				pathFill = null;
				pathStrok = null;
			}
		}

	}

	/**
	 * 初始化填充的画笔
	 */
	private PathEffect effect;

	private void initFill(Paint paint) {
		paint.setAntiAlias(true);// 去锯齿
		paint.setStrokeWidth(strokWidth);
		// 设置填充的样式
		if (CSS_TYPE.CSS_SOLIDCOLOR == getStyle()) {// 如果是纯色
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (getBackColor() == Color.TRANSPARENT) {
				paint.setAlpha(0);
			} else {
				paint.setColor(getBackColor());// 设置多边形的填充颜色
				paint.setAlpha(getAlpha());// 设置透明度
			}
		} else if (CSS_TYPE.CSS_TRANSPARENCE == getStyle()) {// 如果是透明样式
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(getLineColor());
			paint.setStrokeWidth(getLineWidth());
			paint.setAlpha(getAlpha());
		} else {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			Map<String, String> map = getEndPoint();
			String left = map.get("left");
			String right = map.get("right");
			String top = map.get("top");
			String bottom = map.get("bottom");
			Shader shader = fillRender.setRectCss(getStyle(),
					Float.parseFloat(left), Float.parseFloat(top),
					Float.parseFloat(right), Float.parseFloat(bottom),
					getForeColor(), getBackColor());
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
	 * 初始化边框的画笔
	 */
	private void iniStrok(Paint paint) {
		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor());
		paint.setAlpha(getAlpha());
		paint.setStrokeWidth(getLineWidth());
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

	/**
	 * 得到四个端点的坐标
	 * 
	 * @return
	 */
	private Map<String, String> getEndPoint() {

		float[] listX = new float[getM_pointList().size()];
		float[] listY = new float[getM_pointList().size()];
		for (int i = 0; i < getM_pointList().size(); i++) {
			float x = getM_pointList().get(i).x;
			float y = getM_pointList().get(i).y;
			listX[i] = x;
			listY[i] = y;
		}
		listX = bubbleSort2(listX);
		listY = bubbleSort2(listY);
		float left = listX[listX.length - 1];
		float right = listX[0];
		float top = listY[listY.length - 1];
		float bottom = listY[0];
		map.put("left", left + "");
		map.put("right", right + "");
		map.put("top", top + "");
		map.put("bottom", bottom + "");
		listX = null;
		listY = null;
		return map;

	}

	/**
	 * 将数组降序排列
	 * 
	 * @param m
	 * @return
	 */
	public static float[] bubbleSort2(float[] m) {

		int intLenth = m.length;
		/* 执行intLenth次 */
		for (int i = 0; i < intLenth; i++) {
			/* 每执行一次，将最小的数排在后面 */
			for (int j = 0; j < intLenth - i - 1; j++) {
				float a = m[j];
				float b = m[j + 1];
				if (a < b) {
					m[j] = b;
					m[j + 1] = a;
				}
			}
		}
		return m;
	}

}