package com.android.Samkoonhmi.model;

/**
 * 表格加载数据信息
 * @author 刘伟江
 * @since 2013-1-22
 */
public class TableLoadInfo {

	/**
	 * 起始行id
	 */
	public int nRowIndex;
	
	/**
	 * 结束行id
	 */
	public int nEndIndex;
	
	/**
	 * 加载类型
	 * 0=初始化
	 * 1=滑动
	 * 2=点击顶部按钮
	 * 3=点击底部按钮
	 */
	public int nLoadType;
	
	/**
	 * 加载条数
	 */
	public int nLoadCount;
	
	/**
	 * 重新读取
	 */
	public boolean bUpdate;
	
	
}
