package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.skenum.EXPRESS_SIGN;

public class Expression {

	// 单例
	private static Expression sInstance = null;
	public synchronized static Expression getInstance() {
		if (sInstance == null) {
			sInstance = new Expression();
		}
		return sInstance;
	}

	public double evaluate(double p1,double p2,EXPRESS_SIGN type){
		double result=0;
		switch (type) {
		case ADD:
			//加
			result=p1+p2;
			break;
		case REDUCE:
			//减
			result=p1-p2;
			break;
		case MULTIPLY:
			//乘
			result=p1*p2;
			break;
		case DIVIDE:
			//除
			if (p2!=0) {
				result=p1/p2;
			}else {
				result=0;
			}
			break;
		case XOR:
			//异或
			result=(int)p1^(int)p2;
			break;
		case MOD:
			//取余数
			result=p1%p2;
			break;
		case AND:
			//与
			result=(int)p1&(int)p2;
			break;
		case OR:
			//非
			result=(int)p1|(int)p2;
			break;
		case LEFT:
			//左移
			result=(int)p1<<(int)p2;
			break;
		case RIGHT:
			//右移
			result=(int)p1>>(int)p2;
			break;
		}
		return result;
	}
}
