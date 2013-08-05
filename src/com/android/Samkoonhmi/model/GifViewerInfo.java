package com.android.Samkoonhmi.model;
import java.util.ArrayList;
import android.util.Log;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * GIF显示器数据实体类
 * @author 魏 科
 * @date   2012-06-11
 * */
public class GifViewerInfo {
	
	//布局属性
	 private int nItemId;       //控件Id
	 private short  nLp;        //控件左上角X坐标
	 private short  nTp;        //控件左上角Y坐标
	 private short  nWidth;     //控件宽度
	 private short  nHeigth;    //控件高度
	 private short  nGifWidth;  //原始GIF图片宽度
	 private short  nGifHeight; //原始Gif图片高度 
	 
	 private short  nZvalue;    //控件层序号
	 
	 private int   nCollidindId;     //组合ID 
	  
	 private int    nBackColor;      //背景色
	 
	 private String sGifPath;        //GIF动画文件路径
	 
	 private short     nIsBitCtrl;  //是否受位控制
	 private int     nRCount = -1;     //帧组执行的次数
	 private short     nValidBit;   //有效位
	 private AddrProp  mCtrlAddr;
	 
	 
	 //显现
	 private ShowInfo mShowInfo;    //显现属性
	 
	 /**
	  * 设置控制有效位的值
	  * */
     public void setRCount(int v){
    	 
    	 this.nRCount = v;
     }
     
     /**
      * 获得控制有效位的值
      * */
     public int getRCount(){
    	 return this.nRCount;
     }
	 
	 
	 /**
	  * 设置GIF是否受位控制
	  * */
     public void setIsBitCtrl(short is){
    	 
    	 if(1 != is && 0 != is){
    		 Log.e("GifViewerInfo","setIsBitCtrl:invalid is, set to default 0");
    		 is = 0;
    	 }
    	 this.nIsBitCtrl = is;
     }
     
     /**
      * 获得GIF是否受位控制
      * */
     public short getIsBitCtrl(){
    	 return this.nIsBitCtrl;
     }
     
	 /**
	  * 设置控制有效位的值
	  * */
     public void setValidBit(short v){
    	 
    	 this.nValidBit = v;
     }
     
     /**
      * 获得控制有效位的值
      * */
     public short getValidBit(){
    	 return this.nValidBit;
     }
     
	 /**
	  * 设置位控制地址
	  * */
	 public void setCtrlAddr(AddrProp addrprop){
		 if(null ==  addrprop){
			 Log.e("GifViewerInfo","setCtrlAddr: addrprop is null");
			 return;
		 }
		 this.mCtrlAddr = addrprop;
	 }
	 
	 /**
	  * 获得位控制地址
	  * */
	 public AddrProp getCtrlAddr(){
		 return this.mCtrlAddr;
	 }
	 
	 
	 /**
	  * 设置控件左上角X坐标
	  * */
	 public void setLp(short Lp){
		 if(0 < Lp){
			 this.nLp = Lp;
		 }
	 }
	 
	 /**
	  * 获得控件左上角X坐标
	  * */
	 public short getLp(){
		 return this.nLp;
	 }

	 /**
	  *  设置控件左上角y坐标
	  * */
	 public void setTp(short Tp){
		 if(0 < Tp){
			 this.nTp = Tp;
		 }
	 }
	 
	 /**
	  *  获得控件左上角y坐标
	  * */
	 public short getTp(){
		return this.nTp;
	 }
	 
	 /**
	  * 设置控件宽度
	  * */
	 public void setWidth(short width){
		 if(0 < width){
			 this.nWidth = width;
		 }
	 }
	 
	 /**
	  * 获得控件宽度
	  * */
	 public short getWidth(){
		 return this.nWidth;
	 }
	 
	 /**
	  * 设置原始GIF图片宽度
	  * */
	 public void setGifWidth(short width){
		 if(0 < width){
			 this.nGifWidth = width;
		 }
	 }
	 
	 /**
	  * 获得原始GIF图片宽度
	  * */
	 public short getGifWidth(){
		 return this.nGifWidth;
	 }
	 
	 
	 /**
	  * 设置控件高度
	  * */
	 public void setHeight(short height){
		 if(0 < height){
			 this.nHeigth = height;
		 }
	 }
	 
	 /**
	  * 获得控件高度
	  * */
	 public short getHeight(){
		return this.nHeigth;
	 }
	 
	 /**
	  * 获得原始GIF图片高度
	  * */
	 public short getGifHeight(){
		return this.nGifHeight;
	 }
	 
	 /**
	  * 设置原始GIF图片高度
	  * */
	 public void setGifHeight(short height){
		 if(0 < height){
			 this.nGifHeight = height;
		 }
	 }
	 
	 /**
	  * 设置背景颜色
	  * */
	 public void setBackColor(int color){
		 this.nBackColor = color;
	 }
	 
	 /**
	  * 获得背景颜色
	  * */
	 public int getBackColor(){
		 return this.nBackColor;
	 }
	 
	 /**
	  * 设置GIF动画文件路径
	  * */
	 public void setGifPath(String path){
		 this.sGifPath = path;
	 }
	 
	 /**
	  * 获得GIF动画文件的路径
	  * */
	 public String getGifPath(){
		 
		 return this.sGifPath;
		 
	 }
	 /**
	  * 设置层序号
	  * */
	 public void setZvalue(short zvalue){
		 this.nZvalue = zvalue;
	 }
	 
	 /**
	  * 获得层序号
	  * */
	 public short getZValue(){
		 return nZvalue;
	 }
	 
	 /**
	  * 设置组合ID
	  * */
	 public void setCollidindId(int cid){
		 this.nCollidindId = cid;
	 }
	 
	 /**
	  * 获得组合ID
	  * */
	 public int getCollidindId(){
		 return this.nCollidindId;
	 }
	 
	 /**
	  * 获得显现属性
	  * */
	 public ShowInfo getmShowInfo() {
		 return mShowInfo;
	 }
	 /**
	  * 设置显现属性
	  * */
	 public void setmShowInfo(ShowInfo mShowInfo) {
		 this.mShowInfo = mShowInfo;
	 }
	 
	public int getnItemId() {
		return nItemId;
	}

	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
}//End of: class