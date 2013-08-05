package com.android.Samkoonhmi.util;

import android.graphics.DashPathEffect;
import android.graphics.PathEffect;

import com.android.Samkoonhmi.skenum.LINE_TYPE;

/**
 * 线的类型item
 * @author Administrator
 *
 */
public class LineTypeUtil {

	public static  PathEffect getPathEffect(LINE_TYPE styleIndex,int lineWidth) {
		// TODO Auto-generated method stub
		PathEffect effect=null;
		float phase = 0;
		switch(styleIndex)
		{
		case SOLID_LINE: //默认的直线
			effect=null;
			break;
		case DASH_LINE:  //小短横虚线
			effect=new DashPathEffect(new float[]{5*lineWidth,8f}, phase);
			break;
		case DOT_LINE:  //点虚线
			effect=new DashPathEffect(new float[]{2*lineWidth,6f}, phase);
			break;
		case DASH_DOT_LINE: //横点虚线
			effect=new DashPathEffect(new float[]{5*lineWidth,8,2*lineWidth,9}, phase);
			break;
		case DASH_DOT_DOT_LINE:  //横点点横虚线
			 effect=new DashPathEffect(new float[]{5*lineWidth,8,2*lineWidth,8,2*lineWidth,9}, phase);
			break;
		default:
			break;
		}
		return effect;
	}
}
