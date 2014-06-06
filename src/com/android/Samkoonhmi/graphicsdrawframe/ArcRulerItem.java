package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.GLOBAL_POS_PROP;
import com.android.Samkoonhmi.util.FillRender;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Xfermode;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Join;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * 可带刻度的扇形 Canvas 必须带Bitmap
 * 
 * @author 刘伟江
 * @version v 1.0.0.1 创建时间：2012-4-26 最后修改时间 2012-5-3
 */
public class ArcRulerItem extends PolygonDrawItem {

	// 主次标尺长度比例
	private static final float ratio = 0.6f;
	// 起始点角度
	private int startAngle;
	// 转动角度
	private int sweepAngle;
	// 扇形外框所在矩形
	private RectF mRectF;
	// 扇形背景所在矩形
	private RectF mBgRectF;
	// 是否有圆孔
	private boolean bHole;
	// 圆孔半径
	private int nHoleR;
	// 扇形类型，圆or半圆or四分之一圆
	private ArcType eArcType;
	// 是否显示次标尺
	private boolean bShowMinorRuling;
	// 主标尺长度
	private int nMainRulingLength;
	// 次标尺长度
	private int nMinorRulingLength;
	// 主标尺刻度
	private int nMainRuling;
	// 次标尺刻度
	private int nMinorRuling;
	// 是否显示刻度
	private boolean bRuler;
	// 扇形圆心所在
	private float nCenterX;
	private float nCenterY;
	// 圆的半径
	private int nR;
	// 是否以中心点转动
	private boolean useCenter;
	// 扇形所处的位置
	private GLOBAL_POS_PROP eArcOrientation;
	private RectF mMinorRectF;
	private int sweep = 360;
	// 显示颜色
	private int nDataColor;
	// 透明度
	private short nAlphas;
	//刻度的起始角度
	private int nOffsetAngle =-1;
	//是否显示边框
	private boolean bShowFrame = true;
	private int nRulerColor;
	private FillRender fillRender;
	private Shader myShader;
	private double nMax;
	private double nMin;
	private boolean bShowRuleValue;
	private DisplayMetrics dis;
	private float nSize = 8;
	private float nValueWidth;
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private float nRotate;
	public boolean clockwise;
	private Paint mBitmapPaint;//图片专用

	public ArcRulerItem(RectF rectF) {
		this.bShowMinorRuling = false;
		this.nMainRuling = 3;
		this.nMinorRuling = 2;
		this.bHole = false;
		this.bRuler = true;
		this.nMainRulingLength = 0;
		this.mRectF = rectF;// 扇形所在矩形
		if (mRectF != null) {
			mBgRectF = new RectF(mRectF.left + getLineWidth(), mRectF.top
					+ getLineWidth(), mRectF.right - getLineWidth(),
					mRectF.bottom - getLineWidth());
		}
		this.nCenterX = 0;
		this.nCenterY = 0;
		this.nR = 0;
		this.useCenter = false;
		this.eArcOrientation = GLOBAL_POS_PROP.LEFT_POS;
		mMinorRectF = new RectF();
		fillRender = new FillRender();
		dis = new DisplayMetrics();
		dis.density = (float) 1.3125;
		dis.densityDpi = 210;
		dis.scaledDensity = (float) 1.3125;
		dis.xdpi = (float) 225.77777;
		dis.ydpi = (float) 225.77777;
		nSize = TypedValue.applyDimension(2, nSize, dis);
		
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
	}

	private float mMainRotate;
	private int degrees;
	private int nWidth;

	@Override
	public void draw(Paint paint, Canvas canvas) {
		super.draw(paint, canvas);

		// 画扇形
		canvas.save();
		paint.reset();
		paint.setTextSize(nSize);
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeWidth(getLineWidth());

		// 画外框
		if (bShowFrame) {
			paint.setStyle(Style.STROKE);
			paint.setColor(getLineColor());
			canvas.drawArc(mRectF, startAngle, sweepAngle, false, paint);
		}

		// 画背景
		paint.setStyle(Style.FILL);
		paint.setColor(getBackColor());
		paint.setAlpha(nAlphas);
		canvas.drawArc(mBgRectF, startAngle, sweepAngle, useCenter, paint);

		if (eArcType == null) {
			eArcType = ArcType.ROUND;
		}

		switch (eArcType) {
		case ROUND:
			sweep = 360;
			break;
		case HALF:
			sweep = 180;
			break;
		case QUARTER:
			sweep = 90;
			break;
		case THREE:
			sweep = 270;
			break;
		}

		// 是否 有孔
		if (bHole) {
			float padding = mBgRectF.width()/2 - nHoleR;
			mMinorRectF.set(mRectF.left + padding, mRectF.top + padding,
					mRectF.right - padding, mRectF.bottom - padding);
			paint.setColor(Color.TRANSPARENT);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			canvas.drawArc(mMinorRectF, startAngle, sweep, false, paint);
			paint.setXfermode(null);
		}

		paint.setAlpha(255);

		// 画刻度
		if (bRuler) {
			if (bShowRuleValue) {
				nWidth = (int) mRectF.width();
				float rotate = Math.abs(sweepAngle);
				canvas.translate(nCenterX, nCenterY);
				
				mMainRotate = rotate / nMainRuling;
				nRotate = 0;
				paint.setAntiAlias(true);
				paint.setColor(nRulerColor);
				paint.setFilterBitmap(true);
				paint.setStyle(Style.FILL);
				paint.setStrokeJoin(Join.ROUND);
				canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
						Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

				switch (eArcType) {
				case ROUND:// 圆
					drawCircle(canvas, paint);
					break;
				case HALF:// 半圆
					drawHalfCircle(canvas, paint);
					break;
				case QUARTER:// 四分之一圆
					drawQuarterCircle(canvas, paint);
					break;
				case THREE:// 四分之三
					drawThreeCircle(canvas, paint);
					break;
				}
			}
			
		}
		canvas.restore();
	}

	/**
	 * 画圆
	 */
	private void drawCircle(Canvas canvas, Paint paint) {
		if (nOffsetAngle != -1) {
			canvas.rotate(180 +nOffsetAngle);
			degrees = 180 + nOffsetAngle ;
		}else {
			
			switch (eArcOrientation) {
			case LEFT_POS:
				canvas.rotate(90);
				degrees = 90;
				break;
			case MID_TOP:
				canvas.rotate(180);
				degrees = 180;
				break;
			case RIGHT_POS:
				canvas.rotate(270);
				degrees = 270;
				break;
			case MID_BELOW:
				degrees = 0;
				break;
			}
		}
		
		

		nRotate = 0;
		boolean draw;
		int nRrect = (int)mRectF.width() /2;
		for (int i = 0; i <= nMainRuling; i++) {

			// 刻度
			canvas.drawLine(0, nRrect, 0, nRrect + nMainRulingLength, paint);

			// 数值
			draw = true;
			if (mBitmap == null) {
				if(nValueWidth<1){
					nValueWidth=1;
				}
				mBitmap = Bitmap.createBitmap((int) nValueWidth,
						(int) nValueWidth, Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			// mCanvas.drawColor(Color.WHITE);
			String text = "";
			if (clockwise) {
				if (i == nMainRuling && sweepAngle == 360) {
					draw = false;
				}
				text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
			} else {
				if (i == 0 && sweepAngle == 360) {
					draw = false;
				}
				text = getRulerText(nMainRuling - i, nMin, nMax,
						nMainRuling + 1) + "";
			}
			if (draw) {
				Paint  fontPaint = new Paint();
				fontPaint.setColor(nRulerColor);
				float left = (nValueWidth - getFontWidth(text, fontPaint)) / 2;
				float top = (nValueWidth + 8) / 2;
				mCanvas.drawText(text, left, top, fontPaint);
				Bitmap temp = null;
				Matrix matrix = new Matrix();
				matrix.setTranslate(nValueWidth / 2, nValueWidth / 2);
				matrix.postRotate(-degrees - nRotate);
				temp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
 						mBitmap.getHeight(), matrix, true);

				mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				mCanvas.drawBitmap(temp, (nValueWidth - temp.getWidth()) / 2,
						(nValueWidth - temp.getHeight()) / 2, mBitmapPaint);

				left = -nValueWidth / 2;
				top = nRrect + nMainRulingLength;
				canvas.drawBitmap(mBitmap, left, top, mBitmapPaint);
			}
			nRotate += mMainRotate;

			if (bShowMinorRuling) {
 				for (int j = 0; j < nMinorRuling; j++) {
					canvas.rotate(mMainRotate / nMinorRuling);
					if (j < nMinorRuling - 1) {
						canvas.drawLine(0, nRrect, 0, nRrect + nMainRulingLength
								* ratio, paint);
					}
				}
			} else {
				canvas.rotate(mMainRotate);
			}
		}
	}

	/**
	 * 画半圆
	 */
	private void drawHalfCircle(Canvas canvas, Paint paint) {
		switch (eArcOrientation) {
		case LEFT_POS:
			break;
		case MID_TOP:
			canvas.rotate(90);
			degrees = 90;
			break;
		case RIGHT_POS:
			canvas.rotate(180);
			degrees = 180;
			break;
		case MID_BELOW:
			canvas.rotate(-90);
			degrees = -90;
			break;
		}
		int nRrect = (int)mRectF.width() /2;
		for (int i = 0; i <= nMainRuling; i++) {

			// 数值
			if (mBitmap == null) {
				if(nValueWidth<1){
					nValueWidth=1;
				}
				mBitmap = Bitmap.createBitmap((int) nValueWidth,
						(int) nValueWidth, Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			// mCanvas.drawColor(Color.WHITE);
			String text = "";
			if (clockwise) {
				text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
			} else {
				text = getRulerText(nMainRuling - i, nMin, nMax,
						nMainRuling + 1) + "";
			}
			float left = (nValueWidth - getFontWidth(text, paint)) / 2;
			float top = (nValueWidth + 8) / 2;
			mCanvas.drawText(text, left, top, paint);
			Bitmap temp = null;
			Matrix matrix = new Matrix();
			matrix.setTranslate(nValueWidth / 2, nValueWidth / 2);
			matrix.postRotate(-degrees - nRotate);
			temp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
					mBitmap.getHeight(), matrix, true);

			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			mCanvas.drawBitmap(temp, (nValueWidth - temp.getWidth()) / 2,
					(nValueWidth - temp.getHeight()) / 2, mBitmapPaint);

			left = -nValueWidth / 2;
			top = nRrect + nMainRulingLength;
			canvas.drawBitmap(mBitmap, left, top, mBitmapPaint);
			nRotate += mMainRotate;

			canvas.drawLine(0, nWidth / 2, 0, nWidth / 2 + nMainRulingLength,
					paint);
			if (bShowMinorRuling && i != nMainRuling) {
				for (int j = 0; j < nMinorRuling; j++) {
					canvas.rotate(mMainRotate / nMinorRuling);
					if (j < nMinorRuling - 1) {
						canvas.drawLine(0, nWidth / 2, 0, nWidth / 2
								+ nMainRulingLength * ratio, paint);
					}
				}
			} else {
				canvas.rotate(mMainRotate);
			}
		}
	}

	/**
	 * 四分之三
	 */
	private void drawThreeCircle(Canvas canvas, Paint paint) {
		switch (eArcOrientation) {
		case LEFT_POS: // 向左
			canvas.rotate(135);
			degrees = 135;
			break;
		case RIGHT_POS: // 向右
			canvas.rotate(315);
			degrees = 315;
			break;
		case MID_TOP: // 中上
			canvas.rotate(225);
			degrees = 225;
			break;
		case MID_BELOW: // 中下
			canvas.rotate(45);
			degrees = 45;
			break;
		}
		
		int nRrect = (int)mRectF.width() /2;
		for (int i = 0; i <= nMainRuling; i++) {

			// 数值
			if (mBitmap == null) {
				if(nValueWidth<1){
					nValueWidth=1;
				}
				mBitmap = Bitmap.createBitmap((int) nValueWidth,
						(int) nValueWidth, Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			// mCanvas.drawColor(Color.WHITE);
			String text = "";
			if (clockwise) {
				text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
			} else {
				text = getRulerText(nMainRuling - i, nMin, nMax,
						nMainRuling + 1) + "";
			}
			float left = (nValueWidth - getFontWidth(text, paint)) / 2;
			float top = (nValueWidth + 8) / 2;
			mCanvas.drawText(text, left, top, paint);
			Bitmap temp = null;
			Matrix matrix = new Matrix();
			matrix.setTranslate(nValueWidth / 2, nValueWidth / 2);
			matrix.postRotate(-degrees - nRotate);
			temp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
					mBitmap.getHeight(), matrix, true);

			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			mCanvas.drawBitmap(temp, (nValueWidth - temp.getWidth()) / 2,
					(nValueWidth - temp.getHeight()) / 2, mBitmapPaint);

			left = -nValueWidth / 2;
			top = nRrect + nMainRulingLength;
			canvas.drawBitmap(mBitmap, left, top, mBitmapPaint);
			nRotate += mMainRotate;

			canvas.drawLine(0, nWidth / 2, 0, nWidth / 2 + nMainRulingLength,
					paint);
			if (bShowMinorRuling && i != nMainRuling) {
				for (int j = 0; j < nMinorRuling; j++) {
					canvas.rotate(mMainRotate / nMinorRuling);
					if (j < nMinorRuling - 1) {
						canvas.drawLine(0, nWidth / 2, 0, nWidth / 2
								+ nMainRulingLength * ratio, paint);
					}
				}
			} else {
				canvas.rotate(mMainRotate);
			}
		}
	}

	/**
	 * 四分之一
	 */
	private boolean one;
	private void drawQuarterCircle(Canvas canvas, Paint paint) {
		switch (eArcOrientation) {
		case LEFT_POS: // 左
			one = true;
			canvas.rotate(45);
			degrees = 45;
			break;
		case RIGHT_POS: // 右
			one = true;
			canvas.rotate(225);
			degrees = 225;
			break;
		case MID_BELOW: // 中下
			one = true;
			canvas.rotate(315);
			degrees = 315;
			break;
		case MID_TOP: // 中顶
			one = true;
			canvas.rotate(135);
			degrees = 135;
			break;
		case LEFT_TOP: // 左上
			one = false;
			canvas.rotate(90);
			degrees = 90;
			break;
		case LEFT_BELOW: // 左下
			one = false;
			break;
		case RIGHT_TOP: // 右上
			one = false;
			canvas.rotate(180);
			degrees = 180;
			break;
		case RIGHT_BELOW: // 右下
			one = false;
			canvas.rotate(270);
			degrees = 270;
			break;
		}
		
		int nRrect = (int)mRectF.width() /2;
		for (int i = 0; i <= nMainRuling; i++) {

			// 数值
			if (mBitmap == null) {
				if(nValueWidth<1){
					nValueWidth=1;
				}
				mBitmap = Bitmap.createBitmap((int) nValueWidth,
						(int) nValueWidth, Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			// mCanvas.drawColor(Color.WHITE);
			String text = "";
			if (clockwise) {
				text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
			} else {
				text = getRulerText(nMainRuling - i, nMin, nMax,
						nMainRuling + 1) + "";
			}

			float left = (nValueWidth - getFontWidth(text, paint)) / 2;
			float top = (nValueWidth + 8) / 2;
			mCanvas.drawText(text, left, top, paint);
			Bitmap temp = null;
			Matrix matrix = new Matrix();
			matrix.setTranslate(nValueWidth / 2, nValueWidth / 2);
			matrix.postRotate(-degrees - nRotate);
			temp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
					mBitmap.getHeight(), matrix, true);

			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			mCanvas.drawBitmap(temp, (nValueWidth - temp.getWidth()) / 2,
					(nValueWidth - temp.getHeight()) / 2, mBitmapPaint);

			left = -nValueWidth / 2;
			top = nRrect + nMainRulingLength;
			canvas.drawBitmap(mBitmap, left, top, mBitmapPaint);
			nRotate += mMainRotate;

			canvas.drawLine(0, nWidth / 2, 0, nWidth / 2 + nMainRulingLength,
					paint);
			if (bShowMinorRuling && i != nMainRuling) {
				for (int j = 0; j < nMinorRuling; j++) {
					canvas.rotate(mMainRotate / nMinorRuling);
					if (j < nMinorRuling - 1) {
						canvas.drawLine(0, nWidth / 2, 0, nWidth / 2
								+ nMainRulingLength * ratio, paint);
					}
				}
			} else {
				canvas.rotate(mMainRotate);
			}
		}
	}

	/**
	 * 画指针
	 * 
	 * @param degrees
	 *            -指针所在的角度，水平右边为0，顺时针转动
	 * @param type
	 *            -指针类型，1-直线一个像素，2-直线两个像素，3-凌型，4-三角形
	 */
	private Path mPath;

	public void drawPointer(Paint paint, int color, Canvas canvas, int degrees,
			int type) {
		clear(canvas, paint);
		paint.reset();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(color);

		canvas.save();// 保存当前状态
		canvas.translate(nCenterX, nCenterY);
		canvas.rotate(degrees);

		switch (type) {
		case 1:// 直线-1个像素
			paint.setStrokeWidth(getLineWidth());
			canvas.drawLine(0, 0, 0, nR, paint);
			break;
		case 2:// 直线-2个像素
			paint.setStrokeWidth(3 * getLineWidth());
			canvas.drawLine(0, 0, 0, nR, paint);
			break;
		case 3:// 凌型
			if (mPath == null) {
				mPath = new Path();
				mPath.moveTo(0, 4 * getLineWidth());
				mPath.lineTo(-4 * getLineWidth(), (nR - 2 * getLineWidth()) / 2
						+ 2 * getLineWidth());
				mPath.lineTo(0, nR);
				mPath.lineTo(4 * getLineWidth(), (nR - 2 * getLineWidth()) / 2
						+ 2 * getLineWidth());
				mPath.close();
			}
			paint.setStrokeWidth(getLineWidth());
			canvas.drawPath(mPath, paint);
			break;
		case 4:// 三角形
			if (mPath == null) {
				mPath = new Path();
				mPath.moveTo(-4 * getLineWidth(), 0);
				mPath.lineTo(0, nR);
				mPath.lineTo(4 * getLineWidth(), 0);
				mPath.close();
			}
			paint.setStrokeWidth(getLineWidth());
			canvas.drawPath(mPath, paint);
			break;
		}
		canvas.drawCircle(0, 0, 4 * getLineWidth(), paint);
		canvas.restore();// 恢复到刚才保存的状态
	}

	/**
	 * 显示起始位置
	 * 
	 * @param type
	 *            -指针类型，1-直线一个像素，2-直线两个像素，3-凌型，4-三角形
	 */
	public void drawStartLine(Paint paint, int color, Canvas canvas,
			int degrees, int type) {
		paint.reset();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(2 * getLineWidth());
		canvas.save();// 保存当前状态
		canvas.translate(nCenterX, nCenterY);
		canvas.rotate(degrees);

		switch (type) {
		case 1:// 直线-1个像素
			paint.setStrokeWidth(getLineWidth());
//			if (bHole) {
//				canvas.drawLine(0, nHoleR, 0, nR - 1, paint);
//			} else {
//				canvas.drawLine(0, 0, 0, nR - 1, paint);
//			}
			canvas.drawLine(0, 0, 0, nR - 1, paint);
			break;
		case 2:// 直线-2个像素
			paint.setStrokeWidth(3 * getLineWidth());
//			if (bHole) {
//				canvas.drawLine(0, nHoleR, 0, nR - 1, paint);
//			} else {
//				canvas.drawLine(0, 0, 0, nR - 1, paint);
//			}
			canvas.drawLine(0, 0, 0, nR - 1, paint);
			break;
		case 3:// 凌型
			int r = 0;
//			if (bHole) {
//				r = nHoleR;
//			}
			if (mPath == null) {
				mPath = new Path();
				mPath.moveTo(0, r);
				mPath.lineTo(-4 * getLineWidth(), (nR - r) / 2 + r);
				mPath.lineTo(0, nR - 1);
				mPath.lineTo(4 * getLineWidth(), (nR - r) / 2 + r);
				mPath.close();
			}
			paint.setStrokeWidth(getLineWidth());
			canvas.drawPath(mPath, paint);
			break;
		case 4:// 三角形
			int R = 0;
//			if (bHole) {
//				R = nHoleR;
//			}
			if (mPath == null) {
				mPath = new Path();
				mPath.moveTo(-4 * getLineWidth(), R);
				mPath.lineTo(0, nR - 1);
				mPath.lineTo(4 * getLineWidth(), R);
				mPath.close();
			}
			paint.setStrokeWidth(getLineWidth());
			canvas.drawPath(mPath, paint);
			break;
		}
		canvas.restore();// 恢复到刚才保存的状态
	}

	/**
	 * 圆or半圆 实时数据
	 * 
	 * @param sweeps
	 *            -转动的角度
	 */
	public void drawData(Paint paint, Canvas canvas, int sweeps) {

		// 画实时数据
		if (getStyle() == CSS_TYPE.CSS_SOLIDCOLOR
				|| getStyle() == CSS_TYPE.CSS_TRANSPARENCE) {
			// 纯色or透明
			paint.reset();
			paint.setAntiAlias(true);
			paint.setStyle(Style.FILL);
			paint.setColor(nDataColor);
		} else {
			paint.reset();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (myShader == null) {
				myShader = fillRender.setRectCss(getStyle(), mBgRectF.left,
						mBgRectF.top, mBgRectF.right, mBgRectF.bottom,
						getForeColor(), nDataColor);
			}
			paint.setShader(myShader);
		}

		paint.setAlpha(nAlphas);
		canvas.drawArc(mBgRectF, startAngle, sweeps, true, paint);

		// 是否 有孔
		if (bHole) {
			paint.reset();
			paint.setAntiAlias(true);
			paint.setColor(Color.TRANSPARENT);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			canvas.drawArc(mMinorRectF, startAngle, sweep, false, paint);
			paint.setXfermode(null);
		}
	}

	/**
	 * 清除指针
	 */
	private Xfermode xMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	public void clear(Canvas canvas, Paint paint) {
		paint.setXfermode(xMode);
		paint.setColor(Color.TRANSPARENT);
		paint.setAntiAlias(true);
		canvas.drawArc(mBgRectF, 0, 360, useCenter, paint);
		paint.reset();

		// 画背景
		paint.setStyle(Style.FILL);
		paint.setColor(getBackColor());
		paint.setAlpha(nAlphas);
		paint.setAntiAlias(true);
		canvas.drawArc(mBgRectF, startAngle, sweepAngle, useCenter, paint);

		if (bHole) {
			paint.setXfermode(xMode);
			paint.setColor(Color.TRANSPARENT);
			paint.setAntiAlias(true);
			canvas.drawCircle(nCenterX, nCenterY, nHoleR, paint);
			paint.reset();
		}
	}

	/**
	 * @param index
	 *            -第几个刻度
	 * @param nMin
	 *            -最小值
	 * @param nMan
	 *            -最大值
	 * @param nMain
	 *            -刻度数
	 */
	private long getRulerText(int index, double nMin, double nMax, int nMain) {
		double mValue = (nMax - nMin) / (nMain - 1);
		double dVal = 0;
		dVal = nMin + mValue * index;
		if (index == nMain - 1)
			dVal = nMax;
		if (dVal == -0)
			dVal = 0;

		return (long) dVal;
	}

	/**
	 * 获取字体所占宽度
	 * 
	 * @param font
	 *            -文本
	 * @param paint
	 *            -已经设置大小的画笔
	 */
	private int getFontWidth(String font, Paint paint) {
		return (int) paint.measureText(font);
	}

	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent);
	}

	public void setSweepAngle(int sweepAngle) {
		this.sweepAngle = sweepAngle;
	}

	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}

	public enum ArcType {
		ROUND, // 圆
		HALF, // 半圆
		QUARTER, // 四分之一
		THREE; // 四分之三
	}

	public void setmRectF(RectF mRectF) {
		this.mRectF = mRectF;
	}

	public void setbHole(boolean bHole) {
		this.bHole = bHole;
	}

	public void setnHoleR(int nHoleR) {
		this.nHoleR = nHoleR;
	}

	public void seteArcType(ArcType eArcType) {
		this.eArcType = eArcType;
	}

	public void setbShowMinorRuling(boolean bShowMinorRuling) {
		this.bShowMinorRuling = bShowMinorRuling;
	}

	public void setnMainRulingLength(int nMainRulingLength) {
		this.nMainRulingLength = nMainRulingLength;
	}

	public void setnMinorRulingLength(int nMinorRulingLength) {
		this.nMinorRulingLength = nMinorRulingLength;
	}

	public void setnMainRuling(int nMainRuling) {
		this.nMainRuling = nMainRuling;
	}

	public void setnMinorRuling(int nMinorRuling) {
		this.nMinorRuling = nMinorRuling;
	}

	public void setbRuler(boolean bRuler) {
		this.bRuler = bRuler;
	}

	public void setnCenterX(float nCenterX) {
		this.nCenterX = nCenterX;
	}

	public void setnCenterY(float nCenterY) {
		this.nCenterY = nCenterY;
	}

	public void seteArcOrientation(GLOBAL_POS_PROP eArcOrientation) {
		this.eArcOrientation = eArcOrientation;
	}

	public void setUseCenter(boolean useCenter) {
		this.useCenter = useCenter;
	}

	public void setnR(int nR) {
		this.nR = nR;
	}

	public void setnDataColor(int nDataColor) {
		this.nDataColor = nDataColor;
	}

	public void setnRulerColor(int nRulerColor) {
		this.nRulerColor = nRulerColor;
	}

	public void setnMax(double nMax) {
		this.nMax = nMax;
	}

	public void setnMin(double nMin) {
		this.nMin = nMin;
	}

	public void setnValueWidth(int nValueWidth) {
		this.nValueWidth = nValueWidth;
	}

	public void setnAlphas(short nAlpha) {
		this.nAlphas = nAlpha;
	}
	
	public void setbShowRuleValue(boolean bShowRuleValue) {
		this.bShowRuleValue = bShowRuleValue;
	}
	
	public void setnOffsetAngle(int degree){
		this.nOffsetAngle = degree;
	}
	
	
	public RectF getBgRectF(){
		return mBgRectF;
	}
	
	public void setbShowFrame(boolean show){
		bShowFrame = show;
	}
	
}
