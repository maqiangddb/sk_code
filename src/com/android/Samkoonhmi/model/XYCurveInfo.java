package com.android.Samkoonhmi.model;

import java.util.ArrayList;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;


/**
 * XY 曲线数据对象
 */
public class XYCurveInfo {

	private int nId;
	private int nItemId;
	private int nSceneId;
	private int nTopLeftX;
	private int nTopLeftY;
	private int nWidth;
	private int nHeight;
	private int nCurveX;
	private int nCurveY;
	private int nCurveWidth;
	private int nCurveHeight;
	private int nSampleCount;
	private int nChannelNum;
	private AddrProp mControlAddr;
	private boolean bAutoReset;
	private DATA_TYPE eDataType;
	private AddrProp mXShowMinAddr;
	private AddrProp mXShowMaxAddr;
	private AddrProp mYShowMinAddr;
	private AddrProp mYShowMaxAddr;
	private int nXShowType;
	private int nYShowType;
	private boolean bScale;
	private int nXScaleType;
	private int nYScaleType;
	private AddrProp mXTargetMinAddr;
	private AddrProp mXTargetMaxAddr;
	private AddrProp mYTargetMinAddr;
	private AddrProp mYTargetMaxAddr;
	private double nXShowMin;
	private double nXShowMax;
	private double nYShowMin;
	private double nYShowMax;
	private double nXTargetMin;
	private double nXTargetMax;
	private double nYTargetMin;
	private double nYTargetMax;
	private boolean bShowVScale;
	private boolean bShowHScale;
	private boolean bShowVMinorScale;
	private boolean bShowHMinorScale;
	private int nVMajorNum;
	private int nHMajorNum;
	private int nVMinorNum;
	private int nHMinorNum;
	private int nBoradColor;
	private int nScaleColor;
	private int nVNetColor;
	private int nHNetColor;
	private int nGraphColor;
	private boolean bShowNet;
	private int nFontSize;
	private int nFontColor;
	private ShowInfo mShowInfo;
	private int nZvalue;
	private int nCollidindId;
	private ArrayList<ChannelGroupInfo> mChannelList;
	
	/**
	 * 表格序号
	 */
	public int getnId() {
		return nId;
	}
	
	/**
	 * 表格序号
	 */
	public void setnId(int nId) {
		this.nId = nId;
	}
	
	/**
	 * 控件id
	 */
	public int getnItemId() {
		return nItemId;
	}
	
	/**
	 * 控件id
	 */
	public void setnItemId(int nItemId) {
		this.nItemId = nItemId;
	}
	
	/**
	 * 场景id
	 */
	public int getnSceneId() {
		return nSceneId;
	}

	/**
	 * 场景id
	 */
	public void setnSceneId(int nSceneId) {
		this.nSceneId = nSceneId;
	}

	/**
	 * 控件左顶点X坐标
	 */
	public int getnTopLeftX() {
		return nTopLeftX;
	}
	
	/**
	 * 控件左顶点X坐标
	 */
	public void setnTopLeftX(int nTopLeftX) {
		this.nTopLeftX = nTopLeftX;
	}
	
	/**
	 * 控件左顶点Y坐标
	 */
	public int getnTopLeftY() {
		return nTopLeftY;
	}
	
	/**
	 * 控件左顶点Y坐标
	 */
	public void setnTopLeftY(int nTopLeftY) {
		this.nTopLeftY = nTopLeftY;
	}
	
	/**
	 * 控件宽度
	 */
	public int getnWidth() {
		return nWidth;
	}
	
	/**
	 * 控件宽度
	 */
	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}
	
	/**
	 * 控件高度
	 */
	public int getnHeight() {
		return nHeight;
	}
	
	/**
	 * 控件高度
	 */
	public void setnHeight(int nHeight) {
		this.nHeight = nHeight;
	}
	
	/**
	 * 曲线左顶点X坐标
	 */
	public int getnCurveX() {
		return nCurveX;
	}
	
	/**
	 * 曲线左顶点X坐标
	 */
	public void setnCurveX(int nCurveX) {
		this.nCurveX = nCurveX;
	}
	
	/**
	 * 曲线左顶点Y坐标
	 */
	public int getnCurveY() {
		return nCurveY;
	}
	
	/**
	 * 曲线左顶点Y坐标
	 */
	public void setnCurveY(int nCurveY) {
		this.nCurveY = nCurveY;
	}
	
	/**
	 * 曲线所在区域宽度
	 */
	public int getnCurveWidth() {
		return nCurveWidth;
	}
	
	/**
	 * 曲线所在区域宽度
	 */
	public void setnCurveWidth(int nCurveWidth) {
		this.nCurveWidth = nCurveWidth;
	}
	
	/**
	 * 曲线所在区域高度
	 */
	public int getnCurveHeight() {
		return nCurveHeight;
	}
	
	/**
	 * 曲线所在区域高度
	 */
	public void setnCurveHeight(int nCurveHeight) {
		this.nCurveHeight = nCurveHeight;
	}
	
	/**
	 * 采样地址数量
	 */
	public int getnSampleCount() {
		return nSampleCount;
	}

	/**
	 * 采样地址数量
	 */
	public void setnSampleCount(int nSampleCount) {
		this.nSampleCount = nSampleCount;
	}
	
	/**
	 * 通道数量
	 */
	public int getnChannelNum() {
		return nChannelNum;
	}
	
	/**
	 * 通道数量
	 */
	public void setnChannelNum(int nChannelNum) {
		this.nChannelNum = nChannelNum;
	}
	
	/**
	 * 控制显示地址
	 */
	public AddrProp getmControlAddr() {
		return mControlAddr;
	}
	
	/**
	 * 控制显示地址
	 */
	public void setmControlAddr(AddrProp mControlAddr) {
		this.mControlAddr = mControlAddr;
	}
	
	/**
	 * 是否自动复位
	 */
	public boolean isbAutoReset() {
		return bAutoReset;
	}

	/**
	 * 是否自动复位
	 */
	public void setbAutoReset(boolean bAutoReset) {
		this.bAutoReset = bAutoReset;
	}
	
	/**
	 * 数据类型
	 */
	public DATA_TYPE geteDataType() {
		return eDataType;
	}

	/**
	 * 数据类型
	 */
	public void seteDataType(DATA_TYPE eDataType) {
		this.eDataType = eDataType;
	}
	
	/**
	 * X轴最小显示范围地址
	 */
	public AddrProp getmXShowMinAddr() {
		return mXShowMinAddr;
	}
	
	/**
	 * X轴最小显示范围地址
	 */
	public void setmXShowMinAddr(AddrProp mXShowMinAddr) {
		this.mXShowMinAddr = mXShowMinAddr;
	}
	
	/**
	 * X轴最大显示范围地址
	 */
	public AddrProp getmXShowMaxAddr() {
		return mXShowMaxAddr;
	}
	
	/**
	 * X轴最大显示范围地址
	 */
	public void setmXShowMaxAddr(AddrProp mXShowMaxAddr) {
		this.mXShowMaxAddr = mXShowMaxAddr;
	}
	
	/**
	 * Y轴最小显示范围地址
	 */
	public AddrProp getmYShowMinAddr() {
		return mYShowMinAddr;
	}
	
	/**
	 * Y轴最小显示范围地址
	 */
	public void setmYShowMinAddr(AddrProp mYShowMinAddr) {
		this.mYShowMinAddr = mYShowMinAddr;
	}
	
	/**
	 * Y轴最大显示范围地址
	 */
	public AddrProp getmYShowMaxAddr() {
		return mYShowMaxAddr;
	}
	
	/**
	 * Y轴最大显示范围地址
	 */
	public void setmYShowMaxAddr(AddrProp mYShowMaxAddr) {
		this.mYShowMaxAddr = mYShowMaxAddr;
	}
	
	/**
	 * 是否缩放
	 */
	public boolean isbScale() {
		return bScale;
	}

	/**
	 * 是否缩放
	 */
	public void setbScale(boolean bScale) {
		this.bScale = bScale;
	}

	/**
	 * X轴，源范围取值类型
	 */
	public int getnXScaleType() {
		return nXScaleType;
	}

	/**
	 * X轴，源范围取值类型
	 */
	public void setnXScaleType(int nXScaleType) {
		this.nXScaleType = nXScaleType;
	}

	/**
	 * Y轴，源范围取值类型
	 */
	public int getnYScaleType() {
		return nYScaleType;
	}

	/**
	 * Y轴，源范围取值类型
	 */
	public void setnYScaleType(int nYScaleType) {
		this.nYScaleType = nYScaleType;
	}

	/**
	 * X轴最小源范围地址
	 */
	public AddrProp getmXTargetMinAddr() {
		return mXTargetMinAddr;
	}
	
	/**
	 * X轴最小源范围地址
	 */
	public void setmXTargetMinAddr(AddrProp mXTargetMinAddr) {
		this.mXTargetMinAddr = mXTargetMinAddr;
	}
	
	/**
	 * X轴最大源范围地址
	 */
	public AddrProp getmXTargetMaxAddr() {
		return mXTargetMaxAddr;
	}
	
	/**
	 * X轴最大源范围地址
	 */
	public void setmXTargetMaxAddr(AddrProp mXTargetMaxAddr) {
		this.mXTargetMaxAddr = mXTargetMaxAddr;
	}
	
	/**
	 * Y轴最小源范围地址
	 */
	public AddrProp getmYTargetMinAddr() {
		return mYTargetMinAddr;
	}
	
	/**
	 * Y轴最小源范围地址
	 */
	public void setmYTargetMinAddr(AddrProp mYTargetMinAddr) {
		this.mYTargetMinAddr = mYTargetMinAddr;
	}
	
	/**
	 * Y轴最大源范围地址
	 */
	public AddrProp getmYTargetMaxAddr() {
		return mYTargetMaxAddr;
	}
	
	/**
	 * Y轴最大源范围地址
	 */
	public void setmYTargetMaxAddr(AddrProp mYTargetMaxAddr) {
		this.mYTargetMaxAddr = mYTargetMaxAddr;
	}
	
	/**
	 * X轴最小显示值
	 */
	public double getnXShowMin() {
		return nXShowMin;
	}
	
	/**
	 * X轴最小显示值
	 */
	public void setnXShowMin(double nXShowMin) {
		this.nXShowMin = nXShowMin;
	}
	
	/**
	 * X轴最大显示值
	 */
	public double getnXShowMax() {
		return nXShowMax;
	}
	
	/**
	 * X轴最大显示值
	 */
	public void setnXShowMax(double nXShowMax) {
		this.nXShowMax = nXShowMax;
	}
	
	/**
	 * Y轴最小显示值
	 */
	public double getnYShowMin() {
		return nYShowMin;
	}
	
	/**
	 * Y轴最小显示值
	 */
	public void setnYShowMin(double nYShowMin) {
		this.nYShowMin = nYShowMin;
	}

	/**
	 * Y轴最大显示值
	 */
	public double getnYShowMax() {
		return nYShowMax;
	}

	/**
	 * Y轴最大显示值
	 */
	public void setnYShowMax(double nYShowMax) {
		this.nYShowMax = nYShowMax;
	}

	/**
	 * X轴源范围最小值
	 */
	public double getnXTargetMin() {
		return nXTargetMin;
	}

	/**
	 * X轴源范围最小值
	 */
	public void setnXTargetMin(double nXTargetMin) {
		this.nXTargetMin = nXTargetMin;
	}

	/**
	 * X轴源范围最大值
	 */
	public double getnXTargetMax() {
		return nXTargetMax;
	}

	/**
	 * X轴源范围最大值
	 */
	public void setnXTargetMax(double nXTargetMax) {
		this.nXTargetMax = nXTargetMax;
	}

	/**
	 * Y轴源范围最小值
	 */
	public double getnYTargetMin() {
		return nYTargetMin;
	}

	/**
	 * Y轴源范围最小值
	 */
	public void setnYTargetMin(double nYTargetMin) {
		this.nYTargetMin = nYTargetMin;
	}

	/**
	 * Y轴源范围最大值
	 */
	public double getnYTargetMax() {
		return nYTargetMax;
	}

	/**
	 * Y轴源范围最大值
	 */
	public void setnYTargetMax(double nYTargetMax) {
		this.nYTargetMax = nYTargetMax;
	}

	/**
	 * 是否显示垂直刻度
	 */
	public boolean isbShowVScale() {
		return bShowVScale;
	}

	/**
	 * 是否显示垂直刻度
	 */
	public void setbShowVScale(boolean bShowVScale) {
		this.bShowVScale = bShowVScale;
	}

	/**
	 * 是否显示水平刻度
	 */
	public boolean isbShowHScale() {
		return bShowHScale;
	}

	/**
	 * 是否显示水平刻度
	 */
	public void setbShowHScale(boolean bShowHScale) {
		this.bShowHScale = bShowHScale;
	}
	
	/**
	 * 是否垂直显示次刻度
	 */
	public boolean isbShowVMinorScale() {
		return bShowVMinorScale;
	}

	/**
	 * 是否垂直显示次刻度
	 */
	public void setbShowVMinorScale(boolean bShowVMinorScale) {
		this.bShowVMinorScale = bShowVMinorScale;
	}

	/**
	 * 是否水平显示次刻度
	 */
	public boolean isbShowHMinorScale() {
		return bShowHMinorScale;
	}

	/**
	 * 是否水平显示次刻度
	 */
	public void setbShowHMinorScale(boolean bShowHMinorScale) {
		this.bShowHMinorScale = bShowHMinorScale;
	}

	/**
	 * 垂直刻度数
	 */
	public int getnVMajorNum() {
		return nVMajorNum;
	}

	/**
	 * 垂直刻度数
	 */
	public void setnVMajorNum(int nVMajorNum) {
		this.nVMajorNum = nVMajorNum;
	}

	/**
	 * 水平刻度数
	 */
	public int getnHMajorNum() {
		return nHMajorNum;
	}

	/**
	 * 水平刻度数
	 */
	public void setnHMajorNum(int nHMajorNum) {
		this.nHMajorNum = nHMajorNum;
	}

	/**
	 * 垂直刻次度数
	 */
	public int getnVMinorNum() {
		return nVMinorNum;
	}

	/**
	 * 垂直刻次度数
	 */
	public void setnVMinorNum(int nVMinorNum) {
		this.nVMinorNum = nVMinorNum;
	}

	/**
	 * 水平刻次度数
	 */
	public int getnHMinorNum() {
		return nHMinorNum;
	}

	/**
	 * 水平刻次度数
	 */
	public void setnHMinorNum(int nHMinorNum) {
		this.nHMinorNum = nHMinorNum;
	}

	/**
	 * 边框颜色
	 */
	public int getnBoradColor() {
		return nBoradColor;
	}

	/**
	 * 边框颜色
	 */
	public void setnBoradColor(int nBoradColor) {
		this.nBoradColor = nBoradColor;
	}

	/**
	 * 刻度颜色
	 */
	public int getnScaleColor() {
		return nScaleColor;
	}

	/**
	 * 刻度颜色
	 */
	public void setnScaleColor(int nScaleColor) {
		this.nScaleColor = nScaleColor;
	}

	/**
	 * 垂直网格颜色
	 */
	public int getnVNetColor() {
		return nVNetColor;
	}

	/**
	 * 垂直网格颜色
	 */
	public void setnVNetColor(int nVNetColor) {
		this.nVNetColor = nVNetColor;
	}

	/**
	 * 水平网格颜色
	 */
	public int getnHNetColor() {
		return nHNetColor;
	}

	/**
	 * 水平网格颜色
	 */
	public void setnHNetColor(int nHNetColor) {
		this.nHNetColor = nHNetColor;
	}

	/**
	 * 曲线曲线背景颜色
	 */
	public int getnGraphColor() {
		return nGraphColor;
	}

	/**
	 * 曲线曲线背景颜色
	 */
	public void setnGraphColor(int nGraphColor) {
		this.nGraphColor = nGraphColor;
	}

	/**
	 * 是否显示网格
	 */
	public boolean isbShowNet() {
		return bShowNet;
	}

	/**
	 * 是否显示网格
	 */
	public void setbShowNet(boolean bShowNet) {
		this.bShowNet = bShowNet;
	}

	/**
	 * 字体大小
	 */
	public int getnFontSize() {
		return nFontSize;
	}

	/**
	 * 字体大小
	 */
	public void setnFontSize(int nFontSize) {
		this.nFontSize = nFontSize;
	}

	/**
	 * 字体颜色
	 */
	public int getnFontColor() {
		return nFontColor;
	}

	/**
	 * 字体颜色
	 */
	public void setnFontColor(int nFontColor) {
		this.nFontColor = nFontColor;
	}
	
	/**
	 * 显现属性
	 */
	public ShowInfo getmShowInfo() {
		return mShowInfo;
	}
	
	/**
	 * 层次
	 */
	public int getnZvalue() {
		return nZvalue;
	}

	/**
	 * 层次
	 */
	public void setnZvalue(int nZvalue) {
		this.nZvalue = nZvalue;
	}

	/**
	 * 组合
	 */
	public int getnCollidindId() {
		return nCollidindId;
	}

	/**
	 * 组合
	 */
	public void setnCollidindId(int nCollidindId) {
		this.nCollidindId = nCollidindId;
	}

	/**
	 * 显现属性
	 */
	public void setmShowInfo(ShowInfo mShowInfo) {
		this.mShowInfo = mShowInfo;
	}

	/**
	 * 通道信息
	 */
	public ArrayList<ChannelGroupInfo> getmChannelList() {
		return mChannelList;
	}

	/**
	 * 通道信息
	 */
	public void setmChannelList(ArrayList<ChannelGroupInfo> mChannelList) {
		this.mChannelList = mChannelList;
	}
	
	/**
	 * X轴的显示范围，取值类型 1-地址 ，2-常量
	 */
	public int getnXShowType() {
		return nXShowType;
	}

	/**
	 * X轴的显示范围，取值类型 1-地址 ，2-常量
	 */
	public void setnXShowType(int nXShowType) {
		this.nXShowType = nXShowType;
	}

	/**
	 * Y轴的显示范围，取值类型 1-地址 ，2-常量
	 */
	public int getnYShowType() {
		return nYShowType;
	}

	/**
	 * Y轴的显示范围，取值类型 1-地址 ，2-常量
	 */
	public void setnYShowType(int nYShowType) {
		this.nYShowType = nYShowType;
	}
}
