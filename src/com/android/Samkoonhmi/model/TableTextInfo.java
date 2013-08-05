package com.android.Samkoonhmi.model;

/**
 * 表格内容
 * @author 刘伟江
 * 创建时间 2012-8-6
 */
public class TableTextInfo {

	public int index;      //序号
	public int nPage;      //第几页
	public int nRow;       //第几个行，从0开始
	public int nColum;     //第几列，从0开始
	public String sText;   //文本信息
	public boolean click;  //是否可以点击
	public int id;         //id
	public int nClickCount;//点击次数
	public String tag;     //列的类型
}
