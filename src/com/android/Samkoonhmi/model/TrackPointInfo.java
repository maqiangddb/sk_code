package com.android.Samkoonhmi.model;

import android.util.Log;

/**
 * 轨迹点信息
 * @author 魏 科
 * @date   2012-06-12
 * */
public class TrackPointInfo {
     
	 private short nID;    //轨迹点序号
     private short nXPos;  //轨迹点X坐标
     private short nYPos;  //轨迹点Y坐标
     
     /**
      * 设置轨迹点X坐标
      * */
     public void setXPos(short x){
    	 if(0>x){
    		 Log.e("TrackPointInfo","setXPos: Invalid x value");
    		 return;
    	 }
         this.nXPos = x;	 
     }
     
     /**
      * 获得轨迹点X坐标
      * */
     public short getXPos(){
    	 return this.nXPos;
     }
     
     /**
      * 设置轨迹点Y坐标
      * */
     public void setYPos(short y){
    	 if(0>y){
    		 Log.e("TrackPointInfo","setYPos: Invalid y value");
    		 return;
    	 }
         this.nYPos = y;	 
     }
     
     /**
      * 获得轨迹点Y坐标
      * */
     public short getYPos(){
    	 return this.nYPos;
     }
     
     /**
      * 设置轨迹点关联的轨迹序号
      * */
     public void setID(short tid){
    	 if(0>tid){
    		 Log.e("TrackPointInfo","setID: Invalid tid");
    		 return;
    	 }
    	 this.nID = tid;
     }
     
     /**
      * 获得轨迹点关联的ID
      * */
     public short getID(){
    	 return this.nID;
     }  
}//End of class
