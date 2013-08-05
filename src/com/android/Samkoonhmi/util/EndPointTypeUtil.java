package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.skenum.END_POINT_TYPE;

import android.graphics.Paint.Join;

public class EndPointTypeUtil {
	public static Join getJoin(END_POINT_TYPE JoinStyleIndex)
	{
		Join join=null;
		switch(JoinStyleIndex)
		{
		case ROUND_CAP:
			//圆角
			join=Join.ROUND;
			break;
		case FLAT_CAP:
			//直角
			join=Join.MITER;
			break;
		case SQUARE_CAP:
			//截角
			join=Join.BEVEL;
			break;
		default:
				join=Join.MITER;
				break;
		}
		return join;
	}
}
