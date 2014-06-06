package com.android.Samkoonhmi.model;


/**
 * 控件属性接口
 */
public interface IItem {

	/**
	 * 返回控件左边界
	 * @param id-控件id
	 */ 
	public int getItemLeft(int id);
	
	/**
	 * 返回控件顶边界
	 * @param id-控件id
	 */ 
	public int getItemTop(int id);
	
	/**
	 * 返回控件的宽度
	 * @param id-控件id
	 */ 
	public int getItemWidth(int id);
	
	/**
	 * 返回控件的高度
	 * @param id-控件id
	 */ 
	public int getItemHeight(int id);
	
	/**
	 * 返回控件的前景色，RGB
	 * @param id-控件id
	 */ 
	public short[] getItemForecolor(int id);
	
	/**
	 * 返回控件的背景色，RGB
	 * @param id-控件id
	 */ 
	public short[] getItemBackcolor(int id);
	
	/**
	 * 返回控件的线颜色，RGB
	 * @param id-控件id
	 */ 
	public short[] getItemLineColor(int id);
	
	/**
	 * 返回控件的显现属性，true-可见，false-不可见
	 * @param id-控件id
	 */ 
	public boolean getItemVisible(int id);
	
	/**
	 * 返回控件的触控属性，true-可触摸，false-不可触摸
	 * @param id-控件id
	 */ 
	public boolean getItemTouchable(int id);
	
	/**
	 * 设置控件的左边界
	 * @param id-控件id
	 */ 
	public boolean setItemLeft(int id,int x);
	
	/**
	 * 设置控件的顶边界
	 * @param id-控件id
	 */ 
	public boolean setItemTop(int id,int y);
	
	/**
	 * 设置控件的宽度
	 * @param id-控件id
	 * @param w-宽度，单位像素
	 */ 
	public boolean setItemWidth(int id,int w);
	
	/**
	 * 设置控件的高度
	 * @param id-控件id
	 * @param h-高度，单位像素
	 */ 
	public boolean setItemHeight(int id,int h);
	
	/**
	 * 设置前景色
	 * @param id-控件id
	 * @param r
	 * @param g
	 * @param b
	 */ 
	public boolean setItemForecolor(int id,short r,short g,short b);
	
	/**
	 * 设置背景色
	 * @param id-控件id
	 * @param r
	 * @param g
	 * @param b
	 */ 
	public boolean setItemBackcolor(int id,short r,short g,short b);
	
	/**
	 * 设置线颜色
	 * @param id-控件id
	 * @param r
	 * @param g
	 * @param b
	 */ 
	public boolean setItemLineColor(int id,short r,short g,short b);
	
	/**
	 * 设置控件显现属性
	 * @param id-控件id
	 * @param v-true 显示，v-false 隐藏
	 */ 
	public boolean setItemVisible(int id,boolean v);
	
	/**
	 * 设置控件触控属性
	 * @param id-控件id
	 * @param v-true 可触摸，v-false 不可触摸
	 */ 
	public boolean setItemTouchable(int id,boolean v);
	
	/**
	 * 向上翻页
	 * @param id-控件id
	 */ 
	public boolean setItemPageUp(int id);
	
	/**
	 * 向下翻页
	 * @param id-控件id
	 */ 
	public boolean setItemPageDown(int id);
	
	/**
	 * 闪烁
	 * @param id-控件id
	 * @param v-true 闪烁 ，v-false 不闪烁
	 * @param time 闪烁间隔时间，单位100ms
	 * 
	 */ 
	public boolean setItemFlick(int id,boolean v,int time);
	
	/**
	 * 设置控件水平移动
	 * @param id-控件id
	 * @param w>0 向右移动，w<0 向左移动，单位1个像素
	 */ 
	public boolean setItemHroll(int id,int w);
	
	/**
	 * 设置控件垂直移动
	 * @param id-控件id
	 * @param h>0 向下移动，h<0 向上移动，单位1个像素
	 */ 
	public boolean setItemVroll(int id,int h);
	
	/**
	 * 设置GIF状态
	 * @param id-控件id
	 * @param v-true 运行，v-false 停止
	 */ 
	public boolean setGifRun(int id,boolean v);
	
	/**
	 * 设置控件文本
	 * @param id-控件id
	 * @param text-文本内容
	 */ 
	public boolean setItemText(int id,int lid,String text);
	
	/**
	 * 设置控件透明度
	 * @param id-控件id
	 * @param alpha-[0,255]
	 */ 
	public boolean setItemAlpha(int id,int alpha);
	
	/**
	 * 设置控件颜色
	 * @param id-控件id
	 * @param style 颜色
	 */ 
	public boolean setItemStyle(int id,int style);
}
