package com.android.Samkoonhmi.graphicsdrawframe;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.LineTypeUtil;
import com.android.Samkoonhmi.util.TextAlignUtil;
import com.android.Samkoonhmi.util.TextAttribute;


/**
 * 写字
 * 
 * @author Administrator
 * 
 */
public class TextItem {
	private float strokWidth;
	private Shader myShader;
	private FillRender fillRender;
	private StaticTextModel mText;// 静态文本实体类
	private RectF mRect;
	private String[] textArray;
	private int fontWidth = 0;
	private int fontHeight = 0;
	private int decent = 0;
	private float top = 0;
	private DisplayMetrics dis;
	private int fitsizetop = 0;// 自适应top清零
	private static String TAG = "Text";
	private Paint textPaint;
	private Paint backPaint;
	private Paint borderPaint;
	private int nAllHeight = 0;
	private Canvas mCanvas = null;
	private Bitmap mBitmap = null;
	private String fontText="";
	private boolean bSpill;//文本溢出控件范围
	private PointF point;
	private TextAlignUtil mTextAlignUtil;

	public StaticTextModel getmText() {
		return mText;
	}

	public TextItem(StaticTextModel text) {
		// 初始化值
		this.mText = text;
		textPaint = new Paint();
		backPaint = new Paint();
		borderPaint = new Paint();
		mTextAlignUtil=new TextAlignUtil();
		point=new PointF();
		mRect = new RectF();
	}

	
	public void draw(Canvas canvas) {
		if (null == mText) {
			return;
		}
		
		float rectWidth = mText.getRectWidth();
		float rectHeight = mText.getRectHeight();
		
		if (1 > rectWidth || 1 > rectHeight) {
			return;
		}
		
		bSpill=false;
		
		if (mText.getM_sTextStr()==null||mText.getM_sTextStr().equals("")) {
			textArray=null;
			fontWidth=0;
			fontHeight=0;
			nAllHeight=0;
		}else{
			if (mText.isbTextChange()) {
				// 获取字的宽度跟高度
				fontText = mText.getM_sTextStr()+" ";
				textArray = fontText.split("\n");
				fontWidth = getFontWidth(textArray, textPaint);
				fontHeight = getFontHeight(textPaint);
				nAllHeight = fontHeight * textArray.length;
			}
		}
		
		// 判断字的宽度或高度如果大于所在的矩形框 则要用另外一个bitMap画上 以截取掉超出的部分，或者字体大小等于-1时的自适应
		if (fontWidth > rectWidth || nAllHeight > rectHeight
				|| mText.getM_nFontSize() == -1) {
			
			bSpill=true;
			mRect.left =  0;
			mRect.right = mText.getRectWidth();
			mRect.top =  0;
			mRect.bottom = mText.getRectHeight();
			if (mCanvas == null) {
				mBitmap = Bitmap.createBitmap((int) rectWidth,
						(int) rectHeight, Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		} else {
			mRect.left =  mText.getStartX();
			mRect.right = mText.getStartX() + mText.getRectWidth();
			mRect.top = mText.getStartY();
			mRect.bottom = mText.getStartY() + mText.getRectHeight();
		}
		
		// 画矩形背景填充
		if (mText.getM_alphaPadding() > 0) {
			
			if(bSpill){
				mCanvas.drawRect(mRect, backPaint);
			}else {
				canvas.drawRect(mRect, backPaint);
			}
			
		}

		// 画矩形背景边框
		int tmpLineWidth = mText.getLineWidth();
		if (tmpLineWidth > 0 && mText.getBorderAlpha() > 0) {
			if (bSpill) {
				mCanvas.drawRect(mRect, borderPaint);
			}else {
				canvas.drawRect(mRect, borderPaint);
			}
		}

		
		// 画字
		// 根据文字的对齐方式，获得文字在矩形框中的起点
		if (mText.isbTextChange()) {
			mTextAlignUtil.getAlign(point,mText.getM_eTextAlign(),
					mRect.height(), mRect.width(), fontWidth, fontHeight);
			
			if (!bSpill) {
				point.x += mText.getStartX();
				point.y += mText.getStartY();
			}

		}
		
		
		if (null != textArray) {
			if (textArray.length > 1) {
				top = (mRect.height() - nAllHeight) / 2 + 3 * fontHeight
						/ 4;
			} else {
				top = mRect.height() / 2 + fontHeight / 4 + 2;
			}
			if (top < 0) {
				top = 0;
			}
			
			if (mText.getM_nFontSize() > 0) {
				for (int i = 0; i < textArray.length; i++) {
					// 字距大于0，判断换行的下划线是否存在
					if (mText.getM_nFontSpace() > 0) {
						
						textPaint.setUnderlineText(false);
						if (TextAttribute.UNDERLINE == (TextAttribute.UNDERLINE & mText
								.getM_textPro())) {
							String regex = ".*[^ ].*";// 非空字符串且全为空格
							boolean b = textArray[i].matches(regex);
							if (b) {
								textPaint.setUnderlineText(true);
							} 
						}
						
					}
					
					if(mText.isbTextChange() && i == textArray.length -1)
					{
						if( textArray[i].length() > 0)
						textArray[i] = textArray[i].substring(0, textArray[i].length()-1);
					}
					
					if (bSpill) {
						mCanvas.drawText(textArray[i], point.x, top,
								textPaint);
					}else {
						canvas.drawText(textArray[i], point.x,
								top + mText.getStartY(), textPaint);
					}
					
					top += fontHeight;
				}
			} else {
				// 自适应
				for (int i = 0; i < textArray.length; i++) {
					if (bSpill) {
						bFitSize(mCanvas, textPaint, textArray, i);// 自适应
					}else {
						bFitSize(canvas, textPaint, textArray, i);// 自适应
					}
					top += fontHeight;
				}

				fitsizetop = 0;// 自适应top清零
			}

			if (bSpill) {
				canvas.drawBitmap(mBitmap, mText.getStartX(),
						mText.getStartY(), textPaint);
			}

		}
		mText.setbTextChange(false);
	}

	/**
	 * 文本框的边框属性设置
	 * @param paint
	 */
	public void initRectBoderPaint() {
		if (mText.getLineWidth() == 0 || mText.getBorderAlpha() == 0) {
			return;
		}
		
		borderPaint.setAntiAlias(true);// 去锯齿
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setColor(mText.getLineColor());
		borderPaint.setStrokeWidth(mText.getLineWidth());
		borderPaint.setAlpha(mText.getBorderAlpha());

	}

	/**
	 * 初始化写字的画笔
	 * @param paint
	 */
	public void initTextPaint() {
		
		textPaint.reset();
		myShader = textPaint.getShader();
		textPaint.setAntiAlias(true);// 去锯齿
		textPaint.setStyle(Paint.Style.FILL_AND_STROKE);// 样式
		textPaint.setColor(mText.getM_nFontColor());// 字体颜色
		if (null == dis) {
			dis = new DisplayMetrics();

			dis.density = (float) 1.3125;
			dis.densityDpi = 210;
			dis.scaledDensity = (float) 1.3125;
			dis.xdpi = (float) 225.77777;
			dis.ydpi = (float) 225.77777;
		}
		if (null != dis) {
			// 得到字体的单位和大小
			float f = TypedValue.applyDimension(1, mText.getM_nFontSize(), dis);
			textPaint.setTextSize(f);// 字体大小
		} else {
			textPaint.setTextSize(mText.getM_nFontSize());
		}
		textPaint.setStrokeJoin(Join.ROUND);
		textPaint.setTextScaleX(1.0f);
		// 文本位置
		if (mText.getM_eTextAlign() == TEXT_PIC_ALIGN.LEFT) {
			textPaint.setTextAlign(Align.LEFT);// 文本位置
		} else if (mText.getM_eTextAlign() == TEXT_PIC_ALIGN.RIGHT) {
			textPaint.setTextAlign(Align.RIGHT);// 文本位置
		} else {
			textPaint.setTextAlign(Align.CENTER);// 文本位置
		}

		// 设置字体类型
		Typeface typeface = Typeface.DEFAULT;
		;
		if (null != mText.getM_sFontFamly()) {
			String mFontFamly = mText.getM_sFontFamly();
			if (!mFontFamly.equals("")) {
				typeface = TextAlignUtil.getTypeFace(mFontFamly);
			}
		}
		textPaint.setTypeface(typeface);
		// 设置字体的粗体、斜体和下划线
		if (TextAttribute.BOLD == (TextAttribute.BOLD & mText.getM_textPro())) {
			textPaint.setFakeBoldText(true); // true为粗体，false为非粗体
		}

		if (TextAttribute.UNDERLINE == (TextAttribute.UNDERLINE & mText
				.getM_textPro())) {
			textPaint.setUnderlineText(true);// 字体是否有下划线
		}
		if (TextAttribute.ITALIC == (TextAttribute.ITALIC & mText
				.getM_textPro())) {// 斜体
			textPaint.setTextSkewX(-0.25f); // float类型参数，负数表示右斜，整数左斜
		}
		textPaint.setShader(myShader);// 着色
	}

	/**
	 * 初始化背景方形
	 * 
	 * @param paint
	 */
	private PathEffect effect;

	public void initRectPaint() {
		// 透明度为 0 不进行属性设置
		if (mText.getM_alphaPadding() == 0) {
			return;
		}
		
		backPaint.reset();
		strokWidth = backPaint.getStrokeWidth();
		backPaint.setAntiAlias(true);// 去锯齿
		backPaint.setStrokeWidth(strokWidth);// 设置画笔宽度
		if (null == fillRender) {
			fillRender = new FillRender();
		}
		// 设置填充的样式
		if (CSS_TYPE.CSS_SOLIDCOLOR == mText.getM_stylePadding()) {// 如果是纯色
			backPaint.setStyle(Paint.Style.FILL_AND_STROKE);// 样式(填充和描边)

			if (mText.getM_backColorPadding() == Color.TRANSPARENT) {
				backPaint.setAlpha(0);
			} else {
				backPaint.setColor(mText.getM_backColorPadding());// 设置矩形的填充颜色
				backPaint.setAlpha(mText.getM_alphaPadding());// 设置透明度
			}

		} else if (CSS_TYPE.CSS_TRANSPARENCE == mText.getM_stylePadding()) {// 如果是透明样式
			backPaint.setStyle(Paint.Style.STROKE);// 设置样式
			backPaint.setColor(mText.getLineColor());// 设置线颜色
			backPaint.setStrokeWidth(mText.getLineWidth());// 设置线的宽度
			backPaint.setAlpha(0);// 设置透明度
		} else {
			backPaint.setStyle(Paint.Style.FILL_AND_STROKE);// 样式(填充和描边)
			Shader shader = fillRender.setRectCss(mText.getM_stylePadding(),
					mText.getStartX(), mText.getStartY(), mText.getStartX()
							+ mText.getRectWidth(),
					mText.getStartY() + mText.getRectHeight(),
					mText.getM_foreColorPadding(),
					mText.getM_backColorPadding());
			backPaint.setShader(shader);// 着色
			backPaint.setAlpha(mText.getM_alphaPadding());// 透明度
		}

		if (0 == strokWidth)// 不显示的线 即线的颜色为透明
		{
			backPaint.setStrokeWidth(0);// 设置画笔宽度
		} else {
			if (null == effect) {
				effect = LineTypeUtil.getPathEffect(LINE_TYPE.SOLID_LINE, 1);
			}
			if (null != effect) {
				backPaint.setPathEffect(effect);// 设置线的样式
			}
		}
	}

	// 自适应
	private void bFitSize(Canvas canvas, Paint paint, String[] textArray, int i) {
		canvas.save();
		int fontWidths = 0;// 字宽
		int fontHeights = 0;// 字高
		double xScale = 0;// x缩放比例
		double yScale = 0;// y缩放比例
		float height = 0;// 字体所在canvas的y位置
		float rectHeight = mText.getRectHeight();

		if (textArray.length == 1) {// 没有换段
			fontWidths = getFontWidth(textArray, paint);// 字宽
			fontHeights = getFontHeight(paint);// 字高

			xScale = mText.getRectWidth() / (fontWidths + 0.5);// 字宽比例
			yScale = mText.getRectHeight() / fontHeights;// 字高比例
			// height = (float) (mText.getRectHeight() * 2 - fontHeights) / 2;
			canvas.translate(mText.getRectWidth() / 2,
					(int) (mText.getRectHeight() / 1.3));// 文字在画布的哪个位置
			canvas.scale((float) (xScale), (float) (yScale));// 按照比例缩小或放大字体
			canvas.drawText(mText.getM_sTextStr(), 1, 1, paint);
		} else {// 换段
			paint.setTextAlign(Align.LEFT);// 字体左对齐
			fontWidths = getFontWidth(textArray, paint);// 字宽
			fontHeights = getFontHeight(paint);// 字高
			xScale = mText.getRectWidth() / (fontWidths + 0.5);// 字宽比例
			yScale = rectHeight / fontHeights;// 字高比例
			height = (float) (rectHeight * 2 - fontHeights) / 2;// 文本高度比例
			canvas.translate(mText.getRectWidth() / 2, height);// 文字在画布的哪个位置
			canvas.scale((float) (xScale), (float) (yScale) / textArray.length);// 按照比例缩小或放大字体
			canvas.drawText(textArray[i], -getFontWidth(textArray, paint) / 2,
					fitsizetop - fontHeights * (textArray.length - 1), paint);
			fitsizetop += fontHeights;// 累加高度
		}
		canvas.restore();
	}

	/**
	 * 获取字体最宽的一行的宽度
	 * @param font-文本
	 * @param paint-已经设置大小的画笔
	 */
	private int getFontWidth(String[] fontArray, Paint paint) {
		if (null != fontArray) {
			int temp = 0;
			int tempLength = 0;
			if (fontArray.length == 1) {
				return (int) paint.measureText(fontArray[0]);
			} else {

				for (int i = 0; i < fontArray.length; i++) {
					int j = (int) paint.measureText(fontArray[i]);
					if (j > tempLength) {
						tempLength = j;
						temp = i;
					}
				}
				return (int) paint.measureText(fontArray[temp]);
			}

		} else {
			return 0;
		}
	}

	/**
	 * 重新设置背景
	 * @param color-颜色
	 * @param type-1 前景色，type-2 背景色，type-3 边框色
	 */
	public void resetColor(int color,int type){
		if (type==2) {
			backPaint.setColor(color);
		}else if (type==3) {
			borderPaint.setColor(color);
		}
	}
	
	
	/**
	 * 重新设置背景
	 * @param color-颜色
	 */
	public void resetAlpha(int alpha){
		backPaint.setAlpha(alpha);
		borderPaint.setColor(alpha);
	}
	
	public StaticTextModel getModel(){
		return mText;
	}
	
	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) (Math.ceil(fm.descent - fm.ascent));
	}

	private int decent(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) fm.descent;
	}

}