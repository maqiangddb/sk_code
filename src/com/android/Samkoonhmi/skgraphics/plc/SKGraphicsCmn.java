//import SKGraphics;
//import boolean;
//import uchar;
package com.android.Samkoonhmi.skgraphics.plc;

import com.android.Samkoonhmi.skgraphics.SKGraphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 与plc通讯的控件
 * @author Administrator
 *
 */
public abstract  class SKGraphicsCmn extends SKGraphics{

	protected int m_statusDataType;

	   public void SKGraphicsCmn(){
	        // TODO put your implementation here.	
	    } 

    public void addrNoticShow(boolean bShow){
        // TODO put your implementation here.	
    }
	
    public void userLevelNoticShow(boolean bShow){
        // TODO put your implementation here.	
    }
	
    public void setShowAddr(){
        // TODO put your implementation here.	
    }
	
    public void addrNoticStatus(double nStatus){
        // TODO put your implementation here.	
    }
	
    public void setStatusAddr(){
        // TODO put your implementation here.	
    }
	
    public <uchar> boolean doubleToUcharArray(Double nWordArray, uchar valueArray){
		return false;
        // TODO put your implementation here.	
    }
	
    public <uchar> boolean ucharArrayToDouble(uchar valueArray, Double nWordArray){
		return false;
        // TODO put your implementation here.	
    }
	
}