package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.skenum.CSS_TYPE;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class FillRender {

	public   Shader mShader = null;
	private  float startX;
	private  float startY;
	private  float stopX;
	private  float stopY;
	public FillRender(){
		
	}	
	public Shader getRenderShader(){
		return mShader;
	}
	
	/**
	 * 
	 * @param css_index 样式索引
	 * @param x1 起点横坐标
	 * @param y1 起点纵坐标
	 * @param x2 终点横坐标
	 * @param y2 终点纵坐标
	 * @param forceColor 前景色
	 * @param backColor  背景色
	 * @return
	 */
	public  Shader setRectCss(CSS_TYPE type,float x1,float y1,float x2,float y2,int forceColor,int backColor)
	{
		if(null == type)
		{
			return null;
		}
		switch (type) {
		case CSS_ORIENTATION:
			//横向过度
			startX=x1;
			startY=(y1+y2)/2;
			stopX=x2;
			stopY=(y1+y2)/2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);		
			break;
		case CSS_ORIENTATION_SYMMETRY:
			//横向对称过度
			startX=x1;
			startY=(y1+y2)/2;
			stopX=x2;
			stopY=(y1+y2)/2;
			mShader=new LinearGradient(startX, startY, stopX, stopY,new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			break;
		case CSS_PORTRAIT:
			//纵向过度
			startX=(x1+x2)/2;
			startY=y1;
			stopX=(x1+x2)/2;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_PORTRAIT_SYMMETRY:
			//纵向对称过度
			startX=(x1+x2)/2;
			startY=y1;
			stopX=(x1+x2)/2;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			break;
		case CSS_TIP_UP:
			//斜上过度
			startX=x1;
			startY=y1;
			stopX=x2;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_TIP_UP_SYMMETRY:
			//斜上对称渐变
			startX=x1;
			startY=y1;
			stopX=x2;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			 break;
		case CSS_TIP_DOWN:
			//斜下过度
			startX=x2;
			startY=y1;
			stopX=x1;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_TIP_DOWN_SYMMETRY:
			//斜下对称渐变
			startX=x2;
			startY=y1;
			stopX=x1;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			break;
		case CSS_RIGHTCORNER_ERADIATE:
			//右上角辐射
			startX=x2;
			startY=y1;
			stopX=x1;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, backColor, forceColor,Shader.TileMode.MIRROR);
			break;
		case CSS_LEFTCORNER_ERADIATE:
			//左上角辐射
			startX=x1;
			startY=y1;
			stopX=x2;
			stopY=y2;
			mShader=new LinearGradient(startX, startY, stopX, stopY, backColor, forceColor,Shader.TileMode.MIRROR);
			break;
		case CSS_CENTER_ERADIATE:
			//中心辐射
			startX=(x1+x2)/2;
			startY=(y1+y2)/2;
			stopX=(x2-x1)/2;
			stopY=(y2-y1)/2;
			//stopY=15;//当宽度很短，高度很长时，下位与上位的中心辐射样式不能匹配，所以下位给了定值
			float radius=0;
			if(stopX>stopY)
			{
				radius=stopY;
			}
			else 
			{
				radius=stopX;
			}		
			mShader=new RadialGradient(startX, startY, radius, backColor, forceColor, Shader.TileMode.CLAMP);
			break;
		default:
			mShader=null;
			break;
		}
		return mShader;
	}
	/**
	 * @param css_index 样式类型索引
	 * @param x 圆心x
	 * @param y 圆心y
	 * @param radius 半径
	 * @param forceColor 前景色
	 * @param backColor  背景色
	 * @return
	 */
	public  Shader circle_css(CSS_TYPE css_index,float x,float y,int radius,int forceColor,int backColor)
	{
		
		float sinNum=(float) Math.sin(45);
		switch (css_index) {
		case CSS_ORIENTATION:
			//横向过度
			 startX=x-radius;
			startY=y;
			stopX=x+radius;
			 stopY=y;
			mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_ORIENTATION_SYMMETRY:
			//横向对称过度
			 startX=x-radius;
			startY=y;
			 stopX=x+radius;
			 stopY=y;
			 mShader=new LinearGradient(startX, startY, stopX, stopY,new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			break;
		case CSS_PORTRAIT:
			//纵向过度
			startX=x;
			 startY=y-radius;
			 stopX=x;
			 stopY=y+radius;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_PORTRAIT_SYMMETRY:
			//纵向对称过度
			 startX=x;
			 startY=y-radius;
			 stopX=x;
			 stopY=y+radius;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			break;
		case CSS_TIP_UP:
			//斜上过度
			startX=x-radius*sinNum;
			 startY=y-radius*sinNum;
			stopX=x+radius*sinNum;
			 stopY=y+radius*sinNum;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_TIP_UP_SYMMETRY:
			//斜上对称渐变
			startX=x-radius*sinNum;
			 startY=y-radius*sinNum;
			stopX=x+radius*sinNum;
			 stopY=y+radius*sinNum;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			 break;
		case CSS_TIP_DOWN:
			//斜下过度
			startX=x+radius*sinNum;
			 startY=y-radius*sinNum;
			stopX=x-radius*sinNum;
			
			 stopY=y+radius*sinNum;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, forceColor, backColor,Shader.TileMode.MIRROR);
			break;
		case CSS_TIP_DOWN_SYMMETRY:
			//斜下对称渐变
			 startX=x+radius*sinNum;
			 startY=y-radius*sinNum;
			stopX=x-radius*sinNum;
			 stopY=y+radius*sinNum;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, new int []{forceColor,backColor,backColor,forceColor},new float []{0,0.5f,0.5f,1f},Shader.TileMode.MIRROR);
			break;
		case CSS_RIGHTCORNER_ERADIATE:
			//右上角辐射
			 startX=x+radius*sinNum;
			 startY=y-radius*sinNum;
			stopX=x-radius*sinNum;
			 stopY=y+radius*sinNum;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, backColor, forceColor,Shader.TileMode.MIRROR);
			break;
		case CSS_LEFTCORNER_ERADIATE:
			//左上角辐射
			startX=x-radius*sinNum;
			 startY=y-radius*sinNum;
			stopX=x+radius*sinNum;
			 stopY=y+radius*sinNum;
			 mShader=new LinearGradient(startX, startY, stopX, stopY, backColor, forceColor,Shader.TileMode.MIRROR);
			break;
		case CSS_CENTER_ERADIATE:
			//中心辐射
			mShader=new RadialGradient(x, y, radius, backColor, forceColor, Shader.TileMode.MIRROR);
			break;
		default:
			mShader=null;
			break;
		}
		return mShader;
	}
}
