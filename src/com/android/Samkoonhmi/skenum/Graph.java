package com.android.Samkoonhmi.skenum;

/**
 * 图表
 */
public class Graph {

	/**
	 * 图表类型
	 */
	public enum GRAPH_TYPE {

		COMMON,    //普通
		STATISTICS,//统计
		METER      //仪表
	}
	
	/**
	 * 外形枚举
	 */
	public enum SHAPE_TYPE{
		PILLA,  //柱形
		CIRCLE, //圆
		SECTOR, //半圆
		GROOVE  //槽
	};
	
	/**
	 * 图表类型
	 */
	public static SHAPE_TYPE getShapeType(int id){
		switch (id) {
		case 1:
			return SHAPE_TYPE.PILLA;
		case 2:
			return SHAPE_TYPE.CIRCLE;
		case 3:
			return SHAPE_TYPE.SECTOR;
		case 4:
			return SHAPE_TYPE.GROOVE;
		}
		return SHAPE_TYPE.PILLA;
	}
	
	/**
	 * 外形枚举
	 */
	public static GRAPH_TYPE getGraphType(int id){
		switch (id) {
		case 1:
			return GRAPH_TYPE.COMMON;
		case 2:
			return GRAPH_TYPE.STATISTICS;
		case 3:
			return GRAPH_TYPE.METER;
		}
		return GRAPH_TYPE.COMMON;
	}
}
