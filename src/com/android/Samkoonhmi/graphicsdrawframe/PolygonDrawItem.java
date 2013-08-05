//import GraphicsDrawBase;
package com.android.Samkoonhmi.graphicsdrawframe;

import com.android.Samkoonhmi.skenum.CSS_TYPE;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class PolygonDrawItem extends GraphicsDrawBase{
    private int m_nForeColor;
    private int m_nBackColor;

    private CSS_TYPE m_nStyle=CSS_TYPE.CSS_SOLIDCOLOR;

    public PolygonDrawItem(){
        // TODO put your implementation here.	
    	this.m_nForeColor=Color.BLACK;
    	this.m_nBackColor=Color.WHITE;
    	this.m_nStyle=CSS_TYPE.CSS_SOLIDCOLOR;
    }
	
    public void setAlpha(int nAlpha){
        // TODO put your implementation here.	
    	super.setAlpha(nAlpha);
    	
    }
	
    public int getAlpha(){
		return super.getAlpha();
        // TODO put your implementation here.	
    }
	
    public void setForeColor(int nForeColor){
        // TODO put your implementation here.	
    	this.m_nForeColor=nForeColor;
    }
	
    public int getForeColor(){
		return m_nForeColor;
        // TODO put your implementation here.	
    }
	
    public void setBackColor(int nBackColor){
        // TODO put your implementation here.	
    	this.m_nBackColor=nBackColor;
    }
	
    public int getBackColor(){
		return m_nBackColor;
        // TODO put your implementation here.	
    }
	
    public void setStyle(CSS_TYPE nStyle){
        // TODO put your implementation here.	
    	this.m_nStyle=nStyle;
    }
	
    public CSS_TYPE getStyle(){
		return m_nStyle;
        // TODO put your implementation here.	
    }
	
    public void draw(Paint paint,Canvas canvas){
        // TODO put your implementation here.	
    }
	
}