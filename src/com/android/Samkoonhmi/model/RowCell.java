package com.android.Samkoonhmi.model;

import java.util.Vector;

/**
 * 表格行信息
 */
public class RowCell {
	public int nRowIndex;//第几行
	public int nClounmCount;//列数
	public int width;//宽
	public int height;//高
	public int gid;//组id
	public int aid;//组里面子项id
	public int nRowColor;//行颜色
	public boolean nRowClick;//点击行
	public Vector<String> mClounm;
	public boolean isSetRowColor=false;
	public int nClickIndex=0;//点击时行的序号
	public int nClear=0;
	public long nAlarmTime;//报警时间
}
