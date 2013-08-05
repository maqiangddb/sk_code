package com.android.Samkoonhmi.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

/**
 * 控件缓存
 */
public class ItemCache {

	//控件画布
	public Canvas mCanvas;
	//控件缓存
	public Bitmap mBitmap;
	
	public ItemCache(Rect rect){
		if (rect==null||rect.width()<=0||rect.height()<=0) {
			return;
		}
		mBitmap=Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		mCanvas=new Canvas(mBitmap);
	}
}
