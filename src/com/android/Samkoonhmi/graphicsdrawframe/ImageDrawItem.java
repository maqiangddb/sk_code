package com.android.Samkoonhmi.graphicsdrawframe;

//import GraphicsDrawBase;
import com.android.Samkoonhmi.util.ImageFileTool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageDrawItem extends GraphicsDrawBase {
	private String imagePath;
	private Rect mRect;
	private Bitmap bitmap;

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Rect getmRect() {
		return mRect;
	}

	public void setmRect(Rect mRect) {
		this.mRect = mRect;
	}

	public ImageDrawItem(String sImagePath, Rect rect) {
		// TODO put your implementation here.
		setImagePath(sImagePath);
		setmRect(rect);
	}

	public void draw(Paint paint, Canvas canvas) {
		// TODO put your implementation here.
		if (null == getImagePath() || "".equals(getImagePath())
				|| null == getmRect()) {
			return;
		} else {
			paint.reset();
			initPaint(paint);
			if (bitmap == null) {
				bitmap = ImageFileTool.getBitmap(getImagePath());// BitmapFactory.decodeFile(getImagePath());
			}

			if (null != bitmap) {
				canvas.drawBitmap(bitmap, null, getmRect(), paint);
				// bm.recycle();
			}
		}
	}

	/**
	 * 初始化画笔
	 * 
	 * @param paint
	 */
	private void initPaint(Paint paint) {
		paint.setAntiAlias(true);
		paint.setColor(getLineColor());
		paint.setAlpha(getAlpha());
		paint.setStrokeWidth(getLineWidth());

	}

}