package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.skenum.END_POINT_TYPE;
//import Point;
//import PolygonDrawItem;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class SquareItem extends PolygonDrawItem{
    private Rect m_mRect;
    private END_POINT_TYPE type;

    public SquareItem(Rect mRect){
        // TODO put your implementation here.	
    	m_mRect=mRect;
    	this.type=END_POINT_TYPE.FLAT_CAP;
    }
	
    public void setType(END_POINT_TYPE rectType){
        // TODO put your implementation here.	
    	this.type=rectType;
    }
	
    public END_POINT_TYPE getType(){
		return type;
        // TODO put your implementation here.	
    }
	
    public Rect getRect(){
		return m_mRect;
        // TODO put your implementation here.	
    }
	
    public void setRect(Rect mRect){
        // TODO put your implementation here.	
    	this.m_mRect=mRect;
    			
    }
	
    public int getWidth(){
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
		return m_mRect.width();
        // TODO put your implementation here.	
    }
	
    public void setWidth(int nWidth){
        // TODO put your implementation here.	
    	if(nWidth < 0) return ;
    	
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
    	m_mRect.set(m_mRect.left, m_mRect.top, m_mRect.left + nWidth, m_mRect.bottom);
    }
	
    public int getHeight(){
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
		return m_mRect.height();
        // TODO put your implementation here.	
    }
	
    public void setHeight(int nHeight){
        // TODO put your implementation here.
    	if(nHeight < 0) return ;
    	
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
    	m_mRect.set(m_mRect.left, m_mRect.top, m_mRect.right, m_mRect.top + nHeight);
    }
	
    public int getTopPos(){
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
		return m_mRect.top;
        // TODO put your implementation here.	
    }
	
    public void setTopPos(int mPos){
        // TODO put your implementation here.	
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
    	m_mRect.set(m_mRect.left, mPos, m_mRect.right, mPos + m_mRect.height());
    }
    
    public int getLeftPos(){
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
		return m_mRect.left;
        // TODO put your implementation here.	
    }
    
    public void setLeftPos(int mPos){
        // TODO put your implementation here.	
    	if(null == m_mRect)
    	{
    		m_mRect = new Rect();
    	}
    	m_mRect.set(mPos, m_mRect.top, mPos + m_mRect.width(), m_mRect.bottom);
    }
	
    public void setTopLeftPos(Point mPos){
        // TODO put your implementation here.
    	if(null == mPos) return ;
    	
    	m_mRect.set(mPos.x, mPos.y, mPos.x + m_mRect.width(), mPos.y + m_mRect.height());
    }
	
    public void draw(Paint paint,Canvas canvas){
        // TODO put your implementation here.	
    }
	
}