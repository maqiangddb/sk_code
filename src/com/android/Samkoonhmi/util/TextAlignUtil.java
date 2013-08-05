package com.android.Samkoonhmi.util;

import java.util.HashMap;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.FontMapBiz;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;

public class TextAlignUtil {
	
	public  void getAlign(PointF point,TEXT_PIC_ALIGN align, float rectHeight ,float rectWidth,int fontWidth,int fontHeight) {
		if (point==null) {
			point=new PointF();
		}
		float centerY=rectHeight/2+fontHeight/4;
		switch (align) {
		case LEFT:
			point.x = 2;
			point.y =centerY;
			break;
		case CENTER:
			point.x = rectWidth / 2;
			point.y = centerY;
			break;
		case RIGHT:
			point.x = rectWidth-2;
			point.y =centerY;
			break;
		default:
			point.x = rectWidth / 2;
			point.y = centerY;
			break;
		}
	}
	/**
	 * 获取字体
	 * @param ttfName
	 * @return
	 */
	private static  FontMapBiz biz;
	public static Typeface getTypeFace(String fontName)
	{
		
		if (fontName==null||fontName.equals("")) {
			//Log.d("SKScene", "fontName null");
			return Typeface.DEFAULT;
		}
		
		if(null == biz)
		{
			biz = new FontMapBiz();
		}
		
		/**
		 * 已经读到的字体类型
		 */
		if (mTypeface.containsKey(fontName)) {
			//Log.d("SKScene", "存在 fontName:"+fontName);
			return mTypeface.get(fontName);
		}
		
		String ttfName = biz.findTtfName(fontName);
		Typeface myTypeface = Typeface.DEFAULT;
		try{
			if (!ttfName.equals("")) {
				//Log.d("SKScene", "fontName data");
				myTypeface = Typeface.createFromFile("/data/data/com.android.Samkoonhmi/fonts/"+ttfName);
			    if (myTypeface!=null) {
					mTypeface.put(fontName, myTypeface);
				}
			}
		}catch(Exception e)
		{
			myTypeface = Typeface.DEFAULT;
			mTypeface.put(fontName, myTypeface);
			//e.printStackTrace();
		}
		 return myTypeface;
	}

	//用来存字体类型，防止平凡读取数据库
	private static HashMap<String, Typeface> mTypeface=new HashMap<String, Typeface>();

}
