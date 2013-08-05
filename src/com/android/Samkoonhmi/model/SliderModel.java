package com.android.Samkoonhmi.model;

import android.graphics.Color;
import android.graphics.Point;

import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 进度条实体类
 * 
 * @author Eisen
 * 
 */
public class SliderModel {
	private Integer id;
	private int mWidth;// 矩形宽
	private int mHeight;// 矩形高
	private int startX;// 左上角的点x
	private int startY;// 左上角的点y
	private int rectColor;// 矩形背景色
	private int slideBarColor;// 滑轨的颜色
	private int fingerBackColor;// 指标背景颜色
	private int fingerLineColor; // 指标边框颜色
	private CALIBRATION_DIRECTION direction;// 滑块的方向，即指针的方向，与刻度的方向一致
	private CALIBRATION_DIRECTION nPosition;// 刻度文字的方向
	private boolean isShowCalibration;// 是否显示刻度
	private DATA_TYPE dataType;// 数据类别
	private AddrProp writeKeyAdd;// 写入地址
	private boolean isTrend;// 是否显示动态范围
	private double maxTrend=0L;// 动态范围最大值
	private double minTrend=0L;// 动态范围最小值
	private AddrProp maxTrendAdd;// 动态范围最大值地址
	private AddrProp minTrendAdd;// 动态范围最小值地址
	private int nCalibrationColor;// int 刻度颜色
	private int nMaxNumber;// smallint 主刻度数目
	private int nMinNumber;// smallint 次刻度数目
	private boolean bShowText;// 是否显示文字
	private boolean bShowShaft;// 是否显示轴
	private int nTotalCount;// 刻度小数位数总位数
	private int nDecimalCount;// smallint 刻度值小数位数
	private int nTextSize;// smallint 字体大小
	private double nCalibrationMax;// 刻度最大值
	private double nCalibrationMin;// 刻度最小值
	private int nZvalue;
	private int nCollidindId;
	private  Point pointStart;
	private int nSlideWidth;//滑轨的宽度
	private int nSlideHeight;//滑轨的高度
	private TouchInfo touchInfo;// 触控属性
	private ShowInfo showInfo;// 显现属性


	
	public AddrProp getWriteKeyAdd() {
		return writeKeyAdd;
	}

	public void setWriteKeyAdd(AddrProp writeKeyAdd) {
		this.writeKeyAdd = writeKeyAdd;
	}

	public AddrProp getMaxTrendAdd() {
		return maxTrendAdd;
	}

	public void setMaxTrendAdd(AddrProp maxTrendAdd) {
		this.maxTrendAdd = maxTrendAdd;
	}

	public AddrProp getMinTrendAdd() {
		return minTrendAdd;
	}

	public void setMinTrendAdd(AddrProp minTrendAdd) {
		this.minTrendAdd = minTrendAdd;
	}


	public TouchInfo getTouchInfo() {
		return touchInfo;
	}

	public void setTouchInfo(TouchInfo touchInfo) {
		this.touchInfo = touchInfo;
	}

	public ShowInfo getShowInfo() {
		return showInfo;
	}

	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}

	public int getnSlideWidth() {
		return nSlideWidth;
	}

	public void setnSlideWidth(int nSlideWidth) {
		this.nSlideWidth = nSlideWidth;
	}

	public int getnSlideHeight() {
		return nSlideHeight;
	}

	public void setnSlideHeight(int nSlideHeight) {
		this.nSlideHeight = nSlideHeight;
	}

	public Point getPointStart() {
		return pointStart;
	}

	public void setPointStart(Point pointStart) {
		this.pointStart = pointStart;
	}

	public SliderModel() {
		super();
	}


	public int getnCalibrationColor() {
		return nCalibrationColor;
	}

	public void setnCalibrationColor(int nCalibrationColor) {
		this.nCalibrationColor = nCalibrationColor;
	}

	public int getnMaxNumber() {
		return nMaxNumber;
	}

	public void setnMaxNumber(int nMaxNumber) {
		this.nMaxNumber = nMaxNumber;
	}

	public int getnMinNumber() {
		return nMinNumber;
	}

	public void setnMinNumber(int nMinNumber) {
		this.nMinNumber = nMinNumber;
	}

	public int getnDecimalCount() {
		return nDecimalCount;
	}

	public void setnDecimalCount(int nDecimalCount) {
		this.nDecimalCount = nDecimalCount;
	}

	public int getnTextSize() {
		return nTextSize;
	}

	public void setnTextSize(int nTextSize) {
		this.nTextSize = nTextSize;
	}

	public CALIBRATION_DIRECTION getnPosition() {
		return nPosition;
	}

	public void setnPosition(CALIBRATION_DIRECTION nPosition) {
		this.nPosition = nPosition;
	}

	public boolean isbShowShaft() {
		return bShowShaft;
	}

	public void setbShowShaft(boolean bShowShaft) {
		this.bShowShaft = bShowShaft;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getmWidth() {
		return mWidth;
	}

	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}

	public int getmHeight() {
		return mHeight;
	}

	public void setmHeight(int mHeight) {
		this.mHeight = mHeight;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getRectColor() {
		return rectColor;
	}

	public void setRectColor(int rectColor) {
		this.rectColor = rectColor;
	}

	public int getSlideBarColor() {
		return slideBarColor;
	}

	public void setSlideBarColor(int slideBarColor) {
		this.slideBarColor = slideBarColor;
	}

	public int getFingerBackColor() {
		return fingerBackColor;
	}

	public void setFingerBackColor(int fingerBackColor) {
		this.fingerBackColor = fingerBackColor;
	}

	public int getFingerLineColor() {
		return fingerLineColor;
	}

	public void setFingerLineColor(int fingerLineColor) {
		this.fingerLineColor = fingerLineColor;
	}

	public CALIBRATION_DIRECTION getDirection() {
		return direction;
	}

	public void setDirection(CALIBRATION_DIRECTION direction) {
		this.direction = direction;
	}

	public boolean isShowCalibration() {
		return isShowCalibration;
	}

	public void setShowCalibration(boolean isShowCalibration) {
		this.isShowCalibration = isShowCalibration;
	}

	public DATA_TYPE getDataType() {
		return dataType;
	}

	public void setDataType(DATA_TYPE dataType) {
		this.dataType = dataType;
	}


	public boolean isTrend() {
		return isTrend;
	}

	public void setTrend(boolean isTrend) {
		this.isTrend = isTrend;
	}

	public double getMaxTrend() {
		return maxTrend;
	}

	public void setMaxTrend(double maxTrend) {
		this.maxTrend = maxTrend;
	}

	public double getMinTrend() {
		return minTrend;
	}

	public void setMinTrend(double minTrend) {
		this.minTrend = minTrend;
	}

	public int getnZvalue() {
		return nZvalue;
	}

	public void setnZvalue(int nZvalue) {
		this.nZvalue = nZvalue;
	}

	public int getnCollidindId() {
		return nCollidindId;
	}

	public void setnCollidindId(int nCollidindId) {
		this.nCollidindId = nCollidindId;
	}

	public boolean isbShowText() {
		return bShowText;
	}

	public void setbShowText(boolean bShowText) {
		this.bShowText = bShowText;
	}

	public int getnTotalCount() {
		return nTotalCount;
	}

	public void setnTotalCount(int nTotalCount) {
		this.nTotalCount = nTotalCount;
	}

	public double getnCalibrationMax() {
		return nCalibrationMax;
	}

	public void setnCalibrationMax(double nCalibrationMax) {
		this.nCalibrationMax = nCalibrationMax;
	}

	public double getnCalibrationMin() {
		return nCalibrationMin;
	}

	public void setnCalibrationMin(double nCalibrationMin) {
		this.nCalibrationMin = nCalibrationMin;
	}

}
