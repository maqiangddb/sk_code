package com.android.Samkoonhmi.graphicsdrawframe;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Typeface;

import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.util.EndPointTypeUtil;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.LineTypeUtil;
//import boolean;

public class GraphicsDrawBase{
 
    private int m_nAlpha;
      
    private LINE_TYPE m_eLineType;

    private int m_nLineWidth;
    
    private int m_nLineAlpha = -1;

    private int m_nLineColor;

    private int m_nGraphicsType;

    private int m_nItemId;

    private boolean m_bShow;

    public GraphicsDrawBase(){
        // TODO put your implementation here.	
    	this.m_bShow=true;
    	this.m_eLineType=LINE_TYPE.SOLID_LINE;
    	this.m_nAlpha=255;
    	this.m_nLineColor=Color.BLACK;
    	this.m_nLineWidth=1;
    
    }
    
    public void setLineAlpha(int a){
    	this.m_nLineAlpha = a;
    }
    
    public int getLineAlpha(){
    	return this.m_nLineAlpha;
    }
	
    protected void setGraphicsType(int nType){
        // TODO put your implementation here.	
    	this.m_nGraphicsType=nType;
    }
	
    public int getGraphicsType(){
		return m_nGraphicsType;
        // TODO put your implementation here.	
    }
	
    public int getItemId(){
		return m_nItemId;
        // TODO put your implementation here.	
    }
	
    public void setItemId(int nId){
        // TODO put your implementation here.	
    	this.m_nItemId=nId;
    }
	
    public void setAlpha(int nAlpha){
        // TODO put your implementation here.	
    	this.m_nAlpha=nAlpha;
    }
	
    public int getAlpha(){
		return m_nAlpha;
        // TODO put your implementation here.	
    }
	
    public void setLineType(LINE_TYPE nLineType){
        // TODO put your implementation here.	
    	this.m_eLineType=nLineType;
    	
    }
	
    public LINE_TYPE getLineType(){
		return m_eLineType;
        // TODO put your implementation here.	
    }
	
    public void setLineWidth(int nLineWidth){
        // TODO put your implementation here.	
    	this.m_nLineWidth=nLineWidth;
    	
    }
	
    public int getLineWidth(){
		return m_nLineWidth;
        // TODO put your implementation here.	
    }
	
    public void setLineColor(int nLineColor){
        // TODO put your implementation here.	
    	this.m_nLineColor=nLineColor;
    }
	
    public int getLineColor(){
		return m_nLineColor;
        // TODO put your implementation here.	
    }
	
    public void setVisible(boolean bShow){
        // TODO put your implementation here.	
    	this.m_bShow=bShow;
    }
	
    public void draw(Paint paint,Canvas canvas){
        // TODO put your implementation here.	
    }
    
    public void clearPaint(Paint clearPaint){
    	clearPaint.setAlpha(255); //透明度
    	clearPaint.setColor(-1677216);//默认的颜色
    	clearPaint.setStrokeWidth(1); //宽度
    	clearPaint.setTextSize(12); //字体的大小
    	clearPaint.setStyle(Paint.Style.FILL); //默认的样式
    	clearPaint.setPathEffect(LineTypeUtil.getPathEffect(LINE_TYPE.SOLID_LINE,1)); //默认的线条样式
    	clearPaint.setTextAlign(Align.LEFT); //字体的位置
    	clearPaint.setTypeface(Typeface.DEFAULT);//字体的类型
    	clearPaint.setStrokeJoin(EndPointTypeUtil.getJoin(END_POINT_TYPE.FLAT_CAP));//连接角的形状
    	clearPaint.setShader(null); //渲染
    	clearPaint.setUnderlineText(false); //下划线
    	clearPaint.setFakeBoldText(false); //true为粗体，false为非粗体
    	clearPaint.setTextSkewX(0); //float类型参数，负数表示右斜，整数左斜 0 不倾斜
    }
	
}