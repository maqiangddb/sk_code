package com.android.Samkoonhmi.model;

import java.io.Serializable;

import com.android.Samkoonhmi.skgraphics.SKGraphics;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 刷新信息
 */
public class SKItems implements Serializable{
	/**
	 * 序列化id
	 */
	private static final long serialVersionUID = 1L;
	public int sceneId;          //控件属于哪个场景
	public int itemId;           //控件id
	public int nZvalue;          //层id
	public int nCollidindId;     //控件组合id
	public Rect rect;            //控件所在位置
	public Rect mMoveRect;       //控件移动后的位置，主要用于位置改变的控件
	public SKGraphics mGraphics; //控件
	public Canvas mCanvas;       //控件画布
}
