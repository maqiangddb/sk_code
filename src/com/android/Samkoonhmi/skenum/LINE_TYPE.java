package com.android.Samkoonhmi.skenum;

/**
 * 线类型枚举
 * @author Administrator
 *
 */
public enum LINE_TYPE{
    NO_PEN,            //不显示的线 即线的颜色为透明
    SOLID_LINE,        //默认的直线
    DASH_LINE,         //小短横虚线类型
    DOT_LINE,          //点虚线类型
    DASH_DOT_LINE,     //小短横 ——点类型
    DASH_DOT_DOT_LINE, //小短横——点——点——小短横 类型
    CUSTOM_DASH_LINE
}