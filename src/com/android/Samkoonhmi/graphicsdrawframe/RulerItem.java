package com.android.Samkoonhmi.graphicsdrawframe;

import java.math.BigDecimal;

import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 刻度-尺子
 * 
 * @author 刘伟江
 * @version v 1.0.0.1 创建时间 2012-4-24 最后修改时间 2012-4-24
 */
public class RulerItem extends SquareItem {

	// 是否显示次标尺-默认false
	private boolean bShowMinorRuling = false;
	// 标尺方向,向外or向内（向左-表示向外，向右表示向内）
	private DIRECTION eRulerDir;
	// 大小刻度比例
	private static final float ratio = 0.5f;
	// 主刻度
	private int nMainRuling = 2;
	// 次刻度
	private int nRuling = 5;
	// 刻度左顶点X坐标
	private float nLTX;
	// 刻度左顶点Y坐标
	private float nLTY;
	// 是否显示刻度数
	private int nItemX;
	private int nItemY;
	private int nItemRX;
	private int nItemRY;
	private double nMin;
	private double nMax;
	private float nHeight;
	private float nWidht;
	private float nTextWidth;
	private boolean init;
	private DisplayMetrics dis;
	private float nSize = 8;
	private int nRulerDir;// 刻度相对柱状图，标尺方向 0-向外 1-向内
	private int nIncrease;// 是递增还是递减 0-递增，1-递减

	public RulerItem(Rect mRect) {
		super(mRect);
		init = true;
	}

	/**
	 * 初始化刻度
	 */
	private void initRuler() {
		dis = new DisplayMetrics();
		dis.density = (float) 1.3125;
		dis.densityDpi = 210;
		dis.scaledDensity = (float) 1.3125;
		dis.xdpi = (float) 225.77777;
		dis.ydpi = (float) 225.77777;
		nSize = TypedValue.applyDimension(2, nSize, dis);

		nHeight = getHeight();
		nWidht = getWidth();
		switch (eRulerDir) {
		case TOWARD_LEFT: // 向左 or 向右
		case TOWARD_RIGHT:
			nWidht = nWidht + 2;
			nMainLength = nHeight / nMainRuling;
			break;
		case TOWARD_TOP: // 向上 or 向下
		case TOWARD_BOTTOM: // 向下
			nHeight = nHeight + 2;
			nMainLength = nWidht / nMainRuling;
			break;
		}
	}

	/**
	 * 画刻度
	 */
	private float nMainLength;// 主标尺,每一段的长度
	private float nMinorLength;// 次标尺，每一段的长度
	private float nMinorWidth;// 次标尺宽度
	private void drawRuler(Paint paint, Canvas canvas) {
		if (init) {
			initRuler();
			init = false;
		}
		paint.reset();
		paint.setAntiAlias(true);
		paint.setColor(getLineColor());
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(1);
		paint.setStrokeJoin(Join.BEVEL);
		paint.setTextSize(nSize);

		switch (eRulerDir) {
		case TOWARD_LEFT: // 向左
			nMainLength = nHeight / nMainRuling;
			nMinorLength = nMainLength / nRuling;
			nMinorWidth = nWidht * ratio;
			if (nRulerDir == 0) {
				nTextWidth = nLTX - nItemX;
				drawLeftValue(paint, canvas);
			} else {
				nTextWidth = nItemRX - nWidht - nLTX;
				drawRightValue(paint, canvas);
			}
			drawLeft(paint, canvas);
			break;
		case TOWARD_RIGHT: // 向右
			nMainLength = nHeight / nMainRuling;
			nMinorLength = nMainLength / nRuling;
			nMinorWidth = nWidht * ratio;
			if (nRulerDir == 0) {
				nTextWidth = nItemRX - nWidht - nLTX;
				drawRightValue(paint, canvas);
			} else {
				nTextWidth = nLTX - nItemX;
				drawLeftValue(paint, canvas);
			}
			drawRight(paint, canvas);
			break;
		case TOWARD_TOP: // 向上
			nMainLength = nWidht / nMainRuling;
			nMinorLength = nMainLength / nRuling;
			nMinorWidth = nHeight * ratio;
			if (nRulerDir == 0) {
				nTextWidth = nLTY - nItemY;
				drawTopValue(paint, canvas, nRulerDir);
			} else {
				nTextWidth = nItemRY - nLTY;
				drawBottomValue(paint, canvas, nRulerDir);
			}
			drawTop(paint, canvas);
			break;
		case TOWARD_BOTTOM: // 向下
			nMainLength = nWidht / nMainRuling;
			nMinorLength = nMainLength / nRuling;
			nMinorWidth = nHeight * ratio;
			if (nRulerDir == 0) {
				nTextWidth = nItemRY - nLTY - nHeight;
				drawBottomValue(paint, canvas, nRulerDir);
			} else {
				nTextWidth = nLTY - nItemY;
				drawTopValue(paint, canvas, nRulerDir);
			}
			drawBottom(paint, canvas);
			break;
		}
	}

	/**
	 * 刻度-左边
	 */
	private void drawLeft(Paint paint, Canvas canvas) {

		float LTY = 0;
		// 画竖线
		canvas.drawLine(nWidht - 1 + nLTX, 0 + nLTY, nWidht - 1 + nLTX, nHeight
				+ nLTY, paint);
		// 画横线
		for (int i = 0; i <= nMainRuling; i++) {

			if (i == 0) {
				canvas.drawLine(0 + nLTX, 1 + nLTY, nWidht - 2 + nLTX,
						1 + nLTY, paint);
			} else if (i == nMainRuling) {
				canvas.drawLine(0 + nLTX, nHeight - 1 + nLTY,
						nWidht - 2 + nLTX, nHeight - 1 + nLTY, paint);
			} else {
				canvas.drawLine(0 + nLTX, (int) LTY + nLTY, nWidht - 2 + nLTX,
						(int) LTY + nLTY, paint);
			}

			// 次标尺
			if (bShowMinorRuling && i < nMainRuling) {
				float y = (LTY + nMinorLength);
				for (int j = 0; j < nRuling - 1; j++) {
					canvas.drawLine((nWidht - nMinorWidth) - 1 + nLTX, (int) y
							+ nLTY, nWidht - 2 + nLTX, (int) y + nLTY, paint);
					y += nMinorLength;
				}
			}
			LTY += nMainLength;
		}

	}

	/**
	 * 刻度-右边
	 */
	private void drawRight(Paint paint, Canvas canvas) {

		float LTY = 0;
		// 画竖线
		canvas.drawLine(nLTX + 1, 0 + nLTY, nLTX + 1, nHeight + nLTY, paint);

		// 画横线
		for (int i = 0; i <= nMainRuling; i++) {

			if (i == 0) {
				canvas.drawLine(nLTX + 2, 1 + nLTY, nLTX + nWidht, 1 + nLTY,
						paint);
			} else if (i == nMainRuling) {
				canvas.drawLine(nLTX + 2, nHeight - 1 + nLTY, nLTX + nWidht,
						nHeight - 1 + nLTY, paint);
			} else {
				canvas.drawLine(nLTX + 2, (int) LTY + nLTY, nLTX + nWidht,
						(int) LTY + nLTY, paint);
			}

			// 次标尺
			if (bShowMinorRuling && i < nMainRuling) {
				float y = LTY + nMinorLength;
				for (int j = 0; j < nRuling - 1; j++) {
					canvas.drawLine(nLTX + 2, (int) y + nLTY, nLTX
							+ nMinorWidth + 1, (int) y + nLTY, paint);
					y += nMinorLength;
				}
			}
			LTY += nMainLength;
		}
	}

	/**
	 * 刻度-顶部
	 */
	private void drawTop(Paint paint, Canvas canvas) {
		float LTX = 0;
		// 画横线
		canvas.drawLine(nLTX + 0, nHeight - 1 + nLTY, nLTX + nWidht, nHeight
				- 1 + nLTY, paint);

		// 画竖线
		for (int i = 0; i <= nMainRuling; i++) {

			if (i == 0) {
				canvas.drawLine(1 + nLTX, 0 + nLTY, 1 + nLTX, nHeight - 2
						+ nLTY, paint);
			} else if (i == nMainRuling) {
				canvas.drawLine(nLTX + nWidht - 1, 0 + nLTY, nLTX + nWidht - 1,
						nHeight - 2 + nLTY, paint);
			} else {
				canvas.drawLine(nLTX + (int) LTX, 0 + nLTY, nLTX + (int) LTX,
						nHeight - 2 + nLTY, paint);
			}

			// 次标尺
			if (bShowMinorRuling && i < nMainRuling) {
				float x = LTX + nMinorLength;
				for (int j = 0; j < nRuling - 1; j++) {
					canvas.drawLine(nLTX + (int) x, nMinorWidth - 1 + nLTY,
							nLTX + (int) x, nHeight - 2 + nLTY, paint);
					x += nMinorLength;
				}
			}
			LTX += nMainLength;
		}
	}

	/**
	 * 刻度-底部
	 */
	private void drawBottom(Paint paint, Canvas canvas) {
		float LTX = 0;
		// 画横线
		canvas.drawLine(0 + nLTX, 1 + nLTY, nWidht + nLTX, 1 + nLTY, paint);

		// 画竖线
		for (int i = 0; i <= nMainRuling; i++) {

			if (i == 0) {
				canvas.drawLine(1 + nLTX, 2 + nLTY, 1 + nLTX, nHeight + nLTY,
						paint);
			} else if (i == nMainRuling) {
				canvas.drawLine(nWidht - 1 + nLTX, 2 + nLTY, nWidht - 1 + nLTX,
						nHeight + nLTY, paint);
			} else {
				canvas.drawLine((int) LTX + nLTX, 2 + nLTY, (int) LTX + nLTX,
						nHeight + nLTY, paint);
			}

			// 次标尺
			if (bShowMinorRuling && i < nMainRuling) {
				float x = LTX + nMinorLength;
				for (int j = 0; j < nRuling - 1; j++) {
					canvas.drawLine((int) x + nLTX, 2 + nLTY, (int) x + nLTX,
							nMinorWidth + 1 + nLTY, paint);
					x += nMinorLength;
				}
			}
			LTX += nMainLength;
		}
	}

	/**
	 * 左边-数值
	 */
	private void drawLeftValue(Paint paint, Canvas canvas) {
		float LTY = 0;

		/**
		 * 画刻度数
		 */
		LTY = 0;
		if (nIncrease == 0) {
			for (int i = nMainRuling; i >= 0; i--) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);
				int height = getFontHeight(paint);

				float left = nItemX + (nTextWidth - width) / 2;
				if (left < nItemX) {
					left = nItemX;
				}

				if (width > nTextWidth) {
					text = getText(text, (int) nTextWidth + text.length(),
							text.length(), paint);
					left = nItemX - text.length() / 2;
				}

				canvas.drawText(text, left, LTY + nLTY + height / 2 - 2, paint);
				LTY += nMainLength;
			}
		} else {
			for (int i = 0; i <= nMainRuling; i++) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);
				int height = getFontHeight(paint);

				float left = nItemX + (nTextWidth - width) / 2;
				if (left < nItemX) {
					left = nItemX;
				}

				if (width > nTextWidth) {
					text = getText(text, (int) nTextWidth + text.length(),
							text.length(), paint);
					left = nItemX - text.length() / 2;
				}

				canvas.drawText(text, left, LTY + nLTY + height / 2 - 2, paint);
				LTY += nMainLength;
			}
		}

	}

	/**
	 * 右边-数值
	 */
	private void drawRightValue(Paint paint, Canvas canvas) {
		float LTY = 0;
		/**
		 * 画刻度数
		 */
		LTY = 0;
		if (nIncrease == 0) {
			for (int i = 0; i <= nMainRuling; i++) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);
				int height = getFontHeight(paint);

				float left = nLTX + nWidht + (nTextWidth - width) / 2;
				if (left < nLTX + nWidht) {
					left = nLTX + nWidht;
				}

				if (width > nTextWidth) {
					text = getText(text, (int) nTextWidth + text.length(),
							text.length(), paint);
					left = nLTX + nWidht - text.length() / 2;
				}

				canvas.drawText(text, left, LTY + nLTY + height / 2 - 2, paint);
				LTY += nMainLength;
			}
		} else {
			for (int i = nMainRuling; i >= 0; i--) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);
				int height = getFontHeight(paint);

				float left = nLTX + nWidht + (nTextWidth - width) / 2;
				if (left < nLTX + nWidht) {
					left = nLTX + nWidht;
				}

				if (width > nTextWidth) {
					text = getText(text, (int) nTextWidth + text.length(),
							text.length(), paint);
					left = nLTX + nWidht - text.length() / 2;
				}

				canvas.drawText(text, left, LTY + nLTY + height / 2 - 2, paint);
				LTY += nMainLength;
			}
		}

	}

	/**
	 * 上面-数值
	 */
	private void drawTopValue(Paint paint, Canvas canvas, int type) {
		float LTX = 0;
		/**
		 * 画刻度数
		 */
		LTX = 0;
		if (nIncrease == 0) {
			for (int i = 0; i <= nMainRuling; i++) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);

				float left = LTX + nLTX - width / 2;
				if (left < nItemX) {
					left = nItemX;
				}

				if (nLTX + width + LTX > nItemRX) {
					text = getText(text, (int) (nItemRX - left), text.length(),
							paint);
				}

				if (type == 0) {
					// 刻度朝外
					if (nTextWidth > 0) {
						canvas.drawText(text, left,
								nItemY + nTextWidth / 2 + 5, paint);
					} else {
						canvas.drawText(text, left, nItemY, paint);
					}
				} else {
					// 刻度朝内
					if (nTextWidth > 0) {
						canvas.drawText(text, left, nLTY - nTextWidth / 2 + 5,
								paint);
					} else {
						canvas.drawText(text, left, nLTY, paint);
					}
				}

				LTX += nMainLength;
			}
		} else {
			for (int i = nMainRuling; i >= 0; i--) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);

				float left = LTX + nLTX - width / 2;
				if (left < nItemX) {
					left = nItemX;
				}

				if (nLTX + width + LTX > nItemRX) {
					text = getText(text, (int) (nItemRX - left), text.length(),
							paint);
				}

				if (type == 0) {
					// 刻度朝外
					if (nTextWidth > 0) {
						canvas.drawText(text, left,
								nItemY + nTextWidth / 2 + 5, paint);
					} else {
						canvas.drawText(text, left, nItemY, paint);
					}
				} else {
					// 刻度朝内
					if (nTextWidth > 0) {
						canvas.drawText(text, left, nLTY - nTextWidth / 2 + 5,
								paint);
					} else {
						canvas.drawText(text, left, nLTY, paint);
					}
				}

				LTX += nMainLength;
			}
		}
	}

	/**
	 * 底下-数值
	 */
	private void drawBottomValue(Paint paint, Canvas canvas, int type) {
		float LTX = 0;
		/**
		 * 画刻度数
		 */
		LTX = 0;
		if (nIncrease == 0) {
			for (int i = nMainRuling; i >= 0; i--) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);

				float left = LTX + nLTX - width / 2;
				if (left < nItemX) {
					left = nItemX;
				}

				if (nLTX + width + LTX > nItemRX) {
					text = getText(text, (int) (nItemRX - left), text.length(),
							paint);
				}

				if (type == 0) {
					// 刻度朝外
					if (nTextWidth > 0) {
						canvas.drawText(text, left, nLTY + nHeight + nTextWidth
								/ 2 + 3, paint);
					} else {
						canvas.drawText(text, left, nLTY + nHeight, paint);
					}
				} else {
					// 刻度朝内
					if (nTextWidth > 0) {
						canvas.drawText(text, left, nLTY + nTextWidth / 2 + 7,
								paint);
					} else {
						canvas.drawText(text, left, nLTY, paint);
					}
				}

				LTX += nMainLength;
			}
		} else {
			for (int i = 0; i <= nMainRuling; i++) {
				String text = "";
				if (i == 0) {
					text = (int) nMin + "";
				} else if (i == nMainRuling) {
					text = (int) nMax + "";
				} else {
					text = getRulerText(i, nMin, nMax, nMainRuling + 1) + "";
				}
				int width = getFontWidth(text, paint);

				float left = LTX + nLTX - width / 2;
				if (left < nItemX) {
					left = nItemX;
				}

				if (nLTX + width + LTX > nItemRX) {
					text = getText(text, (int) (nItemRX - left), text.length(),
							paint);
				}

				if (type == 0) {
					// 刻度朝外
					if (nTextWidth > 0) {
						canvas.drawText(text, left, nLTY + nHeight + nTextWidth
								/ 2 + 3, paint);
					} else {
						canvas.drawText(text, left, nLTY + nHeight, paint);
					}
				} else {
					// 刻度朝内
					if (nTextWidth > 0) {
						canvas.drawText(text, left, nLTY + nTextWidth / 2 + 7,
								paint);
					} else {
						canvas.drawText(text, left, nLTY, paint);
					}
				}

				LTX += nMainLength;
			}
		}

	}

	@Override
	public void draw(Paint paint, Canvas canvas) {
		super.draw(paint, canvas);

		drawRuler(paint, canvas);
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

		BigDecimal bgDecimal = new BigDecimal(dVal).setScale(0,
				BigDecimal.ROUND_HALF_UP);

		return bgDecimal.longValue();
	}

	/**
	 * 字符串截取
	 * 
	 * @param text
	 *            -原始文本
	 * @param width
	 *            -显示区域的宽度
	 * @param len
	 *            -文本的长度
	 * @param paint
	 *            -已经设置好大小的画笔
	 */
	private String getText(String text, int width, int len, Paint paint) {
		String mText = "";
		int mLen = 0;
		if (!text.equals("")) {
			for (int i = 0; i < len; i++) {
				mLen += getFontWidth(text.charAt(i) + "", paint);
				if (mLen >= width) {
					return mText;
				}
				mText += text.charAt(i);
			}
		}
		return mText;
	}

	public void setbShowMinorRuling(boolean bShowMinorRuling) {
		this.bShowMinorRuling = bShowMinorRuling;
	}

	public void setnMainRuling(int nMainRuling) {
		if (nMainRuling == 0) {
			nMainRuling = 2;
		}
		this.nMainRuling = nMainRuling;
	}

	public void setnRuling(int nRuling) {
		if (nRuling == 0) {
			nRuling = 5;
		}
		this.nRuling = nRuling;
	}

	public void setnLTX(int nLTX) {
		this.nLTX = nLTX;
	}

	public void setnLTY(int nLTY) {
		this.nLTY = nLTY;
	}

	public void seteRulerDir(DIRECTION eRulerDir) {
		this.eRulerDir = eRulerDir;
	}

	public void setnItemX(int nItemX) {
		this.nItemX = nItemX;
	}

	public void setnItemY(int nItemY) {
		this.nItemY = nItemY;
	}

	public void setnMin(double nMin) {
		this.nMin = nMin;
	}

	public void setnMax(double nMax) {
		this.nMax = nMax;
	}

	public void setnItemRX(int nItemRX) {
		this.nItemRX = nItemRX;
	}

	public void setnItemRY(int nItemRY) {
		this.nItemRY = nItemRY;
	}

	public void setnRulerDir(int nRulerDir) {
		this.nRulerDir = nRulerDir;
	}

	public void setnIncrease(int nIncrease) {
		this.nIncrease = nIncrease;
	}
}
