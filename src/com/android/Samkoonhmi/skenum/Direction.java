package com.android.Samkoonhmi.skenum;

public class Direction {

	public enum DIRECTION {
		LEVEL,			  //水平
		VERTICAL,		  //垂直
		TOWARD_LEFT,      //向左
		TOWARD_RIGHT,     //向右
		TOWARD_TOP,       //向上
		TOWARD_BOTTOM,    //向下
		TO_LEFT_ROTATE,   //向左旋转
		TO_RIGHT_ROTATE,  //向右旋转
		NO_MOVE           //静止
	}
	
	public static DIRECTION getDirection(int id){
		switch (id) {
		case 1:
			return DIRECTION.LEVEL;
		case 2:
			return DIRECTION.VERTICAL;
		case 3:
			return DIRECTION.TOWARD_LEFT;
		case 4:
			return DIRECTION.TOWARD_RIGHT;
		case 5:
			return DIRECTION.TOWARD_TOP;
		case 6:
			return DIRECTION.TOWARD_BOTTOM;
		case 7:
			return DIRECTION.TO_LEFT_ROTATE;
		case 8:
			return DIRECTION.TO_RIGHT_ROTATE;
		case 9:
			return DIRECTION.NO_MOVE;
		}
		return DIRECTION.LEVEL;
	}
}
