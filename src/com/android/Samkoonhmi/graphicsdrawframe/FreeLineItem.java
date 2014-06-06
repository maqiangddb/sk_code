package com.android.Samkoonhmi.graphicsdrawframe;
import java.util.Vector;

import com.android.Samkoonhmi.skenum.END_ARROW_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.EndArrowTypeUtil;
import com.android.Samkoonhmi.util.EndPointTypeUtil;
import com.android.Samkoonhmi.util.LineTypeUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Paint.Join;
import android.graphics.PointF;

/**
 * 画自由直线
 * @author Administrator
 *
 */
public class FreeLineItem extends LinearDrawItem{
//	private Vector<Point> m_pointList;
	private Path path;
	private EndArrowTypeUtil mTypeUtil;
	
	/**
	 * @param fpointList-点集合
	 * @param type-箭头
	 */
    public FreeLineItem(Vector<PointF > fpointList,END_ARROW_TYPE type){
        // TODO put your implementation here.	
    	this.setM_fpointList(fpointList);
    	if (type!=END_ARROW_TYPE.STYLE_NONE) {
    		mTypeUtil=new EndArrowTypeUtil();
		}
    }
	
    public void draw(Paint paint,Canvas canvas){
    	path=new Path();
        // TODO put your implementation here.	
    	if(null==this.getM_fpointList()||getM_fpointList().size()==0)
    	{
    		return ;
    	}
    	else{
    		//清空画笔
			paint.reset();
	    	//画自由直线
	    	init(paint);
	    	
    		if(2>this.getM_fpointList().size())
    		{
    			canvas.drawPoint(this.getM_fpointList().get(0).x,this.getM_fpointList().get(0).y, paint);
    		}else{
				path.moveTo(this.getM_fpointList().get(0).x,this.getM_fpointList().get(0).y);
				for(int i=1;i<this.getM_fpointList().size();i++)
				{
					path.lineTo(this.getM_fpointList().get(i).x,this.getM_fpointList().get(i).y);
				}
				canvas.drawPath(path, paint);
				
				//画箭头
				if (getEndArrowType()!=END_ARROW_TYPE.STYLE_NONE) {
					int pointSize=this.getM_fpointList().size();
					//最后一个端点的横坐标
					float lastX=this.getM_fpointList().get(pointSize-1).x;
					//最后一个端点的纵坐标
					float lastY=this.getM_fpointList().get(pointSize-1).y;
					//倒数第二个端点的横坐标
					float lastSecondX=this.getM_fpointList().get(pointSize-2).x;
					//倒数第二个端点的纵坐标
					float lastSecondY=this.getM_fpointList().get(pointSize-2).y;
					mTypeUtil.drawArrow(paint,canvas,lastSecondX,lastSecondY,lastX,lastY,getEndArrowType());
				}
			    //将path置空
			    path=null;
    		}
    	}
    }

    /**
	 * 初始化画笔
	 */
    private PathEffect effect;
	public void  init(Paint paint)
	{
		paint.setAntiAlias(true);// 去锯齿
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getLineColor()); // 线的颜色
		paint.setAlpha(getAlpha()); // 透明度
		paint.setStrokeWidth(getLineWidth()); // 线宽度
		if (getLineType() == LINE_TYPE.NO_PEN)// 不显示的线 即线的颜色为透明
		{
			paint.setColor(Color.TRANSPARENT);
		} else {
			if(null == effect)
			{
				effect= LineTypeUtil.getPathEffect(getLineType(),getLineWidth());
			}
			if (null != effect) {
				paint.setPathEffect(effect);// 设置线的样式
			}
		}
		Join join=EndPointTypeUtil.getJoin(getEndPointType());
		paint.setStrokeJoin(join);//设置端点的形状
	
	}
	
}