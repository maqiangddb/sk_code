package com.android.Samkoonhmi.model;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 状态图片缓存结构
 * @author 魏 科
 * @date   2012-06-04
 * */
public class StateBmpCache {
	
	private short  nStatusId; //关联的状态号
	
	private Bitmap mImg;      //图片缓存
	
	/**
	 * 设置状态号
	 * */
	public void setStatusID(short sid){
		if(0 > sid){
			Log.e("StateBmpCache","setStatusID: sid is null");
			return;
		}
		this.nStatusId = sid;
	}
	
	/**
	 * 获得状态号
	 * */
	public short getStatusID(){
		return this.nStatusId;
	}
	
	/**
	 * 设置位图缓存
	 * */
	public void cacheBitmap(Bitmap img){
		if(null == img){
			Log.e("StateBmpCache","cacheBitmap: img is null");
			return;
		}
		this.mImg = img;
	}
	
	/**
	 * 获得图片缓存
	 * */
	public Bitmap getBitmap(){
		return this.mImg;
	}

}
